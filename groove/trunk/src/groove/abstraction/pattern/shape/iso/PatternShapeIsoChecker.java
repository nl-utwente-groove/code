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
package groove.abstraction.pattern.shape.iso;

import groove.abstraction.Multiplicity;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.iso.IsoChecker;
import groove.util.Pair;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Isomorphism checker for shapes. In addition of isomorphism it also checks
 * for subsumption between shapes. 
 *  
 * @author Eduardo Zambon
 */
public final class PatternShapeIsoChecker extends IsoChecker {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    // Return values for the comparisons.
    private static final int NON_ISO = 0x0;
    private static final int DOM_EQUALS_COD = 0x1;
    private static final int DOM_SUBSUMES_COD = 0x2;
    private static final int COD_SUBSUMES_DOM = 0x4;

    /** The singleton strong instance of this class. */
    private static PatternShapeIsoChecker instance;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Returns the singleton instance of this class.
          */
    public static PatternShapeIsoChecker getInstance() {
        // Initialise lazily to avoid initialisation circularities.
        if (instance == null) {
            instance = new PatternShapeIsoChecker();
        }
        return instance;
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor, for the singleton instance of this class.
     */
    private PatternShapeIsoChecker() {
        super(true);
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
        return isDomSubsumesCod(result) && !isDomEqualsCod(result);
    }

    /**
     * Returns true if we can consider the shapes equal based on the given
     * result of the comparison.
     * The shapes are also considered equal if the
     * co-domain subsumes the domain shape. This means that the new shape (in
     * the domain) will be collapsed under the old one (in the co-domain).
     */
    public boolean areEqual(int result) {
        return isDomEqualsCod(result) || isCodSubsumesDom(result);
    }

    /** See {@link #compareShapes(PatternShape, PatternShape)}. */
    public boolean areIsomorphic(PatternShape dom, PatternShape cod) {
        int result = compareShapes(dom, cod).one();
        return areEqual(result);
    }

    /** Compares the two given shapes and returns a number formed by flags.*/
    public Pair<Integer,Morphism<PatternNode,PatternEdge>> compareShapes(PatternShape dom,
            PatternShape cod) {
        Pair<Integer,Morphism<PatternNode,PatternEdge>> result =
            new Pair<Integer,Morphism<PatternNode,PatternEdge>>(NON_ISO, null);
        if (!passBasicChecks(dom, cod)) {
            return result;
        }
        IsoChecker.IsoCheckerState state = new IsoCheckerState();
        Morphism<PatternNode,PatternEdge> morphism = getIsomorphism(dom, cod, state);
        int comparison = NON_ISO;
        while (morphism != null) {
            // We found an isomorphism between the graph structures.
            // Check for the extra conditions.
            comparison = checkIsomorphism(dom, cod, morphism);
            if (comparison != NON_ISO) {
                result = new Pair<Integer,Morphism<PatternNode,PatternEdge>>(comparison, morphism);
                break;
            } else {
                // Keep trying.
                morphism = getIsomorphism(dom, cod, state);
                if (morphism == null || state.isPlanEmpty()) {
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
    private int checkIsomorphism(PatternShape dom, PatternShape cod,
            Morphism<PatternNode,PatternEdge> morphism) {
        int result = DOM_EQUALS_COD | DOM_SUBSUMES_COD | COD_SUBSUMES_DOM;

        // First check the node multiplicities.
        Map<PatternNode,Multiplicity> domNodeMultMap = dom.getNodeMultMap();
        Map<PatternNode,Multiplicity> codNodeMultMap = cod.getNodeMultMap();
        for (Entry<PatternNode,PatternNode> nodeEntry : morphism.nodeMap().entrySet()) {
            PatternNode domNode = nodeEntry.getKey();
            PatternNode codNode = nodeEntry.getValue();
            Multiplicity domNMult = domNodeMultMap.get(domNode);
            Multiplicity codNMult = codNodeMultMap.get(codNode);
            int comparison = compareMultiplicities(domNMult, codNMult);
            result = updateResult(result, comparison);
            if (result == NON_ISO) {
                return NON_ISO;
            }
        }

        // Now check the edge multiplicities.
        Map<PatternEdge,Multiplicity> domEdgeMultMap = dom.getEdgeMultMap();
        Map<PatternEdge,Multiplicity> codEdgeMultMap = cod.getEdgeMultMap();
        for (Entry<PatternEdge,PatternEdge> edgeEntry : morphism.edgeMap().entrySet()) {
            PatternEdge domEdge = edgeEntry.getKey();
            PatternEdge codEdge = edgeEntry.getValue();
            Multiplicity domEMult = domEdgeMultMap.get(domEdge);
            Multiplicity codEMult = codEdgeMultMap.get(codEdge);
            int comparison = compareMultiplicities(domEMult, codEMult);
            result = updateResult(result, comparison);
            if (result == NON_ISO) {
                return NON_ISO;
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
    private boolean passBasicChecks(PatternShape dom, PatternShape cod) {
        return dom.nodeCount() == cod.nodeCount() && dom.edgeCount() == cod.edgeCount();
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
        if (domMult.subsumes(codMult)) {
            result |= DOM_SUBSUMES_COD;
        }
        if (codMult.subsumes(domMult)) {
            result |= COD_SUBSUMES_DOM;
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

    // ------------------------------------------------------------------------
    // Unimplemented methods
    // ------------------------------------------------------------------------

    @Override
    public boolean areIsomorphic(Graph dom, Graph cod, Object[] domValues, Object[] codValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <N extends Node,E extends Edge> Morphism<N,E> getIsomorphism(Graph dom, Graph cod) {
        throw new UnsupportedOperationException();
    }
}
