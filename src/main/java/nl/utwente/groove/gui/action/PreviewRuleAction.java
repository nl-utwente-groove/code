package nl.utwente.groove.gui.action;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;

/**
 * Creates a dialog showing a normalised rule.
 */
public class PreviewRuleAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public PreviewRuleAction(Simulator simulator) {
        super(simulator, Options.PREVIEW_RULE_ACTION_NAME, Icons.RULE_MODE_ICON, null,
              ResourceKind.RULE);
    }

    @Override
    public void execute() {
        AspectGraph graph = getGraph();
        if (graph != null) {
            GraphPreviewDialog.showGraph(graph);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGraph() != null);
    }

    /** Convenience method to obtain the currently selected rule. */
    private AspectGraph getGraph() {
        AspectGraph result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            RuleModel ruleModel
                = (RuleModel) getSimulatorModel().getGraphResource(getResourceKind());
            if (ruleModel != null) {
                result = ruleModel.getNormalSource();
            }
        }
        assert result == null || result.isFixed();
        return result;
    }
}