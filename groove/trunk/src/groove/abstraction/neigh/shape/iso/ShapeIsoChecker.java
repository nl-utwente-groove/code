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
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.util.Pair;

import java.util.Map.Entry;

/**
 * Isomorphism checker for shapes. In addition of isomorphism it also checks
 * for subsumption between shapes. 
 *  
 * @author Eduardo Zambon
 */
public final class ShapeIsoChecker extends IsoChecker<ShapeNode,ShapeEdge> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /**
     * Flag that indicates whether to check for subsumption or not. Since this
     * greatly improve the performance of exploration, the flag is on by
     * default. It is left here in case subsumption needs to be turned off for
     * debugging.
     * 
     * Bear in mind that the subsumption relation is NOT symmetric!
     */
    public static final boolean CHECK_SUBSUMPTION = true;

    // Return values for the comparisons.
    private static final int NON_ISO = 0x0;
    private static final int DOM_EQUALS_COD = 0x1;
    private static final int DOM_SUBSUMES_COD = 0x2;
    private static final int COD_SUBSUMES_DOM = 0x4;

    /** The singleton strong instance of this class. */
    private static ShapeIsoChecker strongInstance;
    /** The singleton weak instance of this class. */
    private static ShapeIsoChecker weakInstance;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Returns the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    @SuppressWarnings("unchecked")
    public static ShapeIsoChecker getInstance(boolean strong) {
        // Initialise lazily to avoid initialisation circularities.
        if (strongInstance == null) {
            strongInstance = new ShapeIsoChecker(true);
            weakInstance = new ShapeIsoChecker(false);

        }
        return strong ? strongInstance : weakInstance;
    }

    /**
     * Returns true if the shapes are marked as exactly equal, i.e., without
     * taking subsumption into account.
     */
    public static boolean areExactlyEqual(Shape s, Shape t) {
        ShapeIsoChecker checker = getInstance(true);
        int result = checker.compareShapes(s, t).one();
        return checker.isDomEqualsCod(result);
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor, for the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    private ShapeIsoChecker(boolean strong) {
        super(strong);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Convenience method for checking if the flag is set in the given result.*/
    private boolean isDomEqualsCod(int result) {
        return (result & DOM_EQUALS_COD) == DOM_EQUALS_COD;
    }

    /** Convenience method for checking if the flag is set in the given result.*/
    private boolean isDomSubsumesCod(int result) {
        return (result & DOM_SUBSUMES_COD) == DOM_SUBSUMES_COD;
    }

    /** Convenience method for checking if the flag is set in the given result.*/
    public boolean isCodSubsumesDom(int result) {
        return (result & COD_SUBSUMES_DOM) == COD_SUBSUMES_DOM;
    }

    /** 
     * Returns true if the given result indicates that the domain subsumes the
     * co-domain and that they are not equal.
     */
    public boolean isDomStrictlyLargerThanCod(int result) {
        return this.isDomSubsumesCod(result) && !this.isDomEqualsCod(result);
    }

    /**
     * Returns true if we can consider the shapes equal based on the given
     * result of the comparison.
     * If the {@link #CHECK_SUBSUMPTION} flag is off then only the equality
     * flag is checked. Otherwise, the shapes are also considered equal if the
     * co-domain subsumes the domain shape. This means that the new shape (in
     * the domain) will be collapsed under the old one (in the co-domain).
     */
    public boolean areEqual(int result) {
        boolean equal = this.isDomEqualsCod(result);
        if (CHECK_SUBSUMPTION) {
            equal |= this.isCodSubsumesDom(result);
        }
        return equal;
    }

    /** See {@link #compareShapes(Shape, Shape)}. */
    public boolean areIsomorphic(Shape dom, Shape cod) {
        int result = this.compareShapes(dom, cod).one();
        return this.areEqual(result);
    }

    /** Compares the two given shapes and returns a number formed by flags.*/
    public Pair<Integer,Morphism<ShapeNode,ShapeEdge>> compareShapes(Shape dom,
            Shape cod) {
        Pair<Integer,Morphism<ShapeNode,ShapeEdge>> result =
            new Pair<Integer,Morphism<ShapeNode,ShapeEdge>>(NON_ISO, null);
        if (!this.passBasicChecks(dom, cod)) {
            return result;
        }
        Graph<ShapeNode,ShapeEdge> domG = dom.downcast();
        Graph<ShapeNode,ShapeEdge> codG = cod.downcast();
        ShapeIsoChecker isoChecker = ShapeIsoChecker.getInstance(true);
        IsoChecker<ShapeNode,ShapeEdge>.IsoCheckerState state =
            new IsoCheckerState();
        Morphism<ShapeNode,ShapeEdge> morphism =
            isoChecker.getIsomorphism(domG, codG, state);
        int comparison = NON_ISO;
        while (morphism != null) {
            // We found an isomorphism between the graph structures.
            // Check for the extra conditions.
            comparison = this.checkIsomorphism(dom, cod, morphism);
            if (comparison != NON_ISO) {
                result =
                    new Pair<Integer,Morphism<ShapeNode,ShapeEdge>>(comparison,
                        morphism);
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
     * (1) they have the same node multiplicities (maybe up to subsumption);
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
        EquivClass<ShapeNode> mappedCodEc =
            new NodeEquivClass<ShapeNode>(dom.getFactory());
        for (EquivClass<ShapeNode> domEc : dom.getEquivRelation()) {
            ShapeNode codNode = null;
            for (ShapeNode domNode : domEc) {
                codNode = morphism.getNode(domNode);
                mappedCodEc.add(codNode);
            }
            EquivClass<ShapeNode> codEc = cod.getEquivClassOf(codNode);
            // EZ says: we can used equality here because the node equivalence
            // classes are implemented as bit sets and their equals method is
            // as fast as computing a hash.
            if (!codEc.equals(mappedCodEc)) {
                return NON_ISO;
            } else {
                mappedCodEc = new NodeEquivClass<ShapeNode>(dom.getFactory());
            }
        }

        // If we reach this point, all tests passed.
        return result;
    }

    /**
     * Performs some basic comparisons between the two given shapes. This is
     * used to speed-up the iso checking: if this method fail (which is cheap
     * to execute) then for sure the shapes are not isomorphic.
     */
    private boolean passBasicChecks(Shape dom, Shape cod) {
        return dom.nodeCount() == cod.nodeCount()
            && dom.edgeCount() == cod.edgeCount()
            && dom.getEquivRelation().size() == cod.getEquivRelation().size()
            && dom.getEdgeSigSet(EdgeMultDir.OUTGOING).size() == cod.getEdgeSigSet(
                EdgeMultDir.OUTGOING).size()
            && dom.getEdgeSigSet(EdgeMultDir.INCOMING).size() == cod.getEdgeSigSet(
                EdgeMultDir.INCOMING).size();
    }

    /**
     * Compares the given multiplicity for equality and also for subsumption
     * in both directions.
     * Returns an integer with all the proper flags set. 
     */
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

    /**
     * Updated the given result value with the new given comparison.
     * Returns the new result.
     */
    private int updateResult(int result, int comparison) {
        return result & comparison;
    }

    /** Ugly hack for stupid typing problems. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public IsoChecker<HostNode,HostEdge> downcast() {
        return (IsoChecker) this;
    }

    // ------------------------------------------------------------------------
    // Unimplemented methods
    // ------------------------------------------------------------------------

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
}
