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
package groove.lts;

import groove.control.instance.Frame;
import groove.control.instance.Step;
import groove.control.instance.StepAttempt;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorithm class to gradually build up the matches for a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FrameStateMatches extends StateMatches {
    /**
     * Creates an instance for a given state.
     */
    public FrameStateMatches(StateCache cache) {
        super(cache);
        this.cache = cache;
        this.state = cache.getState();
    }

    private StateCache getCache() {
        return this.cache;
    }

    private final StateCache cache;

    private GraphState getState() {
        return this.state;
    }

    private final GraphState state;

    private GraphTransition getTransition(MatchResult m) {
        return getCache().getTransitionMap().get(m);
    }

    /**
     * Returns all unexplored matches of the state, insofar they can be determined
     * without exploring any currently raw successor states. 
     * @return set of unexplored matches
     */
    @Override
    MatchResultSet getAll() {
        // try all schedules as long as this is possible
        while (trySchedule()) {
            // do nothing
        }
        return this;
    }

    /** Returns the first unexplored match of the state. */
    @Override
    MatchResult getOne() {
        MatchResult result = null;
        // compute matches insofar necessary and feasible
        while (isEmpty() && trySchedule()) {
            // do nothing
        }
        // return the first match if there is one
        if (!isEmpty()) {
            result = iterator().next();
        }
        return result;
    }

    private boolean trySchedule() {
        boolean result = false;
        Frame frame = (Frame) getState().getActualFrame();
        // depth of the frame at the start of the method
        int depth = frame.getDepth();
        if (hasOutstanding()) {
            // the schedule has been tried and has yielded matches; 
            // now see if at least one match has resulted
            // in a transition to a present state, or all matches
            // have resulted in transitions to absent states
            boolean allAbsent = true;
            boolean somePresent = false;
            Iterator<MatchResult> matchIter = this.outstanding.iterator();
            while (matchIter.hasNext()) {
                MatchResult m = matchIter.next();
                GraphTransition t = getTransition(m);
                if (t == null) {
                    allAbsent = false;
                } else {
                    GraphState target = t.target();
                    if (target.getAbsence() <= frame.getDepth()) {
                        somePresent = true;
                        break;
                    } else if (target.isAbsent()) {
                        matchIter.remove();
                    } else {
                        allAbsent = false;
                    }
                }
            }
            if (somePresent || allAbsent) {
                // yes, there is a present outgoing transition
                // or all outgoing transitions are absent
                StepAttempt step = frame.getAttempt();
                frame = somePresent ? step.onSuccess() : step.onFailure();
                getState().setFrame(frame);
                this.outstanding = EMPTY_MATCH_SET;
            }
        }
        if (!frame.isTrial()) {
            if (isEmpty()) {
                assert isFinished();
                getState().setClosed(true);
            }
        } else if (!hasOutstanding()) {
            StepAttempt step = frame.getAttempt();
            // Collect the new matches
            // Keep track of increases in transient depth
            boolean depthIncreases = false;
            List<MatchResult> outstanding = new LinkedList<MatchResult>();
            for (Step ct : step) {
                outstanding.addAll(getMatchCollector().computeMatches(ct));
                depthIncreases |= ct.onFinish().getDepth() > frame.getDepth();
            }
            Frame nextFrame;
            if (outstanding.isEmpty()) {
                // no transitions will be generated
                nextFrame = step.onFailure();
            } else if (step.sameVerdict()) {
                // it does not matter whether a transition is generated or not
                nextFrame = step.onSuccess();
            } else if (!depthIncreases) {
                // the control transition does not increase the transient depth
                // so the existence of a match guarantees the existence of a transition
                // to a state that is present on the level of the frame
                nextFrame = step.onSuccess();
            } else {
                nextFrame = frame;
                this.outstanding = outstanding;
            }
            getState().setFrame(nextFrame);
            addAll(outstanding);
            result = true;
        }
        int actualDepth = getState().getActualFrame().getDepth();
        if (actualDepth < depth) {
            getCache().notifyDepth(actualDepth);
        }
        return result;
    }

    private MatchCollector getMatchCollector() {
        if (this.matcher == null) {
            this.matcher = getCache().createMatchCollector();
        }
        return this.matcher;
    }

    /** 
     * Indicates that there are no more matches, and the schedule is finished.
     * If this is the case, the state can be closed.
     */
    @Override
    boolean isFinished() {
        return isEmpty() && !getState().getActualFrame().isTrial();
    }

    /** Strategy object used to find the matches. */
    private MatchCollector matcher;

    /** Tests if there are outstanding matches from the previous call to
     * {@link #trySchedule()}.
     */
    private boolean hasOutstanding() {
        return this.outstanding != null && !this.outstanding.isEmpty();
    }

    /** The matches found during the latest successful call to {@link #trySchedule()}.
     * If at least one of these matches gives rise to a transition to a present state,
     * the schedule has succeeded and we can move to the next. */
    private List<MatchResult> outstanding;

    /** Unique empty match set. */
    static private final List<MatchResult> EMPTY_MATCH_SET = Collections.emptyList();
}
