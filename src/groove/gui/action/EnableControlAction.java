package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.SystemProperties;
import groove.view.ControlModel;

import java.io.IOException;

/** Action to enable the currently displayed control program. */
public class EnableControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EnableControlAction(Simulator simulator) {
        super(simulator, Options.ENABLE_CONTROL_ACTION_NAME, Icons.ENABLE_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (getControlDisplay().cancelEditing(true)) {
            String controlName = getSimulatorModel().getControl().getName();
            SystemProperties oldProperties =
                getSimulatorModel().getGrammar().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(!isControlEnabled());
            newProperties.setControlName(controlName);
            try {
                result = getSimulatorModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while enabling control program "
                    + controlName);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        ControlModel control = getSimulatorModel().getControl();
        setEnabled(control != null);
        if (isEnabled()) {
            putValue(SHORT_DESCRIPTION, isControlEnabled()
                    ? Options.DISABLE_CONTROL_ACTION_NAME
                    : Options.ENABLE_CONTROL_ACTION_NAME);
        }
    }

    private boolean isControlEnabled() {
        ControlModel control = getSimulatorModel().getControl();
        return control != null
            && control.equals(getSimulatorModel().getGrammar().getControlModel());
    }
}