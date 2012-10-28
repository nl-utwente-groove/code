// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: GrooveFileChooser.java,v 1.4 2008-01-30 09:33:42 iovka Exp $
 */
package groove.io;

import groove.util.Groove;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileView;

/**
 * A file chooser with a {@link GrooveFileView}, which prevents traversal of
 * directories if these are selectable by the current file filter.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GrooveFileChooser extends JFileChooser {

    /** File chooser with initial directory {@link Groove#WORKING_DIR}. */
    // This class is now protected, what you probably want are the static
    // methods in the end of this class.
    protected GrooveFileChooser() {
        this(Groove.CURRENT_WORKING_DIR);
    }

    /**
     * File chooser with given initial directory.
     */
    private GrooveFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        setFileView(createFileView());
        setAcceptAllFileFilterUsed(false);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Indicates if the file view should allow traversal of directories.
     * @return <tt>false</tt> if the current file filter is a rule system
     *         filter
     */
    @Override
    public boolean isTraversable(File file) {
        return super.isTraversable(file)
            && !(getFileFilter() instanceof ExtensionFilter && ((ExtensionFilter) getFileFilter()).acceptExtension(file));
    }

    /**
     * This implementation adds a file extension, if the file filter used is an
     * {@link ExtensionFilter}.
     */
    @Override
    public File getSelectedFile() {
        // Set the current directory to be reused later
        File currDir = super.getCurrentDirectory();
        if (currDir != null) {
            Groove.CURRENT_WORKING_DIR = currDir.getAbsolutePath();
        }

        File result = super.getSelectedFile();
        if (result != null && !result.exists()
            && getFileFilter() instanceof ExtensionFilter) {
            ExtensionFilter fileFilter = (ExtensionFilter) getFileFilter();
            String resultName = fileFilter.addExtension(result.getName());
            result = new File(result.getParentFile(), resultName);
        }
        return result;
    }

    @Override
    public void approveSelection() {
        if (getDialogType() == SAVE_DIALOG && isAskOverwrite()) {
            File f = getSelectedFile();
            // When saving, check if file already exists. If so, ask for overwrite confirmation
            if (f.exists()) {
                int result =
                    JOptionPane.showConfirmDialog(this, f.getName()
                        + " already exists, overwrite?",
                        "Overwrite existing file", JOptionPane.YES_NO_OPTION);
                switch (result) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                    // If no or close, do not approve
                case JOptionPane.NO_OPTION:
                default:
                    return;
                }
            } else {
                // Approve if file doesn't exist yet
                super.approveSelection();
                return;
            }
        } else {
            // For open dialog simply approve
            super.approveSelection();
            return;
        }
    }

    /** Changes the confirmation behaviour on overwriting an existing file. */
    public void setAskOverwrite(boolean askOverwrite) {
        this.askOverwrite = askOverwrite;
    }

    /** Returns the current confirmation setting on overwriting existing files. */
    public boolean isAskOverwrite() {
        return this.askOverwrite;
    }

    /**
     * If true, a dialog will show asking if a file should be overwritten 
     * during save if it already exists. Defaults to {@code true}.
     */
    private boolean askOverwrite = true;

    /**
     * Factory method for the file view set in this file chooser.
     * @return This implementation returns a {@link GrooveFileView}.
     */
    protected FileView createFileView() {
        return new GrooveFileView();
    }

    // Maps from filters to choosers.
    private static final Map<ExtensionFilter,GrooveFileChooser> simpleMap =
        new HashMap<ExtensionFilter,GrooveFileChooser>();
    private static final Map<List<ExtensionFilter>,GrooveFileChooser> listMap =
        new HashMap<List<ExtensionFilter>,GrooveFileChooser>();

    /** Returns the file chooser object associated with the given filter. */
    public static GrooveFileChooser getFileChooser(ExtensionFilter filter) {
        GrooveFileChooser chooser = simpleMap.get(filter);
        if (chooser == null) {
            chooser = new GrooveFileChooser();
            chooser.addChoosableFileFilter(filter);
            chooser.setFileSelectionMode(filter.getFileSelectionMode());
            simpleMap.put(filter, chooser);
        }
        chooser.setCurrentDirectory(chooser.getFileSystemView().createFileObject(
            Groove.CURRENT_WORKING_DIR));
        return chooser;
    }

    /** Returns the file chooser object associated with the given filter list. */
    public static GrooveFileChooser getFileChooser(List<ExtensionFilter> filters) {
        GrooveFileChooser chooser = listMap.get(filters);
        if (chooser == null) {
            chooser = new GrooveFileChooser();
            for (ExtensionFilter filter : filters) {
                chooser.addChoosableFileFilter(filter);
            }
            chooser.setFileFilter(filters.get(0));
            chooser.setFileSelectionMode(filters.get(0).getFileSelectionMode());
            listMap.put(filters, chooser);
        }
        chooser.setCurrentDirectory(chooser.getFileSystemView().createFileObject(
            Groove.CURRENT_WORKING_DIR));
        return chooser;
    }

}
