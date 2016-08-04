package groove.io.conceptual.configuration;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.schema.Configuration;
import groove.io.conceptual.configuration.schema.Global.IdOverrides;
import groove.io.conceptual.configuration.schema.IdModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.configuration.schema.StringsType;
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
import groove.util.parse.FormatException;

@SuppressWarnings("javadoc")
public class Config {
    private QualName m_xmlPath;

    private Configuration m_xmlConfig;

    private JAXBContext jaxbContext = null;
    private Unmarshaller unmarshaller = null;

    /** Location of the XSD for configurations. */
    public static final String CONFIG_SCHEMA =
        "groove/io/conceptual/configuration/ConfigSchema.xsd";

    private HashMap<Id,String> m_mappedIds = new HashMap<>();
    private HashMap<String,Id> m_mappedNames = new HashMap<>();

    private Set<String> m_suffixList = new HashSet<>();

    // Current TypeModel to consider for shorter Ids and opposite edges
    // and whatever else requires the type model to be known
    private TypeModel m_activeTypeModel;

    public Config(GrammarModel grammar, QualName xmlPath) {
        this.m_xmlPath = xmlPath;

        try {
            this.jaxbContext = JAXBContext.newInstance("groove.io.conceptual.configuration.schema");
            this.unmarshaller = this.jaxbContext.createUnmarshaller();

            String xmlString = (String) grammar.getResource(ResourceKind.CONFIG, this.m_xmlPath)
                .toResource();
            Object obj = this.unmarshaller.unmarshal(new InputSource(new StringReader(xmlString)));

            Configuration xmlCfg = (Configuration) obj;
            this.m_xmlConfig = xmlCfg;

        } catch (JAXBException e) {
            throw new RuntimeException("Unable to parse configuration " + this.m_xmlPath, e);
        } catch (FormatException e) {
            // ConfigModel doesn't throw this exception, should be safe to ignore
        }

        loadIdInfo();
    }

    /**
     * Set the current type model used to check various constraints
     * @param typeModel TypeModel to set as current type model
     */
    // This is somewhat of a hack, but otherwise the type model has to
    // be passed as an argument all the time
    public void setTypeModel(TypeModel typeModel) {
        this.m_activeTypeModel = typeModel;
    }

    public TypeModel getTypeModel() {
        return this.m_activeTypeModel;
    }

    private void loadIdInfo() {
        for (IdOverrides idOvr : this.m_xmlConfig.getGlobal()
            .getIdOverrides()) {
            String name = idOvr.getName();
            String idStr[] = idOvr.getId()
                .split("\\.");
            Id id = Id.ROOT;
            for (String idName : idStr) {
                id = Id.getId(id, Name.getName(idName));

            }
            this.m_mappedIds.put(id, name);
            this.m_mappedNames.put(name, id);
        }

        if (!this.m_xmlConfig.getTypeModel()
            .isMetaSchema()) {
            if (!this.m_xmlConfig.getGlobal()
                .getStrings()
                .getProperPostfix()
                .isEmpty()) {
                this.m_suffixList.add(this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getProperPostfix());
            }
            if (!this.m_xmlConfig.getGlobal()
                .getStrings()
                .getNullablePostfix()
                .isEmpty()) {
                this.m_suffixList.add(this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getNullablePostfix());
            }
            if (!this.m_xmlConfig.getGlobal()
                .getStrings()
                .getEnumPostfix()
                .isEmpty()) {
                this.m_suffixList.add(this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getEnumPostfix());
            }
            if (!this.m_xmlConfig.getGlobal()
                .getStrings()
                .getDataPostfix()
                .isEmpty()) {
                this.m_suffixList.add(this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getDataPostfix());
            }
            if (!this.m_xmlConfig.getGlobal()
                .getStrings()
                .getTuplePostfix()
                .isEmpty()) {
                this.m_suffixList.add(this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getTuplePostfix());
            }
        }
    }

    public Configuration getConfig() {
        return this.m_xmlConfig;
    }

    public String idToName(Id id) {
        if (this.m_mappedIds.containsKey(id)) {
            return this.m_mappedIds.get(id);
        }

        id = GrooveUtil.getSafeId(id);

        String name = id.getName()
            .toString();

        IdModeType mode = this.m_xmlConfig.getGlobal()
            .getIdMode();
        if (mode.equals(IdModeType.FLAT)) {
            return id.getName()
                .toString();
        }

        if (mode.equals(IdModeType.DISAMBIGUATE) && this.m_activeTypeModel != null) {
            id = this.m_activeTypeModel.getShortId(id);
        }

        String sep = this.m_xmlConfig.getGlobal()
            .getIdSeparator();
        while (id.getNamespace() != Id.ROOT) {
            // Override short circuits namespace id lookups
            if (this.m_mappedIds.containsKey(id.getNamespace())) {
                name = this.m_mappedIds.get(id) + sep + name;
                return name;
            }
            name = id.getNamespace()
                .getName()
                .toString() + sep + name;
            id = id.getNamespace();
        }
        return name;
    }

    public Id nameToId(String name) {
        // Remove suffix if any
        for (String suffix : this.m_suffixList) {
            if (name.endsWith(suffix)) {
                int end = name.length() - suffix.length() + 1;
                name = name.substring(0, end);
                // Remove at most one suffix
                break;
            }
        }

        //TODO: should find longest matching substring, starting from the beginning, in m_mappedNames
        //But for now, just split at the separator and check the first element
        String sep = this.m_xmlConfig.getGlobal()
            .getIdSeparator();
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

    // Unfortunately enough, these checks have a dependency on the type model itself
    // so it should more or less be complete before it can be used
    public boolean useIntermediate(Field f) {
        return useIntermediate(f, true);
    }

    public boolean useIntermediate(Field f, boolean recursive) {
        switch (this.m_xmlConfig.getTypeModel()
            .getFields()
            .getIntermediates()
            .getWhen()) {
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
            if (this.m_xmlConfig.getTypeModel()
                .getFields()
                .isOpposites() && this.m_activeTypeModel != null) {
                for (Property p : this.m_activeTypeModel.getProperties()) {
                    if (p instanceof OppositeProperty) {
                        if (((OppositeProperty) p).getField1() == f) {
                            return true;
                        }
                    }
                }
            }

            if (getConfig().getGlobal()
                .getNullable() == NullableType.NONE && (f.getType() instanceof Container)
                && ((Container) f.getType()).getType() instanceof Class) {
                if (f.getLowerBound() == 0 && f.getUpperBound() == 1) {
                    //If 0..1 container, then intermediate always required as otherwise nullable class cannot be detected
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

    public boolean useIntermediate(Container c) {
        switch (this.m_xmlConfig.getTypeModel()
            .getFields()
            .getIntermediates()
            .getWhen()) {
        case ALWAYS:
            return true;
        case CONTAINER:
            return true;
        case REQUIRED:
            // The somewhat more difficult case. Some factors include:
            // If indexing is required on an intermediate, then the intermediate is required (affects ORD, SEQ), but if preferValue then only if the value is shared
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

    public boolean useIndex(Container c) {
        boolean useIndex = this.m_xmlConfig.getTypeModel()
            .getFields()
            .getContainers()
            .getOrdering()
            .getType() != OrderType.NONE;
        useIndex &= c.getContainerType() == Kind.ORD || c.getContainerType() == Kind.SEQ;

        return useIndex;
    }

    public String getName(Type type) {
        boolean usePostfix = true;
        // When a meta schema is used, postfixes aren't needed
        if (this.m_xmlConfig.getTypeModel()
            .isMetaSchema()) {
            usePostfix = false;
        }

        String name = "type:";

        if (type instanceof Class) {
            Class cmClass = (Class) type;
            name += idToName(cmClass.getId());
            if (cmClass.isProper()) {
                if (usePostfix) {
                    name += this.m_xmlConfig.getGlobal()
                        .getStrings()
                        .getProperPostfix();
                }
            } else {
                // For nullable classes, the postfix is always required (they share the same name with the proper variant)
                // So ignore usePostfix
                name += this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getNullablePostfix();
            }
        } else if (type instanceof Enum) {
            name += idToName(((Enum) type).getId());
            if (usePostfix) {
                name += this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getEnumPostfix();
            }
        } else if (type instanceof CustomDataType) {
            name += idToName(((CustomDataType) type).getId());
            if (usePostfix) {
                name += this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getDataPostfix();
            }
        } else if (type instanceof Tuple) {
            //TODO: tuples have no ID, how to name?
            //name += ((Tuple) type).toString();
            if (this.m_activeTypeModel != null) {
                name += this.m_activeTypeModel.getTupleName((Tuple) type);
            } else {
                name += "tup";
            }
            if (usePostfix) {
                name += this.m_xmlConfig.getGlobal()
                    .getStrings()
                    .getTuplePostfix();
            }
        } else if (type instanceof DataType) {
            name += ((DataType) type).typeString();
        } else if (type instanceof Container) {
            return null;
        }

        return name;
    }

    public String getName(Field field) {
        String classId = idToName(field.getDefiningClass()
            .getId());
        return "type:" + classId + this.m_xmlConfig.getGlobal()
            .getIdSeparator() + field.getName();
    }

    public String getContainerName(String base, Container c) {
        return base + this.m_xmlConfig.getGlobal()
            .getIdSeparator() + getStrings().getIntermediateName();
    }

    public String getContainerPostfix(Container c) {
        String postfix = "";
        if (!getConfig().getTypeModel()
            .isMetaSchema()
            && getConfig().getTypeModel()
                .getFields()
                .getContainers()
                .isUseTypeName()) {
            postfix = "_";
            switch (c.getContainerType()) {
            case SET:
                postfix += getStrings().getMetaContainerSet();
                break;
            case BAG:
                postfix += getStrings().getMetaContainerBag();
                break;
            case ORD:
                postfix += getStrings().getMetaContainerOrd();
                break;
            case SEQ:
                postfix += getStrings().getMetaContainerSeq();
                break;
            }
        }
        return postfix;
    }

    public StringsType getStrings() {
        return this.m_xmlConfig.getGlobal()
            .getStrings();
    }

    public Collection<String> getSuffixes() {
        return this.m_suffixList;
    }
}
