package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.BoolType;

public class BoolValue extends LiteralValue {
    boolean m_value;

    public BoolValue(boolean value) {
        super(BoolType.get());
        m_value = value;
    }

    public boolean getValue() {
        return m_value;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return Boolean.toString(m_value);
    }
}
