package groove.io.conceptual.type;

import groove.grammar.model.FormatException;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.Value;
import groove.util.parse.ExprParser;

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
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        try {
            return new StringValue(ExprParser.toUnquoted(valueString,
                ExprParser.DOUBLE_QUOTE_CHAR));
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
