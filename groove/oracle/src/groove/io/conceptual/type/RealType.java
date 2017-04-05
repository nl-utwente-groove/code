package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.Value;

/** Data type for reals (represented as Java doubles). */
public class RealType extends DataType {
    private RealType() {
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
        double i = 0;
        try {
            i = Double.parseDouble(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
        return new RealValue(i);
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof RealValue);
    }

    /** Returns the singleton instance of this type. */
    public static RealType instance() {
        return instance;
    }

    private static RealType instance = new RealType();
    /** Name of this type. */
    public static final String NAME = "real";
}
