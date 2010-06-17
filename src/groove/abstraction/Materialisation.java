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
 * $Id$
 */
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Materialisation {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private Shape shape;
    private RuleMatch preMatch;
    private NodeEdgeMap absElems;
    private NodeEdgeMap match;
    private List<Node> pendingNodes;
    private Set<Edge> pendingEdges;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Materialisation(Shape shape, RuleMatch preMatch) {
        this.shape = shape;
        this.preMatch = preMatch;
        this.absElems = getAbstractMatchedElems(shape, preMatch);
        this.match = preMatch.getElementMap().clone();

        Graph lhs = preMatch.getRule().lhs();
        this.pendingNodes = new ArrayList<Node>(lhs.nodeCount());
        this.pendingEdges = new HashSet<Edge>(lhs.edgeSet());
        for (Node node : lhs.nodeSet()) {
            if (!this.absElems.containsKey(node)) {
                this.pendingNodes.add(node);
            }
        }
        this.pendingNodes.addAll(this.absElems.nodeMap().keySet());
    }

    /** Copying constructor. */
    private Materialisation(Materialisation mat) {
        this.shape = mat.shape.clone();
        this.preMatch = mat.preMatch;
        this.absElems = mat.absElems;
        this.match = mat.match.clone();
        this.pendingNodes = new ArrayList<Node>(mat.pendingNodes);
        this.pendingEdges = new HashSet<Edge>(mat.pendingEdges);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.shape.toString() + this.preMatch.toString() + "\n"
            + this.absElems.toString();
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    private static NodeEdgeMap getAbstractMatchedElems(Shape shape,
            RuleMatch preMatch) {
        NodeEdgeMap elemsToMat = new NodeEdgeHashMap();
        NodeEdgeMap originalMap = preMatch.getElementMap();

        // Check the node images.
        for (Entry<Node,Node> nodeEntry : originalMap.nodeMap().entrySet()) {
            if (((ShapeNode) nodeEntry.getValue()).getMultiplicity().isAbstract()) {
                // We have a node in the rule that was matched to an abstract node.
                elemsToMat.putNode(nodeEntry.getKey(), nodeEntry.getValue());
            }
        }

        return elemsToMat;
    }

    /** EDUARDO */
    public static Set<Materialisation> getMaterialisations(Shape shape,
            RuleMatch preMatch) {
        Set<Materialisation> result = new HashSet<Materialisation>();

        // Initial materialisation object.
        Materialisation initialMat = new Materialisation(shape, preMatch);

        // Compute how many copies of each of the abstract nodes we need to
        // materialise.
        Set<Pair<ShapeNode,Integer>> nodesToMat =
            new HashSet<Pair<ShapeNode,Integer>>();
        for (Node nodeS : initialMat.absElems.nodeMap().values()) {
            Set<Node> nodesR =
                Util.getReverseNodeMap(initialMat.absElems, nodeS);
            Integer nodesSize = nodesR.size();
            nodesToMat.add(new Pair<ShapeNode,Integer>((ShapeNode) nodeS,
                nodesSize));
        }

        // Clone the given shape to avoid aliasing and unwanted modifications.
        Shape shapeClone = shape.clone();
        // We are going to materialise elements in the cloned shape, so set
        // the original shape as the graph from which the clone was created
        // and create an identity morphism between the elements of the clone
        // and of the original. This morphism will be later updated and when
        // the materialisation is done it will be the shaping morphism.
        shapeClone.setShapeAndCreateIdentityMorphism(shape);
        shapeClone.materialiseNodes(nodesToMat);

        // List of partially constructed materialisations.
        initialMat.shape = shapeClone;
        Set<Materialisation> todoMats = new HashSet<Materialisation>();
        //todoMats.add(initialMat.clone());
        todoMats.add(initialMat);

        while (!todoMats.isEmpty()) {
            Set<Materialisation> newTodoMats = new HashSet<Materialisation>();
            for (Materialisation currMat : todoMats) {
                if (currMat.isFinished()) {
                    result.add(currMat);
                } else {
                    Node nodeR = currMat.pendingNodes.iterator().next();
                    newTodoMats.addAll(currMat.fixMatAtNode(nodeR));
                }
            }
            todoMats = newTodoMats;
        }

        /*// For each node in the LHS of the rule.
        for (Node nodeR : initialMat.pendingNodes) {
            Set<Materialisation> newTodoMats = new HashSet<Materialisation>();
            for (Materialisation currMat : todoMats) {
                if (currMat.isFinished()) {
                    result.add(currMat);
                }
                newTodoMats.addAll(currMat.fixMatAtNode(nodeR));
            }
            todoMats = newTodoMats;
        }
        assert todoMats.isEmpty() : "Something went wrong...";*/

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private boolean isFinished() {
        return this.pendingNodes.isEmpty() && this.pendingEdges.isEmpty();
    }

    private Set<Materialisation> fixMatAtNode(Node nodeR) {
        Set<Materialisation> result = new HashSet<Materialisation>();

        for (Materialisation nodeMat : this.getNodeMatVariations(nodeR)) {
            for (Edge edgeR : nodeMat.getPendingEdges(nodeR)) {
                for (Materialisation edgeMat : this.getEdgeMatVariations(edgeR)) {
                    result.add(edgeMat);
                }
            }
        }

        return result;
    }

    private Set<Materialisation> getNodeMatVariations(Node nodeR) {
        Set<Materialisation> result = new HashSet<Materialisation>();
        ShapeNode nodeS = this.getMappedNode(nodeR);

        Set<Node> nodeVars =
            Util.getReverseNodeMap(this.shape.getNodeShaping(), nodeS);

        if (nodeVars.size() <= 2) {
            if (nodeVars.size() == 2) {
                // We have two nodes. One is the original, the other is the
                // materialised one.
                // Remove the original node because it will not help in
                // expanding the match.
                nodeVars.remove(nodeS);
                nodeS = (ShapeNode) nodeVars.iterator().next();
            } // else nodeVars.size() == 1
            // The mapped node is already correct.
            this.addToMatch(nodeR, nodeS);
            result.add(this);
        } else {
            // We have more than two nodes. One is the original, the others
            // are the materialised ones.
            // Remove the original node because it will not help in
            // expanding the match.
            nodeVars.remove(nodeS);
            // Iterate over the remaining possibilities and create new
            // materialisations.
            for (Node nodeVar : nodeVars) {
                nodeS = (ShapeNode) nodeVar;
                Materialisation newMat = this.clone();
                newMat.addToMatch(nodeR, nodeS);
                result.add(newMat);
            }
        }
        return result;
    }

    private void addToMatch(Node nodeR, ShapeNode nodeS) {
        this.match.putNode(nodeR, nodeS);
        this.pendingNodes.remove(nodeR);
        // Handle the unary edges.
        Set<Edge> edgesToRemove = new HashSet<Edge>();
        for (Edge edgeR : this.pendingEdges) {
            if (edgeR.source().equals(nodeR) && Util.isUnary(edgeR)) {
                edgesToRemove.add(edgeR);
                for (ShapeEdge edgeS : this.shape.outEdgeSet(nodeS)) {
                    if (Util.isUnary(edgeS)) {
                        this.match.putEdge(edgeR, edgeS);
                    }
                }
            }
        }
        this.pendingEdges.removeAll(edgesToRemove);
    }

    private void addToMatch(Edge edgeR, ShapeEdge edgeS) {
        this.match.putEdge(edgeR, edgeS);
        this.pendingEdges.remove(edgeR);
        // By fixing edgeS, several incoming shared multiplicities are no longer
        // valid. So we remove the impossible edges.
        ShapeNode target = edgeS.opposite();
        if (!target.getMultiplicity().isAbstract()) {
            for (ShapeEdge inEdge : this.shape.getInSharedEdges(edgeS)) {
                if (!inEdge.equals(edgeS)) {
                    this.shape.removeEdge(inEdge);
                }
            }
        }
    }

    private Set<Materialisation> getEdgeMatVariations(Edge edgeR) {
        Set<Materialisation> result = new HashSet<Materialisation>();
        ShapeEdge edgeS = this.getMappedEdge(edgeR);

        Set<Edge> edgeVars =
            Util.getReverseEdgeMap(this.shape.getEdgeShaping(), edgeS);

        if (edgeVars.size() <= 2) {
            if (edgeVars.size() == 2) {
                // We have two edges. One is the original, the other is the
                // materialised one.
                // Remove the original edge because it will not help in
                // expanding the match.
                edgeVars.remove(edgeS);
                this.shape.removeEdge(edgeS);
                edgeS = (ShapeEdge) edgeVars.iterator().next();
            } // else nodeVars.size() == 1
            // The mapped edge is already correct.
            this.addToMatch(edgeR, edgeS);
            result.add(this);
        } else {
            /*// We have more than two nodes. One is the original, the others
            // are the materialised ones.
            // Remove the original node because it will not help in
            // expanding the match.
            nodeVars.remove(nodeS);
            // Iterate over the remaining possibilities and create new
            // materialisations.
            for (Node nodeVar : nodeVars) {
                nodeS = (ShapeNode) nodeVar;
                Materialisation newMat = this.clone();
                newMat.addToMatch(nodeR, nodeS);
                result.add(newMat);
            }*/
        }
        return result;
    }

    private ShapeNode getMappedNode(Node nodeR) {
        return (ShapeNode) this.match.getNode(nodeR);
    }

    private ShapeEdge getMappedEdge(Edge edgeR) {
        return (ShapeEdge) this.match.getEdge(edgeR);
    }

    private Set<Edge> getPendingEdges(Node nodeR) {
        Set<Edge> result = new HashSet<Edge>();
        for (Edge edge : this.pendingEdges) {
            if (edge.source().equals(nodeR)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** EDUARDO */
    public Shape applyMatch() {
        // EDUARDO : Implement this.
        return null;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testMaterialisation0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    System.out.println(mat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Unit test. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testMaterialisation0();
    }

}
