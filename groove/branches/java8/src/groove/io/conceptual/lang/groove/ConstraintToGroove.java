package groove.io.conceptual.lang.groove;

import groove.grammar.QualName;
import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
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
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;

import java.util.HashSet;
import java.util.Set;

//separate different graphs for various elements where applicable.
/** Bridge from glossary constraints to GROOVE rules. */
public class ConstraintToGroove extends GlossaryExportBuilder<GrooveExport,AbsNode> {
    private static final String CONSTRAINT_NS = "constraint";

    private GrammarGraph m_currentGraph;

    // If true, hasElement always returns false
    private boolean m_allowDuplicates;
    private boolean m_recursiveTypes;

    public ConstraintToGroove(Glossary glos, GrooveExport export) {
        super(glos, export);
        this.m_cfg = export.getConfig();
        this.m_allowDuplicates = false;
        this.m_recursiveTypes = true;
    }

    private final Config m_cfg;
    private final Set<Property> m_properties = new HashSet<Property>();

    @Override
    public void build() {
        this.m_cfg.setGlossary(getGlossary());
        for (Property p : getGlossary().getProperties()) {
            p.doBuild(this, null);
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
    protected void setElement(Acceptor o, AbsNode n) {
        this.m_currentGraph.m_nodes.put(o, n);
        if (this.m_allowDuplicates && super.hasElement(o)) {
            this.m_elements.put(o, n);
        } else {
            super.setElement(o, n);
        }
    }

    @Override
    protected boolean hasElement(Acceptor o) {
        if (this.m_allowDuplicates) {
            return false;
        }
        return super.hasElement(o);
    }

    @Override
    protected AbsNode getElement(Acceptor o, String param, boolean allowNull) {
        if (this.m_allowDuplicates) {
            o.doBuild(this, param);
        }
        return super.getElement(o, param, allowNull);
    }

    private void setPropertyVisited(Property o) {
        this.m_properties.add(o);
    }

    private boolean propertyVisited(Property o) {
        return this.m_properties.contains(o);
    }

    private GrammarGraph getUniqueGraph(String name, GraphRole role) {
        return getUniqueGraph(name, role, null);
    }

    private GrammarGraph getUniqueGraph(String name, GraphRole role, String ns) {
        this.m_elements.clear();

        name = GrooveUtil.getSafeId(name);
        if (ns != null) {
            name = ns + QualName.SEPARATOR + name;
        }
        int index = 0;
        while (getExport().hasGraph(index == 0 ? name : name + index, role)) {
            index++;
        }
        return getExport().getGraph(index == 0 ? name : name + index, role);
    }

    @Override
    public void addClass(Class c) {
        if (hasElement(c)) {
            return;
        }

        // For properties, nullable doesn't really matter, so just one canonical node (for proper)

        if (!(c.isProper())) {
            AbsNode classNode = getElement(c.getProperClass());
            setElement(c, classNode);
        } else {
            AbsNode classNode = new AbsNode(this.m_cfg.getName(c));
            setElement(c, classNode);
        }

        // Fields are not created automatically, need to be visited explicitly
    }

    @Override
    public void addField(Field field) {
        if (hasElement(field)) {
            return;
        }

        AbsNode fieldNode = null;
        if (field.getType() instanceof Container) {
            fieldNode = getElement(field.getType(), this.m_cfg.getName(field));
        } else {
            if (this.m_cfg.useIntermediate(field)) {
                AbsNode interNode = new AbsNode(this.m_cfg.getName(field));
                String valName = this.m_cfg.getStrings().getValueEdge();
                if (this.m_recursiveTypes) {
                    fieldNode = getElement(field.getType());
                    new AbsEdge(interNode, fieldNode, valName);
                }
                fieldNode = interNode;
            } else {
                fieldNode = getElement(field.getType());
            }
        }

        // Create edge
        //AbsNode classNode = getElement(field.getDefiningClass());
        ///*AbsEdge fieldEdge = */new AbsEdge(classNode, fieldNode, field.getName().toString());

        setElement(field, fieldNode);
    }

    @Override
    public void addDataType(DataType dt) {
        if (hasElement(dt)) {
            return;
        }

        AbsNode typeNode = new AbsNode(this.m_cfg.getName(dt));
        setElement(dt, typeNode);
    }

    @Override
    public void addEnum(Enum e) {
        if (hasElement(e)) {
            return;
        }

        // Ignoring values, so ignoring config

        AbsNode enumNode = new AbsNode(this.m_cfg.getName(e));
        setElement(e, enumNode);

        return;
    }

    @Override
    public void addContainer(Container c, String base) {
        if (hasElement(c)) {
            return;
        }

        AbsNode containerNode = null;
        if (this.m_cfg.useIntermediate(c)) {
            assert base != null;
            containerNode = new AbsNode(base + this.m_cfg.getContainerPostfix(c));
        }

        AbsNode typeNode = null;
        if (this.m_recursiveTypes || !this.m_cfg.useIntermediate(c)) {
            if (!(c.getType() instanceof Container)) {
                typeNode = getElement(c.getType());
            } else {
                assert base != null;
                typeNode = getElement(c.getType(), this.m_cfg.getContainerName(base, c));
            }
        }

        if (this.m_cfg.useIntermediate(c) && this.m_recursiveTypes) {
            String valName = this.m_cfg.getStrings().getValueEdge();
            /*AbsEdge valEdge = */new AbsEdge(containerNode, typeNode, valName);
        }
        if (!this.m_cfg.useIntermediate(c)) {
            containerNode = typeNode;
        }

        setElement(c, containerNode);

        return;
    }

    @Override
    public void addTuple(Tuple tuple) {
        if (hasElement(tuple)) {
            return;
        }

        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tuple));
        setElement(tuple, tupleNode);

        int index = 1;
        if (this.m_recursiveTypes) {
            for (Type t : tuple.getTypes()) {
                AbsNode typeNode = getElement(t);
                /*AbsEdge elemEdge = */new AbsEdge(tupleNode, typeNode, "_" + index++);
            }
        }

        return;
    }

    @Override
    public void addObject(Object object) {
        throw new IllegalArgumentException("Cannot create object node in constraints");
    }

    @Override
    public void addAbstractProp(AbstractProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);
        // Part of type graph
    }

    @Override
    public void addContainmentProp(ContainmentProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);
        // Part of type graph
    }

    @Override
    public void addIdentityProp(IdentityProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckIdentifier()) {
            return;
        }

        this.m_currentGraph =
            getUniqueGraph("Identity_" + prop.getIdClass().getId().getName(),
                GraphRole.RULE,
                CONSTRAINT_NS);
        equivalencyCheck(prop.getIdClass(), prop.getFields(), null);
    }

    @Override
    public void addKeysetProp(KeysetProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckKeyset()) {
            return;
        }

        this.m_currentGraph =
            getUniqueGraph("Keyset_" + prop.getRelField().getName(), GraphRole.RULE, CONSTRAINT_NS);
        equivalencyCheck(prop.getKeyClass(), prop.getKeyFields(), prop.getRelField());
    }

    // Multiplicities are ignored. If upper = 1, also handled by cases for upper > 1
    // If upper == 1, treat as unordered & unique
    private void equivalencyCheck(Class fieldsClass, Field[] fields, Field containfield) {
        String valueName = this.m_cfg.getStrings().getValueEdge();
        String indexName = this.m_cfg.getStrings().getIndexEdge();
        String nextName = this.m_cfg.getStrings().getNextEdge();

        int curQuant = 1;

        this.m_allowDuplicates = true;
        this.m_recursiveTypes = false;

        AbsNode class1Node = getElement(fieldsClass);
        AbsNode class2Node = getElement(fieldsClass);
        new AbsEdge(class1Node, class2Node, "!=");

        if (containfield != null) {
            AbsNode keyNode = getElement(containfield.getDefiningClass());
            AbsNode contain1Node = class1Node;
            AbsNode contain2Node = class2Node;
            if (this.m_cfg.useIntermediate(containfield)) {
                contain1Node = getElement(containfield);
                contain2Node = getElement(containfield);
                new AbsEdge(contain1Node, class1Node, valueName);
                new AbsEdge(contain2Node, class2Node, valueName);
            }
            new AbsEdge(keyNode, contain1Node, containfield.getName().toString());
            new AbsEdge(keyNode, contain2Node, containfield.getName().toString());
        }

        for (Field field : fields) {
            String fieldName = field.getName().toString();

            // Count field
            AbsNode field1CountNode = getElement(field);
            AbsNode field2CountNode = getElement(field);
            new AbsEdge(class1Node, field1CountNode, fieldName);
            new AbsEdge(class2Node, field2CountNode, fieldName);

            AbsNode field1CountForall = new AbsNode("forall:");
            AbsNode field2CountForall = new AbsNode("forall:");
            new AbsEdge(field1CountNode, field1CountForall, "@");
            new AbsEdge(field2CountNode, field2CountForall, "@");

            AbsNode fieldCount = new AbsNode("int:");
            new AbsEdge(field1CountForall, fieldCount, "count");
            new AbsEdge(field2CountForall, fieldCount, "count");

            // Check if intermediate is used
            boolean useIntermediate = this.m_cfg.useIntermediate(field);
            // Check if unique and ordered. If not container, assume unique and unordered
            boolean isUnique = true;
            boolean isOrdered = false;
            if (field.getType() instanceof Container) {
                Container c = (Container) field.getType();
                isUnique = c.getContainerType() == Kind.SET || c.getContainerType() == Kind.ORD;
                isOrdered = c.getContainerType() == Kind.ORD || c.getContainerType() == Kind.SEQ;
            }

            if (!useIntermediate) {
                // This is the simplest case. Only unordered, unique values (otherwise, intermediate would be required)
                // Check that for all field nodes of class1, there exists a similar value for class2, by checking the existence of the field edge
                AbsNode fieldValueNode = getElement(field);
                new AbsEdge(class1Node, fieldValueNode, fieldName);
                new AbsEdge(class2Node, fieldValueNode, "use=qq" + curQuant + ":" + fieldName);

                AbsNode forallNode = new AbsNode("forall:");
                new AbsEdge(fieldValueNode, forallNode, "@");
                AbsNode existsNode = new AbsNode("exists=qq" + curQuant + ":");
                new AbsEdge(existsNode, forallNode, "in");
            } else {
                // Create field nodes with equal value
                AbsNode field1InterNode = getElement(field);
                AbsNode field2InterNode = getElement(field);
                AbsNode fieldValueNode =
                    getElement(field.getType() instanceof Container
                        ? ((Container) field.getType()).getType() : field.getType(),
                        this.m_cfg.getName(field));
                new AbsEdge(class1Node, field1InterNode, fieldName);
                new AbsEdge(class2Node, field2InterNode, fieldName);
                new AbsEdge(field1InterNode, fieldValueNode, valueName);
                new AbsEdge(field2InterNode, fieldValueNode, valueName);

                // Now there are 4 cases (uniqueness doesn't matter for ordered containers):
                // * ordered with next edges
                // * ordered with index value
                // * unordered not unique
                // * unordered unique
                if (isOrdered) {
                    boolean indexValue =
                        (this.m_cfg.getXMLConfig()
                            .getTypeModel()
                            .getFields()
                            .getContainers()
                            .getOrdering()
                            .getType() == OrderType.INDEX);
                    if (indexValue) {
                        //check if all index values are equal for all equal values
                        // Create index node
                        AbsNode indexNode = new AbsNode("int:");
                        new AbsEdge(field1InterNode, indexNode, "index");
                        new AbsEdge(field2InterNode, indexNode, "use=qq" + curQuant + ":"
                            + indexName);

                        // Create quantifier system
                        AbsNode forallNode = new AbsNode("forall:");
                        new AbsEdge(fieldValueNode, forallNode, "@");
                        new AbsEdge(field1InterNode, forallNode, "@");
                        new AbsEdge(indexNode, forallNode, "@");

                        AbsNode existsNode = new AbsNode("exists=qq" + curQuant + ":");
                        new AbsEdge(field2InterNode, existsNode, "@");
                        new AbsEdge(existsNode, forallNode, "in");
                    } else {
                        //check if the value if the intermediate at the next edge is the same of that of the other intermediate's next
                        // Create next intermediates
                        AbsNode field1Inter2Node = getElement(field);
                        AbsNode field2Inter2Node = getElement(field);
                        //AbsNode fieldValue2Node = getElement(field.getType());
                        AbsNode fieldValue2Node =
                            getElement(field.getType() instanceof Container
                                ? ((Container) field.getType()).getType() : field.getType(),
                                this.m_cfg.getName(field));
                        new AbsEdge(field1Inter2Node, fieldValue2Node, valueName);
                        new AbsEdge(field2Inter2Node, fieldValue2Node, valueName);
                        // Next edge
                        new AbsEdge(field1InterNode, field1Inter2Node, nextName);
                        new AbsEdge(field2InterNode, field2Inter2Node, nextName);

                        // Create quantifier system
                        AbsNode forallNode = new AbsNode("forall:");
                        new AbsEdge(fieldValueNode, forallNode, "@");
                        new AbsEdge(field1InterNode, forallNode, "@");
                        AbsNode existsNode = new AbsNode("exists:");
                        new AbsEdge(field2InterNode, existsNode, "@");
                        new AbsEdge(existsNode, forallNode, "in");
                        // Quantifier system for next value
                        AbsNode forall2Node = new AbsNode("forall:");
                        new AbsEdge(fieldValue2Node, forall2Node, "@");
                        new AbsEdge(field1Inter2Node, forall2Node, "@");
                        AbsNode exists2Node = new AbsNode("exists:");
                        new AbsEdge(field2Inter2Node, exists2Node, "@");
                        new AbsEdge(exists2Node, forall2Node, "in");
                        // Connect both quantifer systems
                        new AbsEdge(forall2Node, existsNode, "in");
                    }
                } else {
                    if (isUnique) {
                        //unordered-unique: Simply check if for all intermediate values, the 2nd instance has such an intermediate as well
                        AbsNode forallNode = new AbsNode("forall:");
                        new AbsEdge(fieldValueNode, forallNode, "@");
                        new AbsEdge(field1InterNode, forallNode, "@");
                        AbsNode existsNode = new AbsNode("exists:");
                        new AbsEdge(field2InterNode, existsNode, "@");
                        new AbsEdge(existsNode, forallNode, "in");
                    } else {
                        //unordered-not unique: bijection test
                        // Create additional intermediate node that forces all different values to be checked
                        AbsNode field1Inter2Node = getElement(field);
                        new AbsEdge(class1Node, field1Inter2Node, fieldName);
                        new AbsEdge(field1Inter2Node, fieldValueNode, valueName);

                        // Create quantifier system
                        AbsNode countNode = new AbsNode("int:");
                        AbsNode forallInter1Node = new AbsNode("forall:");
                        AbsNode forallInter2Node = new AbsNode("forall:");
                        new AbsEdge(forallInter1Node, countNode, "count");
                        new AbsEdge(forallInter2Node, countNode, "count");
                        new AbsEdge(field1InterNode, forallInter1Node, "@");
                        new AbsEdge(field2InterNode, forallInter2Node, "@");
                        AbsNode existsCountNode = new AbsNode("exists:");
                        new AbsEdge(forallInter1Node, existsCountNode, "in");
                        new AbsEdge(forallInter2Node, existsCountNode, "in");
                        new AbsEdge(countNode, existsCountNode, "@");

                        AbsNode forallValuesNode = new AbsNode("forall:");
                        new AbsEdge(field1Inter2Node, forallValuesNode, "@");
                        new AbsEdge(fieldValueNode, forallValuesNode, "@");
                        new AbsEdge(existsCountNode, forallValuesNode, "in");
                    }
                }
            }

            curQuant++;
        }

        this.m_allowDuplicates = false;
        this.m_recursiveTypes = true;
    }

    @Override
    // Called twice, opposite has a reverse
    // SO only handle a single direction
    public void addOppositeProp(OppositeProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckOpposite()) {
            return;
        }

        String valueName = this.m_cfg.getStrings().getValueEdge();

        this.m_currentGraph =
            getUniqueGraph("Opposite_" + prop.getField1().getName(), GraphRole.RULE, CONSTRAINT_NS);

        this.m_allowDuplicates = true;
        this.m_recursiveTypes = false;

        AbsNode class1Node = getElement(prop.getClass1());
        AbsNode class2Node = getElement(prop.getClass2());

        AbsNode field1Node = class2Node;
        if (this.m_cfg.useIntermediate(prop.getField1())) {
            field1Node = getElement(prop.getField1());
            new AbsEdge(field1Node, class2Node, valueName);
        }
        AbsNode field2Node = class1Node;
        if (this.m_cfg.useIntermediate(prop.getField2())) {
            field2Node = getElement(prop.getField2());
            // NAC intermediate node
            field2Node.addName("not:");
            new AbsEdge(field2Node, class1Node, valueName);
        }

        new AbsEdge(class1Node, field1Node, prop.getField1().getName().toString());

        if (this.m_cfg.useIntermediate(prop.getField2())) {
            new AbsEdge(class2Node, field2Node, prop.getField2().getName().toString());
        } else {
            // NAC value edge
            new AbsEdge(class2Node, class1Node, "not:" + prop.getField2().getName().toString());
        }

        this.m_allowDuplicates = false;
        this.m_recursiveTypes = true;
    }

    @Override
    public void addDefaultValueProp(DefaultValueProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getFields().getDefaults().isUseRule()) {
            // No default rule to be created
            return;
        }

        if (prop.getField().getType() instanceof Container && prop.getField().getUpperBound() > 1) {
            // Cannot support containers, because hard to determine if container is empty, or not there
            throw new RuntimeException("Container default value not allowed");
        }
        if (prop.getField().getType() instanceof Class) {
            // Cannot support containers, because hard to determine if container is empty, or not there
            throw new RuntimeException("Reference default value not allowed");
        }

        this.m_currentGraph =
            getUniqueGraph("Default_" + prop.getField().getName(), GraphRole.RULE);

        AbsNode classNode = getElement(prop.getField().getDefiningClass());
        //AbsNode fieldNode = getElement(defaultValueProperty.getField()); //this would be the actual value, or intermediate node
        AbsNode valueNode = getElement(prop.getDefaultValue());
        // If custom datatype, create the value node. Otherwise, just use "new" edge.
        if (prop.getField().getType() instanceof CustomDataType) {
            valueNode.addName("new:");
        }

        if (this.m_cfg.useIntermediate(prop.getField())) {
            AbsNode interNotNode = new AbsNode("not:", this.m_cfg.getName(prop.getField()));
            AbsNode interNode = new AbsNode("new:", this.m_cfg.getName(prop.getField()));
            String valName = this.m_cfg.getStrings().getValueEdge();

            new AbsEdge(classNode, interNode, "new:" + prop.getField().getName().toString());
            new AbsEdge(classNode, interNotNode, prop.getField().getName().toString());

            new AbsEdge(interNode, valueNode, valName);
        } else {
            new AbsEdge(classNode, valueNode, "new:" + prop.getField().getName().toString());

            AbsNode notNode = getElement(prop.getField().getType());
            notNode.addName("not:"); //Make NAC
            /*AbsEdge notEdge = */new AbsEdge(classNode, notNode, prop.getField()
                .getName()
                .toString());

        }

    }

    @Override
    public void addRealValue(RealValue realval) {
        if (hasElement(realval)) {
            return;
        }

        AbsNode realNode = new AbsNode("real:" + realval.getValue());
        setElement(realval, realNode);

        return;
    }

    @Override
    public void addStringValue(StringValue stringval) {
        if (hasElement(stringval)) {
            return;
        }

        AbsNode stringNode = new AbsNode("string:\"" + stringval.toEscapedString() + "\"");
        setElement(stringval, stringNode);

        return;
    }

    @Override
    public void addIntValue(IntValue intval) {
        if (hasElement(intval)) {
            return;
        }

        AbsNode intNode = new AbsNode("int:" + intval.getValue());
        setElement(intval, intNode);

        return;
    }

    @Override
    public void addBoolValue(BoolValue boolval) {
        if (hasElement(boolval)) {
            return;
        }

        AbsNode boolNode = new AbsNode("bool:" + boolval.getValue());
        setElement(boolval, boolNode);

        return;
    }

    @Override
    public void addEnumValue(EnumValue val) {
        if (hasElement(val)) {
            return;
        }

        if (this.m_cfg.getXMLConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getXMLConfig().getGlobal().getIdSeparator();
            String litName =
                "type:" + this.m_cfg.idToName(((Enum) val.getType()).getId()) + sep
                    + val.getValue();
            AbsNode enumNode = new AbsNode(litName);
            setElement(val, enumNode);
        } else {
            AbsNode enumNode = new AbsNode(this.m_cfg.getName(val.getType()));
            enumNode.addName("flag:" + val.getValue().toString());
            setElement(val, enumNode);
        }

        return;
    }

    @Override
    public void addCustomDataValue(CustomDataValue val) {
        if (hasElement(val)) {
            return;
        }

        String valueName = this.m_cfg.getStrings().getDataValue();
        AbsNode dataNode =
            new AbsNode(this.m_cfg.getName(val.getType()), "let:" + valueName + "=string:\""
                + val.getValue() + "\"");
        setElement(val, dataNode);
    }

    // Does not support recursive containers
    private void createConstraints(Field field) {
        // Uniqueness: Create field->value type path times two, this will match same value used twice in case of intermediate
        // Class not needed, intermediate node is unique within type graph
        // If no intermediate, no problem
        if (this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckUniqueness()) {
            if (this.m_cfg.useIntermediate(field)
                && field.getType() instanceof Container
                && (((Container) field.getType()).getContainerType() == Kind.SET || ((Container) field.getType()).getContainerType() == Kind.ORD)) {
                GrammarGraph prevGraph = this.m_currentGraph;
                this.m_currentGraph =
                    getUniqueGraph("Unique_" + field.getName(), GraphRole.RULE, CONSTRAINT_NS);

                this.m_allowDuplicates = true;
                this.m_recursiveTypes = false;

                AbsNode classNode = getElement(field.getDefiningClass());
                AbsNode interNode = getElement(field);
                AbsNode interNode2 = getElement(field);
                AbsNode typeNode = null;
                if (((Container) field.getType()).getType() instanceof Container) {
                    typeNode =
                        getElement(((Container) field.getType()).getType(),
                            this.m_cfg.getContainerName(this.m_cfg.getName(field),
                                (Container) field.getType()));
                } else {
                    typeNode = getElement(((Container) field.getType()).getType());
                }

                String valName = this.m_cfg.getStrings().getValueEdge();
                new AbsEdge(classNode, interNode, field.getName().toString());
                new AbsEdge(classNode, interNode2, field.getName().toString());
                new AbsEdge(interNode, typeNode, valName);
                new AbsEdge(interNode2, typeNode, valName);
                new AbsEdge(interNode, interNode2, "!=");

                this.m_allowDuplicates = false;
                this.m_recursiveTypes = true;

                this.m_currentGraph = prevGraph;
            }
        }

        // Ordering, check if indices are well ordered, or next edges don't have two heads (rest is checked by multiplicities)
        if (this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckOrdering()) {
            if (field.getType() instanceof Container
                && (((Container) field.getType()).getContainerType() == Kind.ORD || ((Container) field.getType()).getContainerType() == Kind.SEQ)) {
                GrammarGraph prevGraph = this.m_currentGraph;
                this.m_currentGraph =
                    getUniqueGraph("Ordered_" + field.getName(), GraphRole.RULE, CONSTRAINT_NS);

                this.m_allowDuplicates = true;
                this.m_recursiveTypes = false;

                AbsNode classNode = getElement(field.getDefiningClass());
                AbsNode val1Node = getElement(field);
                AbsNode val2Node = getElement(field);

                new AbsEdge(classNode, val1Node, field.getName().toString());
                new AbsEdge(classNode, val2Node, field.getName().toString());
                new AbsEdge(val1Node, val2Node, "!=");

                if (this.m_cfg.getXMLConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .getOrdering()
                    .getType() == OrderType.INDEX) {
                    // Check if two nodes exist with same index value
                    String indexName = this.m_cfg.getStrings().getIndexEdge();
                    AbsNode indexNode = new AbsNode("int:");
                    new AbsEdge(val1Node, indexNode, indexName);
                    new AbsEdge(val2Node, indexNode, indexName);
                } else if (this.m_cfg.getXMLConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .getOrdering()
                    .getType() == OrderType.EDGE) {
                    // Check if two nodes exist that are head
                    String nextName = this.m_cfg.getStrings().getNextEdge();
                    AbsNode val3Node = getElement(field);
                    val3Node.addName("not:");
                    AbsNode val4Node = getElement(field);
                    val4Node.addName("not:");

                    new AbsEdge(val3Node, val1Node, nextName);
                    new AbsEdge(val4Node, val2Node, nextName);
                }

                this.m_allowDuplicates = false;
                this.m_recursiveTypes = true;

                this.m_currentGraph = prevGraph;
            }
        }
    }

    private void createConstraints(Enum e) {
        if (this.m_cfg.getXMLConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            // Nothing to do, GROOVE handles it
            return;
        }

        if (!this.m_cfg.getXMLConfig().getTypeModel().getConstraints().isCheckEnum()) {
            // No checks for enum
            return;
        }

        // Create rules that prohibit multiple flags

        GrammarGraph prevGraph = this.m_currentGraph;
        this.m_currentGraph =
            getUniqueGraph("Enum_" + e.getId().getName(), GraphRole.RULE, CONSTRAINT_NS);

        AbsNode enumNode = getElement(e);
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
            new AbsEdge(enumNode, enumNode, flagCheck);

        }

        this.m_currentGraph =
            getUniqueGraph("EnumNoflag_" + e.getId().getName(), GraphRole.RULE, CONSTRAINT_NS);

        enumNode = getElement(e);

        for (Name n : e.getLiterals()) {
            new AbsEdge(enumNode, enumNode, "not:flag:" + n.toString());
        }

        this.m_currentGraph = prevGraph;
    }
}
