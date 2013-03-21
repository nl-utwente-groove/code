package groove.abstraction.neigh.trans;

/**
 * Multiplicity variable class. Each variable has an unique pair of 
 * number and type (upper or lower bound). Variables can only be present
 * in equations with compatible types. The variables themselves have no
 * further information (i.e., they don't have a current value). This allows
 * for a static representation of the equation system.  
 */
public final class Var {

    /** Natural number that identifies this variable. */
    final int number;
    /** Upper or lower bound type. */
    final BoundType type;

    /** Basic constructor. */
    Var(int number, BoundType type) {
        this.number = number;
        this.type = type;
    }

    @Override
    public String toString() {
        String prefix = "";
        switch (this.type) {
        case LB:
            prefix = "x_";
            break;
        case UB:
            prefix = "x^";
            break;
        default:
            assert false;
        }
        return prefix + this.number;
    }

    /** Returns the number of this variable. */
    public int getNumber() {
        return this.number;
    }

    /**
     * Indicates if this is a lowerbound or upperbound variable.
     */
    public BoundType getType() {
        return this.type;
    }
}