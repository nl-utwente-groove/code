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
package groove.gui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Menu specialisation with functionality to add submenus and copy items from other menus.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MyJMenu extends JMenu {
    /**
     * Constructs an unnamed, empty menu.
     */
    public MyJMenu() {
        // empty
    }

    /**
     * Constructs a named, empty menu.
     */
    public MyJMenu(String s) {
        super(s);
    }

    /** 
     * Adds a submenu with all the items of another menu.
     * Precedes the new menu with a separator if it is nonempty.
     * The submenu may be {@code null}, in which case nothing is added 
     * @param submenu the menu to be added to the popup menu;
     * will be destroyed as a consequence of this method call
     */
    final public void addSubmenu(JMenu submenu) {
        if (submenu != null && submenu.getItemCount() > 0) {
            // add a separator if this is not the first submenu
            if (getItemCount() > 0) {
                addSeparator();
            }
            addMenuItems(submenu);
        }
    }

    /** 
     * Adds to a given menu all the items of another menu.
     * The submenu may be {@code null}, in which case nothing is added 
     * @param submenu the menu to be added to the popup menu;
     * will be destroyed as a consequence of this method call
     */
    final public void addMenuItems(JMenu submenu) {
        if (submenu != null && submenu.getItemCount() > 0) {
            // as we move items from the submenu to the main menu
            // the submenu gets modified
            while (submenu.getItemCount() > 0) {
                JMenuItem item = submenu.getItem(0);
                if (item == null) {
                    submenu.remove(0);
                    addSeparator();
                } else {
                    add(item);
                }
            }
        }
    }

}
