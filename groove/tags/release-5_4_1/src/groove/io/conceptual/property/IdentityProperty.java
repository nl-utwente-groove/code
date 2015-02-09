package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * Property expressing that a set of fields together define object identity.
 * Allowed field types: Class, DataType, Container(Class|DataType)
 * @author Harold Bruintjes
 * @version $Revision $
 */
public class IdentityProperty implements Property {
    final private Class m_class;
    final private Name[] m_fieldNames;
    private Field[] m_fields;

    /** Constructs a property for a given set of fields (of a given class). */
    public IdentityProperty(Class c, Name... idFields) {
        this.m_class = c;
        this.m_fieldNames = idFields;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    /** Returns the class that has the identity property. */
    public Class getIdClass() {
        return this.m_class;
    }

    /** Returns the field names of the fields that identify the object. */
    public Name[] getNames() {
        return this.m_fieldNames;
    }

    /** 
     * Returns the fields that identify the object.
     * Only initialised after a call to {@link #resolveFields()}.
     */
    public Field[] getFields() {
        return this.m_fields;
    }

    @Override
    public void resolveFields() {
        this.m_fields = new Field[this.m_fieldNames.length];
        int i = 0;
        for (Name fieldName : this.m_fieldNames) {
            this.m_fields[i] = this.m_class.getFieldSuper(fieldName);
            i++;
        }
    }

}
