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

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.dialog.PropertiesTable;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.Util;

/**
 * Display class for system properties.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PropertiesDisplay extends Display implements SimulatorListener {
    /** Creates a display for a given simulator. */
    public PropertiesDisplay(Simulator simulator) {
        super(simulator, DisplayKind.PROPERTIES);
    }

    @Override
    protected void buildDisplay() {
        // do nothing
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR);
        addMouseListener(new DismissDelayer(this));
    }

    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getActions().getEditSystemPropertiesAction());
        result.add(getActions().getLoadSystemPropertiesAction());
        result.add(getActions().getSaveSystemPropertiesAction());
        return result;
    }

    @Override
    public PropertiesTable getList() {
        return (PropertiesTable) super.getList();
    }

    @Override
    protected PropertiesTable createList() {
        PropertiesTable result = new PropertiesTable(GrammarKey.class, false);
        result.addMouseListener(new EditMouseListener());
        return result;
    }

    @Override
    protected JComponent createInfoPanel() {
        return null;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        GrammarModel grammar = source.getGrammar();
        boolean enabled = grammar != null;
        if (enabled) {
            assert grammar != null; // implied by enabled
            GrammarProperties properties = grammar.getProperties();
            getList().setProperties(properties);
            getList().setCheckerMap(properties.getCheckers(grammar));
        } else {
            getList().resetProperties();
        }
        getListPanel().setEnabled(enabled);
    }

    /** Mouse listener that ensures doubleclicking starts an editor for this display. */
    private final class EditMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                getActions().getEditSystemPropertiesAction().execute();
            }
        }
    }

    /** The tab component to be used for non-notable properties. */
    static public final JLabel NORMAL_TAB_COMPONENT
        = new JLabel(null, DisplayKind.PROPERTIES.getTabIcon(), SwingConstants.LEFT);
    /** The error tab component to be used for non-notable properties. */
    static public final JLabel ERROR_TAB_COMPONENT
        = new JLabel(null, Icons.PROPERTIES_ERROR_FRAME_ICON, SwingConstants.LEFT);
    /** The tab component to be used for a notable property set. */
    static public final JLabel NOTABLE_TAB_COMPONENT = new JLabel("" + Util.INFO_SYMBOL,
        Icons.PROPERTIES_NOTABLE_FRAME_ICON, SwingConstants.LEFT);
    /** The error tab component to be used for a notable property set. */
    static public final JLabel NOTABLE_ERROR_TAB_COMPONENT
        = new JLabel("" + Util.INFO_SYMBOL, Icons.PROPERTIES_ERROR_FRAME_ICON, SwingConstants.LEFT);

    {
        NOTABLE_TAB_COMPONENT.setFont(new Font("Dialog", Font.BOLD, 16));
        NOTABLE_TAB_COMPONENT.setForeground(Values.INFO_NORMAL_FOREGROUND);
        NOTABLE_ERROR_TAB_COMPONENT.setFont(new Font("Dialog", Font.BOLD, 16));
        NOTABLE_ERROR_TAB_COMPONENT.setForeground(Values.INFO_NORMAL_FOREGROUND);
    }

    /** Returns the appropriate tab component for a display that may be notable and erroneous. */
    static public JLabel getTabComponent(boolean notable, boolean error) {
        return notable
            ? (error
                ? NOTABLE_ERROR_TAB_COMPONENT
                : NOTABLE_TAB_COMPONENT)
            : (error
                ? ERROR_TAB_COMPONENT
                : NORMAL_TAB_COMPONENT);

    }
}
