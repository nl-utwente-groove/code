/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.jgraph;

import java.util.Map;

import javax.swing.JPanel;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.layout.SpringLayouter;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewModel;

/**
 * Layouter wrapping one of the layout algorithms of the JGraph layout library;
 * the JGraph backend's contribution to the layout palette.
 */
@NonNullByDefault
public class LayouterItem implements Layouter {

    private final LayoutKind kind;
    private final @Nullable JGraph<?> jGraph;
    private @Nullable JGraphFacade facade;
    private final @Nullable JPanel panel;

    /** Builds a prototype instance based on the given layout kind. */
    LayouterItem(LayoutKind kind) {
        this(kind, null, null);
    }

    private LayouterItem(LayoutKind kind, @Nullable JGraph<?> jGraph,
                         @Nullable JGraphFacade facade) {
        this.kind = kind;
        this.jGraph = jGraph;
        this.facade = facade;
        this.panel = jGraph == null
            ? null
            : LayoutKind.createLayoutPanel(this);
        if (jGraph != null) {
            installModelListener(jGraph);
        }
    }

    /** Adds the listener that renews the facade on model changes; generic to capture the graph type. */
    private <H extends Graph> void installModelListener(JGraph<H> jGraph) {
        jGraph.addCanvasListener(new GraphCanvasListener<H>() {
            @Override
            public void viewModelChanged(GraphCanvas<H> canvas,
                                         @Nullable GraphViewModel<H> oldModel,
                                         @Nullable GraphViewModel<H> newModel) {
                LayouterItem.this.facade = newModel == null
                    ? null
                    : new JGraphFacade(jGraph);
            }
        });
    }

    @Override
    public Layouter newInstance(GraphCanvas<?> canvas) {
        // the JGraph layouts work on the backend component
        JGraph<?> jGraph = (JGraph<?>) canvas;
        return new LayouterItem(this.kind, jGraph, new JGraphFacade(jGraph));
    }

    @Override
    public String getName() {
        return this.kind.getDisplayString();
    }

    @Override
    public void start() {
        var jGraph = this.jGraph;
        var facade = this.facade;
        if (jGraph != null && facade != null) {
            jGraph.setLayouting(true);
            jGraph.clearAllEdgePoints();
            getLayout().run(facade);
            Map<?,?> nested = facade.createNestedMap(true, false);
            jGraph.getGraphLayoutCache().edit(nested);
            jGraph.setLayouting(false);
        }
    }

    /** Basic getter method. */
    public JGraphLayout getLayout() {
        return this.kind.getLayout();
    }

    @Override
    public @Nullable JPanel getSettingsPanel() {
        return this.panel;
    }

    @Override
    public Layouter getIncremental() {
        var result = this.incremental;
        if (result == null) {
            var jGraph = this.jGraph;
            assert jGraph != null; // only canvas-bound instances are asked for an incremental
            this.incremental = result = SpringLayouter.PROTOTYPE.newInstance(jGraph);
        }
        return result;
    }

    private @Nullable Layouter incremental;
}
