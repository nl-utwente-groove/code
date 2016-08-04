package groove.io.conceptual.value;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;

import java.util.HashMap;
import java.util.Map;

/**
 * Object in the conceptual model.
 * No two object references are equal if they are not the same underlying Java Object.
 * @author s0141844
 * 
 */
public class Object extends Value {
    /** The name of this object. */
    private Name m_name;

    private Map<Field,Value> m_fieldValues = new HashMap<>();

    /** Constructor for the singleton {@link #NIL} object. */
    private Object(Name name) {
        super(null);
        this.m_name = name;
    }

    /** Constructs a new object, of a given type and with a given name. */
    public Object(Class type, Name name) {
        super(type);
        this.m_name = name;

        // Init some default (empty) field values
        for (Field f : type.getFields()) {
            Value v = null;
            if (f.getType() instanceof Container) {
                v = new ContainerValue((Container) f.getType());
            } else if (f.getType() instanceof Class) {
                v = Object.NIL;
            }
            if (v != null) {
                this.m_fieldValues.put(f, v);
            }
        }
    }

    /** Sets the value of a given field of this object. */
    public void setFieldValue(Field field, Value fieldValue) {
        // SET container is often automatic, so just create container value if required
        if (field.getType() instanceof Container
            && ((Container) field.getType()).getContainerType() == Kind.SET) {
            if (!(fieldValue instanceof ContainerValue)) {
                ContainerValue cv =
                    new ContainerValue((Container) field.getType());
                cv.addValue(fieldValue);
                fieldValue = cv;
            }
        }
        assert (field.getType().acceptValue(fieldValue));
        this.m_fieldValues.put(field, fieldValue);
    }

    /** Returns a string representation of the name of this object. */
    public String getName() {
        if (this.m_name == null) {
            return null;
        }
        return this.m_name.toString();
    }

    @Override
    public String toString() {
        String result = toShortString() + "\n";
        for (java.util.Map.Entry<Field,Value> fieldEntry : this.m_fieldValues.entrySet()) {
            String valString = "null";
            if (fieldEntry.getValue() instanceof Object) {
                valString = ((Object) fieldEntry.getValue()).toShortString();
            } else if (fieldEntry.getValue() != null) {
                valString = fieldEntry.getValue().toString();
            }
            result += fieldEntry.getKey() + ": " + valString + "\n";
        }
        return result;
    }

    /** Returns a short string representation of this object value. */
    public String toShortString() {
        return getName() + "(" + getType() + ")";
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Map<Field,Value> getValue() {
        return this.m_fieldValues;
    }

    /** The singleton NIL object. */
    public static final Object NIL = new Object(Name.getName("Nil"));
}
