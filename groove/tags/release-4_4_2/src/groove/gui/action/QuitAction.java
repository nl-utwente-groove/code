package groove.gui.action;

import static groove.gui.Options.STOP_SIMULATION_OPTION;
import groove.gui.Options;
import groove.gui.Simulator;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Action for quitting the simulator.
 */
public class QuitAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public QuitAction(Simulator simulator) {
        super(simulator, Options.QUIT_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
    }

    @Override
    public void execute() {
        boolean quit = getDisplaysPanel().disposeAllEditors();
        if (quit) {
            groove.gui.UserSettings.synchSettings(getFrame());
            // Saves the current user settings.
            if (getSimulatorModel().getGts() != null) {
                quit = confirmBehaviourOption(STOP_SIMULATION_OPTION);
            } else {
                quit = true;
            }
            if (quit) {
                getFrame().dispose();
                // try to persist the user preferences
                try {
                    Preferences.userRoot().flush();
                } catch (BackingStoreException e) {
                    // do nothing if the backing store is inaccessible
                }
            }
        }
    }
}