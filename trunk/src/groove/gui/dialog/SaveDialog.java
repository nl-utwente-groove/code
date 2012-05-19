/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.io.ExtensionFilter;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Class that show a dialog for saving files.
 */
public class SaveDialog {

    /**
     * Brings up a save dialog based on a given file chooser filter. The chosen
     * filename is appended with the required extension. Confirmation is asked
     * if the chosen filename already exists and does not equal the original
     * file (also passed in as a parameter).
     * @param originalFile the file from which the object to be saved has been
     *        loaded; <code>null</code> if there is none such
     * @return the chosen file, if any; if null, no file has been chosen
     */
    public static File show(JFileChooser chooser, java.awt.Component parent,
            File originalFile) {
        chooser.rescanCurrentDirectory();
        // choose a file name to save to,
        // asking confirmation if an existing file is to be overwritten
        boolean doSave; // indicates that the save should be carried through
        boolean noChoice; // indicates that a definite choice has not been
                          // made
        File res = null; // the file to save to (if doSave)
        do {
            doSave =
                (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION);
            if (doSave) {
                // apparently we're set to save
                res = chooser.getSelectedFile();
                // if the file exists, defer definite choice
                noChoice =
                    res.exists()
                        && (originalFile != null)
                        && !res.getAbsoluteFile().equals(
                            originalFile.getAbsoluteFile());
                if (noChoice) {
                    // ask for confirmation before overwriting file
                    int overwrite =
                        JOptionPane.showConfirmDialog(parent,
                            "Overwrite existing file \"" + res.getName()
                                + "\"?");
                    // any answer but NO is a definite choice
                    noChoice = (overwrite == JOptionPane.NO_OPTION);
                    // any answer but YES means don't save
                    doSave = (overwrite == JOptionPane.YES_OPTION);
                }
                // extend file name if chosen under an extension filter
                javax.swing.filechooser.FileFilter filter =
                    chooser.getFileFilter();
                if (filter instanceof ExtensionFilter) {
                    res =
                        new File(
                            ((ExtensionFilter) filter).addExtension(res.getPath()));
                }
            } else {
                // a choice not to save is a definite choice
                noChoice = false;
            }
        } while (noChoice);
        // return the file if the choice is to save, null otherwise
        if (doSave) {
            return res;
        } else {
            return null;
        }
    }

}
