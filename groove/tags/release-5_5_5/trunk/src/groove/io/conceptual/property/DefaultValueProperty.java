package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.value.Value;

/** Property expressing that a given field should have a default (initial) value. */
public class DefaultValueProperty implements Property {
    private final Class m_class;
    private final Name m_fieldName;
    private Field m_field;
    private final Value m_defaultValue;

    /** Constructs a property.
     * @param c the class type of the field
     * @param field the field that should have a default value
     * @param defValue the default value
     */
    public DefaultValueProperty(Class c, Name field, Value defValue) {
        this.m_class = c;
        this.m_fieldName = field;
        this.m_defaultValue = defValue;
    }

    /**
     * Returns the field that should have a default value.
     * This is only initialised after a call to {@link #resolveFields()}.
     */
    public Field getField() {
        return this.m_field;
    }

    /** Returns the intended default value of the field. */
    public Value getDefaultValue() {
        return this.m_defaultValue;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public void resolveFields() {
        this.m_field = this.m_class.getFieldSuper(this.m_fieldName);
        assert this.m_field.getUpperBound() == 1;
        assert (this.m_field.getType() instanceof DataType || (this.m_field.getType() instanceof Container && ((Container) this.m_field.getType()).getType() instanceof DataType));
    }
}
