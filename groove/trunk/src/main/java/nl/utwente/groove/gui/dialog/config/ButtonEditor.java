/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.dialog.config;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.Setting;
import nl.utwente.groove.explore.config.Setting.ContentType;
import nl.utwente.groove.gui.action.Refreshable;
import nl.utwente.groove.gui.dialog.ExploreConfigDialog;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Editor for an explore key, consisting of buttons for each valid setting kind
 * and optional editors for all kinds with non-{@code null} content.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ButtonEditor extends SettingEditor {
    /**
     * Creates a button editor for a given explore key.
     */
    public ButtonEditor(ExploreConfigDialog dialog, ExploreKey key, String title) {
        this.dialog = dialog;
        this.key = key;
        this.factory = new EditorFactory(dialog);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        boolean content = false;
        for (var kind : key.getKindType().getEnumConstants()) {
            content |= kind.contentType() != ContentType.NULL;
        }
        add(createButtonsPanel());
        if (content) {
            add(getContentPanel());
            // initialise the editors
            getEditor(getSelectedKind()).activate();
        }
        add(Box.createGlue());
        setBorder(BorderFactory.createTitledBorder(title));
        setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
        dialog.addRefreshable(this);
    }

    private ExploreConfigDialog getDialog() {
        return this.dialog;
    }

    private final ExploreConfigDialog dialog;

    /** The editor factory for this dialog. */
    private final EditorFactory factory;

    private JPanel createButtonsPanel() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        for (var kind : getKey().getKindType().getEnumConstants()) {
            buttons.add(getButton(kind));
            buttons.add(Box.createGlue());
        }
        return buttons;
    }

    private SettingButton getButton(Setting.Key kind) {
        return getButtonMap().get(kind);
    }

    private Map<Setting.Key,SettingButton> getButtonMap() {
        if (this.buttonMap == null) {
            this.buttonMap = createButtonMap();
        }
        return this.buttonMap;
    }

    /**
     * Computes and returns a mapping from keys to buttons.
     */
    private Map<Setting.Key,SettingButton> createButtonMap() {
        Map<Setting.Key,SettingButton> buttonMap = new HashMap<>();
        for (var kind : getKey().getKindType().getEnumConstants()) {
            SettingButton button = new SettingButton(kind, getEditor(kind));
            getButtonGroup().add(button);
            buttonMap.put(kind, button);
        }
        return buttonMap;
    }

    private Map<Setting.Key,SettingButton> buttonMap;

    private ButtonGroup getButtonGroup() {
        if (this.buttonGroup == null) {
            this.buttonGroup = new ButtonGroup();
        }
        return this.buttonGroup;
    }

    private ButtonGroup buttonGroup;

    /**
     * Returns the panel holding the content editors.
     */
    private JPanel getContentPanel() {
        if (this.contentPanel == null) {
            this.contentPanel = new JPanel(new CardLayout());
        }
        return this.contentPanel;
    }

    private JPanel contentPanel;

    private SettingEditor getEditor(Setting.Key kind) {
        return getEditorMap().get(kind);
    }

    private Map<Setting.Key,SettingEditor> getEditorMap() {
        if (this.editorMap == null) {
            this.editorMap = createEditorMap();
        }
        return this.editorMap;
    }

    private Map<Setting.Key,SettingEditor> editorMap;

    private Map<Setting.Key,SettingEditor> createEditorMap() {
        Map<Setting.Key,SettingEditor> result = new HashMap<>();
        for (var kind : getKey().getKindType().getEnumConstants()) {
            result.put(kind, this.factory.createEditor(getContentPanel(), getKey(), kind));
        }
        return result;
    }

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
    public Setting.Key getKind() {
        return null;
    }

    @Override
    public void activate() {
        // does nothing
    }

    @Override
    public Setting getSetting() throws FormatException {
        Setting.Key selected = getSelectedKind();
        return getEditor(selected).getSetting();
    }

    /**
     * Returns the currently selected setting kind.
     */
    private Setting.Key getSelectedKind() {
        Setting.Key selected = null;
        ButtonModel model = getButtonGroup().getSelection();
        for (SettingButton button : getButtonMap().values()) {
            if (button.getModel() == model) {
                selected = button.getKind();
                break;
            }
        }
        return selected;
    }

    @Override
    public void setSetting(Setting content) {
        Setting.Key kind = content.key();
        getButton(kind).setSelected(true);
        getEditor(kind).setSetting(content);
    }

    @Override
    public String getError() {
        return getEditor(getSelectedKind()).getError();
    }

    private class SettingButton extends JRadioButton implements Refreshable {
        SettingButton(final Setting.Key kind, final SettingEditor kindEditor) {
            super(Strings.toUpper(kind.getName()));
            this.kind = kind;
            if (getKey().getDefaultKind() == kind) {
                setSelected(true);
            }
            addItemListener(getDialog().getDirtyListener());
            setToolTipText(HTMLConverter.HTML_TAG.on(Strings.toUpper(kind.getExplanation())));
            getDialog().addRefreshable(this);
            addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        getEditor(kind).activate();
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getDialog().setHelp(getKey(), getKind());
                }
            });
        }

        Setting.Key getKind() {
            return this.kind;
        }

        private final Setting.Key kind;

        @Override
        public void refresh() {
            setEnabled(getDialog().hasSelectedName());
        }
    }
}
