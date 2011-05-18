package groove.gui.action;

import static groove.gui.Options.STOP_SIMULATION_OPTION;
import groove.graph.TypeLabel;
import groove.gui.BehaviourOption;
import groove.gui.ControlPanel;
import groove.gui.Refreshable;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorPanel;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.FreshNameDialog;
import groove.gui.dialog.RelabelDialog;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.store.SystemStore;
import groove.util.Duo;
import groove.util.Groove;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Abstract action class for simulator actions.
 * The class contains a host of convenience methods for confirmation
 * dialogs.
 * The actual action to be taken on {@link #actionPerformed(ActionEvent)}
 * is delegated to an abstract method {@link #execute()}.
 */
public abstract class SimulatorAction extends AbstractAction implements
        Refreshable {
    /**
     * Creates an initially disabled action for a given simulator,
     * and with a given name and (possibly {@code null}) icon.
     * The action adds itself to the refreshables of the simulator.
     */
    public SimulatorAction(Simulator simulator, String name, Icon icon) {
        super(name, icon);
        this.simulator = simulator;
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
        simulator.getActions().addRefreshable(this);
    }

    /** The simulator on which this action works. */
    protected final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    protected final SimulatorModel getModel() {
        return this.simulator.getModel();
    }

    /** Convenience method to retrieve the simulator action store. */
    protected final ActionStore getActions() {
        return this.simulator.getActions();
    }

    /** Convenience method to retrieve the simulator model. */
    protected final JFrame getFrame() {
        return this.simulator.getFrame();
    }

    /** Convenience method to retrieve the simulator model. */
    protected final SimulatorPanel getPanel() {
        return this.simulator.getSimulatorPanel();
    }

    /** Returns the control panel that owns the action. */
    final protected ControlPanel getControlPanel() {
        return this.simulator.getControlPanel();
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }

    /** Delegates to {@link #execute()}. */
    @Override
    public void actionPerformed(ActionEvent e) {
        execute();
    }

    /**
     * Method to execute the action encapsulated by this class.
     * Called from {@link #actionPerformed(ActionEvent)}.
     * @return {@code true} if the grammar was invalidated as a result of
     * this action, so that the simulation has to be restarted. 
     */
    public abstract boolean execute();

    /**
     * Enters a dialog that results in a control name that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing names
     * @return a control name not occurring in the current grammar, or
     *         <code>null</code>
     */
    final protected String askNewControlName(String title, String name,
            boolean mustBeFresh) {
        Set<String> existingNames = getModel().getGrammar().getControlNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Enters a dialog that results in a graph name that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing graph names
     * @return a graph name not occurring in the current grammar, or
     *         <code>null</code>
     */
    final protected String askNewGraphName(String title, String name,
            boolean mustBeFresh) {
        Set<String> existingNames = getModel().getGrammar().getGraphNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Enters a dialog that results in a name label that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing rule names
     * @return a rule name not occurring in the current grammar, or
     *         <code>null</code>
     */
    final protected String askNewRuleName(String title, String name,
            boolean mustBeFresh) {
        FreshNameDialog<String> ruleNameDialog =
            new FreshNameDialog<String>(getModel().getGrammar().getRuleNames(),
                name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        ruleNameDialog.showDialog(getFrame(), title);
        return ruleNameDialog.getName();
    }

    /**
     * Enters a dialog that asks for a label to be renamed, and its the
     * replacement.
     * @return A pair consisting of the label to be replaced and its
     *         replacement, neither of which can be <code>null</code>; or
     *         <code>null</code> if the dialog was cancelled.
     */
    final protected Duo<TypeLabel> askRelabelling(TypeLabel oldLabel) {
        RelabelDialog dialog =
            new RelabelDialog(getModel().getGrammar().getLabelStore(), oldLabel);
        if (dialog.showDialog(getFrame(), null)) {
            return new Duo<TypeLabel>(dialog.getOldLabel(),
                dialog.getNewLabel());
        } else {
            return null;
        }
    }

    /**
     * Enters a dialog that results in a type graph that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing names
     * @return a type graph not occurring in the current grammar, or
     *         <code>null</code>
     */
    final protected String askNewTypeName(String title, String name,
            boolean mustBeFresh) {
        Set<String> existingNames = getModel().getGrammar().getTypeNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Creates and shows an {@link ErrorDialog} for a given message and
     * exception.
     */
    final protected void showErrorDialog(Throwable exc, String message,
            Object... args) {
        new ErrorDialog(getFrame(), String.format(message, args), exc).setVisible(true);
    }

    /**
     * If a simulation is active, asks through a dialog whether it may be
     * abandoned.
     * @return <tt>true</tt> if the current grammar may be abandoned
     */
    final protected boolean confirmAbandon() {
        boolean result;
        if (getModel().getGts() != null) {
            result = confirmBehaviourOption(STOP_SIMULATION_OPTION);
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Checks if a given option is confirmed.
     */
    final protected boolean confirmBehaviourOption(String option) {
        return confirmBehaviour(option, null);
    }

    /**
     * Checks if a given option is confirmed. The question can be set
     * explicitly.
     */
    final protected boolean confirmBehaviour(String option, String question) {
        BehaviourOption menu =
            (BehaviourOption) getSimulator().getOptions().getItem(option);
        return menu.confirm(getFrame(), question);
    }

    /**
     * Asks whether a given existing rule should be replaced by a newly loaded
     * one.
     */
    final protected boolean confirmOverwriteRule(String ruleName) {
        int response =
            JOptionPane.showConfirmDialog(getFrame(),
                String.format("Replace existing rule '%s'?", ruleName), null,
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether a given existing type graph should be replaced by a newly
     * loaded one.
     */
    final protected boolean confirmOverwriteType(String typeName) {
        int response =
            JOptionPane.showConfirmDialog(getFrame(),
                String.format("Replace existing type graph '%s'?", typeName),
                null, JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether a given existing control program should be replaced by a 
     * newly loaded one.
     */
    final protected boolean confirmOverwriteControl(String controlName) {
        int response =
            JOptionPane.showConfirmDialog(getFrame(), String.format(
                "Replace existing control program '%s'?", controlName), null,
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether a given existing host graph should be replaced by a newly
     * loaded one.
     */
    final protected boolean confirmOverwriteGraph(String graphName) {
        int response =
            JOptionPane.showConfirmDialog(getFrame(),
                String.format("Replace existing host graph '%s'?", graphName),
                null, JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether a given existing file should be overwritten by a new
     * grammar.
     */
    final protected boolean confirmOverwriteGrammar(File grammarFile) {
        if (grammarFile.exists()) {
            int response =
                JOptionPane.showConfirmDialog(getFrame(),
                    "Overwrite existing grammar?", null,
                    JOptionPane.OK_CANCEL_OPTION);
            return response == JOptionPane.OK_OPTION;
        } else {
            return true;
        }
    }

    /**
     * Returns the file chooser for rule (GPR) files, lazily creating it first.
     */
    final protected JFileChooser getRuleFileChooser() {
        return GrooveFileChooser.getFileChooser(FileType.RULE_FILTER);
    }

    /**
     * Returns the file chooser for state (GST or GXL) files, lazily creating it
     * first.
     */
    final protected JFileChooser getStateFileChooser() {
        return GrooveFileChooser.getFileChooser(FileType.HOSTS_FILTER);
    }

    /**
     * Returns the file chooser for grammar (GPS) files, lazily creating it
     * first.
     */
    final protected JFileChooser getGrammarFileChooser() {
        return getGrammarFileChooser(false);
    }

    /**
     * Returns the file chooser for grammar (GPS) files, lazily creating it
     * first.
     * @param includeArchives flag to indicate if archive (ZIP and JAR) files
     * should also be recognised by the chooser
     */
    final protected JFileChooser getGrammarFileChooser(boolean includeArchives) {
        if (includeArchives) {
            return GrooveFileChooser.getFileChooser(FileType.GRAMMARS_FILTER);
        } else {
            return GrooveFileChooser.getFileChooser(FileType.GRAMMAR_FILTER);
        }
    }

    /**
     * Returns the last file from which a grammar was loaded.
     */
    final protected File getLastGrammarFile() {
        File result = null;
        SystemStore store = getModel().getStore();
        Object location = store == null ? null : store.getLocation();
        if (location instanceof File) {
            result = (File) location;
        } else if (location instanceof URL) {
            result = Groove.toFile((URL) location);
        }
        return result;
    }

    /** The simulator on which this action works. */
    private final Simulator simulator;
}