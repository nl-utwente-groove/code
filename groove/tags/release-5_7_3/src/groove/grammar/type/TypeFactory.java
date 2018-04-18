package groove.grammar.type;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.Sort;
import groove.grammar.QualName;
import groove.graph.EdgeRole;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.util.Pair;

/**
 * Factory creating type nodes and edges.
 * The type nodes are numbered consecutively from 0 onwards.
 */
public class TypeFactory extends ElementFactory<TypeNode,TypeEdge> {
    /**
     * Constructs a factory for a given type graph.
     * Should only be called from the constructor of {@link TypeGraph}.
     * @param typeGraph type graph for the created type nodes and edges;
     * either {@code null} or initially empty
     */
    TypeFactory(TypeGraph typeGraph) {
        assert typeGraph.isEmpty();
        this.typeGraph = typeGraph;
        for (Sort sig : Sort.values()) {
            this.dataTypeMap.put(sig, createNode(TypeLabel.getLabel(sig)));
        }
    }

    @Override
    protected TypeNode newNode(int nr) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the unique top node type, used for implicitly graphs.
     * This is only valid if the type graph is implicit.
     */
    public @NonNull TypeNode getTopNode() {
        TypeNode result = this.topNode;
        if (result == null) {
            this.topNode = result = createNode(TypeLabel.NODE);
        }
        return result;
    }

    /**
     * Looks up or creates a node with a given (non-{@code null}) type label.
     * If the node is created, it is also added to the type graph.
     */
    public @NonNull TypeNode createNode(TypeLabel label) {
        assert label.getRole() == EdgeRole.NODE_TYPE;
        TypeNode result = this.typeNodeMap.get(label);
        if (result == null) {
            result = new TypeNode(getNodeNrDispenser().getNext(), label, getGraph());
            this.typeNodeMap.put(label, result);
            getGraph().addNode(result);
            registerNode(result);
        }
        return result;
    }

    /** Creates a label with the given kind-prefixed text. */
    @Override
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
    public TypeEdge createEdge(TypeNode source, Label label, TypeNode target) {
        return createEdge(source, (TypeLabel) label, target, true);
    }

    /**
     * Retrieves a suitable type edge from the type graph,
     * creating it (and adding it to the graph) if necessary.
     */
    public TypeEdge createEdge(TypeNode source, TypeLabel label, TypeNode target, boolean precise) {
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
    public TypeNode getDataType(Sort signature) {
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
    private final Map<Sort,TypeNode> dataTypeMap = new EnumMap<>(Sort.class);

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

    /** Type node for the top type (in the absence of a type graph). */
    private TypeNode topNode;

    /** Auxiliary map from type labels to type nodes */
    private Map<TypeLabel,TypeNode> typeNodeMap = new HashMap<>();

    /**
     * The internal translation table from strings to type labels,
     * per edge role.
     */
    private final Map<EdgeRole,Map<String,TypeLabel>> labelMaps = new EnumMap<>(EdgeRole.class);

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
        return new TypeGraph(QualName.name("implicit"), true).getFactory();
    }
}