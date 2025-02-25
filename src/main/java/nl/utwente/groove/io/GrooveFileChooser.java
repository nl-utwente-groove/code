// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.io;

import java.io.File;
import java.util.BitSet;
import java.util.Collection;
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.util.Groove;

/**
 * A file chooser with a {@link GrooveFileView}, which prevents traversal of
 * directories if these are selectable by the current file filter.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GrooveFileChooser extends JFileChooser {
    /** File chooser for Groove tools.
     * The initial directory is calculated using {@link #getStartDirectory}
     * @param multi flag determining if multiple file selection is allowed.
     */
    private GrooveFileChooser(Set<FileType> fileTypes, boolean multi) {
        super(getStartDirectory(fileTypes));
        this.fileTypes = fileTypes;
        setFileView(createFileView());
        setAcceptAllFileFilterUsed(false);
        ToolTipManager.sharedInstance().registerComponent(this);
        var prefFilter = getPref(FILTER);
        ExtensionFilter selectedFilter = null;
        for (FileType fileType : fileTypes) {
            ExtensionFilter filter = fileType.getFilter();
            addChoosableFileFilter(filter);
            if (selectedFilter == null || fileType.toString().equals(prefFilter)) {
                selectedFilter = filter;
            }
        }
        if (fileTypes.isEmpty()) {
            setFileSelectionMode(DIRECTORIES_ONLY);
            setApproveButtonText("Select");
        }
        setFileFilter(selectedFilter);
        setMultiSelectionEnabled(multi);
    }

    /** Returns the file types associated with this chooser. */
    public Set<FileType> getFileTypes() {
        return this.fileTypes;
    }

    private final Set<FileType> fileTypes;

    @Override
    public String toString() {
        return toString(this.fileTypes);
    }

    /** Returns a key suitable for storing values of this chooser in the user preferences. */
    public String toKey() {
        return toKey(this.fileTypes);
    }

    @Override
    public boolean isDirectorySelectionEnabled() {
        // normally, this is derived from the file selection mode
        // however, that is left at FILES_ONLY because otherwise
        // directory selection overwrites the selection in the dialog
        // We need to overwrite this to ensure that the Open button
        // in the dialog actually approves a selected directory,
        // See also SF bug #418
        return true;
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
        if (file != null && ui instanceof BasicFileChooserUI bfc) {
            bfc.setFileName(file.getName());
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
        if (current instanceof ExtensionFilter ef) {
            result = ef.getFileType();
        }
        return result;
    }

    @Override
    public void approveSelection() {
        if (getDialogType() == SAVE_DIALOG && isAskOverwrite()) {
            File f = getSelectedFile();
            // When saving, check if file already exists. If so, ask for overwrite confirmation
            if (f.exists()) {
                int result = JOptionPane
                    .showConfirmDialog(this, f.getName() + " already exists, overwrite?",
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

    /** Returns the {@code index}th element of the user preferences for this file chooser.
     */
    private @Nullable String getPref(int index) {
        return getPref(this.fileTypes, index);
    }

    /** Returns the new preference values to be persisted upon quitting. */
    public String[] newPrefs() {
        var fileType = getFileType();
        return new String[] {getCurrentDirectory().getAbsolutePath(), fileType == null
            ? ""
            : fileType.toString()};
    }

    /** Constructs a String representation for a file chooser. */
    static private String toString(Set<FileType> fileTypes) {
        return "GrooveFileChooser [fileTypes=" + fileTypes + "]";
    }

    /** Constructs a unique key for a file chooser. */
    static private String toKey(Set<FileType> fileTypes) {
        BitSet set = new BitSet();
        fileTypes.stream().map(t -> t.ordinal()).forEach(set::set);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < set.size(); i++) {
            builder
                .append(set.get(i)
                    ? '1'
                    : '0');
        }
        return "GFC " + builder;
    }

    /** Returns the starting directory for a new file chooser.
     * This is retrieved from the persistent user properties, or if not present,
     * initialised to {@link Groove#CURRENT_WORKING_DIR}.
     */
    static private @NonNull String getStartDirectory(Set<FileType> fileTypes) {
        var result = getPref(fileTypes, WORKING_DIR);
        return result == null
            ? Groove.CURRENT_WORKING_DIR
            : result;
    }

    /** Returns the {@code index}th element of the user preferences for a given
     * set of file types.
     */
    static private @Nullable String getPref(Set<FileType> fileTypes, int index) {
        var prefs = Options.getUserPrefs(toKey(fileTypes));
        return prefs.length > index
            ? prefs[index]
            : null;
    }

    /** Index of the working directory property in the user properties. */
    static private final int WORKING_DIR = 0;
    /** Index of the file filter  property in the user properties. */
    static private final int FILTER = 1;

    /** Returns all the file choosers created in the course of this invocation. */
    static public Collection<GrooveFileChooser> getChoosers() {
        return listMap.values();
    }

    // Maps from filters to choosers.
    private static final Map<Set<FileType>,GrooveFileChooser> listMap = new HashMap<>();

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
        return getInstance(fileTypes, false);
    }

    /** Returns the file chooser object associated with the given set
     * of file types. If the set is empty, the chooser will accept
     * directories only. A flag controls whether multiple selection is allowed. */
    public static GrooveFileChooser getInstance(Set<FileType> fileTypes, boolean multi) {
        GrooveFileChooser result = listMap.get(fileTypes);
        if (result == null) {
            result = new GrooveFileChooser(fileTypes, multi);
            listMap.put(fileTypes, result);
        }
        return result;
    }
}
