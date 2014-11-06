package groove.io.conceptual.value;

import groove.io.conceptual.ExportBuilder;
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
    public boolean doBuild(ExportBuilder<?> v, String param) {
        v.addRealValue(this);
        return true;
    }

    @Override
    public String toString() {
        return Double.toString(this.m_value);
    }

    private final double m_value;
}
