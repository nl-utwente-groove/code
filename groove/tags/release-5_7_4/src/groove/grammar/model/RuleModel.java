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
 * $Id$
 */

package groove.grammar.model;

import static groove.grammar.aspect.AspectKind.CONNECT;
import static groove.grammar.aspect.AspectKind.EXISTS;
import static groove.grammar.aspect.AspectKind.FORALL_POS;
import static groove.grammar.aspect.AspectKind.PARAM_ASK;
import static groove.grammar.aspect.AspectKind.PARAM_BI;
import static groove.grammar.aspect.AspectKind.PARAM_IN;
import static groove.grammar.aspect.AspectKind.PRODUCT;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.automaton.RegExpr;
import groove.grammar.Action.Role;
import groove.grammar.CheckPolicy;
import groove.grammar.Condition;
import groove.grammar.Condition.Op;
import groove.grammar.EdgeEmbargo;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.Signature;
import groove.grammar.UnitPar;
import groove.grammar.aspect.Aspect;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectElement;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.MatchChecker;
import groove.grammar.rule.MethodName;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleFactory;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleGraphMorphism;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeComparator;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphProperties.Key;
import groove.graph.NodeComparator;
import groove.gui.dialog.GraphPreviewDialog;
import groove.util.DefaultFixable;
import groove.util.Fixable;
import groove.util.Groove;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

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
 * @version $Revision$
 */
public class RuleModel extends GraphBasedModel<Rule> implements Comparable<RuleModel> {
    /**
     * Constructs a rule model from an aspect graph. The rule properties are
     * explicitly given.
     * @param grammar the (non-{@code null}) grammar to which the rule belongs
     * @param graph the graph to be converted (non-null)
     */
    public RuleModel(GrammarModel grammar, AspectGraph graph) {
        super(grammar, graph);
        assert grammar != null;
        graph.testFixed(true);
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        // check for the properties to update the match constraints
        result |= isStale(ResourceKind.PROPERTIES);
        // check for the type graph to get the correct instance
        result |= this.oldTypeGraph != getType();
        // check for Groovy scripts to get the correct match filter
        result |= isStale(ResourceKind.GROOVY);
        this.oldTypeGraph = getType();
        return result;
    }

    @Override
    public boolean isEnabled() {
        return GraphInfo.isEnabled(getSource()) || hasRecipes();
    }

    /** Returns the set of recipe names in which this rule is called. */

    public Set<QualName> getRecipes() {
        return getGrammar().getControlModel()
            .getRecipes(getQualName());
    }

    /** Indicates if this rule occurs as subrule in any recipes. */
    public boolean hasRecipes() {
        return !getRecipes().isEmpty();
    }

    /**
     * Indicates if this rule is a property.
     * @see Rule#isProperty()
     */
    public boolean isProperty() {
        return getRole().isProperty();
    }

    /** Returns the action role of this rule. */
    public Role getRole() {
        if (this.role == null) {
            this.role = GraphInfo.getRole(getSource());
            if (this.role == null) {
                this.role = testAsProperty() == null ? Role.CONDITION : Role.TRANSFORMER;
            }
        }
        return this.role;
    }

    private Role role;

    /** Returns the policy for dealing with rule matches, set in the grammar properties. */
    public CheckPolicy getPolicy() {
        CheckPolicy result = getGrammarProperties().getRulePolicy()
            .get(getQualName());
        if (result == null) {
            result = getRole().isConstraint() ? CheckPolicy.ERROR : CheckPolicy.SILENT;
        }
        return result;
    }

    /**
     * Tests if this rule may be used as a property.
     * @return a non-{@code null} string if the rule is unsuitable
     * @see Rule#isProperty()
     */
    private FormatError testAsProperty() {
        if (getPriority() > 0) {
            return new FormatError("positive priority not allowed");
        }
        for (AspectNode node : getSource().nodeSet()) {
            if (!node.hasAspect()) {
                continue;
            }
            if (node.getParam() != null) {
                // don't use #getRole() here as it causes infinite recursion
                Role role = GraphInfo.getRole(getSource());
                if (role != null && role.isConstraint()) {
                    return new FormatError("parameter not allowed", node);
                } else if (node.getParamKind() == PARAM_IN) {
                    return new FormatError("input parameter not allowed", node);
                } else if (node.getParamNr() < 0) {
                    return new FormatError("anonymous parameter not allowed", node);
                }
            }
            if (node.hasColor()) {
                return new FormatError("colour change not allowed", node);
            }
            switch (node.getKind()) {
            case ERASER:
                return new FormatError("erased not allowed", node);
            case CREATOR:
            case ADDER:
                return new FormatError("creator not allowed", node);
            default:
                // no constraint
            }
        }
        for (AspectEdge edge : getSource().edgeSet()) {
            if (!edge.hasAspect()) {
                continue;
            }
            if (edge.isAssign()) {
                return new FormatError("assignment not allowed", edge.source());
            }
            switch (edge.getKind()) {
            case ERASER:
                return new FormatError("erased not allowed", edge);
            case CREATOR:
            case ADDER:
                return new FormatError("creator not allowed", edge);
            default:
                // no constraint
            }
        }
        return null;
    }

    /**
     * Returns the priority of the rule of which this is a model. Yields the same
     * result as <code>toRule().getPriority()</code>.
     */
    public int getPriority() {
        return GraphInfo.getPriority(getSource());
    }

    /** Convenience method */
    public String getTransitionLabel() {
        return GraphInfo.getTransitionLabel(getSource());
    }

    /** Convenience method */
    public String getFormatString() {
        return GraphInfo.getFormatString(getSource());
    }

    /** Returns the signature of this rule.
     * @throws FormatException if the model contains errors that prevent the signature
     * from being computed
     */
    public Signature<UnitPar.RulePar> getSignature() throws FormatException {
        getErrors().throwException();
        return new Parameters().getSignature();
    }

    @Override
    Rule compute() throws FormatException {
        this.ruleFactory = RuleFactory.newInstance(getType().getFactory());
        this.modelMap = new RuleModelMap(this.ruleFactory);
        GraphInfo.throwException(getSource());
        AspectGraph normalSource = getNormalSource();
        GraphInfo.throwException(normalSource);
        this.levelTree = new LevelTree(normalSource);
        this.modelMap.putAll(this.levelTree.getModelMap());
        this.typeMap = new TypeModelMap(getType().getFactory());
        for (Map.Entry<AspectNode,RuleNode> nodeEntry : this.modelMap.nodeMap()
            .entrySet()) {
            this.typeMap.putNode(nodeEntry.getKey(), nodeEntry.getValue()
                .getType());
        }
        for (Map.Entry<AspectEdge,RuleEdge> edgeEntry : this.modelMap.edgeMap()
            .entrySet()) {
            this.typeMap.putEdge(edgeEntry.getKey(), edgeEntry.getValue()
                .getType());
        }
        Rule result = computeRule(this.levelTree);
        return result;
    }

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.labelSet = null;
        this.levelTree = null;
        this.typeMap = null;
    }

    /** Returns the set of labels occurring in this rule. */
    @Override
    public @NonNull Set<TypeLabel> getLabels() {
        Set<TypeLabel> result = this.labelSet;
        if (result == null) {
            Set<TypeLabel> labelSet = new HashSet<>();
            getNormalSource().edgeSet()
                .stream()
                .map(e -> e.getRuleLabel())
                .filter(l -> l != null)
                .map(l -> l.getMatchExpr())
                .forEach(e -> labelSet.addAll(e.getTypeLabels()));
            result = this.labelSet = labelSet;
        }
        return result;
    }

    @Override
    public RuleModelMap getMap() {
        if (hasErrors()) {
            throw new IllegalStateException(
                "Can't compute map while rule has errors: " + getErrors().toString());
        }
        return this.modelMap;
    }

    @Override
    public TypeModelMap getTypeMap() {
        synchronise();
        return this.typeMap;
    }

    /** Returns a mapping from rule nesting levels to sets of aspect elements on that level. */
    public TreeMap<Index,Set<AspectElement>> getLevelTree() {
        synchronise();
        if (this.levelTree == null) {
            return null;
        }
        TreeMap<Index,Set<AspectElement>> result = new TreeMap<>();
        for (Map.Entry<Index,Level1> levelEntry : this.levelTree.getLevel1Map()
            .entrySet()) {
            Index index = levelEntry.getKey();
            Level1 level = levelEntry.getValue();
            Set<AspectElement> elements = new HashSet<>();
            result.put(index, elements);
            elements.addAll(level.modelNodes);
            elements.addAll(level.modelEdges);
        }
        return result;
    }

    @Override
    public int compareTo(RuleModel o) {
        int result = getPriority() - o.getPriority();
        if (result == 0) {
            result = getQualName().compareTo(o.getQualName());
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Rule model on '%s'", getQualName());
    }

    /** Returns the (implicit or explicit) type graph of this grammar. */
    final TypeGraph getType() {
        return getGrammar().getTypeGraph();
    }

    /**
     * @return Returns the properties.
     */
    final GrammarProperties getGrammarProperties() {
        return getGrammar().getProperties();
    }

    /**
     * Indicates if the rule is to be matched injectively. If so, all context
     * nodes should be part of the root map, otherwise injectivity cannot be
     * checked.
     * @return <code>true</code> if the rule is to be matched injectively.
     */
    final public boolean isInjective() {
        return GraphInfo.isInjective(getSource()) || getGrammarProperties().isInjective();
    }

    final boolean isRhsAsNac() {
        return getGrammarProperties().isRhsAsNac();
    }

    final boolean isCheckCreatorEdges() {
        return getGrammarProperties().isCheckCreatorEdges();
    }

    /**
     * Callback method to compute a rule from the source graph. All auxiliary data
     * structures are assumed to be initialised but empty. After method return,
     * the structures are filled.
     * @throws FormatException if the model cannot be converted to a valid rule
     */
    private Rule computeRule(LevelTree levelTree) throws FormatException {
        Rule result;
        FormatErrorSet errors = createErrors();
        // store the derived subrules in order
        TreeMap<Index,Condition> conditionTree = new TreeMap<>();
        // construct the rule tree and add parent rules
        try {
            for (Level4 level : levelTree.getLevel4Map()
                .values()) {
                Index index = level.getIndex();
                Op operator = index.getOperator();
                Condition condition;
                if (operator.isQuantifier()) {
                    condition = level.computeFlatRule();
                } else {
                    condition = new Condition(index.getName(), operator);
                }
                conditionTree.put(index, condition);
                if (condition.hasRule() && !index.isTopLevel()) {
                    // look for the first parent rule
                    Index parentIndex = index.getParent();
                    while (!conditionTree.get(parentIndex)
                        .hasRule()) {
                        parentIndex = parentIndex.getParent();
                    }
                    condition.getRule()
                        .setParent(conditionTree.get(parentIndex)
                            .getRule(), index.getIntArray());
                }
            }
            // now add subconditions and fix the conditions
            // this needs to be done bottom-up
            for (Map.Entry<Index,Condition> entry : conditionTree.descendingMap()
                .entrySet()) {
                Condition condition = entry.getValue();
                assert condition != null;
                Index index = entry.getKey();
                if (!index.isTopLevel()) {
                    condition.setFixed();
                    Condition parentCond = conditionTree.get(index.getParent());
                    parentCond.addSubCondition(condition);
                }
            }
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        // infer and set the role
        Role role = getRole();
        if (role.isProperty()) {
            FormatError error = testAsProperty();
            if (error != null) {
                errors.add("Rule is unsuitable as %s: %s", role, error);
            }
        }
        // due to errors in the above, it might be that the
        // rule tree is empty, in which case we shouldn't proceed
        if (conditionTree.isEmpty()) {
            result = null;
        } else {
            result = conditionTree.firstEntry()
                .getValue()
                .getRule();
        }
        if (result != null) {
            GraphProperties properties = GraphInfo.getProperties(getSource());
            result.setProperties(properties);
            result.setCheckDangling(getGrammarProperties().isCheckDangling());
            Parameters parameters = new Parameters();
            result.setSignature(parameters.getSignature(), parameters.getHiddenPars());
            result.setRole(role);
            try {
                MethodName matchFilter = (MethodName) properties.parseProperty(Key.FILTER);
                if (matchFilter != null) {
                    result.setMatchFilter(MatchChecker.createChecker(matchFilter, getGrammar()));
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        transferErrors(errors, levelTree.getModelMap()).throwException();
        // only fix if the rule is not null and there were no errors in the subconditions
        if (result != null) {
            result.setFixed();
        }
        return result;
    }

    private AspectGraph getNormalSource() {
        if (this.normalSource == null) {
            this.normalSource = getSource().normalise(null);
            if (NORMALISE_DEBUG) {
                // defer in order to avoid circularities in the derivation of the type graph
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GraphPreviewDialog.showGraph(RuleModel.this.normalSource);
                    }
                });
            }
        }
        return this.normalSource;
    }

    private TypeGraph oldTypeGraph;
    /** The factory for rule elements according to the given type graph. */
    private RuleFactory ruleFactory;
    /**
     * Mapping from the elements of the aspect graph representation to the
     * corresponding elements of the rule.
     */
    private RuleModelMap modelMap;
    /** Map from source model to types. */
    private TypeModelMap typeMap;
    /** The normalised source model. */
    private AspectGraph normalSource;
    /** Set of all labels occurring in the rule. */
    private Set<TypeLabel> labelSet;
    /** Mapping from level indices to conditions on those levels. */
    private LevelTree levelTree;
    /** Debug flag for the attribute syntax normalisation. */
    static private final boolean NORMALISE_DEBUG = false;

    /**
     * Class encoding an index in a tree, consisting of a list of indices at
     * every level of the tree.
     */
    static public class Index extends DefaultFixable implements Comparable<Index> {
        /**
         * Constructs a new level, without setting parent or children.
         * @param levelNode the model level node representing this level; may be
         *        <code>null</code> for an implicit or top level
         * @param namePrefix name prefix in case there is no level node to determine the name
         */
        public Index(Condition.Op operator, boolean positive, AspectNode levelNode,
            QualName namePrefix) {
            assert levelNode == null || levelNode.getKind()
                .isQuantifier();
            this.namePrefix = namePrefix;
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
            this.index = new ArrayList<>(parent.index.size() + 1);
            this.index.addAll(parent.index);
            this.index.add(nr);
            setFixed();
        }

        @Override
        public boolean setFixed() {
            boolean result = super.setFixed();
            if (result && this.index == null) {
                this.index = Collections.emptyList();
            }
            return result;
        }

        /** Returns the parent level of this tree index.
         * @return the parent index, or {@code null} if this is the top level
         */
        public Index getParent() {
            testFixed(true);
            return this.parent;
        }

        /**
         * Returns the (optional) aspect node with which this level is
         * associated.
         */
        public AspectNode getLevelNode() {
            return this.levelNode;
        }

        /**
         * Returns the (non-{@code null}) name of this level. The name is either taken from the
         * representative level node, or constructed by concatenating the rule
         * name and the level indices.
         * @return the name of this level: a non-{@code null} value
         * guaranteed to distinguish all index levels.
         */
        public String getName() {
            String suffix;
            AspectNode levelNode = this.levelNode;
            if (levelNode == null || levelNode.getLevelName() == null) {
                suffix = isTopLevel() ? "" : Groove.toString(this.index.toArray());
            } else {
                suffix = "-" + levelNode.getLevelName();
            }
            return this.namePrefix + suffix;
        }

        /** Lexicographically compares the tree indices.
         * @see #getIntArray() */
        @Override
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
                result = this.index.get(i)
                    .equals(other.index.get(i));
            }
            return result;
        }

        /**
         * Converts this level to an array of {@code int}s. May only be called
         * after {@link Index#setParent(Index,int)}.
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
         * {@link Index#setParent(Index,int)}.
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

        /**
         * Indicates if this or any parent level is universally quantified.
         * This implies that nodes on this level may be matched multiple times.
         */
        public boolean isUniversal() {
            testFixed(true);
            boolean result = this.operator == Op.FORALL;
            if (!result && !isTopLevel()) {
                result = getParent().isUniversal();
            }
            return result;
        }

        @Override
        public String toString() {
            return this.index.toString();
        }

        /** The name prefix of the index (to be followed by the index list). */
        private final QualName namePrefix;
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
            this.level1Map = buildLevels1(indexSet);
            RuleModelMap untypedModelMap = new RuleModelMap();
            SortedMap<Index,Level2> level2Map;
            try {
                level2Map = buildLevels2(this.level1Map, untypedModelMap);
            } catch (FormatException e) {
                throw new FormatException(transferErrors(e.getErrors(), untypedModelMap));
            }
            RuleFactory typedFactory = RuleModel.this.ruleFactory;
            RuleGraphMorphism typingMap = new RuleGraphMorphism(typedFactory);
            try {
                SortedMap<Index,Level3> level3Map = buildLevels3(level2Map, typingMap);
                this.level4Map = build4From3(level3Map);
            } catch (FormatException e) {
                throw new FormatException(transferErrors(e.getErrors(), untypedModelMap));
            }
            RuleModelMap modelMap = new RuleModelMap(typedFactory);
            for (Map.Entry<AspectNode,RuleNode> nodeEntry : untypedModelMap.nodeMap()
                .entrySet()) {
                RuleNode image = typingMap.getNode(nodeEntry.getValue());
                if (image != null) {
                    modelMap.putNode(nodeEntry.getKey(), image);
                }
            }
            for (Map.Entry<AspectEdge,RuleEdge> edgeEntry : untypedModelMap.edgeMap()
                .entrySet()) {
                RuleEdge image = typingMap.getEdge(edgeEntry.getValue());
                if (image != null) {
                    modelMap.putEdge(edgeEntry.getKey(), image);
                }
            }
            this.modelMap = modelMap;
        }

        /** Builds the level data maps. */
        private SortedSet<Index> buildTree() {
            // First build an explicit tree of level nodes
            Map<Index,List<Index>> indexTree = new HashMap<>();
            this.topLevelIndex = createIndex(Op.EXISTS, false, null, indexTree);
            // initialise the data structures
            this.metaIndexMap = new HashMap<>();
            this.nameIndexMap = new HashMap<>();
            // Mapping from levels to match count nodes
            this.matchCountMap = new HashMap<>();
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
                        parentIndex = getIndex(parentKind, parentNode, indexTree);
                    }
                    Index myIndex = getIndex(nodeKind, node, indexTree);
                    indexTree.get(parentIndex)
                        .add(myIndex);
                    if (node.getMatchCount() != null) {
                        this.matchCountMap.put(myIndex, node.getMatchCount());
                    }
                }
            }
            // insert the children into the indices themselves and build the index set
            SortedSet<Index> indexSet = new TreeSet<>();
            Queue<Index> indexQueue = new LinkedList<>();
            indexQueue.add(this.topLevelIndex);
            while (!indexQueue.isEmpty()) {
                Index next = indexQueue.poll();
                next.setFixed();
                List<Index> children = indexTree.get(next);
                // add an implicit existential sub-level to childless universal
                // levels
                if (next.getOperator() == Op.FORALL && children.isEmpty()) {
                    Index implicitChild = createIndex(Op.EXISTS, true, null, indexTree);
                    children.add(implicitChild);
                }
                // set the parent of all children
                for (int i = 0; i < children.size(); i++) {
                    children.get(i)
                        .setParent(next, i);
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
                this.metaIndexMap.put(metaNode,
                    result = createIndex(operator, positive, metaNode, indexTree));
                Aspect id = metaNode.getId();
                if (id != null) {
                    String name = id.getContentString();
                    Index oldIndex = this.nameIndexMap.put(name, result);
                    assert oldIndex == null : String.format("Duplicate quantifier name %s", name);
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
        private Index createIndex(Condition.Op operator, boolean positive, AspectNode levelNode,
            Map<Index,List<Index>> levelTree) {
            Index result = new Index(operator, positive, levelNode, getQualName());
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
            FormatErrorSet errors = createErrors();
            // Set the parentage in tree preorder
            // Build the level data map,
            // in the tree-order of the indices
            SortedMap<Index,Level1> result = new TreeMap<>();
            for (Index index : indexSet) {
                Level1 parentLevel = index.isTopLevel() ? null : result.get(index.getParent());
                Level1 level = new Level1(index, parentLevel);
                result.put(index, level);
            }
            // initialise the match count nodes and check that they are defined at super-levels
            for (Map.Entry<Index,AspectNode> matchCountEntry : this.matchCountMap.entrySet()) {
                AspectNode matchCount = matchCountEntry.getValue();
                Index definedAt = getLevel(result, matchCount).getIndex();
                Index usedAt = matchCountEntry.getKey();
                if (!definedAt.higherThan(usedAt) || definedAt.equals(usedAt)) {
                    throw new FormatException("Match count not defined at appropriate level",
                        matchCount);
                }
                Level1 level = result.get(usedAt);
                // add the match count node to all intermediate levels
                // (between definition and usage)
                Index addTo = usedAt.getParent();
                while (addTo != null && !addTo.equals(definedAt)) {
                    result.get(addTo)
                        .addNode(matchCount);
                    addTo = addTo.getParent();
                }
                level.setMatchCount(matchCount);
            }
            // add nodes to nesting data structures
            for (AspectNode node : this.source.nodeSet()) {
                if (!node.getKind()
                    .isMeta()) {
                    getLevel(result, node).addNode(node);
                }
            }
            // add edges to nesting data structures
            for (AspectEdge edge : this.source.edgeSet()) {
                try {
                    if (edge.getKind() == CONNECT || !edge.getKind()
                        .isMeta()) {
                        getLevel(result, edge).addEdge(edge);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            Map<LabelVar,Set<AspectEdge>> modelVarMap = new HashMap<>();
            for (Level1 level : result.values()) {
                modelVarMap.putAll(level.modelVars);
            }
            Map<String,LabelVar> nameVarMap = new HashMap<>();
            for (Map.Entry<LabelVar,Set<AspectEdge>> varEntry : modelVarMap.entrySet()) {
                LabelVar var = varEntry.getKey();
                LabelVar oldVar = nameVarMap.put(var.getName(), var);
                if (oldVar != null && !oldVar.equals(var)) {
                    errors.add("Duplicate variable '%s' for %s and %s labels",
                        var,
                        var.getKind()
                            .getDescription(false),
                        oldVar.getKind()
                            .getDescription(false),
                        varEntry.getValue()
                            .toArray(),
                        modelVarMap.get(oldVar)
                            .toArray());
                }
            }
            for (Level1 level : result.values()) {
                level.setFixed();
            }
            errors.throwException();
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
                    nestingNode == null ? this.topLevelIndex : this.metaIndexMap.get(nestingNode);
                assert index != null : String.format("No valid nesting level found for %s", node);
                result = level1Map.get(index);
                assert result != null : String
                    .format("Level map %s does not contain entry for index %s", level1Map, index);
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
            if (edge.source()
                .getKind()
                .inNAC() && !sourceLevel.equals(result)
                || edge.target()
                    .getKind()
                    .inNAC() && !targetLevel.equals(result)) {
                result = null;
            }
            if (result == null) {
                throw new FormatException("Source and target of edge %s have incompatible nesting",
                    edge);
            }
            String levelName = edge.getLevelName();
            if (levelName != null) {
                Index edgeLevelIndex = this.nameIndexMap.get(levelName);
                if (edgeLevelIndex == null) {
                    throw new FormatException("Undefined nesting level '%s' in edge %s", levelName,
                        edge);
                }
                result = max(result, level1Map.get(edgeLevelIndex));
                if (result == null) {
                    throw new FormatException(
                        "Nesting level %s in edge %s is incompatible with end nodes", levelName,
                        edge);
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
                this.nodeLevelMap = new HashMap<>();
            }
            return this.nodeLevelMap;
        }

        /** Constructs the level2 map. */
        private SortedMap<Index,Level2> buildLevels2(SortedMap<Index,Level1> level1Map,
            RuleModelMap modelMap) throws FormatException {
            SortedMap<Index,Level2> result = new TreeMap<>();
            FormatErrorSet errors = createErrors();
            for (Level1 level1 : level1Map.values()) {
                try {
                    Index index = level1.getIndex();
                    Level2 parent = index.isTopLevel() ? null : result.get(index.parent);
                    Level2 level2 = new Level2(level1, parent, modelMap);
                    result.put(index, level2);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            errors.throwException();
            return result;
        }

        /** Constructs the level3 map. */
        private SortedMap<Index,Level3> buildLevels3(SortedMap<Index,Level2> level2Map,
            RuleGraphMorphism typingMap) throws FormatException {
            SortedMap<Index,Level3> result = new TreeMap<>();
            FormatErrorSet errors = createErrors();
            for (Level2 level2 : level2Map.values()) {
                Index index = level2.getIndex();
                Level3 parent = index.isTopLevel() ? null : result.get(index.getParent());
                Level3 level3 = new Level3(level2, parent, typingMap);
                result.put(index, level3);
            }
            errors.throwException();
            return result;
        }

        /** Constructs the level4 map. */
        private SortedMap<Index,Level4> build4From3(SortedMap<Index,Level3> level3Map) {
            SortedMap<Index,Level4> result = new TreeMap<>();
            for (Level3 level3 : level3Map.values()) {
                Index index = level3.getIndex();
                Level4 parent = index.isTopLevel() ? null : result.get(index.getParent());
                Level4 level4 = new Level4(level3, parent);
                result.put(index, level4);
            }
            return result;
        }

        /**
         * Returns the quantification levels in ascending or descending order
         */
        public final Map<Index,Level1> getLevel1Map() {
            return this.level1Map;
        }

        /**
         * Returns the quantification levels in ascending or descending order
         */
        public final Map<Index,Level4> getLevel4Map() {
            return this.level4Map;
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
        /** Mapping from level indices to stage 1 levels. */
        private SortedMap<Index,Level1> level1Map;
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
                assert index.getParent()
                    .equals(parent.getIndex()) : String
                        .format("Parent index %s should be parent of %s", parent.index, index);
                parent.addChild(this);
            } else {
                assert index.isTopLevel() : String
                    .format("Level with index %s should have non-null parent", index);
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
                if (!isSetOperator(modelEdge)) {
                    addNodeToParents(modelEdge.target());
                }
                // add variables
                addToVars(modelEdge);
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(modelEdge)) {
                for (Level1 sublevel : this.children) {
                    sublevel.addEdge(modelEdge);
                }
            }
        }

        /** Adds the variables of a given aspect edge to the variable map. */
        private void addToVars(AspectEdge modelEdge) {
            RuleLabel ruleLabel = modelEdge.getRuleLabel();
            if (ruleLabel != null) {
                for (LabelVar var : ruleLabel.allVarSet()) {
                    Set<AspectEdge> binders = this.modelVars.get(var);
                    if (binders == null) {
                        this.modelVars.put(var, binders = new HashSet<>());
                    }
                    binders.add(modelEdge);
                }
            }
        }

        /** Initialises the match count for this (universal) level. */
        public void setMatchCount(AspectNode matchCount) {
            this.countNode = matchCount;
        }

        /**
         * Adds a node to this and all parent levels, if it is not yet there
         */
        private void addNodeToParents(AspectNode modelNode) {
            Level1 ascendingLevel = this;
            while (ascendingLevel.modelNodes.add(modelNode)) {
                assert !ascendingLevel.index.isTopLevel() : String
                    .format("Node not found at any level");
                ascendingLevel = ascendingLevel.parent;
                assert ascendingLevel.modelNodes != null : String
                    .format("Nodes on level %s not yet initialised", ascendingLevel.getIndex());
            }
        }

        private boolean isSetOperator(AspectEdge edge) {
            Operator op = edge.getOperator();
            return op != null && op.isSetOperator();
        }

        /**
         * Indicates if a given element should be included on the level on which
         * it it is defined in the model. Node creators should not appear on
         * universal levels since those get translated to conditions, not rules;
         * instead they are pushed to the next (existential) sublevels.
         * @param elem the element about which the question is asked
         */
        private boolean isForThisLevel(AspectElement elem) {
            return this.index.getOperator()
                .hasPattern();
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the model. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for edges that bind variables.
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(AspectElement elem) {
            assert elem.getKind() == CONNECT || !elem.getKind()
                .isMeta();
            boolean result = false;
            if (!this.index.getOperator()
                .hasPattern()) {
                result = true;
            } else if (elem instanceof AspectNode) {
                // we need to push non-attribute nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result = isInjective() && elem.getKind()
                    .inLHS() && elem.getAttrAspect() == null;
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                // as well as  all node type labels
                // to enable correct typing at sublevels
                //                RuleLabel varLabel = ((AspectEdge) elem).getRuleLabel();
                //                if (varLabel != null) {
                //                    result = getType().isNodeType(varLabel);
                //                }
            }
            return result;
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 1", getQualName(), getIndex());
        }

        @Override
        public int compareTo(Level1 o) {
            return getIndex().compareTo(o.getIndex());
        }

        /**
         * Does some post-processing after all elements have been added
         * to this and the parent levels.
         */
        public void setFixed() {
            if (this.parent != null) {
                for (LabelVar var : this.modelVars.keySet()) {
                    this.parent.testParentBinding(var);
                }
            }
        }

        /** Tests if a given variable is already bound at this or a parent
         * level and, if so, adds it to the {@link #modelVars} at the intermediate
         * levels.
         */
        private boolean testParentBinding(LabelVar var) {
            boolean result = this.modelVars.containsKey(var);
            if (!result && this.parent != null) {
                result = this.parent.testParentBinding(var);
                if (result) {
                    this.modelVars.put(var, new HashSet<AspectEdge>());
                }
            }
            return result;
        }

        /** Index of this level. */
        final Index index;
        /** Parent level; {@code null} if this is the top level. */
        private final Level1 parent;
        /** Children level data. */
        private final List<Level1> children = new ArrayList<>();
        /** Set of model nodes on this level. */
        final Set<AspectNode> modelNodes = new HashSet<>();
        /** Set of model edges on this level. */
        final Set<AspectEdge> modelEdges = new HashSet<>();
        /** Set of label variables used on this level. */
        final Map<LabelVar,Set<AspectEdge>> modelVars = new HashMap<>();
        /** The model node registering the match count. */
        AspectNode countNode;
    }

    /**
     * Class containing all rule elements on a given rule level,
     * differentiated by role (LHS, RHS and NACs).
     */
    private class Level2 {
        /**
         * Creates a new level, with a given index and parent level.
         * @param origin the level 1 object from which this level 2 object is created
         * @param parent the parent's level 2 object, if this is not a top level
         */
        public Level2(Level1 origin, Level2 parent, RuleModelMap modelMap) throws FormatException {
            this.factory = modelMap.getFactory();
            Index index = this.index = origin.index;
            this.parent = parent;
            this.modelMap = modelMap;
            this.isRule = index.isTopLevel();
            // initialise the rule data structures
            this.lhs = createGraph(getQualName() + "-" + index + "-lhs");
            this.mid = createGraph(getQualName() + "-" + index + "-mid");
            this.rhs = createGraph(getQualName() + "-" + index + "-rhs");
            FormatErrorSet errors = createErrors();
            try {
                if (origin.countNode != null) {
                    this.countNode = (VariableNode) getNodeImage(origin.countNode);
                    this.outputNodes.add(this.countNode);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            for (AspectNode modelNode : origin.modelNodes) {
                try {
                    if (modelNode.getAttrKind() != PRODUCT) {
                        processNode(modelNode);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            // if there are errors in the node map, don't try mapping the edges
            errors.throwException();
            for (AspectEdge modelEdge : origin.modelEdges) {
                try {
                    if (modelEdge.getKind() == CONNECT) {
                        addConnect(modelEdge);
                    } else if (modelEdge.getAttrKind() == AspectKind.DEFAULT) {
                        processEdge(modelEdge);
                    } else if (modelEdge.getAttrKind() != AspectKind.ARGUMENT) {
                        addOperator(modelEdge);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            for (LabelVar modelVar : origin.modelVars.keySet()) {
                processVar(modelVar);
            }
            try {
                this.nacs.addAll(computeNacs());
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            if (!index.isTopLevel()) {
                this.parentVars.addAll(origin.parent.modelVars.keySet());
            }
            checkAttributes(errors);
            checkVariables(errors);
            errors.throwException();
        }

        private void processVar(LabelVar modelVar) {
            this.lhs.addVar(modelVar);
        }

        /**
         * Adds a node to the LHS, RHS or NAC node set, whichever is appropriate.
         */
        private void processNode(AspectNode modelNode) throws FormatException {
            AspectKind nodeKind = modelNode.getKind();
            this.isRule |= nodeKind.inLHS() != nodeKind.inRHS();
            RuleNode ruleNode = getNodeImage(modelNode);
            boolean isAskNode = modelNode.getParamKind() == PARAM_ASK;
            if (nodeKind.inLHS() && !isAskNode) {
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
                    if (isRhsAsNac() && !isAskNode) {
                        this.nacNodeSet.add(ruleNode);
                    }
                }
            }
            if (modelNode.hasColor()) {
                this.colorMap.put(ruleNode, (Color) modelNode.getColor()
                    .getContent());
            }
        }

        /**
         * Adds an edge to the LHS, RHS or NAC edge set, whichever is appropriate.
         */
        private void processEdge(AspectEdge modelEdge) throws FormatException {
            AspectKind edgeKind = modelEdge.getKind();
            this.isRule |= edgeKind.inLHS() != edgeKind.inRHS();
            RuleEdge ruleEdge = getEdgeImage(modelEdge);
            if (ruleEdge == null) {
                // this was an argument or operation edge;
                // it has been processed by adding the info to the operator node
                return;
            }
            if (edgeKind.inLHS()) {
                // flag indicating that the rule edge is fresh in the LHS
                boolean freshInLhs = this.lhs.addEdgeContext(ruleEdge);
                if (freshInLhs) {
                    if (edgeKind.inRHS()) {
                        this.rhs.addEdgeContext(ruleEdge);
                        this.mid.addEdgeContext(ruleEdge);
                    } else if (getType().isNodeType(ruleEdge)
                        && this.rhs.containsNode(ruleEdge.source())) {
                        throw new FormatException("Node type label %s cannot be deleted",
                            ruleEdge.label()
                                .text(),
                            modelEdge.source());
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
                    // creator edge
                    if (getType().isNodeType(ruleEdge)
                        && this.lhs.containsNode(ruleEdge.source())) {
                        throw new FormatException("Node type %s cannot be created",
                            ruleEdge.label(), modelEdge.source());
                    }
                    this.rhs.addEdgeContext(ruleEdge);
                    if (isRhsAsNac()) {
                        this.nacEdgeSet.add(ruleEdge);
                    } else if (isCheckCreatorEdges() && modelEdge.source()
                        .getKind()
                        .inLHS()
                        && modelEdge.target()
                            .getKind()
                            .inLHS()) {
                        this.nacEdgeSet.add(ruleEdge);
                    }
                }
            }
        }

        /** Adds a NAC connection edge. */
        private void addConnect(AspectEdge connectEdge) throws FormatException {
            RuleNode node1 = getNodeImage(connectEdge.source());
            RuleNode node2 = getNodeImage(connectEdge.target());
            Set<RuleNode> nodeSet = new HashSet<>(Arrays.asList(node1, node2));
            this.connectMap.put(connectEdge, nodeSet);
        }

        private void addOperator(AspectEdge operatorEdge) throws FormatException {
            AspectNode productNode = operatorEdge.source();
            boolean embargo = productNode.getKind()
                .inNAC();
            List<VariableNode> arguments = new ArrayList<>();
            for (AspectNode argModelNode : productNode.getArgNodes()) {
                VariableNode argument = (VariableNode) getNodeImage(argModelNode);
                boolean argOnThisLevel = this.lhs.nodeSet()
                    .contains(argument);
                if (!(argOnThisLevel || embargo && this.nacNodeSet.contains(argument))) {
                    throw new FormatException(
                        "Argument '%s' must exist on the level of the product node", argModelNode,
                        productNode);
                }
                arguments.add(argument);
            }
            AspectNode targetModelNode = operatorEdge.target();
            VariableNode target = (VariableNode) getNodeImage(targetModelNode);
            Operator operator = operatorEdge.getOperator();
            boolean setOperator = operator.isSetOperator();
            if (!(setOperator || this.lhs.nodeSet()
                .contains(target) || embargo && this.nacNodeSet.contains(target))) {
                throw new FormatException(
                    "Target of operator '%s' must exist on the level of the operator edge",
                    operator.getName(), operatorEdge);
            }
            // make sure that set operator targets appear on the parent level already
            if (setOperator) {
                if (!(this.parent != null && this.parent.lhs.nodeSet()
                    .contains(target))) {
                    throw new FormatException(
                        "Target of set operator '%s' must be defined on the parent level",
                        operator.getName(), operatorEdge);
                }
                if (!getIndex().isUniversal()) {
                    throw new FormatException(
                        "Argument of set operator '%s' must be universally quantified",
                        operator.getName(), operatorEdge);
                }
                if (!operator.isSupportsZero() && !getIndex().isPositive()) {
                    throw new FormatException(
                        "Argument of set operator '%s' needs a non-vacuous quantification",
                        operator.getName(), operatorEdge);
                }
                // a set operator argument is an output node of the condition
                this.outputNodes.add(arguments.get(0));
            }
            RuleNode opNode = this.factory.createOperatorNode(productNode.getNumber(),
                operator,
                arguments,
                target);
            Level2 level = setOperator ? this.parent : this;
            if (operatorEdge.getKind()
                .inNAC()) {
                level.nacNodeSet.add(opNode);
            } else {
                level.lhs.addNode(opNode);
                level.mid.addNode(opNode);
                level.rhs.addNode(opNode);
            }
        }

        /** Constructs the NACs for this rule. */
        private List<RuleGraph> computeNacs() throws FormatException {
            List<RuleGraph> result = new ArrayList<>();
            // add the nacs to the rule
            // find connected sets of NAC nodes, taking the
            // connection edges into account
            for (Cell cell : getConnectedSets()) {
                // construct the NAC graph
                RuleGraph nac = createGraph(this.lhs.getName() + "-nac-" + result.size());
                for (RuleNode node : cell.getNodes()) {
                    nac.addNode(node);
                    if (node instanceof OperatorNode) {
                        nac.addNodeSet(((OperatorNode) node).getArguments());
                        nac.addNode(((OperatorNode) node).getTarget());
                    }
                }
                for (RuleEdge edge : cell.getEdges()) {
                    nac.addEdgeContext(edge);
                }
                result.add(nac);
            }
            return result;
        }

        /**
         * Partitions a set of graph elements into its maximal connected subsets.
         * The set does not necessarily contain all endpoints of edges it contains.
         * A subset is connected if there is a chain of edges and edge endpoints,
         * all of which are in the set, between all pairs of elements in the set.
         * @return The set of maximal connected subsets of {@link #nacNodeSet} and
         * {@link #nacEdgeSet}
         */
        private SortedSet<Cell> getConnectedSets() throws FormatException {
            // mapping from nodes of elementSet to sets of connected elements
            Map<Element,Cell> result = new HashMap<>();
            for (RuleNode node : this.nacNodeSet) {
                Cell nodeCell = new Cell();
                nodeCell.add(node);
                result.put(node, nodeCell);
            }
            // merge cells connected by an operator
            for (RuleNode node : this.nacNodeSet) {
                if (node instanceof OperatorNode) {
                    OperatorNode opNode = (OperatorNode) node;
                    Cell nodeCell = result.get(opNode);
                    for (RuleNode argNode : opNode.getArguments()) {
                        Cell argCell = result.get(argNode);
                        if (argCell != null) {
                            nodeCell.addAll(argCell);
                        }
                    }
                    VariableNode target = opNode.getTarget();
                    Cell targetCell = result.get(target);
                    if (targetCell != null) {
                        nodeCell.addAll(targetCell);
                    }
                    for (RuleElement elem : nodeCell) {
                        result.put(elem, nodeCell);
                    }
                }
            }
            // merge cells connected by an edge
            for (RuleEdge edge : this.nacEdgeSet) {
                Cell edgeCell = new Cell();
                edgeCell.add(edge);
                Cell sourceCell = result.get(edge.source());
                if (sourceCell != null) {
                    edgeCell.addAll(sourceCell);
                }
                Cell targetCell = result.get(edge.target());
                if (targetCell != null) {
                    edgeCell.addAll(targetCell);
                }
                for (RuleElement elem : edgeCell) {
                    result.put(elem, edgeCell);
                }
            }
            // merge cells connected by an explicit connection
            for (Map.Entry<AspectEdge,Set<RuleNode>> connection : this.connectMap.entrySet()) {
                // find the (separate) cells for the target nodes of the connect edge
                Cell newCell = new Cell();
                for (RuleNode node : connection.getValue()) {
                    Cell nodeCell = result.get(node);
                    if (nodeCell == null) {
                        throw new FormatException("Connect edge should be between distinct NACs",
                            connection.getKey());
                    }
                    newCell.addAll(nodeCell);
                }
                for (RuleElement elem : newCell) {
                    result.put(elem, newCell);
                }
            }
            return new TreeSet<>(result.values());
        }

        private class Cell extends HashSet<RuleElement> implements Comparable<Cell>, Fixable {
            public Cell() {
                // empty
            }

            @Override
            public boolean setFixed() {
                boolean result = !this.fixed;
                this.fixed = true;
                return result;
            }

            @Override
            public boolean isFixed() {
                return this.fixed;
            }

            @Override
            public boolean add(RuleElement e) {
                testFixed(false);
                return super.add(e);
            }

            @Override
            public boolean remove(Object o) {
                testFixed(false);
                return super.remove(o);
            }

            @Override
            public void clear() {
                testFixed(false);
                super.clear();
            }

            /**
             * Returns the set of nodes in this cell. Only call after
             * the cell has been completely fixed.
             */
            public SortedSet<RuleNode> getNodes() {
                setFixed();
                if (this.nodes == null) {
                    this.nodes = computeNodes();
                }
                return this.nodes;
            }

            private SortedSet<RuleNode> computeNodes() {
                TreeSet<RuleNode> result = new TreeSet<>(NodeComparator.instance());
                for (RuleElement elem : this) {
                    if (elem instanceof RuleNode) {
                        result.add((RuleNode) elem);
                    }
                }
                return result;
            }

            /**
             * Returns the set of edges in this cell. Only call after
             * the cell has been completely fixed.
             */
            public SortedSet<RuleEdge> getEdges() {
                setFixed();
                if (this.edges == null) {
                    this.edges = computeEdges();
                }
                return this.edges;
            }

            private SortedSet<RuleEdge> computeEdges() {
                TreeSet<RuleEdge> result = new TreeSet<>(EdgeComparator.instance());
                for (RuleElement elem : this) {
                    if (elem instanceof RuleEdge) {
                        result.add((RuleEdge) elem);
                    }
                }
                return result;
            }

            @Override
            public int compareTo(Cell o) {
                // comparison of node set size
                int result = getNodes().size() - o.getNodes()
                    .size();
                if (result != 0) {
                    return result;
                }
                // comparison of edge set size
                result = getEdges().size() - o.getEdges()
                    .size();
                if (result != 0) {
                    return result;
                }
                // lexicographical comparison of the ordered sets of nodes
                Iterator<RuleNode> myNodeIter = getNodes().iterator();
                Iterator<RuleNode> otherNodeIter = o.getNodes()
                    .iterator();
                Comparator<? super RuleNode> nodeComp = getNodes().comparator();
                while (myNodeIter.hasNext()) {
                    result = nodeComp.compare(myNodeIter.next(), otherNodeIter.next());
                    if (result != 0) {
                        return result;
                    }
                }
                // lexicographical comparison of the ordered sets of edges
                Iterator<RuleEdge> myEdgeIter = getEdges().iterator();
                Iterator<RuleEdge> otherEdgeIter = o.getEdges()
                    .iterator();
                Comparator<? super RuleEdge> edgeComp = getEdges().comparator();
                while (myEdgeIter.hasNext()) {
                    result = edgeComp.compare(myEdgeIter.next(), otherEdgeIter.next());
                    if (result != 0) {
                        return result;
                    }
                }
                return result;
            }

            private boolean fixed = false;
            private SortedSet<RuleNode> nodes;
            private SortedSet<RuleEdge> edges;
        }

        /**
         * Checks if all product nodes have all their arguments.
         */
        private void checkAttributes(FormatErrorSet errors) {
            // check if product nodes have all their arguments (on this level)
            for (RuleNode prodNode : this.lhs.nodeSet()) {
                if (!(prodNode instanceof OperatorNode)) {
                    continue;
                }
                OperatorNode opNode = (OperatorNode) prodNode;
                for (RuleNode argNode : opNode.getArguments()) {
                    if (!this.lhs.nodeSet()
                        .contains(argNode)) {
                        errors.add("Argument must occur on the level of the product node",
                            opNode,
                            argNode);

                    }
                }
                RuleNode opTarget = opNode.getTarget();
                if (!this.lhs.nodeSet()
                    .contains(opTarget)) {
                    errors.add("Operation target must occur on the level of the product node",
                        opNode,
                        opTarget);

                }
            }
        }

        /**
         * Checks if all label variables are bound
         */
        private void checkVariables(FormatErrorSet errors) {
            Map<LabelVar,Set<RuleElement>> allVars = new HashMap<>();
            allVars.putAll(this.lhs.varMap());
            allVars.putAll(this.rhs.varMap());
            for (RuleGraph nac : this.nacs) {
                allVars.putAll(nac.varMap());
            }
            Map<String,LabelVar> varNames = new HashMap<>();
            for (Map.Entry<LabelVar,Set<RuleElement>> varEntry : allVars.entrySet()) {
                LabelVar var = varEntry.getKey();
                LabelVar oldVar = varNames.put(var.getKey(), var);
                if (oldVar != null && !oldVar.equals(var)) {
                    errors.add("Duplicate variable '%s' for %s and %s labels",
                        var,
                        var.getKind()
                            .getDescription(false),
                        oldVar.getKind()
                            .getDescription(false),
                        varEntry.getValue()
                            .toArray());
                }
            }
            allVars.keySet()
                .removeAll(this.lhs.getBoundVars());
            allVars.keySet()
                .removeAll(this.parentVars);
            for (Map.Entry<LabelVar,Set<RuleElement>> varEntry : allVars.entrySet()) {
                LabelVar var = varEntry.getKey();
                errors.add("Unassigned label variable %s", var, varEntry.getValue()
                    .toArray());
            }
        }

        /**
         * Lazily creates and returns a rule image for a given model node.
         * @param modelNode the node for which an image is to be created
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private RuleNode getNodeImage(AspectNode modelNode) throws FormatException {
            RuleNode result = this.modelMap.getNode(modelNode);
            if (result == null) {
                this.modelMap.putNode(modelNode, result = computeNodeImage(modelNode));
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
        private RuleEdge getEdgeImage(AspectEdge modelEdge) throws FormatException {
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
        private RuleNode computeNodeImage(AspectNode node) throws FormatException {
            RuleNode result;
            if (node.hasParam() && !this.index.isTopLevel()) {
                throw new FormatException("Parameter '%d' only allowed on top existential level",
                    node.getNumber(), node);
            }
            AspectKind nodeAttrKind = node.getAttrKind();
            int nr = node.getNumber();
            if (nodeAttrKind.hasSort()) {
                Aspect nodeAttr = node.getAttrAspect();
                Expression term;
                String id = node.hasId() ? node.getId()
                    .getContentString() : null;
                if (nodeAttr.hasContent()) {
                    term = (Constant) nodeAttr.getContent();
                } else {
                    String varName = id == null ? VariableNode.TO_STRING_PREFIX + nr : id;
                    term = new Variable(varName, nodeAttrKind.getSort());
                }
                VariableNode image = this.factory.createVariableNode(nr, term);
                if (id != null) {
                    image.setId(id);
                }
                result = image;
            } else {
                DefaultRuleNode image = (DefaultRuleNode) this.factory.createNode(nr);
                result = image;
            }
            return result;
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
            Map<AspectNode,? extends RuleNode> elementMap) throws FormatException {
            assert edge.getRuleLabel() != null : String.format("Edge '%s' does not belong in model",
                edge);
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
            return this.factory.createEdge(sourceImage, edge.getRuleLabel(), targetImage);
        }

        /**
         * Callback method to create an untyped graph that can serve as LHS or RHS of a rule.
         * @see #getSource()
         */
        private RuleGraph createGraph(String name) {
            return new RuleGraph(name, isInjective(), this.factory);
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 2", getQualName(), getIndex());
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        private final RuleFactory factory;
        /** Mapping from aspect graph elements to rule elements. */
        private final RuleModelMap modelMap;
        /** Index of this level. */
        private final Index index;
        /** Parent level. */
        private final Level2 parent;
        /** Map of all connect edges on this level. */
        private final Map<AspectEdge,Set<RuleNode>> connectMap = new HashMap<>();
        /** The rule node registering the match count. */
        private VariableNode countNode;
        /** Condition output nodes. */
        private final Set<VariableNode> outputNodes = new HashSet<>();
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap = new HashMap<>();
        /** Flag indicating that modifiers have been found at this level. */
        private boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** Rule morphism (from LHS to RHS). */
        private final RuleGraph mid;
        /** The set of nodes appearing in NACs. */
        private final Set<RuleNode> nacNodeSet = new HashSet<>();
        /** The set of edges appearing in NACs. */
        private final Set<RuleEdge> nacEdgeSet = new HashSet<>();
        /** Collection of NAC graphs. */
        private final List<RuleGraph> nacs = new ArrayList<>();
        /** Variables bound at the parent level. */
        private final Set<LabelVar> parentVars = new HashSet<>();
    }

    /**
     * A level 3 rule is a typed version of a level 2 rule,
     * or identical to the level 2 rule if there is no type graph.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class Level3 {
        public Level3(Level2 origin, Level3 parent, RuleGraphMorphism globalTypeMap)
            throws FormatException {
            this.parent = parent;
            this.factory = globalTypeMap.getFactory();
            this.index = origin.index;
            this.countNode = origin.countNode;
            this.outputNodes = origin.outputNodes;
            this.globalTypeMap = globalTypeMap;
            RuleGraphMorphism parentTypeMap =
                parent == null ? new RuleGraphMorphism(this.factory) : parent.typeMap;
            this.typeMap = new RuleGraphMorphism(this.factory);
            this.isRule = origin.isRule;
            this.lhs = toTypedGraph(origin.lhs, parentTypeMap, this.typeMap);
            // type the RHS taking the typing of the LHS into account
            // to allow use of the typed label variables
            RuleGraphMorphism lhsTypeMap = new RuleGraphMorphism(this.factory);
            lhsTypeMap.putAll(parentTypeMap);
            lhsTypeMap.putAll(this.typeMap);
            this.rhs = toTypedGraph(origin.rhs, lhsTypeMap, this.typeMap);
            // check against label type restrictions in RHS
            for (Map.Entry<LabelVar,Set<? extends TypeElement>> entry : lhsTypeMap.getVarTyping()
                .entrySet()) {
                LabelVar var = entry.getKey();
                if (!this.typeMap.getVarTyping()
                    .containsKey(var)) {
                    continue;
                }
                Set<? extends TypeElement> lhsTypes = entry.getValue();
                lhsTypes.removeAll(this.typeMap.getVarTypes(var));
                if (!lhsTypes.isEmpty()) {
                    this.errors.add("Invalid %s type%s %s for creator variable %s",
                        var.getKind()
                            .getDescription(false),
                        lhsTypes.size() == 1 ? "" : "s",
                        Groove.toString(lhsTypes.toArray(), "", "", ", "),
                        var);
                }
            }
            this.errors.throwException();
            for (RuleGraph nac : origin.nacs) {
                this.nacs.add(toTypedGraph(nac, this.typeMap, null));
            }
            // check for correct type specialisation
            // this has to be done after the NACs have been added
            try {
                Set<RuleNode> parentNodes = new HashSet<>();
                for (RuleNode origParentNode : parentTypeMap.nodeMap()
                    .keySet()) {
                    parentNodes.add(this.typeMap.getNode(origParentNode));
                }
                checkTypeSpecialisation(parentNodes, this.lhs, this.rhs);
            } catch (FormatException exc) {
                this.errors.addAll(transferErrors(exc.getErrors(), this.typeMap));
            }
            this.errors.throwException();
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
        private RuleGraph toTypedGraph(RuleGraph graph, RuleGraphMorphism parentTypeMap,
            RuleGraphMorphism typeMap) {
            RuleGraph result = createGraph(graph.getName());
            try {
                RuleGraphMorphism typing = getType().analyzeRule(graph, parentTypeMap);
                if (typeMap != null) {
                    typeMap.putAll(typing);
                }
                for (Map.Entry<RuleNode,RuleNode> nodeEntry : typing.nodeMap()
                    .entrySet()) {
                    RuleNode key = nodeEntry.getKey();
                    RuleNode image = nodeEntry.getValue();
                    assert image != null;
                    RuleNode globalImage = this.globalTypeMap.getNode(key);
                    if (globalImage == null) {
                        this.globalTypeMap.putNode(key, image);
                    }
                    result.addNode(image);
                }
                for (Map.Entry<RuleEdge,RuleEdge> edgeEntry : typing.edgeMap()
                    .entrySet()) {
                    RuleEdge key = edgeEntry.getKey();
                    RuleEdge image = edgeEntry.getValue();
                    assert image != null;
                    RuleEdge globalImage = this.globalTypeMap.getEdge(key);
                    if (globalImage == null) {
                        this.globalTypeMap.putEdge(key, globalImage = image);
                    }
                    result.addEdgeContext(globalImage);
                }
                result.addVarSet(graph.varSet());
            } catch (FormatException e) {
                this.errors.addAll(e.getErrors());
            }
            return result;
        }

        /**
         * If the RHS type for a reader node is changed w.r.t. the LHS type,
         * the LHS type has to be sharp and the RHS type a subtype of it.
         * @param parentNodes nodes from a higher quantification level
         * @throws FormatException if there are typing errors
         */
        private void checkTypeSpecialisation(Set<RuleNode> parentNodes, RuleGraph lhs,
            RuleGraph rhs) throws FormatException {
            FormatErrorSet errors = createErrors();
            for (RuleNode node : rhs.nodeSet()) {
                TypeNode nodeType = node.getType();
                if (nodeType.isAbstract() && !lhs.containsNode(node) && node.getTypeGuards()
                    .isEmpty()) {
                    errors.add("Creation of abstract %s-node not allowed", nodeType.label()
                        .text(), node);
                }
            }
            // check for ambiguous mergers
            List<RuleEdge> mergers = new ArrayList<>();
            Set<RuleNode> mergedNodes = new HashSet<>();
            for (RuleEdge edge : this.rhs.edgeSet()) {
                if (isMerger(edge)) {
                    mergers.add(edge);
                    RuleNode source = edge.source();
                    TypeNode sourceType = source.getType();
                    RuleNode target = edge.target();
                    TypeNode targetType = target.getType();
                    if (!targetType.getSupertypes()
                        .containsAll(source.getMatchingTypes())) {
                        errors.add("Actual type of merged %s-node may be subtype of merge target",
                            sourceType.label()
                                .text(),
                            edge);
                    } else if (!mergedNodes.add(source)) {
                        errors.add("%s-node is merged with two distinct nodes", sourceType.label()
                            .text(), source);
                    } else if (isUniversal(target) && !haveMinType(target)) {
                        errors.add("Actual target types of %s-merger may be ambiguous",
                            sourceType.label()
                                .text(),
                            edge);
                    } else if (!getType().isSubtype(targetType, sourceType)) {
                        errors.add("Merged %s-node must be supertype of %s",
                            sourceType.label()
                                .text(),
                            targetType.label()
                                .text(),
                            source);
                    } else if (source.getType()
                        .isDataType()) {
                        errors.add("Primitive %s-node can't be merged", sourceType.label()
                            .text(), source);
                    }
                } else {
                    TypeEdge edgeType = edge.getType();
                    if (edgeType != null && edgeType.isAbstract() && !lhs.containsEdge(edge)) {
                        errors.add("Creation of abstract %s-edge not allowed", edgeType.label()
                            .text(), edge);
                    }
                }
            }
            // check for non-injectively matched merge sources
            if (!isInjective()) {
                outer: for (RuleEdge merger1 : mergers) {
                    for (RuleEdge merger2 : mergers) {
                        // only check lower left half of matrix
                        if (merger1 == merger2) {
                            continue outer;
                        }
                        RuleNode source1 = merger1.source();
                        RuleNode source2 = merger2.source();
                        RuleNode target1 = merger1.target();
                        RuleNode target2 = merger2.target();
                        if (!injective(source1, source2) && !target1.equals(target2)
                            && !haveMinType(target1, target2)) {
                            errors.add(
                                "Non-injectively matched mergers have ambiguous target types",
                                merger1,
                                merger2);
                        }
                    }
                }
            }
            errors.throwException();
        }

        /** Tests if a given node is matched on a universal level. */
        private boolean isUniversal(RuleNode node) {
            Level3 highestLevel = this;
            Level3 parent = this.parent;
            while (parent != null && parent.rhs.containsNode(node)) {
                highestLevel = parent;
                parent = highestLevel.parent;
            }
            return highestLevel.getIndex()
                .isUniversal();
        }

        private boolean injective(RuleNode n1, RuleNode n2) {
            boolean result = false;
            // check for type overlap
            Set<TypeNode> types = new HashSet<>(n1.getMatchingTypes());
            types.retainAll(n2.getMatchingTypes());
            result = types.isEmpty();
            if (!result) {
                // check for != edges
                RuleLabel injection = new RuleLabel(RegExpr.empty()
                    .neg());
                for (RuleEdge edge : this.lhs.edgeSet(injection)) {
                    if (edge.source()
                        .equals(n1)
                        && edge.target()
                            .equals(n2)
                        || edge.source()
                            .equals(n2)
                            && edge.target()
                                .equals(n1)) {
                        result = true;
                        break;
                    }
                }
            }
            if (!result) {
                // check for NACs
                for (RuleGraph nac : this.nacs) {
                    Set<RuleNode> nacNodes = nac.nodeSet();
                    Set<RuleEdge> nacEdges = nac.edgeSet();
                    if (nacNodes.size() == 2 && nacNodes.contains(n1) && nacNodes.contains(n2)
                        && nacEdges.size() == 1 && nacEdges.iterator()
                            .next()
                            .label()
                            .isEmpty()) {
                        result = true;
                        break;
                    }
                }
            }
            if (!result && this.parent != null && this.parent.lhs.containsNode(n1)
                && this.parent.lhs.containsNode(n2)) {
                result = this.parent.injective(n1, n2);
            }
            return result;
        }

        /** Tests if the host nodes that can be matched non-injectively by
         * a given non-empty set of rule nodes are certain to have a minimum type. */
        private boolean haveMinType(RuleNode... mergeTargets) {
            assert mergeTargets.length > 0;
            boolean result = true;
            // collect the common type label variables
            Set<LabelVar> commonVars = null;
            if (mergeTargets.length == 1) {
                commonVars = mergeTargets[0].getVars();
            } else {
                for (RuleNode node : mergeTargets) {
                    if (commonVars == null) {
                        commonVars = new HashSet<>(node.getVars());
                    } else {
                        commonVars.retainAll(node.getVars());
                    }
                }
                assert commonVars != null; // because mergeTargets is not empty
            }
            // if there is a common variable, the types are fixed and equal
            if (commonVars.isEmpty()) {
                // take the union of all merge target types
                Set<TypeNode> allTypes = null;
                if (mergeTargets.length == 1) {
                    allTypes = mergeTargets[0].getMatchingTypes();
                } else {
                    for (RuleNode node : mergeTargets) {
                        if (allTypes == null) {
                            allTypes = new HashSet<>(node.getMatchingTypes());
                        } else {
                            allTypes.addAll(node.getMatchingTypes());
                        }
                    }
                    assert allTypes != null; // because mergeTargets is not empty
                }
                // check that the set of types is linearly ordered
                outer: for (TypeNode one : allTypes) {
                    for (TypeNode two : allTypes) {
                        // we only check the lower left part of the matrix
                        if (two == one) {
                            continue outer;
                        }
                        if (!one.getSubtypes()
                            .contains(two)
                            && !one.getSupertypes()
                                .contains(two)) {
                            result = false;
                            break outer;
                        }
                    }
                }
            }
            return result;
        }

        /** Tests if a given RHS edge is a merger. */
        private boolean isMerger(RuleEdge rhsEdge) {
            return !this.lhs.containsEdge(rhsEdge) && rhsEdge.label()
                .isEmpty();
        }

        /**
         * Callback method to create an untyped graph that can serve as LHS or RHS of a rule.
         * @see #getSource()
         */
        private RuleGraph createGraph(String name) {
            return new RuleGraph(name, isInjective(), this.factory);
        }

        private final Level3 parent;
        private final RuleFactory factory;
        /** Index of this level. */
        private final Index index;
        /** Output nodes of the condition. */
        private final Set<VariableNode> outputNodes;
        /** The rule node registering the match count. */
        private final VariableNode countNode;
        /** The global, rule-wide mapping from untyped to typed rule elements. */
        private final RuleGraphMorphism globalTypeMap;
        /** Combined type map for this level. */
        private final RuleGraphMorphism typeMap;
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap = new HashMap<>();
        /** Flag indicating that modifiers have been found at this level. */
        private final boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** List of NAC graphs. */
        private final List<RuleGraph> nacs = new ArrayList<>();
        /** List of typing errors. */
        private final FormatErrorSet errors = createErrors();
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
        public Level4(Level3 origin, Level4 parent) {
            this.isRule = origin.isRule;
            this.index = origin.index;
            this.parent = parent;
            // initialise the rule data structures
            this.lhs = origin.lhs;
            this.nacs = origin.nacs;
            this.rhs = origin.rhs;
            this.countNode = origin.countNode;
            this.outputNodes = origin.outputNodes;
            this.colorMap = origin.colorMap;
        }

        /**
         * Callback method to compute the rule on this nesting level.
         * The resulting condition is not fixed (see {@link Condition#isFixed()}).
         */
        public Condition computeFlatRule() throws FormatException {
            Condition result;
            FormatErrorSet errors = createErrors();
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
            errors.throwException();
            return result;
        }

        /**
         * Returns the mapping from the LHS rule elements at the parent level to
         * the LHS rule elements at this level.
         */
        private RuleGraph getRootGraph() {
            return this.index.isTopLevel() ? null : getIntersection(this.parent.lhs, this.lhs);
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
            return parent == null ? null : getIntersection(parent.rhs, this.rhs);
        }

        /**
         * Returns a rule graph that forms the intersection of the rule elements
         * of this and the parent level.
         */
        private RuleGraph getIntersection(RuleGraph parentGraph, RuleGraph myGraph) {
            RuleGraph result = parentGraph.newGraph(getQualName() + "-" + getIndex() + "-root");
            for (RuleNode node : parentGraph.nodeSet()) {
                if (myGraph.containsNode(node)) {
                    result.addNode(node);
                }
            }
            for (RuleEdge edge : parentGraph.edgeSet()) {
                if (myGraph.containsEdge(edge)) {
                    result.addEdgeContext(edge);
                }
            }
            for (LabelVar var : parentGraph.varSet()) {
                if (myGraph.containsVar(var)) {
                    result.addVar(var);
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
        private Condition computeNac(RuleGraph lhs, RuleGraph nac) throws FormatException {
            Condition result = null;
            // first check for merge end edge embargoes
            // they are characterised by the fact that there is precisely 1
            // element
            // in the nacElemSet, which is an edge
            if (nac.edgeCount() == 1) {
                RuleEdge embargoEdge = nac.edgeSet()
                    .iterator()
                    .next();
                Set<RuleNode> ends =
                    new HashSet<>(Arrays.asList(embargoEdge.source(), embargoEdge.target()));
                if (nac.nodeSet()
                    .equals(ends)
                    && lhs.nodeSet()
                        .containsAll(ends)
                    && nac.varSet()
                        .isEmpty()) {
                    // this is supposed to be an edge embargo
                    result = createEdgeEmbargo(lhs, embargoEdge);
                }
            }
            if (result == null) {
                // if we're here it means we couldn't make an embargo
                // if the rule is injective, add all non-data lhs nodes to the NAC pattern
                if (isInjective()) {
                    for (RuleNode node : lhs.nodeSet()) {
                        if (node instanceof DefaultRuleNode) {
                            nac.addNode(node);
                        }
                    }
                }
                result = createNAC(lhs, nac);
            }
            result.setFixed();
            return result;
        }

        /**
         * Callback method to create an edge embargo.
         * @param context the context-graph
         * @param embargoEdge the edge to be turned into an embargo
         * @return the new {@link groove.grammar.EdgeEmbargo}
         * @see #toResource()
         */
        private EdgeEmbargo createEdgeEmbargo(RuleGraph context, RuleEdge embargoEdge) {
            return new EdgeEmbargo(context, embargoEdge, getGrammarProperties());
        }

        /**
         * Callback method to create a general NAC on a given graph.
         * @param nac the context-graph
         * @return the new {@link groove.grammar.Condition}
         * @see #toResource()
         */
        private Condition createNAC(RuleGraph lhs, RuleGraph nac) {
            String name = nac.getName();
            return new Condition(name, Condition.Op.NOT, nac, getIntersection(lhs, nac),
                getGrammarProperties());
        }

        /**
         * Factory method for rules.
         * @param condition name of the new rule to be created
         * @param rhs the right hand side graph
         * @param coRoot map of creator nodes in the parent rule to creator
         *        nodes of this rule
         * @return the fresh rule created by the factory
         */
        private Rule createRule(Condition condition, RuleGraph rhs, RuleGraph coRoot) {
            Rule result = new Rule(condition, rhs, coRoot);
            return result;
        }

        /**
         * Factory method for universal conditions.
         * @param root root graph of the new condition
         * @param pattern target graph of the new condition
         * @return the fresh condition
         */
        private Condition createCondition(RuleGraph root, RuleGraph pattern) {
            Condition result = new Condition(this.index.getName(), this.index.getOperator(),
                pattern, root, getGrammarProperties());
            result.setTypeGraph(getType());
            if (this.index.isPositive()) {
                result.setPositive();
            }
            if (this.countNode != null) {
                result.setCountNode(this.countNode);
            }
            result.addOutputNodes(this.outputNodes);
            return result;
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, stage 4", getQualName(), getIndex());
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        /** Index of this level. */
        private final Index index;
        /** Index of this level. */
        private final Level4 parent;
        /** Output nodes of the condition. */
        private final Set<VariableNode> outputNodes;
        /** The rule node registering the match count. */
        private final VariableNode countNode;
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap;
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
            FormatErrorSet errors = createErrors();
            this.hiddenPars = new HashSet<>();
            // Mapping from parameter position to parameter
            Map<Integer,UnitPar.RulePar> parMap = new HashMap<>();
            int parCount = 0;
            // collect parameter nodes
            for (AspectNode node : getSource().nodeSet()) {
                // check if the node is a parameter
                if (node.hasParam()) {
                    Integer nr = (Integer) node.getParam()
                        .getContent();
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
                            throw new FormatException("Anchor node cannot be input or output",
                                node);
                        }
                        if (!node.getKind()
                            .inLHS()) {
                            throw new FormatException("Anchor node must be in LHS", node);
                        }
                        RuleNode nodeImage = RuleModel.this.modelMap.getNode(node);
                        assert nodeImage != null;
                        this.hiddenPars.add(nodeImage);
                    }
                }
            }
            errors.throwException();
            // construct the signature
            // test if parameters form a consecutive sequence
            Set<Integer> missingPars = new TreeSet<>();
            for (int i = 0; i < parCount; i++) {
                missingPars.add(i);
            }
            missingPars.removeAll(parMap.keySet());
            if (!missingPars.isEmpty()) {
                throw new FormatException("Parameters %s missing", missingPars);
            }
            UnitPar.RulePar[] sigArray = new UnitPar.RulePar[parCount];
            for (Map.Entry<Integer,UnitPar.RulePar> parEntry : parMap.entrySet()) {
                sigArray[parEntry.getKey()] = parEntry.getValue();
            }
            this.sig = Arrays.asList(sigArray);
        }

        private void processNode(Map<Integer,UnitPar.RulePar> parMap, AspectNode node, Integer nr)
            throws FormatException {
            AspectKind nodeKind = node.getKind();
            AspectKind paramKind = node.getParamKind();
            RuleNode nodeImage = getMap().getNode(node);
            assert nodeImage != null;
            if (paramKind == PARAM_IN && nodeKind.isCreator()) {
                throw new FormatException("Input parameter %d cannot be creator node", nr, node);
            }
            if (nodeKind.inNAC()) {
                throw new FormatException("Parameter '%d' may not occur in NAC", nr, node);
            }
            UnitPar.RulePar par = new UnitPar.RulePar(paramKind, nodeImage, nodeKind.isCreator());
            UnitPar.RulePar oldPar = parMap.put(nr, par);
            if (oldPar != null) {
                throw new FormatException("Parameter '%d' defined more than once", nr, node,
                    oldPar.getNode());
            }
        }

        /** Lazily creates and returns the rule's hidden parameters. */
        public Set<RuleNode> getHiddenPars() {
            return this.hiddenPars;
        }

        /** Returns the rule signature. */
        public Signature<UnitPar.RulePar> getSignature() {
            return new Signature<>(this.sig);
        }

        /** Set of all rule parameter nodes */
        private Set<RuleNode> hiddenPars;
        /** Signature of the rule. */
        private List<UnitPar.RulePar> sig;
    }

    /** Mapping from aspect graph elements to rule graph elements. */
    private static class RuleModelMap extends ModelMap<RuleNode,RuleEdge> {
        /**
         * Creates a new, empty map to a rule graph with a given type factory.
         */
        public RuleModelMap(RuleFactory factory) {
            super(factory);
        }

        /**
         * Creates a new, empty map to an untyped rule graph.
         */
        public RuleModelMap() {
            super(RuleFactory.newInstance());
        }

        @Override
        public RuleFactory getFactory() {
            return (RuleFactory) super.getFactory();
        }
    }
}
