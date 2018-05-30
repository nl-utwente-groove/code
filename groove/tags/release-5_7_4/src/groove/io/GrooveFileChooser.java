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
 * $Id$
 */
package groove.io;

import groove.util.Groove;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

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
        // EZ says: attempt to fix SF bug #418.
        //setFileSelectionMode(FILES_ONLY);
        setFileSelectionMode(FILES_AND_DIRECTORIES);
    }

    /**
     * Indicates if the file view should allow traversal of directories.
     * @return <tt>false</tt> if the current file filter is a rule system
     *         filter
     */
    @Override
    public boolean isTraversable(File file) {
        return super.isTraversable(file) && !(hasFileType() && getFileType().hasExtension(file));
    }

    /* Makes sure the file name is set in the UI. */
    @Override
    public void setSelectedFile(File file) {
        super.setSelectedFile(file);
        FileChooserUI ui = getUI();
        if (file != null && ui instanceof BasicFileChooserUI) {
            ((BasicFileChooserUI) ui).setFileName(file.getName());
        }
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
        if (result != null && !result.exists() && hasFileType()) {
            result = getFileType().addExtension(result);
        }
        return result;
    }

    /** Indicates if the currently selected file filter has an associated {@link FileType}. */
    public boolean hasFileType() {
        return getFileFilter() instanceof ExtensionFilter;
    }

    /** 
     * Returns the file type of the currently selected file filter, 
     * if that file filter is an {@link ExtensionFilter}.
     */
    public FileType getFileType() {
        FileType result = null;
        FileFilter current = getFileFilter();
        if (current instanceof ExtensionFilter) {
            result = ((ExtensionFilter) current).getFileType();
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
                    JOptionPane.showConfirmDialog(this,
                        f.getName() + " already exists, overwrite?", "Overwrite existing file",
                        JOptionPane.YES_NO_OPTION);
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
    private static final Map<Set<FileType>,GrooveFileChooser> listMap =
        new HashMap<>();

    /** Returns a file chooser object for selecting directories. */
    public static GrooveFileChooser getInstance() {
        return getInstance(Collections.<FileType>emptySet());
    }

    /** Returns the file chooser object associated with the given file type. */
    public static GrooveFileChooser getInstance(FileType fileType) {
        return getInstance(EnumSet.of(fileType));
    }

    /** Returns the file chooser object associated with the given set
     * of file types. If the set is empty, the chooser will accept
     * directories only. */
    public static GrooveFileChooser getInstance(Set<FileType> fileTypes) {
        GrooveFileChooser result = listMap.get(fileTypes);
        if (result == null) {
            result = new GrooveFileChooser();
            ExtensionFilter first = null;
            for (FileType fileType : fileTypes) {
                ExtensionFilter filter = fileType.getFilter();
                result.addChoosableFileFilter(filter);
                if (first == null) {
                    first = filter;
                }
            }
            if (fileTypes.isEmpty()) {
                result.setFileSelectionMode(DIRECTORIES_ONLY);
                result.setApproveButtonText("Select");
            }
            result.setFileFilter(first);
            listMap.put(fileTypes, result);
        }
        result.setCurrentDirectory(result.getFileSystemView().createFileObject(
            Groove.CURRENT_WORKING_DIR));
        return result;
    }
}
