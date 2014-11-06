package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.NullableType;
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
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.Object;
import groove.io.external.PortException;

import java.util.HashSet;
import java.util.Set;

//separate different graphs for various elements where applicable.
public class GlossaryToGroove extends GlossaryExportBuilder<GrooveExport,AbsNode> {
    public GlossaryToGroove(GrooveExport export, Glossary glos) {
        super(export, glos);
        this.m_cfg = export.getConfig();
        this.m_currentGraph = export.getGraph(GrooveUtil.getSafeId(glos.getName()), GraphRole.TYPE);
        this.m_properties = new HashSet<>();
    }

    private final Config m_cfg;
    private final GrammarGraph m_currentGraph;
    private final Set<Property> m_properties;

    @Override
    public void build() throws PortException {
        int timer = Timer.start("TM to GROOVE");
        this.m_cfg.setGlossary(getGlossary());
        super.build();
        Timer.stop(timer);
    }

    @Override
    protected void setElement(Acceptor o, AbsNode n) {
        super.setElement(o, n);
        this.m_currentGraph.m_nodes.put(o, n);
    }

    private void setPropertyVisited(Property o) {
        this.m_properties.add(o);
    }

    private boolean propertyVisited(Property o) {
        return this.m_properties.contains(o);
    }

    @Override
    public void addClass(Class c) {
        if (hasElement(c)) {
            return;
        }

        // If not using the nullable/proper class system, don't instantiate nullable classes
        if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
            if (!c.isProper()) {
                // Simply revert to the proper instance
                AbsNode classNode = getElement(c.getProperClass());
                if (!hasElement(c)) {
                    setElement(c, classNode);
                }
                return;
            }
        }

        AbsNode classNode = new AbsNode(this.m_cfg.getName(c));
        setElement(c, classNode);

        // If nullable class, just make it a superclass of the proper class, and allow NIL
        if (!c.isProper()) {
            classNode.addName("abs:");

            AbsNode nilNode = getElement(Object.NIL);
            /*AbsEdge subEdge = */new AbsEdge(nilNode, classNode, "sub:");

            AbsNode properNode = getElement(c.getProperClass());
            /* AbsEdge properSubEdge = */new AbsEdge(properNode, classNode, "sub:");
            return;
        }

        // From here on working with a proper class

        for (Class clazz : c.getSuperClasses()) {
            AbsNode superClassNode = getElement(clazz.getProperClass());
            /*AbsEdge subEdge = */new AbsEdge(classNode, superClassNode, "sub:");
        }

        for (Field f : c.getFields()) {
            AbsNode fieldNode = getElement(f);

            String edgeLabel = "";
            int lowerBound = f.getLowerBound();
            if (lowerBound == 0 && f.getUpperBound() == 1 && f.getType() instanceof Class) {
                // Nullable, but in GROOVE always Nil value (unless turned off)
                if (this.m_cfg.getXMLConfig().getGlobal().getNullable() != NullableType.NONE) {
                    lowerBound = 1;
                }
            }

            // When using intermediates, ensure each intermediate is linked to one field
            //TODO: temporarily check useIntermediate container, currently out of sync due to multiplicity checks
            if (this.m_cfg.useIntermediate(f) && f.getType() instanceof Container
                && this.m_cfg.useIntermediate((Container) f.getType())) {
                edgeLabel += "in=1:";
            }

            // If not 0..* use out multiplicity
            if (f.getUpperBound() != -1 || lowerBound != 0) {
                edgeLabel += "out=";
                if (lowerBound != f.getUpperBound()) {
                    edgeLabel +=
                        lowerBound + ".." + ((f.getUpperBound() == -1) ? "*" : f.getUpperBound());
                } else {
                    edgeLabel += lowerBound;
                }
                edgeLabel += ":";
            }
            /*AbsEdge fieldEdge = */new AbsEdge(classNode, fieldNode, edgeLabel
                + f.getName().toString());
        }

        // If all nullable classes, get node of nullable version too
        if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.ALL) {
            getElement(c.getNullableClass());
        }
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
            boolean isNullable = false;
            if (this.m_cfg.useIntermediate(field) && field.getType() instanceof Class) {
                isNullable = !((Class) field.getType()).isProper();
                fieldNode = getElement(((Class) field.getType()).getProperClass());
            } else {
                fieldNode = getElement(field.getType());
            }
            if (this.m_cfg.useIntermediate(field)) {
                String valName = this.m_cfg.getStrings().getValueEdge();
                AbsNode interNode = new AbsNode(this.m_cfg.getName(field));
                interNode.addName("edge:\"" + field.getName() + "\"");
                String out = isNullable ? "out=0..1:" : "out=1:";
                /*AbsEdge valEdge = */new AbsEdge(interNode, fieldNode, out + valName);
                fieldNode = interNode;
            }
        }

        setElement(field, fieldNode);
    }

    @Override
    public void addDataType(DataType dt) {
        if (hasElement(dt)) {
            return;
        }

        if (dt instanceof CustomDataType) {
            String valueName = this.m_cfg.getStrings().getDataValue();
            AbsNode dataNode = new AbsNode(this.m_cfg.getName(dt), "string:" + valueName);
            setElement(dt, dataNode);
        } else {
            AbsNode typeNode = new AbsNode(this.m_cfg.getName(dt));
            setElement(dt, typeNode);
        }
    }

    @Override
    public void addEnum(Enum e) {
        if (hasElement(e)) {
            return;
        }
        AbsNode enumNode;
        if (this.m_cfg.getXMLConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getXMLConfig().getGlobal().getIdSeparator();
            enumNode = new AbsNode(this.m_cfg.getName(e), "abs:");
            for (Name n : e.getLiterals()) {
                //String litName = m_cfg.getName(e) + sep + n.toString();
                String litName = "type:" + this.m_cfg.idToName(e.getId()) + sep + n.toString();
                AbsNode valNode = new AbsNode(litName);
                /*AbsEdge valEdge = */new AbsEdge(valNode, enumNode, "sub:");
            }
        } else {
            enumNode = new AbsNode(this.m_cfg.getName(e));
            for (Name n : e.getLiterals()) {
                enumNode.addName("flag:" + n.toString());
            }
        }
        setElement(e, enumNode);
    }

    @Override
    public void addContainer(Container c, String base) {
        if (hasElement(c)) {
            return;
        }

        AbsNode typeNode = null;
        if (!(c.getType() instanceof Container)) {
            typeNode = getElement(c.getType());
        } else {
            assert base != null;
            typeNode = getElement(c.getType(), this.m_cfg.getContainerName(base, c));
        }

        boolean useIndex = this.m_cfg.useIndex(c);
        boolean indexValue =
            (this.m_cfg.getXMLConfig()
                .getTypeModel()
                .getFields()
                .getContainers()
                .getOrdering()
                .getType() == OrderType.INDEX);

        AbsNode containerNode = null;
        if (this.m_cfg.useIntermediate(c)) {
            assert base != null;
            containerNode = new AbsNode(base + this.m_cfg.getContainerPostfix(c));

            // Use just the last part of the container id as the edge name
            int lastIndex =
                base.lastIndexOf(this.m_cfg.getXMLConfig().getGlobal().getIdSeparator());
            String edgeName = base;
            if (lastIndex != -1) {
                edgeName = base.substring(lastIndex + 1);
            }

            if (useIndex && indexValue) {
                String indexName = this.m_cfg.getStrings().getIndexEdge();
                containerNode.addName("edge:\"" + edgeName + " %s\"," + indexName);
            } else {
                containerNode.addName("edge:\"" + edgeName + "\"");
            }

            // If subtype is another container, allow more nodes. Otherwise, just one
            String valName = /*"in=1:" + */this.m_cfg.getStrings().getValueEdge();
            if (c.getType() instanceof Container) {
                valName = "out=1..*:" + valName;
            } else {
                valName = "out=1:" + valName;
            }
            /*AbsEdge valEdge = */new AbsEdge(containerNode, typeNode, valName);
        } else {
            containerNode = typeNode;
        }

        if (useIndex) {
            if (indexValue) {
                String indexName = this.m_cfg.getStrings().getIndexEdge();
                containerNode.addName("out=1:int:" + indexName);
            } else {
                String nextName = this.m_cfg.getStrings().getNextEdge();
                /*AbsEdge nextEdge = */new AbsEdge(containerNode, containerNode, "out=0..1:"
                    + nextName);

                if (this.m_cfg.getXMLConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .getOrdering()
                    .isUsePrevEdge()) {
                    String prevName = this.m_cfg.getStrings().getPrevEdge();
                    new AbsEdge(containerNode, containerNode, "out=0..1:" + prevName);
                }
            }
        }

        setElement(c, containerNode);

        return;
    }

    @Override
    public void addTuple(Tuple tuple) {
        if (hasElement(tuple)) {
            return;
        }

        //TODO: Nodified edge style might suit tuple better
        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tuple));
        setElement(tuple, tupleNode);

        int index = 1;
        for (Type t : tuple.getTypes()) {
            AbsNode typeNode = getElement(t);
            /*AbsEdge elemEdge = */new AbsEdge(tupleNode, typeNode, "_" + index++);
        }

        return;
    }

    @Override
    public void addObject(Object object) {
        if (hasElement(object)) {
            return;
        }
        if (object != Object.NIL) {
            throw new IllegalArgumentException("Cannot create object node in type model");
        }

        String name = this.m_cfg.getStrings().getNilName();
        AbsNode nilNode = new AbsNode("type:" + name);
        setElement(object, nilNode);
    }

    @Override
    public void addAbstractProp(AbstractProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getProperties().isUseAbstract()) {
            return;
        }

        AbsNode classNode = getElement(prop.getAbstractClass().getProperClass());
        classNode.addName("abs:");
    }

    @Override
    public void addContainmentProp(ContainmentProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getProperties().isUseContainment()) {
            return;
        }

        // Add containment to field edge
        String edgeName = prop.getField().getName().toString();
        AbsNode containmentNode = getElement(prop.getContainerClass());
        for (AbsEdge edge : containmentNode.getEdges()) {
            if (edge.getName().endsWith(edgeName)) {
                edge.setName("part:" + edge.getName());
            }
        }

        // Add to intermediate node as well if required
        if (this.m_cfg.useIntermediate(prop.getField())) {
            edgeName = this.m_cfg.getStrings().getValueEdge();
            containmentNode = getElement(prop.getField());

            for (AbsEdge edge : containmentNode.getEdges()) {
                if (edge.getName().endsWith(edgeName)) {
                    edge.setName("part:" + edge.getName());
                }
            }
        }

    }

    @Override
    public void addIdentityProp(IdentityProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        // Nothing to do for type graph
    }

    @Override
    public void addKeysetProp(KeysetProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        // Nothing to do for type graph
    }

    @Override
    // Called twice for each opposite pair, opposite has a reverse
    // So only handle a single direction
    public void addOppositeProp(OppositeProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        if (!this.m_cfg.getXMLConfig().getTypeModel().getProperties().isUseOpposite()) {
            return;
        }

        boolean useOpposites = this.m_cfg.getXMLConfig().getTypeModel().getFields().isOpposites();
        if (!useOpposites) {
            return;
        }

        //TODO: possible for self referential opposites, make sure the field nodes are split in that case
        AbsNode class1Node = getElement(prop.getClass1());
        AbsNode class2Node = getElement(prop.getClass2());

        AbsNode field1Node = getElement(prop.getField1());
        AbsNode field2Node = getElement(prop.getField2());

        AbsNode source = this.m_cfg.useIntermediate(prop.getField1()) ? field1Node : class1Node;
        AbsNode target = this.m_cfg.useIntermediate(prop.getField2()) ? field2Node : class2Node;

        String oppositeName = this.m_cfg.getStrings().getOppositeEdge();
        /*AbsEdge oppositeEdge = */new AbsEdge(source, target, "out=1:" + oppositeName);
    }

    @Override
    public void addDefaultValueProp(DefaultValueProperty prop) {
        if (propertyVisited(prop)) {
            return;
        }
        setPropertyVisited(prop);

        // Nothing to do for type graph
    }
}
