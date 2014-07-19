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
package groove.gui.jgraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.border.LineBorder;

/**
 * Border that uses a given dash pattern (more precisely, {@link Stroke}).
 * @author Arend Rensink
 * @version $Revision$
 */
public class StrokedLineBorder extends LineBorder {
    /** The default stroke for the border. */
    static private final Stroke DEFAULT_STROKE = new BasicStroke();

    /** Constructs a border with a given colour. */
    public StrokedLineBorder(Color c) {
        this(c, DEFAULT_STROKE);
    }

    /** Constructs a border with a given colour and stroke. */
    public StrokedLineBorder(Color c, Stroke s) {
        super(c, (int) ((BasicStroke) s).getLineWidth());
        this.stroke = s;
    }

    /**
     * Overrides the super method to set the stroke first.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(this.stroke);

        Color oldColor = g.getColor();
        int i = this.thickness / 2;
        g.setColor(this.lineColor);
        if (!this.roundedCorners) {
            g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
        } else {
            g.drawRoundRect(x + i, y + i, width - i - i - 1,
                height - i - i - 1, this.thickness, this.thickness);
        }
        g.setColor(oldColor);
    }

    /** The stroke set for this border. */
    private final Stroke stroke;
}
