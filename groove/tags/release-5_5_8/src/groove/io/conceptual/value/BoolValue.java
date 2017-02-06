package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.BoolType;

/** Boolean values. */
public class BoolValue extends LiteralValue {
    /** Constructs a value of the boolean type. */
    private BoolValue(boolean value) {
        super(BoolType.instance());
        this.m_value = value;
    }

    @Override
    public Boolean getValue() {
        return this.m_value;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.m_value);
    }

    private final boolean m_value;

    /** 
     * Returns the fixed representation of a given boolean value.
     * @param value the value to be represented
     * @return either {@link #TRUE} or {@link #FALSE}, depending on {@code value}
     */
    public static BoolValue getInstance(boolean value) {
        return value ? TRUE : FALSE;
    }

    /** Representation of the boolean value {@code true}. */
    public static BoolValue TRUE = new BoolValue(true);
    /** Representation of the boolean value {@code false}. */
    public static BoolValue FALSE = new BoolValue(false);
}
