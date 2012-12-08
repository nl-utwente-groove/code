package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.Value;

//TODO: is singleton
public class StringType extends DataType {
    private static StringType g_stringType = new StringType();

    private StringType() {
        m_id = Id.getId(Id.ROOT, Name.getName("string"));
    }

    public static StringType get() {
        return g_stringType;
    }

    @Override
    public String typeString() {
        return "string";
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        return new StringValue(valueString);
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof StringValue);
    }
}
