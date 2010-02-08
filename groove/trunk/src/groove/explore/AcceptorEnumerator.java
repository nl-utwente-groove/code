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
package groove.explore;

import groove.explore.Serialized.Materialize;
import groove.explore.result.Acceptor;
import groove.explore.result.AnyStateAcceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.Result;
import groove.explore.result.RuleApplicationAcceptor;
import groove.gui.Simulator;
import groove.trans.Rule;

/**
 * An enumeration of Serialized<Acceptor>.
 * Stores all the acceptors that can be executed within Groove.
 *
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class AcceptorEnumerator extends Enumerator<Acceptor> {
    /**
     * Extended constructor. Enumerates the available strategies one by one.
     */
    public AcceptorEnumerator(Simulator simulator) {
        super();

        addElement("Final", "Final States",
            "This acceptor succeeds when a state is added to the LTS that is "
                + "<I>final</I>. A state is final when no rule is applicable "
                + "on it (or rule application results in the same state).",
            new FinalStateAcceptor());

        addElement("Check-Inv", "Check Invariant",
            "This acceptor succeeds when a state is reached in which the "
                + "indicated rule is applicable. Note that this is detected "
                + "<I>before</I> the rule has been applied.<BR> "
                + "This acceptor ignores rule priorities.",
            new CheckInvModeArgument(), new RuleArgument(""),
            new MaterializeCheckInvariantAcceptor());

        addElement("Rule-App", "Rule Application",
            "This acceptor succeeds when a transition of the indicated rule is "
                + "added to the LTS. Note that this is detected <I>after</I> "
                + "the rule has been applied.", new RuleArgument(""),
            new MaterializeRuleApplicationAcceptor());

        addElement("Any", "Any State",
            "This acceptor succeeds whenever a state is added to the LTS.",
            new AnyStateAcceptor());
    }

    private class CheckInvModeArgument extends OptionArgument {
        static final String MODE_POSITIVE = "Positive";
        static final String MODE_POSITIVE_ID =
            "Add to result set when rule matches.";

        static final String MODE_NEGATIVE = "Negative";
        static final String MODE_NEGATIVE_ID =
            "Add to result set when rule does not match.";

        public CheckInvModeArgument() {
            super("Mode");
            addOption(MODE_POSITIVE, MODE_POSITIVE_ID);
            addOption(MODE_NEGATIVE, MODE_NEGATIVE_ID);
            setSerializedValue(MODE_POSITIVE);
        }
    }

    private class MaterializeCheckInvariantAcceptor implements
            Materialize<Acceptor> {

        @Override
        public Acceptor materialize(Object[] arguments) {
            boolean modeArg =
                ((String) arguments[0]).equals(CheckInvModeArgument.MODE_POSITIVE);
            Rule ruleArg = (Rule) arguments[1];
            IsRuleApplicableCondition condition =
                new IsRuleApplicableCondition(ruleArg, modeArg);
            return new InvariantViolatedAcceptor(condition, new Result());
        }
    }

    private class MaterializeRuleApplicationAcceptor implements
            Materialize<Acceptor> {

        @Override
        public Acceptor materialize(Object[] arguments) {
            Rule ruleArg = (Rule) arguments[0];
            IsRuleApplicableCondition condition =
                new IsRuleApplicableCondition(ruleArg, false);
            return new RuleApplicationAcceptor(condition);
        }
    }
}