package groove.io.conceptual.type;

import groove.io.conceptual.Acceptor;
import groove.io.conceptual.value.Value;

public abstract class Type implements Acceptor {
    public boolean isComplex() {
        return false;
    }

    public abstract String typeString();

    @Override
    public String toString() {
        return typeString();
    }

    public boolean acceptValue(Value v) {
        return v.getType().equals(this);
    }
}
