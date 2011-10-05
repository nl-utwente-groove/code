// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: LTSListener.java,v 1.2 2008-01-30 09:32:18 iovka Exp $
 */
package groove.lts;

/**
 * An extended graph listener, which is also notified of explore actions on an
 * LTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GTSListener {
    /**
     * Signals that a node has been added to a given graph.
     * @param gts the graph that has been updated
     * @param state the node that has been added
     * @require <tt>graph.containsElement(elem)</tt>
     */
    void addUpdate(GTS gts, GraphState state);

    /**
     * Signals that an edge has been added to a given graph.
     * @param gts the graph that has been updated
     * @param transition the edge that has been added
     * @require <tt>graph.containsElement(elem)</tt>
     */
    void addUpdate(GTS gts, GraphTransition transition);

    /**
     * Update method called when a state of the LTS is set to closed, in the
     * course of LTS exploration.
     * @see GTS#isOpen(GraphState)
     */
    public void closeUpdate(GTS graph, GraphState explored);
}
