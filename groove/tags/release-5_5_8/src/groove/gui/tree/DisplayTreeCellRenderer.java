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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
            inRecipe = node.isInternal();
        }
        if (icon != null) {
            setIcon(icon);
        }
        setText(text == null ? null : HTMLConverter.HTML_TAG.on(text));
        setToolTipText(tip);
        Values.ColorSet colors =
                inRecipe ? Values.RECIPE_COLORS : error ? Values.ERROR_COLORS : Values.NORMAL_COLORS;
        Color foreground = colors.getForeground(cellSelected, cellFocused);
        setForeground(foreground);
        Color background = colors.getBackground(cellSelected, cellFocused);
        if (background == Color.WHITE) {
            background = null;
        }
        if (cellSelected) {
            setBackgroundSelectionColor(background);
        } else {
            setBackgroundNonSelectionColor(background);
        }
        setTransparent(!enabled);
        setOpaque(false);
        return result;
    }

    /**
     * The component for which this is the renderer.
     */
    private final Component displayList;

    /** Sets the transparency of the node display. */
    private void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    /** Indicates if the node is to be displayed transparently. */
    private boolean isTransparent() {
        return this.transparent;
    }

    private boolean transparent;

    /* Overridden to deal correctly with transparency in HTML. */
    @Override
    protected void paintComponent(Graphics g) {
        if (isTransparent()) {
            Graphics2D g2 = (Graphics2D) g;
            final Composite oldComposite = g2.getComposite();
            g2.setComposite(TRANSPARENT_COMPOSITE);
            super.paintComponent(g);
            g2.setComposite(oldComposite);
        } else {
            super.paintComponent(g);
        }
    }

    /** Transparency value for disabled entries. */
    private static final int TRANSPARANCY = 125;
    /** Transparency composite for disabled entries. */
    private static final Composite TRANSPARENT_COMPOSITE = AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, TRANSPARANCY / 255.0f);
}