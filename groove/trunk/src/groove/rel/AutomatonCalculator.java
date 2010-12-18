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
 * $Id: AutomatonCalculator.java,v 1.4 2008-01-30 09:32:28 iovka Exp $
 */
package groove.rel;

import groove.graph.LabelStore;
import groove.rel.RegExpr.Atom;
import groove.rel.RegExpr.Choice;
import groove.rel.RegExpr.Empty;
import groove.rel.RegExpr.Inv;
import groove.rel.RegExpr.Neg;
import groove.rel.RegExpr.Plus;
import groove.rel.RegExpr.Seq;
import groove.rel.RegExpr.Sharp;
import groove.rel.RegExpr.Star;
import groove.rel.RegExpr.Wildcard;
import groove.trans.RuleLabel;
import groove.util.DefaultDispenser;

import java.util.Iterator;
import java.util.List;

/**
 * Visitor for a {@link RegExpr} that constructs a regular automaton. The
 * automaton is a graph with a distinguished start state and end node.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AutomatonCalculator implements RegExprCalculator<RegAut> {
    /**
     * Applies this calculator to a given regular expression, fixes the
     * resulting automaton and returns it.
     * It is required that all the expression labels occur in the
     * label store.
     * @param labelStore the label store for the automaton (non-{@code null})
     */
    public RegAut compute(RegExpr expr, LabelStore labelStore) {
        this.nodeDispenser.reset();
        this.labelStore = labelStore;
        RegAut result = expr.apply(this);
        result.setFixed();
        return result;
    }

    /**
     * Negation is currently not implemented. We would need at least the
     * alphabet.
     * @throws UnsupportedOperationException always
     */
    public RegAut computeNeg(Neg expr, RegAut arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Identical to {@link #computePlus}, except that the empty
     * word is also always allowed.
     */
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
    public RegAut computeInv(Inv expr, RegAut arg) {
        RegAut result = createAutomaton();
        for (RegEdge edge : arg.edgeSet()) {
            RuleLabel label = invert(edge.label());
            result.addEdge(edge.target(), label, edge.source());
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
                    result.addEdge(result.getStartNode(), nextInitEdge.label(),
                        nextInitEdge.target());
                }
                result.setAcceptsEmptyWord(next.isAcceptsEmptyWord());
            }
            if (next.isAcceptsEmptyWord()) {
                // add final edges for all the final edges of result
                for (RegEdge resultFinalEdge : result.inEdgeSet(result.getEndNode())) {
                    result.addEdge(resultFinalEdge.source(),
                        resultFinalEdge.label(), next.getEndNode());
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
    public RegAut computeAtom(Atom expr) {
        RegAut result = createAutomaton();
        assert this.labelStore.getLabels().contains(expr.toTypeLabel()) : String.format(
            "Unknown label %s", expr.toTypeLabel());
        result.addEdge(result.getStartNode(), expr.toLabel(),
            result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node, labelled
     * with <code>expr</code> (as a {@link RuleLabel}).
     */
    public RegAut computeSharp(Sharp expr) {
        RegAut result = createAutomaton();
        result.addEdge(result.getStartNode(), expr.toLabel(),
            result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node, labelled
     * with <code>expr</code> (as a {@link RuleLabel}).
     */
    public RegAut computeWildcard(Wildcard expr) {
        RegAut result = createAutomaton();
        result.addEdge(result.getStartNode(), expr.toLabel(),
            result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with end state equal to start state, and no
     * transitions.
     */
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
        return new MatrixAutomaton(createNode(), createNode(), this.labelStore);
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
            result = label.getRegExpr().inv().toLabel();
        } else {
            result = invLabel;
        }
        return result;
    }

    /** Factory method for a fresh automaton node. */
    private RegNode createNode() {
        return RegFactory.instance().createNode(this.nodeDispenser.getNext());
    }

    /** Label store currently used to build automata. */
    private LabelStore labelStore;
    /** the dispenser for automaton node identities. */
    private final DefaultDispenser nodeDispenser = new DefaultDispenser();
}