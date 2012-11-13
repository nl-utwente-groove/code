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

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;

import org.jgraph.graph.GraphConstants;

/**
 * Action to reload the LTS into the LTSDisplay, with the current state bound.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ReloadLTSAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    protected ReloadLTSAction(Simulator simulator) {
        super(simulator, Options.RELOAD_LTS_ACTION_NAME, null);
    }

    @Override
    public void execute() {
        LTSJModel ltsJModel = getLtsDisplay().getJModel();
        int newBound = getLtsDisplay().getStateBound();
        int oldBound = ltsJModel.setStateBound(newBound);
        if (oldBound > newBound) {
            ltsJModel.loadGraph(getSimulatorModel().getGts());
        } else {
            LTSJGraph jGraph = getLtsDisplay().getJGraph();
            for (GraphJCell jCell : ltsJModel.getRoots()) {
                GraphConstants.setMoveable(jCell.getAttributes(), false);
            }
            ltsJModel.loadFurther();
            jGraph.getLayouter().start(false);
        }
        getLtsDisplay().getGraphPanel().refreshBackground();
    }
}
