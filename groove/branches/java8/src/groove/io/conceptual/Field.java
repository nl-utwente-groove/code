package groove.io.conceptual;

import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.Type;

/**
 * Represents Fields in the TypeModel
 * @author s0141844
 * @version $Revision $
 */
public class Field implements Acceptor {
    private Name m_name;
    private Type m_type;
    private int m_lowerBound;
    private int m_upperBound;
    private Class m_class;

    /**
     * Create a new Field. The Field is not immediately part of a class, use setDefiningClass for that.
     * If the type is a class, then nullable is used for bound 0..1, and proper for bound 1..1
     * If the upper bound >1 and the type is not a container, it will automatically be changed to a SET container.
     * @param name Name of the Field. Should be unique for a class and all its supertypes.
     * @param type Type of the field
     * @param lower Lower bound of number of field values. Should be >0 and <=upper
     * @param upper Upper bound of field values. Should be >=lower, or -1 for unbounded
     */
    public Field(Name name, Type type, int lower, int upper) {
        this.m_name = name;

        // Force class to nullable or proper depending on bounds
        if (type instanceof Class) {
            if (lower == 0 && upper == 1) {
                type = ((Class) type).getNullableClass();
            } else if (upper > 1) {
                type = ((Class) type).getProperClass();
            }
        }

        // If type not container and
        // - Upper > 1 : create container
        // - Lower == 0 and type not class : create container
        if (((upper > 1 || upper == -1) || !(type instanceof Class) && lower == 0)
            && !(type instanceof Container)) {
            // Unique and non-ordered container (fits best into GROOVE, intermediate not required)
            type = new Container(Kind.SET, type);
        }

        if (type instanceof Container) {
            assert ((Container) type).getField() == null;
            ((Container) type).setField(this);
        }

        this.m_type = type;
        this.m_lowerBound = lower;
        this.m_upperBound = upper;
    }

    /**
     * Returns the name of the field.
     * @return The name of the field
     */
    public Name getName() {
        return this.m_name;
    }

    /**
     * Returns the upper bound of the field multiplicity.
     * @return The upper bound
     */
    public int getUpperBound() {
        return this.m_upperBound;
    }

    /**
     * Returns the lower bound of the field multiplicity.
     * @return The lower bound
     */
    public int getLowerBound() {
        return this.m_lowerBound;
    }

    /**
     * Returns the type of the field.
     * @return The type
     */
    public Type getType() {
        return this.m_type;
    }

    /**
     * Set the class that defines this Field. A Field can only be part of one class.
     * @param cmClass The class to set as the defining class.
     */
    public void setDefiningClass(Class cmClass) {
        this.m_class = cmClass;
    }

    /**
     * Get the class that defines this field, see setDefiningClass
     * @return The class that defined this Field.
     */
    public Class getDefiningClass() {
        return this.m_class;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String toString() {
        return this.m_class.getId().getName() + "." + this.m_name;
    }
}
