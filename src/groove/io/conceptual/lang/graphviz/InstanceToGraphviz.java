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
package groove.io.conceptual.lang.graphviz;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.PortNode;

import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.InstanceExporter;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.conceptual.value.Value;
import groove.io.external.PortException;

@SuppressWarnings("javadoc")
public class InstanceToGraphviz extends InstanceExporter<Node> {
    private Map<Id,Graph> m_packageGraphs = new HashMap<>();
    private Map<InstanceModel,Graph> m_instanceGraphs = new HashMap<>();
    private TypeToGraphviz m_typeToGraphviz;
    private GraphvizResource m_resource;

    private int m_nodeId;

    public InstanceToGraphviz(TypeToGraphviz typeToGraphviz) {
        this.m_typeToGraphviz = typeToGraphviz;

        this.m_resource = (GraphvizResource) this.m_typeToGraphviz.getResource();
    }

    @Override
    public void addInstanceModel(InstanceModel instanceModel) throws PortException {
        int timer = Timer.start("IM to DOT");
        Graph instanceGraph = this.m_resource.getInstanceGraph(instanceModel.getQualName());
        this.m_instanceGraphs.put(instanceModel, instanceGraph);
        this.m_packageGraphs.put(Id.ROOT, instanceGraph);

        visitInstanceModel(instanceModel);
        Timer.stop(timer);
    }

    @Override
    public void visit(groove.io.conceptual.value.Object object, String param) {
        if (hasElement(object)) {
            return;
        }

        Graph objectGraph = getPackageGraph(((Class) object.getType()).getId()
            .getNamespace());
        Node objectNode = new Node();
        objectGraph.addNode(objectNode);

        setElement(object, objectNode);

        objectNode.setId(new com.alexmerz.graphviz.objects.Id());
        objectNode.getId()
            .setId(getElementId());

        String objectLabel = object.getName();
        if (objectLabel == null) {
            objectLabel = ((Class) object.getType()).getId()
                .getName()
                .toString();
        }
        objectNode.setAttribute("label", objectLabel);

        for (Entry<Field,Value> entry : object.getValue()
            .entrySet()) {
            Type fieldType = entry.getKey()
                .getType();
            boolean isContainer = false;
            if (fieldType instanceof Container) {
                fieldType = ((Container) fieldType).getType();
                isContainer = true;
            }

            String fieldName = entry.getKey()
                .getName()
                .toString();
            // For edges, replace fieldname with empty if it has been generated
            if (fieldType instanceof Class || fieldType instanceof Tuple) {
                if (fieldName.matches("edge[0-9]*")) {
                    fieldName = null;
                }
            }

            if (fieldType instanceof Class || fieldType instanceof Tuple) {
                // Add edge
                if (isContainer) {
                    ContainerValue cVal = (ContainerValue) entry.getValue();
                    for (Value v : cVal.getValue()) {
                        Edge fieldEdge = new Edge();
                        Node fieldNode = getElement(v);
                        fieldEdge.setSource(new PortNode(objectNode));
                        fieldEdge.setTarget(new PortNode(fieldNode));
                        if (fieldName != null) {
                            fieldEdge.setAttribute("label", fieldName);
                        }
                        fieldEdge.setType(Graph.DIRECTED);

                        getPackageGraph(Id.ROOT).addEdge(fieldEdge);
                    }
                } else {
                    Edge fieldEdge = new Edge();
                    Node fieldNode = getElement(entry.getValue());
                    fieldEdge.setSource(new PortNode(objectNode));
                    fieldEdge.setTarget(new PortNode(fieldNode));
                    if (fieldName != null) {
                        fieldEdge.setAttribute("label", fieldName);
                    }
                    fieldEdge.setType(Graph.DIRECTED);

                    getPackageGraph(Id.ROOT).addEdge(fieldEdge);
                }
                // recursive container not supported
            } else if (!(fieldType instanceof Container)) {
                // Add label, if isContainer, then use [ ] notation
                String valueLabel = "";
                if (isContainer) {
                    ContainerValue cVal = (ContainerValue) entry.getValue();
                    valueLabel += "[";
                    boolean first = true;
                    for (Value v : cVal.getValue()) {
                        if (!first) {
                            valueLabel += ", ";
                        }
                        valueLabel += v.toString();
                        first = false;
                    }
                    valueLabel += "]";
                } else {
                    valueLabel += entry.getValue()
                        .toString();
                }
                objectNode.setAttribute(fieldName, valueLabel);
            }
        }
    }

    @Override
    public void visit(TupleValue tupleval, String param) {
        if (hasElement(tupleval)) {
            return;
        }

        Graph tupleGraph = getPackageGraph(Id.ROOT);
        Node tupleNode = new Node();
        tupleGraph.addNode(tupleNode);
        tupleNode.setId(new com.alexmerz.graphviz.objects.Id());
        tupleNode.getId()
            .setId(getElementId());

        tupleNode.setAttribute("shape", "record");
        String label = "";

        boolean first = true;
        for (Entry<Integer,Value> entry : tupleval.getValue()
            .entrySet()) {
            Type type = ((Tuple) tupleval.getType()).getTypes()
                .get(entry.getKey());
            boolean isContainer = type instanceof Container;

            if (!first) {
                label += "|";
            }
            if (isNodeType(type)) {
                String port = "<t" + entry.getKey() + ">";
                label += port;
                if (isContainer) {
                    ContainerValue cVal = (ContainerValue) entry.getValue();
                    for (Value v : cVal.getValue()) {
                        Edge valueEdge = new Edge();
                        Node valueNode = getElement(v);
                        valueEdge.setSource(new PortNode(tupleNode, port));
                        valueEdge.setTarget(new PortNode(valueNode));
                        valueEdge.setAttribute("label", "<" + entry.getKey() + ">");
                        valueEdge.setType(Graph.DIRECTED);

                        getPackageGraph(Id.ROOT).addEdge(valueEdge);
                    }
                } else {
                    Node valueNode = getElement(entry.getValue());
                    Edge valueEdge = new Edge();
                    valueEdge.setSource(new PortNode(tupleNode));
                    valueEdge.setTarget(new PortNode(valueNode));
                    valueEdge.setAttribute("label", "<" + entry.getKey() + ">");
                    valueEdge.setType(Graph.DIRECTED);

                    getPackageGraph(Id.ROOT).addEdge(valueEdge);
                }
            } else {
                label += entry.getValue()
                    .toString();
            }
            label += type.toString();
            first = false;
        }

        tupleNode.setAttribute("label", label);

        setElement(tupleval, tupleNode);
    }

    @Override
    public void visit(CustomDataValue dataval, String param) {
        // Not directly translated

    }

    @Override
    public void visit(AbstractProperty abstractProperty, String param) {
        // Not in instance
    }

    @Override
    public void visit(ContainmentProperty containmentProperty, String param) {
        // Not in instance
    }

    @Override
    public void visit(IdentityProperty identityProperty, String param) {
        // Not in instance
    }

    @Override
    public void visit(KeysetProperty keysetProperty, String param) {
        // Not in instance
    }

    @Override
    public void visit(OppositeProperty oppositeProperty, String param) {
        // Not in instance
    }

    @Override
    public void visit(DefaultValueProperty defaultValueProperty, String param) {
        // Not in instance
    }

    private Graph getPackageGraph(Id namespace) {
        if (this.m_packageGraphs.containsKey(namespace)) {
            return this.m_packageGraphs.get(namespace);
        }

        Graph parent = getPackageGraph(namespace.getNamespace());
        Graph packageGraph = new Graph();
        parent.getSubgraphs()
            .add(packageGraph);

        com.alexmerz.graphviz.objects.Id graphId = new com.alexmerz.graphviz.objects.Id();
        graphId.setId("\"cluster_" + namespace.getName()
            .toString() + "\"");
        packageGraph.addAttribute("label", namespace.getName()
            .toString());
        packageGraph.setId(graphId);

        this.m_packageGraphs.put(namespace, packageGraph);

        return packageGraph;
    }

    private boolean isNodeType(Type type) {
        if (type instanceof Container) {
            type = ((Container) type).getType();
        }

        if (type instanceof Class || type instanceof Tuple) {
            return true;
        }

        return false;
    }

    private String getElementId() {
        return "N" + this.m_nodeId++;
    }

}
