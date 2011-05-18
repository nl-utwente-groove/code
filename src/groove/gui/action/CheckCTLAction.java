package groove.gui.action;

import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.StringDialog;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.verify.DefaultMarker;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.ParseException;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Action for verifying a CTL formula.
 */
public class CheckCTLAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public CheckCTLAction(Simulator simulator, boolean full) {
        super(simulator, full ? Options.CHECK_CTL_FULL_ACTION_NAME
                : Options.CHECK_CTL_AS_IS_ACTION_NAME, null);
        this.full = full;
    }

    @Override
    public boolean execute() {
        String property = getCtlFormulaDialog().showDialog(getFrame());
        if (property != null) {
            boolean doCheck = true;
            GTS gts = getModel().getGts();
            if (gts.hasOpenStates() && this.full && getModel().setGts()) {
                getActions().getExploreAction().explore(
                    getModel().getExploration(), true, false);
                doCheck = !gts.hasOpenStates();
            }
            if (doCheck) {
                try {
                    doCheckProperty(gts,
                        FormulaParser.parse(property).toCtlFormula());
                } catch (ParseException e) {
                    // the property has already been parsed by the dialog
                    assert false;
                }
            }
        }
        return false;
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    private StringDialog getCtlFormulaDialog() {
        if (this.ctlFormulaDialog == null) {
            this.ctlFormulaDialog =
                new StringDialog("Enter the CTL Formula",
                    FormulaParser.getDocMap(true)) {
                    @Override
                    public String parse(String text) throws FormatException {
                        try {
                            FormulaParser.parse(text).toCtlFormula();
                        } catch (ParseException efe) {
                            throw new FormatException(efe.getMessage());
                        }
                        return text;
                    }
                };
        }
        return this.ctlFormulaDialog;
    }

    private void doCheckProperty(GTS gts, Formula formula) {
        DefaultMarker modelChecker = new DefaultMarker(formula, gts);
        modelChecker.verify();
        int counterExampleCount = modelChecker.getCount(false);
        List<GraphState> counterExamples =
            new ArrayList<GraphState>(counterExampleCount);
        String message;
        if (counterExampleCount == 0) {
            message =
                String.format("The property '%s' holds for all states", formula);
        } else {
            boolean allStates =
                confirmBehaviour(VERIFY_ALL_STATES_OPTION,
                    "Verify all states? Choosing 'No' will report only on the start state.");
            if (allStates) {
                for (GraphState state : modelChecker.getStates(false)) {
                    counterExamples.add(state);
                }
                message =
                    String.format(
                        "The property '%s' fails to hold in the %d highlighted states",
                        formula, counterExampleCount);
            } else if (modelChecker.hasValue(false)) {
                counterExamples.add(gts.startState());
                message =
                    String.format(
                        "The property '%s' fails to hold in the initial state",
                        formula);
            } else {
                message =
                    String.format(
                        "The property '%s' holds in the initial state", formula);
            }
        }
        getSimulator().getLtsPanel().emphasiseStates(counterExamples, false);
        JOptionPane.showMessageDialog(getSimulator().getFrame(), message);
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGts() != null);
    }

    private final boolean full;

    /**
     * Dialog for entering temporal formulae.
     */
    private StringDialog ctlFormulaDialog;

}