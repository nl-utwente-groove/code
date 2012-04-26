package groove.gui.action;

import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.VersionDialog;
import groove.io.store.DefaultArchiveSystemStore;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.trans.ResourceKind;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Version;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Action for loading a new rule system.
 */
public class LoadGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public LoadGrammarAction(Simulator simulator) {
        super(simulator, Options.LOAD_GRAMMAR_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.OPEN_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = getGrammarFileChooser(true);
        int approve = fileChooser.showOpenDialog(getFrame());
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                showErrorDialog(null, "No file selected");
            } else {
                try {
                    load(selectedFile);
                } catch (IOException exc) {
                    String msg = exc.getMessage();
                    if (msg.endsWith(DefaultArchiveSystemStore.NO_JAR_OR_ZIP_SUFFIX)) {
                        // Can only happen if trying to open a directory.
                        // Don't throw exception, open selector dialog again.
                        execute();
                        return;
                    }
                    showErrorDialog(exc, exc.getMessage());
                }
            }
        }
    }

    /**
     * Loads in a grammar from a given file.
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the load action failed
     */
    public boolean load(File grammarFile) throws IOException {
        boolean result = false;
        // Load the grammar.
        final SystemStore store =
            SystemStoreFactory.newStore(grammarFile, false);
        result = load(store);
        // now we know loading succeeded, we can set the current
        // names & files
        getGrammarFileChooser().setSelectedFile(grammarFile);
        getRuleFileChooser().setCurrentDirectory(grammarFile);
        // MdM - TODO - dont know what to do with the code below....
        //        if (startGraphName != null) {
        //            File startFile = new File(grammarFile, startGraphName);
        //            getStateFileChooser().setSelectedFile(startFile);
        //        } else {
        // make sure the selected file from an old grammar is
        // unselected
        getStateFileChooser().setSelectedFile(null);
        // make sure the dialog for open state opens at the
        // grammar location
        getStateFileChooser().setCurrentDirectory(grammarFile);
        //        }
        return result;
    }

    /**
     * Loads in a given system store.
     */
    public boolean load(final SystemStore store) throws IOException {
        if (!getDisplaysPanel().saveAllEditors(true)) {
            return false;
        }

        // First we check if the versions are compatible.
        store.reload();
        SystemProperties props = store.getProperties();
        if (store.isEmpty()) {
            showErrorDialog(null, store.getLocation()
                + " is not a GROOVE production system.");
            return false;
        }
        String fileGrammarVersion = props.getGrammarVersion();
        int compare = Version.compareGrammarVersion(fileGrammarVersion);
        final boolean saveAfterLoading = (compare != 0);
        final File newGrammarFile;
        if (compare < 0) {
            // Trying to load a newer grammar.
            if (!VersionDialog.showNew(this.getFrame(), props)) {
                return false;
            }
            newGrammarFile = null;
        } else if (compare > 0 && store.getLocation() instanceof File) {
            // Trying to load an older grammar from a file.
            File grammarFile = (File) store.getLocation();
            switch (VersionDialog.showOldFile(this.getFrame(), props)) {
            case 0: // save and overwrite
                newGrammarFile = grammarFile;
                break;
            case 1: // save under different name
                newGrammarFile = selectSaveAs(grammarFile);
                if (newGrammarFile == null) {
                    return false;
                }
                break;
            default: // cancel
                return false;
            }
        } else if (compare > 0) {
            // Trying to load an older grammar from a URL.
            if (!VersionDialog.showOldURL(this.getFrame(), props)) {
                return false;
            }
            newGrammarFile = selectSaveAs(null);
            if (newGrammarFile == null) {
                return false;
            }
        } else {
            // Loading an up-to-date grammar.
            newGrammarFile = null;
        }
        // store.reload(); - MdM - moved to version check code
        if (Version.compareGrammarVersions(fileGrammarVersion,
            Version.GRAMMAR_VERSION_3_1) == -1) {
            boolean success = makeIdentifiersValid(store);
            if (!success) {
                return false;
            }
        }
        final GrammarModel grammar = store.toGrammarModel();
        getSimulatorModel().setGrammar(grammar);
        grammar.getProperties().setCurrentVersionProperties();
        if (saveAfterLoading && newGrammarFile != null) {
            getActions().getSaveGrammarAction().save(newGrammarFile,
                !newGrammarFile.equals(store.getLocation()));
        }
        return true;
    }

    /** 
     * Helper method for doLoadGrammar. Asks the user to select a new name for
     * saving the grammar after it has been loaded (and converted).
     */
    private File selectSaveAs(File oldGrammarFile) {
        if (oldGrammarFile != null) {
            getGrammarFileChooser().getSelectedFile();
            getGrammarFileChooser().setSelectedFile(oldGrammarFile);
        }
        int result = getGrammarFileChooser().showSaveDialog(getFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File selected = getGrammarFileChooser().getSelectedFile();
        if (selected.exists()) {
            if (confirmOverwriteGrammar(selected)) {
                return selected;
            } else {
                return selectSaveAs(oldGrammarFile);
            }
        } else {
            return selected;
        }
    }

    /**
     * Changes all the resource names in the store that do not conform to the
     * restrictions imposed by GrammarVersion 3.1. The changed names are
     * hashed (but legal) versions of the old names.
     * The user must confirm the rename if there are names to be changed.
     */
    private boolean makeIdentifiersValid(SystemStore store) throws IOException {
        boolean confirmed = false;
        String[] options = {"Continue", "Abort"};
        // loop over all resource kinds
        for (ResourceKind kind : ResourceKind.all(false)) {
            Set<String> modifiedNames = new HashSet<String>();
            // collect all resource names of this kind
            Set<String> oldNames = new HashSet<String>();
            if (kind.isGraphBased()) {
                oldNames.addAll(store.getGraphs(kind).keySet());
            } else {
                oldNames.addAll(store.getTexts(kind).keySet());
            }
            // loop over all collected names
            for (String name : oldNames) {
                StringBuilder legal = new StringBuilder();
                // check if name is valid
                if (!RuleName.isValid(name, legal, null)) {
                    // if not, ask confirmation from the user to continue
                    if (!confirmed
                        && JOptionPane.showOptionDialog(
                            getFrame(),
                            "Warning: the grammar contains resources with "
                                + "invalid (since grammar version 3.1) names.\n"
                                + "These will be renamed automatically.",
                            "Warning: invalid identifiers",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, options,
                            "Continue") != JOptionPane.OK_OPTION) {
                        return false;
                    }
                    // user confirmed, rename resource
                    confirmed = true;
                    String newName = legal.toString();
                    // make sure the modified name is fresh
                    while (oldNames.contains(newName)
                        || modifiedNames.contains(newName)) {
                        newName = newName + "_";
                    }
                    modifiedNames.add(newName);
                    // store old name in rule properties, if possible
                    if (kind == ResourceKind.RULE) {
                        AspectGraph rule = store.getGraphs(kind).get(name);
                        GraphProperties properties =
                            GraphInfo.getProperties(rule, true);
                        if (properties != null
                            && properties.getTransitionLabel() == null) {
                            properties.setTransitionLabel(name);
                        }
                    }
                    // do the actual rename 
                    store.rename(kind, name, newName);
                }
            }
        }
        return true;
    }
}