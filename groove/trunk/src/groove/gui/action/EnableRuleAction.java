package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.RuleView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public boolean execute() {
        boolean result = false;
        // collect the selected rule graphs
        List<AspectGraph> ruleGraphs =
            new ArrayList<AspectGraph>(getModel().getRuleSet().size());
        for (RuleView ruleView : getModel().getRuleSet()) {
            ruleGraphs.add(ruleView.getAspectGraph());
        }
        if (confirmAbandon()
            && getPanel().disposeEditors(ruleGraphs.toArray(new AspectGraph[0]))) {
            try {
                result |= getSimulator().getModel().doEnableRules(ruleGraphs);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during rule enabling");
            }
        }
        return result;
    }
}