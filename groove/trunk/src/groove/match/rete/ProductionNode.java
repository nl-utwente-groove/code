/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.trans.Rule;
import groove.util.TreeHashSet;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ProductionNode extends ConditionChecker {

    /**
     * @param network The RETE network to which this node belongs
     * @param p The production rule associated with this checker. 
     */
    public ProductionNode(ReteNetwork network, Rule p,
            ReteStaticMapping antecedents) {
        super(network, p, null, antecedents);
    }

    @Override
    public boolean addSuccessor(ReteNetworkNode nnode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object node) {
        return (this == node)
            || ((node != null) && (node instanceof ProductionNode) && this.getProductionRule().equals(
                ((ProductionNode) node).getProductionRule()));
    }

    @Override
    public int hashCode() {
        return getProductionRule().hashCode();
    }

    /**
     * @return The rule associated with this checker node.
     */
    public Rule getProductionRule() {
        return (Rule) this.getCondition();
    }

    @Override
    public Set<ReteMatch> getConflictSet() {
        Set<ReteMatch> result;
        if (this.getProductionRule().isModifying() || this.isEmpty()) {
            result = super.getConflictSet();
        } else {
            result = new TreeHashSet<ReteMatch>();
            Set<ReteMatch> cs = this.conflictSet;
            if (!this.inhibitionMap.isEmpty() && (cs.size() > 0)) {
                for (ReteMatch m : cs) {
                    if (!this.isInhibited(m)) {
                        result.add(m);
                        break;
                    }
                }
            } else if (cs.size() > 0) {
                result.add(cs.iterator().next());
            }
        }
        return result;
    }

    @Override
    public Iterator<ReteMatch> getConflictSetIterator() {
        if (!this.getProductionRule().isModifying()) {
            return this.getConflictSet().iterator();
        } else {
            return super.getConflictSetIterator();
        }
    }

}
