package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * Instantiate this twice for each opposite, a->b and b->a
 * @author s0141844
 * @version $Revision $
 */
//TODO: not allowed for container of container type?
// Only for relations
public class OppositeProperty implements Property {
    private Class m_class1;
    private Class m_class2;

    private Name m_fieldName1;
    private Name m_fieldName2;
    private Field m_field1;
    private Field m_field2;

    public OppositeProperty(Class class1, Name field1, Class class2, Name field2) {
        m_class1 = class1;
        m_class2 = class2;

        m_fieldName1 = field1;
        m_fieldName2 = field2;
    }

    @Override
    public boolean doVisit(Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getClass1() {
        return m_class1;
    }

    public Class getClass2() {
        return m_class2;
    }

    public Name getFieldName1() {
        return m_fieldName1;
    }

    public Name getFieldName2() {
        return m_fieldName2;
    }

    public Field getField1() {
        return m_field1;
    }

    public Field getField2() {
        return m_field2;
    }

    @Override
    public void resolveFields() {
        m_field1 = m_class1.getFieldSuper(m_fieldName1);
        m_field2 = m_class2.getFieldSuper(m_fieldName2);
    }

}
