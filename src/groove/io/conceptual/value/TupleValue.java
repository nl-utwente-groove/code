package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Tuple;

import java.util.HashMap;
import java.util.Map;

public class TupleValue extends Value {
    private Map<Integer,Value> m_values = new HashMap<Integer,Value>();

    public TupleValue(Tuple type, Value... values) {
        super(type);

        for (Value v : values) {
            m_values.put(m_values.size() + 1, v);
        }
    }

    public TupleValue(Tuple type) {
        super(type);

        for (int i = 0; i < type.getTypes().size(); i++) {
            m_values.put(i + 1, null);
        }
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    // 1 based index
    public void setValue(int index, Value value) {
        m_values.put(index + 1, value);
    }

    public Map<Integer,Value> getValues() {
        return m_values;
    }

    @Override
    public String toString() {
        String valueString = "<";

        for (int i = 0; i < m_values.size(); i++) {
            if (m_values.get(i) instanceof Object) {
                valueString += ((Object) m_values.get(i)).toShortString();
            } else {
                valueString += m_values.get(i);
            }
            if (i < m_values.size() - 1) {
                valueString += ", ";
            }
        }

        valueString += ">";

        return valueString;
    }
}
