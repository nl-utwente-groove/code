package groove.gui.action;

import static groove.graph.GraphRole.TYPE;
import groove.graph.GraphInfo;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/** Action to create and start editing a new type graph. */
public class NewTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public NewTypeAction(Simulator simulator) {
        super(simulator, Options.NEW_TYPE_ACTION_NAME, Icons.NEW_TYPE_ICON);
    }

    @Override
    public boolean execute() {
        String typeName =
            askNewTypeName("Select type graph name", Groove.DEFAULT_TYPE_NAME,
                true);
        if (typeName != null) {
            final AspectGraph initType =
                AspectGraph.emptyGraph(typeName, TYPE).clone();
            GraphInfo.getProperties(initType, true).setEnabled(false);
            initType.setFixed();
            try {
                getModel().doAddType(initType);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getMainPanel().getTypeTab().doEdit(initType);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e, "Error creating new type graph '%s'",
                    typeName);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}