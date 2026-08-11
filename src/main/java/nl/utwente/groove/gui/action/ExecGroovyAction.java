package nl.utwente.groove.gui.action;

import static nl.utwente.groove.grammar.model.ResourceKind.GROOVY;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GroovyModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.GroovyDisplay;

/** Action to execute the currently selected Groovy script. */
public class ExecGroovyAction extends SimulatorAction {
    private static final long serialVersionUID = 1652912426926686879L;

    /** Constructs a new action, for a given control panel. */
    public ExecGroovyAction(Simulator simulator) {
        super(simulator, DESCRIPTION, Icons.GO_START_ICON, null, GROOVY);
    }

    @Override
    public void execute() {
        for (QualName name : getSimulatorModel().getSelectSet(getResourceKind())) {
            GroovyModel model = (GroovyModel) getGrammarModel().getResource(GROOVY, name);
            if (model.isActive()) {
                ((GroovyDisplay) getDisplay()).executeGroovy(name);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(!getSimulatorModel().getSelectSet(getResourceKind()).isEmpty());
    }

    static private final String DESCRIPTION = "Execute Groovy script";
}
