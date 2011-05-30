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
package groove.gui;

import groove.gui.DisplaysPanel.DisplayKind;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Component that can appear on a tab in the {@link SimulatorModel}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Display {
    /** Main panel of this tab; typically this is {@code this}. */
    JComponent getPanel();

    /** List panel corresponding to this tab; may be {@code null}. */
    JPanel getListPanel();

    /** Tab kind of this component. */
    DisplayKind getKind();

    /**
     * Returns the name of the item currently showing in this
     * panel; or {@code null} if there is nothing showing, or there is
     * nothing to select.
     */
    String getName();
}
