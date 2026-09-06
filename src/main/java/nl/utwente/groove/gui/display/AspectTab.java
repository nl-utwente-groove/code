/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.display;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.ResourceProperties.Key;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.dialog.PropertiesTable;
import nl.utwente.groove.gui.list.ErrorEntry;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tab showing an aspect graph on a canvas, with the label tree and the
 * resource properties as its upper info panel. This is the common part of
 * the tab that views the graph-based resources of a display and the tab that
 * edits one of them; {@link #isEditor()} tells the two apart where the shared
 * components differ in detail.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
abstract public class AspectTab extends ResourceTab implements GraphDisplay<@NonNull AspectGraph> {
    /** Constructs a tab for a given display. */
    protected AspectTab(ResourceDisplay display) {
        super(display);
    }

    /** Returns the graph currently shown, if any. */
    public @Nullable AspectGraph getGraph() {
        var viewModel = getViewModel();
        return viewModel == null
            ? null
            : viewModel.getGraph();
    }

    /* Selects the elements of the selected error on the canvas. */
    @Override
    protected PropertyChangeListener createErrorListener() {
        return arg -> {
            if (getViewModel() != null) {
                var entry = (ErrorEntry) arg.getNewValue();
                if (entry == null) {
                    getCanvas().clearSelection();
                } else {
                    getCanvas().selectElements(entry.getElements());
                }
            }
        };
    }

    @Override
    protected GraphPanel<@NonNull AspectGraph> getEditArea() {
        GraphPanel<@NonNull AspectGraph> result = this.editArea;
        if (result == null) {
            this.editArea = result = new GraphPanel<>(getCanvas());
            if (isEditor()) {
                result.setEnabledBackground(Values.EDITOR_BACKGROUND);
                result.initialise();
                result.setEnabled(true);
            } else {
                result.setFocusable(false);
                result.setEnabled(false);
                result.initialise();
            }
        }
        return result;
    }

    /** Graph panel of this tab. */
    private GraphPanel<@NonNull AspectGraph> editArea;

    @Override
    public GraphPanel<@NonNull AspectGraph> getGraphPanel() {
        return getEditArea();
    }

    @Override
    public final @NonNull AspectGraphCanvas getCanvas() {
        return getController().getCanvas();
    }

    @Override
    public final @NonNull AspectGraphViewController getController() {
        AspectGraphViewController result = this.controller;
        if (result == null) {
            result = this.controller
                = new AspectGraphViewController(getSimulator(), getDisplay().getKind(), isEditor());
            result.setLabelTree(getLabelTree());
            result.setLevelTree(getLevelTree());
        }
        return result;
    }

    /** The controller of this tab's graph view. */
    private AspectGraphViewController controller;

    @Override
    public final @Nullable AspectGraphViewModel getViewModel() {
        return getCanvas().getViewModel();
    }

    @Override
    protected JTabbedPane getUpperInfoPanel() {
        JTabbedPane result = this.upperInfoPanel;
        if (result == null) {
            this.upperInfoPanel = result = new JTabbedPane();
            result.add(getLabelPanel());
            if (getResourceKind().hasProperties()) {
                var propertiesPanel = getPropertiesScrollPanel();
                result.add(propertiesPanel);
                int index = result.indexOfComponent(propertiesPanel);
                this.propertiesHeader.setText(propertiesPanel.getName());
                result.setTitleAt(index, null);
                result.setTabComponentAt(index, this.propertiesHeader);
                updatePropertiesNotable();
                result.addChangeListener(createInfoListener(true));
            }
        }
        if (getResourceKind().hasProperties()) {
            result.setSelectedIndex(getDisplay().getInfoTabIndex(true));
        }
        return result;
    }

    /** Upper info panel of this tab. */
    private JTabbedPane upperInfoPanel;

    private TitledPanel getLabelPanel() {
        TitledPanel result = this.labelPanel;
        if (result == null) {
            TypeTree labelTree = getLabelTree();
            this.labelPanel = result = new TitledPanel(Options.LABEL_PANE_TITLE, labelTree,
                labelTree.createToolBar(), true);
            result.setTitled(false);
            if (isEditor()) {
                result.setEnabledBackground(Values.EDITOR_BACKGROUND);
            }
        }
        return result;
    }

    /** Label panel of this tab. */
    private TitledPanel labelPanel;

    /** Lazily creates and returns the label tree; it filters labels only when viewing. */
    protected final TypeTree getLabelTree() {
        TypeTree result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new TypeTree(getCanvas(), !isEditor());
        }
        return result;
    }

    private TypeTree labelTree;

    /**
     * Lazily creates and returns the properties table. When editing, the table
     * is editable and its changes are passed to {@link #propertiesEdited};
     * when viewing, a double click on it opens the editor.
     */
    protected final @NonNull PropertiesTable getPropertiesPanel() {
        PropertiesTable result = this.propertiesPanel;
        if (result == null) {
            final var panel = new PropertiesTable(ResourceProperties.Key.class, isEditor());
            panel.setName("Properties");
            if (isEditor()) {
                panel.setBackground(Values.EDITOR_BACKGROUND);
                panel.getModel().addTableModelListener(e -> {
                    if (AspectTab.this.listenToPropertiesPanel) {
                        propertiesEdited(panel);
                    }
                });
            } else {
                panel.addMouseListener(new EditMouseListener());
            }
            this.propertiesPanel = result = panel;
            this.listenToPropertiesPanel = true;
        }
        return result;
    }

    /** Properties panel of this tab. */
    private PropertiesTable propertiesPanel;

    /**
     * Callback invoked when the user has edited the properties table.
     * Only called when this tab is an editor; does nothing by default.
     */
    protected void propertiesEdited(PropertiesTable panel) {
        // does nothing by default
    }

    /**
     * Loads given properties into the properties table, without passing the
     * change back through {@link #propertiesEdited}.
     * @param properties the properties to be shown
     * @param graph the graph the properties belong to, used to check them
     */
    protected final void loadProperties(ResourceProperties properties,
                                        @Nullable AspectGraph graph) {
        // get the table first as creating it sets listenToPropertiesPanel to true
        PropertiesTable panel = getPropertiesPanel();
        this.listenToPropertiesPanel = false;
        panel.setProperties(properties);
        panel.setCheckerMap(properties.getCheckers(graph));
        this.listenToPropertiesPanel = true;
    }

    /** Flag indicating if table changes should be propagated to the graph properties. */
    private boolean listenToPropertiesPanel;

    /** Lazily creates and returns the scroll pane around the properties table. */
    protected final @NonNull JScrollPane getPropertiesScrollPanel() {
        var result = this.propertiesScrollPanel;
        if (result == null) {
            var propertiesPanel = getPropertiesPanel();
            this.propertiesScrollPanel = result = new JScrollPane(propertiesPanel);
            result.setName(propertiesPanel.getName());
            result.getViewport().setBackground(propertiesPanel.getBackground());
        }
        return result;
    }

    private JScrollPane propertiesScrollPanel;

    /** Tab component of the properties tab in the upper info panel. */
    private final JLabel propertiesHeader = new JLabel();

    /** Adapts the properties header according to the notability of the properties. */
    protected final void updatePropertiesNotable() {
        var graph = getGraph();
        if (graph != null) {
            boolean notableProperties = ResourceProperties.getProperties(graph).isNotable();
            this.propertiesHeader
                .setForeground(notableProperties
                    ? Values.INFO_NORMAL_FOREGROUND
                    : Values.NORMAL_FOREGROUND);
        }
    }

    @Override
    protected void updateDirty() {
        updatePropertiesNotable();
        super.updateDirty();
    }

    @Override
    public void setPropertyKey(Key propertyKey) {
        var upperInfoPanel = getUpperInfoPanel();
        if (upperInfoPanel != null && propertyKey != null) {
            upperInfoPanel.setSelectedComponent(getPropertiesScrollPanel());
            getPropertiesPanel().setSelected(propertyKey);
        }
    }

    /* The lower info panel shows the nesting levels of a viewed rule, if any. */
    @Override
    protected JComponent getLowerInfoPanel() {
        JPanel result = this.lowerInfoPanel;
        RuleLevelTree levelTree = getLevelTree();
        if (result == null && levelTree != null) {
            this.lowerInfoPanel = result = new TitledPanel("Nesting levels", levelTree, null, true);
        }
        return levelTree != null && levelTree.isEnabled()
            ? result
            : null;
    }

    private JPanel lowerInfoPanel;

    /**
     * Lazily creates and returns the nesting-level tree, which exists only
     * when viewing a rule.
     */
    protected final @Nullable RuleLevelTree getLevelTree() {
        RuleLevelTree result = this.levelTree;
        if (result == null && !isEditor() && getResourceKind() == ResourceKind.RULE) {
            result = this.levelTree = new RuleLevelTree(getCanvas());
        }
        return result;
    }

    private RuleLevelTree levelTree;
}
