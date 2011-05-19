package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action for editing the current state or rule.
 */
public class EditRuleAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EditRuleAction(Simulator simulator) {
        super(simulator, Options.EDIT_RULE_ACTION_NAME, Icons.EDIT_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    /**
     * Checks if the enabling condition is satisfied, and if so, calls
     * {@link #setEnabled(boolean)}.
     */
    @Override
    public void refresh() {
        boolean enabled =
            getModel().getRule() != null
                && getModel().getStore().isModifiable()
                && getModel().getRuleSet().size() == 1;
        if (enabled != isEnabled()) {
            setEnabled(enabled);
        }
    }

    /**
     * Invokes the editor on the current rule. Handles the execution of an
     * <code>EditHostOrStateAction</code>, if the current panel is the rule panel.
     * 
     * @require <tt>getCurrentRule != null</tt>.
     */
    @Override
    public boolean execute() {
        getPanel().doEditGraph(getModel().getRule().getAspectGraph());
        return false;
    }
}