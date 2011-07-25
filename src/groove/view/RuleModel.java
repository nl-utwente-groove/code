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

import static groove.view.aspect.AspectKind.CONNECT;
import static groove.view.aspect.AspectKind.EXISTS;
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
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.ElementMap;
import groove.graph.GraphProperties;
import groove.graph.Node;
import groove.graph.TypeEdge;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.VarSupport;
import groove.trans.Condition;
import groove.trans.Condition.Op;
import groove.trans.EdgeEmbargo;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleFactory;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.trans.SystemProperties;
import groove.util.DefaultFixable;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Status;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * Provides a graph-based resource model for a production rule. 
 * The nodes and edges are divided
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
public class RuleModel extends GraphBasedModel<Rule> implements
        Comparable<RuleModel> {
    /**
     * Constructs a rule model from an aspect graph. The rule properties are
     * explicitly given.
     * @param graph the graph to be converted (non-null)
     */
    public RuleModel(GrammarModel grammar, AspectGraph graph) {
        super(grammar, graph);
        graph.testFixed(true);
    }

    /**
     * Returns the priority of the rule of which this is a model. Yields the same
     * result as <code>toRule().getPriority()</code>.
     */
    public int getPriority() {
        return GraphProperties.getPriority(getSource());
    }

    /** Convenience method */
    public String getTransitionLabel() {
        return GraphProperties.getTransitionLabel(getSource());
    }

    /** Convenience method */
    public String getFormatString() {
        return GraphProperties.getFormatString(getSource());
    }

    /**
     * Indicates whether the rule is marked as locally confluent. If the rule is
     * marked as such, only match will be chosen among this and all other
     * locally confluent rules.
     */
    public boolean isConfluent() {
        return GraphProperties.isConfluent(getSource());
    }

    /**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    @Override
    public Rule toResource() throws FormatException {
        return toRule();
    }

    /**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    public Rule toRule() throws FormatException {
        initialise();
        if (this.status == Status.DONE) {
            return this.rule;
        } else {
            throw new FormatException(this.ruleErrors);
        }
    }

    @Override
    public List<FormatError> getErrors() {
        initialise();
        return this.ruleErrors;
    }

    /** Returns the set of labels occurring in this rule. */
    @Override
    public Set<TypeLabel> getLabels() {
        if (this.labelSet == null) {
            this.labelSet = new HashSet<TypeLabel>();
            for (AspectEdge edge : getSource().edgeSet()) {
                RuleLabel label = edge.getRuleLabel();
                if (label != null) {
                    RegExpr labelExpr = label.getMatchExpr();
                    if (labelExpr != null) {
                        this.labelSet.addAll(labelExpr.getTypeLabels());
                    }
                }
            }
        }
        return this.labelSet;
    }

    @Override
    public RuleModelMap getMap() {
        initialise();
        if (this.status == Status.ERROR) {
            throw new IllegalStateException();
        }
        return this.modelMap;
    }

    public int compareTo(RuleModel o) {
        int result = getPriority() - o.getPriority();
        if (result == 0) {
            result = getName().compareTo(o.getName());
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Rule model on '%s'", getName());
    }

    /** Returns the (possibly {@code null}) type graph of this rule. */
    final TypeGraph getType() {
        return getGrammar().getTypeGraph();
    }

    /** Indicates if this rule is typed. */
    boolean isTyped() {
        return getType() != null;
    }

    /**
     * @return Returns the properties.
     */
    final SystemProperties getSystemProperties() {
        return getGrammar().getProperties();
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

    private final void initialise() {
        // reset if the grammar was modified
        if (isGrammarModified()) {
            this.status = Status.START;
        } else if (this.status != Status.START) {
            return;
        }
        if (getSource().hasErrors()) {
            this.ruleErrors.addAll(getSource().getErrors());
            this.status = Status.ERROR;
            return;
        }
        AspectGraph normalSource = getSource().normalise();
        if (normalSource.hasErrors()) {
            this.ruleErrors.addAll(normalSource.getErrors());
            this.status = Status.ERROR;
            return;
        }
        try {
            LevelTree levelTree = new LevelTree(normalSource);
            this.modelMap.clear();
            this.modelMap.putAll(levelTree.getModelMap());
            this.rule = computeRule(levelTree);
            this.status = Status.DONE;
        } catch (FormatException exc) {
            this.ruleErrors.addAll(exc.getErrors());
            this.status = Status.ERROR;
            return;
        }
    }

    /**
     * Callback method to compute a rule from the source graph. All auxiliary data
     * structures are assumed to be initialised but empty. After method return,
     * the structures are filled.
     * @throws FormatException if the model cannot be converted to a valid rule
     */
    private Rule computeRule(LevelTree levelTree) throws FormatException {
        Rule result;
        Collection<FormatError> errors = createErrors();
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        // store the derived subrules in order
        TreeMap<Index,Condition> ruleTree = new TreeMap<Index,Condition>();
        // construct the rule tree and add parent rules
        try {
            for (Level4 level : levelTree.getLevels()) {
                Index index = level.getIndex();
                Op operator = index.getOperator();
                Condition condition;
                if (operator.isQuantifier()) {
                    condition = level.computeFlatRule();
                } else {
                    condition = new Condition(index.getName(), operator);
                }
                ruleTree.put(index, condition);
                if (condition.hasRule() && !index.isTopLevel()) {
                    // look for the first parent rule
                    Index parentIndex = index.getParent();
                    while (!ruleTree.get(parentIndex).hasRule()) {
                        parentIndex = parentIndex.getParent();
                    }
                    condition.getRule().setParent(
                        ruleTree.get(parentIndex).getRule(),
                        index.getIntArray());
                }
            }
            // now add subconditions and fix the conditions
            // this needs to be done bottom-up
            for (Map.Entry<Index,Condition> entry : ruleTree.descendingMap().entrySet()) {
                Condition condition = entry.getValue();
                assert condition != null;
                Index index = entry.getKey();
                if (!index.isTopLevel()) {
                    condition.setFixed();
                    Condition parentCond = ruleTree.get(index.getParent());
                    parentCond.addSubCondition(condition);
                }
            }
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        // due to errors in the above, it might be that the
        // rule tree is empty, in which case we shouldn't proceed
        if (ruleTree.isEmpty()) {
            result = null;
        } else {
            result = ruleTree.firstEntry().getValue().getRule();
            if (result != null) {
                result.setPriority(getPriority());
                result.setConfluent(isConfluent());
                result.setTransitionLabel(getTransitionLabel());
                result.setFormatString(getFormatString());
                result.setCheckDangling(getSystemProperties().isCheckDangling());
                Parameters parameters = new Parameters();
                result.setSignature(parameters.getSignature(),
                    parameters.getHiddenPars());
                try {
                    result.setFixed();
                    if (TO_RULE_DEBUG) {
                        System.out.println("Constructed rule: " + result);
                    }
                } catch (FormatException exc) {
                    result = null;
                    errors.addAll(exc.getErrors());
                }
            }
        }
        if (errors.isEmpty()) {
            return result;
        } else {
            throw new FormatException(transferErrors(errors,
                levelTree.getModelMap()));
        }
    }

    /** Transfers a collection of errors according to the
     * inverse of a model map.
     * @param errors the original errors
     * @param map mapping from aspect elements to rule graph elements
     * @return the transferred errors
     */
    final Collection<FormatError> transferErrors(
            Collection<FormatError> errors, ElementMap<?,?,?,?> map) {
        Map<Element,Element> inverseMap = getInverseMap(map);
        Collection<FormatError> newErrors = createErrors();
        for (FormatError error : errors) {
            newErrors.add(error.transfer(inverseMap));
        }
        return newErrors;
    }

    /** Convenience method to return the inverse of a given model map. */
    private final Map<Element,Element> getInverseMap(ElementMap<?,?,?,?> map) {
        Map<Element,Element> result = new HashMap<Element,Element>();
        for (Map.Entry<? extends Node,? extends Node> nodeEntry : map.nodeMap().entrySet()) {
            result.put(nodeEntry.getValue(), nodeEntry.getKey());
        }
        for (Map.Entry<? extends Edge<?>,? extends Edge<?>> edgeEntry : map.edgeMap().entrySet()) {
            result.put(edgeEntry.getValue(), edgeEntry.getKey());
        }
        return result;
    }

    /**
     * Callback method to create a graph that can serve as LHS or RHS of a rule.
     * @see #getSource()
     */
    RuleGraph createGraph(String name) {
        return new RuleGraph(name);
    }

    /** Callback factory method to create an appropriate error collection. */
    Collection<FormatError> createErrors() {
        return new TreeSet<FormatError>();
    }

    /** Status of the rule construction. */
    private Status status = Status.START;
    /**
     * Mapping from the elements of the aspect graph representation to the
     * corresponding elements of the rule.
     */
    private final RuleModelMap modelMap = new RuleModelMap();

    /** Set of all labels occurring in the rule. */
    private Set<TypeLabel> labelSet;
    /** Errors found while converting the model to a rule. */
    private final List<FormatError> ruleErrors = new ArrayList<FormatError>();
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    static private final RuleFactory ruleFactory = RuleFactory.instance();
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;
    /** Flag for switching on new type system. */
    static private final boolean NEW_TYPING = false;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    private class Index extends DefaultFixable implements Comparable<Index> {
        /**
         * Constructs a new level, without setting parent or children.
         * @param levelNode the model level node representing this level; may be
         *        <code>null</code> for an implicit or top level
         */
        public Index(Condition.Op operator, boolean positive,
                AspectNode levelNode) {
            assert levelNode == null || levelNode.getKind().isQuantifier();
            this.operator = operator;
            this.positive = positive;
            this.levelNode = levelNode;
        }

        /**
         * Sets the parent and index of this level.
         * @param parent the parent of this level.
         */
        public void setParent(Index parent, int nr) {
            testFixed(false);
            assert this.parent == null && parent.isFixed();
            this.parent = parent;
            this.index = new ArrayList<Integer>(parent.index.size() + 1);
            this.index.addAll(parent.index);
            this.index.add(nr);
            setFixed();
        }

        @Override
        public void setFixed() {
            // if the index is null, this is the top level node
            if (this.index == null) {
                this.index = Collections.emptyList();
            }
            super.setFixed();
        }

        /** Returns the parent level of this tree index. */
        public Index getParent() {
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
                return RuleModel.this.getName()
                    + (isTopLevel() ? ""
                            : Groove.toString(this.index.toArray()));
            } else {
                return levelName;
            }
        }

        /** Lexicographically compares the tree indices. 
         * @see #getIntArray() */
        public int compareTo(Index o) {
            int result = 0;
            int[] mine = getIntArray();
            int[] other = o.getIntArray();
            int minLength = Math.min(mine.length, other.length);
            for (int i = 0; result == 0 && i < minLength; i++) {
                result = mine[i] - other[i];
            }
            if (result == 0) {
                result = mine.length - other.length;
            }
            return result;
        }

        /**
         * Tests if this level is smaller (i.e., higher up in the nesting tree)
         * than another, or equal to it. This is the case if the depth of this 
         * nesting does not exceed that of the other, and the indices at every 
         * (common) level coincide.
         */
        public boolean higherThan(Index other) {
            assert isFixed() && other.isFixed();
            boolean result = this.index.size() <= other.index.size();
            for (int i = 0; result && i < this.index.size(); i++) {
                result = this.index.get(i).equals(other.index.get(i));
            }
            return result;
        }

        /**
         * Converts this level to an array of {@code int}s. May only be called
         * after {@link #setParent(Index,int)}.
         */
        public int[] getIntArray() {
            testFixed(true);
            int[] result = new int[this.index.size()];
            for (int i = 0; i < this.index.size(); i++) {
                result[i] = this.index.get(i);
            }
            return result;
        }

        /**
         * Indicates whether this is the top level. May only be called after
         * {@link #setParent(Index,int)}.
         */
        public boolean isTopLevel() {
            testFixed(true);
            return this.parent == null;
        }

        /** Returns the conditional operator of this level. */
        public Op getOperator() {
            return this.operator;
        }

        /**
         * Indicates, for a universal level, if the level is positive.
         */
        public boolean isPositive() {
            return this.positive;
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

        /** The model node representing this quantification level. */
        final Condition.Op operator;
        /** Flag indicating that this level has to be matched more than once. */
        final boolean positive;
        /** The model node representing this quantification level. */
        final AspectNode levelNode;
        /** The index uniquely identifying this level. */
        List<Integer> index;
        /** Parent of this tree index; may be <code>null</code> */
        Index parent;
    }

    /** Tree of quantification levels occurring in this rule model. */
    private class LevelTree {
        /** Constructs an instance for a given source graph. */
        public LevelTree(AspectGraph source) throws FormatException {
            this.source = source;
            SortedSet<Index> indexSet = buildTree();
            SortedMap<Index,Level1> level1Map = buildLevels1(indexSet);
            RuleModelMap untypedModelMap = new RuleModelMap();
            SortedMap<Index,Level2> level2Map =
                buildLevels2(level1Map, untypedModelMap);
            RuleGraphMorphism typingMap = new RuleGraphMorphism();
            try {
                SortedMap<Index,Level3> level3Map =
                    buildLevels3(level2Map, typingMap);
                this.level4Map =
                    NEW_TYPING ? build4From3(level3Map)
                            : build4From2(level2Map);
            } catch (FormatException e) {
                throw new FormatException(transferErrors(e.getErrors(),
                    untypedModelMap));
            }
            if (NEW_TYPING) {
                RuleModelMap modelMap = new RuleModelMap();
                for (Map.Entry<AspectNode,RuleNode> nodeEntry : untypedModelMap.nodeMap().entrySet()) {
                    RuleNode image = typingMap.getNode(nodeEntry.getValue());
                    if (image != null) {
                        modelMap.putNode(nodeEntry.getKey(), image);
                    }
                }
                for (Map.Entry<AspectEdge,RuleEdge> edgeEntry : untypedModelMap.edgeMap().entrySet()) {
                    RuleEdge image = typingMap.getEdge(edgeEntry.getValue());
                    if (image != null) {
                        modelMap.putEdge(edgeEntry.getKey(), image);
                    }
                }
                this.modelMap = modelMap;
            } else {
                this.modelMap = untypedModelMap;
            }
        }

        /** Builds the level data maps. */
        private SortedSet<Index> buildTree() {
            // First build an explicit tree of level nodes
            Map<Index,List<Index>> indexTree = new HashMap<Index,List<Index>>();
            this.topLevelIndex = createIndex(Op.EXISTS, false, null, indexTree);
            // initialise the data structures
            this.metaIndexMap = new HashMap<AspectNode,Index>();
            this.nameIndexMap = new HashMap<String,Index>();
            // Mapping from levels to match count nodes
            this.matchCountMap = new HashMap<Index,AspectNode>();
            // build the index tree
            indexTree.put(this.topLevelIndex, new ArrayList<Index>());
            for (AspectNode node : this.source.nodeSet()) {
                AspectKind nodeKind = node.getKind();
                if (nodeKind.isQuantifier()) {
                    // look for the parent level
                    Index parentIndex;
                    // by the correctness of the aspect graph we know that
                    // there is at most one outgoing edge, which is a parent
                    // edge and points to the parent level node
                    AspectNode parentNode = node.getNestingParent();
                    if (parentNode == null) {
                        parentIndex = this.topLevelIndex;
                    } else {
                        AspectKind parentKind = parentNode.getKind();
                        parentIndex =
                            getIndex(parentKind, parentNode, indexTree);
                    }
                    Index myIndex = getIndex(nodeKind, node, indexTree);
                    indexTree.get(parentIndex).add(myIndex);
                    if (node.getMatchCount() != null) {
                        this.matchCountMap.put(myIndex, node.getMatchCount());
                    }
                }
            }
            // insert the children into the indices themselves and build the index set
            SortedSet<Index> indexSet = new TreeSet<Index>();
            Queue<Index> indexQueue = new LinkedList<Index>();
            indexQueue.add(this.topLevelIndex);
            while (!indexQueue.isEmpty()) {
                Index next = indexQueue.poll();
                next.setFixed();
                List<Index> children = indexTree.get(next);
                // add an implicit existential sub-level to childless universal
                // levels
                if (next.getOperator() == Op.FORALL && children.isEmpty()) {
                    Index implicitChild =
                        createIndex(Op.EXISTS, true, null, indexTree);
                    children.add(implicitChild);
                }
                // set the parent of all children
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).setParent(next, i);
                }
                indexQueue.addAll(children);
                indexSet.add(next);
            }
            return indexSet;
        }

        /**
         * Lazily creates and returns a level index for a given level meta-node.
         * @param metaNode the level node for which a level is to be created;
         *        should satisfy
         *        {@link AspectKind#isQuantifier()}
         */
        private Index getIndex(AspectKind quantifier, AspectNode metaNode,
                Map<Index,List<Index>> indexTree) {
            Index result = this.metaIndexMap.get(metaNode);
            if (result == null) {
                AspectKind kind = metaNode.getKind();
                Condition.Op operator = kind.isExists() ? Op.EXISTS : Op.FORALL;
                boolean positive = kind == EXISTS || kind == FORALL_POS;
                this.metaIndexMap.put(
                    metaNode,
                    result =
                        createIndex(operator, positive, metaNode, indexTree));
                String name = metaNode.getLevelName();
                if (name != null && name.length() > 0) {
                    this.nameIndexMap.put(name, result);
                }
            }
            return result;
        }

        /** Creates a level index for a given meta-node and creates
         * an entry in the level tree.
         * @param levelNode the quantifier meta-node
         * @param levelTree the tree of level indices
         * @return the fresh level index
         */
        private Index createIndex(Condition.Op operator, boolean positive,
                AspectNode levelNode, Map<Index,List<Index>> levelTree) {
            Index result = new Index(operator, positive, levelNode);
            levelTree.put(result, new ArrayList<Index>());
            return result;
        }

        /**
         * Returns the maximum (i.e., lowest-level) level of this and another,
         * given level; or {@code null} if neither is smaller than the other.
         */
        private Level1 max(Level1 first, Level1 other) {
            if (first.index.higherThan(other.index)) {
                return other;
            } else if (other.index.higherThan(first.index)) {
                return first;
            } else {
                return null;
            }
        }

        /** Constructs the stage 1 rule levels. */
        private SortedMap<Index,Level1> buildLevels1(SortedSet<Index> indexSet)
            throws FormatException {
            Collection<FormatError> errors = createErrors();
            // Set the parentage in tree preorder
            // Build the level data map,
            // in the tree-order of the indices
            SortedMap<Index,Level1> result = new TreeMap<Index,Level1>();
            for (Index index : indexSet) {
                Level1 parentLevel =
                    index.isTopLevel() ? null : result.get(index.getParent());
                Level1 level = new Level1(index, parentLevel);
                result.put(index, level);
            }
            // initialise the match count nodes are defined at super-levels
            for (Map.Entry<Index,AspectNode> matchCountEntry : this.matchCountMap.entrySet()) {
                AspectNode matchCount = matchCountEntry.getValue();
                Index definedAt = getLevel(result, matchCount).getIndex();
                Index usedAt = matchCountEntry.getKey();
                if (!definedAt.higherThan(usedAt) || definedAt.equals(usedAt)) {
                    throw new FormatException(
                        "Match count not defined at appropriate level",
                        matchCount);
                }
                Level1 level = result.get(usedAt);
                level.setMatchCount(matchCount);
            }
            // add nodes to nesting data structures
            for (AspectNode node : this.source.nodeSet()) {
                if (!node.getKind().isMeta()) {
                    getLevel(result, node).addNode(node);
                }
            }
            // add edges to nesting data structures
            for (AspectEdge edge : this.source.edgeSet()) {
                try {
                    if (!edge.getKind().isMeta()) {
                        getLevel(result, edge).addEdge(edge);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            return result;
        }

        /**
         * Returns the quantification level of a given aspect rule node.
         * @param node the node for which the quantification level is
         *        determined; must fail to satisfy
         *        {@link AspectKind#isMeta()}
         * @return the level for {@code node}; non-null
         */
        private Level1 getLevel(Map<Index,Level1> level1Map, AspectNode node) {
            Level1 result = getNodeLevelMap().get(node);
            if (result == null) {
                // find the corresponding quantifier node
                AspectNode nestingNode = node.getNestingLevel();
                Index index =
                    nestingNode == null ? this.topLevelIndex
                            : this.metaIndexMap.get(nestingNode);
                assert index != null : String.format(
                    "No valid nesting level found for %s", node);
                result = level1Map.get(index);
                assert result != null : String.format(
                    "Level map %s does not contain entry for index %s",
                    level1Map, index);
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
        private Level1 getLevel(Map<Index,Level1> level1Map, AspectEdge edge)
            throws FormatException {
            Level1 sourceLevel = getLevel(level1Map, edge.source());
            Level1 targetLevel = getLevel(level1Map, edge.target());
            Level1 result = max(sourceLevel, targetLevel);
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
                Index edgeLevelIndex = this.nameIndexMap.get(levelName);
                if (edgeLevelIndex == null) {
                    throw new FormatException(
                        "Undefined nesting level '%s' in edge %s", levelName,
                        edge);
                }
                result = max(result, level1Map.get(edgeLevelIndex));
                if (result == null) {
                    throw new FormatException(
                        "Nesting level %s in edge %s is incompatible with end nodes",
                        levelName, edge);
                }
            }
            return result;
        }

        /**
         * Lazily creates and returns the mapping from rule model nodes to the
         * corresponding quantification levels.
         */
        private Map<AspectNode,Level1> getNodeLevelMap() {
            if (this.nodeLevelMap == null) {
                this.nodeLevelMap = new HashMap<AspectNode,Level1>();
            }
            return this.nodeLevelMap;
        }

        /** Constructs the level2 map. */
        private SortedMap<Index,Level2> buildLevels2(
                SortedMap<Index,Level1> level1Map, RuleModelMap modelMap)
            throws FormatException {
            SortedMap<Index,Level2> rersult = new TreeMap<Index,Level2>();
            Collection<FormatError> errors = createErrors();
            for (Level1 level1 : level1Map.values()) {
                try {
                    Index index = level1.getIndex();
                    Level2 level2 = new Level2(level1, modelMap);
                    rersult.put(index, level2);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            return rersult;
        }

        /** Constructs the level3 map. */
        private SortedMap<Index,Level3> buildLevels3(
                SortedMap<Index,Level2> level2Map, RuleGraphMorphism typingMap)
            throws FormatException {
            SortedMap<Index,Level3> result = new TreeMap<Index,Level3>();
            Collection<FormatError> errors = createErrors();
            for (Level2 level2 : level2Map.values()) {
                Index index = level2.getIndex();
                Level3 parent =
                    index.isTopLevel() ? null : result.get(index.getParent());
                Level3 level3 = new Level3(level2, parent, typingMap);
                result.put(index, level3);
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            return result;
        }

        /** Constructs the level4 map. */
        private SortedMap<Index,Level4> build4From3(
                SortedMap<Index,Level3> level3Map) {
            SortedMap<Index,Level4> result = new TreeMap<Index,Level4>();
            for (Level3 level3 : level3Map.values()) {
                Index index = level3.getIndex();
                Level4 parent =
                    index.isTopLevel() ? null : result.get(index.getParent());
                Level4 level4 = new Level4(level3, parent);
                result.put(index, level4);
            }
            return result;
        }

        /** Constructs the level4 map. */
        private SortedMap<Index,Level4> build4From2(
                SortedMap<Index,Level2> level2Map) {
            SortedMap<Index,Level4> result = new TreeMap<Index,Level4>();
            for (Level2 level2 : level2Map.values()) {
                Index index = level2.getIndex();
                Level4 parent =
                    index.isTopLevel() ? null : result.get(index.getParent());
                Level4 level4 = new Level4(level2, parent);
                result.put(index, level4);
            }
            return result;
        }

        /**
         * Returns the quantification levels in ascending or descending order
         */
        public final Collection<Level4> getLevels() {
            return this.level4Map.values();
        }

        /** Returns the mapping from aspect graph elements to rule elements. */
        public final RuleModelMap getModelMap() {
            return this.modelMap;
        }

        @Override
        public String toString() {
            return "LevelMap: " + this.level4Map;
        }

        /** The normalised source of the rule model. */
        private final AspectGraph source;
        /** The top level of the rule tree. */
        private Index topLevelIndex;
        /** Mapping from level indices to stage 4 levels. */
        private SortedMap<Index,Level4> level4Map;
        /** mapping from nesting meta-nodes nodes to nesting levels. */
        private Map<AspectNode,Index> metaIndexMap;
        /** mapping from nesting level names to nesting levels. */
        private Map<String,Index> nameIndexMap;
        /** Mapping from model nodes to the corresponding nesting level. */
        private Map<AspectNode,Level1> nodeLevelMap;
        /** Mapping from (universal) levels to match count nodes. */
        private Map<Index,AspectNode> matchCountMap;
        /** Mapping from aspect graph elements to untyped rule elements. */
        private RuleModelMap modelMap;
    }

    /**
     * Class collecting all rule model elements on a given rule level.
     * The elements are not yet differentiated by role. 
     * This is the first stage of constructing the
     * flat rule at that level.
     */
    private class Level1 implements Comparable<Level1> {
        /**
         * Creates a new level, with a given index and parent level.
         * @param index the index of the new level
         * @param parent the parent level; may be {@code null} if this is the
         *        top level.
         */
        public Level1(Index index, Level1 parent) {
            this.index = index;
            this.parent = parent;
            if (parent != null) {
                assert index.getParent().equals(parent.getIndex()) : String.format(
                    "Parent index %s should be parent of %s", parent.index,
                    index);
                parent.addChild(this);
            } else {
                assert index.isTopLevel() : String.format(
                    "Level with index %s should have non-null parent", index);
            }
        }

        /** Adds a child level to this level. */
        private void addChild(Level1 child) {
            assert this.index.equals(child.index.parent);
            this.children.add(child);
        }

        /**
         * Considers adding a node to the set of nodes on this level. The node
         * is also added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addNode(AspectNode modelNode) {
            if (isForThisLevel(modelNode)) {
                // put the node on this level
                this.modelNodes.add(modelNode);
            }
            // put the node on the sublevels, if it is supposed to be there
            if (isForNextLevel(modelNode)) {
                for (Level1 sublevel : this.children) {
                    sublevel.addNode(modelNode);
                }
            }
        }

        /**
         * Consider adding an edge to the set of edges on this level. The edge
         * is also added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addEdge(AspectEdge modelEdge) {
            if (isForThisLevel(modelEdge)) {
                // put the edge on this level
                this.modelEdges.add(modelEdge);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                addNodeToParents(modelEdge.source());
                addNodeToParents(modelEdge.target());
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(modelEdge)) {
                for (Level1 sublevel : this.children) {
                    sublevel.addEdge(modelEdge);
                }
            }
        }

        /** Initialises the match count for this (universal) level. */
        public void setMatchCount(AspectNode matchCount) {
            this.matchCountNode = matchCount;
        }

        /**
         * Adds a node to this and all parent levels, if it is not yet there
         */
        private void addNodeToParents(AspectNode modelNode) {
            Level1 ascendingLevel = this;
            while (ascendingLevel.modelNodes.add(modelNode)) {
                assert !ascendingLevel.index.isTopLevel() : String.format("Node not found at any level");
                ascendingLevel = ascendingLevel.parent;
                assert ascendingLevel.modelNodes != null : String.format(
                    "Nodes on level %s not yet initialised",
                    ascendingLevel.getIndex());
            }
        }

        /**
         * Indicates if a given element should be included on the level on which
         * it it is defined in the model. Node creators should not appear on
         * universal levels since those get translated to conditions, not rules;
         * instead they are pushed to the next (existential) sublevels.
         * @param elem the element about which the question is asked
         */
        private boolean isForThisLevel(AspectElement elem) {
            return this.index.getOperator().hasPattern();
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the model. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for edges that bind variables.
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(AspectElement elem) {
            assert !elem.getKind().isMeta();
            boolean result = false;
            if (!this.index.getOperator().hasPattern()) {
                result = true;
            } else if (elem instanceof AspectNode) {
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
            return result;
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 1", getName(),
                getIndex());
        }

        @Override
        public int compareTo(Level1 o) {
            return getIndex().compareTo(o.getIndex());
        }

        /** Index of this level. */
        final Index index;
        /** Parent level; {@code null} if this is the top level. */
        private final Level1 parent;
        /** Children level data. */
        private final List<Level1> children = new ArrayList<Level1>();
        /** Set of model nodes on this level. */
        final Set<AspectNode> modelNodes = new HashSet<AspectNode>();
        /** Set of model edges on this level. */
        final Set<AspectEdge> modelEdges = new HashSet<AspectEdge>();
        /** The model node registering the match count. */
        AspectNode matchCountNode;
    }

    /**
     * Class containing all rule elements on a given rule level,
     * differentiated by role (LHS, RHS and NACs).
     */
    private class Level2 {
        /**
         * Creates a new level, with a given index and parent level.
         * @param origin the level 1 object from which this level 2 object is created
         */
        public Level2(Level1 origin, RuleModelMap modelMap)
            throws FormatException {
            Index index = this.index = origin.index;
            this.modelMap = modelMap;
            this.isRule = index.isTopLevel();
            // initialise the rule data structures
            this.lhs = createGraph(getName() + "-" + index + "-lhs");
            this.mid = createGraph(getName() + "-" + index + "-mid");
            this.rhs = createGraph(getName() + "-" + index + "-rhs");
            Collection<FormatError> errors = createErrors();
            try {
                if (origin.matchCountNode != null) {
                    this.matchCountImage =
                        (VariableNode) getNodeImage(origin.matchCountNode);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            for (AspectNode modelNode : origin.modelNodes) {
                try {
                    processNode(modelNode);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            // if there are errors in the node map, don't try mapping the edges
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            for (AspectEdge modelEdge : origin.modelEdges) {
                try {
                    if (modelEdge.getKind() == CONNECT) {
                        addConnect(modelEdge);
                    } else {
                        processEdge(modelEdge);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            try {
                this.nacs.addAll(computeNacs());
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            checkAttributes(errors);
            checkVariables(errors);
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
        }

        /**
         * Adds a node to the LHS, RHS or NAC node set, whichever is appropriate.
         */
        private void processNode(AspectNode modelNode) throws FormatException {
            AspectKind nodeKind = modelNode.getKind();
            this.isRule |= nodeKind.inLHS() != nodeKind.inRHS();
            RuleNode ruleNode = getNodeImage(modelNode);
            if (nodeKind.inLHS()) {
                this.lhs.addNode(ruleNode);
                if (nodeKind.inRHS()) {
                    this.rhs.addNode(ruleNode);
                    this.mid.addNode(ruleNode);
                }
            } else {
                if (nodeKind.inNAC()) {
                    // embargo node
                    this.nacNodeSet.add(ruleNode);
                }
                if (nodeKind.inRHS()) {
                    // creator node
                    this.rhs.addNode(ruleNode);
                    if (isRhsAsNac()) {
                        this.nacNodeSet.add(ruleNode);
                    }
                }
            }
            if (modelNode.hasColor()) {
                this.colorMap.put(ruleNode,
                    (Color) modelNode.getColor().getContent());
            }
        }

        /**
         * Adds an edge to the LHS, RHS or NAC edge set, whichever is appropriate.
         */
        private void processEdge(AspectEdge modelEdge) throws FormatException {
            AspectKind edgeKind = modelEdge.getKind();
            this.isRule |= edgeKind.inLHS() != edgeKind.inRHS();
            // flag indicating that the rule edge is fresh in the LHS
            RuleEdge ruleEdge = getEdgeImage(modelEdge);
            if (edgeKind.inLHS()) {
                boolean freshInLhs = this.lhs.addEdge(ruleEdge);
                if (freshInLhs) {
                    if (edgeKind.inRHS()) {
                        this.rhs.addEdge(ruleEdge);
                        this.mid.addEdge(ruleEdge);
                    } else if (isTyped() && ruleEdge.label().isNodeType()
                        && this.rhs.containsNode(ruleEdge.source())) {
                        throw new FormatException(
                            "Node type %s cannot be deleted", ruleEdge.label(),
                            ruleEdge.source());
                    }
                } else {
                    if (!edgeKind.inRHS()) {
                        // remove the edge from the RHS, if it was there
                        // (which is the case if it also exists as reader edge)
                        this.rhs.removeEdge(ruleEdge);
                        this.mid.removeEdge(ruleEdge);
                    }
                }
            } else {
                if (edgeKind.inNAC()) {
                    // embargo edge
                    this.nacEdgeSet.add(ruleEdge);
                }
                if (edgeKind.inRHS()) {
                    if (isTyped() && ruleEdge.label().isNodeType()
                        && this.lhs.containsNode(ruleEdge.source())) {
                        throw new FormatException(
                            "Node type %s cannot be created", ruleEdge.label(),
                            ruleEdge.source());
                    }
                    // creator edge
                    this.rhs.addEdge(ruleEdge);
                    if (isRhsAsNac()) {
                        this.nacEdgeSet.add(ruleEdge);
                    } else if (isCheckCreatorEdges()
                        && modelEdge.source().getKind().inLHS()
                        && modelEdge.target().getKind().inLHS()) {
                        this.nacEdgeSet.add(ruleEdge);
                    }
                }
            }
        }

        /** Adds a NAC connection edge. */
        private void addConnect(AspectEdge connectEdge) throws FormatException {
            RuleNode node1 = getNodeImage(connectEdge.source());
            RuleNode node2 = getNodeImage(connectEdge.target());
            Set<RuleNode> nodeSet =
                new HashSet<RuleNode>(Arrays.asList(node1, node2));
            this.connectMap.put(connectEdge, nodeSet);
        }

        /** Constructs the NACs for this rule. */
        private List<RuleGraph> computeNacs() throws FormatException {
            List<RuleGraph> result = new ArrayList<RuleGraph>();
            Collection<FormatError> errors = createErrors();
            // add the nacs to the rule
            // find connected sets of NAC nodes, taking the
            // connection edges into account
            Set<Pair<Set<RuleNode>,Set<RuleEdge>>> partition =
                AbstractGraph.getConnectedSets(this.nacNodeSet, this.nacEdgeSet);
            for (Map.Entry<AspectEdge,Set<RuleNode>> connection : this.connectMap.entrySet()) {
                // find the (separate) cells for the target nodes of the connect edge
                Set<RuleNode> newNodes = new HashSet<RuleNode>();
                Set<RuleEdge> newEdges = new HashSet<RuleEdge>();
                for (RuleNode node : connection.getValue()) {
                    boolean found = false;
                    Iterator<Pair<Set<RuleNode>,Set<RuleEdge>>> cellIter =
                        partition.iterator();
                    while (cellIter.hasNext()) {
                        Pair<Set<RuleNode>,Set<RuleEdge>> cell =
                            cellIter.next();
                        if (cell.one().contains(node)) {
                            found = true;
                            cellIter.remove();
                            newNodes.addAll(cell.one());
                            newEdges.addAll(cell.two());
                            break;
                        }
                    }
                    if (!found) {
                        throw new FormatException(
                            "Connect edge should be between distinct NACs",
                            connection.getKey());
                    }
                }
                partition.add(Pair.newPair(newNodes, newEdges));
            }
            for (Pair<Set<RuleNode>,Set<RuleEdge>> nacPair : partition) {
                Set<RuleNode> nacNodes = nacPair.one();
                Set<RuleEdge> nacEdges = nacPair.two();
                // construct the NAC graph
                RuleGraph nac =
                    createGraph(this.lhs.getName() + "-nac-" + result.size());
                nac.addNodeSet(nacNodes);
                nac.addEdgeSet(nacEdges);
                result.add(nac);
            }
            if (errors.isEmpty()) {
                return result;
            } else {
                throw new FormatException(errors);
            }
        }

        /**
         * Checks if all product nodes have all their arguments.
         */
        private void checkAttributes(Collection<FormatError> errors) {
            // check if product nodes have all their arguments (on this level)
            for (RuleNode prodNode : this.lhs.nodeSet()) {
                if (prodNode instanceof ProductNode
                    && !this.lhs.nodeSet().containsAll(
                        ((ProductNode) prodNode).getArguments())) {
                    // collect all affected nodes
                    Set<RuleNode> nodes =
                        new HashSet<RuleNode>(
                            ((ProductNode) prodNode).getArguments());
                    nodes.removeAll(this.lhs.nodeSet());
                    nodes.add(prodNode);
                    errors.add(new FormatError(
                        "Arguments must be bound on the level of the product node",
                        nodes.toArray()));

                }
            }
        }

        /**
         * Checks if all label variables are bound
         */
        private void checkVariables(Collection<FormatError> errors) {
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
            if (isTyped()) {
                // check use of variables
                lhsVarEdges.removeAll(this.mid.edgeSet());
                for (RuleEdge eraserVarEdge : lhsVarEdges) {
                    for (LabelVar var : VarSupport.getAllVars(eraserVarEdge)) {
                        errors.add(new FormatError(
                            "Typed rule cannot contain variable eraser '%s'",
                            var, eraserVarEdge));
                    }
                }
                rhsVarEdges.removeAll(this.mid.edgeSet());
                for (RuleEdge creatorVarEdge : rhsVarEdges) {
                    for (LabelVar var : VarSupport.getAllVars(creatorVarEdge)) {
                        errors.add(new FormatError(
                            "Typed rule cannot contain variable creator '%s'",
                            var, creatorVarEdge));
                    }
                }
            }
        }

        /**
         * Lazily creates and returns a rule image for a given model node.
         * @param modelNode the node for which an image is to be created
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private RuleNode getNodeImage(AspectNode modelNode)
            throws FormatException {
            RuleNode result = this.modelMap.getNode(modelNode);
            if (result == null) {
                this.modelMap.putNode(modelNode, result =
                    computeNodeImage(modelNode));
            }
            return result;
        }

        /**
         * Lazily creates and returns a rule image for a given model edge.
         * @param modelEdge the node for which an image is to be created
         * @return the rule edge corresponding to <code>viewEdge</code>; may be
         *         <code>null</code>
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private RuleEdge getEdgeImage(AspectEdge modelEdge)
            throws FormatException {
            RuleEdge result = this.modelMap.getEdge(modelEdge);
            if (result == null) {
                result = computeEdgeImage(modelEdge, this.modelMap.nodeMap());
                if (result != null) {
                    this.modelMap.putEdge(modelEdge, result);
                }
            }
            return result;
        }

        /**
         * Creates an image for a given aspect node. Node numbers are copied.
         * @param node the node for which an image is to be created
         * @return the fresh node
         * @throws FormatException if <code>node</code> does not occur in a correct
         *         way in <code>context</code>
         */
        private RuleNode computeNodeImage(AspectNode node)
            throws FormatException {
            if (node.hasParam() && !this.index.isTopLevel()) {
                throw new FormatException(
                    "Parameter '%d' only allowed on top existential level",
                    node.getNumber(), node);
            }
            AspectKind nodeAttrKind = node.getAttrKind();
            if (nodeAttrKind == PRODUCT) {
                return new ProductNode(node.getNumber(),
                    node.getArgNodes().size());
            } else if (nodeAttrKind.isData()) {
                return node.getAttrAspect().getVariableNode(node.getNumber());
            } else {
                return ruleFactory.createNode(node.getNumber());
            }
        }

        /**
         * Creates an edge by copying a given model edge under a given node mapping. The
         * mapping is assumed to have images for all end nodes.
         * @param edge the edge for which an image is to be created
         * @param elementMap the mapping of the end nodes
         * @return the new edge
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

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 2", getName(),
                getIndex());
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        /** Mapping from aspect graph elements to rule elements. */
        private final RuleModelMap modelMap;
        /** Index of this level. */
        private final Index index;
        /** Map of all connect edges on this level. */
        private final Map<AspectEdge,Set<RuleNode>> connectMap =
            new HashMap<AspectEdge,Set<RuleNode>>();
        /** The rule node registering the match count. */
        private VariableNode matchCountImage;
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap =
            new HashMap<RuleNode,Color>();
        /** Flag indicating that modifiers have been found at this level. */
        private boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** Rule morphism (from LHS to RHS). */
        private final RuleGraph mid;
        /** The set of nodes appearing in NACs. */
        private final Set<RuleNode> nacNodeSet = new HashSet<RuleNode>();
        /** The set of edges appearing in NACs. */
        private final Set<RuleEdge> nacEdgeSet = new HashSet<RuleEdge>();
        /** Collection of NAC graphs. */
        private final List<RuleGraph> nacs = new ArrayList<RuleGraph>();
    }

    /**
     * A level 3 rule is a typed version of a level 2 rule,
     * or identical to the level 2 rule if there is no type graph.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class Level3 {
        public Level3(Level2 origin, Level3 parent,
                RuleGraphMorphism globalTypeMap) throws FormatException {
            this.index = origin.index;
            this.matchCountImage = origin.matchCountImage;
            this.globalTypeMap = globalTypeMap;
            RuleGraphMorphism parentTypeMap =
                parent == null ? null : parent.typeMap;
            this.typeMap = new RuleGraphMorphism();
            this.isRule = origin.isRule;
            this.lhs = toTypedGraph(origin.lhs, parentTypeMap, this.typeMap);
            this.rhs = toTypedGraph(origin.rhs, parentTypeMap, this.typeMap);
            if (isTyped()) {
                try {
                    checkTypeSpecialisation(this.lhs, this.rhs);
                } catch (FormatException exc) {
                    this.errors.addAll(transferErrors(exc.getErrors(),
                        this.typeMap));
                }
            }
            for (RuleGraph nac : origin.nacs) {
                this.nacs.add(toTypedGraph(nac, this.typeMap, null));
            }
            if (!this.errors.isEmpty()) {
                throw new FormatException(this.errors);
            }
            for (Map.Entry<RuleNode,Color> colorEntry : origin.colorMap.entrySet()) {
                this.colorMap.put(globalTypeMap.getNode(colorEntry.getKey()),
                    colorEntry.getValue());
            }
        }

        /** Returns the tree index of this rule. */
        public Index getIndex() {
            return this.index;
        }

        /**
         * Constructs a typed version of a given rule graph.
         * {@link #globalTypeMap} is updated with all new elements.
         * @param graph the untyped input graph
         * @param parentTypeMap typing inherited from the parent level; 
         * may be {@code null} if there is no parent level
         * @param typeMap typing constructed for this level; 
         * may be {@code null} if this is a NAC graph of which the typing
         * should not be recorded
         * @return a typed version of the input graph
         */
        private RuleGraph toTypedGraph(RuleGraph graph,
                RuleGraphMorphism parentTypeMap, RuleGraphMorphism typeMap) {
            RuleGraph result;
            if (getType() == null) {
                result = graph;
                for (RuleNode node : graph.nodeSet()) {
                    if (typeMap != null) {
                        typeMap.putNode(node, node);
                    }
                    this.globalTypeMap.putNode(node, node);
                }
                for (RuleEdge edge : graph.edgeSet()) {
                    if (typeMap != null) {
                        typeMap.putEdge(edge, edge);
                    }
                    this.globalTypeMap.putEdge(edge, edge);
                }
            } else {
                result = graph.newGraph(graph.getName());
                try {
                    RuleGraphMorphism typing =
                        getType().analyzeRule(graph, parentTypeMap);
                    if (typeMap != null) {
                        typeMap.putAll(typing);
                    }
                    // create the result graph and update the global type map
                    for (Map.Entry<RuleNode,RuleNode> nodeEntry : typing.nodeMap().entrySet()) {
                        RuleNode key = nodeEntry.getKey();
                        RuleNode image = nodeEntry.getValue();
                        assert image != null;
                        RuleNode globalImage = this.globalTypeMap.getNode(key);
                        if (globalImage == null) {
                            this.globalTypeMap.putNode(key, image);
                            result.addNode(image);
                        } else {
                            result.addNode(globalImage);
                            // add a type test if the global image differs
                            if (image != globalImage) {
                                TypeNode imageType = image.getType();
                                assert getType().isSubtype(imageType,
                                    globalImage.getType());
                                result.addEdge(image,
                                    new RuleLabel(imageType.getLabel()), image);
                            }
                        }
                    }
                    for (Map.Entry<RuleEdge,RuleEdge> edgeEntry : typing.edgeMap().entrySet()) {
                        RuleEdge key = edgeEntry.getKey();
                        RuleEdge image = edgeEntry.getValue();
                        assert image != null;
                        RuleEdge globalImage = this.globalTypeMap.getEdge(key);
                        if (globalImage == null) {
                            this.globalTypeMap.putEdge(key, globalImage = image);
                        }
                        result.addEdge(globalImage);
                    }
                } catch (FormatException e) {
                    this.errors.addAll(e.getErrors());
                }
            }
            return result;
        }

        /**
         * If the RHS type for a reader node is changed w.r.t. the LHS type,
         * the LHS type has to be sharp and the RHS type a subtype of it.
         * @throws FormatException if there are typing errors
         */
        private void checkTypeSpecialisation(RuleGraph lhs, RuleGraph rhs)
            throws FormatException {
            Collection<FormatError> errors = createErrors();
            for (RuleNode node : rhs.nodeSet()) {
                TypeNode nodeType = node.getType();
                if (nodeType != null && nodeType.isAbstract()
                    && !lhs.containsNode(node)) {
                    errors.add(new FormatError(
                        "Creation of abstract %s-edge not allowed",
                        nodeType.getLabel().text(), node));
                }
            }
            // check for ambiguous mergers
            for (RuleEdge edge : this.rhs.edgeSet()) {
                if (isMerger(edge)) {
                    RuleNode source = edge.source();
                    TypeNode sourceType = source.getType();
                    RuleNode target = edge.target();
                    TypeNode targetType = target.getType();
                    if (sourceType.equals(targetType)) {
                        if (!source.isSharp() && !target.isSharp()) {
                            errors.add(new FormatError(
                                "One of merged %s-nodes must be sharply typed",
                                targetType.getLabel().text(), target));
                        }
                    } else if (getType().isSubtype(sourceType, targetType)) {
                        if (!target.isSharp()) {
                            errors.add(new FormatError(
                                "Merged type %s must be sharp",
                                targetType.getLabel().text(), target));
                        }
                    } else if (getType().isSubtype(targetType, sourceType)) {
                        if (!source.isSharp()) {
                            errors.add(new FormatError(
                                "Merged type %s must be sharp",
                                sourceType.getLabel().text(), source));
                        }
                    } else {
                        errors.add(new FormatError(
                            "One of merged types %s and %s must be subtype of the other",
                            sourceType.getLabel().text(),
                            targetType.getLabel().text(), source, target));
                    }
                } else {
                    TypeEdge edgeType = edge.getType();
                    if (edgeType != null && edgeType.isAbstract()
                        && !lhs.containsEdge(edge)) {
                        errors.add(new FormatError(
                            "Creation of abstract %s-edge not allowed",
                            edgeType.label().text(), edge));
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
        }

        /** Tests if a given RHS edge is a merger. */
        private boolean isMerger(RuleEdge rhsEdge) {
            return !this.lhs.containsEdge(rhsEdge) && rhsEdge.label().isEmpty();
        }

        /** Index of this level. */
        private final Index index;
        /** The rule node registering the match count. */
        private final VariableNode matchCountImage;
        /** The global, rule-wide mapping from untyped to typed rule elements. */
        private final RuleGraphMorphism globalTypeMap;
        /** Combined type map for this level. */
        private final RuleGraphMorphism typeMap;
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap =
            new HashMap<RuleNode,Color>();
        /** Flag indicating that modifiers have been found at this level. */
        private final boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** List of NAC graphs. */
        private final List<RuleGraph> nacs = new ArrayList<RuleGraph>();
        /** List of typing errors. */
        private final Collection<FormatError> errors = createErrors();
    }

    /**
     * Class containing all rule elements on a given rule level,
     * differentiated by role (LHS, RHS and NACs).
     */
    private class Level4 {
        /**
         * Creates a new level, with a given index and parent level.
         * @param origin the level 3 object from which this level 4 object is created
         * @param parent the parent level; may be {@code null} if this is the
         *        top level.
         */
        public Level4(Level2 origin, Level4 parent) {
            this.isRule = origin.isRule;
            this.index = origin.index;
            this.parent = parent;
            // initialise the rule data structures
            this.lhs = origin.lhs;
            this.nacs = origin.nacs;
            this.rhs = origin.rhs;
            this.matchCountImage = origin.matchCountImage;
        }

        /**
         * Creates a new level, with a given index and parent level.
         * @param origin the level 3 object from which this level 4 object is created
         * @param parent the parent level; may be {@code null} if this is the
         *        top level.
         */
        public Level4(Level3 origin, Level4 parent) {
            this.isRule = origin.isRule;
            this.index = origin.index;
            this.parent = parent;
            // initialise the rule data structures
            this.lhs = origin.lhs;
            this.nacs = origin.nacs;
            this.rhs = origin.rhs;
            this.matchCountImage = origin.matchCountImage;
        }

        /**
         * Callback method to compute the rule on this nesting level.
         * The resulting condition is not fixed (see {@link Condition#isFixed()}).
         */
        public Condition computeFlatRule() throws FormatException {
            Condition result;
            Collection<FormatError> errors = createErrors();
            // the resulting rule
            result = createCondition(getRootGraph(), this.lhs);
            if (this.isRule) {
                Rule rule = createRule(result, this.rhs, getCoRootGraph());
                rule.addColorMap(this.colorMap);
                result.setRule(rule);
            }
            // add the NACs to the rule
            for (RuleGraph nac : this.nacs) {
                try {
                    result.addSubCondition(computeNac(this.lhs, nac));
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            if (errors.isEmpty()) {
                return result;
            } else {
                throw new FormatException(errors);
            }
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        private RuleGraph getRootGraph() {
            return this.index.isTopLevel() ? null : getIntersection(
                this.parent.lhs, this.lhs);
        }

        /**
         * Returns the intersection of the parent RHS and this RHS
         */
        private RuleGraph getCoRootGraph() {
            // find the first parent that has a rule
            Level4 parent = this.parent;
            while (parent != null && !parent.isRule) {
                parent = parent.parent;
            }
            return parent == null ? null
                    : getIntersection(parent.rhs, this.rhs);
        }

        /**
         * Returns a rule graph that forms the intersection of the rule elements
         * of this and the parent level.
         */
        private RuleGraph getIntersection(RuleGraph parentLhs, RuleGraph myLhs) {
            RuleGraph result =
                createGraph(getName() + "-" + getIndex() + "-root");
            for (RuleNode node : parentLhs.nodeSet()) {
                if (myLhs.containsNode(node)) {
                    result.addNode(node);
                }
            }
            for (RuleEdge edge : parentLhs.edgeSet()) {
                if (myLhs.containsEdge(edge)) {
                    result.addEdge(edge);
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
         * @param nac the NAC graph
         */
        private Condition computeNac(RuleGraph lhs, RuleGraph nac)
            throws FormatException {
            Condition result = null;
            // first check for merge end edge embargoes
            // they are characterised by the fact that there is precisely 1
            // element
            // in the nacElemSet, which is an edge
            if (nac.nodeCount() == 2 && nac.edgeCount() == 1) {
                RuleEdge embargoEdge = nac.edgeSet().iterator().next();
                if (lhs.containsNode(embargoEdge.source())
                    && lhs.containsNode(embargoEdge.target())
                    && VarSupport.getAllVars(embargoEdge).isEmpty()) {
                    // this is supposed to be an edge embargo
                    result = createEdgeEmbargo(lhs, embargoEdge);
                }
            }
            if (result == null) {
                // if we're here it means we couldn't make an embargo
                result = createNAC(nac);
                RuleGraph nacPattern = result.getPattern();
                // if the rule is injective, add all lhs nodes to the NAC pattern
                if (isInjective()) {
                    for (RuleNode node : lhs.nodeSet()) {
                        if (!(node instanceof VariableNode)
                            && !(node instanceof ProductNode)) {
                            nacPattern.addNode(node);
                        }
                    }
                }
                // add variable binders to the NAC pattern
                // first calculate the free variables in the NAC pattern
                Set<LabelVar> freeVars =
                    new HashSet<LabelVar>(VarSupport.getAllVars(nac));
                if (!freeVars.isEmpty()) {
                    // for every free variable, if it is bound in the LHS,
                    // add a binder to the NAC pattern
                    Map<LabelVar,RuleEdge> lhsVarBinders =
                        VarSupport.getVarBinders(lhs);
                    for (LabelVar nacVar : freeVars) {
                        RuleEdge nacVarBinder = lhsVarBinders.get(nacVar);
                        if (nacVarBinder != null) {
                            // add the edge and its end nodes to the NAC, as
                            // pre-matched elements
                            nacPattern.addEdge(nacVarBinder);
                        }
                    }
                }
                // set the nac root graph to the intersection of
                // the nac pattern and the lhs
                RuleGraph nacRoot = result.getRoot();
                for (RuleNode nacNode : nacPattern.nodeSet()) {
                    if (lhs.containsNode(nacNode)) {
                        nacRoot.addNode(nacNode);
                    }
                }
                for (RuleEdge nacEdge : nacPattern.edgeSet()) {
                    if (lhs.containsEdge(nacEdge)) {
                        nacRoot.addEdge(nacEdge);
                    }
                }
            }
            result.setFixed();
            return result;
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
         * @param nac the context-graph
         * @return the new {@link groove.trans.Condition}
         * @see #toRule()
         */
        private Condition createNAC(RuleGraph nac) {
            String name = nac.getName();
            return new Condition(name, Condition.Op.NOT, nac, null,
                getSystemProperties());
        }

        /**
         * Factory method for rules.
         * @param condition name of the new rule to be created
         * @param rhs the right hand side graph
         * @param coRoot map of creator nodes in the parent rule to creator
         *        nodes of this rule
         * @return the fresh rule created by the factory
         */
        private Rule createRule(Condition condition, RuleGraph rhs,
                RuleGraph coRoot) {
            Rule result =
                new Rule(condition, rhs, coRoot, new GraphProperties());
            return result;
        }

        /**
         * Factory method for universal conditions.
         * @param root root graph of the new condition
         * @param pattern target graph of the new condition
         * @return the fresh condition
         */
        private Condition createCondition(RuleGraph root, RuleGraph pattern) {
            Condition result =
                new Condition(this.index.getName(), this.index.getOperator(),
                    pattern, root, getSystemProperties());
            result.setLabelStore(getGrammar().getLabelStore());
            if (this.index.isPositive()) {
                result.setPositive();
            }
            if (this.matchCountImage != null) {
                result.setCountNode(this.matchCountImage);
            }
            return result;
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 4", getName(),
                getIndex());
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        /** Index of this level. */
        private final Index index;
        /** Index of this level. */
        private final Level4 parent;
        /** The rule node registering the match count. */
        private final VariableNode matchCountImage;
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap =
            new HashMap<RuleNode,Color>();
        /** Flag indicating that modifiers have been found at this level. */
        private final boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** List of NAC graphs. */
        private final List<RuleGraph> nacs;
    }

    /** Class that can extract parameter information from the model graph. */
    private class Parameters {
        /** Initialises the internal data structures. */
        public Parameters() throws FormatException {
            Collection<FormatError> errors = createErrors();
            this.hiddenPars = new HashSet<RuleNode>();
            // Mapping from parameter position to parameter
            Map<Integer,CtrlPar.Var> parMap =
                new HashMap<Integer,CtrlPar.Var>();
            int parCount = 0;
            // collect parameter nodes
            for (AspectNode node : getSource().nodeSet()) {
                // check if the node is a parameter
                if (node.hasParam()) {
                    Integer nr = (Integer) node.getParam().getContent();
                    if (nr != null) {
                        parCount = Math.max(parCount, nr + 1);
                        try {
                            processNode(parMap, node, nr);
                        } catch (FormatException exc) {
                            errors.addAll(exc.getErrors());
                        }
                    } else {
                        // this is an unnumbered parameter,
                        // which serves as an explicit anchor node
                        if (node.getParamKind() != PARAM_BI) {
                            throw new FormatException(
                                "Anchor node cannot be input or output", node);
                        }
                        if (!node.getKind().inLHS()) {
                            throw new FormatException(
                                "Anchor node must be in LHS", node);
                        }
                        RuleNode nodeImage =
                            RuleModel.this.modelMap.getNode(node);
                        assert nodeImage != null;
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
            RuleNode nodeImage = RuleModel.this.modelMap.getNode(node);
            assert nodeImage != null;
            boolean creator;
            if (nodeKind.inLHS()) {
                creator = false;
            } else if (nodeKind.inRHS()) {
                if (inOnly) {
                    throw new FormatException(
                        "Creator node cannot be used as input parameter", node);
                }
                outOnly = true;
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

        /** Lazily creates and returns the rule's hidden parameters. */
        public Set<RuleNode> getHiddenPars() {
            return this.hiddenPars;
        }

        /** Returns the rule signature. */
        public List<CtrlPar.Var> getSignature() {
            return this.sig;
        }

        /** Set of all rule parameter nodes */
        private Set<RuleNode> hiddenPars;
        /** Signature of the rule. */
        private List<CtrlPar.Var> sig;
    }

    /** Mapping from aspect graph elements to rule graph elements. */
    public static class RuleModelMap extends ModelMap<RuleNode,RuleEdge> {
        /**
         * Creates a new, empty map.
         */
        public RuleModelMap() {
            super(RuleFactory.instance());
        }

        @Override
        public RuleModelMap newMap() {
            return new RuleModelMap();
        }
    }
}
