package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * Property expressing that two fields are the opposite of one another.
 * Instantiate this twice for each opposite, a->b and b->a
 * TODO: not allowed for container of container type?
 * Only for relations
 * @author s0141844
 * @version $Revision $
 */
public class OppositeProperty implements Property {
    private final Class m_class1;
    private final Class m_class2;

    private final Name m_fieldName1;
    private final Name m_fieldName2;
    private Field m_field1;
    private Field m_field2;

    /** Constructs a property for two given fields. */
    public OppositeProperty(Class class1, Name field1, Class class2, Name field2) {
        this.m_class1 = class1;
        this.m_class2 = class2;

        this.m_fieldName1 = field1;
        this.m_fieldName2 = field2;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    /** Returns the first source class of the opposite fields. */
    public Class getClass1() {
        return this.m_class1;
    }

    /** Returns the second source class of the opposite fields. */
    public Class getClass2() {
        return this.m_class2;
    }

    /** Returns the first field name of the opposite fields. */
    public Name getFieldName1() {
        return this.m_fieldName1;
    }

    /** Returns the second field name of the opposite fields. */
    public Name getFieldName2() {
        return this.m_fieldName2;
    }

    /** 
     * Returns the first opposite field.
     * Only initialised after a call to {@link #resolveFields()}.
     */
    public Field getField1() {
        return this.m_field1;
    }

    /** 
     * Returns the second opposite field.
     * Only initialised after a call to {@link #resolveFields()}.
     */
    public Field getField2() {
        return this.m_field2;
    }

    @Override
    public void resolveFields() {
        this.m_field1 = this.m_class1.getFieldSuper(this.m_fieldName1);
        this.m_field2 = this.m_class2.getFieldSuper(this.m_fieldName2);
    }

}
