package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.RealType;

public class RealValue extends LiteralValue {
    double m_value;

    public RealValue(double value) {
        super(RealType.get());
        m_value = value;
    }

    public double getValue() {
        return m_value;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return Double.toString(m_value);
    }
}
