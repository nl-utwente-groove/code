package groove.gui.action;

import groove.gui.GroovyDisplay;
import groove.gui.Icons;
import groove.gui.Simulator;

/** Action to execute the currently selected Groovy script. */
public class ExecGroovyAction extends SimulatorAction {
    private static final long serialVersionUID = 1652912426926686879L;

    /** Constructs a new action, for a given control panel. */
    public ExecGroovyAction(Simulator simulator) {
        super(simulator, "Execute Groovy script", Icons.GO_START_ICON, null,
            groove.trans.ResourceKind.GROOVY);
    }

    @Override
    public void execute() {
        for (String name : getSimulatorModel().getSelectSet(getResourceKind())) {
            ((GroovyDisplay) getDisplay()).executeGroovy(name);
        }
    }

    @Override
    public void refresh() {
        boolean enabled = getSimulatorModel().isSelected(getResourceKind());
        setEnabled(enabled);
    }
}
