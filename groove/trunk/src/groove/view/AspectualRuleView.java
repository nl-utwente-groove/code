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
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
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
import groove.util.Groove;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.AttributeElementFactory;
import groove.view.aspect.NestingAspect;
import groove.view.aspect.ParameterAspect;
import groove.view.aspect.RuleAspect;

import java.io.File;
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
 * @version $Revision$
 */
public class AspectualRuleView extends AspectualView<Rule> implements RuleView {

    /**
     * Constructs a new rule graph on the basis of a given production rule.
     * @param rule the production rule for which a rule graph is to be
     *        constructed
     * @require <tt>rule != null</tt>
     */
    public AspectualRuleView(Rule rule) {
        this.name = rule.getName();
        this.rule = rule;
        this.properties = rule.getProperties();
        this.viewToRuleMap = new NodeEdgeHashMap();
        this.labelSet = new HashSet<Label>();
        this.graph = computeAspectGraph(rule, this.viewToRuleMap);
        this.attributeFactory =
            new AttributeElementFactory(this.graph, this.properties);
    }

    /**
     * Constructs a rule view from an aspect graph. The rule properties are
     * explicitly given.
     * @param graph the graph to be converted (non-null)
     * @param properties object specifying rule properties, such as injectivity
     *        etc (nullable)
     */
    public AspectualRuleView(AspectGraph graph, SystemProperties properties) {
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

    /** Invokes {@link #AspectualRuleView(Rule)} to construct a rule graph. */
    public RuleView newInstance(Rule rule) throws FormatException {
        return new AspectualRuleView(rule);
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
        NodeEdgeMap viewToRuleMap = new NodeEdgeHashMap();
        // ParameterAspect map with id's bound to nodes
        SortedMap<Integer,Node> lhsParameterMap = new TreeMap<Integer,Node>();
        SortedMap<Integer,Node> creatorParameterMap =
            new TreeMap<Integer,Node>();
        Set<Node> parameters = new HashSet<Node>();

        Set<String> errors = new TreeSet<String>(this.graph.getErrors());
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        TreeIndex topLevel = new TreeIndex();
        // mapping from nesting meta-nodes nodes to nesting levels
        Map<AspectNode,TreeIndex> metaLevelMap =
            new HashMap<AspectNode,TreeIndex>();
        // mapping from nesting level names to nesting levels
        Map<String,TreeIndex> nameLevelMap = new HashMap<String,TreeIndex>();
        // mapping from nesting levels to the number of sub-levels
        Map<TreeIndex,Integer> subLevelCountMap =
            new HashMap<TreeIndex,Integer>();
        subLevelCountMap.put(topLevel, 0);
        createLevelMap(metaLevelMap, nameLevelMap, subLevelCountMap);
        //
        // mapping from rule nodes to nesting levels
        Map<AspectNode,TreeIndex> nodeLevelMap =
            new HashMap<AspectNode,TreeIndex>();
        // mapping from nesting levels to sets of elements on the corresponding
        // level
        // (the inverse of nodeLevelMap)
        Map<TreeIndex,Map<AspectNode,Boolean>> nestedNodesMap =
            new TreeMap<TreeIndex,Map<AspectNode,Boolean>>();
        Map<TreeIndex,Map<AspectEdge,Boolean>> nestedEdgesMap =
            new TreeMap<TreeIndex,Map<AspectEdge,Boolean>>();
        for (TreeIndex level : subLevelCountMap.keySet()) {
            nestedNodesMap.put(level, new HashMap<AspectNode,Boolean>());
            nestedEdgesMap.put(level, new HashMap<AspectEdge,Boolean>());
        }
        try {
            // add nodes to nesting data structures
            int creatorParametersStartAt = -1;
            for (AspectNode node : this.graph.nodeSet()) {
                if (RuleAspect.inRule(node)) {
                    AspectNode nestingNode = getNestingNode(node);
                    TreeIndex level =
                        nestingNode == null ? topLevel
                                : metaLevelMap.get(nestingNode);
                    assert level != null : String.format(
                        "No valid nesting level found for %s", node);
                    String levelName = RuleAspect.getName(node);
                    if (levelName != null) {
                        TreeIndex namedLevel = nameLevelMap.get(levelName);
                        if (namedLevel == null) {
                            throw new FormatException(
                                "Undefined node nesting level '%s'", levelName);
                        }
                        level = level.max(namedLevel);
                        if (level.smallerThan(namedLevel)) {
                            level = namedLevel;
                        } else {
                            throw new FormatException(
                                "Node nesting level '%s' incompatible with actual nesting",
                                levelName);
                        }
                    }
                    // add the node to the appropriate level where it should be
                    // processed
                    if (RuleAspect.isCreator(node) && level.isUniversal()) {
                        for (int child = 0; child < subLevelCountMap.get(level); child++) {
                            addNodeToLevel(node, true, level.getChild(child),
                                nestedNodesMap, subLevelCountMap);
                        }
                    } else {
                        addNodeToLevel(node, true, level, nestedNodesMap,
                            subLevelCountMap);
                    }
                    if (RuleAspect.inNAC(node)) {
                        // correct level for NACs
                        level = level.getNegated();
                    }
                    nodeLevelMap.put(node, level);
                    Node nodeImage = computeNodeImage(node);
                    // check if the node is a parameter
                    Integer nr = ParameterAspect.getParNumber(node);
                    if (nr != null) {
                        if (!RuleAspect.inLHS(node)) {
                            if (creatorParametersStartAt == -1
                                || creatorParametersStartAt > nr) {
                                creatorParametersStartAt = nr;
                            }
                            // throw new
                            // FormatException("Rule parameter %d only allowed on LHS nodes",
                            // nr);
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
                System.err.println("doing nr: " + nextNr);
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
                    TreeIndex sourceLevel = nodeLevelMap.get(edge.source());
                    assert sourceLevel != null : String.format(
                        "Node level map %s does not contain source image for %s",
                        nodeLevelMap, edge);
                    TreeIndex targetLevel = nodeLevelMap.get(edge.opposite());
                    assert targetLevel != null : String.format(
                        "Node level map %s does not contain target image for %s",
                        nodeLevelMap, edge);
                    TreeIndex level = sourceLevel.max(targetLevel);
                    if (level == null) {
                        throw new FormatException(
                            "Source and target of edge %s have incompatible nesting",
                            edge);
                    }
                    String levelName = NestingAspect.getLevelName(edge);
                    if (levelName == null) {
                        levelName = RuleAspect.getName(edge);
                    }
                    if (levelName != null) {
                        TreeIndex edgeLevel = nameLevelMap.get(levelName);
                        if (edgeLevel == null) {
                            throw new FormatException(
                                "Undefined nesting level '%s' in edge %s",
                                levelName, edge);
                        }
                        if (level.smallerThan(edgeLevel)) {
                            level = edgeLevel;
                        } else {
                            throw new FormatException(
                                "Nesting level %s in edge %s is incompatible with end nodes",
                                levelName, edge);
                        }
                    }
                    boolean isNextLevelCreator =
                        RuleAspect.isCreator(edge) && level.isUniversal();
                    if (level.isUniversal() && hasConcreteImage(edge.label())) { // createRuleLabel(edge.label())))
                        // {
                        // add the edge and its end nodes as stale to the next
                        // (rule) level
                        for (int child = 0; child < subLevelCountMap.get(level); child++) {
                            addEdgeToLevel(edge, isNextLevelCreator,
                                level.getChild(child), nestedNodesMap,
                                nestedEdgesMap);
                        }
                    }
                    if (!isNextLevelCreator) {
                        if (level.isNegated()) {
                            // this is an artificial (auxiliary) level
                            // the matching detects negative application
                            // conditions,
                            // so actually add the edge to the level above
                            level = level.getParent();
                        }
                        addEdgeToLevel(edge, true, level, nestedNodesMap,
                            nestedEdgesMap);
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
            Map<TreeIndex,Condition> levelRuleMap =
                new HashMap<TreeIndex,Condition>();
            for (TreeIndex level : nestedNodesMap.keySet()) {
                Map<AspectNode,Boolean> levelNodes = nestedNodesMap.get(level);
                Map<AspectEdge,Boolean> levelEdges = nestedEdgesMap.get(level);
                AbstractCondition<?> condition =
                    computeFlatRule(viewToRuleMap, levelNodes, levelEdges,
                        level.isExistential());
                levelRuleMap.put(level, condition);
                TreeIndex parentLevel =
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
                    String levelName = null;
                    for (Map.Entry<String,TreeIndex> nameLevelEntry : nameLevelMap.entrySet()) {
                        if (nameLevelEntry.getValue().equals(level)) {
                            levelName = nameLevelEntry.getKey();
                            break;
                        }
                    }
                    String ruleNameSuffix = Groove.toString(level.toArray());
                    if (levelName != null) {
                        ruleNameSuffix = levelName + ruleNameSuffix;
                    } else {
                        ruleNameSuffix = this.name + ruleNameSuffix;
                    }
                    condition.setName(new RuleName(ruleNameSuffix));
                    levelRuleMap.get(parentLevel).addSubCondition(condition);
                }
            }
            rule = (SPORule) levelRuleMap.get(topLevel);
            rule.setPriority(getPriority());
            rule.setConfluent(isConfluent());
            rule.setParameters(new ArrayList<Node>(lhsParameterMap.values()),
                new ArrayList<Node>(creatorParameterMap.values()), parameters);
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
    private void addNodeToLevel(AspectNode node, boolean fresh,
            TreeIndex level,
            Map<TreeIndex,Map<AspectNode,Boolean>> nestedNodesMap,
            Map<TreeIndex,Integer> subLevelCountMap) {
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
    private void addEdgeToLevel(AspectEdge edge, boolean fresh,
            TreeIndex level,
            Map<TreeIndex,Map<AspectNode,Boolean>> nestedNodesMap,
            Map<TreeIndex,Map<AspectEdge,Boolean>> nestedEdgesMap) {
        nestedEdgesMap.get(level).put(edge, fresh);
        for (Node end : edge.ends()) {
            TreeIndex nodeLevel = new TreeIndex(level);
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
                // we may have creator nodes on universal levels, if they were
                // actually created
                // on the level above
                assert existential || RuleAspect.inLHS(node)
                    || !nodeEntry.getValue() : String.format(
                    "Creator node %s should be existential", node);
                if (RuleAspect.inLHS(node)) {
                    ruleMorph.putNode(nodeImage, nodeImage);
                } else if (!nodeEntry.getValue()) {
                    coRootMap.putNode(nodeImage, nodeImage);
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
                // assert edgeImage != null : String.format("Image of %s not in
                // map", edge);
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
                        // } else if (!hasConcreteImage(edgeImage.label())) {
                        // throw new FormatException(
                        // "Regular label '%s' may not be used on creators",
                        // edgeImage.label());
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

    /** Returns the aspect node indicating the nesting level of a given node. */
    private AspectNode getNestingNode(AspectNode node) {
        AspectEdge levelEdge = null;
        for (AspectEdge edge : this.graph.outEdgeSet(node)) {
            if (NestingAspect.isLevelEdge(edge)) {
                levelEdge = edge;
                break;
            }
        }
        AspectNode result = levelEdge == null ? null : levelEdge.opposite();
        // if (result != null && !NestingAspect.isMetaElement(result)) {
        // throw new FormatException("Nesting level edge %s does not have proper
        // meta-node target", levelEdge);
        // }
        return result;
    }

    /**
     * Computes various level-related structures. The parameters are expected to
     * be empty structures, which are filled in by the method (serving as output
     * parameters).
     * @param metaLevelMap mapping from aspect meta-nodes to the corresponding
     *        level
     * @param nameLevelMap mapping from level names to levels
     * @param subLevelCountMap mapping from levels to the number of sub-levels
     */
    private void createLevelMap(Map<AspectNode,TreeIndex> metaLevelMap,
            Map<String,TreeIndex> nameLevelMap,
            Map<TreeIndex,Integer> subLevelCountMap) {
        Map<AspectNode,AspectNode> parentMap =
            new HashMap<AspectNode,AspectNode>();
        // compute the level parent map
        for (AspectNode node : this.graph.nodeSet()) {
            if (NestingAspect.isMetaElement(node)) {
                // by the correctness of the aspect graph we know that
                // there is at most one outgoing edge, which is a parent edge
                // and points to a meta-node of the opposite nature
                Set<AspectEdge> outEdges = this.graph.outEdgeSet(node);
                if (!outEdges.isEmpty()) {
                    AspectNode parentNode =
                        outEdges.iterator().next().opposite();
                    parentMap.put(node, parentNode);
                }
            }
        }
        for (AspectNode node : this.graph.nodeSet()) {
            if (NestingAspect.isMetaElement(node)
                && !metaLevelMap.containsKey(node)) {
                addLevel(metaLevelMap, nameLevelMap, subLevelCountMap,
                    parentMap, node);
            }
        }
        // add sub-levels for universal levels that do not have a sub-level yet
        Set<TreeIndex> implicitLevels = new HashSet<TreeIndex>();
        for (Map.Entry<TreeIndex,Integer> levelEntry : subLevelCountMap.entrySet()) {
            TreeIndex level = levelEntry.getKey();
            if (level.isUniversal() && levelEntry.getValue().equals(0)) {
                boolean universal = true;
                for (Map.Entry<AspectNode,TreeIndex> metaLevelEntry : metaLevelMap.entrySet()) {
                    if (metaLevelEntry.getValue().equals(level)) {
                        universal =
                            NestingAspect.isForall(metaLevelEntry.getKey());
                        break;
                    }
                }
                if (universal) {
                    implicitLevels.add(levelEntry.getKey().getChild(0));
                }
            }
        }
        for (TreeIndex implicitLevel : implicitLevels) {
            subLevelCountMap.put(implicitLevel.getParent(), 1);
            subLevelCountMap.put(implicitLevel, 0);
        }
    }

    /**
     * Adds a nesting level for a given node to an existing level map.
     * Recursively ascends from the node to the top level.
     * @param nodeLevelMap mapping from aspect meta-nodes to levels (output
     *        parameter)
     * @param nameLevelMap mapping from level names to levels (output parameter)
     * @param childCountMap mapping to the nodes of which we have already
     *        computed the level, to their number of children encountered so far
     *        (output parameter)
     * @param parentMap mapping from aspect meta-nodes to their parents
     * @param node the node to be added to <code>levelMap</code> and
     *        <code>childCountMap</code>
     * @return the level assigned to <code>node</code>
     */
    private TreeIndex addLevel(Map<AspectNode,TreeIndex> nodeLevelMap,
            Map<String,TreeIndex> nameLevelMap,
            Map<TreeIndex,Integer> childCountMap,
            Map<AspectNode,AspectNode> parentMap, AspectNode node) {
        TreeIndex result;
        AspectNode parentNode = parentMap.get(node);
        // find the tree index of parentNode
        TreeIndex parentLevel;
        if (parentNode == null) {
            // this node is top level
            parentLevel = new TreeIndex();
        } else {
            // this node is a child node
            // maybe the parent is known
            parentLevel = nodeLevelMap.get(parentNode);
            if (parentLevel == null) {
                // recursively add the parent
                parentLevel =
                    addLevel(nodeLevelMap, nameLevelMap, childCountMap,
                        parentMap, parentNode);
            }
        }
        int childNr = childCountMap.get(parentLevel);
        result = parentLevel.getChild(childNr);
        childCountMap.put(parentLevel, childNr + 1);
        String name = NestingAspect.getLevelName(node);
        if (name != null && name.length() > 0) {
            nameLevelMap.put(name, result);
        }
        nodeLevelMap.put(node, result);
        childCountMap.put(result, 0);
        result.setPositive(NestingAspect.isPositive(node));
        return result;
    }

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
     * Computes an aspect graph representation of the rule stored in this rule
     * view.
     */
    protected AspectGraph computeAspectGraph(Rule rule,
            NodeEdgeMap viewToRuleMap) {
        AspectGraph result = createAspectGraph();
        // start with lhs
        Map<Node,AspectNode> lhsNodeMap = new HashMap<Node,AspectNode>();
        // add lhs nodes
        for (Node lhsNode : rule.lhs().nodeSet()) {
            AspectValue nodeRole =
                rule.getMorphism().containsKey(lhsNode) ? READER : ERASER;
            AspectNode nodeImage = computeAspectNode(result, nodeRole, lhsNode);
            result.addNode(nodeImage);
            lhsNodeMap.put(lhsNode, nodeImage);
            viewToRuleMap.putNode(nodeImage, lhsNode);
        }
        // add lhs edges
        for (Edge lhsEdge : rule.lhs().edgeSet()) {
            AspectValue edgeRole =
                rule.getMorphism().containsKey(lhsEdge) ? READER : ERASER;
            AspectEdge edgeImage =
                computeAspectEdge(images(lhsNodeMap, lhsEdge.ends()),
                    unparse(lhsEdge), edgeRole, lhsEdge);
            result.addEdge(edgeImage);
            viewToRuleMap.putEdge(edgeImage, lhsEdge);
            this.labelSet.add(lhsEdge.label());
        }
        // now add the rhs
        Map<Node,AspectNode> rhsNodeMap = new HashMap<Node,AspectNode>();
        // add rhs nodes and mergers to rule graph
        // first find out which rhs nodes correspond to readers
        for (Node lhsNode : rule.lhs().nodeSet()) {
            Node rhsNode = rule.getMorphism().getNode(lhsNode);
            if (rhsNode != null) {
                // we have a rhs reader node
                // check if we had it before (in which case we have a merger)
                if (rhsNodeMap.containsKey(rhsNode)) {
                    // yes, it's a merger
                    List<AspectNode> ends =
                        Arrays.asList(new AspectNode[] {
                            lhsNodeMap.get(lhsNode), rhsNodeMap.get(rhsNode)});
                    result.addEdge(computeAspectEdge(ends,
                        unparse(MERGE_LABEL), CREATOR, null));
                } else {
                    // no, it's a "fresh" reader node
                    rhsNodeMap.put(rhsNode, lhsNodeMap.get(lhsNode));
                }
            }
        }
        // the rhs nodes not yet dealt with must be creators
        // iterate over the rhs nodes
        for (Node rhsNode : rule.rhs().nodeSet()) {
            if (!rhsNodeMap.containsKey(rhsNode)) {
                AspectNode nodeImage =
                    computeAspectNode(result, CREATOR, rhsNode);
                result.addNode(nodeImage);
                rhsNodeMap.put(rhsNode, nodeImage);
                viewToRuleMap.putNode(nodeImage, rhsNode);
            }
        }
        // add rhs edges
        for (Edge rhsEdge : rule.rhs().edgeSet()) {
            if (!rule.getMorphism().containsValue(rhsEdge)) {
                List<AspectNode> endImages = images(rhsNodeMap, rhsEdge.ends());
                Edge edgeImage =
                    (computeAspectEdge(endImages, unparse(rhsEdge), CREATOR,
                        rhsEdge));
                result.addEdge(edgeImage);
                viewToRuleMap.putEdge(edgeImage, rhsEdge);
                this.labelSet.add(rhsEdge.label());
            }
        }
        // now add the NACs
        for (Condition nac : rule.getSubConditions()) {
            NodeEdgeMap nacMorphism = nac.getRootMap();
            if (nac instanceof MergeEmbargo) {
                result.addEdge(computeAspectEdge(images(lhsNodeMap,
                    ((MergeEmbargo) nac).getNodes()), unparse(MERGE_LABEL),
                    EMBARGO, null));
            } else {
                // NOTE: we're assuming the NAC is injective and connected,
                // otherwise no rule graph can be given
                testInjective(nacMorphism);
                // also store the nac into a graph, to test for connectedness
                AspectGraph nacGraph = createAspectGraph();
                // store the mapping from the NAC target nodes to the rule graph
                Map<Node,AspectNode> nacNodeMap =
                    new HashMap<Node,AspectNode>();
                // first register the lhs nodes
                for (Map.Entry<Node,Node> nacEntry : nacMorphism.nodeMap().entrySet()) {
                    AspectNode nacNodeImage = lhsNodeMap.get(nacEntry.getKey());
                    nacNodeMap.put(nacEntry.getValue(), nacNodeImage);
                    nacGraph.addNode(nacNodeImage);
                }
                // add this nac's nodes
                for (Node nacNode : nac.getTarget().nodeSet()) {
                    if (!nacNodeMap.containsKey(nacNode)) {
                        AspectNode nacNodeImage =
                            computeAspectNode(result, EMBARGO, nacNode);
                        nacNodeMap.put(nacNode, nacNodeImage);
                        viewToRuleMap.putNode(nacNodeImage, nacNode);
                        result.addNode(nacNodeImage);
                        nacGraph.addNode(nacNodeImage);
                    }
                }
                Set<Edge> nacEdgeSet =
                    new HashSet<Edge>(nac.getTarget().edgeSet());
                nacEdgeSet.removeAll(nacMorphism.edgeMap().values());
                // add this nac's edges
                for (Edge nacEdge : nacEdgeSet) {
                    List<AspectNode> endImages =
                        images(nacNodeMap, nacEdge.ends());
                    AspectEdge nacEdgeImage =
                        computeAspectEdge(endImages, unparse(nacEdge), EMBARGO,
                            nacEdge);
                    result.addEdge(nacEdgeImage);
                    viewToRuleMap.putEdge(nacEdgeImage, nacEdge);
                    this.labelSet.add(nacEdge.label());
                    nacGraph.addEdge(nacEdgeImage);
                }
                testConnected(nacGraph);
            }
        }
        GraphProperties graphProperties = new GraphProperties();
        graphProperties.setConfluent(rule.isConfluent());
        graphProperties.setPriority(rule.getPriority());
        GraphInfo.setProperties(result, graphProperties);
        result.setFixed();
        return result;
    }

    /** Callback factory method to create an empty aspect graph. */
    protected AspectGraph createAspectGraph() {
        return new AspectGraph();
    }

    /**
     * Factory method for aspect nodes.
     * @param graph the graph in which the node is to be inserted
     * @param role the role of the node to be created
     * @param original the node for which we want a copy; used to determine the
     *        attribute aspect value of the resulting node
     * 
     * @return the fresh rule node
     */
    protected AspectNode computeAspectNode(AspectGraph graph, AspectValue role,
            Node original) {
        AspectNode result = graph.createNode();
        if (role != null) {
            try {
                result.setDeclaredValue(role);
            } catch (FormatException exc) {
                assert false : String.format(
                    "Fresh node %s cannot have two rule aspect values", result);
            }
        }
        AspectValue attributeValue =
            AttributeAspect.getAttributeValueFor(original);
        if (attributeValue != null) {
            try {
                result.setDeclaredValue(attributeValue);
            } catch (FormatException exc) {
                assert false : String.format(
                    "Fresh node %s cannot have two attribute aspect values",
                    result);
            }
        }
        return result;
    }

    /**
     * Factory method for aspect edges.
     * @param ends the end-point for the fresh rule-edge
     * @param label the label of the fresh rule-edge
     * @param role the role of the fresh rule-edge
     * @param edge original edge for which the newly created aspect edge is an
     *        image. Used to determine the attribute aspect value of the result;
     *        may be <code>null</code>
     * @return the fresh rule-edge
     */
    protected AspectEdge computeAspectEdge(List<AspectNode> ends,
            DefaultLabel label, AspectValue role, Edge edge) {
        AspectValue attributeValue =
            edge == null ? null : AttributeAspect.getAttributeValueFor(edge);
        try {
            if (attributeValue == null) {
                return new AspectEdge(ends, label, role);
            } else {
                return new AspectEdge(ends, label, role, attributeValue);
            }
        } catch (FormatException exc) {
            assert false : String.format(
                "Fresh '%s'-edge cannot have two values for the same aspect",
                label);
            return null;
        }
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
    // /**
    // * The priority of the rule represented by this rule graph.
    // */
    // protected final int priority;
    //
    // /**
    // * The enabledness of the rule view.
    // */
    // protected final boolean enabled;
    // /**
    // * The confluency of the rule view.
    // */
    // protected final boolean confluent;

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

    /**
     * This main is provided for testing purposes only.
     * @param args names of XML files to be used as test input
     */
    static public void main(String[] args) {
        System.out.printf("Test of %s%n", AspectualRuleView.class);
        System.out.println("=================");
        for (String element : args) {
            try {
                testFile(new File(element));
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Loads a graph from a file and tests its conversion from aspect graph to
     * rule and back, using {@link #testTranslation(String,AspectGraph)}.
     * Recursively descends into directories.
     */
    private static void testFile(File file) {
        if (file.isDirectory()) {
            for (File nestedFile : file.listFiles()) {
                testFile(nestedFile);
            }
        } else {
            try {
                Graph plainGraph = Groove.loadGraph(file);
                if (plainGraph != null) {
                    System.out.printf("Testing %s%n", file);
                    testTranslation(file.getName(),
                        AspectGraph.newInstance(plainGraph));
                    System.out.println(" - OK");
                }
            } catch (FormatException exc) {
                // do nothing (skip)
            } catch (Exception exc) {
                // do nothing (skip)
            }
        }
    }

    /** Tests the translation from an aspect graph to a rule and back. */
    private static void testTranslation(String name, AspectGraph graph)
        throws FormatException, FormatException {
        // construct rule graph
        AspectualRuleView ruleGraph = graph.toRuleView(null);
        // convert rule graph into rule
        System.out.print("    Constructing rule from rule graph: ");
        Rule rule = ruleGraph.toRule();
        System.out.println("OK");
        // convert rule back into rule graph and test for isomorphism
        System.out.print("    Reconstructing rule graph from rule: ");
        AspectualRuleView newRuleGraph = new AspectualRuleView(rule);
        System.out.println("OK");
        System.out.print("    Testing for isomorphism of original and reconstructed rule graph: ");
        if (isoChecker.areIsomorphic(newRuleGraph.getAspectGraph(),
            ruleGraph.getAspectGraph())) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
            System.out.println("Resulting rule:");
            System.out.println("--------------");
            System.out.println(rule);
            System.out.println("Original rule graph");
            System.out.println("-----------------");
            System.out.println(ruleGraph.getAspectGraph());
            System.out.println("Reconstructed rule graph");
            System.out.println("------------------------");
            System.out.println(newRuleGraph.getAspectGraph());
        }
    }

    /** Label for merges (merger edges and merge embargoes) */
    static public final RegExprLabel MERGE_LABEL = RegExpr.empty().toLabel();
    /** Isomorphism checker (used for testing purposes). */
    static private final IsoChecker isoChecker =
        DefaultIsoChecker.getInstance(true);
    /** Graph factory used for building a graph view of this rule graph. */
    static private final GraphFactory graphFactory = GraphFactory.getInstance();
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    private static class TreeIndex extends ArrayList<Integer> implements
            Comparable<TreeIndex> {
        /** Constructs the tree index of the root. */
        public TreeIndex() {
            // empty
        }

        /**
         * Constructs a copy of a given tree index.
         * @param level the index to be copied
         */
        TreeIndex(TreeIndex level) {
            super(level);
        }

        /** Returns the parent level of this tree index. */
        public TreeIndex getParent() {
            TreeIndex result = new TreeIndex(this);
            result.remove(result.size() - 1);
            return result;
        }

        /** Returns a child level of this index. */
        public TreeIndex getChild(int childNr) {
            TreeIndex result = new TreeIndex(this);
            result.add(childNr);
            return result;
        }

        /**
         * Returns a level that stands for a negation sub-level. All NAC nodes
         * and edges of a given level will be assigned this negated level.
         */
        public TreeIndex getNegated() {
            return getChild(-1);
        }

        /** Lexicographically compares the tree indices. */
        public int compareTo(TreeIndex o) {
            int result = 0;
            int upper = Math.min(size(), o.size());
            for (int i = 0; result == 0 && i < upper; i++) {
                result = get(i) - o.get(i);
            }
            if (result == 0) {
                if (upper < size()) {
                    result = +1;
                } else if (upper < o.size()) {
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
        public boolean smallerThan(TreeIndex other) {
            boolean result = size() <= other.size();
            for (int i = 0; result && i < size(); i++) {
                result = get(i).equals(other.get(i));
            }
            return result;
        }

        /**
         * Returns the maximum of this tree index and a list of other, w.r.t.
         * the ordering imposed by {@link #smallerThan(TreeIndex)}. Returns
         * <code>null</code> if there is no maximum, i.e., some of the indices
         * are unordered.
         */
        public TreeIndex max(TreeIndex... others) {
            TreeIndex result = this;
            for (TreeIndex other : others) {
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
            int[] result = new int[size()];
            for (int i = 0; i < size(); i++) {
                result[i] = get(i);
            }
            return result;

        }

        /** Indicates whether this is the top level. */
        public boolean isTopLevel() {
            return isEmpty();
        }

        /**
         * Indicates whether this level is negated (i.e., the last element is a
         * negative index).
         */
        public boolean isNegated() {
            return !isEmpty() && get(size() - 1) < 0;
        }

        /** Indicates whether this level is universal (i.e., of odd depth). */
        public boolean isUniversal() {
            return size() % 2 == 1 && !isNegated();
        }

        /** Indicates whether this level is existential (i.e., of even depth). */
        public boolean isExistential() {
            return size() % 2 == 0 && !isNegated();
        }

        /**
         * Indicates, for a universal level, if the level is positive.
         */
        public boolean isPositive() {
            return this.positive;
        }

        /**
         * Sets the positive flag of a universal level.
         */
        public void setPositive(boolean positive) {
            if (positive && !isUniversal()) {
                throw new IllegalStateException(
                    "Only universal levels can be positive");
            }
            this.positive = positive;
        }

        /** Flag indicating, for a universal level, if it is positive. */
        private boolean positive;
    }
}