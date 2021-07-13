package groove.gui.action;

import static groove.grammar.model.ResourceKind.GROOVY;

import javax.swing.Action;

import groove.grammar.QualName;
import groove.grammar.groovy.Util;
import groove.grammar.model.GroovyModel;
import groove.gui.Icons;
import groove.gui.Simulator;
import groove.gui.display.GroovyDisplay;
import groove.io.HTMLConverter;

/** Action to execute the currently selected Groovy script. */
public class ExecGroovyAction extends SimulatorAction {
    private static final long serialVersionUID = 1652912426926686879L;

    /** Constructs a new action, for a given control panel. */
    public ExecGroovyAction(Simulator simulator) {
        super(simulator, DESCRIPTION, Icons.GO_START_ICON, null, GROOVY);
        this.enabled = Util.isGroovyPresent();
        if (!this.enabled) {
            StringBuilder descr = new StringBuilder(DESCRIPTION);
            descr.append(HTMLConverter.HTML_LINEBREAK);
            descr.append(DISABLED_DESCRIPTION);
            HTMLConverter.HTML_TAG.on(descr);
            putValue(Action.SHORT_DESCRIPTION, descr.toString());
        }
    }

    @Override
    public void execute() {
        for (QualName name : getSimulatorModel().getSelectSet(getResourceKind())) {
            GroovyModel model = (GroovyModel) getGrammarModel().getResource(GROOVY, name);
            if (model.isEnabled()) {
                ((GroovyDisplay) getDisplay()).executeGroovy(name);
            }
        }
    }

    @Override
    public void refresh() {
        boolean enabled = this.enabled;
        if (enabled) {
            enabled = !getSimulatorModel().getSelectSet(getResourceKind())
                .isEmpty();
        }
        setEnabled(enabled);
    }

    private final boolean enabled;

    static private final String DESCRIPTION = "Execute Groovy script";
    static private final String DISABLED_DESCRIPTION =
        "To enable, insert the Groovy jars in the Groove bin directory";
}
