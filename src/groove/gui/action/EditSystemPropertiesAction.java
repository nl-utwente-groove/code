package groove.gui.action;

import java.io.IOException;
import java.util.Map;

import groove.grammar.GrammarProperties;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.PropertiesDialog;
import groove.util.Properties.CheckerMap;

/** Action to show the system properties. */
public class EditSystemPropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public EditSystemPropertiesAction(Simulator simulator) {
        super(simulator, Options.SYSTEM_PROPERTIES_ACTION_NAME, Icons.EDIT_ICON);
    }

    /**
     * Displays a {@link PropertiesDialog} for the properties of the edited
     * graph.
     */
    @Override
    public void execute() {
        GrammarProperties grammarProperties = getGrammarModel().getProperties();
        CheckerMap checkerMap = grammarProperties.getCheckers(getGrammarModel());
        PropertiesDialog dialog = new PropertiesDialog(grammarProperties, checkerMap);
        if (dialog.showDialog(getFrame())) {
            GrammarProperties newProperties = new GrammarProperties();
            // don't use putAll, as that bypasses the filtering of default entries
            for (Map.Entry<String,String> e : dialog.getProperties()
                .entrySet()) {
                newProperties.setProperty(e.getKey(), e.getValue());
            }
            try {
                getSimulatorModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving edited properties");
            }
        }
    }

    /**
     * Tests if the currently selected grammar has non-<code>null</code>
     * system properties.
     */
    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }
}