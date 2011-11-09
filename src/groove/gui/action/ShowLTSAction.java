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

/**
 * The 'default exploration' action (class).
 */
public class ShowLTSAction extends SimulatorAction {

    /** Constructs a new action, for a given simulator. */
    public ShowLTSAction(Simulator simulator, boolean animated) {
        super(simulator, Options.SHOW_LTS_OPTION, Icons.HIDE_LTS_ICON);
        setEnabled(true);
    }

    @Override
    public void execute() {
        getLtsDisplay().getLTSTab().toggleShowLts();
    }

    @Override
    public void refresh() {
        setEnabled(true);

    }

}
