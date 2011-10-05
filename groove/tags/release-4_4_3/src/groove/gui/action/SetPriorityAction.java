package groove.gui.action;

import static groove.trans.ResourceKind.RULE;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.NumberDialog;
import groove.view.RuleModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Action that raises the priority of a selected set of rules.
 */
public class SetPriorityAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator.
     */
    public SetPriorityAction(Simulator simulator) {
        super(simulator, Options.SET_PRIORITY_ACTION_NAME, null);
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getSimulatorModel().isSelected(RULE);
        setEnabled(ruleSelected
            && getSimulatorModel().getStore().isModifiable());
    }

    @Override
    public void execute() {
        RuleModel ruleModel =
            (RuleModel) getSimulatorModel().getGraphResource(RULE);
        NumberDialog dialog = new NumberDialog("New priority: ");
        if (dialog.showDialog(getFrame(), Options.SET_PRIORITY_ACTION_NAME,
            ruleModel.getPriority())) {
            Map<String,Integer> priorityMap = new HashMap<String,Integer>();
            for (String name : getSimulatorModel().getSelectSet(RULE)) {
                priorityMap.put(name, dialog.getResult());
            }
            if (!priorityMap.isEmpty()) {
                try {
                    getSimulatorModel().doSetPriority(priorityMap);
                } catch (IOException exc) {
                    showErrorDialog(exc, "Error during rule priority change");
                }
            }

        }
    }
}
