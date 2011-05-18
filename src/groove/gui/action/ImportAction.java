package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.external.Importer;
import groove.util.Duo;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Action for importing elements in the grammar.
 */
public class ImportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public ImportAction(Simulator simulator) {
        super(simulator, Options.IMPORT_ACTION_NAME, null);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        Importer importer = Importer.getInstance();
        int approve = importer.showDialog(getFrame(), true);
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
            try {
                AspectGraph importGraph;
                if ((importGraph = importer.importRule()) != null) {
                    result = importRule(importGraph);
                } else if ((importGraph = importer.importState(true)) != null) {
                    result = importState(importGraph);
                } else if ((importGraph = importer.importType()) != null) {
                    result = importType(importGraph);
                } else {
                    Duo<String> control;
                    if ((control = importer.importControl()) != null) {
                        result = importControl(control.one(), control.two());
                    }
                }
            } catch (IOException e) {
                showErrorDialog(e, "Error importing file");
            }
        }
        return result;
    }

    /**
     * Sets the enabling status of this action, depending on whether a
     * grammar is currently loaded.
     */
    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }

    private boolean importRule(AspectGraph rule) throws IOException {
        boolean result = false;
        String ruleName = rule.getName();
        if (getModel().getGrammar().getRuleView(ruleName) == null
            || confirmOverwriteRule(ruleName)) {
            result = getSimulator().getModel().doAddRule(rule);
        }
        return result;
    }

    private boolean importState(AspectGraph state) throws IOException {
        boolean result = false;
        String stateName = state.getName();
        if (getModel().getGrammar().getGraphView(stateName) == null
            || confirmOverwriteGraph(stateName)) {
            result = getModel().doAddHost(state);
        }
        return result;
    }

    private boolean importType(AspectGraph type) throws IOException {
        boolean result = false;
        String typeName = type.getName();
        if (getModel().getGrammar().getTypeView(typeName) == null
            || confirmOverwriteType(typeName)) {
            result = getSimulator().getModel().doAddType(type);
        }
        return result;
    }

    private boolean importControl(String name, String program)
        throws IOException {
        boolean result = false;
        if (getModel().getGrammar().getControlView(name) == null
            || confirmOverwriteControl(name)) {
            result = getSimulator().getModel().doAddControl(name, program);
        }
        return result;
    }
}