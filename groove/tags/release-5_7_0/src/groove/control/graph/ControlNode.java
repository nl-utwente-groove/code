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
package groove.control.graph;

import groove.control.CallStack;
import groove.control.Position;
import groove.graph.ANode;

/**
 * Node in a control graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlNode extends ANode {
    /**
     * Constructs a node for a given control graph, wrapping
     * a control position.
     * Also adds itself as node to the graph.
     */
    public ControlNode(ControlGraph graph, Position<?,?> pos) {
        super(graph.nodeCount());
        graph.addNode(this);
        this.pos = pos;
        this.graph = graph;
    }

    /** Indicates if this is the start node of the control graph. */
    public boolean isStart() {
        return this.graph.getStart() == this;
    }

    /** Returns the control position underlying this node. */
    public Position<?,?> getPosition() {
        return this.pos;
    }

    private final Position<?,?> pos;

    /** The control graph of which this is a node. */
    private final ControlGraph graph;

    /** Adds an outgoing verdict edge to the control graph. */
    public ControlEdge addVerdictEdge(ControlNode target, boolean success) {
        ControlEdge result = new ControlEdge(this, target, success);
        this.graph.addEdge(result);
        return result;
    }

    /** Adds an outgoing call edge to the control graph. */
    public ControlEdge addCallEdge(ControlNode target, CallStack callStack) {
        ControlEdge result = new ControlEdge(this, target, callStack);
        this.graph.addEdge(result);
        return result;
    }

    @Override
    protected String getToStringPrefix() {
        return "c";
    }

    @Override
    public String toString() {
        return this.pos.toString();
    }
}
