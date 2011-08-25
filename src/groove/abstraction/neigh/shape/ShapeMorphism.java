/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.abstraction.neigh.shape;

import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;

import java.util.Set;

/**
 * Morphism between shapes.
 * @author Arend Rensink
 */
public class ShapeMorphism extends HostGraphMorphism {

    private static final boolean DEBUG = false;

    /**
     * Creates a shape morphism with a given element factory.
     */
    public ShapeMorphism(ShapeFactory factory) {
        super(factory);
    }

    @Override
    public ShapeMorphism clone() {
        return (ShapeMorphism) super.clone();
    }

    @Override
    public ShapeMorphism newMap() {
        return new ShapeMorphism(getFactory());
    }

    @Override
    public ShapeMorphism then(Morphism<HostNode,HostEdge> other) {
        return (ShapeMorphism) super.then(other);
    }

    @Override
    public ShapeMorphism inverseThen(Morphism<HostNode,HostEdge> other) {
        return (ShapeMorphism) super.inverseThen(other);
    }

    @Override
    public ShapeNode getNode(Node key) {
        return (ShapeNode) super.getNode(key);
    }

    @Override
    public ShapeEdge getEdge(HostEdge key) {
        return (ShapeEdge) super.getEdge(key);
    }

    @Override
    public ShapeNode putNode(HostNode key, HostNode layout) {
        return (ShapeNode) super.putNode(key, layout);
    }

    @Override
    public ShapeEdge putEdge(HostEdge key, HostEdge layout) {
        return (ShapeEdge) super.putEdge(key, layout);
    }

    @Override
    public ShapeNode removeNode(HostNode key) {
        return (ShapeNode) super.removeNode(key);
    }

    @Override
    public ShapeEdge removeEdge(HostEdge key) {
        return (ShapeEdge) super.removeEdge(key);
    }

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ShapeNode> getPreImages(HostNode node) {
        assert node instanceof ShapeNode;
        return (Set<ShapeNode>) super.getPreImages(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ShapeEdge> getPreImages(HostEdge edge) {
        assert edge instanceof ShapeEdge;
        return (Set<ShapeEdge>) super.getPreImages(edge);
    }

    /**
     * Creates and returns an identity shape morphism between the two given
     * shapes. Used during the materialisation phase.
     * Fails on an assertion if the given shapes are not identical.
     */
    public static ShapeMorphism createIdentityMorphism(Shape from, Shape to) {
        ShapeMorphism result = from.getFactory().createMorphism();
        for (ShapeNode node : from.nodeSet()) {
            assert to.nodeSet().contains(node);
            result.putNode(node, node);
        }
        for (ShapeEdge edge : from.edgeSet()) {
            assert to.edgeSet().contains(edge);
            result.putEdge(edge, edge);
        }
        return result;
    }

    /**
     * Returns the set of pre-images of the given edge signature in the 'from'
     * shape. Only the pre-images of the nodes in the equivalence class of the
     * signature are considered, the node in the source signature is taken to
     * be the given 'nodeS'.
     */
    public Set<EdgeSignature> getPreImages(Shape from, ShapeNode nodeS,
            EdgeSignature esT) {
        Set<EdgeSignature> result = new THashSet<EdgeSignature>();
        TypeLabel label = esT.getLabel();
        for (ShapeNode esEcNodeT : esT.getEquivClass()) {
            Set<ShapeNode> esEcNodesS = this.getPreImages(esEcNodeT); // f^-1(C)
            for (ShapeNode esEcNodeS : esEcNodesS) {
                EquivClass<ShapeNode> ecS = from.getEquivClassOf(esEcNodeS);
                EdgeSignature esS = from.getEdgeSignature(nodeS, label, ecS);
                result.add(esS);
            }
        }
        return result;
    }

    /**
     * Implements the conditions of a shape morphism given on Def. 11, page 14.
     */
    public boolean isConsistent(Shape from, Shape to) {
        // As in the paper, let shape 'from' be S and shape 'to be T.

        // Check for item 1.
        boolean complyToEquivClass = true;
        ecLoop: for (EquivClass<ShapeNode> ecS : from.getEquivRelation()) {
            if (ecS.size() > 1) {
                EquivClass<ShapeNode> ecT = null;
                for (ShapeNode nodeS : ecS) {
                    EquivClass<ShapeNode> otherEcT =
                        to.getEquivClassOf(this.getNode(nodeS));
                    if (ecT == null) {
                        ecT = otherEcT;
                    }
                    if (!ecT.equals(otherEcT)) {
                        complyToEquivClass = false;
                        break ecLoop;
                    }
                }
            }
        }

        // Check for item 2.
        boolean complyToNodeMult = true;
        if (complyToEquivClass) {
            for (ShapeNode nodeT : to.nodeSet()) {
                Multiplicity nodeTMult = to.getNodeMult(nodeT);
                Set<ShapeNode> nodesS = this.getPreImages(nodeT);
                Multiplicity sum = from.getNodeSetMultSum(nodesS);
                // EZ says: we need subsumption, not equality.
                //if (!nodeTMult.equals(sum)) { 
                if (!nodeTMult.subsumes(sum)) {
                    complyToNodeMult = false;
                    break;
                }
            }
        }

        // Check for item 3.
        boolean complyToEdgeMult = true;
        if (complyToEquivClass && complyToNodeMult) {
            dirLoop: for (EdgeMultDir direction : EdgeMultDir.values()) {
                for (EdgeSignature esT : to.getEdgeMultMapKeys(direction)) {
                    Multiplicity esTMult = to.getEdgeSigMult(esT, direction);
                    Set<ShapeNode> nodesS = this.getPreImages(esT.getNode());
                    for (ShapeNode nodeS : nodesS) {
                        Set<EdgeSignature> esSS =
                            this.getPreImages(from, nodeS, esT);
                        Multiplicity sum =
                            from.getEdgeSigSetMultSum(esSS, direction);
                        // EZ says: we need subsumption, not equality.
                        //if (!esTMult.equals(sum)) {
                        if (!esTMult.subsumes(sum)) {
                            complyToEdgeMult = false;
                            break dirLoop;
                        }
                    }
                }
            }
        }

        if (DEBUG
            && !(complyToEquivClass && complyToNodeMult && complyToEdgeMult)) {
            from.setName("from");
            ShapePreviewDialog.showShape(from);
            to.setName("to");
            ShapePreviewDialog.showShape(to);
            System.out.println(this);
        }

        return complyToEquivClass && complyToNodeMult && complyToEdgeMult;
    }
}
