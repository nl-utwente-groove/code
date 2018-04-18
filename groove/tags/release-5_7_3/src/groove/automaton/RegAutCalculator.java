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
package groove.automaton;

import groove.automaton.RegExpr.Atom;
import groove.automaton.RegExpr.Choice;
import groove.automaton.RegExpr.Empty;
import groove.automaton.RegExpr.Inv;
import groove.automaton.RegExpr.Neg;
import groove.automaton.RegExpr.Plus;
import groove.automaton.RegExpr.Seq;
import groove.automaton.RegExpr.Sharp;
import groove.automaton.RegExpr.Star;
import groove.automaton.RegExpr.Wildcard;
import groove.grammar.rule.RuleLabel;
import groove.grammar.type.ImplicitTypeGraph;
import groove.grammar.type.TypeGraph;
import groove.util.DefaultDispenser;
import groove.util.Dispenser;

import java.util.Iterator;
import java.util.List;

/**
 * Visitor for a {@link RegExpr} that constructs a regular automaton. The
 * automaton is a graph with a distinguished start state and end node.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RegAutCalculator implements RegExprCalculator<RegAut> {
    /** Creates an instance based on {@link MatrixAutomaton}. */
    public RegAutCalculator() {
        this(SimpleNFA.PROTOTYPE);
    }

    /** Creates an instance with a given prototype automaton. */
    public RegAutCalculator(RegAut prototype) {
        this.prototype = prototype;
    }

    /**
     * Calculates the automaton for a given regular expression, using the
     * implicit type graph for the labels in the expression.
     */
    public RegAut compute(RegExpr expr) {
        return compute(expr, ImplicitTypeGraph.newInstance(expr.getTypeLabels()));
    }

    /**
     * Calculates the automaton for a given regular expression and type graph.
     * @param typeGraph the type graph for the automaton (non-{@code null})
     */
    public RegAut compute(RegExpr expr, TypeGraph typeGraph) {
        this.nodeDispenser = new DefaultDispenser();
        this.typeGraph = typeGraph;
        RegAut result = expr.apply(this);
        result.setFixed();
        return result;
    }

    /**
     * Negation is currently not implemented. We would need at least the
     * alphabet.
     * @throws UnsupportedOperationException always
     */
    @Override
    public RegAut computeNeg(Neg expr, RegAut arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Identical to {@link #computePlus}, except that the empty
     * word is also always allowed.
     */
    @Override
    public RegAut computeStar(Star expr, RegAut arg) {
        RegAut result = computePlus(null, arg);
        result.setAcceptsEmptyWord(true);
        return result;
    }

    /**
     * Adds a node to the automaton passed in as the argument, and adds edges to
     * that node for every current edge to the end node, and edges from that
     * node for every current edge from the start node.
     */
    @Override
    public RegAut computePlus(Plus expr, RegAut result) {
        RegNode newNode = createNode();
        result.addNode(newNode);
        // copy final edges
        for (RegEdge finalEdge : result.inEdgeSet(result.getEndNode())) {
            result.addEdge(finalEdge.source(), finalEdge.label(), newNode);
        }
        // copy initial edges
        for (RegEdge initEdge : result.outEdgeSet(result.getStartNode())) {
            result.addEdge(newNode, initEdge.label(), initEdge.target());
        }
        return result;
    }

    /**
     * Reverses all the edges as well as the start and end nodes of the
     * automaton passed in as a parameter.
     */
    @Override
    public RegAut computeInv(Inv expr, RegAut arg) {
        RegAut result = createAutomaton();
        for (RegEdge edge : arg.edgeSet()) {
            RuleLabel label = invert(edge.label());
            RegNode source = edge.source();
            RegNode target = edge.target();
            result.addNode(source);
            result.addNode(target);
            result.addEdge(target, label, source);
        }
        result.mergeNodes(arg.getEndNode(), result.getStartNode());
        result.mergeNodes(arg.getStartNode(), result.getEndNode());
        result.setAcceptsEmptyWord(arg.isAcceptsEmptyWord());
        return result;
    }

    /**
     * Merges the end node of one automaton with the start node of the next.
     * Also adds initial/final edges if the first or second argument accepts the
     * empty word. It is assumed that the nodes of the argument automata are
     * disjoint.
     */
    @Override
    public RegAut computeSeq(Seq expr, List<RegAut> argList) {
        Iterator<RegAut> argIter = argList.iterator();
        RegAut result = argIter.next();
        while (argIter.hasNext()) {
            RegAut next = argIter.next();
            // add the elements of next to result
            result.addNodeSet(next.nodeSet());
            result.addEdgeSet(next.edgeSet());
            if (result.isAcceptsEmptyWord()) {
                // add initial edges for all the initial edges of next
                for (RegEdge nextInitEdge : next.outEdgeSet(next.getStartNode())) {
                    RegNode target = nextInitEdge.target();
                    result.addNode(target);
                    result.addEdge(result.getStartNode(), nextInitEdge.label(), target);
                }
                result.setAcceptsEmptyWord(next.isAcceptsEmptyWord());
            }
            if (next.isAcceptsEmptyWord()) {
                // add final edges for all the final edges of result
                for (RegEdge resultFinalEdge : result.inEdgeSet(result.getEndNode())) {
                    RegNode source = resultFinalEdge.source();
                    RegNode target = next.getEndNode();
                    result.addNode(source);
                    result.addNode(target);
                    result.addEdge(source, resultFinalEdge.label(), target);
                }
            }
            result.mergeNodes(result.getEndNode(), next.getStartNode());
            result.setEndNode(next.getEndNode());
        }
        return result;
    }

    /**
     * Merges the operand automata, by adding the nodes and edges to the first
     * and merging the start and end nodes. It is assumed that the nodes of the
     * argument automata are disjoint.
     */
    @Override
    public RegAut computeChoice(Choice expr, List<RegAut> argList) {
        Iterator<RegAut> argIter = argList.iterator();
        RegAut result = argIter.next();
        while (argIter.hasNext()) {
            RegAut next = argIter.next();
            result.addNodeSet(next.nodeSet());
            result.addEdgeSet(next.edgeSet());
            result.mergeNodes(next.getStartNode(), result.getStartNode());
            result.mergeNodes(next.getEndNode(), result.getEndNode());
            if (next.isAcceptsEmptyWord()) {
                result.setAcceptsEmptyWord(true);
            }
        }
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node, labelled
     * with the text of the atomic expression.
     * It is required that the atom's type label occurs in the label store.
     */
    @Override
    public RegAut computeAtom(Atom expr) {
        RegAut result = createAutomaton();
        // if this is an unknown label, don't add the edge
        if (!this.typeGraph.getTypes(expr.toTypeLabel())
            .isEmpty()) {
            result.addEdge(result.getStartNode(), expr.toLabel(), result.getEndNode());
        }
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node, labelled
     * with <code>expr</code> (as a {@link RuleLabel}).
     */
    @Override
    public RegAut computeSharp(Sharp expr) {
        RegAut result = createAutomaton();
        result.addEdge(result.getStartNode(), expr.toLabel(), result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node, labelled
     * with <code>expr</code> (as a {@link RuleLabel}).
     */
    @Override
    public RegAut computeWildcard(Wildcard expr) {
        RegAut result = createAutomaton();
        result.addEdge(result.getStartNode(), expr.toLabel(), result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with end state equal to start state, and no
     * transitions.
     */
    @Override
    public RegAut computeEmpty(Empty expr) {
        RegAut result = createAutomaton();
        result.setAcceptsEmptyWord(true);
        return result;
    }

    /**
     * Callback factory method to create an automaton, with fresh node
     * identities (in the context of this calculator).
     */
    protected RegAut createAutomaton() {
        return this.prototype.newAutomaton(createNode(), createNode(), this.typeGraph);
    }

    /**
     * Constructs the inverse of a given label. The inverse is a
     * {@link RuleLabel} with a {@link RegExpr.Inv} inside if the original
     * label is not already of this form; otherwise, the {@link RegExpr.Inv} is
     * stripped. Also takes wildcards into account.
     */
    protected RuleLabel invert(RuleLabel label) {
        // invert the label
        RuleLabel result;
        RuleLabel invLabel = label.getInvLabel();
        if (invLabel == null) {
            result = label.getMatchExpr()
                .inv()
                .toLabel();
        } else {
            result = invLabel;
        }
        return result;
    }

    /** Factory method for a fresh automaton node. */
    private RegNode createNode() {
        return RegFactory.instance()
            .createNode(this.nodeDispenser.getNext());
    }

    /** Prototype automaton to create new automata from. */
    private final RegAut prototype;
    /** Label store currently used to build automata. */
    private TypeGraph typeGraph;
    /** the dispenser for automaton node identities. */
    private Dispenser nodeDispenser = new DefaultDispenser();
}