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
package groove.gui.tree;

import groove.gui.look.Values;
import groove.io.HTMLConverter;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Cell renderer for display trees.
 */
class DisplayTreeCellRenderer extends DefaultTreeCellRenderer {
    DisplayTreeCellRenderer(Component displayList) {
        this.displayList = displayList;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        boolean cellSelected = isSelected || hasFocus;
        boolean cellFocused = cellSelected && this.displayList.isFocusOwner();
        Component result =
            super.getTreeCellRendererComponent(tree, value, cellSelected, expanded, leaf, row,
                false);
        Icon icon = null;
        String tip = null;
        String text = value.toString();
        boolean enabled = true;
        boolean error = false;
        boolean inRecipe = false;
        if (value instanceof DisplayTreeNode) {
            DisplayTreeNode node = (DisplayTreeNode) value;
            tip = node.getTip();
            icon = node.getIcon();
            text = node.getText();
            enabled = node.isEnabled();
            error = node.isError();
            inRecipe = node.inRecipe();
        }
        if (icon != null) {
            setIcon(icon);
        }
        setText(text == null ? null : HTMLConverter.HTML_TAG.on(text));
        setToolTipText(tip);
        Values.ColorSet colors =
            inRecipe ? Values.RECIPE_COLORS : error ? Values.ERROR_COLORS : Values.NORMAL_COLORS;
        Color background = colors.getBackground(cellSelected, cellFocused);
        Color foreground = colors.getForeground(cellSelected, cellFocused);
        setForeground(enabled ? foreground : transparent(foreground));
        if (background == Color.WHITE) {
            background = null;//this.displayList.getBackground();
        }
        if (cellSelected) {
            setBackgroundSelectionColor(background);
        } else {
            setBackgroundNonSelectionColor(background);
        }
        setOpaque(false);
        return result;
    }

    /** Returns a transparent version of a given colour. */
    private Color transparent(Color c) {
        if (c.equals(Color.WHITE)) {
            return c;
        } else {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), 125);
        }
    }

    /**
     * The component for which this is the renderer.
     */
    private final Component displayList;
}