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
 * $Id: View.java,v 1.4 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.ElementMap;
import groove.graph.GraphProperties;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.Map;
import java.util.Set;

/**
 * General interface for resource models constructed from an
 *  {@link AspectGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class GraphBasedModel<R> extends ResourceModel<R> {
    /** Creates a graph-based resource model from a given source. */
    protected GraphBasedModel(GrammarModel grammar, AspectGraph source) {
        super(grammar, ResourceKind.toResource(source.getRole()),
            source.getName());
        this.source = source;
    }

    @Override
    public boolean isEnabled() {
        return GraphProperties.isEnabled(getSource());
    }

    /**
     * The source of a  graph-based resource is the aspect graph.
     */
    @Override
    public AspectGraph getSource() {
        return this.source;
    }

    /**
     * Returns a mapping from the nodes in the model source to the corresponding 
     * nodes in the resource that is constructed from it.
     * @return the mapping from source graph to resource elements; empty if the model
     *         contains errors.
     */
    abstract public ModelMap<?,?> getMap();

    /**
     * Returns the set of labels occurring in this resource.
     * @return the set of labels occurring in the resource.
     */
    abstract public Set<TypeLabel> getLabels();

    private final AspectGraph source;

    /** Mapping from source graph elements to resource elements. */
    abstract public static class ModelMap<N extends Node,E extends Edge<N>>
            extends ElementMap<AspectNode,AspectEdge,N,E> {
        /**
         * Creates a new map, on the basis of a given factory.
         */
        public ModelMap(ElementFactory<N,E> factory) {
            super(factory);
        }

        /** Specialises the return type. */
        @SuppressWarnings("unchecked")
        @Override
        public Map<AspectNode,N> nodeMap() {
            return (Map<AspectNode,N>) super.nodeMap();
        }

        /** Specialises the return type. */
        @SuppressWarnings("unchecked")
        @Override
        public Map<AspectEdge,E> edgeMap() {
            return (Map<AspectEdge,E>) super.edgeMap();
        }

    }
}
