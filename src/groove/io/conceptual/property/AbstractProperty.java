package groove.io.conceptual.property;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;

public class AbstractProperty implements Property {
    Class m_class;

    public AbstractProperty(Class c) {
        m_class = c;
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getAbstractClass() {
        return m_class;
    }

    @Override
    public void resolveFields() {
        // Nothing to do
    }

}
