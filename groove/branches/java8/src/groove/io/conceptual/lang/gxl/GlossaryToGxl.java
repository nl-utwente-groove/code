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

import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Id;
import groove.io.conceptual.Identifiable;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.GlossaryExportBuilder;
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
import groove.util.Exceptions;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.eclipse.jdt.annotation.NonNull;

import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.TupType;

//Thing to note here: Instance graphs are referred to by their ID, since they dont have a name attribute.
//Type graphs are referred to by the ID (often coinciding with the name) of the GraphClass node. The actual ID
// of the graph in which this node is contained is ignored. This also means one type graph graph can be used
//in one GXL document , but multiple GraphClass nodes are allowed (each resulting in a TypeModel)
/** Bridge from glossary to GXL export. */
public class GlossaryToGxl extends GlossaryExportBuilder<GxlExport,NodeType> {
    /** Constructs an instance for a given glossary and export object. */
    public GlossaryToGxl(GxlExport export, Glossary glos) {
        super(glos, export);
    }

    // Keep track of graph to add nodes to
    private GraphType m_typeGraph;

    // Packages are mapped to subgraphs in instance models
    private Map<Id,NodeType> m_packageNodes = new HashMap<Id,NodeType>();
    private Map<Id,NodeType> m_packageIntermediateNodes = new HashMap<Id,NodeType>();
    private String m_currentTypeName;

    private Map<java.lang.Object,String> m_objectIDs = new HashMap<java.lang.Object,String>();

    // Some tuples must be represented by classes, this map keeps track of them
    private Map<Tuple,Class> m_tupleClasses = new HashMap<Tuple,Class>();

    // To keep track of node Ids
    private int m_nextType = 1;
    private int m_nextEdge = 1;
    private int m_nextValue = 1;

    @Override
    public void build() throws PortException {
        // If no typegraph yet, create one, this also creates a new GraphClass node inside it.
        // Otherwise, insert a new GraphClass node
        if (this.m_typeGraph == null) {
            this.m_typeGraph = getExport().getTypeGraph(getGlossary().getName());
        }

        int timer = Timer.start("TM to GXL");
        this.m_currentTypeName = getGlossary().getName();
        super.build();
        Timer.stop(timer);
    }

    @Override
    protected NodeType addDataType(DataType t) {
        NodeType result = null;

        if (t instanceof StringType) {
            result = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#String", Id.ROOT);
        } else if (t instanceof IntType) {
            result = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Int", Id.ROOT);
        } else if (t instanceof RealType) {
            result = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Float", Id.ROOT);
        } else if (t instanceof BoolType) {
            result = createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#Bool", Id.ROOT);
        } else if (t instanceof CustomDataType) {
            // Create it as a node with a 'value' attribute
            // Commented as this fails when using default values. The name is lost though when using as string attribute
            /*typeNode = createNode("domain" + getID(((CustomDataType) t).getId()), GxlUtil.g_gxlTypeGraphURI + "#NodeClass");
            NodeType attrNode = createNode(getID(((CustomDataType) t).getId()) + "_value", GxlUtil.g_gxlTypeGraphURI + "#AttributeClass");
            GxlUtil.setAttribute(attrNode, "name", "value", AttrTypeEnum.STRING);
            createEdge(attrNode, getElement(StringType.get()), GxlUtil.g_gxlTypeGraphURI + "#hasDomain");
            createEdge(typeNode, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");*/

            // Create as a string, GXL has no notion of custom data types
            result =
                createNode(getId(t), GxlUtil.g_gxlTypeGraphURI + "#String", t.getId()
                    .getNamespace());
        }

        put(t, result);
        return result;
    }

    @Override
    protected NodeType addClass(Class cmClass) {
        if (!cmClass.isProper()) {
            NodeType result = add(cmClass.getProperClass());
            put(cmClass, result);
            return result;
        }

        NodeType result =
            createNode(getId(cmClass), GxlUtil.g_gxlTypeGraphURI + "#NodeClass", cmClass.getId()
                .getNamespace());
        put(cmClass, result);
        GxlUtil.setAttribute(result, "name", idToName(cmClass.getId()), AttrTypeEnum.STRING);
        GxlUtil.setAttribute(result, "isabstract", false, AttrTypeEnum.BOOL);

        for (Class superClass : cmClass.getSuperClasses()) {
            NodeType superNode = add(superClass);
            createEdge(result, superNode, GxlUtil.g_gxlTypeGraphURI + "#isA");
        }

        // Containers can be mapped to their respective types correctly if multiplicity is 0..* and type is SET, BAG or SEQ
        // and type is an attribute type (or suitable container recursing to an attribute type). multiplicities are ignored.
        // If instead referring to a node type, only SET and ORD make sense, although multiplicities can be used.
        for (Field field : cmClass.getFields()) {
            NodeType fieldNode = add(field);
            // Now here is a choice of using an attribute or an edge. Use attribute when limit = [1..1]
            boolean isAttribute = isAttribute(field);
            if (isAttribute) {
                NodeType attrNode = fieldNode;
                createEdge(result, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");
            } else {
                NodeType edgeNode = fieldNode;

                EdgeType fromEdge =
                    createEdge(edgeNode, result, GxlUtil.g_gxlTypeGraphURI + "#from");
                TupType limits = createLimit(0, -1);
                GxlUtil.setAttribute(fromEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(fromEdge, "isordered", false, AttrTypeEnum.BOOL);

            }
        }
        return result;
    }

    @Override
    protected NodeType addField(Field field) {
        NodeType result;
        // Now here is a choice of using an attribute or an edge.
        if (isAttribute(field)) {
            NodeType typeNode = add(field.getType());

            NodeType attrNode =
                createNode(getId(field),
                    GxlUtil.g_gxlTypeGraphURI + "#AttributeClass",
                    field.getDefiningClass().getId().getNamespace());
            GxlUtil.setAttribute(attrNode, "name", field.getName().toString(), AttrTypeEnum.STRING);

            createEdge(attrNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#hasDomain");
            //createEdge(classNode, attrNode, GxlUtil.g_gxlTypeGraphURI + "#hasAttribute");
            result = attrNode;
        } else {
            if (field.getType() instanceof Container) {
                NodeType edgeNode = add(field.getType());
                result = edgeNode;
            } else {
                NodeType edgeNode =
                    createNode(getId(field),
                        GxlUtil.g_gxlTypeGraphURI + "#EdgeClass",
                        field.getDefiningClass().getId().getNamespace());
                GxlUtil.setAttribute(edgeNode,
                    "name",
                    field.getName().toString(),
                    AttrTypeEnum.STRING);
                GxlUtil.setAttribute(edgeNode, "isdirected", true, AttrTypeEnum.BOOL);
                GxlUtil.setAttribute(edgeNode, "isabstract", false, AttrTypeEnum.BOOL);
                boolean ordered = false;

                NodeType typeNode = add(field.getType());
                EdgeType toEdge = createEdge(edgeNode, typeNode, GxlUtil.g_gxlTypeGraphURI + "#to");
                TupType limits = createLimit(field.getLowerBound(), field.getUpperBound());
                GxlUtil.setAttribute(toEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(toEdge, "isordered", ordered, AttrTypeEnum.BOOL);
                result = edgeNode;
            }
        }
        put(field, result);
        return result;
    }

    @Override
    protected NodeType addContainer(Container container, String base) {
        if (!isAttribute(container)) {
            String edgeId = getEdgeId();
            NodeType result = createNode(edgeId, GxlUtil.g_gxlTypeGraphURI + "#EdgeClass", Id.ROOT);
            GxlUtil.setAttribute(result, "name", "value_" + edgeId, AttrTypeEnum.STRING);
            GxlUtil.setAttribute(result, "isdirected", true, AttrTypeEnum.BOOL);
            GxlUtil.setAttribute(result, "isabstract", false, AttrTypeEnum.BOOL);
            put(container, result);

            Kind ct = container.getContainerType();
            boolean ordered = (ct == Kind.ORD || ct == Kind.SEQ);
            NodeType typeNode = add(container.getType());
            // Unique is ignored and assumed to be true. Non-unique is not supported

            if (container.getType() instanceof Container) {
                // typeNode points to an edge, create intermediate node and connect edge node representing this container via that node
                NodeType containerNode =
                    createNode(getId(container), GxlUtil.g_gxlTypeGraphURI + "#NodeClass", Id.ROOT);
                GxlUtil.setAttribute(containerNode, "name", getId(container), AttrTypeEnum.STRING);
                GxlUtil.setAttribute(containerNode, "isabstract", false, AttrTypeEnum.BOOL);

                EdgeType toEdge =
                    createEdge(result, containerNode, GxlUtil.g_gxlTypeGraphURI + "#to");
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
                EdgeType toEdge = createEdge(result, typeNode, GxlUtil.g_gxlTypeGraphURI + "#to");
                TupType limits = createLimit(0, -1);
                GxlUtil.setAttribute(toEdge, "limits", limits, AttrTypeEnum.TUP);
                GxlUtil.setAttribute(toEdge, "isordered", ordered, AttrTypeEnum.BOOL);
            }

            return result;
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

        NodeType result = createNode(getId(container), gxlType, Id.ROOT);
        put(container, result);

        NodeType subTypeNode = add(container.getType());

        createEdge(result, subTypeNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponent");
        return result;
    }

    @Override
    protected NodeType addEnum(Enum cmEnum) {
        NodeType result =
            createNode(getId(cmEnum), GxlUtil.g_gxlTypeGraphURI + "#Enum", cmEnum.getId()
                .getNamespace());
        put(cmEnum, result);

        for (Name literal : cmEnum.getLiterals()) {
            String literalId = getId(cmEnum) + "_" + literal;
            NodeType literalNode =
                createNode(literalId, GxlUtil.g_gxlTypeGraphURI + "#EnumVal", cmEnum.getId()
                    .getNamespace());
            GxlUtil.setAttribute(literalNode, "value", literal.toString(), AttrTypeEnum.STRING);

            createEdge(result, literalNode, GxlUtil.g_gxlTypeGraphURI + "#containsValue");
        }
        return result;
    }

    @Override
    protected NodeType addTuple(Tuple tuple) {
        NodeType result;
        if (isAttribute(tuple)) {

            result = createNode(getId(tuple), GxlUtil.g_gxlTypeGraphURI + "#Tup", Id.ROOT);
            put(tuple, result);

            //GxlUtil.setAttribute(tupleNode, "name", getId(tuple), AttrTypeEnum.STRING);

            int index = 0;
            for (Type type : tuple.getTypes()) {
                NodeType typeNode = add(type);
                EdgeType componentEdge =
                    createEdge(result, typeNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponent");
                componentEdge.setToorder(BigInteger.valueOf(index++));
            }

        } else {
            // Tuple contains relation. Upgrade to NodeClass, with attributes and relations for tuple elements
            Class cmClass = makeClass(tuple);
            this.m_tupleClasses.put(tuple, cmClass);
            put(tuple, result = add(cmClass));
        }
        return result;
    }

    @Override
    protected NodeType addAbstractProp(AbstractProperty prop) {
        put(prop, null);
        NodeType classNode = add(prop.getAbstractClass());
        GxlUtil.setAttribute(classNode, "isabstract", true, AttrTypeEnum.BOOL);
        return null;
    }

    @Override
    protected NodeType addContainmentProp(ContainmentProperty prop) {
        put(prop, null);
        NodeType fieldNode = add(prop.getField());
        GxlUtil.setElemType(fieldNode, GxlUtil.g_gxlTypeGraphURI + "#CompositionClass");
        GxlUtil.setAttribute(fieldNode, "aggregate", "to", AttrTypeEnum.ENUM);
        return null;
    }

    @Override
    protected NodeType addIdentityProp(IdentityProperty prop) {
        // Cannot be used in GXL
        put(prop, null);
        return null;
    }

    @Override
    protected NodeType addKeysetProp(KeysetProperty prop) {
        // Cannot be used in GXL
        put(prop, null);
        return null;
    }

    @Override
    protected NodeType addOppositeProp(OppositeProperty prop) {
        // Cannot be used in GXL
        put(prop, null);
        return null;
    }

    @Override
    protected NodeType addDefaultValueProp(DefaultValueProperty prop) {
        put(prop, null);
        if (!isAttribute(prop.getField())) {
            throw new IllegalArgumentException(
                "Field must be an attribute for use with default value");
        }
        NodeType fieldNode = add(prop.getField());
        NodeType valueNode = addValue(prop.getDefaultValue());
        createEdge(fieldNode, valueNode, GxlUtil.g_gxlTypeGraphURI + "#hasDefaultValue");
        return null;
    }

    /** Returns an identifier for a given conceptual type. */
    public String getId(Type type) {
        String result = this.m_objectIDs.get(type);
        if (result == null) {
            if (type instanceof Identifiable) {
                Id typeId = ((Identifiable) type).getId();
                result = getShortId(typeId).toString();
                // Classes do not get the prefix, as otherwise the GXL validator chokes on the difference between name and ID
                if (!(type instanceof Class)) {
                    result = "type_" + result;
                }
            } else {
                String className = type.getClass().getSimpleName();
                result = "type_" + className + this.m_nextType++;
            }
            this.m_objectIDs.put(type, result);
        }
        return result;
    }

    /** Returns an identifier for a given field type. */
    public String getId(Field field) {
        String result = this.m_objectIDs.get(field);
        if (result == null) {
            result = "field_" + field.getName() + this.m_nextType++;
            this.m_objectIDs.put(field, result);
        }
        return result;
    }

    private Id getShortId(Id id) {
        return id;//m_idMap.containsKey(id) ? m_idMap.get(id) : id;
    }

    /** Generates a fresh edge identifier. */
    private String getEdgeId() {
        return "e" + this.m_nextEdge++;
    }

    /** Generates a fresh value identifier. */
    private String getValueId(Value v) {
        return "val_" + v.toString() + "_" + this.m_nextValue++;
    }

    private NodeType createNode(String id, @NonNull String type, Id packageId) {
        NodeType newNode = new NodeType();
        newNode.setId(id);
        GxlUtil.setElemType(newNode, type);

        this.m_typeGraph.getNodeOrEdgeOrRel().add(newNode);

        //NodeType graphNode = getPackageNode(packageId);
        NodeType graphNode = getPackageNode(Id.ROOT);
        if (graphNode != null) {
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

        this.m_typeGraph.getNodeOrEdgeOrRel().add(newEdge);

        return newEdge;
    }

    private TupType createLimit(int lower, int upper) {
        TupType limitTuple = new TupType();
        JAXBElement<BigInteger> lowerInt =
            GxlUtil.g_objectFactory.createInt(BigInteger.valueOf(lower));
        JAXBElement<BigInteger> upperInt =
            GxlUtil.g_objectFactory.createInt(BigInteger.valueOf(upper));

        limitTuple.getBagOrSetOrSeq().add(lowerInt);
        limitTuple.getBagOrSetOrSeq().add(upperInt);

        return limitTuple;
    }

    /** Tests if a given field is represented by an attribute. */
    public boolean isAttribute(Field field) {
        return isAttribute(field.getType());
    }

    /** Tests if a given conceptual type is represented by an attribute. */
    public boolean isAttribute(Type type) {
        boolean result;
        // Allow custom datatypes as they are mapped to strings
        switch (type.getKind()) {
        case INT_TYPE:
        case BOOL_TYPE:
        case STRING_TYPE:
        case REAL_TYPE:
        case ENUM_TYPE:
        case CUSTOM_TYPE:
            result = true;
            break;
        case TUPLE_TYPE:
            result = true;
            Tuple tup = (Tuple) type;
            for (Type t : tup.getTypes()) {
                result &= isAttribute(t);
            }
            break;
        case CONTAINER_TYPE:
            result = isAttribute(((Container) type).getType());
            break;
        case CLASS_TYPE:
            result = false;
            break;
        default:
            throw Exceptions.illegalArg("Illegal concept kind: %s should be a type", type.getKind());
        }
        return result;
    }

    private NodeType addValue(Value v) {
        NodeType result;
        // This is used for defaultvalue only. These values MUST of of the string attribute type
        switch (v.getKind()) {
        case BOOL_VAL:
            result = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#BoolVal", Id.ROOT);
            GxlUtil.setAttribute(result,
                "value",
                new Boolean(((BoolValue) v).getValue()).toString(),
                AttrTypeEnum.STRING);
            break;
        case INT_VAL:
            result = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#IntVal", Id.ROOT);
            GxlUtil.setAttribute(result,
                "value",
                ((IntValue) v).getValue().toString(),
                AttrTypeEnum.STRING);
            break;
        case REAL_VAL:
            result = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#FloatVal", Id.ROOT);
            GxlUtil.setAttribute(result,
                "value",
                ((RealValue) v).getValue().toString(),
                AttrTypeEnum.STRING);
            break;
        case STRING_VAL:
            result = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#StringVal", Id.ROOT);
            GxlUtil.setAttribute(result, "value", ((StringValue) v).getValue(), AttrTypeEnum.STRING);
            break;
        case ENUM_VAL:
            result =
                createNode(getValueId(v),
                    GxlUtil.g_gxlTypeGraphURI + "#EnumVal",
                    ((Enum) v.getType()).getId().getNamespace());
            GxlUtil.setAttribute(result,
                "value",
                ((EnumValue) v).getValue().toString(),
                AttrTypeEnum.STRING);
            break;
        case CUSTOM_VAL:
            result =
                createNode(getValueId(v),
                    GxlUtil.g_gxlTypeGraphURI + "#StringVal",
                    ((CustomDataType) v.getType()).getId().getNamespace());
            GxlUtil.setAttribute(result,
                "value",
                ((CustomDataValue) v).getValue().toString(),
                AttrTypeEnum.STRING);
            break;
        case CONTAINER_VAL:
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
            result = createNode(getValueId(v), type, Id.ROOT);

            int index = 0;
            for (Value subVal : cv.getValue()) {
                NodeType subValNode = addValue(subVal);
                EdgeType valEdge =
                    createEdge(result, subValNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponentValue");
                valEdge.setToorder(BigInteger.valueOf(index++));
            }
            break;
        case TUPLE_VAL:
            TupleValue tv = (TupleValue) v;

            result = createNode(getValueId(v), GxlUtil.g_gxlTypeGraphURI + "#TupVal", Id.ROOT);

            for (Entry<Integer,Value> subEntry : tv.getValue().entrySet()) {
                NodeType subValNode = addValue(subEntry.getValue());
                EdgeType valEdge =
                    createEdge(result, subValNode, GxlUtil.g_gxlTypeGraphURI + "#hasComponentValue");
                valEdge.setToorder(BigInteger.valueOf(subEntry.getKey()));
            }
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        put(v, result);
        return result;
    }

    private NodeType getPackageNode(Id packageId) {
        if (!this.m_packageNodes.containsKey(packageId)) {
            NodeType graphNode = new NodeType();
            graphNode.setId("graph_" + (packageId == Id.ROOT ? this.m_currentTypeName : packageId));
            GxlUtil.setElemType(graphNode, GxlUtil.g_gxlTypeGraphURI + "#GraphClass");
            GxlUtil.setAttribute(graphNode, "name", packageId == Id.ROOT ? this.m_currentTypeName
                : packageId.getName().toString(), AttrTypeEnum.STRING);
            this.m_typeGraph.getNodeOrEdgeOrRel().add(graphNode);
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
                    createEdge(parentNode, intermediateNode, GxlUtil.g_gxlTypeGraphURI
                        + "#contains");
                    this.m_typeGraph.getNodeOrEdgeOrRel().add(intermediateNode);
                }
                createEdge(intermediateNode, graphNode, GxlUtil.g_gxlTypeGraphURI
                    + "#hasAsComponentGraph");
            }

            return graphNode;
        } else {
            return this.m_packageNodes.get(packageId);
        }
    }

    private String idToName(Id id) {
        String res = id.getName().toString();
        while (id.getNamespace() != Id.ROOT) {
            res = id.getNamespace().getName() + "." + res;
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

    /** Returns the conceptual class that was artificially created for a given tuple type. */
    public Class getTupleClass(Tuple t) {
        return this.m_tupleClasses.get(t);
    }
}
