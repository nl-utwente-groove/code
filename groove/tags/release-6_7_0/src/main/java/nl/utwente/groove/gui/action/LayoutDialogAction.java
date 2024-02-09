package nl.utwente.groove.gui.action;

import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.LayoutDialog;

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