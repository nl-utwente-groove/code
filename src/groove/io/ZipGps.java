/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.io;

import groove.util.Groove;

import java.io.File;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
@Deprecated
public class ZipGps extends ArchiveGps {
    /** create a new instance */
    public ZipGps(boolean layouted) {
        super(layouted);
    }

    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    /** Filter for rule system files. */
    static private final ExtensionFilter SYSTEM_FILTER =
        Groove.createRuleSystemFilter();
    /** File filter for jar files. */
    static protected final ExtensionFilter GRAMMAR_FILTER =
        new ExtensionFilter("Zip-file containing Groove production system",
            ".gps.zip", false) {
            @Override
            public boolean accept(File file) {
                return super.accept(file) || file.isDirectory()
                    && !SYSTEM_FILTER.hasExtension(file.getName());
            }

        };
}
