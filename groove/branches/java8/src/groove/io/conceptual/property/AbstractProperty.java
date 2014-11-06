package groove.io.conceptual.property;

import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.type.Class;

/** Property expressing that a given class type is abstract. */
public class AbstractProperty implements Property {
    /** Constructs a property for a given class. */
    public AbstractProperty(Class c) {
        this.m_class = c;
    }

    @Override
    public boolean doBuild(ExportBuilder<?> v, String param) {
        v.addAbstractProp(this);
        return true;
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
