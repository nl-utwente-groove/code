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

import groove.rel.RegExpr;
import groove.util.TreeHashSet;

/**
 * Represents sequencing path operator that combines two
 * smaller sub-paths into a bigger one by joining them  
 * @author Arash Jalali
 * @version $Revision $
 */
public class SequenceOperatorPathChecker extends AbstractPathChecker {

    private TreeHashSet<RetePathMatch> leftMemory = null;
    private TreeHashSet<RetePathMatch> rightMemory = null;

    /**
     * Creates a path checker node that performs sequencing of matches
     * if possible. 
     *  
     */
    public SequenceOperatorPathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network, expression, isLoop);
        this.leftMemory = new TreeHashSet<RetePathMatch>();
        this.rightMemory = new TreeHashSet<RetePathMatch>();
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            RetePathMatch newMatch) {
        TreeHashSet<RetePathMatch> memory;
        TreeHashSet<RetePathMatch> otherMemory;

        if (this.getAntecedents().get(0) != this.getAntecedents().get(1)) {
            memory =
                (this.getAntecedents().get(0) == source) ? this.leftMemory
                        : this.rightMemory;

        } else {
            memory = (repeatIndex == 0) ? this.leftMemory : this.rightMemory;
        }
        otherMemory =
            (memory == this.leftMemory) ? this.rightMemory : this.leftMemory;

        memory.add(newMatch);
        newMatch.addContainerCollection(memory);
        for (RetePathMatch gOther : otherMemory) {
            RetePathMatch left =
                (memory == this.leftMemory) ? newMatch : gOther;
            RetePathMatch right = (left == newMatch) ? gOther : newMatch;

            if (this.test(left, right)) {
                RetePathMatch combined = this.construct(left, right);
                if ((combined != null)
                    && (!this.loop || combined.isEmpty() || combined.start() == combined.end())) {
                    passDownMatchToSuccessors(combined);
                }
            }
        }
    }

    /**
     * 
     * @return <code>true</code> if the two given patch matches
     * can be combined through the regular expression operator
     * of this node's associated expression. This method is only 
     * called when this operator is binary.
     *  
     */
    protected boolean test(RetePathMatch left, RetePathMatch right) {
        return left.isEmpty() || right.isEmpty()
            || left.end().equals(right.start());
    }

    /**
     * @return combines the left and right matches according the 
     * rules of the associated operator.
     */
    protected RetePathMatch construct(RetePathMatch left, RetePathMatch right) {
        if (!left.isEmpty() && !right.isEmpty()) {
            return (RetePathMatch) left.merge(this, right, false);
        } else if (!left.isEmpty()) {
            return left.reoriginate(this);
        } else {
            return right.reoriginate(this);
        }
    }

    @Override
    public int demandOneMatch() {
        //TODO ARASH: implement the demand-based update
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        //TODO ARASH: implement the demand-based update
        return false;
    }

}