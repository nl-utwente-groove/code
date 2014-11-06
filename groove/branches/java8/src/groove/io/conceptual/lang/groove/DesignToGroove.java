package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Design;
import groove.io.conceptual.Field;
import groove.io.conceptual.Timer;
import groove.io.conceptual.Triple;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.DesignExportBuilder;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//separate different graphs for various elements where applicable.
//TODO: add translate messages here as well?
public class DesignToGroove extends DesignExportBuilder<GrooveExport,java.lang.Object> {
    /** Creates bridge for a given design to a given GROOVE export object. */
    public DesignToGroove(GrooveExport export, Design design) {
        super(export, design);
        this.m_cfg = export.getConfig();
        this.m_currentGraph = export.getGraph(design.getName(), GraphRole.HOST);
    }

    private final GrammarGraph m_currentGraph;
    private final Config m_cfg;

    // This is used to generate opposite edges
    private final Map<Triple<Object,Field,Object>,AbsNode> m_objectNodes =
        new HashMap<Triple<Object,Field,Object>,AbsNode>();

    @Override
    public void build() throws PortException {
        int timer = Timer.start("IM to GROOVE");
        this.m_cfg.setGlossary(getGlossary());
        super.build();
        addOpposites();
        Timer.stop(timer);
    }

    // Generates opposite edges
    private void addOpposites() {
        if (!this.m_cfg.getXMLConfig().getTypeModel().getFields().isOpposites()) {
            return;
        }

        String oppositeName = this.m_cfg.getStrings().getOppositeEdge();

        for (Entry<Triple<Object,Field,Object>,AbsNode> tripleEntry : this.m_objectNodes.entrySet()) {
            Triple<Object,Field,Object> triple = tripleEntry.getKey();
            Field f = triple.getMiddle();
            for (Property p : getGlossary().getProperties()) {
                if (p instanceof OppositeProperty) {
                    OppositeProperty op = (OppositeProperty) p;
                    if (op.getField1() == f) {

                        Triple<Object,Field,Object> opTriple =
                            new Triple<Object,Field,Object>(triple.getRight(), op.getField2(),
                                triple.getLeft());
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
    public void addObject(Object object) {
        if (hasElement(object)) {
            return;
        }

        if (object == Object.NIL) {
            if (this.m_cfg.getXMLConfig().getGlobal().getNullable() != NullableType.NONE) {
                String name = this.m_cfg.getStrings().getNilName();
                AbsNode nilNode = new AbsNode("type:" + name);
                setElement(object, nilNode);
            } else {
                setElement(object, null);
            }
            return;
        }

        AbsNode objectNode = new AbsNode(this.m_cfg.getName(object.getType()));
        if (this.m_cfg.getXMLConfig().getInstanceModel().getObjects().isUseIdentifier()
            && object.getName() != null) {
            String name = object.getName();
            name = name.replaceAll("[^A-Za-z0-9_]", "_");
            if (name.matches("[0-9].*")) {
                name = "_" + name;
            }
            objectNode.addName("id:" + GrooveUtil.getSafeId(name));
        }
        setElement(object, objectNode);

        // Set default values for those fields not set in the object
        Set<Field> defaultFields = new HashSet<Field>();
        if (this.m_cfg.getXMLConfig().getTypeModel().getFields().getDefaults().isSetValue()) {
            for (Property p : getGlossary().getProperties()) {
                if (p instanceof DefaultValueProperty) {
                    DefaultValueProperty dp = (DefaultValueProperty) p;
                    if (((Class) object.getType()).getAllSuperClasses().contains(dp.getField()
                        .getDefiningClass())) {
                        if (!object.getValue().containsKey(dp.getField())) {
                            object.setFieldValue(dp.getField(), dp.getDefaultValue());
                            defaultFields.add(dp.getField());
                        }
                    }
                }
            }
        }

        for (Entry<Field,Value> fieldEntry : object.getValue().entrySet()) {
            Field f = fieldEntry.getKey();
            Value v = fieldEntry.getValue();
            assert (v != null);

            if (v == Object.NIL
                && this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
                continue;
            }

            if (f.getType() instanceof Container) {
                AbsNode valNodes[] = getNodes(v, this.m_cfg.getName(f));
                ContainerValue cv = (ContainerValue) v;
                int i = 0;
                for (AbsNode valNode : valNodes) {
                    /*AbsEdge valEdge = */new AbsEdge(objectNode, valNode, f.getName().toString());
                    if (cv.getValue().get(i) instanceof Object) {
                        this.m_objectNodes.put(new Triple<Object,Field,Object>(object, f,
                            (Object) cv.getValue().get(i)), valNode);
                    }
                    i++;
                }
            } else {
                AbsNode valNode = getNode(v);
                if (this.m_cfg.useIntermediate(f)) {
                    String valName = this.m_cfg.getStrings().getValueEdge();
                    AbsNode interNode = new AbsNode(this.m_cfg.getName(f));
                    /*AbsEdge valEdge = */new AbsEdge(interNode, valNode, valName);
                    valNode = interNode;
                }

                if (v instanceof Object) {
                    this.m_objectNodes.put(new Triple<Object,Field,Object>(object, f, (Object) v),
                        valNode);
                }

                /*AbsEdge valEdge = */new AbsEdge(objectNode, valNode, f.getName().toString());
            }
        }
        // Clear previously set default values so model is not changed by import
        if (this.m_cfg.getXMLConfig().getTypeModel().getFields().getDefaults().isSetValue()) {
            for (Field f : defaultFields) {
                object.getValue().remove(f);
            }
        }

        return;
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

    @Override
    public void addContainerValue(ContainerValue containerVal, String base) {
        if (hasElement(containerVal)) {
            return;
        }

        Container containerType = (Container) containerVal.getType();

        boolean useIntermediate = this.m_cfg.useIntermediate(containerType);
        boolean subContainer = containerType.getType() instanceof Container;

        boolean useIndex = this.m_cfg.useIndex(containerType);
        boolean useEdge =
            this.m_cfg.getXMLConfig()
                .getTypeModel()
                .getFields()
                .getContainers()
                .getOrdering()
                .getType() == OrderType.EDGE;

        AbsNode[] containerNodes = new AbsNode[containerVal.getValue().size()]; //actual nodes to represent this container
        int i = 0;
        int index = 1;
        AbsNode prevValNode = null;
        for (Value subValue : containerVal.getValue()) {
            // No not include Nil if not used (shouldn't have to happen anyway, Nil in container is bad
            if (subValue == Object.NIL
                && this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
                continue;
            }
            AbsNode valueNode = null;
            String valName = this.m_cfg.getStrings().getValueEdge();
            if (!useIntermediate) {
                // subContainer ought to be false too
                AbsNode subNode = getNode(subValue);
                valueNode = subNode;
            } else {
                AbsNode intermediateNode =
                    new AbsNode(base + this.m_cfg.getContainerPostfix(containerType));
                if (subContainer) {
                    ContainerValue cVal = (ContainerValue) subValue;
                    AbsNode subNodes[] =
                        getNodes(cVal,
                            this.m_cfg.getContainerName(base, (Container) cVal.getType()));
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
                        String nextName = this.m_cfg.getStrings().getNextEdge();
                        /*AbsEdge nextEdge = */new AbsEdge(prevValNode, valueNode, nextName);
                        if (this.m_cfg.getXMLConfig()
                            .getTypeModel()
                            .getFields()
                            .getContainers()
                            .getOrdering()
                            .isUsePrevEdge()) {
                            String prevName = this.m_cfg.getStrings().getPrevEdge();
                            new AbsEdge(valueNode, prevValNode, prevName);
                        }
                    }
                    prevValNode = valueNode;
                } else {
                    String indexName = this.m_cfg.getStrings().getIndexEdge();
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
    public void addTupleValue(TupleValue val) {
        if (hasElement(val)) {
            return;
        }

        Tuple tup = (Tuple) val.getType();
        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tup));
        setElement(val, tupleNode);

        for (Integer i : val.getValue().keySet()) {
            Value v = val.getValue().get(i);
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
