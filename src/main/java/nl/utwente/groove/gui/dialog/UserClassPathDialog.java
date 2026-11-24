/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nl.utwente.groove.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.FileType;

/**
 * Modal dialog for editing the installation-level user class path
 * (see {@link nl.utwente.groove.util.UserClassPath}): an ordered list of
 * directories and jar files.
 * @author Arend Rensink
 */
@AIGenerated("Claude Opus 5, 2026-10")
@NonNullByDefault
public class UserClassPathDialog {
    /** Creates a dialog showing a given initial list of entries. */
    public UserClassPathDialog(List<Path> entries) {
        for (var entry : entries) {
            this.listModel.addElement(entry);
        }
        this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.list.setVisibleRowCount(8);
        this.list.addListSelectionListener(e -> refreshButtons());
        this.addButton.addActionListener(e -> doAdd());
        this.removeButton.addActionListener(e -> doRemove());
        this.upButton.addActionListener(e -> doMove(-1));
        this.downButton.addActionListener(e -> doMove(+1));
        this.okButton.addActionListener(e -> close(this.okButton));
        this.cancelButton.addActionListener(e -> close(this.cancelButton));
        this.optionPane = new JOptionPane(createMainPanel(), JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, new Object[] {this.okButton, this.cancelButton});
        refreshButtons();
    }

    /**
     * Makes the dialog visible and awaits the user's response.
     * @param parent the component on which the dialog is shown
     * @return the edited list of entries if the user pressed OK,
     * or {@code null} if the dialog was cancelled
     */
    public @Nullable List<Path> showDialog(@Nullable Component parent) {
        var dialog = this.optionPane.createDialog(parent, TITLE);
        this.dialog = dialog;
        dialog.setResizable(true);
        dialog.setVisible(true);
        dialog.dispose();
        this.dialog = null;
        if (this.optionPane.getValue() != this.okButton) {
            return null;
        }
        var result = new ArrayList<Path>();
        for (int i = 0; i < this.listModel.size(); i++) {
            result.add(this.listModel.get(i));
        }
        return result;
    }

    /** Creates the panel with the explanation, the entry list and the editing buttons. */
    private JPanel createMainPanel() {
        var buttons = new JPanel(new GridLayout(0, 1, 0, 5));
        buttons.add(this.addButton);
        buttons.add(this.removeButton);
        buttons.add(this.upButton);
        buttons.add(this.downButton);
        var east = new JPanel(new BorderLayout());
        east.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        east.add(buttons, BorderLayout.NORTH);
        var scroller = new JScrollPane(this.list);
        scroller.setPreferredSize(new Dimension(450, 180));
        var label = new JLabel("<html><body style='width: 400px'>" + EXPLANATION);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        var result = new JPanel(new BorderLayout());
        result.add(label, BorderLayout.NORTH);
        result.add(scroller, BorderLayout.CENTER);
        result.add(east, BorderLayout.EAST);
        return result;
    }

    /** Lets the user choose directories or jar files, and adds them after the selection. */
    private void doAdd() {
        var chooser = GrooveFileChooser.getInstance(EnumSet.of(FileType.JAR), true);
        chooser.setDialogTitle("Add to user class path");
        if (chooser.showOpenDialog(this.dialog) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File[] files = chooser.getSelectedFiles();
        if (files.length == 0) {
            var file = chooser.getSelectedFile();
            files = file == null
                ? new File[0]
                : new File[] {file};
        }
        int index = this.list.getMaxSelectionIndex() < 0
            ? this.listModel.size()
            : this.list.getMaxSelectionIndex() + 1;
        int first = index;
        for (var file : files) {
            var path = file.toPath().toAbsolutePath().normalize();
            if (!this.listModel.contains(path)) {
                this.listModel.add(index, path);
                index++;
            }
        }
        if (index > first) {
            this.list.setSelectionInterval(first, index - 1);
        }
    }

    /** Removes the selected entries. */
    private void doRemove() {
        int[] selected = this.list.getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            this.listModel.remove(selected[i]);
        }
    }

    /** Moves the selected entries one position up (negative delta) or down (positive delta). */
    private void doMove(int delta) {
        int[] selected = this.list.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        int min = selected[0];
        int max = selected[selected.length - 1];
        if (min + delta < 0 || max + delta >= this.listModel.size()) {
            return;
        }
        if (delta < 0) {
            for (int i : selected) {
                swap(i, i + delta);
            }
        } else {
            for (int k = selected.length - 1; k >= 0; k--) {
                swap(selected[k], selected[k] + delta);
            }
        }
        for (int k = 0; k < selected.length; k++) {
            selected[k] += delta;
        }
        this.list.setSelectedIndices(selected);
        this.list.ensureIndexIsVisible(delta < 0
            ? min + delta
            : max + delta);
    }

    /** Swaps the list entries at two indices. */
    private void swap(int i, int j) {
        var entry = this.listModel.get(i);
        this.listModel.set(i, this.listModel.get(j));
        this.listModel.set(j, entry);
    }

    /** Enables the selection-dependent buttons according to the current selection. */
    private void refreshButtons() {
        int[] selected = this.list.getSelectedIndices();
        boolean any = selected.length > 0;
        this.removeButton.setEnabled(any);
        this.upButton.setEnabled(any && selected[0] > 0);
        this.downButton.setEnabled(any && selected[selected.length - 1] < this.listModel.size() - 1);
    }

    /** Closes the dialog, recording the button that closed it. */
    private void close(JButton source) {
        this.optionPane.setValue(source);
        this.optionPane.setVisible(false);
    }

    private final DefaultListModel<Path> listModel = new DefaultListModel<>();
    private final JList<Path> list = new JList<>(this.listModel);
    private final JButton addButton = new JButton("Add...");
    private final JButton removeButton = new JButton("Remove");
    private final JButton upButton = new JButton("Up");
    private final JButton downButton = new JButton("Down");
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    private final JOptionPane optionPane;
    /** The currently shown dialog, if any; parent of the file chooser. */
    private @Nullable JDialog dialog;

    /** Title of the dialog. */
    private static final String TITLE = "User class path";
    /** Explanatory text shown above the entry list. */
    private static final String EXPLANATION
        = "Directories and jars from which the classes named in a grammar's userOperations property are loaded, "
            + "after the grammar's own lib/ and classes/ directories and its userClassPath property.";
}
