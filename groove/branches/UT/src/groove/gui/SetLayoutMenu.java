// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: SetLayoutMenu.java,v 1.1.1.2 2007-03-20 10:42:45 kastenberg Exp $
 */
package groove.gui;

import groove.gui.jgraph.*;
import groove.gui.layout.*;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.SpringLayouter;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu to choose between layout actions,
 * and offering a menu item that displays the currently set action.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class SetLayoutMenu extends JMenu {
    /**
     * Constructs a layout menu and initializes it with a given layout action.
     * The initial layout action is set on the jgraph.
     * @param jgraph the jgraph upon which the actions in this menu should work
     * @param initialLayout the initial (only) layout action in the menu
     */
    public SetLayoutMenu(JGraph jgraph, Layouter initialLayout) {
        super(Options.SET_LAYOUT_MENU_NAME);
        this.jgraph = jgraph;
        addLayoutItem(initialLayout);
    }

    /**
     * Constructs a layout menu with <tt>SpringLayout</tt> and <tt>ForestLayout</tt>.
     * @param jgraph the jgraph upon which the actions in this menu should work
     */
    public SetLayoutMenu(JGraph jgraph) {
        this(jgraph, new SpringLayouter());
        addLayoutItem(new ForestLayouter());
    }

    /** Returns the layout item describing the currently selected layout action. */
    public JMenuItem getCurrentLayoutItem() {
        return layoutItem;
    }

    /**
     * Creates a new menu item for setting a given layout action,
     * and adds it to the menu. Also returns the item as a result.
     * @param prototypeLayout the layout action for the new menu item
     * @return the new menu item
     */
    public JMenuItem addLayoutItem(Layouter prototypeLayout) {
        JMenuItem result = add(createLayoutItem(prototypeLayout));
        if (getItemCount() == 1) {
            selectLayoutAction(prototypeLayout);
        }
        return result;
    }

    /**
     * Sets a given layouter for the jgraph and returns the corresponding layout action.
     * Also notifies the underlying jgraph.
     * @param prototypeLayout the new layout action
     */
    public LayoutAction selectLayoutAction(Layouter prototypeLayout) {
        jgraph.setLayouter(prototypeLayout);
        LayoutAction result = new LayoutAction(jgraph.getLayouter());
        layoutItem.setAction(result);
        for (int i = 0; i < getMenuComponentCount(); i++) {
            Component item = getMenuComponent(i);
            if (item instanceof LayoutItem) {
                LayoutItem layoutItem = (LayoutItem) item;
                layoutItem.setSelected(layoutItem.layouter.getName().equals(prototypeLayout.getName()));
            }
        }
        return result;
//        jgraph.doLayout();
    }

    /**
     * Factory method to create a checkable menu item to select a layouter.
     */
    protected LayoutItem createLayoutItem(Layouter prototypeLayout) {
        return new LayoutItem(prototypeLayout);
    }

    /**
     * Menu item class to select a layouter.
     */
    private class LayoutItem extends JCheckBoxMenuItem {
    	/** Constructs a new instance with a given layouter. */
        private LayoutItem(Layouter layouter) {
            this.layouter = layouter;
            setAction(new AbstractAction(layouter.getName()) {
                public void actionPerformed(ActionEvent evt) {
                    selectLayoutAction(LayoutItem.this.layouter).actionPerformed(null);
                }
            });
        }

        /** The layouter to be set by this item. */
        private final Layouter layouter;
    }

    /** The j-graph to be layoed out. */
    private final JGraph jgraph;
    
    /** Menu item whose label reflects the currently selected layouter. */
    private final JMenuItem layoutItem = new JMenuItem() {
        public String getText() {
            Action action = getAction();
            if (action == null) {
                return super.getText();
            } else {
                return (String) action.getValue(Action.NAME);
            }
        }
    };
}