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
 * $Id: ExtensionFilter.java,v 1.10 2008-03-11 15:46:59 kastenberg Exp $
 */
package groove.io;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Implements a file filter based on filename extension.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-11 15:46:59 $
 */
public class ExtensionFilter extends javax.swing.filechooser.FileFilter
        implements java.io.FileFilter {

    /**
     * Constructs a new extension file filter, with empty description. This is
     * only good for adding and stripping extensions. The filter initially also
     * accepts directories.
     * @param extension the filename extension (including any preceding ".") of
     *        the files to be accepted
     */
    public ExtensionFilter(String extension) {
        this("", extension);
    }

    /**
     * Constructs a new extension file filter, with a given description and
     * filename extension. The filter initially also accepts directories.
     * @param description the textual description of the files to be accepted
     * @param extension the filename extension (including any preceding ".") of
     *        the files to be accepted
     */
    public ExtensionFilter(String description, String extension) {
        this(description, extension, true);
    }

    /**
     * Constructs a new extension file filter, with a given description and
     * filename extension, and a flag to set whether directories are accepted.
     * @param description the textual description of the files to be accepted
     * @param extension the filename extension (including any preceding ".") of
     *        the files to be accepted
     * @param acceptDirectories <tt>true</tt> if the filter is to accept
     *        directories
     */
    public ExtensionFilter(String description, String extension,
            boolean acceptDirectories) {
        this.description = description + " (*" + extension + ")";
        this.extension = extension;
        setAcceptDirectories(acceptDirectories);
    }

    /**
     * Accepts a file if its name ends on this filter's extension, or it is a
     * directory and directories are accepted.
     * @see #acceptExtension(java.io.File)
     * @see #acceptDirectories
     */
    @Override
    public boolean accept(java.io.File file) {
        return acceptExtension(file)
            || (this.acceptDirectories && file.isDirectory());
    }

    /**
     * Accepts a file if its name ends on this filter's extension.
     */
    public boolean acceptExtension(java.io.File file) {
        return file.getName().endsWith(this.extension);
    }

    /**
     * Returns this filter's description.
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns this filter's extension.
     */
    public String getExtension() {
        return this.extension;
    }

    /**
     * Strips an extension from a filename, if the extension is in fact there.
     * @param filename the filename to be stripped
     */
    public String stripExtension(String filename) {
        if (filename.endsWith(this.extension)) {
            return filename.substring(0, filename.lastIndexOf(this.extension));
        } else {
            return filename;
        }
    }

    /**
     * Adds an extension to filename, if the extension is not yet there.
     * @param filename the filename to be provided with an extension
     */
    public String addExtension(String filename) {
        if (hasExtension(filename)) {
            return filename;
        } else {
            return filename + this.extension;
        }
    }

    /**
     * Tests if a given filename has the extension of this filter.
     * @param filename the filename to be tested
     * @return <code>true</code> if <code>filename</code> has the extension
     *         of this filter
     */
    public boolean hasExtension(String filename) {
        return filename.endsWith(this.extension);
    }

    /**
     * Indicates whether this filter accepts directory files, in addition to
     * files ending on the required extension.
     */
    public boolean isAcceptDirectories() {
        return this.acceptDirectories;
    }

    /**
     * Sets whether this filter accepts directory files, in addition to files
     * ending on the required extension.
     * @param accept if true, this filter will accept directories
     */
    public void setAcceptDirectories(boolean accept) {
        this.acceptDirectories = accept;
    }

    /** The description of this filter. */
    private final String description;
    /** The filenam extension on which this filter selects. */
    private final String extension;
    /** Indicates whether this filter also accepts directories. */
    private boolean acceptDirectories;

    /**
     * Brings up a save dialog based on a given file chooser filter. The chosen
     * filename is appended with the required extension. Confirmation is asked
     * if the chosen filename already exists and does not equal the original
     * file (also passed in as a parameter).
     * @param originalFile the file from which the object to be saved has been
     *        loaded; <code>null</code> if there is none such
     * @return the chosen file, if any; if null, no file has been chosen
     */
    public static File showSaveDialog(JFileChooser chooser,
            java.awt.Component parent, File originalFile) {
        // HARMEN: what if original file is null ?
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

    /**
     * Returns the extension part of a file name. The extension is taken to be
     * the part from the last #SEPARATOR occurrence (inclusive).
     * @param file the file to obtain the name from
     * @return the extension part of <code>file.getName()</code>
     * @see File#getName()
     */
    static public String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(SEPARATOR));
    }

    /**
     * Returns the name part of a file name, without extension. The extension is
     * taken to be the part from the last #SEPARATOR occurrence (inclusive).
     * @param file the file to obtain the name from
     * @return the name part of <code>file.getName()</code>, without the
     *         extension
     * @see File#getName()
     */
    static public String getPureName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(SEPARATOR);
        if (index < 0) {
            return name;
        } else {
            return name.substring(0, index);
        }
    }

    /**
     * Separator character between filename and extension.
     */
    static public final char SEPARATOR = '.';
}