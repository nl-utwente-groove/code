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
import groove.abstraction.pattern.lts.MatchResult;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.trans.PatternRule;
import groove.control.CtrlFrame;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlStep;

import java.util.Collection;
import java.util.List;

/**
 * Collector of rule matches over a pattern graph.
 */
public class PatternGraphMatchSetCollector {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The host graph we are working on. */
    private final PatternState state;
    /** The control state of the graph state, if any. */
    private final CtrlFrame ctrlState;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs a match collector for the given state. */
    public PatternGraphMatchSetCollector(PatternState state) {
        this.state = state;
        this.ctrlState = state.getFrame();
        assert this.ctrlState != null;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<MatchResult> getMatchSet() {
        Collection<MatchResult> result = new MyHashSet<MatchResult>();
        CtrlStep ctrlTrans = firstCall();
        while (ctrlTrans != null) {
            boolean hasMatches = collectEvents(ctrlTrans, result);
            ctrlTrans = nextCall(hasMatches);
        }
        return result;
    }

    /** Matching should be injective or not. */
    protected boolean isInjective() {
        return true;
    }

    /** Returns the first rule of the state's control schedule. */
    private CtrlStep firstCall() {
        CtrlStep result;
        CtrlSchedule schedule = ((CtrlState) this.ctrlState).getSchedule();
        this.state.setFrame(schedule);
        if (schedule.isDead()) {
            result = null;
        } else {
            result = schedule.getTransitions().get(0);
            this.transIx = 1;
            this.matchFound = false;
        }
        return result;
    }

    /** Increments the rule iterator, and returns the next rule. */
    private CtrlStep nextCall(boolean matchFound) {
        CtrlStep result;
        CtrlSchedule schedule = (CtrlSchedule) this.state.getCurrentFrame();
        this.matchFound |= matchFound;
        if (schedule.isDead()) {
            result = null;
        } else if (this.transIx < schedule.getTransitions().size()) {
            result = schedule.getTransitions().get(this.transIx);
            this.transIx++;
        } else {
            this.state.setFrame(schedule = schedule.next(this.matchFound));
            if (schedule.isDead()) {
                result = null;
            } else {
                result = schedule.getTransitions().get(0);
                this.transIx = 1;
                this.matchFound = false;
            }
        }
        return result;
    }

    /** Adds the matching events for a given rule into an existing set. */
    private boolean collectEvents(CtrlStep ctrlTrans, final Collection<MatchResult> result) {
        String ruleName = ctrlTrans.getRule().getLastName();
        PatternRule pRule = this.state.getPGTS().getGrammar().getRule(ruleName);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, isInjective());
        List<MatchResult> matches = matcher.findMatches(this.state.getGraph(), ctrlTrans);
        result.addAll(matches);
        return !matches.isEmpty();
    }

    private int transIx;
    private boolean matchFound;
}
