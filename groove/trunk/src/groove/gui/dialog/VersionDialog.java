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
public class VersionDialog {

    /**
     * Shows the dialog for loading an newer grammar version.
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return true if loading should continue, false otherwise
     */
    public static boolean showNew(Component parent,
            SystemProperties grammarProperties) {
        String msg =
            "The grammar you are trying to load was created with GROOVE\n"
                + "version " + grammarProperties.getGrooveVersion()
                + ", which is newer than this version "
                + Version.getCurrentGrooveVersion()
                + ".\nIf you try to open this grammar errors may occur.\n"
                + "Try to load the grammar anyway?";
        int buttonPressed =
            JOptionPane.showConfirmDialog(parent, msg,
                "Error loading the grammar", JOptionPane.YES_NO_OPTION);
        return buttonPressed == JOptionPane.YES_OPTION;
    }

    /**
     * Shows the dialog for loading an older grammar version from a file
     * (overwriting possible).
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return 0 if loading should continue, overwriting the current file,
     *         1 if loading should continue, creating a new local grammar, and
     *         -1 otherwise
     */
    public static int showOldFile(Component parent,
            SystemProperties grammarProperties) {
        String msg =
            "The grammar you are trying to load was created with GROOVE "
                + "version " + grammarProperties.getGrooveVersion()
                + ", which is older \nthan this version "
                + Version.getCurrentGrooveVersion()
                + ". When the grammar is opened, it will be converted "
                + "automatically, \nmaking it unusable in the old "
                + grammarProperties.getGrooveVersion() + " version.\n\n";
        String overwrite_text = "Overwrite";
        String save_as_text = "Save As";
        String cancel_text = "Cancel";
        String[] options = {overwrite_text, save_as_text, cancel_text};
        switch (JOptionPane.showOptionDialog(parent, msg,
            "Warning! Loading old grammar", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, overwrite_text)) {
        case JOptionPane.YES_OPTION:
            return 0;
        case JOptionPane.NO_OPTION:
            return 1;
        default:
            return -1;
        }
    }

    /**
     * Shows the dialog for loading an older grammar version from a url
     * (overwriting not possible).
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return true if loading should continue, creating a new local grammar,
     *         false if loading should be canceled
     */
    public static boolean showOldURL(Component parent,
            SystemProperties grammarProperties) {
        String msg =
            "The grammar you are trying to load was created with GROOVE "
                + "version " + grammarProperties.getGrooveVersion()
                + ", which is older \nthan this version "
                + Version.getCurrentGrooveVersion()
                + ". When the grammar is opened, it will be converted "
                + "automatically, \nmaking it unusable in the old "
                + grammarProperties.getGrooveVersion() + " version.\n\n";
        String save_as_text = "Save As";
        String cancel_text = "Cancel";
        String[] options = {save_as_text, cancel_text};
        int buttonPressed =
            JOptionPane.showOptionDialog(parent, msg,
                "Warning! Loading old grammar", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, save_as_text);
        return buttonPressed == JOptionPane.YES_OPTION;
    }
}
