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
        this.m_tm = tm;
        this.m_name = name;
    }

    /**
     * Returns the type model.
     * @return The type model
     */
    public TypeModel getTypeModel() {
        return this.m_tm;
    }

    /**
     * Returns the name of the instance model.
     * @return The name
     */
    public String getName() {
        return this.m_name;
    }

    /**
     * Adds an object to this instance model. Does nothing if Object was already added.
     * @param o The object to add.
     */
    public Object addObject(Object o) {
        if (this.m_objects.contains(o)) {
            return o;
        }
        this.m_objects.add(o);
        return o;
    }

    /**
     * Returns a collection of objects in this instance model.
     * @return The collection of objects contained in this instance model.
     */
    public Collection<Object> getObjects() {
        return this.m_objects;
    }
}
