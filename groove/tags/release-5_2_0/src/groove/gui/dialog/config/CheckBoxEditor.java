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

import groove.explore.config.BooleanKey;
import groove.explore.config.ExploreKey;
import groove.explore.config.SettingKey;
import groove.explore.config.SettingList;
import groove.grammar.model.FormatException;
import groove.gui.action.Refreshable;
import groove.gui.dialog.ConfigDialog;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Editor for an explore key, consisting of buttons for each valid setting kind
 * and optional editors for all kinds with non-{@code null} content.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CheckBoxEditor extends SettingEditor {
    /**
     * Creates a button editor for a given explore key.
     */
    public CheckBoxEditor(ConfigDialog<?> dialog, ExploreKey key, String title) {
        this.dialog = dialog;
        this.key = key;
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(createCheckBoxPanel());
        add(Box.createGlue());
        setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
        dialog.addRefreshable(this);
    }

    private ConfigDialog<?> getDialog() {
        return this.dialog;
    }

    private final ConfigDialog<?> dialog;

    private JPanel createCheckBoxPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        result.add(getCheckBox());
        result.add(Box.createGlue());
        return result;
    }

    private SettingCheckBox getCheckBox() {
        if (this.checkBox == null) {
            this.checkBox = new SettingCheckBox(getKey());
        }
        return this.checkBox;
    }

    private SettingCheckBox checkBox;

    @Override
    public void refresh() {
        setEnabled(getDialog().hasSelectedName());
    }

    @Override
    public ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    @Override
    public SettingKey getKind() {
        return null;
    }

    @Override
    public void activate() {
        // does nothing
    }

    @Override
    public SettingList getSetting() throws FormatException {
        return (getCheckBox().isSelected() ? BooleanKey.TRUE : BooleanKey.FALSE).getDefaultSetting();
    }

    @Override
    public void setSetting(SettingList content) {
        getCheckBox().setSelected(content.single().getKind() == BooleanKey.TRUE);
    }

    @Override
    public boolean hasError() {
        return getError() != null;
    }

    @Override
    public String getError() {
        return null;
    }

    private class SettingCheckBox extends JCheckBox implements Refreshable {
        SettingCheckBox(ExploreKey key) {
            super(key.getExplanation());
            addItemListener(getDialog().getDirtyListener());
            setToolTipText(key.getExplanation());
            getDialog().addRefreshable(this);
        }

        @Override
        public void refresh() {
            setEnabled(getDialog().hasSelectedName());
        }
    }
}
