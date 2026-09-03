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
package nl.utwente.groove.gui.action;

import java.awt.event.ActionEvent;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.jgraph.AspectJCell;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.look.VisualKey;

/**
 * Action to set the label of the currently selected j-cell to its default
 * position.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ResetLabelPositionAction extends JCellEditAction {
    /** Constructs an instance of the action. */
    public ResetLabelPositionAction(AspectJGraph jGraph) {
        super(jGraph, Options.RESET_LABEL_POSITION_ACTION, false);
    }

    /** Resets the label positions of the selected cells. */
    @Override
    public void actionPerformed(ActionEvent evt) {
        for (AspectJCell jCell : this.jCells) {
            execute(jCell);
        }
    }

    /**
     * Resets the label position of a given a given j-edge to the default
     * position.
     * @param jEdge the j-edge to be modified
     */
    public void execute(AspectJCell jEdge) {
        edit(jEdge, VisualKey.LABEL_POS, VisualKey.LABEL_POS.getDefaultValue());
    }
}
