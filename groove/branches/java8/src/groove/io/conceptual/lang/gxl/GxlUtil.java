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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

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

/** Utilities for handling GXL files. */
public class GxlUtil {
    /** Wrapper of a GXL node. */
    public static class NodeWrapper {
        /** Constructs a wrapper for a given node. */
        public NodeWrapper(NodeType node) {
            this.m_node = node;
            this.m_type = GxlUtil.getElemType(node);
            assert (this.m_type != null);
        }

        /** Returns the wrapped GXL node. */
        public NodeType getNode() {
            return this.m_node;
        }

        private final NodeType m_node;

        /** Returns a string description of the type of this node. */
        public String getType() {
            return this.m_type;
        }

        private final String m_type;

        /** Adds an outgoing edge to this node. */
        protected void addEdge(EdgeWrapper edge) {
            assert (edge.getSource() == this);
            this.m_edges.add(edge);
        }

        /** Returns the set of outgoing edges of this node. */
        public List<EdgeWrapper> getEdges() {
            return this.m_edges;
        }

        private List<EdgeWrapper> m_edges = new ArrayList<EdgeWrapper>();

        /** Adds an incoming edge to this node. */
        protected void addIncomingEdge(EdgeWrapper edge) {
            assert (edge.getTarget() == this);
            this.m_incomingEdges.add(edge);
        }

        /** Returns the set of incoming edges of this node. */
        public List<EdgeWrapper> getIncomingEdges() {
            return this.m_incomingEdges;
        }

        private List<EdgeWrapper> m_incomingEdges = new ArrayList<EdgeWrapper>();

        /** Sorts the outgoing edges of this node according to label and multiplicity. */
        public void sortEdges() {
            Collections.sort(this.m_edges, new Comparator<EdgeWrapper>() {
                @Override
                public int compare(EdgeWrapper ew1, EdgeWrapper ew2) {
                    int stringCompare = ew1.getType().compareTo(ew2.getType());
                    if (stringCompare == 0) {
                        BigInteger eo1 = ew1.getEdge().getToorder();
                        BigInteger eo2 = ew2.getEdge().getToorder();
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

    /** Wrapper of a GXL edge. */
    public static class EdgeWrapper {
        /** Constructs a wrapper for a given GXL edge. */
        public EdgeWrapper(EdgeType edge) {
            this.m_edge = edge;
            this.m_type = GxlUtil.getElemType(edge);
            assert (this.m_type != null);
        }

        /** Returns the wrapped GXL edge. */
        public EdgeType getEdge() {
            return this.m_edge;
        }

        private final EdgeType m_edge;

        /** Returns a string representation of the edge type. */
        public String getType() {
            return this.m_type;
        }

        private final String m_type;

        /** Sets the source and target nodes for this edge. */
        public void setWrapper(NodeWrapper nodeFrom, NodeWrapper nodeTo) {
            this.m_nodeFrom = nodeFrom;
            this.m_nodeTo = nodeTo;
            this.m_nodeEdge = true;
        }

        /** Returns the source node of this edge, if any. */
        public NodeWrapper getSource() {
            return this.m_nodeFrom;
        }

        /** Returns the target node of this edge, if any. */
        public NodeWrapper getTarget() {
            return this.m_nodeTo;
        }

        private NodeWrapper m_nodeFrom;
        private NodeWrapper m_nodeTo;

        /** Sets source and target edges for this edge. */
        public void setWrapper(EdgeWrapper edgeFrom, EdgeWrapper edgeTo) {
            this.m_edgeFrom = edgeFrom;
            this.m_edgeTo = edgeTo;
            this.m_nodeEdge = false;
        }

        /** Returns the source edge of this edge, if any. */
        public EdgeWrapper getSourceEdge() {
            return this.m_edgeFrom;
        }

        /** Returns the target edge of this edge, if any. */
        public EdgeWrapper getTargetEdge() {
            return this.m_edgeTo;
        }

        private EdgeWrapper m_edgeFrom;
        private EdgeWrapper m_edgeTo;

        /** Adds an outgoing edge to this edge. */
        protected void addEdge(EdgeWrapper edge) {
            assert (edge.getSourceEdge() == this);
            this.m_edges.add(edge);
        }

        /** Returns the list of outgoing edges of this edge. */
        public List<EdgeWrapper> getEdges() {
            return this.m_edges;
        }

        private final List<EdgeWrapper> m_edges = new ArrayList<>();

        /** Returns the list of incoming edges of this edge. */
        public List<EdgeWrapper> getIncomingEdges() {
            return this.m_incomingEdges;
        }

        private final List<EdgeWrapper> m_incomingEdges = new ArrayList<>();

        /** Adds an incoming edge to this edge. */
        protected void addIncomingEdge(EdgeWrapper edge) {
            assert (edge.getTargetEdge() == this);
            this.m_incomingEdges.add(edge);
        }

        /** Indicates if this edge connects nodes; if false, it connects edges. */
        public boolean connectsNodes() {
            return this.m_nodeEdge;
        }

        /** True if connecting nodes, false if connecting edges. */
        private boolean m_nodeEdge;
    }

    /** URI of the GXL schema. */
    public static final String g_gxlTypeGraphURI = "http://www.gupro.de/GXL/gxl-1.0.gxl";

    /** Marshaller for the GXL documents. */
    public static final Marshaller g_marshaller;
    /** Unmarshaller for the GXL documents. */
    public static final Unmarshaller g_unmarshaller;
    static {
        try {
            JAXBContext g_context = JAXBContext.newInstance(GxlType.class.getPackage().getName());
            g_marshaller = g_context.createMarshaller();
            g_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            g_unmarshaller = g_context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /** Globally fixed GXL factory. */
    public static final ObjectFactory g_objectFactory = new ObjectFactory();

    /** Returns a type description for a given GXL element. */
    public static String getElemType(TypedElementType elem) {
        String type = null;
        Map<QName,String> attrMap = elem.getType().getOtherAttributes();
        for (QName attr : attrMap.keySet()) {
            if (attr.getPrefix().equals("xlink") && attr.getLocalPart().equals("href")) {
                if (attrMap.get(attr).startsWith(g_gxlTypeGraphURI)) {
                    //Found a type attribute
                    String fullType = attrMap.get(attr);
                    if (fullType.startsWith(g_gxlTypeGraphURI + "#")) {
                        type = fullType.substring(g_gxlTypeGraphURI.length() + 1);
                        break;
                    }
                } else if (attrMap.get(attr).startsWith("#")) {
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

    /** Sets a type value for a GXL element type.*/
    public static void setElemType(TypedElementType elem, String type) {
        TypeType typeType = new TypeType();
        Map<QName,String> attrMap = typeType.getOtherAttributes();
        attrMap.put(new QName("http://www.w3.org/1999/xlink", "href", "xlink"), type);
        elem.setType(typeType);
    }

    /** Extracts a mapping from GXL nodes to node wrappers from a GXL graph. */
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
        NodeWrapper result = nodes.get(node);
        if (result == null) {
            result = new NodeWrapper(node);
            nodes.put(node, result);
        }
        return result;
    }

    private static EdgeWrapper getWrapper(Map<NodeType,NodeWrapper> nodes,
        Map<EdgeType,EdgeWrapper> edges, EdgeType edge) {
        EdgeWrapper result = edges.get(edge);
        if (result == null) {
            result = new EdgeWrapper(edge);
            edges.put(edge, result);

            if (edge.getFrom() instanceof NodeType) {
                NodeType source = (NodeType) edge.getFrom();
                NodeType target = (NodeType) edge.getTo();

                NodeWrapper sourceWrapper = getWrapper(nodes, source);
                NodeWrapper targetWrapper = getWrapper(nodes, target);

                result.setWrapper(sourceWrapper, targetWrapper);
                sourceWrapper.addEdge(result);
                targetWrapper.addIncomingEdge(result);
            } else if (edge.getFrom() instanceof EdgeType) {
                EdgeType source = (EdgeType) edge.getFrom();
                EdgeType target = (EdgeType) edge.getTo();

                EdgeWrapper sourceWrapper = getWrapper(nodes, edges, source);
                EdgeWrapper targetWrapper = getWrapper(nodes, edges, target);

                result.setWrapper(sourceWrapper, targetWrapper);
                sourceWrapper.addEdge(result);
                targetWrapper.addIncomingEdge(result);
            }
            // else ignore edge, cannot handle it
        }
        return result;
    }

    /** Attribute type enum reflecting the GXL attribute types. */
    public enum AttrTypeEnum {
        /** String type. */
        STRING(AttrType::getString, String.class),
        /** Boolean type. */
        BOOL(AttrType::isBool, Boolean.class),
        /** Integer type. */
        INT(AttrType::getInt, BigInteger.class),
        /** Floating number type. */
        FLOAT(AttrType::getFloat, Float.class),
        /** Location type. */
        LOCATOR(AttrType::getLocator, LocatorType.class),
        /** Enumeration type. */
        ENUM(AttrType::getEnum, null),
        /** Multiset type. */
        BAG(AttrType::getBag, BagType.class),
        /** Set type. */
        SET(AttrType::getSet, SetType.class),
        /** Sequence type. */
        SEQ(AttrType::getSeq, SeqType.class),
        /** Tuple type. */
        TUP(AttrType::getTup, TupType.class),
        /** Automatically try to determine the correct type. */
        AUTO(null, null), ;

        AttrTypeEnum(Function<AttrType,Object> f, Class<?> type) {
            this.f = f;
            this.type = type;
        }

        /** Tests if a given GXL attribute is of this type. */
        boolean isTypeOf(AttrType attr) {
            return this.f != null && this.f.apply(attr) != null;
        }

        /** Retrieves the value of a given GXL attribute, if it is of this type. */
        Object getValue(AttrType attr) {
            return this.f == null ? null : this.f.apply(attr);
        }

        private final Function<AttrType,Object> f;

        /** Tests if a given object value is of this type. */
        boolean isTypeOf(Object o) {
            return this.type != null && this.type.isInstance(o);
        }

        private final Class<?> type;
    }

    /** Retrieves an attribute value from a given GXL element. */
    public static Object getAttribute(TypedElementType elem, String name, AttrTypeEnum type) {
        List<AttrType> attrs = elem.getAttr();
        Object value = null;
        Optional<AttrType> attr = attrs.stream().filter(a -> name.equals(a.getName())).findAny();
        if (attr.isPresent()) {
            if (type == AttrTypeEnum.AUTO) {
                type =
                    Arrays.stream(AttrTypeEnum.values())
                        .filter(t -> t.isTypeOf(attr.get()))
                        .findAny()
                        .get();
            }
            value = type.getValue(attr.get());
        }
        return value;
    }

    /** Sets an attribute value for a given GXL element. */
    public static void setAttribute(TypedElementType elem, String name, Object value,
        AttrTypeEnum type) {
        List<AttrType> attrs = elem.getAttr();
        // find the attribute with the right name, if any
        Optional<AttrType> optAttr = attrs.stream().filter(a -> a.getName().equals(name)).findAny();
        // construct a new attribute if there was none
        AttrType attr = optAttr.orElseGet(() -> {
            AttrType newAttr = new AttrType();
            newAttr.setName(name);
            attrs.add(newAttr);
            return newAttr;
        });
        if (type == AttrTypeEnum.AUTO) {
            // Note that enum cannot be detected because it cannot be distinguished from a normal string
            type =
                Arrays.stream(AttrTypeEnum.values()).filter(a -> a.isTypeOf(value)).findAny().get();
        }
        switch (type) {
        case STRING:
            attr.setString((String) value);
            break;
        case BOOL:
            attr.setBool((Boolean) value);
            break;
        case INT:
            attr.setInt((BigInteger) value);
            break;
        case FLOAT:
            attr.setFloat((Float) value);
            break;
        case LOCATOR:
            attr.setLocator((LocatorType) value);
            break;
        case ENUM:
            attr.setEnum((String) value);
            break;
        case BAG:
            attr.setBag((BagType) value);
            break;
        case SET:
            attr.setSet((SetType) value);
            break;
        case SEQ:
            attr.setSeq((SeqType) value);
            break;
        case TUP:
            attr.setTup((TupType) value);
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the design value of a GXL attribute. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Value getTypedAttrValue(AttrType attr, Type type) {
        return getAttrObject(attr).map(o -> {
            // Wrap in JAXBElement for getTypedValue
            JAXBElement<?> elem = new JAXBElement(new QName("attr"), o.getClass(), o);
            return getTypedValue(elem, type);
        }).orElse(null);
    }

    /** Returns the object value of a GXL attribute. */
    public static Optional<Object> getAttrObject(AttrType attr) {
        return Arrays.stream(AttrTypeEnum.values())
            .filter(t -> t.isTypeOf(attr))
            .map(t -> t.getValue(attr))
            .findAny();
    }

    private static Value getTypedValue(JAXBElement<?> elem, Type type) {
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
            if (elems.size() != tup.getTypes().size()) {
                return null;
            }
            List<Value> values = new ArrayList<Value>();
            int i = 0;
            for (JAXBElement<?> subElem : elems) {
                Value v = getTypedValue(subElem, tup.getTypes().get(i++));
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

    /** Converts a design value into a GXL value. */
    public static JAXBElement<?> valueToGxl(Value val) {
        if (val instanceof BoolValue) {
            return g_objectFactory.createBool(((BoolValue) val).getValue());
        } else if (val instanceof IntValue) {
            return g_objectFactory.createInt(((IntValue) val).getValue());
        } else if (val instanceof StringValue) {
            return g_objectFactory.createString(((StringValue) val).getValue());
        } else if (val instanceof RealValue) {
            // the GXL representation can only take floats
            return g_objectFactory.createFloat(new Float(((RealValue) val).getValue()));
        } else if (val instanceof EnumValue) {
            return GxlUtil.g_objectFactory.createEnum(((EnumValue) val).getValue().toString());
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
                cvt.getBagOrSetOrSeq().add(valueToGxl(subVal));
            }

            return elem;
        }

        if (val instanceof TupleValue) {
            TupType tup = new TupType();
            JAXBElement<TupType> tupElem = g_objectFactory.createTup(tup);

            for (Entry<Integer,Value> tupEntry : ((TupleValue) val).getValue().entrySet()) {
                tup.getBagOrSetOrSeq().add(valueToGxl(tupEntry.getValue()));
            }

            return tupElem;
        }

        //CustomDataType & Object & Enum not supported

        return null;
    }
}
