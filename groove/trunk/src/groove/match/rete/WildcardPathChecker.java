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
package groove.match.rete;

import groove.rel.RegExpr.Wildcard;
import groove.rel.RegExpr.Wildcard.LabelConstraint;
import groove.trans.HostEdge;

/**
 * Checks against some edge that conforms to a specific {@link Wildcard}
 * expression.
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public class WildcardPathChecker extends SingleEdgePathChecker {

    /**
     * Creates a checker node for a given wild-card expression 
     */
    public WildcardPathChecker(ReteNetwork network, Wildcard expression) {
        super(network, expression);
    }

    @Override
    protected RetePathMatch makeMatch(HostEdge edge) {
        RetePathMatch m;
        if (this.getExpression().allVarSet().size() > 0) {
            m = new RetePathMatch(this, edge);
            m.getValuation().put(
                this.getExpression().allVarSet().iterator().next(),
                edge.getType());
        } else {
            m = new RetePathMatch(this, edge);
        }
        return m;
    }

    @Override
    public void receive(ReteNetworkNode source, HostEdge gEdge, Action action) {
        if ((this.getGuard() == null)
            || (this.getGuard().isSatisfied(gEdge.label()))) {
            super.receive(source, gEdge, action);
        }
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (this == node)
            || ((node instanceof WildcardPathChecker)
                && this.getOwner().equals(node.getOwner()) && this.expression.equals(((WildcardPathChecker) node).getExpression()));
    }

    /**
     * @return <code>true</code> if the wild card associated with this
     * n-node is guarded.
     */
    public boolean isGuarded() {
        return ((Wildcard) this.getExpression()).getGuard() != null;
    }

    /**
     * Returns the constraint associated with this n-node's wild-card
     * @return an instance of the {@link LabelConstraint} object, or 
     * <code>null</code> if this n-node's wild-card is not guarded.
     */
    public LabelConstraint getGuard() {
        return ((Wildcard) this.getExpression()).getGuard();
    }

    @Override
    public void updateBegin() {
        //Do nothing

    }

    @Override
    public void updateEnd() {
        //Do nothing        
    }
}
