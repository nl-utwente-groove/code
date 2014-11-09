package groove.io.conceptual.value;

import groove.io.conceptual.Concept;
import groove.io.conceptual.type.Type;

/** Superclass of all values. */
public abstract class Value extends Concept {
    /** Constructs a new value for a given type. */
    public Value(Type type) {
        this.m_type = type;
        assert getKind().isValue();
        assert type == null ? getKind() == Kind.OBJECT_VAL : getKind().getType() == type.getKind();
    }

    /** Returns the (exact) conceptual type of this value. */
    public Type getType() {
        return this.m_type;
    }

    /** Returns the Java representation of this value. */
    public abstract java.lang.Object getValue();

    /** The exact type of this value. */
    private final Type m_type;
}
