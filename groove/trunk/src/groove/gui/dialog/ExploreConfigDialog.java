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
package groove.gui.dialog;

import groove.explore.ExploreConfig;
import groove.explore.config.BooleanKey;
import groove.explore.config.ExploreKey;
import groove.explore.config.SettingKey;
import groove.explore.config.SettingList;
import groove.gui.action.Refreshable;
import groove.util.Groove;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Dialog to manage exploration configurations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreConfigDialog extends ConfigDialog<ExploreConfig> {
    @Override
    protected void selectConfig(String name) {
        super.selectConfig(name);
        boolean wasListening = resetDirtyListening();
        ExploreConfig config = getSelectedConfig();
        if (config != null) {
            for (ExploreKey key : ExploreKey.values()) {
                setSelected(key, config.get(key));
            }
        }
        setDirtyListening(wasListening);
    }

    @Override
    protected ExploreConfig createConfig() {
        return new ExploreConfig();
    }

    @Override
    protected JPanel createMainPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.add(createButtonPanel(ExploreKey.STRATEGY, "Search strategy"));
        result.add(createCheckboxPanel(ExploreKey.RANDOM, "Randomisation"));
        result.add(createButtonPanel(ExploreKey.ALGEBRA, "Algebra"));
        result.add(createCheckboxPanel(ExploreKey.ISO, "Isomorphism checking"));
        result.add(Box.createGlue());
        return result;
    }

    @Override
    protected boolean testDirty() {
        boolean result = false;
        for (ExploreKey key : ExploreKey.values()) {
            SettingList selectedValue = getSelectedConfig().get(key);
            SettingList editedValue = getSelection(key);
            result =
                selectedValue == null ? editedValue != null : !selectedValue.equals(editedValue);
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    protected ExploreConfig extractConfig() {
        ExploreConfig result = createConfig();
        for (ExploreKey key : ExploreKey.values()) {
            SettingList editedValue = getSelection(key);
            if (editedValue != null) {
                result.put(key, editedValue);
            }
        }
        return result;
    }

    private JPanel createButtonPanel(ExploreKey exploreKey, String title) {
        JPanel result = new JPanel();
        result.setBorder(BorderFactory.createTitledBorder(title));
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        for (SettingKey kind : exploreKey.getKindType().getEnumConstants()) {
            result.add(getButton(exploreKey, kind));
        }
        result.add(Box.createGlue());
        result.setMaximumSize(new Dimension(result.getMaximumSize().width,
            result.getPreferredSize().height));
        return result;
    }

    private JPanel createCheckboxPanel(ExploreKey exploreKey, String title) {
        JPanel result = new JPanel();
        result.setBorder(BorderFactory.createTitledBorder(title));
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        result.add(getCheckbox(exploreKey));
        result.add(Box.createGlue());
        result.setMaximumSize(new Dimension(result.getMaximumSize().width,
            result.getPreferredSize().height));
        return result;
    }

    private JRadioButton getButton(ExploreKey exploreKey, SettingKey kind) {
        Map<SettingKey,JRadioButton> keyMap = this.settingButtonMaps.get(exploreKey);
        if (keyMap == null) {
            initButtons(exploreKey);
            keyMap = this.settingButtonMaps.get(exploreKey);
        }
        return keyMap.get(kind);
    }

    private ButtonGroup getButtonGroup(ExploreKey keyType) {
        ButtonGroup result = this.buttonGroups.get(keyType);
        if (result == null) {
            initButtons(keyType);
            result = this.buttonGroups.get(keyType);
        }
        return result;
    }

    private Map<ButtonModel,SettingList> getSettingMap(ExploreKey keyType) {
        Map<ButtonModel,SettingList> result = this.buttonSettingMaps.get(keyType);
        if (result == null) {
            initButtons(keyType);
            result = this.buttonSettingMaps.get(keyType);
        }
        return result;
    }

    /**
     * Initialises a radio button group for a given key type.
     */
    private void initButtons(ExploreKey exploreKey) {
        Map<SettingKey,JRadioButton> buttonMap = new HashMap<SettingKey,JRadioButton>();
        Map<ButtonModel,SettingList> settingMap = new HashMap<ButtonModel,SettingList>();
        ButtonGroup buttonGroup = new ButtonGroup();
        for (SettingKey kind : exploreKey.getKindType().getEnumConstants()) {
            JRadioButton button = new SettingButton(kind, kind == exploreKey.getDefaultKind());
            buttonGroup.add(button);
            buttonMap.put(kind, button);
            settingMap.put(button.getModel(), kind.getDefaultSetting());
        }
        this.settingButtonMaps.put(exploreKey, buttonMap);
        this.buttonGroups.put(exploreKey, buttonGroup);
        this.buttonSettingMaps.put(exploreKey, settingMap);
    }

    private final Map<ExploreKey,ButtonGroup> buttonGroups = new EnumMap<ExploreKey,ButtonGroup>(
        ExploreKey.class);
    private final Map<ExploreKey,Map<SettingKey,JRadioButton>> settingButtonMaps =
        new EnumMap<ExploreKey,Map<SettingKey,JRadioButton>>(ExploreKey.class);
    private final Map<ExploreKey,Map<ButtonModel,SettingList>> buttonSettingMaps =
        new EnumMap<ExploreKey,Map<ButtonModel,SettingList>>(ExploreKey.class);

    private SettingCheckbox getCheckbox(ExploreKey key) {
        SettingCheckbox result = this.checkBoxMap.get(key);
        if (result == null) {
            String question = null;
            switch (key) {
            case ISO:
                question = "Should isomorphic states be collapsed?";
                break;
            case RANDOM:
                question = "Should successors be explored in random order?";
            }
            this.checkBoxMap.put(key, result = new SettingCheckbox(key, question));
        }
        return result;
    }

    private final Map<ExploreKey,SettingCheckbox> checkBoxMap =
        new EnumMap<ExploreKey,ExploreConfigDialog.SettingCheckbox>(ExploreKey.class);

    /** Selects a given setting for a given exploration key. */
    private void setSelected(ExploreKey key, SettingList value) {
        switch (key) {
        //case ACCEPTOR:
        case ALGEBRA:
            break;
        //case BOUNDARY:
        case COUNT:
        case MATCHER:
            break;
        case ISO:
        case RANDOM:
            getCheckbox(key).setSelected((BooleanKey) value.single() == BooleanKey.TRUE);
            break;
        case STRATEGY:
            getButton(key, (SettingKey) value.single()).setSelected(true);
            break;
        default:
            assert false;
        }
    }

    private SettingList getSelection(ExploreKey key) {
        switch (key) {
        //case ACCEPTOR:
        //case BOUNDARY:
        case COUNT:
        case MATCHER:
            return null;
        case ISO:
        case RANDOM:
            return BooleanKey.getKey(getCheckbox(key).isSelected()).getDefaultSetting();
        case ALGEBRA:
        case STRATEGY:
            return getSettingMap(key).get(getButtonGroup(key).getSelection());
        }
        assert false;
        return null;
    }

    private class SettingCheckbox extends JCheckBox implements Refreshable {
        SettingCheckbox(ExploreKey key, String question) {
            super(question);
            addItemListener(getDirtyListener());
            setToolTipText(key.getExplanation());
            addRefreshable(this);
        }

        @Override
        public void refresh() {
            setEnabled(hasSelectedName());
        }
    }

    private class SettingButton extends JRadioButton implements Refreshable {
        SettingButton(SettingKey kind, boolean isDefault) {
            super(Groove.convertCase(kind.getName(), true));
            if (isDefault) {
                setSelected(true);
            }
            addItemListener(getDirtyListener());
            setToolTipText(Groove.convertCase(kind.getExplanation(), true));
            addRefreshable(this);
        }

        @Override
        public void refresh() {
            setEnabled(hasSelectedName());
        }
    }

    boolean resetDirtyListening() {
        boolean result = this.dirtyListening;
        this.dirtyListening = false;
        return result;
    }

    void setDirtyListening(boolean listening) {
        this.dirtyListening = listening;
    }

    private boolean dirtyListening;

    DirtyListener getDirtyListener() {
        if (this.dirtyListener == null) {
            this.dirtyListener = new DirtyListener();
            this.dirtyListening = true;
        }
        return this.dirtyListener;
    }

    private DirtyListener dirtyListener;

    private class DirtyListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            boolean wasListening = resetDirtyListening();
            if (wasListening) {
                testSetDirty();
                refreshActions();
            }
            setDirtyListening(wasListening);
        }
    }

    /** Main method, for testing purposes. */
    public static void main(String[] args) {
        new ExploreConfigDialog().getConfiguration();
    }
}
