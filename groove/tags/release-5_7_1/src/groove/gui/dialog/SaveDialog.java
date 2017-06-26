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

import groove.io.GrooveFileChooser;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Class that shows a dialog for saving files.
 */
public class SaveDialog {
    /**
     * Brings up a save dialog based on a given file chooser filter. The chosen
     * filename is appended with the required extension.
     * @param chooser the (non-{@code null}) file chooser to be used
     * @param parent parent for the dialog; may be {@code null}.
     * @param originalFile the file from which the object to be saved has been
     *        loaded; <code>null</code> if there is none such
     * @return the chosen file, if any; if null, no file has been chosen
     */
    public static File show(GrooveFileChooser chooser,
            java.awt.Component parent, File originalFile) {
        File result = null;
        chooser.rescanCurrentDirectory();
        // choose a file name to save to,
        // asking confirmation if an existing file is to be overwritten
        boolean doSave =
            (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION);
        if (doSave) {
            // apparently we're set to save
            result = chooser.getSelectedFile();
            // extend file name if chosen under an extension filter
            if (chooser.hasFileType()) {
                result = chooser.getFileType().addExtension(result);
            }
        }
        return result;
    }
}
