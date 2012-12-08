package groove.io.conceptual.type;

import groove.io.conceptual.Field;

public class Container extends Type {
    public enum ContainerType {
        BAG, SET, SEQ, ORD
    }

    private ContainerType m_ctype;
    private Type m_contentType;
    // This field is kind of a hack to be able to determine if this container requires an intermediate node by its containing field
    private Field m_containingField;
    // parent container. Info is required to detect of a parent is used, as an intermediate is required then
    private Container m_parent;

    public Container(ContainerType ctype, Type contentType) {
        m_ctype = ctype;

        if (contentType instanceof Class) {
            // Containers always proper type
            contentType = ((Class) contentType).getProperClass();
        }
        if (contentType instanceof Container) {
            ((Container) contentType).m_parent = this;
        }

        m_contentType = contentType;
        m_parent = null;
    }

    public void setField(Field field) {
        m_containingField = field;
    }

    public Field getField() {
        return m_containingField;
    }

    public Container getParent() {
        return m_parent;
    }

    public void setOrdered(boolean ordered) {
        if (ordered) {
            if (m_ctype == ContainerType.SET) {
                m_ctype = ContainerType.ORD;
            }
            if (m_ctype == ContainerType.BAG) {
                m_ctype = ContainerType.SEQ;
            }
        } else {
            if (m_ctype == ContainerType.ORD) {
                m_ctype = ContainerType.SET;
            }
            if (m_ctype == ContainerType.SEQ) {
                m_ctype = ContainerType.BAG;
            }
        }
    }

    public void setUnique(boolean unique) {
        if (unique) {
            if (m_ctype == ContainerType.BAG) {
                m_ctype = ContainerType.SET;
            }
            if (m_ctype == ContainerType.SEQ) {
                m_ctype = ContainerType.ORD;
            }
        } else {
            if (m_ctype == ContainerType.SET) {
                m_ctype = ContainerType.BAG;
            }
            if (m_ctype == ContainerType.ORD) {
                m_ctype = ContainerType.SEQ;
            }
        }
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public String typeString() {
        return "Container";
    }

    @Override
    public String toString() {
        return typeString() + "<" + m_ctype + ">(" + (m_contentType.isComplex() ? m_contentType.typeString() : m_contentType.toString()) + ")";
    }

    public ContainerType getContainerType() {
        return m_ctype;
    }

    public void setContainerType(ContainerType ctype) {
        m_ctype = ctype;
    }

    public Type getType() {
        return m_contentType;
    }

    public void setType(Type contentType) {
        m_contentType = contentType;
    }

    public boolean isUnique() {
        return m_ctype == ContainerType.SET || m_ctype == ContainerType.ORD;
    }

    public boolean isOrdered() {
        return m_ctype == ContainerType.SEQ || m_ctype == ContainerType.ORD;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, Object param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Container)) {
            return false;
        }
        Container c = (Container) o;
        return c.getContainerType() == getContainerType() && c.getType().equals(getType());
    }
}
