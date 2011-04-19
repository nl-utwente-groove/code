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

import static groove.graph.EdgeRole.NODE_TYPE;
import static groove.view.aspect.AspectKind.EXISTS;
import static groove.view.aspect.AspectKind.FORALL;
import static groove.view.aspect.AspectKind.FORALL_POS;
import static groove.view.aspect.AspectKind.PARAM_BI;
import static groove.view.aspect.AspectKind.PARAM_IN;
import static groove.view.aspect.AspectKind.PARAM_OUT;
import static groove.view.aspect.AspectKind.PRODUCT;
import static groove.view.aspect.AspectKind.UNTYPED;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.graph.AbstractGraph;
import groove.graph.Element;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.VarSupport;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.EdgeEmbargo;
import groove.trans.ForallCondition;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleLabel;
import groove.trans.RuleName;
import groove.trans.RuleNode;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.util.DefaultFixable;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

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
import java.util.SortedSet;
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
public class DefaultRuleView implements RuleView {
    /**
     * Constructs a rule view from an aspect graph. The rule properties are
     * explicitly given.
     * @param graph the graph to be converted (non-null)
     * @param properties object specifying rule properties, such as injectivity
     *        etc (nullable)
     */
    public DefaultRuleView(AspectGraph graph, SystemProperties properties) {
        graph.testFixed(true);
        this.name = new RuleName(graph.getName());
        this.systemProperties = properties;
        this.graph = graph;
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
        return getRuleName() == null ? null : getRuleName().toString();
    }

    public int getPriority() {
        return GraphProperties.getPriority(getAspectGraph());
    }

    /** Convenience method */
    public String getTransitionLabel() {
        return GraphProperties.getTransitionLabel(getAspectGraph());
    }

    /** Convenience method */
    public String getFormatString() {
        return GraphProperties.getFormatString(getAspectGraph());
    }

    public boolean isEnabled() {
        return GraphProperties.isEnabled(getAspectGraph());
    }

    public boolean isConfluent() {
        return GraphProperties.isConfluent(getAspectGraph());
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

    public List<FormatError> getErrors() {
        initialise();
        return this.ruleErrors;
    }

    /** Returns the set of labels occurring in this rule. */
    public Set<TypeLabel> getLabels() {
        initialise();
        return this.levelTree == null ? Collections.<TypeLabel>emptySet()
                : this.levelTree.getLabelSet();
    }

    @Override
    final public AspectGraph getAspectGraph() {
        return this.graph;
    }

    @Override
    public ViewToRuleMap getMap() {
        initialise();
        return this.levelTree == null ? new ViewToRuleMap()
                : this.levelTree.getViewToRuleMap();
    }

    /**
     * Sets the properties of this view. This means that the previously
     * constructed model (if any) becomes invalid.
     */
    public final void setSystemProperties(SystemProperties properties) {
        if (properties == null ? this.systemProperties != null
                : !properties.equals(this.systemProperties)) {
            this.systemProperties = properties;
            invalidate();
        }
    }

    /** Changes the type graph under against which the model should be tested. */
    @Override
    public void setType(TypeGraph type) {
        if (this.type != type) {
            this.type = type;
            invalidate();
        }
    }

    /** Returns the (possibly {@code null}) type graph of this rule. */
    TypeGraph getType() {
        return this.type;
    }

    /** Indicates if this rule is typed. */
    boolean isTyped() {
        return this.type != null;
    }

    @Override
    public String toString() {
        return String.format("Rule view on '%s'", getName());
    }

    /** Returns the internal tree of rule levels. */
    final LevelMap getLevelTree() {
        return this.levelTree;
    }

    /**
     * @return Returns the properties.
     */
    final SystemProperties getSystemProperties() {
        return this.systemProperties;
    }

    /**
     * Indicates if the rule is to be matched injectively. If so, all context
     * nodes should be part of the root map, otherwise injectivity cannot be
     * checked.
     * @return <code>true</code> if the rule is to be matched injectively.
     */
    final boolean isInjective() {
        return getSystemProperties() != null
            && getSystemProperties().isInjective();
    }

    final boolean isRhsAsNac() {
        return getSystemProperties() != null
            && getSystemProperties().isRhsAsNac();
    }

    final boolean isCheckCreatorEdges() {
        return getSystemProperties() != null
            && getSystemProperties().isCheckCreatorEdges();
    }

    /**
     * Invalidates any previous construction of the underlying rule. This means
     * the rule will be reconstructed when there is a call for it, using
     * {@link #initialise()}.
     */
    private void invalidate() {
        this.rule = null;
        this.ruleErrors = null;
        this.levelTree = null;
    }

    /** Initialises the derived data structures. */
    private void initialise() {
        // only do something if there is something to be done
        if (this.ruleErrors == null) {
            this.ruleErrors = new ArrayList<FormatError>();
            this.ruleErrors.addAll(getAspectGraph().getErrors());
            // trying to initialise with view errors, e.g. an
            // at-edge from a forall:-node, may throw exceptions
            if (this.ruleErrors.isEmpty()) {
                this.levelTree = new LevelMap();
                try {
                    this.levelTree.initialise();
                    this.rule = computeRule();
                } catch (FormatException exc) {
                    Map<RuleElement,AspectElement> inverseMap =
                        this.levelTree.getRuleToViewMap();
                    for (FormatError error : exc.getErrors()) {
                        this.ruleErrors.add(error.transfer(inverseMap));
                    }
                }
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
        Set<FormatError> errors = new TreeSet<FormatError>();
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
                    Condition parentCond = ruleTree.get(parentLevel);
                    if (parentCond != null) {
                        parentCond.addSubCondition(condition);
                    }

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
        if (rule != null) {
            rule.setPriority(getPriority());
            rule.setConfluent(isConfluent());
            rule.setTransitionLabel(getTransitionLabel());
            rule.setFormatString(getFormatString());
            Parameters parameters = new Parameters();
            try {
                rule.setSignature(parameters.getSignature(),
                    parameters.getHiddenPars());
                rule.setFixed();
                if (TO_RULE_DEBUG) {
                    System.out.println("Constructed rule: " + rule);
                }
            } catch (FormatException exc) {
                rule = null;
                errors.addAll(exc.getErrors());
            }
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
    private RuleNode computeNodeImage(AspectNode node) throws FormatException {
        AspectKind nodeAttrKind = node.getAttrKind();
        if (nodeAttrKind == PRODUCT) {
            return new ProductNode(node.getNumber(), node.getArgNodes().size());
        } else if (nodeAttrKind.isData()) {
            return node.getAttrAspect().getVariableNode(node.getNumber());
        } else {
            return ruleFactory.createNode(node.getNumber());
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
    private RuleEdge computeEdgeImage(AspectEdge edge,
            Map<AspectNode,? extends RuleNode> elementMap)
        throws FormatException {
        assert edge.getRuleLabel() != null : String.format(
            "Edge '%s' does not belong in model", edge);
        RuleNode sourceImage = elementMap.get(edge.source());
        if (sourceImage == null) {
            throw new FormatException(
                "Cannot compute image of '%s'-edge: source node does not have image",
                edge.label(), edge.source());
        }
        RuleNode targetImage = elementMap.get(edge.target());
        if (targetImage == null) {
            throw new FormatException(
                "Cannot compute image of '%s'-edge: target node does not have image",
                edge.label(), edge.target());
        }
        return ruleFactory.createEdge(sourceImage, edge.getRuleLabel(),
            targetImage);
    }

    /**
     * Callback method to create a graph that can serve as LHS or RHS of a rule.
     * @see #getAspectGraph()
     */
    RuleGraph createGraph(String name) {
        return new RuleGraph(name);
    }

    /**
     * The name of the rule represented by this rule graph.
     */
    private final RuleName name;

    /** The view graph representation of the rule. */
    private final AspectGraph graph;
    /** The graph for this view, if any. */
    private TypeGraph type;
    /** The level tree for this rule view. */
    private LevelMap levelTree;
    /** Errors found while converting the view to a rule. */
    private List<FormatError> ruleErrors;
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    /** Rule properties set for this rule. */
    private SystemProperties systemProperties;

    static private final RuleFactory ruleFactory = RuleFactory.instance();
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
            assert levelNode == null || levelNode.getKind().isQuantifier();
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
                isImplicit() ? null : this.levelNode.getLevelName();
            if (levelName == null) {
                return DefaultRuleView.this.getName()
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
         * than another, or equal to it. This is the case if the depth of this 
         * nesting does not exceed that of the other, and the indices at every 
         * (common) level coincide.
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
                AspectKind nodeKind = this.levelNode.getKind();
                return nodeKind == FORALL || nodeKind == FORALL_POS;
            }
        }

        /** Indicates whether this level is existential. */
        public boolean isExistential() {
            testIndexFixed();
            if (isImplicit()) {
                // it's an implicit level
                return isTopLevel() || this.parent.isUniversal();
            } else {
                AspectKind nodeKind = this.levelNode.getKind();
                return nodeKind == EXISTS;
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
         */
        public boolean isPositive() {
            return isImplicit() ? this.index.size() == 1
                    : this.levelNode.getKind() == FORALL_POS;
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
        final AspectNode levelNode;
        /** The index uniquely identifying this level. */
        final List<Integer> index = new ArrayList<Integer>();
        /** List of children of this tree index. */
        final List<LevelIndex> children = new ArrayList<LevelIndex>();
        /** Parent of this tree index; may be <code>null</code> */
        LevelIndex parent;
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
            // Mapping from levels to match count nodes
            this.matchCountMap = new HashMap<LevelIndex,AspectNode>();
            for (AspectNode node : getAspectGraph().nodeSet()) {
                AspectKind nodeKind = node.getKind();
                if (nodeKind.isQuantifier()) {
                    LevelIndex nodeLevel = getIndex(node);
                    metaNodeTree.put(nodeLevel, createChildren());
                    // look for the parent level
                    LevelIndex parentLevel;
                    // by the correctness of the aspect graph we know that
                    // there is at most one outgoing edge, which is a parent
                    // edge and points to the parent level node
                    AspectNode parentNode = node.getNestingParent();
                    if (parentNode == null) {
                        if (nodeKind == FORALL || nodeKind == FORALL_POS) {
                            parentLevel = this.topLevelIndex;
                        } else {
                            // create an artificial intermediate level to
                            // accommodate erroneous top-level existential node
                            parentLevel = new LevelIndex();
                            metaNodeTree.put(parentLevel, createChildren());
                            indexParentMap.put(parentLevel, this.topLevelIndex);
                        }
                    } else {
                        parentLevel = getIndex(parentNode);
                    }
                    indexParentMap.put(nodeLevel, parentLevel);
                    if (node.getMatchCount() != null) {
                        this.matchCountMap.put(nodeLevel, node.getMatchCount());
                    }
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
            // check that match count nodes are defined at super-levels
            for (Map.Entry<LevelIndex,AspectNode> matchCountEntry : this.matchCountMap.entrySet()) {
                LevelIndex definingLevel =
                    getLevel(matchCountEntry.getValue()).getIndex();
                LevelIndex usedLevel = matchCountEntry.getKey();
                if (!definingLevel.smallerThan(usedLevel)
                    || definingLevel.equals(usedLevel)) {
                    throw new FormatException(
                        "Match count not defined at appropriate level",
                        matchCountEntry.getValue());
                }
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
         *        {@link AspectKind#isQuantifier()}
         */
        private LevelIndex getIndex(AspectNode levelNode) {
            LevelIndex result = this.metaIndexMap.get(levelNode);
            if (result == null) {
                this.metaIndexMap.put(levelNode, result =
                    new LevelIndex(levelNode));
                String name = levelNode.getLevelName();
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
            this.viewToRuleMap.clear();
            Set<FormatError> errors = new TreeSet<FormatError>();
            // initialise the match count nodes
            // this is done straight away, to ensure that the rule graph
            // of counted subconditions is pushed to the existential level
            for (Map.Entry<LevelIndex,AspectNode> matchCountEntry : this.matchCountMap.entrySet()) {
                AspectNode matchCount = matchCountEntry.getValue();
                this.indexLevelMap.get(matchCountEntry.getKey()).setMatchCount(
                    matchCount, (VariableNode) getNodeImage(matchCount));
            }
            // add nodes to nesting data structures
            for (AspectNode node : getAspectGraph().nodeSet()) {
                if (!node.getKind().isMeta()) {
                    try {
                        Level level = getLevel(node);
                        level.addNode(node, getNodeImage(node));
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
            }
            // add edges to nesting data structures
            for (AspectEdge edge : getAspectGraph().edgeSet()) {
                if (!edge.getKind().isMeta()) {
                    try {
                        Level level = getLevel(edge);
                        RuleEdge edgeImage = getEdgeImage(edge);
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
         *        determined; must fail to satisfy
         *        {@link AspectKind#isMeta()}
         * @return the level for {@code node}; non-null
         */
        private Level getLevel(AspectNode node) {
            Level result = getNodeLevelMap().get(node);
            if (result == null) {
                // find the corresponding quantifier node
                AspectNode nestingNode = node.getNestingLevel();
                LevelIndex index =
                    nestingNode == null ? this.topLevelIndex
                            : this.metaIndexMap.get(nestingNode);
                assert index != null : String.format(
                    "No valid nesting level found for %s", node);
                result = this.indexLevelMap.get(index);
                assert result != null : String.format(
                    "Level map %s does not contain entry for index %s",
                    this.indexLevelMap, index);
                getNodeLevelMap().put(node, result);
            }
            return result;
        }

        /**
         * Returns the quantification level of a given aspect rule edge.
         * @param edge the edge for which the quantification level is
         *        determined; must fail to satisfy 
         *        {@link AspectKind#isMeta()}
         */
        private Level getLevel(AspectEdge edge) throws FormatException {
            Level sourceLevel = getLevel(edge.source());
            Level targetLevel = getLevel(edge.target());
            Level result = sourceLevel.max(targetLevel);
            // if one of the end nodes is a NAC, it must be the max of the two
            if (edge.source().getKind().inNAC() && !sourceLevel.equals(result)
                || edge.target().getKind().inNAC()
                && !targetLevel.equals(result)) {
                result = null;
            }
            if (result == null) {
                throw new FormatException(
                    "Source and target of edge %s have incompatible nesting",
                    edge);
            }
            String levelName = edge.getLevelName();
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
        private RuleNode getNodeImage(AspectNode viewNode)
            throws FormatException {
            RuleNode result = this.viewToRuleMap.getNode(viewNode);
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
        private RuleEdge getEdgeImage(AspectEdge viewEdge)
            throws FormatException {
            RuleEdge result = this.viewToRuleMap.getEdge(viewEdge);
            if (result == null) {
                result =
                    computeEdgeImage(viewEdge, this.viewToRuleMap.nodeMap());
                if (result != null) {
                    this.viewToRuleMap.putEdge(viewEdge, result);
                    RegExpr labelExpr = result.label().getMatchExpr();
                    if (labelExpr != null) {
                        this.labelSet.addAll(labelExpr.getTypeLabels());
                    }
                }
            }
            return result;
        }

        /** Returns the view-to-rule element map. */
        public final ViewToRuleMap getViewToRuleMap() {
            return this.viewToRuleMap;
        }

        /** Returns the inverse of the view-to-rule map. */
        public final Map<RuleElement,AspectElement> getRuleToViewMap() {
            Map<RuleElement,AspectElement> result =
                new HashMap<RuleElement,AspectElement>();
            for (Map.Entry<AspectNode,? extends RuleNode> nodeEntry : this.viewToRuleMap.nodeMap().entrySet()) {
                result.put(nodeEntry.getValue(), nodeEntry.getKey());
            }
            for (Map.Entry<AspectEdge,? extends RuleEdge> edgeEntry : this.viewToRuleMap.edgeMap().entrySet()) {
                result.put(edgeEntry.getValue(), edgeEntry.getKey());
            }
            return result;
        }

        /**
         * Mapping from the elements of the aspect graph representation to the
         * corresponding elements of the rule.
         */
        private final ViewToRuleMap viewToRuleMap = new ViewToRuleMap();

        /** Returns the set of labels occurring in the view. */
        public final Set<TypeLabel> getLabelSet() {
            return this.labelSet;
        }

        /** Set of all labels occurring in the rule. */
        private final Set<TypeLabel> labelSet = new HashSet<TypeLabel>();
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
        /** Mapping from (universal) levels to match count nodes. */
        private Map<LevelIndex,AspectNode> matchCountMap;
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
        public void addNode(AspectNode viewNode, RuleNode ruleNode) {
            testMode(LevelMode.TREE_SET);
            // put the node on this level, if it is supposed to be there
            if (isForThisLevel(viewNode)) {
                this.viewToLevelMap.putNode(viewNode, ruleNode);
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
        public void addEdge(AspectEdge viewEdge, RuleEdge ruleEdge) {
            testMode(LevelMode.TREE_SET);
            // put the edge on this level, if it is supposed to be there
            if (isForThisLevel(viewEdge)) {
                this.viewToLevelMap.putEdge(viewEdge, ruleEdge);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                addNodeToParents(viewEdge.source(), ruleEdge.source());
                addNodeToParents(viewEdge.target(), ruleEdge.target());
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(viewEdge)) {
                for (Level sublevel : this.children) {
                    sublevel.addEdge(viewEdge, ruleEdge);
                }
            } else if (!viewEdge.getKind().inNAC()
                && ruleEdge.getRole() == NODE_TYPE) {
                // add type edges to all sublevels
                for (Level sublevel : this.children) {
                    sublevel.addParentType(ruleEdge);
                }
            }
        }

        /** Initialises the match count for this (universal) level. */
        public void setMatchCount(AspectNode matchCount,
                VariableNode matchCountImage) {
            assert getIndex().isUniversal();
            assert matchCountImage != null;
            this.matchCountImage = matchCountImage;
        }

        /**
         * Adds a node to this and all parent levels, if it is not yet there
         */
        private void addNodeToParents(AspectNode viewNode, RuleNode ruleNode) {
            Level ascendingLevel = this;
            while (ascendingLevel.viewToLevelMap.putNode(viewNode, ruleNode) == null) {
                assert !ascendingLevel.index.isTopLevel() : String.format("Node not found at any level");
                ascendingLevel = ascendingLevel.parent;
                assert ascendingLevel.viewToLevelMap != null : String.format(
                    "Nodes on level %s not yet initialised",
                    ascendingLevel.getIndex());
            }
        }

        /**
         * Adds a type edge (from a parent level) to this level.
         * The edge is not properly part of the rule, but may be
         * necessary to check the typing.
         */
        private void addParentType(RuleEdge ruleEdge) {
            Set<TypeLabel> parentTypes =
                this.parentTypeMap.get(ruleEdge.source());
            if (parentTypes == null) {
                this.parentTypeMap.put(ruleEdge.source(), parentTypes =
                    new HashSet<TypeLabel>());
            }
            if (ruleEdge.label().isAtom()) {
                parentTypes.add(ruleEdge.label().getTypeLabel());
            }
            for (Level sublevel : this.children) {
                sublevel.addParentType(ruleEdge);
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
            AspectKind kind = elem.getKind();
            return this.index.isExistential() || !kind.isCreator();
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the view. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for active elements (erasers and creators) on universal levels, since
         * they cannot be handled there.
         * Type edges are also pushed to the next level, to enable type checking
         * on all levels.
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(AspectElement elem) {
            AspectKind kind = elem.getKind();
            assert !kind.isMeta();
            boolean result = false;
            if (elem instanceof AspectNode) {
                // we need to push non-attribute nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result =
                    isInjective() && elem.getKind().inLHS()
                        && elem.getAttrAspect() == null;
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                RuleLabel varLabel = ((AspectEdge) elem).getRuleLabel();
                if (varLabel != null) {
                    result = varLabel.getWildcardId() != null;
                }
            }
            if (!result) {
                result =
                    this.index.isUniversal()
                        && (kind.isEraser() || kind.isCreator() || this.matchCountImage != null);
            }
            return result;
        }

        private void processView() throws FormatException {
            // initialise the rule data structures
            this.lhs = createGraph(getName() + "-" + getIndex() + "-lhs");
            this.rhs = createGraph(getName() + "-" + getIndex() + "-rhs");
            this.ruleMorph = createMorphism();
            this.lhsMap = new ViewToRuleMap();
            this.rhsMap = new ViewToRuleMap();
            this.nacNodeSet = new HashSet<RuleNode>();
            this.nacEdgeSet = new HashSet<RuleEdge>();
            this.typableNodes = new HashSet<RuleNode>();
            this.typableEdges = new HashSet<RuleEdge>();
            this.mergerMap = computeMergerMap();
            Set<FormatError> errors = new TreeSet<FormatError>();
            for (Map.Entry<AspectNode,? extends RuleNode> viewNodeEntry : this.viewToLevelMap.nodeMap().entrySet()) {
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
            for (Map.Entry<AspectEdge,? extends RuleEdge> viewEdgeEntry : this.viewToLevelMap.edgeMap().entrySet()) {
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

        /** Collects the set of merger edges on this level. */
        private Map<AspectEdge,RuleEdge> computeMergerMap() {
            Map<AspectEdge,RuleEdge> result =
                new HashMap<AspectEdge,RuleEdge>();
            for (Map.Entry<AspectEdge,? extends RuleEdge> edgeEntry : this.viewToLevelMap.edgeMap().entrySet()) {
                AspectEdge viewEdge = edgeEntry.getKey();
                RuleEdge ruleEdge = edgeEntry.getValue();
                AspectKind kind = viewEdge.getKind();
                if (kind.isCreator() && ruleEdge.label().isEmpty()) {
                    result.put(viewEdge, ruleEdge);
                }
            }
            return result;
        }

        /**
         * Adds a node to this level.
         * @return {@code true} if the node is fresh
         */
        private boolean processNode(AspectNode viewNode, RuleNode lhsNode)
            throws FormatException {
            boolean result = true;
            AspectKind nodeKind = viewNode.getKind();
            if (nodeKind.inLHS() || nodeKind.inNAC()) {
                RuleNode oldLhsNode = this.lhsMap.putNode(viewNode, lhsNode);
                assert oldLhsNode == null || oldLhsNode.equals(lhsNode) : String.format(
                    "Old and new LHS images '%s' and '%s' should be the same",
                    oldLhsNode, lhsNode);
                result = oldLhsNode == null;
                if (nodeKind.inLHS()) {
                    this.lhs.addNode(lhsNode);
                    this.typableNodes.add(lhsNode);
                } else {
                    this.nacNodeSet.add(lhsNode);
                }
            }
            if (nodeKind.inRHS()) {
                this.typableNodes.add(lhsNode);
                RuleNode rhsNode = getRepresentative(viewNode);
                RuleNode oldRhsNode = this.rhsMap.putNode(viewNode, rhsNode);
                assert oldRhsNode == null || oldRhsNode.equals(rhsNode) : String.format(
                    "Old and new RHS images '%s' and '%s' should be the same",
                    oldRhsNode, lhsNode);
                result &= oldRhsNode == null;
                this.rhs.addNode(rhsNode);
                if (nodeKind.inLHS()) {
                    this.ruleMorph.putNode(lhsNode, rhsNode);
                } else {
                    if (isRhsAsNac()) {
                        this.nacNodeSet.add(lhsNode);
                    }
                }
            }
            return result;
        }

        private void processEdge(AspectEdge viewEdge, RuleEdge lhsEdge)
            throws FormatException {
            AspectKind edgeKind = viewEdge.getKind();
            if (edgeKind.inLHS() || edgeKind.inNAC()) {
                this.lhsMap.putEdge(viewEdge, lhsEdge);
                if (edgeKind.inLHS()) {
                    this.typableEdges.add(lhsEdge);
                    this.lhs.addEdge(lhsEdge);
                } else {
                    this.nacEdgeSet.add(lhsEdge);
                }
            }
            if (edgeKind.inRHS() && !this.mergerMap.containsKey(viewEdge)) {
                this.typableEdges.add(lhsEdge);
                RuleEdge rhsEdge =
                    computeEdgeImage(viewEdge, this.rhsMap.nodeMap());
                assert rhsEdge != null : String.format(
                    "View edge '%s' does not have image in %s", viewEdge,
                    this.rhsMap);
                this.rhsMap.putEdge(viewEdge, rhsEdge);
                this.rhs.addEdge(rhsEdge);
                if (edgeKind.inLHS()) {
                    this.ruleMorph.putEdge(lhsEdge, rhsEdge);
                } else if (isRhsAsNac()) {
                    this.nacEdgeSet.add(lhsEdge);
                } else if (isCheckCreatorEdges()
                    && viewEdge.source().getKind().inLHS()
                    && viewEdge.target().getKind().inLHS()) {
                    this.nacEdgeSet.add(lhsEdge);
                }
            }
        }

        /**
         * Returns the mapping from LHS view elements to rule elements on this
         * level.
         */
        public ViewToRuleMap getLhsMap() {
            testMode(LevelMode.FIXED);
            return this.lhsMap;
        }

        /**
         * Returns the mapping from RHS view elements to rule elements on this level.
         */
        public ViewToRuleMap getRhsMap() {
            testMode(LevelMode.FIXED);
            return this.rhsMap;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        private RuleGraphMorphism getRootMap() {
            testMode(LevelMode.FIXED);
            return this.index.isTopLevel() ? null : getConnectingMap(
                this.parent.lhsMap, this.lhsMap);
        }

        /**
         * Returns the mapping from the RHS rule elements at the parent level to
         * the RHS rule elements at this level.
         */
        private RuleGraphMorphism getCoRootMap() {
            testMode(LevelMode.FIXED);
            return this.index.isTopLevel() ? null : getConnectingMap(
                this.parent.parent.rhsMap, this.rhsMap);
        }

        /**
         * Returns a mapping from the rule elements at a parent level to the
         * rule elements at this level, given view-to-rule maps for both levels.
         */
        private RuleGraphMorphism getConnectingMap(ViewToRuleMap parentMap,
                ViewToRuleMap myMap) {
            RuleGraphMorphism result = new RuleGraphMorphism();
            for (Map.Entry<AspectNode,RuleNode> parentEntry : parentMap.nodeMap().entrySet()) {
                RuleNode image = myMap.getNode(parentEntry.getKey());
                if (image != null) {
                    RuleNode oldImage =
                        result.putNode(parentEntry.getValue(), image);
                    assert oldImage == null || oldImage.equals(image);
                }
            }
            for (Map.Entry<AspectEdge,RuleEdge> parentEntry : parentMap.edgeMap().entrySet()) {
                RuleEdge image = myMap.getEdge(parentEntry.getKey());
                if (image != null) {
                    RuleEdge oldImage =
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
        private RuleNode getRepresentative(AspectNode node)
            throws FormatException {
            // if there are no mergers, each node is its own representative
            // unless one of the top level calculated its partition
            boolean needsPartition =
                hasPartition() || !this.mergerMap.isEmpty()
                    || this.parent != null && this.parent.hasPartition();
            if (needsPartition) {
                SortedSet<AspectNode> cell = getPartition().get(node);
                assert cell != null : String.format(
                    "Partition %s does not contain cell for '%s'",
                    getPartition(), node);
                return this.viewToLevelMap.getNode(cell.first());
            } else {
                return this.viewToLevelMap.getNode(node);
            }
        }

        /** 
         * Indicates that the partition has been created.
         * This means that all sublevels should do the same, even if there
         * are no further mergers there.
         */
        private boolean hasPartition() {
            return this.partition != null;
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
         * Computes the partition of rule nodes according to RHS mergers.
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
            for (AspectNode newNode : this.viewToLevelMap.nodeMap().keySet()) {
                // test if the node is new
                if (!result.containsKey(newNode)) {
                    SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                    newCell.add(newNode);
                    result.put(newNode, newCell);
                }
            }
            // now merge nodes whenever there is a merger
            for (AspectEdge merger : this.mergerMap.keySet()) {
                SortedSet<AspectNode> newCell = new TreeSet<AspectNode>();
                newCell.addAll(result.get(merger.source()));
                newCell.addAll(result.get(merger.target()));
                for (AspectNode node : newCell) {
                    result.put(node, newCell);
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
            Set<FormatError> errors = new TreeSet<FormatError>();
            // check if label variables are bound
            Set<LabelVar> boundVars =
                VarSupport.getSimpleVarBinders(this.lhs).keySet();
            Set<RuleEdge> lhsVarEdges = VarSupport.getVarEdges(this.lhs);
            Set<RuleEdge> rhsVarEdges = VarSupport.getVarEdges(this.rhs);
            Set<RuleEdge> varEdges = new HashSet<RuleEdge>(lhsVarEdges);
            varEdges.addAll(rhsVarEdges);
            varEdges.addAll(this.nacEdgeSet);
            Map<String,LabelVar> varNames = new HashMap<String,LabelVar>();
            for (RuleEdge varEdge : varEdges) {
                Set<LabelVar> edgeVars = VarSupport.getAllVars(varEdge);
                // check for name overlap
                for (LabelVar var : edgeVars) {
                    String varName = var.getName();
                    LabelVar oldVar = varNames.put(varName, var);
                    if (oldVar != null && !var.equals(oldVar)) {
                        errors.add(new FormatError(
                            "Duplicate variable name '%s' for different label types",
                            varName));
                    }
                }
                edgeVars.removeAll(boundVars);
                for (LabelVar var : edgeVars) {
                    errors.add(new FormatError(
                        "Variable '%s' not bound on left hand side", var,
                        varEdge));
                }
            }
            // check typing
            if (isTyped()) {
                // check use of variables
                lhsVarEdges.removeAll(this.ruleMorph.edgeMap().keySet());
                for (RuleEdge eraserVarEdge : lhsVarEdges) {
                    for (LabelVar var : VarSupport.getAllVars(eraserVarEdge)) {
                        errors.add(new FormatError(
                            "Typed rule cannot contain variable eraser '%s'",
                            var, eraserVarEdge));
                    }
                }
                rhsVarEdges.removeAll(this.ruleMorph.edgeMap().values());
                for (RuleEdge creatorVarEdge : rhsVarEdges) {
                    for (LabelVar var : VarSupport.getAllVars(creatorVarEdge)) {
                        errors.add(new FormatError(
                            "Typed rule cannot contain variable creator '%s'",
                            var, creatorVarEdge));
                    }
                }
                // check type specialisation
                try {
                    TypeGraph.Typing<RuleNode,RuleEdge> lhsTypeMap =
                        getType().getTyping(this.lhs, this.parentTypeMap);
                    TypeGraph.Typing<RuleNode,RuleEdge> rhsTypeMap =
                        getType().getTyping(this.rhs, this.parentTypeMap);
                    checkTypeSpecialisation(lhsTypeMap, rhsTypeMap);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            // the resulting rule
            if (this.index.isExistential()) {
                result =
                    createRule(this.index.getName(), this.lhs, this.rhs,
                        this.ruleMorph, getRootMap(), getCoRootMap());
            } else {
                result =
                    createForall(this.lhs, getRootMap(), this.index.getName(),
                        this.index.isPositive());
            }
            // add the nacs to the rule
            // first add parent type edges for NAC nodes
            Map<RuleNode,Set<TypeLabel>> parentTypeMap =
                new HashMap<RuleNode,Set<TypeLabel>>(this.parentTypeMap);
            for (Map.Entry<AspectEdge,RuleEdge> lhsEdgeEntry : this.lhsMap.edgeMap().entrySet()) {
                RuleEdge typeEdge = lhsEdgeEntry.getValue();
                RuleLabel label = typeEdge.label();
                if (lhsEdgeEntry.getKey().getKind().inLHS()
                    && typeEdge.getRole() == NODE_TYPE
                    && (label.isAtom() || label.isSharp())) {
                    // add the type to the parent types
                    Set<TypeLabel> parentTypes =
                        parentTypeMap.get(typeEdge.source());
                    // copy rather than share the set
                    if (parentTypes == null) {
                        parentTypes = new HashSet<TypeLabel>();
                    } else {
                        parentTypes = new HashSet<TypeLabel>(parentTypes);
                    }
                    assert typeEdge.label().getTypeLabel() != null;
                    parentTypes.add(typeEdge.label().getTypeLabel());
                    parentTypeMap.put(typeEdge.source(), parentTypes);
                }
            }
            for (Pair<Set<RuleNode>,Set<RuleEdge>> nacPair : AbstractGraph.getConnectedSets(
                this.nacNodeSet, this.nacEdgeSet)) {
                Set<RuleNode> nacNodes = nacPair.one();
                Set<RuleEdge> nacEdges = nacPair.two();
                // to avoid duplicate error messages, only check typing if
                // the positive part of the rule was error-free
                if (errors.isEmpty() && isTyped()) {
                    try {
                        // check NAC typing
                        // first add end nodes of NAC edges
                        Set<RuleNode> typableNacNodes =
                            new HashSet<RuleNode>(nacNodes);
                        for (RuleEdge nacEdge : nacEdges) {
                            typableNacNodes.add(nacEdge.source());
                            typableNacNodes.add(nacEdge.target());
                        }
                        Set<RuleEdge> typableNacEdges =
                            new HashSet<RuleEdge>(nacEdges);
                        checkTyping(typableNacNodes, typableNacEdges,
                            parentTypeMap);
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
                // construct the NAC itself
                result.addSubCondition(computeNac(this.lhs, nacNodes, nacEdges));
            }
            if (errors.isEmpty()) {
                return result;
            } else {
                throw new FormatException(errors);
            }
        }

        /** 
         * Checks the type of a given set of nodes and edges.
         * @throws FormatException if there are typing errors
         */
        private void checkTyping(Collection<RuleNode> nodeSet,
                Collection<RuleEdge> edgeSet,
                Map<RuleNode,Set<TypeLabel>> parentTypeMap)
            throws FormatException {
            RuleGraph graph = createGraph(getName() + "-type");
            graph.addNodeSet(nodeSet);
            graph.addEdgeSet(edgeSet);
            getType().getTyping(graph, parentTypeMap);
        }

        /**
         * If the RHS type for a reader node is changed w.r.t. the LHS type,
         * the LHS type has to be sharp and the RHS type a subtype of it.
         * @throws FormatException if there are typing errors
         */
        private void checkTypeSpecialisation(
                TypeGraph.Typing<RuleNode,RuleEdge> lhsTyping,
                TypeGraph.Typing<RuleNode,RuleEdge> rhsTyping)
            throws FormatException {
            Set<FormatError> errors = new TreeSet<FormatError>();
            // check for type changes
            for (Map.Entry<RuleNode,TypeLabel> lhsTypeEntry : lhsTyping.getTypeMap().entrySet()) {
                RuleNode lhsNode = lhsTypeEntry.getKey();
                TypeLabel lhsType = lhsTypeEntry.getValue();
                RuleNode rhsNode = this.ruleMorph.getNode(lhsNode);
                // test if this is a reader node
                if (rhsNode != null && !lhsType.isDataType()) {
                    RuleEdge lhsEdge =
                        ruleFactory.createEdge(lhsNode, new RuleLabel(lhsType),
                            lhsNode);
                    // test if the type is deleted
                    // note that (in case of nested levels) the type edge
                    // may actually fail to exist in the lhs
                    if (this.lhs.containsEdge(lhsEdge)
                        && !this.ruleMorph.containsEdgeKey(lhsEdge)) {
                        if (!lhsTyping.isSharp(lhsNode)) {
                            errors.add(new FormatError(
                                "Modified type '%s' should be sharp", lhsType,
                                lhsNode));
                        }
                        TypeLabel rhsType = rhsTyping.getType(rhsNode);
                        if (!getType().getLabelStore().getSubtypes(lhsType).contains(
                            rhsType)) {
                            errors.add(new FormatError(
                                "New type '%s' should be subtype of modified type '%s'",
                                rhsType, lhsType, lhsNode));
                        }
                    }
                }
            }
            // Merged equal types are not caught, so we have to
            // check them for sharpness separately
            for (Map.Entry<AspectEdge,RuleEdge> mergerEntry : this.mergerMap.entrySet()) {
                RuleEdge edge = mergerEntry.getValue();
                RuleNode source = edge.source();
                Label sourceType = lhsTyping.getType(source);
                RuleNode target = edge.target();
                Label targetType = lhsTyping.getType(target);
                if (sourceType.equals(targetType) && !lhsTyping.isSharp(source)
                    && !lhsTyping.isSharp(target)) {
                    errors.add(new FormatError(
                        "One of the merged types '%s' or '%s' should be sharp",
                        sourceType, targetType, edge));
                }
            }
            // check for creation of abstract elements
            Set<Element> abstractElems =
                new HashSet<Element>(rhsTyping.getAbstractElements());
            abstractElems.removeAll(this.ruleMorph.nodeMap().values());
            abstractElems.removeAll(this.ruleMorph.edgeMap().values());
            for (Element elem : abstractElems) {
                if (elem instanceof RuleNode) {
                    errors.add(new FormatError(
                        "Creation of abstract %s-node not allowed",
                        rhsTyping.getType((RuleNode) elem), elem));
                } else {
                    errors.add(new FormatError(
                        "Creation of abstract %s-edge not allowed",
                        ((RuleEdge) elem).label(), elem));
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
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
        private NotCondition computeNac(RuleGraph lhs,
                Set<RuleNode> nacNodeSet, Set<RuleEdge> nacEdgeSet) {
            NotCondition result = null;
            // first check for merge end edge embargoes
            // they are characterised by the fact that there is precisely 1
            // element
            // in the nacElemSet, which is an edge
            if (nacNodeSet.size() == 0 && nacEdgeSet.size() == 1) {
                RuleEdge embargoEdge = nacEdgeSet.iterator().next();
                if (embargoEdge.label().isEmpty()) {
                    // this is supposed to be a merge embargo
                    result =
                        createMergeEmbargo(lhs, embargoEdge.source(),
                            embargoEdge.target());
                } else if (VarSupport.getAllVars(embargoEdge).isEmpty()) {
                    // this is supposed to be an edge embargo
                    result = createEdgeEmbargo(lhs, embargoEdge);
                }
            }
            if (result == null) {
                // if we're here it means we couldn't make an embargo
                result = createNAC(lhs);
                RuleGraph nacTarget = result.getTarget();
                RuleGraphMorphism nacPatternMap = result.getRootMap();
                // add all nodes to nacTarget
                nacTarget.addNodeSet(nacNodeSet);
                // if the rule is injective, add all lhs nodes to the pattern
                // map
                if (isInjective()) {
                    for (RuleNode node : lhs.nodeSet()) {
                        nacTarget.addNode(node);
                        nacPatternMap.putNode(node, node);
                    }
                }
                // add edges and embargoes to nacTarget
                for (RuleEdge edge : nacEdgeSet) {
                    // for all variables in the edge, add a LHS edge to the nac
                    // that
                    // binds the variable, if any
                    Set<LabelVar> vars = VarSupport.getAllVars(edge);
                    if (!vars.isEmpty()) {
                        Map<LabelVar,RuleEdge> lhsVarBinders =
                            VarSupport.getVarBinders(lhs);
                        for (LabelVar nacVar : vars) {
                            RuleEdge nacVarBinder = lhsVarBinders.get(nacVar);
                            if (nacVarBinder != null) {
                                // add the edge and its end nodes to the nac, as
                                // pre-matched elements
                                addToNacPattern(nacTarget, nacPatternMap,
                                    nacVarBinder.source());
                                addToNacPattern(nacTarget, nacPatternMap,
                                    nacVarBinder.target());
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
                    addToNacPattern(nacTarget, nacPatternMap, edge.source());
                    addToNacPattern(nacTarget, nacPatternMap, edge.target());
                    nacTarget.addEdge(edge);
                }
            }
            return result;
        }

        /**
         * Adds a given node to a NAC pattern map.
         */
        private void addToNacPattern(RuleGraph nacTarget,
                RuleGraphMorphism nacPatternMap, RuleNode node) {
            if (nacTarget.addNode(node)) {
                // the node identity in the lhs is the same
                nacPatternMap.putNode(node, node);
            }
        }

        /**
         * Callback method to create a merge embargo.
         * @param context the context-graph
         * @param embargoSource the source node of the merge embargo
         * @return the new {@link groove.trans.MergeEmbargo}
         * @see #toRule()
         */
        private MergeEmbargo createMergeEmbargo(RuleGraph context,
                RuleNode embargoSource, RuleNode embargoTarget) {
            return new MergeEmbargo(context, embargoSource, embargoTarget,
                getSystemProperties());
        }

        /**
         * Callback method to create an edge embargo.
         * @param context the context-graph
         * @param embargoEdge the edge to be turned into an embargo
         * @return the new {@link groove.trans.EdgeEmbargo}
         * @see #toRule()
         */
        private EdgeEmbargo createEdgeEmbargo(RuleGraph context,
                RuleEdge embargoEdge) {
            return new EdgeEmbargo(context, embargoEdge, getSystemProperties());
        }

        /**
         * Callback method to create a general NAC on a given graph.
         * @param context the context-graph
         * @return the new {@link groove.trans.NotCondition}
         * @see #toRule()
         */
        private NotCondition createNAC(RuleGraph context) {
            return new NotCondition(
                context.newGraph(context.getName() + "-nac"),
                getSystemProperties());
        }

        /**
         * Factory method for rules.
         * @param name name of the new rule to be created
         * @param lhs the left hand side graph
         * @param rhs the right hand side graph
         * @param ruleMorphism morphism of the new rule to be created
         * @param rootMap pattern map leading into the LHS
         * @param coRootMap map of creator nodes in the parent rule to creator
         *        nodes of this rule
         * @return the fresh rule created by the factory
         */
        private SPORule createRule(String name, RuleGraph lhs, RuleGraph rhs,
                RuleGraphMorphism ruleMorphism, RuleGraphMorphism rootMap,
                RuleGraphMorphism coRootMap) {
            SPORule result =
                new SPORule(new RuleName(name), lhs, rhs, ruleMorphism,
                    rootMap, coRootMap, new GraphProperties(),
                    getSystemProperties());
            return result;
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
        private ForallCondition createForall(RuleGraph target,
                RuleGraphMorphism rootMap, String name, boolean positive) {
            ForallCondition result =
                new ForallCondition(new RuleName(name), target, rootMap,
                    getSystemProperties(), this.matchCountImage);
            if (positive) {
                result.setPositive();
            }
            return result;
        }

        /**
         * Callback method to create an ordinary graph morphism.
         * @see #toRule()
         */
        private RuleGraphMorphism createMorphism() {
            return new RuleGraphMorphism();
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
        private final ViewToRuleMap viewToLevelMap = new ViewToRuleMap();
        /** Set of additional (parent level) node type edges. */
        private final Map<RuleNode,Set<TypeLabel>> parentTypeMap =
            new HashMap<RuleNode,Set<TypeLabel>>();
        /** The rule node registering the match count. */
        private VariableNode matchCountImage;
        /** Mapping from view nodes to LHS and NAC nodes. */
        private ViewToRuleMap lhsMap;
        /** Mapping from view nodes to RHS nodes. */
        private ViewToRuleMap rhsMap;
        /** The left hand side graph of the rule. */
        private RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private RuleGraph rhs;
        /** The set of mergers on this level. */
        private Map<AspectEdge,RuleEdge> mergerMap;
        /** Set of all (lhs and rhs) typable nodes. */
        private Set<RuleNode> typableNodes;
        /** Set of all (lhs and rhs) typable edges. */
        private Set<RuleEdge> typableEdges;
        /** Rule morphism (from LHS to RHS). */
        private RuleGraphMorphism ruleMorph;
        /** The set of nodes appearing in NACs. */
        private Set<RuleNode> nacNodeSet;
        /** The set of edges appearing in NACs. */
        private Set<RuleEdge> nacEdgeSet;
        /** Node partition on this quantification level. */
        private Map<AspectNode,SortedSet<AspectNode>> partition;
        /** The current mode of this level data. */
        private LevelMode mode = LevelMode.START;
    }

    /** Intermediate mode values for {@link Level}. */
    private static enum LevelMode {
        START, TREE_SET, FIXED
    }

    /** Class that can extract parameter information from the view graph. */
    private class Parameters {
        /** Lazily creates and returns the rule's hidden parameters. */
        public Set<RuleNode> getHiddenPars() throws FormatException {
            if (this.hiddenPars == null) {
                initialise();
            }
            return this.hiddenPars;
        }

        /** Returns the rule signature. */
        public List<CtrlPar.Var> getSignature() throws FormatException {
            if (this.sig == null) {
                initialise();
            }
            return this.sig;
        }

        /** Initialises the internal data structures. */
        private void initialise() throws FormatException {
            Set<FormatError> errors = new TreeSet<FormatError>();
            this.hiddenPars = new HashSet<RuleNode>();
            // Mapping from parameter position to parameter
            Map<Integer,CtrlPar.Var> parMap =
                new HashMap<Integer,CtrlPar.Var>();
            int parCount = 0;
            // add nodes to nesting data structures
            for (AspectNode node : getAspectGraph().nodeSet()) {
                // check if the node is a parameter
                if (node.hasParam()) {
                    Level level = getLevelTree().getLevel(node);
                    Integer nr = (Integer) node.getParam().getContent();
                    if (nr != null) {
                        if (!level.getIndex().isTopLevel()) {
                            throw new FormatException(
                                "Parameter '%d' only allowed on top existential level",
                                nr, node);
                        }
                        parCount = Math.max(parCount, nr + 1);
                        try {
                            processNode(parMap, node, nr);
                        } catch (FormatException exc) {
                            errors.addAll(exc.getErrors());
                        }
                    } else {
                        // this is an unnumbered parameter,
                        // which serves as an explicit anchor node
                        if (!level.getIndex().isTopLevel()) {
                            throw new FormatException(
                                "Anchor node only allowed on top existential level",
                                node);
                        }
                        if (node.getParamKind() != PARAM_BI) {
                            throw new FormatException(
                                "Anchor node cannot be input or output", node);
                        }
                        if (!node.getKind().inLHS()) {
                            throw new FormatException(
                                "Anchor node must be in LHS", node);
                        }
                        RuleNode nodeImage = level.getLhsMap().getNode(node);
                        this.hiddenPars.add(nodeImage);
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            // construct the signature
            // test if parameters form a consecutive sequence
            Set<Integer> missingPars = new TreeSet<Integer>();
            for (int i = 0; i < parCount; i++) {
                missingPars.add(i);
            }
            missingPars.removeAll(parMap.keySet());
            if (!missingPars.isEmpty()) {
                throw new FormatException("Parameters %s missing", missingPars);
            }
            CtrlPar.Var[] sigArray = new CtrlPar.Var[parCount];
            for (Map.Entry<Integer,CtrlPar.Var> parEntry : parMap.entrySet()) {
                sigArray[parEntry.getKey()] = parEntry.getValue();
            }
            this.sig = Arrays.asList(sigArray);
        }

        private void processNode(Map<Integer,CtrlPar.Var> parMap,
                AspectNode node, Integer nr) throws FormatException {
            AspectKind nodeKind = node.getKind();
            AspectKind paramKind = node.getParamKind();
            boolean hasControl =
                getSystemProperties() != null
                    && getSystemProperties().isUseControl();
            CtrlType varType;
            AspectKind attrKind = node.getAttrKind();
            if (!attrKind.isData()) {
                varType = CtrlType.getNodeType();
            } else if (attrKind == UNTYPED) {
                varType = CtrlType.getAttrType();
            } else {
                varType = CtrlType.getDataType(attrKind.getName());
            }
            CtrlVar var = new CtrlVar("arg" + nr, varType);
            boolean inOnly = paramKind == PARAM_IN;
            boolean outOnly = paramKind == PARAM_OUT;
            if (inOnly && !hasControl) {
                throw new FormatException(
                    "Parameter '%d' is a required input, but no control is in use",
                    nr, node);
            }
            Level level = getLevelTree().getLevel(node);
            RuleNode nodeImage;
            boolean creator;
            if (nodeKind.inLHS()) {
                nodeImage = level.getLhsMap().getNode(node);
                creator = false;
            } else if (nodeKind.inRHS()) {
                if (inOnly) {
                    throw new FormatException(
                        "Creator node cannot be used as input parameter", node);
                }
                outOnly = true;
                nodeImage = level.getRhsMap().getNode(node);
                creator = true;
            } else {
                throw new FormatException(
                    "Parameter '%d' may not occur in NAC", nr, node);
            }
            CtrlPar.Var par =
                inOnly || outOnly ? new CtrlPar.Var(var, inOnly)
                        : new CtrlPar.Var(var);
            par.setRuleNode(nodeImage, creator);
            CtrlPar.Var oldPar = parMap.put(nr, par);
            if (oldPar != null) {
                throw new FormatException(
                    "Parameter '%d' defined more than once", nr, node,
                    oldPar.getRuleNode());
            }
        }

        /** Set of all rule parameter nodes */
        private Set<RuleNode> hiddenPars;
        /** Signature of the rule. */
        private List<CtrlPar.Var> sig;
    }

    /** Mapping from aspect graph elements to rule graph elements. */
    public static class ViewToRuleMap extends ViewToModelMap<RuleNode,RuleEdge> {
        /**
         * Creates a new, empty map.
         */
        public ViewToRuleMap() {
            super(RuleFactory.instance());
        }

        @Override
        public ViewToRuleMap newMap() {
            return new ViewToRuleMap();
        }
    }
}
