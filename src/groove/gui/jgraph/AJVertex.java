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
package groove.gui.jgraph;

import groove.graph.Graph;

import java.util.Set;

/**
 * Convenience subclass of {@link JVertex},
 * containing unchecked casts to appropriate subtypes.
 * @author rensink
 * @version $Revision $
 */
public abstract class AJVertex<G extends Graph<?,?>,JG extends JGraph<G>,JM extends JModel<G>,JE extends JEdge<G>>
        extends JVertex<G> {
    @Override
    @SuppressWarnings("unchecked")
    public Set<JE> getContext() {
        return getPort().getEdges();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JG getJGraph() {
        return (JG) super.getJGraph();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JM getJModel() {
        return (JM) super.getJModel();
    }
}
