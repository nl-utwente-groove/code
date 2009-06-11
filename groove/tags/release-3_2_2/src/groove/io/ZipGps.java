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

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class ZipGps extends ArchiveGps {

    /** File filter for jar files. */
    static protected final ExtensionFilter GRAMMAR_FILTER =
        Groove.getFilter("Zip-file containing Groove production system", ".zip",
            false);
    
    /** create a new instance */
    public ZipGps(boolean layouted) {
        super(layouted);
    }

    public ExtensionFilter getExtensionFilter() {
            return GRAMMAR_FILTER;
        }
}
