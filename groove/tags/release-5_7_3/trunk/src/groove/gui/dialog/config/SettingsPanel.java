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
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;

import groove.explore.config.ExploreKey;
import groove.explore.config.SettingKey;
import groove.gui.dialog.ConfigDialog;

/**
 * Panel for a group of setting kinds.
 * This is divided into a sub-panel for the setting editors, and a sub-panel for syntax help.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingsPanel extends JPanel {
    /**
     * Creates a new settings panel, with a given name.
     */
    public SettingsPanel(ConfigDialog<?> dialog, String name) {
        setName(name);
        this.dialog = dialog;
        this.helpFactory = new HelpFactory();
        this.knownHelp = new HashSet<>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(getEditorPanel());
        add(getHelpPanel());
    }

    /** Returns the dialog on which this panel is placed. */
    public ConfigDialog<?> getDialog() {
        return this.dialog;
    }

    private final ConfigDialog<?> dialog;

    /** Lazily creates and returns the sub-panel for the editors. */
    private JPanel getEditorPanel() {
        if (this.editorPanel == null) {
            this.editorPanel = computeEditorPanel();
        }
        return this.editorPanel;
    }

    private JPanel editorPanel;

    private JPanel computeEditorPanel() {
        JPanel result = new JPanel();
        result.setBorder(getDialog().createEmptyBorder());
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setPreferredSize(new Dimension(250, 100));
        return result;
    }

    /** Lazily creates and returns the sub-panel for the syntax help. */
    private JPanel getHelpPanel() {
        if (this.helpPanel == null) {
            this.helpPanel = computeHelpPanel();
        }
        return this.helpPanel;
    }

    private JPanel helpPanel;

    private JPanel computeHelpPanel() {
        JPanel result = new JPanel(new CardLayout());
        // add a dummy panel to ensure first help screen really shows up
        result.add(new JPanel(), "dummy");
        result.setBorder(new CompoundBorder(getDialog().createEmptyBorder(),
            BorderFactory.createTitledBorder("Help")));
        result.setPreferredSize(new Dimension(200, 100));
        return result;
    }

    /** Adds the editors and syntax help for a given key. */
    public void add(SettingEditor editor) {
        getEditorPanel().add(editor);
    }

    /** Adds vertical glue to the editor panel. */
    public void addGlue() {
        getEditorPanel().add(Box.createGlue());
    }

    /** Sets the syntax help for a given combination of exploration key and setting kind. */
    public void setHelp(ExploreKey key, SettingKey kind) {
        String name = getName(key, kind);
        if (this.knownHelp.add(name)) {
            JComponent help = this.helpFactory.createHelp(key, kind);
            JScrollPane scrollPane = new JScrollPane(help);
            getHelpPanel().add(scrollPane, name);
        }
        CardLayout cl = (CardLayout) getHelpPanel().getLayout();
        cl.show(getHelpPanel(), name);
    }

    /** Converts a combination of exploration key and setting kind to
     * a single name, for the {@link CardLayout} of the help panel.
     */
    private String getName(ExploreKey key, SettingKey kind) {
        return key.getName() + ":" + kind.getName();
    }

    private final Set<String> knownHelp;

    private final HelpFactory helpFactory;
}
