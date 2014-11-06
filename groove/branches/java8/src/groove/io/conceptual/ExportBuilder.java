package groove.io.conceptual;

import groove.io.conceptual.lang.Export;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
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

/**
 * Functionality to build an {@link Export} object.
 * @author Harold Bruijntjes
 */
public interface ExportBuilder<E> {
    /** Builds the export object. */
    abstract public void build() throws PortException;

    /** Returns this builder's export object. */
    abstract public E getExport();

    /** Adds a {@link DataType} instance to the target export. */
    default void addDataType(DataType t) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Class} instance to the target export. */
    default void addClass(Class claz) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Field} instance to the target export. */
    default void addField(Field field) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Container} instance to the target export.
     *@param base base name for the possibly required intermediate node type
     */
    default void addContainer(Container container, String base) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link Enum} instance to the target export. */
    default void addEnum(Enum envm) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link Tuple} instance to the target export. */
    default void addTuple(Tuple tuple) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link Object} instance to the target export. */
    default public void addObject(Object object) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link RealValue} instance to the target export. */
    default void addRealValue(RealValue realval) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link StringValue} instance to the target export. */
    default void addStringValue(StringValue stringval) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link IntValue} instance to the target export. */
    default void addIntValue(IntValue intval) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link BoolValue} instance to the target export. */
    default void addBoolValue(BoolValue boolval) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link EnumValue} instance to the target export. */
    default void addEnumValue(EnumValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link ContainerValue} instance to the target export.
     * @param base base name for the possibly required intermediate node type
     */
    default void addContainerValue(ContainerValue val, String base) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link TupleValue} instance to the target export. */
    default void addTupleValue(TupleValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link CustomDataValue} instance to the target export. */
    default void addCustomDataValue(CustomDataValue val) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link AbstractProperty} instance to the target export. */
    default void addAbstractProp(AbstractProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link ContainmentProperty} instance to the target export. */
    default void addContainmentProp(ContainmentProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds an {@link IdentityProperty} instance to the target export. */
    default void addIdentityProp(IdentityProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link KeysetProperty} instance to the target export. */
    default void addKeysetProp(KeysetProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link OppositeProperty} instance to the target export. */
    default void addOppositeProp(OppositeProperty prop) {
        throw new UnsupportedOperationException();
    }

    /** Adds a {@link DefaultValueProperty} instance to the target export. */
    default void addDefaultValueProp(DefaultValueProperty prop) {
        throw new UnsupportedOperationException();
    }
}
