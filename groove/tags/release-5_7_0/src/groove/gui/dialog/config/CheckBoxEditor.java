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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import groove.explore.config.BooleanKey;
import groove.explore.config.ExploreKey;
import groove.explore.config.Setting;
import groove.explore.config.SettingKey;
import groove.gui.action.Refreshable;
import groove.gui.dialog.ExploreConfigDialog;

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
    public CheckBoxEditor(ExploreConfigDialog dialog, ExploreKey key, String title) {
        this.dialog = dialog;
        this.key = key;
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(createCheckBoxPanel());
        add(Box.createGlue());
        setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
        dialog.addRefreshable(this);
    }

    private ExploreConfigDialog getDialog() {
        return this.dialog;
    }

    private final ExploreConfigDialog dialog;

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
        return getSetting();
    }

    @Override
    public void activate() {
        // does nothing
    }

    @Override
    public BooleanKey getSetting() {
        return (getCheckBox().isSelected() ? BooleanKey.TRUE : BooleanKey.FALSE)
            .getDefaultSetting();
    }

    @Override
    public void setSetting(Setting<?,?> content) {
        getCheckBox().setSelected(content.getKind() == BooleanKey.TRUE);
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
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getDialog().setHelp(getKey(), getKind());
                }
            });
        }

        @Override
        public void refresh() {
            setEnabled(getDialog().hasSelectedName());
        }
    }
}
