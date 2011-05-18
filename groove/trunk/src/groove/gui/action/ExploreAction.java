package groove.gui.action;

import groove.explore.Exploration;
import groove.gui.Icons;
import groove.gui.LTSPanel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.jgraph.LTSJModel;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * The 'default exploration' action (class).
 */
public class ExploreAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public ExploreAction(Simulator simulator) {
        super(simulator, Options.DEFAULT_EXPLORATION_ACTION_NAME,
            Icons.FORWARD_ICON);
        putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
        this.ltsPanel = simulator.getLtsPanel();
    }

    @Override
    protected boolean doAction() {
        explore(getModel().getExploration(), true, true);
        return false;
    }

    /**
     * Run a given exploration. Can be called from outside the Simulator.
     * @param exploration the exploration strategy to be used
     * @param setResult if {@code true}, the result of the exploration will be set in the GTS
     * @param emphasise if {@code true}, the result of the exploration will be emphasised
     */
    public void explore(Exploration exploration, boolean setResult,
            boolean emphasise) {
        Action applyAction = getSimulator().getApplyTransitionAction();
        getModel().setExploration(exploration);
        // disable rule application for the time being
        boolean applyEnabled = applyAction.isEnabled();
        applyAction.setEnabled(false);
        LTSJModel ltsJModel = this.ltsPanel.getJModel();
        if (ltsJModel == null) {
            if (getModel().setGts()) {
                ltsJModel = this.ltsPanel.getJModel();
            } else {
                return;
            }
        }
        synchronized (ltsJModel) {
            GTS gts = getModel().getGts();
            int size = gts.size();
            // unhook the lts' jmodel from the lts, for efficiency's sake
            gts.removeLTSListener(ltsJModel);
            // create a thread to do the work in the background
            Thread generateThread = new ExploreThread(exploration);
            // go!
            getModel().getExplorationStats().start();
            generateThread.start();
            getModel().getExplorationStats().stop();
            gts.addLTSListener(ltsJModel);
            // collect the result states
            if (setResult) {
                gts.setResult(exploration.getLastResult());
            }
            if (gts.size() != size) {
                // get the lts' jmodel back on line and re-synchronize its state
                ltsJModel.loadGraph(gts);
            }
            // re-enable rule application
            // reset lts display visibility
            getSimulator().switchTabs(this.ltsPanel);
            getModel().setGts(gts);
            // emphasise the result states, if required
            if (emphasise) {
                Collection<GraphState> result =
                    exploration.getLastResult().getValue();
                this.ltsPanel.emphasiseStates(
                    new ArrayList<GraphState>(result), true);
            }
        }
        applyAction.setEnabled(applyEnabled);
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphView() != null
            && grammar.getErrors().isEmpty());
        String toolTipText =
            HTMLConverter.HTML_TAG.on(String.format(
                "%s (%s)",
                Options.DEFAULT_EXPLORATION_ACTION_NAME,
                HTMLConverter.STRONG_TAG.on(getModel().getExploration().getIdentifier())));
        putValue(Action.SHORT_DESCRIPTION, toolTipText);
    }

    private final LTSPanel ltsPanel;

    /**
     * Class that spawns a thread to perform a long-lasting action, while
     * displaying a dialog that can interrupt the thread.
     */
    public class ExploreThread extends Thread {
        /**
         * Constructs a generate thread for a given exploration strategy.
         * @param exploration the exploration handler of this thread
         */
        public ExploreThread(Exploration exploration) {
            this.cancelDialog = createCancelDialog();
            this.exploration = exploration;
            this.progressListener = createProgressListener();
        }

        @Override
        public void start() {
            super.start();
            // make dialog visible
            this.cancelDialog.setVisible(true);
            // wait for the thread to return
            try {
                this.join();
            } catch (InterruptedException exc) {
                // thread is done
            }
            synchronized (this.cancelDialog) {
                this.cancelDialog.dispose();
            }
        }

        /**
         * Runs the exploration as a parallel thread;
         * then hides the cancel dialog invisible, causing the event
         * dispatch thread to continue. 
         */
        @Override
        final public void run() {
            SimulatorModel simulatorModel = getModel();
            GTS gts = simulatorModel.getGts();
            GraphState state = simulatorModel.getState();
            displayProgress(gts);
            gts.addLTSListener(this.progressListener);
            try {
                this.exploration.play(gts, state);
            } catch (FormatException exc) {
                String[] options = {"Yes", "No"};
                String message =
                    "The last exploration is no longer valid in the "
                        + "current grammar. \n" + "Cannot apply '"
                        + this.exploration.getIdentifier() + "'.\n\n"
                        + "Use Breadth-First Exploration instead?\n";
                int response =
                    JOptionPane.showOptionDialog(getFrame(), message,
                        "Invalid Exploration", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == JOptionPane.OK_OPTION) {
                    Exploration newExplore = new Exploration();
                    simulatorModel.setExploration(newExplore);
                    try {
                        newExplore.play(gts, state);
                    } catch (FormatException e) {
                        showErrorDialog("Error: cannot parse exploration.", e);
                    }
                }
            }
            gts.removeLTSListener(this.progressListener);
            synchronized (this.cancelDialog) {
                // wait for the cancel dialog to become visible
                // (this is necessary if the doAction was actually very fast)
                while (!this.cancelDialog.isVisible()) {
                    try {
                        this.cancelDialog.wait(10);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
                this.cancelDialog.setVisible(false);
            }
        }

        /**
         * Creates a modal dialog that will interrupt this thread, when the
         * cancel button is pressed.
         */
        private JDialog createCancelDialog() {
            JDialog result;
            // create message dialog
            JOptionPane message =
                new JOptionPane(new Object[] {getStateCountLabel(),
                    getTransitionCountLabel()}, JOptionPane.PLAIN_MESSAGE);
            JButton cancelButton = new JButton("Cancel");
            // add a button to interrupt the generation process and
            // wait for the thread to finish and rejoin this one
            cancelButton.addActionListener(createCancelListener());
            message.setOptions(new Object[] {cancelButton});
            result =
                message.createDialog(ExploreAction.this.ltsPanel,
                    "Exploring state space");
            result.pack();
            return result;
        }

        /**
         * Returns a listener to this {@link ExploreThread} that interrupts
         * the thread and waits for it to rejoin this thread.
         */
        private ActionListener createCancelListener() {
            return new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    ExploreThread.this.interrupt();
                }
            };
        }

        /**
         * Creates a graph listener that displays the progress of the generate
         * thread on the cancel dialog.
         */
        private GTSListener createProgressListener() {
            return new GTSAdapter() {
                @Override
                public void addUpdate(GTS gts, GraphState state) {
                    displayProgress(gts);
                }

                @Override
                public void addUpdate(GTS gts, GraphTransition transition) {
                    displayProgress(gts);
                }
            };
        }

        /**
         * Returns the {@link JLabel} used to display the state count in the
         * cencel dialog; first creates the label if that is not yet done.
         */
        private JLabel getStateCountLabel() {
            // lazily create the label
            if (this.stateCountLabel == null) {
                this.stateCountLabel = new JLabel();
            }
            return this.stateCountLabel;
        }

        /**
         * Returns the {@link JLabel} used to display the state count in the
         * cencel dialog; first creates the label if that is not yet done.
         */
        private JLabel getTransitionCountLabel() {
            // lazily create the label
            if (this.transitionCountLabel == null) {
                this.transitionCountLabel = new JLabel();
            }
            return this.transitionCountLabel;
        }

        /**
         * Displays the number of lts states and transitions in the message
         * dialog.
         */
        void displayProgress(GTS gts) {
            getStateCountLabel().setText("States: " + gts.nodeCount());
            getTransitionCountLabel().setText("Transitions: " + gts.edgeCount());
        }

        /** Dialog for cancelling the thread. */
        private final JDialog cancelDialog;
        /** LTS generation strategy of this thread. (new version) */
        private final Exploration exploration;
        /** Progress listener for the generate thread. */
        private final GTSListener progressListener;
        /** Label displaying the number of states generated so far. */
        private JLabel transitionCountLabel;
        /** Label displaying the number of transitions generated so far. */
        private JLabel stateCountLabel;
    }

}