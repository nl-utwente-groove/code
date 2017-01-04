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
package groove.fx;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

/**
 * @author rensink
 * @version $Revision $
 */
public class SimulatorController {
    @FXML private MenuItem undoButton;

    @FXML
    private void printUndo(ActionEvent evt) {
        System.out.println("Undo pressed");
    }

    private int counter;

    @FXML
    private void refreshEditMenu(Event evt) {
        System.out.println("Edit menu shown");
        this.counter++;
        this.undoButton.setText("Undo " + this.counter);
    }
}
