package groove.gui.action;

import groove.graph.TypeLabel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.list.SearchResult;

import java.util.List;

/**
 * Action to search for a label in the graphs.
 */
public class SearchAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SearchAction(Simulator simulator) {
        super(simulator, Options.SEARCH_ACTION_NAME, Icons.SEARCH_ICON);
        putValue(ACCELERATOR_KEY, Options.SEARCH_KEY);
    }

    @Override
    public void execute() {
        TypeLabel label = askSearch();
        if (label != null) {
            List<SearchResult> searchResults =
                getSimulatorModel().searchLabel(label);
            getSimulator().setSearchResults(searchResults);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }

}