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
package nl.utwente.groove.gui.display;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ToolTipManager;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.gui.Simulator;

/**
 * The Simulator panel that shows the settings resources, with an info panel
 * documenting the keys of the schema of the currently selected resource.
 * @author Arend Rensink
 */
final public class SettingsDisplay extends ResourceDisplay {
    /**
     * @param simulator The Simulator the panel is added to.
     */
    SettingsDisplay(Simulator simulator) {
        super(simulator, ResourceKind.SETTINGS);
    }

    @Override
    protected JComponent createInfoPanel() {
        return this.infoPanel = new TitledPanel(DOC_TITLE, getDocList(), null, true);
    }

    /** The info panel of this display; showing the schema documentation. */
    private TitledPanel infoPanel;

    @Override
    protected void buildInfoPanel() {
        QualName selected = getSimulatorModel().getSelected(ResourceKind.SETTINGS);
        SettingsSchema schema = selected == null
            ? null
            : SettingsSchemas.get(selected.get(0));
        if (schema != this.displayedSchema) {
            this.displayedSchema = schema;
            this.docMap = schema == null
                ? new HelpMap()
                : schema.getHelpMap();
            getDocList().setListData(this.docMap.keySet().toArray(new String[0]));
            this.infoPanel
                .setName(schema == null
                    ? DOC_TITLE
                    : String.format("%s ('%s' schema)", DOC_TITLE, schema.getName()));
        }
    }

    /** The schema whose documentation is currently shown, if any. */
    private SettingsSchema displayedSchema;

    /** Lazily creates and returns the list showing the schema documentation. */
    private JList<String> getDocList() {
        JList<String> result = this.docList;
        if (result == null) {
            this.docList = result = new JList<>();
            result.setCellRenderer(new DocCellRenderer());
            result.setBackground(null);
            ToolTipManager.sharedInstance().registerComponent(result);
            result.addMouseListener(new DismissDelayer(result));
        }
        return result;
    }

    /** Documentation list. */
    private JList<String> docList;

    /** Mapping from the documentation items to their tool tips. */
    private HelpMap docMap = new HelpMap();

    /** Cell renderer that adds the tool tips of the documentation items. */
    private class DocCellRenderer extends DefaultListCellRenderer {
        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component result
                = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(SettingsDisplay.this.docMap.get(value));
            }
            return result;
        }
    }

    /** Base title of the info panel. */
    static private final String DOC_TITLE = "Settings keys";
}
