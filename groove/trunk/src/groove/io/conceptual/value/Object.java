package groove.io.conceptual.value;

import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.ContainerType;

import java.util.HashMap;
import java.util.Map;

//TODO: Merge with ClassValue
/**
 * Object in the conceptual model. No two object references are equal if they are not the same underlying Java Object.
 * @author s0141844
 * 
 */
public class Object extends Value {
    public static final Object NIL = new Object(Name.getName("Nil"));
    //Actual id is this Object itself
    private Name m_name;

    private Map<Field,Value> m_fieldValues = new HashMap<Field,Value>();

    private Object(Name name) {
        super(null);
        m_name = name;
    }

    public Object(Class type, Name name) {
        super(type);
        m_name = name;

        // Init some default (empty) field values
        for (Field f : type.getFields()) {
            Value v = null;
            if (f.getType() instanceof Container) {
                v = new ContainerValue((Container) f.getType());
            } else if (f.getType() instanceof Class) {
                v = Object.NIL;
            }
            if (v != null) {
                m_fieldValues.put(f, v);
            }
        }
    }

    public void setFieldValue(Field field, Value fieldValue) {
        // SET container is often automatic, so just create container value if required
        if (field.getType() instanceof Container && ((Container) field.getType()).getContainerType() == ContainerType.SET) {
            if (!(fieldValue instanceof ContainerValue)) {
                ContainerValue cv = new ContainerValue((Container) field.getType());
                cv.addValue(fieldValue);
                fieldValue = cv;
            }
        }
        assert (field.getType().acceptValue(fieldValue));
        m_fieldValues.put(field, fieldValue);
    }

    public String getName() {
        if (m_name == null) {
            return null;
        }
        return m_name.toString();
    }

    @Override
    public String toString() {
        String result = m_name.toString() + "(" + m_type + ")" + "\n";
        for (java.util.Map.Entry<Field,Value> fieldEntry : m_fieldValues.entrySet()) {
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

    public String toShortString() {
        return m_name.toString() + "(" + m_type + ")";
    }

    @Override
    public boolean doVisit(Visitor v, java.lang.Object param) {
        v.visit(this, param);
        return true;
    }

    public Map<Field,Value> getValues() {
        return m_fieldValues;
    }
}
