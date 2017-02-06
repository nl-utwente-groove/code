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
package groove.gui.display;

import groove.grammar.GrammarKey;
import groove.grammar.GrammarProperties;
import groove.grammar.model.GrammarModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.dialog.PropertiesTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 * Display class for system properties.
 * @author rensink
 * @version $Revision $
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
                getActions().getEditSystemPropertiesAction()
                    .execute();
            }
        }
    }
}
