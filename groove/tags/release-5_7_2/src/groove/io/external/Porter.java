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

import groove.grammar.model.ResourceModel;
import groove.graph.Graph;
import groove.gui.Simulator;
import groove.gui.jgraph.JGraph;
import groove.io.FileType;

import java.util.Set;

/**
 * Supertype for exporters and importers.
 * @author Harold Bruintjes
 * @version $Revision $
 */
public interface Porter {
    /** Indicates what kind of objects this porter handles. */
    public Set<Kind> getFormatKinds();

    /** Sets the parent component to use in orienting dialogs. */
    public void setSimulator(Simulator simulator);

    /**
     * Get list of file types this im-/exporter can handle.
     * @return list of supported file types.
     */
    public Set<FileType> getSupportedFileTypes();

    /** Kinds of objects that can be ported. */
    public enum Kind {
        /** Instances of {@link Graph}. */
        GRAPH,
        /** Instances of {@link JGraph}. */
        JGRAPH,
        /** Instances of {@link ResourceModel}. */
        RESOURCE;
    }
}
