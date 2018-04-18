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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

/**
 * Mouse listener that will set the tool tip dismiss delay to
 * infinite whenever a given component is entered.
 */
public final class DismissDelayer extends MouseAdapter {
    /**
     * Constructs a delayer for a given component.
     * @param component the component on which this mouse listener works.
     */
    public DismissDelayer(JComponent component) {
        this.component = component;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == this.component) {
            this.manager.setDismissDelay(Integer.MAX_VALUE);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == this.component) {
            this.manager.setDismissDelay(this.standardDelay);
        }
    }

    /**
     * Component on which this adapter works.
     */
    private final JComponent component;
    /** The shared tool tip manager. */
    private final ToolTipManager manager = ToolTipManager.sharedInstance();
    /** The standard dismiss delay, used to restore upon exit. */
    private final int standardDelay = this.manager.getDismissDelay();
}