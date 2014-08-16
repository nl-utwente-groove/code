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
package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JVertex;
import groove.gui.layout.Layouter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Wraps a <tt>Layouter</tt> into an action. Invoking the action comes down to
 * starting the layout.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayoutAction extends AbstractAction {
    /** Constructs a layout action for a given layouter. */
    public LayoutAction(JGraph<?> jGraph) {
        super(jGraph.getLayouter().getName(), Icons.LAYOUT_ICON);
        putValue(ACCELERATOR_KEY, Options.LAYOUT_KEY);
        this.jGraph = jGraph;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.jGraph.isEnabled()) {
            doLayout();
        }
    }

    /**
     * Starts the actual layouter.
     */
    public void doLayout() {
        // only layout selected cells, if there are any
        Object[] selection = this.jGraph.getSelectionCells();
        this.jGraph.getModel().setLayoutable(selection.length == 0);
        for (Object jCell : selection) {
            if (jCell instanceof JVertex) {
                ((JVertex<?>) jCell).setLayoutable(true);
            }
        }
        getLayouter().start();
    }

    /**
     * Overwrites the method so as to query the underlying layouter for the
     * text.
     */
    @Override
    public Object getValue(String key) {
        if (key.equals(NAME)) {
            return getLayouter().getName();
        } else {
            return super.getValue(key);
        }
    }

    private Layouter getLayouter() {
        return this.jGraph.getLayouter();
    }

    /** The JGraph on which this action works. */
    private final JGraph<?> jGraph;
}
