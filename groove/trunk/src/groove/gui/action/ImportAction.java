package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.external.Importer;
import groove.trans.ResourceKind;
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
        super(simulator, Options.IMPORT_ACTION_NAME, Icons.IMPORT_ICON);
    }

    @Override
    public void execute() {
        Importer importer = Importer.getInstance();
        int approve = importer.showDialog(getFrame(), true);
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            try {
                AspectGraph graph = null;
                Duo<String> text = null;
                ResourceKind kind = null;
                if ((graph = importer.importRule()) != null) {
                    kind = ResourceKind.RULE;
                } else if ((graph = importer.importState(true)) != null) {
                    kind = ResourceKind.HOST;
                } else if ((graph = importer.importType()) != null) {
                    kind = ResourceKind.TYPE;
                } else if ((text = importer.importControl()) != null) {
                    kind = ResourceKind.CONTROL;
                } else if ((text = importer.importProlog()) != null) {
                    kind = ResourceKind.PROLOG;
                }
                if (kind != null) {
                    if (kind.isGraphBased()) {
                        importGraph(kind, graph);
                    } else {
                        importText(kind, text);
                    }
                }
            } catch (IOException e) {
                showErrorDialog(e, "Error importing file");
            }
        }
    }

    /**
     * Sets the enabling status of this action, depending on whether a
     * grammar is currently loaded.
     */
    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }

    private boolean importGraph(ResourceKind kind, AspectGraph graph)
        throws IOException {
        boolean result = false;
        String name = graph.getName();
        if (getGrammarModel().getResource(kind, name) == null
            || confirmOverwrite(kind, name)) {
            result = getSimulatorModel().doAddGraph(kind, graph, false);
        }
        return result;
    }

    private boolean importText(ResourceKind kind, Duo<String> text)
        throws IOException {
        boolean result = false;
        String name = text.one();
        String program = text.two();
        if (getGrammarModel().getResource(kind, name) == null
            || confirmOverwrite(kind, name)) {
            result = getSimulatorModel().doAddText(kind, name, program);
        }
        return result;
    }
}