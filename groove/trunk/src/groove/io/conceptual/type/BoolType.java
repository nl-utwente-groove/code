package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.Value;

//TODO: is singleton
public class BoolType extends DataType {
    private static BoolType g_boolType = new BoolType();

    private BoolType() {
        m_id = Id.getId(Id.ROOT, Name.getName("bool"));
    }

    public static BoolType get() {
        return g_boolType;
    }

    @Override
    public String typeString() {
        return "bool";
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        if (valueString.equals("true")) {
            return new BoolValue(true);
        }
        return new BoolValue(false);
    }
}
