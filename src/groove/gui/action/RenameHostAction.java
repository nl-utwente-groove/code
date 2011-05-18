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
package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.GraphView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.Collection;

/**
 * Action that takes care of renaming a host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RenameHostAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public RenameHostAction(Simulator simulator) {
        super(simulator, Options.RENAME_GRAPH_ACTION_NAME, Icons.RENAME_ICON);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && getModel().getStore().isModifiable()
            && !getModel().getHostSet().isEmpty());

        if (getSimulator().getGraphPanel() == getSimulator().getStatePanel()) {
            getSimulator().getRenameMenuItem().setAction(this);
        }
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // Multiple selection
        // copy selected graph names
        Collection<GraphView> selectedGraphs = getModel().getHostSet();
        // first collect the affected graphs
        AspectGraph[] hostGraphs = new AspectGraph[selectedGraphs.size()];
        int count = 0;
        for (GraphView hostView : selectedGraphs) {
            hostGraphs[count] = hostView.getAspectGraph();
            count++;
        }
        if (getPanel().disposeEditors(hostGraphs)) {
            for (AspectGraph graph : hostGraphs) {
                String oldName = graph.getName();
                String newName =
                    askNewGraphName("Select new graph name", oldName, false);
                if (newName != null && !oldName.equals(newName)) {
                    try {
                        result |= getModel().doRenameHost(graph, newName);
                    } catch (IOException exc) {
                        showErrorDialog(exc, String.format(
                            "Error while renaming graph '%s' into '%s'",
                            oldName, newName));
                    }
                }
            }
        }
        return result;
    }
}