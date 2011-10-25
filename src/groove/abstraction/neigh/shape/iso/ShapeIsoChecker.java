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

    private static final boolean CHECK_SUBSUMPTION = false;

    private static final int NON_ISO = 0x0;
    private static final int DOM_EQUALS_COD = 0x1;
    private static final int DOM_SUBSUMES_COD = 0x2;
    private static final int COD_SUBSUMES_DOM = 0x4;

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
            Graph<ShapeNode,ShapeEdge> cod, ShapeNode[] domNodes,
            ShapeNode[] codNodes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Morphism<ShapeNode,ShapeEdge> getIsomorphism(
            Graph<ShapeNode,ShapeEdge> dom, Graph<ShapeNode,ShapeEdge> cod) {
        throw new UnsupportedOperationException();
    }

    private boolean isDomEqualsCod(int result) {
        return (result & DOM_EQUALS_COD) == DOM_EQUALS_COD;
    }

    private boolean isDomSubsumesCod(int result) {
        return (result & DOM_SUBSUMES_COD) == DOM_SUBSUMES_COD;
    }

    /** Returns true if the given result value has the equals flag set. */
    public boolean areEqual(int result) {
        return this.isDomEqualsCod(result);
    }

    /** 
     * Returns true if the given result indicates that the domain subsumes the
     * co-domain and that they are not equal.
     */
    public boolean isDomStrictlyLargerThanCod(int result) {
        return this.isDomSubsumesCod(result) && !this.isDomEqualsCod(result);
    }

    /** See {@link #areIsomorphic(Graph, Graph)}. */
    public boolean areIsomorphic(Shape dom, Shape cod) {
        int result = this.compareShapes(dom, cod);
        return this.areEqual(result);
    }

    /** Compares the two given shapes and returns a number formed by flags.*/
    public int compareShapes(Shape dom, Shape cod) {
        if (!this.passBasicChecks(dom, cod)) {
            return NON_ISO;
        }
        Graph<ShapeNode,ShapeEdge> domG = dom.downcast();
        Graph<ShapeNode,ShapeEdge> codG = cod.downcast();
        ShapeIsoChecker isoChecker = ShapeIsoChecker.getInstance(true);
        IsoChecker<ShapeNode,ShapeEdge>.IsoCheckerState state =
            new IsoCheckerState();
        Morphism<ShapeNode,ShapeEdge> morphism =
            isoChecker.getIsomorphism(domG, codG, state);
        int result = NON_ISO;
        while (morphism != null) {
            // We found an isomorphism between the graph structures.
            // Check for the extra conditions.
            result = this.checkIsomorphism(dom, cod, morphism);
            if (result != NON_ISO) {
                break;
            } else {
                // Keep trying.
                morphism = isoChecker.getIsomorphism(domG, codG, state);
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
    private int checkIsomorphism(Shape dom, Shape cod,
            Morphism<ShapeNode,ShapeEdge> morphism) {
        int result = DOM_EQUALS_COD | DOM_SUBSUMES_COD | COD_SUBSUMES_DOM;

        // First check the node multiplicities.
        for (Entry<ShapeNode,ShapeNode> nodeEntry : morphism.nodeMap().entrySet()) {
            ShapeNode domNode = nodeEntry.getKey();
            ShapeNode codNode = nodeEntry.getValue();
            Multiplicity domNMult = dom.getNodeMult(domNode);
            Multiplicity codNMult = cod.getNodeMult(codNode);
            int comparison = this.compareMultiplicities(domNMult, codNMult);
            result = this.updateResult(result, comparison);
            if (result == NON_ISO) {
                return NON_ISO;
            }
        }

        // Now check the edge multiplicities.
        for (Entry<ShapeEdge,ShapeEdge> edgeEntry : morphism.edgeMap().entrySet()) {
            ShapeEdge domEdge = edgeEntry.getKey();
            ShapeEdge codEdge = edgeEntry.getValue();
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                Multiplicity domEMult = dom.getEdgeMult(domEdge, direction);
                Multiplicity codEMult = cod.getEdgeMult(codEdge, direction);
                int comparison = this.compareMultiplicities(domEMult, codEMult);
                result = this.updateResult(result, comparison);
                if (result == NON_ISO) {
                    return NON_ISO;
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
                return NON_ISO;
            } else {
                mappedCodEc = new EquivClass<ShapeNode>();
            }
        }

        // If we reach this point, all tests passed.
        return result;
    }

    private boolean passBasicChecks(Shape dom, Shape cod) {
        return dom.nodeCount() == cod.nodeCount()
            && dom.edgeCount() == cod.edgeCount()
            && dom.getEquivRelation().size() == cod.getEquivRelation().size()
            && dom.getEdgeMultMapKeys(EdgeMultDir.OUTGOING).size() == cod.getEdgeMultMapKeys(
                EdgeMultDir.OUTGOING).size()
            && dom.getEdgeMultMapKeys(EdgeMultDir.INCOMING).size() == cod.getEdgeMultMapKeys(
                EdgeMultDir.INCOMING).size();
    }

    private int compareMultiplicities(Multiplicity domMult, Multiplicity codMult) {
        int result = NON_ISO;
        if (domMult.equals(codMult)) {
            result |= DOM_EQUALS_COD;
        }
        if (CHECK_SUBSUMPTION) {
            if (domMult.subsumes(codMult)) {
                result |= DOM_SUBSUMES_COD;
            }
            if (codMult.subsumes(domMult)) {
                result |= COD_SUBSUMES_DOM;
            }
        }
        return result;
    }

    private int updateResult(int result, int comparison) {
        return result & comparison;
    }
}
