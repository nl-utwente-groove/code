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
package groove.gui;

import groove.gui.SimulatorModel.Change;

import java.util.Set;

/**
 * Observer interface for simulation.
 * @see Simulator
 */
public interface SimulatorListener {
    /**
     * Reports an update in the state of the simulator.
     * @param source the state object originating the update
     * @param oldModel previous GUI state
     * @param changes set of changes made since the previous state
     */
    void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes);
}
