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

import groove.io.conceptual.Design;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.lang.DesignExportBuilder;
import groove.io.conceptual.lang.gxl.GxlUtil.AttrTypeEnum;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
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
import groove.util.Exceptions;

import java.math.BigInteger;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import de.gupro.gxl.gxl_1_0.BagType;
import de.gupro.gxl.gxl_1_0.CompositeValueType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.SeqType;
import de.gupro.gxl.gxl_1_0.SetType;
import de.gupro.gxl.gxl_1_0.TupType;

public class DesignToGxl extends DesignExportBuilder<GxlExport,Object> {
    // Keep track of node and edge Ids
    //private int m_nextEdge = 1;
    private int m_nextNode = 1;

    /**
     * Constructs a converter from a given design to a GXL instance graph,
     * given an existing glossary-to-type converter.
     */
    public DesignToGxl(GlossaryToGxl glossToGxl, Design design) {
        super(glossToGxl.getExport(), design);
        this.m_glossToGxl = glossToGxl;
        this.m_instanceGraph =
            glossToGxl.getExport().getInstanceGraph("graph_" + design.getGlossary().getName(),
                design.getName());

    }

    private final GlossaryToGxl m_glossToGxl;
    private final GraphType m_instanceGraph;

    @Override
    public void addObject(groove.io.conceptual.value.Object object) {
        if (hasElement(object)) {
            return;
        }

        Class cmClass = (Class) object.getType();
        String classNodeId = this.m_glossToGxl.getId(cmClass);

        String id = object.getName();
        if (id == null) {
            id = getNodeId();
        }
        NodeType objectNode = createNode(id, "#" + classNodeId, cmClass.getId().getNamespace());
        setElement(object, objectNode);

        for (Entry<Field,Value> fieldEntry : object.getValue().entrySet()) {
            Value fieldValue = fieldEntry.getValue();
            // if unset value, dont set it in the Ecore model either
            if (fieldValue == null || fieldValue == groove.io.conceptual.value.Object.NIL) {
                continue;
            }

            if (this.m_glossToGxl.isAttribute(fieldEntry.getKey())) {
                JAXBElement<?> attrObject = (JAXBElement<?>) getElement(fieldValue);
                GxlUtil.setAttribute(objectNode,
                    fieldEntry.getKey().getName().toString(),
                    attrObject.getValue(),
                    AttrTypeEnum.AUTO);
            } else {
                // Create edge or edges
                String fieldEdgeId = "#" + this.m_glossToGxl.getId(fieldEntry.getKey());
                if (fieldValue instanceof ContainerValue) {
                    ContainerValue cv = (ContainerValue) fieldValue;
                    boolean isordered =
                        ((Container) cv.getType()).getContainerType() == Kind.ORD
                            || ((Container) cv.getType()).getContainerType() == Kind.SEQ;
                    int index = 0;
                    for (Value subValue : cv.getValue()) {
                        NodeType valueNode = (NodeType) getElement(subValue);
                        EdgeType edge = createEdge(objectNode, valueNode, fieldEdgeId);
                        if (isordered) {
                            edge.setToorder(BigInteger.valueOf(index++));
                        }
                    }
                } else {
                    NodeType valueNode = (NodeType) getElement(fieldValue);
                    createEdge(objectNode, valueNode, fieldEdgeId);
                }
            }
        }
    }

    @Override
    public void addRealValue(RealValue realval) {
        if (hasElement(realval)) {
            return;
        }

        JAXBElement<Float> floatElem =
            GxlUtil.g_objectFactory.createFloat(new Float(realval.getValue()));
        setElement(realval, floatElem);
    }

    @Override
    public void addStringValue(StringValue stringval) {
        if (hasElement(stringval)) {
            return;
        }

        JAXBElement<String> stringElem = GxlUtil.g_objectFactory.createString(stringval.getValue());
        setElement(stringval, stringElem);
    }

    @Override
    public void addIntValue(IntValue intval) {
        if (hasElement(intval)) {
            return;
        }

        JAXBElement<BigInteger> intElem = GxlUtil.g_objectFactory.createInt(intval.getValue());
        setElement(intval, intElem);
    }

    @Override
    public void addBoolValue(BoolValue boolval) {
        if (hasElement(boolval)) {
            return;
        }

        JAXBElement<Boolean> boolElem =
            GxlUtil.g_objectFactory.createBool(new Boolean(boolval.getValue()));
        setElement(boolval, boolElem);
    }

    @Override
    public void addEnumValue(EnumValue val) {
        if (hasElement(val)) {
            return;
        }

        JAXBElement<String> enumElem =
            GxlUtil.g_objectFactory.createEnum(val.getValue().toString());
        setElement(val, enumElem);
    }

    @Override
    public void addContainerValue(ContainerValue val, String base) {
        //TODO: check if for an attribute. Reference should be handled directly
        if (hasElement(val)) {
            return;
        }

        CompositeValueType cntType = null;
        JAXBElement<?> cntElem = null;

        switch (((Container) val.getType()).getContainerType()) {
        case SET:
            cntType = GxlUtil.g_objectFactory.createSetType();
            cntElem = GxlUtil.g_objectFactory.createSet((SetType) cntType);
            break;
        case BAG:
            cntType = GxlUtil.g_objectFactory.createBagType();
            cntElem = GxlUtil.g_objectFactory.createBag((BagType) cntType);
            break;
        case ORD:
        case SEQ:
            cntType = GxlUtil.g_objectFactory.createSeqType();
            cntElem = GxlUtil.g_objectFactory.createSeq((SeqType) cntType);
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        setElement(val, cntElem);

        for (Value subVal : val.getValue()) {
            JAXBElement<?> cntValue = (JAXBElement<?>) getElement(subVal);
            cntType.getBagOrSetOrSeq().add(cntValue);
        }
    }

    @Override
    public void addTupleValue(TupleValue val) {
        if (hasElement(val)) {
            return;
        }

        if (this.m_glossToGxl.isAttribute(val.getType())) {
            TupType tupType = GxlUtil.g_objectFactory.createTupType();
            JAXBElement<TupType> tupElem = GxlUtil.g_objectFactory.createTup(tupType);
            setElement(val, tupElem);

            for (Entry<Integer,Value> entry : val.getValue().entrySet()) {
                JAXBElement<?> tupValue = (JAXBElement<?>) getElement(entry.getValue());
                tupType.getBagOrSetOrSeq().add(tupValue);
            }
        } else {
            // Tuple represented by class
            Class cmClass = this.m_glossToGxl.getTupleClass((Tuple) val.getType());
            groove.io.conceptual.value.Object o =
                new groove.io.conceptual.value.Object(cmClass, null);

            for (Entry<Integer,Value> entry : val.getValue().entrySet()) {
                o.setFieldValue(cmClass.getField(Name.getName("_" + entry.getKey())),
                    entry.getValue());
            }

            setElement(val, getElement(o));
        }
    }

    @Override
    public void addCustomDataValue(CustomDataValue val) {
        if (hasElement(val)) {
            return;
        }

        //DataValue treated as string in GXL
        JAXBElement<String> stringElem = GxlUtil.g_objectFactory.createString(val.getValue());
        setElement(val, stringElem);
    }

    private NodeType createNode(String id, String type, Id packageId) {
        NodeType newNode = new NodeType();
        newNode.setId(id);
        if (type != null) {
            GxlUtil.setElemType(newNode, getExport().getTypePath() + type);
        }

        //getPackageGraph(packageId).getNodeOrEdgeOrRel().add(newNode);
        // Use the main graph, no subgraphs
        this.m_instanceGraph.getNodeOrEdgeOrRel().add(newNode);

        return newNode;
    }

    /*
    private GraphType getPackageGraph(Id packageId) {
        if (m_packageGraphs.containsKey(packageId)) {
            return m_packageGraphs.get(packageId);
        } else {
            // Create subgraph
            GraphType subGraph = new GraphType();
            subGraph.setId(packageId.toString());
            subGraph.setEdgeids(true);
            subGraph.setEdgemode(EdgemodeType.DEFAULTDIRECTED);
            GxlUtil.setElemType(subGraph, "#" + m_typeToGxl.getId(packageId));
            m_packageGraphs.put(packageId, subGraph);

            if (packageId != Id.ROOT) {// && packageId.getNamespace() != Id.ROOT) {
                // Create intermediate node
                NodeType intermediateNode = new NodeType();
                intermediateNode.setId(packageId.toString());
                GxlUtil.setElemType(intermediateNode, "#" + (packageId.getNamespace() == Id.ROOT ? "ROOT" : packageId.getNamespace().toString()));

                // Insert node into parent graph, and subgraph into node
                GraphType parentGraph = getPackageGraph(packageId.getNamespace());
                parentGraph.getNodeOrEdgeOrRel().add(intermediateNode);
                intermediateNode.getGraph().add(subGraph);
            } else {
                //m_instanceGraph.getNodeOrEdgeOrRel().add(subGraph);
            }

            return subGraph;
        }
    }
    */

    private EdgeType createEdge(NodeType from, NodeType to, String type) {
        EdgeType newEdge = new EdgeType();
        newEdge.setFrom(from);
        newEdge.setTo(to);
        // No edge ID in instances, not used and may conflict with type
        //newEdge.setId(getEdgeId());
        if (type != null) {
            GxlUtil.setElemType(newEdge, type);
        }

        //TODO: if subgraphs are used, do it here as well
        this.m_instanceGraph.getNodeOrEdgeOrRel().add(newEdge);

        return newEdge;
    }

    /*private String getEdgeId() {
        return "e" + m_nextEdge++;
    }*/

    private String getNodeId() {
        return "n" + this.m_nextNode++;
    }
}
