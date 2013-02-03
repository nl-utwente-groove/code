package groove.io.conceptual.value;

import groove.io.conceptual.Acceptor;
import groove.io.conceptual.type.Type;

/** Superclass of all values. */
public abstract class Value implements Acceptor {
    /** Constructs a new value for a given type. */
    public Value(Type type) {
        this.m_type = type;
    }

    /** Returns the (exact) type of this value. */
    public Type getType() {
        return this.m_type;
    }

    /** Returns the Java representation of this value. */
    public abstract java.lang.Object getValue();

    /** The exact type of this value. */
    private final Type m_type;
}
