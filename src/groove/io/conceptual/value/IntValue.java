package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.IntType;

public class IntValue extends LiteralValue {
    int m_value;

    public IntValue(int value) {
        super(IntType.get());
        m_value = value;
    }

    public int getValue() {
        return m_value;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(m_value);
    }
}
