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
 * $Id: VerifyMenu.java,v 1.5 2008-03-04 14:46:56 kastenberg Exp $
 */

package groove.gui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Menu item for the simulator for starting the verification process.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class VerifyMenu extends JMenu {
    /**
     * Constructs an verify menu on top of a given simulator. The menu will
     * appear as soon as a grammar has been loaded.
     * @param simulator the associated simulator
     */
    public VerifyMenu(Simulator simulator) {
        super(Options.VERIFY_MENU_NAME);
        setMnemonic(Options.VERIFY_MENU_MNEMONIC);
        JMenuItem item =
            new JMenuItem(simulator.getProvideTemporalFormulaAction());
        add(item);
    }
}
