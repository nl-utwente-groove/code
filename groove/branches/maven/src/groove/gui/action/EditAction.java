package groove.gui.action;

import static groove.grammar.model.ResourceKind.HOST;

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

/** Action to start editing the currently displayed resource. */
public class EditAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.MODIFY, resource);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        this.editStateAction = simulator.getActions()
            .getEditStateAction();
    }

    @Override
    public void execute() {
        if (isForState()) {
            this.editStateAction.execute();
        } else {
            for (QualName name : getSimulatorModel().getSelectSet(getResourceKind())) {
                getDisplay().startEditResource(name);
            }
        }
    }

    @Override
    public void refresh() {
        boolean enabled = getGrammarModel() != null
            && (getSimulatorModel().isSelected(getResourceKind()) || isForState());
        setEnabled(enabled);
        if (getResourceKind() == HOST) {
            String name =
                isForState() ? (String) this.editStateAction.getValue(NAME) : getEditActionName();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }
    }

    private boolean isForState() {
        return getDisplaysPanel().getSelectedDisplay() == getLtsDisplay()
            && getLtsDisplay().isActive();
    }

    private final EditStateAction editStateAction;
}