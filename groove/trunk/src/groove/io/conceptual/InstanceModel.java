package groove.io.conceptual;

import groove.io.conceptual.value.Object;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * represents instance models in the abstraction.
 * Contains objects with possibly assigned values
 * @author s0141844
 * @version $Revision $
 */
public class InstanceModel implements Serializable {
    private TypeModel m_tm;
    private String m_name;

    private Set<Object> m_objects = new HashSet<Object>();

    /**
     * Create new instance model, based on given type model and with the given name
     * @param tm TypeModel this InstanceModel is based on
     * @param name Name of this InstanceModel
     */
    public InstanceModel(TypeModel tm, String name) {
        m_tm = tm;
        m_name = name;
    }

    /**
     * Get the type model
     * @return The type model
     */
    public TypeModel getTypeModel() {
        return m_tm;
    }

    /**
     * Get the name
     * @return The name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Add an object to this instance model. Does nothing if Object was already added.
     * @param o The object to add.
     */
    public Object addObject(Object o) {
        if (m_objects.contains(o)) {
            return o;
        }
        m_objects.add(o);
        return o;
    }

    /**
     * Return a collection of objects in this instance model.
     * @return The collection of objects contained in this instance model.
     */
    public Collection<Object> getObjects() {
        return m_objects;
    }
}
