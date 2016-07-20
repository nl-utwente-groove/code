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
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author rensink
 * @version $Revision $
 */
public class TextFieldEditor extends ContentEditor {
    /**
     * Constructs an editor with a text field for user-defined input.
     */
    public TextFieldEditor(ConfigDialog<?> dialog, JPanel holder, ExploreKey key, SettingKey kind) {
        super(dialog, holder, key, kind);
        setLayout(new BorderLayout());
        assert kind.getContentType() != Null.class;
        JLabel label = this.label = new JLabel(StringHandler.toUpper(kind.getContentName()) + ": ");
        add(label, BorderLayout.WEST);
        add(getTextField(), BorderLayout.CENTER);
        this.contentParser = kind.parser();
        holder.add(this, kind.getName());
        dialog.addRefreshable(this);
    }

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
    public void activate() {
        getTextField().setText("");
        super.activate();
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
                result =
                    "Value '" + text + "' is not valid (" + StringHandler.toLower(exc.getMessage())
                        + ")";
            }
        }
        return result == null ? null : "Error in " + getKind().getContentName() + ": " + result;
    }

    @Override
    protected void testError() {
        getTextField().setForeground(hasError() ? Color.RED : Color.BLACK);
        super.testError();
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
