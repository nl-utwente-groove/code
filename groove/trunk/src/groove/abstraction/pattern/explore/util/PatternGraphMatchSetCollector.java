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
package groove.abstraction.pattern.explore.util;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.trans.PatternRule;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.explore.util.MatchSetCollector;

import java.util.Collection;
import java.util.List;

/**
 * See {@link MatchSetCollector}. 
 */
public class PatternGraphMatchSetCollector {

    /** The host graph we are working on. */
    private final PatternState state;
    /** The control state of the graph state, if any. */
    private final CtrlState ctrlState;

    /**
     * Constructs a match collector for a given (start) state.
     */
    public PatternGraphMatchSetCollector(PatternState state) {
        this.state = state;
        this.ctrlState = state.getCtrlState();
        assert this.ctrlState != null;
    }

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<Match> getMatchSet() {
        Collection<Match> result = new MyHashSet<Match>();
        CtrlTransition ctrlTrans = firstCall();
        while (ctrlTrans != null) {
            boolean hasMatches = collectEvents(ctrlTrans, result);
            ctrlTrans = nextCall(hasMatches);
        }
        return result;
    }

    /**
     * Returns the first rule of the state's control schedule.
     */
    private CtrlTransition firstCall() {
        CtrlTransition result;
        CtrlSchedule schedule = this.ctrlState.getSchedule();
        this.state.setSchedule(schedule);
        if (schedule.isFinished()) {
            result = null;
        } else {
            result = schedule.getTransition();
        }
        return result;
    }

    /**
     * Increments the rule iterator, and returns the next rule.
     */
    private CtrlTransition nextCall(boolean matchFound) {
        CtrlTransition result;
        CtrlSchedule schedule = this.state.getSchedule();
        if (schedule.isFinished()) {
            result = null;
        } else {
            this.state.setSchedule(schedule = schedule.next(matchFound));
            if (schedule.isFinished()) {
                result = null;
            } else {
                result = schedule.getTransition();
            }
        }
        return result;
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     */
    private boolean collectEvents(CtrlTransition ctrlTrans,
            final Collection<Match> result) {
        String ruleName = ctrlTrans.getRule().getLastName();
        PatternRule pRule = this.state.getPGTS().getGrammar().getRule(ruleName);
        Matcher matcher =
            MatcherFactory.instance().getMatcher(pRule, isInjective());
        List<Match> matches = matcher.findMatches(this.state.getGraph());
        result.addAll(matches);
        return !matches.isEmpty();
    }

    /** Matching should be injective or not. */
    protected boolean isInjective() {
        return true;
    }
}
