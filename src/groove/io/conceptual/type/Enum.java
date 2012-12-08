package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Enum extends DataType {
    private List<Name> m_values = new ArrayList<Name>();

    public Enum(Id id, Name... values) {
        this.m_id = id;
        this.m_values.addAll(Arrays.asList(values));
    }

    public void addLiteral(Name litName) {
        this.m_values.add(litName);
    }

    @Override
    public String typeString() {
        return "Enum";
    }

    @Override
    public String toString() {
        return m_id + "<" + typeString() + ">";
    }

    @Override
    public Id getId() {
        return m_id;
    }

    public Collection<Name> getLiterals() {
        return m_values;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        Name valName = Name.getName(valueString);
        if (m_values.contains(valName)) {
            return new EnumValue(this, valName);
        } else {
            return null;
        }
    }

    /*@Override
    public boolean equals(Object o) {
        if (!(o instanceof Enum)) {
            return false;
        }

        Enum e = (Enum) o;
        return (e == this);
    }*/
}
