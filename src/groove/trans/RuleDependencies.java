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

import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.rel.RegAut;
import groove.trans.Condition.Op;
import groove.util.Groove;
import groove.view.GrammarModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            GrammarModel grammar =
                Groove.loadGrammar(args[0], args.length == 1 ? null : args[1]);
            RuleDependencies data = new RuleDependencies(grammar);
            data.collectCharacteristics();
            for (Rule rule : grammar.getRules()) {
                System.out.println("Rule " + rule.getName() + ":");
                System.out.println("Positive labels: "
                    + data.positiveMap.get(rule));
                System.out.println("Negative labels: "
                    + data.negativeMap.get(rule));
                System.out.println("Consumed labels: "
                    + data.consumedMap.get(rule));
                System.out.println("Produced labels: "
                    + data.producedMap.get(rule));
                Collection<String> enablerNames = new ArrayList<String>();
                for (Rule depRule : data.getEnablers(rule)) {
                    enablerNames.add(depRule.getName());
                }
                Collection<String> disablerNames = new ArrayList<String>();
                for (Rule depRule : data.getDisablers(rule)) {
                    disablerNames.add(depRule.getName());
                }
                Collection<String> enabledNames = new ArrayList<String>();
                for (Rule depRule : data.getEnableds(rule)) {
                    enabledNames.add(depRule.getName());
                }
                Collection<String> disabledNames = new ArrayList<String>();
                for (Rule depRule : data.getDisableds(rule)) {
                    disabledNames.add(depRule.getName());
                }
                // disablerNames.removeAll(enablerNames);
                // disabledNames.removeAll(enabledNames);
                Collection<String> allRuleNames = new ArrayList<String>();
                for (Rule otherRule : grammar.getRules()) {
                    allRuleNames.add(otherRule.getName());
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

    /** Constructs a new dependencies object, for a given rule system. */
    public RuleDependencies(GrammarModel ruleSystem) {
        this.rules = ruleSystem.getRules();
        this.properties = ruleSystem.getProperties();
        this.typeGraph = ruleSystem.getTypeGraph();
    }

    /** Constructs a new dependencies object, for a given rule system. */
    public RuleDependencies(GraphGrammar ruleSystem) {
        this.rules = ruleSystem.getRules();
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
            Set<TypeElement> consumedSet = new HashSet<TypeElement>();
            this.consumedMap.put(rule, Collections.unmodifiableSet(consumedSet));
            Set<TypeElement> producedSet = new HashSet<TypeElement>();
            this.producedMap.put(rule, Collections.unmodifiableSet(producedSet));
            collectRuleCharacteristics(rule, consumedSet, producedSet);
            Set<TypeElement> positiveSet = new HashSet<TypeElement>();
            this.positiveMap.put(rule, Collections.unmodifiableSet(positiveSet));
            Set<TypeElement> negativeSet = new HashSet<TypeElement>();
            this.negativeMap.put(rule, Collections.unmodifiableSet(negativeSet));
            collectConditionCharacteristics(rule.getCondition(), positiveSet,
                negativeSet);
        }
        // initialize the dependency maps
        init(this.enablerMap);
        init(this.disablerMap);
        init(this.enabledMap);
        init(this.disabledMap);
        for (Rule rule : this.rules) {
            Set<TypeElement> positives = this.positiveMap.get(rule);
            Set<TypeElement> negatives = this.negativeMap.get(rule);
            //            Set<CtrlType> inPars = this.inParameterMap.get(rule);
            for (Rule depRule : this.rules) {
                // a positive dependency exists if the other rule produces
                // labels
                // that this one needs
                Set<TypeElement> depProduces =
                    new HashSet<TypeElement>(this.producedMap.get(depRule));
                if (depProduces.removeAll(positives)) {
                    addEnabling(depRule, rule);
                }
                // a positive dependency exists if the other rule consumes
                // labels
                // that this one forbids
                Set<TypeElement> depConsumes =
                    new HashSet<TypeElement>(this.consumedMap.get(depRule));
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
                // labels
                // that this one forbids, or if the other rule contains mergers
                // HARMEN: what is the point with mergers?
                depProduces =
                    new HashSet<TypeElement>(this.producedMap.get(depRule));
                if (depProduces.removeAll(negatives)) {
                    addDisabling(depRule, rule);
                }
                // a negative dependency exists if the other rule consumes
                // labels
                // that this one needs
                depConsumes =
                    new HashSet<TypeElement>(this.consumedMap.get(depRule));
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

    private void addEraserNode(Set<TypeElement> consumed, RuleNode eraserNode,
            RuleGraph lhs) {
        TypeNode eraserType = eraserNode.getType();
        if (eraserNode.isSharp()) {
            addSharpEraserNode(consumed, eraserType);
        } else {
            for (TypeNode subtype : this.typeGraph.getSubtypes(eraserType)) {
                addSharpEraserNode(consumed, subtype);
            }
        }
        if (this.properties.isCheckDangling()) {
            // the incident edges of eraser nodes are not eraser edges,
            // so we have to add them explicitly to the consumed edges
            for (RuleEdge edge : lhs.edgeSet(eraserNode)) {
                consumed.addAll(getMatchingTypes(edge));
            }
        }
    }

    private void addSharpEraserNode(Set<TypeElement> consumed,
            TypeNode eraserType) {
        consumed.add(eraserType);
        if (!this.properties.isCheckDangling()) {
            consumed.addAll(this.typeGraph.edgeSet(eraserType));
        }
    }

    /** 
     * Adds the incident edges of a merged node as well as
     * the node type of the merge target to the produced elements.
     */
    private void addMerger(Set<TypeElement> produced,
            Set<TypeElement> consumed, RuleGraph lhs, RuleEdge merger) {
        addEraserNode(consumed, merger.source(), lhs);
        for (RuleEdge sourceEdge : lhs.edgeSet(merger.source())) {
            Set<TypeElement> types = getMatchingTypes(sourceEdge);
            consumed.addAll(types);
            produced.addAll(types);
        }
    }

    void collectConditionCharacteristics(Condition cond,
            Set<TypeElement> positive, Set<TypeElement> negative) {
        if (cond.hasPattern()) {
            collectPatternCharacteristics(cond, positive, negative);
        }
        for (Condition subCond : cond.getSubConditions()) {
            Set<TypeElement> subPositives = new HashSet<TypeElement>();
            Set<TypeElement> subNegatives = new HashSet<TypeElement>();
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

    void collectPatternCharacteristics(Condition cond,
            Set<TypeElement> positive, Set<TypeElement> negative) {
        RuleGraph pattern = cond.getPattern();
        // collected the isolated fresh nodes
        Set<RuleNode> isolatedNodes = new HashSet<RuleNode>(pattern.nodeSet());
        isolatedNodes.removeAll(cond.getRoot().nodeSet());
        // iterate over the edges that are new in the target
        Set<RuleEdge> freshTargetEdges =
            new HashSet<RuleEdge>(pattern.edgeSet());
        freshTargetEdges.removeAll(cond.getRoot().edgeSet());
        for (RuleEdge edge : freshTargetEdges) {
            RuleLabel label = edge.label();
            // don't look at attribute-related edges
            if (!(label.isArgument() || label.isOperator())) {
                // flag indicating that the edge always tests positively
                // for the presence of connecting structure
                boolean presence = true;
                Set<TypeElement> affectedSet;
                if (label.isNeg()) {
                    affectedSet = negative;
                    presence = false;
                } else {
                    affectedSet = positive;
                    presence = !label.getMatchExpr().isAcceptsEmptyWord();
                }
                affectedSet.addAll(getMatchingTypes(edge));
                if (presence) {
                    isolatedNodes.remove(edge.source());
                    isolatedNodes.remove(edge.target());
                }
            }
        }
        // if there is a dangling edge check, dangling edge types are negative conditions
        if (this.properties.isCheckDangling() && cond.hasRule()) {
            RuleGraph rhs = cond.getRule().rhs();
            for (RuleNode lhsNode : pattern.nodeSet()) {
                if (!rhs.containsNode(lhsNode)) {
                    Set<TypeEdge> danglingEdges =
                        new HashSet<TypeEdge>(
                            this.typeGraph.edgeSet(lhsNode.getType()));
                    for (RuleEdge rhsEdge : pattern.edgeSet(lhsNode)) {
                        TypeEdge edgeType = rhsEdge.getType();
                        if (edgeType != null) {
                            danglingEdges.remove(edgeType);
                        }
                    }
                    negative.addAll(danglingEdges);
                }
            }
        }
        // does the condition test for an isolated node?
        for (RuleNode isolatedNode : isolatedNodes) {
            positive.add(isolatedNode.getType());
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
     * The label may not wrap {@link groove.rel.RegExpr.Neg}.
     */
    private Set<TypeElement> getMatchingTypes(RuleEdge edge) {
        Set<TypeElement> result = new HashSet<TypeElement>();
        TypeEdge edgeType = edge.getType();
        if (edgeType == null) {
            RuleLabel label = edge.label();
            if (label.isNeg()) {
                label = label.getNegOperand().toLabel();
            }
            if (label.isMatchable()) {
                RegAut labelAut = label.getAutomaton(this.typeGraph);
                result.addAll(labelAut.getAlphabet());
                if (labelAut.isAcceptsEmptyWord()) {
                    result.addAll(this.typeGraph.nodeSet());
                }
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
        return new HashSet<Rule>();
    }

    /** The set of rules for which the analysis is done. */
    private final Collection<Rule> rules;
    /** The system properties of the rules. */
    private final SystemProperties properties;
    /** Alphabet of the rule system. */
    private final TypeGraph typeGraph;
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
    private final Map<Rule,Set<TypeElement>> positiveMap =
        new HashMap<Rule,Set<TypeElement>>();
    /** Mapping from rules to the sets of labels tested for negatively. */
    private final Map<Rule,Set<TypeElement>> negativeMap =
        new HashMap<Rule,Set<TypeElement>>();
    /** Mapping from rules to the sets of labels consumed by those rules. */
    private final Map<Rule,Set<TypeElement>> consumedMap =
        new HashMap<Rule,Set<TypeElement>>();
    /** Mapping from rules to the sets of labels produced by those rules. */
    private final Map<Rule,Set<TypeElement>> producedMap =
        new HashMap<Rule,Set<TypeElement>>();
}