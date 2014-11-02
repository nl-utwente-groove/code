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

import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.io.FileType;

import java.nio.file.Path;
import java.util.Set;

/**
 * Importer for resources. Can import either graphs or text files.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public interface Importer extends Porter {
    /**
     * Imports resource from file.
     * @param file file to read from
     * @param fileType determines format (importer) to be used
     * @param grammar target grammar for the imported resources; used to
     * determine import parameters but treated read-only
     * @return set of imported resources; may be empty but not {@code null}
     * @throws PortException if an error (typically IO-related) occurred during import
     */
    public Set<Resource> doImport(Path file, FileType fileType, GrammarModel grammar)
        throws PortException;
}
