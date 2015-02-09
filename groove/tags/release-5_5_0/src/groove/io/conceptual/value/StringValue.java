package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.StringType;

import java.util.regex.Matcher;

/** Representation of string values. */
public class StringValue extends LiteralValue {
    /** Constructs a value wrapping a given string. */
    public StringValue(java.lang.String value) {
        super(StringType.instance());
        this.m_value = value;
    }

    @Override
    public String getValue() {
        return this.m_value;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return this.m_value;
    }

    /**
     * Returns a version of the string value in which all backslash escapes have themselves
     * been escaped.
     */
    public String toEscapedString() {
        return this.m_value.replaceAll(Matcher.quoteReplacement("\\"),
            "\\\\\\\\").replaceAll(Matcher.quoteReplacement("\""), "\\\\\"");
    }

    /** The wrapped string value. */
    private final String m_value;
}
