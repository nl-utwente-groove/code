package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.StringType;

import java.util.regex.Matcher;

public class StringValue extends LiteralValue {
    String m_value;

    public StringValue(java.lang.String value) {
        super(StringType.get());
        m_value = value;
    }

    public String getValue() {
        return m_value;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return m_value;
    }

    public String toEscapedString() {
        return m_value.replaceAll(Matcher.quoteReplacement("\\"), "\\\\\\\\").replaceAll(Matcher.quoteReplacement("\""), "\\\\\"");
    }
}
