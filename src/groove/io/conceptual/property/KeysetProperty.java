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
public class KeysetProperty implements Property {

    private Class m_relClass;
    private Name m_relName;
    private Field m_relField;

    private Class m_keyClass;
    private Name[] m_keyNames;
    private Field[] m_keyFields;

    public KeysetProperty(Class relClass, Name rel, Class keyClass, Name... keyFields) {
        m_relClass = relClass;
        m_relName = rel;
        m_keyClass = keyClass;
        m_keyNames = keyFields;
    }

    @Override
    public boolean doVisit(Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getRelClass() {
        return m_relClass;
    }

    public Name getRelName() {
        return m_relName;
    }

    public Field getRelField() {
        return m_relField;
    }

    public Class getKeyClass() {
        return m_keyClass;
    }

    public Name[] getKeyNames() {
        return m_keyNames;
    }

    public Field[] getKeyFields() {
        return m_keyFields;
    }

    @Override
    public void resolveFields() {
        m_relField = m_relClass.getFieldSuper(m_relName);

        m_keyFields = new Field[m_keyNames.length];
        int i = 0;
        for (Name fieldName : m_keyNames) {
            m_keyFields[i] = m_keyClass.getFieldSuper(fieldName);
            i++;
        }
    }

}
