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
package nl.utwente.groove.gui.tree;

import javax.swing.JCheckBox;

import nl.utwente.groove.gui.Icons;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class JCheckBoxPassive extends JCheckBox {
    /**
     * Creates a checkbox with a correct icon.
     */
    public JCheckBoxPassive() {
        super(Icons.CHECKBOX_PASSIVE_ICON);
    }

    /** Indicates if this checkbox is currently passive. */
    public boolean isPassive() {
        return this.passive;
    }

    /** Changes the passive state of this checkbox.
     * @return {@code true} if the passive state was changed as a result of this call.
     */
    public boolean setPassive(boolean passive) {
        boolean oldPassive = passive;
        this.passive = passive;
        return oldPassive != passive;
    }

    private boolean passive;
}
