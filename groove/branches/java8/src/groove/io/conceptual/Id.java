package groove.io.conceptual;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Class that represents Ids. If id.equals(otherId), then id == id.
 * Ids are immutable
 * @author s0141844
 * @version $Revision $
 */
public class Id implements Serializable {
    /** Constructs a nested ID. */
    private Id(Id namespace, Name name) {
        this.m_namespace = namespace;
        this.m_name = name;

        this.m_depth = namespace.m_depth + 1;
    }

    /** Constructs the root ID. */
    private Id() {
        this.m_name = null;
        this.m_namespace = this;
        this.m_depth = 0;
    }

    private final int m_depth;
    private final Map<Name,Id> m_subIds = new HashMap<Name,Id>();

    /**
     * Returns the name of the identifier.
     * @return The name
     */
    public Name getName() {
        return this.m_name;
    }

    private final Name m_name;

    /**
     * Returns the namespace of the identifier.
     * @return The namespace
     */
    public Id getNamespace() {
        return this.m_namespace;
    }

    private final Id m_namespace;

    /**
     * Returns the part of the namespace that both Ids share
     * So if this = a.b.c.d, and other = a.b.e.f, then result = a.b
     * @param other The other Id to intersect the namespace with
     * @return The intersecting namespace
     */
    public Id getCommonPart(Id other) {
        Id curThis = this;
        Id curOther = other;

        while (curThis.m_depth > curOther.m_depth) {
            curThis = curThis.getNamespace();
        }
        while (curOther.m_depth > curThis.m_depth) {
            curOther = curOther.getNamespace();
        }

        while (curThis != curOther) {
            curThis = curThis.getNamespace();
            curOther = curOther.getNamespace();
        }

        return curThis;
    }

    /**
     * Get the Id which is this Id with prefix removed. If prefix is not an actual prefix of this Id, this Id is returned instead
     * Thus a.b.c . removePrefix(a.b) = c. x.y.z . removePrefix(a.b) = x.y.z
     * @param prefix The prefix to remove
     * @return The Id corresponding to this Id with prefix prefix removed
     */
    public Id removePrefix(Id prefix) {
        Id newId = this;
        Stack<Name> namestack = new Stack<Name>();
        // Push names onto stack that are not part of the prefix. Stack is used as Ids are technically a linked list
        while (newId.m_depth > prefix.m_depth) {
            namestack.push(newId.getName());
            newId = newId.getNamespace();
        }
        // Check if actually a prefix. If not ,abort and return this
        boolean isMatch = true;
        while (isMatch && newId.m_depth > 0) {
            isMatch = (newId.getName() == prefix.getName());
            newId = newId.getNamespace();
            prefix = prefix.getNamespace();
        }
        if (!isMatch) {
            return this;
        }

        // Build new Id from ROOT and stacked names
        newId = Id.ROOT;
        while (!namestack.isEmpty()) {
            newId = Id.getId(newId, namestack.pop());
        }

        return newId;
    }

    @Override
    public String toString() {
        if (this == ROOT) {
            return "";
        }
        return (this.m_namespace == ROOT ? "" : this.m_namespace.toString() + ".")
            + this.m_name.toString();
    }

    @Override
    public int hashCode() {
        if (this.m_hashCode == 0) {
            this.m_hashCode = toString().hashCode();
        }
        return this.m_hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Id)) {
            return false;
        }
        // Compare ROOT _instance_ (short circuit member checks)
        if (this == ROOT || o == ROOT) {
            return this == o;
        }
        Id other = (Id) o;
        return other.m_name.equals(this.m_name) && other.m_namespace.equals(this.m_namespace);
    }

    private int m_hashCode = 0;

    /** The ROOT namespace Id. All Ids have ROOT as the first namespace. */
    public static final Id ROOT = new Id();

    /**
     * Return the Id with the given name and namespace.
     * @param namespace The namespace of the resulting Id
     * @param name The name of the resulting Id
     * @return The Id namespace.name
     */
    public static Id getId(Id namespace, Name name) {
        Id result = null;
        if (namespace != null && name != null) {
            result = namespace.m_subIds.get(name);
            if (result == null) {
                result = new Id(namespace, name);
                namespace.m_subIds.put(name, result);
            }
        }
        return result;
    }

    /**
     * Get the Id represented by the given string. uses period (.) as separator
     * @param idString String to translate to Id, should be of the format "a.b.c"
     * @return The matching Id
     */
    public static Id getIdFromString(String idString) {
        String[] parts = idString.split("\\.");
        Id resultId = Id.ROOT;
        for (String part : parts) {
            resultId = Id.getId(resultId, Name.getName(part));
        }
        return resultId;
    }
}
