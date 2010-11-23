/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.gui.jgraph;

import groove.abstraction.EdgeSignature;
import groove.abstraction.EquivClass;
import groove.abstraction.EquivRelation;
import groove.abstraction.Shape;
import groove.abstraction.ShapeEdge;
import groove.abstraction.ShapeNode;
import groove.abstraction.Util;
import groove.graph.Edge;
import groove.gui.jgraph.JAttr;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.ParentMap;
import org.jgraph.graph.Port;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJGraph extends JGraph {

    private final Shape shape;
    private final HashMap<ShapeNode,ShapeJVertex> nodeMap;
    private final HashMap<ShapeEdge,ShapeJEdge> edgeMap;
    private final HashMap<EdgeSignature,ShapeJPort> outEsMap;
    private final HashMap<EdgeSignature,ShapeJPort> inEsMap;

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJGraph(ShapeJModel model) {
        super(model);

        GraphLayoutCache view =
            new GraphLayoutCache(model, new ShapeJCellViewFactory());
        view.setAutoSizeOnValueChange(true);
        this.setGraphLayoutCache(view);

        this.setPortsVisible(true);
        this.setEditable(false);
        this.setConnectable(false);
        this.setDisconnectable(false);
        this.setDisconnectOnMove(false);

        this.shape = model.getShape();
        this.nodeMap = new HashMap<ShapeNode,ShapeJVertex>();
        this.edgeMap = new HashMap<ShapeEdge,ShapeJEdge>();
        this.outEsMap = new HashMap<EdgeSignature,ShapeJPort>();
        this.inEsMap = new HashMap<EdgeSignature,ShapeJPort>();

        this.createElements();
    }

    private void createElements() {
        this.createNodes();
        this.createEdgeSigPorts();
        this.createEdges();
        //createEdgeMults(jgraph, shape);
        this.createEquivClasses();
    }

    private void createNodes() {
        int nodeCount = this.shape.nodeCount();
        ShapeJVertex vertices[] = new ShapeJVertex[nodeCount];
        int i = 0;
        for (ShapeNode node : this.shape.nodeSet()) {
            vertices[i] = new ShapeJVertex(this.shape, node);
            this.nodeMap.put(node, vertices[i]);
            i++;
        }
        this.getGraphLayoutCache().insert(vertices);
    }

    private void createEdgeSigPorts() {
        for (EdgeSignature es : this.shape.getOutEdgeMultMap().keySet()) {
            ShapeJVertex vertex = this.nodeMap.get(es.getNode());
            ShapeJPort port = new ShapeJPort(this.shape, es, vertex, true);
            this.outEsMap.put(es, port);
        }
        for (EdgeSignature es : this.shape.getInEdgeMultMap().keySet()) {
            ShapeJVertex vertex = this.nodeMap.get(es.getNode());
            ShapeJPort port = new ShapeJPort(this.shape, es, vertex, false);
            this.inEsMap.put(es, port);
        }
    }

    private void createEdges() {
        ArrayList<ShapeJEdge> edges = new ArrayList<ShapeJEdge>();
        for (Edge edge : Util.getBinaryEdges(this.shape)) {
            ShapeEdge edgeS = (ShapeEdge) edge;
            EdgeSignature outEs = this.shape.getEdgeOutSignature(edgeS);
            EdgeSignature inEs = this.shape.getEdgeInSignature(edgeS);
            Port source = this.outEsMap.get(outEs);
            Port target = this.inEsMap.get(inEs);
            ShapeJEdge jEdge =
                new ShapeJEdge(this.shape, edgeS, source, target);
            edges.add(jEdge);
            this.edgeMap.put(edgeS, jEdge);
        }
        this.getGraphLayoutCache().insert(edges.toArray());
    }

    private void createEquivClasses() {
        ParentMap parentMap = new ParentMap();
        EquivRelation<ShapeNode> er = this.shape.getEquivRelation();
        int erCount = er.size();
        DefaultGraphCell ecs[] = new DefaultGraphCell[erCount];
        int i = 0;
        for (EquivClass<ShapeNode> ec : er) {
            ecs[i] = new DefaultGraphCell();
            AttributeMap attrMap = ecs[i].getAttributes();
            GraphConstants.setBounds(attrMap, new Rectangle2D.Double(20, 20,
                40, 20));
            GraphConstants.setAutoSize(attrMap, true);
            GraphConstants.setGroupOpaque(attrMap, true);
            GraphConstants.setInset(attrMap, 8);
            GraphConstants.setBorder(attrMap, JAttr.NESTED_BORDER);
            for (ShapeNode node : ec) {
                parentMap.addEntry(this.nodeMap.get(node), ecs[i]);
            }
            i++;
        }
        this.getGraphLayoutCache().edit(null, null, parentMap, null);
        this.getGraphLayoutCache().insert(ecs);
    }

    /**
     * EDUARDO: Comment this...
     */
    public void runLayout() {
        Object roots[] = this.getRoots();
        JGraphFacade facade = new JGraphFacade(this, roots);
        facade.setIgnoresUnconnectedCells(false);
        JGraphFastOrganicLayout layout = new JGraphFastOrganicLayout();
        layout.setForceConstant(200.0);
        layout.run(facade);
        Map<?,?> nested = facade.createNestedMap(true, true);
        this.getGraphLayoutCache().edit(nested);
    }

}
