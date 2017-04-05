package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Container;

import java.util.ArrayList;
import java.util.List;

/** Conceptual container values: lists, sets, etc. */
public class ContainerValue extends Value {
    // List has ordering. Depending on the type of the container, it may be interpreted ordered or unordered
    private List<Value> m_values;

    /** Constructs an initially empty container. */
    public ContainerValue(Container type) {
        super(type);
        this.m_values = new ArrayList<>();
    }

    /** Adds a value to the container. */
    public void addValue(Value v) {
        this.m_values.add(v);
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public List<Value> getValue() {
        return this.m_values;
    }

    @Override
    public String toString() {
        String valueString = "[";

        for (int i = 0; i < this.m_values.size(); i++) {
            if (this.m_values.get(i) instanceof Object) {
                valueString += ((Object) this.m_values.get(i)).toShortString();
            } else {
                valueString += this.m_values.get(i);
            }
            if (i < this.m_values.size() - 1) {
                valueString += ", ";
            }
        }

        valueString += "]";
        return valueString;
    }
}
