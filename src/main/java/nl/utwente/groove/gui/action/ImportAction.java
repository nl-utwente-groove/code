package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.EcoreOptionsDialog;
import nl.utwente.groove.gui.dialog.GrooveFileChooser;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.Importer;
import nl.utwente.groove.io.external.Importers;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping;
import nl.utwente.groove.util.FileType;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Action for importing elements in the grammar.
 * Doubles as the dialog-based driver of the {@link Importers} registry.
 * <p>
 * Importers are side-effect free, so this action is the only writer: it stores
 * every resource an importer returns, asking before it overwrites an existing
 * one. A resource flagged as an {@link Imported#update()} is stored without
 * asking — it is an update of an existing resource that the importer computed
 * from that resource's own content, as the Ecore porter does for the
 * {@code ecore} settings resource it records the round-trip metadata in.
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
            // the Ecore formats have encoding options, which have to be settled
            // (and stored) before the importer reads them from the grammar
            if (!askEcoreOptions(getFormatChooser().getFileType())) {
                return;
            }
            try {
                doChosenImport(getGrammarModel());
            } catch (PortException | FormatException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Asks the user for the Ecore encoding options on the first Ecore import
     * into a grammar, if a given file type calls for them, and stores the
     * chosen options in a fresh Ecore mapping settings resource under the
     * default name {@link EcoreMapping#RESOURCE_NAME}.
     * Once the grammar has such a resource the dialog is skipped, whatever
     * the state of the resource (gh #558): the settings display edits the
     * options directly, and a broken or ambiguous resource is reported by the
     * port itself, which the dialog could not repair anyway.
     * The resource is created through the (undoable) store, so that the
     * subsequent import sees the chosen values; this is why the dialog is
     * shown before the import rather than as part of it. The import writes to
     * the same resource afterwards, recording what the type graph does not
     * determine about the imported metamodel — but it does so by returning
     * the updated text as an {@link Imported}, since importers are
     * side-effect free. Exports never ask: they use the resource if there is
     * one and the default options otherwise.
     * @param fileType the file type chosen for the import
     * @return {@code false} if the user cancelled the dialog, in which case the
     * import should not go ahead
     * @throws IOException if storing the new settings failed
     */
    private boolean askEcoreOptions(FileType fileType) throws IOException {
        if (fileType != FileType.ECORE && fileType != FileType.XMI) {
            return true;
        }
        if (!EcoreMapping.candidates(getGrammarModel()).isEmpty()) {
            return true;
        }
        EcoreMapping defaults = EcoreMapping.getDefault();
        EcoreOptionsDialog dialog
            = new EcoreOptionsDialog(defaults.ordering(), defaults.useIdentifiers());
        if (!dialog.showDialog(getFrame(), null)) {
            return false;
        }
        // always create the resource, also for the default values, so that the
        // global option lines are there to be edited and the dialog stays away
        String text = EcoreMapping.setGlobals(null, dialog.getOrdering(), dialog.isUseIdentifiers());
        getSimulatorModel().doAddText(ResourceKind.SETTINGS, EcoreMapping.RESOURCE_QUAL_NAME, text);
        return true;
    }

    private void doChosenImport(GrammarModel grammar) throws PortException, FormatException,
                                                      IOException {
        FileType fileType = getFormatChooser().getFileType();
        Importer ri = Importers.getImporter(fileType);
        // the chooser only offers file types for which there is an importer
        assert ri != null;
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
                // an update was computed from the existing resource, so there
                // is nothing an overwrite question could save
                if (resource.update() || grammar.getResource(kind, name) == null
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
