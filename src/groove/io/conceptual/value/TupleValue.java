package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Tuple;

import java.util.HashMap;
import java.util.Map;

/** Conceptual tuple values. */
public class TupleValue extends Value {
    private Map<Integer,Value> m_values = new HashMap<>();

    /** Constructs an initialised tuple value. */
    public TupleValue(Tuple type, Value[] values) {
        super(type);

        for (Value v : values) {
            this.m_values.put(this.m_values.size() + 1, v);
        }
    }

    /** Constructs an initially empty tuple value. */
    public TupleValue(Tuple type) {
        super(type);

        for (int i = 0; i < type.getTypes().size(); i++) {
            this.m_values.put(i + 1, null);
        }
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    /** Sets the tuple value at a given index. */
    public void setValue(int index, Value value) {
        this.m_values.put(index + 1, value);
    }

    @Override
    public Map<Integer,Value> getValue() {
        return this.m_values;
    }

    @Override
    public String toString() {
        String valueString = "<";

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

        valueString += ">";

        return valueString;
    }
}
