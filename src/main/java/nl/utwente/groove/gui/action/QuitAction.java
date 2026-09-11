package nl.utwente.groove.gui.action;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.UserSettings;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Log;

/**
 * Action for quitting the simulator.
 */
@NonNullByDefault
public class QuitAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public QuitAction(Simulator simulator) {
        super(simulator, Options.QUIT_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
    }

    @Override
    public void execute() {
        tryQuit();
    }

    /**
     * Tries to quit the simulator, giving the user the chance to save
     * unsaved changes or to cancel.
     * @return {@code true} if the simulator was closed, {@code false}
     * if the user cancelled the quit
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public boolean tryQuit() {
        boolean quit = getDisplaysPanel().saveAllEditors(true);
        if (quit) {
            // Saving the user settings is a convenience, whereas disposing of
            // the frame below is the only way the simulator ever closes: the
            // frame itself ignores the window closing event. A failure here is
            // therefore reported, but must not abort the shutdown.
            try {
                UserSettings.syncSettings(getSimulator());
            } catch (Throwable exc) {
                LOGGER.log(Level.WARNING, "Could not persist the user settings", exc);
            }
            getDisplaysPanel().dispose();
            getFrame().dispose();
            // try to persist the user preferences
            try {
                Preferences.userRoot().flush();
            } catch (BackingStoreException e) {
                // do nothing if the backing store is inaccessible
            }

        }
        return quit;
    }

    /** Logger for failures encountered while shutting down. */
    static private final Logger LOGGER = Log.getLogger("gui");
}