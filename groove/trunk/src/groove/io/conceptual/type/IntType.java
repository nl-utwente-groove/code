package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.Value;

//TODO: is singleton
public class IntType extends DataType {
    private static IntType g_intType = new IntType();

    private IntType() {
        m_id = Id.getId(Id.ROOT, Name.getName("int"));
    }

    public static IntType get() {
        return g_intType;
    }

    @Override
    public String typeString() {
        return "int";
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        int i = 0;
        try {
            i = Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
        return new IntValue(i);
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof IntValue);
    }
}
