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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.BagType;
import de.gupro.gxl.gxl_1_0.CompositeValueType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import de.gupro.gxl.gxl_1_0.LocatorType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.ObjectFactory;
import de.gupro.gxl.gxl_1_0.SeqType;
import de.gupro.gxl.gxl_1_0.SetType;
import de.gupro.gxl.gxl_1_0.TupType;
import de.gupro.gxl.gxl_1_0.TypeType;
import de.gupro.gxl.gxl_1_0.TypedElementType;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.DataType;
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
import groove.util.Exceptions;

@SuppressWarnings("javadoc")
public class GxlUtil {
    public static class GraphWrapper {
        private GraphType m_graph;
        private String m_type;

        private Set<NodeWrapper> m_nodes = new HashSet<>();

        public GraphWrapper(GraphType graph) {
            this.m_graph = graph;
            this.m_type = GxlUtil.getElemType(graph);
            assert (this.m_type != null);
        }

        protected void addNode(NodeWrapper node) {
            this.m_nodes.add(node);
        }

        public GraphType getGraph() {
            return this.m_graph;
        }

        public String getType() {
            return this.m_type;
        }

        public Set<NodeWrapper> getNodes() {
            return this.m_nodes;
        }
    }

    public static class NodeWrapper {
        private NodeType m_node;
        private String m_type;

        private List<EdgeWrapper> m_edges = new ArrayList<>();
        private List<EdgeWrapper> m_incomingEdges = new ArrayList<>();

        public NodeWrapper(NodeType node) {
            this.m_node = node;
            this.m_type = GxlUtil.getElemType(node);
            assert (this.m_type != null);
        }

        protected void addEdge(EdgeWrapper edge) {
            assert (edge.getSource() == this);
            this.m_edges.add(edge);
        }

        protected void addIncomingEdge(EdgeWrapper edge) {
            assert (edge.getTarget() == this);
            this.m_incomingEdges.add(edge);
        }

        public NodeType getNode() {
            return this.m_node;
        }

        public List<EdgeWrapper> getEdges() {
            return this.m_edges;
        }

        public List<EdgeWrapper> getIncomingEdges() {
            return this.m_incomingEdges;
        }

        public String getType() {
            return this.m_type;
        }

        public void sortEdges() {
            Collections.sort(this.m_edges, new Comparator<EdgeWrapper>() {
                @Override
                public int compare(EdgeWrapper ew1, EdgeWrapper ew2) {
                    int stringCompare = ew1.getType()
                        .compareTo(ew2.getType());
                    if (stringCompare == 0) {
                        BigInteger eo1 = ew1.getEdge()
                            .getToorder();
                        BigInteger eo2 = ew2.getEdge()
                            .getToorder();
                        if (eo1 == null || eo2 == null) {
                            if (eo1 == null) {
                                return eo2 == null ? 0 : 1;
                            }
                            return -1;
                        }
                        return eo1.compareTo(eo2);
                    }
                    return stringCompare;
                }

            });
        }
        //sortIncomingEdges possible to using getFromorder
    }

    public static class EdgeWrapper {
        private EdgeType m_edge;
        private String m_type;

        private NodeWrapper m_nodeFrom;
        private NodeWrapper m_nodeTo;

        private EdgeWrapper m_edgeFrom;
        private EdgeWrapper m_edgeTo;

        private List<EdgeWrapper> m_edges = new ArrayList<>();
        private List<EdgeWrapper> m_incomingEdges = new ArrayList<>();

        // True if connecting nodes, false if connecting edges
        private boolean m_nodeEdge;

        public EdgeWrapper(EdgeType edge) {
            this.m_edge = edge;
            this.m_type = GxlUtil.getElemType(edge);
            assert (this.m_type != null);
        }

        public EdgeType getEdge() {
            return this.m_edge;
        }

        public void setWrapper(NodeWrapper nodeFrom, NodeWrapper nodeTo) {
            this.m_nodeFrom = nodeFrom;
            this.m_nodeTo = nodeTo;
            this.m_nodeEdge = true;
        }

        public void setWrapper(EdgeWrapper edgeFrom, EdgeWrapper edgeTo) {
            this.m_edgeFrom = edgeFrom;
            this.m_edgeTo = edgeTo;
            this.m_nodeEdge = false;
        }

        public NodeWrapper getSource() {
            return this.m_nodeFrom;
        }

        public NodeWrapper getTarget() {
            return this.m_nodeTo;
        }

        public EdgeWrapper getSourceEdge() {
            return this.m_edgeFrom;
        }

        public EdgeWrapper getTargetEdge() {
            return this.m_edgeTo;
        }

        public List<EdgeWrapper> getEdges() {
            return this.m_edges;
        }

        public List<EdgeWrapper> getIncomingEdges() {
            return this.m_incomingEdges;
        }

        protected void addEdge(EdgeWrapper edge) {
            assert (edge.getSourceEdge() == this);
            this.m_edges.add(edge);
        }

        protected void addIncomingEdge(EdgeWrapper edge) {
            assert (edge.getTargetEdge() == this);
            this.m_incomingEdges.add(edge);
        }

        public String getType() {
            return this.m_type;
        }

        public boolean connectsNodes() {
            return this.m_nodeEdge;
        }
    }

    public static String g_gxlTypeGraphURI = "http://www.gupro.de/GXL/gxl-1.0.gxl";

    public static final JAXBContext g_context;
    public static final Marshaller g_marshaller;
    public static final Unmarshaller g_unmarshaller;

    static {
        try {
            g_context = JAXBContext.newInstance(GxlType.class.getPackage()
                .getName());
            g_marshaller = g_context.createMarshaller();
            g_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            g_unmarshaller = g_context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static final ObjectFactory g_objectFactory = new ObjectFactory();

    public static String getElemType(TypedElementType elem) {
        String type = null;
        Map<QName,String> attrMap = elem.getType()
            .getOtherAttributes();
        for (QName attr : attrMap.keySet()) {
            if (attr.getPrefix()
                .equals("xlink")
                && attr.getLocalPart()
                    .equals("href")) {
                if (attrMap.get(attr)
                    .startsWith(g_gxlTypeGraphURI)) {
                    //Found a type attribute
                    String fullType = attrMap.get(attr);
                    if (fullType.startsWith(g_gxlTypeGraphURI + "#")) {
                        type = fullType.substring(g_gxlTypeGraphURI.length() + 1);
                        break;
                    }
                } else if (attrMap.get(attr)
                    .startsWith("#")) {
                    //Found a local type attribute
                    String fullType = attrMap.get(attr);
                    type = fullType.substring(1);
                    break;
                } else {
                    //TODO: paths should be resolved, but for now just assume the schema is provided via the TypeModel system
                    String fullType = attrMap.get(attr);
                    if (fullType.indexOf("#") != -1) {
                        type = fullType.substring(fullType.indexOf("#") + 1);
                    }
                }
            }
        }

        return type;
    }

    public static void setElemType(TypedElementType elem, String type) {
        TypeType typeType = new TypeType();
        Map<QName,String> attrMap = typeType.getOtherAttributes();
        attrMap.put(new QName("http://www.w3.org/1999/xlink", "href", "xlink"), type);

        elem.setType(typeType);
    }

    public static Map<NodeType,NodeWrapper> wrapGraph(GraphType graph) {
        Map<NodeType,NodeWrapper> nodes = new HashMap<>();
        Map<EdgeType,EdgeWrapper> edges = new HashMap<>();

        for (GraphElementType elem : graph.getNodeOrEdgeOrRel()) {
            if (elem instanceof NodeType) {
                getWrapper(nodes, (NodeType) elem);
            } else if (elem instanceof EdgeType) {
                getWrapper(nodes, edges, (EdgeType) elem);
            }
        }
        return nodes;
    }

    private static NodeWrapper getWrapper(Map<NodeType,NodeWrapper> nodes, NodeType node) {
        if (nodes.containsKey(node)) {
            return nodes.get(node);
        }
        NodeWrapper nw = new NodeWrapper(node);
        nodes.put(node, nw);

        return nw;
    }

    private static EdgeWrapper getWrapper(Map<NodeType,NodeWrapper> nodes,
        Map<EdgeType,EdgeWrapper> edges, EdgeType edge) {
        if (edges.containsKey(edge)) {
            return edges.get(edge);
        }
        EdgeWrapper ew = new EdgeWrapper(edge);
        edges.put(edge, ew);

        if (edge.getFrom() instanceof NodeType) {
            NodeType source = (NodeType) edge.getFrom();
            NodeType target = (NodeType) edge.getTo();

            NodeWrapper sourceWrapper = getWrapper(nodes, source);
            NodeWrapper targetWrapper = getWrapper(nodes, target);

            ew.setWrapper(sourceWrapper, targetWrapper);
            sourceWrapper.addEdge(ew);
            targetWrapper.addIncomingEdge(ew);
        } else if (edge.getFrom() instanceof EdgeType) {
            EdgeType source = (EdgeType) edge.getFrom();
            EdgeType target = (EdgeType) edge.getTo();

            EdgeWrapper sourceWrapper = getWrapper(nodes, edges, source);
            EdgeWrapper targetWrapper = getWrapper(nodes, edges, target);

            ew.setWrapper(sourceWrapper, targetWrapper);
            sourceWrapper.addEdge(ew);
            targetWrapper.addIncomingEdge(ew);
        }
        // else ignore edge, cannot handle it

        return ew;
    }

    public enum AttrTypeEnum {
        STRING,
        BOOL,
        INT,
        FLOAT,
        LOCATOR,
        ENUM,
        BAG,
        SET,
        SEQ,
        TUP,
        AUTO //automatically try to determine the correct type when applicable
    }

    public static Object getAttribute(TypedElementType elem, String name, AttrTypeEnum type) {
        List<AttrType> attrs = elem.getAttr();
        Object value = null;
        for (AttrType attr : attrs) {
            if (name.equals(attr.getName())) {
                if (type == AttrTypeEnum.AUTO) {
                    if (attr.getString() != null) {
                        type = AttrTypeEnum.STRING;
                    } else if (attr.isBool() != null) {
                        type = AttrTypeEnum.BOOL;
                    } else if (attr.getInt() != null) {
                        type = AttrTypeEnum.INT;
                    } else if (attr.getFloat() != null) {
                        type = AttrTypeEnum.FLOAT;
                    } else if (attr.getLocator() != null) {
                        type = AttrTypeEnum.LOCATOR;
                    } else if (attr.getEnum() != null) {
                        type = AttrTypeEnum.ENUM;
                    } else if (attr.getBag() != null) {
                        type = AttrTypeEnum.BAG;
                    } else if (attr.getSet() != null) {
                        type = AttrTypeEnum.SET;
                    } else if (attr.getSeq() != null) {
                        type = AttrTypeEnum.SEQ;
                    } else if (attr.getTup() != null) {
                        type = AttrTypeEnum.TUP;
                    }
                }

                switch (type) {
                case STRING:
                    value = attr.getString();
                    return value;
                case BOOL:
                    value = attr.isBool();
                    return value;
                case INT:
                    value = attr.getInt();
                    return value;
                case FLOAT:
                    value = attr.getFloat();
                    return value;
                case LOCATOR:
                    value = attr.getLocator();
                    return value;
                case ENUM:
                    value = attr.getEnum();
                    return value;
                case BAG:
                    value = attr.getBag();
                    return value;
                case SET:
                    value = attr.getSet();
                    return value;
                case SEQ:
                    value = attr.getSeq();
                    return value;
                case TUP:
                    value = attr.getTup();
                    return value;
                default:
                    throw Exceptions.UNREACHABLE;
                }
            }
        }
        return null;
    }

    public static void setAttribute(TypedElementType elem, String name, Object value,
        AttrTypeEnum type) {
        List<AttrType> attrs = elem.getAttr();
        AttrType attr = null;
        // If attr already exists, use that instead
        for (AttrType curAttr : attrs) {
            if (curAttr.getName()
                .equals(name)) {
                attr = curAttr;
                break;
            }
        }
        if (attr == null) {
            attr = new AttrType();
            attrs.add(attr);
        }

        // Note that enum is missing because it cannot be distinguished from a normal string
        if (type == AttrTypeEnum.AUTO) {
            if (value instanceof String) {
                type = AttrTypeEnum.STRING;
            } else if (value instanceof Boolean) {
                type = AttrTypeEnum.BOOL;
            } else if (value instanceof BigInteger) {
                type = AttrTypeEnum.INT;
            } else if (value instanceof Float) {
                type = AttrTypeEnum.FLOAT;
            } else if (value instanceof LocatorType) {
                type = AttrTypeEnum.LOCATOR;
            } else if (value instanceof BagType) {
                type = AttrTypeEnum.BAG;
            } else if (value instanceof SetType) {
                type = AttrTypeEnum.SET;
            } else if (value instanceof SeqType) {
                type = AttrTypeEnum.SEQ;
            } else if (value instanceof TupType) {
                type = AttrTypeEnum.TUP;
            }
        }

        attr.setName(name);
        switch (type) {
        case STRING:
            attr.setString((String) value);
            return;
        case BOOL:
            attr.setBool((Boolean) value);
            return;
        case INT:
            attr.setInt((BigInteger) value);
            return;
        case FLOAT:
            attr.setFloat((Float) value);
            return;
        case LOCATOR:
            attr.setLocator((LocatorType) value);
            return;
        case ENUM:
            attr.setEnum((String) value);
            return;
        case BAG:
            attr.setBag((BagType) value);
            return;
        case SET:
            attr.setSet((SetType) value);
            return;
        case SEQ:
            attr.setSeq((SeqType) value);
            return;
        case TUP:
            attr.setTup((TupType) value);
            return;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    public static Object getAttrObject(AttrType attr) {
        if (attr.isBool() != null) {
            return attr.isBool();
        } else if (attr.getInt() != null) {
            return attr.getInt();
        } else if (attr.getFloat() != null) {
            return attr.getFloat();
        } else if (attr.getString() != null) {
            return attr.getString();
        } else if (attr.getLocator() != null) {
            return attr.getLocator();
        } else if (attr.getSet() != null) {
            return attr.getSet();
        } else if (attr.getBag() != null) {
            return attr.getBag();
        } else if (attr.getSeq() != null) {
            return attr.getSeq();
        } else if (attr.getTup() != null) {
            return attr.getTup();
        } else if (attr.getEnum() != null) {
            return attr.getEnum();
        }

        return null;
    }

    public static Value getTypedAttrValue(AttrType attr, Type type) {
        Object o = getAttrObject(attr);
        if (o == null) {
            return null;
        }
        // Wrap in JAXBElement for getTypedValue
        @SuppressWarnings({"rawtypes", "unchecked"}) JAXBElement<?> elem =
            new JAXBElement(new QName("attr"), o.getClass(), o);
        return getTypedValue(elem, type);
    }

    public static Value getTypedValue(JAXBElement<?> elem, Type type) {
        Object o = elem.getValue();
        if (type instanceof DataType) {
            if (o instanceof JAXBElement<?>) {
                o = ((JAXBElement<?>) o).getValue();
            }

            if (type instanceof IntType && o instanceof BigInteger) {
                return new IntValue(((BigInteger) o).intValue());
            } else if (type instanceof RealType && o instanceof Float) {
                return new RealValue((Float) o);
            } else if (type instanceof BoolType && o instanceof Boolean) {
                return BoolValue.getInstance((Boolean) o);
            } else if (type instanceof StringType && o instanceof String) {
                return new StringValue((String) o);
            } else if (type instanceof StringType && o instanceof LocatorType) {
                return new StringValue(((LocatorType) o).toString());
            }
            //No valid conversion
            return null;
        } else if (type instanceof Container) {
            Container ct = (Container) type;
            ContainerValue cv = new ContainerValue(ct);

            switch (ct.getContainerType()) {
            case BAG:
                if (!(o instanceof BagType)) {
                    return null;
                }
                break;
            case SET:
                if (!(o instanceof SetType)) {
                    return null;
                }
                break;
            case SEQ:
                if (!(o instanceof SeqType)) {
                    return null;
                }
                break;
            case ORD:
                throw new IllegalArgumentException("ORD not supported as GXL import type");
            }

            CompositeValueType gxlContainer = (CompositeValueType) o;
            List<JAXBElement<?>> elems = gxlContainer.getBagOrSetOrSeq();
            for (JAXBElement<?> subElem : elems) {
                Value v = getTypedValue(subElem, ct.getType());
                if (v == null) {
                    return null;
                }
                cv.addValue(v);
            }
            return cv;
        } else if (type instanceof Tuple) {
            Tuple tup = (Tuple) type;

            if (!(o instanceof TupType)) {
                return null;
            }
            List<JAXBElement<?>> elems = ((TupType) o).getBagOrSetOrSeq();
            if (elems.size() != tup.getTypes()
                .size()) {
                return null;
            }
            List<Value> values = new ArrayList<>();
            int i = 0;
            for (JAXBElement<?> subElem : elems) {
                Value v = getTypedValue(subElem,
                    tup.getTypes()
                        .get(i++));
                if (v == null) {
                    return null;
                }
                values.add(v);
            }

            TupleValue tv = new TupleValue(tup, values.toArray(new Value[values.size()]));
            return tv;
        }
        return null;
    }

    public static JAXBElement<?> valueToGxl(Value val) {
        if (val instanceof BoolValue) {
            return g_objectFactory.createBool(((BoolValue) val).getValue());
        } else if (val instanceof IntValue) {
            return g_objectFactory.createInt(((IntValue) val).getValue());
        } else if (val instanceof StringValue) {
            return g_objectFactory.createString(((StringValue) val).getValue());
        } else if (val instanceof RealValue) {
            // the GXL representation can only take floats
            return g_objectFactory.createFloat(((RealValue) val).getValue()
                .floatValue());
        } else if (val instanceof EnumValue) {
            return GxlUtil.g_objectFactory.createEnum(((EnumValue) val).getValue()
                .toString());
        } else if (val instanceof CustomDataValue) {
            return GxlUtil.g_objectFactory.createString(((CustomDataValue) val).getValue());
        }

        if (val instanceof ContainerValue) {
            ContainerValue cv = (ContainerValue) val;

            CompositeValueType cvt = null;
            JAXBElement<?> elem = null;
            switch (((Container) cv.getType()).getContainerType()) {
            case SET:
                cvt = g_objectFactory.createSetType();
                elem = g_objectFactory.createSet((SetType) cvt);
                break;
            case BAG:
                cvt = g_objectFactory.createBagType();
                elem = g_objectFactory.createBag((BagType) cvt);
                break;
            case SEQ:
            case ORD:
                cvt = g_objectFactory.createSeqType();
                elem = g_objectFactory.createSeq((SeqType) cvt);
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }

            for (Value subVal : cv.getValue()) {
                cvt.getBagOrSetOrSeq()
                    .add(valueToGxl(subVal));
            }

            return elem;
        }

        if (val instanceof TupleValue) {
            TupType tup = new TupType();
            JAXBElement<TupType> tupElem = g_objectFactory.createTup(tup);

            for (Entry<Integer,Value> tupEntry : ((TupleValue) val).getValue()
                .entrySet()) {
                tup.getBagOrSetOrSeq()
                    .add(valueToGxl(tupEntry.getValue()));
            }

            return tupElem;
        }

        //CustomDataType & Object & Enum not supported

        return null;
    }
}
