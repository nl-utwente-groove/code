package groove.io.conceptual.lang.groove;

import groove.grammar.QualName;
import groove.graph.GraphRole;
import groove.io.conceptual.Concept;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Name;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.GlossaryExportBuilder;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.util.Exceptions;

//separate different graphs for various elements where applicable.
/** Bridge from glossary constraints to GROOVE rules. */
public class ConstraintToGroove extends GlossaryExportBuilder<GrooveExport,AbsNode> {
    private static final String CONSTRAINT_NS = "constraint";

    /** Constructs an instance for a given glossary and GROOVE export. */
    public ConstraintToGroove(Glossary glos, GrooveExport export) {
        super(glos, export);
        this.m_cfg = export.getConfig();
    }

    private final Config m_cfg;

    @Override
    public void build() {
        this.m_cfg.setGlossary(getGlossary());
        for (Property p : getGlossary().getProperties()) {
            add(p);
        }

        // Run through fields and create constraints
        for (Class cmClass : getGlossary().getClasses()) {
            for (Field f : cmClass.getFields()) {
                createConstraints(f);
            }
        }
        // Run through enums and create constraints
        for (Enum cmEnum : getGlossary().getEnums()) {
            createConstraints(cmEnum);
        }
    }

    @Override
    protected AbsNode addAbstractProp(AbstractProperty prop) {
        // Part of type graph
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addContainmentProp(ContainmentProperty prop) {
        // Part of type graph
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addIdentityProp(IdentityProperty prop) {
        if (this.m_cfg.getConstraints().getChecks().isCheckIdentifier()) {
            setRuleGraph("Identity_" + prop.getIdClass().getId().getName(), CONSTRAINT_NS, false);
            equivalencyCheck(prop.getIdClass(), prop.getFields(), null);
        }
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addKeysetProp(KeysetProperty prop) {
        if (this.m_cfg.getConstraints().getChecks().isCheckKeyset()) {
            setRuleGraph("Keyset_" + prop.getRelField().getName(), CONSTRAINT_NS, false);
            equivalencyCheck(prop.getKeyClass(), prop.getKeyFields(), prop.getRelField());
        }
        put(prop, null);
        return null;
    }

    // Multiplicities are ignored. If upper = 1, also handled by cases for upper > 1
    // If upper == 1, treat as unordered & unique
    private void equivalencyCheck(Class fieldsClass, Field[] fields, Field containfield) {
        String valueName = this.m_cfg.getStrings().getValueEdge();
        String indexName = this.m_cfg.getStrings().getIndexEdge();
        String nextName = this.m_cfg.getStrings().getNextEdge();

        int curQuant = 1;

        AbsNode class1Node = addNode(fieldsClass);
        AbsNode class2Node = addNode(fieldsClass);
        addEdge(class1Node, class2Node, "!=");

        if (containfield != null) {
            AbsNode keyNode = addNode(containfield.getDefiningClass());
            AbsNode contain1Node = class1Node;
            AbsNode contain2Node = class2Node;
            if (this.m_cfg.useIntermediate(containfield)) {
                contain1Node = addNode(containfield);
                contain2Node = addNode(containfield);
                addEdge(contain1Node, class1Node, valueName);
                addEdge(contain2Node, class2Node, valueName);
            }
            addEdge(keyNode, contain1Node, containfield.getName().toString());
            addEdge(keyNode, contain2Node, containfield.getName().toString());
        }

        for (Field field : fields) {
            String fieldName = field.getName().toString();

            // Count field
            AbsNode field1CountNode = addNode(field);
            AbsNode field2CountNode = addNode(field);
            addEdge(class1Node, field1CountNode, fieldName);
            addEdge(class2Node, field2CountNode, fieldName);

            AbsNode field1CountForall = addNode("forall:");
            AbsNode field2CountForall = addNode("forall:");
            addEdge(field1CountNode, field1CountForall, "@");
            addEdge(field2CountNode, field2CountForall, "@");

            AbsNode fieldCount = addNode("int:");
            addEdge(field1CountForall, fieldCount, "count");
            addEdge(field2CountForall, fieldCount, "count");

            // Check if intermediate is used
            boolean useIntermediate = this.m_cfg.useIntermediate(field);
            // Check if unique and ordered. If not container, assume unique and unordered
            boolean isUnique = true;
            boolean isOrdered = false;
            if (field.getType() instanceof Container) {
                Container c = (Container) field.getType();
                isUnique = c.isUnique();
                isOrdered = c.isOrdered();
            }

            if (!useIntermediate) {
                // This is the simplest case. Only unordered, unique values (otherwise, intermediate would be required)
                // Check that for all field nodes of class1, there exists a similar value for class2, by checking the existence of the field edge
                AbsNode fieldValueNode = addNode(field);
                addEdge(class1Node, fieldValueNode, fieldName);
                addEdge(class2Node, fieldValueNode, "use=qq" + curQuant + ":" + fieldName);

                AbsNode forallNode = addNode("forall:");
                addEdge(fieldValueNode, forallNode, "@");
                AbsNode existsNode = addNode("exists=qq" + curQuant + ":");
                addEdge(existsNode, forallNode, "in");
            } else {
                // Create field nodes with equal value
                AbsNode field1InterNode = addNode(field);
                AbsNode field2InterNode = addNode(field);
                AbsNode fieldValueNode =
                    addNode(field.getType() instanceof Container
                        ? ((Container) field.getType()).getType() : field.getType(),
                        this.m_cfg.getName(field));
                addEdge(class1Node, field1InterNode, fieldName);
                addEdge(class2Node, field2InterNode, fieldName);
                addEdge(field1InterNode, fieldValueNode, valueName);
                addEdge(field2InterNode, fieldValueNode, valueName);

                // Now there are 4 cases (uniqueness doesn't matter for ordered containers):
                // * ordered with next edges
                // * ordered with index value
                // * unordered not unique
                // * unordered unique
                if (isOrdered) {
                    boolean indexValue =
                        (this.m_cfg.getTypeModel()
                            .getFields()
                            .getContainers()
                            .getOrdering()
                            .getType() == OrderType.INDEX);
                    if (indexValue) {
                        //check if all index values are equal for all equal values
                        // Create index node
                        AbsNode indexNode = addNode("int:");
                        addEdge(field1InterNode, indexNode, "index");
                        addEdge(field2InterNode, indexNode, "use=qq" + curQuant + ":" + indexName);

                        // Create quantifier system
                        AbsNode forallNode = addNode("forall:");
                        addEdge(fieldValueNode, forallNode, "@");
                        addEdge(field1InterNode, forallNode, "@");
                        addEdge(indexNode, forallNode, "@");

                        AbsNode existsNode = addNode("exists=qq" + curQuant + ":");
                        addEdge(field2InterNode, existsNode, "@");
                        addEdge(existsNode, forallNode, "in");
                    } else {
                        //check if the value if the intermediate at the next edge is the same of that of the other intermediate's next
                        // Create next intermediates
                        AbsNode field1Inter2Node = addNode(field);
                        AbsNode field2Inter2Node = addNode(field);
                        //AbsNode fieldValue2Node = getElement(field.getType());
                        AbsNode fieldValue2Node =
                            addNode(field.getType() instanceof Container
                                ? ((Container) field.getType()).getType() : field.getType(),
                                this.m_cfg.getName(field));
                        addEdge(field1Inter2Node, fieldValue2Node, valueName);
                        addEdge(field2Inter2Node, fieldValue2Node, valueName);
                        // Next edge
                        addEdge(field1InterNode, field1Inter2Node, nextName);
                        addEdge(field2InterNode, field2Inter2Node, nextName);

                        // Create quantifier system
                        AbsNode forallNode = addNode("forall:");
                        addEdge(fieldValueNode, forallNode, "@");
                        addEdge(field1InterNode, forallNode, "@");
                        AbsNode existsNode = addNode("exists:");
                        addEdge(field2InterNode, existsNode, "@");
                        addEdge(existsNode, forallNode, "in");
                        // Quantifier system for next value
                        AbsNode forall2Node = addNode("forall:");
                        addEdge(fieldValue2Node, forall2Node, "@");
                        addEdge(field1Inter2Node, forall2Node, "@");
                        AbsNode exists2Node = addNode("exists:");
                        addEdge(field2Inter2Node, exists2Node, "@");
                        addEdge(exists2Node, forall2Node, "in");
                        // Connect both quantifer systems
                        addEdge(forall2Node, existsNode, "in");
                    }
                } else {
                    if (isUnique) {
                        //unordered-unique: Simply check if for all intermediate values, the 2nd instance has such an intermediate as well
                        AbsNode forallNode = addNode("forall:");
                        addEdge(fieldValueNode, forallNode, "@");
                        addEdge(field1InterNode, forallNode, "@");
                        AbsNode existsNode = addNode("exists:");
                        addEdge(field2InterNode, existsNode, "@");
                        addEdge(existsNode, forallNode, "in");
                    } else {
                        //unordered-not unique: bijection test
                        // Create additional intermediate node that forces all different values to be checked
                        AbsNode field1Inter2Node = addNode(field);
                        addEdge(class1Node, field1Inter2Node, fieldName);
                        addEdge(field1Inter2Node, fieldValueNode, valueName);

                        // Create quantifier system
                        AbsNode countNode = addNode("int:");
                        AbsNode forallInter1Node = addNode("forall:");
                        AbsNode forallInter2Node = addNode("forall:");
                        addEdge(forallInter1Node, countNode, "count");
                        addEdge(forallInter2Node, countNode, "count");
                        addEdge(field1InterNode, forallInter1Node, "@");
                        addEdge(field2InterNode, forallInter2Node, "@");
                        AbsNode existsCountNode = addNode("exists:");
                        addEdge(forallInter1Node, existsCountNode, "in");
                        addEdge(forallInter2Node, existsCountNode, "in");
                        addEdge(countNode, existsCountNode, "@");

                        AbsNode forallValuesNode = addNode("forall:");
                        addEdge(field1Inter2Node, forallValuesNode, "@");
                        addEdge(fieldValueNode, forallValuesNode, "@");
                        addEdge(existsCountNode, forallValuesNode, "in");
                    }
                }
            }

            curQuant++;
        }
    }

    @Override
    // Called twice, opposite has a reverse
    // SO only handle a single direction
    protected AbsNode addOppositeProp(OppositeProperty prop) {
        if (this.m_cfg.getConstraints().getChecks().isCheckOpposite()) {
            String valueName = this.m_cfg.getStrings().getValueEdge();

            setRuleGraph("Opposite_" + prop.getField1().getName(), CONSTRAINT_NS, false);

            AbsNode class1Node = addNode(prop.getClass1());
            AbsNode class2Node = addNode(prop.getClass2());

            AbsNode field1Node = class2Node;
            if (this.m_cfg.useIntermediate(prop.getField1())) {
                field1Node = addNode(prop.getField1());
                addEdge(field1Node, class2Node, valueName);
            }
            AbsNode field2Node = class1Node;
            if (this.m_cfg.useIntermediate(prop.getField2())) {
                field2Node = addNode(prop.getField2());
                // NAC intermediate node
                field2Node.addName("not:");
                addEdge(field2Node, class1Node, valueName);
            }

            addEdge(class1Node, field1Node, prop.getField1().getName().toString());

            if (this.m_cfg.useIntermediate(prop.getField2())) {
                addEdge(class2Node, field2Node, prop.getField2().getName().toString());
            } else {
                // NAC value edge
                addEdge(class2Node, class1Node, "not:" + prop.getField2().getName().toString());
            }
        }
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addDefaultValueProp(DefaultValueProperty prop) {
        if (this.m_cfg.getXMLConfig().getConstraints().getRules().isSetDefault()) {
            if (prop.getField().getType() instanceof Container
                && prop.getField().getUpperBound() > 1) {
                // Cannot support containers, because hard to determine if container is empty, or not there
                throw new RuntimeException("Container default value not allowed");
            }
            if (prop.getField().getType() instanceof Class) {
                // Cannot support reference types
                throw new RuntimeException("Reference default value not allowed");
            }

            setRuleGraph("Default_" + prop.getField().getName(), null, true);

            AbsNode classNode = addNode(prop.getField().getDefiningClass());
            //AbsNode fieldNode = getElement(defaultValueProperty.getField()); //this would be the actual value, or intermediate node
            AbsNode valueNode = addNode(prop.getDefaultValue());
            // If custom datatype, create the value node. Otherwise, just use "new" edge.
            if (prop.getField().getType() instanceof CustomDataType) {
                valueNode.addName("new:");
            }

            if (this.m_cfg.useIntermediate(prop.getField())) {
                AbsNode interNotNode = addNode("not:", this.m_cfg.getName(prop.getField()));
                AbsNode interNode = addNode("new:", this.m_cfg.getName(prop.getField()));
                String valName = this.m_cfg.getStrings().getValueEdge();

                addEdge(classNode, interNode, "new:" + prop.getField().getName().toString());
                addEdge(classNode, interNotNode, prop.getField().getName().toString());

                addEdge(interNode, valueNode, valName);
            } else {
                addEdge(classNode, valueNode, "new:" + prop.getField().getName().toString());

                AbsNode notNode = addNode(prop.getField().getType());
                notNode.addName("not:"); //Make NAC
                /*AbsEdge notEdge = */addEdge(classNode, notNode, prop.getField()
                    .getName()
                    .toString());

            }
        }
        put(prop, null);
        return null;
    }

    private AbsNode createRealValue(RealValue val) {
        return addNode("real:" + val.getValue());
    }

    private AbsNode createStringValue(StringValue val) {
        return addNode("string:\"" + val.toEscapedString() + "\"");
    }

    private AbsNode createIntValue(IntValue val) {
        return addNode("int:" + val.getValue());
    }

    private AbsNode createBoolValue(BoolValue val) {
        return addNode("bool:" + val.getValue());
    }

    private AbsNode createEnumValue(EnumValue val) {
        AbsNode result;
        if (this.m_cfg.getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getXMLConfig().getGlobal().getIdSeparator();
            String litName =
                "type:" + this.m_cfg.idToName(((Enum) val.getType()).getId()) + sep
                    + val.getValue();
            result = addNode(litName);
        } else {
            result = addNode(val.getType());
            result.addName("flag:" + val.getValue().toString());
        }
        return result;
    }

    private AbsNode createCustomValue(CustomDataValue val) {
        String valueName = this.m_cfg.getStrings().getDataValue();
        return addNode(this.m_cfg.getName(val.getType()),
            "let:" + valueName + "=string:\"" + val.getValue() + "\"");
    }

    // Does not support recursive containers
    private void createConstraints(Field field) {
        // Uniqueness: Create field->value type path times two, this will match same value used twice in case of intermediate
        // Class not needed, intermediate node is unique within type graph
        // If no intermediate, no problem
        Container fieldType =
            field.getType() instanceof Container ? (Container) field.getType() : null;
        Container.Kind containerKind = fieldType == null ? null : fieldType.getContainerType();
        if (this.m_cfg.getConstraints().getChecks().isCheckUniqueness()
            && this.m_cfg.useIntermediate(field)
            && (containerKind == Kind.SET || containerKind == Kind.ORD)) {
            assert fieldType != null : "Guaranteed by containerKind != null";
            setRuleGraph("Unique_" + field.getName(), CONSTRAINT_NS, false);

            AbsNode classNode = addNode(field.getDefiningClass());
            AbsNode interNode = addNode(field);
            AbsNode interNode2 = addNode(field);
            AbsNode typeNode = null;
            if (fieldType.getType() instanceof Container) {
                typeNode =
                    add(fieldType.getType(),
                        this.m_cfg.getContainerName(this.m_cfg.getName(field), fieldType));
            } else {
                typeNode = addNode(fieldType.getType());
            }

            String valName = this.m_cfg.getStrings().getValueEdge();
            addEdge(classNode, interNode, field.getName().toString());
            addEdge(classNode, interNode2, field.getName().toString());
            addEdge(interNode, typeNode, valName);
            addEdge(interNode2, typeNode, valName);
            addEdge(interNode, interNode2, "!=");
        }

        // Ordering, check if indices are well ordered, or next edges don't have two heads (rest is checked by multiplicities)
        if (this.m_cfg.getConstraints().getChecks().isCheckOrdering()
            && (containerKind == Kind.ORD || containerKind == Kind.SEQ)) {
            setRuleGraph("Ordered_" + field.getName(), CONSTRAINT_NS, false);

            AbsNode classNode = addNode(field.getDefiningClass());
            AbsNode val1Node = addNode(field);
            AbsNode val2Node = addNode(field);

            addEdge(classNode, val1Node, field.getName().toString());
            addEdge(classNode, val2Node, field.getName().toString());
            addEdge(val1Node, val2Node, "!=");

            switch (this.m_cfg.getTypeModel().getFields().getContainers().getOrdering().getType()) {
            case INDEX:
                // Check if two nodes exist with same index value
                String indexName = this.m_cfg.getStrings().getIndexEdge();
                AbsNode indexNode = addNode("int:");
                addEdge(val1Node, indexNode, indexName);
                addEdge(val2Node, indexNode, indexName);
                break;
            case EDGE:
                // Check if two nodes exist that are head
                String nextName = this.m_cfg.getStrings().getNextEdge();
                AbsNode val3Node = addNode(field);
                val3Node.addName("not:");
                AbsNode val4Node = addNode(field);
                val4Node.addName("not:");
                addEdge(val3Node, val1Node, nextName);
                addEdge(val4Node, val2Node, nextName);
            }
        }
    }

    private void createConstraints(Enum e) {
        if (this.m_cfg.getTypeModel().getEnumMode() == EnumModeType.NODE) {
            // Nothing to do, GROOVE handles it
            return;
        }

        if (!this.m_cfg.getConstraints().getChecks().isCheckEnum()) {
            // No checks for enum
            return;
        }

        // Create rules that prohibit multiple flags

        setRuleGraph("Enum_" + e.getId().getName(), CONSTRAINT_NS, true);

        AbsNode enumNode = addNode(e);
        //hacky crappy double loop checking all combinations
        if (e.getLiterals().size() > 1) {
            String flagCheck = "";
            boolean first = true;
            for (Name n : e.getLiterals()) {
                if (!first) {
                    flagCheck += "|";
                }
                flagCheck += "flag:" + n + ".(";
                boolean subfirst = true;
                for (Name n2 : e.getLiterals()) {
                    if (n2 == n) {
                        continue;
                    }
                    if (!subfirst) {
                        flagCheck += "|";
                    }
                    flagCheck += "flag:" + n2;
                    subfirst = false;
                }
                flagCheck += ")";
                first = false;
            }
            addEdge(enumNode, enumNode, flagCheck);

        }

        setRuleGraph("EnumNoflag_" + e.getId().getName(), CONSTRAINT_NS, true);
        enumNode = addNode(e);
        for (Name n : e.getLiterals()) {
            addEdge(enumNode, enumNode, "not:flag:" + n.toString());
        }
    }

    private void setRuleGraph(String name, String ns, boolean recursiveTypes) {
        name = GrooveUtil.getSafeId(name);
        if (ns != null) {
            name = ns + QualName.SEPARATOR + name;
        }
        int index = 0;
        while (getExport().hasGraph(index == 0 ? name : name + index, GraphRole.RULE)) {
            index++;
        }
        PreGraph graph = getExport().getGraph(index == 0 ? name : name + index, GraphRole.RULE);
        this.m_currentGraph = graph;
        this.m_recursiveTypes = recursiveTypes;
    }

    private PreGraph m_currentGraph;
    // If true, hasElement always returns false
    private boolean m_recursiveTypes;

    /** Creates a node for a given concept and adds it to the current graph. */
    private AbsNode addNode(Concept c) {
        return addNode(c, null);
    }

    /** Creates a node for a given concept, using an optional parameter,
     * and adds it to the current graph. */
    private AbsNode addNode(Concept c, String param) {
        AbsNode result;
        switch (c.getKind()) {
        case CLASS_TYPE:
            result = addClassNode((Class) c);
            break;
        case INT_TYPE:
        case BOOL_TYPE:
        case REAL_TYPE:
        case STRING_TYPE:
        case CUSTOM_TYPE:
        case ENUM_TYPE:
            result = addTypeNode((Type) c);
            break;
        case TUPLE_TYPE:
            result = addTupleNode((Tuple) c);
            break;
        case CONTAINER_TYPE:
            result = addContainerNode((Container) c, param);
            break;
        case FIELD:
            result = addFieldNode((Field) c);
            break;
        case BOOL_VAL:
            result = createBoolValue((BoolValue) c);
            break;
        case INT_VAL:
            result = createIntValue((IntValue) c);
            break;
        case REAL_VAL:
            result = createRealValue((RealValue) c);
            break;
        case STRING_VAL:
            result = createStringValue((StringValue) c);
            break;
        case ENUM_VAL:
            result = createEnumValue((EnumValue) c);
            break;
        case CUSTOM_VAL:
            result = createCustomValue((CustomDataValue) c);
            break;
        default:
            throw Exceptions.illegalArg("Unexpected concept kind %s", c.getKind());
        }
        return result;
    }

    private AbsNode addClassNode(Class c) {
        // For properties, nullable doesn't really matter, so just one canonical node (for proper)
        if (!c.isProper()) {
            c = c.getProperClass();
        }
        return addTypeNode(c);
        // Fields are not created automatically, need to be visited explicitly
    }

    private AbsNode addFieldNode(Field field) {
        AbsNode result;
        if (field.getType() instanceof Container) {
            result = addNode(field.getType(), this.m_cfg.getName(field));
        } else if (this.m_cfg.useIntermediate(field)) {
            result = addNode(this.m_cfg.getName(field));
            if (this.m_recursiveTypes) {
                AbsNode fieldNode = addNode(field.getType());
                String valName = this.m_cfg.getStrings().getValueEdge();
                addEdge(result, fieldNode, valName);
            }
        } else {
            result = addTypeNode(field.getType());
        }
        return result;
    }

    private AbsNode addTupleNode(Tuple tuple) {
        AbsNode result = addTypeNode(tuple);
        int index = 1;
        if (this.m_recursiveTypes) {
            for (Type t : tuple.getTypes()) {
                AbsNode typeNode = addNode(t);
                addEdge(result, typeNode, "_" + index++);
            }
        }
        return result;
    }

    private AbsNode addContainerNode(Container c, String base) {
        AbsNode result;
        if (this.m_cfg.useIntermediate(c)) {
            assert base != null;
            result = addNode(base + this.m_cfg.getContainerPostfix(c));
            if (this.m_recursiveTypes) {
                AbsNode typeNode = addNode(c.getType(), this.m_cfg.getContainerName(base, c));
                String valName = this.m_cfg.getStrings().getValueEdge();
                addEdge(result, typeNode, valName);
            }
        } else {
            result = addNode(c.getType(), this.m_cfg.getContainerName(base, c));
        }
        return result;
    }

    private AbsNode addTypeNode(Type t) {
        return addNode(this.m_cfg.getName(t));
    }

    /** Adds a node to the current graph, with a set of labels. */
    private AbsNode addNode(String... labels) {
        AbsNode result = new AbsNode(labels);
        this.m_currentGraph.addNodes(result);
        return result;
    }

    /** Adds edges to the current graph for the given labels. */
    private void addEdge(AbsNode source, AbsNode target, String... labels) {
        for (String label : labels) {
            new AbsEdge(source, target, label);
        }
    }
}
