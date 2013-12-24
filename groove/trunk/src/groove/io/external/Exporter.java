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

import java.io.File;
import java.util.Set;

/**
 * Subtype of exporters.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Exporter extends Porter {
    /** 
     * Indicates if this exporter is suitable for processing a given exportable.
     * This is true if and only if {@link #getFileTypes(Exportable)} is non-empty.
     */
    public boolean exports(Exportable exportable);

    /** Returns the file types that can be used for a given exportable. */
    public Set<FileType> getFileTypes(Exportable exportable);

    /** 
     * Exports a given exportable resource.
     * @param exportable the (non-{@code null}) resource to be exported
     * @param file destination file
     * @param fileType used to determine format and extension
     * @throws PortException if something went wrong during export (typically I/O-related)
     */
    public void doExport(Exportable exportable, File file, FileType fileType)
        throws PortException;
}
