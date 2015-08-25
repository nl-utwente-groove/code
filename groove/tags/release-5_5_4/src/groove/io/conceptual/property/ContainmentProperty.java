package groove.io.conceptual.property;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

/**
 * TODO: This needs to be able to detect cycles, so should communicate with other containment properties in the metamodel.
 * @author Harold Bruintjes
 */
public class ContainmentProperty implements Property {
    private final Class m_class;
    private final Name m_fieldName;
    private Field m_field;

    /** Property stating that a given field is a containment field. */
    public ContainmentProperty(Class c, Name field) {
        this.m_class = c;
        this.m_fieldName = field;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    /** Returns the source type of the containment. */
    public Class getContainerClass() {
        return this.m_class;
    }

    /** Returns the containment field name. */
    public Name getFieldName() {
        return this.m_fieldName;
    }

    /** Returns the containment field.
     * This is only initialised after a call to {@link #resolveFields()}.
     */
    public Field getField() {
        return this.m_field;
    }

    @Override
    public void resolveFields() {
        this.m_field = this.m_class.getFieldSuper(this.m_fieldName);
    }
}
