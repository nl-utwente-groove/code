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
package groove.verify.ltl2ba;

import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;
import gov.nasa.ltl.trans.LTL2Buchi;
import gov.nasa.ltl.trans.ParseErrorException;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.DefaultBuchiLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class NASABuchiGraph extends AbstractBuchiGraph {
    private static final String FALSE = "false";
    private Map<Node,BuchiLocation> node2location;

    private NASABuchiGraph() {
        this.node2location = new HashMap<Node,BuchiLocation>();
    }

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new NASABuchiGraph();
    }

    @Override
    public BuchiGraph newBuchiGraph(String formula) {
        String strippedFormula = stripSurroundingParentheses(formula);
        if (isFalse(strippedFormula)) {
            return getFalseBuchiGraph();
        } else {
            final BuchiGraph result = new NASABuchiGraph();
            try {
                Graph graph = LTL2Buchi.translate(strippedFormula);
                Node init = graph.getInit();
                IVisitor visitor = new Visitor(result);
                visitor.visitNode(init);
                result.addInitialLocation(getLocation(init));
            } catch (ParseErrorException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private BuchiGraph getFalseBuchiGraph() {
        BuchiGraph result = new NASABuchiGraph();
        result.addInitialLocation(new DefaultBuchiLocation());
        return result;
    }

    /**
     * Strips surrounding parentheses to effectively parse the given formula.
     * @param formula the formula to be stripped
     * @return the stripped formula
     */
    private String stripSurroundingParentheses(String formula) {
        if (formula.startsWith("(") && formula.endsWith(")")) {
            return stripSurroundingParentheses(formula.substring(1, formula.length()-1));
        } else {
            return formula;
        }
    }

    /**
     * Checks whether the given string equals the formula false (<code>ff</code>).
     * @param formula the formula to check for falseness
     * @return <code>true</code> if the given string represents the false formula, <code>false</code> otherwise
     */
    private boolean isFalse(String formula) {
        if (formula.startsWith("!")) {
            return isTrue(stripSurroundingParentheses(formula.substring(1)));
        } else {
            if (formula.equalsIgnoreCase(FALSE))
                return true;
            else
                return false;
        }
    }

    private boolean isTrue(String formula) {
        if (formula.startsWith("!")) {
            return isFalse(stripSurroundingParentheses(formula.substring(1)));
        } else {
            if (formula.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }
    }

    private BuchiLocation getLocation(Node node) {
        BuchiLocation result = null;
        if (this.node2location.containsKey(node)) {
            result = this.node2location.get(node);
        } else {
            result = new DefaultBuchiLocation();
            this.node2location.put(node, result);
        }
        return result;
    }

    private interface IVisitor {
        /**
         * Visit the provided node.
         * @param node the node to visit
         */
        public void visitNode(Node node);

        /**
         * Visit the provided edge;
         * @param edge the edge to visit
         */
        public void visitEdge(Edge edge);
    }

    private class Visitor implements IVisitor {
        private BuchiGraph graph;

        public Visitor(BuchiGraph graph) {
            this.graph = graph;
        }

        public void visitNode(Node node) {
            if (null == getLocation(node).outTransitions()) {
                for (Edge edge : node.getOutgoingEdges()) {
                    visitEdge(edge);
                }
                if (node.getAttributes().getBoolean("accepting")) {
                    getLocation(node).setAccepting();
                    this.graph.addAcceptingLocation(getLocation(node));
                }
            } else {
                // this node has already been visited
            }
        }

        public void visitEdge(Edge edge) {
            NASABuchiLabel label =
                new NASABuchiLabel(edge.getAction(), edge.getGuard());
            Node source = edge.getSource();
            Node target = edge.getNext();
            BuchiTransition transition =
                new NASABuchiTransition(getLocation(source), label,
                    getLocation(target));
            this.graph.addTransition(transition);
            visitNode(target);
        }
    }
}
