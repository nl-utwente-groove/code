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

    /** Creates a node with a given type label. */
    public TypeNode createNode(TypeLabel label) {
        return new TypeNode(getNextNodeNr(), label);
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
        return this.maxNodeNr;
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
    private TypeLabel newLabel(EdgeRole kind, String text) {
        Map<String,TypeLabel> labelMap;
        labelMap = this.labelMaps.get(kind);
        TypeLabel result = labelMap.get(text);
        if (result == null) {
            int index = labelMap.size();
            result = new TypeLabel(text, kind, index);
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

    private int maxNodeNr;
    /**
     * The internal translation table from strings to type labels,
     * per edge role.
     */
    private final Map<EdgeRole,Map<String,TypeLabel>> labelMaps =
        new EnumMap<EdgeRole,Map<String,TypeLabel>>(EdgeRole.class);
    {
        for (EdgeRole kind : EnumSet.allOf(EdgeRole.class)) {
            this.labelMaps.put(kind, new HashMap<String,TypeLabel>());
        }
    }

    /** The internal translation table from node type labels to created type nodes. */

    /** Returns the singleton instance of this class. */
    public static TypeFactory instance() {
        return INSTANCE;
    }

    /** Singleton instance of this class. */
    private static final TypeFactory INSTANCE = new TypeFactory();
}