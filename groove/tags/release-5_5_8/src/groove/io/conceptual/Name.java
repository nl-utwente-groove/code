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
    private static Map<String,Name> g_nameMap = new HashMap<>();

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.m_name == null) ? 0 : this.m_name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Name)) {
            return false;
        }
        Name other = (Name) obj;
        if (this.m_name == null) {
            if (other.m_name != null) {
                return false;
            }
        } else if (!this.m_name.equals(other.m_name)) {
            return false;
        }
        return true;
    }
}
