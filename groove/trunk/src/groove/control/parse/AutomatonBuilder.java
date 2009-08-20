/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AutomatonBuilder.java,v 1.3 2008-01-30 09:33:34 iovka Exp $
 */
package groove.control.parse;

import groove.control.ControlAutomaton;
import groove.control.ControlShape;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleName;
import groove.view.FormatException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * The AutomatonBuilder is used by the parser, checker and builder (generated
 * with antlr) to create the ControlShape's representing the control program.
 * 
 * This class can be used to create en automaton by calling the public methods.
 * 
 * @author Arend Rensink
 * @version $Revision $
 */
public class AutomatonBuilder extends Namespace {

    /** label used to identify delta in init result. */
    public static final String _DELTA_ = "/%/__DELTA__/%/";

    /** label used to identify other keyword in transition label. */
    public static final String _OTHER_ = "/%/__OTHER__/%/";

    /** label used to identify any keyword in transition label. */
    public static final String _ANY_ = "/%/__ANY__/%/";

    /** Contains transitions that may lead to merging of source and target. */
    private final HashSet<ControlTransition> mergeCandidates =
        new HashSet<ControlTransition>();

    /**
     * The automaton that is created. It is returned as an empty automaton by
     * startProgram, before it is filled.
     */
    private ControlAutomaton aut;

    /** container for all transitions, to be iterated when merging states. */
    private final HashSet<ControlTransition> transitions =
        new HashSet<ControlTransition>();

    /**
     * Holds the active start state, a local startstate for the next parsed
     * block.
     */
    private ControlState currentStart;

    /** Holds the active end state, a local endstate for the next parsed block */
    private ControlState currentEnd;

    /**
     * Returns the current start state.
     */
    public ControlState getStart() {
        return this.currentStart;
    }

    /**
     * Returns the current end state.
     */
    public ControlState getEnd() {
        return this.currentEnd;
    }

    /** Sets the current start and end states to a given pair of states. */
    public void restore(ControlState start, ControlState end) {
        this.currentStart = start;
        this.currentEnd = end;
        debug("restore: start = " + start + ", end = " + end);
    }

    /**
     * Start method for building the automaton of a program
     */
    public ControlAutomaton startProgram() {
        this.aut = new ControlAutomaton();

        this.currentStart = new ControlState(this.aut);
        this.currentEnd = new ControlState(this.aut);
        this.aut.addState(this.currentStart);
        this.aut.setStart(this.currentStart);
        this.currentEnd.setSuccess();
        this.aut.addState(this.currentEnd);

        return this.aut;
    }

    /**
     * Finished building the automaton of the program.
     */
    public void endProgram() {
        // nothing to do
    }

    /**
     * Adds a new labelled transition between the current start and end states.
     * @param label the label of the new transition
     */
    public void addTransition(String label) {
        ControlTransition ct =
            new ControlTransition(this.currentStart, this.currentEnd, label);
        // basic init stuff: if an outgoing transitions is added, the init of
        // the state gets the label added
        this.currentStart.addInit(label);

        debug("addTransition: " + ct);

        storeTransition(ct);
    }

    /**
     * Adds an "other" transition between the current start and end states.
     */
    public void addOther() {
        ControlTransition ct =
            new ControlTransition(this.currentStart, this.currentEnd, _OTHER_);

        // basic init stuff: if an outgoing transitions is added, the init of
        // the state gets the label added
        this.currentStart.addInit(_OTHER_);

        debug("addOther: " + ct);

        storeTransition(ct);
    }

    /**
     * Adds an "any" transition between the current start and end states.
     */
    public void addAny() {
        ControlTransition ct =
            new ControlTransition(this.currentStart, this.currentEnd, _ANY_);

        // basic init stuff: if an outgoing transitions is added, the init of
        // the state gets the label added
        this.currentStart.addInit(_ANY_);

        debug("addOther: " + ct);

        storeTransition(ct);
    }

    /**
     * Adds a new lambda-transition between the current start and end states.
     */
    public void addLambda() {
        ControlTransition ct =
            new ControlTransition(this.currentStart, this.currentEnd);

        debug("addLambda: " + ct);

        testMerge(ct);

        storeTransition(ct);
    }

    /**
     * Add a delta to the init of this state.
     * @param state
     */
    public void tagDelta(ControlState state) {
        state.addInit(_DELTA_);
        debug("tagDelta: " + state);
    }

    /**
     * Copies the init of the source state to the target state
     * @param source
     * @param target
     */
    public void initCopy(ControlState source, ControlState target) {
        debug("initCopy: " + source + " to " + target);
        target.addInit(source);
        debug("initCopy: updated target " + target);
    }

    /**
     * Copies the init of the source state to the target state. The delta is
     * removed from the target before the init is copied from the source.
     * @param source
     * @param target has a delta in its init
     */
    public void deltaInitCopy(ControlState source, ControlState target) {
        debug("deltaInitCopy: " + source + " to " + target);
        if (target.getInit().contains(_DELTA_)) {
            debug("deltaInitCopy: Delta found in " + target + target.getInit());
            target.delInit(_DELTA_);
            target.addInit(source);
            debug("deltaInitCopy: updated target " + target);
        } else {
            debug("deltaInitCopy: No delta found");
        }
    }

    /**
     * A failure is added to the transitions using the init from the given
     * state.
     * @param state
     * @param trans
     */
    public void fail(ControlState state, ControlTransition trans) {
        debug("fail: " + state + " to " + trans);
        trans.setFailureFromInit(state);
        debug("fail: updated trans = " + trans);
    }

    /**
     * Creates a transition without any label. The fail(state,trans) method
     * should be used to set the failure of this transition once it is know.
     * @return the created transition
     */
    public ControlTransition addElse() {
        ControlTransition ct =
            new ControlTransition(this.currentStart, this.currentEnd);
        storeTransition(ct);
        return ct;
    }

    /**
     * Creates a nwe state and adds this state to the automaton.
     * @return the fresh state.
     */
    public ControlState newState() {
        ControlState newState = new ControlState(this.aut);
        this.aut.addState(newState);
        debug("newState: created " + newState);
        return newState;
    }

    /**
     * Stores a transition in the automaton, and in the local cache of
     * transitions.
     * @param ct
     */
    private void storeTransition(ControlTransition ct) {
        this.transitions.add(ct);
        this.aut.addTransition(ct);
        ct.setParent(this.aut);
    }

    /**
     * Deletes a transition from the automaton and in any local cache it is in.
     * @param ct
     */
    private void rmTransition(ControlTransition ct) {
        debug("rmTransition: removed transition " + ct);
        this.transitions.remove(ct);
        // in case it is a merge candidate
        this.mergeCandidates.remove(ct);
        // remove the transition from the automaton
        this.aut.removeTransition(ct);
    }

    /**
     * Removes a state from the automaton.
     * @param state
     */
    public void rmState(ControlState state) {
        debug("rmState: " + state);
        this.aut.removeState(state);
    }

    /**
     * Merges currentState and currentEnd. Will result in currentStart to become
     * currentEnd.
     */
    public void merge() {

        debug("merge: Merging " + this.currentStart + " with "
            + this.currentEnd);

        // so: currentEnd becomes currentStart
        for (ControlTransition ct : this.transitions) {
            if (ct.source() == this.currentStart) {
                ct.setSource(this.currentEnd);
            }
            if (ct.target() == this.currentStart) {
                ct.setTarget(this.currentEnd);
            }
        }

        if (this.currentStart.isSuccess()) {
            this.currentEnd.setSuccess();
        }

        if (this.currentStart.getParent().getStart() == this.currentStart) {
            this.currentStart.getParent().setStart(this.currentEnd);
        }
        if (this.aut.getStart() == this.currentStart) {
            this.aut.setStart(this.currentEnd);
        }

        // copy any init values, since the states are considered only seperated
        // by a lambda,
        // the target init is reachable from the source also
        this.currentEnd.addInit(this.currentStart);

        rmState(this.currentStart);

        debug("merge: removed " + this.currentStart);
        debug("merge: updated " + this.currentEnd);

        this.currentStart = this.currentEnd;
    }

    /**
     * Removes certain lambda transitions by merging the source and target
     * states. Removes failures with delta's. Removes unreachable states.
     */
    public void optimize() {

        Set<ControlState> checkOrphan = new HashSet<ControlState>();

        Set<ControlTransition> merge = new HashSet<ControlTransition>();

        for (ControlTransition ct : this.mergeCandidates) {

            boolean sourceProblem = false;
            boolean targetProblem = false;

            // let's first see if the source has any other outgoing transitions
            // (with a different target)
            for (ControlTransition t : this.transitions) {
                if (t != ct && t.source() == ct.source()
                    && t.target() != ct.target()) {
                    sourceProblem = true;
                }
            }

            // there is only a problem if the target has any incoming
            // transitions other then ct
            for (ControlTransition t : this.transitions) {
                if (t != ct && t.target() == ct.target()) {
                    targetProblem = true;
                }
            }

            if (!targetProblem || !sourceProblem) {
                merge.add(ct);
            }

        }

        // now we know what to merge we can do it
        for (ControlTransition ct : merge) {
            checkOrphan.add(ct.target());
            restore(ct.source(), ct.target());
            rmTransition(ct);
            merge();
        }

        // Now we go over the failure transitions and see if there are delta's.
        // Transitions are removed, target may be unreachable
        Set<ControlTransition> remove = new HashSet<ControlTransition>();

        for (ControlTransition ct : this.transitions) {
            if (ct.getFailures().contains(_DELTA_)) {
                checkOrphan.add(ct.target());
                remove.add(ct);
            }
        }
        // Do actual remove
        for (ControlTransition ct : remove) {
            rmTransition(ct);
        }

        // Removing unreachable states
        for (ControlState t : checkOrphan) {
            boolean delete = true;
            for (ControlTransition ct : this.transitions) {
                if (ct.target() == t) {
                    delete = false;
                }
            }
            // delete is it is not the startstate
            if (delete && this.aut.getStart() != t) {
                rmState(t);
            }
        }
    }

    /**
     * Adds a rule instance to the RuleControlTransitions and then adds the
     * transitions to the source states of the transitions.
     * 
     * @param grammar
     */
    public void finalize(GraphGrammar grammar) throws FormatException {
        Set<Rule> otherRules = new HashSet<Rule>(grammar.getRules());
        for (ControlTransition transition : this.transitions) {
            if (transition instanceof ControlShape) {
                // don't process these
            } else if (hasSpecialLabel(transition)) {
                // don't process these just yet
            } else if (transition.hasLabel()) {
                Rule rule = grammar.getRule(transition.getLabel());
                if (rule != null) {
                    transition.setRule(rule);
                    transition.source().add(transition);
                    otherRules.remove(rule);
                } else {
                    // if the rulename is a group, this will add all child
                    // rules.
                    Set<Rule> rules =
                        getChildRules(grammar, transition.getText());
                    if (!rules.isEmpty()) {
                        ControlTransition childTrans;
                        for (Rule childRule : rules) {
                            childTrans =
                                new ControlTransition(transition.source(),
                                    transition.target(),
                                    childRule.getName().text());
                            childTrans.setRule(childRule);
                            transition.source().add(childTrans);
                            otherRules.remove(childRule);
                            // this is for viewing purposes only
                            childTrans.setVisibleParent(transition);
                        }
                        // remove the original transition;
                        // automaton.removeTransition(transition);
                    } else {
                        throw new FormatException(
                            "Control: Unknown rule reference: "
                                + transition.getText());
                    }
                }
            } else {
                // must be either lambda or else..
                transition.source().add(transition);
            }
        }

        // now process the OTHER-transitions
        Set<ControlTransition> otherTransitions =
            new HashSet<ControlTransition>();
        Iterator<ControlTransition> transIter = this.transitions.iterator();
        while (transIter.hasNext()) {
            ControlTransition trans = transIter.next();
            if (hasSpecialLabel(trans)) {
                transIter.remove();
                trans.getParent().removeTransition(trans);
                java.util.Collection<Rule> addedRules =
                    trans.getLabel().equals(_OTHER_) ? otherRules
                            : grammar.getRules();
                for (Rule rule : addedRules) {
                    // create a corresponding transition for each of the other
                    // rules
                    ControlTransition otherTrans =
                        new ControlTransition(trans.source(), trans.target(),
                            rule.getName().text());
                    otherTrans.setRule(rule);
                    otherTrans.setParent(trans.getParent());
                    trans.source().add(otherTrans);
                    otherTransitions.add(otherTrans);
                    trans.getParent().addTransition(otherTrans);
                }
            }
        }
        this.transitions.addAll(otherTransitions);
        // collect the names of the other rules
        Set<String> otherRuleNames = new HashSet<String>();
        for (Rule rule : otherRules) {
            otherRuleNames.add(rule.getName().text());
        }
        // update the failure sets of else-transitions
        // this assumes that there are no outgoing lambda's from states that
        // have else-transitions
        for (ControlTransition transition : this.transitions) {
            Set<String> failureNames = transition.getFailures();
            if (failureNames.remove(_OTHER_)) {
                failureNames.addAll(otherRuleNames);
            }
            if (failureNames.remove(_ANY_)) {
                failureNames.addAll(getRuleNames());
            }
            Set<Rule> failures = new HashSet<Rule>();
            for (String s : failureNames) {
                failures.add(grammar.getRule(s));
            }
            transition.setFailureSet(failures);
        }
        return;
    }

    /**
     * Returns all rules from a given grammar with a structured rule name that
     * have a given parent name.
     */
    private Set<Rule> getChildRules(GraphGrammar grammar, String parent) {
        Set<Rule> result = new HashSet<Rule>();
        for (Rule rule : grammar.getRules()) {
            RuleName label = rule.getName().parent();
            while (label != null) {
                if (label.text().compareTo(parent) == 0) {
                    result.add(rule);
                    break;
                } else {
                    label = label.parent();
                }
            }
        }
        return result;
    }

    /**
     * Tests if the transition label is special, such as {@link #_ANY_} or
     * {@link #_OTHER_}.
     */
    private boolean hasSpecialLabel(ControlTransition trans) {
        return trans.hasLabel()
            && (trans.getLabel().equals(_OTHER_) || trans.getLabel().equals(
                _ANY_));
    }

    /**
     * Tags this transition as a candidate for merging source and target.
     * @param ct
     */
    private void testMerge(ControlTransition ct) {
        this.mergeCandidates.add(ct);
    }

    private void debug(String msg) {
        // System.err.println("debug: " + msg);
    }
}