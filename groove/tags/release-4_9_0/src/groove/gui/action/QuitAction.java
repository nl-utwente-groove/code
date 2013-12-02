package groove.gui.action;

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
        boolean quit = getDisplaysPanel().saveAllEditors(true);
        if (quit) {
            // Saves the current user settings.
            groove.gui.UserSettings.syncSettings(getSimulator());
            getDisplaysPanel().dispose();
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