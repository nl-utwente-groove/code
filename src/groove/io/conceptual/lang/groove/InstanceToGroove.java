package groove.io.conceptual.lang.groove;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Field;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.Triple;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.InstanceExporter;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.conceptual.value.Value;
import groove.io.external.PortException;
import groove.util.parse.IdValidator;

//separate different graphs for various elements where applicable.
//TODO: add translate messages here as well?
@SuppressWarnings("javadoc")
public class InstanceToGroove extends InstanceExporter<java.lang.Object> {
    private GrammarGraph m_currentGraph;
    private GrooveResource m_grooveResource;
    private Config m_cfg;
    private TypeModel m_currentTypeModel;

    // Used to find all opposite properties
    private Collection<Property> m_properties;
    // This is used to generate opposite edges
    private Map<Triple<Object,Field,Object>,AbsNode> m_objectNodes =
        new HashMap<>();

    public InstanceToGroove(GrooveResource grooveResource) {
        this.m_grooveResource = grooveResource;
        this.m_cfg = this.m_grooveResource.getConfig();
    }

    @Override
    public void addInstanceModel(InstanceModel instanceModel) throws PortException {
        int timer = Timer.start("IM to GROOVE");
        this.m_properties = instanceModel.getTypeModel()
            .getProperties();
        this.m_currentGraph =
            this.m_grooveResource.getGraph(instanceModel.getQualName(), GraphRole.HOST);

        this.m_currentTypeModel = instanceModel.getTypeModel();
        visitInstanceModel(instanceModel, this.m_cfg);

        // Prefetch? Uncomment for more accurate timings
        this.m_currentGraph.getGraph()
            .toAspectGraph();

        Timer.stop(timer);
    }

    @Override
    //Override for opposites
    protected void visitInstanceModel(InstanceModel instanceModel) {
        // Cache all nodes
        for (Object o : instanceModel.getObjects()) {
            getElement(o);
        }

        addOpposites();
    }

    // Do not actually override super methods directly,
    // because of difference between multiple and one element

    private void setElement(Acceptor o, AbsNode n) {
        this.m_currentGraph.m_nodes.put(o, n);
        super.setElement(o, n);
    }

    private void setElements(Acceptor o, AbsNode[] n) {
        this.m_currentGraph.m_multiNodes.put(o, n);
        super.setElement(o, n);
    }

    private AbsNode getNode(Acceptor o) {
        return getNode(o, null);
    }

    private AbsNode getNode(Acceptor o, String param) {
        return (AbsNode) super.getElement(o, param);
    }

    private AbsNode[] getNodes(Acceptor o, String param) {
        return (AbsNode[]) super.getElement(o, param);
    }

    @Override
    public void visit(Object object, String param) {
        if (hasElement(object)) {
            return;
        }

        if (object == Object.NIL) {
            if (this.m_cfg.getConfig()
                .getGlobal()
                .getNullable() != NullableType.NONE) {
                String name = this.m_cfg.getStrings()
                    .getNilName();
                AbsNode nilNode = new AbsNode("type:" + name);
                setElement(object, nilNode);
            } else {
                setElement(object, null);
            }
            return;
        }

        AbsNode objectNode = new AbsNode(this.m_cfg.getName(object.getType()));
        if (this.m_cfg.getConfig()
            .getInstanceModel()
            .getObjects()
            .isUseIdentifier() && object.getName() != null) {
            objectNode.addName("id:" + IdValidator.JAVA_ID.repair(object.getName()));
        }
        setElement(object, objectNode);

        // Set default values for those fields not set in the object
        Set<Field> defaultFields = new HashSet<>();
        if (this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getDefaults()
            .isSetValue()) {
            for (Property p : this.m_currentTypeModel.getProperties()) {
                if (p instanceof DefaultValueProperty) {
                    DefaultValueProperty dp = (DefaultValueProperty) p;
                    if (((Class) object.getType()).getAllSuperClasses()
                        .contains(dp.getField()
                            .getDefiningClass())) {
                        if (!object.getValue()
                            .containsKey(dp.getField())) {
                            object.setFieldValue(dp.getField(), dp.getDefaultValue());
                            defaultFields.add(dp.getField());
                        }
                    }
                }
            }
        }

        for (Entry<Field,Value> fieldEntry : object.getValue()
            .entrySet()) {
            Field f = fieldEntry.getKey();
            Value v = fieldEntry.getValue();
            assert(v != null);

            if (v == Object.NIL && this.m_cfg.getConfig()
                .getGlobal()
                .getNullable() == NullableType.NONE) {
                continue;
            }

            if (f.getType() instanceof Container) {
                AbsNode valNodes[] = getNodes(v, this.m_cfg.getName(f));
                ContainerValue cv = (ContainerValue) v;
                int i = 0;
                for (AbsNode valNode : valNodes) {
                    /*AbsEdge valEdge = */new AbsEdge(objectNode, valNode, f.getName()
                        .toString());
                    if (cv.getValue()
                        .get(i) instanceof Object) {
                        this.m_objectNodes
                            .put(new Triple<>(object, f, (Object) cv.getValue()
                                .get(i)), valNode);
                    }
                    i++;
                }
            } else {
                AbsNode valNode = getNode(v);
                if (this.m_cfg.useIntermediate(f)) {
                    String valName = this.m_cfg.getStrings()
                        .getValueEdge();
                    AbsNode interNode = new AbsNode(this.m_cfg.getName(f));
                    /*AbsEdge valEdge = */new AbsEdge(interNode, valNode, valName);
                    valNode = interNode;
                }

                if (v instanceof Object) {
                    this.m_objectNodes.put(new Triple<>(object, f, (Object) v),
                        valNode);
                }

                /*AbsEdge valEdge = */new AbsEdge(objectNode, valNode, f.getName()
                    .toString());
            }
        }
        // Clear previously set default values so model is not changed by import
        if (this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getDefaults()
            .isSetValue()) {
            for (Field f : defaultFields) {
                object.getValue()
                    .remove(f);
            }
        }

        return;
    }

    // Generates opposite edges
    private void addOpposites() {
        if (!this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .isOpposites()) {
            return;
        }

        String oppositeName = this.m_cfg.getStrings()
            .getOppositeEdge();

        for (Entry<Triple<Object,Field,Object>,AbsNode> tripleEntry : this.m_objectNodes
            .entrySet()) {
            Triple<Object,Field,Object> triple = tripleEntry.getKey();
            Field f = triple.getMiddle();
            for (Property p : this.m_properties) {
                if (p instanceof OppositeProperty) {
                    OppositeProperty op = (OppositeProperty) p;
                    if (op.getField1() == f) {

                        Triple<Object,Field,Object> opTriple = new Triple<>(
                            triple.getRight(), op.getField2(), triple.getLeft());
                        if (!this.m_objectNodes.containsKey(opTriple)) {
                            continue;
                        }

                        new AbsEdge(tripleEntry.getValue(), this.m_objectNodes.get(opTriple),
                            oppositeName);
                    }
                }
            }
        }
    }

    @Override
    public void visit(RealValue realval, String param) {
        if (hasElement(realval)) {
            return;
        }

        AbsNode realNode = new AbsNode("real:" + realval.getValue());
        setElement(realval, realNode);

        return;
    }

    @Override
    public void visit(StringValue stringval, String param) {
        if (hasElement(stringval)) {
            return;
        }

        AbsNode stringNode = new AbsNode("string:\"" + stringval.toEscapedString() + "\"");
        setElement(stringval, stringNode);

        return;
    }

    @Override
    public void visit(IntValue intval, String param) {
        if (hasElement(intval)) {
            return;
        }

        AbsNode intNode = new AbsNode("int:" + intval.getValue());
        setElement(intval, intNode);

        return;
    }

    @Override
    public void visit(BoolValue boolval, String param) {
        if (hasElement(boolval)) {
            return;
        }

        AbsNode boolNode = new AbsNode("bool:" + boolval.getValue());
        setElement(boolval, boolNode);

        return;
    }

    @Override
    public void visit(EnumValue enumval, String param) {
        if (hasElement(enumval)) {
            return;
        }

        if (this.m_cfg.getConfig()
            .getTypeModel()
            .getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getConfig()
                .getGlobal()
                .getIdSeparator();
            String litName = "type:" + this.m_cfg.idToName(((Enum) enumval.getType()).getId()) + sep
                + enumval.getValue();
            AbsNode enumNode = new AbsNode(litName);
            setElement(enumval, enumNode);
        } else {
            AbsNode enumNode = new AbsNode(this.m_cfg.getName(enumval.getType()));
            enumNode.addName("flag:" + enumval.getValue()
                .toString());
            setElement(enumval, enumNode);
        }

        return;
    }

    @Override
    public void visit(CustomDataValue dataval, String param) {
        if (hasElement(dataval)) {
            return;
        }

        String valueName = this.m_cfg.getStrings()
            .getDataValue();
        AbsNode dataNode = new AbsNode(this.m_cfg.getName(dataval.getType()),
            "let:" + valueName + "=string:\"" + dataval.getValue() + "\"");
        setElement(dataval, dataNode);

    }

    @Override
    public void visit(ContainerValue containerVal, String param) {
        if (hasElement(containerVal)) {
            return;
        }

        if (param == null) {
            throw new IllegalArgumentException("Container value visitor requires String argument");
        }
        String containerId = param;

        Container containerType = (Container) containerVal.getType();

        boolean useIntermediate = this.m_cfg.useIntermediate(containerType);
        boolean subContainer = containerType.getType() instanceof Container;

        boolean useIndex = this.m_cfg.useIndex(containerType);
        boolean useEdge = this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getContainers()
            .getOrdering()
            .getType() == OrderType.EDGE;

        AbsNode[] containerNodes = new AbsNode[containerVal.getValue()
            .size()]; //actual nodes to represent this container
        int i = 0;
        int index = 1;
        AbsNode prevValNode = null;
        for (Value subValue : containerVal.getValue()) {
            // No not include Nil if not used (shouldn't have to happen anyway, Nil in container is bad
            if (subValue == Object.NIL && this.m_cfg.getConfig()
                .getGlobal()
                .getNullable() == NullableType.NONE) {
                continue;
            }
            AbsNode valueNode = null;
            String valName = this.m_cfg.getStrings()
                .getValueEdge();
            if (!useIntermediate) {
                // subContainer ought to be false too
                AbsNode subNode = getNode(subValue);
                valueNode = subNode;
            } else {
                AbsNode intermediateNode =
                    new AbsNode(containerId + this.m_cfg.getContainerPostfix(containerType));
                if (subContainer) {
                    ContainerValue cVal = (ContainerValue) subValue;
                    AbsNode subNodes[] = getNodes(cVal,
                        this.m_cfg.getContainerName(containerId, (Container) cVal.getType()));
                    for (AbsNode subNode : subNodes) {
                        /*AbsEdge intermediateEdge = */new AbsEdge(intermediateNode, subNode,
                            valName);
                    }
                } else {
                    AbsNode subNode = getNode(subValue);
                    /*AbsEdge intermediateEdge = */new AbsEdge(intermediateNode, subNode, valName);
                }
                valueNode = intermediateNode;
            }

            if (useIndex) {
                if (useEdge) {
                    if (prevValNode != null) {
                        String nextName = this.m_cfg.getStrings()
                            .getNextEdge();
                        /*AbsEdge nextEdge = */new AbsEdge(prevValNode, valueNode, nextName);
                        if (this.m_cfg.getConfig()
                            .getTypeModel()
                            .getFields()
                            .getContainers()
                            .getOrdering()
                            .isUsePrevEdge()) {
                            String prevName = this.m_cfg.getStrings()
                                .getPrevEdge();
                            new AbsEdge(valueNode, prevValNode, prevName);
                        }
                    }
                    prevValNode = valueNode;
                } else {
                    String indexName = this.m_cfg.getStrings()
                        .getIndexEdge();
                    valueNode.addName("let:" + indexName + "=" + index);
                    index++;
                }
            }

            containerNodes[i] = valueNode;
            i++;
        }

        // Set the intermediate nodes as the node values
        setElements(containerVal, containerNodes);

        return;
    }

    @Override
    public void visit(TupleValue tupleval, String param) {
        if (hasElement(tupleval)) {
            return;
        }

        Tuple tup = (Tuple) tupleval.getType();
        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tup));
        setElement(tupleval, tupleNode);

        for (Integer i : tupleval.getValue()
            .keySet()) {
            Value v = tupleval.getValue()
                .get(i);
            AbsNode valNode = getNode(v);
            if (valNode == null) {
                // Happens if Nil value and not using nullable classes
                continue;
            }
            /*AbsEdge valEdge = */new AbsEdge(tupleNode, valNode, "_" + i);
        }

        return;
    }
}
