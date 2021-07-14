package nl.utwente.groove.io.conceptual.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.utwente.groove.io.conceptual.Id;
import nl.utwente.groove.io.conceptual.Name;
import nl.utwente.groove.io.conceptual.value.EnumValue;
import nl.utwente.groove.io.conceptual.value.Value;

/** Enumerated types. */
public class Enum extends DataType {
    private List<Name> m_values = new ArrayList<>();

    /** Constructs an initially empty enumerated type with a given identifier. */
    public Enum(Id id) {
        super(id);
    }

    /** Adds an allowed value to this enumerated type. */
    public void addLiteral(Name litName) {
        this.m_values.add(litName);
    }

    @Override
    public String typeString() {
        return "Enum";
    }

    @Override
    public String toString() {
        return getId() + "<" + typeString() + ">";
    }

    /** Returns the collection of allowed values of this enumerated type. */
    public Collection<Name> getLiterals() {
        return this.m_values;
    }

    @Override
    public boolean doVisit(nl.utwente.groove.io.conceptual.Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        Name valName = Name.getName(valueString);
        if (this.m_values.contains(valName)) {
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
