package groove.gui.action;

import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import groove.explore.ExploreResult;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.StringDialog;
import groove.gui.display.DisplayKind;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.parse.FormatException;
import groove.verify.CTLMarker;
import groove.verify.CTLModelChecker;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.Logic;

/**
 * Action for verifying a CTL formula.
 */
public class CheckCTLAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public CheckCTLAction(Simulator simulator, boolean full) {
        super(simulator,
            full ? Options.CHECK_CTL_FULL_ACTION_NAME : Options.CHECK_CTL_AS_IS_ACTION_NAME, null);
        this.full = full;
    }

    @Override
    public void execute() {
        String property = getCtlFormulaDialog().showDialog(getFrame());
        if (property != null) {
            boolean doCheck = true;
            GTS gts = getSimulatorModel().getGTS();
            // completely re-explore if the GTS has open states
            if (gts.hasOpenStates() && this.full && getSimulatorModel().resetGTS()) {
                getActions().getExploreAction()
                    .explore(getGrammarModel().getDefaultExploreType());
                gts = getSimulatorModel().getGTS();
                doCheck = !gts.hasOpenStates();
            }
            if (doCheck) {
                try {
                    doCheckProperty(getSimulatorModel().getExploreResult(), property);
                } catch (FormatException e) {
                    // the property has already been parsed by the dialog
                    assert false;
                }
            }
        }
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    private StringDialog getCtlFormulaDialog() {
        if (this.ctlFormulaDialog == null) {
            this.ctlFormulaDialog =
                new StringDialog("Enter the CTL Formula", FormulaParser.getDocMap(Logic.CTL)) {
                    @Override
                    public String parse(String text) throws FormatException {
                        Formula.parse(Logic.CTL, text);
                        return text;
                    }
                };
        }
        return this.ctlFormulaDialog;
    }

    /**
     * Model checks a given property on an exploration result.
     * @throws FormatException if the property is not a properly formatted CTL property
     */
    private void doCheckProperty(ExploreResult result, String property) throws FormatException {
        Formula formula = Formula.parse(property)
            .toCtlFormula();
        CTLMarker modelChecker = new CTLMarker(formula, CTLModelChecker.newModel(result));
        int counterExampleCount = modelChecker.getCount(false);
        List<GraphState> counterExamples = new ArrayList<>(counterExampleCount);
        String message;
        if (counterExampleCount == 0) {
            message = String.format("The property '%s' holds for all states", property);
        } else {
            boolean allStates = confirmBehaviour(VERIFY_ALL_STATES_OPTION,
                "Verify all states? Choosing 'No' will report only on the start state.");
            if (allStates) {
                for (Node state : modelChecker.getStates(false)) {
                    counterExamples.add((GraphState) state);
                }
                message =
                    String.format("The property '%s' fails to hold in the %d highlighted states",
                        property,
                        counterExampleCount);
            } else if (modelChecker.hasValue(false)) {
                counterExamples.add(result.getGTS()
                    .startState());
                message =
                    String.format("The property '%s' fails to hold in the initial state", property);
            } else {
                message = String.format("The property '%s' holds in the initial state", property);
            }
        }
        getLtsDisplay().emphasiseStates(counterExamples, false);
        getSimulatorModel().setDisplay(DisplayKind.LTS);
        JOptionPane.showMessageDialog(getFrame(), message);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGTS() != null);
    }

    private final boolean full;
    /**
     * Dialog for entering temporal formulae.
     */
    private StringDialog ctlFormulaDialog;

}