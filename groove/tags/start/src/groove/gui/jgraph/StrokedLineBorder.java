/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: StrokedLineBorder.java,v 1.1.1.2 2007-03-20 10:42:47 kastenberg Exp $
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
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class StrokedLineBorder extends LineBorder {
    static private final Stroke DEFAULT_STROKE = new BasicStroke();

    public StrokedLineBorder(Color c) {
        this(c, DEFAULT_STROKE);
    }

    public StrokedLineBorder(Color c, Stroke s) {
        super(c, (int) ((BasicStroke) s).getLineWidth());
        this.stroke = s;
    }

    /** Overrides the super method to set the stroke first. 
    * @param c the component for which this border is being painted
    * @param g the paint graphics
    * @param x the x position of the painted border
    * @param y the y position of the painted border
    * @param width the width of the painted border
    * @param height the height of the painted border
    */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(stroke);

        Color oldColor = g.getColor();
        int i = thickness / 2;
        g.setColor(lineColor);
        if (!roundedCorners)
            g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
        else
            g.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, thickness, thickness);
        g.setColor(oldColor);
    }

    private final Stroke stroke;
}
