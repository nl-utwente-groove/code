package groove.io.conceptual.configuration;

import groove.grammar.model.GrammarModel;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.configuration.schema.Configuration;
import groove.io.conceptual.configuration.schema.Constraints;
import groove.io.conceptual.configuration.schema.Global;
import groove.io.conceptual.configuration.schema.Global.IdOverrides;
import groove.io.conceptual.configuration.schema.IdModeType;
import groove.io.conceptual.configuration.schema.InstanceModel;
import groove.io.conceptual.configuration.schema.Meta;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.configuration.schema.StringsType;
import groove.io.conceptual.configuration.schema.TypeModel;
import groove.io.conceptual.lang.groove.GrooveUtil;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

/**
 * Im-/export configuration.
 * Consists of am xml-bound {@link Configuration} object
 * with helper methods.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Config {
    /** Location of the XSD for configurations. */
    public static final String CONFIG_SCHEMA = "ConfigSchema.xsd";
    /** Location of the XUI for the editor configuration. */
    public static final String CONFIG_XUI = "ConfigSchema.xui";

    /** Instantiates a given named configuration. */
    public Config(GrammarModel grammar, String xml) {
        try {
            JAXBContext jaxbContext =
                JAXBContext.newInstance("groove.io.conceptual.configuration.schema");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            String xmlString = grammar.getConfigModel(xml).toConfig();
            Object obj = unmarshaller.unmarshal(new InputSource(new StringReader(xmlString)));

            this.m_xmlConfig = (Configuration) obj;
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Unable to parse configuration " + xml, e);
        }

        for (IdOverrides idOvr : getXMLGlobal().getIdOverrides()) {
            String name = idOvr.getName();
            String idStr[] = idOvr.getId().split("\\.");
            Id id = Id.ROOT;
            for (String idName : idStr) {
                id = Id.getId(id, Name.getName(idName));
            }
            this.m_mappedIds.put(id, name);
            this.m_mappedNames.put(name, id);
        }
    }

    /** Returns the wrapped XML-bound configuration object. */
    final public Configuration getXMLConfig() {
        return this.m_xmlConfig;
    }

    /** The XML-bound configuration object. */
    private final Configuration m_xmlConfig;

    /** Convenience method to return the global settings part
     * of the XML-bound configuration object. */
    final public Global getXMLGlobal() {
        return getXMLConfig().getGlobal();
    }

    /** Convenience method to returns the globally defined strings
     * from the XML-bound configuration object. */
    final public StringsType getStrings() {
        return getXMLGlobal().getStrings();
    }

    /** Convenience method to return the glossary
     * of the XML-bound configuration object. */
    final public TypeModel getTypeModel() {
        return getXMLConfig().getTypeModel();
    }

    /** Convenience method to return the meta-graph information
     * of the XML-bound configuration object. */
    final public Meta getMeta() {
        return getXMLConfig().getMeta();
    }

    /** Convenience method to return the instance model information
     * of the XML-bound configuration object. */
    final public InstanceModel getInstanceModel() {
        return getXMLConfig().getInstanceModel();
    }

    /** Convenience method to return the constraints information
     * of the XML-bound configuration object. */
    final public Constraints getConstraints() {
        return getXMLConfig().getConstraints();
    }

    /** Configuration-induced mapping from simple names to identifiers. */
    private final HashMap<String,Id> m_mappedNames = new HashMap<String,Id>();
    /** Configuration-induced mapping from identifiers to simple names. */
    private final HashMap<Id,String> m_mappedIds = new HashMap<Id,String>();

    /**
     * Sets the current glossary used to check various constraints
     */
    public void setGlossary(Glossary glos) {
        this.m_activeGlossary = glos;
    }

    /** Returns the current glossary. */
    public Glossary getGlossary() {
        return this.m_activeGlossary;
    }

    // Current TypeModel to consider for shorter Ids and opposite edges
    // and whatever else requires the type model to be known
    private Glossary m_activeGlossary;

    /**
     * Returns the list of name suffixes.
     */
    private Set<String> getSuffixSet() {
        if (this.m_suffixSet == null && !this.m_xmlConfig.getMeta().isMetaSchema()) {
            this.m_suffixSet = new HashSet<>();
            if (!getStrings().getProperPostfix().isEmpty()) {
                this.m_suffixSet.add(getStrings().getProperPostfix());
            }
            if (!getStrings().getNullablePostfix().isEmpty()) {
                this.m_suffixSet.add(getStrings().getNullablePostfix());
            }
            if (!getStrings().getEnumPostfix().isEmpty()) {
                this.m_suffixSet.add(getStrings().getEnumPostfix());
            }
            if (!getStrings().getDataPostfix().isEmpty()) {
                this.m_suffixSet.add(getStrings().getDataPostfix());
            }
            if (!getStrings().getTuplePostfix().isEmpty()) {
                this.m_suffixSet.add(getStrings().getTuplePostfix());
            }
        }
        return this.m_suffixSet;
    }

    private Set<String> m_suffixSet;

    /** Converts an identifier to a flat name, according to the rules of the configuration. */
    public String idToName(Id id) {
        String name = this.m_mappedIds.get(id);
        if (name == null) {
            id = GrooveUtil.getSafeId(id);
            IdModeType mode = getXMLGlobal().getIdMode();
            if (mode.equals(IdModeType.FLAT)) {
                name = id.getName().toString();
            } else {
                if (mode.equals(IdModeType.DISAMBIGUATE) && this.m_activeGlossary != null) {
                    id = this.m_activeGlossary.getShortId(id);
                }
                String sep = getXMLGlobal().getIdSeparator();
                // Flatten the identifier into separated strings
                name = id.getName().toString();
                Id namespace = id.getNamespace();
                while (namespace != Id.ROOT) {
                    // Override short circuits namespace id lookups
                    if (this.m_mappedIds.containsKey(namespace)) {
                        name = this.m_mappedIds.get(namespace) + sep + name;
                        break;
                    }
                    name = namespace.getName().toString() + sep + name;
                    namespace = namespace.getNamespace();
                }
            }
        }
        return name;
    }

    /** Converts a flag name into an identifier, according to the rules of this configuration. */
    public Id nameToId(String name) {
        // Remove suffix if any
        for (String suffix : getSuffixSet()) {
            if (name.endsWith(suffix)) {
                int end = name.length() - suffix.length() + 1;
                name = name.substring(0, end);
                // Remove at most one suffix
                break;
            }
        }

        //TODO: should find longest matching substring, starting from the beginning, in m_mappedNames
        //But for now, just split at the separator and check the first element
        String sep = getXMLGlobal().getIdSeparator();
        String names[] = name.split("\\Q" + sep + "\\E");

        Id result = Id.ROOT;
        if (names.length > 0 && this.m_mappedNames.containsKey(names[0])) {
            result = this.m_mappedNames.get(names[0]);
        }

        for (String part : names) {
            Name n = Name.getName(part);
            result = Id.getId(result, n);
        }
        return result;
    }

    /**
     * Tests whether intermediate nodes are required to represent the elements
     * of a given container.
     */
    public boolean useIntermediate(Container c) {
        switch (this.m_xmlConfig.getTypeModel().getFields().getIntermediates().getWhen()) {
        case ALWAYS:
            return true;
        case CONTAINER:
            return true;
        case REQUIRED:
            // The somewhat more difficult case. Some factors include:
            // If indexing is required on an intermediate, then the intermediate is required
            //(affects ORD, SEQ), but if preferValue then only if the value is shared
            // Otherwise, if non-unique, then required (BAG)
            // Otherwise, not required
            boolean useIndex = useIndex(c);
            boolean unique = c.getContainerType() == Kind.SET || c.getContainerType() == Kind.ORD;

            if (!unique) {
                return true;
            }

            //TODO: Intermediate required when:
            // -index or next names used as fields
            // value shared by the same type of container
            // same value multiple times in container

            //TODO: adding index edges on a node is not possible if a supertype already has one
            // Not possible for datatypes
            // required when index on non-unique
            // required when index on identity/keyset
            if (useIndex) {
                //TODO: for now, just say yes
                return true;
            }

            if (c.getType() instanceof Container) {
                return true;
            }
            if (c.getParent() != null) {
                return true;
            }

            // In case of opposite
            if (c.getField() != null) {
                return useIntermediate(c.getField(), false);
            }
            return false;
        }
        return false;
    }

    /**
     * Tests if intermediate nodes are required to represent the values
     * of a given field.
     */
    public boolean useIntermediate(Field f) {
        // Unfortunately enough, these checks have a dependency on the type model itself
        // so it should more or less be complete before it can be used
        return useIntermediate(f, true);
    }

    private boolean useIntermediate(Field f, boolean recursive) {
        switch (this.m_xmlConfig.getTypeModel().getFields().getIntermediates().getWhen()) {
        case ALWAYS:
            return true;
        case CONTAINER:
            if (f.getType() instanceof Container) {
                return true;
            }
            //$FALL-THROUGH$
        case REQUIRED:
            // Intermediate required when opposite is used
            // Opposite always in  && m_activeTypeModel != nulleld2
            if (this.m_xmlConfig.getTypeModel().getFields().isOpposites()
                && this.m_activeGlossary != null) {
                for (Property p : this.m_activeGlossary.getProperties()) {
                    if (p instanceof OppositeProperty) {
                        if (((OppositeProperty) p).getField1() == f) {
                            return true;
                        }
                    }
                }
            }

            if (getXMLGlobal().getNullable() == NullableType.NONE
                && (f.getType() instanceof Container)
                && ((Container) f.getType()).getType() instanceof Class) {
                if (f.getLowerBound() == 0 && f.getUpperBound() == 1) {
                    //If 0..1 container, then intermediate always required
                    // as otherwise nullable class cannot be detected
                    return true;
                }
            }

            if (recursive && f.getType() instanceof Container) {
                return useIntermediate((Container) f.getType());
            } else {
                return false;
            }
        }
        return true;
    }

    /** Indicates if an index should be used to number the elements of a given container. */
    public boolean useIndex(Container c) {
        boolean useIndex =
            this.m_xmlConfig.getTypeModel().getFields().getContainers().getOrdering().getType() != OrderType.NONE;
        useIndex &= c.getContainerType() == Kind.ORD || c.getContainerType() == Kind.SEQ;

        return useIndex;
    }

    /** Constructs the GROOVE type label to be used for a given design type. */
    public String getName(Type type) {
        boolean usePostfix = !this.m_xmlConfig.getMeta().isMetaSchema();
        String name = "type:";

        if (type instanceof Class) {
            Class cmClass = (Class) type;
            name += idToName(cmClass.getId());
            if (cmClass.isProper()) {
                if (usePostfix) {
                    name += getStrings().getProperPostfix();
                }
            } else {
                // For nullable classes, the postfix is always required (they share the same name with the proper variant)
                // So ignore usePostfix
                name += getStrings().getNullablePostfix();
            }
        } else if (type instanceof Enum) {
            name += idToName(((Enum) type).getId());
            if (usePostfix) {
                name += getStrings().getEnumPostfix();
            }
        } else if (type instanceof CustomDataType) {
            name += idToName(((CustomDataType) type).getId());
            if (usePostfix) {
                name += getStrings().getDataPostfix();
            }
        } else if (type instanceof Tuple) {
            //TODO: tuples have no ID, how to name?
            //name += ((Tuple) type).toString();
            if (this.m_activeGlossary != null) {
                name += this.m_activeGlossary.getTupleName((Tuple) type);
            } else {
                name += "tup";
            }
            if (usePostfix) {
                name += getStrings().getTuplePostfix();
            }
        } else if (type instanceof DataType) {
            name += ((DataType) type).typeString();
        } else if (type instanceof Container) {
            return null;
        }

        return name;
    }

    /** Returns the flat type name for a given field, if represented by an intermediate node. */
    public String getName(Field field) {
        String classId = idToName(field.getDefiningClass().getId());
        return "type:" + classId + this.m_xmlConfig.getGlobal().getIdSeparator() + field.getName();
    }

    /** Returns the flat type name for a given container, if represented by an intermediate node. */
    public String getContainerName(String base, Container c) {
        return base == null ? null : base + this.m_xmlConfig.getGlobal().getIdSeparator()
            + getStrings().getIntermediateName();
    }

    /** Returns the suffix to be used for container names. */
    public String getContainerPostfix(Container c) {
        String result = "";
        if (!getXMLConfig().getMeta().isMetaSchema()
            && getXMLConfig().getTypeModel().getFields().getContainers().isUseTypeName()) {
            result = "_";
            switch (c.getContainerType()) {
            case SET:
                result += getXMLConfig().getMeta().getMetaContainerSet();
                break;
            case BAG:
                result += getXMLConfig().getMeta().getMetaContainerBag();
                break;
            case ORD:
                result += getXMLConfig().getMeta().getMetaContainerOrd();
                break;
            case SEQ:
                result += getXMLConfig().getMeta().getMetaContainerSeq();
                break;
            }
        }
        return result;
    }
}
