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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Condition.Op;
import nl.utwente.groove.grammar.EdgeEmbargo;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.rule.DefaultRuleNode;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RegExpr;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleElement;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.match.automaton.RegAutCalculator;
import nl.utwente.groove.match.automaton.RegAutCoverage;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * The pass that assembles the typed level patterns of a rule into the tree
 * of {@link Condition}s and {@link Rule}s: cross-level eraser-conflict
 * import, per-level condition and rule construction with NACs and
 * embargoes, condition-tree wiring, and the regular-expression erasure
 * check.
 * @author Arend Rensink
 * @version $Revision$
 */
class ConditionAssembler {
    /** Constructs an assembler for a given rule compiler.
     * @param compiler the compiler providing the compilation context
     */
    ConditionAssembler(RuleCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Assembles the tree of conditions and rules from the typed level
     * patterns. Problems are reported in the given error set; the result is
     * the top-level rule, or {@code null} if it could not be constructed.
     */
    Rule assemble(SortedMap<Index,LevelPattern> patternMap, FormatErrorSet errors) {
        Rule result;
        // store the derived subrules in order
        TreeMap<Index,Condition> conditionTree = new TreeMap<>();
        // import cross-level eraser-conflict elements (root extension);
        // this must happen top-down and before any condition is built.
        // The identification condition applies only under DPO semantics;
        // under SPO (simple graphs or multigraphs alike), identifications
        // are resolved by letting deletion win
        if (getGrammarProperties().getParallelMode().isDPO()) {
            for (LevelPattern level : patternMap.values()) {
                importEraserConflicts(level);
            }
        }
        // construct the rule tree and add parent rules
        try {
            for (LevelPattern level : patternMap.values()) {
                Index index = level.getIndex();
                Op operator = index.getOperator();
                Condition condition;
                if (operator.isQuantifier()) {
                    condition = computeFlatRule(level);
                } else {
                    condition = new Condition(index.getName(), operator);
                }
                conditionTree.put(index, condition);
                var rule = condition.getRule();
                if (rule != null && !index.isTopLevel()) {
                    // look for the first parent rule
                    Index parentIndex = index.getParent();
                    var parentLevelCond = conditionTree.get(parentIndex);
                    assert parentLevelCond != null; // all indices are mapped to conditions
                    var parentRule = parentLevelCond.getRule();
                    while (parentRule == null) {
                        parentIndex = parentIndex.getParent();
                        parentLevelCond = conditionTree.get(parentIndex);
                        assert parentLevelCond != null; // all indices are mapped to conditions
                        parentRule = parentLevelCond.getRule();
                    }
                    rule.setParent(parentRule, index.getIntArray());
                }
            }
            // now add subconditions and fix the conditions
            // this needs to be done bottom-up
            for (Map.Entry<Index,Condition> entry : conditionTree.descendingMap().entrySet()) {
                Condition condition = entry.getValue();
                assert condition != null;
                Index index = entry.getKey();
                if (!index.isTopLevel()) {
                    condition.setFixed();
                    Condition parentCond = conditionTree.get(index.getParent());
                    assert parentCond != null; // all indices are mapped to conditions
                    parentCond.addSubCondition(condition);
                }
            }
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        checkRegExprErasure(patternMap, errors);
        // due to errors in the above, it might be that the
        // rule tree is empty, in which case we shouldn't proceed
        if (conditionTree.isEmpty()) {
            result = null;
        } else {
            result = conditionTree.firstEntry().getValue().getRule();
        }
        return result;
    }

    /** Convenience method to retrieve the qualified rule name from the compiler. */
    private QualName getQualName() {
        return this.compiler.getQualName();
    }

    /** Convenience method to retrieve the grammar properties from the compiler. */
    private GrammarProperties getGrammarProperties() {
        return this.compiler.getGrammarProperties();
    }

    /** Convenience method to retrieve the type graph from the compiler. */
    private TypeGraph getTypeGraph() {
        return this.compiler.getTypeGraph();
    }

    /** Convenience method to test for injective matching. */
    private boolean isInjective() {
        return this.compiler.isInjective();
    }

    /** The compiler providing the compilation context. */
    private final RuleCompiler compiler;

    /**
     * Checks, for grammars with parallel edges, that no composite regular
     * expression edge can match a path through an edge that the rule erases.
     * Composite regular expressions (those without a single host edge image)
     * have untracked path witnesses, so the identification condition on
     * erasers cannot be enforced for them at match time; rather than
     * silently transforming away such witnesses, potential overlaps are
     * reported as errors, unless the ignoreRegExp grammar property is set.
     * The check spans the entire quantification tree in both directions,
     * since amalgamation lets erasers at any level destroy witnesses matched
     * at any other level. The traversable edge types of a regular expression
     * are computed positionally, by {@link RegAutCoverage}. Eraser nodes
     * contribute nothing: DPO semantics implies the dangling-edge condition,
     * so node deletion can never erase unmatched edges. Negated expressions
     * are exempt, as erasure cannot invalidate an established negative
     * condition; the empty expression traverses nothing.
     */
    private void checkRegExprErasure(SortedMap<Index,LevelPattern> patternMap,
                                     FormatErrorSet errors) {
        var properties = getGrammarProperties();
        if (!properties.getParallelMode().isDPO() || properties.isIgnoreRegExp()) {
            return;
        }
        // collect the possibly erased edge types over all levels,
        // with a witnessing eraser element for error reporting
        Map<TypeEdge,RuleElement> erasedTypes = new LinkedHashMap<>();
        var typeGraph = getTypeGraph();
        for (LevelPattern level : patternMap.values()) {
            if (!level.getIndex().getOperator().isQuantifier()) {
                continue;
            }
            Set<RuleEdge> eraserEdges = new LinkedHashSet<>(level.lhs.edgeSet());
            eraserEdges.removeAll(level.rhs.edgeSet());
            for (RuleEdge eraser : eraserEdges) {
                for (TypeEdge type : eraser.getMatchingTypes()) {
                    erasedTypes.putIfAbsent(type, eraser);
                }
            }
        }
        if (erasedTypes.isEmpty()) {
            return;
        }
        // check the composite regular expression edges of all levels
        Set<RuleEdge> checked = new HashSet<>();
        for (LevelPattern level : patternMap.values()) {
            if (!level.getIndex().getOperator().isQuantifier()) {
                continue;
            }
            for (RuleEdge edge : level.lhs.edgeSet()) {
                RuleLabel label = edge.label();
                if (edge.hasEdgeImage() || label.isEmpty() || label.isNeg()) {
                    continue;
                }
                // a lone node type atom is not a path: its witness is the
                // (tracked) node itself, not an edge traversal
                if (label.getRole() == EdgeRole.NODE_TYPE) {
                    continue;
                }
                if (!checked.add(edge)) {
                    // root edges are shared between levels; check them once
                    continue;
                }
                var labelAut = RegAutCalculator.instance().compute(label.getMatchExpr(), typeGraph);
                var coverage = new RegAutCoverage(labelAut, edge.source().getMatchingTypes(),
                    edge.target().getMatchingTypes());
                for (var erasedEntry : erasedTypes.entrySet()) {
                    if (coverage.result().contains(erasedEntry.getKey())) {
                        errors
                            .add("Regular expression %s may match a path through a %s-edge "
                                + "erased by this rule (set the ignoreRegExp grammar property "
                                + "to accept this)", label, erasedEntry.getKey().label(), edge,
                                 erasedEntry.getValue());
                        break;
                    }
                }
            }
        }
    }
    /**
     * Imports ancestor-level elements whose image may coincide with that
     * of an eraser at this or the ancestor level (root extension): such
     * an element is added, as a reader, to the LHS and RHS of every level
     * from just below its own down to this one, so that its image is
     * seeded into the search of this level's condition, where the
     * conflict machinery enforces the cross-level DPO identification
     * condition. Imported ancestor erasers are additionally recorded, to
     * take part in the conflict computation as erasers.
     * Must be called top-down over the level tree, before any condition
     * is built.
     */
    private void importEraserConflicts(LevelPattern level) {
        // snapshots of this level's own elements, before any imports
        List<RuleNode> myNodes = new ArrayList<>(level.lhs.nodeSet());
        List<RuleEdge> myEdges = new ArrayList<>(level.lhs.edgeSet());
        Set<RuleNode> myEraserNodes = new LinkedHashSet<>(myNodes);
        myEraserNodes.removeAll(level.rhs.nodeSet());
        Set<RuleEdge> myEraserEdges = new LinkedHashSet<>(myEdges);
        myEraserEdges.removeAll(level.rhs.edgeSet());
        // the levels from just below the currently inspected ancestor
        // down to this level, into which conflicting elements are imported
        List<LevelPattern> path = new ArrayList<>();
        path.add(level);
        for (LevelPattern anc = level.parent; anc != null; anc = anc.parent) {
            Set<RuleEdge> ancEraserEdges = new LinkedHashSet<>(anc.lhs.edgeSet());
            ancEraserEdges.removeAll(anc.rhs.edgeSet());
            for (RuleEdge ancEdge : anc.lhs.edgeSet()) {
                boolean ancIsEraser = ancEraserEdges.contains(ancEdge);
                // a non-eraser ancestor edge only conflicts with my erasers
                Collection<RuleEdge> mine = ancIsEraser
                    ? myEdges
                    : myEraserEdges;
                boolean conflict = false;
                for (RuleEdge myEdge : mine) {
                    if (myEdge != ancEdge && myEdge.canShareImage(ancEdge)) {
                        conflict = true;
                        break;
                    }
                }
                if (conflict) {
                    importEdge(ancEdge, path);
                    if (ancIsEraser) {
                        this.ancestorEraserEdgeMap
                            .computeIfAbsent(level.getIndex(), i -> new LinkedHashSet<>())
                            .add(ancEdge);
                    }
                }
            }
            Set<RuleNode> ancEraserNodes = new LinkedHashSet<>(anc.lhs.nodeSet());
            ancEraserNodes.removeAll(anc.rhs.nodeSet());
            for (RuleNode ancNode : anc.lhs.nodeSet()) {
                if (!(ancNode instanceof DefaultRuleNode)) {
                    continue;
                }
                boolean ancIsEraser = ancEraserNodes.contains(ancNode);
                Collection<RuleNode> mine = ancIsEraser
                    ? myNodes
                    : myEraserNodes;
                boolean conflict = false;
                for (RuleNode myNode : mine) {
                    if (myNode != ancNode && myNode instanceof DefaultRuleNode
                        && !Collections
                            .disjoint(myNode.getMatchingTypes(), ancNode.getMatchingTypes())) {
                        conflict = true;
                        break;
                    }
                }
                if (conflict) {
                    importNode(ancNode, path);
                    if (ancIsEraser) {
                        this.ancestorEraserNodeMap
                            .computeIfAbsent(level.getIndex(), i -> new LinkedHashSet<>())
                            .add(ancNode);
                    }
                }
            }
            path.add(anc);
        }
    }

    /** Adds an ancestor edge, with its end nodes, as reader to the given levels. */
    private void importEdge(RuleEdge edge, List<LevelPattern> levels) {
        for (LevelPattern level : levels) {
            if (!level.lhs.containsEdge(edge)) {
                level.lhs.addEdgeContext(edge);
            }
            if (!level.rhs.containsEdge(edge)) {
                level.rhs.addEdgeContext(edge);
            }
        }
    }

    /** Adds an ancestor node as reader to the given levels. */
    private void importNode(RuleNode node, List<LevelPattern> levels) {
        for (LevelPattern level : levels) {
            level.lhs.addNode(node);
            level.rhs.addNode(node);
        }
    }

    /** Returns the ancestor-level eraser edges imported into a given level's pattern. */
    private Set<RuleEdge> getAncestorEraserEdges(LevelPattern level) {
        return this.ancestorEraserEdgeMap.getOrDefault(level.getIndex(), Collections.emptySet());
    }

    /** Returns the ancestor-level eraser nodes imported into a given level's pattern. */
    private Set<RuleNode> getAncestorEraserNodes(LevelPattern level) {
        return this.ancestorEraserNodeMap.getOrDefault(level.getIndex(), Collections.emptySet());
    }

    /** Ancestor-level eraser edges imported by {@link #importEraserConflicts},
     * keyed by level index. */
    private final Map<Index,Set<RuleEdge>> ancestorEraserEdgeMap = new TreeMap<>();
    /** Ancestor-level eraser nodes imported by {@link #importEraserConflicts},
     * keyed by level index. */
    private final Map<Index,Set<RuleNode>> ancestorEraserNodeMap = new TreeMap<>();

    /**
     * Callback method to compute the rule on a given nesting level.
     * The resulting condition is not fixed (see {@link Condition#isFixed()}).
     */
    private Condition computeFlatRule(LevelPattern level) throws FormatException {
        Condition result;
        FormatErrorSet errors = new FormatErrorSet();
        // the resulting rule
        result = createCondition(level, getRootGraph(level), level.lhs);
        result.addAncestorEraserEdges(getAncestorEraserEdges(level));
        if (level.isRule) {
            Rule rule = createRule(result, level.rhs, getCoRootGraph(level));
            rule.addColorMap(level.colorMap);
            result.setRule(rule);
        }
        // add the NACs to the rule
        for (RuleGraph nac : level.nacs) {
            try {
                result.addSubCondition(computeNac(level, level.lhs, nac));
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        addEraserNodeEmbargoes(level, result);
        errors.throwException();
        return result;
    }

    /**
     * Adds merge embargoes to the level condition for every pair of a
     * deleted node and another type-compatible LHS node, enforcing the
     * DPO identification condition on nodes: if a deleted node is
     * identified with any other matched node, the pushout complement is
     * not unique. Deleted nodes are the eraser nodes of this level plus
     * the imported ancestor-level eraser nodes; for the latter, pairs
     * with other nodes shared with the parent level are skipped, as they
     * are already checked at the ancestor level where both nodes first
     * coexist. The identification condition applies only under DPO
     * semantics; under SPO (simple graphs or multigraphs alike),
     * identifications are resolved by letting deletion win. Also skipped
     * under injective matching, which subsumes the condition; the
     * generated embargoes compile to equality tests in the search plan.
     */
    private void addEraserNodeEmbargoes(LevelPattern level,
                                        Condition condition) throws FormatException {
        if (!getGrammarProperties().getParallelMode().isDPO() || isInjective()) {
            return;
        }
        Set<RuleNode> erasers = new LinkedHashSet<>(level.lhs.nodeSet());
        erasers.removeAll(level.rhs.nodeSet());
        Set<RuleNode> ancestorErasers = getAncestorEraserNodes(level);
        if (erasers.isEmpty() && ancestorErasers.isEmpty()) {
            return;
        }
        RuleLabel equality = new RuleLabel(RegExpr.empty());
        List<RuleNode> nodes = new ArrayList<>(level.lhs.nodeSet());
        for (int i = 0; i < nodes.size(); i++) {
            RuleNode one = nodes.get(i);
            if (!(one instanceof DefaultRuleNode)) {
                continue;
            }
            for (int j = i + 1; j < nodes.size(); j++) {
                RuleNode two = nodes.get(j);
                if (!(two instanceof DefaultRuleNode)) {
                    continue;
                }
                boolean needed = erasers.contains(one) || erasers.contains(two);
                if (!needed) {
                    // pairs of nodes shared with the parent level are
                    // checked at the ancestor level where both first coexist
                    needed = ancestorErasers.contains(one) && !inParentLhs(level, two)
                        || ancestorErasers.contains(two) && !inParentLhs(level, one);
                }
                if (!needed) {
                    continue;
                }
                if (Collections.disjoint(one.getMatchingTypes(), two.getMatchingTypes())) {
                    continue;
                }
                RuleEdge embargoEdge = level.lhs.getFactory().createEdge(one, equality, two);
                EdgeEmbargo embargo = createEdgeEmbargo(level.lhs, embargoEdge);
                embargo.setFixed();
                condition.addSubCondition(embargo);
            }
        }
    }

    /** Tests if a given node occurs in the parent level's LHS. */
    private boolean inParentLhs(LevelPattern level, RuleNode node) {
        var parent = level.parent;
        return parent != null && parent.lhs.containsNode(node);
    }

    /**
     * Returns the mapping from the LHS rule elements at the parent level to
     * the LHS rule elements at a given level.
     */
    private RuleGraph getRootGraph(LevelPattern level) {
        if (level.getIndex().isTopLevel()) {
            return null;
        }
        var parent = level.parent;
        assert parent != null; // the pattern tree is congruent to the index tree
        return getIntersection(level, parent.lhs, level.lhs);
    }

    /**
     * Returns the intersection of the parent RHS and a given level's RHS
     */
    private RuleGraph getCoRootGraph(LevelPattern level) {
        // find the first parent that has a rule
        LevelPattern parent = level.parent;
        while (parent != null && !parent.isRule) {
            parent = parent.parent;
        }
        return parent == null
            ? null
            : getIntersection(level, parent.rhs, level.rhs);
    }

    /**
     * Returns a rule graph that forms the intersection of the rule elements
     * of a given level and its parent level.
     */
    private RuleGraph getIntersection(LevelPattern level, RuleGraph parentGraph,
                                      RuleGraph myGraph) {
        RuleGraph result
            = parentGraph.newGraph(getQualName() + "-" + level.getIndex() + "-root");
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
    private Condition computeNac(LevelPattern level, RuleGraph lhs,
                                 RuleGraph nac) throws FormatException {
        Condition result = null;
        // first check for merge end edge embargoes
        // they are characterised by the fact that there is precisely 1
        // element
        // in the nacElemSet, which is an edge
        if (nac.edgeCount() == 1) {
            RuleEdge embargoEdge = nac.edgeSet().iterator().next();
            Set<RuleNode> ends
                = new HashSet<>(Arrays.asList(embargoEdge.source(), embargoEdge.target()));
            if (nac.nodeSet().equals(ends) && lhs.nodeSet().containsAll(ends)
                && nac.varSet().isEmpty()) {
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
            result = createNAC(level, lhs, nac);
        }
        result.setFixed();
        return result;
    }

    /**
     * Callback method to create an edge embargo.
     * @param context the context-graph
     * @param embargoEdge the edge to be turned into an embargo
     * @return the new {@link nl.utwente.groove.grammar.EdgeEmbargo}
     * @see RuleModel#toResource()
     */
    private EdgeEmbargo createEdgeEmbargo(RuleGraph context, RuleEdge embargoEdge) {
        return new EdgeEmbargo(context, embargoEdge, getGrammarProperties());
    }

    /**
     * Callback method to create a general NAC on a given graph.
     * @param nac the context-graph
     * @return the new {@link nl.utwente.groove.grammar.Condition}
     * @see RuleModel#toResource()
     */
    private Condition createNAC(LevelPattern level, RuleGraph lhs, RuleGraph nac) {
        String name = nac.getName();
        return new Condition(name, Condition.Op.NOT, nac, getIntersection(level, lhs, nac),
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
    private Condition createCondition(LevelPattern level, RuleGraph root, RuleGraph pattern) {
        Condition result = new Condition(level.getIndex().getName(),
            level.getIndex().getOperator(), pattern, root, getGrammarProperties());
        result.setTypeGraph(getTypeGraph());
        if (level.getIndex().isPositive()) {
            result.setPositive();
        }
        var countNode = level.countNode;
        if (countNode != null) {
            result.setCountNode(countNode);
        }
        result.addOutputNodes(level.outputNodes);
        return result;
    }
}
