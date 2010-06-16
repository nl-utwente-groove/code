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
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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
    private RuleMatch match;
    private NodeEdgeMap elemsToMat;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Materialisation(Shape shape, RuleMatch preMatch) {
        this.shape = shape;
        this.match = preMatch;
        this.elemsToMat = getAbstractMatchedElems(shape, preMatch);
    }

    private Materialisation(Shape shape, RuleMatch match, NodeEdgeMap elemsToMat) {
        this.shape = shape;
        this.match = match;
        this.elemsToMat = elemsToMat;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.shape.toString() + this.match.toString() + "\n"
            + this.elemsToMat.toString();
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

        // Check the edge images.
        for (Entry<Edge,Edge> edgeEntry : originalMap.edgeMap().entrySet()) {
            ShapeEdge valueEdge = (ShapeEdge) edgeEntry.getValue();
            Multiplicity outMult = shape.getEdgeOutMult(valueEdge);
            Multiplicity inMult = shape.getEdgeInMult(valueEdge);
            if (outMult.isAbstract() || inMult.isAbstract()) {
                // We have an edge in the rule that was matched to an abstract edge.
                elemsToMat.putEdge(edgeEntry.getKey(), valueEdge);
            }
        }

        return elemsToMat;
    }

    /** EDUARDO */
    public static Set<Materialisation> getMats(Shape shape, RuleMatch preMatch) {
        Set<Materialisation> result = new HashSet<Materialisation>();
        // The initial materialisation configuration.
        Materialisation initialMat = new Materialisation(shape, preMatch);

        // Perform a DFS by trying to refine the partially constructed
        // materialisation until we find a final one.
        Stack<Materialisation> stack = new Stack<Materialisation>();
        stack.push(initialMat);

        while (!stack.isEmpty()) {
            Materialisation currMat = stack.pop();
            if (currMat.isFinished()) {
                // This materialisation is complete. Store it.
                result.add(currMat);
            } else {
                // Try to refine our current materialisation.
                // This will produce zero or more new objects.
                for (Materialisation refMat : currMat.refine()) {
                    // Push the refined materialisations in the stack.
                    stack.push(refMat);
                }
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private boolean isFinished() {
        return this.elemsToMat.isEmpty();
    }

    private Set<Materialisation> refine() {
        assert !this.isFinished() : "Nothing to refine!";
        Set<Materialisation> result = new HashSet<Materialisation>();

        if (!this.elemsToMat.nodeMap().isEmpty()) {
            // Remove a node from the elements that still need to be materialised.
            Entry<Node,Node> nodeEntry =
                this.elemsToMat.nodeMap().entrySet().iterator().next();
            this.refineNodeEntry(nodeEntry, result);
        } else {
            // No more nodes to materialise, check if there are more edges.
            if (!this.elemsToMat.edgeMap().isEmpty()) {
                // Remove an edge from the elements that still need to
                // be materialised.
                Entry<Edge,Edge> edgeEntry =
                    this.elemsToMat.edgeMap().entrySet().iterator().next();
                this.refineEdgeEntry(edgeEntry, result);
            } // else. Done.         
        }

        return result;
    }

    private void refineNodeEntry(Entry<Node,Node> nodeEntry,
            Set<Materialisation> result) {
        // Convenience references.
        Node nodeR = nodeEntry.getKey(); // Rule node.
        ShapeNode nodeS = (ShapeNode) nodeEntry.getValue(); // Shape node.
        Multiplicity oneMult = Multiplicity.getMultOf(1);

        // We will materialise the node from the entry, so remove it from
        // the pending elements.
        this.elemsToMat.removeNode(nodeR);

        // Subtract one from the shape node multiplicity.
        // This produces a set of multiplicities. By the whole algorithm
        // construction, this set is guaranteed to be non-empty and to
        // contain only positive multiplicities.
        Set<Multiplicity> mults = nodeS.getMultiplicity().subNodeMult(oneMult);

        // Make a deep copy of all elements so we know that we don't have
        // aliasing to the objects we are going to modify.
        Materialisation newMat =
            new Materialisation(this.shape.clone(), this.match.createMatch(),
                this.elemsToMat.clone());
    }

    private void refineEdgeEntry(Entry<Edge,Edge> edgeEntry,
            Set<Materialisation> result) {
        // EDUARDO : Implement this.
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
            Graph graph = view.getGraphView("shape-build-test-8").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-match-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Materialisation mat = new Materialisation(shape, preMatch);
                System.out.println(mat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private static void testMaterialisation1() {
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
                Materialisation mat = new Materialisation(shape, preMatch);
                System.out.println(mat);
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
        //testMaterialisation0();
        testMaterialisation1();
    }

}
