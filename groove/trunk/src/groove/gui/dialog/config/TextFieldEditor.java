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
package groove.gui.dialog.config;

import groove.explore.config.ExploreKey;
import groove.explore.config.Null;
import groove.explore.config.Setting;
import groove.explore.config.SettingKey;
import groove.gui.dialog.ConfigDialog;
import groove.gui.dialog.ConfigDialog.DirtyListener;
import groove.util.parse.Fallible;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;
import groove.util.parse.StringHandler;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author rensink
 * @version $Revision $
 */
public class TextFieldEditor extends SettingEditor {
    /**
     * Constructs an editor with a text field for user-defined input.
     */
    public TextFieldEditor(ConfigDialog<?> dialog, JPanel holder, ExploreKey key, SettingKey kind) {
        setLayout(new BorderLayout());
        assert kind.getContentType() != Null.class;
        this.dialog = dialog;
        JLabel label = this.label = new JLabel(StringHandler.toUpper(kind.getContentName()) + ": ");
        add(label, BorderLayout.WEST);
        add(getTextField(), BorderLayout.CENTER);
        this.holder = holder;
        this.kind = kind;
        this.key = key;
        this.contentParser = kind.parser();
        holder.add(this, kind.getName());
        dialog.addRefreshable(this);
    }

    private ConfigDialog<?> getDialog() {
        return this.dialog;
    }

    private final ConfigDialog<?> dialog;

    private JPanel getHolder() {
        return this.holder;
    }

    private final JPanel holder;

    private final Parser<?> getContentParser() {
        return this.contentParser;
    }

    private final Parser<?> contentParser;

    private JLabel getLabel() {
        return this.label;
    }

    private final JLabel label;

    private JTextField getTextField() {
        if (this.textField == null) {
            this.textField = new JTextField();
            this.textField.getDocument().addDocumentListener(new TextDirtyListener());
            this.textField.setBackground(this.textField.getBackground());
        }
        return this.textField;
    }

    private JTextField textField;

    @Override
    public void refresh() {
        boolean enabled = getDialog().hasSelectedName();
        setEnabled(enabled);
        getLabel().setEnabled(enabled);
        getTextField().setEnabled(enabled);
    }

    @Override
    public ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    @Override
    public SettingKey getKind() {
        return this.kind;
    }

    private final SettingKey kind;

    @Override
    public void activate() {
        getTextField().setText("");
        testError();
        CardLayout layout = (CardLayout) getHolder().getLayout();
        layout.show(getHolder(), getKind().getName());
        getTextField().requestFocus();
    }

    @Override
    public Setting<?,?> getSetting() throws FormatException {
        String error = getError();
        if (error == null) {
            return getKind().createSetting(getContentParser().parse(getTextField().getText()));
        } else {
            throw new FormatException(error);
        }
    }

    @Override
    public void setSetting(Setting<?,?> setting) {
        getTextField().setText(getContentParser().toParsableString(setting.getContent()));
    }

    @Override
    public boolean hasError() {
        return getError() != null;
    }

    @Override
    public String getError() {
        String result = null;
        String text = getTextField().getText();
        try {
            Object parsedValue = getContentParser().parse(text);
            if (parsedValue instanceof Fallible) {
                Fallible fallible = (Fallible) parsedValue;
                if (fallible.hasErrors()) {
                    result = fallible.getErrors().iterator().next().toString();
                }
            }
        } catch (FormatException exc) {
            if (text.isEmpty()) {
                result = "Empty string is not valid";
            } else {
                result = "Value '" + text + "' is not valid";
            }
        }
        return result == null ? null : "Error in " + getKind().getContentName() + ": " + result;
    }

    /** Tests if the editor currently contains an erroneous value, and if so,
     * reports it to the dialog.
     */
    private void testError() {
        getTextField().setForeground(hasError() ? Color.RED : Color.BLACK);
        getDialog().setError(getKey(), getError());
    }

    private class TextDirtyListener extends DirtyListener {
        public TextDirtyListener() {
            super(getDialog());
        }

        @Override
        protected void notifyDocumentChanged() {
            testError();
            super.notifyDocumentChanged();
        }
    }
}
