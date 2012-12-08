package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.value.Value;

public class DefaultValueProperty implements Property {
    private Class m_class;
    private Name m_fieldName;
    private Field m_field;
    private Value m_defaultValue;

    public DefaultValueProperty(Class c, Name field, Value defValue) {
        m_class = c;
        m_fieldName = field;
        m_defaultValue = defValue;
    }

    public Field getField() {
        return m_field;
    }

    public Value getDefaultValue() {
        return m_defaultValue;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public void resolveFields() {
        m_field = m_class.getFieldSuper(m_fieldName);
        assert m_field.getUpperBound() == 1;
        assert (m_field.getType() instanceof DataType || (m_field.getType() instanceof Container && ((Container) m_field.getType()).getType() instanceof DataType));
    }

}
