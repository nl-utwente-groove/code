package groove.io.conceptual.type;

import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Identifiable;
import groove.io.conceptual.Name;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This type is based on a 'Class' identifier.
 * The public constructor creates a nullable and proper version of the same class,
 * with the proper class referring to the nullable class as a supertype.
 * 
 * @author Me
 */
public class Class extends Type implements Identifiable {
    private Id m_id;

    private Map<Name,Field> m_fields = new HashMap<>();
    private List<Class> m_superClasses = new ArrayList<>();

    private boolean m_proper;

    // Each class instantiation creates a nullable and proper version
    private Class m_nullableClass;
    /** The corresponding proper class, if this is a nullable class. */
    private Class m_properClass;

    /**
     * Creates a nullable class with a given name.
     * @param name the name of the nullable class; non-{@code null}
     * @param proper the corresponding proper class; non-{@code null}
     */
    private Class(Id name, Class proper) {
        this.m_id = name;
        this.m_proper = false;
        this.m_nullableClass = this;
        this.m_properClass = proper;
    }

    /**
     * Creates a proper class with a given name.
     * @param name the name of the nullable class; non-{@code null}
     */
    public Class(Id name) {
        this.m_id = name;
        this.m_proper = true;
        this.m_properClass = this;
        this.m_nullableClass = new Class(name, this);
        //addSuperClass(nullable); //dont do this, cyclic result
        // Commented out: the type graph builder will do this on its own. Allows lazy building the nullable class
        //m_superClasses.add(m_nullableClass);
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public String typeString() {
        return "Class";
    }

    @Override
    public String toString() {
        if (this.m_proper) {
            return this.m_id.toString() + "<Proper>";
        }
        return this.m_id.toString() + "<Nullable>";
    }

    /** Adds a direct superclass to this class type. */
    public void addSuperClass(Class c) {
        if (!this.m_proper) {
            this.m_properClass.addSuperClass(c);
            return;
        }
        assert c.isProper();
        if (c == this) {
            return;
        }
        if (!this.m_superClasses.contains(c)) {
            this.m_superClasses.add(c);
        }
    }

    /** Adds a field to this class type. */
    public Field addField(Field f) {
        if (!this.m_proper) {
            return this.m_properClass.addField(f);
        }
        //if (!m_fields.values().contains(f))
        if (!this.m_fields.containsKey(f.getName())) {
            this.m_fields.put(f.getName(), f);
            f.setDefiningClass(this);
        }
        return f;
    }

    /** Returns the field with a given name, if any. */
    public Field getField(Name name) {
        if (!this.m_proper) {
            return this.m_properClass.getField(name);
        }
        if (this.m_fields.containsKey(name)) {
            return this.m_fields.get(name);
        }
        return null;
    }

    /** Returns the field with a given name from either this class or a superclass, if any. */
    public Field getFieldSuper(Name name) {
        if (!this.m_proper) {
            return this.m_properClass.getFieldSuper(name);
        }
        if (this.m_fields.containsKey(name)) {
            return this.m_fields.get(name);
        }

        for (Class c : this.m_superClasses) {
            Field f = c.getFieldSuper(name);
            if (f != null) {
                return f;
            }
        }

        return null;
    }

    /** Returns the fields of this class type. */
    public Collection<Field> getFields() {
        if (!this.m_proper) {
            return this.m_properClass.getFields();
        }
        return this.m_fields.values();
    }

    /** Returns the combined fields of this class type and all its supertypes. */
    public Collection<Field> getAllFields() {
        if (!this.m_proper) {
            return this.m_properClass.getAllFields();
        }
        Set<Field> fields = new HashSet<>(this.m_fields.values());
        for (Class sup : this.m_superClasses) {
            fields.addAll(sup.getAllFields());
        }
        return fields;
    }

    /** Returns the direct superclasses of this class type. */
    public Collection<Class> getSuperClasses() {
        if (!this.m_proper) {
            return this.m_properClass.getSuperClasses();
        } else {
            return this.m_superClasses;
        }
    }

    /** Returns the transitively closed set of superclasses of this class type. */
    public Collection<Class> getAllSuperClasses() {
        if (!this.m_proper) {
            return this.m_properClass.getAllSuperClasses();
        }
        Set<Class> superClasses = new HashSet<>(this.m_superClasses);
        superClasses.add(this);
        for (Class sup : this.m_superClasses) {
            superClasses.addAll(sup.getAllSuperClasses());
        }

        return superClasses;
    }

    @Override
    public Id getId() {
        return this.m_id;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v,
            String param) {
        v.visit(this, param);
        return true;
    }

    /** Returns the propert version of this class type. */
    public Class getProperClass() {
        return this.m_properClass;
    }

    /** Returns the nullable version of this class type. */
    public Class getNullableClass() {
        return this.m_nullableClass;
    }

    /** Indicates if this class is proper, i.e., does not allow {@link Object#NIL} as a value. */
    public boolean isProper() {
        return this.m_proper;
    }

    @Override
    public boolean acceptValue(Value v) {
        if (v == null) {
            return !this.m_proper;
        }
        if (!(v instanceof Object)) {
            return false;
        }

        Class objClass = (Class) v.getType();
        return objClass.getAllSuperClasses().contains(getProperClass());
    }
}
