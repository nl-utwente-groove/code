package groove.io.conceptual.value;

import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Enum;

public class EnumValue extends Value {
    private Name m_value;

    public EnumValue(Enum e, Name value) {
        super(e);
        m_value = value;
    }

    public Name getValue() {
        return m_value;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return m_type + ":" + m_value.toString();
    }
}
