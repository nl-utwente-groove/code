package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.GrooveFileChooser;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.Importer;
import nl.utwente.groove.io.external.Importers;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Action for importing elements in the grammar.
 * Doubles as the dialog-based driver of the {@link Importers} registry.
 */
public class ImportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public ImportAction(Simulator simulator) {
        super(simulator, Options.IMPORT_ACTION_NAME, Icons.IMPORT_ICON);
    }

    @Override
    public void execute() {
        try {
            doImport();
            getSimulatorModel().doRefreshGrammar();
        } catch (IOException e) {
            showErrorDialog(e, "Error importing file");
        }
    }

    /**
     * Performs the import. Shows the open dialog, and based on the selected
     * format imports the selected files.
     */
    private void doImport() throws IOException {
        int approve = getFormatChooser().showDialog(getFrame(), "Import");
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            try {
                doChosenImport(getGrammarModel());
            } catch (PortException | FormatException e) {
                throw new IOException(e);
            }
        }
    }

    private void doChosenImport(GrammarModel grammar) throws PortException, FormatException,
                                                      IOException {
        FileType fileType = getFormatChooser().getFileType();
        Importer ri = Importers.getImporter(fileType);
        var store = getGrammarStore();
        Set<Imported> resources = new HashSet<>();
        for (var file : getFormatChooser().getSelectedFiles()) {
            resources.addAll(ri.doImport(file, fileType, grammar));
        }
        if (!resources.isEmpty()) {
            Map<ResourceKind,Collection<AspectGraph>> newGraphs = new EnumMap<>(ResourceKind.class);
            Map<ResourceKind,Map<QualName,String>> newTexts = new EnumMap<>(ResourceKind.class);
            for (Imported resource : resources) {
                QualName name = resource.qualName();
                name.getErrors().throwException();
                ResourceKind kind = resource.kind();
                if (grammar.getResource(kind, name) == null
                    || confirmOverwrite(kind, name.toString())) {
                    if (resource.isGraph()) {
                        AspectGraph graph = resource.graph();
                        Collection<AspectGraph> graphs = newGraphs.get(kind);
                        if (graphs == null) {
                            newGraphs.put(kind, graphs = new ArrayList<>());
                        }
                        graphs.add(graph);
                    } else {
                        String text = resource.text();
                        Map<QualName,String> texts = newTexts.get(kind);
                        if (texts == null) {
                            newTexts.put(kind, texts = new HashMap<>());
                        }
                        texts.put(name, text);
                        store.putTexts(kind, Collections.singletonMap(name, text));
                    }
                }
            }
            for (Map.Entry<ResourceKind,Collection<AspectGraph>> entry : newGraphs.entrySet()) {
                store.putGraphs(entry.getKey(), entry.getValue(), true);
            }
            for (Map.Entry<ResourceKind,Map<QualName,String>> entry : newTexts.entrySet()) {
                store.putTexts(entry.getKey(), entry.getValue());
            }
        }
    }

    /** Returns the file chooser for all importers. */
    private static GrooveFileChooser getFormatChooser() {
        if (formatChooser == null) {
            formatChooser = GrooveFileChooser.getInstance(Importers.getFileTypes(), true);
        }
        return formatChooser;
    }

    /** File chooser with native and external import filters. */
    private static GrooveFileChooser formatChooser;

    /**
     * Sets the enabling status of this action, depending on whether a
     * grammar is currently loaded.
     */
    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}
