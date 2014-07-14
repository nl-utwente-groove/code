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
import groove.explore.ExploreConfig;
import groove.explore.config.ExploreKey;
import groove.explore.config.SettingList;
import groove.grammar.model.FormatException;
import groove.gui.dialog.config.EditorFactory;
import groove.gui.dialog.config.SettingEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
                try {
                    Properties props = new Properties();
                    InputStream in = new FileInputStream(file);
                    props.load(in);
                    in.close();
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
        JTabbedPane result = new JTabbedPane();
        // panel with basic settings
        JPanel basicPanel = new JPanel();
        basicPanel.setBorder(createEmptyBorder());
        basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
        basicPanel.add(getEditor(ExploreKey.STRATEGY));
        basicPanel.add(getEditor(ExploreKey.RANDOM));
        basicPanel.add(getEditor(ExploreKey.ACCEPTOR));
        basicPanel.add(getEditor(ExploreKey.COUNT));
        basicPanel.add(Box.createGlue());
        // panel with advanced settings
        JPanel advancedPanel = new JPanel();
        advancedPanel.setBorder(createEmptyBorder());
        advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.Y_AXIS));
        advancedPanel.add(getEditor(ExploreKey.ALGEBRA));
        advancedPanel.add(getEditor(ExploreKey.ISO));
        advancedPanel.add(Box.createGlue());
        result.add(basicPanel, "Basic");
        result.add(advancedPanel, "Advanced");
        return result;
    }

    private SettingEditor getEditor(ExploreKey key) {
        return getEditorMap().get(key);
    }

    private Map<ExploreKey,SettingEditor> getEditorMap() {
        if (this.editorMap == null) {
            EditorFactory factory = new EditorFactory(this);
            this.editorMap = new EnumMap<ExploreKey,SettingEditor>(ExploreKey.class);
            for (ExploreKey key : ExploreKey.values()) {
                this.editorMap.put(key, factory.createEditor(key));
            }
        }
        return this.editorMap;
    }

    private Map<ExploreKey,SettingEditor> editorMap;

    @Override
    protected boolean testDirty() {
        boolean result = false;
        for (SettingEditor editor : getEditorMap().values()) {
            try {
                SettingList selectedValue = getSelectedConfig().get(editor.getKey());
                SettingList editedValue = editor.getSetting();
                result =
                    selectedValue == null ? editedValue != null
                        : !selectedValue.equals(editedValue);
                if (result) {
                    break;
                }
            } catch (FormatException exc) {
                exc.printStackTrace();
                // do nothing
            }
        }
        return result;
    }

    @Override
    protected ExploreConfig extractConfig() {
        ExploreConfig result = createConfig();
        for (SettingEditor editor : getEditorMap().values()) {
            try {
                SettingList editedValue = editor.getSetting();
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
        try {
            OutputStream out = new FileOutputStream(getFile(name));
            config.getProperties().store(out, "Exploration configuration '" + name + "'");
            out.close();
        } catch (IOException exc) {
            // give up
        }
    }

    private boolean storing;

    private File getFile(String name) {
        return new File(CONFIG_DIR, PROPERTY.addExtension(name));
    }

    /** Name of the configuration directory. */
    public static final String CONFIG_DIR = ".config";

    /** Main method, for testing purposes. */
    public static void main(String[] args) {
        new ExploreConfigDialog().getConfiguration();
    }
}
