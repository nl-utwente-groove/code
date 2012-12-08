package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * Allowed field types: Class, DataType, Container(Class|DataType)
 * @author s0141844
 * @version $Revision $
 */
public class IdentityProperty implements Property {
    private Class m_class;
    private Name[] m_fieldNames;
    private Field[] m_fields;

    public IdentityProperty(Class c, Name... idFields) {
        m_class = c;
        m_fieldNames = idFields;
    }

    @Override
    public boolean doVisit(Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getIdClass() {
        return m_class;
    }

    public Name[] getNames() {
        return m_fieldNames;
    }

    public Field[] getFields() {
        return m_fields;
    }

    @Override
    public void resolveFields() {
        m_fields = new Field[m_fieldNames.length];
        int i = 0;
        for (Name fieldName : m_fieldNames) {
            m_fields[i] = m_class.getFieldSuper(fieldName);
            i++;
        }
    }

}
