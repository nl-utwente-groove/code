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
import groove.gui.GraphDisplay.GraphTab;
import groove.io.HTMLConverter;
import groove.view.aspect.AspectGraph;

/**
 * @author Frank van Es
 * @version $Revision $
 */
final public class TypePanel extends GraphTab {
    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(simulator, GraphRole.TYPE);
        initialise();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
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

    /** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";
}
