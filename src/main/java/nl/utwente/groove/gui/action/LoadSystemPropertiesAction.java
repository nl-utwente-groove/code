package nl.utwente.groove.gui.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Action for loading a new rule system.
 */
public class LoadSystemPropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public LoadSystemPropertiesAction(Simulator simulator) {
        super(simulator, Options.LOAD_SYSTEM_PROPERTIES_ACTION_NAME, Icons.OPEN_ICON, null,
              ResourceKind.PROPERTIES);
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = getSystemPropertiesFileChooser();
        int approve = fileChooser.showOpenDialog(getFrame());
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                showErrorDialog(null, "No file selected");
            } else {
                try {
                    load(selectedFile);
                } catch (IOException | FormatException exc) {
                    showErrorDialog(exc, exc.getMessage());
                }
            }
        }
    }

    /**
     * Loads in new system properties from a given file.
     * @throws IOException if the load action failed
     */
    public void load(File propertiesFile) throws IOException, FormatException {
        var grammarPath = getGrammarModel().getProperties().getLocation();
        var properties = new GrammarProperties();
        try (InputStream s = new FileInputStream(propertiesFile)) {
            properties.load(s);
        }
        getGrammarModel().setProperties(properties);
        properties = properties.repairVersion().addDerivedProperties(grammarPath);
    }
}