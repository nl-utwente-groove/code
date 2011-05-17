package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.trans.SystemProperties;
import groove.view.CtrlView;
import groove.view.StoredGrammarView;

import java.io.IOException;

/** Action to enable the currently displayed control program. */
public class EnableControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public EnableControlAction(ControlPanel panel) {
        super(panel, Options.ENABLE_CONTROL_ACTION_NAME, Icons.ENABLE_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getPanel().stopEditing(true)) {
            String controlName = getModel().getControl().getName();
            SystemProperties oldProperties =
                getModel().getGrammar().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(true);
            newProperties.setControlName(controlName);
            try {
                result = getModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog("Error while enabling control program "
                    + controlName, exc);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        CtrlView control = getModel().getControl();
        setEnabled(control != null
            && (!grammar.isUseControl() || !grammar.getControlView().equals(
                control)));
    }
}