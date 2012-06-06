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
package groove.abstraction.pattern.gui.dialog;

import groove.abstraction.pattern.gui.jgraph.PatternJGraph;
import groove.abstraction.pattern.gui.jgraph.PatternJModel;
import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.Simulator;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.layout.LayoutKind;
import groove.gui.layout.LayouterItem;

/**
 * Dialog for displaying pattern graphs.
 * 
 * @author Eduardo Zambon
 */
public final class PatternPreviewDialog extends GraphPreviewDialog {

    /** Constructs a new dialog, for a given shape. */
    private PatternPreviewDialog(Simulator simulator,
            AbstractPatternGraph<?,?> pGraph) {
        super(simulator, pGraph);
        this.setTitle(pGraph.getName());
    }

    /**
     * Creates a dialog for the given shape, and sets it to visible.
     */
    public static void showPatternGraph(AbstractPatternGraph<?,?> pGraph) {
        showPatternGraph(null, pGraph);
    }

    /**
     * Creates a dialog for the given shape and (possibly {@code null}) 
     * simulator, and sets it to visible.
     */
    public static void showPatternGraph(Simulator simulator,
            AbstractPatternGraph<?,?> pGraph) {
        new PatternPreviewDialog(simulator, pGraph).setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PatternJGraph createJGraph() {
        PatternJGraph jGraph = new PatternJGraph(this.simulator);
        PatternJModel model = jGraph.newModel();
        model.loadGraph((Graph<Node,Edge>) this.graph);
        jGraph.setModel(model);
        LayouterItem layouter =
            LayoutKind.getLayouterItemProto(LayoutKind.HIERARCHICAL);
        jGraph.setLayouter(layouter);
        jGraph.doGraphLayout();
        return jGraph;
    }

}
