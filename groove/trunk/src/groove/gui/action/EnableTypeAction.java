package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.TypeModel;

import java.io.IOException;

/** 
 * Action to switch the enabledness of a type graph.
 */
public class EnableTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EnableTypeAction(Simulator simulator) {
        super(simulator, "Enable type", Icons.ENABLE_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        try {
            result =
                getSimulatorModel().doEnableType(getSimulatorModel().getType().getSource());
        } catch (IOException e) {
            showErrorDialog(e, "Error while enabling type graph");
        }
        return result;
    }

    @Override
    public void refresh() {
        TypeModel typeView = getSimulatorModel().getType();
        setEnabled(typeView != null);
        if (isEnabled()) {
            putValue(SHORT_DESCRIPTION, typeView.isEnabled()
                    ? Options.DISABLE_TYPE_ACTION_NAME
                    : Options.ENABLE_TYPE_ACTION_NAME);
        }
    }
}