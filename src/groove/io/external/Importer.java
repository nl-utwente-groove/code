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

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.io.FileType;

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
    public Set<Resource> doImport(File file, FileType fileType, GrammarModel grammar)
        throws PortException;

    /**
     * Imports resource from data stream.
     * @param name name of resource to import
     * @param stream stream to read data from
     * @param fileType determines format (importer) to be used
     * @param grammar target grammar for the imported resources; used to
     * determine import parameters but treated read-only
     * @return set of imported resources; may be empty but not {@code null}
     * @throws PortException if an error (typically IO-related) occurred during import
     */
    public Set<Resource> doImport(QualName name, InputStream stream, FileType fileType,
        GrammarModel grammar) throws PortException;

    /**
     * A resource that may be generated during import, can contain either a graph or text (not both).
     * Simply union for both types.
     * @author Harold Bruintjes
     * @version $Revision $
     */
    public class Resource {
        private final QualName qualName;
        private final ResourceKind kind;
        private final AspectGraph resourceGraph;
        private final String resourceString;

        /** Constructs a graph-based resource. */
        public Resource(ResourceKind kind, AspectGraph resource) {
            this.kind = kind;
            this.qualName = resource.getQualName();
            this.resourceGraph = resource;
            this.resourceString = null;
        }

        /** Constructs a text-based resource. */
        public Resource(ResourceKind kind, QualName name, String resource) {
            this.kind = kind;
            this.qualName = name;
            this.resourceGraph = null;
            this.resourceString = resource;
        }

        /** Returns the kind of this resource. */
        public ResourceKind getKind() {
            return this.kind;
        }

        /** Returns the name of this resource. */
        public QualName getQualName() {
            return this.qualName;
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
