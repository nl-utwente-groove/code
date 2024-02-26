/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.jgraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import nl.utwente.groove.gui.Options;

/**
 * Class of constant definitions.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JAttr {
    static {
        Options.initLookAndFeel();
    }

    /**
     * The line width used for edges and node borders.
     */
    public static final int DEFAULT_LINE_WIDTH = 1;
    /**
     * The inner line width for adders and final states.
     */
    public static final int INNER_LINE_WIDTH = 2;

    /** Default background for active resources. */
    public static final Color ACTIVE_BACKGROUND = Color.WHITE;

    /** Default background for inactive resources. */
    public static final Color INACTIVE_BACKGROUND = new Color(245, 245, 245);

    /** Default background for editor panels. */
    public static final Color EDITOR_BACKGROUND = new Color(255, 255, 230);

    /** Default background for state panels. */
    public static final Color STATE_BACKGROUND = new Color(242, 250, 254);

    /** Error background for panels. */
    public static final Color ERROR_BACKGROUND = new Color(255, 242, 242);

    /** Background for internal panels. */
    public static final Color INTERNAL_BACKGROUND = new Color(250, 245, 250);

    /** Fully transparent colour. */
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    /** Default background for LTS with filtering. */
    public static final Color FILTER_BACKGROUND = new Color(230, 230, 255);

    /** The size of the rounded corners for rounded-rectangle vertices. */
    public static final int NORMAL_ARC_SIZE = 5;

    /** The size of the rounded corners for strongly rounded-rectangle vertices. */
    public static final int STRONG_ARC_SIZE = 20;

    /**
     * The standard bounds used for nodes.
     */
    public static final Rectangle DEFAULT_NODE_BOUNDS = new Rectangle(10, 10, 19, 19);

    /**
     * The standard size used for nodes.
     */
    public static final Dimension DEFAULT_NODE_SIZE
        = new Dimension(DEFAULT_NODE_BOUNDS.width, DEFAULT_NODE_BOUNDS.height);

    /** Space left outside the borders of nodes to enable larger
     * error or emphasis overlays to be painted correctly.
     * This also influences the initial positioning of the nodes
     * (at creation time).
     */
    public static final int EXTRA_BORDER_SPACE = 6;

    /** Node radius for nodified edges. */
    static final public Dimension NODE_EDGE_DIMENSION = new Dimension(6, 6);

    /** The font used for adornment text. */
    public static final Font ADORNMENT_FONT = Options.getLabelFont();
    /** Foreground (= border) colour of the rubber band selector. */
    static public final Color RUBBER_FOREGROUND = new Color(150, 150, 150);
    /** Foreground (= border) colour of the rubber band selector. */
    static public final Color RUBBER_BACKGROUND = new Color(100, 212, 224, 40);

    /** Line width used for emphasised cells. */
    public static final int EMPH_WIDTH = 3;
    /** Difference in line width between emphasised and non-emphasised. */
    public static final float EMPH_INCREMENT = EMPH_WIDTH - DEFAULT_LINE_WIDTH;

    /**
     * Static flag determining if gradient background paint should be used.
     * Gradient paint looks better, but there is a performance hit.
     */
    static final private boolean GRADIENT_PAINT = false;
    /** Key value for vertex shapes in the attribute map. */
    static public String SHAPE_KEY = "vertexShape";

    /** Creates a stroke with a given line width and dash pattern. */
    public static Stroke createStroke(float width, float[] dash) {
        Stroke result;
        if (dash == null) {
            result = new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        } else {
            result = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                dash, 1.0f);
        }
        return result;
    }

    /** Paints a hatch pattern over a given component. */
    public static void paintHatch(JComponent component, Graphics g) {
        var g2 = (Graphics2D) g;
        g2.setPaint(createHatchPaint());
        g2.fill(new Rectangle(0, 0, component.getWidth(), component.getHeight()));
    }

    /** Creates a hatch pattern in transparent black. */
    public static Paint createHatchPaint() {
        int size = 30;
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setPaint(new Color(0, 0, 0, 20));
        g2.drawLine(0, 0, size, size);
        Rectangle r = new Rectangle(0, 0, size, size);
        return new TexturePaint(bi, r);
    }

    /** Returns the background colour, depending on the error and transient status of a state. */
    public static Color getStateBackground(boolean error, boolean internal) {
        return error
            ? ERROR_BACKGROUND
            : internal
                ? INTERNAL_BACKGROUND
                : STATE_BACKGROUND;
    }

    /**
     * Creates paint for a vertex with given bounds and (inner) colour.
     */
    static public Paint createPaint(Rectangle b, Color c) {
        // only bother with special paint if the vertex is not too small to notice
        if (!GRADIENT_PAINT || b.width < 10 && b.height < 10) {
            return c;
        } else {
            int cx = b.x + b.width / 2;
            int cy = b.y + b.height / 2;
            int fx = b.x + b.width / 3;
            int fy = b.y + 2 * b.height / 3;
            int rx = b.width - fx;
            int ry = b.height - fy;
            float r = (float) Math.sqrt(rx * rx + ry * ry);
            Paint newPaint = new RadialGradientPaint(cx, cy, r, fx, fy, new float[] {0f, 1f},
                getGradient(c), CycleMethod.NO_CYCLE);
            return newPaint;
        }
    }

    /** Lazily creates and returns the colour gradient derived from a given colour. */
    static private Color[] getGradient(Color c) {
        Color[] result = gradientMap.get(c);
        if (result == null) {
            float factor = .9f;
            Color inC = new Color((int) Math.min(c.getRed() / factor, 255),
                (int) Math.min(c.getGreen() / factor, 255),
                (int) Math.min(c.getBlue() / factor, 255), c.getAlpha());
            Color outC = new Color((int) (c.getRed() * factor), (int) (c.getGreen() * factor),
                (int) (c.getBlue() * factor), c.getAlpha());
            gradientMap.put(c, result = new Color[] {inC, outC});
        }
        return result;
    }

    /** Mapping from colours to colour gradients for {@link #createPaint(Rectangle, Color)}. */
    static private Map<Color,Color[]> gradientMap = new HashMap<>();
}
