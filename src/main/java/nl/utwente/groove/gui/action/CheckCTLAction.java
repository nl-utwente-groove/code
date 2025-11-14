package nl.utwente.groove.gui.action;

import static nl.utwente.groove.gui.Options.VERIFY_ALL_STATES_OPTION;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.StringDialog;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.CTLMarker;
import nl.utwente.groove.verify.CTLModelChecker;
import nl.utwente.groove.verify.Formula;
import nl.utwente.groove.verify.FormulaParser;
import nl.utwente.groove.verify.Logic;

/**
 * Action for verifying a CTL formula.
 */
public class CheckCTLAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public CheckCTLAction(Simulator simulator, boolean full) {
        super(simulator, full
            ? Options.CHECK_CTL_FULL_ACTION_NAME
            : Options.CHECK_CTL_AS_IS_ACTION_NAME, null);
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
                getActions().getExploreAction().explore(getGrammarModel().getDefaultExploreType());
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
            this.ctlFormulaDialog
                = new StringDialog("Enter the CTL Formula", FormulaParser.getDocMap(Logic.CTL)) {
                    @Override
                    public String parse(String text) throws FormatException {
                        var formula = Formula.parse(Logic.CTL, text);
                        formula.check(getSimulatorModel().getGTS());
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
        Formula formula = Formula.parse(property).toCtlFormula();
        formula.check(result.getGTS());
        CTLMarker modelChecker = new CTLMarker(formula, CTLModelChecker.newModel(result));
        int witnesscCount = modelChecker.getCount();
        List<GraphState> witnesses = new ArrayList<>(witnesscCount);
        String message;
        if (witnesscCount == 0) {
            message = String.format("The property '%s' does not hold anywhere", property);
        } else {
            boolean allStates
                = confirmBehaviour(VERIFY_ALL_STATES_OPTION,
                                   "Verify all states? Choosing 'No' will report only on the start state.");
            if (allStates) {
                modelChecker.stateStream().map(n -> (GraphState) n).forEach(witnesses::add);
                message = String
                    .format("The property '%s' holds in the %d highlighted states", property,
                            witnesscCount);
            } else if (modelChecker.hasValue()) {
                witnesses.add(result.getGTS().startState());
                message = String.format("The property '%s' holds in the initial state", property);
            } else {
                message = String
                    .format("The property '%s' fails to hold in the initial state", property);
            }
        }
        // Create a fresh result to be independent on whatever result states were there
        formula_count++;
        var name = "f" + formula_count;
        result = new ExploreResult(name, result.getGTS());
        witnesses.forEach(result::addState);
        result.push();
        getSimulatorModel().setExploreResult(result);
        getLtsDisplay().emphasiseStates(witnesses, false);
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

    /** Count of all invocations of #execute, used to give unique names to formulas. */
    static private int formula_count;
}