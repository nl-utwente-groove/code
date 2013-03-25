package groove.io.conceptual.type;

import groove.io.conceptual.Acceptor;
import groove.io.conceptual.value.Value;

/** Superclass of all types. */
public abstract class Type implements Acceptor {
    /** Indicates if this is a composite type. */
    public boolean isComplex() {
        return false;
    }

    /** Returns the string representation of this type. */
    public abstract String typeString();

    @Override
    public String toString() {
        return typeString();
    }

    /** Tests if a given value belongs to this type. */
    public boolean acceptValue(Value v) {
        return v.getType().equals(this);
    }
}
