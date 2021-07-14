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
package nl.utwente.groove.io.conceptual.lang.graphviz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.conceptual.Field;
import nl.utwente.groove.io.conceptual.Id;
import nl.utwente.groove.io.conceptual.InstanceModel;
import nl.utwente.groove.io.conceptual.Name;
import nl.utwente.groove.io.conceptual.Timer;
import nl.utwente.groove.io.conceptual.TypeModel;
import nl.utwente.groove.io.conceptual.lang.ImportException;
import nl.utwente.groove.io.conceptual.lang.InstanceImporter;
import nl.utwente.groove.io.conceptual.lang.Message;
import nl.utwente.groove.io.conceptual.lang.Message.MessageType;
import nl.utwente.groove.io.conceptual.type.Class;
import nl.utwente.groove.io.conceptual.type.Container;
import nl.utwente.groove.io.conceptual.type.StringType;
import nl.utwente.groove.io.conceptual.type.Type;
import nl.utwente.groove.io.conceptual.type.Container.Kind;
import nl.utwente.groove.io.conceptual.value.ContainerValue;
import nl.utwente.groove.io.conceptual.value.Object;
import nl.utwente.groove.io.conceptual.value.StringValue;

@SuppressWarnings("javadoc")
public class GraphvizToInstance extends InstanceImporter {
    private Map<Node,Object> m_nodeMap = new HashMap<>();

    public GraphvizToInstance(String filename) throws ImportException {
        ArrayList<Graph> graphs = null;
        File file = new File(filename);
        try (FileReader in = new FileReader(file)) {
            Parser p = new Parser();
            int timer = Timer.start("Load DOT");
            p.parse(in);
            graphs = p.getGraphs();
            Timer.stop(timer);
        } catch (ParseException e) {
            throw new ImportException(e);
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        //Import all graphs in the same instance model.
        TypeModel typeModel = new TypeModel(QualName.name("DOTType"));
        InstanceModel instanceModel =
            new InstanceModel(typeModel, QualName.name(FileType.getPureName(file)));
        int timer = Timer.start("DOT to IM");
        visitGraphs(instanceModel, graphs, Id.ROOT);
        Timer.stop(timer);
        addInstanceModel(instanceModel);
    }

    private void visitGraphs(InstanceModel model, List<Graph> graphs, Id graphId) {
        for (Graph graph : graphs) {
            String graphName = graph.getId()
                .getLabel();
            if (graphName.equals("")) {
                graphName = graph.getId()
                    .getId();
            }
            if (graphName.equals("")) {
                graphName = graph.getAttribute("label");
            }
            if (graphName == null) {
                graphName = "graph";
            }

            Id graphNS = Id.getId(graphId, Name.getName(graphName));

            for (Node node : graph.getNodes(true)) {
                if (node.isSubgraph()) {
                    continue;
                }
                visitNode(model, node, graphNS);
            }

            // Visit subgraph before parsing edges, so all nodes are known
            visitGraphs(model, graph.getSubgraphs(), graphNS);

            for (Edge edge : graph.getEdges()) {
                visitEdge(model, edge);
            }
        }
    }

    private Object visitNode(InstanceModel model, Node node, Id graphId) {
        if (this.m_nodeMap.containsKey(node)) {
            return this.m_nodeMap.get(node);
        }
        if (graphId == null) {
            addMessage(new Message("Attempting to add edge for unvisited node" + node.toString(),
                MessageType.ERROR));
        }

        String nodeName = node.getId()
            .getLabel();
        if (nodeName.equals("")) {
            nodeName = node.getId()
                .getId();
        }
        if (nodeName.equals("")) {
            nodeName = node.getAttribute("label");
        }
        if (nodeName == null) {
            nodeName = "node";
        }

        Class c = model.getTypeModel()
            .getClass(Id.getId(graphId, Name.getName(nodeName)), true);
        Object object = new Object(c, Name.getName(nodeName));
        this.m_nodeMap.put(node, object);

        //ContainerValue attrContainer = new ContainerValue((Container) GraphvizUtil.g_AttrField.getType());
        //object.setFieldValue(GraphvizUtil.g_AttrField, attrContainer);
        for (Entry<String,String> entry : node.getAttributes()
            .entrySet()) {
            if (entry.getKey()
                .equals("label")) {
                continue;
            }

            // Add field of type container, as attributes are optional
            Field f = new Field(Name.getName(entry.getKey()),
                new Container(Kind.SET, StringType.instance()), 0, 1);
            c.addField(f);

            ContainerValue v = new ContainerValue((Container) f.getType());
            v.addValue(new StringValue(entry.getValue()));
            object.setFieldValue(f, v);
        }

        model.addObject(object);

        return object;
    }

    private void visitEdge(InstanceModel model, Edge edge) {
        Node source = edge.getSource()
            .getNode();
        Node target = edge.getTarget()
            .getNode();

        if (source.isSubgraph() || target.isSubgraph()) {
            // Edge to graph not supported
            return;
        }

        Object sourceObj = visitNode(model, source, null);
        Object targetObj = visitNode(model, target, null);

        String label = edge.getAttribute("label");
        if (label == null) {
            label = "edge";
        }

        Field f = null;
        Type fType = null;
        Type targetType = targetObj.getType();
        int index = 0;
        do {
            f = ((Class) sourceObj.getType())
                .getField(Name.getName(index == 0 ? label : label + index));
            if (f != null) {
                index++;
                fType = ((Container) f.getType()).getType();
            } else {
                fType = null;
            }
        } while (f != null && fType != targetType);

        if (f == null) {
            // Always unique and unordered
            Container ctype = new Container(Kind.SET, targetObj.getType());
            f = new Field(Name.getName(index == 0 ? label : label + index), ctype, 0, -1);
            ((Class) sourceObj.getType()).addField(f);
            sourceObj.setFieldValue(f, new ContainerValue(ctype));
        }

        ContainerValue cv = (ContainerValue) sourceObj.getValue()
            .get(f);
        cv.addValue(targetObj);
    }
}
