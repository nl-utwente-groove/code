package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.trans.SystemProperties;

import java.io.IOException;

/** Action to disable the currently displayed control program. */
public class DisableControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public DisableControlAction(ControlPanel panel) {
        super(panel, Options.DISABLE_CONTROL_ACTION_NAME,
            Icons.DISABLE_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getPanel().stopEditing(true)) {
            SystemProperties oldProperties =
                getModel().getGrammar().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(false);
            try {
                result = getModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog("Error while disabling control", exc);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && getModel().getGrammar().isUseControl());
    }
}