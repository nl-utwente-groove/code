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
import groove.explore.result.FinalStateAcceptor;
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
        
        addObject(new Documented<Acceptor>(new AnyStateAcceptor(),
            "Any",
            "Any State",
            "TBA"));
        
        addObject(new Documented<Acceptor>(new FinalStateAcceptor(),
            "Final",
            "Final States",
            "TBA"));
        
        addObject(new AcceptorRequiringRule(null,
            "Check-Inv",
            "Check Invariant",
            "This acceptor takes as a result a (negated) " +
            "match of a selected rule.",
            true));
        
        addObject(new AcceptorRequiringRule(null,
            "Rule-App",
            "Rule Application",
            "This acceptor takes as a result an " + 
            "application of a selected rule.",
            false));
    }
    
    private class AcceptorRequiringRule extends Documented<Acceptor> {

        private boolean mayBeNegated;
        
        public AcceptorRequiringRule(Acceptor object, String keyword,
                String name, String explanation, boolean mayBeNegated) {
            super(object, keyword, name, explanation);
            this.mayBeNegated = mayBeNegated;
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
                return new RuleApplicationAcceptor(condition);
            }
        }
    }

}