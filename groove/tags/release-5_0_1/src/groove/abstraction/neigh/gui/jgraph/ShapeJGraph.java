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
package groove.abstraction.neigh.gui.jgraph;

import groove.abstraction.neigh.gui.look.ShapeAdornmentValue;
import groove.abstraction.neigh.gui.look.ShapeLabelValue;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Simulator;
import groove.gui.jgraph.JCellViewFactory;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JGraphFactory;
import groove.gui.jgraph.JVertex;
import groove.gui.layout.AbstractLayouter;
import groove.gui.layout.Layouter;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualValue;

import java.awt.Rectangle;
import java.util.Map;

import javax.swing.SwingConstants;

import org.jgraph.graph.CellView;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;

/**
 * JGraph class for displaying Shapes. 
 * 
 * @author Eduardo Zambon
 */
public final class ShapeJGraph extends JGraph<Shape> {

    /** Constructs an instance of the j-graph for a given simulator. */
    public ShapeJGraph(Simulator simulator) {
        super(simulator);
        setPortsVisible(true);
    }

    @Override
    public GraphRole getGraphRole() {
        return GraphRole.SHAPE;
    }

    /** Specialises the return type to a {@link ShapeJModel}. */
    @Override
    public ShapeJModel getModel() {
        return (ShapeJModel) this.graphModel;
    }

    @Override
    protected JCellViewFactory createViewFactory() {
        return new ShapeJCellViewFactory(this);
    }

    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return false;
    }

    @Override
    public boolean isShowBidirectionalEdges() {
        return false;
    }

    @Override
    protected Layouter createLayouter() {
        return new MyLayouter();
    }

    /** Returns the shape from the model. */
    public Shape getShape() {
        return getModel().getGraph();
    }

    @Override
    protected ShapeJCell getFirstCellForLocation(double x, double y,
            boolean vertex, boolean edge) {
        x /= this.scale;
        y /= this.scale;
        ShapeJCell vertexOrEdgeResult = null;
        ShapeJCell ecResult = null;
        Rectangle xyArea = new Rectangle((int) (x - 2), (int) (y - 2), 4, 4);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = this.graphLayoutCache.getRoots();
        outerLoop: for (int i = viewRoots.length - 1; i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            if (!(jCellView.getCell() instanceof ShapeJCell)) {
                continue outerLoop;
            }
            ShapeJCell jCell = (ShapeJCell) jCellView.getCell();
            boolean typeCorrect =
                vertex ? (jCell instanceof JVertex) : edge
                        ? jCell instanceof JEdge : true;
            if (typeCorrect && jCell instanceof EcJVertex) {
                // We have an equivalence class.
                for (CellView childView : jCellView.getChildViews()) {
                    // Check proximity with all nodes inside.
                    ShapeJCell jCellChild = (ShapeJCell) childView.getCell();
                    if (typeCorrect && !jCellChild.isGrayedOut()) {
                        // Now see if this child is sufficiently close to the point.
                        if (childView.intersects(this, xyArea)) {
                            vertexOrEdgeResult = jCellChild;
                            break outerLoop;
                        }
                    }
                }
                // Failed, check intersection with the equivalence class.
                if (jCellView.intersects(this, xyArea)) {
                    // We found a class with an intersection.
                    ecResult = jCell;
                    continue outerLoop;
                }
            }
            if (typeCorrect && !jCell.isGrayedOut()) {
                // We are interested in edges and this jCell is an edge.
                if (jCellView.intersects(this, xyArea)) {
                    // We found our edge.
                    vertexOrEdgeResult = jCell;
                    break outerLoop;
                }
            }
        }
        ShapeJCell result =
            vertexOrEdgeResult == null ? ecResult : vertexOrEdgeResult;
        return result;
    }

    @Override
    protected JGraphFactory<Shape> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<Shape> {
        public MyFactory() {
            super(ShapeJGraph.this);
        }

        @Override
        public ShapeJGraph getJGraph() {
            return (ShapeJGraph) super.getJGraph();
        }

        @Override
        public ShapeJVertex newJVertex(Node node) {
            assert node instanceof ShapeNode;
            return ShapeJVertex.newInstance();
        }

        @Override
        public ShapeJEdge newJEdge(Edge edge) {
            return ShapeJEdge.newInstance();
        }

        @Override
        public ShapeJModel newModel() {
            return new ShapeJModel(getJGraph());
        }

        @Override
        public VisualValue<?> newVisualValue(VisualKey key) {
            switch (key) {
            case ADORNMENT:
                return new ShapeAdornmentValue();
            case LABEL:
                return new ShapeLabelValue(getJGraph());
            default:
                return super.newVisualValue(key);
            }
        }
    }

    private static class MyLayouter extends AbstractLayouter {

        JGraphFacade facade;
        JGraphCompactTreeLayout treeLayout;

        MyLayouter() {
            super("ShapeJGraph Layouter");
        }

        MyLayouter(String name, ShapeJGraph jgraph) {
            super(name, jgraph);
        }

        @Override
        public Layouter newInstance(JGraph<?> jgraph) {
            return new MyLayouter(getName(), (ShapeJGraph) jgraph);
        }

        @Override
        public void start() {
            prepareLayouting();
            run();
            finishLayouting();
        }

        ShapeJGraph getJGraph() {
            return (ShapeJGraph) this.jGraph;
        }

        void prepareLayouting() {
            getJGraph().setLayouting(true);
            this.facade = new JGraphFacade(getJGraph());
            this.treeLayout = new JGraphCompactTreeLayout();
            this.treeLayout.setOrientation(SwingConstants.NORTH);
        }

        void run() {
            this.treeLayout.run(this.facade);
        }

        void finishLayouting() {
            Map<?,?> nested = this.facade.createNestedMap(true, true);
            getJGraph().getGraphLayoutCache().edit(nested);
            getJGraph().setLayouting(false);
            getJGraph().refreshAllCells();
        }
    }
}
