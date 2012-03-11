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
package groove.abstraction.neigh.shape;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.graph.TypeLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
class ShapeStore2 implements ShapeStore {
    /** Private constructor. */
    private ShapeStore2() {
        // empty
    }

    @Override
    public ShapeStore flatten(ShapeCache cache) {
        ShapeStore2 result = new ShapeStore2();
        result.edges = cache.getEdgeSet().toArray(new ShapeEdge[0]);
        char[] nodeEquivArray = flattenNodeEquiv(cache);
        result.nodeEquivArray = nodeEquivArray;
        result.nodeMultArray = flattenNodeMultMap(cache);
        result.inEdgeSigs = flattenEdgeSigSet(cache, INCOMING, nodeEquivArray);
        result.outEdgeSigs = flattenEdgeSigSet(cache, OUTGOING, nodeEquivArray);
        assert nodeEquivArray != null;
        return result;
    }

    /** Computes the flattened representation of an edge multiplicity map. */
    private Object[] flattenEdgeSigSet(ShapeCache cache, EdgeMultDir dir,
            char[] nodeEquiv) {
        Map<EdgeSignature,Multiplicity> sigSet =
            cache.getEdgeSigStore(dir).getMultMap();
        Object[] result = new Object[sigSet.size() * 4];
        int ix = 0;
        for (Map.Entry<EdgeSignature,Multiplicity> multEntry : sigSet.entrySet()) {
            EdgeSignature es = multEntry.getKey();
            result[ix] = es.getNode();
            ix++;
            result[ix] = es.getLabel();
            ix++;
            int nodeNr = es.getEquivClass().iterator().next().getNumber();
            result[ix] = getCanon(nodeEquiv[nodeNr]);
            ix++;
            result[ix] = multEntry.getValue();
            ix++;
        }
        return result;
    }

    /** Computes the flattened representation of the node multiplicity map. */
    private char[] flattenNodeMultMap(ShapeCache cache) {
        char[] result = new char[cache.getNodeStoreSize()];
        for (Map.Entry<ShapeNode,Multiplicity> multEntry : cache.getNodeMultMap().entrySet()) {
            result[multEntry.getKey().getNumber()] =
                multEntry.getValue().getIndex();
        }
        return result;
    }

    /** Computes the flattened representation of the node equivalence relation. */
    private char[] flattenNodeEquiv(ShapeCache cache) {
        char[] result = new char[cache.getNodeStoreSize()];
        char cellIx = 1;
        for (EquivClass<ShapeNode> cell : cache.getEquivRel()) {
            for (ShapeNode node : cell) {
                result[node.getNumber()] = cellIx;
            }
            cellIx++;
            assert cellIx != 0 : "Too many cells in the node partition";
        }
        return result;
    }

    @Override
    public void fill(ShapeCache cache) {
        assert this.edges != null;
        setNodeSet(cache);
        setEdgeSet(cache);
        setNodeEquiv(cache);
        setNodeMultMap(cache);
        setEdgeSigSets(cache);
    }

    private void setNodeSet(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        Set<ShapeNode> nodeSet = cache.createElementSet();
        char[] nodeEquiv = this.nodeEquivArray;
        for (int i = 0; i < nodeEquiv.length; i++) {
            if (nodeEquiv[i] != 0) {
                nodeSet.add(factory.getNode(i));
            }
        }
        cache.setNodeSet(nodeSet);
    }

    private void setEdgeSet(ShapeCache cache) {
        Set<ShapeEdge> edgeSet = cache.createElementSet();
        ShapeEdge[] edges = this.edges;
        for (int i = 0; i < edges.length; i++) {
            edgeSet.add(edges[i]);
        }
        cache.setEdgeSet(edgeSet);
    }

    private void setNodeEquiv(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        EquivRelation<ShapeNode> equivRel = cache.createNodeEquiv();
        char[] nodeEquiv = this.nodeEquivArray;
        List<EquivClass<ShapeNode>> cells =
            new ArrayList<EquivClass<ShapeNode>>(nodeEquiv.length);
        for (ShapeNode node : cache.getNodeSet()) {
            int cellIx = nodeEquiv[node.getNumber()];
            while (cellIx > cells.size()) {
                cells.add(new NodeEquivClass<ShapeNode>(factory));
            }
            cells.get(cellIx - 1).add(node);
        }
        equivRel.addAll(cells);
        cache.setEquivRel(equivRel);
    }

    private void setNodeMultMap(ShapeCache cache) {
        MyHashMap<ShapeNode,Multiplicity> nodeMultMap =
            cache.createNodeMultMap();
        char[] nodeMultArray = this.nodeMultArray;
        for (ShapeNode node : cache.getNodeSet()) {
            Multiplicity mult =
                Multiplicity.getMultiplicity(nodeMultArray[node.getNumber()],
                    MultKind.NODE_MULT);
            nodeMultMap.put(node, mult);
        }
        cache.setNodeMultMap(nodeMultMap);
    }

    private void setEdgeSigSets(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        char[] nodeEquivArray = this.nodeEquivArray;
        Map<EdgeMultDir,EdgeSignatureStore> edgeSigSets =
            cache.createEdgeSigSets();
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            EdgeSignatureStore edgeMultMap = edgeSigSets.get(dir);
            Object[] records =
                dir == EdgeMultDir.INCOMING ? this.inEdgeSigs
                        : this.outEdgeSigs;
            for (int i = 0; i < records.length;) {
                ShapeNode node = (ShapeNode) records[i];
                i++;
                TypeLabel label = (TypeLabel) records[i];
                i++;
                char targetEc = ((Character) records[i]).charValue();
                i++;
                EquivClass<ShapeNode> ec =
                    new NodeEquivClass<ShapeNode>(factory);
                for (int n = 0; n < nodeEquivArray.length; n++) {
                    if (nodeEquivArray[n] == targetEc) {
                        ec.add(factory.getNode(n));
                    }
                }
                EdgeSignature sig =
                    cache.getGraph().createEdgeSignature(dir, node, label, ec);
                Multiplicity mult = (Multiplicity) records[i];
                i++;
                edgeMultMap.setEdgeMult(sig, mult);
            }
        }
        cache.setEdgeSigSets(edgeSigSets);
    }

    /** Flattened set of edges, filled when the shape is fixed. */
    private ShapeEdge[] edges;
    /** Flattened node equivalence relation, filled when the shape is fixed. */
    private char[] nodeEquivArray;
    /** Flattened node multiplicity map, filled when the shape is fixed. */
    private char[] nodeMultArray;
    /** Flattened incoming edge multiplicity map, filled when the shape is fixed. */
    private Object[] inEdgeSigs;
    /** Flattened outgoing edge multiplicity map, filled when the shape is fixed. */
    private Object[] outEdgeSigs;

    /** Prototype instance of this store implementation. */
    public static final ShapeStore PROTOTYPE = new ShapeStore2();

    /** Returns a canonical {@link Character} wrapping a given {@code char} value. */
    private static Character getCanon(char v) {
        if (v >= canon.size()) {
            for (char i = (char) canon.size(); i <= v; i++) {
                canon.add(new Character(i));
            }
        }
        return canon.get(v);
    }

    private static final List<Character> canon = new ArrayList<Character>();
}
