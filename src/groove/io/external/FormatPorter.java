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

import groove.graph.Graph;
import groove.gui.jgraph.GraphJGraph;
import groove.view.ResourceModel;

import java.awt.Frame;
import java.util.Collection;

/**
 * Supertype for exporters and importers.
 * @author Harold Bruintjes
 * @version $Revision $
 */
public interface FormatPorter {
    /** Indicates what kind of objects this porter handles. */
    public Kind getFormatKind();

    /** Sets the parent component to use in orienting dialogs. */
    public void setParent(Frame parent);

    /**
     * Get list of formats this exporter can handle, for a given resource kind.
     * @return list of supported formats.
     */
    public Collection<? extends Format> getSupportedFormats();

    /** Kinds of objects that can be ported. */
    public enum Kind {
        /** Instances of {@link Graph}. */
        GRAPH,
        /** Instances of {@link GraphJGraph}. */
        JGRAPH,
        /** Instances of {@link ResourceModel}. */
        RESOURCE;
    }
}
