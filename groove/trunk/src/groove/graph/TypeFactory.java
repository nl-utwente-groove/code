package groove.graph;

import groove.algebra.SignatureKind;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Factory creating type nodes and edges.
 * The type nodes are numbered consecutively from 0 onwards.
 */
public class TypeFactory implements ElementFactory<TypeNode,TypeEdge> {
    /**
     * Constructs a factory for a given type graph.
     * Should only be called from the constructor of {@link TypeGraph}.
     * @param typeGraph type graph for the created type nodes and edges; 
     * either {@code null} or initially empty
     */
    TypeFactory(TypeGraph typeGraph) {
        assert typeGraph.isEmpty();
        this.typeGraph = typeGraph;
        for (SignatureKind sig : SignatureKind.values()) {
            this.dataTypeMap.put(sig, createNode(TypeLabel.getLabel(sig)));
        }
    }

    @Override
    public TypeNode createNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    /** 
     * Returns the unique top node type, used for implicitly graphs.
     * This is only valid if the type graph is implicit. 
     */
    public TypeNode getTopNode() {
        if (this.topNode == null) {
            this.topNode = createNode(TypeLabel.NODE);
        }
        return this.topNode;
    }

    /** 
     * Looks up or creates a node with a given (non-{@code null}) type label.
     * If the node is created, it is also added to the type graph.
     */
    public TypeNode createNode(TypeLabel label) {
        assert label.getRole() == EdgeRole.NODE_TYPE;
        TypeNode result = this.typeNodeMap.get(label);
        if (result == null) {
            result = new TypeNode(getNextNodeNr(), label, getGraph());
            this.typeNodeMap.put(label, result);
            this.typeNodeList.add(result);
            getGraph().addNode(result);
        }
        return result;
    }

    /** Looks up a node with a given (non-{@code null}) type label. */
    public TypeNode getNode(TypeLabel label) {
        assert label.getRole() == EdgeRole.NODE_TYPE;
        return this.typeNodeMap.get(label);
    }

    /** Creates a label with the given kind-prefixed text. */
    public TypeLabel createLabel(String text) {
        Pair<EdgeRole,String> parsedLabel = EdgeRole.parseLabel(text);
        return createLabel(parsedLabel.one(), parsedLabel.two());
    }

    /** Returns a label with the given text and label kind. */
    public TypeLabel createLabel(EdgeRole kind, String text) {
        assert text != null : "Label text of type label should not be null";
        return newLabel(kind, text);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, String text, TypeNode target) {
        return createEdge(source, createLabel(text), target, true);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, Label label, TypeNode target) {
        return createEdge(source, (TypeLabel) label, target, true);
    }

    /** 
     * Retrieves a suitable type edge from the type graph,
     * creating it (and adding it to the graph) if necessary.
     */
    public TypeEdge createEdge(TypeNode source, TypeLabel label,
            TypeNode target, boolean precise) {
        TypeEdge result = null;
        result = getGraph().getTypeEdge(source, label, target, precise);
        if (result == null) {
            result = new TypeEdge(source, label, target, getGraph());
            getGraph().addEdge(result);
        }
        return result;
    }

    /** Type graph morphisms are not supported. */
    @Override
    public Morphism<TypeNode,TypeEdge> createMorphism() {
        throw new UnsupportedOperationException();
    }

    /** Returns the default type node for a given data signature. */
    public TypeNode getDataType(SignatureKind signature) {
        return this.dataTypeMap.get(signature);
    }

    /** Returns the default type node for a given data signature. */
    public Collection<TypeNode> getDataTypes() {
        return this.dataTypeMap.values();
    }

    /** Returns the type graph to which this factory belongs. */
    public TypeGraph getGraph() {
        return this.typeGraph;
    }

    /** Mapping from signatures to corresponding type nodes. */
    private final Map<SignatureKind,TypeNode> dataTypeMap =
        new EnumMap<SignatureKind,TypeNode>(SignatureKind.class);

    /**
     * Returns a label with the given text, reusing previously created
     * labels where possible.
     * @param text the label text being looked up
     * @return the (reused or new) label object.
     */
    private TypeLabel newLabel(EdgeRole kind, String text) {
        Map<String,TypeLabel> labelMap;
        labelMap = this.labelMaps.get(kind);
        TypeLabel result = labelMap.get(text);
        if (result == null) {
            result = new TypeLabel(text, kind);
            labelMap.put(text, result);
            return result;
        }
        return result;
    }

    /** Returns the next node number according to an internal counter. */
    private int getNextNodeNr() {
        this.maxNodeNr++;
        return this.maxNodeNr;
    }

    /** The maximum node number used so far. */
    private int maxNodeNr;

    /** Type node for the top type (in the absence of a type graph). */
    private TypeNode topNode;

    /** Auxiliary map from type labels to type nodes */
    private Map<TypeLabel,TypeNode> typeNodeMap =
        new HashMap<TypeLabel,TypeNode>();

    /** List of type nodes, in the order of their node number. */
    private List<TypeNode> typeNodeList = new ArrayList<TypeNode>();

    /**
     * The internal translation table from strings to type labels,
     * per edge role.
     */
    private final Map<EdgeRole,Map<String,TypeLabel>> labelMaps =
        new EnumMap<EdgeRole,Map<String,TypeLabel>>(EdgeRole.class);
    {
        for (EdgeRole kind : EdgeRole.values()) {
            this.labelMaps.put(kind, new HashMap<String,TypeLabel>());
        }
    }

    /** 
     * Type graph for this factory. 
     */
    private final TypeGraph typeGraph;

    /** Returns a fresh factory, backed up by an (also fresh) implicit type graph. */
    public static TypeFactory newInstance() {
        return new TypeGraph("implicit", true).getFactory();
    }
}