package nl.utwente.groove.gui.action;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;

/**
 * Creates a dialog showing the composite type graph.
 */
public class PreviewHostAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public PreviewHostAction(Simulator simulator) {
        super(simulator, Options.PREVIEW_HOST_ACTION_NAME, Icons.GRAPH_MODE_ICON, null,
              ResourceKind.HOST);
    }

    @Override
    public void execute() {
        var graph = getGraph();
        if (graph != null) {
            GraphPreviewDialog.showGraph(graph);
        }
    }

    @Override
    public void refresh() {
        var hostGraph = getGraph();
        setEnabled(hostGraph != null);
    }

    /** Convenience method to obtain the composite type graph. */
    private AspectGraph getGraph() {
        AspectGraph result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            var hostModel = grammarModel.getStartGraphModel();
            if (hostModel.isMultiple()) {
                result = hostModel.getSource();
            }
        }
        assert result == null || result.isFixed();
        return result;
    }
}