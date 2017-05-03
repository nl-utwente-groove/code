package groove.gui.action;

import static groove.grammar.model.ResourceKind.RULE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import groove.grammar.QualName;
import groove.grammar.model.RuleModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.NumberDialog;

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
        setEnabled(ruleSelected);
    }

    @Override
    public void execute() {
        RuleModel ruleModel = (RuleModel) getSimulatorModel().getGraphResource(RULE);
        NumberDialog dialog = new NumberDialog("New priority: ");
        if (dialog.showDialog(getFrame(),
            Options.SET_PRIORITY_ACTION_NAME,
            ruleModel.getPriority())) {
            Map<QualName,Integer> priorityMap = new HashMap<>();
            for (QualName name : getSimulatorModel().getSelectSet(RULE)) {
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
