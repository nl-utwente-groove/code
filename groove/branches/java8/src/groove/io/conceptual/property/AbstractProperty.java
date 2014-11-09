package groove.io.conceptual.property;

import groove.io.conceptual.type.Class;

/** Property expressing that a given class type is abstract. */
public class AbstractProperty extends Property {
    /** Constructs a property for a given class. */
    public AbstractProperty(Class c) {
        this.m_class = c;
    }

    /** The class type that according to this property should be abstract. */
    public Class getAbstractClass() {
        return this.m_class;
    }

    @Override
    public void resolveFields() {
        // Nothing to do
    }

    private final Class m_class;
}
