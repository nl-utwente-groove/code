package groove.sts;

import groove.algebra.SignatureKind;

/**
 * A variable in an sts.
 * 
 * @author Vincent de Bruijn
 *
 */
public class Variable {

    /**
     * The label of this variable.
     */
    protected String label;
    /**
     * The data type of this variable.
     */
    protected SignatureKind type;

    /**
     * Creates a new instance.
     * @param label The label of the new variable.
     * @param type The type of the new variable.
     */
    public Variable(String label, SignatureKind type) {
        this.label = label;
        this.type = type;
    }

    /** 
     * Gets the label of this variable.
     * @return The label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Gets the type of this variable.
     * @return The type.
     */
    public SignatureKind getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) o;
        return other.getLabel() == getLabel();
    }

    @Override
    public int hashCode() {
        return getLabel().hashCode();
    }
}
