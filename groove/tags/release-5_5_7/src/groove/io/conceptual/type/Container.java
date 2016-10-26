package groove.io.conceptual.type;

import groove.io.conceptual.Field;

/** Container type. */
public class Container extends Type {
    /** Kind of container type. */
    public enum Kind {
        /** Unordered container with duplicates. */
        BAG,
        /** Unordered container without duplicates. */
        SET,
        /** Ordered container with duplicates. */
        SEQ,
        /** Ordered container without duplicates. */
        ORD
    }

    private Kind m_ctype;
    private Type m_contentType;
    /** Field that is set if the representation of the container uses an intermediate node. */
    private Field m_containingField;
    /** Field that is set this container type is wrapped in another container type. */
    private Container m_parent;

    /** Constructs a container of a given kind and element type. */
    public Container(Kind ctype, Type contentType) {
        this.m_ctype = ctype;

        if (contentType instanceof Class) {
            // Containers always proper type
            contentType = ((Class) contentType).getProperClass();
        }
        if (contentType instanceof Container) {
            ((Container) contentType).m_parent = this;
        }

        this.m_contentType = contentType;
        this.m_parent = null;
    }

    /** Sets the field representing this container, if an intermediate node is used. */
    public void setField(Field field) {
        this.m_containingField = field;
    }

    /** Returns the field representing this container, if an intermediate node is used. */
    public Field getField() {
        return this.m_containingField;
    }

    /** Returns the parent container in which this type is wrapped, if any. */
    public Container getParent() {
        return this.m_parent;
    }

    /** Changes the kind of container to ordered or unordered. */
    public void setOrdered(boolean ordered) {
        if (ordered) {
            if (this.m_ctype == Kind.SET) {
                this.m_ctype = Kind.ORD;
            }
            if (this.m_ctype == Kind.BAG) {
                this.m_ctype = Kind.SEQ;
            }
        } else {
            if (this.m_ctype == Kind.ORD) {
                this.m_ctype = Kind.SET;
            }
            if (this.m_ctype == Kind.SEQ) {
                this.m_ctype = Kind.BAG;
            }
        }
    }

    /** Changes the presence of duplicates in the container kind. */
    public void setUnique(boolean unique) {
        if (unique) {
            if (this.m_ctype == Kind.BAG) {
                this.m_ctype = Kind.SET;
            }
            if (this.m_ctype == Kind.SEQ) {
                this.m_ctype = Kind.ORD;
            }
        } else {
            if (this.m_ctype == Kind.SET) {
                this.m_ctype = Kind.BAG;
            }
            if (this.m_ctype == Kind.ORD) {
                this.m_ctype = Kind.SEQ;
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
        return typeString() + "<" + this.m_ctype + ">(" + (this.m_contentType.isComplex()
            ? this.m_contentType.typeString() : this.m_contentType.toString()) + ")";
    }

    /** Returns the kind of this container. */
    public Kind getContainerType() {
        return this.m_ctype;
    }

    /** Returns the type of the container elements. */
    public Type getType() {
        return this.m_contentType;
    }

    /** Indicates if this container supports duplicates. */
    public boolean isUnique() {
        return this.m_ctype == Kind.SET || this.m_ctype == Kind.ORD;
    }

    /** Indicates if this container is ordered. */
    public boolean isOrdered() {
        return this.m_ctype == Kind.SEQ || this.m_ctype == Kind.ORD;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        int prime = 31;
        result = getContainerType().hashCode();
        result = prime * result + getType().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Container)) {
            return false;
        }
        Container c = (Container) o;
        return c.getContainerType() == getContainerType() && c.getType()
            .equals(getType());
    }
}
