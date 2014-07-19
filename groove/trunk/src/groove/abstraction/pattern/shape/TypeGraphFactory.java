// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.abstraction.pattern.shape;

import groove.abstraction.MyHashMap;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectNode;
import groove.grammar.host.HostNode;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Class to load pattern type graphs from GXL files.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraphFactory {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Prefixes used in identities. */
    private static final char NODE_ID_PREFIX = 't';
    private static final char EDGE_ID_PREFIX = 'm';

    private static final TypeGraphFactory instance = new TypeGraphFactory();

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the singleton instance of this class. */
    public static TypeGraphFactory getInstance() {
        return instance;
    }

    private static int parseId(String id) {
        return Integer.parseInt(id.substring(1));
    }

    private static int parseNodeId(String id) throws IOException {
        if (id.charAt(0) != NODE_ID_PREFIX) {
            throw new IOException(String.format(
                "Cannot parse node ID: %s, invalid initial character.", id));
        }
        return parseId(id);
    }

    private static int parseEdgeId(String id) throws IOException {
        if (id.charAt(0) != EDGE_ID_PREFIX) {
            throw new IOException(String.format(
                "Cannot parse edge ID: %s, invalid initial character.", id));
        }
        return parseId(id);
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The type graph loaded. */
    private TypeGraph tGraph;
    /** Auxiliary maps. */
    private Map<Integer,TypeNode> tNodeMap;
    private Map<Integer,TypeEdge> tEdgeMap;
    private Map<Integer,HostNode> sNodeMap;
    private Map<AspectNode,TypeNode> aNodeMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private TypeGraphFactory() {
        this.tGraph = null;
        this.tNodeMap = new MyHashMap<Integer,TypeNode>();
        this.tEdgeMap = new MyHashMap<Integer,TypeEdge>();
        this.sNodeMap = new MyHashMap<Integer,HostNode>();
        this.aNodeMap = new MyHashMap<AspectNode,TypeNode>();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Converts an aspect graph into a type graph. */
    public TypeGraph toTypeGraph(AspectGraph aGraph) throws IOException {
        clearMaps();
        createTypeGraph(aGraph.getName());
        createTypeNodes(aGraph);
        createTypeEdges(aGraph);
        createPatterns(aGraph);
        createMorphisms();
        this.tGraph.setFixed();
        return this.tGraph;
    }

    private void clearMaps() {
        this.tNodeMap.clear();
        this.tEdgeMap.clear();
        this.sNodeMap.clear();
        this.aNodeMap.clear();
    }

    private void createTypeGraph(String name) {
        this.tGraph = new TypeGraph(name);
    }

    private void createTypeNodes(AspectGraph aGraph) throws IOException {
        // Iterate only over the self-edges that are remarks.
        for (AspectEdge aEdge : aGraph.edgeSet()) {
            AspectNode aSrc = aEdge.source();
            if (aEdge.getKind() == AspectKind.REMARK
                && aSrc.equals(aEdge.target())) {
                TypeNode tNode = getTypeNode(aEdge.getInnerText());
                this.aNodeMap.put(aSrc, tNode);
                // Also create the simple graph nodes because some may not
                // have labels.
                int sNodeNr = aSrc.getNumber();
                HostNode sNode = tNode.getPattern().addNode(sNodeNr);
                this.sNodeMap.put(sNodeNr, sNode);
            }
        }
    }

    private void createTypeEdges(AspectGraph aGraph) throws IOException {
        // Iterate only over the binary edges that are remarks.
        for (AspectEdge aEdge : aGraph.edgeSet()) {
            AspectNode aSrc = aEdge.source();
            if (aEdge.getKind() == AspectKind.REMARK
                && !aSrc.equals(aEdge.target())) {
                AspectNode aTgt = aEdge.target();
                TypeEdge tEdge = getTypeEdge(aEdge.getInnerText(), aSrc, aTgt);
                // Also update the simple graph morphism.
                HostNode sSrc = this.sNodeMap.get(aSrc.getNumber());
                HostNode sTgt = this.sNodeMap.get(aTgt.getNumber());
                tEdge.getMorphism().putNode(sSrc, sTgt);
            }
        }
    }

    private void createPatterns(AspectGraph aGraph) throws IOException {
        // Iterate over all normal edges.
        for (AspectEdge aEdge : aGraph.edgeSet()) {
            if (aEdge.getKind() == AspectKind.REMARK) {
                continue;
            }
            AspectNode aSrc = aEdge.source();
            AspectNode aTgt = aEdge.target();
            TypeNode tSrc = this.aNodeMap.get(aSrc);
            TypeNode tTgt = this.aNodeMap.get(aTgt);
            if (tSrc != tTgt) {
                throw new IOException(
                    String.format(
                        "Inconsistent pattern, source (%s) and target (%s) nodes are in distinct type nodes: %s, %s.",
                        aSrc, aTgt, tSrc, tTgt));
            }
            HostNode sSrc = this.sNodeMap.get(aSrc.getNumber());
            HostNode sTgt = this.sNodeMap.get(aTgt.getNumber());
            tSrc.getPattern().addEdge(sSrc, aEdge.getInnerText(), sTgt);
        }
    }

    private void createMorphisms() {
        // Go over all patterns from layer 2+ and extend the existing node
        // morphisms to edges.
        for (TypeEdge tEdge : this.tGraph.edgeSet()) {
            tEdge.extendMorphism();
        }
    }

    private TypeNode getTypeNode(String IdStr) throws IOException {
        int id = parseNodeId(IdStr);
        TypeNode result = this.tNodeMap.get(id);
        if (result == null) {
            result = this.tGraph.addNode(id);
            this.tNodeMap.put(id, result);
        }
        return result;
    }

    private TypeEdge getTypeEdge(String IdStr, AspectNode aSrc, AspectNode aTgt)
        throws IOException {
        int id = parseEdgeId(IdStr);
        TypeEdge result = this.tEdgeMap.get(id);
        if (result == null) {
            TypeNode tSrc = this.aNodeMap.get(aSrc);
            TypeNode tTgt = this.aNodeMap.get(aTgt);
            result = this.tGraph.addEdge(id, tSrc, tTgt);
            this.tEdgeMap.put(id, result);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a pattern type graph from the given file. */
    public static TypeGraph unmarshalTypeGraph(File file) throws IOException {
        return getInstance().toTypeGraph(
            AspectGraph.newInstance(Groove.loadGraph(file)));
    }
}