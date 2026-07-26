package nl.utwente.groove.gui.action;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.GrooveFileChooser;
import nl.utwente.groove.gui.dialog.SaveDialog;
import nl.utwente.groove.gui.display.Display;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.display.GraphEditorTab;
import nl.utwente.groove.gui.display.GraphTab;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.gui.display.ResourceTab;
import nl.utwente.groove.gui.export.JGraphExportable;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.Exporters;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.Exceptions;

/**
 * Action to save the content of a {@link JGraph},
 * as a graph or in some export format.
 * There is a discrepancy between exporter action for JGraphs and for displays: JGraph exports have no access to the original resource (if any)
 * and so an export initiated from a JGraph directly (as opposed for example form the menu) will never show an export option that requires a resource
 * Doubles as the dialog-based driver of the {@link Exporters} registry.
 */
public class ExportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given display. */
    public ExportAction(Simulator simulator, DisplayKind displayKind) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(simulator, Options.EXPORT_ACTION_NAME, Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.displayKind = displayKind;
        this.display = simulator.getDisplaysPanel().getDisplay(displayKind);
        this.jGraph = null;
        this.isGraph = this.displayKind.isGraphBased();
    }

    /** Constructs an instance of the action. */
    public ExportAction(JGraph<?> jGraph) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(jGraph.getActions().getSimulator(), Options.EXPORT_ACTION_NAME, Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.display = null;
        this.displayKind = null;
        this.jGraph = jGraph;
        this.isGraph = true;
    }

    @Override
    public void execute() {
        Exportable exportable;
        if (this.isGraph) {
            // Export graph
            exportable = JGraphExportable.instance(getJGraph());
        } else {
            // Export resource
            exportable = Exportable.resource(getResource());
        }
        doExport(exportable);
    }

    /**
     * Exports the object contained in an exportable, using an
     * exporter chosen through a save dialog.
     * @param exportable container with object to export
     */
    private void doExport(Exportable exportable) {
        // determine the set of suitable file types and exporters
        Map<FileType,Exporter> exporters = new EnumMap<>(FileType.class);
        for (Exporter exporter : Exporters.getExporters()) {
            exporter.getFileTypes(exportable).forEach(ft -> exporters.put(ft, exporter));
        }
        assert !exporters.isEmpty();
        // choose a file and exporter
        GrooveFileChooser chooser = GrooveFileChooser.getInstance(exporters.keySet());
        chooser.setSelectedFile(exportable.qualName().toFile());
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                // Get exporter
                FileType fileType = chooser.getFileType();
                // the Ecore formats have encoding options, which have to be settled
                // (and stored) before the exporter reads them from the grammar
                if (!askEcoreOptions(fileType)) {
                    return;
                }
                exporters.get(fileType).doExport(exportable, selectedFile, fileType);
            } catch (PortException | IOException e) {
                showErrorDialog(e, "Error while exporting to " + selectedFile);
            }
        }
    }

    /** Refreshes the name of this action. */
    @Override
    public void refresh() {
        boolean setenabled = getSimulatorModel().getGrammar() != null;
        if (this.isGraph && setenabled) {
            JGraph<?> jGraph = getJGraph();
            setenabled = jGraph != null && jGraph.isEnabled();
        } else if (setenabled) {
            setenabled = getResource() != null;
        }
        setEnabled(setenabled);
        if (setenabled) {
            // there is certainly a graph, so now we can set the real action name
            putValue(NAME, getActionName());
            putValue(SHORT_DESCRIPTION, getActionName());
        } else {
            // When disabled, use generic description
            putValue(NAME, "Export...");
            putValue(SHORT_DESCRIPTION, "Export...");
        }
    }

    /** Returns the export action name for a given JGraph being saved. */
    private String getActionName() {
        String type = null;
        if (this.isGraph) {
            JGraph<?> jGraph = getJGraph();
            Graph graph = jGraph.getGraph();
            assert graph != null;
            GraphRole role = graph.getRole();
            boolean isState = jGraph instanceof AspectJGraph ag && ag.isForState();
            type = isState
                ? "State"
                : role.getDescription();
        } else {
            type = this.displayKind.getResource().getDescription();
        }
        return "Export " + type + " ...";
    }

    /** Get active resource if any */
    private final NamedResourceModel<?> getResource() {
        if (!(this.display instanceof ResourceDisplay)) {
            return null;
        }

        ResourceTab tab = ((ResourceDisplay) this.display).getSelectedTab();
        if (tab == null) {
            return null;
        }
        return getGrammarModel().getResource(this.displayKind.getResource(), tab.getQualName());
    }

    // Get active graph if any
    private final JGraph<?> getJGraph() {
        assert (this.isGraph);
        if (this.jGraph == null) {
            switch (this.displayKind) {
            case HOST:
            case RULE:
            case TYPE:
                ResourceTab selectedTab = ((ResourceDisplay) this.display).getSelectedTab();
                return selectedTab == null
                    ? null
                    : selectedTab instanceof GraphTab gt
                        ? gt.getJGraph()
                        : ((GraphEditorTab) selectedTab).getJGraph();
            case STATE:
                return getStateDisplay().getJGraph();
            case LTS:
                return getLtsDisplay().getJGraph();
            default:
                throw Exceptions.unreachable();
            }
        } else {
            return this.jGraph;
        }
    }

    /** The fixed JGraph with which this action is associated,
     * if it is not associated with a {@link Display}.
     */
    private final JGraph<?> jGraph;
    /**
     * The display with which this action is associated,
     * if it is not associated with a fixed {@link JGraph}.
     */
    private final Display display;
    /** The display kind, if the display is set. */
    private final DisplayKind displayKind;
    /** True if exporter for jgraphs, false otherwise. */
    private boolean isGraph;
}
