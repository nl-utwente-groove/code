package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.SystemStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

/**
 * Action for loading a new rule system.
 */
public class LoadGrammarFromURLAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public LoadGrammarFromURLAction(Simulator simulator) {
        super(simulator, Options.LOAD_URL_GRAMMAR_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.OPEN_URL_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public void execute() {
        String input = JOptionPane.showInputDialog("Input Grammar URL:");
        if (input != null) {
            try {
                URL url = new URL(input);
                final SystemStore store = SystemStore.newStore(url);
                //                String startGraphName = url.getQuery();
                getActions().getLoadGrammarAction().load(store);
            } catch (MalformedURLException e) {
                showErrorDialog(e,
                    String.format("Invalid URL '%s'", e.getMessage()));
            } catch (IOException exc) {
                showErrorDialog(exc, exc.getMessage());
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }
}