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
package groove.abs;

import groove.abs.Abstraction.AbstrGraphsRelation;
import groove.abs.Abstraction.MultInfoRelation;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.TreeHashSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A pattern shape graph is a graph together with types (graph patterns) and
 * multiplicities associated to its nodes.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class DefaultAbstrGraph extends DefaultGraph implements AbstrGraph {

    /** Used by AGTS. */
    public static final AbstrGraph INVALID_AG = new DefaultAbstrGraph();

    /**
     * Creates a factory object with specified family and precision.
     * @param family
     * @param precision
     * @return a factory object
     */
    public static DefaultAbstrGraph.Factory factory(PatternFamily family,
            int precision) {
        return (new DefaultAbstrGraph()).new Factory(family, precision);
    }

    /**
     * The multiplicity of n in the shape
     * @param n
     * @return the multiplicity of n
     * @require n is a node of this shape
     */
    public MultiplicityInformation multiplicityOf(Node n) {
        assert containsElement(n) : "Node " + n + " not is this shape";
        return this.type.get(n).getMult();
    }

    /**
     * The type (graph pattern) of a node.
     * @param n
     * @return the type of n
     * @require n is a node of this shape
     */
    public GraphPattern typeOf(Node n) {
        assert containsElement(n) : "Node " + n + " not is this shape";
        return this.type.get(n).getPattern();
    }

    /**
     * Returns the unique node having some type, or null if this type is not
     * present in the graph.
     * @param p
     * @return The unique node having type <code>p</code>, or null if such
     *         node does not exist.
     */
    final public Node nodeFor(GraphPattern p) {
        checkInvariants();
        return this.invType.get(p);
    }

    public void removeFrom(Node n, int q) throws ExceptionRemovalImpossible {
        checkInvariants();
        NodeType c = this.type.get(n);
        this.type.put(
            n,
            new NodeType(c.getPattern(), Abstraction.MULTIPLICITY.remove(
                c.getMult(), q)));
        checkInvariants();
    }

    /**
     * Ensures that the pattern is represented in the graph, and adds q to its
     * multiplicity.
     * @return <code>true</code> if the multiplicity of the pattern p was zero
     */
    public boolean addTo(GraphPattern p, int q) {
        checkInvariants();
        Node old = nodeFor(p);
        boolean result =
            old != null && Abstraction.MULTIPLICITY.isZero(multiplicityOf(old));
        Node n = old != null ? old : ensureType(p);
        NodeType c = this.type.get(n);
        this.type.put(n,
            new NodeType(p, Abstraction.MULTIPLICITY.add(c.getMult(), q)));
        checkInvariants();
        return result;
    }

    /**
     * Two abstract graphs are isomorphic if there is an isomorphism on the
     * underlying graph structure that preserves typing.
     */
    public Morphism getIsomorphismToAbstrGraph(AbstrGraph other) {
        checkInvariants();
        if (family() != other.family()) {
            return null;
        }
        if (nodeCount() != other.nodeCount()) {
            return null;
        }
        if (edgeCount() != other.edgeCount()) {
            return null;
        }
        // OPTIM test equality of graphs certificates here to improve
        // performance

        // Construct a map matching nodes with same type
        NodeEdgeMap map = new NodeEdgeHashMap();
        for (Map.Entry<GraphPattern,Node> entry : this.invType.entrySet()) {
            Node image = other.nodeFor(entry.getKey());
            if (image == null) {
                return null;
            }
            map.putNode(entry.getValue(), image);
        }
        for (VarNodeEdgeMap r : Util.getInjMatchesIter(this, other, map)) {
            Morphism result = new DefaultMorphism(this, other, r);
            if (result.isSurjective()) {
                return result;
            }
        }
        checkInvariants();
        return null;
    }

    /**
     * Determines whether a given map into this abstract graph is injective. A
     * map into an abstract graph is injective if it respects multiplicities:
     * the number of pre-images of each nodes of the graph is less than its
     * multiplicity.
     * @param om A map into this abstract graph
     * @return True if <code>om</code> is injective.
     */
    public boolean isInjectiveMap(NodeEdgeMap om) {
        ExtendedVarNodeEdgeMap map = null;
        if (om instanceof ExtendedVarNodeEdgeMap) {
            map = (ExtendedVarNodeEdgeMap) om;
        } else {
            map = new ExtendedVarNodeEdgeMap(om);
        }

        Map<GraphPattern,Integer> maxAllowed = maxAllowed();
        for (Node n : map.nodeMap().values()) {
            Integer max = maxAllowed.get(typeOf(n));
            if (max != null && map.getNbPreIm(n) > max) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used to save a morphism used for the equals or quasiEquals functions.
     * This is an optimisation in case the same equality test is performed twice
     * one after the other.
     */
    private static Morphism saveEqualsMorphism = null;

    public AbstrGraphsRelation compare(AbstrGraph other, boolean belongsIsSub) {
        if (!family().equals(other.family())
            || precision() != other.precision()) {
            return Abstraction.AbstrGraphsRelation.NOTEQUAL;
        }
        Morphism morphism;

        // test whether the same test was already performed recently
        if (saveEqualsMorphism != null && saveEqualsMorphism.dom() == this
            && saveEqualsMorphism.cod() == other) {
            morphism = saveEqualsMorphism;
        } else {
            morphism = getIsomorphismToAbstrGraph(other);
        }
        if (morphism == null) {
            return Abstraction.AbstrGraphsRelation.NOTEQUAL;
        }

        // if the morphism is not null, then the graphs are at least quasi-equal
        // the result won't be NOTEQUAL, thus save the morphism in case the next
        // call compares the same graphs
        if (isFixed() && other.isFixed()) {
            saveEqualsMorphism = morphism;
        }

        Iterator<Map.Entry<Node,Node>> it =
            morphism.elementMap().nodeMap().entrySet().iterator();
        // first phase : guess a relation, by the first non equal tuple of nodes
        AbstrGraphsRelation result = AbstrGraphsRelation.EQUAL;
        while (it.hasNext() && result == AbstrGraphsRelation.EQUAL) {
            Map.Entry<Node,Node> entry = it.next();
            MultInfoRelation compare =
                Abstraction.MULTIPLICITY.compare(
                    multiplicityOf(entry.getKey()),
                    other.multiplicityOf(entry.getValue()));
            switch (compare) {
            case M_EQUAL:
                break;
            case M_SUBSET:
                result = AbstrGraphsRelation.SUB;
                break;
            case M_SUPERSET:
                result = AbstrGraphsRelation.SUPER;
                break;
            case M_BELONGS:
                if (belongsIsSub) {
                    result = AbstrGraphsRelation.SUB;
                } else {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_CONTAINS:
                if (belongsIsSub) {
                    result = AbstrGraphsRelation.SUPER;
                } else {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_NOTEQUAL:
                return AbstrGraphsRelation.QUASI;
            }
        }

        // second phase : check that guess is preserved
        while (it.hasNext()) {
            Map.Entry<Node,Node> entry = it.next();
            MultInfoRelation compare =
                Abstraction.MULTIPLICITY.compare(
                    multiplicityOf(entry.getKey()),
                    other.multiplicityOf(entry.getValue()));
            switch (compare) {
            case M_EQUAL:
                break; // never happens
            case M_SUBSET:
                if (result != AbstrGraphsRelation.SUB) {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_SUPERSET:
                if (result != AbstrGraphsRelation.SUPER) {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_BELONGS:
                if (!belongsIsSub || result != AbstrGraphsRelation.SUB) {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_CONTAINS:
                if (!belongsIsSub || result != AbstrGraphsRelation.SUPER) {
                    return AbstrGraphsRelation.QUASI;
                }
                break;
            case M_NOTEQUAL:
                return Abstraction.AbstrGraphsRelation.QUASI;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultAbstrGraph)) {
            return false;
        }
        DefaultAbstrGraph g = (DefaultAbstrGraph) o;
        Morphism m = getIsomorphismToAbstrGraph(g);
        if (m == null) {
            return false;
        }
        for (Map.Entry<Node,Node> nn : m.nodeMap().entrySet()) {
            if (!multiplicityOf(nn.getKey()).equals(
                g.multiplicityOf(nn.getValue()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * @return the family of this shape
     */
    final public PatternFamily family() {
        return this.family;
    }

    /**
     * @return the precision of this shape
     */
    final public int precision() {
        return this.precision;
    }

    /**
     * Checks whether this PatternShapeGraph represents a unique shape (i.e. all
     * multiplicities are precise)
     * @return true if all multiplicities are precise
     */
    public boolean isPrecise() {
        for (NodeType c : this.type.values()) {
            if (!Abstraction.MULTIPLICITY.isPrecise(c.getMult())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether this PatternShapeGraph represents a normalised shape (i.e.
     * a unique node per type)
     * @return true if each PatternGraph present as type of a node of this shape
     *         is the node of a unique shape
     */
    public boolean isNormalised() {
        Set<GraphPattern> setOfTypes = new TreeHashSet<GraphPattern>();
        for (Node n : nodeSet()) {
            GraphPattern t = typeOf(n);
            if (setOfTypes.contains(t)) {
                return false;
            }
            setOfTypes.add(t);
        }
        return true;
    }

    @Override
    public boolean removeNode(Node n) {
        assert Abstraction.MULTIPLICITY.isZero(multiplicityOf(n)) : "Removing node with non zero multiplicity";
        GraphPattern typeOf = typeOf(n);
        boolean result = super.removeNode(n);
        if (result) {
            this.invType.remove(typeOf);
            this.type.remove(n);
        }
        return result;
    }

    @Override
    public boolean removeNodeWithoutCheck(Node n) {
        throw new UnsupportedOperationException();
    }

    // ----------------------------------------------------------------------------------
    // SUBTYPES
    // ----------------------------------------------------------------------------------

    /**
     * Computes a map indicating the maximal allowed number of nodes of given
     * type, whenever this number is finite.
     * @return A map from GraphPatterns that are types in this  abstract graph
     *         into their multiplicity, whenever this multiplicity is finite.
     *         OPTIM this is computed each time. Maybe it should be
     *         pre-computed, and updated whenever a graph is changed.
     */
    Map<GraphPattern,Integer> maxAllowed() {
        checkInvariants();
        Map<GraphPattern,Integer> result = new HashMap<GraphPattern,Integer>();
        for (Node n : nodeSet()) {
            int card = Abstraction.MULTIPLICITY.preciseCard(multiplicityOf(n));
            if (card != -1) {
                // I assume that each type is associated to a unique node. This
                // invariant could be checked
                result.put(typeOf(n), card);
            }
        }
        checkInvariants();
        return result;
    }

    private Node ensureType(GraphPattern p) {
        checkInvariants();
        Node n = nodeFor(p);
        // The pattern is not in the graph
        if (n != null) {
            return n;
        }
        n = addNode();
        this.type.put(
            n,
            new NodeType(p, Abstraction.MULTIPLICITY.getElement(0,
                this.precision)));
        this.invType.put(p, n);
        checkInvariants();
        return n;
    }

    /**
     * Computes the set of nodes that have a zero multiplicity with a given
     * embedding.
     * @param om The embedding.
     * @return The set of nodes with zero multiplicity, or null if the embedding
     *         is not possible.
     */
    Set<Node> zeroMultNodes(ExtendedVarNodeEdgeMap om) {
        Map<GraphPattern,Integer> maxAllowed = maxAllowed();
        Set<Node> result = new HashSet<Node>();
        for (Node n : om.nodeMap().values()) {
            Integer max = maxAllowed.get(typeOf(n));
            if (max != null && om.getNbPreIm(n) > max) {
                return null;
            } else if (max != null && om.getNbPreIm(n) == max) {
                result.add(n);
            }
        }
        return result;
    }

    /**
     * Adds an edge between the nodes corresponding to two patterns.
     * @param sourcePattern
     * @param l
     * @param targetPattern
     * @require sourcePattern and targetPattern are present in the abstract
     *          graph
     * @return The edge possibly already present between these nodes, or null if
     *         there was no such edge.
     */
    public Edge addEdgeBetweenPatterns(GraphPattern sourcePattern, Label l,
            GraphPattern targetPattern) {
        return addEdge(nodeFor(sourcePattern), l, nodeFor(targetPattern));
    }

    /**
     * Adds an edge between the node corresponding to some pattern and another
     * node.
     * @param sourcePattern
     * @param l
     * @param targetNode
     * @require sourcePattern and targetNode are present in the abstract graph
     * @return The edge possibly already present between these nodes, or null if
     *         there was no such edge.
     */
    public Edge addEdgeBetweenPatterns(GraphPattern sourcePattern, Label l,
            Node targetNode) {
        return addEdge(nodeFor(sourcePattern), l, targetNode);
    }

    /**
     * Adds an edge between a node and the node corresponding to some pattern.
     * @param sourceNode
     * @param l
     * @param targetPattern
     * @require sourceNode and targetPattern are present in the abstract graph
     * @return The edge possibly already present between these nodes, or null if
     *         there was no such edge.
     */
    public Edge addEdgeBetweenPatterns(Node sourceNode, Label l,
            GraphPattern targetPattern) {
        return addEdge(sourceNode, l, nodeFor(targetPattern));
    }

    void computeHashCode() {
        Object certificate = getCertifier(true).getGraphCertificate();
        this.hashCode = certificate.hashCode();
    }

    // ----------------------------------------------------------------------------------
    // FIELDS, CONSTRUCTORS AND STANDARD METHODS
    // ----------------------------------------------------------------------------------

    /**
     * Private constructor to be used only for creating a factory and for
     * INVALID_AG.
     */
    private DefaultAbstrGraph() {
        this.family = null;
        this.precision = 0;
        this.type = new HashMap<Node,NodeType>(0);
        this.invType = new HashMap<GraphPattern,Node>(0);
    }

    /**
     * Copying constructor.
     * @param other
     */
    DefaultAbstrGraph(DefaultAbstrGraph other) {
        super(other);
        this.family = other.family();
        this.precision = other.precision();
        this.type = new HashMap<Node,NodeType>(other.type);
        this.invType = new HashMap<GraphPattern,Node>(other.invType);
        this.hashCode = other.hashCode;
    }

    /**
     * Private constructor. Used by the factory and for creating the invalid
     * abstract graph.
     * @param family
     * @param precision
     */
    DefaultAbstrGraph(PatternFamily family, int precision) {
        super();
        this.family = family;
        this.precision = precision;
    }

    /** The Pattern family that defines types for nodes in this shape graph. */
    protected final PatternFamily family;
    /** The precision of multiplicity information. */
    protected final int precision;
    /**
     * Associates a pattern and a multiplicity with each node in the graph (the
     * couple is called type).
     * @invariant (A) nodeSet() is equal to typeMult.keySet()
     * @invariant (B) for all node of this graph, multiplicityOf(node) is a
     *            multiplicity with precision this.precision
     * @invariant (C) for all node of this graph, typeOf(node) is issued by
     *            this.family
     */
    protected Map<Node,NodeType> type;
    /**
     * Associates the nodes to their types.
     * @invariant (D) invType.get(type.get(n).getPattrn()) == n
     */
    protected Map<GraphPattern,Node> invType;
    /** The hash code. Is computed on construct time. */
    private int hashCode;

    @Override
    public String toString() {
        String result = super.toString();
        result += "; Types: " + this.type + ";";
        return result;
    }

    /**
     * A string representation of the underlying graph.
     * @return A string representation of the underlying graph.
     */
    protected String graphToString() {
        return super.toString();
    }

    /**
     * @return A shallow copy of the type map of this PatternShapeGraph. Keys
     *         and values in the map are not copied, only the map is.
     */
    Map<Node,NodeType> typeMapClone() {
        return new HashMap<Node,NodeType>(this.type);
    }

    // ----------------------------------------------------------------------------------
    // SUBTYPES
    // ----------------------------------------------------------------------------------

    /**
     * A factory for PatternShapeGraph objects. Such a factory is the unique way
     * for obtaining PatternShapeGraph objects.
     * @author io
     * 
     */
    public class Factory {
        /** The Pattern family that defines types for nodes in this shape graph. */
        private final PatternFamily myFamily;
        /** The precision of multiplicity information. */
        private final int myPrecision;

        /**
         * Creates a factory for PatternShapeGraph objects with common
         * PatternFamily and precision.
         * @param family
         * @param precision
         */
        public Factory(PatternFamily family, int precision) {
            super();
            this.myFamily = family;
            this.myPrecision = precision;
        }

        /**
         * @return the family of this factory
         */
        final public PatternFamily getFamily() {
            return this.myFamily;
        }

        /**
         * @return the precision of the factory
         */
        final public int getPrecision() {
            return this.myPrecision;
        }

        /**
         * Computes the abstract graph for g. All computed patterns are added to
         * the family of this factory.
         * @param graph
         * @return The computed shape graph
         * @throws ExceptionIncompatibleWithMaxIncidence
         */
        public DefaultAbstrGraph getShapeGraphFor(Graph graph)
            throws ExceptionIncompatibleWithMaxIncidence {

            DefaultAbstrGraph result =
                new DefaultAbstrGraph(this.myFamily, this.myPrecision);
            // OPTIM the initial capacity may probably be optimised
            result.type = new HashMap<Node,NodeType>(graph.nodeCount());
            result.invType = new HashMap<GraphPattern,Node>(graph.nodeCount());

            // Stores the pattern computed for each node of the parameter graph
            Map<Node,GraphPattern> computedPattern =
                new HashMap<Node,GraphPattern>(graph.nodeCount());
            // Stores the correspondence between the computed patterns and the
            // corresponding node in the result shape
            // (inverse to the typeMult map when multiplicities are abstracted)
            // Should be added an entry at the same time as to result.typeMult
            Map<GraphPattern,Node> inverseMap =
                new HashMap<GraphPattern,Node>(graph.nodeCount());

            // for each node of the graph, compute the corresponding pattern,
            // add a new node to the resulting graph if necessary, and update
            // the typeMult map
            for (Node currNode : graph.nodeSet()) {
                GraphPattern pattern =
                    this.myFamily.computeAddPattern(graph, currNode);
                computedPattern.put(currNode, pattern);

                Node nodeInResult = inverseMap.get(pattern);
                if (nodeInResult != null) {
                    NodeType tt = result.type.get(nodeInResult);
                    tt.setMult(Abstraction.MULTIPLICITY.add(tt.getMult(), 1));
                } else {
                    nodeInResult = DefaultNode.createNode();
                    result.addNode(nodeInResult);
                    result.type.put(
                        nodeInResult,
                        new NodeType(pattern,
                            Abstraction.MULTIPLICITY.getElement(1,
                                this.myPrecision)));
                    result.invType.put(pattern, nodeInResult);
                    inverseMap.put(pattern, nodeInResult);
                }
            }

            // add the edges
            for (Edge e : graph.edgeSet()) {
                Node srcPattern = inverseMap.get(computedPattern.get(e.end(0)));
                Node tgtPattern = inverseMap.get(computedPattern.get(e.end(1)));
                result.addEdge(srcPattern, e.label(), tgtPattern);
            }

            result.setFixed();
            result.computeHashCode();
            result.checkInvariants();
            return result;
        }

    }

    /**
     * This implementation uses the criterion that, for a graph with
     * concretisations, the type of all node should be present in its
     * neighbourhood. That is, returns false if for some <code>node</code> in
     * the graph, there is no morphism from the type of <code>node</code> into
     * the graph that maps the center of the type with <code>node</code>.
     */
    public boolean isWithoutConcretisation() {
        for (Map.Entry<GraphPattern,Node> entry : this.invType.entrySet()) {
            GraphPattern pattern = entry.getKey();
            NodeEdgeMap map = new NodeEdgeHashMap();
            map.putNode(entry.getKey().central(), entry.getValue());
            boolean hasInjective = false;
            for (VarNodeEdgeMap match : Util.getMatchesIter(pattern, this, map)) {
                if (isInjectiveMap(match)) {
                    hasInjective = true;
                    break;
                }
            }
            if (!hasInjective) {
                return true;
            }
        }
        return false;
    }

    // ////////////////////////////////////////////////////////
    // Checking invariants
    // ////////////////////////////////////////////////////////

    /*
     * @invariant (A) nodeSet() is equal to typeMult.keySet()
     * @invariant (B) for all node of this graph, multiplicityOf(node) is a 
     * multiplicity with precision this.precision
     * @invariant (C) for all node of this graph, typeOf(node) is issued by
     * this.family
     */
    void checkInvariants() {
        if (!Util.ea()) {
            return;
        }
        checkInvA();
        checkInvB();
        checkInvC();
        checkInvD();
    }

    /** */
    private void checkInvA() {
        Set<Node> typeKeySet = this.type.keySet();
        assert super.nodeSet().equals(typeKeySet) : "Invariant A failed";
        Set<Node> values = new HashSet<Node>();
        for (Node n : this.invType.values()) {
            values.add(n);
        }
        assert super.nodeSet().equals(values) : "Invariant A failed";
    }

    /** */
    private void checkInvB() {
        for (Node n : super.nodeSet()) {
            MultiplicityInformation mult = this.type.get(n).getMult();
            assert Abstraction.MULTIPLICITY.getPrecision(mult) == this.precision : "Invariant B failed";
        }
    }

    /** */
    private void checkInvC() {
        for (Node n : nodeSet()) {
            GraphPattern type = typeOf(n);
            assert this.family.issued(type) : "Invariant C failed for node "
                + n + " and type " + type;
        }
    }

    private void checkInvD() {
        assert this.invType.size() == nodeSet().size() : "Invariant D failed";
        for (Map.Entry<GraphPattern,Node> entry : this.invType.entrySet()) {
            assert this.type.get(entry.getValue()).getPattern().equals(
                entry.getKey());
        }
    }

    // DEBUGGING CLASS

    /** Returns an instance of the AbstrGraphCreator */
    public static AbstrGraphCreator getAbstrGraphCreatorInstance() {
        return (new DefaultAbstrGraph()).new AbstrGraphCreator();
    }

    /**
     * Allows to construct an abstract graph by giving directly nodes with their
     * multiplicity and type. Debugging class.
     */
    public class AbstrGraphCreator {

        /** */
        public void init(PatternFamily family, int precision) {
            this.graph = new DefaultAbstrGraph(family, precision);
            this.graph.type = new HashMap<Node,NodeType>();
            this.graph.invType = new HashMap<GraphPattern,Node>();
        }

        /**
         * Adds a node with given multiplicity and type. If a node with the
         * given type already exists, then the graph under construction is not
         * modified.
         * @param mult
         * @param type
         * @return The node that has been added (or the one with the same type
         *         that already existed).
         */
        public Node addNode(MultiplicityInformation mult, GraphPattern type) {
            Node result = this.graph.nodeFor(type);
            if (result == null) {
                result = this.graph.addNode();
                this.graph.type.put(result, new NodeType(type, mult));
                this.graph.invType.put(type, result);
            }
            return result;
        }

        /**
         * Adds an edge between two nodes.
         * @param source
         * @param label
         * @param target
         * @require source and target nodes are already in the graph
         * @return the added edge, or the one that already existed, if any
         */
        public Edge addEdge(Node source, Label label, Node target) {
            if (!this.graph.containsElement(source)
                || !this.graph.containsElement(target)) {
                throw new UnsupportedOperationException(
                    "Adding edge only possible between existing nodes.");
            }
            Edge result = DefaultEdge.createEdge(source, label, target);
            this.graph.addEdge(result);
            return result;
        }

        /** */
        public DefaultAbstrGraph getConstructedGraph() {
            return this.graph;
        }

        /** */
        public void setFixed() {
            this.graph.setFixed();
        }

        DefaultAbstrGraph graph;

    }

}
