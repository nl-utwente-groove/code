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

import groove.automaton.RegExpr;
import groove.grammar.host.HostNode;
import groove.util.collect.MapSet;

import java.util.List;
import java.util.Set;

/**
 * Represents sequencing path operator that combines two
 * smaller sub-paths into a bigger one by joining them  
 * @author Arash Jalali
 * @version $Revision $
 */
public class SequenceOperatorPathChecker extends AbstractPathChecker implements
        ReteStateSubscriber {
    /** Mapping from target nodes to matches of the left hand operand. */
    private final MapSet<HostNode,RetePathMatch> leftMemory =
        new MapSet<HostNode,RetePathMatch>() {
            @Override
            protected HostNode getKey(Object value) {
                return ((RetePathMatch) value).end();
            }
        };
    private RetePathMatch leftEmpty;
    private final MapSet<HostNode,RetePathMatch> rightMemory =
        new MapSet<HostNode,RetePathMatch>() {
            @Override
            protected HostNode getKey(Object value) {
                return ((RetePathMatch) value).start();
            }
        };
    private RetePathMatch rightEmpty;

    /**
     * Creates a path checker node that performs sequencing of matches
     * if possible. 
     *  
     */
    public SequenceOperatorPathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network, expression, isLoop);
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            RetePathMatch newMatch) {
        // determine if the new match is from the left or right ancestor
        boolean fromLeft;
        if (this.getAntecedents().get(0) != this.getAntecedents().get(1)) {
            fromLeft = (this.getAntecedents().get(0) == source);
        } else {
            fromLeft = (repeatIndex == 0);
        }
        if (fromLeft) {
            if (newMatch.isEmpty()) {
                assert this.leftEmpty == null;
                this.leftEmpty = newMatch;
                constructAndPassDown(true, newMatch, this.rightMemory);
            } else {
                newMatch.addContainerCollection(this.leftMemory);
                this.leftMemory.add(newMatch);
                constructAndPassDown(true, newMatch,
                    this.rightMemory.get(newMatch.end()));
            }
        } else {
            if (newMatch.isEmpty()) {
                assert this.rightEmpty == null;
                this.rightEmpty = newMatch;
                constructAndPassDown(false, newMatch, this.leftMemory);
            } else {
                newMatch.addContainerCollection(this.rightMemory);
                this.rightMemory.add(newMatch);
                constructAndPassDown(false, newMatch,
                    this.leftMemory.get(newMatch.start()));
            }
        }
    }

    /** 
     * Combines a new match with each of a set of old matches, and the empty match if present.
     * Sends the combined match to all successor n-nodes
     * @param fromLeft if {@code true}, the new match is from the left antecedent
     * @param newMatch the new match
     * @param oldMatches the set of existing matches; may be {@code null}
     */
    private void constructAndPassDown(boolean fromLeft, RetePathMatch newMatch,
            Set<RetePathMatch> oldMatches) {
        if (oldMatches != null) {
            for (RetePathMatch oldMatch : oldMatches) {
                constructAndPassDown(fromLeft, newMatch, oldMatch);
            }
        }
        RetePathMatch empty = fromLeft ? this.rightEmpty : this.leftEmpty;
        if (empty != null) {
            constructAndPassDown(fromLeft, newMatch, empty);
        }
    }

    /** 
     * Combines a new match with a given old match.
     * Sends the combined match to all successor n-nodes
     * @param fromLeft if {@code true}, the new match is from the left antecedent
     * @param newMatch the new match
     * @param oldMatch the existing match
     */
    private void constructAndPassDown(boolean fromLeft, RetePathMatch newMatch,
            RetePathMatch oldMatch) {
        RetePathMatch left = fromLeft ? newMatch : oldMatch;
        RetePathMatch right = fromLeft ? oldMatch : newMatch;
        RetePathMatch combined = construct(left, right);
        if (combined != null) {
            passDownMatchToSuccessors(combined);
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
     * Combines the left and right matches according the 
     * rules of the associated operator.
     * @return a combined match, or {@code null} if the left and right operands
     * do not combine to a valid match  
     */
    protected RetePathMatch construct(RetePathMatch left, RetePathMatch right) {
        if (left.isEmpty()) {
            if (isLoop() && !right.isEmpty() && right.start() != right.end()) {
                return null;
            } else {
                return right.reoriginate(this);
            }
        } else if (right.isEmpty()) {
            if (isLoop() && left.start() != left.end()) {
                return null;
            } else {
                return left.reoriginate(this);
            }
        } else {
            if (isLoop() && left.start() != right.end()) {
                return null;
            } else {
                return left.concatenate(this, right, false);
            }
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

    @Override
    public void clear() {
        super.clear();
        this.leftEmpty = null;
        this.rightEmpty = null;
        this.leftMemory.clear();
        this.rightMemory.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        super.initialize();
        return null;
    }

    @Override
    public void updateBegin() {
        // Do nothing        
    }

    @Override
    public void updateEnd() {
        // Do nothing        
    }

}