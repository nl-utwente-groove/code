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

import static groove.io.FileType.PROPERTY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.itextpdf.text.Font;

import groove.explore.ExploreConfig;
import groove.explore.config.ExploreKey;
import groove.explore.config.Setting;
import groove.explore.config.SettingKey;
import groove.gui.Options;
import groove.gui.action.Refreshable;
import groove.gui.dialog.config.EditorFactory;
import groove.gui.dialog.config.SettingEditor;
import groove.gui.dialog.config.SettingsPanel;
import groove.util.parse.FormatException;

/**
 * Dialog to manage exploration configurations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreConfigDialog extends ConfigDialog<ExploreConfig> {
    /** Constructs a new dialog, and attempts to load it from the property files in {@link #CONFIG_DIR}. */
    public ExploreConfigDialog() {
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        if (configDir.isDirectory()) {
            this.storing = false;
            for (File file : configDir.listFiles(PROPERTY.getFilter())) {
                try (InputStream in = new FileInputStream(file)) {
                    Properties props = new Properties();
                    props.load(in);
                    addConfig(PROPERTY.stripExtension(file.getName()), new ExploreConfig(props));
                } catch (IOException exc) {
                    // skip this file
                }
            }
            this.storing = true;
        }
    }

    @Override
    protected void selectConfig(String name) {
        super.selectConfig(name);
        boolean wasListening = resetDirtListening();
        ExploreConfig config = getSelectedConfig();
        if (config != null) {
            for (SettingEditor editor : getEditorMap().values()) {
                editor.setSetting(config.get(editor.getKey()));
            }
        }
        setDirtListening(wasListening);
    }

    @Override
    protected ExploreConfig createConfig() {
        return new ExploreConfig();
    }

    @Override
    protected JComponent createMainPanel() {
        JPanel result = new JPanel(new BorderLayout());
        result.add(createTabsPanel(), BorderLayout.CENTER);
        result.add(createCommandLinePanel(), BorderLayout.SOUTH);
        return result;
    }

    private JComponent createTabsPanel() {
        JTabbedPane result = new JTabbedPane();
        result.setPreferredSize(new Dimension(500, 100));
        // panel with basic settings
        SettingsPanel searchPanel = new SettingsPanel(this, "Search");
        addEditor(searchPanel, ExploreKey.TRAVERSE);
        addEditor(searchPanel, ExploreKey.RANDOM);
        addEditor(searchPanel, ExploreKey.ACCEPTOR);
        addEditor(searchPanel, ExploreKey.COUNT);
        addTab(result, searchPanel);
        // panel with basic settings
        SettingsPanel checkingPanel = new SettingsPanel(this, "Model Checking");
        addEditor(checkingPanel, ExploreKey.CHECKING);
        addTab(result, checkingPanel);
        // panel with advanced settings
        SettingsPanel advancedPanel = new SettingsPanel(this, "Advanced");
        addEditor(advancedPanel, ExploreKey.ALGEBRA);
        addEditor(advancedPanel, ExploreKey.ISO);
        addEditor(advancedPanel, ExploreKey.MATCHER);
        addTab(result, advancedPanel);
        return result;
    }

    /** Adds the editor for a given exploration key to a given settings panel. */
    private void addEditor(SettingsPanel panel, ExploreKey key) {
        SettingsPanel oldPanel = this.panelMap.put(key, panel);
        assert oldPanel == null;
        panel.add(getEditorMap().get(key));
    }

    private final Map<ExploreKey,SettingsPanel> panelMap = new EnumMap<>(ExploreKey.class);

    private Map<ExploreKey,SettingEditor> getEditorMap() {
        if (this.editorMap == null) {
            EditorFactory factory = new EditorFactory(this);
            this.editorMap = new EnumMap<>(ExploreKey.class);
            for (ExploreKey key : ExploreKey.values()) {
                this.editorMap.put(key, factory.createEditor(key));
            }
        }
        return this.editorMap;
    }

    private Map<ExploreKey,SettingEditor> editorMap;

    /** Adds a given settings panel as tab to the tabbed pane of the main panel. */
    private void addTab(JTabbedPane pane, SettingsPanel panel) {
        panel.addGlue();
        pane.add(panel, panel.getName());
    }

    private JPanel createCommandLinePanel() {
        JPanel result = new JPanel(new BorderLayout());
        result.setBorder(BorderFactory.createEmptyBorder(6, 5, 0, 5));
        result.add(new JLabel("Command: "), BorderLayout.WEST);
        result.add(getCommandLineField(), BorderLayout.CENTER);
        return result;
    }

    private CommandLineField getCommandLineField() {
        if (this.commandLineField == null) {
            this.commandLineField = new CommandLineField();
        }
        return this.commandLineField;
    }

    private CommandLineField commandLineField;

    private class CommandLineField extends JTextField implements Refreshable {
        /**
         * Constructs an initially empty command line field.
         */
        CommandLineField() {
            addRefreshable(this);
            setEditable(false);
            setBackground(Color.WHITE);
        }

        @Override
        public void refresh() {
            ExploreConfig config = extractConfig();
            String commandLine = config.toCommandLine();
            if (commandLine.isEmpty()) {
                setForeground(Color.GRAY);
                setFont(getFont().deriveFont(Font.ITALIC));
                setText("No parameters");
                setEnabled(false);
            } else {
                setForeground(Color.BLACK);
                setFont(getFont().deriveFont(Font.NORMAL));
                setText(commandLine);
                setEnabled(true);
            }
        }
    }

    @Override
    protected boolean testDirty() {
        boolean result = false;
        ExploreConfig config = getSelectedConfig();
        if (config != null) {
            for (SettingEditor editor : getEditorMap().values()) {
                try {
                    Setting<?,?> selectedValue = config.get(editor.getKey());
                    Setting<?,?> editedValue = editor.getSetting();
                    result = selectedValue == null ? editedValue != null
                        : !selectedValue.equals(editedValue);
                } catch (FormatException exc) {
                    // there is an error in a setting, so the state must be dirty
                    result = true;
                }
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected ExploreConfig extractConfig() {
        ExploreConfig result = createConfig();
        for (SettingEditor editor : getEditorMap().values()) {
            try {
                Setting<?,?> editedValue = editor.getSetting();
                if (editedValue != null) {
                    result.put(editor.getKey(), editedValue);
                }
            } catch (FormatException exc) {
                // do nothing
            }
        }
        return result;
    }

    @Override
    void addConfig(String newName, ExploreConfig newConfig) {
        super.addConfig(newName, newConfig);
        store(newName, newConfig);
    }

    @Override
    void saveConfig() {
        String currentName = getSelectedName();
        String newName = getEditedName();
        if (!currentName.equals(newName)) {
            getFile(currentName).delete();
        }
        super.saveConfig();
        store(getSelectedName(), getSelectedConfig());
    }

    @Override
    void deleteConfig() {
        getFile(getSelectedName()).delete();
        super.deleteConfig();
    }

    private void store(String name, ExploreConfig config) {
        if (!this.storing) {
            return;
        }
        try (OutputStream out = new FileOutputStream(getFile(name))) {
            config.getProperties()
                .store(out, "Exploration configuration '" + name + "'");
        } catch (IOException exc) {
            // give up
        }
    }

    private boolean storing;

    private File getFile(String name) {
        return new File(CONFIG_DIR, PROPERTY.addExtension(name));
    }

    /** Sets the help panel for a given combination of exploration key and setting kind. */
    public void setHelp(ExploreKey key, SettingKey kind) {
        SettingsPanel panel = this.panelMap.get(key);
        if (panel != null) {
            panel.setHelp(key, kind);
        }
    }

    /** Name of the configuration directory. */
    public static final String CONFIG_DIR = ".config";

    /** Main method, for testing purposes. */
    public static void main(String[] args) {
        Options.initLookAndFeel();
        new ExploreConfigDialog().getConfiguration();
    }
}
