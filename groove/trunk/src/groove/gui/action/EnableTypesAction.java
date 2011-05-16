package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Action to disable or enable typing. */
public class EnableTypesAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EnableTypesAction(Simulator simulator, boolean enable) {
        super(simulator, (enable ? "Check" : "Uncheck") + " all type graphs",
            enable ? Icons.ENABLE_ICON : Icons.DISABLE_ICON);
        this.enable = enable;
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        try {
            result = getModel().doSetActiveTypes(getChangedTypes());
        } catch (IOException exc) {
            showErrorDialog("Error while resetting type graphs", exc);
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && !getModel().getGrammar().getActiveTypeNames().equals(
                getChangedTypes()));
    }

    /** Returns the intended list of types when this action is invoked. */
    private List<String> getChangedTypes() {
        return this.enable ? new ArrayList<String>(
            getModel().getGrammar().getTypeNames())
                : Collections.<String>emptyList();
    }

    private final boolean enable;
}