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

import groove.explore.result.Acceptor;
import groove.explore.result.AnyStateAcceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.RuleApplicationAcceptor;
import groove.gui.Simulator;
import groove.gui.dialog.RuleSelectionDialog;
import groove.trans.Rule;

import java.awt.Component;

/**
 * An enumeration of Documented<Acceptor>.
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
    public AcceptorEnumerator() {
        super();
                
        addObject(new Documented<Acceptor>(new FinalStateAcceptor(),
            "Final",
            "Final States",
            "This acceptor succeeds when a state is added to the LTS that is <I>final</I>. " +
            "A state is final when no rule is applicable on it (or rule application results " +
            "in the same state)."));
        
        addObject(new AcceptorRequiringRule(null,
            "Check-Inv",
            "Check Invariant",
            "This acceptor succeeds when a state is reached in which the indicated rule is applicable. " +
            "Note that this is detected <I>before</I> the rule has been applied.<BR> " +
            "This acceptor can also be negated (succeeds when the rule is <I>not</I> applicable).<BR> " +
            "This acceptor ignores rule priorities.", 
            true,
            new InvariantViolatedAcceptor()));
        
        addObject(new AcceptorRequiringRule(null,
            "Rule-App",
            "Rule Application",
            "This acceptor succeeds when a transition of the indicated rule is added to the LTS. " + 
            "Note that this is detected <I>after</I> the rule has been applied.",
            false,
            new RuleApplicationAcceptor()));

        addObject(new Documented<Acceptor>(new AnyStateAcceptor(),
            "Any",
            "Any State",
            "This acceptor succeeds whenever a state is added to the LTS."));
    }

    /**
     * Class that inherits from Documented<Acceptor>, but by default requires
     * a rule to be selected. The additional selection either takes place by
     * opening a RuleSelectionDialog, or by parsing a command line argument.
     * (the latter is not implemented yet). 
     */
    private class AcceptorRequiringRule extends Documented<Acceptor> {

        private boolean mayBeNegated;
        ConditionalAcceptor<Rule> acceptor;
        
        public AcceptorRequiringRule(Acceptor object, String keyword,
                String name, String explanation, boolean mayBeNegated,
                ConditionalAcceptor<Rule> acceptor) {
            super(object, keyword, name, explanation);
            this.mayBeNegated = mayBeNegated;
            this.acceptor = acceptor;
        }
        
        @Override
        public Acceptor queryUser(Simulator simulator, Component owner) {
            RuleSelectionDialog dialog =
                new RuleSelectionDialog(simulator, owner, this.mayBeNegated);
            if (dialog.wasCanceled()) {
                return null;
            } else {
                Rule rule = dialog.getSelectedRule();
                boolean isNegated = dialog.isNegated();
                dialog.dispose();
                IsRuleApplicableCondition condition =
                                new IsRuleApplicableCondition(rule, isNegated);
                this.acceptor.setCondition(condition);
                return this.acceptor;
            }
        }
    }
}