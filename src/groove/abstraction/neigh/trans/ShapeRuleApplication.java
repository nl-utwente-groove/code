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
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.transform.DeltaTarget;
import groove.transform.RuleApplication;
import groove.transform.RuleEffect;
import groove.transform.RuleEvent;

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
        return target;
    }

    @Override
    protected void removeNodes(RuleEffect record, DeltaTarget target) {
        Shape shape = (Shape) target;
        if (record.hasRemovedNodes()) {
            for (HostNode node : record.getRemovedNodes()) {
                // the incident edges are now removed as part of eraseEdges
                // Remove the node and all incident edges.
                shape.removeNodeContext(node);
            }
        }
    }
}
