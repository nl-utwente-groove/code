package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.Value;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/** Data type for strings. */
public class StringType extends DataType {
    private StringType() {
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
        try {
            return new StringValue(StringHandler.toUnquoted(valueString,
                StringHandler.DOUBLE_QUOTE_CHAR));
        } catch (FormatException e) {
            return null;
        }
    }

    @Override
    public boolean acceptValue(Value v) {
        return (v instanceof StringValue);
    }

    /** Returns the singleton instance of this class. */
    public static StringType instance() {
        return instance;
    }

    /** The singleton instance of this class. */
    private static final StringType instance = new StringType();
    /** Name of this type. */
    public static final String NAME = "string";
}
