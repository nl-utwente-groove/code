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
package groove.graph;

import groove.lts.GraphState;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.view.PostApplicationError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@link EdgeMultiplicityVerifier} class provides functionality for
 * verifying edge multiplicities in a {@link HostGraph}.
 * 
 * @Author Maarten de Mol
 */
public class EdgeMultiplicityVerifier {

    // The multiplicity verifiers of the type graph.
    // The first TypeLabel is for the TypeNode, the second for the TypeEdge.
    private final Map<TypeLabel,Map<TypeLabel,MultiplicityVerifier>> inVerifyMap;
    private final Map<TypeLabel,Map<TypeLabel,MultiplicityVerifier>> outVerifyMap;
    private final Set<MultiplicityVerifier> verifiers;

    // The errors that were the result of the last verification.
    private final Set<PostApplicationError> errors;

    /**
     * Default constructor. Collects the type edges that have a multiplicity,
     * and stores them for quick lookup.
     */
    public EdgeMultiplicityVerifier(TypeGraph type) {

        // initialize fields
        this.inVerifyMap =
            new HashMap<TypeLabel,Map<TypeLabel,MultiplicityVerifier>>();
        this.outVerifyMap =
            new HashMap<TypeLabel,Map<TypeLabel,MultiplicityVerifier>>();
        this.verifiers = new HashSet<MultiplicityVerifier>();
        this.errors = new HashSet<PostApplicationError>();

        // extract multiplicity verifiers from the type graph
        for (TypeEdge edge : type.edgeSet()) {
            if (edge.getInMult() != null) {
                MultiplicityVerifier verifier =
                    new MultiplicityVerifier(edge, InOrOut.MULT_IN);
                this.verifiers.add(verifier);
                for (TypeNode node : type.getSubtypes(edge.target())) {
                    mapVerifier(node, edge, verifier, this.inVerifyMap);
                }
            }
            if (edge.getOutMult() != null) {
                MultiplicityVerifier verifier =
                    new MultiplicityVerifier(edge, InOrOut.MULT_OUT);
                this.verifiers.add(verifier);
                for (TypeNode node : type.getSubtypes(edge.source())) {
                    mapVerifier(node, edge, verifier, this.outVerifyMap);
                }
            }
        }
    }

    /** Adds a verifier to a verifier map. */
    private void mapVerifier(TypeNode node, TypeEdge edge,
            MultiplicityVerifier verifier,
            Map<TypeLabel,Map<TypeLabel,MultiplicityVerifier>> map) {

        // add to outer level map
        Map<TypeLabel,MultiplicityVerifier> innerMap = map.get(node.label());
        if (innerMap == null) {
            innerMap = new HashMap<TypeLabel,MultiplicityVerifier>();
            map.put(node.label(), innerMap);
        }

        // add to inner level map
        innerMap.put(edge.label(), verifier);
    }

    /** Reset all verifiers and clear all stored errors. */
    public void reset() {
        for (MultiplicityVerifier verifier : this.verifiers) {
            verifier.reset();
        }
        this.errors.clear();
    }

    /** 
     * Count all the edges (that have a multiplicity restriction) of a single
     * {@link HostNode} in a given {@link HostGraph}. The type label of the
     * node type of the node must be given as an explicit argument.
     */
    public void count(HostGraph graph, HostNode node, TypeLabel nodeType) {

        // count in edges, if necessary
        Map<TypeLabel,MultiplicityVerifier> inMap =
            this.inVerifyMap.get(nodeType);
        if (inMap != null) {
            count(node, graph.inEdgeSet(node), inMap);
        }

        // count out edges, if necessary
        Map<TypeLabel,MultiplicityVerifier> outMap =
            this.outVerifyMap.get(nodeType);
        if (outMap != null) {
            count(node, graph.outEdgeSet(node), outMap);
        }
    }

    /** Count all given edges of a node in the given map. */
    private void count(HostNode node, Set<? extends HostEdge> edges,
            Map<TypeLabel,MultiplicityVerifier> map) {

        // create a node counter for each verifier
        for (MultiplicityVerifier verifier : map.values()) {
            verifier.addNodeCounter(node);
        }

        // increase the appropriate node counter for each edge
        for (HostEdge edge : edges) {
            MultiplicityVerifier verifier = map.get(edge.getType().label());
            if (verifier != null) {
                verifier.incNodeCounter(node);
            }
        }

    }

    /**
     * Count the occurrences of all {@link HostEdge}s in the argument
     * {@link HostGraph}.
     * TODO - optimize
     */
    public void count(HostGraph graph) {
        if (!this.verifiers.isEmpty()) {
            for (HostNode node : graph.nodeSet()) {
                count(graph, node, node.getType().label());
            }
        }
    }

    /** 
     * Check if the counters of the verifiers are within the ranges of their
     * respective multiplicities. The argument object indicates the source
     * object that the possible errors will be bound to. This should either be
     * a {@link HostGraph} or a {@link GraphState}.
     */
    public boolean check(Object source) {
        for (MultiplicityVerifier verifier : this.verifiers) {
            verifier.checkCounters(source);
        }
        return this.errors.isEmpty();
    }

    /** Getter for the stored errors. */
    public Set<PostApplicationError> getErrors() {
        return this.errors;
    }

    /**
     * Produce the {@link PostApplicationError} for a failed multiplicity
     * check.
     */
    private PostApplicationError multError(HostNode node, TypeLabel type,
            int count, Multiplicity mult, InOrOut direction, Object source) {
        String directionText;
        switch (direction) {
        case MULT_IN:
            directionText = "'in'";
            break;
        default:
            directionText = "'out'";
        }
        String msg =
            directionText + " multiplicity of edge '" + type
                + "' in node '%s' is out of range (got: " + count
                + "; required: " + mult.one() + ".."
                + (mult.isUnbounded() ? "*" : mult.two()) + ")";
        return new PostApplicationError(msg, node, source);
    }

    // ========================================================================
    // LOCAL ENUM: InOrOut
    // ========================================================================

    /** Distinction between 'in' and 'out' multiplicity. */
    private static enum InOrOut {
        MULT_IN, MULT_OUT
    }

    // ========================================================================
    // LOCAL CLASS: MultiplicityVerifier
    // ========================================================================

    /**
     * A {@link MultiplicityVerifier} provides the functionality for checking
     * the multiplicity of a single type edge. It can only verify one direction
     * of the edge (either 'in' or 'out'), and stores a mapping from instance
     * nodes to their number of occurrences.  
     */
    private class MultiplicityVerifier {

        // The multiplicity to be verified.
        private final TypeEdge typeEdge;
        private final InOrOut direction;

        // The number of occurrences counted so far.
        private final Map<HostNode,Counter> counters;

        /** Default constructor. */
        public MultiplicityVerifier(TypeEdge typeEdge, InOrOut direction) {
            this.typeEdge = typeEdge;
            this.direction = direction;
            this.counters = new HashMap<HostNode,Counter>();
        }

        /** Reset all counters. */
        public void reset() {
            this.counters.clear();
        }

        /** Add a node to be counted. */
        public void addNodeCounter(HostNode node) {
            this.counters.put(node, new Counter(0));
        }

        /** Increase the counter of a node (by one). */
        public void incNodeCounter(HostNode node) {
            this.counters.get(node).inc();
        }

        /** 
         * Check the validity of all the node counters, and create a
         * {@link PostApplicationError} in the surrounding
         * {@link EdgeMultiplicityVerifier} if this is not the case.
         */
        public void checkCounters(Object source) {
            for (Map.Entry<HostNode,Counter> entry : this.counters.entrySet()) {
                HostNode node = entry.getKey();
                int count = entry.getValue().value;
                Multiplicity mult;
                switch (this.direction) {
                case MULT_IN:
                    mult = this.typeEdge.getInMult();
                    break;
                default:
                    mult = this.typeEdge.getOutMult();
                }
                if (!mult.inRange(count)) {
                    EdgeMultiplicityVerifier.this.errors.add(multError(node,
                        this.typeEdge.label(), count, mult, this.direction,
                        source));
                }
            }
        }
    }

    // ========================================================================
    // LOCAL CLASS: Counter
    // ========================================================================

    /** {@link Counter} is a modifiable integer wrapped into an Object. */
    private static class Counter {

        // The wrapped value.
        public int value;

        /** Default constructor. */
        public Counter(int initialValue) {
            this.value = initialValue;
        }

        /** Increase the counter. */
        public void inc() {
            this.value++;
        }

    }
}
