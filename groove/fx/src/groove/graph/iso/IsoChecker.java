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
 * $Id$
 */
package groove.graph.iso;

import java.io.File;
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

import groove.control.Valuator;
import groove.graph.AGraph;
import groove.graph.Edge;
import groove.graph.EdgeComparator;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy.EdgeCertificate;
import groove.graph.iso.CertificateStrategy.ElementCertificate;
import groove.graph.iso.CertificateStrategy.NodeCertificate;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainMorphism;
import groove.graph.plain.PlainNode;
import groove.util.Groove;
import groove.util.Reporter;
import groove.util.collect.Bag;
import groove.util.collect.HashBag;
import groove.util.collect.SmallCollection;

/**
 * Implementation of an isomorphism checking algorithm that first tries to
 * decide isomorphism directly on the basis of a
 * {@link groove.graph.iso.CertificateStrategy}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class IsoChecker {
    /**
     * Empty constructor, for the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    protected IsoChecker(boolean strong) {
        this.strong = strong;
    }

    /**
     * Tests if two graphs are isomorphic. Implementations of this method are
     * allowed to be incomplete, in the sense that a <code>false</code> answer
     * does not guarantee non-isomorphism, but a <code>true</code> answer does
     * guarantee isomorphism. Although a complete algorithm is optimal, for the
     * purpose of collapsing states an "almost" complete but faster algorithm is
     * better than a complete, slow one.
     * @param dom First graph to be tested
     * @param cod Second graph to be tested
     * @return <code>true</code> only if <code>dom</code> and
     *         <code>cod</code> are isomorphic
     */
    public <N extends Node,E extends Edge> boolean areIsomorphic(Graph dom, Graph cod) {
        return areIsomorphic(dom, cod, null, null);
    }

    /** Tests if two graphs, together with corresponding lists of nodes, are isomorphic. */
    public boolean areIsomorphic(Graph dom, Graph cod, Object[] domValues, Object[] codValues) {
        if (ISO_PRINT) {
            System.out.printf("Comparing: %n   %s%n   %s", dom, cod);
        }
        boolean result;
        if ((domValues == null) != (codValues == null)
            || (domValues != null && codValues != null && domValues.length != codValues.length)) {
            result = false;
            if (ISO_PRINT) {
                System.out.printf("DIFFERENT NODE COUNTS%n", dom, cod);
            }
        } else if (areGraphEqual(dom, cod, domValues, codValues)) {
            equalGraphsCount++;
            result = true;
        } else {
            if (ISO_PRINT) {
                System.out.printf("GRAPHS NOT EQUAL%n", dom, cod);
            }
            areIsoReporter.start();
            CertificateStrategy domCertifier = getCertifier(dom, true);
            CertificateStrategy codCertifier = getCertifier(cod, true);
            result = areIsomorphic(domCertifier, codCertifier, domValues, codValues);
            if (ISO_ASSERT) {
                assert checkBisimulator(dom, cod, result);
                assert result == hasIsomorphism(new Bisimulator(dom),
                    new Bisimulator(cod),
                    domValues,
                    codValues);
            }
            if (TEST_FALSE_NEGATIVES && result) {
                CertificateStrategy altDomCert =
                    this.certificateFactory.newInstance(dom, this.strong);
                CertificateStrategy altCodCert =
                    this.certificateFactory.newInstance(cod, this.strong);
                if (!areIsomorphic(altDomCert, altCodCert, domValues, codValues)) {
                    System.out.printf("Certifier '%s' gives a false negative on%n%s%n%s%n",
                        altDomCert.getClass(),
                        dom,
                        cod);
                    if (SAVE_FALSE_NEGATIVES) {
                        try {
                            File file1 = Groove.saveGraph(dom, "graph1");
                            File file2 = Groove.saveGraph(cod, "graph2");
                            System.out.printf("Graphs saved as '%s' and '%s'", file1, file2);
                            System.exit(0);
                        } catch (IOException exc) {
                            System.out.printf("Can't save graph: %s", exc.getMessage());
                        }
                    }
                }
            }
            areIsoReporter.stop();
        }
        totalCheckCount++;
        return result;
    }

    /**
     * This method wraps a node and edge set equality test on two graphs.
     * Optional arrays of nodes are also tested for equality; these may be
     * (simultaneously {@code null} but are otherwise guaranteed to be of
     * the same length
     * @param domValues list of nodes (from the domain) to compare
     * in addition to the graphs themselves; may be {@code null}
     * @param codValues list of nodes (from the codomain) to compare
     * in addition to the graphs themselves; may be {@code null}
     */
    private boolean areGraphEqual(Graph dom, Graph cod, Object[] domValues, Object[] codValues) {
        equalsTestReporter.start();
        // test if the value lists of domain and codomain coincide
        boolean result = (domValues == null || Valuator.areEqual(domValues, codValues));
        if (result) {
            CertificateStrategy domCertifier = getCertifier(dom, false);
            CertificateStrategy codCertifier = getCertifier(cod, false);
            int domNodeCount =
                domCertifier == null ? dom.nodeCount() : domCertifier.getNodeCertificates().length;
            int codNodeCount =
                codCertifier == null ? cod.nodeCount() : codCertifier.getNodeCertificates().length;
            result = domNodeCount == codNodeCount;
            if (result) {
                // test if the edge sets of domain and codomain coincide
                Set<?> domEdgeSet, codEdgeSet;
                if (domCertifier == null || codCertifier == null) {
                    // copy the edge set of the codomain to avoid sharing problems
                    codEdgeSet = new HashSet<Edge>(cod.edgeSet());
                    domEdgeSet = dom.edgeSet();
                } else {
                    codEdgeSet = codCertifier.getCertificateMap()
                        .keySet();
                    domEdgeSet = domCertifier.getCertificateMap()
                        .keySet();
                }
                result = domEdgeSet.equals(codEdgeSet);
            }
        }
        equalsTestReporter.stop();
        return result;
    }

    /**
     * Tests if two unequal graphs, given by their respective
     * certificate strategies, are isomorphic. Optional arrays of nodes are
     * also tested for isomorphism; these may be
     * (simultaneously {@code null} but are otherwise guaranteed to be of
     * the same length
     * @param domValues list of nodes (from the domain) to compare
     * in addition to the graphs themselves
     * @param codValues list of nodes (from the codomain) to compare
     * in addition to the graphs themselves
     */
    private boolean areIsomorphic(CertificateStrategy domCertifier,
        CertificateStrategy codCertifier, Object[] domValues, Object[] codValues) {
        boolean result;
        if (!domCertifier.getGraphCertificate()
            .equals(codCertifier.getGraphCertificate())) {
            if (ISO_PRINT) {
                System.out.printf("UNEQUAL GRAPH CERTIFICATES: %s versus %s%n",
                    domCertifier.getGraphCertificate(),
                    codCertifier.getGraphCertificate());
            }
            intCertOverlap++;
            result = false;
        } else if (hasDiscreteCerts(codCertifier)) {
            isoCertCheckReporter.start();
            if (hasDiscreteCerts(domCertifier)) {
                result = areCertEqual(domCertifier, codCertifier, domValues, codValues);
            } else {
                if (ISO_PRINT) {
                    System.out.println("Codomain has discrete partition but domain has not");
                }
                distinctCertsCount++;
                result = false;
            }
            isoCertCheckReporter.stop();
            if (result) {
                equalCertsCount++;
            } else {
                distinctCertsCount++;
            }
        } else {
            // EZ: don't start the reporter here otherwise we get spurious times
            // if ISO_PRINT == true.
            // isoSimCheckReporter.start();
            if (domCertifier.getNodePartitionCount() == codCertifier.getNodePartitionCount()) {
                isoSimCheckReporter.start();
                result = hasIsomorphism(domCertifier, codCertifier, domValues, codValues);
                isoSimCheckReporter.stop();
                // EZ: Moved the count here.
                if (result) {
                    equalSimCount++;
                } else {
                    distinctSimCount++;
                }
            } else {
                if (ISO_PRINT) {
                    System.out.println("Unequal node partition counts");
                }
                distinctCertsCount++;
                result = false;
            }
            // isoSimCheckReporter.stop();
            // EZ: We can't count this here because there are times when
            // the counters are incremented twice!
            /*if (result) {
                equalSimCount++;
            } else {
                distinctSimCount++;
            }*/
        }
        return result;
    }

    /**
     * Tests if an isomorphism can be constructed on the basis of distinct
     * certificates. It is assumed that <code>hasDistinctCerts(dom)</code>
     * holds.
     * @param dom the first graph to be tested
     * @param cod the second graph to be tested
     * @param domValues list of nodes (from the domain) to compare
     * in addition to the graphs themselves
     * @param codValues list of nodes (from the codomain) to compare
     * in addition to the graphs themselves
     */
    private boolean areCertEqual(CertificateStrategy dom, CertificateStrategy cod,
        Object[] domValues, Object[] codValues) {
        boolean result;
        // map to store dom-to-cod node mapping
        Morphism<Node,Edge> iso = getCertEqualIsomorphism(dom, cod);
        result = iso != null;
        if (result && domValues != null) {
            assert iso != null;
            // now test correspondence of the node arrays
            result = Valuator.areEqual(domValues, codValues, iso.nodeMap());
        }
        if (ISO_PRINT) {
            if (!result) {
                System.out.printf("Graphs have distinct but unequal certificates%n");
            }
        }
        return result;
    }

    /**
     * Constructs a isomorphism of the non-isolated nodes on the basis of distinct
     * certificates. It is assumed that <code>hasDistinctCerts(dom)</code>
     * holds, and that the node and edge counts of domain and codomain coincide.
     * Isolated nodes are <i>not</i> mapped. Note that, because of the distinctness
     * of the node certificates, there can be at most one isolated node of
     * every type.
     * @param dom certifier of the first graph to be tested
     * @param cod certifier of the second graph to be tested
     */
    @SuppressWarnings("unchecked")
    private Morphism<Node,Edge> getCertEqualIsomorphism(CertificateStrategy dom,
        CertificateStrategy cod) {
        Morphism<Node,Edge> result = (Morphism<Node,Edge>) dom.getGraph()
            .getFactory()
            .createMorphism();
        // the certificates uniquely identify the elements;
        // it is straightforward to construct a morphism
        // Go over the domain edges
        ElementCertificate<Edge>[] edgeCerts = dom.getEdgeCertificates();
        PartitionMap<Edge> codPartitionMap = cod.getEdgePartitionMap();
        int edgeCount = edgeCerts.length;
        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
            ElementCertificate<Edge> domEdgeCert = edgeCerts[i];
            SmallCollection<Edge> image = codPartitionMap.get(domEdgeCert);
            if (image == null) {
                result = null;
                break;
            }
            Edge edgeKey = domEdgeCert.getElement();
            Edge edgeImage = image.getSingleton();
            assert edgeImage != null; // image is known to be a singleton
            // add the source mapping to the result, and test for compatibility
            Node imageSource = edgeImage.source();
            Node oldSourceImage = result.putNode(edgeKey.source(), imageSource);
            if (oldSourceImage != null && !oldSourceImage.equals(imageSource)) {
                result = null;
                break;
            }
            // add the target mapping to the result, and test for compatibility
            Node imageTarget = edgeImage.target();
            Node oldTargetImage = result.putNode(edgeKey.target(), imageTarget);
            if (oldTargetImage != null && !oldTargetImage.equals(imageTarget)) {
                result = null;
                break;
            }
            result.putEdge(edgeKey, edgeImage);
        }
        return result;
    }

    private boolean hasIsomorphism(CertificateStrategy domCertifier,
        CertificateStrategy codCertifier, Object[] domValues, Object[] codValues) {
        boolean result;
        IsoCheckerState state = new IsoCheckerState();
        // repeatedly look for the next isomorphism until one is found
        // that also maps the domain and codomain nodes correctly
        do {
            Morphism<Node,Edge> iso = computeIsomorphism(domCertifier, codCertifier, state);
            result = iso != null;
            if (result && domValues != null) {
                assert iso != null;
                result = Valuator.areEqual(domValues, codValues, iso.nodeMap());
            } else {
                break;
            }
        } while (!result);
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
    public <N extends Node,E extends Edge> Morphism<N,E> getIsomorphism(Graph dom, Graph cod) {
        return getIsomorphism(getCertifier(dom, true), getCertifier(cod, true), null);
    }

    /**
     * Tries to construct the next isomorphism between the two given graphs. The
     * result is a bijective mapping from the nodes and edges of the source
     * graph to those of the target graph, or <code>null</code> if no such
     * mapping could be found. A third parameter stores the state of the isomorphism search;
     * each successive call (with the same state object) returns the next isomorphism.
     * @param dom the first graph to be compared
     * @param cod the second graph to be compared
     * @param state the state for the iso checker
     */
    public <N extends Node,E extends Edge> Morphism<N,E> getIsomorphism(Graph dom, Graph cod,
        IsoCheckerState state) {
        return getIsomorphism(getCertifier(dom, true), getCertifier(cod, true), state);
    }

    @SuppressWarnings("unchecked")
    private <N extends Node,E extends Edge> Morphism<N,E> getIsomorphism(
        CertificateStrategy domCertifier, CertificateStrategy codCertifier, IsoCheckerState state) {
        Morphism<Node,Edge> result = computeIsomorphism(domCertifier, codCertifier, state);
        // test if there are isolated nodes unaccounted for
        if (result != null && result.nodeMap()
            .size() != domCertifier.getGraph()
                .nodeCount()) {
            // there's sure to be an isomorphism, but we have to add the
            // isolated nodes
            PartitionMap<Node> codPartitionMap = codCertifier.getNodePartitionMap();
            Set<Node> usedNodeImages = new HashSet<>();
            NodeCertificate[] nodeCerts = domCertifier.getNodeCertificates();
            for (NodeCertificate nodeCert : nodeCerts) {
                Node node = nodeCert.getElement();
                if (!result.nodeMap()
                    .containsKey(node)) {
                    // this is an isolated node
                    SmallCollection<Node> nodeImages = codPartitionMap.get(nodeCert);
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
        return (Morphism<N,E>) result;
    }

    /**
     * Tries to construct an isomorphism between the two given graphs, using
     * only the edges. The result is a bijective mapping from the non-isolated
     * nodes and edges of the source graph to those of the target graph, or
     * <code>null</code> if no such mapping could be found.
     * @param domCertifier the certificate strategy of the first graph to be
     *        compared
     * @param codCertifier the certificate strategy of the second graph to be
     *        compared
     * @param state intermediate state of the isomorphism search, passed
     *        between successive calls to this method to search for the
     *        next isomorphism. If {@code null}, only the first
     *        isomorphism will be found.
     */
    @SuppressWarnings("unchecked")
    private <N extends Node,E extends Edge> Morphism<N,E> computeIsomorphism(
        CertificateStrategy domCertifier, CertificateStrategy codCertifier, IsoCheckerState state) {
        // make sure the graphs are of the same size
        Graph dom = domCertifier.getGraph();
        Graph cod = codCertifier.getGraph();
        if (dom.nodeCount() != cod.nodeCount() || dom.edgeCount() != cod.edgeCount()) {
            return null;
        }
        // make sure the certificate counts are equal
        if (domCertifier.getNodeCertificates().length != codCertifier.getNodeCertificates().length
            || domCertifier.getEdgeCertificates().length != codCertifier
                .getEdgeCertificates().length) {
            return null;
        }
        if (hasDiscreteCerts(domCertifier)) {
            if (state != null) {
                if (state.foundCertBijection) {
                    return null;
                } else {
                    state.foundCertBijection = true;
                }
            }
            return (Morphism<N,E>) getCertEqualIsomorphism(domCertifier, codCertifier);
        }
        Morphism<Node,Edge> result;
        Set<Node> usedNodeImages;

        // Compute a new plan or restore the one from the state.
        List<IsoSearchItem> plan;
        if (state != null && state.plan != null) {
            // use the plan computed the previous time around
            plan = state.plan;
            if (state.result == null || state.i == plan.size()) {
                // there are no more results to be found
                return null;
            } else {
                usedNodeImages = new HashSet<>(state.usedNodeImages);
                result = state.result.clone();
            }
        } else {
            // construct the search plan
            result = (Morphism<Node,Edge>) domCertifier.getGraph()
                .getFactory()
                .createMorphism();
            usedNodeImages = new HashSet<>();
            plan = computePlan(domCertifier, codCertifier, result, usedNodeImages);
        }
        if (plan == null) {
            return null;
        }

        // Create new records and images or restore the ones from the state.
        Iterator<Edge>[] records;
        Node[] sourceImages;
        Node[] targetImages;
        if (state != null && state.records != null) {
            records = state.records;
        } else {
            records = new Iterator[plan.size()];
        }
        if (state != null && state.sourceImages != null) {
            sourceImages = state.sourceImages;
        } else {
            sourceImages = new Node[plan.size()];
        }
        if (state != null && state.targetImages != null) {
            targetImages = state.targetImages;
        } else {
            targetImages = new Node[plan.size()];
        }

        if (ISO_PRINT) {
            System.out.printf("%nIsomorphism check: ");
        }
        int i;
        if (state != null) {
            i = state.i;
        } else {
            i = 0;
        }
        while (i >= 0 && i < records.length) {
            if (ISO_PRINT) {
                System.out.printf("%d ", i);
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
                        sourceImages[i],
                        item.key.source(),
                        usedNodeImages);
                    sourceImages[i] = null;
                }
                if (!item.targetPreMatched && targetImages[i] != null) {
                    boolean removed = usedNodeImages.remove(targetImages[i]);
                    assert removed : String.format(
                        "Image %s for target %s not present in used node set %s",
                        targetImages[i],
                        item.key.target(),
                        usedNodeImages);
                    targetImages[i] = null;
                }
            }
            if (!records[i].hasNext()) {
                // we're moving backward
                records[i] = null;
                i--;
            } else {
                Edge key = item.key;
                Node keyTarget = key.target();
                Node keySource = key.source();
                Edge image = records[i].next();
                Node imageSource = image.source();
                Node imageTarget = image.target();
                if (item.sourcePreMatched) {
                    if (!result.getNode(keySource)
                        .equals(imageSource)) {
                        // the source node had a different image; take next edge
                        // image
                        continue;
                    }
                } else {
                    if (!usedNodeImages.add(imageSource)) {
                        // injectivity is destroyed; take next edge image
                        continue;
                    }
                    result.putNode(keySource, imageSource);
                    sourceImages[i] = imageSource;
                }
                if (item.targetPreMatched) {
                    // check if the old and new images coincide
                    if (!result.getNode(keyTarget)
                        .equals(imageTarget)) {
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
                    if (!usedNodeImages.add(imageTarget)) {
                        // injectivity is destroyed; take next edge image
                        // but first roll back the choice of source node image
                        if (!item.sourcePreMatched) {
                            usedNodeImages.remove(sourceImages[i]);
                            sourceImages[i] = null;
                        }
                        continue;
                    }
                    result.putNode(keyTarget, imageTarget);
                    targetImages[i] = imageTarget;
                }
                result.putEdge(key, image);
                i++;
            }
        }
        if (i < 0) {
            if (ISO_PRINT) {
                System.out.printf("Failed%n");
            }
            return null;
        } else {
            if (ISO_PRINT) {
                System.out.printf("Succeeded%n");
            }
            assert checkIsomorphism(domCertifier.getGraph(), result) : String
                .format("Erronous result using plan %s", plan);
            // Store the variables in the state.
            if (state != null) {
                state.plan = plan;
                state.result = result.clone();
                state.usedNodeImages = new HashSet<>(usedNodeImages);
                state.sourceImages = sourceImages;
                state.targetImages = targetImages;
                state.records = records;
                state.i = i - 1;
            }
            return (Morphism<N,E>) result;
        }
    }

    /** Constructs a search plan for isomorphism.
     * @param domCertifier certifier for the domain of the prospective isomorphism
     * @param codCertifier certifier for the codomain of the prospective isomorphism
     * @param resultMap part of the isomorphism that can already be constructed,
     * on the basis of unique edge certificates
     * @param usedNodeImages set of codomain nodes already used as images
     * for the part of the isomorphism that can be directly constructed
     * @return the constructed search plan; {@code null} if the
     * domain and codomain certificates do not allow an isomorphism to be constructed
     */
    private List<IsoSearchItem> computePlan(CertificateStrategy domCertifier,
        CertificateStrategy codCertifier, Morphism<Node,Edge> resultMap, Set<Node> usedNodeImages) {
        Graph dom = domCertifier.getGraph();
        List<IsoSearchItem> result = new ArrayList<>();
        PartitionMap<Edge> codPartitionMap = codCertifier.getEdgePartitionMap();
        Map<Edge,Collection<Edge>> remainingEdgeSet = new HashMap<>();
        // the set of dom nodes that have an image in result, but whose incident
        // images possibly don't
        Set<Node> connectedNodes = new HashSet<>();
        ElementCertificate<Edge>[] edgeCerts = domCertifier.getEdgeCertificates();
        // collect the pairs of edge keys and edge image sets
        int edgeCount = edgeCerts.length;
        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
            ElementCertificate<Edge> edgeCert = edgeCerts[i];
            SmallCollection<Edge> images = codPartitionMap.get(edgeCert);
            if (images == null) {
                return null;
            } else if (images.isSingleton()) {
                if (!setEdge(edgeCert.getElement(),
                    images.getSingleton(),
                    resultMap,
                    connectedNodes,
                    usedNodeImages)) {
                    return null;
                }
            } else {
                remainingEdgeSet.put(edgeCert.getElement(), images);
            }
        }
        // pick an edge key to start planning the next connected component
        while (!remainingEdgeSet.isEmpty()) {
            Iterator<Map.Entry<Edge,Collection<Edge>>> remainingEdgeIter =
                remainingEdgeSet.entrySet()
                    .iterator();
            Map.Entry<Edge,Collection<Edge>> first = remainingEdgeIter.next();
            remainingEdgeIter.remove();
            TreeSet<IsoSearchItem> subPlan = new TreeSet<>();
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
                Node keyTarget = next.key.target();
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
    private boolean setEdge(Edge key, Edge value, Morphism<Node,Edge> result,
        Set<Node> connectedNodes, Set<Node> usedCodNodes) {
        if (!setNode(key.source(), value.source(), result, connectedNodes, usedCodNodes)) {
            return false;
        }
        if (!setNode(key.target(), value.target(), result, connectedNodes, usedCodNodes)) {
            return false;
        }
        result.putEdge(key, value);
        return true;
    }

    /**
     * Inserts a node into the result mapping, testing if this is consistent.
     */
    private boolean setNode(Node end, Node endImage, Morphism<Node,Edge> result,
        Set<Node> connectedNodes, Set<Node> usedCodNodes) {
        Node oldEndImage = result.putNode(end, endImage);
        if (oldEndImage == null) {
            if (!usedCodNodes.add(endImage)) {
                return false;
            }
        } else if (oldEndImage != endImage) {
            return false;
        }
        connectedNodes.add(end);
        return true;
    }

    /**
     * Tests if the nodes of a graph have all different certificates.
     * @param certifier the graph to be tested
     * @return <code>true</code> if the graph has distinct
     *         node certificates
     */
    private boolean hasDiscreteNodeCerts(CertificateStrategy certifier) {
        return certifier.getNodePartitionMap()
            .isOneToOne()
            && certifier.getEdgePartitionMap()
                .isOneToOne();
    }

    /**
     * Tests if the elements of a graph have all different certificates. If this
     * holds, then
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy, Object[], Object[])} can be
     * called to check for isomorphism.
     * @param certifier the graph to be tested
     * @return <code>true</code> if <code>graph</code> has distinct
     *         certificates
     */
    private boolean hasDiscreteCerts(CertificateStrategy certifier) {
        return hasDiscreteNodeCerts(certifier) && certifier.getEdgePartitionMap()
            .isOneToOne();
    }

    /**
     * Retrieve or construct a certifier for a give graph.
     * A parameter controls whether a certifier is always returned, or only
     * if one is already constructed.
     * @param graph the graph for which the certifier is requested
     * @param always if {@code true}, the certifier should always be
     * constructed; otherwise, it is only retrieved from the graph if the graph
     * has already stored a certifier.
     */
    public CertificateStrategy getCertifier(Graph graph, boolean always) {
        CertificateStrategy result = null;
        if (graph instanceof AGraph) {
            if (always || ((AGraph<?,?>) graph).hasCertifier(isStrong())) {
                result = ((AGraph<?,?>) graph).getCertifier(isStrong());
            }
        } else if (always) {
            result = AGraph.getCertificateFactory()
                .newInstance(graph, isStrong());
        }
        return result;
    }

    private boolean checkIsomorphism(Graph dom, Morphism<Node,Edge> map) {
        for (Edge edge : dom.edgeSet()) {
            if (!edge.isLoop() && !map.edgeMap()
                .containsKey(edge)) {
                System.out.printf("Result contains no image for %s%n", edge);
                return false;
            }
        }
        for (Map.Entry<Edge,Edge> edgeEntry : map.edgeMap()
            .entrySet()) {
            Edge key = edgeEntry.getKey();
            Node keySource = key.source();
            Node keyTarget = key.target();
            Edge value = edgeEntry.getValue();
            if (!map.getNode(keySource)
                .equals(value.source())) {
                System.out.printf("Edge %s mapped to %s, but source mapped to %s%n",
                    key,
                    value,
                    map.getNode(keySource));
                return false;
            }
            if (!map.getNode(keyTarget)
                .equals(value.target())) {
                System.out.printf("Edge %s mapped to %s, but target mapped to %s%n",
                    key,
                    value,
                    map.getNode(keyTarget));
                return false;
            }
        }
        if (map.nodeMap()
            .size() != new HashSet<>(map.nodeMap()
                .values()).size()) {
            for (Map.Entry<Node,Node> first : map.nodeMap()
                .entrySet()) {
                for (Map.Entry<Node,Node> second : map.nodeMap()
                    .entrySet()) {
                    if (first != second && first.getValue() == second.getValue()) {
                        System.out.printf("Image of %s and %s both %s%n",
                            first.getKey(),
                            second.getKey(),
                            first.getValue());
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
            CertificateStrategy domBis = new PartitionRefiner(dom, isStrong());
            CertificateStrategy codBis = new PartitionRefiner(cod, isStrong());
            Bag<NodeCertificate> domNodes =
                new HashBag<>(Arrays.asList(domBis.getNodeCertificates()));
            Bag<EdgeCertificate> domEdges =
                new HashBag<>(Arrays.asList(domBis.getEdgeCertificates()));
            Bag<NodeCertificate> codNodes =
                new HashBag<>(Arrays.asList(codBis.getNodeCertificates()));
            Bag<EdgeCertificate> codEdges =
                new HashBag<>(Arrays.asList(codBis.getEdgeCertificates()));
            Bag<NodeCertificate> domMinCodNodes = new HashBag<>(domNodes);
            domMinCodNodes.removeAll(codNodes);
            assert domMinCodNodes.isEmpty() : String
                .format("Node certificates %s in dom but not cod", domMinCodNodes);
            Bag<NodeCertificate> codMinDomNodes = new HashBag<>(codNodes);
            codMinDomNodes.removeAll(domNodes);
            assert codMinDomNodes.isEmpty() : String
                .format("Node certificates %s in cod but not cod", codMinDomNodes);
            Bag<EdgeCertificate> domMinCodEdges = new HashBag<>(domEdges);
            domMinCodEdges.removeAll(codEdges);
            assert domMinCodEdges.isEmpty() : String
                .format("Edge certificates %s in dom but not cod", domMinCodEdges);
            Bag<EdgeCertificate> codMinDomEdges = new HashBag<>(codEdges);
            codMinDomEdges.removeAll(domEdges);
            assert codMinDomEdges.isEmpty() : String
                .format("Edge certificates %s in cod but not cod", codMinDomEdges);
        }
        return true;
    }

    /**
     * Indicates if the checker is currently set to strong.
     * If the checker is strong, no false negatives will be returned.
     */
    public synchronized boolean isStrong() {
        return this.strong;
    }

    /**
     * Flag indicating the strength of the isomorphism check. If
     * <code>true</code>, no false negatives are returned.
     */
    private final boolean strong;

    /**
     * Returns the singleton instance of this class.
     * @param strong if <code>true</code>, the checker will not returns false
     *        negatives.
     */
    static public IsoChecker getInstance(boolean strong) {
        // initialise lazily to avoid initialisation circularities
        if (strongInstance == null) {
            strongInstance = new IsoChecker(true);
            weakInstance = new IsoChecker(false);

        }
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
        return CertificateStrategy.computeCertReporter.getTotalTime()
            + CertificateStrategy.getPartitionReporter.getTotalTime();
    }

    /**
     * Returns the time spent checking for isomorphism. This does not include
     * the time spent computing isomorphism certificates; that is reported
     * instead by {@link #getCertifyingTime()}.
     */
    static public long getIsoCheckTime() {
        return areIsoReporter.getTotalTime();
    }

    /**
     * Returns the time spent establishing isomorphism by direct equality.
     */
    static public long getEqualCheckTime() {
        return equalsTestReporter.getTotalTime();
    }

    /**
     * Returns the time spent establishing isomorphism by certificate equality.
     */
    static public long getCertCheckTime() {
        return isoCertCheckReporter.getTotalTime();
    }

    /**
     * Returns the time spent establishing isomorphism by explicit simulation.
     */
    static public long getSimCheckTime() {
        return isoSimCheckReporter.getTotalTime();
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
        try {
            if (args.length == 1) {
                testIso(args[0]);
            } else if (args.length == 2) {
                compareGraphs(args[0], args[1]);
            } else {
                System.out.println("Usage: DefaultIsoChecker file1 file2");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testIso(String name) throws IOException {
        PlainGraph graph1 = Groove.loadGraph(name);
        IsoChecker checker = getInstance(true);
        System.out.printf("Graph certificate: %s%n", checker.getCertifier(graph1, true)
            .getGraphCertificate());
        for (int i = 0; i < 1000; i++) {
            PlainGraph graph2 = new PlainGraph(name, GraphRole.NONE);
            PlainMorphism nodeMap = new PlainMorphism();
            for (PlainNode node : graph1.nodeSet()) {
                PlainNode newNode = graph2.addNode();
                nodeMap.putNode(node, newNode);
            }
            for (PlainEdge edge : graph1.edgeSet()) {
                graph2.addEdgeContext(nodeMap.mapEdge(edge));
            }
            if (!checker.areIsomorphic(graph1, graph2)) {
                System.out.println("Error! Graph not isomorphic to itself");
            }
        }
    }

    @SuppressWarnings({})
    private static void compareGraphs(String name1, String name2) throws IOException {
        PlainGraph graph1 = Groove.loadGraph(name1);
        PlainGraph graph2 = Groove.loadGraph(name2);
        System.out.printf("Graphs '%s' and '%s' isomorphic?%n", name1, name2);
        System.out.printf("Done. Result: %b%n",
            (IsoChecker.getInstance(true)).areIsomorphic(graph1, graph2));
        System.out.printf("Certification time: %d%n", getCertifyingTime());
        System.out.printf("Simulation time: %d%n", getSimCheckTime());
    }

    /** The singleton strong instance of this class. */
    static private IsoChecker strongInstance;
    /** The singleton weak instance of this class. */
    static private IsoChecker weakInstance;
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
    /**
     * Flag to check for false negatives in the certification, for debugging
     * purposes.
     */
    static private final boolean TEST_FALSE_NEGATIVES = false;
    /**
     * Flag to save false negatives and exit
     */
    static private final boolean SAVE_FALSE_NEGATIVES = false;
    /** Flag to switch assertions on, for debugging purposes. */
    static private final boolean ISO_ASSERT = false;
    /** Reporter instance for profiling IsoChecker methods. */
    static public final Reporter reporter = Reporter.register(IsoChecker.class);
    /** Handle for profiling {@link #areIsomorphic(Graph, Graph)}. */
    static public final Reporter areIsoReporter = reporter.register("areIsomorphic(Graph,Graph)");
    /**
     * Handle for profiling
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy, Object[], Object[])}.
     */
    static final Reporter isoCertCheckReporter = reporter.register("Isomorphism by certificates");
    /** Handle for profiling isomorphism by simulation. */
    static final Reporter isoSimCheckReporter = reporter.register("Isomorphism by simulation");
    /** Handle for profiling {@link #areGraphEqual(Graph, Graph, Object[], Object[])}. */
    static final Reporter equalsTestReporter = reporter.register("Equality test");

    // the following has to be defined here in order to avoid
    // circularities in class initialisation
    /** Certificate factory for testing purposes. */
    private final CertificateStrategy certificateFactory = new PartitionRefiner(null);

    private class IsoSearchPair implements Comparable<IsoSearchPair> {
        /** Constructs an instance from given data. */
        public IsoSearchPair(Edge key, Collection<Edge> images) {
            super();
            this.key = key;
            this.images = images;
        }

        @Override
        public int compareTo(IsoSearchPair o) {
            // lower images set size is better
            int result = this.images.size() - o.images.size();
            if (result == 0) {
                // no criteria; just take the key edge
                result = EdgeComparator.instance()
                    .compare(this.key, o.key);
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
            int result = ((IsoSearchItem) o).getPreMatchCount() - this.getPreMatchCount();
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
            return String.format("(%s,%s,%s,%s)",
                this.key,
                this.images,
                this.sourcePreMatched,
                this.targetPreMatched);
        }

        /** Flag indicating if the key source node has already been matched. */
        boolean sourcePreMatched;
        /** Flag indicating if the key target node has already been matched. */
        boolean targetPreMatched;
    }

    /**
     * Simple class to store the state of the isomorphism checker method in
     * order to allow resuming of the search. Can be used to produce all
     * isomorphisms between two graphs.
     */
    public class IsoCheckerState {
        /** The search plan for the isomorphism. */
        List<IsoSearchItem> plan = null;
        /** Set of images used in the isomorphism so far. */
        Set<Node> usedNodeImages = null;
        /** Records of the search for isomorphism. */
        Iterator<Edge>[] records = null;
        /** Array of source nodes in the order of the search plan. */
        Node[] sourceImages = null;
        /** Array of target nodes in the order of the search plan. */
        Node[] targetImages = null;
        /** Result of the search. */
        Morphism<Node,Edge> result = null;
        /** Position in the search plan. */
        int i = 0;
        /**
         * Flag stating that there was a one-to-one mapping of the certificates,
         * which was already returned
         */
        boolean foundCertBijection;

        /** Returns true if the plan size is zero. */
        public boolean isPlanEmpty() {
            return this.plan == null || this.plan.size() == 0;
        }
    }
}
