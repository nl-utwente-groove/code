package groove.gui.action;

import groove.graph.GraphRole;
import groove.gui.Options;
import groove.gui.Refreshable;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.SaveDialog;
import groove.gui.jgraph.GraphJGraph;
import groove.io.external.Exporter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;

/**
 * Action to save the content of a {@link GraphJGraph},
 * as a graph or in some export format.
 * @see Exporter#export(GraphJGraph, File)
 */
public class ExportAction extends AbstractAction implements Refreshable {
    /** Constructs an instance of the action. */
    public ExportAction(GraphJGraph jGraph) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(Options.EXPORT_ACTION_NAME);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.jGraph = jGraph;
    }

    public void actionPerformed(ActionEvent e) {
        String fileName = this.jGraph.getModel().getName();
        if (fileName != null) {
            this.exporter.getFileChooser().setSelectedFile(new File(fileName));
        }
        File selectedFile =
            SaveDialog.show(this.exporter.getFileChooser(), this.jGraph, null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                this.exporter.export(this.jGraph, selectedFile);
            } catch (IOException exc) {
                new ErrorDialog(this.jGraph, "Error while exporting to "
                    + selectedFile, exc).setVisible(true);
            }

        }
    }

    /** Refreshes the name of this action. */
    public void refresh() {
        boolean enabled = this.jGraph.isEnabled();
        setEnabled(enabled);
        if (enabled) {
            // there is certainly a graph, so now we can set the real action name
            GraphRole role = this.jGraph.getModel().getGraph().getRole();
            putValue(NAME, getActionName(role));
            putValue(SHORT_DESCRIPTION, getActionName(role));
        }
    }

    private final GraphJGraph jGraph;
    private final Exporter exporter = Exporter.getInstance();

    /** Returns the export action name for a given JGraph being saved. */
    static private String getActionName(GraphRole role) {
        return "Export " + role.getDescription() + " ...";
    }
}