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

import groove.explore.config.ExploreKey;
import groove.explore.config.Null;
import groove.explore.config.Setting;
import groove.explore.config.SettingKey;
import groove.gui.action.Refreshable;
import groove.gui.dialog.ExploreConfigDialog;
import groove.io.HTMLConverter;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * Editor for an explore key, consisting of buttons for each valid setting kind
 * and optional editors for all kinds with non-{@code null} content.
 * @author Arend Rensink
 * @version $Revision $
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
        for (SettingKey kind : key.getKindType()
            .getEnumConstants()) {
            content |= kind.getContentType() != Null.class;
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
        for (SettingKey kind : getKey().getKindType()
            .getEnumConstants()) {
            buttons.add(getButton(kind));
            buttons.add(Box.createGlue());
        }
        return buttons;
    }

    private SettingButton getButton(SettingKey kind) {
        return getButtonMap().get(kind);
    }

    private Map<SettingKey,SettingButton> getButtonMap() {
        if (this.buttonMap == null) {
            this.buttonMap = createButtonMap();
        }
        return this.buttonMap;
    }

    /**
     * Computes and returns a mapping from keys to buttons.
     */
    private Map<SettingKey,SettingButton> createButtonMap() {
        Map<SettingKey,SettingButton> buttonMap = new HashMap<>();
        for (SettingKey kind : getKey().getKindType()
            .getEnumConstants()) {
            SettingButton button = new SettingButton(kind, getEditor(kind));
            getButtonGroup().add(button);
            buttonMap.put(kind, button);
        }
        return buttonMap;
    }

    private Map<SettingKey,SettingButton> buttonMap;

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

    private SettingEditor getEditor(SettingKey kind) {
        return getEditorMap().get(kind);
    }

    private Map<SettingKey,SettingEditor> getEditorMap() {
        if (this.editorMap == null) {
            this.editorMap = createEditorMap();
        }
        return this.editorMap;
    }

    private Map<SettingKey,SettingEditor> editorMap;

    private Map<SettingKey,SettingEditor> createEditorMap() {
        Map<SettingKey,SettingEditor> result = new HashMap<>();
        for (SettingKey kind : getKey().getKindType()
            .getEnumConstants()) {
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
    public SettingKey getKind() {
        return null;
    }

    @Override
    public void activate() {
        // does nothing
    }

    @Override
    public Setting<?,?> getSetting() throws FormatException {
        SettingKey selected = getSelectedKind();
        return getEditor(selected).getSetting();
    }

    /**
     * Returns the currently selected setting kind.
     */
    private SettingKey getSelectedKind() {
        SettingKey selected = null;
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
    public void setSetting(Setting<?,?> content) {
        SettingKey kind = content.getKind();
        getButton(kind).setSelected(true);
        getEditor(kind).setSetting(content);
    }

    @Override
    public String getError() {
        return getEditor(getSelectedKind()).getError();
    }

    private class SettingButton extends JRadioButton implements Refreshable {
        SettingButton(final SettingKey kind, final SettingEditor kindEditor) {
            super(StringHandler.toUpper(kind.getName()));
            this.kind = kind;
            if (getKey().getDefaultKind() == kind) {
                setSelected(true);
            }
            addItemListener(getDialog().getDirtyListener());
            setToolTipText(HTMLConverter.HTML_TAG.on(StringHandler.toUpper(kind.getExplanation())));
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

        SettingKey getKind() {
            return this.kind;
        }

        private final SettingKey kind;

        @Override
        public void refresh() {
            setEnabled(getDialog().hasSelectedName());
        }
    }
}
