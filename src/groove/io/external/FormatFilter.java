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

import groove.io.ExtensionFilter;

import java.io.File;

/**
 * ExtensionFilter specialisation for Format
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class FormatFilter extends ExtensionFilter {
    private final Format format;

    /** Constructs a filter for a given (non-{@code null}) format. */
    public FormatFilter(Format format) {
        super(format.getDescription(), false);
        this.format = format;
    }

    @Override
    public boolean acceptExtension(File f) {
        f.getName();
        for (String ext : this.format.getExtensions()) {
            if (f.getName().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return this.format.getDescription();
    }

    @Override
    public String getExtension() {
        return this.format.getExtensions().iterator().next();
    }

    /** Returns the format on which this filter is based. */
    public Format getFormat() {
        return this.format;
    }

    @Override
    public boolean isAcceptDirectories() {
        return true;
    }

}
