package groove.io.conceptual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.QualName;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;

/**
 * Class responsible for identifiables, don't create self.
 *
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class TypeModel implements Serializable {
    private QualName m_name;

    private Map<Id,Class> m_classes = new HashMap<>();
    private Map<Id,Enum> m_enums = new HashMap<>();
    private Map<Id,CustomDataType> m_datatypes = new HashMap<>();
    private List<Property> m_properties = new ArrayList<>();
    /** Map of tuple to unique name of tuple in type model. This is to help exporting. */
    private Map<Tuple,String> m_tupleNames = new HashMap<>();

    private Set<Id> m_usedIds = new HashSet<>();
    private Id m_commonPrefix;

    /** Constructs a new, named type model. */
    public TypeModel(QualName name) {
        this.m_name = name;
        this.m_commonPrefix = Id.ROOT;
    }

    /** Returns the type model name. */
    public QualName getQualName() {
        return this.m_name;
    }

    /**
     * Resolve all properties. Must be called when the TypeModel is complete and before it is used by an InstanceModel
     */
    public void resolve() {
        for (Property p : this.m_properties) {
            p.resolveFields();
        }
    }

    /**
     * True if the given Id is used by any element in this TypeModel
     * @param id Id to check
     * @return true if used, false otherwise
     */
    public boolean idInUse(Id id) {
        return this.m_usedIds.contains(id);
    }

    // Add Id to used list
    private void addUsedId(Id id) {
        if (this.m_usedIds.size() == 0) {
            this.m_commonPrefix = id;
        }
        this.m_usedIds.add(id);
        this.m_commonPrefix = this.m_commonPrefix.getCommonPart(id);
    }

    /**
     * Return Class with given Id, or null if not found
     * @param id Id of Class to find
     * @return The Class with the given Id, or null if not found
     */
    public Class getClass(Id id) {
        return getClass(id, false);
    }

    /**
     * Return Class with given Id. If not found, return null or create new instance depending on create. Returns null if conflicts with Id of other element in
     * metamodel, even if create is true.
     * @param id Id of Class to find or create
     * @param create If true, create Class instance if not found, returns null otherwise
     * @return The Class with the given Id
     */
    public Class getClass(Id id, boolean create) {
        if (this.m_classes.containsKey(id)) {
            return this.m_classes.get(id);
        }
        if (!create) {
            return null;
        }
        if (idInUse(id)) {
            return null;
        }

        addUsedId(id);
        Class newClass = new Class(id);
        this.m_classes.put(id, newClass);
        return newClass;
    }

    /**
     * Check if Id is used by a class
     * @param id id to check
     * @return true if a class exists with given Id
     */
    public boolean hasClass(Id id) {
        return this.m_classes.containsKey(id);
    }

    /**
     * Return the enum with the given Id, or null if it doesnt exist
     * @param id Id of enum to check for
     * @return Enum with given Id, or null
     */
    public Enum getEnum(Id id) {
        return getEnum(id, false);
    }

    /**
     * Return Enum with given Id. If not found, return null or create new instance depending on create. Returns null if conflicts with Id of other element in
     * metamodel, even if create is true.
     * @param id Id of Enum to find or create
     * @param create If true, create Enum instance if not found, returns null otherwise
     * @return The Enum with the given Id, or null if not found and create is false
     */
    public Enum getEnum(Id id, boolean create) {
        if (this.m_enums.containsKey(id)) {
            return this.m_enums.get(id);
        }
        if (!create) {
            return null;
        }
        if (idInUse(id)) {
            return null;
        }

        addUsedId(id);
        Enum newEnum = new Enum(id);
        this.m_enums.put(id, newEnum);
        return newEnum;
    }

    /**
     * Check if Id is used by an enum
     * @param id id to check
     * @return true if an enum exists with given Id
     */
    public boolean hasEnum(Id id) {
        return this.m_enums.containsKey(id);
    }

    /** Returns the datatype with a given identifier, or {@code null} if none such exists. */
    public CustomDataType getDatatype(Id id) {
        return getDatatype(id, false);
    }

    /**
     * Return CustomDataType with given Id. If not found, return null or create new instance depending on create. Returns null if conflicts with Id of other
     * element in metamodel, even if create is true.
     * @param id Id of CustomDataType to find or create
     * @param create If true, create CustomDataType instance if not found, returns null otherwise
     * @return The Datatype with the given Id, or null if not found
     */
    public CustomDataType getDatatype(Id id, boolean create) {
        if (this.m_datatypes.containsKey(id)) {
            return this.m_datatypes.get(id);
        }
        if (!create) {
            return null;
        }

        if (idInUse(id)) {
            return null;
        }

        addUsedId(id);
        CustomDataType newDatatype = new CustomDataType(id);
        this.m_datatypes.put(id, newDatatype);
        return newDatatype;
    }

    /**
     * Check if Id is used by a datatype
     * @param id id to check
     * @return true if a datatype exists with given Id
     */
    public boolean hasDatatype(Id id) {
        return this.m_datatypes.containsKey(id);
    }

    /**
     * Add the given proeprty to the TypeModel
     * @param p Proeprty to add. Does nothing if p has already been added.
     */
    public void addProperty(Property p) {
        this.m_properties.add(p);
    }

    /**
     * Get all the classes in the TypeModel
     * @return Classes in the TypeModel
     */
    public Collection<Class> getClasses() {
        return this.m_classes.values();
    }

    /**
     * Get all the enums in the TypeModel
     * @return Enums in the TypeModel
     */
    public Collection<Enum> getEnums() {
        return this.m_enums.values();
    }

    /**
     * Get all the datatypes in the TypeModel
     * @return Datatypes in the TypeModel
     */
    public Collection<CustomDataType> getDatatypes() {
        return this.m_datatypes.values();
    }

    /**
     * Get all the properties in the TypeModel
     * @return Properties in the TypeModel
     */
    public Collection<Property> getProperties() {
        return this.m_properties;
    }

    /**
     * Get the set of all Ids used in this TypeModel
     * @return Set of used Ids
     */
    public Set<Id> getUsedIds() {
        return this.m_usedIds;
    }

    /**
     * Returns a shortened version of id such that getShortIds().get(id) equals the result fo this function
     * @param id Id to shorten
     * @return Shortened Id
     */
    public Id getShortId(Id id) {
        return id.removePrefix(this.m_commonPrefix);
    }

    /**
     * Returns a map from each Id to a possible shorter Id, such that the map specifies an injective function
     * @return See description
     */
    public Map<Id,Id> getShortIds() {
        HashMap<Id,Id> mappedIds = new HashMap<>();

        if (this.m_usedIds.size() == 0) {
            return mappedIds;
        }

        // Go through all Ids, and remove from the prefix the trailing part that is different
        for (Id id : this.m_usedIds) {
            // Check if the prefix doesn't actually match the Id, otherwise it would be Id.ROOT which is invalid
            if (id != this.m_commonPrefix) {
                mappedIds.put(id, id.removePrefix(this.m_commonPrefix));
            }
        }

        return mappedIds;
    }

    /**
     * The the Id that is the namespace of all Ids in the TypeModel.
     * @return Commonly shared namespace
     */
    public Id getCommonPrefix() {
        return this.m_commonPrefix;
    }

    /** Returns an auxiliary name for a given tuple. */
    public String getTupleName(Tuple tup) {
        if (!this.m_tupleNames.containsKey(tup)) {
            this.m_tupleNames.put(tup, "tup" + this.m_tupleNames.size() + 1);
        }

        return this.m_tupleNames.get(tup);
    }
}
