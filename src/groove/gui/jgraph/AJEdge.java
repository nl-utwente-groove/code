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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.DefaultPort;

/**
 * Convenience subclass of {@link JEdge},
 * containing unchecked casts to appropriate subtypes.
 * @author rensink
 * @version $Revision $
 */
abstract public class AJEdge<G extends Graph<?,?>,JG extends JGraph<G>,JM extends JModel<G>,JV extends JVertex<G>>
        extends JEdge<G> {
    @SuppressWarnings("unchecked")
    @Override
    public JV getSourceVertex() {
        DefaultPort source = getSource();
        return source == null ? null : (JV) source.getParent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JV getTargetVertex() {
        DefaultPort target = getTarget();
        return target == null ? null : (JV) target.getParent();
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

    @Override
    public Set<JV> getContext() {
        Set<JV> result;
        if (isLoop()) {
            result = Collections.singleton(getSourceVertex());
        } else {
            result = new HashSet<JV>();
            result.add(getSourceVertex());
            result.add(getTargetVertex());
        }
        return result;
    }
}
