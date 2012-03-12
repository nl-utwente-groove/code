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

import groove.graph.AbstractGraph;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy.ElementCertificate;
import groove.util.Bag;
import groove.util.Groove;
import groove.util.HashBag;
import groove.util.Reporter;
import groove.util.SmallCollection;

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

/**
 * Implementation of an isomorphism checking algorithm that first tries to
 * decide isomorphism directly on the basis of a
 * {@link groove.graph.iso.CertificateStrategy}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class IsoChecker<N extends Node,E extends Edge> {
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
    public boolean areIsomorphic(Graph<N,E> dom, Graph<N,E> cod) {
        return areIsomorphic(dom, cod, null, null);
    }

    /** Tests if two graphs, together with corresponding lists of nodes, are isomorphic. */
    public boolean areIsomorphic(Graph<N,E> dom, Graph<N,E> cod, N[] domNodes,
            N[] codNodes) {
        if (ISO_PRINT) {
            System.out.printf("Comparing: %n   %s%n   %s", dom, cod);
        }
        boolean result;
        if ((domNodes == null) != (codNodes == null)
            || (domNodes != null && domNodes.length != codNodes.length)) {
            result = false;
            if (ISO_PRINT) {
                System.out.printf("DIFFERENT NODE COUNTS%n", dom, cod);
            }
        } else if (areGraphEqual(dom, cod, domNodes, codNodes)) {
            equalGraphsCount++;
            result = true;
        } else {
            if (ISO_PRINT) {
                System.out.printf("GRAPHS NOT EQUAL%n", dom, cod);
            }
            areIsoReporter.start();
            CertificateStrategy<N,E> domCertifier = getCertifier(dom, true);
            CertificateStrategy<N,E> codCertifier = getCertifier(cod, true);
            result =
                areIsomorphic(domCertifier, codCertifier, domNodes, codNodes);
            if (ISO_ASSERT) {
                assert checkBisimulator(dom, cod, result);
                assert result == hasIsomorphism(new Bisimulator<N,E>(dom),
                    new Bisimulator<N,E>(cod), domNodes, codNodes);
            }
            if (TEST_FALSE_NEGATIVES && result) {
                CertificateStrategy<N,E> altDomCert =
                    this.certificateFactory.newInstance(dom, this.strong);
                CertificateStrategy<N,E> altCodCert =
                    this.certificateFactory.newInstance(cod, this.strong);
                if (!areIsomorphic(altDomCert, altCodCert, domNodes, codNodes)) {
                    System.out.printf(
                        "Certifier '%s' gives a false negative on%n%s%n%s%n",
                        altDomCert.getClass(), dom, cod);
                    if (SAVE_FALSE_NEGATIVES) {
                        try {
                            File file1 = Groove.saveGraph(dom, "graph1");
                            File file2 = Groove.saveGraph(cod, "graph2");
                            System.out.printf("Graphs saved as '%s' and '%s'",
                                file1, file2);
                            System.exit(0);
                        } catch (IOException exc) {
                            System.out.printf("Can't save graph: %s",
                                exc.getMessage());
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
     * This method wraps a node and edge set equality test on two graphs, under
     * the assumption that the node and edge counts are already known to
     * coincide. Optional arrays of nodes are also tested for equality; these may be 
     * (simultaneously {@code null} but are otherwise guaranteed to be of
     * the same length
     * @param domNodes list of nodes (from the domain) to compare 
     * in addition to the graphs themselves
     * @param codNodes list of nodes (from the codomain) to compare 
     * in addition to the graphs themselves
     */
    private boolean areGraphEqual(Graph<N,E> dom, Graph<N,E> cod,
            Node[] domNodes, Node[] codNodes) {
        equalsTestReporter.start();
        // test if the node counts of domain and codomain coincide
        boolean result =
            (domNodes == null || Arrays.equals(domNodes, codNodes));
        if (result) {
            CertificateStrategy<N,E> domCertifier = getCertifier(dom, false);
            CertificateStrategy<N,E> codCertifier = getCertifier(cod, false);
            int domNodeCount =
                domCertifier == null ? dom.nodeCount()
                        : domCertifier.getNodeCertificates().length;
            int codNodeCount =
                codCertifier == null ? cod.nodeCount()
                        : codCertifier.getNodeCertificates().length;
            result = domNodeCount == codNodeCount;
            if (result) {
                // test if the edge sets of domain and codomain coincide
                Set<?> domEdgeSet, codEdgeSet;
                if (domCertifier == null || codCertifier == null) {
                    // copy the edge set of the codomain to avoid sharing problems
                    codEdgeSet = new HashSet<Edge>(cod.edgeSet());
                    domEdgeSet = dom.edgeSet();
                } else {
                    codEdgeSet = codCertifier.getCertificateMap().keySet();
                    domEdgeSet = domCertifier.getCertificateMap().keySet();
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
     * @param domNodes list of nodes (from the domain) to compare 
     * in addition to the graphs themselves
     * @param codNodes list of nodes (from the codomain) to compare 
     * in addition to the graphs themselves
     */
    private boolean areIsomorphic(CertificateStrategy<N,E> domCertifier,
            CertificateStrategy<N,E> codCertifier, N[] domNodes, N[] codNodes) {
        boolean result;
        if (!domCertifier.getGraphCertificate().equals(
            codCertifier.getGraphCertificate())) {
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
                result =
                    areCertEqual(domCertifier, codCertifier, domNodes, codNodes);
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
            isoSimCheckReporter.start();
            if (getNodePartitionCount(domCertifier) == getNodePartitionCount(codCertifier)) {
                result =
                    hasIsomorphism(domCertifier, codCertifier, domNodes,
                        codNodes);
            } else {
                if (ISO_PRINT) {
                    System.out.println("Unequal node partition counts");
                }
                distinctCertsCount++;
                result = false;
            }
            isoSimCheckReporter.stop();
            if (result) {
                equalSimCount++;
            } else {
                distinctSimCount++;
            }
        }
        return result;
    }

    /**
     * Tests if an isomorphism can be constructed on the basis of distinct
     * certificates. It is assumed that <code>hasDistinctCerts(dom)</code>
     * holds.
     * @param dom the first graph to be tested
     * @param cod the second graph to be tested
     * @param domNodes list of nodes (from the domain) to compare 
     * in addition to the graphs themselves
     * @param codNodes list of nodes (from the codomain) to compare 
     * in addition to the graphs themselves
     */
    private boolean areCertEqual(CertificateStrategy<N,E> dom,
            CertificateStrategy<N,E> cod, N[] domNodes, N[] codNodes) {
        boolean result;
        // map to store dom-to-cod node mapping
        Morphism<N,E> iso = getCertEqualIsomorphism(dom, cod);
        result = iso != null;
        if (result && domNodes != null) {
            // now test correspondence of the node arrays
            for (int i = 0; result && i < domNodes.length; i++) {
                result = iso.getNode(domNodes[i]).equals(codNodes[i]);
            }
        }
        if (ISO_PRINT) {
            if (!result) {
                System.out.printf("Graphs have distinct but unequal certificates%n");
            }
        }
        return result;
    }

    /**
     * Tests if an isomorphism can be constructed on the basis of distinct
     * certificates. It is assumed that <code>hasDistinctCerts(dom)</code>
     * holds.
     * @param dom the first graph to be tested
     * @param cod the second graph to be tested
     */
    @SuppressWarnings("unchecked")
    private Morphism<N,E> getCertEqualIsomorphism(CertificateStrategy<N,E> dom,
            CertificateStrategy<N,E> cod) {
        Morphism<N,E> result = dom.getGraph().getFactory().createMorphism();
        // the certificates uniquely identify the dom elements;
        // it suffices to test if this gives rise to a consistent one-to-one
        // node map
        // Certificate<Node>[] nodeCerts = dom.getNodeCertificates();
        ElementCertificate<E>[] edgeCerts = dom.getEdgeCertificates();
        PartitionMap<E> codPartitionMap = cod.getEdgePartitionMap();
        int edgeCount = edgeCerts.length;
        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
            ElementCertificate<E> domEdgeCert = edgeCerts[i];
            SmallCollection<E> image = codPartitionMap.get(domEdgeCert);
            if (image == null) {
                result = null;
                break;
            }
            E edgeKey = domEdgeCert.getElement();
            E edgeImage = image.getSingleton();
            N imageSource = (N) edgeImage.source();
            N oldImageSource =
                result.putNode((N) edgeKey.source(), imageSource);
            if (oldImageSource != null && !oldImageSource.equals(imageSource)) {
                result = null;
                break;
            }
            N imageTarget = (N) edgeImage.target();
            N oldImageTarget =
                result.putNode((N) edgeKey.target(), imageTarget);
            if (oldImageTarget != null && !oldImageTarget.equals(imageTarget)) {
                result = null;
                break;
            }
            result.putEdge(edgeKey, edgeImage);
        }
        return result;
    }

    private boolean hasIsomorphism(CertificateStrategy<N,E> domCertifier,
            CertificateStrategy<N,E> codCertifier, N[] domNodes, N[] codNodes) {
        boolean result;
        IsoCheckerState state = new IsoCheckerState();
        // repeatedly look for the next isomorphism until one is found
        // that also maps the domain and codomain nodes correctly
        do {
            Morphism<N,E> iso =
                computeIsomorphism(domCertifier, codCertifier, state);
            result = iso != null;
            if (result && domNodes != null) {
                for (int i = 0; result && i < domNodes.length; i++) {
                    result = iso.getNode(domNodes[i]).equals(codNodes[i]);
                }
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
    public Morphism<N,E> getIsomorphism(Graph<N,E> dom, Graph<N,E> cod) {
        return getIsomorphism(getCertifier(dom, true), getCertifier(cod, true),
            null);
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
    public Morphism<N,E> getIsomorphism(Graph<N,E> dom, Graph<N,E> cod,
            IsoCheckerState state) {
        return getIsomorphism(getCertifier(dom, true), getCertifier(cod, true),
            state);
    }

    private Morphism<N,E> getIsomorphism(CertificateStrategy<N,E> domCertifier,
            CertificateStrategy<N,E> codCertifier, IsoCheckerState state) {
        Morphism<N,E> result =
            computeIsomorphism(domCertifier, codCertifier, state);
        if (result != null
            && result.nodeMap().size() != domCertifier.getGraph().nodeCount()) {
            // there's sure to be an isomorphism, but we have to add the
            // isolated nodes
            PartitionMap<N> codPartitionMap =
                codCertifier.getNodePartitionMap();
            Set<N> usedNodeImages = new HashSet<N>();
            ElementCertificate<N>[] nodeCerts =
                domCertifier.getNodeCertificates();
            for (ElementCertificate<N> nodeCert : nodeCerts) {
                N node = nodeCert.getElement();
                if (!result.containsNodeKey(node)) {
                    // this is an isolated node
                    SmallCollection<N> nodeImages =
                        codPartitionMap.get(nodeCert);
                    if (nodeImages.isSingleton()) {
                        // it follows that there is only one isolated node
                        result.putNode(node, nodeImages.getSingleton());
                        break;
                    } else {
                        // find an unused node
                        for (N nodeImage : nodeImages) {
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
     * @param domCertifier the certificate strategy of the first graph to be
     *        compared
     * @param codCertifier the certificate strategy of the second graph to be
     *        compared
     */
    @SuppressWarnings("unchecked")
    private Morphism<N,E> computeIsomorphism(
            CertificateStrategy<N,E> domCertifier,
            CertificateStrategy<N,E> codCertifier, IsoCheckerState state) {
        // make sure the graphs are of the same size
        Graph<N,E> dom = domCertifier.getGraph();
        Graph<N,E> cod = codCertifier.getGraph();
        if (dom.nodeCount() != cod.nodeCount()
            || dom.edgeCount() != cod.edgeCount()) {
            return null;
        }
        // make sure the certificate counts are equal
        if (domCertifier.getNodeCertificates().length != codCertifier.getNodeCertificates().length
            || domCertifier.getEdgeCertificates().length != codCertifier.getEdgeCertificates().length) {
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
            return getCertEqualIsomorphism(domCertifier, codCertifier);
        }
        Morphism<N,E> result;
        Set<N> usedNodeImages;

        // Compute a new plan or restore the one from the state.
        List<IsoSearchItem> plan;
        if (state != null && state.plan != null) {
            plan = state.plan;
            if (state.result == null || state.i == plan.size()) {
                // there are no more results to be found
                return null;
            } else {
                usedNodeImages = new HashSet<N>(state.usedNodeImages);
                result = state.result.clone();
            }
        } else {
            result = domCertifier.getGraph().getFactory().createMorphism();
            usedNodeImages = new HashSet<N>();
            plan =
                computePlan(domCertifier, codCertifier, result, usedNodeImages);
        }
        if (plan == null) {
            return null;
        }

        // Create new records and images or restore the ones from the state.
        Iterator<E>[] records;
        N[] sourceImages;
        N[] targetImages;
        if (state != null && state.records != null) {
            records = state.records;
        } else {
            records = new Iterator[plan.size()];
        }
        if (state != null && state.sourceImages != null) {
            sourceImages = state.sourceImages;
        } else {
            sourceImages = (N[]) new Node[plan.size()];
        }
        if (state != null && state.targetImages != null) {
            targetImages = state.targetImages;
        } else {
            targetImages = (N[]) new Node[plan.size()];
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
                        sourceImages[i], item.key.source(), usedNodeImages);
                    sourceImages[i] = null;
                }
                if (!item.targetPreMatched && targetImages[i] != null) {
                    boolean removed = usedNodeImages.remove(targetImages[i]);
                    assert removed : String.format(
                        "Image %s for target %s not present in used node set %s",
                        targetImages[i], item.key.target(), usedNodeImages);
                    targetImages[i] = null;
                }
            }
            if (!records[i].hasNext()) {
                // we're moving backward
                records[i] = null;
                i--;
            } else {
                E key = item.key;
                N keyTarget = (N) key.target();
                N keySource = (N) key.source();
                E image = records[i].next();
                N imageSource = (N) image.source();
                N imageTarget = (N) image.target();
                if (item.sourcePreMatched) {
                    if (!result.getNode(keySource).equals(imageSource)) {
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
                    if (!result.getNode(keyTarget).equals(imageTarget)) {
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
            assert checkIsomorphism(domCertifier.getGraph(), result) : String.format(
                "Erronous result using plan %s", plan);
            // Store the variables in the state.
            if (state != null) {
                state.plan = plan;
                state.result = result.clone();
                state.usedNodeImages = new HashSet<N>(usedNodeImages);
                state.sourceImages = sourceImages;
                state.targetImages = targetImages;
                state.records = records;
                state.i = i - 1;
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private List<IsoSearchItem> computePlan(
            CertificateStrategy<N,E> domCertifier,
            CertificateStrategy<N,E> codCertifier, Morphism<N,E> resultMap,
            Set<N> usedNodeImages) {
        Graph<N,E> dom = domCertifier.getGraph();
        List<IsoSearchItem> result = new ArrayList<IsoSearchItem>();
        PartitionMap<E> codPartitionMap = codCertifier.getEdgePartitionMap();
        Map<E,Collection<E>> remainingEdgeSet = new HashMap<E,Collection<E>>();
        // the set of dom nodes that have an image in result, but whose incident
        // images possibly don't
        Set<N> connectedNodes = new HashSet<N>();
        ElementCertificate<E>[] edgeCerts = domCertifier.getEdgeCertificates();
        // collect the pairs of edge keys and edge image sets
        int edgeCount = edgeCerts.length;
        for (int i = 0; i < edgeCount && edgeCerts[i] != null; i++) {
            ElementCertificate<E> edgeCert = edgeCerts[i];
            SmallCollection<E> images = codPartitionMap.get(edgeCert);
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
            Iterator<Map.Entry<E,Collection<E>>> remainingEdgeIter =
                remainingEdgeSet.entrySet().iterator();
            Map.Entry<E,Collection<E>> first = remainingEdgeIter.next();
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
                N keySource = (N) next.key.source();
                next.sourcePreMatched = !connectedNodes.add(keySource);
                if (!next.sourcePreMatched) {
                    for (E edge : dom.edgeSet(keySource)) {
                        Collection<E> images = remainingEdgeSet.remove(edge);
                        if (images != null) {
                            subPlan.add(new IsoSearchItem(edge, images));
                        }
                    }
                }
                // add incident edges from the target node, if that was not
                // already matched
                N keyTarget = (N) next.key.target();
                next.targetPreMatched = !connectedNodes.add(keyTarget);
                if (!next.targetPreMatched) {
                    for (E edge : dom.edgeSet(keyTarget)) {
                        Collection<E> images = remainingEdgeSet.remove(edge);
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
    @SuppressWarnings("unchecked")
    private boolean setEdge(E key, E value, Morphism<N,E> result,
            Set<N> connectedNodes, Set<N> usedCodNodes) {
        if (!setNode((N) key.source(), (N) value.source(), result,
            connectedNodes, usedCodNodes)) {
            return false;
        }
        if (!setNode((N) key.target(), (N) value.target(), result,
            connectedNodes, usedCodNodes)) {
            return false;
        }
        result.putEdge(key, value);
        return true;
    }

    /**
     * Inserts a node into the result mapping, testing if this is consistent.
     */
    private boolean setNode(N end, N endImage, Morphism<N,E> result,
            Set<N> connectedNodes, Set<N> usedCodNodes) {
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
     * Tests if the elements of a graph have all different certificates. If this
     * holds, then
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy, Node[], Node[])} can be
     * called to check for isomorphism.
     * @param certifier the graph to be tested
     * @return <code>true</code> if <code>graph</code> has distinct
     *         certificates
     */
    private boolean hasDiscreteCerts(CertificateStrategy<?,?> certifier) {
        return certifier.getNodePartitionMap().isOneToOne();
    }

    /**
     * Convenience method for
     * <code>graph.getCertificateStrategy().getNodePartitionCount()</code>.
     */
    private int getNodePartitionCount(CertificateStrategy<?,?> certifier) {
        return certifier.getNodePartitionCount();
    }

    /**
     * Tests if a given node map contains an entry consisting of a certain
     * key and image. Adds the entry if the key is not in the map.
     * @return {@code true} if the key is new or the image equals the
     * given image; {@code false} if the key is mapped to a different image
     */
    private boolean checkNodeMap(Map<N,N> nodeMap, N key, N image) {
        boolean result = true;
        N oldImage = nodeMap.get(key);
        if (oldImage == null) {
            nodeMap.put(key, image);
        } else {
            result = oldImage.equals(image);
        }
        return result;
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
    public CertificateStrategy<N,E> getCertifier(Graph<N,E> graph,
            boolean always) {
        CertificateStrategy<N,E> result = null;
        if (graph instanceof AbstractGraph) {
            if (always || ((AbstractGraph<N,E>) graph).hasCertifier(isStrong())) {
                result = ((AbstractGraph<N,E>) graph).getCertifier(isStrong());
            }
        } else if (always) {
            result =
                AbstractGraph.getCertificateFactory().newInstance(graph,
                    isStrong());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean checkIsomorphism(Graph<N,E> dom, Morphism<N,E> map) {
        for (E edge : dom.edgeSet()) {
            if (edge.source() != edge.target()
                && !map.edgeMap().containsKey(edge)) {
                System.out.printf("Result contains no image for %s%n", edge);
                return false;
            }
        }
        for (Map.Entry<E,E> edgeEntry : map.edgeMap().entrySet()) {
            E key = edgeEntry.getKey();
            N keySource = (N) key.source();
            N keyTarget = (N) key.target();
            E value = edgeEntry.getValue();
            if (!map.getNode(keySource).equals(value.source())) {
                System.out.printf(
                    "Edge %s mapped to %s, but source mapped to %s%n", key,
                    value, keySource, map.getNode(keySource));
                return false;
            }
            if (!map.getNode(keyTarget).equals(value.target())) {
                System.out.printf(
                    "Edge %s mapped to %s, but end %s mapped to %s%n", key,
                    value, key.target(), map.getNode(keyTarget));
                return false;
            }
        }
        if (map.nodeMap().size() != new HashSet<N>(map.nodeMap().values()).size()) {
            for (Map.Entry<N,N> first : map.nodeMap().entrySet()) {
                for (Map.Entry<N,N> second : map.nodeMap().entrySet()) {
                    if (first != second
                        && first.getValue() == second.getValue()) {
                        System.out.printf("Image of %s and %s both %s%n",
                            first.getKey(), second.getKey(), first.getValue());
                    }
                }
            }
            return false;
        }
        return true;
    }

    /** Method to be used in an assert on the correctness of isomorphism. */
    private boolean checkBisimulator(Graph<N,E> dom, Graph<N,E> cod,
            boolean result) {
        if (result && isStrong()) {
            CertificateStrategy<N,E> domBis =
                new PartitionRefiner<N,E>(dom, isStrong());
            CertificateStrategy<N,E> codBis =
                new PartitionRefiner<N,E>(cod, isStrong());
            Bag<ElementCertificate<N>> domNodes =
                new HashBag<ElementCertificate<N>>(
                    Arrays.asList(domBis.getNodeCertificates()));
            Bag<ElementCertificate<E>> domEdges =
                new HashBag<ElementCertificate<E>>(
                    Arrays.asList(domBis.getEdgeCertificates()));
            Bag<ElementCertificate<N>> codNodes =
                new HashBag<ElementCertificate<N>>(
                    Arrays.asList(codBis.getNodeCertificates()));
            Bag<ElementCertificate<E>> codEdges =
                new HashBag<ElementCertificate<E>>(
                    Arrays.asList(codBis.getEdgeCertificates()));
            Bag<ElementCertificate<N>> domMinCodNodes =
                new HashBag<ElementCertificate<N>>(domNodes);
            domMinCodNodes.removeAll(codNodes);
            assert domMinCodNodes.isEmpty() : String.format(
                "Node certificates %s in dom but not cod", domMinCodNodes);
            Bag<ElementCertificate<N>> codMinDomNodes =
                new HashBag<ElementCertificate<N>>(codNodes);
            codMinDomNodes.removeAll(domNodes);
            assert codMinDomNodes.isEmpty() : String.format(
                "Node certificates %s in cod but not cod", codMinDomNodes);
            Bag<ElementCertificate<E>> domMinCodEdges =
                new HashBag<ElementCertificate<E>>(domEdges);
            domMinCodEdges.removeAll(codEdges);
            assert domMinCodEdges.isEmpty() : String.format(
                "Edge certificates %s in dom but not cod", domMinCodEdges);
            Bag<ElementCertificate<E>> codMinDomEdges =
                new HashBag<ElementCertificate<E>>(codEdges);
            codMinDomEdges.removeAll(domEdges);
            assert codMinDomEdges.isEmpty() : String.format(
                "Edge certificates %s in cod but not cod", codMinDomEdges);
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
    @SuppressWarnings("unchecked")
    static public <N extends Node,E extends Edge> IsoChecker<N,E> getInstance(
            boolean strong) {
        // initialise lazily to avoid initialisation circularities
        if (strongInstance == null) {
            strongInstance = new IsoChecker<Node,Edge>(true);
            weakInstance = new IsoChecker<Node,Edge>(false);

        }
        return (IsoChecker<N,E>) (strong ? strongInstance : weakInstance);
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
        return PartitionRefiner.computeCertReporter.getTotalTime()
            + PartitionRefiner.getPartitionReporter.getTotalTime();
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
        if (args.length == 1) {
            testIso(args[0]);
        } else if (args.length == 2) {
            compareGraphs(args[0], args[1]);
        } else {
            System.out.println("Usage: DefaultIsoChecker file1 file2");
            return;
        }
    }

    private static void testIso(String name) {
        try {
            DefaultGraph graph1 = Groove.loadGraph(name);
            IsoChecker<DefaultNode,DefaultEdge> checker = getInstance(true);
            System.out.printf("Graph certificate: %s%n",
                checker.getCertifier(graph1, true).getGraphCertificate());
            for (int i = 0; i < 1000; i++) {
                DefaultGraph graph2 = new DefaultGraph(name);
                DefaultMorphism nodeMap = new DefaultMorphism();
                for (DefaultNode node : graph1.nodeSet()) {
                    DefaultNode newNode = graph2.addNode();
                    nodeMap.putNode(node, newNode);
                }
                for (DefaultEdge edge : graph1.edgeSet()) {
                    graph2.addEdge(nodeMap.mapEdge(edge));
                }
                if (!checker.areIsomorphic(graph1, graph2)) {
                    System.out.println("Error! Graph not isomorphic to itself");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void compareGraphs(String name1, String name2) {
        try {
            DefaultGraph graph1 = Groove.loadGraph(name1);
            DefaultGraph graph2 = Groove.loadGraph(name2);
            System.out.printf("Graphs '%s' and '%s' isomorphic?%n", name1,
                name2);
            System.out.printf("Done. Result: %b%n",
                (IsoChecker.getInstance(true)).areIsomorphic((Graph) graph1,
                    (Graph) graph2));
            System.out.printf("Certification time: %d%n", getCertifyingTime());
            System.out.printf("Simulation time: %d%n", getSimCheckTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** The singleton strong instance of this class. */
    static private IsoChecker<?,?> strongInstance;
    /** The singleton weak instance of this class. */
    static private IsoChecker<?,?> weakInstance;
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
    static public final Reporter areIsoReporter =
        reporter.register("areIsomorphic(Graph,Graph)");
    /**
     * Handle for profiling
     * {@link #areCertEqual(CertificateStrategy, CertificateStrategy, Node[], Node[])}.
     */
    static final Reporter isoCertCheckReporter =
        reporter.register("Isomorphism by certificates");
    /** Handle for profiling isomorphism by simulation. */
    static final Reporter isoSimCheckReporter =
        reporter.register("Isomorphism by simulation");
    /** Handle for profiling {@link #areGraphEqual(Graph, Graph, Node[], Node[])}. */
    static final Reporter equalsTestReporter =
        reporter.register("Equality test");

    // the following has to be defined here in order to avoid
    // circularities in class initialisation
    /** Certificate factory for testing purposes. */
    private final CertificateStrategy<?,?> certificateFactory =
        new PartitionRefiner<Node,Edge>(null);

    private class IsoSearchPair implements Comparable<IsoSearchPair> {
        /** Constructs an instance from given data. */
        public IsoSearchPair(E key, Collection<E> images) {
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
        final E key;
        /**
         * The codomain images of this record; guaranteed to contain at least
         * two elements.
         */
        final Collection<E> images;
    }

    /**
     * Item in an isomorphism search plan
     */
    private class IsoSearchItem extends IsoSearchPair {
        /** Constructs an instance from given data. */
        public IsoSearchItem(E key, Collection<E> images) {
            super(key, images);
        }

        @Override
        public int compareTo(IsoSearchPair o) {
            // higher pre-match count is better
            @SuppressWarnings("unchecked")
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

    /**
     * Simple class to store the state of the isomorphism checker method in
     * order to allow resuming of the search. Can be used to produce all
     * isomorphisms between two graphs.
     */
    public class IsoCheckerState {
        /** The search plan for the isomorphism. */
        List<IsoSearchItem> plan = null;
        /** Set of images used in the isomorphism so far. */
        Set<N> usedNodeImages = null;
        /** Records of the search for isomorphism. */
        Iterator<E>[] records = null;
        /** Array of source nodes in the order of the search plan. */
        N[] sourceImages = null;
        /** Array of target nodes in the order of the search plan. */
        N[] targetImages = null;
        /** Result of the search. */
        Morphism<N,E> result = null;
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
