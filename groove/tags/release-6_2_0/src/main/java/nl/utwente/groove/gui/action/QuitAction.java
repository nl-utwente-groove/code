package nl.utwente.groove.gui.action;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;

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
            nl.utwente.groove.gui.UserSettings.syncSettings(getSimulator());
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