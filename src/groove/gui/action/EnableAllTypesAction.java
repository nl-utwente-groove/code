package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Action to disable or enable typing. */
public class EnableAllTypesAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EnableAllTypesAction(Simulator simulator, boolean enable) {
        super(simulator, (enable ? "Check" : "Uncheck") + " all type graphs",
            enable ? Icons.ENABLE_ICON : Icons.DISABLE_ICON);
        this.enable = enable;
    }

    @Override
    public boolean execute() {
        boolean result = false;
        try {
            result = getModel().doSetActiveTypes(getChangedTypes());
        } catch (IOException exc) {
            showErrorDialog(exc, "Error while resetting type graphs");
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

    /** Flag indicating if all types should be enabled or disabled. */
    private final boolean enable;
}