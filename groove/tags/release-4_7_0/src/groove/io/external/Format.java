/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.external;

import groove.io.FileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * File format for importing or exporting.
 * Contains a reference to a ResourceFormatter which may either export or import this Format.
 * @author Harold
 * @version $Revision $
 */
public class Format {
    private final List<String> extensions;
    private final String description;
    private final FormatPorter formatter;

    /** Creates an instance with a given formatter and supporting a given set of file extensions. */
    public Format(FormatPorter formatter, String description,
            String... extensions) {
        this.formatter = formatter;
        this.extensions = Arrays.asList(extensions);
        this.description = description;
    }

    /** Creates an instance with a given formatter and supporting a given set of file types. */
    public Format(FormatPorter formatter, FileType... types) {
        this.formatter = formatter;
        List<String> exts = new ArrayList<String>();
        for (FileType type : types) {
            exts.add(type.getExtension());
        }
        this.extensions = exts;
        this.description = types[0].getDescription();
    }

    /** Returns the formatter. */
    public FormatPorter getFormatter() {
        return this.formatter;
    }

    /** Returns a list of supported file name extensions. */
    public List<String> getExtensions() {
        return this.extensions;
    }

    /** Returns a description of this format. */
    public String getDescription() {
        Iterator<String> it = this.extensions.iterator();

        StringBuilder exts = new StringBuilder("*" + it.next());
        while (it.hasNext()) {
            exts.append(";*");
            exts.append(it.next());
        }
        return this.description + " (" + exts.toString() + ")";
    }

    /**
     * Strips the extension off a filename, if it ends on one of the
     * extensions recognised by this format.
     */
    public String stripExtension(String fileName) {
        for (String ext : this.extensions) {
            if (fileName.endsWith(ext)) {
                fileName =
                    fileName.substring(0, fileName.length() - ext.length());
                break;
            }
        }
        return fileName;
    }
}
