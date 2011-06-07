/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui;

import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

/**
 * Simulator tab that itself is a tabbed panel.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class GraphDisplay extends TabbedResourceDisplay implements
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator and (graph-based) resource kind.
     */
    public GraphDisplay(Simulator simulator, ResourceKind resource) {
        super(simulator, resource);
        assert resource.isGraphBased();
    }

    @Override
    public TabbedDisplayPanel getDisplayPanel() {
        return getTabPane();
    }

    @Override
    public GraphTab getMainTab() {
        return (GraphTab) super.getMainTab();
    }

    @Override
    protected GraphTab createMainTab() {
        return new GraphTab(getSimulator(), getResourceKind());
    }

    @Override
    protected EditorTab createEditorTab(String name) {
        AspectGraph graph =
            getSimulatorModel().getStore().getGraphs(getResourceKind()).get(
                name);
        final GraphEditorTab result = new GraphEditorTab(this, graph);
        // start the editor only after it has been added
        result.start();
        return result;
    }
}
