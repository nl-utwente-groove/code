package groove.gui.action;

import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.FreshNameDialog;
import groove.trans.ResourceKind;
import groove.view.SearchResult;
import groove.view.aspect.AspectGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Action to search for a label in the graphs.
 */
public class SearchAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SearchAction(Simulator simulator) {
        super(simulator, Options.SEARCH_NAME, null);
        putValue(ACCELERATOR_KEY, Options.SEARCH_KEY);
    }

    @Override
    public void execute() {
        String text = askLabel();
        TypeLabel label = TypeLabel.createLabel(text);
        List<SearchResult> searchResults = new ArrayList<SearchResult>();
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (!kind.isGraphBased()) {
                continue;
            }
            for (AspectGraph graph : this.getSimulator().getModel().getStore().getGraphs(
                kind).values()) {
                graph.getSearchResults(label, searchResults);
            }
        }
        getSimulator().setSearchResults(searchResults);
    }

    final private String askLabel() {
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(Collections.<String>emptySet(), "",
                false) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), "Label to search: ");
        return nameDialog.getName();
    }

}