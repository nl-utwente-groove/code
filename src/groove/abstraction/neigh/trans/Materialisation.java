/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.MultKind.NODE_MULT;
import static org.junit.Assert.assertEquals;
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;
import groove.trans.BasicEvent;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleNode;
import groove.trans.SystemRecord;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * This class represents an attempt to materialise a certain shape, driven by
 * a certain pre-match of a rule into the shape. We assume that the reader is
 * familiar with the concepts of shape abstraction, in particular Sect. 6
 * (pg 27) of the Technical Report "Graph Abstraction and Abstract Graph
 * Transformation".
 * 
 * The constructors of this class are all private. This means that it is not
 * possible to freely create objects of this class. The only way to do so is
 * by calling the static method getMaterialisations().
 * 
 * WARNING: Beware of the code in this class. It's rather tricky.
 * 
 * @author Eduardo Zambon
 */
public final class Materialisation {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The shape we are trying to materialise.
     */
    private Shape shape;
    /**
     * The matched rule.
     */
    private final Rule matchedRule;
    /**
     * A copy of the original match of the rule into the (partially) materialised shape.
     * This is left unchanged during the materialisation.
     */
    final RuleToShapeMap originalMatch;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     * This is modified as part of the materialisation.
     */
    final RuleToShapeMap match;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation(Shape shape, Proof preMatch) {
        this.shape = shape;
        this.matchedRule = preMatch.getRule();
        this.originalMatch = (RuleToShapeMap) preMatch.getPatternMap();
        this.match = this.originalMatch.clone();
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Constructs and returns the set of all possible materialisations of the
     * given shape and pre-match. This method resolves all non-determinism
     * in the materialisation phase, so the shapes in the returned
     * materialisations are ready to be transformed by conventional rule
     * application.
     */
    public static Set<Materialisation> getMaterialisations(Shape shape,
            Proof preMatch) {
        Set<Materialisation> result = new THashSet<Materialisation>();
        Materialisation mat = new Materialisation(shape, preMatch);
        mat.compute();
        result.add(mat);
        return result;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape + "Match: "
            + this.match + "\n";
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Applies the rule match defined by this materialisation and returns the
     * transformed shape, which is not yet normalised.
     */
    public Shape applyMatch(SystemRecord record) {
        assert this.hasConcreteMatch();
        RuleEvent event = new BasicEvent(this.matchedRule, this.match, true);
        if (record != null) {
            event = record.normaliseEvent(event);
        }
        RuleApplication app = new RuleApplication(event, this.shape);
        Shape result = (Shape) app.getTarget();
        return result;
    }

    /**
     * Checks if the match in the materialisation is concrete. See items 3, 4,
     * and 5 of Def. 35 in pg. 21 of the technical report. 
     * @return true if all three items are satisfied; false, otherwise.
     */
    private boolean hasConcreteMatch() {
        assert this.match.isConsistent();
        // Check for items 3 and 4.
        boolean complyToNodeMult = true;
        boolean complyToEquivClass = true;
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);
        // For all nodes in the image of the LHS.
        for (ShapeNode nodeS : this.match.nodeMap().values()) {
            // Item 3: check that all nodes in the image of the LHS have
            // multiplicity one.
            if (!this.shape.getNodeMult(nodeS).equals(one)) {
                complyToNodeMult = false;
                break;
            }
            // Item 4: check that for all nodes in the image of the LHS, their
            // equivalence class is a singleton set.
            if (!this.shape.getEquivClassOf(nodeS).isSingleton()) {
                complyToEquivClass = false;
                break;
            }
        }

        // Item 5: check that for any two nodes in the image of the LHS, their
        // outgoing and incoming multiplicities are equal and correspond to
        // the number of edges in the underlying graph structure of the shape.
        boolean complyToEdgeMult = true;
        if (complyToNodeMult && complyToEquivClass) {
            // For all binary labels.
            for (TypeLabel label : Util.getBinaryLabels(this.shape)) {
                // For all nodes v in the image of the LHS.
                for (ShapeNode v : this.match.nodeMap().values()) {
                    // For all nodes w in the image of the LHS.
                    for (ShapeNode w : this.match.nodeMap().values()) {
                        EquivClass<ShapeNode> ecW =
                            this.shape.getEquivClassOf(w);
                        EdgeSignature es =
                            this.shape.getEdgeSignature(v, label, ecW);
                        Multiplicity outMult =
                            this.shape.getEdgeSigMult(es, EdgeMultDir.OUTGOING);
                        Multiplicity inMult =
                            this.shape.getEdgeSigMult(es, EdgeMultDir.INCOMING);
                        Set<HostEdge> vInterW =
                            Util.getIntersectEdges(this.shape, v, w, label);
                        Multiplicity vInterWMult =
                            Multiplicity.getEdgeSetMult(vInterW);
                        Set<HostEdge> wInterV =
                            Util.getIntersectEdges(this.shape, w, v, label);
                        Multiplicity wInterVMult =
                            Multiplicity.getEdgeSetMult(wInterV);
                        if (!outMult.equals(vInterWMult)
                            || !inMult.equals(wInterVMult)) {
                            complyToEdgeMult = false;
                            break;
                        }
                    }
                }
            }
        }

        return complyToNodeMult && complyToEquivClass && complyToEdgeMult;
    }

    private void compute() {
        // Search for nodes in the original match image that have to be
        // materialised. 
        for (ShapeNode nodeS : this.originalMatch.nodeMap().values()) {
            if (this.shape.getNodeMult(nodeS).isCollector()) {
                // We have a node in the rule that was matched to a collector
                // node. We need to materialise this collector node.
                // Check the nodes on the rule that were mapped to nodeS.
                Set<RuleNode> nodesR = this.originalMatch.getPreImages(nodeS);
                this.shape.materialiseNode(nodeS, nodesR, this.match);
            }
        }
        // Search for edges in the original match image that have to be
        // materialised. 
        for (ShapeEdge edgeS : this.originalMatch.edgeMap().values()) {
            if (edgeS.getRole() != EdgeRole.BINARY) {
                continue;
            }
            if (!this.shape.isEdgeConcrete(edgeS)) {
                // We have an edge in the rule that was matched to an edge
                // in the shape with a collector multiplicity or that is part
                // of an edge bundle. We need to materialise this edge.
                // Check the edges on the rule that were mapped to edgeS.
                Set<RuleEdge> edgesR = this.originalMatch.getPreImages(edgeS);
                this.shape.materialiseEdge(edgeS, edgesR, this.match);
            }
        }
        assert this.match.isConsistent();
        ShapePreviewDialog.showShape(this.shape);
    }

    /** blah */
    public static void main(String args[]) {
        String DIRECTORY = "junit/samples/abs-test.gps/";
        Multiplicity.initMultStore();
        File file = new File(DIRECTORY);
        try {
            GrammarModel view = GrammarModel.newInstance(file, false);
            HostGraph graph =
                view.getHostModel("materialisation-test-0").toResource();
            Shape shape = Shape.createShape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (Proof preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                /*assertEquals(6, mats.size());
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    int binaryEdgeCount = getBinaryEdges(matShape).size();
                    assertTrue((matShape.nodeSet().size() == 5 && binaryEdgeCount == 4)
                        || (matShape.nodeSet().size() == 6 && binaryEdgeCount == 7));
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

}
