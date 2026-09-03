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
import nl.utwente.groove.gui.jgraph.AspectJGraph;

/**
 * Action to edit the label of the currently selected j-cell.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EditLabelAction extends JCellEditAction {
    /** Constructs an instance of the action. */
    public EditLabelAction(AspectJGraph jGraph) {
        super(jGraph, Options.EDIT_LABEL_ACTION);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        this.jGraph.startEditingAtCell(this.jCell);
    }
}
