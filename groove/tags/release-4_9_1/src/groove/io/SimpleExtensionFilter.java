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

/**
 * An implementation of ExtensionFilter that is associated with a single
 * extension.
 * 
 * @author Eduardo Zambon
 */
public final class SimpleExtensionFilter extends ExtensionFilter {

    /**
     * Constructs a new extension file filter, with a given description and
     * filename extension, and a flag to set whether directories are accepted.
     * @param description the textual description of the files to be accepted
     * @param acceptDir <tt>true</tt> if the filter is to accept directories
     */
    public SimpleExtensionFilter(String description, String extension,
            boolean acceptDir) {
        super(description + " (*" + extension + ")", acceptDir);
        this.extension = extension;
    }

    /** The filename extension on which this filter selects. */
    private final String extension;

    @Override
    public boolean acceptExtension(String filename) {
        return filename.endsWith(this.getExtension());
    }

    @Override
    public boolean acceptExtension(File file) {
        return acceptExtension(file.getName());
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

}
