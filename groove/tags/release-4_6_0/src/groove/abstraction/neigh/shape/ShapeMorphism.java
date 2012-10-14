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

import groove.abstraction.Multiplicity;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Morphism between shapes.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeMorphism extends HostGraphMorphism {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a shape morphism with a given element factory.
     */
    ShapeMorphism(ShapeFactory factory) {
        super(factory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

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

    /** Removes the node from the morphism and all incident edges. */
    @Override
    public ShapeNode removeNode(HostNode key) {
        Iterator<HostEdge> iter = this.edgeMap().keySet().iterator();
        while (iter.hasNext()) {
            HostEdge edge = iter.next();
            if (edge.source().equals(key) || edge.target().equals(key)) {
                iter.remove();
            }
        }
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
            assert to.containsNode(node);
            result.putNode(node, node);
        }
        for (ShapeEdge edge : from.edgeSet()) {
            assert to.containsEdge(edge);
            result.putEdge(edge, edge);
        }
        return result;
    }

    /**
     * Returns the multiplicity of pre-images of the given edge signature in the 'from'
     * shape. Only the pre-images of the nodes in the equivalence class of the
     * signature are considered, the node in the source signature is taken to
     * be the given 'nodeS'.
     * 
     * @param from the shape that is the pre-image of this morphism.
     * @param nodeS the node in the 'from' shape the is to used when looking
     *              for edge signatures.
     * @param esT the edge signature in the image of this morphism.
     * @return the combined multiplicity of all signatures mapped into esT
     */
    // EZ says: this method is a bit expensive so maybe it might payoff to try
    // to improve its performance.
    public Multiplicity getPreImagesMult(Shape from, ShapeNode nodeS,
            EdgeSignature esT) {
        Multiplicity result = Multiplicity.ZERO_EDGE_MULT;
        EdgeMultDir direction = esT.getDirection();
        TypeLabel label = esT.getLabel();
        EdgeSignatureStore fromStore = from.getEdgeSigStore();
        EquivRelation<ShapeNode> fromEquivRelation = from.getEquivRelation();
        // first collect equivalence classes, to eliminate overlap
        Set<EquivClass<ShapeNode>> ecSS = new HashSet<EquivClass<ShapeNode>>();
        for (ShapeNode esEcNodeT : esT.getEquivClass()) {
            Set<ShapeNode> esEcNodesS = this.getPreImages(esEcNodeT); // f^-1(C)
            for (ShapeNode esEcNodeS : esEcNodesS) {
                ecSS.add(fromEquivRelation.getEquivClassOf(esEcNodeS));
            }
        }
        // now sum the multiplicities
        for (EquivClass<ShapeNode> ecS : ecSS) {
            EdgeSignature esS = fromStore.getSig(direction, nodeS, label, ecS);
            if (esS != null) {
                Multiplicity esMult = fromStore.getMult(esS);
                if (esMult != null) {
                    result = result.add(esMult);
                }
            }
        }
        return result;
    }

    /** Returns an edge signature in the given target shape. */
    public EdgeSignature getEdgeSignature(Shape to, EdgeSignature esFrom) {
        return to.getEdgeSignature(
            esFrom.getDirection(),
            this.getNode(esFrom.getNode()),
            esFrom.getLabel(),
            to.getEquivClassOf(this.getNode(esFrom.getEquivClass().iterator().next())));
    }

    /** Returns true if all keys are in 'from' and all values in 'to'. */
    public boolean isValid(Shape from, Shape to) {
        for (Entry<HostNode,HostNode> entry : this.nodeMap().entrySet()) {
            if (!from.containsNode(entry.getKey())
                || !to.containsNode(entry.getValue())) {
                return false;
            }
        }
        for (Entry<HostEdge,HostEdge> entry : this.edgeMap().entrySet()) {
            if (!from.containsEdge(entry.getKey())
                || !to.containsEdge(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implements the conditions of a shape morphism given on Def. 11, page 14.
     */
    public boolean isConsistent(Shape from, Shape to) {
        // As in the paper, let shape 'from' be S and shape 'to' be T.

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
            dirLoop: for (EdgeSignature esT : to.getEdgeSigSet()) {
                Multiplicity esTMult = to.getEdgeSigMult(esT);
                Set<ShapeNode> nodesS = this.getPreImages(esT.getNode());
                for (ShapeNode nodeS : nodesS) {
                    Multiplicity sum = this.getPreImagesMult(from, nodeS, esT);
                    // EZ says: we need subsumption, not equality.
                    //if (!esTMult.equals(sum)) {
                    if (!esTMult.subsumes(sum)) {
                        complyToEdgeMult = false;
                        break dirLoop;
                    }
                }
            }
        }

        if (!(complyToEquivClass && complyToNodeMult && complyToEdgeMult)) {
            // This method is only used in assertions so if we hit this point
            // it means we found a bug in the code. Print some information
            // to help debugging.
            System.out.println("\n\nInconsistent shape morphism!");
            System.out.println("From shape:");
            System.out.println(from);
            System.out.println("To shape:");
            System.out.println(to);
            System.out.println("Morphism:");
            System.out.println(this);
            System.out.println("EC test: " + complyToEquivClass
                + ", Node mult test: " + complyToNodeMult
                + ", Edge mult test: " + complyToEdgeMult);
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                ShapePreviewDialog.showShape(from.clone());
                ShapePreviewDialog.showShape(to.clone());
            }
        }

        return complyToEquivClass && complyToNodeMult && complyToEdgeMult;
    }
}
