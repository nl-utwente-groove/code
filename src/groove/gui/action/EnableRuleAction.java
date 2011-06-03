package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.RuleModel;

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
        super(simulator, Options.DISABLE_RULE_ACTION_NAME, Icons.ENABLE_ICON);
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getSimulatorModel().getRule() != null;
        setEnabled(ruleSelected && getSimulatorModel().getStore().isModifiable());
        String description;
        if (ruleSelected && getSimulatorModel().getRule().isEnabled()) {
            description = Options.DISABLE_RULE_ACTION_NAME;
        } else {
            description = Options.ENABLE_RULE_ACTION_NAME;
        }
        putValue(NAME, description);
        putValue(SHORT_DESCRIPTION, description);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // collect the selected rule graphs
        List<String> ruleGraphs =
            new ArrayList<String>(getSimulatorModel().getRuleSet().size());
        for (RuleModel ruleView : getSimulatorModel().getRuleSet()) {
            ruleGraphs.add(ruleView.getName());
        }
        if (confirmAbandon()
            && getRuleDisplay().disposeEditors(
                ruleGraphs.toArray(new String[0]))) {
            try {
                result |= getSimulator().getModel().doEnableRules(ruleGraphs);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during rule enabling");
            }
        }
        return result;
    }
}