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
import groove.explore.result.RuleApplicationAcceptor;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;
import groove.trans.Rule;
import groove.trans.RuleName;

import java.awt.FlowLayout;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

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

        addObject(new Documented<Acceptor>(new FinalStateAcceptor(), "Final",
            "Final States",
            "This acceptor succeeds when a state is added to the LTS that is "
                + "<I>final</I>. A state is final when no rule is applicable "
                + "on it (or rule application results in the same state)."));

        addObject(new AcceptorRequiringRule("Check-Inv", "Check Invariant",
            "This acceptor succeeds when a state is reached in which the "
                + "indicated rule is applicable. Note that this is detected "
                + "<I>before</I> the rule has been applied.<BR> "
                + "This acceptor ignores rule priorities.", true,
            new InvariantViolatedAcceptor()));

        addObject(new AcceptorRequiringRule("Rule-App", "Rule Application",
            "This acceptor succeeds when a transition of the indicated rule is "
                + "added to the LTS. Note that this is detected <I>after</I> "
                + "the rule has been applied.", false,
            new RuleApplicationAcceptor()));

        addObject(new Documented<Acceptor>(new AnyStateAcceptor(), "Any",
            "Any State",
            "This acceptor succeeds whenever a state is added to the LTS."));
    }

    /**
     * Class that inherits from Documented<Acceptor>, but by default requires
     * a rule to be selected. The additional selection takes place on the
     * created argument panel, or by parsing a command line argument.
     * (the latter is not implemented yet). 
     */
    private class AcceptorRequiringRule extends Documented<Acceptor> {

        private boolean mayBeNegated;
        private JPanel argumentPanel;

        public AcceptorRequiringRule(String keyword, String name,
                String explanation, boolean mayBeNegated,
                ConditionalAcceptor<Rule> acceptor) {
            super(null, keyword, name, explanation);
            this.mayBeNegated = mayBeNegated;
            this.object = acceptor;
            this.argumentPanel = null;
        }

        @Override
        public JPanel getArgumentPanel(Simulator simulator) {
            if (this.argumentPanel != null) {
                return this.argumentPanel;
            }

            JComboBox ruleSelector = new JComboBox();
            Set<RuleName> ruleSet = simulator.getGrammarView().getRuleNames();
            for (RuleName name : ruleSet) {
                if (simulator.getGrammarView().getRuleView(name).isEnabled()) {
                    ruleSelector.addItem("<HTML><B><FONT color="
                        + ExplorationDialog.INFO_COLOR + ">" + name
                        + "</FONT></B></HTML>");
                }
            }

            ruleSelector.addItem("<HTML><B>Bogus #1</B></HTML>");
            ruleSelector.addItem("Bogus #2");
            ruleSelector.setBackground(ExplorationDialog.INFO_BG_COLOR);

            JPanel argumentLine =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            argumentLine.setBackground(ExplorationDialog.INFO_BG_COLOR);
            argumentLine.add(new JLabel("<HTML><FONT color="
                + ExplorationDialog.INFO_COLOR
                + ">Rule argument: </FONT></HTML>"));
            argumentLine.add(ruleSelector);

            this.argumentPanel = new JPanel(new SpringLayout());
            this.argumentPanel.setBackground(ExplorationDialog.INFO_BG_COLOR);
            this.argumentPanel.add(argumentLine);
            SpringUtilities.makeCompactGrid(this.argumentPanel, 1, 1, 0, 0, 0,
                0);
            return this.argumentPanel;
        }

        /*
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
        */
    }
}