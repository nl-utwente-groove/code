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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import de.gupro.gxl.gxl_1_0.NodeType;
import groove.grammar.QualName;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.InstanceImporter;
import groove.io.conceptual.lang.gxl.GxlUtil.EdgeWrapper;
import groove.io.conceptual.lang.gxl.GxlUtil.NodeWrapper;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.Value;

@SuppressWarnings("javadoc")
public class GxlToInstance extends InstanceImporter {
    private Map<QualName,GraphType> m_instanceGraphs = new HashMap<>();

    private GxlToType m_gxlToType;

    // Map to keep track of nodes and their objects
    private Map<NodeType,Object> m_nodeValues = new HashMap<>();
    // Also for complex edges
    private Map<EdgeType,Object> m_edgeValues = new HashMap<>();

    public GxlToInstance(GxlToType gxlToType, String instanceModel) throws ImportException {
        this.m_gxlToType = gxlToType;

        // Load the GXL
        try (FileInputStream in = new FileInputStream(instanceModel)) {
            int timer = Timer.cont("Load GXL");
            @SuppressWarnings("unchecked") JAXBElement<GxlType> doc =
                (JAXBElement<GxlType>) GxlUtil.g_unmarshaller.unmarshal(in);
            in.close();
            for (GraphType g : doc.getValue()
                .getGraph()) {
                String type = GxlUtil.getElemType(g);
                if (!("gxl-1.0".equals(type))) {
                    this.m_instanceGraphs.put(QualName.parse(g.getId()), g);
                }
            }
            Timer.stop(timer);
        } catch (JAXBException e) {
            throw new ImportException(e);
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        // Preload Models
        int timer = Timer.start("GXL to IM");
        for (QualName instanceName : this.m_instanceGraphs.keySet()) {
            getInstanceModel(instanceName);
        }
        Timer.stop(timer);
    }

    @Override
    public InstanceModel getInstanceModel(QualName modelName) {
        InstanceModel result = super.getInstanceModel(modelName);
        if (result != null) {
            return result;
        }
        if (this.m_instanceGraphs.containsKey(modelName)) {
            // Find the type of the graph
            QualName type =
                QualName.parse(GxlUtil.getElemType(this.m_instanceGraphs.get(modelName)));
            TypeModel mm = this.m_gxlToType.getTypeModel(type);
            if (mm == null) {
                return null;
            }

            InstanceModel m = new InstanceModel(mm, modelName);

            visitGraph(m, this.m_instanceGraphs.get(modelName));
            addInstanceModel(m);
            //System.out.println("GXL instance elem: " + count);
            return m;
        }

        return null;
    }

    private void visitGraph(InstanceModel m, GraphType graph) {
        Map<NodeType,NodeWrapper> nodes = GxlUtil.wrapGraph(graph);
        String type = GxlUtil.getElemType(graph);
        Id graphId = this.m_gxlToType.getGraphId(type);//Id.getId(Id.ROOT, Name.getName(type));
        for (Entry<NodeType,NodeWrapper> entry : nodes.entrySet()) {
            NodeWrapper node = entry.getValue();

            if (node.getNode()
                .getGraph()
                .isEmpty()) {
                Object cmObject = visitObject(m, node, graphId);
                m.addObject(cmObject);
            } else {
                // Found a subgraph in a Node. Ignore the node, treat subgraph as namespace (this will be handled automatically by GxlToType)
                for (GraphType subGraph : node.getNode()
                    .getGraph()) {
                    visitGraph(m, subGraph);
                }
            }

        }
    }

    private Object visitObject(InstanceModel m, NodeWrapper nodeWrapper, Id graphId) {
        NodeType node = nodeWrapper.getNode();

        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            return val;
        }

        String type = GxlUtil.getElemType(node);
        Name name = Name.getName(node.getId());

        //Class cmClass = m.getTypeModel().getClass(Id.getId(graphId, Name.getName(type)));
        Class cmClass = (Class) this.m_gxlToType.getIdType(type);
        if (cmClass == null) {
            return null;
        }

        Object o = new Object(cmClass, name);

        this.m_nodeValues.put(node, o);

        List<AttrType> attrs = node.getAttr();

        // Handle attributes
        for (AttrType attr : attrs) {
            String attrName = attr.getName();
            Field f = cmClass.getFieldSuper(Name.getName(attrName));
            assert(f != null);

            Value v = GxlUtil.getTypedAttrValue(attr, f.getType());
            assert(v != null);

            o.setFieldValue(f, v);
        }

        // Handle references
        Map<String,Value> currentValues = new HashMap<>();

        // Sort the edges in case some of them are ordered
        nodeWrapper.sortEdges();
        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            String edgeType = GxlUtil.getElemType(ew.getEdge());
            if (this.m_gxlToType.isComplex(edgeType)) {
                visitEdge(m, ew, graphId);
                continue;
            }

            Object oTarget = visitObject(m, ew.getTarget(), graphId);
            String refName = GxlUtil.getElemType(ew.getEdge());
            Field f = this.m_gxlToType.getIdField(refName);
            assert(f != null);

            Value v = null;
            if (currentValues.containsKey(refName)) {
                v = currentValues.get(refName);
            } else {
                if (f.getType() instanceof Container) {
                    v = new ContainerValue((Container) f.getType());
                } else {
                    v = oTarget;
                }
                currentValues.put(refName, v);
            }

            if (f.getType() instanceof Container) {
                ContainerValue cv = (ContainerValue) v;
                cv.addValue(oTarget);
            }

            o.setFieldValue(f, v);
        }

        return o;
    }

    private Object visitEdge(InstanceModel m, EdgeWrapper edgeWrapper, Id graphId) {
        EdgeType edge = edgeWrapper.getEdge();

        if (this.m_edgeValues.containsKey(edge)) {
            Object val = this.m_edgeValues.get(edge);
            return val;
        }

        String type = GxlUtil.getElemType(edge);
        Name name = Name.getName(edge.getId());

        Class cmClass = (Class) this.m_gxlToType.getIdType(type);
        Object o = new Object(cmClass, name);

        Object oSource = null;
        Object oTarget = null;

        if (edgeWrapper.connectsNodes()) {
            oSource = visitObject(m, edgeWrapper.getSource(), graphId);
            oTarget = visitObject(m, edgeWrapper.getTarget(), graphId);
        } else {
            oSource = visitEdge(m, edgeWrapper.getSourceEdge(), graphId);
            oTarget = visitEdge(m, edgeWrapper.getTargetEdge(), graphId);
        }

        Field fieldFrom = cmClass.getField(Name.getName("from"));
        Field fieldTo = cmClass.getField(Name.getName("to"));
        assert(fieldFrom != null && fieldTo != null);

        if (fieldFrom.getType() instanceof Container) {
            ContainerValue cv = new ContainerValue((Container) fieldFrom.getType());
            o.setFieldValue(fieldFrom, cv);
            cv.addValue(oSource);
        } else {
            o.setFieldValue(fieldFrom, oSource);
        }
        if (fieldTo.getType() instanceof Container) {
            ContainerValue cv = new ContainerValue((Container) fieldTo.getType());
            o.setFieldValue(fieldTo, cv);
            cv.addValue(oTarget);
        } else {
            o.setFieldValue(fieldTo, oTarget);
        }

        List<AttrType> attrs = edge.getAttr();

        // Handle attributes
        for (AttrType attr : attrs) {
            String attrName = attr.getName();
            Field f = cmClass.getFieldSuper(Name.getName(attrName));
            assert(f != null);

            Value v = GxlUtil.getTypedAttrValue(attr, f.getType());
            assert(v != null);

            o.setFieldValue(f, v);
        }

        // Sort the edges in case some of them are ordered
        for (EdgeWrapper ew : edgeWrapper.getEdges()) {
            visitEdge(m, ew, graphId);
        }

        m.addObject(o);
        this.m_edgeValues.put(edge, o);

        return o;
    }
}
