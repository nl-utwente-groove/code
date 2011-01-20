/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.gui.dialog;

import groove.trans.SystemProperties;
import groove.util.Version;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class VersionErrorDialog {

    /**
     * Dialog for the showing errors with grammar versions.
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return the value of the button pressed.
     */
    public static int show(Component parent, SystemProperties grammarProperties) {
        String msg =
            "The grammar you are trying to load was created with GROOVE\n"
                + "version " + grammarProperties.getGrooveVersion()
                + ", which is newer than this version "
                + Version.getCurrentGrooveVersion()
                + ".\nIf you try to open this grammar errors may occur.\n"
                + "Try to load the grammar anyway?";
        return JOptionPane.showConfirmDialog(parent, msg,
            "Error loading the grammar", JOptionPane.YES_NO_OPTION);
    }

}
