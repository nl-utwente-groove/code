package groove.sts;

import groove.algebra.Sort;

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
    protected Sort type;

    /**
     * Creates a new instance.
     * @param label The label of the new variable.
     * @param type The type of the new variable.
     */
    public Variable(String label, Sort type) {
        this.label = label;
        this.type = type;
    }

    /**
     * Gets the default value of a variable with type s.
     * @param s The type.
     * @return The default value.
     */
    public static Object getDefaultValue(Sort s) {
        switch (s) {
        case INT:
            return Integer.valueOf(0);
        case BOOL:
            return Boolean.valueOf(false);
        case REAL:
            return Double.valueOf(0.0);
        case STRING:
            return "";
        default:
            return null;
        }
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
    public Sort getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) o;
        return other.getLabel()
            .equals(getLabel());
    }

    @Override
    public int hashCode() {
        return getLabel().hashCode();
    }
}
