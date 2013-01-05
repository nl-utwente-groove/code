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
package groove.grammar.type;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.FormatException;
import groove.grammar.model.PostApplicationError;
import groove.graph.Multiplicity;
import groove.lts.GraphState;

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
    private final Map<TypeNode,Map<TypeEdge,MultiplicityVerifier>> inVerifyMap;
    private final Map<TypeNode,Map<TypeEdge,MultiplicityVerifier>> outVerifyMap;
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
            new HashMap<TypeNode,Map<TypeEdge,MultiplicityVerifier>>();
        this.outVerifyMap =
            new HashMap<TypeNode,Map<TypeEdge,MultiplicityVerifier>>();
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
            Map<TypeNode,Map<TypeEdge,MultiplicityVerifier>> map) {

        // add to outer level map
        Map<TypeEdge,MultiplicityVerifier> innerMap = map.get(node);
        if (innerMap == null) {
            innerMap = new HashMap<TypeEdge,MultiplicityVerifier>();
            map.put(node, innerMap);
        }

        // add to inner level map
        innerMap.put(edge, verifier);
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
     * {@link HostNode} in a given {@link HostGraph}.
     */
    private void count(HostGraph graph, HostNode node) {

        // count in edges, if necessary
        Map<TypeEdge,MultiplicityVerifier> inMap =
            this.inVerifyMap.get(node.getType());
        if (inMap != null) {
            count(node, graph.inEdgeSet(node), inMap);
        }

        // count out edges, if necessary
        Map<TypeEdge,MultiplicityVerifier> outMap =
            this.outVerifyMap.get(node.getType());
        if (outMap != null) {
            count(node, graph.outEdgeSet(node), outMap);
        }
    }

    /** Count all given edges of a node in the given map. */
    private void count(HostNode node, Set<? extends HostEdge> edges,
            Map<TypeEdge,MultiplicityVerifier> map) {

        // create a node counter for each verifier
        for (MultiplicityVerifier verifier : map.values()) {
            verifier.addNodeCounter(node);
        }

        // increase the appropriate node counter for each edge
        for (HostEdge edge : edges) {
            MultiplicityVerifier verifier = map.get(edge.getType());
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
                count(graph, node);
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
        return !hasError();
    }

    /** Getter for the stored errors. Creates a fresh clone. */
    public Set<PostApplicationError> getErrors() {
        return new HashSet<PostApplicationError>(this.errors);
    }

    /** Checks if there is a stored error. */
    public boolean hasError() {
        return !this.errors.isEmpty();
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

    /**
     * Checks the edge multiplicities of a given {@link HostGraph} in the
     * context of a given {@link TypeGraph}.
     */
    public static void verifyMultiplicities(HostGraph graph, TypeGraph typeGraph)
        throws FormatException {
        EdgeMultiplicityVerifier verifier =
            new EdgeMultiplicityVerifier(typeGraph);
        verifier.count(graph);
        if (!verifier.check(graph)) {
            throw new FormatException(verifier.getErrors());
        }
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
