package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.SystemProperties;
import groove.view.CtrlView;

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
        if (getControlPanel().stopEditing(true)) {
            String controlName = getModel().getControl().getName();
            SystemProperties oldProperties =
                getModel().getGrammar().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(!isControlEnabled());
            newProperties.setControlName(controlName);
            try {
                result = getModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while enabling control program "
                    + controlName);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        CtrlView control = getModel().getControl();
        setEnabled(control != null);
        if (isEnabled()) {
            putValue(SHORT_DESCRIPTION, isControlEnabled()
                    ? Options.DISABLE_CONTROL_ACTION_NAME
                    : Options.ENABLE_CONTROL_ACTION_NAME);
        }
    }

    private boolean isControlEnabled() {
        CtrlView control = getModel().getControl();
        return control != null
            && control.equals(getModel().getGrammar().getControlView());
    }
}