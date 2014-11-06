package groove.io.conceptual.value;

import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.type.IntType;

import java.math.BigInteger;

/** Representation of integer values. */
public class IntValue extends LiteralValue {
    /** Constructs an integer value. */
    public IntValue(int value) {
        super(IntType.instance());
        this.m_value = value;
    }

    @Override
    public BigInteger getValue() {
        return BigInteger.valueOf(this.m_value);
    }

    @Override
    public boolean doBuild(ExportBuilder<?> v, String param) {
        v.addIntValue(this);
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(this.m_value);
    }

    private final int m_value;
}
