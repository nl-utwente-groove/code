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
 * $Id: AbstractCondition.java,v 1.15 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.algebra.AlgebraFamily;
import groove.control.CtrlPar.Var;
import groove.graph.EdgeRole;
import groove.graph.TypeGraph;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.VariableNode;
import groove.io.HTMLConverter;
import groove.io.Util;
import groove.match.SearchEngine.SearchMode;
import groove.util.Fixable;
import groove.view.FormatErrorSet;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Type of conditions over graphs.
 * A condition is a hierarchical structure, the levels of which are
 * essentially first-order operators, in particular existential or
 * universal quantifiers. Each quantifier or negation has an associated 
 * <i>pattern</i>, which is the graph that should be matched (existentially 
 * or universally) on that level.
 * <p>
 * A condition consists of the following elements:
 * <ul>
 * <li> The <i>operator</i>: this is the first-order logic operator. It is
 * a value of type {@link Op}.
 * <li> The <i>root</i>: the parent graph in the condition hierarchy.
 * A condition can only be matched relative to a match of its root. A condition
 * is called <i>ground</i> if its root is the empty graph.
 * <li> The <i>pattern</i>: the graph describing the structure that is to
 * be matched in a host graph. The (implicit) morphism between the root and
 * the pattern is based on node and edge identity, and is not explicitly
 * stored.
 * <li> The <i>seed</i>: the intersection of the root and the pattern.
 * The seed is thus the subgraph of the pattern that is pre-matched
 * before the condition itself is matched.
 * <li> The <i>anchor</i>: the subgraph of the pattern whose exact image in the
 * host graph is relevant. This includes at least the seed and the elements mapped
 * to the next levels in the condition tree.
 * <li> The <i>subconditions</i>: the next levels in the condition tree. Each
 * subcondition has the pattern of this condition as its root.
 * </ul>
 * The following concepts play a role when matching a condition:
 * <ul>
 * <li> A <i>context map</i>: Mapping from the root to a host graph
 * <li> A <i>seed map</i>: Mapping from the seed to a host graph. This is
 * essentially a context map restricted to the condition's root
 * <li> A <i>pattern map</i>: Mapping from the pattern to the host graph. This
 * is determined by searching for an extension to a seed map.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
public class Condition implements Fixable {
    /** Constructs a condition for a non-pattern operator. */
    public Condition(String name, Op operator) {
        assert !operator.hasPattern();
        this.op = operator;
        this.name = name;
        this.factory = null;
        this.pattern = null;
        this.root = null;
        this.systemProperties = null;
    }

    /**
     * Constructs a (named) graph condition based on a given pattern graph
     * and root graph.
     * @param name the name of the condition; may be <code>null</code>
     * @param operator the top-level operator of this condition
     * @param pattern the graph to be matched
     * @param root the root graph of the condition; may be <code>null</code> if the condition is
     *        ground
     * @param properties properties for matching the condition
     */
    public Condition(String name, Op operator, RuleGraph pattern,
            RuleGraph root, SystemProperties properties) {
        assert operator.hasPattern();
        this.op = operator;
        this.name = name;
        this.factory = pattern.getFactory();
        this.root =
            root == null ? pattern.newGraph((name == null ? "root" : name
                + "-root")) : root;
        this.pattern = pattern;
        this.systemProperties = properties;
    }

    /** Returns the secondary properties of this graph condition. */
    public SystemProperties getSystemProperties() {
        return this.systemProperties;
    }

    /**
     * Sets the type graph of this graph condition.
     */
    public void setTypeGraph(TypeGraph typeGraph) {
        assert typeGraph != null;
        this.typeGraph = typeGraph;
        for (Condition sub : getSubConditions()) {
            sub.setTypeGraph(typeGraph);
        }
    }

    /**
     * Returns the type graph of this graph condition.
     * The type graph must be set before the graph is fixed.
     */
    public TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /** 
     * Returns the root graph of this condition.
     * The root graph is the subgraph of the pattern that the
     * condition has in common with its parent in the condition tree.
     */
    public RuleGraph getRoot() {
        return this.root;
    }

    /**
     * Returns the subset of the root nodes that are certainly
     * bound before the condition has to be matched.
     */
    final public Set<RuleNode> getInputNodes() {
        if (this.inputNodes == null) {
            this.inputNodes = computeInputNodes();
        }
        return this.inputNodes;
    }

    /**
     * Computes the set of input nodes nodes of this condition. 
     * These are the nodes that are certainly
     * bound before the condition has to be matched.
     */
    Set<RuleNode> computeInputNodes() {
        if (hasRule() && getRule().isTop()) {
            // collect the input parameters
            Set<RuleNode> result = new HashSet<RuleNode>();
            for (Var var : getRule().getSignature()) {
                if (var.isInOnly()) {
                    result.add(var.getRuleNode());
                }
            }
            return result;
        } else {
            return new HashSet<RuleNode>(this.root.nodeSet());
        }
    }

    /** Indicates if this condition has an associated graph pattern.
     * This is the case if and only if the condition operator is a quantifier.
     * @return {@code true} if and only if {@link #getPattern()} does not return
     * {@code null}
     */
    public boolean hasPattern() {
        return getPattern() != null;
    }

    /**
     * Returns the pattern of the condition, i.e., the structure that
     * the condition actually tests for.
     */
    public RuleGraph getPattern() {
        return this.pattern;
    }

    /**
     * Returns the name of this condition. A return value of <code>null</code>
     * indicates that the condition is unnamed.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Indicates if this condition is closed, which is to say that it has
     * an empty root.
     * @return <code>true</code> if this condition has an empty root.
     */
    public boolean isGround() {
        assert isFixed();
        return this.root.isEmpty();
    }

    /**
     * Returns the collection of sub-conditions of this graph condition. The
     * intended interpretation of the sub-conditions (as conjuncts or disjuncts)
     * depends on this condition.
     */
    public Collection<Condition> getSubConditions() {
        return this.subConditions;
    }

    /**
     * Adds a subcondition to this graph condition.
     * The subcondition should already be fixed.
     * @param condition the condition to be added
     * @see #getSubConditions()
     */
    public void addSubCondition(Condition condition) {
        if (!getOp().hasOperands()) {
            throw new UnsupportedOperationException(String.format(
                "%s conditions cannot have subconditions", condition.getOp()));
        }
        condition.testFixed(true);
        testFixed(false);
        if (this.typeGraph != null) {
            condition.setTypeGraph(this.typeGraph);
        }
        getSubConditions().add(condition);
        if (getRule() != null) {
            for (Rule subRule : condition.getTopRules()) {
                getRule().addSubRule(subRule);
            }
        }
    }

    /**
     * Tests if any of the rules in this condition (or one of its subconditions)
     * is modifying.
     */
    boolean hasModifyingSubrule() {
        boolean result = false;
        for (Rule rule : getTopRules()) {
            if (rule.isModifying()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** Returns the collection of top-level (sub)rules of this condition.
     * This is either the rule associated with this condition (if any),
     * or, recursively, with any of its subconditions.
     * @return the collection of top-level (sub)rules of this condition
     */
    private List<Rule> getTopRules() {
        List<Rule> result = new ArrayList<Rule>();
        if (getRule() == null) {
            for (Condition subCond : getSubConditions()) {
                result.addAll(subCond.getTopRules());
            }
        } else {
            result.add(getRule());
        }
        return result;
    }

    /** Fixes this condition and all its subconditions. */
    public boolean setFixed() throws FormatException {
        boolean result = !isFixed();
        if (result && !this.fixing) {
            this.fixing = true;
            for (Condition subCondition : getSubConditions()) {
                subCondition.testFixed(true);
            }
            this.fixed = true;
            if (hasPattern()) {
                getPattern().setFixed();
                if (!getSystemProperties().getAlgebraFamily().supportsSymbolic()) {
                    checkResolution();
                }
                if (getRule() != null) {
                    getRule().setFixed();
                }
            }
            this.fixing = false;
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    /**
     * Tests if the condition is fixed or not. Throws an exception if the
     * fixedness does not coincide with the given value.
     * 
     * @param value the expected fixedness state
     * @throws IllegalStateException if {@link #isFixed()} does not yield
     *         <code>value</code>
     */
    @Override
    public void testFixed(boolean value) throws IllegalStateException {
        if (isFixed() != value) {
            String message;
            if (value) {
                message = "Graph condition should be fixed in this state";
            } else {
                message = "Graph condition should not be fixed in this state";
            }
            throw new IllegalStateException(message);
        }
    }

    /**
     * Tests if the condition can be used to tests on graphs rather than
     * morphisms. This is the case if and only if the condition is ground (i.e.,
     * the root graph is empty), as determined by {@link #isGround()}.
     * 
     * @throws IllegalStateException if this condition is not ground.
     * @see #isGround()
     */
    void testGround() throws IllegalStateException {
        if (!isGround()) {
            throw new IllegalStateException(
                "Method only allowed on ground condition");
        }
    }

    /**
     * Tests if the algebra part of the target graph can be matched. This
     * requires that there are no variable nodes that cannot be resolved, no
     * typing conflicts, and no missing arguments. This is checked at fixing
     * time of the condition.
     * @throws FormatException if the algebra part cannot be matched
     */
    public void checkResolution() throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Map<VariableNode,List<Set<VariableNode>>> resolverMap =
            createResolvers();
        stabilise(resolverMap);
        for (RuleNode node : resolverMap.keySet()) {
            errors.add(
                "Variable node '%s' cannot always be assigned (use %s algebra for symbolic exploration)",
                node, AlgebraFamily.POINT.getName());
        }
        errors.throwException();
    }

    /** 
     * Creates a mapping from unresolved variables to potential resolvers.
     * Each resolver is a set of variables that all have to be resolved in order
     * for the key to be resolved.
     */
    private Map<VariableNode,List<Set<VariableNode>>> createResolvers() {
        Map<VariableNode,List<Set<VariableNode>>> result =
            new HashMap<VariableNode,List<Set<VariableNode>>>();
        // Set of variable nodes already found to have been resolved
        Set<VariableNode> resolved = new HashSet<VariableNode>();
        for (RuleNode node : getInputNodes()) {
            if (node instanceof VariableNode) {
                resolved.add((VariableNode) node);
            }
        }
        // Set of variable nodes needing resolution
        for (RuleNode node : getPattern().nodeSet()) {
            if (node instanceof VariableNode
                && ((VariableNode) node).getConstant() == null
                && !resolved.contains(node)) {
                VariableNode varNode = (VariableNode) node;
                boolean isResolved = false;
                for (RuleEdge inEdge : getPattern().inEdgeSet(node)) {
                    if (!inEdge.label().isEmpty()) {
                        isResolved = true;
                        break;
                    }
                }
                if (isResolved) {
                    resolved.add(varNode);
                } else {
                    result.put(varNode, new ArrayList<Set<VariableNode>>());
                }
            }
        }
        // first collect the count nodes of subconditions, if this node is conjunctive
        // or there is only one subcondition
        if (getOp().isConjunctive() || getSubConditions().size() == 1) {
            for (Condition subCondition : getSubConditions()) {
                VariableNode countNode = subCondition.getCountNode();
                // check if the condition has a non-constant count node
                if (countNode != null && countNode.getConstant() == null
                    && !resolved.contains(countNode)) {
                    Set<VariableNode> resolver = new HashSet<VariableNode>();
                    // add the unresolved root nodes of the subcondition to the resolver
                    for (RuleNode rootNode : subCondition.getInputNodes()) {
                        if (rootNode instanceof VariableNode
                            && ((VariableNode) rootNode).getConstant() == null) {
                            resolver.add((VariableNode) rootNode);
                        }
                    }
                    resolver.removeAll(resolved);
                    if (resolver.isEmpty()) {
                        resolved.add(countNode);
                    } else {
                        addResolver(result, countNode, resolver);
                    }
                }
            }
        }
        // now add resolvers due to operator nodes
        for (RuleNode node : getPattern().nodeSet()) {
            if (node instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) node;
                VariableNode target = opNode.getTarget();
                if (result.containsKey(target) && !resolved.contains(target)) {
                    // collect the argument nodes
                    Set<VariableNode> resolver = new HashSet<VariableNode>();
                    for (VariableNode arg : opNode.getArguments()) {
                        if (!arg.hasConstant()) {
                            resolver.add(arg);
                        }
                    }
                    resolver.removeAll(resolved);
                    if (resolver.isEmpty()) {
                        resolved.add(target);
                    } else {
                        result.get(target).add(resolver);
                    }
                }
            }
        }
        for (VariableNode node : resolved) {
            result.remove(node);
        }
        return result;
    }

    /** Adds a value to the list of values for a given key. */
    private <K,V> void addResolver(Map<K,List<V>> map, K key, V value) {
        List<V> entry = map.get(key);
        if (entry == null) {
            map.put(key, entry = new ArrayList<V>());
        }
        entry.add(value);
    }

    /** Removes entries from the resolver map if they have an empty resolver. */
    private void stabilise(Map<VariableNode,List<Set<VariableNode>>> resolverMap) {
        boolean stable = false;
        // repeat until no more changes
        while (!stable) {
            stable = true;
            // iterate over all resolver lists
            Iterator<List<Set<VariableNode>>> iter =
                resolverMap.values().iterator();
            while (iter.hasNext()) {
                // try each resolver in turn
                for (Set<VariableNode> resolver : iter.next()) {
                    // restrict to the unresolved nodes
                    resolver.retainAll(resolverMap.keySet());
                    // if there are no unresolved nodes, the entry may be removed
                    if (resolver.isEmpty()) {
                        iter.remove();
                        stable = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    /** Returns a string description, indented with a certain prefix. */
    public String toString(String prefix) {
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        result.append(String.format("%s condition %s", getOp().getName(),
            getName()));
        if (hasPattern()) {
            result.append('\n');
            result.append(prefix);
            result.append(" * Root:    " + getRoot());
            result.append('\n');
            result.append(prefix);
            result.append(" * Pattern: " + getPattern());
        }
        if (hasRule()) {
            result.append('\n');
            result.append(prefix);
            result.append(" * RHS:     " + getRule().rhs());
        }
        if (hasCountNode()) {
            result.append('\n');
            result.append(prefix);
            result.append(" * Count:   " + getCountNode());
        }
        if (!getSubConditions().isEmpty()) {
            result.append('\n');
            result.append(prefix);
            result.append(" * Subconditions:");
            for (Condition sub : getSubConditions()) {
                result.append('\n');
                result.append(sub.toString(prefix + "     "));
            }
        }
        return result.toString();
    }

    /** Returns the operator of this condition. */
    public final Op getOp() {
        return this.op;
    }

    /** Returns true if the operator of this condition is a NOT. */
    public final boolean isNot() {
        return getOp() == Op.NOT;
    }

    /** Returns the rule factory of this condition. */
    public RuleFactory getFactory() {
        return this.factory;
    }

    /** Sets a count node for this universal condition. 
     * @see #getCountNode() */
    public void setCountNode(VariableNode countNode) {
        assert !isFixed();
        this.countNode = countNode;
    }

    /** 
     * Returns the count node of this universal condition, if any.
     * The count node is bound to the number of matches of the condition.
     * @return the count node, or {@code null} if there is none
     */
    public VariableNode getCountNode() {
        return this.countNode;
    }

    /** 
     * Tests if this is a universal condition with a count node.
     */
    public boolean hasCountNode() {
        return this.countNode != null;
    }

    /** Sets this universal condition to positive (meaning that
     * it should have at least one match). */
    public void setPositive() {
        assert !isFixed();
        this.positive = true;
    }

    /**
     * Indicates if this condition is positive. A universal condition is
     * positive if it cannot be vacuously fulfilled; i.e., there must always be
     * at least one match.
     */
    public boolean isPositive() {
        return this.positive;
    }

    /** Indicates if this condition should be matched injectively. */
    public boolean isInjective() {
        return getSystemProperties() != null
            && getSystemProperties().isInjective();
    }

    /** Sets the associated rule of this condition. */
    public void setRule(Rule rule) {
        assert !isFixed();
        this.rule = rule;
    }

    /**
     * Indicates if there is a rule associated with this condition.
     * Only existential and universal conditions can have associated rules.
     * Convenience method for {@code getRule() != null}.
     * @return {@code true} if there is a rule associated with this condition
     * @see #getRule()
     */
    final public boolean hasRule() {
        return getRule() != null;
    }

    /**
     * Returns the rule associated with this condition, if any.
     * Only existential and universal conditions can have associated rules.
     * @return The rule associated with this condition, or {@code null}
     * if there is no associated rule.
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Returns a new condition object that is the reverse of this one. Hence,
     * a NAC becomes a Positive Application Condition.
     * Used in the abstraction code.
     * Fails in an assertion if this condition is not a NAC. 
     */
    public Condition reverse() {
        assert getOp() == Op.NOT;
        Condition result =
            new Condition(getName(), Op.FORALL, getPattern(), getRoot(),
                getSystemProperties());
        result.addSubCondition(True);
        if (getTypeGraph() != null) {
            result.setTypeGraph(getTypeGraph());
        }
        if (isFixed()) {
            try {
                result.setFixed();
            } catch (FormatException e) {
                // Cannot happen since we already had a fixed condition.
                e.printStackTrace();
            }
        }
        return result;
    }

    /** Returns true if this condition can be reversed. */
    public boolean isReversable() {
        return isNot() && hasBinaryEdges();
    }

    /**
     * Returns true if this condition is compatible with the given search
     * mode and therefore should be included in the search plan.
     */
    public boolean isCompatible(SearchMode searchMode) {
        switch (searchMode) {
        case NORMAL:
            return true;
        case MINIMAL:
            return !(isNot() && hasBinaryEdges());
        case REVERSE:
            if (!isNot()) {
                // EZ says: current abstraction implementation does not support
                // rules with multiple levels. Therefore, if this point in the
                // code is reached it is due to artificial FORALL level that is
                // introduced when we reverse a NAC. This handles the True
                // sub-condition.
                assert getOp() == Op.TRUE;
                return true;
            } else { // isNot()
                // EZ says: there are two possibilities for this condition.
                // Either it will be discarded (because it was already handled
                // in the minimal mode) or it will be reversed. In any case we
                // set it to incompatible at this point.
                return false;
            }
        default:
            assert false;
            return false;
        }
    }

    private boolean hasBinaryEdges() {
        for (RuleEdge ruleEdge : getPattern().edgeSet()) {
            if (ruleEdge.getRole() == EdgeRole.BINARY) {
                return true;
            }
        }
        return false;
    }

    /** The operator of this condition. */
    private final Op op;
    /**
     * The name of this condition. May be <code>code</code> null.
     */
    private final String name;
    /** The factory responsible for creating rule nodes and edges. */
    private final RuleFactory factory;

    /** The rule associated with this condition, if any. */
    private Rule rule;

    /** The collection of sub-conditions of this condition. */
    private final Collection<Condition> subConditions =
        new ArrayList<Condition>();

    /** Flag indicating if this condition is now fixed, i.e., unchangeable. */
    private boolean fixed;
    /** Flag indicating if this condition is in the process of fixing. */
    private boolean fixing;
    /**
     * The root map of this condition, i.e., the element map from the root
     * graph to the pattern graph.
     */
    private final RuleGraph root;

    /** Subset of the root nodes that are bound to be bound before the condition is matched. */
    private Set<RuleNode> inputNodes;

    /** The pattern graph of this morphism. */
    private final RuleGraph pattern;

    /**
     * Factory instance for creating the correct simulation.
     */
    private final SystemProperties systemProperties;
    /** Subtyping relation, derived from the SystemProperties. */
    private TypeGraph typeGraph;

    /** Node capturing the match count of this condition. */
    private VariableNode countNode;

    /**
     * Flag indicating whether the condition is positive, i.e., cannot be
     * vacuously true.
     */
    private boolean positive;

    /** Constant condition that is always satisfied. */
    static public final Condition True = new Condition("true", Op.TRUE);
    /** Constant condition that is never satisfied. */
    static public final Condition False = new Condition("false", Op.FALSE);
    static {
        try {
            True.setFixed();
            False.setFixed();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Constructs a disjunctive condition for a non-empty list of operands. */
    static public final Condition newOr(Condition... operands) {
        return newCondition(Op.OR, "or", operands);
    }

    /** Constructs a conjunctive condition for a non-empty list of operands. */
    static public final Condition newAnd(Condition... operands) {
        return newCondition(Op.AND, "and", operands);
    }

    /** Constructs a disjunctive condition for a non-empty list of operands. */
    static private final Condition newCondition(Op op, String descr,
            Condition... operands) {
        if (operands.length == 0) {
            throw new IllegalArgumentException(String.format(
                "Can't build '%s' with empty operand list", descr));
        }
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < operands.length; i++) {
            if (i > 0) {
                name.append(' ');
                name.append(descr);
                name.append(' ');
            }
            name.append('(');
            name.append(operands[i].getName());
            name.append(')');
        }
        Condition result = new Condition(name.toString(), op);
        for (Condition oper : operands) {
            result.addSubCondition(oper);
        }
        try {
            result.setFixed();
        } catch (FormatException e) {
            throw new IllegalArgumentException(
                String.format("Error while fixing new condition %s: %s", name,
                    e.getMessage()));
        }
        return result;
    }

    /** 
     * The (top-level) operator of this condition.
     */
    public static enum Op {
        /** Universally quantified pattern. */
        FORALL("Universal", Util.FORALL),
        /** Existentially quantified pattern. */
        EXISTS("Existential", Util.EXISTS),
        /** Negated condition. */
        NOT("Negated", Util.NEG),
        /** Conjunction of subconditions. */
        AND("Conjunctive", Util.WEDGE),
        /** Disjunction of subconditions. */
        OR("Disjunctive", Util.VEE),
        /** Truth. */
        TRUE("True", HTMLConverter.STRONG_TAG.on(Boolean.TRUE)),
        /** Falsehood. */
        FALSE("False", HTMLConverter.STRONG_TAG.on(Boolean.FALSE));

        private Op(String name, char symbol) {
            this(name, "" + symbol);
        }

        private Op(String name, String symbol) {
            this.name = name;
            this.symbol = symbol;
        }

        /** Returns the symbol for this condition operator. */
        public final String getSymbol() {
            return this.symbol;
        }

        /** Returns the name of this condition operator. */
        public final String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return getName();
        }

        /** Indicates if this operator is a quantifier. */
        public boolean isQuantifier() {
            return this == FORALL || this == EXISTS;
        }

        /** 
         * Indicates if this operator has an associated graph pattern.
         * This is the case if it is a quantifier or {@link #NOT}.
         */
        public boolean hasPattern() {
            return isQuantifier() || this == NOT;
        }

        /** 
         * Indicates if this operator may have operands,
         * i.e., sub-conditions (apart from the possible graph pattern). 
         */
        public boolean hasOperands() {
            return isQuantifier() || this == AND || this == OR;
        }

        /** 
         * Indicates if this is a conjunctive operator,
         * meaning that its operands (i.e., subconditions) should all be satisfied.
         */
        public boolean isConjunctive() {
            return this == EXISTS || this == AND;
        }

        private final String name;
        private final String symbol;
    }
}