package groove.io.conceptual.value;

import groove.io.conceptual.type.IntType;

import java.math.BigInteger;

/** Representation of integer values. */
public class IntValue extends Value {
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
    public String toString() {
        return Integer.toString(this.m_value);
    }

    private final int m_value;
}
