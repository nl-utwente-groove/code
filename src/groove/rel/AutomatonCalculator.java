/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: AutomatonCalculator.java,v 1.1.1.2 2007-03-20 10:42:52 kastenberg Exp $
 */
package groove.rel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExpr.Atom;
import groove.rel.RegExpr.Choice;
import groove.rel.RegExpr.Empty;
import groove.rel.RegExpr.Inv;
import groove.rel.RegExpr.Neg;
import groove.rel.RegExpr.Plus;
import groove.rel.RegExpr.Seq;
import groove.rel.RegExpr.Star;
import groove.rel.RegExpr.Wildcard;

/**
 * Visitor for a {@link groove.rel.RegExpr} that constructs a regular automaton.
 * The automaton is a graph with a distinguished start state and end node.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class AutomatonCalculator implements RegExprCalculator<Automaton> {
    /**
     * Applies this calculator to a given regular expression,
     * fixes the resulting automaton and returns it.
     */
    public Automaton compute(RegExpr expr) {
        Automaton result = expr.apply(this);
        result.setFixed();
        return result;
    }
    
    /**
     * Negation is currently not implemented.
     * We would need at least the alphabet.
     * @throws UnsupportedOperationException always
     */
    public Automaton computeNeg(Neg expr, Automaton arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Identical to {@link groove.rel.AutomatonCalculator#computePlus(Plus, Automaton)},
     * except that the empty word is also always allowed. 
     */
    public Automaton computeStar(Star expr, Automaton arg) {
        Automaton result = computePlus(null, arg);
        result.setAcceptsEmptyWord(true);
        return result;
    }

    /**
     * Adds a node to the automaton passed in as the argument,
     * and adds edges to that node for every current edge to the end node,
     * and edges from that node for every current edge from the start node.
     */
    public Automaton computePlus(Plus expr, Automaton result) {
        Node newNode = result.addNode();
        // copy final edges
        for (Edge finalEdge: result.edgeSet(result.getEndNode(), Edge.TARGET_INDEX)) {
            Node[] ends = Arrays.asList(finalEdge.ends()).toArray(new Node[0]);
            ends[Edge.TARGET_INDEX] = newNode;
            result.addEdge(ends, finalEdge.label());
        }
        // copy initial edges
        for (Edge initEdge: result.outEdgeSet(result.getStartNode())) {
            Node[] ends = Arrays.asList(initEdge.ends()).toArray(new Node[0]);
            ends[Edge.SOURCE_INDEX] = newNode;
            result.addEdge(ends, initEdge.label());
        }
        return result;
    }

    /**
     * Reverses all the edges as well as the start and end nodes
     * of the automaton passed in as a parameter.
     */
    public Automaton computeInv(Inv expr, Automaton arg) {
        Automaton result = createAutomaton();
        for (Edge edge: arg.edgeSet()) {
            Label label = invert(edge.label());
            if (edge.endCount() == 1) {
                result.addEdge(edge.ends(), label);
            } else {
                Node[] ends = edge.ends();
                Node tmp = ends[Edge.SOURCE_INDEX];
                ends[Edge.SOURCE_INDEX] = ends[Edge.TARGET_INDEX];
                ends[Edge.TARGET_INDEX] = tmp;
                result.addEdge(ends, label);
            }
        }
        result.mergeNodes(arg.getEndNode(), result.getStartNode());
        result.mergeNodes(arg.getStartNode(), result.getEndNode());
        result.setAcceptsEmptyWord(arg.isAcceptsEmptyWord());
        return result;
    }

    /**
     * Merges the end node of one automaton with the start node of the next.
     * Also adds initial/final edges if the first or second argument accepts the empty word.
     */
    public Automaton computeSeq(Seq expr, List<Automaton> argList) {
        Iterator<Automaton> argIter = argList.iterator();
        Automaton result = argIter.next();
        while (argIter.hasNext()) {
            Automaton next = argIter.next();
            // add the elements of next to result
            result.addNodeSet(next.nodeSet());
            result.addEdgeSet(next.edgeSet());
            if (result.isAcceptsEmptyWord()) {
                // add initial edges for all the initial edges of next
            	for (Edge nextInitEdge: next.outEdgeSet(next.getStartNode())) {
                    Node[] ends = Arrays.asList(nextInitEdge.ends()).toArray(new Node[0]);
                    ends[Edge.SOURCE_INDEX] = result.getStartNode();
                    result.addEdge(ends, nextInitEdge.label());
                }
                result.setAcceptsEmptyWord(next.isAcceptsEmptyWord());
            }
            if (next.isAcceptsEmptyWord()) {
                // add final edges for all the final edges of result
            	for (Edge resultFinalEdge: result.edgeSet(result.getEndNode(),Edge.TARGET_INDEX)) {
                    Node[] ends = Arrays.asList(resultFinalEdge.ends()).toArray(new Node[0]);
                    ends[Edge.TARGET_INDEX] = next.getEndNode();
                    result.addEdge(ends, resultFinalEdge.label());
                }                
            }
            result.mergeNodes(result.getEndNode(), next.getStartNode());
            result.setEndNode(next.getEndNode());
        }
        return result;
    }

    /**
     * Merges the operand automata,
     * by adding the nodes and edges to the first and merging the start and end nodes.
     */
    public Automaton computeChoice(Choice expr, List<Automaton> argList) {
        Iterator<Automaton> argIter = argList.iterator();
        Automaton result = argIter.next();
        while (argIter.hasNext()) {
            Automaton next = argIter.next();
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
     * Returns an automaton with a single edge, from start to end node,
     * labelled with the text of the atomic expression.
     */
    public Automaton computeAtom(Atom expr) {
        Automaton result = createAutomaton();
        result.addEdge(result.getStartNode(), DefaultLabel.createLabel(expr.text()), result.getEndNode());
        return result;
    }

    /**
     * Returns an automaton with a single edge, from start to end node,
     * labelled with <code>expr</code> (as a {@link RegExprLabel}).
     */
    public Automaton computeWildcard(Wildcard expr) {
        Automaton result = createAutomaton();
        result.addEdge(result.getStartNode(), new RegExprLabel(expr), result.getEndNode());
        return result;
    }

    /**
     * Returns an eutomaton with end state equal to start state, and no transitions.
     */
    public Automaton computeEmpty(Empty expr) {
        Automaton result = createAutomaton();
        result.setAcceptsEmptyWord(true);
        return result;
    }

    /**
     * Callback factory method to create an automaton.
     */
    protected Automaton createAutomaton() {
        return new MatrixAutomaton();
    }
    
    /**
     * Constructs the inverse of a given label.
     * The inverse is a {@link RegExprLabel} with a {@link RegExpr.Inv} inside
     * if the original label is not already of this form; otherwise, the
     * {@link RegExpr.Inv} is stripped.
     * Also takes wildcards into account.
     */
    protected Label invert(Label label) {
        // invert the label
        RegExpr invOperand = RegExprLabel.getInvOperand(label);
        if (invOperand == null) {
            RegExpr innerExpr = label instanceof RegExprLabel ? ((RegExprLabel) label).getRegExpr() : RegExpr.atom(label.text()); 
            return new RegExprLabel(innerExpr.inv());
        } else {
            if (invOperand.isWildcard()) {
                return new RegExprLabel(invOperand);
            } else {
                return DefaultLabel.createLabel(invOperand.getAtomText());
            }
        }
    }
}