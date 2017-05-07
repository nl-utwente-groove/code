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

import java.io.File;
import java.util.List;

/**
 * Implements a file filter based on the filename extensions of a file type.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-11 15:46:59 $
 */
public class ExtensionFilter extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter {
    /**
     * Constructs a new extension file filter, to be
     * associated with a given file type.
     */
    public ExtensionFilter(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Returns the file type associated with this filter.
     */
    public FileType getFileType() {
        return this.fileType;
    }

    /**
     * Returns this filter's description.
     */
    @Override
    public String getDescription() {
        if (this.description == null) {
            this.description = createDescription();
        }
        return this.description;
    }

    /**
     * Returns this filter's extensions.
     */
    public List<String> getExtensions() {
        return getFileType().getExtensions();
    }

    /**
     * Accepts a file if its name ends on this filter's extension, or it is a
     * non-hidden directory with a (non-empty) pure name.
     * @see FileType#hasExtension(File)
     */
    @Override
    public boolean accept(File file) {
        return getFileType().hasExtension(file)
            || file.isDirectory() && !file.isHidden() && FileType.hasPureName(file);
    }

    private String createDescription() {
        StringBuilder result = new StringBuilder(this.fileType.getDescription());
        result.append(" (");
        boolean first = true;
        for (String extension : getExtensions()) {
            if (first) {
                first = false;
            } else {
                result.append(", ");
            }
            result.append("*");
            result.append(extension);
        }
        result.append(')');
        return result.toString();
    }

    /** The description of this filter. */
    private String description;
    /** The file type associated with this filter. */
    private final FileType fileType;
}
