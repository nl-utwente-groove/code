package groove.graph;

import groove.util.Pair;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/** Factory creating type nodes and edges. */
public class TypeFactory implements ElementFactory<TypeNode,TypeEdge> {
    private TypeFactory() {
        // empty
    }

    @Override
    public TypeNode createNode(int nr) {
        throw new UnsupportedOperationException();
    }

    /** Creates a label with the given kind-prefixed text. */
    public TypeLabel createLabel(String text) {
        Pair<EdgeRole,String> parsedLabel = EdgeRole.parseLabel(text);
        return createLabel(parsedLabel.one(), parsedLabel.two());
    }

    /** Returns a label with the given text and label kind. */
    public TypeLabel createLabel(EdgeRole kind, String text) {
        assert text != null : "Label text of type label should not be null";
        return newLabel(null, kind, text);
    }

    /** Returns a flag or binary edge label with the given source type, text and label kind. */
    public TypeLabel createLabel(TypeLabel sourceType, EdgeRole kind,
            String text) {
        assert sourceType != null;
        assert text != null : "Label text of type label should not be null";
        return newLabel(sourceType, kind, text);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, String text, TypeNode target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, Label label, TypeNode target) {
        return new TypeEdge(source, (TypeLabel) label, target);
    }

    /** Type graph morphisms are not supported. */
    @Override
    public Morphism<TypeNode,TypeEdge> createMorphism() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxNodeNr() {
        throw new UnsupportedOperationException();
    }

    /**
     * Yields the number of labels created in the course of the program.
     * @return Number of labels created
     */
    public int getLabelCount() {
        int result = 0;
        for (Map<?,?> map : this.labelMaps.values()) {
            result += map.size();
        }
        return result;
    }

    /**
     * Returns a label with the given text, reusing previously created
     * labels where possible.
     * @param sourceType type label of source nodes with this label, if any. 
     * This is only relevant for flag and binary edge labels
     * @param text the label text being looked up
     * @return the (reused or new) label object.
     */
    private TypeLabel newLabel(TypeLabel sourceType, EdgeRole kind, String text) {
        Map<String,TypeLabel> labelMap;
        if (sourceType == null) {
            labelMap = this.labelMaps.get(kind);
        } else {
            assert sourceType.isNodeType();
            assert kind != EdgeRole.NODE_TYPE;
            labelMap = this.typedLabelMaps.get(kind).get(sourceType);
            if (labelMap == null) {
                labelMap = new HashMap<String,TypeLabel>();
                this.typedLabelMaps.get(kind).put(sourceType, labelMap);
            }
        }
        TypeLabel result = labelMap.get(text);
        if (result == null) {
            int index = labelMap.size();
            result = new TypeLabel(text, kind, index);
            labelMap.put(text, result);
            return result;
        }
        return result;
    }

    /**
     * The internal translation table from strings to label indices,
     * per label type.
     */
    private final Map<EdgeRole,Map<String,TypeLabel>> labelMaps =
        new EnumMap<EdgeRole,Map<String,TypeLabel>>(EdgeRole.class);
    /**
     * The internal translation table from strings to label indices,
     * per label type and source node type.
     */
    private final Map<EdgeRole,Map<TypeLabel,Map<String,TypeLabel>>> typedLabelMaps =
        new EnumMap<EdgeRole,Map<TypeLabel,Map<String,TypeLabel>>>(
            EdgeRole.class);
    {
        for (EdgeRole kind : EnumSet.allOf(EdgeRole.class)) {
            this.labelMaps.put(kind, new HashMap<String,TypeLabel>());
            this.typedLabelMaps.put(kind,
                new HashMap<TypeLabel,Map<String,TypeLabel>>());
        }
    }

    /** Returns the singleton instance of this class. */
    public static TypeFactory instance() {
        return INSTANCE;
    }

    /** Singleton instance of this class. */
    private static final TypeFactory INSTANCE = new TypeFactory();
}