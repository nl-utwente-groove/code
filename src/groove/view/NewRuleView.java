/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AspectualRuleView.java,v 1.40 2008-03-04 10:10:15 fladder Exp $
 */

package groove.view;

import static groove.view.aspect.AttributeAspect.getAttributeValue;
import static groove.view.aspect.RuleAspect.CREATOR;
import static groove.view.aspect.RuleAspect.EMBARGO;
import static groove.view.aspect.RuleAspect.ERASER;
import static groove.view.aspect.RuleAspect.READER;
import static groove.view.aspect.RuleAspect.getRuleValue;
import groove.graph.AbstractGraph;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.VarSupport;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.EdgeEmbargo;
import groove.trans.ForallCondition;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.Rule;
import groove.trans.RuleName;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.util.DefaultFixable;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeElementFactory;
import groove.view.aspect.NestingAspect;
import groove.view.aspect.ParameterAspect;
import groove.view.aspect.RuleAspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides a graph view upon a production rule. The nodes and edges are divided
 * into embargoes, erasers, readers and creators, with the following intuition:
 * <ul>
 * <li>Maximal connected embargo subgraphs correspond to negative application
 * conditions.
 * <li>Erasers correspond to LHS elements that are not RHS.
 * <li>Readers (the default) are elements that are both LHS and RHS.
 * <li>Creators are RHS elements that are not LHS.
 * </ul>
 * @author Arend Rensink
 * @version $Revision: 1923 $
 */
public class NewRuleView extends AspectualView<Rule> implements RuleView {
    /**
     * Constructs a rule view from an aspect graph. The rule properties are
     * explicitly given.
     * @param graph the graph to be converted (non-null)
     * @param properties object specifying rule properties, such as injectivity
     *        etc (nullable)
     */
    public NewRuleView(AspectGraph graph, SystemProperties properties) {
        String name = GraphInfo.getName(graph);
        this.name = name == null ? null : new RuleName(name);
        this.properties = properties;
        this.graph = graph;
        this.attributeFactory = new AttributeElementFactory(graph, properties);
        this.labelSet = new HashSet<Label>();
        if (!graph.getErrors().isEmpty()) {
            this.errors = graph.getErrors();
        }
        // we fix the view; is it conceptually right to do that here?
        // graph.setFixed();
    }

    /**
     * Checks if the variables bound by the left hand side of an aspect graph
     * cover all variables used in the right hand side and the NACs.
     * @throws FormatException if there is a free variable in the rhs or NAC
     */
    protected void testVariableBinding() throws FormatException {
        Set<String> boundVars = getVars(READER, true);
        boundVars.addAll(getVars(ERASER, true));
        Set<String> rhsOnlyVars = getVars(CREATOR, false);
        if (!boundVars.containsAll(rhsOnlyVars)) {
            rhsOnlyVars.removeAll(boundVars);
            throw new FormatException(
                "Right hand side variables %s not bound on left hand side",
                rhsOnlyVars);
        }
        Set<String> embargoVars = getVars(EMBARGO, false);
        if (!boundVars.containsAll(embargoVars)) {
            embargoVars.removeAll(boundVars);
            throw new FormatException(
                "NAC variables %s not bound on left hand side", embargoVars);
        }
    }

    /**
     * Collects the variables from the regular expressions in edges with a given
     * role from a given graph. A flag indicates if it is just the bound
     * variables we are interested in.
     * @param role the role to look for
     * @param bound if <code>true</code>, collect bound variables only
     * @return the requested set of variables
     */
    protected Set<String> getVars(AspectValue role, boolean bound) {
        Set<String> result = new HashSet<String>();
        Iterator<? extends Edge> edgeIter = this.graph.edgeSet().iterator();
        while (edgeIter.hasNext()) {
            AspectEdge edge = (AspectEdge) edgeIter.next();
            if (role.equals(getRuleValue(edge))) {
                try {
                    Label varLabel =
                        getDefaultLabelParser().parse(edge.label());
                    if (varLabel instanceof RegExprLabel) {
                        RegExpr expr = ((RegExprLabel) varLabel).getRegExpr();
                        result.addAll(bound ? expr.boundVarSet()
                                : expr.allVarSet());
                    }
                } catch (FormatException e) {
                    // not a regular expression; do nothing
                }
            }
        }
        return result;
    }

    /**
     * Returns the name of the rule represented by this rule graph, set at
     * construction time.
     */
    public RuleName getRuleName() {
        return this.name;
    }

    /** Convenience method for <code>getNameLabel().text()</code>. */
    public String getName() {
        return getRuleName() == null ? null : getRuleName().text();
    }

    public int getPriority() {
        return GraphProperties.getPriority(this.graph);
    }

    public boolean isEnabled() {
        return GraphProperties.isEnabled(this.graph);
    }

    public boolean isConfluent() {
        return GraphProperties.isConfluent(this.graph);
    }

    public int compareTo(RuleView o) {
        int result = getPriority() - o.getPriority();
        if (result == 0) {
            result = getRuleName().compareTo(o.getRuleName());
        }
        return result;
    }

    /** Throws an {@link UnsupportedOperationException}. */
    public RuleView newInstance(Rule rule) throws FormatException {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    public Rule toModel() throws FormatException {
        return toRule();
    }

    /**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    public Rule toRule() throws FormatException {
        if (this.errors != null) {
            throw new FormatException(this.errors);
        }
        if (this.rule == null) {
            Pair<Rule,NodeEdgeMap> ruleMapPair = computeRule();
            this.rule = ruleMapPair.first();
            this.viewToRuleMap = ruleMapPair.second();
        }
        return this.rule;
    }

    public List<String> getErrors() {
        if (this.errors == null) {
            try {
                toRule();
                this.errors = Collections.emptyList();
            } catch (FormatException exc) {
                this.errors = exc.getErrors();
            }
        }
        return this.errors;
    }

    /** Returns the set of labels occurring in this rule. */
    public Set<Label> getLabels() {
        // first check if the rule has already been built
        if (this.rule == null) {
            try {
                toRule();
            } catch (FormatException exc) {
                // ignore the exception
            }
        }
        return this.labelSet;
    }

    @Override
    public AspectGraph getAspectGraph() {
        return this.graph;
    }

    @Override
    public NodeEdgeMap getMap() {
        if (this.viewToRuleMap == null) {
            try {
                Pair<Rule,NodeEdgeMap> ruleMapPair = computeRule();
                this.rule = ruleMapPair.first();
                this.viewToRuleMap = ruleMapPair.second();
            } catch (FormatException exc) {
                // do nothing; the map will be empty
            }
        }
        return this.viewToRuleMap;
    }

    /**
     * Sets the properties of this view. This means that the previously
     * constructed model (if any) becomes invalid.
     */
    public final void setProperties(SystemProperties properties) {
        this.properties = properties;
        invalidateRule();
    }

    /**
     * @return Returns the properties.
     */
    protected final SystemProperties getProperties() {
        return this.properties;
    }

    /**
     * Indicates if the rule is to be matched injectively. If so, all context
     * nodes should be part of the root map, otherwise injectivity cannot be
     * checked.
     * @return <code>true</code> if the rule is to be matched injectively.
     */
    private final boolean isInjective() {
        return getProperties() != null && getProperties().isInjective();
    }

    @Override
    protected LabelParser getDefaultLabelParser() {
        return RegExprLabelParser.getInstance();
    }

    /**
     * Invalidates any previous construction of the underlying rule.
     */
    private void invalidateRule() {
        this.rule = null;
        this.errors = null;
    }

    /**
     * Callback method to compute a rule from an aspect graph.
     */
    private Pair<Rule,NodeEdgeMap> computeRule() throws FormatException {
        SPORule rule;
        // map from the nodes and edges of the view (i.e., the aspect graph)
        // to their representatives in the rule.
        NodeEdgeMap viewToRuleMap = new NodeEdgeHashMap();
        // map from eraser/reader parameter indices
        // to corresponding LHS (rule) nodes
        SortedMap<Integer,Node> lhsParameterMap = new TreeMap<Integer,Node>();
        // map from creator parameter indices to corresponding RHS (rule) nodes
        SortedMap<Integer,Node> creatorParameterMap =
            new TreeMap<Integer,Node>();
        // set of all rule parameter nodes
        Set<Node> parameters = new HashSet<Node>();

        Set<String> errors = new TreeSet<String>(this.graph.getErrors());
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        // Object keeping track of quantification levels in the rule view
        LevelTree levelTree = new LevelTree();
        LevelViewMap levelViewMap = new LevelViewMap();
        LevelRuleMap levelRuleMap = new LevelRuleMap();
        try {
            // add nodes to nesting data structures
            int creatorParametersStartAt = -1;
            for (AspectNode node : this.graph.nodeSet()) {
                if (RuleAspect.inRule(node)) {
                    Level level = levelTree.getLevel(node);
                    // add the node to the appropriate level where it should be
                    // processed
                    if (RuleAspect.isCreator(node) && level.isUniversal()) {
                        for (Level sublevel : level.getChildren()) {
                            levelViewMap.addNode(sublevel, node);
                        }
                    } else {
                        levelViewMap.addNode(level, node);
                    }
                    Node nodeImage = computeNodeImage(node);
                    // check if the node is a parameter
                    Integer nr = ParameterAspect.getParNumber(node);
                    if (nr != null) {
                        if (!RuleAspect.inLHS(node)) {
                            if (creatorParametersStartAt == -1
                                || creatorParametersStartAt > nr) {
                                creatorParametersStartAt = nr;
                            }
                            throw new FormatException(
                                "Rule parameter %d only allowed on LHS nodes",
                                nr);
                        }
                        if (!level.isTopLevel()) {
                            throw new FormatException(
                                "Rule parameter %d only allowed on top existential level",
                                nr);
                        }
                        parameters.add(nodeImage);
                        if (!nr.equals(0) && RuleAspect.inLHS(node)) {
                            // store the node w.r.t the ID
                            Node oldValue = lhsParameterMap.put(nr, nodeImage);
                            if (oldValue != null) {
                                throw new FormatException(
                                    "Parameter number %d occurs more than once",
                                    nr);
                            }
                        } else if (!nr.equals(0) && !RuleAspect.inLHS(node)) {
                            Node oldValue =
                                creatorParameterMap.put(nr, nodeImage);
                            if (oldValue != null) {
                                throw new FormatException(
                                    "Parameter number %d occurs more than once",
                                    nr);
                            }
                        }
                    }
                    viewToRuleMap.putNode(node, nodeImage);
                }
            }

            // test if the parameter nodes form a consecutive sequence, starting
            // at 1,
            // and are not interleaved with creator-parameters
            Iterator<Integer> parameterNrIter =
                lhsParameterMap.keySet().iterator();
            int nr = 0;
            while (parameterNrIter.hasNext()) {
                int nextNr = parameterNrIter.next();
                if (nextNr >= creatorParametersStartAt
                    && creatorParameterMap.size() > 0) {
                    throw new FormatException(
                        "Non-creator parameters should come before creator-parameters");
                }
                if (nextNr != nr + 1) {
                    throw new FormatException("Parameter number %d missing",
                        nr + 1);
                }
                nr = nextNr;
            }

            // test if the creator-parameters start at the last
            // non-creator-parameter + 1
            if (creatorParametersStartAt != -1
                && creatorParametersStartAt != nr + 1) {
                throw new FormatException(
                    "Creator parameters should continue the sequence of non-creator-parameters, start at %d",
                    nr + 1);
            }

            // test if there are no creator-parameters that come before
            // non-creator-parameters,
            // and if they form a consecutive sequence
            Iterator<Integer> creatorParameterNrIter =
                creatorParameterMap.keySet().iterator();
            nr = creatorParametersStartAt - 1;
            while (creatorParameterNrIter.hasNext()) {
                int nextNr = creatorParameterNrIter.next();
                if (nextNr < creatorParametersStartAt) {
                    throw new FormatException(
                        "Non-creator parameters should come before creator-parameters (%d)",
                        nextNr);
                }
                if (nextNr != nr + 1) {
                    throw new FormatException("Creator-parameter %d missing",
                        nr);
                }
                nr = nextNr;
            }

            // add edges to nesting data structures
            for (AspectEdge edge : this.graph.edgeSet()) {
                if (RuleAspect.inRule(edge)) {
                    Level level = levelTree.getLevel(edge);
                    if (level.isExistential() || !RuleAspect.isCreator(edge)) {
                        levelViewMap.addEdge(level, edge);
                    }
                    if (level.isUniversal() && hasConcreteImage(edge.label())) {
                        // add the edge and its end nodes to the next
                        // (rule) level
                        for (Level sublevel : level.getChildren()) {
                            levelViewMap.addEdge(sublevel, edge);
                        }
                    }
                    Edge edgeImage =
                        computeEdgeImage(edge, viewToRuleMap.nodeMap());
                    if (edgeImage != null) {
                        Label edgeLabel = edgeImage.label();
                        if (edgeLabel.isNodeType()) {
                            if (!edgeImage.source().equals(edgeImage.opposite())) {
                                throw new FormatException(
                                    "Node type label '%s' only allowed on self-edges",
                                    edgeLabel);
                            }
                        }
                        viewToRuleMap.putEdge(edge, edgeImage);
                        this.labelSet.add(edgeLabel);
                    }
                }
            }
            testVariableBinding();
            Map<Level,Condition> levelRuleMap = new HashMap<Level,Condition>();
            for (Level level : nestedNodesMap.keySet()) {
                Map<AspectNode,Boolean> levelNodes = nestedNodesMap.get(level);
                Map<AspectEdge,Boolean> levelEdges = nestedEdgesMap.get(level);
                AbstractCondition<?> condition =
                    computeFlatRule(viewToRuleMap, levelNodes, levelEdges,
                        level.isExistential());
                levelRuleMap.put(level, condition);
                Level parentLevel =
                    level.isTopLevel() ? null : level.getParent();
                if (level.isExistential() && !level.isTopLevel()) {
                    ((SPORule) condition).setParent(
                        (SPORule) levelRuleMap.get(parentLevel.getParent()),
                        level.getIntArray());
                } else if (level.isPositive()) {
                    ((ForallCondition) condition).setPositive();
                }
                if (level.isTopLevel()) {
                    condition.setName(this.name);
                } else {
                    String ruleNamePrefix =
                        (level.getName() == null) ? this.name.text()
                                : level.getName();
                    String ruleNameSuffix =
                        Groove.toString(level.getChildren().toArray());
                    condition.setName(new RuleName(ruleNamePrefix
                        + ruleNameSuffix));
                    levelRuleMap.get(parentLevel).addSubCondition(condition);
                }
            }
            rule = (SPORule) levelRuleMap.get(topLevel);
            rule.setPriority(getPriority());
            rule.setConfluent(isConfluent());
            rule.setParameters(new ArrayList<Node>(lhsParameterMap.values()),
                parameters);
            rule.setCreatorParameters(new ArrayList<Node>(
                creatorParameterMap.values()));
            rule.setFixed();

            if (TO_RULE_DEBUG) {
                System.out.println("Constructed rule: " + rule);
            }
        } catch (FormatException e) {
            rule = null;
            errors.addAll(e.getErrors());
        }
        if (errors.isEmpty()) {
            return new Pair<Rule,NodeEdgeMap>(rule, viewToRuleMap);
        } else {
            throw new FormatException(new ArrayList<String>(errors));
        }
    }

    /**
     * Adds a node to a nesting level, as well as to all sub-levels if the rule
     * is injective.
     */
    private void addNodeToLevel(AspectNode node, boolean fresh, Level level,
            Map<Level,Map<AspectNode,Boolean>> nestedNodesMap,
            Map<Level,Integer> subLevelCountMap) {
        nestedNodesMap.get(level).put(node, fresh);
        if (isInjective()) {
            // add the node as stale to all next (rule) levels
            for (int child = 0; child < subLevelCountMap.get(level); child++) {
                addNodeToLevel(node, false, level.getChild(child),
                    nestedNodesMap, subLevelCountMap);
            }
        }
    }

    /**
     * Adds an edge and, if necessary, its end nodes, to the maps of nested
     * element.
     * @param edge the edge to be added
     * @param fresh indicates if <code>edge</code> is fresh on this level
     * @param level the level to which the edge should be added
     * @param nestedNodesMap mapping from levels to maps of nodes on that level,
     *        to flags indicating if the nodes are fresh here
     * @param nestedEdgesMap mapping from levels to maps of edges on that level,
     *        to flags indicating if the edges are fresh here
     */
    private void addEdgeToLevel(AspectEdge edge, boolean fresh, Level level,
            Map<Level,Map<AspectNode,Boolean>> nestedNodesMap,
            Map<Level,Map<AspectEdge,Boolean>> nestedEdgesMap) {
        nestedEdgesMap.get(level).put(edge, fresh);
        for (Node end : edge.ends()) {
            Level nodeLevel = new Level(level);
            Map<AspectNode,Boolean> nestedNodes = nestedNodesMap.get(nodeLevel);
            while (!nestedNodes.containsKey(end)) {
                nestedNodes.put((AspectNode) end, false);
                if (!nodeLevel.isTopLevel()) {
                    nodeLevel = nodeLevel.getParent();
                    nestedNodes = nestedNodesMap.get(nodeLevel);
                }
            }
        }
    }

    /**
     * Callback method to compute a rule (on a given nesting level) from sets of
     * aspect nodes and edges that appear new on this level.
     * @param newNodes mapping from nodes that appear on this level, to a
     *        boolean indicating if they are new on this level
     * @param newEdges mapping from edges that appear on this level, to a
     *        boolean indicating if they are new on this level
     * @return a pair consisting of the resulting rule and a mapping from the
     *         aspect nodes and edges to the corresponding rule elements
     */
    private AbstractCondition<?> computeFlatRule(NodeEdgeMap viewToRuleMap,
            Map<AspectNode,Boolean> newNodes, Map<AspectEdge,Boolean> newEdges,
            boolean existential) throws FormatException {
        AbstractCondition<?> result;
        Set<String> errors = new TreeSet<String>();
        // create the new lhs
        Graph lhs = createGraph();
        // we separately keep a set of NAC-only elements
        Set<Node> nacNodeSet = new HashSet<Node>();
        Set<Edge> nacEdgeSet = new HashSet<Edge>();
        // create the new rhs
        Graph rhs = createGraph();
        // such end nodes are either roots or co-roots of the rule
        NodeEdgeMap rootMap = new NodeEdgeHashMap();
        NodeEdgeMap coRootMap = new NodeEdgeHashMap();
        // rule morphism for the resulting production rule
        Morphism ruleMorph = createMorphism(lhs, rhs);
        // first add nodes to lhs, rhs, morphism and left graph
        for (Map.Entry<AspectNode,Boolean> nodeEntry : newNodes.entrySet()) {
            AspectNode node = nodeEntry.getKey();
            Node nodeImage = viewToRuleMap.getNode(node);
            if (RuleAspect.inLHS(node)) {
                lhs.addNode(nodeImage);
                if (!nodeEntry.getValue()) {
                    rootMap.putNode(nodeImage, nodeImage);
                }
            }
            if (RuleAspect.inRHS(node)) {
                rhs.addNode(nodeImage);
                if (!nodeEntry.getValue()) {
                    coRootMap.putNode(nodeImage, nodeImage);
                }
                // we may have creator nodes on universal levels, if they were
                // actually created on the level above
                assert existential || RuleAspect.inLHS(node)
                    || !nodeEntry.getValue() : String.format(
                    "Creator node %s should be existential", node);
                if (RuleAspect.inLHS(node)) {
                    ruleMorph.putNode(nodeImage, nodeImage);
                }
            }
            if (RuleAspect.inNAC(node)) {
                nacNodeSet.add(nodeImage);
            }
        }
        // add mergers
        // collect mergers in a merge map
        MergeMap mergers = new MergeMap();
        for (AspectEdge edge : newEdges.keySet()) {
            if (RuleAspect.isMerger(edge)) {
                assert existential;
                // it's a merger; it's bound to be binary
                assert edge.endCount() == 2 : "Merger edge " + edge
                    + " should be binary";
                Node mergeSource = viewToRuleMap.getNode(edge.source());
                Node mergeTarget = viewToRuleMap.getNode(edge.opposite());
                mergers.putNode(mergeSource, mergeTarget);
                // existing edges will automatically be redirected
                rhs.mergeNodes(mergeSource, mergers.getNode(mergeTarget));
            }
        }
        // copy of the node map for the RHS to account for mergers
        Map<AspectNode,Node> toRight = new HashMap<AspectNode,Node>();
        for (Map.Entry<Node,Node> nodeEntry : viewToRuleMap.nodeMap().entrySet()) {
            toRight.put((AspectNode) nodeEntry.getKey(),
                mergers.getNode(nodeEntry.getValue()));
        }
        try {
            // now add edges to lhs, rhs and morphism
            // remember from which nodes a type edge are added and deleted
            Set<Node> deletedTypes = new HashSet<Node>();
            Map<Node,Label> addedTypes = new HashMap<Node,Label>();
            for (Map.Entry<AspectEdge,Boolean> edgeEntry : newEdges.entrySet()) {
                AspectEdge edge = edgeEntry.getKey();
                Edge edgeImage = viewToRuleMap.getEdge(edge);
                if (edgeImage != null && RuleAspect.inLHS(edge)) {
                    lhs.addEdge(edgeImage);
                    if (hasConcreteImage(edgeImage.label())) {
                        if (!edgeEntry.getValue()) {
                            rootMap.putEdge(edgeImage, edgeImage);
                        }
                        // } else if (!RuleAspect.inRHS(edge) &&
                        // !RegExprLabel.isNeg(edge.label())) {
                        // throw new FormatException(
                        // "Regular label '%s' may not be used on erasers",
                        // edgeImage.label());
                    }
                    if (!RuleAspect.inRHS(edge)
                        && edgeImage.label().isNodeType()) {
                        deletedTypes.add(edgeImage.source());
                    }
                }
                if (edgeImage != null && RuleAspect.inRHS(edge)
                    && !RuleAspect.isMerger(edge)) {
                    // use the toRight map because we may have merged nodes
                    Edge rhsEdgeImage = computeEdgeImage(edge, toRight);
                    rhs.addEdge(rhsEdgeImage);
                    assert existential || RuleAspect.inLHS(edge);
                    if (RuleAspect.inLHS(edge)) {
                        ruleMorph.putEdge(edgeImage, rhsEdgeImage);
                    } else {
                        if (RuleAspect.inLHS(edge.source())
                            && edgeImage.label().isNodeType()) {
                            addedTypes.put(edgeImage.source(),
                                edgeImage.label());
                        }
                        if (!edgeEntry.getValue()) {
                            coRootMap.putEdge(edgeImage, rhsEdgeImage);
                        }
                    }
                }
                if (edgeImage != null && RuleAspect.inNAC(edge)) {
                    nacEdgeSet.add(edgeImage);
                }
            }
            // check if node type additions and deletions are balanced
            for (Node deletedType : deletedTypes) {
                addedTypes.remove(deletedType);
            }
            if (!addedTypes.isEmpty()) {
                StringBuilder error = new StringBuilder();
                for (Label type : addedTypes.values()) {
                    if (error.length() > 0) {
                        error.append(String.format("%n"));
                    }
                    error.append(String.format(
                        "New node type '%s' not allowed without removing old type",
                        type));
                }
                throw new FormatException(error.toString());
            }
            // the resulting rule
            if (existential) {
                result = createRule(ruleMorph, rootMap, coRootMap, null);
            } else {
                result = createForall(lhs, rootMap, null);
            }
            // add the nacs to the rule
            for (Pair<Set<Node>,Set<Edge>> nacPair : AbstractGraph.getConnectedSets(
                nacNodeSet, nacEdgeSet)) {
                result.addSubCondition(computeNac(lhs, nacPair.first(),
                    nacPair.second()));
            }
        } catch (FormatException e) {
            result = null;
            errors.addAll(e.getErrors());
        }
        if (errors.isEmpty()) {
            return result;
        } else {
            throw new FormatException(new ArrayList<String>(errors));
        }
    }

    //
    // /**
    // * Returns the aspect node indicating the nesting level of a given node,
    // if
    // * any. This is a node to which there exists an
    // * {@link NestingAspect#AT_LABEL}-edge in the view.
    // */
    // private AspectNode getNestingNode(AspectNode node) {
    // AspectEdge levelEdge = null;
    // for (AspectEdge edge : this.graph.outEdgeSet(node)) {
    // if (NestingAspect.isLevelEdge(edge)) {
    // levelEdge = edge;
    // break;
    // }
    // }
    // return levelEdge == null ? null : levelEdge.opposite();
    // }

    //
    // /**
    // * Computes various level-related structures. The parameters are expected
    // to
    // * be empty structures, which are filled in by the method (serving as
    // output
    // * parameters).
    // * @param metaLevelMap mapping from aspect meta-nodes to the corresponding
    // * level
    // * @param nameLevelMap mapping from level names to levels
    // * @param subLevelCountMap mapping from levels to the number of sub-levels
    // */
    // private void createLevelMap(Map<AspectNode,TreeIndex> metaLevelMap,
    // Map<String,TreeIndex> nameLevelMap,
    // Map<TreeIndex,Integer> subLevelCountMap) {
    // Map<AspectNode,AspectNode> parentMap =
    // new HashMap<AspectNode,AspectNode>();
    // // compute the level parent map
    // for (AspectNode node : this.graph.nodeSet()) {
    // if (NestingAspect.isMetaElement(node)) {
    // // by the correctness of the aspect graph we know that
    // // there is at most one outgoing edge, which is a parent edge
    // // and points to a meta-node of the opposite nature
    // Set<AspectEdge> outEdges = this.graph.outEdgeSet(node);
    // if (!outEdges.isEmpty()) {
    // AspectNode parentNode =
    // outEdges.iterator().next().opposite();
    // parentMap.put(node, parentNode);
    // }
    // }
    // }
    // for (AspectNode node : this.graph.nodeSet()) {
    // if (NestingAspect.isMetaElement(node)
    // && !metaLevelMap.containsKey(node)) {
    // addLevel(metaLevelMap, nameLevelMap, subLevelCountMap,
    // parentMap, node);
    // }
    // }
    // // add sub-levels for universal levels that do not have a sub-level yet
    // Set<TreeIndex> implicitLevels = new HashSet<TreeIndex>();
    // for (Map.Entry<TreeIndex,Integer> levelEntry :
    // subLevelCountMap.entrySet()) {
    // TreeIndex level = levelEntry.getKey();
    // if (level.isUniversal() && levelEntry.getValue().equals(0)) {
    // boolean universal = true;
    // for (Map.Entry<AspectNode,TreeIndex> metaLevelEntry :
    // metaLevelMap.entrySet()) {
    // if (metaLevelEntry.getValue().equals(level)) {
    // universal =
    // NestingAspect.isForall(metaLevelEntry.getKey());
    // break;
    // }
    // }
    // if (universal) {
    // implicitLevels.add(levelEntry.getKey().getChild(0));
    // }
    // }
    // }
    // for (TreeIndex implicitLevel : implicitLevels) {
    // subLevelCountMap.put(implicitLevel.getParent(), 1);
    // subLevelCountMap.put(implicitLevel, 0);
    // }
    // }
    //
    // /**
    // * Adds a nesting level for a given node to an existing level map.
    // * Recursively ascends from the node to the top level.
    // * @param nodeLevelMap mapping from aspect meta-nodes to levels (output
    // * parameter)
    // * @param nameLevelMap mapping from level names to levels (output
    // parameter)
    // * @param childCountMap mapping to the nodes of which we have already
    // * computed the level, to their number of children encountered so far
    // * (output parameter)
    // * @param parentMap mapping from aspect meta-nodes to their parents
    // * @param node the node to be added to <code>levelMap</code> and
    // * <code>childCountMap</code>
    // * @return the level assigned to <code>node</code>
    // */
    // private TreeIndex addLevel(Map<AspectNode,TreeIndex> nodeLevelMap,
    // Map<String,TreeIndex> nameLevelMap,
    // Map<TreeIndex,Integer> childCountMap,
    // Map<AspectNode,AspectNode> parentMap, AspectNode node) {
    // TreeIndex result;
    // AspectNode parentNode = parentMap.get(node);
    // // find the tree index of parentNode
    // TreeIndex parentLevel;
    // if (parentNode == null) {
    // // this node is top level
    // parentLevel = new TreeIndex();
    // } else {
    // // this node is a child node
    // // maybe the parent is known
    // parentLevel = nodeLevelMap.get(parentNode);
    // if (parentLevel == null) {
    // // recursively add the parent
    // parentLevel =
    // addLevel(nodeLevelMap, nameLevelMap, childCountMap,
    // parentMap, parentNode);
    // }
    // }
    // int childNr = childCountMap.get(parentLevel);
    // result = parentLevel.getChild(childNr);
    // childCountMap.put(parentLevel, childNr + 1);
    // String name = NestingAspect.getLevelName(node);
    // if (name != null && name.length() > 0) {
    // nameLevelMap.put(name, result);
    // }
    // nodeLevelMap.put(node, result);
    // childCountMap.put(result, 0);
    // result.setPositive(NestingAspect.isPositive(node));
    // return result;
    // }

    /**
     * Creates an image for a given aspect node. Node numbers are copied.
     * @param node the node to be copied
     * @return the fresh node
     * @throws FormatException if <code>node</code> does not occur in a correct
     *         way in <code>context</code>
     */
    protected Node computeNodeImage(AspectNode node) throws FormatException {
        if (getAttributeValue(node) == null) {
            return DefaultNode.createNode(node.getNumber());
        } else {
            return this.attributeFactory.createAttributeNode(node);
        }
    }

    /**
     * Creates a an edge by copying a given edge under a given node mapping. The
     * mapping is assumed to have images for all end nodes.
     * @param edge the edge for which an image is to be created
     * @param elementMap the mapping of the end nodes
     * @return the new edge; may be <code>null</code> if the edge stands for an
     *         attribute value
     * @throws FormatException if <code>edge</code> does not occur in a correct
     *         way in <code>context</code>
     */
    protected Edge computeEdgeImage(AspectEdge edge,
            Map<? extends Node,Node> elementMap) throws FormatException {
        Node[] ends = new Node[edge.endCount()];
        for (int i = 0; i < ends.length; i++) {
            Node endImage = elementMap.get(edge.end(i));
            if (endImage == null) {
                throw new FormatException(
                    "Cannot compute image of '%s'-edge: %s node does not have image",
                    edge.label(), i == Edge.SOURCE_INDEX ? "source" : "target");
            }
            ends[i] = endImage;
        }
        // compute the label; either a DefaultLabel or a RegExprLabel
        if (getAttributeValue(edge) == null) {
            return createEdge(ends, parse(edge));// createRuleLabel(edge.label()));
        } else {
            return this.attributeFactory.createAttributeEdge(edge, ends);
        }
    }

    /**
     * Constructs a negative application condition based on a LHS graph and a
     * set of graph elements that should make up the NAC target. The connection
     * between LHS and NAC target is given by identity, i.e., those elements in
     * the NAC set that are in the LHS graph are indeed LHS elements.
     * @param lhs the LHS graph
     * @param nacNodeSet set of graph elements that should be turned into a NAC
     *        target
     */
    protected NotCondition computeNac(Graph lhs, Set<Node> nacNodeSet,
            Set<Edge> nacEdgeSet) {
        NotCondition result = null;
        // first check for merge end edge embargoes
        // they are characterised by the fact that there is precisely 1 element
        // in the nacElemSet, which is an edge
        if (nacNodeSet.size() == 0 && nacEdgeSet.size() == 1) {
            Edge embargoEdge = nacEdgeSet.iterator().next();
            if (RegExprLabel.isEmpty(embargoEdge.label())) {
                // this is supposed to be a merge embargo
                result = createMergeEmbargo(lhs, embargoEdge.ends());
            } else {
                // this is supposed to be an edge embargo
                result = createEdgeEmbargo(lhs, embargoEdge);
            }
        } else {
            // if we're here it means we couldn't make an embargo
            result = createNAC(lhs);
            Graph nacTarget = result.getTarget();
            NodeEdgeMap nacPatternMap = result.getRootMap();
            // add all nodes to nacTarget
            nacTarget.addNodeSet(nacNodeSet);
            // if the rule is injective, add all lhs nodes to the pattern map
            if (isInjective()) {
                for (Node node : lhs.nodeSet()) {
                    nacTarget.addNode(node);
                    nacPatternMap.putNode(node, node);
                }
            }
            // add edges and embargoes to nacTarget
            for (Edge edge : nacEdgeSet) {
                // for all variables in the edge, add a LHS edge to the nac that
                // binds the variable, if any
                Set<String> vars = VarSupport.getAllVars(edge);
                if (!vars.isEmpty()) {
                    Map<String,Edge> lhsVarBinders =
                        VarSupport.getVarBinders(lhs);
                    for (String nacVar : vars) {
                        Edge nacVarBinder = lhsVarBinders.get(nacVar);
                        if (nacVarBinder != null) {
                            // add the edge and its end nodes to the nac, as
                            // pre-matched elements
                            for (Node end : nacVarBinder.ends()) {
                                nacTarget.addNode(end);
                                nacPatternMap.putNode(end, end);
                            }
                            nacTarget.addEdge(nacVarBinder);
                            nacPatternMap.putEdge(nacVarBinder, nacVarBinder);
                        }
                    }
                }
                // add the endpoints that were not in the nac element set; it
                // means
                // they are lhs nodes, so add them to the nacMorphism as well
                for (int i = 0; i < edge.endCount(); i++) {
                    Node end = edge.end(i);
                    if (nacTarget.addNode(end)) {
                        // the node identity in the lhs is the same
                        nacPatternMap.putNode(end, end);
                    }
                }
                nacTarget.addEdge(edge);
            }
        }
        return result;
    }

    /**
     * Callback method to create a merge embargo.
     * @param context the context-graph
     * @param embargoNodes the nodes involved in this merge-embargo
     * @return the new {@link groove.trans.MergeEmbargo}
     * @see #toRule()
     */
    protected MergeEmbargo createMergeEmbargo(Graph context, Node[] embargoNodes) {
        return new MergeEmbargo(context, embargoNodes, getProperties());
    }

    /**
     * Callback method to create an edge embargo.
     * @param context the context-graph
     * @param embargoEdge the edge to be turned into an embargo
     * @return the new {@link groove.trans.EdgeEmbargo}
     * @see #toRule()
     */
    protected EdgeEmbargo createEdgeEmbargo(Graph context, Edge embargoEdge) {
        return new EdgeEmbargo(context, embargoEdge, getProperties());
    }

    /**
     * Callback method to create a general NAC on a given graph.
     * @param context the context-graph
     * @return the new {@link groove.trans.NotCondition}
     * @see #toRule()
     */
    protected NotCondition createNAC(Graph context) {
        return new NotCondition(context.newGraph(), getProperties());
    }

    /**
     * Factory method for rules.
     * @param ruleMorphism morphism of the new rule to be created
     * @param name name of the new rule to be created
     * @param priority the priority of the new rule.
     * @return the fresh rule created by the factory
     */
    protected Rule createRule(Morphism ruleMorphism, RuleName name,
            int priority, boolean confluent) {
        return new SPORule(ruleMorphism, name, priority, confluent,
            getProperties());
    }

    /**
     * Factory method for rules.
     * @param ruleMorphism morphism of the new rule to be created
     * @param rootMap pattern map leading into the LHS
     * @param coRootMap map of creator nodes in the parent rule to creator nodes
     *        of this rule
     * @param name name of the new rule to be created
     * @return the fresh rule created by the factory
     */
    protected SPORule createRule(Morphism ruleMorphism, NodeEdgeMap rootMap,
            NodeEdgeMap coRootMap, RuleName name) {
        return new SPORule(ruleMorphism, rootMap, coRootMap, name,
            getProperties());
    }

    /**
     * Factory method for universal conditions.
     * @param target target graph of the new condition
     * @param rootMap root map of the new condition
     * @param name name of the new condition to be created
     * @return the fresh condition
     */
    protected ForallCondition createForall(Graph target, NodeEdgeMap rootMap,
            RuleName name) {
        return new ForallCondition(target, rootMap, name, getProperties());
    }

    //
    // /**
    // * Factory method for negative conditions.
    // * @param target target graph of the new condition
    // * @param rootMap root map of the new condition
    // * @return the fresh condition
    // */
    // protected NotCondition createNeg(Graph target, NodeEdgeMap rootMap) {
    // return new NotCondition(target, rootMap, getProperties());
    // }

    /**
     * Callback method to create an ordinary graph morphism.
     * @see #toRule()
     */
    protected Morphism createMorphism(Graph dom, Graph cod) {
        return graphFactory.newMorphism(dom, cod);
    }

    /**
     * Callback factory method for a binary edge.
     * @param ends the end nodes for the new edge; should contain exactly two
     *        element
     * @param label the label for the new edge
     * @return a DefaultEdge with the given end nodes and label
     */
    protected Edge createEdge(Node[] ends, Label label) {
        assert ends.length == 2 : String.format(
            "Cannot create edge with end nodes %s", Arrays.toString(ends));
        Node source = ends[Edge.SOURCE_INDEX];
        Node target = ends[Edge.TARGET_INDEX];
        return DefaultEdge.createEdge(source, label, target);
    }

    /**
     * Indicates if an edge with a given label has a concrete image in a match,
     * which can be deleted or created.
     */
    private boolean hasConcreteImage(Label label) {
        return (label instanceof DefaultLabel)
            || RegExprLabel.getWildcardId(label) != null;
    }

    /**
     * Callback method to create a graph that can serve as LHS or RHS of a rule.
     * @see #getAspectGraph()
     */
    protected Graph createGraph() {
        return graphFactory.newGraph();
    }

    /**
     * Tests if a given morphism is injective; throws a
     * {@link IllegalArgumentException} if it is not.
     * @param morphism the morphisms to be check for injectivity
     * @throws IllegalArgumentException if <code>morphism</code> is not
     *         injective
     */
    protected void testInjective(NodeEdgeMap morphism) {
        if (morphism.size() < new HashSet<Node>(morphism.nodeMap().values()).size()) {
            throw new IllegalArgumentException("Morpism " + morphism
                + " should be injective");
        }
    }

    /**
     * Tests if a given graph is connected; throws a
     * {@link IllegalArgumentException} if it is not.
     * @param graph the graph to be tested for connectivity
     * @throws IllegalArgumentException if <code>graph</code> is not connected
     * @see AbstractGraph#isConnected()
     */
    protected void testConnected(Graph graph) {
        if (!((AbstractGraph<?>) graph).isConnected()) {
            throw new IllegalArgumentException("Graph " + graph
                + " should be connected");
        }
    }

    /**
     * Convenience method to map an array of nodes to an array of rule nodes,
     * given a mapping from individual nodes to rule nodes.
     * @param map the map in which to look for images
     * @param sources the nodes for which to get the images
     * @return the array containing the images for the given nodes, or
     *         <code>null</code> if one of the nodes does not have an image in
     *         <code>map</code>
     */
    protected <N extends Node> List<N> images(Map<Node,N> map, Node[] sources) {
        List<N> result = new ArrayList<N>();
        for (Node element : sources) {
            result.add(map.get(element));
        }
        return result;
    }

    /**
     * The name of the rule represented by this rule graph.
     */
    protected final RuleName name;
    /** The aspect graph representation of the rule. */
    private final AspectGraph graph;
    /** The attribute element factory for this view. */
    private final AttributeElementFactory attributeFactory;
    /** Set of labels occurring in this rule. */
    private final Set<Label> labelSet;
    /** Errors found while converting the view to a rule. */
    private List<String> errors;
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    /**
     * Mapping from the elements of the aspect graph representation to the
     * corresponding elements of the rule.
     */
    private NodeEdgeMap viewToRuleMap;
    /** Rule properties set for this rule. */
    private SystemProperties properties;

    /** Label for merges (merger edges and merge embargoes) */
    static public final RegExprLabel MERGE_LABEL = RegExpr.empty().toLabel();
    /** Graph factory used for building a graph view of this rule graph. */
    static private final GraphFactory graphFactory = GraphFactory.getInstance();
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    private static class Level extends DefaultFixable implements
            Comparable<Level> {
        /**
         * Constructs a new level, without setting parent or children.
         * @param levelNode the view level node representing this level; may be
         *        <code>null</code> for an implicit level
         */
        public Level(AspectNode levelNode) {
            this.levelNode = levelNode;
        }

        /**
         * Sets the parent of this level.
         * @param parent the parent of this level.
         */
        public void setParent(Level parent) {
            testFixed(false);
            assert this.parent == null;
            this.index.addAll(parent.index);
            this.index.add(parent.children.size());
            this.parent = parent;
            this.parent.children.add(this);
        }

        /** Returns the parent level of this tree index. */
        public Level getParent() {
            testFixed(true);
            return this.parent;
        }

        /** Returns a child level of this index. */
        public Level getChild(int childNr) {
            testFixed(true);
            return this.children.get(childNr);
        }

        /** Returns the child levels of this level. */
        public List<Level> getChildren() {
            testFixed(true);
            return this.children;
        }

        /**
         * Returns the name of this level, if any. The name is taken from the
         * representative level node.
         */
        public String getName() {
            return this.levelNode == null ? null
                    : NestingAspect.getLevelName(this.levelNode);
        }

        /**
         * Sets the level to fixed; in addition, adds an implicit existential
         * sublevel if this is a universal level without sublevels.
         */
        @Override
        public void setFixed() {
            if (NestingAspect.isForall(this.levelNode)
                && this.children.isEmpty()) {
                Level sublevel = new Level(null);
                sublevel.setParent(this);
                sublevel.setFixed();
            }
            super.setFixed();
        }

        /** Lexicographically compares the tree indices. */
        public int compareTo(Level o) {
            testFixed(true);
            o.testFixed(true);
            int result = 0;
            int upper = Math.min(this.index.size(), o.index.size());
            for (int i = 0; result == 0 && i < upper; i++) {
                result = this.index.get(i) - o.index.get(i);
            }
            if (result == 0) {
                if (upper < this.index.size()) {
                    result = +1;
                } else if (upper < o.index.size()) {
                    result = -1;
                }
            }
            return result;
        }

        /**
         * Tests if this level is smaller (i.e., higher up in the nesting tree)
         * than another. This is the case if the depth of this nesting does not
         * exceed that of the other, and the indices at every (common) level
         * coincide.
         */
        public boolean smallerThan(Level other) {
            testFixed(true);
            other.testFixed(true);
            boolean result = this.index.size() <= other.index.size();
            for (int i = 0; result && i < this.index.size(); i++) {
                result = this.index.get(i).equals(other.index.get(i));
            }
            return result;
        }

        /**
         * Returns the maximum of this tree index and a list of other, w.r.t.
         * the partial ordering imposed by {@link #smallerThan(Level)}. Returns
         * <code>null</code> if there is no maximum, i.e., some of the indices
         * are unordered.
         */
        public Level max(Level... others) {
            Level result = this;
            for (Level other : others) {
                if (result.smallerThan(other)) {
                    result = other;
                } else if (!other.smallerThan(result)) {
                    result = null;
                    break;
                }
            }
            return result;
        }

        /** Converts this level to an array of ints. */
        public int[] getIntArray() {
            testFixed(true);
            int[] result = new int[this.index.size()];
            for (int i = 0; i < this.index.size(); i++) {
                result[i] = this.index.get(i);
            }
            return result;

        }

        /** Indicates whether this is the top level. */
        public boolean isTopLevel() {
            testFixed(true);
            return this.parent == null;
        }

        /**
         * Indicates if this is an implicit level, i.e., without representative
         * level node. This could mean it's the top level, or the implicit
         * universal sub-level of the top level to accommodate existential top
         * level nodes, or the implicit existential sub-level of a childless
         * universal level.
         */
        public boolean isImplicit() {
            return this.levelNode == null;
        }

        /** Indicates whether this level is universal. */
        public boolean isUniversal() {
            testFixed(true);
            if (this.levelNode == null) {
                // it's an implicit level
                return !isTopLevel() && this.parent.isExistential();
            } else {
                return NestingAspect.isForall(this.levelNode);
            }
        }

        /** Indicates whether this level is existential. */
        public boolean isExistential() {
            testFixed(true);
            if (this.levelNode == null) {
                // it's an implicit level
                return isTopLevel() || this.parent.isUniversal();
            } else {
                return NestingAspect.isExists(this.levelNode);
            }
        }

        /**
         * Indicates, for a universal level, if the level is positive.
         * @see NestingAspect#isPositive(groove.view.aspect.AspectElement)
         */
        public boolean isPositive() {
            return NestingAspect.isPositive(this.levelNode);
        }

        /** The view node representing this quantification level. */
        private final AspectNode levelNode;
        /** The index uniquely identifying this level. */
        private final List<Integer> index = new ArrayList<Integer>();
        /** List of children of this tree index. */
        private final List<Level> children = new ArrayList<Level>();
        /** Parent of this tree index; may be <code>null</code> */
        private Level parent;
    }

    /** Tree of quantification levels occurring in this rule view. */
    private class LevelTree {
        /**
         * Returns the quantification level of a given aspect rule node.
         * @param node the node for which the quantification level is
         *        determined; must satisfy
         *        {@link RuleAspect#inRule(groove.view.aspect.AspectElement)}
         */
        public Level getLevel(AspectNode node) throws FormatException {
            Level result = getNodeLevelMap().get(node);
            if (result == null) {
                // find the corresponding quantifier node
                AspectNode nestingNode = getLevelNode(node);
                result =
                    nestingNode == null ? getTopLevel()
                            : getMetaLevelMap().get(nestingNode);
                assert result != null : String.format(
                    "No valid nesting level found for %s", node);
                String levelName = RuleAspect.getName(node);
                if (levelName != null) {
                    Level namedLevel = getNameLevelMap().get(levelName);
                    if (namedLevel == null) {
                        throw new FormatException(
                            "Undefined node nesting level '%s'", levelName);
                    }
                    if (result.smallerThan(namedLevel)) {
                        result = namedLevel;
                    } else {
                        throw new FormatException(
                            "Node nesting level '%s' incompatible with actual nesting",
                            levelName);
                    }
                }
            }
            return result;
        }

        /**
         * Returns the aspect node indicating the nesting level of a given node,
         * if any. This is a node to which there exists an
         * {@link NestingAspect#AT_LABEL}-edge in the view.
         */
        private AspectNode getLevelNode(AspectNode node) {
            AspectEdge levelEdge = null;
            for (AspectEdge edge : NewRuleView.this.graph.outEdgeSet(node)) {
                if (NestingAspect.isLevelEdge(edge)) {
                    levelEdge = edge;
                    break;
                }
            }
            return levelEdge == null ? null : levelEdge.opposite();
        }

        /**
         * Returns the quantification level of a given aspect rule edge.
         * @param edge the edge for which the quantification level is
         *        determined; must satisfy
         *        {@link RuleAspect#inRule(groove.view.aspect.AspectElement)}
         */
        public Level getLevel(AspectEdge edge) throws FormatException {
            Level sourceLevel = getNodeLevelMap().get(edge.source());
            assert sourceLevel != null : String.format(
                "Node level map %s does not contain source image for %s",
                this.nodeLevelMap, edge);
            Level targetLevel = getNodeLevelMap().get(edge.opposite());
            assert targetLevel != null : String.format(
                "Node level map %s does not contain target image for %s",
                getNodeLevelMap(), edge);
            Level result = sourceLevel.max(targetLevel);
            if (result == null) {
                throw new FormatException(
                    "Source and target of edge %s have incompatible nesting",
                    edge);
            }
            String levelName = NestingAspect.getLevelName(edge);
            if (levelName == null) {
                levelName = RuleAspect.getName(edge);
            }
            if (levelName != null) {
                Level edgeLevel = getNameLevelMap().get(levelName);
                if (edgeLevel == null) {
                    throw new FormatException(
                        "Undefined nesting level '%s' in edge %s", levelName,
                        edge);
                }
                if (result.smallerThan(edgeLevel)) {
                    result = edgeLevel;
                } else {
                    throw new FormatException(
                        "Nesting level %s in edge %s is incompatible with end nodes",
                        levelName, edge);
                }
            }
            return result;
        }

        /**
         * Lazily creates and returns the mapping from rule view nodes to the
         * corresponding quantification levels.
         */
        private Map<AspectNode,Level> getNodeLevelMap() {
            if (this.nodeLevelMap == null) {
                this.nodeLevelMap = new HashMap<AspectNode,Level>();
            }
            return this.nodeLevelMap;
        }

        /**
         * Lazily creates and returns the mapping from quantification nodes in
         * the rule view to quantification levels.
         */
        private Map<AspectNode,Level> getMetaLevelMap() {
            if (this.metaLevelMap == null) {
                initialise();
            }
            return this.metaLevelMap;
        }

        /**
         * Lazily creates and returns the mapping from quantification level
         * names in the rule view to quantification levels.
         */
        private Map<String,Level> getNameLevelMap() {
            if (this.nameLevelMap == null) {
                initialise();
            }
            return this.nameLevelMap;
        }

        /**
         * Lazily creates and returns the top level of the tree.
         */
        private Level getTopLevel() {
            if (this.topLevel == null) {
                this.topLevel = createLevel(null);
            }
            return this.topLevel;
        }

        /**
         * Lazily creates and returns the implicit universal level of the tree.
         * The implicit universal is a child of the top level that is only there
         * if there is a top-level existential node in the rule view.
         */
        private Level getImplicitUniversalLevel() {
            if (this.implicitUniversalLevel == null) {
                this.implicitUniversalLevel = createLevel(null);
                this.implicitUniversalLevel.setParent(getTopLevel());
            }
            return this.implicitUniversalLevel;
        }

        private void initialise() {
            // initialise the data structures
            this.metaLevelMap = new HashMap<AspectNode,Level>();
            this.nameLevelMap = new HashMap<String,Level>();
            this.levels = new HashSet<Level>();
            for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                if (NestingAspect.isMetaElement(node)) {
                    // look for the parent level
                    Level parentLevel;
                    // by the correctness of the aspect graph we know that
                    // there is at most one outgoing edge, which is a parent
                    // edge and points to the parent level node
                    Set<AspectEdge> outEdges =
                        NewRuleView.this.graph.outEdgeSet(node);
                    if (outEdges.isEmpty()) {
                        // this is a top node in the level node tree
                        // if it's existential, take the
                        // universal sublevel of the top level
                        if (NestingAspect.isExists(node)) {
                            parentLevel = getImplicitUniversalLevel();
                        } else {
                            parentLevel = getTopLevel();
                        }
                    } else {
                        AspectNode parentNode =
                            outEdges.iterator().next().opposite();
                        parentLevel = getLevelForNode(parentNode);
                    }
                    getLevelForNode(node).setParent(parentLevel);
                }
            }
            for (Level level : this.levels) {
                level.setFixed();
            }
        }

        /**
         * Lazily creates and returns a level object for a given level node.
         * @param levelNode the level node for which a level is to be created;
         *        should satisfy
         *        {@link NestingAspect#isMetaElement(groove.view.aspect.AspectElement)}
         */
        private Level getLevelForNode(AspectNode levelNode) {
            Level result = this.metaLevelMap.get(levelNode);
            if (result == null) {
                this.metaLevelMap.put(levelNode, result =
                    createLevel(levelNode));
                String name = NestingAspect.getLevelName(levelNode);
                if (name != null && name.length() > 0) {
                    getNameLevelMap().put(name, result);
                }
            }
            return result;
        }

        /**
         * Creates and returns a level for a given level node. Also stores the
         * newly created node in {@link #levels}.
         * @param levelNode the representative node for the level; may be
         *        <code>null</code>, if the level is implicit.
         */
        private Level createLevel(AspectNode levelNode) {
            Level result = new Level(levelNode);
            this.levels.add(result);
            return result;
        }

        /** The top level of the rule tree. */
        private Level topLevel;
        /** The implicit universal sublevel of the top level, if any. */
        private Level implicitUniversalLevel;
        /** The set of all levels in this tree. */
        private Set<Level> levels = new HashSet<Level>();
        /** mapping from nesting meta-nodes nodes to nesting levels. */
        private Map<AspectNode,Level> metaLevelMap;
        /** mapping from nesting level names to nesting levels. */
        private Map<String,Level> nameLevelMap;
        /** Mapping from view nodes to the corresponding nesting level. */
        private Map<AspectNode,Level> nodeLevelMap;
    }

    /**
     * Class implementing a mapping from quantification levels to view elements.
     */
    static private class LevelViewMap extends DefaultFixable {
        /** Adds a node to the set of nodes on a given quantification level. */
        public void addNode(Level index, AspectNode node) {
            testFixed(false);
            Set<AspectNode> nodes = this.nodeMap.get(index);
            if (nodes == null) {
                this.nodeMap.put(index, nodes = new HashSet<AspectNode>());
            }
            boolean fresh = nodes.add(node);
            assert fresh : String.format("Node %s already in node set %s",
                node, nodes);
        }

        /** Adds an edge to the set of edges on a given quantification level. */
        public void addEdge(Level index, AspectEdge edge) {
            testFixed(false);
            Set<AspectEdge> edges = this.edgeMap.get(index);
            if (edges == null) {
                this.edgeMap.put(index, edges = new HashSet<AspectEdge>());
            }
            boolean fresh = edges.add(edge);
            assert fresh : String.format("Node %s already in node set %s",
                edge, edges);
        }

        /**
         * Returns the set of equivalent nodes on a given quantification level.
         * @throws FormatException if a formatting error in the view is detected
         */
        public Set<AspectNode> getCell(Level index, AspectNode node)
            throws FormatException {
            return getPartition(index).get(node);
        }

        /**
         * Lazily creates and returns the partition on a given quantification
         * level.
         */
        private Map<AspectNode,Set<AspectNode>> getPartition(Level index)
            throws FormatException {
            setFixed();
            Map<AspectNode,Set<AspectNode>> result =
                this.partitionMap.get(index);
            if (result == null) {
                // create the partition from scratch
                result = new HashMap<AspectNode,Set<AspectNode>>();
                if (!index.isTopLevel()) {
                    // first copy the parent level partition
                    for (Map.Entry<AspectNode,Set<AspectNode>> parentEntry : getPartition(
                        index.getParent()).entrySet()) {
                        result.put(parentEntry.getKey(),
                            new HashSet<AspectNode>(parentEntry.getValue()));
                    }
                }
                // create singleton cells for the nodes appearing fresh on this
                // level
                for (AspectNode newNode : this.nodeMap.get(index)) {
                    Set<AspectNode> newCell = new HashSet<AspectNode>();
                    newCell.add(newNode);
                    result.put(newNode, newCell);
                }
                // now merge nodes whenever there is a merger
                for (AspectEdge newEdge : this.edgeMap.get(index)) {
                    if (RuleAspect.isMerger(newEdge)) {
                        Set<AspectNode> newCell = new HashSet<AspectNode>();
                        for (Node mergedNode : newEdge.ends()) {
                            newCell.addAll(result.get(mergedNode));
                        }
                        for (AspectNode node : newCell) {
                            result.put(node, newCell);
                        }
                    }
                }
            }
            return result;
        }

        /** Mapping from quantification levels to sets of nodes at that level. */
        private Map<Level,Set<AspectNode>> nodeMap =
            new HashMap<Level,Set<AspectNode>>();
        /** Mapping from quantification levels to sets of edges at that level. */
        private Map<Level,Set<AspectEdge>> edgeMap =
            new HashMap<Level,Set<AspectEdge>>();
        /** Mapping from quantification levels to node partitions on that level. */
        private Map<Level,Map<AspectNode,Set<AspectNode>>> partitionMap =
            new HashMap<Level,Map<AspectNode,Set<AspectNode>>>();
    }

    /** Class implementing a mapping from tree levels to subrule elements. */
    static private class LevelRuleMap {
        /** Returns the view-to-LHS element map at a given tree level. */
        public NodeEdgeMap getLhsMap(Level index) {
            return this.levelLhsMap.get(index);
        }

        /** Returns the view-to-RHS element map at a given tree level. */
        public NodeEdgeMap getRhsMap(Level index) {
            NodeEdgeMap result = this.levelRhsMap.get(index);
            if (result == null) {
                this.levelRhsMap.put(index, result = new NodeEdgeHashMap());

            }
            return result;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        public NodeEdgeMap getRootMap(Level index) {
            return getRootMap(index, this.levelLhsMap);
        }

        /**
         * Returns the mapping from the RHS rule elements at the parent level to
         * the RHS rule elements at this level.
         */
        public NodeEdgeMap getCoRootMap(Level index) {
            return getRootMap(index, this.levelRhsMap);
        }

        /**
         * Returns a mapping from the rule elements at the parent level to the
         * rule elements at this level, for a given level-to-elementmap-map.
         */
        private NodeEdgeMap getRootMap(Level index,
                Map<Level,NodeEdgeMap> levelMap) {
            NodeEdgeMap result = null;
            if (!index.isTopLevel()) {
                result = new NodeEdgeHashMap();
                NodeEdgeMap parentMap = levelMap.get(index.getParent());
                NodeEdgeMap myMap = levelMap.get(index);
                for (Map.Entry<Node,Node> parentEntry : parentMap.nodeMap().entrySet()) {
                    Node image = myMap.getNode(parentEntry.getKey());
                    if (image != null) {
                        Node oldImage =
                            result.putNode(parentEntry.getValue(), image);
                        assert oldImage == null || oldImage.equals(image);
                    }
                }
                for (Map.Entry<Edge,Edge> parentEntry : parentMap.edgeMap().entrySet()) {
                    Edge image = myMap.getEdge(parentEntry.getKey());
                    if (image != null) {
                        Edge oldImage =
                            result.putEdge(parentEntry.getValue(), image);
                        assert oldImage == null || oldImage.equals(image);
                    }
                }
            }
            return result;
        }

        /** Element map for the LHS at each tree level. */
        private final Map<Level,NodeEdgeMap> levelLhsMap =
            new HashMap<Level,NodeEdgeMap>();
        /** Element map for the RHS at each tree level. */
        private final Map<Level,NodeEdgeMap> levelRhsMap =
            new HashMap<Level,NodeEdgeMap>();
    }
}
