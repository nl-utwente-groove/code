package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.RealType;

/** Representation of real values. */
public class RealValue extends LiteralValue {
    /** Constructs a value wrapping a given java double. */
    public RealValue(double value) {
        super(RealType.instance());
        this.m_value = value;
    }

    @Override
    public Double getValue() {
        return this.m_value;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return Double.toString(this.m_value);
    }

    private final double m_value;
}
