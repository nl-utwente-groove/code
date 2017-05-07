package groove.gui.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import groove.grammar.GrammarKey;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.GraphInfo;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.VersionDialog;
import groove.io.store.SystemStore;
import groove.util.ThreeValued;
import groove.util.Version;

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
        final SystemStore store = SystemStore.newStore(grammarFile, false);
        result = load(store);
        // now we know loading succeeded, we can set the current
        // names & files
        getGrammarFileChooser().setSelectedFile(grammarFile);
        getRuleFileChooser().setCurrentDirectory(grammarFile);
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
        GrammarProperties props = store.getProperties();
        if (store.isEmpty()) {
            showErrorDialog(null, store.getLocation() + " is not a GROOVE production system.");
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
        } else if (compare > 0) {
            // Trying to load an older grammar from a file.
            File grammarFile = store.getLocation();
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
        if (Version.compareGrammarVersions(fileGrammarVersion, Version.GRAMMAR_VERSION_3_1) == -1) {
            boolean success = repairIdentifiers(store);
            if (!success) {
                return false;
            }
        }
        if (Version.compareGrammarVersions(fileGrammarVersion, Version.GRAMMAR_VERSION_3_4) == -1) {
            if (!repairGrammarProperties(store)) {
                return false;
            }
        }
        final GrammarModel grammar = store.toGrammarModel();
        getSimulatorModel().setGrammar(grammar);
        grammar.getProperties()
            .setCurrentVersionProperties();
        if (saveAfterLoading && newGrammarFile != null) {
            getActions().getSaveGrammarAction()
                .save(newGrammarFile, !newGrammarFile.equals(store.getLocation()));
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
    private boolean repairIdentifiers(SystemStore store) throws IOException {
        boolean result = true;
        boolean confirmed = false;
        store.setUndoSuspended(true);
        // loop over all resource kinds
        outer: for (ResourceKind kind : ResourceKind.all(false)) {
            Set<QualName> newNames = new HashSet<>();
            // collect all resource names of this kind
            Set<QualName> oldNames =
                (kind.isGraphBased() ? store.getGraphs(kind) : store.getTexts(kind)).keySet();
            // loop over all collected names,
            // construct a renaming of illegal qualified names into safe names
            Map<QualName,QualName> renameMap = new HashMap<>();
            for (QualName name : oldNames) {
                // check if name is valid
                if (name.hasErrors()) {
                    // if not, ask confirmation from the user to continue
                    if (!confirmed && !askReplaceNames()) {
                        result = false;
                        break outer;
                    }
                    // user confirmed, rename resource
                    confirmed = true;
                    QualName newName = name.toValidName();
                    // make sure the modified name is fresh
                    while (oldNames.contains(newName) || newNames.contains(newName)) {
                        newName = newName.parent()
                            .extend(newName.last() + "_");
                    }
                    newNames.add(newName);
                    renameMap.put(name, newName);
                }
            }
            if (!renameMap.isEmpty()) {
                if (kind.isGraphBased()) {
                    replaceGraphs(store, kind, renameMap);
                } else {
                    replaceTexts(store, kind, renameMap);
                }
            }
        }
        store.setUndoSuspended(false);
        return result;
    }

    private void replaceGraphs(SystemStore store, ResourceKind kind,
        Map<QualName,QualName> renameMap) throws IOException {
        Map<QualName,AspectGraph> oldGraphMap = store.getGraphs(kind);
        List<AspectGraph> newGraphs = new ArrayList<>();
        for (Map.Entry<QualName,QualName> e : renameMap.entrySet()) {
            QualName oldName = e.getKey();
            QualName newName = e.getValue();
            AspectGraph oldGraph = oldGraphMap.get(oldName);
            AspectGraph newGraph = oldGraph.clone();
            newGraph.setName(newName.toString());
            if (kind == ResourceKind.RULE) {
                // store old name as transition label
                // if there was no explicit transition label
                // so the name change does not affect the LTS
                if (GraphInfo.getTransitionLabel(newGraph) == null) {
                    GraphInfo.setTransitionLabel(newGraph, oldName.toString());
                }
            }
            newGraph.setFixed();
            newGraphs.add(newGraph);
        }
        store.deleteGraphs(kind, renameMap.keySet());
        store.putGraphs(kind, newGraphs, false);
    }

    private void replaceTexts(SystemStore store, ResourceKind kind,
        Map<QualName,QualName> renameMap) throws IOException {
        Map<QualName,String> oldTextMap = store.getTexts(kind);
        Map<QualName,String> newTextMap = new HashMap<>();
        for (Map.Entry<QualName,QualName> e : renameMap.entrySet()) {
            QualName oldName = e.getKey();
            QualName newName = e.getValue();
            String text = oldTextMap.get(oldName);
            newTextMap.put(newName, text);
        }
        store.deleteTexts(kind, renameMap.keySet());
        store.putTexts(kind, newTextMap);
    }

    private boolean askReplaceNames() {
        String[] options = {"Continue", "Abort"};
        return JOptionPane.showOptionDialog(getFrame(),
            "Warning: the grammar contains resources with "
                + "invalid (since grammar version 3.1) names.\n"
                + "These will be renamed automatically.",
            "Warning: invalid identifiers",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            "Continue") == JOptionPane.OK_OPTION;
    }

    /**
     * Changes the value of certain grammar properties to conform to syntax
     * introduced in grammar version 3.4, and removes grammar
     * properties that are no longer supported.
     */
    private boolean repairGrammarProperties(SystemStore store) throws IOException {
        boolean changed = false;
        GrammarProperties props = store.getProperties();
        changed |= props.remove(GrammarKey.ATTRIBUTE_SUPPORT) != null;
        changed |= props.remove(GrammarKey.TRANSITION_BRACKETS) != null;
        // convert numeric value of TRANSITION_PARAMETERS
        GrammarKey paramsKey = GrammarKey.TRANSITION_PARAMETERS;
        String paramsVal = (String) props.get(paramsKey.getName());
        if (paramsVal != null && !paramsKey.parser()
            .accepts(paramsVal)) {
            try {
                int paramsIntVal = Integer.parseInt(paramsVal);
                props.setUseParameters(paramsIntVal == 0 ? ThreeValued.FALSE : ThreeValued.TRUE);
            } catch (NumberFormatException exc) {
                // it was not a number either; remove the key altogether
                props.remove(paramsKey.getName());
            }
            changed = true;
        }
        if (changed) {
            store.setUndoSuspended(true);
            store.putProperties(props);
            store.setUndoSuspended(false);
        }
        return true;
    }
}