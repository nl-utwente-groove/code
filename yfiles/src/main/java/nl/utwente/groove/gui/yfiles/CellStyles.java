/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.gui.yfiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.UIManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.HtmlLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.view.Pen;

import nl.utwente.groove.gui.look.EdgeEnd;
import nl.utwente.groove.gui.look.MultiLabel;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Fonts;
import nl.utwente.groove.util.NodeShape;
import nl.utwente.groove.util.line.HTMLLineFormat;

/**
 * Translation of GROOVE's visual model ({@link VisualMap}) into yFiles styles and
 * style parameters: the shared node and edge styles, the label styles per font style,
 * arrows, pens, the HTML text of labels, and the text-fitted size of nodes.
 * The numbers reproduce the JGraph rendering: the insets around the node text, the
 * space kept around the visible node border for the emphasis and error overlays, and
 * the line-width increment of emphasised cells.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class CellStyles {
    /** Not instantiated: this class has static members only. */
    private CellStyles() {
        // no instances
    }

    /**
     * Space kept around the visible node border, in which the emphasis and error
     * overlays are painted. It is part of the rendered node size (the facade's
     * {@code TEXT_SIZE}) but not of the node layout.
     */
    static final int EXTRA_BORDER_SPACE = 6;
    /** Line width increment of emphasised (selected) cells. */
    static final float EMPH_INCREMENT = 2f;
    /** Width of the inner line of nodes and edges that have one. */
    static final float INNER_LINE_WIDTH = 2f;
    /** Size of the inscription of a node without text. */
    static final Dimension DEFAULT_NODE_SIZE = new Dimension(19, 19);
    /** Size of the inscription of a nodified edge. */
    static final Dimension NODE_EDGE_SIZE = new Dimension(6, 6);
    /** Insets around the text of a node with text. */
    static final Insets DEFAULT_INSETS = new Insets(2, 4, 2, 4);
    /** Height of the parameter and identifier adornments of a node. */
    static final int ADORNMENT_HEIGHT = 12;
    /** Colour of the dashed selection border around emphasised cells. */
    static final Color SELECTION_COLOR = selectionColor();
    /** Stroke of the dashed selection border around emphasised cells. */
    static final Stroke SELECTION_STROKE
        = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
            new float[] {3f, 3f}, 0f);

    /**
     * Returns the colour of the dashed selection border, as the look and feel defines it
     * ({@code Tree.selectionBorderColor}), or blue if it defines none.
     */
    private static Color selectionColor() {
        Color result = UIManager.getColor("Tree.selectionBorderColor");
        return result == null
            ? Color.BLUE
            : result;
    }

    /** The node style shared by all nodes; it reads its parameters from the node's cell. */
    static final CellNodeStyle NODE_STYLE = new CellNodeStyle();
    /** The edge style shared by all edges; its renderer reads its parameters from the edge's cell. */
    static final PolylineEdgeStyle EDGE_STYLE = new PolylineEdgeStyle(new CellEdgeStyleRenderer());

    /** Returns the font used for the adornments of nodes. */
    static Font adornmentFont() {
        return Fonts.getLabelFont();
    }

    /**
     * Returns the label style for a given font style ({@link Font#PLAIN}, {@link Font#BOLD},
     * {@link Font#ITALIC} or their combination). Labels are HTML-formatted, as in the
     * JGraph backend; the font style comes from the label font.
     */
    static HtmlLabelStyle nodeLabelStyle(int fontStyle) {
        int key = fontStyle & (Font.BOLD | Font.ITALIC);
        @Nullable HtmlLabelStyle result = nodeLabelStyles[key];
        if (result == null) {
            result = new HtmlLabelStyle(Fonts.getLabelFont().deriveFont(key), Color.BLACK);
            result.setInsets(InsetsD.EMPTY);
            result.setAutoFlippingEnabled(false);
            nodeLabelStyles[key] = result;
        }
        return result;
    }

    /** The node label styles, indexed by font style. */
    private static final @Nullable HtmlLabelStyle[] nodeLabelStyles = new HtmlLabelStyle[4];

    /**
     * Returns the style of edge labels: the label font in the style of the font visual,
     * on an opaque box in the background colour of the canvas with a one-pixel margin, so
     * that the edge does not show through the label.
     */
    static HtmlLabelStyle edgeLabelStyle(int fontStyle, Color background) {
        var key = new EdgeLabelKey(fontStyle & (Font.BOLD | Font.ITALIC), background);
        @Nullable HtmlLabelStyle result = edgeLabelStyles.get(key);
        if (result == null) {
            result = new HtmlLabelStyle(Fonts.getLabelFont().deriveFont(key.fontStyle()),
                Color.BLACK);
            result.setInsets(new InsetsD(EDGE_LABEL_MARGIN));
            result.setBackgroundPaint(background);
            result.setAutoFlippingEnabled(false);
            edgeLabelStyles.put(key, result);
        }
        return result;
    }

    /** Key of the edge label style cache. */
    private record EdgeLabelKey(int fontStyle, Color background) {
        // no additional functionality
    }

    /** The edge label styles, indexed by font style and canvas background colour. */
    private static final Map<EdgeLabelKey,HtmlLabelStyle> edgeLabelStyles = new HashMap<>();
    /** Margin around the text of an edge label, as in the JGraph backend. */
    private static final int EDGE_LABEL_MARGIN = 1;

    /** Creates a stroke with a given line width and dash pattern, as the JGraph backend does. */
    static Stroke stroke(float width, float @Nullable [] dash) {
        return isDashed(dash)
            ? new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
                1.0f)
            : new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    }

    /** Tests if a dash pattern describes a dashed line rather than a solid one. */
    static boolean isDashed(float @Nullable [] dash) {
        return dash != null && dash.length >= 2 && dash[1] > 0;
    }

    /**
     * Returns the yFiles pen for the shaft of an edge with given visuals. The pen of a
     * dashed edge is transparent: yFiles pens take dash patterns in units of the line
     * width and cap every dash, which cannot render GROOVE's absolute patterns on thick
     * edges, so the renderer paints the dashes itself with {@link #stroke}. The pen still
     * carries the width, for hit testing.
     */
    static Pen pen(VisualMap visuals) {
        return new Pen(isDashed(visuals.getDash())
            ? TRANSPARENT
            : visuals.getForeground(), strokeWidth(visuals));
    }

    /** Returns the width of the shaft of an edge with given visuals, emphasis included. */
    static float strokeWidth(VisualMap visuals) {
        float result = visuals.getLineWidth();
        if (visuals.isEmphasised()) {
            result += EMPH_INCREMENT;
        }
        return result;
    }

    /** A fully transparent colour. */
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    /**
     * Returns the arrow head for an edge end of a given kind and colour, on an edge of a
     * given line width. The head has the size of the end for its length, and is at least
     * as wide, but wider than the shaft by a margin on either side.
     */
    static IArrow arrow(EdgeEnd end, Color color, float lineWidth) {
        if (end == EdgeEnd.NONE) {
            return IArrow.NONE;
        }
        var key = new ArrowKey(end, color, lineWidth);
        @Nullable IArrow result = arrows.get(key);
        if (result == null) {
            result = createArrow(end, color, lineWidth);
            arrows.put(key, result);
        }
        return result;
    }

    /** Key of the arrow cache. */
    private record ArrowKey(EdgeEnd end, Color color, float lineWidth) {
        // no additional functionality
    }

    /** The cache of arrow heads, keyed by the properties that determine them. */
    private static final Map<ArrowKey,IArrow> arrows = new HashMap<>();

    /** Margin by which an arrow head is wider than its shaft, on either side. */
    private static final double ARROW_MARGIN = 3;

    /**
     * Creates the arrow head returned by {@link #arrow}, which caches the result; see there
     * for the sizing. The kind of the end determines the shape of the head, and may not be
     * {@link EdgeEnd#NONE}.
     */
    private static IArrow createArrow(EdgeEnd end, Color color, float lineWidth) {
        CellArrow.Shape shape = switch (end) {
        case ARROW, UNFILLED -> CellArrow.Shape.CLASSIC;
        case SUBTYPE -> CellArrow.Shape.TRIANGLE;
        case COMPOSITE -> CellArrow.Shape.DIAMOND;
        case NESTING, SIMPLE -> CellArrow.Shape.SIMPLE;
        case NONE -> throw new IllegalArgumentException();
        };
        double length = end.getSize();
        double width = Math.max(length, lineWidth + 2 * ARROW_MARGIN);
        return new CellArrow(shape, length, width, color, end.isFilled(), lineWidth);
    }

    /** Returns the HTML rendering of a label in a given colour, between given edge end points. */
    static String labelHtml(MultiLabel label, Color foreground, @Nullable Point2D start,
                            @Nullable Point2D end) {
        return HTMLLineFormat
            .toHtml(label.toString(HTMLLineFormat.instance(), start, end), foreground);
    }

    /**
     * Measures the size of a given HTML text in a given font style, according to the
     * label style's own renderer. Sizes are cached by text, with digits unified.
     */
    static SizeD measure(String html, int fontStyle) {
        String key = convertDigits(html);
        var byStyle = textSizes.computeIfAbsent(key, k -> new HashMap<>());
        @Nullable SizeD cached = byStyle.get(fontStyle);
        SizeD result;
        if (cached == null) {
            HtmlLabelStyle style = nodeLabelStyle(fontStyle);
            SimpleLabel label = new SimpleLabel(MEASURING_OWNER, html, InteriorLabelModel.CENTER);
            label.setStyle(style);
            result = style.getRenderer().getPreferredSize(label, style);
            byStyle.put(fontStyle, result);
        } else {
            result = cached;
        }
        return result;
    }

    /**
     * Measures the size of a given HTML text in a given label style, according to the
     * style's own renderer. Sizes are cached by style and text, with digits unified.
     */
    static SizeD measure(String html, HtmlLabelStyle style) {
        var byText = styledTextSizes.computeIfAbsent(style, k -> new HashMap<>());
        String key = convertDigits(html);
        @Nullable SizeD cached = byText.get(key);
        SizeD result;
        if (cached == null) {
            SimpleLabel label = new SimpleLabel(MEASURING_OWNER, html, InteriorLabelModel.CENTER);
            label.setStyle(style);
            result = style.getRenderer().getPreferredSize(label, style);
            byText.put(key, result);
        } else {
            result = cached;
        }
        return result;
    }

    /** Owner of the labels used for measuring. */
    private static final SimpleNode MEASURING_OWNER = new SimpleNode();
    /** Cache of measured text sizes, by label style and (unified) text. */
    private static final Map<HtmlLabelStyle,Map<String,SizeD>> styledTextSizes
        = new IdentityHashMap<>();
    /** Cache of measured text sizes, by (unified) text and font style. */
    private static final Map<String,Map<Integer,SizeD>> textSizes = new HashMap<>();

    /**
     * Converts all digits in a string in the range 2-9 to 0, outside HTML tags and
     * entities. This does not affect the size of the string but unifies the keys of
     * the size cache (taken from the JGraph backend).
     */
    private static String convertDigits(String original) {
        char[] array = original.toCharArray();
        boolean htmlTag = false;
        boolean htmlChar = false;
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (htmlChar) {
                htmlChar = c != ';';
            } else if (c == '&') {
                htmlChar = true;
            } else if (htmlTag) {
                htmlTag = c != '>';
            } else if (c == '<') {
                htmlTag = true;
            } else if ('2' <= c && c <= '9') {
                array[i] = '0';
            }
        }
        return String.valueOf(array);
    }

    /** Returns the width of an adornment text in the adornment font; cached. */
    static int adornmentWidth(String text) {
        @Nullable Integer result = adornmentWidths.get(text);
        if (result == null) {
            var metrics = MEASURING_OWNER_COMPONENT.getFontMetrics(adornmentFont());
            result = metrics.stringWidth(text);
            adornmentWidths.put(text, result);
        }
        return result;
    }

    /** Component providing the font metrics of the adornment font. */
    private static final javax.swing.JLabel MEASURING_OWNER_COMPONENT = new javax.swing.JLabel();
    /** Cache of adornment widths by text. */
    private static final Map<String,Integer> adornmentWidths = new HashMap<>();

    /**
     * Returns the HTML text of the inscription of a vertex; empty for a vertex without
     * text and for a nodified edge.
     */
    static String nodeHtml(ViewVertex<?> vertex) {
        VisualMap visuals = vertex.getVisuals();
        MultiLabel label = visuals.getLabel();
        return label == null
            ? ""
            : labelHtml(label, visuals.getForeground(), null, null);
    }

    /** Indicates if a vertex is rendered as a nodified edge: a small box without text. */
    static boolean isNodified(ViewVertex<?> vertex) {
        return vertex.getVisuals().getLabel() == null;
    }

    /**
     * Returns the visible size of a vertex: its text-fitted inscription plus the
     * insets that its shape and adornments require. As a side effect, supplies the
     * inscription size ({@link VisualKey#NODE_SIZE}) and the rendered size
     * ({@link VisualKey#TEXT_SIZE}, the visible size plus the extra border space)
     * under the staleness protocol of the cell.
     */
    static Dimension2D nodeSize(ViewVertex<?> vertex) {
        VisualMap visuals = vertex.getVisuals();
        boolean nodified = isNodified(vertex);
        String html = nodified
            ? ""
            : nodeHtml(vertex);
        Dimension2D text;
        if (vertex.isStale(VisualKey.NODE_SIZE)) {
            Dimension size = new Dimension();
            if (nodified) {
                size.setSize(NODE_EDGE_SIZE);
            } else if (html.isEmpty()) {
                size.setSize(DEFAULT_NODE_SIZE);
            } else {
                SizeD measured = measure(html, visuals.getFont());
                size.setSize(Math.ceil(measured.getWidth()), Math.ceil(measured.getHeight()));
            }
            vertex.putVisual(VisualKey.NODE_SIZE, size);
            text = size;
        } else {
            text = visuals.getNodeSize();
        }
        Insets insets = insets(visuals, text, !html.isEmpty());
        Dimension result = new Dimension();
        result
            .setSize(text.getWidth() + insets.left + insets.right,
                     text.getHeight() + insets.top + insets.bottom);
        if (vertex.isStale(VisualKey.TEXT_SIZE)) {
            Dimension rendered = new Dimension();
            rendered
                .setSize(result.getWidth() + 2 * EXTRA_BORDER_SPACE,
                         result.getHeight() + 2 * EXTRA_BORDER_SPACE);
            vertex.putVisual(VisualKey.TEXT_SIZE, rendered);
        }
        return result;
    }

    /**
     * Returns the rendered size of a vertex: the visible size plus the extra border
     * space on all sides, which is what the JGraph backend reports as preferred size
     * and what the backend-independent layouters space nodes by.
     */
    static Dimension2D renderedSize(ViewVertex<?> vertex) {
        nodeSize(vertex);
        return vertex.getVisuals().getTextSize();
    }

    /**
     * Computes the insets around the inscription of a node, from its shape, its
     * adornments and its predefined inset (taken from the JGraph vertex renderer).
     */
    private static Insets insets(VisualMap visuals, Dimension2D textSize, boolean hasText) {
        Insets result = hasText
            ? (Insets) DEFAULT_INSETS.clone()
            : new Insets(0, 0, 0, 0);
        String parAdornment = visuals.getParAdornment();
        int parWidth = parAdornment == null
            ? 0
            : adornmentWidth(parAdornment);
        String idAdornment = visuals.getIdAdornment();
        int idWidth = idAdornment == null
            ? 0
            : adornmentWidth(idAdornment);
        result.left += Math.max(0, parWidth - 6);
        int inset = visuals.getInset();
        result.left += inset;
        result.right += inset;
        result.top += inset;
        result.bottom += inset;
        int textWidth = (int) textSize.getWidth();
        int textHeight = (int) textSize.getHeight();
        switch (visuals.getNodeShape()) {
        case ELLIPSE:
            result.left += textWidth / 8;
            result.right += textWidth / 8;
            result.top += textHeight / 8;
            result.bottom += textHeight / 8;
            break;
        case HEXAGON:
            result.left += textHeight * NodeShape.HEX_EXTEND_RATIO - inset;
            result.right += textHeight * NodeShape.HEX_EXTEND_RATIO - inset;
            break;
        case DIAMOND:
            result.left += textWidth / 3;
            result.right += textWidth / 3;
            result.top += textHeight / 3;
            result.bottom += textHeight / 3;
            break;
        case RECTANGLE, ROUNDED:
            if (idAdornment != null) {
                result.top += 2;
            }
            break;
        case OVAL:
            result.left += Values.STRONG_ARC_SIZE / 6;
            result.right += Values.STRONG_ARC_SIZE / 6;
            break;
        default:
            // no adjustments
        }
        result.right += Math
            .max(0, idWidth - result.right - (int) visuals.getLineWidth() - textWidth / 2 - 2);
        return result;
    }
}
