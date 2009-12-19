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
import groove.graph.AbstractGraph;
import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
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
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AttributeElementFactory;
import groove.view.aspect.NestingAspect;
import groove.view.aspect.ParameterAspect;
import groove.view.aspect.RuleAspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
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
public class NewRuleView extends AbstractView<Rule> implements RuleView {
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
        this.viewErrors =
            graph.getErrors().isEmpty() ? null : graph.getErrors();
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
        initialise();
        if (this.rule == null) {
            throw new FormatException(this.ruleErrors);
        } else {
            return this.rule;
        }
    }

    public List<String> getErrors() {
        initialise();
        return this.ruleErrors;
    }

    /** Returns the set of labels occurring in this rule. */
    public Set<Label> getLabels() {
        initialise();
        return this.levelMap == null ? Collections.<Label>emptySet()
                : this.levelMap.getLabelSet();
    }

    @Override
    public AspectGraph getView() {
        return this.graph;
    }

    @Override
    public NodeEdgeMap getMap() {
        initialise();
        return this.levelMap == null ? new NodeEdgeHashMap()
                : this.levelMap.getViewToRuleMap();
    }

    /**
     * Sets the properties of this view. This means that the previously
     * constructed model (if any) becomes invalid.
     */
    public final void setProperties(SystemProperties properties) {
        this.properties = properties;
        invalidate();
    }

    @Override
    public String toString() {
        return String.format("Rule view on '%s'", getName());
    }

    /**
     * @return Returns the properties.
     */
    private final SystemProperties getProperties() {
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
     * Invalidates any previous construction of the underlying rule. This means
     * the rule will be reconstructed when there is a call for it, using
     * {@link #initialise()}.
     */
    private void invalidate() {
        this.attributeFactory = null;
        this.rule = null;
        this.ruleErrors = null;
        this.levelTree = null;
        this.levelMap = null;
    }

    /** Initialises the derived data structures. */
    private void initialise() {
        // only do something if there is something to be done
        if (this.attributeFactory == null) {
            this.attributeFactory =
                new AttributeElementFactory(this.graph, this.properties);
            if (this.viewErrors == null) {
                // the view graph contains errors, so we can't compute the rule
                this.levelTree = new LevelTree();
                this.levelMap = new LevelMap();
                try {
                    this.levelMap.initialise();
                    this.rule = computeRule();
                    this.ruleErrors = Collections.<String>emptyList();
                } catch (FormatException exc) {
                    this.ruleErrors = exc.getErrors();
                }
            } else {
                this.ruleErrors = this.viewErrors;
            }
        }
    }

    /**
     * Callback method to compute a rule from the view graph. All auxiliary data
     * structures are assumed to be initialised but empty. After method return,
     * the structures are filled.
     * @throws FormatException if the view cannot be converted to a valid rule
     */
    private Rule computeRule() throws FormatException {
        SPORule rule;
        Set<String> errors = new TreeSet<String>(this.graph.getErrors());
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        try {
            Map<Level,Condition> ruleTree = new HashMap<Level,Condition>();
            for (Level level : this.levelTree.getLevels()) {
                AbstractCondition<?> condition = computeFlatRule(level);
                ruleTree.put(level, condition);
                if (!level.isTopLevel()) {
                    Level parentLevel = level.getParent();
                    ruleTree.get(parentLevel).addSubCondition(condition);
                    if (level.isExistential()) {
                        ((SPORule) condition).setParent(
                            (SPORule) ruleTree.get(parentLevel.getParent()),
                            level.getIntArray());
                    }
                }
            }
            rule = (SPORule) ruleTree.get(this.levelTree.getTopLevel());
            rule.setPriority(getPriority());
            rule.setConfluent(isConfluent());
            Parameters parameters = new Parameters();
            rule.setParameters(parameters.getInPars(), parameters.getOutPars(),
                parameters.getHiddenPars());
            rule.setFixed();

            if (TO_RULE_DEBUG) {
                System.out.println("Constructed rule: " + rule);
            }
        } catch (FormatException e) {
            rule = null;
            errors.addAll(e.getErrors());
        }
        if (errors.isEmpty()) {
            return rule;
        } else {
            throw new FormatException(new ArrayList<String>(errors));
        }
    }

    /**
     * Callback method to compute a rule (on a given nesting level) from sets of
     * aspect nodes and edges that appear on this level.
     * @param level the level of the (sub)rule to be computed
     */
    private AbstractCondition<?> computeFlatRule(Level level)
        throws FormatException {
        AbstractCondition<?> result;
        Set<String> errors = new TreeSet<String>();
        // create the new lhs
        Graph lhs = createGraph();
        // we separately keep a set of NAC-only elements
        Set<Node> nacNodeSet = new HashSet<Node>();
        Set<Edge> nacEdgeSet = new HashSet<Edge>();
        // create the new rhs
        Graph rhs = createGraph();
        // rule morphism for the resulting production rule
        Morphism ruleMorph = createMorphism(lhs, rhs);
        // first add nodes to lhs, rhs, morphism and left graph
        NodeEdgeMap lhsMap = this.levelMap.getLhsMap(level);
        NodeEdgeMap rhsMap = this.levelMap.getRhsMap(level);
        for (AspectNode node : this.levelMap.getNodes(level)) {
            Node lhsNodeImage = lhsMap.getNode(node);
            if (RuleAspect.inLHS(node)) {
                lhs.addNode(lhsNodeImage);
            }
            if (RuleAspect.inRHS(node)) {
                Node rhsNodeImage = rhsMap.getNode(node);
                rhs.addNode(rhsNodeImage);
                if (RuleAspect.inLHS(node)) {
                    ruleMorph.putNode(lhsNodeImage, rhsNodeImage);
                }
            }
            if (RuleAspect.inNAC(node)) {
                nacNodeSet.add(lhsNodeImage);
            }
        }
        try {
            // now add edges to lhs, rhs and morphism
            // remember from which nodes a type edge are added and deleted
            Set<Node> deletedTypes = new HashSet<Node>();
            Map<Node,Label> addedTypes = new HashMap<Node,Label>();
            for (AspectEdge edge : this.levelMap.getEdges(level)) {
                Edge lhsEdgeImage = lhsMap.getEdge(edge);
                if (RuleAspect.inLHS(edge)) {
                    assert lhsEdgeImage != null : String.format(
                        "View edge '%s' has no LHS image", edge);
                    lhs.addEdge(lhsEdgeImage);
                    if (!RuleAspect.inRHS(edge)
                        && lhsEdgeImage.label().isNodeType()) {
                        deletedTypes.add(lhsEdgeImage.source());
                    }
                }
                if (RuleAspect.inRHS(edge) && !RuleAspect.isMerger(edge)) {
                    Edge rhsEdgeImage = rhsMap.getEdge(edge);
                    assert rhsEdgeImage != null : String.format(
                        "View edge '%s' has no RHS image", edge);
                    rhs.addEdge(rhsEdgeImage);
                    assert level.isExistential() || RuleAspect.inLHS(edge);
                    if (RuleAspect.inLHS(edge)) {
                        ruleMorph.putEdge(lhsEdgeImage, rhsEdgeImage);
                    } else {
                        Label edgeLabel = rhsEdgeImage.label();
                        if (RuleAspect.inLHS(edge.source())
                            && edgeLabel.isNodeType()) {
                            addedTypes.put(rhsEdgeImage.source(), edgeLabel);
                        }
                    }
                }
                if (RuleAspect.inNAC(edge) && lhsEdgeImage != null) {
                    nacEdgeSet.add(lhsEdgeImage);
                }
            }
            // check if label variables are bound
            Set<String> boundVars = getVars(lhs.edgeSet(), true);
            Set<String> lhsVars = getVars(lhs.edgeSet(), false);
            if (!boundVars.containsAll(lhsVars)) {
                lhsVars.removeAll(boundVars);
                throw new FormatException(
                    "Left hand side variables %s not bound on left hand side",
                    lhsVars);
            }
            Set<String> rhsVars = getVars(rhs.edgeSet(), false);
            if (!boundVars.containsAll(rhsVars)) {
                rhsVars.removeAll(boundVars);
                throw new FormatException(
                    "Right hand side variables %s not bound on left hand side",
                    rhsVars);
            }
            Set<String> nacVars = getVars(nacEdgeSet, false);
            if (!boundVars.containsAll(nacVars)) {
                nacVars.removeAll(boundVars);
                throw new FormatException(
                    "NAC variables %s not bound on left hand side", nacVars);
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
            if (level.isExistential()) {
                result =
                    createRule(ruleMorph, this.levelMap.getRootMap(level),
                        this.levelMap.getCoRootMap(level), level.getName());
            } else {
                result =
                    createForall(lhs, this.levelMap.getRootMap(level),
                        level.getName(), level.isPositive());
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

    /**
     * Collects the variables from the regular expressions in a set of edges. A
     * flag indicates if it is just the bound variables we are interested in.
     * @param edgeSet the set of edges to investigate
     * @param bound if <code>true</code>, collect bound variables only
     * @return the requested set of variables
     */
    private Set<String> getVars(Set<? extends Edge> edgeSet, boolean bound) {
        Set<String> result = new HashSet<String>();
        for (Edge edge : edgeSet) {
            if (edge.label() instanceof RegExprLabel) {
                RegExpr expr = ((RegExprLabel) edge.label()).getRegExpr();
                result.addAll(bound ? expr.boundVarSet() : expr.allVarSet());
            }
        }
        return result;
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
    private NotCondition computeNac(Graph lhs, Set<Node> nacNodeSet,
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
    private MergeEmbargo createMergeEmbargo(Graph context, Node[] embargoNodes) {
        return new MergeEmbargo(context, embargoNodes, getProperties());
    }

    /**
     * Callback method to create an edge embargo.
     * @param context the context-graph
     * @param embargoEdge the edge to be turned into an embargo
     * @return the new {@link groove.trans.EdgeEmbargo}
     * @see #toRule()
     */
    private EdgeEmbargo createEdgeEmbargo(Graph context, Edge embargoEdge) {
        return new EdgeEmbargo(context, embargoEdge, getProperties());
    }

    /**
     * Callback method to create a general NAC on a given graph.
     * @param context the context-graph
     * @return the new {@link groove.trans.NotCondition}
     * @see #toRule()
     */
    private NotCondition createNAC(Graph context) {
        return new NotCondition(context.newGraph(), getProperties());
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
    private SPORule createRule(Morphism ruleMorphism, NodeEdgeMap rootMap,
            NodeEdgeMap coRootMap, String name) {
        return new SPORule(ruleMorphism, rootMap, coRootMap,
            new RuleName(name), getProperties());
    }

    /**
     * Factory method for universal conditions.
     * @param target target graph of the new condition
     * @param rootMap root map of the new condition
     * @param name name of the new condition to be created
     * @param positive if <code>true</code>, the condition should be matched
     *        non-vacuously
     * @return the fresh condition
     */
    private ForallCondition createForall(Graph target, NodeEdgeMap rootMap,
            String name, boolean positive) {
        ForallCondition result =
            new ForallCondition(target, rootMap, new RuleName(name),
                getProperties());
        if (positive) {
            result.setPositive();
        }
        return result;
    }

    /**
     * Callback method to create an ordinary graph morphism.
     * @see #toRule()
     */
    private Morphism createMorphism(Graph dom, Graph cod) {
        return graphFactory.newMorphism(dom, cod);
    }

    /**
     * Callback method to create a graph that can serve as LHS or RHS of a rule.
     * @see #getView()
     */
    private Graph createGraph() {
        return graphFactory.newGraph();
    }

    /**
     * The name of the rule represented by this rule graph.
     */
    private final RuleName name;
    /** The view graph representation of the rule. */
    private final AspectGraph graph;
    /**
     * The list of errors in the view graph; if <code>null</code>, there are no
     * view errors.
     */
    private final List<String> viewErrors;
    /** The attribute element factory for this view. */
    private AttributeElementFactory attributeFactory;
    /** The level tree for this rule view. */
    private LevelTree levelTree;
    /** The level map for this rule view. */
    private LevelMap levelMap;
    /** Errors found while converting the view to a rule. */
    private List<String> ruleErrors;
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    /** Rule properties set for this rule. */
    private SystemProperties properties;

    /** Label for merges (merger edges and merge embargoes) */
    static public final RegExprLabel MERGE_LABEL = RegExpr.empty().toLabel();
    /** Graph factory used for building a graph view of this rule graph. */
    static private final GraphFactory graphFactory = GraphFactory.getInstance();
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;

    /** Status reached by a {@link LevelData} after construction. */
    private static final int LEVEL_START = 0;
    /**
     * Status reached by a {@link LevelData} after the child levels have all
     * been added.
     */
    private static final int LEVEL_TREE_SET = 1;
    /**
     * Status reached by a {@link LevelData} after the rule nodes have all been
     * added.
     */
    private static final int LEVEL_NODES_SET = 2;
    /**
     * Status reached by a {@link LevelData} after the rule nodes and edges have
     * all been added.
     */
    private static final int LEVEL_FIXED = 3;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    private class Level extends DefaultFixable implements Comparable<Level> {
        /** Constructs the top level. */
        public Level() {
            this(null);
            this.indexFix.setFixed();
        }

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
            this.indexFix.setFixed();
        }

        /** Returns the parent level of this tree index. */
        public Level getParent() {
            testFixed(true);
            return this.parent;
        }

        /** Returns the child levels of this level. */
        public List<Level> getChildren() {
            testFixed(true);
            return this.children;
        }

        /**
         * Returns the name of this level. The name is either taken from the
         * representative level node, or constructed by concatenating the rule
         * name and the level indices.
         */
        public String getName() {
            String levelName =
                isImplicit() ? null
                        : NestingAspect.getLevelName(this.levelNode);
            if (levelName == null) {
                return NewRuleView.this.getName()
                    + (isTopLevel() ? ""
                            : Groove.toString(this.index.toArray()));
            } else {
                return levelName;
            }
        }

        /**
         * Sets the index and the level to fixed.
         */
        @Override
        public void setFixed() {
            this.indexFix.setFixed();
            super.setFixed();
        }

        /** Lexicographically compares the tree indices. */
        public int compareTo(Level o) {
            if (this.levelNode == null) {
                return -1;
            } else if (o.levelNode == null) {
                return 1;
            } else {
                return this.levelNode.compareTo(o.levelNode);
            }
        }

        /**
         * Tests if this level is smaller (i.e., higher up in the nesting tree)
         * than another. This is the case if the depth of this nesting does not
         * exceed that of the other, and the indices at every (common) level
         * coincide.
         */
        public boolean smallerThan(Level other) {
            testIndexFixed();
            other.testIndexFixed();
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

        /**
         * Converts this level to an array of {@code int}s. May only be called
         * after {@link #setParent(Level)}.
         */
        public int[] getIntArray() {
            testIndexFixed();
            int[] result = new int[this.index.size()];
            for (int i = 0; i < this.index.size(); i++) {
                result[i] = this.index.get(i);
            }
            return result;

        }

        /**
         * Indicates whether this is the top level. May only be called after
         * {@link #setParent(Level)}.
         */
        public boolean isTopLevel() {
            testIndexFixed();
            return this.parent == null;
        }

        /**
         * Indicates whether this level is universal. May only be called after
         * {@link #setParent(Level)}.
         */
        public boolean isUniversal() {
            testIndexFixed();
            if (isImplicit()) {
                // it's an implicit level
                return !isTopLevel() && this.parent.isExistential();
            } else {
                return NestingAspect.isForall(this.levelNode);
            }
        }

        /** Indicates whether this level is existential. */
        public boolean isExistential() {
            testIndexFixed();
            if (isImplicit()) {
                // it's an implicit level
                return isTopLevel() || this.parent.isUniversal();
            } else {
                return NestingAspect.isExists(this.levelNode);
            }
        }

        /**
         * Tests if the information needed to calculate the level index has been
         * set.
         */
        private void testIndexFixed() {
            this.indexFix.testFixed(true);
        }

        /**
         * Indicates, for a universal level, if the level is positive.
         * @see NestingAspect#isPositive(groove.view.aspect.AspectElement)
         */
        public boolean isPositive() {
            return NestingAspect.isPositive(this.levelNode);
        }

        @Override
        public String toString() {
            return this.index.toString();
        }

        /**
         * Indicates if this is an implicit level, i.e., without representative
         * level node. This could mean it's the top level, or the implicit
         * universal sub-level of the top level to accommodate existential top
         * level nodes, or the implicit existential sub-level of a childless
         * universal level.
         */
        private boolean isImplicit() {
            return this.levelNode == null;
        }

        /** Returns the meta-node associated with this level. */
        public final AspectNode getLevelNode() {
            return this.levelNode;
        }

        /** The view node representing this quantification level. */
        private final AspectNode levelNode;
        /** The index uniquely identifying this level. */
        private final List<Integer> index = new ArrayList<Integer>();
        /** List of children of this tree index. */
        private final List<Level> children = new ArrayList<Level>();
        /** Parent of this tree index; may be <code>null</code> */
        private Level parent;
        /**
         * Object to test if the parent has been set, meaning that the index is
         * fixed.
         */
        private final DefaultFixable indexFix = new DefaultFixable();
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
                getNodeLevelMap().put(node, result);
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
            Level sourceLevel = getLevel(edge.source());
            assert sourceLevel != null : String.format(
                "Node level map %s does not contain source image for %s",
                this.nodeLevelMap, edge);
            Level targetLevel = getLevel(edge.opposite());
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
         * @return Returns the set of all quantification levels.
         */
        public final Collection<Level> getLevels() {
            if (this.levelDataMap == null) {
                initialise();
            }
            return this.levelDataMap.keySet();
        }

        /**
         * Lazily creates and returns the mapping from rule view nodes to the
         * corresponding quantification levels.
         */
        private Map<AspectNode,LevelData> getNodeLevelMap() {
            if (this.nodeLevelMap == null) {
                this.nodeLevelMap = new HashMap<AspectNode,LevelData>();
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
        public Level getTopLevel() {
            if (this.topLevel == null) {
                initialise();
            }
            return this.topLevel;
        }

        private void initialise() {
            buildTree();
            initData();
        }

        private void buildTree() {
            this.topLevel = new Level();
            // initialise the data structures
            this.metaLevelMap = new HashMap<AspectNode,Level>();
            this.nameLevelMap = new HashMap<String,Level>();
            // First build an explicit tree of levels
            // Mapping from children to parent
            Map<Level,Level> parentMap = new HashMap<Level,Level>();
            // Mapping from parent to their set of children
            Map<Level,Set<Level>> metaNodeTree =
                new HashMap<Level,Set<Level>>();
            metaNodeTree.put(getTopLevel(), createChildren());
            for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                if (NestingAspect.isMetaElement(node)) {
                    Level nodeLevel = getLevelForNode(node);
                    metaNodeTree.put(nodeLevel, createChildren());
                    // look for the parent level
                    Level parentLevel;
                    // by the correctness of the aspect graph we know that
                    // there is at most one outgoing edge, which is a parent
                    // edge and points to the parent level node
                    Set<AspectEdge> outEdges =
                        NewRuleView.this.graph.outEdgeSet(node);
                    if (outEdges.isEmpty()) {
                        // this is a top node in the level node tree
                        assert NestingAspect.isForall(node) : String.format(
                            "Top level '%s' should not be existential", node);
                        parentLevel = getTopLevel();
                    } else {
                        AspectNode parentNode =
                            outEdges.iterator().next().opposite();
                        parentLevel = getLevelForNode(parentNode);
                    }
                    parentMap.put(nodeLevel, parentLevel);
                }
            }
            // Now fill the tree from the parent map
            for (Map.Entry<Level,Level> parentEntry : parentMap.entrySet()) {
                metaNodeTree.get(parentEntry.getValue()).add(
                    parentEntry.getKey());
            }
            List<Level> levels = new LinkedList<Level>();
            // Set the parentage in tree preorder
            Queue<Level> levelQueue = new LinkedList<Level>();
            levelQueue.add(getTopLevel());
            while (!levelQueue.isEmpty()) {
                Level next = levelQueue.poll();
                // set parent, except for top level
                if (parentMap.containsKey(next)) {
                    next.setParent(parentMap.get(next));
                }
                Set<Level> children = metaNodeTree.get(next);
                // add an implicit existential sub-level to childless universal
                // levels
                if (children.isEmpty() && next.isUniversal()) {
                    Level implicitChild = new Level(null);
                    metaNodeTree.put(implicitChild, createChildren());
                    parentMap.put(implicitChild, next);
                    children.add(implicitChild);
                }
                levelQueue.addAll(children);
            }
            // now fix all levels and build the level data map
            this.levelDataMap = new LinkedHashMap<Level,LevelData>();
            for (Level level : levels) {
                level.setFixed();
                LevelData parentData =
                    level.isTopLevel() ? null
                            : this.levelDataMap.get(level.getParent());
                LevelData thisData = new LevelData(level, parentData);
                this.levelDataMap.put(level, thisData);
            }
            // fix the level data
            for (LevelData data : this.levelDataMap.values()) {
                data.setMode(LevelMode.TREE_SET);
            }
        }

        private void initData() {
            try {
                // add nodes to nesting data structures
                for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                    if (RuleAspect.inRule(node)) {
                        Level level = NewRuleView.this.levelTree.getLevel(node);
                        addNode(level, node);
                    }
                }
                // add edges to nesting data structures
                for (AspectEdge edge : NewRuleView.this.graph.edgeSet()) {
                    if (RuleAspect.inRule(edge)) {
                        Level level = NewRuleView.this.levelTree.getLevel(edge);
                        addEdge(level, edge);
                    }
                }
            } finally {
                setFixed();
            }

        }

        /**
         * Creates an ordered set to store the children of a meta-node. The
         * ordering is based on the level node corresponding to the level.
         * @see Level#getLevelNode()
         */
        private Set<Level> createChildren() {
            return new TreeSet<Level>();
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
                this.metaLevelMap.put(levelNode, result = new Level(levelNode));
                String name = NestingAspect.getLevelName(levelNode);
                if (name != null && name.length() > 0) {
                    this.nameLevelMap.put(name, result);
                }
            }
            return result;
        }

        /** The top level of the rule tree. */
        private Level topLevel;
        /** The set of all levels in this tree. */
        private Map<Level,LevelData> levelDataMap;
        /** mapping from nesting meta-nodes nodes to nesting levels. */
        private Map<AspectNode,Level> metaLevelMap;
        /** mapping from nesting level names to nesting levels. */
        private Map<String,LevelData> nameLevelMap;
        /** Mapping from view nodes to the corresponding nesting level. */
        private Map<AspectNode,LevelData> nodeLevelMap;
    }

    /**
     * Class implementing a mapping from quantification levels to view elements.
     */
    private class LevelMap extends DefaultFixable {
        /** Initialises all data structures and fixes the map. */
        public void initialise() throws FormatException {
            try {
                for (Level index : NewRuleView.this.levelTree.getLevels()) {
                    this.nodeMap.put(index, new HashSet<AspectNode>());
                    this.edgeMap.put(index, new HashSet<AspectEdge>());
                }
                // add nodes to nesting data structures
                for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                    if (RuleAspect.inRule(node)) {
                        Level level = NewRuleView.this.levelTree.getLevel(node);
                        addNode(level, node);
                    }
                }
                // add edges to nesting data structures
                for (AspectEdge edge : NewRuleView.this.graph.edgeSet()) {
                    if (RuleAspect.inRule(edge)) {
                        Level level = NewRuleView.this.levelTree.getLevel(edge);
                        addEdge(level, edge);
                    }
                }
            } finally {
                setFixed();
            }
        }

        /**
         * Adds a node to the set of nodes on a given quantification level, as
         * well as all its sublevels.
         * @return the rule image of the added view node
         * @throws FormatException if there is an error in the context of the
         *         node
         */
        private Node addNode(Level index, AspectNode node)
            throws FormatException {
            testFixed(false);
            Node result = getNodeImage(node);
            Set<AspectNode> nodes = this.nodeMap.get(index);
            // put the node on this level, if it is supposed to be there
            if (isForThisLevel(index, node)) {
                boolean fresh = nodes.add(node);
                assert fresh : String.format("Node %s already in node set %s",
                    node, nodes);
            }
            // put the node on the sublevels, if it is supposed to be there
            if (isForNextLevel(index, node)) {
                for (Level sublevel : index.getChildren()) {
                    addNode(sublevel, node);
                }
            }
            return result;
        }

        //
        // /**
        // * Lazily creates and returns the current set of nodes at a given
        // level.
        // */
        // private Set<AspectNode> getNodesAt(Level index) {
        // Set<AspectNode> result = this.nodeMap.get(index);
        // if (result == null) {
        // this.nodeMap.put(index, result = new HashSet<AspectNode>());
        // while (!index.isTopLevel()) {
        // index = index.getParent();
        // getNodesAt(index);
        // }
        // }
        // return result;
        // }

        /**
         * Adds an edge to the set of edges on a given quantification level, as
         * well as all its sublevels.
         * @return the rule image of the added edge; may be <code>null</code> if
         *         the edge is not of the type to be added to the rule
         * @throws FormatException if there is an error in the context of the
         *         node
         */
        private Edge addEdge(Level index, AspectEdge edge)
            throws FormatException {
            testFixed(false);
            Edge result = getEdgeImage(edge);
            if (result == null) {
                return null;
            }
            Set<AspectEdge> edges = this.edgeMap.get(index);
            // put the edge on this level, if it is supposed to be there
            if (isForThisLevel(index, edge)) {
                boolean fresh = edges.add(edge);
                assert fresh : String.format("Node %s already in node set %s",
                    edge, edges);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                for (Node end : edge.ends()) {
                    Level ascendingLevel = index;
                    while (this.nodeMap.get(ascendingLevel).add(
                        (AspectNode) end)) {
                        assert !ascendingLevel.isTopLevel() : String.format(
                            "End node '%s' of '%s' not found at any level in '%s'",
                            end, edge, this.nodeMap);
                        ascendingLevel = ascendingLevel.getParent();
                    }
                }
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(index, edge)) {
                for (Level sublevel : index.getChildren()) {
                    addEdge(sublevel, edge);
                }
            }
            // create an image for the edge
            return result;
        }

        //
        // /**
        // * Lazily creates and returns the current set of edges at a given
        // level.
        // */
        // private Set<AspectEdge> getEdgesAt(Level index) {
        // Set<AspectEdge> result = this.edgeMap.get(index);
        // if (result == null) {
        // this.edgeMap.put(index, result = new HashSet<AspectEdge>());
        // while (!index.isTopLevel()) {
        // index = index.getParent();
        // getEdgesAt(index);
        // }
        // }
        // return result;
        // }

        /**
         * Indicates if a given element should be included on the level on which
         * it it is defined in the view. Node creators should not appear on
         * universal levels since those get translated to conditions, not rules;
         * instead they are pushed to the next (existential) sublevels.
         * @param index the level on which the element is defined
         * @param elem the element about which the question is asked
         */
        private boolean isForThisLevel(Level index, AspectElement elem) {
            return index.isExistential() || !RuleAspect.isCreator(elem);
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the view. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for active elements (erasers and creators) on universal levels, since
         * they cannot be handled there.
         * @param index the level on which the element is defined
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(Level index, AspectElement elem) {
            boolean result;
            if (elem instanceof AspectNode) {
                // we need to push nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result = isInjective();
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                result =
                    RegExprLabel.getWildcardId(((AspectEdge) elem).label()) != null;
            }
            if (!result) {
                result =
                    index.isUniversal()
                        && (RuleAspect.isEraser(elem) || RuleAspect.isCreator(elem));
            }
            return result;
        }

        /**
         * Returns the mapping from view elements to (unmerged) rule elements.
         */
        public NodeEdgeMap getViewToRuleMap() {
            testFixed(true);
            return this.viewToRuleMap;
        }

        /**
         * Returns the set of labels occurring in the rule.
         */
        public Set<Label> getLabelSet() {
            testFixed(true);
            return this.labelSet;
        }

        /** Returns the set of view edges on a given tree level. */
        public Set<AspectNode> getNodes(Level index) {
            testFixed(true);
            return this.nodeMap.get(index);
        }

        /** Returns the set of view edges on a given tree level. */
        public Set<AspectEdge> getEdges(Level index) {
            testFixed(true);
            return this.edgeMap.get(index);
        }

        /**
         * Lazily creates and returns the mapping from LHS view elements to rule
         * elements on a given level.
         */
        public NodeEdgeMap getLhsMap(Level index) {
            testFixed(true);
            NodeEdgeMap result = this.levelLhsMap.get(index);
            if (result == null) {
                this.levelLhsMap.put(index, result = computeLhsMap(index));
                while (!index.isTopLevel()) {
                    index = index.getParent();
                    getLhsMap(index);
                }
            }
            return result;
        }

        /**
         * Computes the mapping from LHS view elements to rule elements on a
         * given level.
         */
        private NodeEdgeMap computeLhsMap(Level index) {
            NodeEdgeMap result = new NodeEdgeHashMap();
            assert this.nodeMap.containsKey(index) : String.format(
                "Node map '%s' does not contain image for index '%s'",
                this.nodeMap, index);
            for (AspectNode viewNode : getNodes(index)) {
                if (RuleAspect.inLHS(viewNode) || RuleAspect.inNAC(viewNode)) {
                    Node ruleNode = this.viewToRuleMap.getNode(viewNode);
                    result.putNode(viewNode, ruleNode);
                }
            }
            for (AspectEdge viewEdge : getEdges(index)) {
                if (RuleAspect.inLHS(viewEdge) || RuleAspect.inNAC(viewEdge)) {
                    result.putEdge(viewEdge,
                        this.viewToRuleMap.getEdge(viewEdge));
                }
            }
            return result;
        }

        /**
         * Lazily creates and returns the mapping from RHS view elements to rule
         * elements on a given level.
         */
        public NodeEdgeMap getRhsMap(Level index) throws FormatException {
            NodeEdgeMap result = this.levelRhsMap.get(index);
            if (result == null) {
                this.levelRhsMap.put(index, result = computeRhsMap(index));
                while (!index.isTopLevel()) {
                    index = index.getParent();
                    getRhsMap(index);
                }
            }
            return result;
        }

        /**
         * Computes the mapping from RHS view elements to rule elements on a
         * given level.
         */
        private NodeEdgeMap computeRhsMap(Level index) throws FormatException {
            NodeEdgeMap result = new NodeEdgeHashMap();
            for (AspectNode viewNode : getNodes(index)) {
                if (RuleAspect.inRHS(viewNode)) {
                    AspectNode representative =
                        getRepresentative(index, viewNode);
                    Node ruleNode = this.viewToRuleMap.getNode(representative);
                    result.putNode(viewNode, ruleNode);
                }
            }
            for (AspectEdge viewEdge : getEdges(index)) {
                if (RuleAspect.inRHS(viewEdge)) {
                    result.putEdge(viewEdge, computeEdgeImage(viewEdge,
                        result.nodeMap()));
                }
            }
            return result;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        public NodeEdgeMap getRootMap(Level index) {
            return index.isTopLevel() ? null : getConnectingMap(
                this.levelLhsMap.get(index.getParent()),
                this.levelLhsMap.get(index));
        }

        /**
         * Returns the mapping from the RHS rule elements at the parent level to
         * the RHS rule elements at this level.
         */
        public NodeEdgeMap getCoRootMap(Level index) {
            return index.isTopLevel() ? null : getConnectingMap(
                this.levelRhsMap.get(index.getParent().getParent()),
                this.levelRhsMap.get(index));
        }

        /**
         * Returns a mapping from the rule elements at a parent level to the
         * rule elements at this level, given view-to-rule maps for both levels.
         */
        private NodeEdgeMap getConnectingMap(NodeEdgeMap parentMap,
                NodeEdgeMap myMap) {
            NodeEdgeMap result = new NodeEdgeHashMap();
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
            return result;
        }

        /**
         * Returns a representative node from the set of merged nodes on a given
         * quantification level.
         * @throws FormatException if a formatting error in the view is detected
         */
        public AspectNode getRepresentative(Level index, AspectNode node)
            throws FormatException {
            return getPartition(index).get(node).first();
        }

        /**
         * Lazily creates and returns the partition on a given quantification
         * level.
         */
        private Map<AspectNode,SortedSet<AspectNode>> getPartition(Level index)
            throws FormatException {
            setFixed();
            Map<AspectNode,SortedSet<AspectNode>> result =
                this.partitionMap.get(index);
            if (result == null) {
                result = computePartition(index);
            }
            return result;
        }

        /**
         * Computes the partition on a given quantification level.
         */
        private Map<AspectNode,SortedSet<AspectNode>> computePartition(
                Level index) throws FormatException {
            Map<AspectNode,SortedSet<AspectNode>> result =
                new HashMap<AspectNode,SortedSet<AspectNode>>();
            if (!index.isTopLevel()) {
                // first copy the parent level partition
                for (Map.Entry<AspectNode,SortedSet<AspectNode>> parentEntry : getPartition(
                    index.getParent()).entrySet()) {
                    result.put(parentEntry.getKey(), new TreeSet<AspectNode>(
                        parentEntry.getValue()));
                }
            }
            // create singleton cells for the nodes appearing fresh on this
            // level
            for (AspectNode newNode : getNodes(index)) {
                // test if the node is new
                if (!result.containsKey(newNode)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    newCell.add(newNode);
                    result.put(newNode, newCell);
                }
            }
            // now merge nodes whenever there is a merger
            for (AspectEdge newEdge : getEdges(index)) {
                if (RuleAspect.isMerger(newEdge)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    for (Node mergedNode : newEdge.ends()) {
                        newCell.addAll(result.get(mergedNode));
                    }
                    for (AspectNode node : newCell) {
                        result.put(node, newCell);
                    }
                }
            }
            return result;
        }

        /**
         * Lazily creates and returns a rule image for a given view node.
         * @param viewNode the node for which an image is to be created
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private Node getNodeImage(AspectNode viewNode) throws FormatException {
            Node result = this.viewToRuleMap.getNode(viewNode);
            if (result == null) {
                this.viewToRuleMap.putNode(viewNode, result =
                    computeNodeImage(viewNode));

            }
            return result;
        }

        /**
         * Creates an image for a given aspect node. Node numbers are copied.
         * @param node the node for which an image is to be created
         * @return the fresh node
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private Node computeNodeImage(AspectNode node) throws FormatException {
            if (getAttributeValue(node) == null) {
                return DefaultNode.createNode(node.getNumber());
            } else {
                return NewRuleView.this.attributeFactory.createAttributeNode(node);
            }
        }

        /**
         * Lazily creates and returns a rule image for a given view edge.
         * @param viewEdge the node for which an image is to be created
         * @return the rule edge corresponding to <code>viewEdge</code>; may be
         *         <code>null</code>
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private Edge getEdgeImage(AspectEdge viewEdge) throws FormatException {
            Edge result = this.viewToRuleMap.getEdge(viewEdge);
            if (result == null) {
                result =
                    computeEdgeImage(viewEdge, this.viewToRuleMap.nodeMap());
                if (result != null) {
                    this.viewToRuleMap.putEdge(viewEdge, result);
                    Label edgeLabel = result.label();
                    if (edgeLabel.isNodeType()) {
                        if (!result.source().equals(result.opposite())) {
                            throw new FormatException(
                                "Node type label '%s' only allowed on self-edges",
                                edgeLabel);
                        }
                    }
                    this.labelSet.add(edgeLabel);
                }
            }
            return result;
        }

        /**
         * Creates a an edge by copying a given edge under a given node mapping.
         * The mapping is assumed to have images for all end nodes.
         * @param edge the edge for which an image is to be created
         * @param elementMap the mapping of the end nodes
         * @return the new edge; may be <code>null</code> if the edge stands for
         *         an attribute value
         * @throws FormatException if <code>edge</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private Edge computeEdgeImage(AspectEdge edge,
                Map<? extends Node,Node> elementMap) throws FormatException {
            Node[] ends = new Node[edge.endCount()];
            for (int i = 0; i < ends.length; i++) {
                Node endImage = elementMap.get(edge.end(i));
                if (endImage == null) {
                    throw new FormatException(
                        "Cannot compute image of '%s'-edge: %s node does not have image",
                        edge.label(), i == Edge.SOURCE_INDEX ? "source"
                                : "target");
                }
                ends[i] = endImage;
            }
            // compute the label; either a DefaultLabel or a RegExprLabel
            if (getAttributeValue(edge) == null) {
                return createEdge(ends, parse(edge));// createRuleLabel(edge.label()));
            } else {
                return NewRuleView.this.attributeFactory.createAttributeEdge(
                    edge, ends);
            }
        }

        /**
         * Callback factory method for a binary edge.
         * @param ends the end nodes for the new edge; should contain exactly
         *        two element
         * @param label the label for the new edge
         * @return a DefaultEdge with the given end nodes and label
         */
        private Edge createEdge(Node[] ends, Label label) {
            assert ends.length == 2 : String.format(
                "Cannot create edge with end nodes %s", Arrays.toString(ends));
            Node source = ends[Edge.SOURCE_INDEX];
            Node target = ends[Edge.TARGET_INDEX];
            return DefaultEdge.createEdge(source, label, target);
        }

        /** Mapping from quantification levels to sets of nodes at that level. */
        private final Map<Level,Set<AspectNode>> nodeMap =
            new HashMap<Level,Set<AspectNode>>();
        /** Mapping from quantification levels to sets of edges at that level. */
        private final Map<Level,Set<AspectEdge>> edgeMap =
            new HashMap<Level,Set<AspectEdge>>();
        /** Mapping from quantification levels to node partitions on that level. */
        private final Map<Level,Map<AspectNode,SortedSet<AspectNode>>> partitionMap =
            new HashMap<Level,Map<AspectNode,SortedSet<AspectNode>>>();
        /**
         * Mapping from the elements of the aspect graph representation to the
         * corresponding elements of the rule.
         */
        private final NodeEdgeMap viewToRuleMap = new NodeEdgeHashMap();
        /** Set of all labels occurring in the rule. */
        private final Set<Label> labelSet = new HashSet<Label>();
        /** Element map for the LHS at each tree level. */
        private final Map<Level,NodeEdgeMap> levelLhsMap =
            new HashMap<Level,NodeEdgeMap>();
        /** Element map for the RHS at each tree level. */
        private final Map<Level,NodeEdgeMap> levelRhsMap =
            new HashMap<Level,NodeEdgeMap>();
    }

    /**
     * Class containing all elements on a given rule level.
     */
    private class LevelData {
        public LevelData(Level index, LevelData parent) {
            this.index = index;
            this.parent = parent;
            this.parent.addChild(this);
        }

        /** Adds a child level to this level. */
        private void addChild(LevelData child) {
            testMode(LevelMode.START);
            assert this.index.equals(child.index.parent);
            this.children.add(child);
        }

        /**
         * Considers adding a node to the set of nodes on this level. The node
         * is only really added if it satisfies
         * {@link #isForThisLevel(AspectElement)}; moreover, it is added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         * @throws FormatException if there is an error in the context of the
         *         node
         */
        public void maybeAddNode(AspectNode viewNode, Node ruleNode)
            throws FormatException {
            testMode(LevelMode.TREE_SET);
            // put the node on this level, if it is supposed to be there
            if (isForThisLevel(viewNode)) {
                addNode(viewNode, ruleNode);
            }
            // put the node on the sublevels, if it is supposed to be there
            if (isForNextLevel(viewNode)) {
                for (LevelData sublevel : this.children) {
                    sublevel.maybeAddNode(viewNode, ruleNode);
                }
            }
        }

        /**
         * Consider adding an edge to the set of edges on this level. The edge
         * is only really added if it satisfies
         * {@link #isForThisLevel(AspectElement)}; moreover, it is added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         * @throws FormatException if there is an error in the context of the
         *         edge
         */
        public void maybeAddEdge(AspectEdge viewEdge, Edge ruleEdge)
            throws FormatException {
            testMode(LevelMode.TREE_SET);
            // put the edge on this level, if it is supposed to be there
            if (isForThisLevel(viewEdge)) {
                addEdge(viewEdge, ruleEdge);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                for (int i = 0; i < viewEdge.endCount(); i++) {
                    LevelData ascendingLevel = this;
                    while (ascendingLevel.addNode((AspectNode) viewEdge.end(i),
                        ruleEdge.end(i))) {
                        assert !ascendingLevel.index.isTopLevel() : String.format(
                            "End node '%s' of '%s' not found at any level", i,
                            viewEdge);
                        ascendingLevel = ascendingLevel.parent;
                    }
                }
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(viewEdge)) {
                for (LevelData sublevel : this.children) {
                    sublevel.maybeAddEdge(viewEdge, ruleEdge);
                }
            }
        }

        /**
         * Indicates if a given element should be included on the level on which
         * it it is defined in the view. Node creators should not appear on
         * universal levels since those get translated to conditions, not rules;
         * instead they are pushed to the next (existential) sublevels.
         * @param elem the element about which the question is asked
         */
        private boolean isForThisLevel(AspectElement elem) {
            return this.index.isExistential() || !RuleAspect.isCreator(elem);
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the view. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for active elements (erasers and creators) on universal levels, since
         * they cannot be handled there.
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(AspectElement elem) {
            boolean result;
            if (elem instanceof AspectNode) {
                // we need to push nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result = isInjective();
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                result =
                    RegExprLabel.getWildcardId(((AspectEdge) elem).label()) != null;
            }
            if (!result) {
                result =
                    this.index.isUniversal()
                        && (RuleAspect.isEraser(elem) || RuleAspect.isCreator(elem));
            }
            return result;
        }

        /**
         * Adds a node to this level.
         * @return {@code true} if the node is fresh
         */
        private boolean addNode(AspectNode viewNode, Node ruleNode)
            throws FormatException {
            boolean result = true;
            if (RuleAspect.inLHS(viewNode) || RuleAspect.inNAC(viewNode)) {
                Node oldLhsNode = this.lhsMap.putNode(viewNode, ruleNode);
                assert oldLhsNode == null || oldLhsNode.equals(ruleNode) : String.format(
                    "Old and new LHS images '%s' and '%s' should be the same",
                    oldLhsNode, ruleNode);
                result = oldLhsNode == null;
                if (RuleAspect.inLHS(viewNode)) {
                    this.lhs.addNode(ruleNode);
                } else {
                    this.nacNodeSet.add(ruleNode);
                }
            }
            if (RuleAspect.inRHS(viewNode)) {
                AspectNode rhsNode = getRepresentative(viewNode);
                Node oldRhsNode = this.rhsMap.putNode(viewNode, rhsNode);
                assert oldRhsNode == null || oldRhsNode.equals(rhsNode) : String.format(
                    "Old and new RHS images '%s' and '%s' should be the same",
                    oldRhsNode, ruleNode);
                result &= oldRhsNode == null;
                this.rhs.addNode(ruleNode);
                if (RuleAspect.inLHS(viewNode)) {
                    this.ruleMorph.putNode(ruleNode, rhsNode);
                }
            }
            return result;
        }

        private void addEdge(AspectEdge viewEdge, Edge ruleEdge)
            throws FormatException {
            if (RuleAspect.inLHS(viewEdge) || RuleAspect.inNAC(viewEdge)) {
                this.lhsMap.putEdge(viewEdge, ruleEdge);
                if (RuleAspect.inLHS(viewEdge)) {
                    this.lhs.addEdge(ruleEdge);
                } else {
                    this.nacEdgeSet.add(ruleEdge);
                }
                if (!RuleAspect.inRHS(viewEdge)
                    && ruleEdge.label().isNodeType()) {
                    this.deletedTypes.add(ruleEdge.source());
                }
            }
            if (RuleAspect.inRHS(viewEdge)) {
                Edge rhsEdge =
                    computeEdgeImage(viewEdge, this.rhsMap.nodeMap());
                assert rhsEdge != null : String.format(
                    "View edge '%s' does not have image in %s", viewEdge,
                    this.rhsMap);
                this.rhsMap.putEdge(viewEdge, rhsEdge);
                this.rhs.addEdge(rhsEdge);
                if (RuleAspect.inLHS(viewEdge)) {
                    this.ruleMorph.putEdge(ruleEdge, rhsEdge);
                } else {
                    Label edgeLabel = rhsEdge.label();
                    if (RuleAspect.inLHS(viewEdge.source())
                        && edgeLabel.isNodeType()) {
                        this.addedTypes.put(rhsEdge.source(), edgeLabel);
                    }
                }

            }
        }

        /**
         * Lazily creates and returns the mapping from LHS view elements to rule
         * elements on a given level.
         */
        public NodeEdgeMap getLhsMap() {
            testMode(LevelMode.FIXED);
            return this.lhsMap;
        }

        /**
         * Lazily creates and returns the mapping from RHS view elements to rule
         * elements on a given level.
         */
        public NodeEdgeMap getRhsMap() throws FormatException {
            testMode(LevelMode.FIXED);
            return this.rhsMap;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        public NodeEdgeMap getRootMap() {
            testMode(LevelMode.FIXED);
            return this.index.isTopLevel() ? null : getConnectingMap(
                this.parent.lhsMap, this.lhsMap);
        }

        /**
         * Returns the mapping from the RHS rule elements at the parent level to
         * the RHS rule elements at this level.
         */
        public NodeEdgeMap getCoRootMap(Level index) {
            testMode(LevelMode.FIXED);
            return index.isTopLevel() ? null : getConnectingMap(
                this.parent.parent.rhsMap, this.rhsMap);
        }

        /**
         * Returns a mapping from the rule elements at a parent level to the
         * rule elements at this level, given view-to-rule maps for both levels.
         */
        private NodeEdgeMap getConnectingMap(NodeEdgeMap parentMap,
                NodeEdgeMap myMap) {
            NodeEdgeMap result = new NodeEdgeHashMap();
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
            return result;
        }

        /**
         * Returns a representative node from the set of merged nodes on a given
         * quantification level.
         * @throws FormatException if a formatting error in the view is detected
         */
        private AspectNode getRepresentative(AspectNode node)
            throws FormatException {
            return getPartition().get(node).first();
        }

        /**
         * Lazily creates and returns the partition on a given quantification
         * level.
         */
        private Map<AspectNode,SortedSet<AspectNode>> getPartition()
            throws FormatException {
            if (this.partition == null) {
                this.partition = computePartition();
            }
            return this.partition;
        }

        /**
         * Computes the partition on a given quantification level.
         */
        private Map<AspectNode,SortedSet<AspectNode>> computePartition()
            throws FormatException {
            Map<AspectNode,SortedSet<AspectNode>> result =
                new HashMap<AspectNode,SortedSet<AspectNode>>();
            if (!this.index.isTopLevel()) {
                // first copy the parent level partition
                for (Map.Entry<AspectNode,SortedSet<AspectNode>> parentEntry : this.parent.getPartition().entrySet()) {
                    result.put(parentEntry.getKey(), new TreeSet<AspectNode>(
                        parentEntry.getValue()));
                }
            }
            // create singleton cells for the nodes appearing fresh on this
            // level
            for (Node newNode : this.rhsMap.nodeMap().keySet()) {
                // test if the node is new
                if (!result.containsKey(newNode)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    newCell.add((AspectNode) newNode);
                    result.put((AspectNode) newNode, newCell);
                }
            }
            // now merge nodes whenever there is a merger
            for (Edge newEdge : this.rhsMap.edgeMap().keySet()) {
                if (RuleAspect.isMerger((AspectEdge) newEdge)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    for (Node mergedNode : newEdge.ends()) {
                        newCell.addAll(result.get(mergedNode));
                    }
                    for (AspectNode node : newCell) {
                        result.put(node, newCell);
                    }
                }
            }
            return result;
        }

        /**
         * Creates a an edge by copying a given edge under a given node mapping.
         * The mapping is assumed to have images for all end nodes.
         * @param edge the edge for which an image is to be created
         * @param elementMap the mapping of the end nodes
         * @return the new edge; may be <code>null</code> if the edge stands for
         *         an attribute value
         * @throws FormatException if <code>edge</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private Edge computeEdgeImage(AspectEdge edge,
                Map<? extends Node,Node> elementMap) throws FormatException {
            Node[] ends = new Node[edge.endCount()];
            for (int i = 0; i < ends.length; i++) {
                Node endImage = elementMap.get(edge.end(i));
                if (endImage == null) {
                    throw new FormatException(
                        "Cannot compute image of '%s'-edge: %s node does not have image",
                        edge.label(), i == Edge.SOURCE_INDEX ? "source"
                                : "target");
                }
                ends[i] = endImage;
            }
            // compute the label; either a DefaultLabel or a RegExprLabel
            if (getAttributeValue(edge) == null) {
                return createEdge(ends, parse(edge));// createRuleLabel(edge.label()));
            } else {
                return NewRuleView.this.attributeFactory.createAttributeEdge(
                    edge, ends);
            }
        }

        /**
         * Callback factory method for a binary edge.
         * @param ends the end nodes for the new edge; should contain exactly
         *        two element
         * @param label the label for the new edge
         * @return a DefaultEdge with the given end nodes and label
         */
        private Edge createEdge(Node[] ends, Label label) {
            assert ends.length == 2 : String.format(
                "Cannot create edge with end nodes %s", Arrays.toString(ends));
            Node source = ends[Edge.SOURCE_INDEX];
            Node target = ends[Edge.TARGET_INDEX];
            return DefaultEdge.createEdge(source, label, target);
        }

        /**
         * Sets the mode for this level data to a next value.
         * @param mode the new level mode.
         * @throws IllegalArgumentException if the transition from the current
         *         mode to {@code mode} is illegal.
         */
        public void setMode(LevelMode mode) throws IllegalArgumentException {
            if (this.mode.compareTo(mode) >= 0) {
                throw new IllegalArgumentException(String.format(
                    "Illegal mode transition from '%s' to '%s'", this.mode,
                    mode));
            }
            this.mode = mode;
            if (mode.equals(LevelMode.TREE_SET)) {
                // initialise the rule data structures
                this.lhs = createGraph();
                this.rhs = createGraph();
                this.ruleMorph = createMorphism(this.lhs, this.rhs);
                this.lhsMap = new NodeEdgeHashMap();
                this.rhsMap = new NodeEdgeHashMap();
                this.nacNodeSet = new HashSet<Node>();
                this.nacEdgeSet = new HashSet<Edge>();
                this.addedTypes = new HashMap<Node,Label>();
                this.deletedTypes = new HashSet<Node>();
            }
        }

        /**
         * Tests if the current level mode equals a given value.
         * @param mode the value for which is tested
         * @throws IllegalArgumentException if the current mode is not equal to
         *         {@code mode}
         */
        private void testMode(LevelMode mode) throws IllegalArgumentException {
            if (this.mode.compareTo(mode) == 0) {
                throw new IllegalArgumentException(String.format(
                    "Mode should be '%s' but is '%s'", mode, this.mode));
            }
        }

        private final Level index;
        /** Node partition on this quantification level. */
        private Map<AspectNode,SortedSet<AspectNode>> partition;
        /** Mapping from view nodes to LHS and NAC nodes. */
        private NodeEdgeMap lhsMap;
        /** Mapping from view nodes to RHS nodes. */
        private NodeEdgeMap rhsMap;
        /** The left hand side graph of the rule. */
        private Graph lhs;
        /** The right hand side graph of the rule. */
        private Graph rhs;
        /** Rule morphism (from LHS to RHS). */
        private Morphism ruleMorph;
        /** The set of nodes appearing in NACs. */
        private Set<Node> nacNodeSet;
        /** The set of edges appearing in NACs. */
        private Set<Edge> nacEdgeSet;
        /** Mapping from rule nodes to type labels added to that node. */
        private Map<Node,Label> addedTypes;
        /** Set of nodes for which a type label is deleted. */
        private Set<Node> deletedTypes;
        /** Parent level data. */
        private LevelData parent;
        /** Children level data. */
        private List<LevelData> children;
        /** The current mode of this level data. */
        private LevelMode mode = LevelMode.START;
    }

    /** Intermediate mode values for {@link LevelData}. */
    private static enum LevelMode {
        START, TREE_SET, FIXED
    }

    /** Class that can extract parameter information from the view graph. */
    private class Parameters {
        /** Lazily creates and returns the rule's input parameters. */
        public List<Node> getInPars() throws FormatException {
            if (this.inPars == null) {
                initialise();
            }
            return this.inPars;
        }

        /** Lazily creates and returns the rule's output parameters. */
        public List<Node> getOutPars() throws FormatException {
            if (this.outPars == null) {
                initialise();
            }
            return this.outPars;
        }

        /** Lazily creates and returns the rule's hidden parameters. */
        public Set<Node> getHiddenPars() throws FormatException {
            if (this.hiddenPars == null) {
                initialise();
            }
            return this.hiddenPars;
        }

        /** Initialises the internal data structures. */
        private void initialise() throws FormatException {
            SortedMap<Integer,Node> inParMap = new TreeMap<Integer,Node>();
            SortedMap<Integer,Node> outParMap = new TreeMap<Integer,Node>();
            this.hiddenPars = new HashSet<Node>();
            // set of all parameter numbers, to check duplicates
            Set<Integer> parNumbers = new HashSet<Integer>();
            // add nodes to nesting data structures
            for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                // check if the node is a parameter
                Integer nr = ParameterAspect.getParNumber(node);
                if (nr != null) {
                    if (!parNumbers.add(nr)) {
                        throw new FormatException(
                            "Parameter number '%d' occurs more than once", nr);
                    }
                    Level level = NewRuleView.this.levelTree.getLevel(node);
                    if (!level.isTopLevel()) {
                        throw new FormatException(
                            "Rule parameter '%d' only allowed on top existential level",
                            nr);
                    }
                    if (RuleAspect.inLHS(node)) {
                        Node nodeImage =
                            NewRuleView.this.levelMap.getLhsMap(level).getNode(
                                node);
                        if (nr.equals(0)) {
                            this.hiddenPars.add(nodeImage);
                        } else {
                            inParMap.put(nr, nodeImage);
                        }
                    } else if (RuleAspect.inRHS(node)) {
                        if (nr.equals(0)) {
                            throw new FormatException(
                                "Anonymous parameters should only occur on the left hand side");
                        }
                        Node nodeImage =
                            NewRuleView.this.levelMap.getLhsMap(level).getNode(
                                node);
                        outParMap.put(nr, nodeImage);
                    } else {
                        throw new FormatException(
                            "Parameter '%d' may not occur in NAC", nr);
                    }
                }
            }
            // test if parameters form a consecutive sequence
            for (int nr = 1; nr <= inParMap.size() + outParMap.size(); nr++) {
                if (!parNumbers.contains(nr)) {
                    throw new FormatException("Parameter number %d missing", nr);
                }
            }
            // test if LHS parameters come before RHS parameters
            if (!outParMap.isEmpty() && outParMap.firstKey() <= inParMap.size()) {
                throw new FormatException(
                    "Non-creator parameters should come before creator-parameters");
            }
            this.inPars = new ArrayList<Node>(inParMap.values());
            this.outPars = new ArrayList<Node>(outParMap.values());
        }

        /** The list of input parameters, in increasing parameter number. */
        private List<Node> inPars;
        /** The list of output parameters, in increasing parameter number. */
        private List<Node> outPars;
        /** Set of all rule parameter nodes */
        private Set<Node> hiddenPars;
    }
}
