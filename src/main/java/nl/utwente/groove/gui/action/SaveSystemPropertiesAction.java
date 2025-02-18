package nl.utwente.groove.gui.action;

import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.util.Groove;

/**
 * Action for loading a new rule system.
 */
public class SaveSystemPropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveSystemPropertiesAction(Simulator simulator) {
        super(simulator, Options.SAVE_SYSTEM_PROPERTIES_ACTION_NAME, Icons.SAVE_AS_ICON, null,
              ResourceKind.PROPERTIES);
    }

    @Override
    public void execute() {
        File selectedFile = askSaveResource(QualName
            .name(PROPERTIES.getFileType().addExtension(Groove.PROPERTY_NAME)));
        if (selectedFile == null) {
            showErrorDialog(null, "No file selected");
        } else {
            try {
                saveAs(selectedFile);
            } catch (IOException exc) {
                showErrorDialog(exc, exc.getMessage());
            }
        }
    }

    /**
     * Loads in new system properties from a given file.
     * @throws IOException if the load action failed
     */
    public void saveAs(File propertiesFile) throws IOException {
        try (Writer propertiesWriter = new FileWriter(propertiesFile)) {
            getGrammarModel().getProperties().store(propertiesWriter);
        }
    }
}