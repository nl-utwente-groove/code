package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.Value;

/** Singleton class for the boolean type. */
public class BoolType extends DataType {
    private BoolType() {
        super(Id.getId(Id.ROOT, Name.getName(NAME)));
    }

    @Override
    public String typeString() {
        return NAME;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        return BoolValue.getInstance(Boolean.getBoolean(valueString));
    }

    /** Returns the singleton instance of the boolean type. */
    public static BoolType instance() {
        return instance;
    }

    private static BoolType instance = new BoolType();
    /** Name of the boolean type. */
    public final static String NAME = "bool";
}
