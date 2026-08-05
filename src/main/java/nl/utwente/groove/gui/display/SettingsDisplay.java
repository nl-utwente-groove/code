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
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ToolTipManager;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.explore.config.ExploreConfigSchema;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.gui.Icons;
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

    /* Settings resources of an activatable schema can be enabled, meaning
     * they become the resource of that schema the grammar actually uses;
     * the button is disabled for resources of other schemas. */
    @Override
    public boolean hasEnableButton() {
        return true;
    }

    /* The leading segment of a settings resource name is the schema folder.
     * In this display the schema is evident from the resource's place in the
     * tree (and from the schema icon and documentation panel), so in the tab
     * titles it is noise: 'explore.fast' is shown as 'fast'. A nested local
     * name keeps its remaining segments, and a singleton-form name (the bare
     * schema name) has nothing to strip. */
    @Override
    public String getDisplayName(QualName name) {
        return name.size() > 1
            ? name.removeParent(QualName.name(name.get(0))).toString()
            : name.toString();
    }

    /* Settings resources of different schemas are told apart by their icon,
     * for those schemas that have one of their own. */
    @Override
    public Icon getListIcon(QualName name) {
        Icon result = null;
        if (!isEdited(name)) {
            result = getSchemaIcon(name);
        }
        return result == null
            ? super.getListIcon(name)
            : result;
    }

    @Override
    public Icon getMainTabIcon(QualName name) {
        Icon result = name == null
            ? null
            : getSchemaIcon(name);
        return result == null
            ? super.getMainTabIcon(name)
            : result;
    }

    /**
     * Returns the icon of the schema of a given settings resource, or
     * {@code null} if that schema has no icon of its own (or the resource
     * does not exist).
     */
    private Icon getSchemaIcon(QualName name) {
        var model = getResource(name);
        return model instanceof SettingsModel settings
            ? SCHEMA_ICON_MAP.get(settings.getSchemaName())
            : null;
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
        // the schema is implied by the location of the resource; ask the model
        // rather than dissecting the name here
        var model = selected == null
            ? null
            : getResource(selected);
        SettingsSchema schema = model instanceof SettingsModel settings
            ? settings.getSchema()
            : null;
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
    /**
     * Mapping from schema name to the icon under which the resources of that
     * schema are shown, for the schemas that have an icon of their own; the
     * others fall back on the generic settings icon.
     * The mapping lives on the GUI side because {@link SettingsSchema} is a
     * non-GUI interface: putting an {@link Icon} on it would make
     * {@code grammar.model} (and every headless schema implementation) depend
     * on Swing.
     */
    static private final Map<String,Icon> SCHEMA_ICON_MAP
        = Map.of(ExploreConfigSchema.NAME, Icons.COMPASS_ICON);
}
