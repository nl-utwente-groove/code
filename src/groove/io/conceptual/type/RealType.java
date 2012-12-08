package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.Value;

//TODO: is singleton
public class RealType extends DataType {
    private static RealType g_realType = new RealType();

    private RealType() {
        m_id = Id.getId(Id.ROOT, Name.getName("real"));
    }

    public static RealType get() {
        return g_realType;
    }

    @Override
    public String typeString() {
        return "real";
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        float i = 0;
        try {
            i = Float.parseFloat(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
        return new RealValue(i);
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof RealValue);
    }
}
