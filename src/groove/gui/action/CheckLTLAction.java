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
package groove.gui.action;

import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.StrategyValue;
import groove.explore.encode.Serialized;
import groove.explore.strategy.Boundary;
import groove.gui.Simulator;
import groove.gui.dialog.BoundedModelCheckingDialog;
import groove.gui.dialog.StringDialog;
import groove.lts.GraphState;
import groove.verify.FormulaParser;
import groove.verify.ParseException;
import groove.view.FormatException;

import java.util.Collection;

import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class CheckLTLAction extends ExploreAction {
    /**
     * Constructs a checking action for a given simulator and strategy.
     */
    public CheckLTLAction(Simulator simulator, StrategyValue strategyType,
            String name) {
        super(simulator);
        assert StrategyValue.LTL_STRATEGIES.contains(strategyType);
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, strategyType.getDescription());
        putValue(Action.ACCELERATOR_KEY, null);
        putValue(Action.SMALL_ICON, null);
        this.strategyType = strategyType;
    }

    @Override
    public boolean execute() {
        Serialized strategy;
        // prompt for a formula to model check
        String property = getLtlFormulaDialog().showDialog(getFrame());
        if (property == null) {
            return false;
        }
        // prompt for a boundary, if the LTL strategy is bounded
        if (this.strategyType == StrategyValue.LTL) {
            strategy = this.strategyType.getTemplate().toSerialized(property);
        } else {
            BoundedModelCheckingDialog dialog =
                new BoundedModelCheckingDialog();
            dialog.setGrammar(getSimulatorModel().getGts().getGrammar());
            dialog.showDialog(getFrame());
            Boundary boundary = dialog.getBoundary();
            if (boundary == null) {
                return false;
            }
            strategy =
                this.strategyType.getTemplate().toSerialized(boundary, property);
        }
        Exploration exploration =
            new Exploration(strategy, AcceptorValue.CYCLE.toSerialized(), 1);
        getActions().getExploreAction().explore(exploration, false, true);
        Collection<GraphState> result = exploration.getLastResult().getValue();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(
                getFrame(),
                String.format("The property %s holds for this system", property));
        } else {
            JOptionPane.showMessageDialog(getFrame(), String.format(
                "A counter-example to %s is highlighted", property));
        }
        return false;
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    private StringDialog getLtlFormulaDialog() {
        if (this.ltlFormulaDialog == null) {
            this.ltlFormulaDialog =
                new StringDialog("Enter the LTL Formula",
                    FormulaParser.getDocMap(false)) {
                    @Override
                    public String parse(String text) throws FormatException {
                        try {
                            FormulaParser.parse(text).toLtlFormula();
                        } catch (ParseException e) {
                            throw new FormatException(e.getMessage());
                        }
                        return text;
                    }
                };
        }
        return this.ltlFormulaDialog;
    }

    /**
     * Dialog for entering temporal formulae.
     */
    private StringDialog ltlFormulaDialog;

    /** The strategy for the exploration. */
    private final StrategyValue strategyType;
}
