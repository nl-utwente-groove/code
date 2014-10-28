package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * Property expressing that a given set of fields identifies the elements of
 * a container field.
 * Allowed field types: Class, DataType, Container(Class|DataType)
 * @author s0141844
 * @version $Revision $
 */
public class KeysetProperty implements Property {
    private final Class m_relClass;
    private final Name m_relName;
    private Field m_relField;

    private final Class m_keyClass;
    private final Name[] m_keyNames;
    private Field[] m_keyFields;

    /** Constructs a property for a given container and set of field names. */
    public KeysetProperty(Class relClass, Name rel, Class keyClass,
            Name... keyFields) {
        this.m_relClass = relClass;
        this.m_relName = rel;
        this.m_keyClass = keyClass;
        this.m_keyNames = keyFields;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    /** Returns the source class of the container. */
    public Class getRelClass() {
        return this.m_relClass;
    }

    /** Returns the field name of the container field. */
    public Name getRelName() {
        return this.m_relName;
    }

    /** 
     * Returns the container field.
     * Only initialised after a call to {@link #resolveFields()}.
     */
    public Field getRelField() {
        return this.m_relField;
    }

    /** Returns the contained class type. */
    public Class getKeyClass() {
        return this.m_keyClass;
    }

    /** Returns the field names constituting the key for the contained values. */
    public Name[] getKeyNames() {
        return this.m_keyNames;
    }

    /** 
     * Returns the fields constituting the key for the contained values. 
     * Only initialised after a call to {@link #resolveFields()}.
     */
    public Field[] getKeyFields() {
        return this.m_keyFields;
    }

    @Override
    public void resolveFields() {
        this.m_relField = this.m_relClass.getFieldSuper(this.m_relName);

        this.m_keyFields = new Field[this.m_keyNames.length];
        int i = 0;
        for (Name fieldName : this.m_keyNames) {
            this.m_keyFields[i] = this.m_keyClass.getFieldSuper(fieldName);
            i++;
        }
    }

}
