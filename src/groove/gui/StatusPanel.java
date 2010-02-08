/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * A panel that is extended with a boolean flag.
 */
public class StatusPanel extends JPanel {
    // Additional status variable.
    private Boolean status = true;

    /**
     * Constructor. Does not set status flag (default = true).
     */
    public StatusPanel() {
        super();
    }

    /**
     * Constructor. Does not set status flag (default = true).
     */
    public StatusPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Getter for the status flag.
     * @return the current value of the status flag
     */
    public Boolean getStatus() {
        return this.status;
    }

    /**
     * Setter for the status flag.
     * @param status - new value for the status flag
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }
}
