package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.PropertiesDialog;
import groove.trans.SystemProperties;

import java.io.IOException;
import java.util.Properties;

/** Action to show the system properties. */
public class EditSystemPropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public EditSystemPropertiesAction(Simulator simulator) {
        super(simulator, Options.SYSTEM_PROPERTIES_ACTION_NAME, null);
    }

    /**
     * Displays a {@link PropertiesDialog} for the properties of the edited
     * graph.
     */
    @Override
    public boolean execute() {
        boolean result = false;
        Properties systemProperties = getModel().getGrammar().getProperties();
        PropertiesDialog dialog =
            new PropertiesDialog(systemProperties,
                SystemProperties.DEFAULT_KEYS, true);
        if (dialog.showDialog(getFrame()) && confirmAbandon()) {
            SystemProperties newProperties = new SystemProperties();
            newProperties.putAll(dialog.getEditedProperties());
            try {
                result = getSimulator().getModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving edited properties");
            }
        }
        return result;
    }

    /**
     * Tests if the currently selected grammar has non-<code>null</code>
     * system properties.
     */
    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && getModel().getStore().isModifiable());
    }
}