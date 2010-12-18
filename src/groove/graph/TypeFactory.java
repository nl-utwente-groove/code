package groove.graph;

import java.util.HashMap;
import java.util.Map;

/** Factory creating type nodes and edges. */
public class TypeFactory implements ElementFactory<TypeNode,TypeLabel,TypeEdge> {
    private TypeFactory() {
        // empty
    }

    @Override
    public TypeNode createNode(int nr) {
        throw new UnsupportedOperationException();
    }

    /** Creates a label with the given kind-prefixed text. */
    public TypeLabel createLabel(String text) {
        int kind = TypeLabel.BINARY;
        if (text.startsWith(TypeLabel.getPrefix(TypeLabel.NODE_TYPE))) {
            kind = TypeLabel.NODE_TYPE;
        } else if (text.startsWith(TypeLabel.getPrefix(TypeLabel.FLAG))) {
            kind = TypeLabel.FLAG;
        }
        String actualText = text.substring(TypeLabel.getPrefix(kind).length());
        return createLabel(actualText, kind);
    }

    /** Returns a label with the given text and label kind. */
    public TypeLabel createLabel(String text, int kind) {
        assert text != null : "Label text of type label should not be null";
        return newLabel(text, kind);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, String text, TypeNode target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public TypeEdge createEdge(TypeNode source, TypeLabel label, TypeNode target) {
        return new TypeEdge(source, label, target);
    }

    /**
     * Yields the number of labels created in the course of the program.
     * @return Number of labels created
     */
    public int getLabelCount() {
        int result = 0;
        for (Map<?,?> map : this.labelMaps) {
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
    private TypeLabel newLabel(String text, int kind) {
        assert TypeLabel.isValidKind(kind);
        Map<String,TypeLabel> labelMap = this.labelMaps[kind];
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
    @SuppressWarnings("unchecked")
    private final Map<String,TypeLabel>[] labelMaps = new HashMap[3];
    {
        for (int i = 0; i < this.labelMaps.length; i++) {
            this.labelMaps[i] = new HashMap<String,TypeLabel>();
        }
    }
    /** Singleton instance of this class. */
    public static final TypeFactory INSTANCE = new TypeFactory();
}