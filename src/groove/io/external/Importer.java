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

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.io.FileType;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * Importer for resources. Can import either graphs or text files.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public interface Importer extends Porter {
    /**
     * Import resource from file.
     * @param file File to read from.
     * @param fileType Format to use.
     * @param grammar Grammar used as target (treat read-only)
     * @return Resources to be added to the grammar.
     * @throws PortException if an error (typically IO-related) occurred during import
     */
    public Set<Resource> doImport(File file, FileType fileType, GrammarModel grammar)
        throws PortException;

    /**
     * Import resource from data stream
     * @param name Name of resource to import
     * @param stream Stream to read data from
     * @param fileType Format to use.
     * @param grammar Grammar used as target (treat read-only)
     * @return Resources to be added to the grammar.
     * @throws PortException if an error (typically IO-related) occurred during import
     */
    public Set<Resource> doImport(String name, InputStream stream,
            FileType fileType, GrammarModel grammar) throws PortException;

    /**
     * A resource that may be generated during import, can contain either a graph or text (not both).
     * Simply union for both types.
     * @author Harold Bruintjes
     * @version $Revision $
     */
    public class Resource {
        private final String name;
        private final ResourceKind kind;
        private final AspectGraph resourceGraph;
        private final String resourceString;

        /** Constructs a graph-based resource. */
        public Resource(ResourceKind kind, String name, AspectGraph resource) {
            this.kind = kind;
            this.name = name;
            this.resourceGraph = resource;
            this.resourceString = null;
        }

        /** Constructs a text-based resource. */
        public Resource(ResourceKind kind, String name, String resource) {
            this.kind = kind;
            this.name = name;
            this.resourceGraph = null;
            this.resourceString = resource;
        }

        /** Returns the kind of this resource. */
        public ResourceKind getKind() {
            return this.kind;
        }

        /** Returns the name of this resource. */
        public String getName() {
            return this.name;
        }

        /** Indicates if this is a graph-based resource. */
        public boolean isGraph() {
            return this.resourceGraph != null;
        }

        /** Returns the wrapped graph-based resource, if any. */
        public AspectGraph getGraphResource() {
            return this.resourceGraph;
        }

        /** Returns the wrapped text-based resource, if any. */
        public String getTextResource() {
            return this.resourceString;
        }
    }
}
