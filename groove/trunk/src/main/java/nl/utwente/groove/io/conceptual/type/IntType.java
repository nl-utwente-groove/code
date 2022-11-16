package nl.utwente.groove.io.conceptual.type;

import nl.utwente.groove.io.conceptual.Id;
import nl.utwente.groove.io.conceptual.Name;
import nl.utwente.groove.io.conceptual.value.IntValue;
import nl.utwente.groove.io.conceptual.value.Value;

/** Data type for integers (represented as Java ints). */
public class IntType extends DataType {
    private IntType() {
        super(Id.getId(Id.ROOT, Name.getName(NAME)));
    }

    @Override
    public String typeString() {
        return NAME;
    }

    @Override
    public boolean doVisit(nl.utwente.groove.io.conceptual.Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        Value result;
        try {
            result = new IntValue(Integer.parseInt(valueString));
        } catch (NumberFormatException e) {
            result = null;
        }
        return result;
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof IntValue);
    }

    /** Returns the singleton instance of this class. */
    public static IntType instance() {
        return instance;
    }

    /** The singleton instance of this class. */
    private static final IntType instance = new IntType();
    /** Name of this type. */
    public static final String NAME = "int";
}
