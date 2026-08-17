package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nl.utwente.groove.explore.config.ExploreConfigSchema;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.UserSettings;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.util.QualName;

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
                             JOptionPane.QUESTION_MESSAGE, null, names, SCHEMA_PREF.get());
        if (choice == null) {
            return null;
        }
        SCHEMA_PREF.set((String) choice);
        return SettingsSchemas.get((String) choice);
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

    /**
     * Preference holding the settings schema chosen last, so that the schema
     * prompt opens on it again in a later run.
     * There is one {@link NewAction} per resource kind, so this is static:
     * the preference is a property of the user, not of an action instance,
     * and the registration with {@link UserSettings} must happen only once.
     */
    private static final SchemaPref SCHEMA_PREF = new SchemaPref();

    static {
        UserSettings.register(SCHEMA_PREF);
    }

    /** Persistable last-chosen settings schema. */
    private static class SchemaPref implements UserSettings.Persistable {
        /**
         * Returns the schema name to propose: the one chosen last, or the
         * first registered schema if that one is not (or no longer) known.
         */
        String get() {
            var names = SettingsSchemas.getNames();
            String result = this.lastName;
            if (result == null) {
                result = getPref(SCHEMA_ENTRY);
            }
            return names.contains(result)
                ? result
                : names.iterator().next();
        }

        /** Records a chosen schema name. */
        void set(String name) {
            this.lastName = name;
            putPref(SCHEMA_ENTRY, name);
        }

        @Override
        public void sync() {
            if (this.lastName != null) {
                putPref(SCHEMA_ENTRY, this.lastName);
            }
        }

        @Override
        public Preferences getPrefs() {
            return userPrefs;
        }

        /** The schema chosen in this run, if any. */
        private String lastName;

        /** User preferences object for this class. */
        static private final Preferences userPrefs
            = Preferences.userNodeForPackage(NewAction.class);
        /** User preferences entry for the last chosen settings schema. */
        static private final Entry SCHEMA_ENTRY
            = new Entry("last-settings-schema", ExploreConfigSchema.INSTANCE.getName());
    }
}
