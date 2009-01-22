/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultIsoChecker.java,v 1.21 2007-11-29 12:44:41 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.util.Bag;
import groove.util.Groove;
import groove.util.HashBag;
import groove.util.Reporter;
import groove.util.SmallCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of an isomorphism checking algorithm that first tries to
 * decide isomorphism directly on the basis of a
 * {@link groove.graph.iso.CertificateStrategy}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultIsoChecker implements IsoChecker {
    /**
     * Empty constructor, for the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    private DefaultIsoChecker(boolean strong) {
        this.strong = strong;
    }

    public boolean areIsomorphic(Graph dom, Graph cod) {
        boolean result;
        // pre-calculate the node counts to take the time for
        // constructing the graph out of the isomorphism check time
        int domNodeCount = dom.nodeCount();
        int codNodeCount = cod.nodeCount();
        reporter.start(ISO_CHECK);
        if (domNodeCount != codNodeCount || dom.edgeCount() != cod.edgeCount()) {
            distinctSizeCount++;
            result = false;
        } else if (areGraphEqual(dom, cod)) {
            equalGraphsCount++;
            result = true;
        } else {
            CertificateStrategy domCertifier = dom.getCertifier(isStrong());
            CertificateStrategy codCertifier = cod.getCertifier(isStrong());
            result = areIsomorphic(domCertifier, codCertifier);
            if (ISO_ASSERT) {
                assert checkBisimulator(dom, cod, result);
                assert result == hasIsomorphism(new Bisimulator(dom), new Bisimulator(cod));
            }
        }
        reporter.stop();
        totalCheckCount++;
        return result;
    }

    private boolean areIsomorphic(CertificateStrategy domCertifier, CertificateStrategy codCertifier) {
        boolean result;
        if (!domCertifier.getGraphCertificate().equals(
            codCertifier.getGraphCertificate())) {
            if (ISO_PRINT) {
                System.err.println("Unequal graph certificates");
            }
            intCertOverlap++;
            result = false;
        } else if (hasDistinctCerts(codCertifier)) {
            reporter.start(ISO_CERT_CHECK);
            if (hasDistinctCerts(domCertifier)) {
                result = areCertEqual(domCertifier, codCertifier);
            } else {
                if (ISO_PRINT) {
                    System.err.println("Codomain has distinct certs but domain has not");
                }
                result = false;
            }
            reporter.stop();
            if (result) {
                equalCertsCount++;
            } else {
                distinctCertsCount++;
            }
        } else {
            reporter.start(ISO_SIM_CHECK);
            if (getNodePartitionCount(domCertifier) == getNodePartitionCount(codCertifier)) {
                result = hasIsomorphism(domCertifier, codCertifier);
            } else {
                if (ISO_PRINT) {
                    System.err.println("Unequal node partition counts");
                }
                result = false;
            }
            reporter.stop();
            if (result) {
                equalSimCount++;
            } else {
                distinctSimCount++;
            }
        }
        return result;
    }

    /**
     * Tries to construct an isomorphism between the two given graphs, and
     * reports if this succeeds.
     */
    public boolean hasIsomorphism(Graph dom, Graph cod) {
        return hasIsomorphism(dom.getCertifier(isStrong()), cod.getCertifier(isStrong()));
    }
    
    private boolean hasIsomorphism(CertificateStrategy domCertifier, CertificateStrategy codCertifier) {
        boolean result = computeIsomorphism(domCertifier, codCertifier) != null;
        return result;
    }

    /**
     * Tries to construct an isomorphism between the two given graphs. The
     * result is a bijective mapping from the nodes and edges of the source
     * graph to those of the target graph, or <code>null</code> if no such
     * mapping could be found.
     * @param dom the first graph to be compared
     * @param cod the second graph to be compared
     */
    public NodeEdgeMap getIsomorphism(Graph dom, Graph cod) {
        return getIsomorphism(dom.getCertifier(isStrong()), cod.getCertifier(isStrong()));
    }
    
    private NodeEdgeMap getIsomorphism(CertificateStrategy domCertifier, CertificateStrategy codCertifier) {
        NodeEdgeMap result = computeIsomorphism(domCertifier, codCertifier);
        if (result != null && result.nodeMap().size() != domCertifier.getGraph().nodeCount()) {
            // there's sure to be an isomorphism, but we have to add the
            // isolated nodes
            PartitionMap<Node> codPartitionMap =
                codCertifier.getNodePartitionMap();
            Set<Node> usedNodeImages = new HashSet<Node>();
            Certificate<Node>[] nodeCerts =
                domCertifier.getNodeCertificates();
            for (Certificate<Node> nodeCert : nodeCerts) {
                Node node = nodeCert.getElement();
                if (!result.containsKey(node)) {
                    // this is an isolated node
                    SmallCollection<Node> nodeImages =
                        codPartitionMap.get(nodeCert);
                    if (nodeImages.isSingleton()) {
                        // it follows that there is only one isolated node
                        result.putNode(node, nodeImages.getSingleton());
                        break;
                    } else {
                        // find an unused node
                        for (Node nodeImage : nodeImages) {
                            if (usedNodeImages.add(nodeImage)) {
                                result.putNode(node, nodeImage);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Tries to construct an isomorphism between the two given graphs, using
     * only the edges. The result is a bijective mapping from the non-isolated
     * nodes and edges of the source graph to those of the target graph, or
     * <code>null</code> if no such mapping could be found.
     * @param domCertifier the certificate strategy of the first graph to be compared
     * @param codCertifier the certificate strategy of the second graph to be compared
     */
    private NodeEdgeMap computeIsomorphism(CertificateStrategy domCertifier, CertificateStrategy codCertifier) {
        Graph dom = domCertifier.getGraph();
        Graph cod = codCertifier.getGraph();
        // make sure the graphs are of the same size
        if (dom.nodeCount() != cod.nodeCount()
            || dom.edgeCount() != cod.edgeCount()) {
            return null;
        }
        NodeEdgeMap result = new NodeEdgeHashMap();
        Set<Node> usedNodeImages = new HashSet<Node>();
        List<IsoSearchItem> plan =
            computePlan(domCertifier, codCertifier, result, usedNodeImages);
        if (plan == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Iterator<Edge>[] records = new Iterator[plan.size()];
        Node[] sourceImages = new Node[plan.size()];
        Node[] targetImages = new Node[plan.size()];
        if (ISO_PRINT) {
            System.err.printf("%nIsomorphism check: ");
        }
        int i = 0;
        while (i >= 0 && i < records.length) {
            if (ISO_PRINT) {
                System.err.printf("%d ", i);
            }
            IsoSearchItem item = plan.get(i);
            if (records[i] == null) {
                // we're moving forward
                records[i] = item.images.iterator();
            } else {
                // we're trying the next element of this record;
                // first wipe out the traces of the previous match
                if (!item.sourcePreMatched && sourceImages[i] != null) {
                    boolean removed = usedNodeImages.remove(sourceImages[i]);
                    assert removed : String.format(
                        "Image %s for source %s not present in used node set %s",
                        sourceImages[i], item.key.source(), usedNodeImages);
                    sourceImages[i] = null;
                }
                if (!item.targetPreMatched && targetImages[i] != null) {
                    boolean removed = usedNodeImages.remove(targetImages[i]);
                    assert removed : String.format(
                        "Image %s for target %s not present in used node set %s",
                        targetImages[i], item.key.opposite(), usedNodeImages);
                    targetImages[i] = null;
                }
            }
            if (!records[i].hasNext()) {
                // we're moving backward
                records[i] = null;
                i--;
            } else {
                Edge key = item.key;
                Edge image = records[i].next();
                if (item.sourcePreMatched) {
                    if (!result.getNode(key.source()).equals(image.source())) {
                        // the source node had a different image; take next edge
                        // image
                        continue;
                    }
                } else {
                    if (!usedNodeImages.add(image.source())) {
                        // injectivity is destroyed; take next edge image
                        continue;
                    }
                    result.putNode(key.source(), image.source());
                    sourceImages[i] = image.source();
                }
                if (item.targetPreMatched) {
                    // check if the old and new images coincide
                    if (!result.getNode(key.opposite()).equals(image.opposite())) {
                        // the target node had a different image; take next edge
                        // image
                        // but first roll back the choice of source node image
                        if (!item.sourcePreMatched) {
                            usedNodeImages.remove(sourceImages[i]);
                            sourceImages[i] = null;
                        }
                        continue;
                    }
                } else {
                    if (!usedNodeImages.add(image.opposite())) {
                        // injectivity is destroyed; take next edge image
                        // but first roll back the choice of source node image
                        if (!item.sourcePreMatched) {
                            usedNodeImages.remove(sourceImages[i]);
                            sourceImages[i] = null;
                        }
                        continue;
                    }
                    result.putNode(key.opposite(), image.opposite());
                    targetImages[i] = image.opposite();
                }
                result.putEdge(key, image);
                i++;
            }
        }
        if (i < 0) {
            if (ISO_PRINT) {
                System.err.printf("Failed%n");
            }
            return null;
        } else {
            if (ISO_PRINT) {
                System.err.printf("Succeeded%n");
            }
            assert checkIsomorphism(dom, cod, result) : String.format(
                "Erronous result using plan %s", plan);
            return result;
        }
    }

    private List<IsoSearchItem> computePlan(CertificateStrategy domCertifier, CertificateStrategy codCertifier,
            NodeEdgeMap resultMap, Set<Node> usedNodeImages) {
        Graph dom = domCertifier.getGraph();
        List<IsoSearchItem> result = new ArrayList<IsoSearchItem>();
        PartitionMap<Edge> codPartitionMap = codCertifier.getEdgePartitionMap();
        Map<Edge,Collection<Edge>> remainingEdgeSet =
            new HashMap<Edge,Collection<Edge>>();
        // the set of dom nodes that have an image in result, but whose incident
        // images possibly don't
        Set<Node> connectedNodes = new HashSet<Node>();
        Certificate<Edge>[] edgeCerts = domCertifier.getEdgeCertificates();
        // collect the pairs of edge keys and edge image sets
        int edgeCount = edgeCerts.length;
        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
            Certificate<Edge> edgeCert = edgeCerts[i];
            SmallCollection<Edge> images = codPartitionMap.get(edgeCert);
            if (images == null) {
                return null;
            } else if (images.isSingleton()) {
                if (!setEdge(edgeCert.getElement(), images.getSingleton(),
                    resultMap, connectedNodes, usedNodeImages)) {
                    return null;
                }
            } else {
                remainingEdgeSet.put(edgeCert.getElement(), images);
            }
        }
        // pick an edge key to start planning the next connected component
        while (!remainingEdgeSet.isEmpty()) {
            Iterator<Map.Entry<Edge,Collection<Edge>>> remainingEdgeIter =
                remainingEdgeSet.entrySet().iterator();
            Map.Entry<Edge,Collection<Edge>> first = remainingEdgeIter.next();
            remainingEdgeIter.remove();
            TreeSet<IsoSearchItem> subPlan = new TreeSet<IsoSearchItem>();
            subPlan.add(new IsoSearchItem(first.getKey(), first.getValue()));
            // repeatedly pick an edge from the component
            while (!subPlan.isEmpty()) {
                Iterator<IsoSearchItem> subIter = subPlan.iterator();
                IsoSearchItem next = subIter.next();
                subIter.remove();
                // add incident edges from the source node, if that was not
                // already matched
                Node keySource = next.key.source();
                next.sourcePreMatched = !connectedNodes.add(keySource);
                if (!next.sourcePreMatched) {
                    for (Edge edge : dom.edgeSet(keySource)) {
                        Collection<Edge> images = remainingEdgeSet.remove(edge);
                        if (images != null) {
                            subPlan.add(new IsoSearchItem(edge, images));
                        }
                    }
                }
                // add incident edges from the target node, if that was not
                // already matched
                Node keyTarget = next.key.opposite();
                next.targetPreMatched = !connectedNodes.add(keyTarget);
                if (!next.targetPreMatched) {
                    for (Edge edge : dom.edgeSet(keyTarget)) {
                        Collection<Edge> images = remainingEdgeSet.remove(edge);
                        if (images != null) {
                            subPlan.add(new IsoSearchItem(edge, images));
                        }
                    }
                }
                result.add(next);
            }
        }
        return result;
    }
//
//    /**
//     * Tries to construct an isomorphism between the two given graphs, using
//     * only the edges. The result is a bijective mapping from the non-isolated
//     * nodes and edges of the source graph to those of the target graph, or
//     * <code>null</code> if no such mapping could be found.
//     * @param dom the first graph to be compared
//     * @param cod the second graph to be compared
//     */
//    private NodeEdgeMap computeIsomorphism(Graph dom, Graph cod) {
//        // make sure the graphs are of the same size
//        if (dom.nodeCount() != cod.nodeCount()
//            || dom.edgeCount() != cod.edgeCount()) {
//            return null;
//        }
//        NodeEdgeMap result = new NodeEdgeHashMap();
//        PartitionMap<Edge> codPartitionMap =
//            cod.getCertifier(isStrong()).getEdgePartitionMap();
//        // the mapping has to be injective, so we remember the used cod nodes
//        Set<Node> usedNodeImages = new HashSet<Node>();
//        // the set of dom nodes that have an image in result, but whose incident
//        // images possibly don't
//        Set<Node> connectedNodes = new HashSet<Node>();
//        Map<Edge,Collection<Edge>> edgeImageMap =
//            new HashMap<Edge,Collection<Edge>>();
//        Certificate<Edge>[] edgeCerts =
//            dom.getCertifier(isStrong()).getEdgeCertificates();
//        // construct a mapping from the domain edges
//        // to either unique codomain edges or sets of them
//        int edgeCount = edgeCerts.length;
//        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
//            Certificate<Edge> edgeCert = edgeCerts[i];
//            SmallCollection<Edge> images = codPartitionMap.get(edgeCert);
//            if (images == null) {
//                return null;
//            } else if (images.isSingleton()) {
//                if (!setEdge(edgeCert.getElement(), images.getSingleton(),
//                    result, connectedNodes, usedNodeImages)) {
//                    return null;
//                }
//            } else {
//                edgeImageMap.put(edgeCert.getElement(), images);
//            }
//        }
//        while (!edgeImageMap.isEmpty()) {
//            if (connectedNodes.isEmpty()) {
//                // there are no edges connected to the part of the graph that
//                // is already mapped;
//                Iterator<Map.Entry<Edge,Collection<Edge>>> edgeImageEntryIter =
//                    edgeImageMap.entrySet().iterator();
//                Map.Entry<Edge,Collection<Edge>> edgeImageEntry =
//                    edgeImageEntryIter.next();
//                edgeImageEntryIter.remove();
//                Edge domEdge = edgeImageEntry.getKey();
//                // search a suitable value
//                boolean found = false;
//                search: for (Edge codEdge : edgeImageEntry.getValue()) {
//                    for (Node valueEnd : codEdge.ends()) {
//                        if (usedNodeImages.contains(valueEnd)) {
//                            continue search;
//                        }
//                    }
//                    // this image is OK
//                    for (int i = 0; i < domEdge.endCount(); i++) {
//                        Node domNode = domEdge.end(i);
//                        connectedNodes.add(domNode);
//                        Node codNode = codEdge.end(i);
//                        usedNodeImages.add(codNode);
//                        result.putNode(domNode, codNode);
//                    }
//                    result.putEdge(domEdge, codEdge);
//                    found = true;
//                    break;
//                }
//                if (!found) {
//                    return null;
//                }
//            } else {
//                Iterator<Node> connectedNodeIter = connectedNodes.iterator();
//                Node connectedNode = connectedNodeIter.next();
//                connectedNodeIter.remove();
//                for (Edge edge : dom.edgeSet(connectedNode)) {
//                    Collection<Edge> images = edgeImageMap.remove(edge);
//                    if (images != null
//                        && !selectEdge(edge, images, result, connectedNodes,
//                            usedNodeImages)) {
//                        // the edge is unmapped, and no suitable image can be
//                        // found
//                        return null;
//                    }
//                }
//            }
//        }
//        assert checkIsomorphism(dom, cod, result);
//        // assert dom.edgeSet().containsAll(result.edgeMap().keySet());
//        // assert cod.edgeSet().containsAll(result.edgeMap().values());
//        // assert result.nodeMap().keySet().equals(dom.nodeSet());
//        // assert result.nodeMap().keySet().equals(cod.nodeSet());
//        return result;
//    }
//
//    /**
//     * Inserts an edge from a set of possible edges into the result mapping, if
//     * one can be found that is consistent with the current state.
//     * @param key the dom edge to be inserted
//     * @param values the set of cod edges that should be tried as image of
//     *        <code>key</code>
//     * @param result the result map
//     * @param connectedNodes the set of dom nodes that are mapped but may have
//     *        unmapped incident edges
//     * @param usedCodNodes the set of node values in <code>result</code>
//     * @return <code>true</code> if the key/value-pair was successfully added
//     *         to <code>result</code>
//     */
//    private boolean selectEdge(Edge key, Collection<Edge> values,
//            NodeEdgeMap result, Set<Node> connectedNodes, Set<Node> usedCodNodes) {
//        int arity = key.endCount();
//        Node[] nodeImages = new Node[arity];
//        for (int i = 0; i < arity; i++) {
//            nodeImages[i] = result.getNode(key.end(i));
//        }
//        for (Edge value : values) {
//            // first test if this edge value is viable
//            boolean correct = true;
//            for (int i = 0; correct && i < key.endCount(); i++) {
//                if (nodeImages[i] == null) {
//                    correct = !usedCodNodes.contains(value.end(i));
//                } else {
//                    correct = nodeImages[i] == value.end(i);
//                }
//            }
//            if (correct) {
//                for (int i = 0; i < key.endCount(); i++) {
//                    if (nodeImages[i] == null) {
//                        Node nodeImage =
//                            result.putNode(key.end(i), value.end(i));
//                        assert nodeImage == null;
//                        connectedNodes.add(key.end(i));
//                        usedCodNodes.add(value.end(i));
//                    }
//                }
//                Edge edgeImage = result.putEdge(key, value);
//                assert edgeImage == null;
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Inserts an edge into the result mapping, testing if the resulting end
     * node mapping is consistent with the current state.
     * @param key the dom edge to be inserted
     * @param value the cod edge that is the image of <code>key</code>
     * @param result the result map
     * @param connectedNodes the set of dom nodes that are mapped but may have
     *        unmapped incident edges
     * @param usedCodNodes the set of node values in <code>result</code>
     * @return <code>true</code> if the key/value-pair was successfully added
     *         to <code>result</code>
     */
    private boolean setEdge(Edge key, Edge value, NodeEdgeMap result,
            Set<Node> connectedNodes, Set<Node> usedCodNodes) {
        for (int i = 0; i < key.endCount(); i++) {
            Node end = key.end(i);
            Node endImage = value.end(i);
            Node oldEndImage = result.putNode(end, endImage);
            if (oldEndImage == null) {
                if (!usedCodNodes.add(endImage)) {
                    return false;
                }
            } else if (oldEndImage != endImage) {
                return false;
            }
            connectedNodes.add(end);
        }
        result.putEdge(key, value);
        return true;
    }

    /**
     * Tests if the elements of a graph have all different certificates. If this
     * holds, then
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy)} can be
     * called to check for isomorphism.
     * @param certifier the graph to be tested
     * @return <code>true</code> if <code>graph</code> has distinct
     *         certificates
     */
    private boolean hasDistinctCerts(CertificateStrategy certifier) {
        return certifier.getNodePartitionMap().isOneToOne();
    }

    /**
     * Convenience method for
     * <code>graph.getCertificateStrategy().getNodePartitionCount()</code>.
     */
    private int getNodePartitionCount(CertificateStrategy certifier) {
        return certifier.getNodePartitionCount();
    }

    /**
     * Tests if an isomorphism can be constructed on the basis of distinct
     * certificates. It is assumed that <code>hasDistinctCerts(dom)</code>
     * holds.
     * @param dom the first graph to be tested
     * @param cod the second graph to be tested
     */
    private boolean areCertEqual(CertificateStrategy dom,
            CertificateStrategy cod) {
        boolean result;
        reporter.stop();
        reporter.stop();
        // the certificates uniquely identify the dom elements;
        // it suffices to test if this gives rise to a consistent one-to-one
        // node map
        // Certificate<Node>[] nodeCerts = dom.getNodeCertificates();
        Certificate<Edge>[] edgeCerts = dom.getEdgeCertificates();
        PartitionMap<Edge> codPartitionMap = cod.getEdgePartitionMap();
        reporter.restart(ISO_CHECK);
        reporter.restart(ISO_CERT_CHECK);
        result = true;
        // map to store dom-to-cod node mapping
        Map<Node,Node> nodeMap = new HashMap<Node,Node>();
        // // iterate over the dom node certificates
        // int nodeCount = nodeCerts.length;
        // for (int i = 0; result && i < nodeCount; i++) {
        // Certificate<Node> domNodeCert = nodeCerts[i];
        // SmallCollection<Node> image = codPartitionMap.get(domNodeCert);
        // result = image.isSingleton();
        // if (result) {
        // nodeMap.put(domNodeCert.getElement(), image.getSingleton());
        // }
        // }
        // iterate over the dom edge certificates
        int edgeCount = edgeCerts.length;
        for (int i = 0; result && i < edgeCount && edgeCerts[i] != null; i++) {
            Certificate<Edge> domEdgeCert = edgeCerts[i];
            SmallCollection<Edge> image = codPartitionMap.get(domEdgeCert);
            result = image != null && image.isSingleton();
            if (result) {
                Edge edgeKey = domEdgeCert.getElement();
                Edge edgeImage = image.getSingleton();
                int endCount = edgeKey.endCount();
                for (int end = 0; result && end < endCount; end++) {
                    Node endImage = nodeMap.get(edgeKey.end(end));
                    if (endImage == null) {
                        nodeMap.put(edgeKey.end(end), edgeImage.end(end));
                    } else {
                        result = endImage.equals(edgeImage.end(end));
                    }
                }
            }
        }
        if (ISO_PRINT) {
            if (!result) {
                System.err.printf("Graphs have distinct but unequal certificates%n");
            }
        }
        return result;
    }

    /**
     * This method wraps a node and edge set equality test on two graphs, under
     * the assumption that the node and edge counts are already known to
     * coincide.
     */
    private boolean areGraphEqual(Graph dom, Graph cod) {
        reporter.start(EQUALS_TEST);
        // boolean result = ((DeltaGraph)
        // dom).equalNodeEdgeSets((DeltaGraph)cod);
        Set<?> domEdgeSet = dom.edgeSet();
        Set<?> codEdgeSet = cod.edgeSet();
        boolean result = domEdgeSet.equals(codEdgeSet);
        // assert result == (dom.edgeCount() == 0 && dom.nodeCount() ==
        // cod.nodeCount()) || dom.nodeEdgeMap().equals(cod.nodeEdgeMap()):
        // "TreeStoreSet.equals wrongly gives "+result+"
        // on\n"+dom.nodeSet()+"\n"+cod.nodeSet()+"\n"+dom.edgeSet()+"\n"+cod.edgeSet();
        reporter.stop();
        return result;
    }

    private boolean checkIsomorphism(Graph dom, Graph cod, NodeEdgeMap map) {
        for (Edge edge : dom.edgeSet()) {
            if (edge.source() != edge.opposite()
                && !map.edgeMap().containsKey(edge)) {
                System.err.printf("Result contains no image for %s%n", edge);
                return false;
            }
        }
        for (Map.Entry<Edge,Edge> edgeEntry : map.edgeMap().entrySet()) {
            Edge key = edgeEntry.getKey();
            Edge value = edgeEntry.getValue();
            for (int i = 0; i < key.endCount(); i++) {
                if (!map.getNode(key.end(i)).equals(value.end(i))) {
                    System.err.printf(
                        "Edge %s mapped to %s, but end %s mapped to %s%n", key,
                        value, key.end(i), map.getNode(key.end(i)));
                    return false;
                }
            }
        }
        if (map.nodeMap().size() != new HashSet<Node>(map.nodeMap().values()).size()) {
            for (Map.Entry<Node,Node> first : map.nodeMap().entrySet()) {
                for (Map.Entry<Node,Node> second : map.nodeMap().entrySet()) {
                    if (first != second
                        && first.getValue() == second.getValue()) {
                        System.err.printf("Image of %s and %s both %s%n",
                            first.getKey(), second.getKey(), first.getValue());
                    }
                }
            }
            return false;
        }
        return true;
    }

    /** Method to be used in an assert on the correctness of isomorphism. */
    private boolean checkBisimulator(Graph dom, Graph cod, boolean result) {
        if (result && isStrong()) {
            CertificateStrategy domBis = new PartitionRefiner(dom);
            CertificateStrategy codBis = new PartitionRefiner(cod);
            Bag<Certificate<Node>> domNodes =
                new HashBag<Certificate<Node>>(
                    Arrays.asList(domBis.getNodeCertificates()));
            Bag<Certificate<Edge>> domEdges =
                new HashBag<Certificate<Edge>>(
                    Arrays.asList(domBis.getEdgeCertificates()));
            Bag<Certificate<Node>> codNodes =
                new HashBag<Certificate<Node>>(
                    Arrays.asList(codBis.getNodeCertificates()));
            Bag<Certificate<Edge>> codEdges =
                new HashBag<Certificate<Edge>>(
                    Arrays.asList(codBis.getEdgeCertificates()));
            Bag<Certificate<Node>> domMinCodNodes =
                new HashBag<Certificate<Node>>(domNodes);
            domMinCodNodes.removeAll(codNodes);
            assert domMinCodNodes.isEmpty() : String.format(
                "Node certificates %s in dom but not cod", domMinCodNodes);
            Bag<Certificate<Node>> codMinDomNodes =
                new HashBag<Certificate<Node>>(codNodes);
            codMinDomNodes.removeAll(domNodes);
            assert codMinDomNodes.isEmpty() : String.format(
                "Node certificates %s in cod but not cod", codMinDomNodes);
            Bag<Certificate<Edge>> domMinCodEdges =
                new HashBag<Certificate<Edge>>(domEdges);
            domMinCodEdges.removeAll(codEdges);
            assert domMinCodEdges.isEmpty() : String.format(
                "Edge certificates %s in dom but not cod", domMinCodEdges);
            Bag<Certificate<Edge>> codMinDomEdges =
                new HashBag<Certificate<Edge>>(codEdges);
            codMinDomEdges.removeAll(domEdges);
            assert codMinDomEdges.isEmpty() : String.format(
                "Edge certificates %s in cod but not cod", codMinDomEdges);
        }
        return true;
    }

    public boolean isStrong() {
        return this.strong;
    }

    /**
     * Sets the checker strength.
     * @see #isStrong()
     */
    public void setStrong(boolean strong) {
        this.strong = strong;
    }

    /**
     * Flag indicating the strength of the isomorphism check. If
     * <code>true</code>, no false negatives are returned.
     */
    private boolean strong;

    /**
     * Returns the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    static public DefaultIsoChecker getInstance(boolean strong) {
        return strong ? strongInstance : weakInstance;
    }

    /**
     * Returns the number of times an isomorphism was suspected on the basis of
     * the "early warning system", viz. the graph certificate.
     */
    static public int getIntCertOverlap() {
        return intCertOverlap;
    }

    /**
     * Returns the total time doing isomorphism-related computations. This
     * includes time spent in certificate calculation.
     */
    static public long getTotalTime() {
        return getIsoCheckTime() + getCertifyingTime();
    }

    /**
     * Returns the time spent calculating certificates, certificate maps and
     * partition maps in {@link PartitionRefiner}.
     */
    static public long getCertifyingTime() {
        return PartitionRefiner.reporter.getTotalTime(PartitionRefiner.COMPUTE_CERTIFICATES)
            + PartitionRefiner.reporter.getTotalTime(PartitionRefiner.GET_PARTITION_MAP);
    }

    /**
     * Returns the time spent checking for isomorphism. This does not include
     * the time spent computing isomorphism certificates; that is reported
     * instead by {@link #getCertifyingTime()}.
     */
    static public long getIsoCheckTime() {
        return reporter.getTotalTime(ISO_CHECK);
    }

    /**
     * Returns the time spent establishing isomorphism by direct equality.
     */
    static public long getEqualCheckTime() {
        return reporter.getTotalTime(EQUALS_TEST);
    }

    /**
     * Returns the time spent establishing isomorphism by certificate equality.
     */
    static public long getCertCheckTime() {
        return reporter.getTotalTime(ISO_CERT_CHECK);
    }

    /**
     * Returns the time spent establishing isomorphism by explicit simulation.
     */
    static public long getSimCheckTime() {
        return reporter.getTotalTime(ISO_SIM_CHECK);
    }

    /**
     * Returns the number of total checks performed, i.e., the number of calls
     * to {@link #areIsomorphic(Graph, Graph)}.
     */
    static public int getTotalCheckCount() {
        return totalCheckCount;
    }

    /**
     * Returns the number of times that non-isomorphism was established on the
     * basis of graph sizes.
     */
    static public int getDistinctSizeCount() {
        return distinctSizeCount;
    }

    /**
     * Returns the number of times that isomorphism was established on the basis
     * of graph equality.
     */
    static public int getEqualGraphsCount() {
        return equalGraphsCount;
    }

    /**
     * Returns the number of times that isomorphism was established on the basis
     * of (a one-to-one mapping betwen) certificates.
     */
    static public int getEqualCertsCount() {
        return equalCertsCount;
    }

    /**
     * Returns the number of times that non-isomorphism was established on the
     * basis of (a one-to-one mapping betwen) certificates.
     */
    static public int getDistinctCertsCount() {
        return distinctCertsCount;
    }

    /**
     * Returns the number of times that isomorphism was established on the basis
     * of simulation.
     */
    static public int getEqualSimCount() {
        return equalSimCount;
    }

    /**
     * Returns the number of times that isomorphism was established on the basis
     * of simulation.
     */
    static public int getDistinctSimCount() {
        return distinctSimCount;
    }
    
    /** 
     * If called with two file names, compares the graphs stored in those files
     * and reports whether they are isomorphic.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            testIso(args[0]);
        } else if (args.length == 2) {
            compareGraphs(args[0],args[1]);
        } else {
            System.err.println("Usage: DefaultIsoChecker file1 file2");
            return;
        }
    }

    private static void testIso(String name) {
        try {
            Graph graph1 = Groove.loadGraph(name);
            System.out.printf("Graph certificate: %s%n", graph1.getCertifier(true).getGraphCertificate());
            IsoChecker checker = new DefaultIsoChecker(true);
            for (int i = 0; i < 1000; i++) {
                Graph graph2 = new NodeSetEdgeSetGraph();
                NodeEdgeMap nodeMap = new NodeEdgeHashMap();
                for (Node node: graph1.nodeSet()) {
                    Node newNode = DefaultNode.createNode();
                    graph2.addNode(newNode);
                    nodeMap.putNode(node,newNode);
                }
                for (Edge edge: graph1.edgeSet()) {
                    graph2.addEdge(nodeMap.mapEdge(edge));
                }
                if (!checker.areIsomorphic(graph1, graph2)) {
                    System.err.println("Error! Graph not isomorphic to itself");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compareGraphs(String name1, String name2) {
        try {
            Graph graph1 = Groove.loadGraph(name1);
            Graph graph2 = Groove.loadGraph(name2);
            System.out.printf("Graphs '%s' and '%s' isomorphic?%n", name1,
                name2);
            System.out.printf("Done. Result: %b%n",
                new DefaultIsoChecker(true).areIsomorphic(graph1, graph2));
            System.out.printf("Certification time: %d%n", getCertifyingTime());
            System.out.printf("Simulation time: %d%n", getSimCheckTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** The singleton strong instance of this class. */
    static private final DefaultIsoChecker strongInstance =
        new DefaultIsoChecker(true);
    /** The singleton weak instance of this class. */
    static private final DefaultIsoChecker weakInstance =
        new DefaultIsoChecker(false);
    /** The total number of isomorphism checks. */
    static private int totalCheckCount;
    /**
     * The number of times graph sizes were compares and found to be different.
     */
    static private int distinctSizeCount;
    /**
     * The number of times graphs were compared based on their elements and
     * found to be isomorphic.
     */
    static private int equalGraphsCount;
    /**
     * The number of times graphs were compared based on their certificates and
     * found to be isomorphic.
     */
    static private int equalCertsCount;
    /**
     * The number of times graphs were compared based on their certificates and
     * found to be non-isomorphic.
     */
    static private int distinctCertsCount;
    /**
     * The number of times graphs were simulated and found to be isomorphic.
     */
    static private int equalSimCount;
    /**
     * The number of isomorphism warnings given while exploring the GTS.
     */
    static private int intCertOverlap = 0;
    /**
     * The number of times graphs were simulated and found to be non-isomorphic.
     */
    static private int distinctSimCount;
    /** Flag to switch printing on, for debugging purposes. */
    static private final boolean ISO_PRINT = false;
    /** Flag to switch assertions on, for debugging purposes. */
    static private final boolean ISO_ASSERT = false;
    /** Reporter instance for profiling IsoChecker methods. */
    static public final Reporter reporter = Reporter.register(IsoChecker.class);
    /** Handle for profiling {@link #areIsomorphic(Graph, Graph)}. */
    static public final int ISO_CHECK =
        reporter.newMethod("areIsomorphic(Graph,Graph)");
    /**
     * Handle for profiling
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy)}.
     */
    static final int ISO_CERT_CHECK =
        reporter.newMethod("Isomorphism by certificates");
    /** Handle for profiling isomorphism by simulation. */
    static final int ISO_SIM_CHECK =
        reporter.newMethod("Isomorphism by simulation");
    /** Handle for profiling {@link #areGraphEqual(Graph, Graph)}. */
    static final int EQUALS_TEST = reporter.newMethod("Equality test");

    private class IsoSearchPair implements Comparable<IsoSearchPair> {
        /** Constructs an instance from given data. */
        public IsoSearchPair(Edge key, Collection<Edge> images) {
            super();
            this.key = key;
            this.images = images;
        }

        public int compareTo(IsoSearchPair o) {
            // lower images set size is better
            int result = this.images.size() - o.images.size();
            if (result == 0) {
                // no criteria; just take the key edge
                result = this.key.compareTo(o.key);
            }
            return result;
        }

        /** The domain key of this record. */
        final Edge key;
        /**
         * The codomain images of this record; guaranteed to contain at least
         * two elements.
         */
        final Collection<Edge> images;
    }

    /**
     * Item in an isomorphism search plan
     */
    private class IsoSearchItem extends IsoSearchPair {
        /** Constructs an instance from given data. */
        public IsoSearchItem(Edge key, Collection<Edge> images) {
            super(key, images);
        }

        @Override
        public int compareTo(IsoSearchPair o) {
            // higher pre-match count is better
            int result =
                ((IsoSearchItem) o).getPreMatchCount()
                    - this.getPreMatchCount();
            if (result == 0) {
                result = super.compareTo(o);
            }
            return result;
        }

        private int getPreMatchCount() {
            int preMatchCount = 0;
            if (this.sourcePreMatched) {
                preMatchCount++;
            }
            if (this.targetPreMatched) {
                preMatchCount++;
            }
            return preMatchCount;
        }

        @Override
        public String toString() {
            return String.format("(%s,%s,%s,%s)", this.key, this.images,
                this.sourcePreMatched, this.targetPreMatched);
        }

        /** Flag indicating if the key source node has already been matched. */
        boolean sourcePreMatched;
        /** Flag indicating if the key target node has already been matched. */
        boolean targetPreMatched;
    }
}
