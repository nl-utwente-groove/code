package groove.io.conceptual;

import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.TupleValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Concept in a design or glossary.
 * @author Harold Bruijntjes
 */
public abstract class Concept implements Serializable {
    /** Returns the kind of concept. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind = getKind(this);

    /** Returns the kind of a certain concept. */
    public static Kind getKind(Concept c) {
        Kind result = kindMap.get(c.getClass());
        assert result != null : String.format("Concept type %s does not have corresponding kind",
            c.getClass());
        return result;
    }

    private final static Map<Class<? extends Concept>,Kind> kindMap = new HashMap<>();

    static {
        for (Kind k : Kind.values()) {
            kindMap.put(k.getClaz(), k);
        }
    }

    /** Concept kind type. */
    public static enum Kind {
        /** Kind for {@link BoolType}. */
        BOOL_TYPE(BoolType.class),
        /** Kind for {@link groove.io.conceptual.type.Class}. */
        CLASS_TYPE(groove.io.conceptual.type.Class.class),
        /** Kind for {@link Container}. */
        CONTAINER_TYPE(Container.class),
        /** Kind for {@link CustomDataType}. */
        CUSTOM_TYPE(CustomDataType.class),
        /** Kind for {@link groove.io.conceptual.type.Enum}. */
        ENUM_TYPE(groove.io.conceptual.type.Enum.class),
        /** Kind for {@link IntType}. */
        INT_TYPE(IntType.class),
        /** Kind for {@link RealType}. */
        REAL_TYPE(RealType.class),
        /** Kind for {@link StringType}. */
        STRING_TYPE(StringType.class),
        /** Kind for {@link Tuple}. */
        TUPLE_TYPE(Tuple.class),
        /** Kind for {@link BoolValue}. */
        BOOL_VAL(BoolValue.class, BOOL_TYPE),
        /** Kind for {@link ContainerValue}. */
        CONTAINER_VAL(ContainerValue.class, CONTAINER_TYPE),
        /** Kind of {@link CustomDataValue}. */
        CUSTOM_VAL(CustomDataValue.class, CUSTOM_TYPE),
        /** Kind for {@link EnumValue}. */
        ENUM_VAL(EnumValue.class, ENUM_TYPE),
        /** Kind for {@link IntValue}. */
        INT_VAL(IntValue.class, INT_TYPE),
        /** Kind for {@link groove.io.conceptual.value.Object}. */
        OBJECT_VAL(groove.io.conceptual.value.Object.class, CLASS_TYPE),
        /** Kind for {@link RealValue}. */
        REAL_VAL(RealValue.class, REAL_TYPE),
        /** Kind for {@link StringValue}. */
        STRING_VAL(StringValue.class, STRING_TYPE),
        /** Kind for {@link TupleValue}. */
        TUPLE_VAL(TupleValue.class, TUPLE_TYPE),
        /** Kind for {@link AbstractProperty}. */
        ABSTRACT_PROP(AbstractProperty.class),
        /** Kind for {@link ContainmentProperty}. */
        CONTAINMENT_PROP(ContainmentProperty.class),
        /** Kind for {@link DefaultValueProperty}. */
        DEFAULT_PROP(DefaultValueProperty.class),
        /** Kind for {@link IdentityProperty}. */
        IDENTITY_PROP(IdentityProperty.class),
        /** Kind for {@link KeysetProperty}. */
        KEYSET_PROP(KeysetProperty.class),
        /** Kind for {@link OppositeProperty}. */
        OPPOSITE_PROP(OppositeProperty.class),
        /** Kind for {@link Field}. */
        FIELD(Field.class), ;

        /** Constructs a non-value concept. */
        private Kind(Class<? extends Concept> claz) {
            this(claz, null);
        }

        /** Constructs a value concept, with a given corresponding type kind. */
        private Kind(Class<? extends Concept> claz, Kind type) {
            this.claz = claz;
            this.type = type;
        }

        /** Returns the class of which this is the kind. */
        public Class<? extends Concept> getClaz() {
            return this.claz;
        }

        private final Class<? extends Concept> claz;

        /** Returns the type corresponding to this kind, if the kind represents a value. */
        public @Nullable Kind getType() {
            return this.type;
        }

        /** The optional type of this kind, if the kind represents a value. */
        private final @Nullable Kind type;

        /** Indicates if this is a value concept. */
        public boolean isValue() {
            return getType() != null;
        }

        /** Indicates if a concept of this kind is expected to have a parameter. */
        public boolean hasParam() {
            return this == CONTAINER_TYPE || this == CONTAINER_VAL;
        }
    }
}
