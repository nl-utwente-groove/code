package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Identifiable;
import groove.io.conceptual.value.Value;

/** Abstract superclass for all primitive data types. */
public abstract class DataType extends Type implements Identifiable {
    /** Constructor setting the identifier for the type. */
    protected DataType(Id id) {
        this.m_id = id;
    }

    @Override
    public Id getId() {
        return this.m_id;
    }

    /** Method to parse a string representation into a value of this type. */
    public abstract Value valueFromString(String valueString);

    @Override
    public int hashCode() {
        return this.m_id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataType other = (DataType) obj;
        if (!this.m_id.equals(other.m_id)) {
            return false;
        }
        return true;
    }

    /** The identifier for this data type. */
    private final Id m_id;
}
