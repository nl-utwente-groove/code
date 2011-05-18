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

import groove.gui.ControlPanel;

import javax.swing.Icon;

/**
 * Action class that works on top of a control panel.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ControlAction extends SimulatorAction {
    /**
     * Constructs an action.
     * @param panel control panel that owns the action
     * @param name name of the action
     * @param icon icon of the action
     */
    public ControlAction(ControlPanel panel, String name, Icon icon) {
        super(panel.getSimulator(), name, icon);
        panel.addRefreshable(this);
        this.panel = panel;
    }

    /** Returns the control panel that owns the action. */
    final protected ControlPanel getPanel() {
        return this.panel;
    }

    private final ControlPanel panel;
}
