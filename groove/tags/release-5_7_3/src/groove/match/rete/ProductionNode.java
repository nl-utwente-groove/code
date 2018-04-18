/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.grammar.Rule;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.util.Reporter;
import groove.util.collect.TreeHashSet;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ProductionNode extends ConditionChecker {

    /**
     * Report collector for production nodes
     */
    protected static final Reporter reporter =
        Reporter.register(ProductionNode.class);

    /**
     * For collecting reports on the number of time the 
     * {@link #demandOneMatch()} method is called for production nodes only.
     */
    protected static final Reporter demandOneMatchReporter =
        reporter.register("demandOneMatch");

    /**
     * @param network The RETE network to which this node belongs
     * @param p The production rule associated with this checker. 
     */
    public ProductionNode(ReteNetwork network, Rule p,
            ReteStaticMapping antecedents) {
        super(network, p.getCondition(), null, antecedents);
    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
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
        return this.getCondition().getRule();
    }

    @Override
    public Set<ReteSimpleMatch> getConflictSet() {
        Set<ReteSimpleMatch> result;

        if (this.getProductionRule().isModifying() || this.isEmpty()) {
            result = super.getConflictSet();
        } else {
            result = new TreeHashSet<>();
            Set<ReteSimpleMatch> cs = this.conflictSet;
            if (this.hasNacs()) {
                this.demandUpdateOnlyIfNecessary();
            }
            if (!this.inhibitionMap.isEmpty() && (cs.size() > 0)) {
                for (ReteSimpleMatch m : cs) {
                    if (!this.isInhibited(m)) {
                        result.add(m);
                        break;
                    }
                }
            } else if (cs.size() > 0) {
                result.add(cs.iterator().next());
            } else {
                demandOneMatchReporter.start();
                if (this.demandOneMatch() > 0) {
                    result.add(cs.iterator().next());
                }
                demandOneMatchReporter.stop();
            }
        }
        return result;
    }

    private void demandUpdateOnlyIfNecessary() {
        if (this.conflictSet.size() == this.inhibitionMap.elementSet().size()) {
            demandUpdate();
        } else if (!allNacsUpToDate()) {
            demandUpdate();
        }
    }

    /**
     * Determines if all the NACs are up to date w.r.t. 
     * the matches currently present in the conflict set. That is,
     * no match in the conflict set is wrongfully considered uninhibitted.
     */
    protected boolean allNacsUpToDate() {
        boolean result = this.hasNacs();
        if (result) {
            for (ConditionChecker cc : this.getSubConditionCheckers()) {
                if (cc instanceof CompositeConditionChecker) {
                    result =
                        result
                            && ((CompositeConditionChecker) cc).isNegativePartUpToDate();
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<ReteSimpleMatch> getConflictSetIterator() {
        if (!this.getProductionRule().isModifying()) {
            return this.getConflictSet().iterator();
        } else {
            return super.getConflictSetIterator();
        }
    }

}
