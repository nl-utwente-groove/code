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
 * $Id: DefaultGraph.java,v 1.8 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph.plain;

import groove.graph.GraphRole;

/**
 * Graph based on {@link PlainNode}s and {@link PlainEdge}s.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
public class PlainGraph extends NodeSetOutEdgeMapGraph<PlainNode,PlainEdge>
        implements Cloneable {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to be
     *         used for its <tt>newGraph()</tt> method.
     */
    static public PlainGraph getPrototype() {
        return new PlainGraph(NO_NAME, GraphRole.NONE);
    }

    /**
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     * @param name the (non-{@code null}) name of the graph.
     */
    public PlainGraph(String name, GraphRole role) {
        super(name, role);
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected PlainGraph(PlainGraph graph) {
        super(graph);
    }

    @Override
    public PlainGraph clone() {
        PlainGraph result = new PlainGraph(this);
        return result;
    }

    public PlainGraph newGraph(String name) {
        return new PlainGraph(getName(), getRole());
    }

    @Override
    public PlainFactory getFactory() {
        return PlainFactory.instance();
    }
}