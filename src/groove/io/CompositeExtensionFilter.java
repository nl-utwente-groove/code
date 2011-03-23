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
package groove.io;

import java.io.File;
import java.util.EnumSet;

/**
 * An implementation of ExtensionFilter that is associated with a multiple
 * extensions.
 * Objects of this class store a list of extension filters which compose the
 * object. 
 * 
 * @author Eduardo Zambon
 */
public final class CompositeExtensionFilter extends ExtensionFilter {

    /**
     * Constructs a new extension file filter, from a given list of file types.
     * @param acceptDir <tt>true</tt> if the filter is to accept directories
     */
    public CompositeExtensionFilter(EnumSet<FileType> fileTypes,
            boolean acceptDir) {
        super(FileType.getDescription(fileTypes), acceptDir);
        int size = fileTypes.size();
        FileType fileType[] = new FileType[size];
        fileTypes.toArray(fileType);
        this.filters = new ExtensionFilter[size];
        for (int i = 0; i < size; i++) {
            this.filters[i] = FileType.getFilter(fileType[i]);
        }
    }

    /** The array of filters composing this object. */
    private final ExtensionFilter filters[];

    /**
     * Accepts the given file if there is a filter in the list that accepts
     * the file extension, or the file is a directory and directories are
     * accepted.
     */
    @Override
    public boolean accept(File file) {
        boolean result = this.isAcceptDirectories() && file.isDirectory();
        if (!result) {
            for (ExtensionFilter filter : this.filters) {
                if (filter.acceptExtension(file)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Accepts the given file if there is a filter in the list that accepts
     * the file extension.
     */
    @Override
    public boolean acceptExtension(File file) {
        boolean result = false;
        for (ExtensionFilter filter : this.filters) {
            if (filter.acceptExtension(file)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * There is no single extension associated with a composite filter.
     * This method always returns the empty string.
     */
    @Override
    public String getExtension() {
        return "";
    }

    /** 
     * Tries all filters in the list until one filter can strip the extension
     * or the list is exhausted.
     */
    @Override
    public String stripExtension(String fileName) {
        File file = new File(fileName);
        for (ExtensionFilter filter : this.filters) {
            if (filter.acceptExtension(file)) {
                return filter.stripExtension(fileName);
            }
        }
        return fileName;
    }

}
