package nl.utwente.groove.gui.action;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.store.EditType;

/** Action to create and start editing a new control program. */
public class NewAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.CREATE, resource);
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();
        // the schema of a settings resource is implied by its location, so the
        // schema has to be asked for before the name: it fixes the folder
        SettingsSchema schema = null;
        final QualName newName;
        if (resource == ResourceKind.SETTINGS) {
            schema = askSchema();
            if (schema == null) {
                return;
            }
            if (schema.isSingular()) {
                // a singular schema has just one resource, in the singleton
                // form: the bare schema name, so there is nothing to ask
                QualName singleton = QualName.name(schema.getName());
                if (getGrammarModel().getNames(resource).contains(singleton)) {
                    showErrorDialog(null, "Settings resource '%s' already exists;"
                        + " schema '%s' admits only one resource", singleton, schema.getName());
                    return;
                }
                newName = singleton;
            } else {
                // the schema name is the folder, which askNewName keeps fixed
                newName = askNewName(QualName
                    .name(schema.getName(), DEFAULT_LOCAL_NAME)
                    .toString(), true);
            }
        } else {
            newName = askNewName(Options.getNewResourceName(resource), true);
        }
        if (newName != null) {
            try {
                if (resource.isGraphBased()) {
                    final AspectGraph newGraph = AspectGraph
                        .emptyGraph(newName.toString(), resource.getGraphRole(),
                                    !getGrammarModel().getProperties().getParallelMode().isMulti());
                    getSimulatorModel().doAddGraph(resource, newGraph, false);
                } else {
                    getSimulatorModel().doAddText(getResourceKind(), newName, initText(schema));
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplay().startEditResource(newName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e, "Error creating new %s '%s'", resource.getDescription(),
                                newName);
            }
        }
    }

    /**
     * Asks the user for the schema of a new settings resource.
     * @return the chosen schema, or {@code null} if the dialog was cancelled
     */
    private SettingsSchema askSchema() {
        String[] names = SettingsSchemas.getNames().toArray(new String[0]);
        Object choice = JOptionPane
            .showInputDialog(getFrame(), "Schema of the new settings resource", "Select schema",
                             JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        return choice == null
            ? null
            : SettingsSchemas.get((String) choice);
    }

    /**
     * Returns the initial text of a new text resource.
     * @param schema the schema of the new resource, for a settings resource;
     * {@code null} for any other text resource
     */
    private String initText(SettingsSchema schema) {
        return schema == null
            ? ""
            : schema.getNewText();
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }

    /** Local name proposed for a new settings resource inside its schema folder. */
    private static final String DEFAULT_LOCAL_NAME = "default";
}