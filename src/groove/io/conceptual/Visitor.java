package groove.io.conceptual;

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

/**
 * Visitor interface for visitor pattern. Visits Acceptor classes.
 * All methods accept a parameter of any type
 * @author s0141844
 */
public interface Visitor {
    /** Visits a {@link DataType} instance. */
    void visit(DataType t, java.lang.Object param);

    /** Visits a {@link Class} instance. */
    void visit(Class class1, java.lang.Object param);

    /** Visits a {@link Field} instance. */
    void visit(Field field, java.lang.Object param);

    /** Visits a {@link Container} instance. */
    void visit(Container container, java.lang.Object param);

    /** Visits an {@link Enum} instance. */
    void visit(Enum enum1, java.lang.Object param);

    /** Visits a {@link DataType} instance. */
    void visit(Tuple tuple, java.lang.Object param);

    /** Visits an {@link Object} instance. */
    void visit(Object object, java.lang.Object param);

    /** Visits a {@link RealValue} instance. */
    void visit(RealValue realval, java.lang.Object param);

    /** Visits a {@link StringValue} instance. */
    void visit(StringValue stringval, java.lang.Object param);

    /** Visits an {@link IntValue} instance. */
    void visit(IntValue intval, java.lang.Object param);

    /** Visits a {@link BoolValue} instance. */
    void visit(BoolValue boolval, java.lang.Object param);

    /** Visits an {@link EnumValue} instance. */
    void visit(EnumValue enumval, java.lang.Object param);

    /** Visits a {@link ContainerValue} instance. */
    void visit(ContainerValue containerval, java.lang.Object param);

    /** Visits a {@link TupleValue} instance. */
    void visit(TupleValue tupleval, java.lang.Object param);

    /** Visits a {@link CustomDataValue} instance. */
    void visit(CustomDataValue dataval, java.lang.Object param);

    /** Visits an {@link AbstractProperty} instance. */
    void visit(AbstractProperty abstractProperty, java.lang.Object param);

    /** Visits a {@link ContainmentProperty} instance. */
    void visit(ContainmentProperty containmentProperty, java.lang.Object param);

    /** Visits a {@link IdentityProperty} instance. */
    void visit(IdentityProperty identityProperty, java.lang.Object param);

    /** Visits a {@link KeysetProperty} instance. */
    void visit(KeysetProperty keysetProperty, java.lang.Object param);

    /** Visits a {@link OppositeProperty} instance. */
    void visit(OppositeProperty oppositeProperty, java.lang.Object param);

    /** Visits a {@link DefaultValueProperty} instance. */
    void visit(DefaultValueProperty defaultValueProperty, java.lang.Object param);
}
