package groove.io.conceptual.type;

import groove.io.conceptual.ExportBuilder;

import java.util.Arrays;
import java.util.List;

/** Tupe type representation in the conceptual model. */
public class Tuple extends Type {
    private Type[] m_types;

    /** Constructs a tuple type expecting values from a given range of types. */
    public Tuple(Type... types) {
        this.m_types = types;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public String typeString() {
        return "Tuple";
    }

    @Override
    public String toString() {
        String res = typeString() + "<";
        boolean first = true;
        for (Type t : this.m_types) {
            if (!first) {
                res += ", ";
            }
            if (t.isComplex()) {
                res += t.typeString();
            } else {
                res += t.toString();
            }
            first = false;
        }
        res += ">";
        return res;
    }

    /** Returns the sequence of types expected for this tuple type. */
    public List<Type> getTypes() {
        return Arrays.asList(this.m_types);
    }

    /** Sets the sequence of types expected for values of this tuple type. */
    public void setTypes(Type... types) {
        this.m_types = types;
    }

    @Override
    public boolean doBuild(ExportBuilder<?> v, String param) {
        v.addTuple(this);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }

        Tuple t = (Tuple) o;
        if (t.m_types.length != this.m_types.length) {
            return false;
        }

        for (int i = 0; i < this.m_types.length; i++) {
            boolean eq = this.m_types[i].equals(t.m_types[i]);
            if (!eq) {
                return false;
            }
        }

        return true;
    }
}
