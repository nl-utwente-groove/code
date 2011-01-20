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
 * $Id: RuleDependencies.java,v 1.19 2008-03-04 10:51:27 rensink Exp $
 */
package groove.trans;

import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.TypeEdge;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.rel.RegAut;
import groove.rel.RegExpr;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Class with utilities to compute dependencies between rules in a graph
 * grammar.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-04 10:51:27 $
 */
public class RuleDependencies {
    /** Label text for merges (merger edges and merge embargoes) */
    static private final String MERGE_LABEL_TEXT = "'node merge";
    /** Label for merges (merger edges and merge embargoes) */
    static private final TypeLabel MERGE_LABEL =
        TypeLabel.createBinaryLabel(MERGE_LABEL_TEXT);
    /** Label text for merges (merger edges and merge embargoes) */
    static private final String ALL_LABEL_TEXT = "'all labels";
    /** Label for merges (merger edges and merge embargoes) */
    static private final TypeLabel ALL_LABEL =
        TypeLabel.createBinaryLabel(ALL_LABEL_TEXT);
    /** Label text indicating an isolated node. */
    static private final String ANY_NODE_TEXT = "'any node";
    /** Label to indicate that a condition or rule contains an isolated node. */
    static private final TypeLabel ANY_NODE =
        TypeLabel.createBinaryLabel(ANY_NODE_TEXT);

    /**
     * Analyzes and prints the dependencies of a given graph grammar.
     */
    public static void main(String[] args) {
        try {
            GraphGrammar grammar = Groove.loadGrammar(args[0]).toGrammar();
            RuleDependencies data = new RuleDependencies(grammar);
            data.collectCharacteristics();
            for (Rule rule : grammar.getRules()) {
                System.out.println("Rule " + rule.getName() + ":");
                System.out.println("Positive labels: "
                    + data.positiveLabelsMap.get(rule));
                System.out.println("Negative labels: "
                    + data.negativeLabelsMap.get(rule));
                System.out.println("Consumed labels: "
                    + data.consumedLabelsMap.get(rule));
                System.out.println("Produced labels: "
                    + data.producedLabelsMap.get(rule));
                System.out.println("Input types:  "
                    + data.inParameterMap.get(rule));
                System.out.println("Output types: "
                    + data.outParameterMap.get(rule));
                Collection<RuleName> enablerNames = new ArrayList<RuleName>();
                for (Rule depRule : data.getEnablers(rule)) {
                    enablerNames.add(depRule.getName());
                }
                Collection<RuleName> disablerNames = new ArrayList<RuleName>();
                for (Rule depRule : data.getDisablers(rule)) {
                    disablerNames.add(depRule.getName());
                }
                Collection<RuleName> enabledNames = new ArrayList<RuleName>();
                for (Rule depRule : data.getEnableds(rule)) {
                    enabledNames.add(depRule.getName());
                }
                Collection<RuleName> disabledNames = new ArrayList<RuleName>();
                for (Rule depRule : data.getDisableds(rule)) {
                    disabledNames.add(depRule.getName());
                }
                // disablerNames.removeAll(enablerNames);
                // disabledNames.removeAll(enabledNames);
                Collection<RuleName> allRuleNames =
                    new ArrayList<RuleName>(grammar.getRuleNames());
                allRuleNames.removeAll(enablerNames);
                allRuleNames.removeAll(disablerNames);
                System.out.println("Enabled rules:  " + enabledNames);
                System.out.println("Disabled rules: " + disabledNames);
                System.out.println("Enablers:       " + enablerNames);
                System.out.println("Disablers:      " + disablerNames);
                System.out.println("No dependency:  " + allRuleNames);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Constructs a new dependencies object, for a given rule system. */
    public RuleDependencies(RuleSystem ruleSystem) {
        this.rules = ruleSystem.getRules();
        this.properties = ruleSystem.getProperties();
        this.labelStore = ruleSystem.getLabelStore();
        this.type = ruleSystem.getType();
    }

    /**
     * Returns a map from the rules in this system to their enablers, i.e.,
     * those rules that it may depend upon positively. A rule depends on another
     * positively if the other may increase the applicability of this one.
     * @return A map from rules to sets of rules that the key rule depends upon
     *         positively.
     */
    Map<Rule,Set<Rule>> getEnablerMap() {
        if (!this.rules.isEmpty() && this.enablerMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.enablerMap);
    }

    /**
     * Returns a map from the rules in this system to their disablers, i.e.,
     * those rules that it may depend upon negatively. A rule depends on another
     * negatively if the other may decrease the applicability of this one.
     * @return A map from rules to sets of rules that the key rule depends upon
     *         negatively.
     */
    Map<Rule,Set<Rule>> getDisablerMap() {
        if (!this.rules.isEmpty() && this.disablerMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.disablerMap);
    }

    /**
     * Returns, for a given rule, the set of rules it enables, i.e., those rules
     * that are <i>increased</i> in applicability.
     * @param rule the rule for which we want to have the enabled rules
     * @return the set of enabled rules for <code>rule</code>
     */
    public Set<Rule> getEnableds(Rule rule) {
        if (!this.rules.isEmpty() && this.enabledMap.isEmpty()) {
            collectCharacteristics();
        }
        return this.enabledMap.get(rule);
    }

    /**
     * Returns, for a given rule, the set of its enablers, i.e., those rules
     * that <i>increase</i> its applicability.
     * @param rule the rule for which we want to have the enablers
     * @return the set of enablers for <code>rule</code>
     */
    public Set<Rule> getEnablers(Rule rule) {
        if (!this.rules.isEmpty() && this.enablerMap.isEmpty()) {
            collectCharacteristics();
        }
        return this.enablerMap.get(rule);
    }

    /**
     * Returns, for a given rule, the set of rules it disables, i.e., those
     * rules that are <i>decreased</i> in applicability.
     * @param rule the rule for which we want to have the disabled rules
     * @return the set of disabled rules for <code>rule</code>
     */
    public Set<Rule> getDisableds(Rule rule) {
        if (!this.rules.isEmpty() && this.disabledMap.isEmpty()) {
            collectCharacteristics();
        }
        return this.disabledMap.get(rule);
    }

    /**
     * Returns, for a given rule, the set of its disablers, i.e., those rules
     * that <i>increase</i> its applicability.
     * @param rule the rule for which we want to have the disablers
     * @return the set of disablers for <code>rule</code>
     */
    public Set<Rule> getDisablers(Rule rule) {
        if (!this.rules.isEmpty() && this.disablerMap.isEmpty()) {
            collectCharacteristics();
        }
        return this.disablerMap.get(rule);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * consumed by those rules.
     */
    Map<Rule,Set<TypeLabel>> getConsumedLabelsMap() {
        if (!this.rules.isEmpty() && this.consumedLabelsMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.consumedLabelsMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * occurring in a negative application condition.
     */
    Map<Rule,Set<TypeLabel>> getNegativeLabelsMap() {
        if (!this.rules.isEmpty() && this.negativeLabelsMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.negativeLabelsMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * occurring in a positive application condition.
     */
    Map<Rule,Set<TypeLabel>> getPositiveLabelsMap() {
        if (!this.rules.isEmpty() && this.positiveLabelsMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.positiveLabelsMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * produced by those rules.
     */
    Map<Rule,Set<TypeLabel>> getProducedLabelsMap() {
        if (!this.rules.isEmpty() && this.producedLabelsMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.producedLabelsMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * produced by those rules.
     */
    Map<Rule,Set<CtrlType>> getOutParameterMap() {
        if (!this.rules.isEmpty() && this.outParameterMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.outParameterMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * produced by those rules.
     */
    Map<Rule,Set<CtrlType>> getInParameterMap() {
        if (!this.rules.isEmpty() && this.inParameterMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.inParameterMap);
    }

    /**
     * Collect the characteristics of the rules in the grammar into relevant
     * maps.
     */
    void collectCharacteristics() {
        for (Rule rule : this.rules) {
            Set<TypeLabel> consumedLabelsSet = new HashSet<TypeLabel>();
            this.consumedLabelsMap.put(rule,
                Collections.unmodifiableSet(consumedLabelsSet));
            Set<TypeLabel> producedLabelsSet = new HashSet<TypeLabel>();
            this.producedLabelsMap.put(rule,
                Collections.unmodifiableSet(producedLabelsSet));
            Set<CtrlType> inParSet = new HashSet<CtrlType>();
            this.inParameterMap.put(rule, Collections.unmodifiableSet(inParSet));
            Set<CtrlType> outParSet = new HashSet<CtrlType>();
            this.outParameterMap.put(rule,
                Collections.unmodifiableSet(outParSet));
            collectRuleCharacteristics(rule, consumedLabelsSet,
                producedLabelsSet, inParSet, outParSet);
            Set<TypeLabel> positiveLabelSet = new HashSet<TypeLabel>();
            this.positiveLabelsMap.put(rule,
                Collections.unmodifiableSet(positiveLabelSet));
            Set<TypeLabel> negativeLabelSet = new HashSet<TypeLabel>();
            this.negativeLabelsMap.put(rule,
                Collections.unmodifiableSet(negativeLabelSet));
            collectConditionCharacteristics(rule, positiveLabelSet,
                negativeLabelSet);
        }
        // initialize the dependency maps
        init(this.enablerMap);
        init(this.disablerMap);
        init(this.enabledMap);
        init(this.disabledMap);
        for (Rule rule : this.rules) {
            Set<TypeLabel> positives = this.positiveLabelsMap.get(rule);
            Set<TypeLabel> negatives = this.negativeLabelsMap.get(rule);
            //            Set<CtrlType> inPars = this.inParameterMap.get(rule);
            for (Rule depRule : this.rules) {
                // a positive dependency exists if the other rule produces
                // labels
                // that this one needs
                Set<Label> depProduces =
                    new HashSet<Label>(this.producedLabelsMap.get(depRule));
                if (positives.contains(ALL_LABEL) && !depProduces.isEmpty()
                    || depProduces.contains(ALL_LABEL) && !positives.isEmpty()
                    || depProduces.removeAll(positives)) {
                    addEnabling(depRule, rule);
                }
                // a positive dependency exists if the other rule consumes
                // labels
                // that this one forbids
                Set<Label> depConsumes =
                    new HashSet<Label>(this.consumedLabelsMap.get(depRule));
                if (negatives.contains(ALL_LABEL) && !depConsumes.isEmpty()
                    || depConsumes.contains(ALL_LABEL) && !negatives.isEmpty()
                    || depConsumes.removeAll(negatives)) {
                    addEnabling(depRule, rule);
                }
                // a positive dependency exists if the other rule has higher
                // priority than this one
                int rulePriority = rule.getPriority();
                int depRulePriority = depRule.getPriority();
                if (rulePriority < depRulePriority) {
                    addEnabling(depRule, rule);
                }
                // a negative dependency exists if the other rule produces
                // labels
                // that this one forbids, or if the other rule contains mergers
                // HARMEN: what is the point with mergers?
                depProduces =
                    new HashSet<Label>(this.producedLabelsMap.get(depRule));
                if (negatives.contains(ALL_LABEL) && !depProduces.isEmpty()
                    || depProduces.contains(MERGE_LABEL)
                    || depProduces.contains(ALL_LABEL) && !negatives.isEmpty()
                    || depProduces.removeAll(negatives)) {
                    addDisabling(depRule, rule);
                }
                // a negative dependency exists if the other rule consumes
                // labels
                // that this one needs
                depConsumes =
                    new HashSet<Label>(this.consumedLabelsMap.get(depRule));
                if (positives.contains(ALL_LABEL) && !depConsumes.isEmpty()
                    || depConsumes.contains(ALL_LABEL) && !positives.isEmpty()
                    || depConsumes.removeAll(positives)) {
                    addDisabling(depRule, rule);
                }
                // a positive and negative dependency exists if the other
                // rule has output parameters of a type of which this rule 
                // has input parameters.
                // switched off again as this is taken care of by 
                // a more careful analysis of the actual rule calls
                //                Set<CtrlType> depOutTypes =
                //                    new HashSet<CtrlType>(this.outParameterMap.get(depRule));
                //                if (depOutTypes.removeAll(inPars)) {
                //                    addEnabling(depRule, rule);
                //                    addDisabling(depRule, rule);
                //                }
            }
        }
    }

    /**
     * s Collects the labels produced and consumed by a given rule. In this
     * implementation, if a rule deletes a node, it is assumed to be able to
     * delete all labels; this is to take dangling edges into account. The
     * method also tests for the production of isolated nodes.
     */
    void collectRuleCharacteristics(Rule rule, Set<TypeLabel> consumed,
            Set<TypeLabel> produced, Set<CtrlType> inPars, Set<CtrlType> outPars) {
        RuleGraph lhs = rule.lhs();
        RuleGraph rhs = rule.rhs();
        RuleGraphMorphism ruleMorphism = rule.getMorphism();
        // test if a node is consumed (and there is no dangling edge check)
        Iterator<RuleNode> lhsNodeIter = lhs.nodeSet().iterator();
        while (lhsNodeIter.hasNext() && !consumed.contains(ALL_LABEL)
            && !this.properties.isCheckDangling()) {
            RuleNode lhsNode = lhsNodeIter.next();
            if (!ruleMorphism.containsNodeKey(lhsNode)) {
                consumed.addAll(getIncidentLabels(lhs, lhsNode));
            }
        }
        // determine the set of edges consumed
        Iterator<RuleEdge> lhsEdgeIter = lhs.edgeSet().iterator();
        while (lhsEdgeIter.hasNext() && !consumed.contains(ALL_LABEL)) {
            RuleEdge lhsEdge = lhsEdgeIter.next();
            if (!ruleMorphism.containsEdgeKey(lhsEdge)) {
                // the only regular expressions allowed on erasers are wildcards
                consumed.addAll(getMatchedLabels(lhsEdge.label()));
            }
        }
        // determine the set of edges produced
        Iterator<RuleEdge> rhsEdgeIter = rhs.edgeSet().iterator();
        while (rhsEdgeIter.hasNext() && !produced.contains(ALL_LABEL)) {
            RuleEdge rhsEdge = rhsEdgeIter.next();
            if (!ruleMorphism.containsEdgeValue(rhsEdge)) {
                produced.add(getSharpLabel(rhsEdge.label()));
            }
        }
        // determine if the rule contains a merger
        if (!ruleMorphism.isInjective()) {
            produced.add(MERGE_LABEL);
            produced.add(ALL_LABEL);
        }
        // determine if the rule introduces an isolated node
        for (RuleNode rhsNode : rhs.nodeSet()) {
            if (!ruleMorphism.containsNodeValue(rhsNode)) { // &&
                // rhs.edgeSet(rhsNode).isEmpty())
                // {
                produced.add(ANY_NODE);
            }
        }
        for (CtrlPar.Var par : rule.getSignature()) {
            CtrlType parType = par.getType();
            if (!par.isInOnly()) {
                outPars.add(parType);
            }
            if (!par.isOutOnly()) {
                inPars.add(parType);
            }
        }
        // now investigate the negative conjunct, taking care to swap positive
        // and negative
        for (Condition negCond : rule.getSubConditions()) {
            for (Condition subRule : negCond.getSubConditions()) {
                if (subRule instanceof Rule) {
                    collectRuleCharacteristics((Rule) subRule, consumed,
                        produced, inPars, outPars);
                }
            }
        }
    }

    void collectConditionCharacteristics(Condition cond,
            Set<TypeLabel> positive, Set<TypeLabel> negative) {
        RuleGraphMorphism pattern = cond.getRootMap();
        RuleGraph target = cond.getTarget();
        // collected the isolated fresh nodes
        Set<RuleNode> isolatedNodes = new HashSet<RuleNode>(target.nodeSet());
        isolatedNodes.removeAll(pattern.nodeMap().values());
        // iterate over the edges that are new in the target
        Set<RuleEdge> freshTargetEdges =
            new HashSet<RuleEdge>(target.edgeSet());
        freshTargetEdges.removeAll(pattern.edgeMap().values());
        for (RuleEdge edge : freshTargetEdges) {
            RuleLabel label = edge.label();
            // don't look at attribute-related edges
            if (!(label.isArgument() || label.isOperator())) {
                // flag indicating that the edge always tests positively
                // for the presence of connecting structure
                boolean presence = true;
                Set<TypeLabel> affectedSet;
                if (label.isNeg()) {
                    label = label.getNegOperand().toLabel();
                    affectedSet = negative;
                    presence = false;
                } else {
                    affectedSet = positive;
                    presence = !label.getMatchExpr().isAcceptsEmptyWord();
                }
                affectedSet.addAll(getMatchedLabels(label));
                if (presence) {
                    isolatedNodes.remove(edge.source());
                    isolatedNodes.remove(edge.target());
                }
            }
        }
        // if the condition pattern is non-injective, it means merging is part
        // of the condition
        if (this.properties.isInjective()
            || pattern.nodeMap().size() > new HashSet<RuleNode>(
                pattern.nodeMap().values()).size()) {
            positive.add(MERGE_LABEL);
        }
        // if there is a dangling edge check, all labels are negative conditions
        if (this.properties.isCheckDangling()) {
            negative.add(ALL_LABEL);
        }
        // does the condition test for an isolated node?
        if (!isolatedNodes.isEmpty()) {
            positive.add(ANY_NODE);
        }
        for (Condition negCond : cond.getSubConditions()) {
            Set<TypeLabel> subPositives = new HashSet<TypeLabel>();
            Set<TypeLabel> subNegatives = new HashSet<TypeLabel>();
            collectConditionCharacteristics(negCond, subPositives, subNegatives);
            if (negCond instanceof PositiveCondition<?> == cond instanceof PositiveCondition<?>
                || negCond instanceof ForallCondition) {
                positive.addAll(subPositives);
                negative.addAll(subNegatives);
            }
            if (negCond instanceof PositiveCondition<?> != cond instanceof PositiveCondition<?>
                || negCond instanceof ForallCondition) {
                negative.addAll(subPositives);
                positive.addAll(subNegatives);
            }
        }
    }

    /**
     * Adds a pair of rules to the enabling relation.
     * @param enabler rule that enables applications of the other
     * @param enabled rule that receives more applications
     */
    void addEnabling(Rule enabler, Rule enabled) {
        add(this.enablerMap, enabled, enabler);
        add(this.enabledMap, enabler, enabled);
    }

    /**
     * Adds a pair of rules to the disabling relation.
     * @param disabler rule that disables applications of the other
     * @param disabled rule that receives fewer applications
     */
    void addDisabling(Rule disabler, Rule disabled) {
        add(this.disablerMap, disabled, disabler);
        add(this.disabledMap, disabler, disabled);
        // if the disabled rule has (universal) subrules, then its
        // events will be {@link CompositeEvents}, meaning that they will
        // claim that they never match on the next state, even if they actually
        // do.
        // In order not to miss events, the disabled rule must be re-enabled as
        // well.
        if (disabled instanceof SPORule && ((SPORule) disabled).hasSubRules()) {
            addEnabling(disabler, disabled);
        }
    }

    /**
     * Initializes a relational map so that all rules are mapped to empty sets.
     */
    void init(Map<Rule,Set<Rule>> map) {
        for (Rule rule : this.rules) {
            map.put(rule, createRuleSet());
        }
    }

    /**
     * Adds a key/value pair to a map that implements a relation.
     */
    <S,T> void add(Map<S,Set<T>> map, S key, T value) {
        Set<T> valueSet = map.get(key);
        // if (valueSet == null) {
        // map.put(key, valueSet = createRuleSet());
        // }
        valueSet.add(value);
    }

    /**
     * Returns the (default) labels that may be matched modulo subtyping by a given
     * condition label - such as a sharp label, wildcard, or other
     * type of regular expression.
     * The label may not wrap {@link RegExpr.Neg}.
     */
    private Set<TypeLabel> getMatchedLabels(RuleLabel label) {
        assert !label.isNeg();
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        if (label.isMatchable()) {
            RegAut labelAut = label.getAutomaton(this.labelStore);
            for (Label autLabel : labelAut.getAlphabet()) {
                result.add((TypeLabel) autLabel);
            }
            if (labelAut.isAcceptsEmptyWord()) {
                result.add(ALL_LABEL);
            }
        }
        return result;
    }

    /**
     * Returns the (default) label that may be precisely matched by a given
     * condition label - such as a default label or wildcard.
     * The label may not wrap {@link RegExpr.Neg}.
     */
    private TypeLabel getSharpLabel(RuleLabel label) {
        assert !label.isNeg();
        TypeLabel result;
        if (label.isWildcard()) {
            result = ALL_LABEL;
        } else {
            assert label.isAtom() || label.isSharp();
            result = label.getTypeLabel();
        }
        return result;
    }

    /**
     * Computes an over-approximation of the labels that will be
     * deleted if a node is deleted from a graph.
     */
    private Set<TypeLabel> getIncidentLabels(RuleGraph graph, RuleNode node) {
        Set<TypeLabel> result;
        if (this.type == null) {
            result = Collections.<TypeLabel>singleton(ALL_LABEL);
        } else {
            result = new HashSet<TypeLabel>();
            // the type labels that can be matched by the node
            Set<TypeLabel> typeLabels = null;
            for (RuleEdge incidentEdge : graph.edgeSet(node)) {
                RuleLabel label = incidentEdge.label();
                if (label.isNodeType() && !label.isWildcard()) {
                    if (label.isSharp()) {
                        typeLabels =
                            Collections.singleton(label.getTypeLabel());
                    } else {
                        // since the node is an eraser, it cannot have
                        // arbitrary regular expressions on incident edges
                        assert label.isAtom();
                        typeLabels =
                            this.labelStore.getSubtypes(label.getTypeLabel());
                    }
                    break;
                }
            }
            // typeLabels could be null if we're on a lower nesting level
            //            assert typeLabels != null : String.format("No type label among %s",
            //                graph.edgeSet(node));
            if (typeLabels != null) {
                for (Label typeLabel : typeLabels) {
                    // find the type node
                    TypeNode typeNode =
                        this.type.labelEdgeSet(typeLabel).iterator().next().source();
                    // now find all incident labels of the type node
                    for (TypeEdge typeEdge : this.type.edgeSet(typeNode)) {
                        result.add(typeEdge.label());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Factory method to create a set of rules.
     */
    protected Set<Rule> createRuleSet() {
        return new HashSet<Rule>();
    }

    /** The set of rules for which the analysis is done. */
    private final Collection<Rule> rules;
    /** The system properties of the rules. */
    private final SystemProperties properties;
    /** Alphabet of the rule system. */
    private final LabelStore labelStore;
    /** Type graph of the rule system. */
    private final TypeGraph type;
    /**
     * Mapping from rules to sets of enablers, i.e., rules that may increase
     * their applicability.
     */
    private final Map<Rule,Set<Rule>> enablerMap =
        new HashMap<Rule,Set<Rule>>();
    /**
     * Mapping from rules to sets of disablers, i.e., rules that may decrease
     * their applicability.
     */
    private final Map<Rule,Set<Rule>> disablerMap =
        new HashMap<Rule,Set<Rule>>();
    /**
     * Mapping from rules to sets of enabled rules, i.e., rules that may be
     * increased in their applicability.
     */
    private final Map<Rule,Set<Rule>> enabledMap =
        new HashMap<Rule,Set<Rule>>();
    /**
     * Mapping from rules to sets of disabled rules, i.e., rules that may be
     * decreased in their applicability.
     */
    private final Map<Rule,Set<Rule>> disabledMap =
        new HashMap<Rule,Set<Rule>>();
    /** Mapping from rules to the sets of labels tested for positively. */
    private final Map<Rule,Set<TypeLabel>> positiveLabelsMap =
        new HashMap<Rule,Set<TypeLabel>>();
    /** Mapping from rules to the sets of labels tested for negatively. */
    private final Map<Rule,Set<TypeLabel>> negativeLabelsMap =
        new HashMap<Rule,Set<TypeLabel>>();
    /** Mapping from rules to the sets of labels consumed by those rules. */
    private final Map<Rule,Set<TypeLabel>> consumedLabelsMap =
        new HashMap<Rule,Set<TypeLabel>>();
    /** Mapping from rules to the sets of labels produced by those rules. */
    private final Map<Rule,Set<TypeLabel>> producedLabelsMap =
        new HashMap<Rule,Set<TypeLabel>>();
    /** Mapping from rules to the sets of labels produced by those rules. */
    private final Map<Rule,Set<CtrlType>> inParameterMap =
        new HashMap<Rule,Set<CtrlType>>();
    /** Mapping from rules to the sets of labels produced by those rules. */
    private final Map<Rule,Set<CtrlType>> outParameterMap =
        new HashMap<Rule,Set<CtrlType>>();
}