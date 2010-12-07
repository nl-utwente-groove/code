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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    private Shape shape;
    private final HashMap<ShapeNode,ShapeJVertex> nodeMap;
    private final HashMap<ShapeEdge,ShapeJEdge> edgeMap;
    private final HashMap<EdgeSignature,ShapeJPort> outEsMap;
    private final HashMap<EdgeSignature,ShapeJPort> inEsMap;

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJGraph() {
        super();

        this.setPortsVisible(true);
        this.setEditable(false);
        this.setConnectable(false);
        this.setDisconnectable(false);
        this.setDisconnectOnMove(false);

        this.addMouseListener(new MyMouseListener());

        this.nodeMap = new HashMap<ShapeNode,ShapeJVertex>();
        this.edgeMap = new HashMap<ShapeEdge,ShapeJEdge>();
        this.outEsMap = new HashMap<EdgeSignature,ShapeJPort>();
        this.inEsMap = new HashMap<EdgeSignature,ShapeJPort>();
    }

    @Override
    public Dimension getPreferredSize() {
        double maxX = 0.0;
        double maxY = 0.0;
        for (ShapeJVertex vertex : this.nodeMap.values()) {
            Rectangle2D bounds =
                GraphConstants.getBounds(vertex.getAttributes());
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        // Add some inset space...
        maxX += 25.0;
        maxY += 25.0;
        return new Dimension((int) maxX, (int) maxY);
    }

    /**
     * EDUARDO: Comment this...
     */
    public void setJModel(ShapeJModel model) {
        super.setModel(model);

        GraphLayoutCache view =
            new GraphLayoutCache(model, new ShapeJCellViewFactory());
        view.setAutoSizeOnValueChange(true);
        this.setGraphLayoutCache(view);

        this.shape = model.getShape();

        this.nodeMap.clear();
        this.edgeMap.clear();
        this.outEsMap.clear();
        this.inEsMap.clear();

        this.createElements();
    }

    private void createElements() {
        this.createNodes();
        this.createEdgeSigPorts();
        this.createEdges();
        this.createEdgeMults();
        this.createEquivClasses();
    }

    private void createNodes() {
        int nodeCount = this.shape.nodeCount();
        ShapeJVertex vertices[] = new ShapeJVertex[nodeCount];
        int i = 0;
        for (ShapeNode node : this.shape.nodeSet()) {
            vertices[i] =
                new ShapeJVertex(this.shape, node,
                    ((ShapeJModel) this.getModel()).getOptions());
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

    private void createEdgeMults() {
        Point2D[] labelPositions =
            {new Point2D.Double(GraphConstants.PERMILLE * 95 / 100, -10),
                new Point2D.Double(GraphConstants.PERMILLE * 5 / 100, -10)};

        HashMap<ShapeEdge,String> edge2OutMult =
            new HashMap<ShapeEdge,String>();
        HashMap<ShapeEdge,String> edge2InMult = new HashMap<ShapeEdge,String>();
        this.getEdgeToMultMaps(edge2OutMult, edge2InMult);

        for (Edge edge : Util.getBinaryEdges(this.shape)) {
            ShapeEdge edgeS = (ShapeEdge) edge;
            ShapeJEdge jEdge = this.edgeMap.get(edgeS);
            String labels[] = new String[2];
            labels[0] = edge2InMult.get(edgeS);
            labels[1] = edge2OutMult.get(edgeS);
            if (labels[0] == null) {
                labels[0] = "";
            } else {
                jEdge.setMainTgt(true);
            }
            if (labels[1] == null) {
                labels[1] = "";
            } else {
                jEdge.setMainSrc(true);
            }
            AttributeMap attrMap = jEdge.getAttributes();
            GraphConstants.setExtraLabelPositions(attrMap, labelPositions);
            GraphConstants.setExtraLabels(attrMap, labels);
            /*if (main) {
                GraphConstants.setLineColor(attrMap, Color.BLUE);
            }*/
        }
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
        layout.setInitialTemp(25.0);
        layout.run(facade);
        Map<?,?> nested = facade.createNestedMap(true, true);
        this.getGraphLayoutCache().edit(nested);
    }

    private void getEdgeToMultMaps(HashMap<ShapeEdge,String> edge2OutMult,
            HashMap<ShapeEdge,String> edge2InMult) {
        HashMap<ShapeJPort,ShapeEdge> outPort2Edge =
            new HashMap<ShapeJPort,ShapeEdge>();
        for (ShapeJPort outPort : this.outEsMap.values()) {
            outPort2Edge.put(outPort, null);
        }
        HashMap<ShapeJPort,ShapeEdge> inPort2Edge =
            new HashMap<ShapeJPort,ShapeEdge>();
        for (ShapeJPort inPort : this.inEsMap.values()) {
            inPort2Edge.put(inPort, null);
        }

        for (Edge edge : Util.getBinaryEdges(this.shape)) {
            edge2OutMult.put((ShapeEdge) edge, null);
            edge2InMult.put((ShapeEdge) edge, null);
        }

        for (Entry<EdgeSignature,ShapeJPort> entry : this.outEsMap.entrySet()) {
            EdgeSignature outEs = entry.getKey();
            ShapeJPort outPort = entry.getValue();
            Set<ShapeEdge> outEdges = this.shape.getEdgesFrom(outEs, true);
            if (this.shape.isOutEdgeSigUnique(outEs)) {
                ShapeEdge outEdge = outEdges.iterator().next();
                outPort2Edge.put(outPort, outEdge);
                edge2OutMult.put(outEdge,
                    this.shape.getEdgeOutMult(outEdge).toString());
            } else {
                for (ShapeEdge outEdge : outEdges) {
                    if (!outEdge.isLoop() && outPort2Edge.get(outEdge) == null) {
                        outPort2Edge.put(outPort, outEdge);
                        edge2OutMult.put(outEdge,
                            this.shape.getEdgeOutMult(outEdge).toString());
                        break;
                    }
                }
            }
        }

        for (Entry<EdgeSignature,ShapeJPort> entry : this.inEsMap.entrySet()) {
            EdgeSignature inEs = entry.getKey();
            ShapeJPort inPort = entry.getValue();
            Set<ShapeEdge> inEdges = this.shape.getEdgesFrom(inEs, false);
            if (this.shape.isInEdgeSigUnique(inEs)) {
                ShapeEdge inEdge = inEdges.iterator().next();
                inPort2Edge.put(inPort, inEdge);
                edge2InMult.put(inEdge,
                    this.shape.getEdgeInMult(inEdge).toString());
            } else {
                for (ShapeEdge inEdge : inEdges) {
                    if (!inEdge.isLoop() && inPort2Edge.get(inEdge) == null) {
                        inPort2Edge.put(inPort, inEdge);
                        edge2InMult.put(inEdge,
                            this.shape.getEdgeInMult(inEdge).toString());
                        break;
                    }
                }
            }
        }

    }

    private class MyMouseListener extends MouseAdapter {

        /** Empty constructor wit the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            ShapeJGraph.this.refresh();
        }
    }

}
