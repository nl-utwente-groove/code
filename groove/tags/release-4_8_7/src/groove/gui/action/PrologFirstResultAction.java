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
 * Action to set the first result of a query on the Prolog display.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PrologFirstResultAction extends SimulatorAction {
    /** Creates an instance of this action. */
    public PrologFirstResultAction(Simulator simulator) {
        super(simulator, Options.PROLOG_FIRST_ACTION_NAME, Icons.GO_START_ICON);
        putValue(SHORT_DESCRIPTION, Options.PROLOG_FIRST_ACTION_NAME);
    }

    @Override
    public void execute() {
        getPrologDisplay().executeQuery();
        getPrologDisplay().giveFocusToNextResultButton();
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().hasGrammar());
    }
}
