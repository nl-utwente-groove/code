package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.jgraph.LTSJModel;

/**
 * Action for filtering the LTS JGraph. 
 */
public class FilterLTSAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public FilterLTSAction(Simulator simulator, boolean animated) {
        super(simulator, Options.FILTER_LTS_NAME, Icons.FILTER_LTS_ICON);
    }

    @Override
    public void execute() {
        if (getLtsDisplay().getLtsModel() != null) {
            getLtsDisplay().getLtsJGraph().filterLTSFromResultStates();
        }
    }

    @Override
    public void refresh() {
        LTSJModel ltsModel = getLtsDisplay().getLtsModel();
        boolean enabled =
            ltsModel != null
                && !ltsModel.getGraph().getResultStates().isEmpty();
        setEnabled(enabled);
    }

}