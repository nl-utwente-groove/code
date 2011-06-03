/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui;

import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.TypeGraph;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJGraph;
import groove.io.HTMLConverter;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.util.Set;

import javax.swing.JToolBar;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<AspectJGraph> implements
        SimulatorListener {
    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.TYPE), false);
        setFocusable(false);
        initialise();
        setEnabled(false);
    }

    @Override
    protected JToolBar createToolBar() {
        return null;
    }

    @Override
    protected TabLabel createTabLabel() {
        return new TabLabel(this, Icons.TYPE_MODE_ICON, "");
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
    }

    @Override
    protected String getStatusText() {
        StringBuilder result = new StringBuilder();
        if (!isEnabled()) {
            result.append("No type graph selected");
        } else {
            AspectGraph type = getGraph();
            String typeName = type.getName();
            result.append("Type graph: ");
            result.append(HTMLConverter.STRONG_TAG.on(typeName));
            if (!GraphProperties.isEnabled(type)) {
                result.append(" (inactive)");
            }
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            GrammarModel grammar = source.getGrammar();
            if (grammar != null) {
                // set either the type or the label store of the associated JGraph
                TypeGraph type;
                try {
                    type = grammar.toModel().getType();
                } catch (FormatException e) {
                    type = null;
                }
                if (type == null) {
                    getJGraph().setLabelStore(grammar.getLabelStore());
                } else {
                    getJGraph().setType(type, null);
                }
            }
        }
    }

    /** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";
}
