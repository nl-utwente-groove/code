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
 * This type is based on a 'Class' identifier. The public constructor creates a nullable and proper version of the same class, with the proper class referring
 * to the nullable class as a supertype.
 * 
 * @author Me
 */
public class Class extends Type implements Identifiable {
    private Id m_id;

    private Map<Name,Field> m_fields = new HashMap<Name,Field>();
    private List<Class> m_superClasses = new ArrayList<Class>();

    private boolean m_proper;

    // Each class instantiation creates a nullable and proper version
    private Class m_nullableClass;
    private Class m_properClass;

    // Create nullable class
    private Class(Id name, Class proper) {
        m_id = name;
        m_proper = false;
        m_nullableClass = this;
        m_properClass = proper;
    }

    // Create proper class
    public Class(Id name) {
        m_id = name;
        m_proper = true;
        m_properClass = this;
        m_nullableClass = new Class(name, this);

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
        if (m_proper) {
            return m_id.toString() + "<Proper>";
        }
        return m_id.toString() + "<Nullable>";
    }

    public void addSuperClass(Class c) {
        if (!m_proper) {
            m_properClass.addSuperClass(c);
            return;
        }
        assert c.isProper();
        if (c == this) {
            return;
        }
        if (!m_superClasses.contains(c)) {
            m_superClasses.add(c);
        }
    }

    public Field addField(Field f) {
        if (!m_proper) {
            return m_properClass.addField(f);
        }
        //if (!m_fields.values().contains(f))
        if (!m_fields.containsKey(f.getName())) {
            m_fields.put(f.getName(), f);
            f.setDefiningClass(this);
        }
        return f;
    }

    public Field getField(Name name) {
        if (!m_proper) {
            return m_properClass.getField(name);
        }
        if (m_fields.containsKey(name)) {
            return m_fields.get(name);
        }
        return null;
    }

    //DFS search for field in superclasses
    public Field getFieldSuper(Name name) {
        if (!m_proper) {
            return m_properClass.getFieldSuper(name);
        }
        if (m_fields.containsKey(name)) {
            return m_fields.get(name);
        }

        for (Class c : m_superClasses) {
            Field f = c.getFieldSuper(name);
            if (f != null) {
                return f;
            }
        }

        return null;
    }

    public Collection<Field> getFields() {
        if (!m_proper) {
            return m_properClass.getFields();
        }
        return m_fields.values();
    }

    public Collection<Field> getAllFields() {
        if (!m_proper) {
            return m_properClass.getAllFields();
        }
        Set<Field> fields = new HashSet<Field>(m_fields.values());
        for (Class sup : m_superClasses) {
            fields.addAll(sup.getAllFields());
        }
        return fields;
    }

    public Collection<Class> getSuperClasses() {
        if (!m_proper) {
            return m_properClass.getSuperClasses();
        }
        return m_superClasses;
    }

    public Collection<Class> getAllSuperClasses() {
        if (!m_proper) {
            return m_properClass.getAllSuperClasses();
        }
        Set<Class> superClasses = new HashSet<Class>(m_superClasses);
        superClasses.add(this);
        for (Class sup : m_superClasses) {
            superClasses.addAll(sup.getAllSuperClasses());
        }

        return superClasses;
    }

    @Override
    public Id getId() {
        return m_id;
    }

    @Override
    public boolean doVisit(groove.io.conceptual.Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    public Class getProperClass() {
        return m_properClass;
    }

    public Class getNullableClass() {
        return m_nullableClass;
    }

    public boolean isProper() {
        return m_proper;
    }

    @Override
    public boolean acceptValue(Value v) {
        if (v == null) {
            return !m_proper;
        }
        if (!(v instanceof Object)) {
            return false;
        }

        Class objClass = (Class) v.getType();
        return objClass.getAllSuperClasses().contains(getProperClass());
    }
}
