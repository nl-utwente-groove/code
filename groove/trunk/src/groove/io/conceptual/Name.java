package groove.io.conceptual;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents names in the conceptual model. Names are globally unique
 * See also Id
 * @author s0141844
 */
public class Name implements Serializable {
    private String m_name;
    // The global set of names. Names are unique within the entire application,
    // so they are safe to reuse in different models
    private static Map<String,Name> g_nameMap = new HashMap<String,Name>();

    // Create new name
    private Name(String name) {
        this.m_name = name;
    }

    /**
     * Returns the name associated with the given string
     * @param name String representation of the Name
     * @return The Name for this string
     */
    public static Name getName(String name) {
        if (name == null) {
            return null;
        }
        if (g_nameMap.containsKey(name)) {
            return g_nameMap.get(name);
        }

        Name newName = new Name(name);
        g_nameMap.put(name, newName);
        return newName;
    }

    @Override
    public String toString() {
        return this.m_name;
    }

    /**
     * Equal if o is a Name with same name string, or o is a String with same contents as toString();
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        /*if (o == null || (!(o instanceof String) && !(o instanceof Name))) {
            return false;
        }*/
        if (o instanceof String) {
            return ((String) o).equals(this.m_name);
        }
        return false;
        //return ((Name) o).m_name.equals(this.m_name);
    }
}
