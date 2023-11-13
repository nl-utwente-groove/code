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
package nl.utwente.groove.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

/**
 * Mouse adapter that will cause tool tips to stay up (essentially) forever.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LongToolTipAdapter extends MouseAdapter {
    /** Constructs an adapter for a given source object. */
    public LongToolTipAdapter(Object source) {
        this.source = source;
    }

    private final Object source;

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == this.source) {
            this.manager.setDismissDelay(Integer.MAX_VALUE);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == this.source) {
            this.manager.setDismissDelay(this.standardDelay);
        }
    }

    private final ToolTipManager manager = ToolTipManager.sharedInstance();
    private final int standardDelay = this.manager.getDismissDelay();
}
