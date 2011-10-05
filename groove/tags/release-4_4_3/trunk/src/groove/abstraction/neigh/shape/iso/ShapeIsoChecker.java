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
package groove.abstraction.neigh.shape.iso;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.trans.HostEdge;
import groove.trans.HostNode;

import java.util.Map.Entry;

/**
 * Isomorphism checker for shapes.
 *  
 * @author Eduardo Zambon
 */
public class ShapeIsoChecker extends IsoChecker<ShapeNode,ShapeEdge> {

    /** The singleton strong instance of this class. */
    static private ShapeIsoChecker strongInstance;
    /** The singleton weak instance of this class. */
    static private ShapeIsoChecker weakInstance;

    /**
     * Private constructor, for the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    private ShapeIsoChecker(boolean strong) {
        super(strong);
    }

    /**
     * Returns the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    @SuppressWarnings("unchecked")
    static public ShapeIsoChecker getInstance(boolean strong) {
        // Initialise lazily to avoid initialisation circularities.
        if (strongInstance == null) {
            strongInstance = new ShapeIsoChecker(true);
            weakInstance = new ShapeIsoChecker(false);

        }
        return strong ? strongInstance : weakInstance;
    }

    /** Ugly hack for stupid typing problems. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public IsoChecker<HostNode,HostEdge> downcast() {
        return (IsoChecker) this;
    }

    @Override
    public boolean areIsomorphic(Graph<ShapeNode,ShapeEdge> dom,
            Graph<ShapeNode,ShapeEdge> cod) {
        return this.areIsomorphic(dom, cod, null, null);
    }

    /** See {@link #areIsomorphic(Graph, Graph)}. */
    public boolean areIsomorphic(Shape dom, Shape cod) {
        return this.areIsomorphic(dom.downcast(), cod.downcast());
    }

    @Override
    public boolean areIsomorphic(Graph<ShapeNode,ShapeEdge> dom,
            Graph<ShapeNode,ShapeEdge> cod, ShapeNode[] domNodes,
            ShapeNode[] codNodes) {
        return this.basicChecks(dom, cod)
            && super.areIsomorphic(dom, cod, domNodes, codNodes)
            && this.getIsomorphism(dom, cod) != null;
    }

    @Override
    public Morphism<ShapeNode,ShapeEdge> getIsomorphism(
            Graph<ShapeNode,ShapeEdge> dom, Graph<ShapeNode,ShapeEdge> cod) {
        if (!this.basicChecks(dom, cod)) {
            return null;
        }
        Morphism<ShapeNode,ShapeEdge> result = null;
        ShapeIsoChecker isoChecker = ShapeIsoChecker.getInstance(true);
        IsoChecker<ShapeNode,ShapeEdge>.IsoCheckerState state =
            new IsoCheckerState();
        Morphism<ShapeNode,ShapeEdge> morphism =
            isoChecker.getIsomorphism(dom, cod, state);
        while (morphism != null) {
            // We found an isomorphism between the graph structures.
            // Check for the extra conditions.
            if (this.isValidIsomorphism(dom, cod, morphism)) {
                // Valid shape isomorphism.
                result = morphism;
                break;
            } else {
                // Keep trying.
                morphism = isoChecker.getIsomorphism(dom, cod, state);
                if (state.isPlanEmpty()) {
                    // We got the same morphism back. The check fails.
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns true if the given pre-isomorphism is a valid shape isomorphism.
     * Two shapes are isomorphic if:
     * (0) their underlying graph structures are isomorphic (the given morphism);
     * (1) they have the same node multiplicities;
     * (2) they have the same outgoing and incoming edge multiplicities; and
     * (3) they have the same equivalence relation. 
     */
    private boolean isValidIsomorphism(Graph<ShapeNode,ShapeEdge> domG,
            Graph<ShapeNode,ShapeEdge> codG,
            Morphism<ShapeNode,ShapeEdge> morphism) {
        Shape dom = Shape.upcast(domG);
        Shape cod = Shape.upcast(codG);

        // First check the node multiplicities.
        for (Entry<ShapeNode,ShapeNode> nodeEntry : morphism.nodeMap().entrySet()) {
            ShapeNode domNode = nodeEntry.getKey();
            ShapeNode codNode = nodeEntry.getValue();
            Multiplicity domNMult = dom.getNodeMult(domNode);
            Multiplicity codNMult = cod.getNodeMult(codNode);
            if (!domNMult.equals(codNMult)) {
                return false;
            }
        }

        // Now check the edge multiplicities.
        for (Entry<ShapeEdge,ShapeEdge> edgeEntry : morphism.edgeMap().entrySet()) {
            ShapeEdge domEdge = edgeEntry.getKey();
            ShapeEdge codEdge = edgeEntry.getValue();
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                Multiplicity domEMult = dom.getEdgeMult(domEdge, direction);
                Multiplicity codEMult = cod.getEdgeMult(codEdge, direction);
                if (!domEMult.equals(codEMult)) {
                    return false;
                }
            }
        }

        // Last, check the equivalence relation.
        EquivClass<ShapeNode> mappedCodEc = new EquivClass<ShapeNode>();
        for (EquivClass<ShapeNode> domEc : dom.getEquivRelation()) {
            ShapeNode codNode = null;
            for (ShapeNode domNode : domEc) {
                codNode = morphism.getNode(domNode);
                mappedCodEc.add(codNode);
            }
            EquivClass<ShapeNode> codEc = cod.getEquivClassOf(codNode);
            if (!codEc.equals(mappedCodEc)) {
                return false;
            } else {
                mappedCodEc = new EquivClass<ShapeNode>();
            }
        }

        // If we reach this point, all tests passed.
        return true;
    }

    private boolean basicChecks(Graph<ShapeNode,ShapeEdge> domG,
            Graph<ShapeNode,ShapeEdge> codG) {
        boolean pass;
        // First, basic node and edge count check.
        pass =
            domG.nodeCount() == codG.nodeCount()
                && domG.edgeCount() == codG.edgeCount();
        if (pass) {
            Shape dom = Shape.upcast(domG);
            Shape cod = Shape.upcast(codG);
            // Check the size of the equivalence relation and the size of the edge multiplicity maps.
            pass =
                dom.getEquivRelation().size() == cod.getEquivRelation().size()
                    && dom.getEdgeMultMapKeys(EdgeMultDir.OUTGOING).size() == cod.getEdgeMultMapKeys(
                        EdgeMultDir.OUTGOING).size()
                    && dom.getEdgeMultMapKeys(EdgeMultDir.INCOMING).size() == cod.getEdgeMultMapKeys(
                        EdgeMultDir.INCOMING).size();
        }
        return pass;
    }

}
