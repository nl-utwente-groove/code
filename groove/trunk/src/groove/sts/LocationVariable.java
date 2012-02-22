package groove.sts;

import groove.algebra.SignatureKind;

/**
 * A location variable in an sts.
 * @author Vincent de Bruijn
 *
 */
public class LocationVariable extends Variable {

    private Object initialValue;

    /**
     * Creates a new instance.
     * @param label The label of the new variable. 
     * @param type The type of the new variable.
     * @param initialValue The initial value of the new variable.
     */
    public LocationVariable(String label, SignatureKind type,
            Object initialValue) {
        super(label, type);
        this.initialValue = initialValue;
    }

    /**
     * Gets the initial value of this variable.
     * @return The initial value.
     */
    public Object getInitialValue() {
        return this.initialValue;
    }

    /**
     * Creates a JSON formatted string based on this variable.
     * @return The JSON string.
     */
    public String toJSON() {
        return "\"" + getLabel() + "\":{\"type\":\"" + getType()
            + "\",\"init\":" + getInitialValue().toString() + "}";
    }

}
