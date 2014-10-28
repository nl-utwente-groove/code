package groove.io.conceptual.value;

import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Enum;

/** Values of an enumerated type. */
public class EnumValue extends Value {
    /** Constructs a new enumerated value, for a given enumerated type. */
    public EnumValue(Enum e, Name value) {
        super(e);
        this.m_value = value;
    }

    @Override
    public Name getValue() {
        return this.m_value;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return getType() + ":" + this.m_value.toString();
    }

    private final Name m_value;
}
