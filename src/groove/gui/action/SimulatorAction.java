package groove.gui.action;

import static groove.gui.Options.STOP_SIMULATION_OPTION;
import groove.graph.Graph;
import groove.graph.TypeLabel;
import groove.gui.BehaviourOption;
import groove.gui.ControlDisplay;
import groove.gui.DisplaysPanel;
import groove.gui.EditType;
import groove.gui.HostDisplay;
import groove.gui.Icons;
import groove.gui.LTSDisplay;
import groove.gui.Options;
import groove.gui.PrologDisplay;
import groove.gui.Refreshable;
import groove.gui.ResourceDisplay;
import groove.gui.RuleDisplay;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.TypeDisplay;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.FreshNameDialog;
import groove.gui.dialog.RelabelDialog;
import groove.gui.dialog.SaveDialog;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.store.SystemStore;
import groove.trans.ResourceKind;
import groove.trans.RuleName;
import groove.util.Duo;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
     * Internal constructor to set all fields.
     */
    protected SimulatorAction(Simulator simulator, String name, Icon icon,
            EditType edit, ResourceKind resource) {
        super(name, icon);
        this.simulator = simulator;
        this.resource = resource;
        this.edit = edit;
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
        if (simulator != null) {
            simulator.getActions().addRefreshable(this);
        }
    }

    /**
     * Creates an initially disabled action for a given simulator,
     * and with a given name and (possibly {@code null}) icon.
     * The action adds itself to the refreshables of the simulator.
     */
    protected SimulatorAction(Simulator simulator, String name, Icon icon) {
        this(simulator, name, icon, null, null);
    }

    /**
     * Creates an initially disabled edit action for a given simulator.
     * The edit type and edited resource automatically generate the name and icon.
     * The action adds itself to the refreshables of the simulator.
     */
    protected SimulatorAction(Simulator simulator, EditType edit,
            ResourceKind resource) {
        this(simulator, Options.getEditActionName(edit, resource, false),
            Icons.getEditIcon(edit, resource), edit, resource);
    }

    /** Returns the edit name for this action, if it is an edit action. */
    protected String getEditActionName() {
        if (getEditType() == null) {
            return null;
        } else {
            return Options.getEditActionName(getEditType(), getResourceKind(),
                false);
        }
    }

    /** The simulator on which this action works. */
    protected final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    protected final SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the grammar model from the simulator model. */
    protected final GrammarModel getGrammarModel() {
        return getSimulatorModel().getGrammar();
    }

    /** Convenience method to retrieve the grammar store from the simulator model. */
    protected final SystemStore getGrammarStore() {
        return getSimulatorModel().getStore();
    }

    /** Convenience method to retrieve the simulator action store. */
    protected final ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** Convenience method to retrieve the frame of the simulator. */
    protected final JFrame getFrame() {
        return getSimulator().getFrame();
    }

    /** Convenience method to retrieve the main simulator panel. */
    protected final DisplaysPanel getDisplaysPanel() {
        return getSimulator().getSimulatorPanel();
    }

    /** 
     * Returns the simulator display for the resource kind of this action.
     * @throws IllegalStateException if there is no resource kind
     */
    protected final ResourceDisplay getDisplay() {
        if (getResourceKind() == null) {
            throw new IllegalStateException();
        }
        switch (getResourceKind()) {
        case CONTROL:
            return getControlDisplay();
        case HOST:
            return getStateDisplay();
        case PROLOG:
            return getPrologDisplay();
        case RULE:
            return getRuleDisplay();
        case TYPE:
            return getTypeDisplay();
        case PROPERTIES:
        default:
            assert false;
            return null;
        }
    }

    /** Convenience method to retrieve the state panel of the simulator. */
    protected final HostDisplay getStateDisplay() {
        return getDisplaysPanel().getStateDisplay();
    }

    /** Convenience method to retrieve the rule panel of the simulator */
    protected final RuleDisplay getRuleDisplay() {
        return getDisplaysPanel().getRuleDisplay();
    }

    /** Convenience method to retrieve the type panel of the simulator. */
    protected final TypeDisplay getTypeDisplay() {
        return getDisplaysPanel().getTypeDisplay();
    }

    /** Returns the control panel that owns the action. */
    final protected ControlDisplay getControlDisplay() {
        return getSimulator().getControlDisplay();
    }

    /** Returns the prolog panel that owns the action. */
    final protected PrologDisplay getPrologDisplay() {
        return getSimulator().getPrologDisplay();
    }

    /** Returns the prolog panel that owns the action. */
    final protected LTSDisplay getLtsDisplay() {
        return getSimulator().getLtsDisplay();
    }

    /** Returns the (possibly {@code null}) edit type of this action.*/
    final protected EditType getEditType() {
        return this.edit;
    }

    /** Returns the (possibly {@code null}) grammar resource being edited by this action.*/
    final protected ResourceKind getResourceKind() {
        return this.resource;
    }

    /** Disposes the action by unregistering it as a listener. */
    public void dispose() {
        getActions().removeRefreshable(this);
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
     * Enters a dialog that results in a name that is not in a set of
     * current names, or <code>null</code> if the dialog was cancelled.
     * @param kind kind of resource for which we want a name
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing names
     * @return a type graph not occurring in the current grammar, or
     *         <code>null</code>
     */
    final protected String askNewName(ResourceKind kind, String name,
            boolean mustBeFresh) {
        String title =
            String.format("Select %s%s name", mustBeFresh ? "new " : "",
                kind.getDescription());
        Set<String> existingNames =
            getSimulatorModel().getGrammar().getNames(kind);
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
     * Invokes a file chooser of the right type to save a given aspect graph,
     * and returns the chosen (possibly {@code null}) file.
     */
    final protected File askSaveGraph(Graph<?,?> graph) {
        ExtensionFilter filter = FileType.getFilter(graph.getRole());
        String name = graph.getName();
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(name));
        return SaveDialog.show(chooser, getFrame(), null);
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
            new RelabelDialog(getSimulatorModel().getGrammar().getLabelStore(),
                oldLabel);
        if (dialog.showDialog(getFrame(), null)) {
            return new Duo<TypeLabel>(dialog.getOldLabel(),
                dialog.getNewLabel());
        } else {
            return null;
        }
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
    final protected boolean confirmStopSimulation() {
        boolean result;
        if (getSimulatorModel().getGts() != null
            && getSimulatorModel().getGts().size() > 1) {
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
     * Asks whether a given existing resource, of a the kind of this action,
     * should be replaced by a newly loaded one.
     */
    final protected boolean confirmOverwrite(String name) {
        return confirmOverwrite(getResourceKind(), name);
    }

    /**
     * Asks whether a given existing resource, of a given kind,
     * should be replaced by a newly loaded one.
     */
    final protected boolean confirmOverwrite(ResourceKind resource, String name) {
        int response =
            JOptionPane.showConfirmDialog(
                getFrame(),
                String.format("Replace existing %s '%s'?",
                    resource.getDescription(), name), null,
                JOptionPane.OK_CANCEL_OPTION);
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
     * Return a file chooser for prolog files
     */
    final protected JFileChooser getPrologFileChooser() {
        return GrooveFileChooser.getFileChooser(FileType.PROLOG_FILTER);
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
        SystemStore store = getSimulatorModel().getStore();
        Object location = store == null ? null : store.getLocation();
        if (location instanceof File) {
            result = (File) location;
        } else if (location instanceof URL) {
            result = Groove.toFile((URL) location);
        }
        return result;
    }

    /** Constructs a grammar element name from a file, if it is within the grammar location.
     * The element name is relative to the grammar location.
     * A flag controls if the name should be treated as a rule name, i.e.,
     * divided into fragments standing for subdirectories.
     * @param selectedPath the canonical file name of the grammar element, without extension
     * @param structured if the file name is to be interpreted as a structured name
     * @throws IOException if the name is not well-formed
     */
    final protected String getNameInGrammar(String selectedPath,
            boolean structured) throws IOException {
        String name = null;
        Object location = getSimulatorModel().getStore().getLocation();
        if (location instanceof File) {
            String grammarPath = ((File) location).getCanonicalPath();
            if (selectedPath.startsWith(grammarPath)) {
                String diff = selectedPath.substring(grammarPath.length());
                File pathDiff = new File(diff);
                List<String> pathFragments = new ArrayList<String>();
                while (pathDiff.getName().length() > 0) {
                    pathFragments.add(pathDiff.getName());
                    pathDiff = pathDiff.getParentFile();
                }
                assert !pathFragments.isEmpty();
                int i = pathFragments.size() - 1;
                if (structured) {
                    RuleName ruleName = new RuleName(pathFragments.get(i));
                    for (i--; i >= 0; i--) {
                        try {
                            ruleName =
                                new RuleName(ruleName, pathFragments.get(i));
                        } catch (FormatException e) {
                            throw new IOException("Malformed rule name " + diff);
                        }
                    }
                    name = ruleName.toString();
                } else if (pathFragments.size() > 1) {
                    throw new IOException(
                        "Can't save to a grammar subdirectory");
                } else {
                    name = pathFragments.get(0);
                }
            }
        }
        return name;
    }

    /** The simulator on which this action works. */
    private final Simulator simulator;
    /** Possibly {@code null} edit type of this action. */
    private final EditType edit;
    /** Possibly {@code null} resource being edited by this action. */
    private final ResourceKind resource;
}