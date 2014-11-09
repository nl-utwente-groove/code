package groove.io.conceptual.lang.groove;

import static groove.io.conceptual.value.Object.NIL;
import groove.graph.GraphRole;
import groove.io.conceptual.Concept;
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
import groove.io.conceptual.graph.AbsNodeIter;
import groove.io.conceptual.graph.AbsNodeList;
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
/**
 * Bridge from design to GROOVE export.
 * @author rensink
 * @version $Revision $
 */
public class DesignToGroove extends DesignExportBuilder<GrooveExport,AbsNodeIter> {
    /** Creates bridge for a given design to a given GROOVE export object. */
    public DesignToGroove(Design design, GrooveExport export) {
        super(design, export);
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

    @Override
    protected boolean put(Concept o, AbsNodeIter e) {
        boolean result = super.put(o, e);
        if (result) {
            this.m_currentGraph.addNodes(e);
        }
        return result;
    }

    private AbsNode getNode(Concept o) {
        return (AbsNode) add(o);
    }

    private AbsNodeList getNodes(Concept o, String param) {
        return (AbsNodeList) add(o, param);
    }

    @Override
    protected AbsNodeIter addObject(groove.io.conceptual.value.Object object) {
        if (object == NIL) {
            AbsNode result = null;
            if (this.m_cfg.getXMLConfig().getGlobal().getNullable() != NullableType.NONE) {
                String name = this.m_cfg.getStrings().getNilName();
                result = new AbsNode("type:" + name);
            }
            put(object, result);
            return result;
        }

        AbsNode result = new AbsNode(this.m_cfg.getName(object.getType()));
        if (this.m_cfg.getXMLConfig().getInstanceModel().getObjects().isUseIdentifier()
            && object.getName() != null) {
            String name = object.getName();
            name = name.replaceAll("[^A-Za-z0-9_]", "_");
            if (name.matches("[0-9].*")) {
                name = "_" + name;
            }
            result.addName("id:" + GrooveUtil.getSafeId(name));
        }
        put(object, result);

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

            if (v == NIL
                && this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
                continue;
            }

            if (f.getType() instanceof Container) {
                AbsNodeList valNodes = getNodes(v, this.m_cfg.getName(f));
                ContainerValue cv = (ContainerValue) v;
                int i = 0;
                for (AbsNode valNode : valNodes) {
                    /*AbsEdge valEdge = */new AbsEdge(result, valNode, f.getName().toString());
                    if (cv.getValue().get(i) instanceof groove.io.conceptual.value.Object) {
                        this.m_objectNodes.put(new Triple<Object,Field,Object>(object, f,
                            cv.getValue().get(i)), valNode);
                    }
                    i++;
                }
            } else {
                AbsNode valNode = getNode(v);
                if (this.m_cfg.useIntermediate(f)) {
                    String valName = this.m_cfg.getStrings().getValueEdge();
                    AbsNode interNode = new AbsNode(this.m_cfg.getName(f));
                    new AbsEdge(interNode, valNode, valName);
                    valNode = interNode;
                }

                if (v instanceof groove.io.conceptual.value.Object) {
                    this.m_objectNodes.put(new Triple<Object,Field,Object>(object, f, v), valNode);
                }
                new AbsEdge(result, valNode, f.getName().toString());
            }
        }
        // Clear previously set default values so model is not changed by import
        if (this.m_cfg.getXMLConfig().getTypeModel().getFields().getDefaults().isSetValue()) {
            for (Field f : defaultFields) {
                object.getValue().remove(f);
            }
        }

        return result;
    }

    @Override
    protected AbsNodeIter addRealValue(RealValue realval) {
        AbsNode result = new AbsNode("real:" + realval.getValue());
        put(realval, result);
        return result;
    }

    @Override
    protected AbsNodeIter addStringValue(StringValue stringval) {
        AbsNode result = new AbsNode("string:\"" + stringval.toEscapedString() + "\"");
        put(stringval, result);
        return result;
    }

    @Override
    protected AbsNodeIter addIntValue(IntValue val) {
        AbsNode intNode = new AbsNode("int:" + val.getValue());
        put(val, intNode);
        return intNode;
    }

    @Override
    protected AbsNodeIter addBoolValue(BoolValue val) {
        AbsNode result = new AbsNode("bool:" + val.getValue());
        put(val, result);
        return result;
    }

    @Override
    protected AbsNodeIter addEnumValue(EnumValue val) {
        AbsNode result;
        if (this.m_cfg.getXMLConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getXMLConfig().getGlobal().getIdSeparator();
            String litName =
                "type:" + this.m_cfg.idToName(((Enum) val.getType()).getId()) + sep
                    + val.getValue();
            result = new AbsNode(litName);
        } else {
            result = new AbsNode(this.m_cfg.getName(val.getType()));
            result.addName("flag:" + val.getValue().toString());
        }
        put(val, result);
        return result;
    }

    @Override
    protected AbsNodeIter addCustomDataValue(CustomDataValue val) {
        String valueName = this.m_cfg.getStrings().getDataValue();
        AbsNode result =
            new AbsNode(this.m_cfg.getName(val.getType()), "let:" + valueName + "=string:\""
                + val.getValue() + "\"");
        put(val, result);
        return result;
    }

    @Override
    protected AbsNodeIter addContainerValue(ContainerValue val, String base) {
        Container containerType = (Container) val.getType();

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

        AbsNodeList result = new AbsNodeList(); //actual nodes to represent this container
        // Set the intermediate nodes as the node values
        put(val, result);

        int index = 1;
        AbsNode prevValNode = null;
        for (Value subValue : val.getValue()) {
            // No not include Nil if not used (shouldn't have to happen anyway, Nil in container is bad
            if (subValue == NIL
                && this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
                continue;
            }
            AbsNode valueNode = null;
            String valName = this.m_cfg.getStrings().getValueEdge();
            if (!useIntermediate) {
                // subContainer ought to be false too
                valueNode = getNode(subValue);
            } else {
                AbsNode intermediateNode =
                    new AbsNode(base + this.m_cfg.getContainerPostfix(containerType));
                if (subContainer) {
                    ContainerValue cVal = (ContainerValue) subValue;
                    AbsNodeList subNodes =
                        getNodes(cVal,
                            this.m_cfg.getContainerName(base, (Container) cVal.getType()));
                    for (AbsNode subNode : subNodes) {
                        new AbsEdge(intermediateNode, subNode, valName);
                    }
                } else {
                    AbsNode subNode = getNode(subValue);
                    new AbsEdge(intermediateNode, subNode, valName);
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

            result.add(valueNode);
        }

        return result;
    }

    @Override
    protected AbsNodeIter addTupleValue(TupleValue val) {
        Tuple tup = (Tuple) val.getType();
        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tup));
        put(val, tupleNode);

        for (Integer i : val.getValue().keySet()) {
            Value v = val.getValue().get(i);
            AbsNode valNode = getNode(v);
            if (valNode == null) {
                // Happens if Nil value and not using nullable classes
                continue;
            }
            /*AbsEdge valEdge = */new AbsEdge(tupleNode, valNode, "_" + i);
        }

        return tupleNode;
    }
}
