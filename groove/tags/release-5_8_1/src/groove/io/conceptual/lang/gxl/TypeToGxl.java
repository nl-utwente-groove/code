/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.io.conceptual.lang.gxl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.TupType;
import groove.grammar.QualName;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Identifiable;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.TypeExporter;
import groove.io.conceptual.lang.gxl.GxlUtil.AttrTypeEnum;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
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
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.conceptual.value.Value;
import groove.io.external.PortException;

//Thing to note here: Instance graphs are referred to by their ID, since they dont have a name attribute.
//Type graphs are referred to by the ID (often coinciding with the name) of the GraphClass node. The actual ID
// of the graph in which this node is contained is ignored. This also means one type graph graph can be used
//in one GXL document , but multiple GraphClass nodes are allowed (each resulting in a TypeModel)
@SuppressWarnings("javadoc")
public class TypeToGxl extends TypeExporter<NodeType> {

    private GxlResource m_gxlResource;

    // Keep track of graph to add nodes to
    private GraphType m_typeGraph;

    // Packages are mapped to subgraphs in instance models
    private Map<Id,NodeType> m_packageNodes = new HashMap<>();
    private Map<Id,NodeType> m_packageIntermediateNodes = new HashMap<>();
    private QualName m_currentTypeName;

    private Map<java.lang.Object,String> m_objectIDs = new HashMap<>();

    // Some tuples must be represented by classes, this map keeps track of them
    private Map<Tuple,Class> m_tupleClasses = new HashMap<>();

    // To keep track of node Ids
    private int m_nextType = 1;
    private int m_nextEdge = 1;
    private int m_nextValue = 1;

    public TypeToGxl(GxlResource gxlResource) {
        this.m_gxlResource = gxlResource;
    }

    @Override
    public void addTypeModel(TypeModel typeModel) throws PortException {
        //m_idMap = typeModel.getShortIds();

        // If no typegraph yet, create one, this also creates a new GraphClass node inside it. Otherwise, insert a new GraphClass node
        if (this.m_typeGraph == null) {
            this.m_typeGraph = this.m_gxlResource.getTypeGraph(typeModel.getQualName()
                .toString());
        }

        int timer = Timer.start("TM to GXL");
        this.m_currentTypeName = typeModel.getQualName();
        visitTypeModel(typeModel);
        Timer.stop(timer);
    }

    @Override
    public ExportableResource getResource() {
        return this.m_gxlResource;
    }

    @Override
    public void visit(DataType t, String param) {
        if (hasElement(t)) {
            return;
        }

        NodeType typeNode = null;

        if (t instanceof StringType) {
            typeNode = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#String", Id.ROOT);
        } else if (t instanceof IntType) {
            typeNode = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Int", Id.ROOT);
        } else if (t instanceof RealType) {
            typeNode = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Float", Id.ROOT);
        } else if (t instanceof BoolType) {
            typeNode = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Bool", Id.ROOT);
        } else if (t instanceof CustomDataType) {
            // Create it as a node with a 'value' attribute
            // Commented as this fails when using default values. The name is lost though when using as string attribute
            /*typeNode = createNode("domain" + getID(((CustomDataType) t).getId()), GxlUtil.g_gxlTypeGraphURI + "#NodeClass");
            NodeType attrNode = createNode(getID(((CustomDataType) t).getId()) + "_value", GxlUtil.g_gxlTypeGraphURI + "#AttributeClass");
            GxlUtil.setAttribute(attrNode, "name", "value", AttrTypeEnum.STRING);
            createEdge(attrNode, getElement(StringType.get()), GxlUtil.g_gxlTypeGraphURI + "#hasDomain");
            createEdge(typeNode, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");*/

            // Create as a string, GXL has no notion of custom data types
            typeNode = createNode(getId(t),
                GxlUtil.g_gxlTypeGraphURI + "#String",
                t.getId()
                    .getNamespace());
        }

        setElement(t, typeNode);
    }

    @Override
    public void visit(Class cmClass, String param) {
        if (hasElement(cmClass)) {
            return;
        }

        if (!cmClass.isProper()) {
            setElement(cmClass, getElement(cmClass.getProperClass()));
            return;
        }

        NodeType classNode = createNode(getId(cmClass),
            GxlUtil.g_gxlTypeGraphURI + "#NodeClass",
            cmClass.getId()
                .getNamespace());
        setElement(cmClass, classNode);
        GxlUtil.setAttribute(classNode, "name", idToName(cmClass.getId()), AttrTypeEnum.STRING);
        GxlUtil.setAttribute(classNode, "isabstract", false, AttrTypeEnum.BOOL);

        for (Class superClass : cmClass.getSuperClasses()) {
            NodeType superNode = getElement(superClass);
            createEdge(classNode, superNode, GxlUtil.g_gxlTypeGraphURI + "#isA");
        }

        // Containers can be mapped to their respective types correctly if multiplicity is 0..* and type is SET, BAG or SEQ
        // and type is an attribute type (or suitable container recursing to an attribute type). multiplicities are ignored.
        // If instead referring to a node type, only SET and ORD make sense, although multiplicities can be used.
        for (Field field : cmClass.getFields()) {
            NodeType fieldNode = getElement(field);
            // Now here is a choice of using an attribute or an edge. Use attribute when limit = [1..1]
            boolean isAttribute = isAttribute(field);
            if (isAttribute) {
                NodeType attrNode = fieldNode;
                createEdge(classNode, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");
            } else {
                NodeType edgeNode = fieldNode;

                EdgeType fromEdge =
                    createEdge(edgeNode, classNode, GxlUtil.g_gxlTypeGraphURI + "#from");
                TupType limits = createLimit(0, -1);
                GxlUtil.setAttribute(fromEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(fromEdge, "isordered", false, AttrTypeEnum.BOOL);

            }
        }
    }

    @Override
    public void visit(Field field, String param) {
        if (hasElement(field)) {
            return;
        }

        NodeType fieldNode = null;
        // Now here is a choice of using an attribute or an edge.
        if (isAttribute(field)) {
            NodeType typeNode = getElement(field.getType());

            NodeType attrNode = createNode(getId(field),
                GxlUtil.g_gxlTypeGraphURI + "#AttributeClass",
                field.getDefiningClass()
                    .getId()
                    .getNamespace());
            GxlUtil.setAttribute(attrNode,
                "name",
                field.getName()
                    .toString(),
                AttrTypeEnum.STRING);

            createEdge(attrNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#hasDomain");
            //createEdge(classNode, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");
            fieldNode = attrNode;
        } else {
            if (field.getType() instanceof Container) {
                NodeType edgeNode = getElement(field.getType());
                fieldNode = edgeNode;
            } else {
                NodeType edgeNode = createNode(getId(field),
                    GxlUtil.g_gxlTypeGraphURI + "#EdgeClass",
                    field.getDefiningClass()
                        .getId()
                        .getNamespace());
                GxlUtil.setAttribute(edgeNode,
                    "name",
                    field.getName()
                        .toString(),
                    AttrTypeEnum.STRING);
                GxlUtil.setAttribute(edgeNode, "isdirected", true, AttrTypeEnum.BOOL);
                GxlUtil.setAttribute(edgeNode, "isabstract", false, AttrTypeEnum.BOOL);
                boolean ordered = false;

                NodeType typeNode = getElement(field.getType());
                EdgeType toEdge = createEdge(edgeNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#to");
                TupType limits = createLimit(field.getLowerBound(), field.getUpperBound());
                GxlUtil.setAttribute(toEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(toEdge, "isordered", ordered, AttrTypeEnum.BOOL);
                fieldNode = edgeNode;
            }
        }

        setElement(field, fieldNode);

        return;
    }

    @Override
    public void visit(Container container, String param) {
        if (!isAttribute(container)) {
            String edgeId = getEdgeId();
            NodeType edgeNode =
                createNode(edgeId, GxlUtil.g_gxlTypeGraphURI + "#EdgeClass", Id.ROOT);
            GxlUtil.setAttribute(edgeNode, "name", "value_" + edgeId, AttrTypeEnum.STRING);
            GxlUtil.setAttribute(edgeNode, "isdirected", true, AttrTypeEnum.BOOL);
            GxlUtil.setAttribute(edgeNode, "isabstract", false, AttrTypeEnum.BOOL);
            setElement(container, edgeNode);

            Kind ct = container.getContainerType();
            boolean ordered = (ct == Kind.ORD || ct == Kind.SEQ);
            NodeType typeNode = getElement(container.getType());
            // Unique is ignored and assumed to be true. Non-unique is not supported

            if (container.getType() instanceof Container) {
                // typeNode points to an edge, create intermediate node and connect edge node representing this container via that node
                NodeType containerNode =
                    createNode(getId(container), GxlUtil.g_gxlTypeGraphURI + "#NodeClass", Id.ROOT);
                GxlUtil.setAttribute(containerNode, "name", getId(container), AttrTypeEnum.STRING);
                GxlUtil.setAttribute(containerNode, "isabstract", false, AttrTypeEnum.BOOL);

                EdgeType toEdge =
                    createEdge(edgeNode, containerNode, GxlUtil.g_gxlTypeGraphURI + "#to");
                TupType limits = createLimit(0, -1);
                GxlUtil.setAttribute(toEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(toEdge, "isordered", ordered, AttrTypeEnum.BOOL);

                EdgeType fromEdge =
                    createEdge(typeNode, containerNode, GxlUtil.g_gxlTypeGraphURI + "#from");
                // Container node always exactly 1 incoming edge
                limits = createLimit(1, 1);
                GxlUtil.setAttribute(fromEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(fromEdge, "isordered", false, AttrTypeEnum.BOOL);
            } else {
                //typeNode points to another node, simply connect the edge
                EdgeType toEdge = createEdge(edgeNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#to");
                TupType limits = createLimit(0, -1);
                GxlUtil.setAttribute(toEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(toEdge, "isordered", ordered, AttrTypeEnum.BOOL);
            }

            return;
        }

        String gxlType = GxlUtil.g_gxlTypeGraphURI;
        //if type is ORD, revert to SEQ.
        switch (container.getContainerType()) {
        case SET:
            gxlType += "#Set";
            break;
        case BAG:
            gxlType += "#Bag";
            break;
        case SEQ:
            gxlType += "#Seq";
            break;
        case ORD:
            gxlType += "#Seq";
            break;
        }

        NodeType containerNode = createNode(getId(container), gxlType, Id.ROOT);
        setElement(container, containerNode);

        NodeType subTypeNode = getElement(container.getType());

        createEdge(containerNode, subTypeNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponent");
    }

    @Override
    public void visit(Enum cmEnum, String param) {
        if (hasElement(cmEnum)) {
            return;
        }

        NodeType enumNode = createNode(getId(cmEnum),
            GxlUtil.g_gxlTypeGraphURI + "#Enum",
            cmEnum.getId()
                .getNamespace());
        setElement(cmEnum, enumNode);

        for (Name literal : cmEnum.getLiterals()) {
            String literalId = getId(cmEnum) + "_" + literal;
            NodeType literalNode = createNode(literalId,
                GxlUtil.g_gxlTypeGraphURI + "#EnumVal",
                cmEnum.getId()
                    .getNamespace());
            GxlUtil.setAttribute(literalNode, "value", literal.toString(), AttrTypeEnum.STRING);

            createEdge(enumNode, literalNode, GxlUtil.g_gxlTypeGraphURI + "#containsValue");
        }
    }

    @Override
    public void visit(Tuple tuple, String param) {
        if (hasElement(tuple)) {
            return;
        }

        if (isAttribute(tuple)) {

            NodeType tupleNode =
                createNode(getId(tuple), GxlUtil.g_gxlTypeGraphURI + "#Tup", Id.ROOT);
            setElement(tuple, tupleNode);

            //GxlUtil.setAttribute(tupleNode, "name", getId(tuple), AttrTypeEnum.STRING);

            int index = 0;
            for (Type type : tuple.getTypes()) {
                NodeType typeNode = getElement(type);
                EdgeType componentEdge =
                    createEdge(tupleNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponent");
                componentEdge.setToorder(BigInteger.valueOf(index++));
            }

        } else {
            // Tuple contains relation. Upgrade to NodeClass, with attributes and relations for tuple elements
            Class cmClass = makeClass(tuple);
            this.m_tupleClasses.put(tuple, cmClass);
            setElement(tuple, getElement(cmClass));
        }
    }

    @Override
    public void visit(AbstractProperty abstractProperty, String param) {
        if (hasElement(abstractProperty)) {
            return;
        }
        setElement(abstractProperty, null);

        NodeType classNode = getElement(abstractProperty.getAbstractClass());
        GxlUtil.setAttribute(classNode, "isabstract", true, AttrTypeEnum.BOOL);
    }

    @Override
    public void visit(ContainmentProperty containmentProperty, String param) {
        if (hasElement(containmentProperty)) {
            return;
        }
        setElement(containmentProperty, null);

        NodeType fieldNode = getElement(containmentProperty.getField());

        GxlUtil.setElemType(fieldNode, GxlUtil.g_gxlTypeGraphURI + "#CompositionClass");
        GxlUtil.setAttribute(fieldNode, "aggregate", "to", AttrTypeEnum.ENUM);
    }

    @Override
    public void visit(IdentityProperty identityProperty, String param) {
        if (hasElement(identityProperty)) {
            return;
        }
        setElement(identityProperty, null);

        // Cannot be used in GXL
    }

    @Override
    public void visit(KeysetProperty keysetProperty, String param) {
        if (hasElement(keysetProperty)) {
            return;
        }
        setElement(keysetProperty, null);

        // Cannot be used in GXL
    }

    @Override
    public void visit(OppositeProperty oppositeProperty, String param) {
        if (hasElement(oppositeProperty)) {
            return;
        }
        setElement(oppositeProperty, null);

        // Cannot be used in GXL
    }

    @Override
    public void visit(DefaultValueProperty defaultValueProperty, String param) {
        if (hasElement(defaultValueProperty)) {
            return;
        }
        setElement(defaultValueProperty, null);

        if (!isAttribute(defaultValueProperty.getField())) {
            throw new IllegalArgumentException(
                "Field must be an attribute for use with default value");
        }

        NodeType fieldNode = getElement(defaultValueProperty.getField());

        NodeType valueNode = getValueElement(defaultValueProperty.getDefaultValue());

        createEdge(fieldNode, valueNode, GxlUtil.g_gxlTypeGraphURI + "#hasDefaultValue");

        //JAXBElement<?> gxlValue = GxlUtil.valueToGxl(defaultValueProperty.getDefaultValue());
        //GxlUtil.setAttribute(fieldNode, "defaultValue", gxlValue.getValue(), AttrTypeEnum.AUTO);
    }

    public String getId(Type type) {
        if (this.m_objectIDs.containsKey(type)) {
            return this.m_objectIDs.get(type);
        }

        String nodeId = null;

        if (type instanceof Identifiable) {
            Id typeId = ((Identifiable) type).getId();
            nodeId = getShortId(typeId).toString();
            // Classes do not get the prefix, as otherwise the GXL validator chokes on the difference between name and ID
            if (!(type instanceof Class)) {
                nodeId = "type_" + nodeId;
            }
        } else {
            String className = type.getClass()
                .getSimpleName();
            nodeId = "type_" + className + this.m_nextType++;
        }

        this.m_objectIDs.put(type, nodeId);

        return nodeId;
    }

    public String getId(Field field) {
        if (this.m_objectIDs.containsKey(field)) {
            return this.m_objectIDs.get(field);
        }

        String id = null;

        id = "field_" + field.getName() + this.m_nextType++;

        this.m_objectIDs.put(field, id);

        return id;
    }

    public String getId(Id packageId) {
        return this.m_packageNodes.get(packageId)
            .getId();
    }

    private Id getShortId(Id id) {
        return id;//m_idMap.containsKey(id) ? m_idMap.get(id) : id;
    }

    private String getEdgeId() {
        return "e" + this.m_nextEdge++;
    }

    private String getValueId(Value v) {
        return "val_" + v.toString() + "_" + this.m_nextValue++;
    }

    private NodeType createNode(String id, String type, Id packageId) {
        NodeType newNode = new NodeType();
        newNode.setId(id);
        if (type != null) {
            GxlUtil.setElemType(newNode, type);
        }

        this.m_typeGraph.getNodeOrEdgeOrRel()
            .add(newNode);

        //NodeType graphNode = getPackageNode(packageId);
        NodeType graphNode = getPackageNode(Id.ROOT);
        if (graphNode != null && type != null) {
            // Add nodes, edges and relations
            if (type.equals(GxlUtil.g_gxlTypeGraphURI + "#NodeClass")
                || type.equals(GxlUtil.g_gxlTypeGraphURI + "#EdgeClass")
                || type.equals(GxlUtil.g_gxlTypeGraphURI + "#CompositionClass")
                || type.equals(GxlUtil.g_gxlTypeGraphURI + "#AggregationClass")
                || type.equals(GxlUtil.g_gxlTypeGraphURI + "#RelationClass")) {
                createEdge(graphNode, newNode, GxlUtil.g_gxlTypeGraphURI + "#contains");
            }
        }

        return newNode;
    }

    private EdgeType createEdge(NodeType from, NodeType to, String type) {
        EdgeType newEdge = new EdgeType();
        newEdge.setFrom(from);
        newEdge.setTo(to);
        newEdge.setId(getEdgeId());
        if (type != null) {
            GxlUtil.setElemType(newEdge, type);
        }

        this.m_typeGraph.getNodeOrEdgeOrRel()
            .add(newEdge);

        return newEdge;
    }

    private TupType createLimit(int lower, int upper) {
        TupType limitTuple = new TupType();
        JAXBElement<BigInteger> lowerInt =
            GxlUtil.g_objectFactory.createInt(BigInteger.valueOf(lower));
        JAXBElement<BigInteger> upperInt =
            GxlUtil.g_objectFactory.createInt(BigInteger.valueOf(upper));

        limitTuple.getBagOrSetOrSeq()
            .add(lowerInt);
        limitTuple.getBagOrSetOrSeq()
            .add(upperInt);

        return limitTuple;
    }

    public boolean isAttribute(Field field) {
        return isAttribute(field.getType());
    }

    public boolean isAttribute(Type type) {
        while (type instanceof Container) {
            type = ((Container) type).getType();
        }

        // Allow custom datatypes as they are mapped to strings
        boolean isAttribute = (type instanceof DataType);// && !(fieldType instanceof CustomDataType));

        if (type instanceof Tuple) {
            isAttribute = true;
            Tuple tup = (Tuple) type;
            for (Type t : tup.getTypes()) {
                isAttribute &= isAttribute(t);
            }
        }

        if (type instanceof Container) {
            // Recursive
            return isAttribute(type);
        }

        return isAttribute;
    }

    private NodeType getValueElement(Value v) {
        if (hasElement(v)) {
            return getElement(v);
        }

        NodeType valNode = null;

        // This is used for defaultvalue only. These values MUST of of the string attribute type
        if (v instanceof BoolValue) {
            valNode = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#BoolVal", Id.ROOT);
            GxlUtil.setAttribute(valNode,
                "value",
                Boolean.valueOf(((BoolValue) v).getValue())
                    .toString(),
                AttrTypeEnum.STRING);
            return valNode;
        } else if (v instanceof IntValue) {
            valNode = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#IntVal", Id.ROOT);
            GxlUtil.setAttribute(valNode,
                "value",
                ((IntValue) v).getValue()
                    .toString(),
                AttrTypeEnum.STRING);
            return valNode;
        } else if (v instanceof RealValue) {
            valNode = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#FloatVal", Id.ROOT);
            GxlUtil.setAttribute(valNode,
                "value",
                ((RealValue) v).getValue()
                    .toString(),
                AttrTypeEnum.STRING);
            return valNode;
        } else if (v instanceof StringValue) {
            valNode = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#StringVal", Id.ROOT);
            GxlUtil
                .setAttribute(valNode, "value", ((StringValue) v).getValue(), AttrTypeEnum.STRING);
            return valNode;
        } else if (v instanceof EnumValue) {
            valNode = createNode(getValueId(v),
                GxlUtil.g_gxlTypeGraphURI + "#EnumVal",
                ((Enum) v.getType()).getId()
                    .getNamespace());
            GxlUtil.setAttribute(valNode,
                "value",
                ((EnumValue) v).getValue()
                    .toString(),
                AttrTypeEnum.STRING);
            return valNode;
        } else if (v instanceof CustomDataValue) {
            valNode = createNode(getValueId(v),
                GxlUtil.g_gxlTypeGraphURI + "#StringVal",
                ((CustomDataType) v.getType()).getId()
                    .getNamespace());
            GxlUtil.setAttribute(valNode,
                "value",
                ((CustomDataValue) v).getValue()
                    .toString(),
                AttrTypeEnum.STRING);
            return valNode;
        }
        // Composite types
        else if (v instanceof ContainerValue) {
            ContainerValue cv = (ContainerValue) v;
            String type = GxlUtil.g_gxlTypeGraphURI + "#";
            switch (((Container) cv.getType()).getContainerType()) {
            case SET:
                type += "SetVal";
                break;
            case BAG:
                type += "BagVal";
                break;
            case ORD:
            case SEQ:
                type += "SeqVal";
                break;
            }
            valNode = createNode(getValueId(v), type, Id.ROOT);

            int index = 0;
            for (Value subVal : cv.getValue()) {
                NodeType subValNode = getValueElement(subVal);
                EdgeType valEdge = createEdge(valNode,
                    subValNode,
                    GxlUtil.g_gxlTypeGraphURI + "#hasComponentValue");
                valEdge.setToorder(BigInteger.valueOf(index++));
            }

            return valNode;
        } else if (v instanceof TupleValue) {
            TupleValue tv = (TupleValue) v;

            valNode = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#TupVal", Id.ROOT);

            for (Entry<Integer,Value> subEntry : tv.getValue()
                .entrySet()) {
                NodeType subValNode = getValueElement(subEntry.getValue());
                EdgeType valEdge = createEdge(valNode,
                    subValNode,
                    GxlUtil.g_gxlTypeGraphURI + "#hasComponentValue");
                valEdge.setToorder(BigInteger.valueOf(subEntry.getKey()));
            }

            return valNode;
        }

        setElement(v, valNode);

        return valNode;
    }

    private NodeType getPackageNode(Id packageId) {
        if (!this.m_packageNodes.containsKey(packageId)) {
            NodeType graphNode = new NodeType();
            graphNode.setId("graph_" + (packageId == Id.ROOT ? this.m_currentTypeName : packageId));
            GxlUtil.setElemType(graphNode, GxlUtil.g_gxlTypeGraphURI + "#GraphClass");
            GxlUtil.setAttribute(graphNode,
                "name",
                packageId == Id.ROOT ? this.m_currentTypeName : packageId.getName()
                    .toString(),
                AttrTypeEnum.STRING);
            this.m_typeGraph.getNodeOrEdgeOrRel()
                .add(graphNode);
            this.m_packageNodes.put(packageId, graphNode);

            // ROOT package, just create a ROOT graph (these kind of subgraph namespaces usually disappear when importing back again)
            if (packageId == Id.ROOT) {// || packageId.getNamespace() == Id.ROOT) {
                // insert into graph directly, no intermediate nodes etc
            } else {
                // Parent graph, insert intermediate node
                NodeType parentNode = getPackageNode(packageId.getNamespace());
                NodeType intermediateNode = null;
                if (this.m_packageIntermediateNodes.containsKey(packageId.getNamespace())) {
                    intermediateNode =
                        this.m_packageIntermediateNodes.get(packageId.getNamespace());
                } else {
                    intermediateNode = new NodeType();
                    intermediateNode.setId("graphnode_" + packageId.getNamespace());
                    GxlUtil.setElemType(graphNode, GxlUtil.g_gxlTypeGraphURI + "#NodeClass");
                    createEdge(parentNode,
                        intermediateNode,
                        GxlUtil.g_gxlTypeGraphURI + "#contains");
                    this.m_typeGraph.getNodeOrEdgeOrRel()
                        .add(intermediateNode);
                }
                createEdge(intermediateNode,
                    graphNode,
                    GxlUtil.g_gxlTypeGraphURI + "#hasAsComponentGraph");
            }

            return graphNode;
        } else {
            return this.m_packageNodes.get(packageId);
        }
    }

    private String idToName(Id id) {
        String res = id.getName()
            .toString();
        while (id.getNamespace() != Id.ROOT) {
            res = id.getNamespace()
                .getName() + "." + res;
            id = id.getNamespace();
        }
        return res;
    }

    private Class makeClass(Tuple tuple) {
        Class cmClass = new Class(Id.getId(Id.ROOT, Name.getName(getId(tuple))));

        int index = 1;
        for (Type t : tuple.getTypes()) {
            cmClass.addField(new Field(Name.getName("_" + index++), t, 1, 1));
        }

        return cmClass;
    }

    public Class getTupleClass(Tuple t) {
        if (this.m_tupleClasses.containsKey(t)) {
            return this.m_tupleClasses.get(t);
        }

        return null;
    }
}
