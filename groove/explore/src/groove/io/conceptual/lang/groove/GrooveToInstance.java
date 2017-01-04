package groove.io.conceptual.lang.groove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.aspect.GraphConverter.HostToAspectMap;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostEdgeStore;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;
import groove.graph.EdgeRole;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.lang.InstanceImporter;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.groove.GraphNodeTypes.ModelType;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
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

/**
 * Converts a Groove {@link HostGraph} to a conceptual model instance.
 */
public class GrooveToInstance extends InstanceImporter {
    private GraphNodeTypes m_types;
    private Config m_cfg;

    private TypeModel m_typeModel;

    private Map<HostNode,Object> m_objectNodes = new HashMap<>();
    private Map<HostNode,Value> m_nodeValues = new HashMap<>();
    private HostEdgeStore<HostNode> m_nodeEdges = new HostEdgeStore<>();

    private int m_nodeCounter = 1;

    /**
     * Creates instance models from a collection of host models
     * @param hostGraph HostGraph to import
     * @param types As filled by TypeGraphVisitor (when used without error)
     * @param typeModel TypeModel for the generated InstanceModel
     */
    public GrooveToInstance(HostGraph hostGraph, GraphNodeTypes types, Config cfg,
        TypeModel typeModel) {
        this.m_types = types;
        this.m_cfg = cfg;

        this.m_typeModel = typeModel;

        int timer = Timer.start("GROOVE to IM");
        buildInstanceModel(hostGraph);
        Timer.stop(timer);
    }

    private void buildInstanceModel(HostGraph hostGraph) {
        InstanceModel instanceModel =
            new InstanceModel(this.m_typeModel, QualName.parse(hostGraph.getName()));
        this.m_cfg.setTypeModel(this.m_typeModel);

        // Map nodes to edges
        for (HostNode n : hostGraph.nodeSet()) {
            this.m_nodeEdges.addKey(n);
        }
        for (HostEdge e : hostGraph.edgeSet()) {
            this.m_nodeEdges.get(e.source())
                .add(e);
        }

        // Set of Nodes that need to be walked through
        HostNodeSet unvisitedNodes = new HostNodeSet(hostGraph.nodeSet());

        // Some trickery is required to obtain the ID of a node
        // Find original aspect node and obtain ID from that
        HostToAspectMap map = GraphConverter.toAspectMap(hostGraph);
        // Find all class instances
        for (HostNode node : hostGraph.nodeSet()) {
            AspectNode aspectNode = map.getNode(node);

            Type t = getNodeType(node);
            if (t instanceof Class) {
                unvisitedNodes.remove(node);
                //Object nodeObj = new Object((Class) t, Name.getName(getNodeName(node)));
                Object nodeObj = new Object((Class) t, Name.getName(getNodeName(aspectNode)));
                this.m_objectNodes.put(node, nodeObj);
                instanceModel.addObject(nodeObj);
            } else {
                // Ignore
            }
        }

        // Find all attributes/references
        for (Entry<HostNode,Object> entry : this.m_objectNodes.entrySet()) {
            // Run through all the fields (this creates duplicate work for edges, but meh)
            for (Field field : ((Class) entry.getValue()
                .getType()).getAllFields()) {
                String fieldName = field.getName()
                    .toString();
                Value fieldValue = null;
                if (field.getType() instanceof Container) {
                    if (!this.m_cfg.useIntermediate(field)) {
                        fieldValue = getFieldContainerValue(entry.getKey(),
                            fieldName,
                            (Container) field.getType());
                    } else {
                        fieldValue = getContainerValue(entry.getKey(), fieldName);
                    }
                } else {
                    fieldValue = getNodeValue(getEdgeNode(entry.getKey(), fieldName));

                }
                if (fieldValue != null) {
                    entry.getValue()
                        .setFieldValue(field, fieldValue);
                } else {
                    addMessage(new Message("Cannot obtain value for field " + field.getName(),
                        MessageType.WARNING));
                }
            }
        }

        // And we're done
        addInstanceModel(instanceModel);
    }

    private Type getNodeType(HostNode node) {
        String label = node.getType()
            .label()
            .text();
        return this.m_types.getType(label);
    }

    private String getNodeName(AspectNode node) {
        if (this.m_cfg.getConfig()
            .getInstanceModel()
            .getObjects()
            .isUseIdentifier() && node.getId() != null) {
            return node.getId()
                .getContentString();
        } else {
            return "node" + this.m_nodeCounter++;
        }
    }

    // next edges: Returns INT_MAX - (0 for end, index for next node + 1)
    // index value: Returns value of index attr
    // Integer.MIN_VALUE on error
    private int getNodeIndex(HostNode node) {
        OrderType orderType = this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getContainers()
            .getOrdering()
            .getType();
        if (orderType == OrderType.INDEX) {
            String indexName = this.m_cfg.getStrings()
                .getIndexEdge();
            HostNode indexNode = getEdgeNode(node, indexName);
            if (indexNode == null) {
                return Integer.MIN_VALUE;
            } else {
                ValueNode valNode = (ValueNode) indexNode;
                return (Integer) valNode.toJavaValue();
            }
        } else if (orderType == OrderType.EDGE) {
            String nextName = this.m_cfg.getStrings()
                .getNextEdge();
            HostNode nextNode = getEdgeNode(node, nextName);
            if (nextNode == null) {
                return Integer.MAX_VALUE;
            } else {
                return getNodeIndex(nextNode) - 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    // TODO: check for cycles!
    // TODO: groove was refactored to use Constant class as opposed to direct values. How will this work with state exploration exports?
    private Value getNodeValue(HostNode node) {
        // Might be some intermediate node
        if (this.m_types.getModelType(node.getType()
            .label()
            .text()) == ModelType.TypeIntermediate) {
            String valueEdge = this.m_cfg.getStrings()
                .getValueEdge();
            Value resultValue = getNodeValue(getEdgeNode(node, valueEdge));
            this.m_nodeValues.put(node, resultValue);
            return resultValue;
        }
        Type nodeType = getNodeType(node);

        if (nodeType == null) {
            // ERROR
            return null;
        }

        Value resultValue = null;

        if (nodeType instanceof Class) {
            resultValue = this.m_objectNodes.get(node);
        }
        // Enum type
        else if (nodeType instanceof Enum) {
            Enum e = (Enum) nodeType;
            if (this.m_cfg.getConfig()
                .getTypeModel()
                .getEnumMode() == EnumModeType.NODE) {
                Id id = this.m_cfg.nameToId(node.getType()
                    .label()
                    .text());
                EnumValue ev = new EnumValue(e, id.getName());
                resultValue = ev;
            } else {
                HostEdgeSet edges = this.m_nodeEdges.get(node);
                for (HostEdge enumEdge : edges) {
                    if (enumEdge.getType()
                        .getRole() == EdgeRole.FLAG) {
                        EnumValue ev = new EnumValue(e, Name.getName(enumEdge.label()
                            .text()));
                        resultValue = ev;
                        break;
                    }
                }
            }
        }
        // Custom data type
        else if (nodeType instanceof CustomDataType) {
            CustomDataType cdt = (CustomDataType) nodeType;
            String dataValueName = this.m_cfg.getStrings()
                .getDataValue();
            HostNode valueNode = getEdgeNode(node, dataValueName);
            String valueString = (((ValueNode) valueNode).getValue()
                .toString());
            CustomDataValue dv = new CustomDataValue(cdt, valueString);
            resultValue = dv;
        }
        // Containers & tuples
        else if (nodeType instanceof Container) {
            Container ct = (Container) nodeType;
            ContainerValue cv = new ContainerValue(ct);
            String valueEdge = this.m_cfg.getStrings()
                .getValueEdge();
            SortedMap<Integer,Value> containerValues = new TreeMap<>();
            for (HostEdge e : this.m_nodeEdges.get(node)) {
                if (e.label()
                    .text()
                    .equals(valueEdge)) {
                    Value subVal = getNodeValue(e.target());
                    int index = 0;
                    if (ct.getContainerType() == Kind.ORD || ct.getContainerType() == Kind.SEQ) {
                        index = getNodeIndex(e.target());
                    }
                    containerValues.put(index, subVal);
                }
            }
            for (Value subVal : containerValues.values()) {
                cv.addValue(subVal);
            }
            resultValue = cv;
        } else if (nodeType instanceof Tuple) {
            Tuple tup = (Tuple) nodeType;
            TupleValue tv = new TupleValue(tup);
            for (int i = 0; i < tup.getTypes()
                .size(); i++) {
                HostNode subValNode = getEdgeNode(node, "_" + (i + 1));
                if (getNodeType(subValNode) instanceof Container) {
                    Value subVal = getContainerValue(node, "_" + (i + 1));
                    tv.setValue(i + 1, subVal);
                } else {
                    Value subVal = getNodeValue(subValNode);
                    tv.setValue(i + 1, subVal);
                }
            }
            resultValue = tv;
        }
        // (Other) data types
        else if (nodeType instanceof BoolType) {
            ValueNode valNode = (ValueNode) node;
            Boolean value = (Boolean) valNode.toJavaValue();
            resultValue = BoolValue.getInstance(value);
        } else if (nodeType instanceof IntType) {
            ValueNode valNode = (ValueNode) node;
            Integer value = (Integer) valNode.toJavaValue();
            resultValue = new IntValue(value);
        } else if (nodeType instanceof RealType) {
            ValueNode valNode = (ValueNode) node;
            Double value = (Double) valNode.toJavaValue();
            resultValue = new RealValue(value);
        } else if (nodeType instanceof StringType) {
            ValueNode valNode = (ValueNode) node;
            String value = (String) valNode.toJavaValue();
            resultValue = new StringValue(value);
        }
        this.m_nodeValues.put(node, resultValue);

        return resultValue;
    }

    private HostNode getEdgeNode(HostNode node, String edge) {
        HostEdgeSet nodeEdges = this.m_nodeEdges.get(node);
        for (HostEdge e : nodeEdges) {
            if (e.label()
                .text()
                .equals(edge)) {
                return e.target();
            }
        }
        return null;
    }

    // For container field w/o intermediate
    private ContainerValue getFieldContainerValue(HostNode fieldNode, String fieldName,
        Container containerType) {
        Set<HostEdge> nodeEdges = new HashSet<>();
        for (HostEdge e : this.m_nodeEdges.get(fieldNode)) {
            if (e.label()
                .text()
                .equals(fieldName)) {
                nodeEdges.add(e);
            }
        }
        if (nodeEdges.size() == 0) {
            return null;
        }

        SortedMap<Integer,Value> containerValues = new TreeMap<>();
        for (HostEdge e : nodeEdges) {
            Value subVal = getNodeValue(e.target());
            int index = 0;
            if (containerType.getContainerType() == Kind.ORD
                || containerType.getContainerType() == Kind.SEQ) {
                index = getNodeIndex(e.target());
            }
            containerValues.put(index, subVal);
        }

        ContainerValue cv = new ContainerValue(containerType);
        for (Value subVal : containerValues.values()) {
            cv.addValue(subVal);
        }

        return cv;

    }

    // For intermediate nodes for containers
    private Value getContainerValue(HostNode node, String edgeName) {
        Set<HostEdge> nodeEdges = new HashSet<>();
        for (HostEdge e : this.m_nodeEdges.get(node)) {
            if (e.label()
                .text()
                .equals(edgeName)) {
                nodeEdges.add(e);
            }
        }
        if (nodeEdges.size() == 0) {
            return null;
        }

        Type nextType = getNodeType(nodeEdges.iterator()
            .next()
            .target());

        // Value is not a container value
        if (!(nextType instanceof Container)) {
            // Simply return the direct value if its just the one
            if (nodeEdges.size() == 1) {
                return getNodeValue(nodeEdges.iterator()
                    .next()
                    .target());
            }
            // multiple values: improvise and create container type by guessing
            nextType = new Container(Kind.SET, nextType);
        }

        ContainerValue cv = new ContainerValue((Container) nextType);
        String valueName = this.m_cfg.getStrings()
            .getValueEdge();

        SortedMap<Integer,Value> containerValues = new TreeMap<>();
        for (HostEdge e : nodeEdges) {
            Type checkType = getNodeType(e.target());
            if (!nextType.equals(checkType)) {
                // Inconsistent types between container values
                addMessage(new Message("Invalid container value, type " + nextType
                    + " does not correspond with type " + checkType));
                return null;
            } else {
                Value subVal = getContainerValue(e.target(), valueName);
                int index = 0;
                if (((Container) nextType).getContainerType() == Kind.ORD
                    || ((Container) nextType).getContainerType() == Kind.SEQ) {
                    index = getNodeIndex(e.target());
                }
                containerValues.put(index, subVal);
            }
        }

        for (Value subVal : containerValues.values()) {
            cv.addValue(subVal);
        }

        return cv;
    }
}
