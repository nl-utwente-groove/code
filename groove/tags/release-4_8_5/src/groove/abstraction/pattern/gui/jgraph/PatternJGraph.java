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
import groove.abstraction.pattern.gui.look.PatternAdornmentValue;
import groove.abstraction.pattern.gui.look.PatternLabelValue;
import groove.abstraction.pattern.shape.AbstractPatternEdge;
import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Simulator;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JGraphFactory;
import groove.gui.jgraph.JVertex;
import groove.gui.layout.AbstractLayouter;
import groove.gui.layout.Layouter;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualValue;

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
public final class PatternJGraph extends JGraph<AbstractPatternGraph<?,?>> {

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /** Constructs an instance of the j-graph for a given simulator. */
    public PatternJGraph(Simulator simulator) {
        super(simulator);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphRole getGraphRole() {
        return GraphRole.PATTERN;
    }

    @Override
    public boolean isShowBidirectionalEdges() {
        return false;
    }

    /** Specialises the return type to a {@link PatternJModel}. */
    @Override
    public PatternJModel getModel() {
        return (PatternJModel) this.graphModel;
    }

    @Override
    protected Layouter createLayouter() {
        return new MyLayouter();
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
        public Layouter newInstance(JGraph<?> jgraph) {
            return new MyLayouter(getName(), (PatternJGraph) jgraph);
        }

        @Override
        public void start() {
            prepareLayouting();
            run();
            finishLayouting();
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
            for (List<PatternJCell> roots : getJGraph().getModel().getReverseParentMap().values()) {
                layoutPattern(roots);
            }
            // The layout the pattern graph structure.
            this.facade.setVerticesFilter(null);
            List<JCell<AbstractPatternGraph<?,?>>> roots =
                getJGraph().getModel().getPatternRoots();
            this.facade.setRoots(roots);
            this.facade.setIgnoresCellsInGroups(true);
            JGraphHierarchicalLayout hLayout = new JGraphHierarchicalLayout();
            hLayout.setLayoutFromSinks(false);
            hLayout.run(this.facade);
        }

        void layoutPattern(List<PatternJCell> roots) {
            Set<PatternJVertex> verticesFilter =
                new MyHashSet<PatternJVertex>();
            for (PatternJCell jCell : roots) {
                if (jCell instanceof JVertex) {
                    verticesFilter.add((PatternJVertex) jCell);
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
        void run(JGraphFacade facade, Set<PatternJVertex> vertices) {
            int x = 0;
            int y = 0;
            int hSpace = 70;
            for (PatternJVertex vertex : vertices) {
                facade.setLocation(vertex, x, y);
                x += hSpace;
            }
        }
    }

    @Override
    protected JGraphFactory<AbstractPatternGraph<?,?>> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<AbstractPatternGraph<?,?>> {
        public MyFactory() {
            super(PatternJGraph.this);
        }

        @Override
        public PatternJGraph getJGraph() {
            return (PatternJGraph) super.getJGraph();
        }

        @Override
        public PatternJModel newModel() {
            return new PatternJModel(getJGraph());
        }

        @Override
        public PatternJVertex newJVertex(Node node) {
            return PatternJVertex.newInstance(node instanceof AbstractPatternNode);
        }

        @Override
        public PatternJEdge newJEdge(Edge edge) {
            return PatternJEdge.newInstance(edge instanceof AbstractPatternEdge<?>);
        }

        @Override
        public VisualValue<?> newVisualValue(VisualKey key) {
            switch (key) {
            case ADORNMENT:
                return new PatternAdornmentValue();
            case LABEL:
                return new PatternLabelValue(getJGraph());
            default:
                return super.newVisualValue(key);
            }
        }
    }
}
