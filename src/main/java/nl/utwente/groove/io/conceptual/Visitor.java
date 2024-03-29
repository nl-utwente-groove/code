package nl.utwente.groove.io.conceptual;

import nl.utwente.groove.io.conceptual.property.AbstractProperty;
import nl.utwente.groove.io.conceptual.property.ContainmentProperty;
import nl.utwente.groove.io.conceptual.property.DefaultValueProperty;
import nl.utwente.groove.io.conceptual.property.IdentityProperty;
import nl.utwente.groove.io.conceptual.property.KeysetProperty;
import nl.utwente.groove.io.conceptual.property.OppositeProperty;
import nl.utwente.groove.io.conceptual.type.Class;
import nl.utwente.groove.io.conceptual.type.Container;
import nl.utwente.groove.io.conceptual.type.DataType;
import nl.utwente.groove.io.conceptual.type.Enum;
import nl.utwente.groove.io.conceptual.type.Tuple;
import nl.utwente.groove.io.conceptual.value.BoolValue;
import nl.utwente.groove.io.conceptual.value.ContainerValue;
import nl.utwente.groove.io.conceptual.value.CustomDataValue;
import nl.utwente.groove.io.conceptual.value.EnumValue;
import nl.utwente.groove.io.conceptual.value.IntValue;
import nl.utwente.groove.io.conceptual.value.Object;
import nl.utwente.groove.io.conceptual.value.RealValue;
import nl.utwente.groove.io.conceptual.value.StringValue;
import nl.utwente.groove.io.conceptual.value.TupleValue;

/**
 * Visitor interface for visitor pattern. Visits Acceptor classes.
 * All methods accept a parameter of any type
 * @author s0141844
 */
public interface Visitor {
    /** Visits a {@link DataType} instance. */
    void visit(DataType t, String param);

    /** Visits a {@link Class} instance. */
    void visit(Class class1, String param);

    /** Visits a {@link Field} instance. */
    void visit(Field field, String param);

    /** Visits a {@link Container} instance. */
    void visit(Container container, String param);

    /** Visits an {@link Enum} instance. */
    void visit(Enum enum1, String param);

    /** Visits a {@link DataType} instance. */
    void visit(Tuple tuple, String param);

    /** Visits an {@link Object} instance. */
    void visit(Object object, String param);

    /** Visits a {@link RealValue} instance. */
    void visit(RealValue realval, String param);

    /** Visits a {@link StringValue} instance. */
    void visit(StringValue stringval, String param);

    /** Visits an {@link IntValue} instance. */
    void visit(IntValue intval, String param);

    /** Visits a {@link BoolValue} instance. */
    void visit(BoolValue boolval, String param);

    /** Visits an {@link EnumValue} instance. */
    void visit(EnumValue enumval, String param);

    /** Visits a {@link ContainerValue} instance. */
    void visit(ContainerValue containerval, String param);

    /** Visits a {@link TupleValue} instance. */
    void visit(TupleValue tupleval, String param);

    /** Visits a {@link CustomDataValue} instance. */
    void visit(CustomDataValue dataval, String param);

    /** Visits an {@link AbstractProperty} instance. */
    void visit(AbstractProperty abstractProperty, String param);

    /** Visits a {@link ContainmentProperty} instance. */
    void visit(ContainmentProperty containmentProperty, String param);

    /** Visits a {@link IdentityProperty} instance. */
    void visit(IdentityProperty identityProperty, String param);

    /** Visits a {@link KeysetProperty} instance. */
    void visit(KeysetProperty keysetProperty, String param);

    /** Visits a {@link OppositeProperty} instance. */
    void visit(OppositeProperty oppositeProperty, String param);

    /** Visits a {@link DefaultValueProperty} instance. */
    void visit(DefaultValueProperty defaultValueProperty, String param);
}
