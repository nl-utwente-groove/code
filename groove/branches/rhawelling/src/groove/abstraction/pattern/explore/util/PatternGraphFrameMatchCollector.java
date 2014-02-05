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
import groove.control.CtrlStep;
import groove.control.instance.Frame;
import groove.control.instance.Step;

import java.util.Collection;
import java.util.List;

/**
 * Collector of rule matches over a pattern graph.
 */
public class PatternGraphFrameMatchCollector extends PatternGraphMatchSetCollector {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The host graph we are working on. */
    private final PatternState state;
    /** The control state of the graph state, if any. */
    private final Frame frame;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs a match collector for the given state. */
    public PatternGraphFrameMatchCollector(PatternState state) {
        super(state);
        this.state = state;
        this.frame = (Frame) state.getFrame();
        assert this.frame != null;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    @Override
    public Collection<MatchResult> getMatchSet() {
        Collection<MatchResult> result = new MyHashSet<MatchResult>();
        CtrlStep step = firstCall();
        while (step != null) {
            boolean hasMatches = collectEvents(step, result);
            step = nextCall(hasMatches);
        }
        return result;
    }

    /** Matching should be injective or not. */
    @Override
    protected boolean isInjective() {
        return true;
    }

    /** Returns the first rule of the state's control schedule. */
    private Step firstCall() {
        Step result;
        Frame frame = this.frame;
        this.state.setFrame(frame);
        if (frame.isTrial()) {
            result = frame.getAttempt();
            this.matchFound = false;
        } else {
            result = null;
        }
        return result;
    }

    /** Increments the rule iterator, and returns the next rule. */
    private Step nextCall(boolean matchFound) {
        Step result;
        Frame frame = (Frame) this.state.getCurrentFrame();
        this.matchFound |= matchFound;
        if (frame.isTrial()) {
            Step step = frame.getAttempt();
            this.state.setFrame(frame = this.matchFound ? step.onSuccess() : step.onFailure());
            if (frame.isTrial()) {
                result = frame.getAttempt();
                this.matchFound = false;
            } else {
                result = null;
            }
        } else {
            result = null;
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

    private boolean matchFound;
}
