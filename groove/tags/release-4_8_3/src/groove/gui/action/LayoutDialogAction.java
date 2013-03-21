package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.LayoutDialog;

/** Action to open the Layout Dialog. */
public class LayoutDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public LayoutDialogAction(Simulator simulator) {
        super(simulator, Options.LAYOUT_DIALOG_ACTION_NAME, Icons.LAYOUT_ICON);
    }

    @Override
    public void execute() {
        LayoutDialog.getInstance(this.getSimulator()).showDialog();
    }

}