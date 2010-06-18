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
            ShapeNode node = (ShapeNode) nodeEntry.getValue();
            if (shape.getNodeMult(node).isAbstract()) {
                // We have a node in the rule that was matched to an abstract node.
                elemsToMat.putNode(nodeEntry.getKey(), node);
            }
        }

        return elemsToMat;
    }

    /** EDUARDO */
    public static Set<Materialisation> getMaterialisations(Shape shape,
            RuleMatch preMatch) {
        Set<Materialisation> result = new HashSet<Materialisation>();

        // Clone the given shape to avoid aliasing and unwanted modifications.
        Shape shapeClone = shape.clone();
        // We are going to materialise elements in the cloned shape, so set
        // the original shape as the graph from which the clone was created
        // and create an identity morphism between the elements of the clone
        // and of the original. This morphism will be later updated and when
        // the materialisation is done it will be the shaping morphism.

        // EDUARDO FIXME
        // shapeClone.setShapeAndCreateIdentityMorphism(shape);

        // Initial materialisation object.
        Materialisation initialMat = new Materialisation(shapeClone, preMatch);

        Set<Materialisation> possibleMats =
            initialMat.materialiseNodesAndExtendPreMatch();

        for (Materialisation possibleMat : possibleMats) {
            result.addAll(possibleMat.finishMaterialisation());
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private Set<Materialisation> materialiseNodesAndExtendPreMatch() {
        Set<Materialisation> result = new HashSet<Materialisation>();

        return result;
    }

    private Set<Materialisation> finishMaterialisation() {
        Set<Materialisation> result = new HashSet<Materialisation>();

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
