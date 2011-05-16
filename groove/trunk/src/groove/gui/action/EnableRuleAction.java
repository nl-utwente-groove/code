package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.RuleView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action that changes the enabledness status of the currently selected
 * rule.
 */
public class EnableRuleAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EnableRuleAction(Simulator simulator) {
        super(simulator, Options.DISABLE_RULE_ACTION_NAME, null);
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getModel().getRule() != null;
        setEnabled(ruleSelected && getModel().getStore().isModifiable());
        if (ruleSelected && getModel().getRule().isEnabled()) {
            putValue(NAME, Options.DISABLE_RULE_ACTION_NAME);
        } else {
            putValue(NAME, Options.ENABLE_RULE_ACTION_NAME);
        }
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        // collect the selected rule graphs
        AspectGraph[] ruleGraphs =
            new AspectGraph[getModel().getRuleSet().size()];
        int i = 0;
        for (RuleView ruleView : getModel().getRuleSet()) {
            ruleGraphs[i] = ruleView.getAspectGraph();
            i++;
        }
        if (confirmAbandon() && getSimulator().disposeEditors(ruleGraphs)) {
            for (AspectGraph ruleGraph : ruleGraphs) {
                try {
                    result |= getSimulator().getModel().doEnableRule(ruleGraph);
                } catch (IOException exc) {
                    showErrorDialog(String.format(
                        "Error while enabling rule '%s'", ruleGraph.getName()),
                        exc);
                }
            }
        }
        return result;
    }
}