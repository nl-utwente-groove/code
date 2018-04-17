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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.PortNode;

import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.TypeExporter;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.external.PortException;

@SuppressWarnings("javadoc")
public class TypeToGraphviz extends TypeExporter<Node> {
    private Map<Id,Graph> m_packageGraphs = new HashMap<>();
    private Map<TypeModel,Graph> m_typeGraphs = new HashMap<>();
    private TypeModel m_currentTypeModel;

    private int m_nodeId;

    private GraphvizResource m_resource;

    public TypeToGraphviz(GraphvizResource resource) {
        this.m_resource = resource;
    }

    @Override
    public void addTypeModel(TypeModel typeModel) throws PortException {
        int timer = Timer.start("TM to DOT");
        Graph typeGraph = this.m_resource.getTypeGraph(typeModel.getQualName());
        this.m_typeGraphs.put(typeModel, typeGraph);
        this.m_packageGraphs.put(Id.ROOT, typeGraph);

        this.m_currentTypeModel = typeModel;
        visitTypeModel(typeModel);
        Timer.stop(timer);
    }

    @Override
    public ExportableResource getResource() {
        return this.m_resource;
    }

    @Override
    //Override, since datatypes and properties are not supported
    protected void visitTypeModel(TypeModel typeModel) {
        for (Class cmClass : typeModel.getClasses()) {
            getElement(cmClass);
        }

        for (Enum cmEnum : typeModel.getEnums()) {
            getElement(cmEnum);
        }
    }

    @Override
    public void visit(Class class1, String param) {
        if (hasElement(class1)) {
            return;
        }

        if (!class1.isProper()) {
            setElement(class1, getElement(class1.getProperClass()));
            return;
        }

        Graph classGraph = getPackageGraph(class1.getId()
            .getNamespace());
        Node classNode = new Node();
        classGraph.addNode(classNode);
        classNode.setId(new com.alexmerz.graphviz.objects.Id());
        classNode.getId()
            .setId(getNodeId());

        for (Property p : this.m_currentTypeModel.getProperties()) {
            if (p instanceof AbstractProperty) {
                if (((AbstractProperty) p).getAbstractClass() == class1) {
                    classNode.setAttribute("style", "dashed");
                }
            }
        }

        setElement(class1, classNode);

        Set<Field> edgeFields = new HashSet<>();
        for (Field f : class1.getFields()) {
            Node fieldNode = null;
            Type fieldType = f.getType();
            if (fieldType instanceof Container) {
                fieldType = ((Container) fieldType).getType();
            }

            if (fieldType instanceof Class || fieldType instanceof Tuple
                || fieldType instanceof Enum) {
                fieldNode = getElement(fieldType);
            }

            if (fieldNode != null) {
                Edge fieldEdge = new Edge();
                fieldEdge.setSource(new PortNode(classNode));
                fieldEdge.setTarget(new PortNode(fieldNode));
                fieldEdge.setAttribute("label", f.getName()
                    .toString());
                fieldEdge.setType(Graph.DIRECTED);

                fieldEdge.setAttribute("headlabel", f.getLowerBound() + ".." + f.getUpperBound());

                for (Property p : this.m_currentTypeModel.getProperties()) {
                    if (p instanceof ContainmentProperty) {
                        if (((ContainmentProperty) p).getField() == f) {
                            fieldEdge.setAttribute("arrowtail", "diamond");
                        }
                    }
                }

                getPackageGraph(Id.ROOT).addEdge(fieldEdge);
                edgeFields.add(f);
            }
        }

        String label = class1.getId()
            .getName() + "\\n";
        for (Field f : class1.getFields()) {
            if (!edgeFields.contains(f)) {
                label += f.getName();

                for (Property p : this.m_currentTypeModel.getProperties()) {
                    if (p instanceof DefaultValueProperty) {
                        if (((DefaultValueProperty) p).getField() == f) {
                            label += " = " + ((DefaultValueProperty) p).getDefaultValue();
                        }
                    }
                }

                label += " : " + f.getLowerBound() + ".." + f.getUpperBound() + "\\n";
            }
        }

        classNode.setAttribute("label", label);

        for (Class supClass : class1.getSuperClasses()) {
            Node supNode = getElement(supClass);

            Edge supEdge = new Edge();
            supEdge.setSource(new PortNode(classNode));
            supEdge.setTarget(new PortNode(supNode));
            supEdge.setAttribute("arrowhead", "empty");
            supEdge.setType(Graph.DIRECTED);

            getPackageGraph(Id.ROOT).addEdge(supEdge);
        }
    }

    @Override
    public void visit(Container container, String param) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Enum enum1, String param) {
        if (hasElement(enum1)) {
            return;
        }

        Graph enumGraph = getPackageGraph(enum1.getId()
            .getNamespace());
        Node enumNode = new Node();
        enumGraph.addNode(enumNode);
        enumNode.setId(new com.alexmerz.graphviz.objects.Id());
        enumNode.getId()
            .setId(getNodeId());

        enumNode.setAttribute("shape", "record");
        String label = "{" + enum1.getId()
            .getName();

        for (Name literal : enum1.getLiterals()) {
            label += " | " + literal;
        }

        label += "}";
        enumNode.setAttribute("label", label);

        setElement(enum1, enumNode);
    }

    @Override
    public void visit(Tuple tuple, String param) {
        if (hasElement(tuple)) {
            return;
        }

        Graph tupleGraph = getPackageGraph(Id.ROOT);
        Node tupleNode = new Node();
        tupleGraph.addNode(tupleNode);
        tupleNode.setId(new com.alexmerz.graphviz.objects.Id());
        tupleNode.getId()
            .setId(getNodeId());

        tupleNode.setAttribute("shape", "record");
        String label = "";

        boolean first = true;
        for (Type type : tuple.getTypes()) {
            if (!first) {
                label += "|";
            }
            label += type.toString();
            first = false;
        }

        tupleNode.setAttribute("label", label);

        setElement(tuple, tupleNode);
    }

    private Graph getPackageGraph(Id namespace) {
        if (this.m_packageGraphs.containsKey(namespace)) {
            return this.m_packageGraphs.get(namespace);
        }

        Graph parent = getPackageGraph(namespace.getNamespace());
        Graph packageGraph = new Graph();
        parent.addSubgraph(packageGraph);

        com.alexmerz.graphviz.objects.Id graphId = new com.alexmerz.graphviz.objects.Id();
        graphId.setId("\"cluster_" + namespace.getName()
            .toString() + "\"");
        packageGraph.addAttribute("label", namespace.getName()
            .toString());
        packageGraph.setId(graphId);

        this.m_packageGraphs.put(namespace, packageGraph);

        return packageGraph;
    }

    private String getNodeId() {
        return "N" + this.m_nodeId++;
    }
}
