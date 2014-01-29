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

import groove.control.CtrlSchedule;
import groove.control.CtrlTransition;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorithm class to gradually build up the matches for a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateMatches extends MatchResultSet {
    /**
     * Creates an instance for a given state.
     */
    public StateMatches(StateCache cache) {
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
    MatchResultSet getAll() {
        // try all schedules as long as this is possible
        while (trySchedule()) {
            // do nothing
        }
        return this;
    }

    /** Returns the first unexplored match of the state. */
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
        CtrlSchedule schedule = (CtrlSchedule) getState().getActualFrame();
        boolean isTransient = schedule.isTransient();
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
                    if (target.isPresent()) {
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
                schedule = schedule.next(somePresent);
                getState().setFrame(schedule);
                this.outstanding = EMPTY_MATCH_SET;
            }
        }
        if (schedule.isDead()) {
            if (isEmpty()) {
                assert isFinished();
                getState().setClosed(true);
            }
        } else if (!hasOutstanding()) {
            // flag collecting if none of the transitions in this schedule
            // have a transient target
            boolean noTransientTargets = true;
            List<MatchResult> latestMatches = new LinkedList<MatchResult>();
            for (CtrlTransition ct : schedule.getTransitions()) {
                latestMatches.addAll(getMatchCollector().computeMatches(ct));
                noTransientTargets &= !ct.target().isTransient();
            }
            CtrlSchedule nextSchedule;
            if (latestMatches.isEmpty()) {
                // no transitions will be generated
                nextSchedule = schedule.next(false);
            } else if (schedule.next(true) == schedule.next(false)) {
                // it does not matter whether a transition is generated or not
                nextSchedule = schedule.next(false);
            } else if (schedule.isTransient() || noTransientTargets) {
                // the control transition is atomic
                // so the existence of a match guarantees the existence of a transition
                nextSchedule = schedule.next(true);
            } else {
                nextSchedule = schedule;
                this.outstanding = latestMatches;
            }
            getState().setFrame(nextSchedule);
            addAll(latestMatches);
            result = true;
        }
        if (isTransient && !getState().isTransient()) {
            getCache().setPresent();
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
    boolean isFinished() {
        return isEmpty() && getState().getActualFrame().isDead();
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
