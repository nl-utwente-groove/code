package nl.utwente.groove.gui.action;

import nl.utwente.groove.grammar.model.CompositeTypeModel;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;

/**
 * Creates a dialog showing the composite type graph.
 */
public class PreviewTypeAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public PreviewTypeAction(Simulator simulator) {
        super(simulator, Options.PREVIEW_TYPE_ACTION_NAME, Icons.TYPE_MODE_ICON, null,
              ResourceKind.TYPE);
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
        setEnabled(getGraph() != null);
    }

    /** Convenience method to obtain the composite type graph. */
    private TypeGraph getGraph() {
        TypeGraph result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            CompositeTypeModel typeModel = grammarModel.getTypeModel();
            if (typeModel.isMultiple()) {
                result = typeModel.getTypeGraph();
            }
        }
        assert result == null || result.isFixed();
        return result;
    }
}