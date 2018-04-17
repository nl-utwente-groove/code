package groove.io.conceptual.type;

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
    public boolean doVisit(groove.io.conceptual.Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.m_types);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple other = (Tuple) obj;
        if (!Arrays.equals(this.m_types, other.m_types)) {
            return false;
        }
        return true;
    }
}
