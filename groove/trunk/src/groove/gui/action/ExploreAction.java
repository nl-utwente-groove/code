package groove.gui.action;

import groove.explore.Exploration;
import groove.gui.DisplayKind;
import groove.gui.Icons;
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
import groove.view.GrammarModel;

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
        super(simulator, animated ? Options.ANIMATE_ACTION_NAME
                : Options.EXPLORE_ACTION_NAME, animated ? Icons.GO_START_ICON
                : Icons.GO_FORWARD_ICON);
        if (!animated) {
            putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
        }
        this.animated = animated;
    }

    @Override
    public void execute() {
        explore(getSimulatorModel().getExploration(), true, true);
    }

    /**
     * Run a given exploration. Can be called from outside the Simulator.
     * @param exploration the exploration strategy to be used
     * @param setResult if {@code true}, the result of the exploration will be set in the GTS
     * @param emphasise if {@code true}, the result of the exploration will be emphasised
     */
    public void explore(Exploration exploration, boolean setResult,
            boolean emphasise) {
        getSimulatorModel().setExploration(exploration);
        LTSJModel ltsJModel = getLtsDisplay().getLtsModel();
        if (ltsJModel == null) {
            if (getSimulatorModel().setGts()) {
                ltsJModel = getLtsDisplay().getLtsModel();
            } else {
                return;
            }
        }
        GTS gts = getSimulatorModel().getGts();
        // unhook the lts' jmodel from the lts, for efficiency's sake
        gts.removeLTSListener(ltsJModel);
        if (isAnimated()) {
            getSimulatorModel().setDisplay(DisplayKind.LTS);
            getLtsDisplay().selectStateTab();
        }
        // create a thread to do the work in the background
        Thread generateThread = new ExploreThread();
        // go!
        getSimulatorModel().getExplorationStats().start();
        generateThread.start();
        getSimulatorModel().getExplorationStats().stop();
        gts.addLTSListener(ltsJModel);
        // collect the result states
        if (setResult) {
            gts.setResult(exploration.getLastResult());
        }
        // emphasise the result states, if required
        if (emphasise) {
            Collection<GraphState> result =
                exploration.getLastResult().getValue();
            getLtsDisplay().emphasiseStates(new ArrayList<GraphState>(result),
                true);
        }
        getSimulatorModel().setGts(gts, true);
        if (isAnimated() && exploration.getLastState() != null) {
            getSimulatorModel().setState(exploration.getLastState());
        }
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphModel() != null
            && grammar.getErrors().isEmpty());
        String toolTipText =
            HTMLConverter.HTML_TAG.on(String.format(
                "%s (%s)",
                this.animated ? Options.ANIMATE_ACTION_NAME
                        : Options.EXPLORE_ACTION_NAME,
                HTMLConverter.STRONG_TAG.on(getSimulatorModel().getExploration().getIdentifier())));
        putValue(Action.SHORT_DESCRIPTION, toolTipText);
    }

    final boolean isAnimated() {
        return this.animated;
    }

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
            this.animationPanel.setLayout(new BoxLayout(this.animationPanel,
                BoxLayout.Y_AXIS));
            this.animationPanel.add(label);
            this.animationPanel.add(slider);
        }
        return this.animationPanel;
    }

    /**
     * Displays the number of lts states and transitions in the message
     * dialog.
     */
    final void displayProgress(GTS gts) {
        getStateCountLabel().setText("States: " + gts.nodeCount());
        getTransitionCountLabel().setText("Transitions: " + gts.edgeCount());
    }

    /** Slider for the animation speed. */
    private JPanel animationPanel;
    /** Label displaying the number of states generated so far. */
    private JLabel transitionCountLabel;
    /** Label displaying the number of transitions generated so far. */
    private JLabel stateCountLabel;
    /** Flag indicating that the exploration is animated. */
    private final boolean animated;
    /** Animation speed (between 1 and 10). */
    private int speed = 2;

    private final class AnimateListener extends GTSAdapter {
        @Override
        public void addUpdate(GTS gts, final GraphState state) {
            displayProgress(gts);
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getSimulatorModel().setState(state);
                    }
                });
                Thread.sleep(getPause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            displayProgress(gts);
        }
    }

    private final class ExploreListener extends GTSAdapter {
        @Override
        public void addUpdate(GTS gts, final GraphState state) {
            displayProgress(gts);
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            displayProgress(gts);
        }
    }

    /**
     * Class that spawns a thread to perform a long-lasting action, while
     * displaying a dialog that can interrupt the thread.
     */
    public class ExploreThread extends Thread {
        /**
         * Constructs a generate thread for a given exploration strategy.
         */
        public ExploreThread() {
            this.cancelDialog = createCancelDialog();
            this.progressListener = createProgressListener();
        }

        @Override
        public void start() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ExploreThread.super.start();
                }
            });
            // make dialog visible
            this.cancelDialog.setVisible(true);
            // wait for the thread to return
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
            GTS gts = simulatorModel.getGts();
            displayProgress(gts);
            gts.addLTSListener(this.progressListener);
            GraphState state = simulatorModel.getState();
            Exploration exploration = simulatorModel.getExploration();
            try {
                exploration.play(gts, state);
            } catch (FormatException exc) {
                String[] options = {"Yes", "No"};
                String message =
                    "The last exploration is no longer valid in the "
                        + "current grammar. \n" + "Cannot apply '"
                        + exploration.getIdentifier() + "'.\n\n"
                        + "Use Breadth-First Exploration instead?\n";
                int response =
                    JOptionPane.showOptionDialog(getFrame(), message,
                        "Invalid Exploration", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == JOptionPane.OK_OPTION) {
                    exploration = new Exploration();
                    simulatorModel.setExploration(exploration);
                    try {
                        exploration.play(gts, state);
                    } catch (FormatException e) {
                        showErrorDialog(e, "Error: cannot parse exploration.");
                    }
                }
            }
            gts.removeLTSListener(this.progressListener);
            disposeCancelDialog();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    GraphState lastState =
                        getSimulatorModel().getExploration().getLastState();
                    if (lastState != null) {
                        simulatorModel.setState(lastState);
                    }
                }
            });
        }

        private void disposeCancelDialog() {
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

        /**
         * Creates a modal dialog that will interrupt this thread, when the
         * cancel button is pressed.
         */
        private JDialog createCancelDialog() {
            JDialog result;
            // create message dialog
            JOptionPane message =
                new JOptionPane(isAnimated() ? getAnimationPanel()
                        : new Object[] {getStateCountLabel(),
                            getTransitionCountLabel()},
                    JOptionPane.PLAIN_MESSAGE);
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
            return result;
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
            return isAnimated() ? new AnimateListener() : new ExploreListener();
        }

        /** Dialog for cancelling the thread. */
        private final JDialog cancelDialog;
        /** Button that cancels the thread. */
        private JButton cancelButton;
        /** Progress listener for the generate thread. */
        private final GTSListener progressListener;
    }

}