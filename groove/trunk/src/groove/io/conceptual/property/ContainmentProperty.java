package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * TODO: This needs to be able to detect cycles, so should communicate with other containerment properties in the metamodel.
 * @author s0141844
 * 
 */
public class ContainmentProperty implements Property {
    private Class m_class;
    private Name m_fieldName;
    private Field m_field;

    public ContainmentProperty(Class c, Name field) {
        m_class = c;
        m_fieldName = field;
    }

    @Override
    public boolean doVisit(Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getContainerClass() {
        return m_class;
    }

    public Name getFieldName() {
        return m_fieldName;
    }

    public Field getField() {
        return m_field;
    }

    @Override
    public void resolveFields() {
        m_field = m_class.getFieldSuper(m_fieldName);
    }

}
