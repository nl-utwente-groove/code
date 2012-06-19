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
 * $Id: LayoutAction.java,v 1.4 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui.action;

import groove.gui.Icons;
import groove.gui.layout.Layouter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Wraps a <tt>Layouter</tt> into an action. Invoking the action comes down to
 * starting the layout.
 * @author Arend Rensink
 * @version $Revision: 3410 $
 */
public class LayoutAction extends AbstractAction {
    /** Constructs a layout action for a given layouter. */
    public LayoutAction(Layouter layouter) {
        super(layouter.getName(), Icons.LAYOUT_ICON);
        this.layouter = layouter;
    }

    public void actionPerformed(ActionEvent e) {
        if (this.layouter.isEnabled()) {
            doLayout();
        }
    }

    /**
     * Starts the actual layouter.
     */
    public void doLayout() {
        this.layouter.start(true);
    }

    /**
     * Overwrites the method so as to query the underlying layouter for the
     * text.
     */
    @Override
    public Object getValue(String key) {
        if (key.equals(NAME)) {
            return this.layouter.getText();
        } else {
            return super.getValue(key);
        }
    }

    /** The layouter of this action. */
    private final Layouter layouter;
}