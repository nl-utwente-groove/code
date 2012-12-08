package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Container;

import java.util.ArrayList;
import java.util.List;

public class ContainerValue extends Value {
    // List has ordering. Depending on the type of the container, it may be interpreted ordered or unordered
    private List<Value> m_values;

    public ContainerValue(Container type, Value... vals) {
        super(type);
        m_values = new ArrayList<Value>();
        for (Value v : vals) {
            m_values.add(v);
        }
    }

    public void addValue(Value v) {
        m_values.add(v);
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    public List<Value> getValues() {
        return m_values;
    }

    @Override
    public String toString() {
        String valueString = "[";

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

        valueString += "]";
        return valueString;
    }
}
