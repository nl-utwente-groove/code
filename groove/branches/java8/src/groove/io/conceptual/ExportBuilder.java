package groove.io.conceptual;

import groove.io.conceptual.lang.Export;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.external.PortException;
import groove.util.Exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Functionality to build an {@link Export} object.
 * @param <X> type of the export object
 * @param <E> type of the export elements
 * @author Harold Bruijntjes
 */
public abstract class ExportBuilder<X,E> {
    /** Builds the export object. */
    abstract public void build() throws PortException;

    /** Returns this builder's export object. */
    abstract public X getExport();

    /**
     * Adds a given, parameterless concept to this builder.
     * Returns a previously generated target element, or generates the element as part of the call.
     */
    final public E add(Concept c) {
        return add(c, null);
    }

    /**
     * Adds a given concept to this builder, with an optional parameter.
     * Returns a previously generated target element, or generates the element as part of the call.
     */
    final public E add(Concept c, String param) {
        assert param != null || !c.getKind().hasParam();
        if (has(c)) {
            return get(c);
        }
        switch (c.getKind()) {
        case ABSTRACT_PROP:
            return addAbstractProp((AbstractProperty) c);
        case BOOL_TYPE:
            return addDataType((BoolType) c);
        case BOOL_VAL:
            return addBoolValue((BoolValue) c);
        case CLASS_TYPE:
            return addClass((Class) c);
        case CONTAINER_TYPE:
            return addContainer((Container) c, param);
        case CONTAINER_VAL:
            return addContainerValue((ContainerValue) c, param);
        case CONTAINMENT_PROP:
            return addContainmentProp((ContainmentProperty) c);
        case CUSTOM_TYPE:
            return addDataType((CustomDataType) c);
        case CUSTOM_VAL:
            return addCustomDataValue((CustomDataValue) c);
        case DEFAULT_PROP:
            return addDefaultValueProp((DefaultValueProperty) c);
        case ENUM_TYPE:
            return addEnum((Enum) c);
        case ENUM_VAL:
            return addEnumValue((EnumValue) c);
        case FIELD:
            return addField((Field) c);
        case IDENTITY_PROP:
            return addIdentityProp((IdentityProperty) c);
        case INT_TYPE:
            return addDataType((DataType) c);
        case INT_VAL:
            return addIntValue((IntValue) c);
        case KEYSET_PROP:
            return addKeysetProp((KeysetProperty) c);
        case OBJECT_VAL:
            return addObject((Object) c);
        case OPPOSITE_PROP:
            return addOppositeProp((OppositeProperty) c);
        case REAL_TYPE:
            return addDataType((DataType) c);
        case REAL_VAL:
            return addRealValue((RealValue) c);
        case STRING_TYPE:
            return addDataType((DataType) c);
        case STRING_VAL:
            return addStringValue((StringValue) c);
        case TUPLE_TYPE:
            return addTuple((Tuple) c);
        case TUPLE_VAL:
            return addTupleValue((TupleValue) c);
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Tests if a given concept has been added to the map.
     * Use this in preference to {@code get(c) != null} to allow for {@code null} values.
     */
    private boolean has(Concept c) {
        return this.map.containsKey(c);
    }

    /** Returns the generated target element for a given concept, if any. */
    private E get(Concept c) {
        return this.map.get(c);
    }

    /** Inserts a new concept with target element into the map.
     * The concept is assumed to be fresh.
     * @return if {@code false}, the mapping was already present
     */
    protected boolean put(Concept c, E e) {
        E oldE = this.map.put(c, e);
        assert oldE == null || oldE.equals(e);
        return oldE == null;
    }

    private final Map<Concept,E> map = new HashMap<>();

    /** Adds a {@link DataType} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addDataType(DataType t) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Class} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addClass(Class claz) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Field} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addField(Field field) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Container} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     * @param base base name for the possibly required intermediate node type
     */
    protected E addContainer(Container container, String base) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link Enum} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addEnum(Enum envm) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Tuple} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addTuple(Tuple tuple) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link Object} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addObject(Object object) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link RealValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addRealValue(RealValue realval) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link StringValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addStringValue(StringValue stringval) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link IntValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addIntValue(IntValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link BoolValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addBoolValue(BoolValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link EnumValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addEnumValue(EnumValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link ContainerValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     * @param base base name for the possibly required intermediate node type
     */
    protected E addContainerValue(ContainerValue val, String base) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link TupleValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addTupleValue(TupleValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link CustomDataValue} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addCustomDataValue(CustomDataValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link AbstractProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addAbstractProp(AbstractProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link ContainmentProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addContainmentProp(ContainmentProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link IdentityProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addIdentityProp(IdentityProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link KeysetProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addKeysetProp(KeysetProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link OppositeProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addOppositeProp(OppositeProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link DefaultValueProperty} instance to the target export, and returns the target element.
     * It is assumed that the concept has not been added before.
     */
    protected E addDefaultValueProp(DefaultValueProperty prop) {
        throw new UnsupportedOperationException();
    }
}
