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
 * $Id: AbstractLayouter.java,v 1.6 2008-01-30 09:33:01 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.GraphJGraph;

import java.util.Map;

import javax.swing.JPanel;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;

/** Class representing elements of the layout menu. */
public class LayouterItem implements Layouter {

    private final String actionName;
    private final JGraphLayout layout;
    private final GraphJGraph jGraph;
    private final JGraphFacade facade;
    private final JPanel panel;

    /** Builds a prototype instance based on the given layout kind. */
    public LayouterItem(LayoutKind kind) {
        this.actionName = kind.getDisplayString();
        this.layout = kind.getLayout();
        this.jGraph = null;
        this.facade = null;
        this.panel = null;
    }

    private LayouterItem(String actionName, JGraphLayout layout,
            GraphJGraph jGraph, JGraphFacade facade) {
        this.actionName = actionName;
        this.layout = layout;
        this.jGraph = jGraph;
        this.facade = facade;
        this.panel = LayoutKind.createLayoutPanel(this);
    }

    @Override
    public Layouter newInstance(GraphJGraph jGraph) {
        return new LayouterItem(this.actionName, this.layout, jGraph,
            new JGraphFacade(jGraph));
    }

    @Override
    public String getName() {
        return this.actionName;
    }

    @Override
    public String getText() {
        return this.actionName;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void start(boolean complete) {
        this.jGraph.clearAllEdgePoints();
        this.layout.run(this.facade);
        Map<?,?> nested = this.facade.createNestedMap(true, true);
        this.jGraph.getGraphLayoutCache().edit(nested);
    }

    @Override
    public void stop() {
        // Empty by design.
    }

    /** Basic getter method. */
    public JGraphLayout getLayout() {
        return this.layout;
    }

    /** Basic getter method. */
    public JPanel getPanel() {
        return this.panel;
    }

}
