package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/** Action to create and load a new, initially empty graph grammar. */
public class NewGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public NewGrammarAction(Simulator simulator) {
        super(simulator, Options.NEW_GRAMMAR_ACTION_NAME, null);
    }

    @Override
    public void execute() {
        File grammarFile = getLastGrammarFile();
        File newGrammar;
        if (grammarFile == null) {
            newGrammar = new File(Simulator.NEW_GRAMMAR_NAME);
        } else {
            newGrammar =
                new File(grammarFile.getParentFile(),
                    Simulator.NEW_GRAMMAR_NAME);
        }
        JFileChooser fileChooser = getGrammarFileChooser(false);
        fileChooser.setSelectedFile(newGrammar);
        boolean ok = false;
        while (!ok) {
            if (fileChooser.showDialog(getFrame(), "New") == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.exists()) {
                    int response =
                        JOptionPane.showConfirmDialog(getFrame(),
                            String.format("Load existing grammar %s?",
                                selectedFile.getName()));
                    if (response == JOptionPane.OK_OPTION) {
                        try {
                            getActions().getLoadGrammarAction().load(
                                selectedFile);
                        } catch (IOException exc) {
                            showErrorDialog(exc, exc.getMessage());
                        }
                    }
                    ok = response != JOptionPane.NO_OPTION;
                } else if (getDisplaysPanel().saveAllEditors(true)) {
                    try {
                        getSimulatorModel().doNewGrammar(selectedFile);
                    } catch (IOException exc) {
                        showErrorDialog(exc,
                            String.format(
                                "Error while creating grammar at '%s'",
                                grammarFile));
                    }
                    ok = true;
                }
            } else {
                ok = true;
            }
        }

    }

    @Override
    public void refresh() {
        setEnabled(true);
    }
}