package groove.gui.action;

import groove.control.CtrlAut;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.jgraph.CtrlJGraph;
import groove.trans.GraphGrammar;
import groove.trans.ResourceKind;
import groove.view.ControlModel;
import groove.view.FormatException;
import groove.view.GrammarModel;

import javax.swing.JDialog;

/**
 * Creates a dialog showing the control automaton.
 */
public class PreviewControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public PreviewControlAction(Simulator simulator) {
        super(simulator, Options.PREVIEW_CONTROL_ACTION_NAME,
            Icons.CONTROL_MODE_ICON, null, ResourceKind.CONTROL);
    }

    @Override
    public void execute() {
        try {
            CtrlAut aut = getCtrlAut();
            if (aut != null) {
                getJGraph().setModel(aut);
                getDialog().setVisible(true);
            }
        } catch (FormatException exc) {
            showErrorDialog(exc, String.format("Error in control program '%s'",
                getSimulatorModel().getSelected(ResourceKind.CONTROL)));
        }
    }

    @Override
    public void refresh() {
        try {
            setEnabled(getCtrlAut() != null);
        } catch (FormatException e) {
            setEnabled(false);
        }
    }

    private CtrlJGraph getJGraph() throws FormatException {
        if (this.jGraph == null) {
            this.jGraph = new CtrlJGraph(getSimulator());
            this.jGraph.setModel(getCtrlAut());
            this.jGraph.getLayouter().start(true);
        }
        return this.jGraph;
    }

    private CtrlJGraph jGraph;

    private JDialog getDialog() throws FormatException {
        return new GraphPreviewDialog(getSimulator(), getCtrlAut());
    }

    //    private JDialog getDialog() throws FormatException {
    //        JDialog result = this.dialog;
    //        if (result == null) {
    //            JGraphPanel<?> autPanel =
    //                new JGraphPanel<CtrlJGraph>(getJGraph(), true);
    //            autPanel.initialise();
    //            autPanel.setEnabled(true);
    //            result = this.dialog = new JDialog(getFrame(), "Control Automaton");
    //            result.add(autPanel);
    //            result.setSize(600, 700);
    //            Point p = getFrame().getLocation();
    //            result.setLocation(new Point(p.x + 50, p.y + 50));
    //            result.setVisible(true);
    //        }
    //        return result;
    //    }
    //
    //    private JDialog dialog;

    /** Convenience method to obtain the currently selected control automaton. */
    private CtrlAut getCtrlAut() throws FormatException {
        CtrlAut result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            GraphGrammar grammar = grammarModel.toGrammar();
            ControlModel controlModel =
                (ControlModel) getSimulatorModel().getResource(
                    getResourceKind());
            result =
                controlModel == null ? grammar.getCtrlAut()
                        : controlModel.toCtrlAut();
        }
        return result;
    }
}