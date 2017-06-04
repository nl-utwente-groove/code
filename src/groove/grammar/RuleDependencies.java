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
package groove.grammar;

import static groove.grammar.model.ResourceKind.RULE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import groove.automaton.RegAut;
import groove.grammar.Condition.Op;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.RuleModel;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * Class with utilities to compute dependencies between rules in a graph
 * grammar.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-04 10:51:27 $
 */
public class RuleDependencies {
    /**
     * Analyzes and prints the dependencies of a given graph grammar.
     */
    public static void main(String[] args) {
        try {
            GrammarModel grammar = Groove.loadGrammar(args[0]);
            RuleDependencies data = new RuleDependencies(grammar);
            data.collectCharacteristics();
            List<Rule> rules = getRules(grammar);
            for (Rule rule : rules) {
                System.out.println("Rule " + rule.getQualName() + ":");
                System.out.println("Positive labels: " + data.positiveMap.get(rule));
                System.out.println("Negative labels: " + data.negativeMap.get(rule));
                System.out.println("Consumed labels: " + data.consumedMap.get(rule));
                System.out.println("Produced labels: " + data.producedMap.get(rule));
                Collection<QualName> enablerNames = new ArrayList<>();
                for (Action depRule : data.getEnablers(rule)) {
                    enablerNames.add(depRule.getQualName());
                }
                Collection<QualName> disablerNames = new ArrayList<>();
                for (Action depRule : data.getDisablers(rule)) {
                    disablerNames.add(depRule.getQualName());
                }
                Collection<QualName> enabledNames = new ArrayList<>();
                for (Action depRule : data.getEnableds(rule)) {
                    enabledNames.add(depRule.getQualName());
                }
                Collection<QualName> disabledNames = new ArrayList<>();
                for (Action depRule : data.getDisableds(rule)) {
                    disabledNames.add(depRule.getQualName());
                }
                // disablerNames.removeAll(enablerNames);
                // disabledNames.removeAll(enabledNames);
                Collection<QualName> allRuleNames = new ArrayList<>();
                for (Action otherRule : rules) {
                    allRuleNames.add(otherRule.getQualName());
                }
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
        }
    }

    /** Returns the set of enabled rules that do not have errors. */
    static private List<Rule> getRules(GrammarModel grammar) {
        List<Rule> result = new ArrayList<>();
        // set rules
        for (NamedResourceModel<?> ruleModel : grammar.getResourceSet(RULE)) {
            try {
                // only add the enabled rules
                if (ruleModel.isEnabled()) {
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
    }

    /** Constructs a new dependencies object, for a given rule system. */
    public RuleDependencies(GrammarModel grammar) {
        this.rules = getRules(grammar);
        this.properties = grammar.getProperties();
        this.typeGraph = grammar.getTypeGraph();
    }

    /** Constructs a new dependencies object, for a given rule system. */
    public RuleDependencies(Grammar ruleSystem) {
        this.rules = ruleSystem.getAllRules();
        this.properties = ruleSystem.getProperties();
        this.typeGraph = ruleSystem.getTypeGraph();
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
    Map<Rule,Set<TypeElement>> getConsumedMap() {
        if (!this.rules.isEmpty() && this.consumedMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.consumedMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * occurring in a negative application condition.
     */
    Map<Rule,Set<TypeElement>> getNegativeMap() {
        if (!this.rules.isEmpty() && this.negativeMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.negativeMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * occurring in a positive application condition.
     */
    Map<Rule,Set<TypeElement>> getPositiveMap() {
        if (!this.rules.isEmpty() && this.positiveMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.positiveMap);
    }

    /**
     * Constructs and returns a mapping from rules to the sets of labels
     * produced by those rules.
     */
    Map<Rule,Set<TypeElement>> getProducedElementMap() {
        if (!this.rules.isEmpty() && this.producedMap.isEmpty()) {
            collectCharacteristics();
        }
        return Collections.unmodifiableMap(this.producedMap);
    }

    /**
     * Collect the characteristics of the rules in the grammar into relevant
     * maps.
     */
    void collectCharacteristics() {
        for (Rule rule : this.rules) {
            Set<TypeElement> consumedSet = new HashSet<>();
            this.consumedMap.put(rule, Collections.unmodifiableSet(consumedSet));
            Set<TypeElement> producedSet = new HashSet<>();
            this.producedMap.put(rule, Collections.unmodifiableSet(producedSet));
            collectRuleCharacteristics(rule, consumedSet, producedSet);
            Set<TypeElement> positiveSet = new HashSet<>();
            this.positiveMap.put(rule, Collections.unmodifiableSet(positiveSet));
            Set<TypeElement> negativeSet = new HashSet<>();
            this.negativeMap.put(rule, Collections.unmodifiableSet(negativeSet));
            collectConditionCharacteristics(rule.getCondition(), positiveSet, negativeSet);
        }
        // initialize the dependency maps
        init(this.enablerMap);
        init(this.disablerMap);
        init(this.enabledMap);
        init(this.disabledMap);
        for (Rule rule : this.rules) {
            Set<TypeElement> positives = this.positiveMap.get(rule);
            Set<TypeElement> negatives = this.negativeMap.get(rule);
            boolean hasMatchFilter = rule.getMatchFilter()
                .isPresent();
            for (Rule depRule : this.rules) {
                // Positive as well as negative dependencies exist if this rule has a match filter
                if (hasMatchFilter) {
                    addEnabling(depRule, rule);
                    addDisabling(depRule, rule);
                    continue;
                }
                // a positive dependency exists if the other rule produces
                // labels that this one needs
                Set<TypeElement> depProduces = new HashSet<>(this.producedMap.get(depRule));
                if (depProduces.removeAll(positives)) {
                    addEnabling(depRule, rule);
                }
                // a positive dependency exists if the other rule consumes
                // labels that this one forbids
                Set<TypeElement> depConsumes = new HashSet<>(this.consumedMap.get(depRule));
                if (depConsumes.removeAll(negatives)) {
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
                // labels that this one forbids, or if the other rule contains mergers
                // HARMEN: what is the point with mergers?
                depProduces = new HashSet<>(this.producedMap.get(depRule));
                if (depProduces.removeAll(negatives)) {
                    addDisabling(depRule, rule);
                }
                // a negative dependency exists if the other rule consumes
                // labels that this one needs
                depConsumes = new HashSet<>(this.consumedMap.get(depRule));
                if (depConsumes.removeAll(positives)) {
                    addDisabling(depRule, rule);
                }
            }
        }
    }

    /**
     * s Collects the labels produced and consumed by a given rule. In this
     * implementation, if a rule deletes a node, it is assumed to be able to
     * delete all labels; this is to take dangling edges into account. The
     * method also tests for the production of isolated nodes.
     */
    void collectRuleCharacteristics(Rule rule, Set<TypeElement> consumed,
        Set<TypeElement> produced) {
        RuleGraph lhs = rule.lhs();
        // test if a node is consumed (and there is no dangling edge check)
        for (RuleNode eraserNode : rule.getEraserNodes()) {
            addEraserNode(consumed, eraserNode, lhs);
        }
        // determine the set of edges consumed
        for (RuleEdge eraserEdge : rule.getEraserEdges()) {
            consumed.addAll(getMatchingTypes(eraserEdge));
        }
        // determine if the rule introduces an isolated node
        for (RuleNode creatorNode : rule.getCreatorNodes()) {
            produced.add(creatorNode.getType());
        }
        // determine the set of edges produced
        for (RuleEdge creatorEdge : rule.getCreatorEdges()) {
            produced.addAll(getMatchingTypes(creatorEdge));
        }
        // determine if the rule contains a merger
        for (RuleEdge merger : rule.getLhsMergers()) {
            addMerger(produced, consumed, lhs, merger);
        }
        for (RuleEdge merger : rule.getRhsMergers()) {
            addMerger(produced, consumed, lhs, merger);
        }
        // Recursively investigate the subrules
        for (Rule subRule : rule.getSubRules()) {
            collectRuleCharacteristics(subRule, consumed, produced);
        }
    }

    private void addEraserNode(Set<TypeElement> consumed, RuleNode eraserNode, RuleGraph lhs) {
        TypeNode eraserType = eraserNode.getType();
        Set<TypeNode> sharpEraserTypes = new HashSet<>();
        if (eraserNode.isSharp()) {
            sharpEraserTypes.add(eraserType);
        } else {
            sharpEraserTypes.addAll(this.typeGraph.getSubtypes(eraserType));
        }
        addSharpEraserTypes(consumed, sharpEraserTypes);
        boolean checkDangling = this.properties.isCheckDangling();
        lhs.edgeSet(eraserNode)
            .stream()
            .filter(e -> !checkDangling || e.target() instanceof VariableNode)
            .map(RuleEdge::getMatchingTypes)
            .forEach(ts -> consumed.addAll(ts));
    }

    /**
     * Adds a given set of node types to the
     * of consumed types. If the rule does not check for dangling edges,
     * also adds all potential incident edge types.
     */
    private void addSharpEraserTypes(Set<TypeElement> consumed, Set<TypeNode> nodeTypes) {
        consumed.addAll(nodeTypes);
        boolean checkDangling = this.properties.isCheckDangling();
        Set<TypeNode> superTypes = new HashSet<>();
        for (TypeNode type : nodeTypes) {
            superTypes.addAll(type.getSupertypes());
        }
        Set<TypeEdge> incidentEdgeTypes = new HashSet<>();
        superTypes.stream()
            .flatMap(n -> this.typeGraph.inEdgeSet(n)
                .stream())
            .filter(e -> !checkDangling)
            .forEach(e -> incidentEdgeTypes.add(e));
        superTypes.stream()
            .flatMap(n -> this.typeGraph.outEdgeSet(n)
                .stream())
            .filter(e -> !checkDangling || e.target()
                .isDataType())
            .forEach(e -> incidentEdgeTypes.add(e));
        for (TypeNode superType : superTypes) {
            incidentEdgeTypes.addAll(this.typeGraph.inEdgeSet(superType));
            incidentEdgeTypes.addAll(this.typeGraph.outEdgeSet(superType));
        }
        consumed.addAll(incidentEdgeTypes);
    }

    /**
     * Adds the incident edges of a merged node as well as
     * the node type of the merge target to the produced elements.
     */
    private void addMerger(Set<TypeElement> produced, Set<TypeElement> consumed, RuleGraph lhs,
        RuleEdge merger) {
        addEraserNode(consumed, merger.source(), lhs);
        for (RuleEdge sourceEdge : lhs.edgeSet(merger.source())) {
            Set<TypeElement> types = getMatchingTypes(sourceEdge);
            consumed.addAll(types);
            produced.addAll(types);
        }
    }

    /** Collects the type elements for which a condition tests positively and negatively. */
    void collectConditionCharacteristics(Condition cond, Set<TypeElement> positive,
        Set<TypeElement> negative) {
        if (cond.hasPattern()) {
            collectPatternCharacteristics(cond, positive, negative);
        }
        for (Condition subCond : cond.getSubConditions()) {
            Set<TypeElement> subPositives = new HashSet<>();
            Set<TypeElement> subNegatives = new HashSet<>();
            collectConditionCharacteristics(subCond, subPositives, subNegatives);
            Op subOp = subCond.getOp();
            if (subOp != Op.NOT) {
                positive.addAll(subPositives);
                negative.addAll(subNegatives);
            }
            if (subOp == Op.FORALL || subOp == Op.NOT) {
                negative.addAll(subPositives);
                positive.addAll(subNegatives);
            }
        }
    }

    void collectPatternCharacteristics(Condition cond, Set<TypeElement> positive,
        Set<TypeElement> negative) {
        RuleGraph pattern = cond.getPattern();
        // collected the isolated fresh nodes
        Set<RuleNode> isolatedNodes = new HashSet<>(pattern.nodeSet());
        isolatedNodes.removeAll(cond.getRoot()
            .nodeSet());
        // iterate over the edges that are new in the target
        Set<RuleEdge> freshTargetEdges = new HashSet<>(pattern.edgeSet());
        freshTargetEdges.removeAll(cond.getRoot()
            .edgeSet());
        for (RuleEdge edge : freshTargetEdges) {
            RuleLabel label = edge.label();
            // flag indicating that the edge always tests positively
            // for the presence of connecting structure
            boolean presence = true;
            Set<TypeElement> affectedSet;
            if (label.isNeg()) {
                affectedSet = negative;
                presence = false;
            } else {
                affectedSet = positive;
                presence = !label.getMatchExpr()
                    .isAcceptsEmptyWord();
            }
            affectedSet.addAll(getMatchingTypes(edge));
            if (presence) {
                isolatedNodes.remove(edge.source());
                isolatedNodes.remove(edge.target());
            }
        }
        // if there is a dangling edge check, dangling edge types are negative conditions
        if (this.properties.isCheckDangling() && cond.hasRule()) {
            RuleGraph rhs = cond.getRule()
                .rhs();
            for (RuleNode lhsNode : pattern.nodeSet()) {
                if (!rhs.containsNode(lhsNode)) {
                    Set<TypeEdge> danglingEdges = new HashSet<>();
                    // add all incoming edge types
                    this.typeGraph.inEdgeSet(lhsNode.getType())
                        .stream()
                        .forEach(e -> danglingEdges.add(e));
                    // add all non-attribute outgoing edge types
                    this.typeGraph.outEdgeSet(lhsNode.getType())
                        .stream()
                        .filter(e -> !e.target()
                            .isDataType())
                        .forEach(e -> danglingEdges.add(e));
                    // remove all edges that are explicitly removed
                    pattern.edgeSet(lhsNode)
                        .stream()
                        .map(RuleEdge::getType)
                        .filter(Objects::nonNull)
                        .forEach(e -> danglingEdges.remove(e));
                    negative.addAll(danglingEdges);
                }
            }
        }
        // does the condition test for an isolated node?
        for (RuleNode isolatedNode : isolatedNodes) {
            positive.addAll(isolatedNode.getType()
                .getSubtypes());
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
        // claim that they never match on the next state, even if they
        // actually do.
        // In order not to miss events, the disabled rule must be re-enabled as
        // well.
        // NEWSFLASH: this is no longer true!
        //        if (disabled.hasSubRules()) {
        //            addEnabling(disabler, disabled);
        //        }
    }

    /**
     * Initialises a relational map so that all rules are mapped to empty sets.
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
     * Returns the type elements that may be matched modulo subtyping by a given
     * rule edge.
     * The label may not wrap {@link groove.automaton.RegExpr.Neg}.
     */
    private Set<TypeElement> getMatchingTypes(RuleEdge edge) {
        Set<TypeElement> result = new HashSet<>();
        TypeEdge edgeType = edge.getType();
        if (edgeType == null) {
            RuleLabel label = edge.label();
            if (label.isNeg()) {
                label = label.getNegOperand()
                    .toLabel();
            }
            RegAut labelAut = label.getAutomaton(this.typeGraph);
            result.addAll(labelAut.getAlphabet());
            if (labelAut.isAcceptsEmptyWord()) {
                result.addAll(this.typeGraph.nodeSet());
            }
        } else {
            result.addAll(this.typeGraph.getSubtypes(edgeType));
        }
        return result;
    }

    /**
     * Factory method to create a set of rules.
     */
    protected Set<Rule> createRuleSet() {
        return new HashSet<>();
    }

    /** The set of rules for which the analysis is done. */
    private final Collection<Rule> rules;
    /** The system properties of the rules. */
    private final GrammarProperties properties;
    /** Alphabet of the rule system. */
    private final TypeGraph typeGraph;
    /**
     * Mapping from rules to sets of enablers, i.e., rules that may increase
     * their applicability.
     */
    private final Map<Rule,Set<Rule>> enablerMap = new HashMap<>();
    /**
     * Mapping from rules to sets of disablers, i.e., rules that may decrease
     * their applicability.
     */
    private final Map<Rule,Set<Rule>> disablerMap = new HashMap<>();
    /**
     * Mapping from rules to sets of enabled rules, i.e., rules that may be
     * increased in their applicability.
     */
    private final Map<Rule,Set<Rule>> enabledMap = new HashMap<>();
    /**
     * Mapping from rules to sets of disabled rules, i.e., rules that may be
     * decreased in their applicability.
     */
    private final Map<Rule,Set<Rule>> disabledMap = new HashMap<>();
    /** Mapping from rules to the sets of labels tested for positively. */
    private final Map<Rule,Set<TypeElement>> positiveMap = new HashMap<>();
    /** Mapping from rules to the sets of labels tested for negatively. */
    private final Map<Rule,Set<TypeElement>> negativeMap = new HashMap<>();
    /** Mapping from rules to the sets of labels consumed by those rules. */
    private final Map<Rule,Set<TypeElement>> consumedMap = new HashMap<>();
    /** Mapping from rules to the sets of labels produced by those rules. */
    private final Map<Rule,Set<TypeElement>> producedMap = new HashMap<>();
}