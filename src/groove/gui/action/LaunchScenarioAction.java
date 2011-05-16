package groove.gui.action;

import groove.explore.ModelCheckingScenario;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.gui.Simulator;
import groove.gui.dialog.BoundedModelCheckingDialog;
import groove.gui.dialog.StringDialog;
import groove.gui.jgraph.LTSJModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.verify.FormulaParser;
import groove.verify.ParseException;
import groove.view.FormatException;

import javax.swing.Action;

/** An action used for launching a scenario. */
public class LaunchScenarioAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public LaunchScenarioAction(Simulator simulator,
            ModelCheckingScenario scenario) {
        super(simulator, scenario.getName(), null);
        this.scenario = scenario;
    }

    @Override
    public void refresh() {
        super.refresh();
        putValue(Action.NAME, this.scenario.getName());
    }

    @Override
    protected boolean doAction() {
        doGenerate(this.scenario);
        return false;
    }

    /**
     * Will be replaced by doRunExploration.
     */
    private void doGenerate(ModelCheckingScenario scenario) {

        /*
         * When a (LTL) ModelCheckingScenario is started, initialize by asking
         * the user to enter a property (via a getFormulaDialog).
         */
        String property = getLtlFormulaDialog().showDialog(getFrame());
        if (property == null) {
            return;
        }
        scenario.setProperty(property);

        GTS gts = getModel().getGts();
        GraphState state = getModel().getState();
        /*
         * When a (LTL) BoundedModelCheckingScenario is started, also prompt the
         * user to enter a boundary (via a BoundedModelCheckingDialog).
         */
        if (scenario.getStrategy() instanceof BoundedModelCheckingStrategy) {
            BoundedModelCheckingDialog dialog =
                new BoundedModelCheckingDialog();
            dialog.setGrammar(gts.getGrammar());
            dialog.showDialog(getFrame());
            Boundary boundary = dialog.getBoundary();
            if (boundary == null) {
                return;
            }
            ((BoundedModelCheckingStrategy) scenario.getStrategy()).setBoundary(boundary);
        }

        scenario.prepare(gts, state);
        LTSJModel ltsJModel = getSimulator().getLtsPanel().getJModel();
        Action applyAction = getSimulator().getApplyTransitionAction();
        synchronized (ltsJModel) {
            // unhook the lts' jmodel from the lts, for efficiency's sake
            gts.removeLTSListener(ltsJModel);
            int size = gts.size();
            // disable rule application for the time being
            boolean applyEnabled = applyAction.isEnabled();
            applyAction.setEnabled(false);
            // create a thread to do the work in the background
            Thread generateThread = getSimulator().new LaunchThread(scenario);
            // go!
            generateThread.start();
            if (gts.size() == size) {
                // the exploration had no effect
                gts.addLTSListener(ltsJModel);
            } else {
                // get the lts' jmodel back on line and re-synchronize its state
                ltsJModel.loadGraph(ltsJModel.getGraph());
                // re-enable rule application
                applyAction.setEnabled(applyEnabled);
                // reset lts display visibility
                getSimulator().switchTabs(getSimulator().getLtsPanel());
                getModel().setGts(gts);
            }
        }
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    private StringDialog getLtlFormulaDialog() {
        if (this.ltlFormulaDialog == null) {
            this.ltlFormulaDialog =
                new StringDialog("Enter the LTL Formula",
                    FormulaParser.getDocMap(false)) {
                    @Override
                    public String parse(String text) throws FormatException {
                        try {
                            FormulaParser.parse(text).toLtlFormula();
                        } catch (ParseException e) {
                            throw new FormatException(e.getMessage());
                        }
                        return text;
                    }
                };
        }
        return this.ltlFormulaDialog;
    }

    /**
     * Dialog for entering temporal formulae.
     */
    private StringDialog ltlFormulaDialog;

    private final ModelCheckingScenario scenario;
}