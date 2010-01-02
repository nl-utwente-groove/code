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
import groove.view.aspect.AttributeAspect;
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
public class NewRuleView implements RuleView {
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
        if (this.ruleErrors.isEmpty()) {
            return this.rule;
        } else {
            throw new FormatException(this.ruleErrors);
        }
    }

    public List<String> getErrors() {
        initialise();
        return this.ruleErrors;
    }

    /** Returns the set of labels occurring in this rule. */
    public Set<Label> getLabels() {
        initialise();
        return this.levelTree == null ? Collections.<Label>emptySet()
                : this.levelTree.getLabelSet();
    }

    @Override
    public AspectGraph getView() {
        return this.graph;
    }

    @Override
    public NodeEdgeMap getMap() {
        initialise();
        return this.levelTree == null ? new NodeEdgeHashMap()
                : this.levelTree.getViewToRuleMap();
    }

    /**
     * Sets the properties of this view. This means that the previously
     * constructed model (if any) becomes invalid.
     */
    public final void setProperties(SystemProperties properties) {
        if (properties == null ? this.properties != null
                : !properties.equals(this.properties)) {
            this.properties = properties;
            invalidate();
        }
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
    }

    /** Initialises the derived data structures. */
    private void initialise() {
        // only do something if there is something to be done
        if (this.attributeFactory == null) {
            this.attributeFactory =
                new AttributeElementFactory(this.graph, this.properties);
            this.ruleErrors = new ArrayList<String>();
            if (this.viewErrors != null) {
                this.ruleErrors.addAll(this.viewErrors);
            }
            this.levelTree = new LevelMap();
            try {
                this.levelTree.initialise();
                this.rule = computeRule();
            } catch (FormatException exc) {
                this.ruleErrors.addAll(exc.getErrors());
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
        Set<String> errors = new TreeSet<String>();
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        Map<Level,Condition> ruleTree = new HashMap<Level,Condition>();
        for (Level level : this.levelTree.getLevels()) {
            try {
                AbstractCondition<?> condition = level.computeFlatRule();
                ruleTree.put(level, condition);
                LevelIndex index = level.getIndex();
                if (!index.isTopLevel()) {
                    Level parentLevel = level.getParent();
                    ruleTree.get(parentLevel).addSubCondition(condition);
                    if (index.isExistential()) {
                        ((SPORule) condition).setParent(
                            (SPORule) ruleTree.get(parentLevel.getParent()),
                            index.getIntArray());
                    }
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        rule = (SPORule) ruleTree.get(this.levelTree.getTopLevel());
        rule.setPriority(getPriority());
        rule.setConfluent(isConfluent());
        Parameters parameters = new Parameters();
        try {
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
            throw new FormatException(errors);
        }
    }

    /**
     * Creates an image for a given aspect node. Node numbers are copied.
     * @param node the node for which an image is to be created
     * @return the fresh node
     * @throws FormatException if <code>node</code> does not occur in a correct
     *         way in <code>context</code>
     */
    private Node computeNodeImage(AspectNode node) throws FormatException {
        if (getAttributeValue(node) == null) {
            return DefaultNode.createNode(node.getNumber());
        } else {
            return NewRuleView.this.attributeFactory.createAttributeNode(node);
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
    private Edge computeEdgeImage(AspectEdge edge,
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
            return createEdge(ends, edge.getModelLabel(true));
        } else {
            return NewRuleView.this.attributeFactory.createAttributeEdge(edge,
                ends);
        }
    }

    /**
     * Callback factory method for a binary edge.
     * @param ends the end nodes for the new edge; should contain exactly two
     *        element
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
    private LevelMap levelTree;
    /** Errors found while converting the view to a rule. */
    private List<String> ruleErrors;
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    /** Rule properties set for this rule. */
    private SystemProperties properties;

    /** Label for merges (merger edges and merge embargoes) */
    static public final Label MERGE_LABEL = RegExpr.empty().toLabel();
    /** Graph factory used for building a graph view of this rule graph. */
    static private final GraphFactory graphFactory = GraphFactory.getInstance();
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    private class LevelIndex extends DefaultFixable implements
            Comparable<LevelIndex> {
        /** Constructs the top level. */
        public LevelIndex() {
            this(null);
            this.indexFix.setFixed();
        }

        /**
         * Constructs a new level, without setting parent or children.
         * @param levelNode the view level node representing this level; may be
         *        <code>null</code> for an implicit level
         */
        public LevelIndex(AspectNode levelNode) {
            this.levelNode = levelNode;
        }

        /**
         * Sets the parent of this level.
         * @param parent the parent of this level.
         */
        public void setParent(LevelIndex parent) {
            testFixed(false);
            assert this.parent == null;
            this.index.addAll(parent.index);
            this.index.add(parent.children.size());
            this.parent = parent;
            this.parent.children.add(this);
            this.indexFix.setFixed();
        }

        /** Returns the parent level of this tree index. */
        public LevelIndex getParent() {
            testFixed(true);
            return this.parent;
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
        public int compareTo(LevelIndex o) {
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
        public boolean smallerThan(LevelIndex other) {
            testIndexFixed();
            other.testIndexFixed();
            boolean result = this.index.size() <= other.index.size();
            for (int i = 0; result && i < this.index.size(); i++) {
                result = this.index.get(i).equals(other.index.get(i));
            }
            return result;
        }

        /**
         * Converts this level to an array of {@code int}s. May only be called
         * after {@link #setParent(LevelIndex)}.
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
         * {@link #setParent(LevelIndex)}.
         */
        public boolean isTopLevel() {
            testIndexFixed();
            return this.parent == null;
        }

        /**
         * Indicates whether this level is universal. May only be called after
         * {@link #setParent(LevelIndex)}.
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

        /** The view node representing this quantification level. */
        private final AspectNode levelNode;
        /** The index uniquely identifying this level. */
        private final List<Integer> index = new ArrayList<Integer>();
        /** List of children of this tree index. */
        private final List<LevelIndex> children = new ArrayList<LevelIndex>();
        /** Parent of this tree index; may be <code>null</code> */
        private LevelIndex parent;
        /**
         * Object to test if the parent has been set, meaning that the index is
         * fixed.
         */
        private final DefaultFixable indexFix = new DefaultFixable();
    }

    /** Tree of quantification levels occurring in this rule view. */
    private class LevelMap extends DefaultFixable {
        public void initialise() throws FormatException {
            buildTree();
            initData();
        }

        /** Builds the level data maps. */
        private void buildTree() throws FormatException {
            this.topLevelIndex = new LevelIndex();
            // initialise the data structures
            this.metaIndexMap = new HashMap<AspectNode,LevelIndex>();
            this.nameIndexMap = new HashMap<String,LevelIndex>();
            // First build an explicit tree of levels
            // Mapping from children to parent
            Map<LevelIndex,LevelIndex> indexParentMap =
                new HashMap<LevelIndex,LevelIndex>();
            // Mapping from parent to their set of children
            Map<LevelIndex,Set<LevelIndex>> metaNodeTree =
                new HashMap<LevelIndex,Set<LevelIndex>>();
            metaNodeTree.put(this.topLevelIndex, createChildren());
            for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                if (NestingAspect.isMetaElement(node)) {
                    LevelIndex nodeLevel = getIndex(node);
                    metaNodeTree.put(nodeLevel, createChildren());
                    // look for the parent level
                    LevelIndex parentLevel;
                    // by the correctness of the aspect graph we know that
                    // there is at most one outgoing edge, which is a parent
                    // edge and points to the parent level node
                    Set<AspectEdge> outEdges =
                        NewRuleView.this.graph.outEdgeSet(node);
                    if (outEdges.isEmpty()) {
                        // this is a top node in the level node tree
                        assert NestingAspect.isForall(node) : String.format(
                            "Top level '%s' should not be existential", node);
                        parentLevel = this.topLevelIndex;
                    } else {
                        AspectNode parentNode =
                            outEdges.iterator().next().opposite();
                        parentLevel = getIndex(parentNode);
                    }
                    indexParentMap.put(nodeLevel, parentLevel);
                }
            }
            // Now fill the tree from the parent map
            for (Map.Entry<LevelIndex,LevelIndex> parentEntry : indexParentMap.entrySet()) {
                metaNodeTree.get(parentEntry.getValue()).add(
                    parentEntry.getKey());
            }
            List<LevelIndex> indices = new LinkedList<LevelIndex>();
            // Set the parentage in tree preorder
            Queue<LevelIndex> indexQueue = new LinkedList<LevelIndex>();
            indexQueue.add(this.topLevelIndex);
            while (!indexQueue.isEmpty()) {
                LevelIndex next = indexQueue.poll();
                indices.add(next);
                // set parent, except for top level
                if (indexParentMap.containsKey(next)) {
                    next.setParent(indexParentMap.get(next));
                }
                Set<LevelIndex> children = metaNodeTree.get(next);
                // add an implicit existential sub-level to childless universal
                // levels
                if (children.isEmpty() && next.isUniversal()) {
                    LevelIndex implicitChild = new LevelIndex(null);
                    metaNodeTree.put(implicitChild, createChildren());
                    indexParentMap.put(implicitChild, next);
                    children.add(implicitChild);
                }
                indexQueue.addAll(children);
            }
            // now fix all levels and build the level data map
            this.indexLevelMap = new LinkedHashMap<LevelIndex,Level>();
            for (LevelIndex index : indices) {
                index.setFixed();
                Level parentData =
                    index.isTopLevel() ? null
                            : this.indexLevelMap.get(index.getParent());
                Level thisData = new Level(index, parentData);
                this.indexLevelMap.put(index, thisData);
            }
            // fix the level data
            for (Level level : this.indexLevelMap.values()) {
                level.setMode(LevelMode.TREE_SET);
            }
            setFixed();
        }

        /**
         * Lazily creates and returns a level index for a given level meta-node.
         * @param levelNode the level node for which a level is to be created;
         *        should satisfy
         *        {@link NestingAspect#isMetaElement(groove.view.aspect.AspectElement)}
         */
        private LevelIndex getIndex(AspectNode levelNode) {
            LevelIndex result = this.metaIndexMap.get(levelNode);
            if (result == null) {
                this.metaIndexMap.put(levelNode, result =
                    new LevelIndex(levelNode));
                String name = NestingAspect.getLevelName(levelNode);
                if (name != null && name.length() > 0) {
                    this.nameIndexMap.put(name, result);
                }
            }
            return result;
        }

        /**
         * Creates an ordered set to store the children of a meta-node. The
         * ordering is based on the meta-node corresponding to the level.
         */
        private Set<LevelIndex> createChildren() {
            return new TreeSet<LevelIndex>();
        }

        private void initData() throws FormatException {
            Set<String> errors = new TreeSet<String>();
            // add nodes to nesting data structures
            for (AspectNode node : NewRuleView.this.graph.nodeSet()) {
                if (RuleAspect.inRule(node)) {
                    try {
                        Level level = getLevel(node);
                        level.addNode(node, getNodeImage(node));
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
            }
            // add edges to nesting data structures
            for (AspectEdge edge : NewRuleView.this.graph.edgeSet()) {
                if (RuleAspect.inRule(edge)) {
                    try {
                        Level level = getLevel(edge);
                        Edge edgeImage = getEdgeImage(edge);
                        if (edgeImage != null) {
                            level.addEdge(edge, edgeImage);
                        }
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            // fix the level data
            for (Level level : this.indexLevelMap.values()) {
                level.setMode(LevelMode.FIXED);
            }
        }

        /**
         * Returns the quantification level of a given aspect rule node.
         * @param node the node for which the quantification level is
         *        determined; must satisfy
         *        {@link RuleAspect#inRule(groove.view.aspect.AspectElement)}
         * @return the level for {@code node}; non-null
         */
        private Level getLevel(AspectNode node) throws FormatException {
            Level result = getNodeLevelMap().get(node);
            if (result == null) {
                // find the corresponding quantifier node
                AspectNode nestingNode = getLevelNode(node);
                LevelIndex index =
                    nestingNode == null ? this.topLevelIndex
                            : this.metaIndexMap.get(nestingNode);
                assert index != null : String.format(
                    "No valid nesting level found for %s", node);
                result = this.indexLevelMap.get(index);
                assert result != null : String.format(
                    "Level map %s does not contain entry for index %s",
                    this.indexLevelMap, index);
                String levelName = RuleAspect.getName(node);
                if (levelName != null) {
                    LevelIndex namedLevelIndex =
                        this.nameIndexMap.get(levelName);
                    if (namedLevelIndex == null) {
                        throw new FormatException(
                            "Undefined node nesting level '%s'", levelName);
                    }
                    result =
                        result.max(this.indexLevelMap.get(namedLevelIndex));
                    if (result == null) {
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
        private Level getLevel(AspectEdge edge) throws FormatException {
            Level sourceLevel = getLevel(edge.source());
            Level targetLevel = getLevel(edge.opposite());
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
                LevelIndex edgeLevelIndex = this.nameIndexMap.get(levelName);
                if (edgeLevelIndex == null) {
                    throw new FormatException(
                        "Undefined nesting level '%s' in edge %s", levelName,
                        edge);
                }
                result = result.max(this.indexLevelMap.get(edgeLevelIndex));
                if (result == null) {
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
            testFixed(true);
            return this.indexLevelMap.values();
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
         * Lazily creates and returns the top level of the tree.
         */
        public Level getTopLevel() {
            testFixed(true);
            return this.indexLevelMap.get(this.topLevelIndex);
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
                    this.labelSet.add(result.label());
                }
            }
            return result;
        }

        /** Returns the view-to-rule element map. */
        public final NodeEdgeMap getViewToRuleMap() {
            return this.viewToRuleMap;
        }

        /**
         * Mapping from the elements of the aspect graph representation to the
         * corresponding elements of the rule.
         */
        private final NodeEdgeMap viewToRuleMap = new NodeEdgeHashMap();

        /** Returns the set of labels occurring in the view. */
        public final Set<Label> getLabelSet() {
            return this.labelSet;
        }

        /** Set of all labels occurring in the rule. */
        private final Set<Label> labelSet = new HashSet<Label>();
        /** The top level of the rule tree. */
        private LevelIndex topLevelIndex;
        /** The set of all levels in this tree. */
        private Map<LevelIndex,Level> indexLevelMap;
        /** mapping from nesting meta-nodes nodes to nesting levels. */
        private Map<AspectNode,LevelIndex> metaIndexMap;
        /** mapping from nesting level names to nesting levels. */
        private Map<String,LevelIndex> nameIndexMap;
        /** Mapping from view nodes to the corresponding nesting level. */
        private Map<AspectNode,Level> nodeLevelMap;
    }

    /**
     * Class containing all elements on a given rule level. Capable of computing
     * the rule on that level.
     */
    private class Level {
        /**
         * Creates a new level, with a given index and parent level.
         * @param index the index of the new level
         * @param parent the parent level; may be {@code null} if this is the
         *        top level.
         */
        public Level(LevelIndex index, Level parent) {
            this.index = index;
            this.parent = parent;
            if (parent != null) {
                assert index.getParent().equals(parent.index) : String.format(
                    "Parent index %s should be parent of %s", parent.index,
                    index);
                this.parent.addChild(this);
            } else {
                assert index.isTopLevel() : String.format(
                    "Level with index %s should have non-null parent", index);
            }
        }

        /** Adds a child level to this level. */
        private void addChild(Level child) {
            testMode(LevelMode.START);
            assert this.index.equals(child.index.parent);
            this.children.add(child);
        }

        /**
         * Returns the maximum (i.e., lowest-level) level of this and another,
         * given level; or {@code null} if neither is smaller than the other.
         */
        public Level max(Level other) {
            if (this.index.smallerThan(other.index)) {
                return other;
            } else if (other.index.smallerThan(this.index)) {
                return this;
            } else {
                return null;
            }
        }

        /**
         * Considers adding a node to the set of nodes on this level. The node
         * is only really added if it satisfies
         * {@link #isForThisLevel(AspectElement)}; moreover, it is added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addNode(AspectNode viewNode, Node ruleNode) {
            testMode(LevelMode.TREE_SET);
            // put the node on this level, if it is supposed to be there
            if (isForThisLevel(viewNode)) {
                this.viewNodes.put(viewNode, ruleNode);
            }
            // put the node on the sublevels, if it is supposed to be there
            if (isForNextLevel(viewNode)) {
                for (Level sublevel : this.children) {
                    sublevel.addNode(viewNode, ruleNode);
                }
            }
        }

        /**
         * Consider adding an edge to the set of edges on this level. The edge
         * is only really added if it satisfies
         * {@link #isForThisLevel(AspectElement)}; moreover, it is added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addEdge(AspectEdge viewEdge, Edge ruleEdge) {
            testMode(LevelMode.TREE_SET);
            // put the edge on this level, if it is supposed to be there
            if (isForThisLevel(viewEdge)) {
                this.viewEdges.put(viewEdge, ruleEdge);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                for (int i = 0; i < viewEdge.endCount(); i++) {
                    Level ascendingLevel = this;
                    while (ascendingLevel.viewNodes.put(
                        (AspectNode) viewEdge.end(i), ruleEdge.end(i)) == null) {
                        assert !ascendingLevel.index.isTopLevel() : String.format(
                            "End node '%s' of '%s' not found at any level", i,
                            viewEdge);
                        ascendingLevel = ascendingLevel.parent;
                        assert ascendingLevel.viewNodes != null : String.format(
                            "Nodes on level %s not yet initialised",
                            ascendingLevel.getIndex());
                    }
                }
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(viewEdge)) {
                for (Level sublevel : this.children) {
                    sublevel.addEdge(viewEdge, ruleEdge);
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
        private boolean isForNextLevel(AspectElement elem) { // throws
            // FormatException
            // {
            boolean result;
            if (elem instanceof AspectNode) {
                // we need to push non-attribute nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result = isInjective() && !AttributeAspect.isAttributeNode(elem);
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                try {
                    Label varLabel = ((AspectEdge) elem).getModelLabel(true);
                    result = RegExprLabel.getWildcardId(varLabel) != null;
                } catch (FormatException exc) {
                    result = false;
                }
            }
            if (!result) {
                result =
                    this.index.isUniversal()
                        && (RuleAspect.isEraser(elem) || RuleAspect.isCreator(elem));
            }
            return result;
        }

        private void processView() throws FormatException {
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
            Set<String> errors = new TreeSet<String>();
            for (Map.Entry<AspectNode,Node> viewNodeEntry : this.viewNodes.entrySet()) {
                try {
                    processNode(viewNodeEntry.getKey(),
                        viewNodeEntry.getValue());
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            // if there are errors in the node map, don't try mapping the edges
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            for (Map.Entry<AspectEdge,Edge> viewEdgeEntry : this.viewEdges.entrySet()) {
                try {
                    processEdge(viewEdgeEntry.getKey(),
                        viewEdgeEntry.getValue());
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
        }

        /**
         * Adds a node to this level.
         * @return {@code true} if the node is fresh
         */
        private boolean processNode(AspectNode viewNode, Node lhsNode)
            throws FormatException {
            boolean result = true;
            if (RuleAspect.inLHS(viewNode) || RuleAspect.inNAC(viewNode)) {
                Node oldLhsNode = this.lhsMap.putNode(viewNode, lhsNode);
                assert oldLhsNode == null || oldLhsNode.equals(lhsNode) : String.format(
                    "Old and new LHS images '%s' and '%s' should be the same",
                    oldLhsNode, lhsNode);
                result = oldLhsNode == null;
                if (RuleAspect.inLHS(viewNode)) {
                    this.lhs.addNode(lhsNode);
                } else {
                    this.nacNodeSet.add(lhsNode);
                }
            }
            if (RuleAspect.inRHS(viewNode)) {
                Node rhsNode = computeNodeImage(getRepresentative(viewNode));
                Node oldRhsNode = this.rhsMap.putNode(viewNode, rhsNode);
                assert oldRhsNode == null || oldRhsNode.equals(rhsNode) : String.format(
                    "Old and new RHS images '%s' and '%s' should be the same",
                    oldRhsNode, lhsNode);
                result &= oldRhsNode == null;
                this.rhs.addNode(rhsNode);
                if (RuleAspect.inLHS(viewNode)) {
                    this.ruleMorph.putNode(lhsNode, rhsNode);
                }
            }
            return result;
        }

        private void processEdge(AspectEdge viewEdge, Edge lhsEdge)
            throws FormatException {
            if (RuleAspect.inLHS(viewEdge) || RuleAspect.inNAC(viewEdge)) {
                this.lhsMap.putEdge(viewEdge, lhsEdge);
                if (RuleAspect.inLHS(viewEdge)) {
                    this.lhs.addEdge(lhsEdge);
                } else {
                    this.nacEdgeSet.add(lhsEdge);
                }
                if (!RuleAspect.inRHS(viewEdge) && lhsEdge.label().isNodeType()) {
                    this.deletedTypes.add(lhsEdge.source());
                }
            }
            if (RuleAspect.inRHS(viewEdge) && !RuleAspect.isMerger(viewEdge)) {
                Edge rhsEdge =
                    computeEdgeImage(viewEdge, this.rhsMap.nodeMap());
                assert rhsEdge != null : String.format(
                    "View edge '%s' does not have image in %s", viewEdge,
                    this.rhsMap);
                this.rhsMap.putEdge(viewEdge, rhsEdge);
                this.rhs.addEdge(rhsEdge);
                if (RuleAspect.inLHS(viewEdge)) {
                    this.ruleMorph.putEdge(lhsEdge, rhsEdge);
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
         * Returns the mapping from LHS view elements to rule elements on this
         * level.
         */
        public NodeEdgeMap getLhsMap() {
            testMode(LevelMode.FIXED);
            return this.lhsMap;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        private NodeEdgeMap getRootMap() {
            testMode(LevelMode.FIXED);
            return this.index.isTopLevel() ? null : getConnectingMap(
                this.parent.lhsMap, this.lhsMap);
        }

        /**
         * Returns the mapping from the RHS rule elements at the parent level to
         * the RHS rule elements at this level.
         */
        private NodeEdgeMap getCoRootMap() {
            testMode(LevelMode.FIXED);
            return this.index.isTopLevel() ? null : getConnectingMap(
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
            SortedSet<AspectNode> cell = getPartition().get(node);
            assert cell != null : String.format(
                "Partition %s does not contain cell for '%s'", getPartition(),
                node);
            return cell.first();
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
            for (Node newNode : this.viewNodes.keySet()) {
                // test if the node is new
                if (!result.containsKey(newNode)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    newCell.add((AspectNode) newNode);
                    result.put((AspectNode) newNode, newCell);
                }
            }
            // now merge nodes whenever there is a merger
            for (Edge newEdge : this.viewEdges.keySet()) {
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
         * Callback method to compute the rule on this nesting level from sets
         * of aspect nodes and edges that appear on this level.
         */
        public AbstractCondition<?> computeFlatRule() throws FormatException {
            AbstractCondition<?> result;
            Set<String> errors = new TreeSet<String>();
            // check if label variables are bound
            Set<String> boundVars = getVars(this.lhs.edgeSet(), true);
            Set<String> lhsVars = getVars(this.lhs.edgeSet(), false);
            if (!boundVars.containsAll(lhsVars)) {
                lhsVars.removeAll(boundVars);
                errors.add(String.format(
                    "Left hand side variables %s not bound on left hand side",
                    lhsVars));
            }
            Set<String> rhsVars = getVars(this.rhs.edgeSet(), false);
            if (!boundVars.containsAll(rhsVars)) {
                rhsVars.removeAll(boundVars);
                errors.add(String.format(
                    "Right hand side variables %s not bound on left hand side",
                    rhsVars));
            }
            Set<String> nacVars = getVars(this.nacEdgeSet, false);
            if (!boundVars.containsAll(nacVars)) {
                nacVars.removeAll(boundVars);
                errors.add(String.format(
                    "NAC variables %s not bound on left hand side", nacVars));
            }

            // check if node type additions and deletions are balanced
            for (Node deletedType : this.deletedTypes) {
                this.addedTypes.remove(deletedType);
            }
            if (!this.addedTypes.isEmpty()) {
                StringBuilder error = new StringBuilder();
                for (Label type : this.addedTypes.values()) {
                    if (error.length() > 0) {
                        error.append(String.format("%n"));
                    }
                    error.append(String.format(
                        "New node type '%s' not allowed without removing old type",
                        type));
                }
                errors.add(error.toString());
            }
            // the resulting rule
            if (this.index.isExistential()) {
                result =
                    createRule(this.ruleMorph, getRootMap(), getCoRootMap(),
                        this.index.getName());
            } else {
                result =
                    createForall(this.lhs, getRootMap(), this.index.getName(),
                        this.index.isPositive());
            }
            // add the nacs to the rule
            for (Pair<Set<Node>,Set<Edge>> nacPair : AbstractGraph.getConnectedSets(
                this.nacNodeSet, this.nacEdgeSet)) {
                result.addSubCondition(computeNac(this.lhs, nacPair.first(),
                    nacPair.second()));
            }
            if (errors.isEmpty()) {
                return result;
            } else {
                throw new FormatException(errors);
            }
        }

        /**
         * Collects the variables from the regular expressions in a set of
         * edges. A flag indicates if it is just the bound variables we are
         * interested in.
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
         * Constructs a negative application condition based on a LHS graph and
         * a set of graph elements that should make up the NAC target. The
         * connection between LHS and NAC target is given by identity, i.e.,
         * those elements in the NAC set that are in the LHS graph are indeed
         * LHS elements.
         * @param lhs the LHS graph
         * @param nacNodeSet set of graph elements that should be turned into a
         *        NAC target
         */
        private NotCondition computeNac(Graph lhs, Set<Node> nacNodeSet,
                Set<Edge> nacEdgeSet) {
            NotCondition result = null;
            // first check for merge end edge embargoes
            // they are characterised by the fact that there is precisely 1
            // element
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
                // if the rule is injective, add all lhs nodes to the pattern
                // map
                if (isInjective()) {
                    for (Node node : lhs.nodeSet()) {
                        nacTarget.addNode(node);
                        nacPatternMap.putNode(node, node);
                    }
                }
                // add edges and embargoes to nacTarget
                for (Edge edge : nacEdgeSet) {
                    // for all variables in the edge, add a LHS edge to the nac
                    // that
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
                                nacPatternMap.putEdge(nacVarBinder,
                                    nacVarBinder);
                            }
                        }
                    }
                    // add the endpoints that were not in the nac element set;
                    // it
                    // means
                    // they are lhs nodes, so add them to the nacMorphism as
                    // well
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
        private MergeEmbargo createMergeEmbargo(Graph context,
                Node[] embargoNodes) {
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
         * @param coRootMap map of creator nodes in the parent rule to creator
         *        nodes of this rule
         * @param name name of the new rule to be created
         * @return the fresh rule created by the factory
         */
        private SPORule createRule(Morphism ruleMorphism, NodeEdgeMap rootMap,
                NodeEdgeMap coRootMap, String name) {
            return new SPORule(ruleMorphism, rootMap, coRootMap, new RuleName(
                name), getProperties());
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
         * Callback method to create a graph that can serve as LHS or RHS of a
         * rule.
         * @see #getView()
         */
        private Graph createGraph() {
            return graphFactory.newGraph();
        }

        /**
         * Sets the mode for this level data to a next value.
         * @param mode the new level mode.
         * @throws IllegalArgumentException if the transition from the current
         *         mode to {@code mode} is illegal.
         */
        public void setMode(LevelMode mode) throws IllegalArgumentException,
            FormatException {
            if (this.mode.compareTo(mode) >= 0) {
                throw new IllegalArgumentException(String.format(
                    "Illegal mode transition from '%s' to '%s'", this.mode,
                    mode));
            }
            this.mode = mode;
            if (mode.equals(LevelMode.FIXED)) {
                processView();
            }
        }

        /**
         * Tests if the current level mode equals a given value.
         * @param mode the value for which is tested
         * @throws IllegalArgumentException if the current mode is not equal to
         *         {@code mode}
         */
        private void testMode(LevelMode mode) throws IllegalArgumentException {
            if (this.mode.compareTo(mode) != 0) {
                throw new IllegalArgumentException(String.format(
                    "Mode should be '%s' but is '%s'", mode, this.mode));
            }
        }

        /** Returns the index of this level. */
        public final LevelIndex getIndex() {
            return this.index;
        }

        /** Index of this level. */
        private final LevelIndex index;

        /** Returns the parent of this level. */
        public final Level getParent() {
            return this.parent;
        }

        /** Parent level; {@code null} if this is the top level. */
        private final Level parent;
        /** Children level data. */
        private final List<Level> children = new ArrayList<Level>();
        /** Map of all view nodes on this level. */
        private final Map<AspectNode,Node> viewNodes =
            new HashMap<AspectNode,Node>();
        /** Map of all view edges on this level. */
        private final Map<AspectEdge,Edge> viewEdges =
            new HashMap<AspectEdge,Edge>();
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
        /** Node partition on this quantification level. */
        private Map<AspectNode,SortedSet<AspectNode>> partition;
        /** Mapping from rule nodes to type labels added to that node. */
        private Map<Node,Label> addedTypes;
        /** Set of nodes for which a type label is deleted. */
        private Set<Node> deletedTypes;
        /** The current mode of this level data. */
        private LevelMode mode = LevelMode.START;
    }

    /** Intermediate mode values for {@link Level}. */
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
            Set<String> errors = new TreeSet<String>();
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
                    try {
                        if (!parNumbers.add(nr)) {
                            throw new FormatException(
                                "Parameter number '%d' occurs more than once",
                                nr);
                        }
                        Level level = NewRuleView.this.levelTree.getLevel(node);
                        if (!level.getIndex().isTopLevel()) {
                            throw new FormatException(
                                "Rule parameter '%d' only allowed on top existential level",
                                nr);
                        }
                        if (RuleAspect.inLHS(node)) {
                            Node nodeImage = level.getLhsMap().getNode(node);
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
                            Node nodeImage = level.getLhsMap().getNode(node);
                            outParMap.put(nr, nodeImage);
                        } else {
                            throw new FormatException(
                                "Parameter '%d' may not occur in NAC", nr);
                        }
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            // test if parameters form a consecutive sequence
            Set<Integer> missingPars = new TreeSet<Integer>();
            for (int nr = 1; nr <= inParMap.size() + outParMap.size(); nr++) {
                if (!parNumbers.contains(nr)) {
                    missingPars.add(nr);
                }
            }
            if (!missingPars.isEmpty()) {
                throw new FormatException("Parameters %s missing", missingPars);
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
