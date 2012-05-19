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
import groove.graph.Element;
import groove.graph.ElementFactory;
import groove.graph.ElementMap;
import groove.graph.GraphProperties;
import groove.graph.Node;
import groove.graph.TypeEdge;
import groove.graph.TypeFactory;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * General interface for resource models constructed from an
 *  {@link AspectGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class GraphBasedModel<R> extends ResourceModel<R> {
    /** 
     * Creates a graph-based resource model from a given source.
     * @param grammar the grammar to which this resource belongs; may be {@code null}
     * if the resource is being considered outside the context of a grammar
     * @param source the aspect graph from which the resource is derived
     */
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
     * This method never returns {@code null}, and does a best-effort computation
     * even if the resource contains errors.
     * @return the set of labels occurring in the resource.
     */
    abstract public Set<TypeLabel> getLabels();

    /** 
     * Returns a mapping from elements of the source aspect graph to their corresponding
     * types.
     * @return a mapping from the elements of {@link #getSource()} to types in the
     * associated type graph, or {@code null} if {@link #hasErrors()} holds 
     */
    abstract public groove.view.GraphBasedModel.TypeModelMap getTypeMap();

    /** 
     * Transfers a collection of errors according to the
     * inverse of a model map.
     * @param errors the original errors
     * @param map mapping from aspect elements to rule graph elements
     * @return the transferred errors
     */
    final Collection<FormatError> transferErrors(
            Collection<FormatError> errors, ElementMap<?,?,?,?> map) {
        Map<Element,Element> inverseMap = getInverseMap(map);
        Collection<FormatError> newErrors = createErrors();
        for (FormatError error : errors) {
            newErrors.add(error.transfer(inverseMap));
        }
        return newErrors;
    }

    /** Convenience method to return the inverse of a given model map. */
    private final Map<Element,Element> getInverseMap(ElementMap<?,?,?,?> map) {
        Map<Element,Element> result = new HashMap<Element,Element>();
        for (Map.Entry<? extends Node,? extends Node> nodeEntry : map.nodeMap().entrySet()) {
            result.put(nodeEntry.getValue(), nodeEntry.getKey());
        }
        for (Map.Entry<? extends Edge,? extends Edge> edgeEntry : map.edgeMap().entrySet()) {
            result.put(edgeEntry.getValue(), edgeEntry.getKey());
        }
        return result;
    }

    private final AspectGraph source;

    /** Mapping from source graph elements to resource elements. */
    abstract public static class ModelMap<N extends Node,E extends Edge>
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

    /** Mapping from aspect graph elements to type graph elements. */
    public static class TypeModelMap extends ModelMap<TypeNode,TypeEdge> {
        /**
         * Creates a new, empty map.
         */
        public TypeModelMap(TypeFactory factory) {
            super(factory);
        }

        @Override
        public TypeFactory getFactory() {
            return (TypeFactory) super.getFactory();
        }

        @Override
        public TypeModelMap newMap() {
            return new TypeModelMap(getFactory());
        }
    }
}
