package groove.io.conceptual.type;

import java.util.Arrays;
import java.util.List;

public class Tuple extends Type {
    private Type[] m_types;

    public Tuple(Type... types) {
        m_types = types;
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
        for (Type t : m_types) {
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

    public List<Type> getTypes() {
        return Arrays.asList(m_types);
    }

    public void setTypes(Type... types) {
        m_types = types;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }

        Tuple t = (Tuple) o;
        if (t.m_types.length != m_types.length) {
            return false;
        }

        for (int i = 0; i < m_types.length; i++) {
            boolean eq = m_types[i].equals(t.m_types[i]);
            if (!eq) {
                return false;
            }
        }

        return true;
    }
}
