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
package groove.gui.menu;

import groove.gui.jgraph.JGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JViewport;
import javax.swing.KeyStroke;

/**
 * Menu for zoomin in/out on a jgraph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ZoomMenu extends JMenu {
    /** The menu name. */
    static public final String ZOOM_MENU_NAME = "Zoom";

    /**
     * Constructs a standard zoom menu with default name.
     * @see #ZOOM_MENU_NAME
     */
    public ZoomMenu(JGraph<?> jgraph) {
        super(ZOOM_MENU_NAME);
        this.jgraph = jgraph;
        add(this.zoomToFitAction);
        add(this.zoomInAction);
        add(this.zoomOutAction);
        add(this.resetZoomAction);
        setMnemonic(MENU_MNEMONIC);
        registerAction(this.zoomInAction, KeyEvent.VK_EQUALS);
        registerAction(this.zoomOutAction, KeyEvent.VK_MINUS);
        registerAction(this.resetZoomAction, KeyEvent.VK_0);
        reset();
    }

    /** Registers a keyboard accelerator for a given action. */
    private void registerAction(Action action, int keyCode) {
        action.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyCode, ActionEvent.CTRL_MASK));
        this.jgraph.addAccelerator(action);
    }

    /**
     * Passes the invocation on to <tt>super</tt> and then calls
     * {@link #reset()}.
     */
    @Override
    public void menuSelectionChanged(boolean isIncluded) {
        super.menuSelectionChanged(isIncluded);
        if (isIncluded) {
            reset();
        }
    }

    /** Resets the menu actions. */
    public void reset() {
        setActionsEnabled();
    }

    /** Enables the menu actions according to the current scaling. */
    protected void setActionsEnabled() {
        // zoomInAction.setEnabled(jgraph.getScale() < 1);
    }

    /**
     * Creates and returns an action to set the zoom factor so the whole graph
     * is displayed, to center the view on this graph, in a given scroll pane.
     */
    protected final Action zoomToFitAction = new AbstractAction("Zoom to fit") {
        @Override
        public void actionPerformed(ActionEvent evt) {
            Component component = ZoomMenu.this.jgraph.getParent();
            while (component != null && !(component instanceof JViewport)) {
                component = component.getParent();
            }
            if (component != null) {
                final JViewport viewport = (JViewport) component;
                Rectangle2D graphBounds = ZoomMenu.this.jgraph.getGraphBounds();
                Dimension viewportBounds = viewport.getExtentSize();
                double scale =
                    Math.min(viewportBounds.width / graphBounds.getWidth(), viewportBounds.height
                        / graphBounds.getHeight());
                ZoomMenu.this.jgraph.setScale(Math.min(scale, 1.0));
                ZoomMenu.this.jgraph.scrollRectToVisible(graphBounds.getBounds());
                setActionsEnabled();
            }
        }
    };

    /** Action for zooming in, i.e., enlarging the figure. */
    protected final Action zoomInAction = new AbstractAction("Zoom in") {
        @Override
        public void actionPerformed(ActionEvent evt) {
            ZoomMenu.this.jgraph.changeScale(1);
            setActionsEnabled();
        }
    };

    /** Action for zooming out, i.e., making the figure smaller. */
    protected final Action zoomOutAction = new AbstractAction("Zoom out") {
        @Override
        public void actionPerformed(ActionEvent evt) {
            ZoomMenu.this.jgraph.changeScale(-1);
            setActionsEnabled();
        }
    };

    /** Action for resetting the zoom factor to the original (1.0). */
    protected final Action resetZoomAction = new AbstractAction("Reset zoom") {
        @Override
        public void actionPerformed(ActionEvent evt) {
            ZoomMenu.this.jgraph.setScale(1.0);
            setActionsEnabled();
        }
    };

    /** The component for which zooming is to be done. */
    final JGraph<?> jgraph;

    /** Mnemonic key for the menu. */
    private static final int MENU_MNEMONIC = KeyEvent.VK_Z;
}
