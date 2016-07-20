/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.gui.layout;

import groove.gui.jgraph.JGraph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JPanel;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;

/** Class representing elements of the layout menu. */
public class LayouterItem implements Layouter {

    private final LayoutKind kind;
    private final JGraph<?> jGraph;
    private JGraphFacade facade;
    private final JPanel panel;

    /** Builds a prototype instance based on the given layout kind. */
    public LayouterItem(LayoutKind kind) {
        this(kind, null, null);
    }

    private LayouterItem(LayoutKind kind, final JGraph<?> jGraph, JGraphFacade facade) {
        this.kind = kind;
        this.jGraph = jGraph;
        this.facade = facade;
        this.panel = jGraph == null ? null : LayoutKind.createLayoutPanel(this);
        if (jGraph != null) {
            jGraph.addPropertyChangeListener(org.jgraph.JGraph.GRAPH_MODEL_PROPERTY,
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        LayouterItem.this.facade =
                                jGraph.getModel() == null ? null : new JGraphFacade(jGraph);
                    }
                });
        }
    }

    @Override
    public Layouter newInstance(JGraph<?> jGraph) {
        return new LayouterItem(this.kind, jGraph, new JGraphFacade(jGraph));
    }

    @Override
    public String getName() {
        return this.kind.getDisplayString();
    }

    @Override
    public void start() {
        if (this.facade != null) {
            prepareLayouting();
            run();
            finishLayouting();
        }
    }

    /** Basic getter method. */
    public JGraphLayout getLayout() {
        return this.kind.getLayout();
    }

    /** Basic getter method. */
    public JPanel getPanel() {
        return this.panel;
    }

    private void prepareLayouting() {
        this.jGraph.setLayouting(true);
        this.jGraph.clearAllEdgePoints();
    }

    private void run() {
        getLayout().run(this.facade);
        Map<?,?> nested = this.facade.createNestedMap(true, false);
        this.jGraph.getGraphLayoutCache().edit(nested);
    }

    private void finishLayouting() {
        this.jGraph.setLayouting(false);
    }

    @Override
    public Layouter getIncremental() {
        if (this.incremental == null) {
            this.incremental = SpringLayouter.PROTOTYPE.newInstance(this.jGraph);
        }
        return this.incremental;
    }

    private Layouter incremental;
}
