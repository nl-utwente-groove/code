/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.io.conceptual.configuration;

import groove.io.FileType;
import groove.io.GrooveFileChooser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jaxfront.core.error.UserError;
import com.jaxfront.core.type.Type;
import com.jaxfront.swing.ui.visualizers.AbstractSimpleTypeView;

/**
 * View component for file names.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MyFileDialog extends AbstractSimpleTypeView {
    /**
     * Constructs a new dialog, for a given JAXFront type.
     */
    public MyFileDialog(Type context) {
        super(context.getParent());
        setLayout(new FlowLayout());
        setModel(context);
    }

    @Override
    protected JComponent createEditorComponent() {
        return getEditorPanel();
    }

    @Override
    public String getText() {
        return getResultField().getText();
    }

    @Override
    public void populateView() {
        setEditorField(getResultField());
    }

    private JPanel getEditorPanel() {
        if (this.editorPanel == null) {
            this.editorPanel = new JPanel(new BorderLayout());
            this.editorPanel.add(getResultField());
            this.editorPanel.add(getChooserButton(), BorderLayout.EAST);
        }
        return this.editorPanel;
    }

    private JPanel editorPanel;

    private JButton getChooserButton() {
        if (this.chooserButton == null) {
            this.chooserButton = new JButton();
            this.chooserButton.setAction(createBrowseAction());
        }
        return this.chooserButton;
    }

    private JButton chooserButton;

    private JTextField getResultField() {
        if (this.resultField == null) {
            this.resultField = new JTextField();
            this.resultField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    notifyTextChanged();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    notifyTextChanged();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    notifyTextChanged();
                }
            });
            this.resultField.setText(getModel().getParent()
                .getDirectChildValue(getModel().getName()));
        }
        return this.resultField;
    }

    private JTextField resultField;

    /** Callback method in case the text in the textfield has changed.
     * Sets the text in the model and adds an error when the text is not an existing file name.
     */
    private void notifyTextChanged() {
        String text = getResultField().getText();
        getModel().getParent().setDirectChildValue(getModel().getName(), text);
        try {
            if (Files.exists(Paths.get(text))) {
                removeError();
            } else {
                addError("File '" + text + "' does not exist");
            }
        } catch (InvalidPathException exc) {
            addError(exc.getMessage());
        }
    }

    /** Adds an error message to the dialog. */
    private void addError(String message) {
        UserError error =
            new UserError(ERROR_NAME, getModel(), message, UserError.CLASSIFICATION_ERROR);
        getDOM().getController().getErrorController().addError(error);
    }

    /** Removes the error message to the dialog. */
    private void removeError() {
        getDOM().getController().getErrorController().removeUserError(ERROR_NAME, getModel());
    }

    /**
     * Creates an action that calls {@link #handleBrowseAction(JTextField)}
     * with a given text field.
     */
    private Action createBrowseAction() {
        return new AbstractAction("Browse...") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Optional<File> selection = handleBrowseAction(getResultField());
                if (selection.isPresent()) {
                    getResultField().setText(selection.get().getPath());
                }
            }
        };
    }

    /**
     * Starts a file chooser and sets the selected file name in a given text
     * field.
     */
    private Optional<File> handleBrowseAction(JTextField fileField) {
        this.browseChooser.setSelectedFile(new File(fileField.getText()));
        int answer = this.browseChooser.showOpenDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            return Optional.of(this.browseChooser.getSelectedFile());
        } else {
            return Optional.empty();
        }
    }

    /** File chooser for the browse actions. */
    private final JFileChooser browseChooser = GrooveFileChooser.getInstance(FileType.ECORE_META,
        FileType.GXL);
    /** Name of the error generated for this dialog. */
    private final static String ERROR_NAME = "file-exists";
}
