package groove.gui.action;

import groove.grammar.model.ResourceKind;
import groove.graph.GraphRole;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.FileType;
import groove.io.conceptual.Design;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.ConfigDialog;
import groove.io.conceptual.configuration.schema.Constraints;
import groove.io.conceptual.lang.groove.ConstraintToGroove;
import groove.io.conceptual.lang.groove.GrooveExport;
import groove.io.external.ConceptualPorter;
import groove.io.external.Importers;
import groove.io.external.PortException;
import groove.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import javax.swing.JOptionPane;

/**
 * Action for importing elements in the grammar.
 */
public class ImportRulesAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public ImportRulesAction(Simulator simulator) {
        super(simulator, Options.IMPORT_RULES_ACTION_NAME, null);
    }

    @Override
    public void execute() {
        Optional<Config> cfg = ConfigDialog.load(getSimulator(), Constraints.class);
        Optional<Path> location = cfg.flatMap(c -> c.getFormatLocation());
        if (location.isPresent()) {
            try {
                FileType formatType = extractFileType(location.get());
                ConceptualPorter porter = (ConceptualPorter) Importers.getImporter(formatType);
                Pair<Glossary,Design> result =
                    porter.importGlossary(location.get(), getGrammarModel());
                GrooveExport export = new GrooveExport(cfg.get(), getSimulatorModel(), "");
                new ConstraintToGroove(result.one(), export).build();
                Set<String> ruleNames = getGrammarModel().getNames(ResourceKind.RULE);
                int overlap =
                    (int) export.getGraphs()
                        .get(GraphRole.RULE)
                        .keySet()
                        .stream()
                        .filter(n -> ruleNames.contains(n))
                        .count();
                if (overlap == 0 || confirmReplace(overlap)) {
                    export.export();
                    getSimulatorModel().doRefreshGrammar();
                }
            } catch (PortException | IOException e) {
                showErrorDialog(e, "Error importing rules");
            }
        }
    }

    /**
     * Asks whether a given existing resource, of a given kind,
     * should be replaced by a newly loaded one.
     */
    private boolean confirmReplace(int count) {
        int response =
            JOptionPane.showConfirmDialog(getSimulator().getFrame(),
                String.format("Replace %d existing rules?", count),
                null,
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Extracts a format file type from a given location.
     * @throws IOException if the given location does not specify a valid external format
     */
    private FileType extractFileType(Path location) throws IOException {
        Set<FileType> fileTypes = FileType.getType(location);
        FileType result = null;
        if (fileTypes.contains(FileType.GXL)) {
            result = FileType.GXL;
        } else if (fileTypes.contains(FileType.ECORE_META)) {
            result = FileType.ECORE_META;
        }
        if (result == null) {
            throw new IOException("Location " + location
                + " does not contain a valid external format");
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().hasGrammar());
    }
}