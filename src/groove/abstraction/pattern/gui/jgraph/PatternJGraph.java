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
package groove.abstraction.pattern.gui.jgraph;

import groove.gui.LabelTree;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jgraph.graph.CellView;

/**
 * JGraph class for displaying pattern shapes. 
 * 
 * @author Eduardo Zambon
 */
public final class PatternJGraph extends GraphJGraph {

    /** Constructs an instance of the j-graph for a given simulator. */
    public PatternJGraph(Simulator simulator) {
        super(simulator, false);
        addMouseListener(new MyMouseListener());
    }

    /** Specialises the return type to a {@link PatternJModel}. */
    @Override
    public PatternJModel getModel() {
        return (PatternJModel) this.graphModel;
    }

    @Override
    public PatternJModel newModel() {
        return new PatternJModel(GraphJVertex.getPrototype(this),
            GraphJEdge.getPrototype(this), PatternJVertex.getPrototype(this),
            PatternJEdge.getPrototype(this));
    }

    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return false;
    }

    /** Callback method to create the label tree. */
    @Override
    protected LabelTree createLabelTree() {
        // Create a pesky label tree...
        return new LabelTree(new GraphJGraph(null, false), false, false);
    }

    @Override
    protected GraphJCell getFirstCellForLocation(double x, double y,
            boolean vertex, boolean edge) {
        x /= this.scale;
        y /= this.scale;
        GraphJCell vertexOrEdgeResult = null;
        GraphJCell ecResult = null;
        Rectangle xyArea = new Rectangle((int) (x - 2), (int) (y - 2), 4, 4);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = this.graphLayoutCache.getRoots();
        outerLoop: for (int i = viewRoots.length - 1; i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            if (!(jCellView.getCell() instanceof GraphJCell)) {
                continue outerLoop;
            }
            GraphJCell jCell = (GraphJCell) jCellView.getCell();
            boolean typeCorrect =
                vertex ? (jCell instanceof GraphJVertex) : edge
                        ? jCell instanceof GraphJEdge : true;
            if (typeCorrect && jCell instanceof PatternJVertex) {
                // We have an equivalence class.
                for (CellView childView : jCellView.getChildViews()) {
                    // Check proximity with all nodes inside.
                    GraphJCell jCellChild = (GraphJCell) childView.getCell();
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
        GraphJCell result =
            vertexOrEdgeResult == null ? ecResult : vertexOrEdgeResult;
        return result;
    }

    /**
     * Mouse listener that creates the popup menu and adds and deletes points on
     * appropriate events.
     */

    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor wit the correct visibility. */

        MyMouseListener() {
            // empty
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
            PatternJGraph.this.refreshAllCells();
        }
    }

}
