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
package nl.utwente.groove.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;

/**
 * Checkbox icon that can show an active or passive state.
 * Adapted from {@link com.jgoodies.looks.plastic.PlasticIconFactory#CheckButton}
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class CheckBoxPassiveIcon implements Icon, UIResource, Serializable {
    private CheckBoxPassiveIcon() {
        // empty
    }

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        JCheckBox cb = (JCheckBox) c;
        ButtonModel model = cb.getModel();
        boolean passive = cb instanceof JCheckBoxPassive cbt && cbt.isPassive();

        if (model.isEnabled()) {
            Color checkColor = MetalLookAndFeel.getControlInfo();
            if (passive) {
                g.setColor(PASSIVE_BACKGROUND);
                g.fillRect(x, y, SIZE - 1, SIZE - 1);
                drawPressed3DBorder(g, x, y, SIZE, SIZE);
                checkColor = PASSIVE_FOREGROUND;
            } else if (cb.isBorderPaintedFlat()) {
                g.setColor(PlasticLookAndFeel.getControlDarkShadow());
                g.drawRect(x, y, SIZE - 2, SIZE - 2);
                // inside box
                g.setColor(PlasticLookAndFeel.getControlHighlight());
                g.fillRect(x + 1, y + 1, SIZE - 3, SIZE - 3);
            } else if (model.isPressed() && model.isArmed()) {
                g.setColor(MetalLookAndFeel.getControlShadow());
                g.fillRect(x, y, SIZE - 1, SIZE - 1);
                drawPressed3DBorder(g, x, y, SIZE, SIZE);
            } else {
                drawFlush3DBorder(g, x, y, SIZE, SIZE);
            }
            g.setColor(checkColor);
        } else {
            g.setColor(MetalLookAndFeel.getControlShadow());
            g.drawRect(x, y, SIZE - 2, SIZE - 2);
        }

        if (model.isSelected()) {
            drawCheck(g, x, y);
        }
    }

    /*
     * Copied from {@code MetalUtils}.
     */
    static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        drawFlush3DBorder(g, 0, 0, w, h);
        g.setColor(MetalLookAndFeel.getControlShadow());
        g.drawLine(1, 1, 1, h - 3);
        g.drawLine(1, 1, w - 3, 1);
        g.translate(-x, -y);
    }

    /*
     * Unlike {@code MetalUtils} we first draw with highlight then dark shadow
     */
    static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlHighlight());
        drawRect(g, 1, 1, w - 2, h - 2);
        g.drawLine(0, h - 1, 0, h - 1);
        g.drawLine(w - 1, 0, w - 1, 0);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        drawRect(g, 0, 0, w - 2, h - 2);
        g.translate(-x, -y);
    }

    /*
     * An optimized version of Graphics.drawRect.
     */
    private static void drawRect(Graphics g, int x, int y, int w, int h) {
        g.fillRect(x, y, w + 1, 1);
        g.fillRect(x, y + 1, 1, h);
        g.fillRect(x + 1, y + h, w, 1);
        g.fillRect(x + w, y + 1, 1, h);
    }

    // Helper method utilized by the CheckBoxIcon and the CheckBoxMenuItemIcon.
    private static void drawCheck(Graphics g, int x, int y) {
        g.translate(x, y);
        g.drawLine(3, 5, 3, 5);
        g.fillRect(3, 6, 2, 2);
        g.drawLine(4, 8, 9, 3);
        g.drawLine(5, 8, 9, 4);
        g.drawLine(5, 9, 9, 5);
        g.translate(-x, -y);
    }

    /** Horizontal and vertical dimension of the checkbox. */
    private static final int SIZE = 13;

    private static final Color PASSIVE_BACKGROUND = PlasticLookAndFeel.getPrimaryControl();
    private static final Color PASSIVE_FOREGROUND = PASSIVE_BACKGROUND.darker();

    /** Returns the singleton instance of this class. */
    static public CheckBoxPassiveIcon instance() {
        return INSTANCE;
    }

    private static final CheckBoxPassiveIcon INSTANCE = new CheckBoxPassiveIcon();
}
