package groove.gui.action;

import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.StrategyValue;
import groove.explore.util.StatisticsReporter;
import groove.grammar.model.GrammarModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.dialog.ExploreWarningDialog;
import groove.gui.display.DisplayKind;
import groove.gui.jgraph.LTSJModel;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GTSChangeListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.util.parse.FormatException;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * The 'default exploration' action (class).
 */
public class ExploreAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public ExploreAction(Simulator simulator, boolean animated) {
        super(simulator, animated ? Options.ANIMATE_ACTION_NAME : Options.EXPLORE_ACTION_NAME,
            animated ? Icons.GO_START_ICON : Icons.GO_FORWARD_ICON);
        if (!animated) {
            putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
        }
        this.animated = animated;
    }

    @Override
    public void execute() {
        explore(getSimulatorModel().getExploration(), true);
    }

    /** Fully explores a given state of the GTS. */
    public void doExploreState() {
        GraphState state = getSimulatorModel().getState();
        explore(getStateExploration(), true);
        getSimulatorModel().doSetStateAndMatch(state, null);
    }

    /**
     * Run a given exploration. Can be called from outside the Simulator.
     * @param exploration the exploration strategy to be used
     * @param emphasise if {@code true}, the result of the exploration will be emphasised
     */
    public void explore(Exploration exploration, boolean emphasise) {
        LTSJModel ltsJModel = getLtsDisplay().getJModel();
        if (ltsJModel == null) {
            if (getSimulatorModel().setGts()) {
                ltsJModel = getLtsDisplay().getJModel();
            } else {
                return;
            }
        }
        SimulatorModel simModel = getSimulatorModel();
        GTS gts = simModel.getGTS();
        if (isAnimated()) {
            simModel.setDisplay(DisplayKind.LTS);
        }
        // unhook the lts' jmodel from the lts, for efficiency's sake
        ltsJModel.setExploring(true);
        this.bound = INITIAL_STATE_BOUND;
        // create a thread to do the work in the background
        ExploreThread generateThread = new ExploreThread(exploration);
        // go!
        StatisticsReporter exploreStats = simModel.getExplorationStats();
        exploration.addListener(exploreStats);
        generateThread.start();
        exploration.removeListener(exploreStats);
        exploreStats.report();
        // emphasise the result states, if required
        ltsJModel.setExploring(false);
        simModel.setGts(gts, true);
        if (exploration.getLastState() != null) {
            simModel.setState(exploration.getLastState());
        }
        if (emphasise) {
            Collection<GraphState> result = exploration.getResult().getStates();
            getLtsDisplay().emphasiseStates(new ArrayList<GraphState>(result), true);
        }
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        Exploration exploration = getSimulatorModel().getExploration();
        boolean enabled =
            grammar != null && grammar.getStartGraphModel() != null && !grammar.hasErrors()
                && grammar.hasRules();
        FormatException compatibilityError = null;
        if (enabled) {
            try {
                exploration.test(grammar.toGrammar());
            } catch (FormatException exc) {
                compatibilityError = exc;
                enabled = false;
            }
        }
        setEnabled(enabled);
        String toolTipText =
            String.format("%s (%s)",
                this.animated ? Options.ANIMATE_ACTION_NAME : Options.EXPLORE_ACTION_NAME,
                HTMLConverter.STRONG_TAG.on(exploration.getIdentifier()));
        if (compatibilityError != null) {
            toolTipText +=
                HTMLConverter.HTML_LINEBREAK
                    + HTMLConverter.EMBARGO_TAG.on(HTMLConverter.toHtml(compatibilityError.getMessage()));
        }
        putValue(Action.SHORT_DESCRIPTION, HTMLConverter.HTML_TAG.on(toolTipText));
    }

    final boolean isAnimated() {
        return this.animated;
    }

    /** Flag indicating that the exploration is animated. */
    private final boolean animated;

    /**
     * Returns the pause between animation steps, in milliseconds.
     * The pause equals {@code 4000/(speed+1)}.
     */
    final int getPause() {
        return 4000 / (getSpeed() + 1);
    }

    /** Returns the animation speed. */
    final int getSpeed() {
        return this.speed;
    }

    /** Sets the animation speed to a certain value. */
    final void setSpeed(int speed) {
        this.speed = Math.min(10, Math.max(speed, 1));
    }

    /** Animation speed (between 1 and 10). */
    private int speed = 2;

    /**
     * Returns the {@link JLabel} used to display the state count in the
     * cencel dialog; first creates the label if that is not yet done.
     */
    final JLabel getStateCountLabel() {
        // lazily create the label
        if (this.stateCountLabel == null) {
            this.stateCountLabel = new JLabel();
        }
        return this.stateCountLabel;
    }

    /** Label displaying the number of transitions generated so far. */
    private JLabel stateCountLabel;

    /**
     * Returns the {@link JLabel} used to display the state count in the
     * cencel dialog; first creates the label if that is not yet done.
     */
    final JLabel getTransitionCountLabel() {
        // lazily create the label
        if (this.transitionCountLabel == null) {
            this.transitionCountLabel = new JLabel();
        }
        return this.transitionCountLabel;
    }

    /** Label displaying the number of states generated so far. */
    private JLabel transitionCountLabel;

    /**
     * Returns the frames-per-second slider for the animation dialog.
     */
    final JPanel getAnimationPanel() {
        // lazily create the label
        if (this.animationPanel == null) {
            JLabel label = new JLabel("Animation Speed");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 2) {
                @Override
                public void updateUI() {
                    setUI(BasicSliderUI.createUI(this));
                }
            };
            slider.setMajorTickSpacing(9);
            slider.setMinorTickSpacing(1);
            slider.setSnapToTicks(true);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setFocusable(false);
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int speed = slider.getValue();
                    setSpeed(speed);
                }
            });
            slider.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.animationPanel = new JPanel();
            this.animationPanel.setLayout(new BoxLayout(this.animationPanel, BoxLayout.Y_AXIS));
            this.animationPanel.add(label);
            this.animationPanel.add(slider);
        }
        return this.animationPanel;
    }

    /** Slider for the animation speed. */
    private JPanel animationPanel;

    /**
     * Displays the number of lts states and transitions in the message
     * dialog.
     */
    final void displayProgress(final GTS gts) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getStateCountLabel().setText("States: " + gts.nodeCount());
                getTransitionCountLabel().setText("Transitions: " + gts.edgeCount());
            }
        });
    }

    /**
     * If the number of states now exeeds a bound, ask whether we
     * should continue exploring (and by how much).
     */
    final void checkContinue(final GTS gts) {
        if (gts.nodeCount() >= this.bound && !isInterrupted()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        ExploreWarningDialog dialog = ExploreWarningDialog.instance();
                        dialog.setBound(ExploreAction.this.bound);
                        if (dialog.ask(getFrame())) {
                            ExploreAction.this.bound = dialog.getBound();
                        } else {
                            setInterrupted(true);
                        }
                    }
                });
            } catch (InterruptedException e) {
                setInterrupted(true);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
            if (isInterrupted()) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Returns the explore-strategy for exploring a single state
     */
    private Exploration getStateExploration() {
        if (this.stateExploration == null) {
            this.stateExploration = new Exploration(StrategyValue.STATE, AcceptorValue.NONE, 0);
        }
        return this.stateExploration;
    }

    private Exploration stateExploration;

    /** Number of states after which exploration should halt. */
    private int bound;

    private boolean isInterrupted() {
        return this.interrupted;
    }

    private void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /** Interrupt flag set during the progress warning. */
    private boolean interrupted;

    /** Initial number of states after which exploration should halt. */
    private static final int INITIAL_STATE_BOUND = 1000;

    private final class AnimateListener extends GTSChangeListener {
        @Override
        public void addUpdate(final GTS gts, final GraphState state) {
            super.addUpdate(gts, state);
            displayProgress(gts);
            try {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            getSimulatorModel().setState(state);
                        }
                    });
                } catch (InvocationTargetException e) {
                    // do nothing
                }
                Thread.sleep(getPause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void addUpdate(final GTS gts, final GraphTransition transition) {
            super.addUpdate(gts, transition);
            displayProgress(gts);
        }
    }

    private final class ExploreListener extends GTSChangeListener {
        @Override
        public void addUpdate(GTS gts, final GraphState state) {
            super.addUpdate(gts, state);
            displayProgress(gts);
            checkContinue(gts);
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            super.addUpdate(gts, transition);
            displayProgress(gts);
        }
    }

    /**
     * Class that spawns a thread to perform a long-lasting action, while
     * displaying a dialog that can interrupt the thread.
     */
    private class ExploreThread extends Thread {
        /**
         * Constructs a generate thread for a given exploration strategy.
         */
        public ExploreThread(Exploration exploration) {
            this.cancelDialog = createCancelDialog();
            this.progressListener = createProgressListener();
            this.exploration = exploration;
        }

        @Override
        public void start() {
            ExploreThread.super.start();
            // start up the cancel dialog
            this.cancelDialog.setVisible(true);
            // wait for the explore thread to finish
            try {
                this.join();
            } catch (InterruptedException exc) {
                // thread is done
            }
            this.cancelDialog.dispose();
        }

        /**
         * Runs the exploration as a parallel thread;
         * then hides the cancel dialog invisible, causing the event
         * dispatch thread to continue.
         */
        @Override
        final public void run() {
            final SimulatorModel simulatorModel = getSimulatorModel();
            GTS gts = simulatorModel.getGTS();
            displayProgress(gts);
            gts.addLTSListener(this.progressListener);
            setInterrupted(false);
            GraphState state = simulatorModel.getState();
            try {
                this.exploration.play(gts, state);
            } catch (FormatException exc) {
                // this should not occur, as the exploration and the
                // grammar in the simulator model should always be compatible
                showErrorDialog(exc,
                    "Exploration strategy %s incompatible with grammar",
                    this.exploration.getIdentifier());
            }
            gts.removeLTSListener(this.progressListener);
            disposeCancelDialog();
        }

        /**
         * Creates a modal dialog that will interrupt this thread, when the
         * cancel button is pressed.
         */
        private JDialog createCancelDialog() {
            JDialog result;
            // create message dialog
            JOptionPane message =
                new JOptionPane(isAnimated() ? getAnimationPanel() : new Object[] {
                    getStateCountLabel(), getTransitionCountLabel()}, JOptionPane.PLAIN_MESSAGE);
            message.setOptions(new Object[] {getCancelButton()});
            result = message.createDialog(getFrame(), "Exploring state space");
            result.pack();
            result.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            result.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ExploreThread.this.interrupt();
                }
            });
            result.setAlwaysOnTop(true);
            return result;
        }

        /**
         * Disposes the dialog that can cancel the exploration thread.
         * May only be invoked from the exploration thread.
         */
        private void disposeCancelDialog() {
            assert !SwingUtilities.isEventDispatchThread();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        ExploreThread.this.cancelDialog.dispose();
                    }
                });
            } catch (InterruptedException e) {
                // do nothing
            } catch (InvocationTargetException e) {
                // do nothing
            }
        }

        private JButton getCancelButton() {
            if (this.cancelButton == null) {
                this.cancelButton = new JButton("Cancel");
                // add a button to interrupt the generation process and
                // wait for the thread to finish and rejoin this one
                this.cancelButton.addActionListener(createCancelListener());
            }
            return this.cancelButton;
        }

        /**
         * Returns a listener to this {@link ExploreThread} that interrupts
         * the thread and waits for it to rejoin this thread.
         */
        private ActionListener createCancelListener() {
            return new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    ExploreThread.this.interrupt();
                }
            };
        }

        /**
         * Creates a graph listener that displays the progress of the generate
         * thread on the cancel dialog.
         */
        private GTSChangeListener createProgressListener() {
            return isAnimated() ? new AnimateListener() : new ExploreListener();
        }

        /** Exploration to be used. */
        private final Exploration exploration;
        /** Dialog for cancelling the thread. */
        private final JDialog cancelDialog;
        /** Button that cancels the thread. */
        private JButton cancelButton;
        /** Progress listener for the generate thread. */
        private final GTSChangeListener progressListener;
    }

}