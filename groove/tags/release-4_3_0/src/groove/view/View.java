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
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * General interface for classes that provide a view upon some other object (the
 * model) in terms of an {@link AspectGraph}. This is not a view in the MVC
 * sense; here, the view is more a kind a syntactical description for the model.
 * This syntactical description may still contain errors which prevent it from
 * being translated to a model.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface View<Model> {
    /**
     * Returns the actual view graph.
     */
    AspectGraph getAspectGraph();

    /** 
     * Sets a type graph for this view.
     */
    void setType(TypeGraph type);

    /**
     * Returns a mapping from the nodes in the view to the corresponding nodes
     * in the model that is being viewed.
     * @return the mapping from view to model elements; empty if the view
     *         contains errors.
     */
    ViewToModelMap<?,?> getMap();

    /**
     * Returns the (non-<code>null</code>) name of the underlying model.
     */
    String getName();

    /**
     * Returns the set of labels occurring in this view.
     * @return the set of labels occurring in the view; empty if the view
     *         contains errors.
     */
    public Set<TypeLabel> getLabels() throws FormatException;

    /**
     * Returns the underlying model. This can only be successful if there are no
     * syntax errors reported by {@link #getErrors()}.
     * @throws FormatException if there are syntax errors in the view that
     *         prevent it from being translated to a model
     */
    Model toModel() throws FormatException;

    /**
     * Retrieves the list of syntax errors in this view. Conversion to a model
     * can only be successful if this list is empty.
     * @return a non-<code>null</code>, possibly empty list of syntax errors
     */
    List<FormatError> getErrors();

    /** Mapping from view graph elements to model graph elements. */
    abstract class ViewToModelMap<N extends Node,E extends Edge<N>> extends
            ElementMap<AspectNode,AspectEdge,N,E> {
        /**
         * Creates a new map, on the basis of a given factory.
         */
        public ViewToModelMap(ElementFactory<N,E> factory) {
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