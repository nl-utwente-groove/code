package nl.utwente.groove.gui.action;

import java.io.IOException;

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
        // for settings, the name must start with a schema; seed with the first one
        String seedName = resource == ResourceKind.SETTINGS
            ? SettingsSchemas.getNames().iterator().next()
            : Options.getNewResourceName(resource);
        final QualName newName = askNewName(seedName, true);
        if (newName != null) {
            try {
                if (resource.isGraphBased()) {
                    final AspectGraph newGraph = AspectGraph
                        .emptyGraph(newName.toString(), resource.getGraphRole(),
                                    !getGrammarModel().getProperties().getParallelMode().isMulti());
                    getSimulatorModel().doAddGraph(resource, newGraph, false);
                } else {
                    getSimulatorModel().doAddText(getResourceKind(), newName, initText(newName));
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

    /** Returns the initial text of a new text resource with a given name. */
    private String initText(QualName name) {
        if (getResourceKind() != ResourceKind.SETTINGS) {
            return "";
        }
        // the schema is known: askNewName validated the leading segment
        SettingsSchema schema = SettingsSchemas.get(name.get(0));
        assert schema != null;
        return schema.getNewText();
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }
}