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

/**
 * Implements a file filter based on filename extension.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-11 15:46:59 $
 */
public abstract class ExtensionFilter extends
        javax.swing.filechooser.FileFilter implements java.io.FileFilter {

    /**
     * Constructs a new extension file filter, with a given description and
     * filename extension, and a flag to set whether directories are accepted.
     * @param description the textual description of the files to be accepted
     * @param acceptDirectories <tt>true</tt> if the filter is to accept
     *        directories
     */
    public ExtensionFilter(String description, boolean acceptDirectories) {
        this.description = description;
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
        return this.acceptExtension(file)
            || (this.isAcceptDirectories() && file.isDirectory());
    }

    /**
     * Accepts a file if its name ends on this filter's extension.
     */
    abstract public boolean acceptExtension(java.io.File file);

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
    abstract public String getExtension();

    /**
     * Strips an extension from a filename, if the extension is in fact there.
     * @param filename the filename to be stripped
     */
    public String stripExtension(String filename) {
        if (filename.endsWith(this.getExtension())) {
            return filename.substring(0,
                filename.lastIndexOf(this.getExtension()));
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
            return filename + this.getExtension();
        }
    }

    /**
     * Tests if a given filename has the extension of this filter.
     * @param filename the filename to be tested
     * @return <code>true</code> if <code>filename</code> has the extension
     *         of this filter
     */
    public boolean hasExtension(String filename) {
        return filename.endsWith(this.getExtension());
    }

    /**
     * Tests if a given filename has any extension.
     * @param filename the filename to be tested
     * @return <code>true</code> if <code>filename</code> has an extension
     * (not necessarily of this filter).
     */
    public boolean hasAnyExtension(String filename) {
        return new File(filename).getName().indexOf(SEPARATOR) >= 0;
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
    public final void setAcceptDirectories(boolean accept) {
        this.acceptDirectories = accept;
    }

    /** Returns the proper mode for a file chooser dialog. */
    public final int getFileSelectionMode() {
        int result;
        if (this.isAcceptDirectories()) {
            result = JFileChooser.FILES_AND_DIRECTORIES;
        } else {
            result = JFileChooser.FILES_ONLY;
        }
        return result;
    }

    /** The description of this filter. */
    private final String description;
    /** Indicates whether this filter also accepts directories. */
    private boolean acceptDirectories;

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
        return getPureName(file.getName());
    }

    /**
     * Returns the name part of a file name, without extension. The extension is
     * taken to be the part from the last #SEPARATOR occurrence (inclusive).
     * @param filename the filename to be stripped
     * @return the name part of <code>file.getName()</code>, without the
     *         extension
     */
    static public String getPureName(String filename) {
        int index = filename.lastIndexOf(SEPARATOR);
        if (index < 0) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Separator character between filename and extension.
     */
    static public final char SEPARATOR = '.';
}
