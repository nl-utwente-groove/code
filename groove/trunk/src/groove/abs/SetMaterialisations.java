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
 * $Id: SetMaterialisations.java,v 1.3 2008-01-30 09:32:22 iovka Exp $
 */
package groove.abs;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.RuleApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A set of materialisations is defined by a concrete part, and abstract part
 * and an embedding of the concrete part into the abstract part. The set of
 * materialisations can be transformed yielding a set of resulting abstract
 * graphs.
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
public class SetMaterialisations {

    /**
     * Performs the actual transformation and returns the set of resulting
     * abstract graphs.
     * @param appl The matching associated to this rule application should be
     *        the same as for the construction of this SetMaterialiastion
     *        object.
     * @return The set of resulting abstract graphs. This method can be called
     *         immediately after the creation of the object.
     */
    public Collection<AbstrGraph> transform(RuleApplication appl,
            NodeFactory nodeFactory) {
        computeSet();
        if (this.origins.size() == 0) {
            return new ArrayList<AbstrGraph>(0);
        }
        try {
            transformAux(appl, nodeFactory);
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            ArrayList<AbstrGraph> result = new ArrayList<AbstrGraph>(1);
            result.add(DefaultAbstrGraph.INVALID_AG);
            return result;
        }
        return _transfResults(nodeFactory);
    }

    // --------------------------------------------------------------------------------------
    // MAIN ALGORITHMS
    // --------------------------------------------------------------------------------------
    /**
     * Computes the internal representation of the set, so that it can be
     * transformed.
     * @ensure For all couple of nodes (cn,an) that is in one of the origin
     *         maps, this.data contains a value for this couple.
     */
    private void computeSet() {
        this.data = new HashMap<CNN,Collection<MapPattern>>();
        // Compute all possible origin embeddings that extend the initial one
        this.origins = new ArrayList<ExtendedVarNodeEdgeMap>();
        for (VarNodeEdgeMap origin : Util.getMatchesIter(
            this.concrPart.graph(), this.abstrPart, this.originBase)) {
            this.origins.add(new ExtendedVarNodeEdgeMap(origin));
        }
        cleanImpossibleOrigins();
        ((ArrayList<ExtendedVarNodeEdgeMap>) this.origins).trimToSize();

        // For all origin
        Iterator<ExtendedVarNodeEdgeMap> it = this.origins.iterator();
        originsLoop: while (it.hasNext()) {
            ExtendedVarNodeEdgeMap originMap = it.next();
            Map<CNN,Collection<MapPattern>> addToData =
                new HashMap<CNN,Collection<MapPattern>>();

            // For all couple (cn,an) in origin and such that cn is not in the
            // central nodes,
            // compute the corresponding type if it was not yet computed
            for (Map.Entry<Node,Graph> entry : this.concrPart.neigh().entrySet()) {
                Node n = entry.getKey();
                if (!this.data.containsKey(CNN.cnn(n, originMap.getNode(n)))) {
                    // avoid to do things twice
                    Graph neigh = entry.getValue();
                    GraphPattern type =
                        this.abstrPart.typeOf(originMap.getNode(n));
                    ArrayList<MapPattern> theTypes =
                        new ArrayList<MapPattern>();
                    addToData.put(CNN.cnn(n, originMap.getNode(n)), theTypes);
                    for (VarNodeEdgeMap m : type.possibleTypings(neigh, n,
                        this.options.SYMMETRY_REDUCTION)) {
                        theTypes.add(new MapPattern(m));
                    }
                    // if no types were found, then this origin mapping is not
                    // possible
                    if (theTypes.size() == 0) {
                        it.remove();
                        continue originsLoop;
                    }
                }
            }
            this.data.putAll(addToData);

        }
    }

    /**
     * Performs the actual transformation of the concrete part. Also computes
     * possible new types of the nodes of the new concrete part.
     * @ensure {@link #data} is updated with the new types
     * @ensure {@link #centerType}, {@link #newConcrPart}, {@link #morph} are
     *         computed
     * @ensure {@link #transformed} is set to true
     * @require {@link #computeSet()} should have been called before
     * @throws ExceptionIncompatibleWithMaxIncidence when the new concrete part
     *         contains incorrect types
     */
    private void transformAux(RuleApplication appl, NodeFactory nodeFactory)
        throws ExceptionIncompatibleWithMaxIncidence {
        this.centerType =
            new HashMap<Node,GraphPattern>(
                this.concrPart.graph().nodeSet().size()
                    - this.concrPart.neigh().size());

        // IOVKA if not cloning, the source graph is modified
        Graph clone = this.concrPart.graph().clone();
        appl.applyDelta(clone);
        this.newConcrPart = appl.getTarget();
        this.morph = appl.getMorphism();

        // Update the data map with the transformed neighbourhoods
        for (Map.Entry<CNN,Collection<MapPattern>> entry : this.data.entrySet()) {
            for (MapPattern mp : entry.getValue()) {
                mp.setPattern(newType(entry.getKey().n1(),
                    this.concrPart.neigh().get(entry.getKey().n1()),
                    this.abstrPart.typeOf(entry.getKey().n2()), mp.getMap(),
                    nodeFactory));
            }
        }

        // compute the types for the central nodes
        for (Node n : this.concrPart.centerNodes()) {
            if (!this.morph.containsKey(n)) {
                continue;
            } // check whether deleted node
              // this is a read central node
            try {
                this.centerType.put(
                    n,
                    this.abstrPart.family().computeAddPattern(
                        this.newConcrPart, this.morph.getNode(n)));
            } catch (ExceptionIncompatibleWithMaxIncidence e) {
                throw e;
            }
        }
        // compute the types for the new nodes
        for (Node n : appl.getCreatedNodes()) {
            try {
                this.centerType.put(
                    n,
                    this.abstrPart.family().computeAddPattern(
                        this.newConcrPart, n));
            } catch (ExceptionIncompatibleWithMaxIncidence e) {
                throw e;
            }
        }
        this.transformed = true;

        checkFullTyping();
    }

    Collection<AbstrGraph> _transfResults(NodeFactory nodeFactory) {

        Collection<AbstrGraph> result = new ArrayList<AbstrGraph>();

        // # For all possible origin
        for (final ExtendedVarNodeEdgeMap origin : this.origins) {

            TupleIterator.Mapping<Node,MapPattern> mappingIt =
                new TupleIterator.Mapping<Node,MapPattern>() {
                    public Iterator<MapPattern> itFor(Node n) {
                        return SetMaterialisations.this.data.get(
                            CNN.cnn(n, origin.getNode(n))).iterator();
                    }

                    public Collection<Node> keySet() {
                        return SetMaterialisations.this.concrPart.neigh().keySet();
                    }

                    public int size() {
                        return keySet().size();
                    }
                };
            TupleIterator<Node,MapPattern> it =
                new TupleIterator<Node,MapPattern>(mappingIt);

            // # o For all typings of the nodes of the concrete part
            while (it.hasNext()) {
                // # Compute the possible links
                final Map<Node,MapPattern> mapNodePattern = it.next();

                ArrayList<Set<Edge>> possibleSrcLinks =
                    new ArrayList<Set<Edge>>();
                ArrayList<Set<Edge>> possibleTgtLinks =
                    new ArrayList<Set<Edge>>();
                ArrayList<Set<Node>> zeroMultNodes = new ArrayList<Set<Node>>();
                ArrayList<Set<Edge>> linkConsumedEdges =
                    new ArrayList<Set<Edge>>();
                ArrayList<Set<Edge>> internalEdges = new ArrayList<Set<Edge>>();
                possibleLinks(origin, mapNodePattern, possibleSrcLinks,
                    possibleTgtLinks, zeroMultNodes, linkConsumedEdges,
                    internalEdges, nodeFactory);

                // # For all possible links
                for (int i = 0; i < possibleSrcLinks.size(); i++) {
                    // # Merge the concrete and abstract part

                    // o Copy the old abstract part and remove the
                    // 0-multiplicity nodes
                    DefaultAbstrGraph res = newAbstractPart(origin);
                    _removeZeroMultNodes(res);

                    // o Remove from the abstract part edges that have been used
                    // for links
                    for (Edge e : linkConsumedEdges.get(i)) {
                        res.removeEdge(e);
                    }

                    // o If there are some internal edges, they have to be added
                    // to the concrete part
                    // o Only edges in the new concrete part are to be
                    // considered (i.e. not edges to/from deleted nodes)
                    Graph newConcrPartCopy = this.newConcrPart;
                    if (internalEdges.size() != 0) {
                        newConcrPartCopy = this.newConcrPart.clone();
                        for (Edge e : internalEdges.get(i)) {
                            if (newConcrPartCopy.containsElement(e.source())
                                && newConcrPartCopy.containsElement(e.opposite())) {
                                newConcrPartCopy.addEdge(e);
                            }
                        }
                    }

                    // o Add the concrete part to the abstract part
                    ConcretePart.Typing nodeTypes = new ConcretePart.Typing() {
                        public GraphPattern typeOf(Node n) {
                            MapPattern t = mapNodePattern.get(n);
                            return t != null
                                    ? t.getPattern()
                                    : SetMaterialisations.this.centerType.get(n);
                        }
                    };

                    _addConcrToAbstr(res, newConcrPartCopy, nodeTypes);

                    // o Add the link edges
                    for (Edge ee : possibleSrcLinks.get(i)) {
                        DefaultEdge e = (DefaultEdge) ee;
                        res.addEdgeBetweenPatterns(
                            nodeTypes.typeOf(e.source()), e.label(), e.target());
                    }
                    for (Edge ee : possibleTgtLinks.get(i)) {
                        DefaultEdge e = (DefaultEdge) ee;
                        res.addEdgeBetweenPatterns(e.source(), e.label(),
                            nodeTypes.typeOf(e.target()));
                    }

                    // o Add the computed abstract graph, whenever it is not
                    // eliminated because without concretisations
                    if (!res.isWithoutConcretisation()) {
                        result.add(res);
                    }
                }
            }
        }
        return result;
    }

    /** Removes from g the nodes with multiplicity 0 */
    private static void _removeZeroMultNodes(AbstrGraph g) {
        ArrayList<Node> toRemove = new ArrayList<Node>();
        Iterator<? extends Node> nodeIt = g.nodeSet().iterator();
        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            if (Abstraction.MULTIPLICITY.isZero(g.multiplicityOf(n))) {
                toRemove.add(n);
            }
        }
        for (Node n : toRemove) {
            g.removeNode(n);
        }
    }

    private static void _addConcrToAbstr(DefaultAbstrGraph ag, Graph cp,
            ConcretePart.Typing typing) {
        // Add the nodes
        for (Node n : cp.nodeSet()) {
            ag.addTo(typing.typeOf(n), 1);
        }

        // Add the edges
        for (Edge ee : cp.edgeSet()) {
            DefaultEdge e = (DefaultEdge) ee;
            ag.addEdgeBetweenPatterns(typing.typeOf(e.source()), e.label(),
                typing.typeOf(e.target()));
        }
    }

    // --------------------------------------------------------------------------------------
    // AUXILIARY ALGORITHMS
    // --------------------------------------------------------------------------------------

    /**
     * Computes the new type of a node after transformation.
     * @param n A node from the concrete part.
     * @param neighN The neighbourhood of <code>n</code> in the old concrete
     *        part.
     * @param typeN the type of <code>n</code> before transformation.
     * @param typeMorph A morphism from the neighbourhood of n in the concrete
     *        part into the corresponding type (from neighN into typeN).
     * @require n is not deleted by the rule application
     * @return The new pattern for the node n.
     * @throws ExceptionIncompatibleWithMaxIncidence
     */
    private GraphPattern newType(Node n, Graph neighN, GraphPattern typeN,
            NodeEdgeMap typeMorph, NodeFactory nodeFactory)
        throws ExceptionIncompatibleWithMaxIncidence {
        // For an injective morphism
        // oldNeigh, newNeigh, t: oldNeigh -> oldType,
        // - remove nodes/edges from oldType :
        // - for all node/edge not in the domain of ra.getMorphism(), remove
        // typeMorph(node/edge) from oldType
        // - compute the morphism mm : newNeigh -> oldType s.t. m \circ
        // ra.getMorphism() = t
        // - dunion of newNeigh, oldType(modified) with m

        Graph newNeigh = null;
        try {
            newNeigh =
                this.abstrPart.family().getNeighInGraph(this.newConcrPart,
                    this.morph.getNode(n));
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            throw e;
        }
        Graph modTypeN = typeN.clone();
        NodeEdgeMap mm = new NodeEdgeHashMap();
        for (Node nn : neighN.nodeSet()) {
            if (!this.morph.containsKey(n)) {
                modTypeN.removeNode(typeMorph.getNode(nn));
            } else {
                Node x = this.morph.getNode(nn);
                if (newNeigh.nodeSet().contains(x)) {
                    mm.putNode(x, typeMorph.getNode(nn));
                }
            }
        }
        for (Edge ee : neighN.edgeSet()) {
            if (!this.morph.containsKey(ee)) {
                modTypeN.removeEdge(typeMorph.getEdge(ee));
            } else {
                Edge x = this.morph.getEdge(ee);
                if (newNeigh.edgeSet().contains(x)) {
                    mm.putEdge(x, typeMorph.getEdge(ee));
                }
            }
        }
        Util.dunion(newNeigh, modTypeN, mm, nodeFactory);
        try {
            return this.abstrPart.family().computeAddPattern(newNeigh,
                this.morph.getNode(n));
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            // What to do ?
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Removes from the set of origins all these that are not possible due to
     * multiplicity constraints.
     */
    private void cleanImpossibleOrigins() {
        Iterator<ExtendedVarNodeEdgeMap> it = this.origins.iterator();
        while (it.hasNext()) {
            ExtendedVarNodeEdgeMap next = it.next();
            if (!this.abstrPart.isInjectiveMap(next)) {
                it.remove();
            }
        }

        // If some of the non center nodes of the concrete part do not have a
        // correct typing

    }

    /**
     * Constructs a new abstract part by updating multiplicities of
     * this.abstrPart w.r.t. some origin.
     * @param om An embedding into this.abstrPart
     * @return Copy of this.abstrPart with updated multiplicities w.r.t.
     *         <code>om</code>.
     */
    private DefaultAbstrGraph newAbstractPart(ExtendedVarNodeEdgeMap om) {
        DefaultAbstrGraph result = new DefaultAbstrGraph(this.abstrPart);
        for (Node n : new HashSet<Node>(om.nodeMap().values())) {
            try {
                result.removeFrom(n, om.getNbPreIm(n));
            } catch (ExceptionRemovalImpossible e) {
                // Never happens, as only possible origin embeddings are
                // considered
                e.printStackTrace();
            }
        }
        return result;
    }

    /** If high links' precision. */
    private void possibleLinksHigh(final VarNodeEdgeMap origin,
            final Map<Node,MapPattern> typing, ArrayList<Set<Edge>> srcLinks,
            ArrayList<Set<Edge>> tgtLinks, ArrayList<Set<Node>> zeroNodes,
            ArrayList<Set<Edge>> consumedEdges,
            ArrayList<Set<Edge>> internalEdges, NodeFactory nodeFactory) {
        assert srcLinks.size() == 0 && tgtLinks.size() == 0 : "The out parameter sets are not empty.";

        ConcretePart.SubTyping subTyping = new ConcretePart.SubTyping() {
            public GraphPattern typeOf(Node n) {
                return SetMaterialisations.this.abstrPart.typeOf(origin.getNode(n));
            }

            public NodeEdgeMap typeMapOf(Node n) {
                return typing.get(n).getMap();
            }
        };

        // First compute all the extensions, and for each extension its possible
        // embeddings
        for (Graph g : ConcretePart.extensions(this.concrPart, getDist1Nodes(),
            subTyping, this.abstrPart.family(),
            this.options.SYMMETRY_REDUCTION, nodeFactory)) {

            // Determine the new edges, which are to be added to the concrete
            // part
            Set<Edge> currInternalEdges = new HashSet<Edge>();
            for (Edge e : g.edgeSet()) {
                if (this.concrPart.graph().containsElement(e.source())
                    && this.concrPart.graph().containsElement(e.opposite())
                    && !this.concrPart.graph().containsElement(e)) {
                    currInternalEdges.add(e);
                }
            }

            // Determine the new nodes, common to all possible embeddings
            Collection<Node> newNodes = new ArrayList<Node>();
            for (Node n : g.nodeSet()) {
                if (!this.concrPart.graph().containsElement(n)) {
                    newNodes.add(n);
                }
            }

            for (VarNodeEdgeMap emb : Util.getMatchesIter(g, this.abstrPart,
                origin)) {
                // For each embedding, test whether it is a possible embedding
                // and if yes, compute the corresponding set of links
                // OPTIM This test is inefficient, as it takes into account all
                // nodes, and not only new nodes
                Set<Node> currZeroNodes =
                    this.abstrPart.zeroMultNodes(new ExtendedVarNodeEdgeMap(emb));
                if (currZeroNodes == null) {
                    // this embedding is not possible
                    continue;
                }

                Set<Edge> currSrcLinks = new HashSet<Edge>();
                Set<Edge> currTgtLinks = new HashSet<Edge>();
                Set<Edge> currConsEdges = new HashSet<Edge>();
                for (Node n : newNodes) {
                    for (Edge ee : g.edgeSet(n)) {
                        DefaultEdge e = (DefaultEdge) ee;
                        // e should be an edge between a new node and an
                        // existing node
                        if (!newNodes.contains(e.source())
                            || !newNodes.contains(e.target())) {
                            Node imageN = emb.getNode(n);
                            if (e.source() == n) {
                                currTgtLinks.add(DefaultEdge.createEdge(imageN,
                                    e.label(), e.target()));
                            } else {
                                currSrcLinks.add(DefaultEdge.createEdge(
                                    e.source(), e.label(), imageN));
                            }
                        }
                    }
                }
                srcLinks.add(currSrcLinks);
                tgtLinks.add(currTgtLinks);
                zeroNodes.add(currZeroNodes);
                consumedEdges.add(currConsEdges);
                internalEdges.add(currInternalEdges);
            }
        }
    }

    /**
     * Computes all possible linking edges between the concrete part and the
     * abstract part, in the case of low precision.
     * 
     * 
     */
    private void possibleLinksLow(final VarNodeEdgeMap origin,
            ArrayList<Set<Edge>> srcLinks, ArrayList<Set<Edge>> tgtLinks,
            ArrayList<Set<Edge>> consumedEdges,
            ArrayList<Set<Edge>> internalEdges) {

        srcLinks.add(new HashSet<Edge>());
        tgtLinks.add(new HashSet<Edge>());
        internalEdges.add(new HashSet<Edge>());
        Set<Edge> x = Collections.emptySet();
        consumedEdges.add(x);
        Set<Node> zeroMultNodes =
            this.abstrPart.zeroMultNodes(new ExtendedVarNodeEdgeMap(origin));
        ArrayList<Node> linkableNodes = getLinkableNodesLow();
        ArrayList<Node> images = new ArrayList<Node>(linkableNodes.size());
        for (int i = 0; i < linkableNodes.size(); i++) {
            images.add(origin.getNode(linkableNodes.get(i)));
        }
        for (int i = 0; i < linkableNodes.size(); i++) {
            Node node = linkableNodes.get(i);
            Node imageN = images.get(i);

            // the srcLinks
            for (Edge ee : this.abstrPart.edgeSet(imageN, Edge.SOURCE_INDEX)) {
                DefaultEdge e = (DefaultEdge) ee;
                if (!zeroMultNodes.contains(e.target())) {
                    srcLinks.get(0).add(
                        DefaultEdge.createEdge(node, e.label(), e.target()));
                } else {
                    // this will be an internal edge
                    ArrayList<Integer> targetsIndices =
                        new ArrayList<Integer>();
                    for (int k = 0; k < images.size(); k++) {
                        if (images.get(k).equals(e.target())) {
                            targetsIndices.add(k);
                        }
                    }
                    for (int k = 0; k < targetsIndices.size(); k++) {
                        internalEdges.get(0).add(
                            DefaultEdge.createEdge(node, e.label(),
                                linkableNodes.get(k)));
                    }
                }
            }

            // the tgtLinks
            for (Edge ee : this.abstrPart.edgeSet(imageN, Edge.TARGET_INDEX)) {
                DefaultEdge e = (DefaultEdge) ee;
                if (!zeroMultNodes.contains(e.source())) {
                    tgtLinks.get(0).add(
                        DefaultEdge.createEdge(e.source(), e.label(), node));
                } else {
                    // this will be an internal edge
                    ArrayList<Integer> sourcesIndices =
                        new ArrayList<Integer>();
                    for (int k = 0; k < images.size(); k++) {
                        if (images.get(k).equals(e.source())) {
                            sourcesIndices.add(k);
                        }
                    }
                    for (int k = 0; k < sourcesIndices.size(); k++) {
                        internalEdges.get(0).add(
                            DefaultEdge.createEdge(linkableNodes.get(k),
                                e.label(), node));
                    }
                }
            }
        }
    }

    /**
     * The set of nodes in the concrete part that can be linked to nodes of the
     * abstract part, in case of options.LINK_PRECISION ==
     * Abstraction.LinkPrecision.LOW
     * 
     */
    private ArrayList<Node> getLinkableNodesLow() {
        assert this.options.LINK_PRECISION == Abstraction.LinkPrecision.LOW : "Should not use this with high precision";
        ArrayList<Node> linkableNodes = new ArrayList<Node>();
        linkableNodes.addAll(this.concrPart.graph().nodeSet());
        if (this.abstrPart.family().getRadius() != 0) {
            linkableNodes.removeAll(this.concrPart.centerNodes());
        }
        return linkableNodes;
    }

    /** Callback method initializing the distance one nodes whenever necessary. */
    private Collection<Node> getDist1Nodes() {
        if (this.dist1Nodes == null) {
            this.dist1Nodes = this.concrPart.nodesAtDist(1);
        }
        return this.dist1Nodes;
    }

    // /** Nodes in the concrete part that support links with nodes of the
    // abstract part.
    // * Used only for low precision. */
    // private Collection<Node> linkableNodes;
    /** The nodes at distance one from the center, in the concrete part. */
    private Collection<Node> dist1Nodes;

    /**
     * Constructs the possible links given an embedding of the concrete part
     * into the abstract part, a typing of the the nodes in the concrete part.
     * @param origin Embedding of the concrete part into the abstract part.
     * @param typing Associates a typing morphism with nodes from the concrete
     *        part. Only the map components are used.
     * @param srcLinks Out parameter. After return, contains the set of links
     *        which source node is in the concrete part.
     * @param tgtLinks Out parameter. After return, contains the set of links
     *        which target node is in the concrete part.
     * @param zeroNodes Out parameter. After return, contains the set of nodes
     *        which multiplicity became 0 while computing the links.
     * @param consumedEdges Out parameter. After return, contains the edges from
     *        the abstract part that have been consumed by some link.
     * @param internalEdges Out parameter. After return, contains the edges
     *        between nodes of the concrete part that have to be added to the
     *        concrete part.
     * @require srcLinks and tgtLinks are empty sets and zeroNodes
     */
    private void possibleLinks(final VarNodeEdgeMap origin,
            final Map<Node,MapPattern> typing, ArrayList<Set<Edge>> srcLinks,
            ArrayList<Set<Edge>> tgtLinks, ArrayList<Set<Node>> zeroNodes,
            ArrayList<Set<Edge>> consumedEdges,
            ArrayList<Set<Edge>> internalEdges, NodeFactory nodeFactory) {
        if (this.abstrPart.family().getRadius() == 0
            || this.options.LINK_PRECISION == Abstraction.LinkPrecision.LOW) {
            possibleLinksLow(origin, srcLinks, tgtLinks, consumedEdges,
                internalEdges);
        } else {
            possibleLinksHigh(origin, typing, srcLinks, tgtLinks, zeroNodes,
                consumedEdges, internalEdges, nodeFactory);
        }
    }

    /**
     * Updates a matching that matched into the abstract graph to match into the
     * concrete part.
     * @param match
     */
    public VarNodeEdgeMap updateMatch(VarNodeEdgeMap match) {
        VarNodeEdgeMap result = new VarNodeEdgeHashMap();
        for (Node n : match.nodeMap().keySet()) {
            result.putNode(n, n);
        }
        for (Edge e : match.edgeMap().keySet()) {
            result.putEdge(e, e);
        }
        return result;
    }

    // --------------------------------------------------------------------------------------
    // FIELDS, CONSTRUCTORS, STANDARD METHODS
    // --------------------------------------------------------------------------------------

    // FIELDS USED FOR MATERIALISATION
    /** The common concrete part of the set of materialisations. */
    private final ConcretePart concrPart;
    /** The common abstract part. */
    private final DefaultAbstrGraph abstrPart;
    /** The initial matching used for constructing the concrete part. */
    private final NodeEdgeMap originBase;

    /**
     * With each couple of concrete and abstract nodes (cn, an) associates the
     * set of possible typings of cn w.r.t. its type determined by an. The keys
     * of this map are exactly the couples (cn,an) that appear in some of the
     * origin mappings and s.t. cn is not a central node in the concrete part.
     */
    private Map<CNN,Collection<MapPattern>> data;
    /**
     * Used to store the types of the center and new nodes of the transformed
     * concrete part, after they are computed.
     */
    private Map<Node,GraphPattern> centerType;
    /** The set of possible embedings of concrPart.graph() into abstrPart. */
    private Collection<ExtendedVarNodeEdgeMap> origins;

    // FIELDS USED FOR TRANSFORMATION
    /** Set when the transformation is performed */
    private boolean transformed;

    private Graph newConcrPart;
    /**
     * The morphism of the rule application used for transforming the concrete
     * part.
     */
    private Morphism morph;
    // private Map<CNN, Collection<GraphPattern>> newData;

    private final Abstraction.Parameters options;

    /**
     * Defines (but does not compute) the set of materialisations.
     * @param cp
     * @param ag
     * @param origin Defines also the matching used for the transformation
     * @see #computeSet()
     */
    public SetMaterialisations(ConcretePart cp, DefaultAbstrGraph ag,
            NodeEdgeMap origin, Abstraction.Parameters options) {
        this.concrPart = cp;
        this.abstrPart = ag;
        this.originBase = origin;
        this.transformed = false;
        this.options = options;
    }

    @Override
    public String toString() {
        String result = new String();
        result += "Abstr part : " + this.abstrPart + "\n";
        result += "Concr part : " + this.concrPart + "\n";
        result += "Origins    : \n";
        for (ExtendedVarNodeEdgeMap originMap : this.origins) {
            result += "  " + originMap + "\n";
        }

        result += "Typing data: ";
        result += "{\n";
        for (Map.Entry<CNN,Collection<MapPattern>> entry : this.data.entrySet()) {
            result += "  " + entry.getKey() + "=" + entry.getValue() + "\n";
        }
        result += "}";

        return result;
    }

    // --------------------------------------------------------------------------------------
    // SUBTYPES
    // --------------------------------------------------------------------------------------

    /** Represents a couple of a map and a pattern. */
    class MapPattern {
        private final VarNodeEdgeMap map;
        private GraphPattern pattern;

        MapPattern(VarNodeEdgeMap map) {
            this.map = map;
        }

        VarNodeEdgeMap getMap() {
            return this.map;
        }

        GraphPattern getPattern() {
            return this.pattern;
        }

        void setPattern(GraphPattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            return SetMaterialisations.this.transformed ? "(* "
                + getMap().toString() + ", * ," + getPattern().toString()
                + " *)" : getMap().toString();
        }
    }

    // ----------------------------------------------------------------------------------
    // CHECKING INVARIANTS AND PROPERTIES
    // ----------------------------------------------------------------------------------

    /**
     * Check whether all the nodes of the new concrete part have their new type
     * computed.
     */
    private void checkFullTyping() {
        if (!Util.ea()) {
            return;
        }
        if (!this.transformed) {
            return;
        }
        for (ExtendedVarNodeEdgeMap origin : this.origins) {
            for (Map.Entry<Node,Node> entry : origin.nodeMap().entrySet()) {
                if (this.concrPart.centerNodes().contains(entry.getKey())) {
                    continue;
                }
                CNN couple = CNN.cnn(entry.getKey(), entry.getValue());
                Collection<MapPattern> dataC = this.data.get(couple);
                assert dataC != null : "No data entry for " + couple;
                assert dataC.size() != 0 : "Zero typings for " + couple;
                for (MapPattern p : dataC) {
                    assert p.getMap() != null && p.getPattern() != null : "Map or pattern missing for a data for "
                        + couple;
                }
            }
        }

        for (Node n : this.concrPart.centerNodes()) {
            if (!this.morph.containsKey(n)) {
                continue;
            }
            assert this.centerType.get(n) != null : "Type missing for center node "
                + n;
        }
        for (Node n : this.newConcrPart.nodeSet()) {
            if (!this.morph.nodeMap().values().contains(n)) { // this is a new
                // node
                assert this.centerType.get(n) != null : "Type missing for new node "
                    + n;
            }
        }
    }
}
