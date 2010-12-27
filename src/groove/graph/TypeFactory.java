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
        Pair<LabelKind,String> parsedLabel = LabelKind.parse(text);
        return createLabel(parsedLabel.one(), parsedLabel.two());
    }

    /** Returns a label with the given text and label kind. */
    public TypeLabel createLabel(LabelKind kind, String text) {
        assert text != null : "Label text of type label should not be null";
        return newLabel(text, kind);
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
     * @param text the label text being looked up
     * @return the (reused or new) label object.
     */
    private TypeLabel newLabel(String text, LabelKind kind) {
        Map<String,TypeLabel> labelMap = this.labelMaps.get(kind);
        TypeLabel result = labelMap.get(text);
        if (result == null) {
            int index = labelMap.size();
            result = new TypeLabel(text, kind, index);
            labelMap.put(text, result);
            return result;
        } else {
            return result;
        }
    }

    /**
     * The internal translation table from strings to standard (non-node type)
     * label indices.
     */
    private final Map<LabelKind,Map<String,TypeLabel>> labelMaps =
        new EnumMap<LabelKind,Map<String,TypeLabel>>(LabelKind.class);
    {
        for (LabelKind kind : EnumSet.allOf(LabelKind.class)) {
            this.labelMaps.put(kind, new HashMap<String,TypeLabel>());
        }
    }

    /** Returns the singleton instance of this class. */
    public static TypeFactory instance() {
        return INSTANCE;
    }

    /** Singleton instance of this class. */
    private static final TypeFactory INSTANCE = new TypeFactory();
}