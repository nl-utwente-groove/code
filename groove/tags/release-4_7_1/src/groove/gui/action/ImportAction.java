package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.external.Importer;

import java.io.IOException;

/**
 * Action for importing elements in the grammar.
 */
public class ImportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public ImportAction(Simulator simulator) {
        super(simulator, Options.IMPORT_ACTION_NAME, Icons.IMPORT_ICON);

        //TODO: Inits are pretty much a hack
        //ConceptualPorter.initSimulator(getSimulator());
    }

    @Override
    public void execute() {
        try {
            Importer.instance().doImport(getFrame(), getGrammarModel());
            getSimulatorModel().doRefreshGrammar();
        } catch (IOException e) {
            showErrorDialog(e, "Error importing file");
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
}