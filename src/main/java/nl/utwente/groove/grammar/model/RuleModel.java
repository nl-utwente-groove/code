/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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

package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_IN;
import static nl.utwente.groove.grammar.model.ResourceKind.GROOVY;
import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Condition.Op;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

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
        addDependencies(PROPERTIES, GROOVY);
        assert grammar != null;
        graph.testFixed(true);
    }

    @Override
    public @NonNull GrammarModel getGrammar() {
        var result = super.getGrammar();
        assert result != null;
        return result;
    }

    @Override
    public boolean isActive() {
        return getGrammar().getActiveNames(ResourceKind.RULE).contains(getQualName())
            || hasRecipes();
    }

    /** Returns the set of recipe names in which this rule is called. */
    public Set<QualName> getRecipes() {
        return getGrammar().getControlModel().getRecipes(getQualName());
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
            this.role = ResourceProperties.getRole(getSource()).orElse(getDefaultRole());
        }
        return this.role;
    }

    private Role role;

    /** Returns the policy for dealing with rule matches, set in the grammar properties. */
    public CheckPolicy getPolicy() {
        CheckPolicy result = getGrammarProperties().getRulePolicy().get(getQualName());
        if (result == null) {
            result = getRole().isConstraint()
                ? CheckPolicy.ERROR
                : CheckPolicy.SILENT;
        }
        return result;
    }

    /** Returns the default role for this rule.
     * The default is {@link Role#CONDITION} if the rule has no qualities that change the graph.
     */
    private Role getDefaultRole() {
        return testAsProperty(getSource()) == null
            ? Role.CONDITION
            : Role.TRANSFORMER;
    }

    /**
     * Tests if the rule given by an aspect graph may be used as a graph condition.
     * @return an appropriate {@link FormatError} if the rule is unsuitable
     * @see Rule#isProperty()
     */
    static FormatError testAsProperty(AspectGraph source) {
        if (ResourceProperties.getPriority(source) > 0) {
            return new FormatError("positive priority not allowed");
        }
        for (AspectNode node : source.nodeSet()) {
            /* Only input parameters are problematic for conditions */
            if (node.has(PARAM_IN)) {
                return new FormatError("input parameter not allowed", node);
            }
            if (node.hasColor()) {
                return new FormatError("colour change not allowed", node);
            }
            if (node.has(Category.ROLE, k -> k.isEraser())) {
                return new FormatError("eraser not allowed", node);
            }
            if (node.has(Category.ROLE, k -> k.isCreator())) {
                return new FormatError("creator not allowed", node);
            }
        }
        for (AspectEdge edge : source.edgeSet()) {
            if (edge.isAssign()) {
                return new FormatError("assignment not allowed", edge.source());
            }
            if (edge.has(Category.ROLE, k -> k.isEraser())) {
                return new FormatError("eraser not allowed", edge);
            }
            if (edge.has(Category.ROLE, k -> k.isCreator())) {
                return new FormatError("creator not allowed", edge);
            }
        }
        return null;
    }

    /**
     * Returns the priority of the rule of which this is a model. Yields the same
     * result as <code>toRule().getPriority()</code>.
     */
    public int getPriority() {
        return ResourceProperties.getPriority(getSource());
    }

    @Override
    Rule compute() throws FormatException {
        getSource().getErrors().throwException();
        AspectGraph normalSource = getNormalSource();
        normalSource.getErrors().throwException();
        var compiler = this.compiler
            = new RuleCompiler(getGrammar(), getSource(), normalSource, getRole());
        return compiler.compile();
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (getGrammar().getTypeModel().isImplicit()) {
            // the implicit type graph gets rebuilt when the start graph changes
            // so we must also rebuild, otherwise the type graphs will diverge
            result |= isStale(ResourceKind.HOST);
        } else {
            // the type graph is a dependency only if it is not implicit
            // if it is implicit, then instead it depends on the set of rules
            result |= isStale(ResourceKind.TYPE);
        }
        return result;
    }

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.labelSet = null;
        this.compiler = null;
    }

    /** Returns the set of labels occurring in this rule. */
    @Override
    public @NonNull Set<@NonNull TypeLabel> getTypeLabels() {
        Set<TypeLabel> result = this.labelSet;
        if (result == null) {
            Set<TypeLabel> labelSet = new HashSet<>();
            getNormalSource()
                .edgeSet()
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
            throw Exceptions
                .illegalState("Can't compute map while rule has errors: %s", getErrors());
        }
        var compiler = this.compiler;
        assert compiler != null; // absence of errors implies successful compilation
        return compiler.getModelMap();
    }

    @Override
    public TypeModelMap getTypeMap() {
        synchronise();
        var compiler = this.compiler;
        return compiler == null
            ? null
            : compiler.getTypeMap();
    }

    /** Returns the (implicit or explicit) type graph of this grammar. */
    @Override
    public TypeGraph getTypeGraph() {
        return getGrammar().getTypeGraph();
    }

    /** Returns a mapping from rule nesting levels to sets of aspect elements on that level. */
    public TreeMap<Index,Set<AspectElement>> getLevelTree() {
        synchronise();
        var compiler = this.compiler;
        return compiler == null
            ? null
            : compiler.getLevelTree();
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
        return ResourceProperties.isInjective(getSource()) || getGrammarProperties().isInjective();
    }

    /** Returns the normalised aspect graph underlying this rule model. */
    public AspectGraph getNormalSource() {
        var result = this.normalSource;
        if (result == null) {
            result = this.normalSource = getSource().normalise();
        }
        return result;
    }

    /** The normalised source model. */
    private @Nullable AspectGraph normalSource;

    /** Set of all labels occurring in the rule. */
    private Set<TypeLabel> labelSet;

    /** The compiler of the most recent (re)computation, holding the compiled
     * rule together with the level tree and the model and type maps;
     * {@code null} if the model has not been (re)computed. */
    private @Nullable RuleCompiler compiler;

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
            assert levelNode == null || levelNode.has(Category.NESTING);
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
            if (levelNode == null || levelNode.getId() == null) {
                suffix = isTopLevel()
                    ? ""
                    : Strings.toString(this.index.toArray());
            } else {
                suffix = "-" + levelNode.getId();
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
                result = this.index.get(i).equals(other.index.get(i));
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

    /** Mapping from aspect graph elements to rule graph elements. */
    public static class RuleModelMap extends ModelMap<@NonNull RuleNode,@NonNull RuleEdge> {
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
