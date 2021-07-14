package nl.utwente.groove.io.conceptual.value;

import nl.utwente.groove.io.conceptual.type.DataType;

/** Abstract superclass of data type values. */
public abstract class LiteralValue extends Value {
    /** Constructs a value for a given data type. */
    public LiteralValue(DataType type) {
        super(type);
    }
}
