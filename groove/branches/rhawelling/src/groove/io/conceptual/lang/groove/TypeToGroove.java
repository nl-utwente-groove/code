package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Field;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.TypeExporter;
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
public class TypeToGroove extends TypeExporter<AbsNode> {
    private GrooveResource m_grooveResource;
    private Config m_cfg;
    private GrammarGraph m_currentGraph;
    private Set<Property> m_properties = new HashSet<Property>();

    public TypeToGroove(GrooveResource grooveResource) {
        m_grooveResource = grooveResource;
        m_cfg = m_grooveResource.getConfig();
    }

    @Override
    public void addTypeModel(TypeModel typeModel) throws PortException {
        int timer = Timer.start("TM to GROOVE");
        m_currentGraph = m_grooveResource.getGraph(GrooveUtil.getSafeId(typeModel.getName()), GraphRole.TYPE);
        m_properties.clear();
        visitTypeModel(typeModel, m_cfg);

        // Prefetch? Uncomment for more accurate timings
        m_currentGraph.getGraph().toAspectGraph(m_currentGraph.getGraphName(), m_currentGraph.getGraphRole());

        Timer.stop(timer);
    }

    @Override
    public ExportableResource getResource() {
        return m_grooveResource;
    }

    @Override
    protected void setElement(Acceptor o, AbsNode n) {
        super.setElement(o, n);
        m_currentGraph.m_nodes.put(o, n);
    }

    private void setPropertyVisited(Property o) {
        m_properties.add(o);
    }

    private boolean propertyVisited(Property o) {
        return m_properties.contains(o);
    }

    @Override
    public void visit(Class c, java.lang.Object param) {
        if (hasElement(c)) {
            return;
        }

        // If not using the nullable/proper class system, don't instantiate nullable classes
        if (m_cfg.getConfig().getGlobal().getNullable() == NullableType.NONE) {
            if (!c.isProper()) {
                // Simply revert to the proper instance
                AbsNode classNode = getElement(c.getProperClass());
                if (!hasElement(c)) {
                    setElement(c, classNode);
                }
                return;
            }
        }

        AbsNode classNode = new AbsNode(m_cfg.getName(c));
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
                if (m_cfg.getConfig().getGlobal().getNullable() != NullableType.NONE) {
                    lowerBound = 1;
                }
            }

            // When using intermediates, ensure each intermediate is linked to one field
            //TODO: temporarily check useIntermediate container, currently out of sync due to multiplicity checks
            if (m_cfg.useIntermediate(f) && f.getType() instanceof Container && m_cfg.useIntermediate((Container) f.getType())) {
                edgeLabel += "in=1:";
            }

            // If not 0..* use out multiplicity
            if (f.getUpperBound() != -1 || lowerBound != 0) {
                edgeLabel += "out=";
                if (lowerBound != f.getUpperBound()) {
                    edgeLabel += lowerBound + ".." + ((f.getUpperBound() == -1) ? "*" : f.getUpperBound());
                } else {
                    edgeLabel += lowerBound;
                }
                edgeLabel += ":";
            }
            /*AbsEdge fieldEdge = */new AbsEdge(classNode, fieldNode, edgeLabel + f.getName().toString());
        }

        // If all nullable classes, get node of nullable version too
        if (m_cfg.getConfig().getGlobal().getNullable() == NullableType.ALL) {
            getElement(c.getNullableClass());
        }
    }

    @Override
    public void visit(Field field, java.lang.Object param) {
        if (hasElement(field)) {
            return;
        }

        AbsNode fieldNode = null;

        if (field.getType() instanceof Container) {
            fieldNode = getElement(field.getType(), m_cfg.getName(field));
        } else {
            boolean isNullable = false;
            if (m_cfg.useIntermediate(field) && field.getType() instanceof Class) {
                isNullable = !((Class) field.getType()).isProper();
                fieldNode = getElement(((Class) field.getType()).getProperClass());
            } else {
                fieldNode = getElement(field.getType());
            }
            if (m_cfg.useIntermediate(field)) {
                String valName = m_cfg.getStrings().getValueEdge();
                AbsNode interNode = new AbsNode(m_cfg.getName(field));
                interNode.addName("edge:\"" + field.getName() + "\"");
                String out = isNullable ? "out=0..1:" : "out=1:";
                /*AbsEdge valEdge = */new AbsEdge(interNode, fieldNode, out + valName);
                fieldNode = interNode;
            }
        }

        setElement(field, fieldNode);
    }

    @Override
    public void visit(DataType dt, java.lang.Object param) {
        if (hasElement(dt)) {
            return;
        }

        if (dt instanceof CustomDataType) {
            String valueName = m_cfg.getStrings().getDataValue();
            AbsNode dataNode = new AbsNode(m_cfg.getName(dt), "string:" + valueName);
            setElement(dt, dataNode);
        } else {
            AbsNode typeNode = new AbsNode(m_cfg.getName(dt));
            setElement(dt, typeNode);
        }
    }

    @Override
    public void visit(Enum e, java.lang.Object param) {
        if (hasElement(e)) {
            return;
        }

        if (m_cfg.getConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = m_cfg.getConfig().getGlobal().getIdSeparator();
            AbsNode enumNode = new AbsNode(m_cfg.getName(e), "abs:");
            setElement(e, enumNode);

            for (Name n : e.getLiterals()) {
                //String litName = m_cfg.getName(e) + sep + n.toString();
                String litName = "type:" + m_cfg.idToName(e.getId()) + sep + n.toString();
                AbsNode valNode = new AbsNode(litName);
                /*AbsEdge valEdge = */new AbsEdge(valNode, enumNode, "sub:");
            }
        } else {
            AbsNode enumNode = new AbsNode(m_cfg.getName(e));
            setElement(e, enumNode);

            for (Name n : e.getLiterals()) {
                enumNode.addName("flag:" + n.toString());
            }
        }

        return;
    }

    @Override
    public void visit(Container c, java.lang.Object param) {
        if (hasElement(c)) {
            return;
        }

        if (param == null || !(param instanceof String)) {
            throw new IllegalArgumentException("Container visitor requires String argument");
        }
        String containerId = (String) param;

        AbsNode typeNode = null;
        if (!(c.getType() instanceof Container)) {
            typeNode = getElement(c.getType());
        } else {
            typeNode = getElement(c.getType(), m_cfg.getContainerName(containerId, c));
        }

        boolean useIndex = m_cfg.useIndex(c);
        boolean indexValue = (m_cfg.getConfig().getTypeModel().getFields().getContainers().getOrdering().getType() == OrderType.INDEX);

        AbsNode containerNode = null;
        if (m_cfg.useIntermediate(c)) {
            containerNode = new AbsNode(containerId + m_cfg.getContainerPostfix(c));

            // Use just the last part of the container id as the edge name
            int lastIndex = containerId.lastIndexOf(m_cfg.getConfig().getGlobal().getIdSeparator());
            String edgeName = containerId;
            if (lastIndex != -1) {
                edgeName = containerId.substring(lastIndex + 1);
            }

            if (useIndex && indexValue) {
                String indexName = m_cfg.getStrings().getIndexEdge();
                containerNode.addName("edge:\"" + edgeName + " %s\"," + indexName);
            } else {
                containerNode.addName("edge:\"" + edgeName + "\"");
            }

            // If subtype is another container, allow more nodes. Otherwise, just one
            String valName = /*"in=1:" + */m_cfg.getStrings().getValueEdge();
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
                String indexName = m_cfg.getStrings().getIndexEdge();
                containerNode.addName("out=1:int:" + indexName);
            } else {
                String nextName = m_cfg.getStrings().getNextEdge();
                /*AbsEdge nextEdge = */new AbsEdge(containerNode, containerNode, "out=0..1:" + nextName);

                if (m_cfg.getConfig().getTypeModel().getFields().getContainers().getOrdering().isUsePrevEdge()) {
                    String prevName = m_cfg.getStrings().getPrevEdge();
                    new AbsEdge(containerNode, containerNode, "out=0..1:" + prevName);
                }
            }
        }

        setElement(c, containerNode);

        return;
    }

    @Override
    public void visit(Tuple tuple, java.lang.Object param) {
        if (hasElement(tuple)) {
            return;
        }

        //TODO: Nodified edge style might suit tuple better
        AbsNode tupleNode = new AbsNode(m_cfg.getName(tuple));
        setElement(tuple, tupleNode);

        int index = 1;
        for (Type t : tuple.getTypes()) {
            AbsNode typeNode = getElement(t);
            /*AbsEdge elemEdge = */new AbsEdge(tupleNode, typeNode, "_" + index++);
        }

        return;
    }

    @Override
    public void visit(Object object, java.lang.Object param) {
        if (hasElement(object)) {
            return;
        }
        if (object != Object.NIL) {
            throw new IllegalArgumentException("Cannot create object node in type model");
        }

        String name = m_cfg.getStrings().getNilName();
        AbsNode nilNode = new AbsNode("type:" + name);
        setElement(object, nilNode);
    }

    @Override
    public void visit(AbstractProperty abstractProperty, java.lang.Object param) {
        if (propertyVisited(abstractProperty)) {
            return;
        }
        setPropertyVisited(abstractProperty);

        if (!m_cfg.getConfig().getTypeModel().getProperties().isUseAbstract()) {
            return;
        }

        AbsNode classNode = getElement(abstractProperty.getAbstractClass().getProperClass());
        classNode.addName("abs:");
    }

    @Override
    public void visit(ContainmentProperty containmentProperty, java.lang.Object param) {
        if (propertyVisited(containmentProperty)) {
            return;
        }
        setPropertyVisited(containmentProperty);

        if (!m_cfg.getConfig().getTypeModel().getProperties().isUseContainment()) {
            return;
        }

        // Add containment to field edge
        String edgeName = containmentProperty.getField().getName().toString();
        AbsNode containmentNode = getElement(containmentProperty.getContainerClass());
        for (AbsEdge edge : containmentNode.getEdges()) {
            if (edge.getName().endsWith(edgeName)) {
                edge.setName("part:" + edge.getName());
            }
        }

        // Add to intermediate node as well if required
        if (m_cfg.useIntermediate(containmentProperty.getField())) {
            edgeName = m_cfg.getStrings().getValueEdge();
            containmentNode = getElement(containmentProperty.getField());

            for (AbsEdge edge : containmentNode.getEdges()) {
                if (edge.getName().endsWith(edgeName)) {
                    edge.setName("part:" + edge.getName());
                }
            }
        }

    }

    @Override
    public void visit(IdentityProperty identityProperty, java.lang.Object param) {
        if (propertyVisited(identityProperty)) {
            return;
        }
        setPropertyVisited(identityProperty);

        // Nothing to do for type graph
    }

    @Override
    public void visit(KeysetProperty keysetProperty, java.lang.Object param) {
        if (propertyVisited(keysetProperty)) {
            return;
        }
        setPropertyVisited(keysetProperty);

        // Nothing to do for type graph
    }

    @Override
    // Called twice for each opposite pair, opposite has a reverse
    // So only handle a single direction
    public void visit(OppositeProperty oppositeProperty, java.lang.Object param) {
        if (propertyVisited(oppositeProperty)) {
            return;
        }
        setPropertyVisited(oppositeProperty);

        if (!m_cfg.getConfig().getTypeModel().getProperties().isUseOpposite()) {
            return;
        }

        boolean useOpposites = m_cfg.getConfig().getTypeModel().getFields().isOpposites();
        if (!useOpposites) {
            return;
        }

        //TODO: possible for self referential opposites, make sure the field nodes are split in that case
        AbsNode class1Node = getElement(oppositeProperty.getClass1());
        AbsNode class2Node = getElement(oppositeProperty.getClass2());

        AbsNode field1Node = getElement(oppositeProperty.getField1());
        AbsNode field2Node = getElement(oppositeProperty.getField2());

        AbsNode source = m_cfg.useIntermediate(oppositeProperty.getField1()) ? field1Node : class1Node;
        AbsNode target = m_cfg.useIntermediate(oppositeProperty.getField2()) ? field2Node : class2Node;

        String oppositeName = m_cfg.getStrings().getOppositeEdge();
        /*AbsEdge oppositeEdge = */new AbsEdge(source, target, "out=1:" + oppositeName);
    }

    @Override
    public void visit(DefaultValueProperty defaultValueProperty, java.lang.Object param) {
        if (propertyVisited(defaultValueProperty)) {
            return;
        }
        setPropertyVisited(defaultValueProperty);

        // Nothing to do for type graph
    }
}
