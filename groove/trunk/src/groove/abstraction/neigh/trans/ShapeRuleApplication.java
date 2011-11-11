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
package groove.abstraction.neigh.trans;

import groove.abstraction.neigh.shape.Shape;
import groove.trans.DeltaTarget;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleApplication;
import groove.trans.RuleEffect;
import groove.trans.RuleEvent;

import java.util.Set;

/**
 * Rule application tuned for shapes.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeRuleApplication extends RuleApplication {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. Delegates to super. */
    public ShapeRuleApplication(RuleEvent event, HostGraph source) {
        super(event, source);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public Shape getTarget() {
        return (Shape) super.getTarget();
    }

    /**
     * Returns the source since the original shape was already cloned during
     * materialisation.
     */
    @Override
    protected Shape createTarget() {
        return (Shape) this.getSource();
    }

    @Override
    protected HostGraph computeTarget() {
        HostGraph target = this.createTarget();
        this.applyDelta(target);
        // EZ says: don't fix the target here, otherwise we can't show it
        // in a dialog.
        // target.setFixed();
        return target;
    }

    @Override
    protected void mergeNodes(RuleEffect record, DeltaTarget target) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void eraseNodes(RuleEffect record, DeltaTarget target) {
        Shape shape = (Shape) target;
        Set<HostNode> nodeSet = record.getErasedNodes();
        // Also remove the incident edges of the eraser nodes.
        if (nodeSet != null && !nodeSet.isEmpty()) {
            for (HostNode node : nodeSet) {
                for (HostEdge edge : shape.edgeSet(node)) {
                    if (!record.isErasedEdge(edge)) {
                        // EZ says: don't remove the edge from the shape here
                        // because this is an expensive operation. It has to
                        // go update the edge signatures as well, and this
                        // will be done many times in this loop.
                        // Also, since we're operating on the source, this
                        // operating will trigger a concurrent modification
                        // exception on the edge set.

                        // target.removeEdge(edge);

                        // Just mark the edge as erased.
                        this.registerErasure(edge);
                    }
                }
                // Remove the node and all incident edges.
                shape.removeNode(node);
            }
        }
        this.removeIsolatedValueNodes(target);
    }
}
