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

import groove.abstraction.MyHashSet;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.layout.AbstractLayouter;
import groove.gui.layout.Layouter;
import groove.gui.tree.LabelTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;

/**
 * JGraph class for displaying pattern graphs. 
 * 
 * @author Eduardo Zambon
 */
public final class PatternJGraph extends GraphJGraph {

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /** Constructs an instance of the j-graph for a given simulator. */
    public PatternJGraph(Simulator simulator) {
        super(simulator, false);
        addMouseListener(new MyMouseListener());
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Specialises the return type to a {@link PatternJModel}. */
    @Override
    public PatternJModel getModel() {
        return (PatternJModel) this.graphModel;
    }

    @Override
    public PatternJModel newModel() {
        return new PatternJModel(this, GraphJVertex.getPrototype(this),
            GraphJEdge.getPrototype(this), PatternJVertex.getPrototype(this),
            PatternJEdge.getPrototype(this));
    }

    /** Callback method to create the label tree. */
    @Override
    protected LabelTree createLabelTree() {
        // EZ says: ugly hack to keep things moving...
        // Create a pesky label tree...
        return new LabelTree(new GraphJGraph(null, false), false, false);
    }

    /** Creates and returns a special layouter for pattern graphs. */
    public Layouter createLayouter() {
        return new MyLayouter();
    }

    /** Mouse listener that refreshes the jGraph after every click. */
    private class MyMouseListener extends MouseAdapter {

        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            refreshAllCells();
        }
    }

    private static class MyLayouter extends AbstractLayouter {

        JGraphFacade facade;
        JGraphCompactTreeLayout treeLayout;
        LineLayout lineLayout;

        MyLayouter() {
            super("PatternJGraph Layouter");
        }

        MyLayouter(String name, PatternJGraph jgraph) {
            super(name, jgraph);
        }

        @Override
        public Layouter newInstance(GraphJGraph jgraph) {
            return new MyLayouter(this.name, (PatternJGraph) jgraph);
        }

        @Override
        public void start(boolean complete) {
            prepareLayouting();
            run();
            finishLayouting();
        }

        @Override
        public void stop() {
            // Empty by design.
        }

        PatternJGraph getJGraph() {
            return (PatternJGraph) this.jgraph;
        }

        void prepareLayouting() {
            getJGraph().setLayouting(true);
            this.facade = new JGraphFacade(getJGraph());
            this.treeLayout = new JGraphCompactTreeLayout();
            this.treeLayout.setOrientation(SwingConstants.WEST);
            this.lineLayout = new LineLayout();
        }

        void run() {
            // First layout each individual pattern.
            for (List<GraphJCell> roots : getJGraph().getModel().getReverseParentMap().values()) {
                layoutPattern(roots);
            }
            // The layout the pattern graph structure.
            this.facade.setVerticesFilter(null);
            List<GraphJCell> roots = getJGraph().getModel().getPatternRoots();
            this.facade.setRoots(roots);
            this.facade.setIgnoresCellsInGroups(true);
            JGraphHierarchicalLayout hLayout = new JGraphHierarchicalLayout();
            hLayout.setLayoutFromSinks(false);
            hLayout.run(this.facade);
        }

        void layoutPattern(List<GraphJCell> roots) {
            Set<GraphJVertex> verticesFilter = new MyHashSet<GraphJVertex>();
            for (GraphJCell jCell : roots) {
                if (jCell instanceof GraphJVertex) {
                    verticesFilter.add((GraphJVertex) jCell);
                }
            }
            if (this.lineLayout == null) {
                this.facade.setVerticesFilter(verticesFilter);
                this.facade.findTreeRoots();
                this.treeLayout.run(this.facade);
            } else {
                this.lineLayout.run(this.facade, verticesFilter);
            }
        }

        void finishLayouting() {
            Map<?,?> nested = this.facade.createNestedMap(true, true);
            getJGraph().getGraphLayoutCache().edit(nested);
            getJGraph().setLayouting(false);
            getJGraph().refreshAllCells();
        }
    }

    private static class LineLayout {
        void run(JGraphFacade facade, Set<GraphJVertex> vertices) {
            int x = 0;
            int y = 0;
            int hSpace = 70;
            for (GraphJVertex vertex : vertices) {
                facade.setLocation(vertex, x, y);
                x += hSpace;
            }
        }
    }

}
