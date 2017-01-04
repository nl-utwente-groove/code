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
package groove.gui.look;

import groove.gui.look.VisualKey.Nature;
import groove.util.DefaultFixable;
import groove.util.NodeShape;
import groove.util.line.LineStyle;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;

/**
 * Map of visual attributes to corresponding values.
 * The map maintains a JGraph-style attribute map, which is kept in sync.
 */
public class VisualMap extends DefaultFixable {
    /** Constructs a new, empty attribute map. */
    public VisualMap() {
        this.map = new EnumMap<>(VisualKey.class);
        this.attrMap = new VisualAttributeMap(this);
    }

    /** Adds an the value for a controlled (i.e., non-derived) key to the map. */
    public void put(VisualKey key, Object value) {
        assert key.getNature() != Nature.DERIVED;
        put(key, value, true);
    }

    /**
     * Adds an attribute to the map, and on request set the corresponding
     * key in the attribute map to stale.
     * @param refresh if {@code true}, the key value should be marked as changed
     * in the attribute map
     */
    void put(VisualKey key, Object value, boolean refresh) {
        testFixed(false);
        Object oldValue = this.map.get(key);
        boolean change;
        if (oldValue == null) {
            change = value != null;
        } else {
            change = !oldValue.equals(value);
        }
        if (change) {
            key.test(value);
            this.map.put(key, value);
            if (refresh) {
                this.attrMap.setStale(key);
            }
        }
    }

    /** Copies all attributes from another map to this one. */
    public void putAll(VisualMap other) {
        testFixed(false);
        this.map.putAll(other.map);
        this.attrMap.setStale(other.map.keySet());
    }

    /**
     * Copies all non-derived visual attributes
     * from another map to this one.
     */
    public void putNonDerived(VisualMap other) {
        testFixed(false);
        for (VisualKey key : VisualKey.values()) {
            if (key.getNature() != Nature.DERIVED) {
                Object newValue = other.map.get(key);
                if (newValue == null) {
                    this.map.remove(key);
                } else {
                    this.map.put(key, newValue);
                }
            }
            this.attrMap.setStale(key);
        }
    }

    /**
     * Resets all derived attributes,
     * based on a set of looks.
     */
    public void setLooks(Set<Look> looks) {
        testFixed(false);
        VisualMap newValues = Look.getVisualsFor(looks);
        Set<VisualKey> staleKeys = EnumSet.noneOf(VisualKey.class);
        for (VisualKey key : VisualKey.deriveds()) {
            if (key.getNature() == Nature.DERIVED) {
                Object newValue = newValues.map.get(key);
                boolean refresh;
                if (newValue == null) {
                    Object oldValue = this.map.remove(key);
                    refresh = oldValue != null;
                } else {
                    Object oldValue = this.map.put(key, newValue);
                    refresh = !newValue.equals(oldValue);
                }
                // tell the attribute to refresh
                // if something actually changed
                if (refresh) {
                    staleKeys.add(key);
                }
            }
        }
        this.attrMap.setStale(staleKeys);
    }

    /**
     * Returns the value for a given key.
     * If the map contains no value for the key, the default value is returned.
     * @see VisualKey#getDefaultValue()
     */
    public Object get(VisualKey key) {
        Object result;
        if (this.map.containsKey(key)) {
            result = this.map.get(key);
        } else {
            result = key.getDefaultValue();
        }
        return result;
    }

    /** Clears all values from the map. */
    public void clear() {
        testFixed(false);
        this.attrMap.setStale(this.map.keySet());
        this.map.clear();
    }

    /** Returns the set of keys that is currently in the map. */
    public Set<VisualKey> keySet() {
        return this.map.keySet();
    }

    /** Indicates if a given key is in the map. */
    public boolean containsKey(VisualKey key) {
        return this.map.containsKey(key);
    }

    /** Removes a given key from the map. */
    public void remove(VisualKey key) {
        remove(key, true);
    }

    /** Removes a given key from the map, notifying the dependent map
     * if required.
     */
    void remove(VisualKey key, boolean refresh) {
        testFixed(false);
        this.map.remove(key);
        if (refresh) {
            this.attrMap.setStale(key);
        }
    }

    /**
     * Returns the node adornment text stored in this attribute map.
     * @return the stored adornment text (possibly {@code null})
     * @see VisualKey#ADORNMENT
     */
    public String getAdornment() {
        return (String) get(VisualKey.ADORNMENT);
    }

    /**
     * Sets a new adornment value.
     * @see VisualKey#ADORNMENT
     */
    public void setAdornment(String newValue) {
        put(VisualKey.ADORNMENT, newValue);
    }

    /**
     * Returns the node shape stored in this attribute map.
     * @return the stored node shape, or the default value if
     * there is nothing stored.
     * @see VisualKey#NODE_SHAPE
     */
    public NodeShape getNodeShape() {
        return (NodeShape) get(VisualKey.NODE_SHAPE);
    }

    /**
     * Returns the background colour according to this attribute map.
     * If {@link #getColor()} is non-{@code null}, the result is a whitewashed
     * version of that; otherwise, if the stored value for
     * {@link VisualKey#BACKGROUND} is {@code null}, the result is a
     * whitewashed version of the value for {@link VisualKey#FOREGROUND}.
     * In all other circumstances, the value is the stored value for
     * {@link VisualKey#BACKGROUND}.
     * @return the background colour, computed as described above
     * @see VisualKey#BACKGROUND
     */
    public Color getBackground() {
        Color result = null;
        Color whitewash = getColor();
        if (whitewash == null) {
            result = (Color) get(VisualKey.BACKGROUND);
            if (result == null) {
                whitewash = (Color) get(VisualKey.FOREGROUND);
            }
        }
        if (whitewash != null) {
            result = whitewash(whitewash);
        }
        return result;
    }

    /**
     * Returns the controlled colour stored in this attribute map.
     * @return the controlled colour, or {@code null} if
     * there is nothing stored.
     * @see VisualKey#COLOR
     */
    public Color getColor() {
        return (Color) get(VisualKey.COLOR);
    }

    /**
     * Sets a new controlled colour.
     * @see VisualKey#COLOR
     */
    public void setColor(Color newValue) {
        put(VisualKey.COLOR, newValue);
    }

    /**
     * Returns the dash pattern stored in this attribute map.
     * @return the stored dash pattern, or the default value if
     * there is nothing stored.
     * @see VisualKey#DASH
     */
    public float[] getDash() {
        return (float[]) get(VisualKey.DASH);
    }

    /**
     * Returns the edge source decoration stored in this attribute map.
     * @return the stored edge source decoration, or the default value if
     * there is nothing stored.
     * @see VisualKey#EDGE_SOURCE_SHAPE
     */
    public EdgeEnd getEdgeSourceShape() {
        return (EdgeEnd) get(VisualKey.EDGE_SOURCE_SHAPE);
    }

    /**
     * Returns the edge source label stored in this attribute map.
     * @return the stored edge source label; may be {@code null}
     * @see VisualKey#EDGE_SOURCE_LABEL
     */
    public String getEdgeSourceLabel() {
        return (String) get(VisualKey.EDGE_SOURCE_LABEL);
    }

    /**
     * Sets a new edge source label.
     * @see VisualKey#EDGE_SOURCE_LABEL
     */
    public void setEdgeSourceLabel(String newValue) {
        put(VisualKey.EDGE_SOURCE_LABEL, newValue);
    }

    /**
     * Returns the edge source label position stored in this attribute map.
     * @return the stored edge source label position, or the default value if
     * there is nothing stored
     * @see VisualKey#EDGE_SOURCE_POS
     */
    public Point2D getEdgeSourcePos() {
        return (Point2D) get(VisualKey.EDGE_SOURCE_POS);
    }

    /**
     * Sets a new edge source position.
     * @see VisualKey#EDGE_SOURCE_POS
     */
    public void setEdgeSourcePos(Point2D newValue) {
        put(VisualKey.EDGE_SOURCE_POS, newValue);
    }

    /**
     * Returns the edge target decoration stored in this attribute map.
     * @return the stored edge target decoration, or the default value if
     * there is nothing stored
     * @see VisualKey#EDGE_TARGET_SHAPE
     */
    public EdgeEnd getEdgeTargetShape() {
        return (EdgeEnd) get(VisualKey.EDGE_TARGET_SHAPE);
    }

    /**
     * Returns the edge target label stored in this attribute map.
     * @return the stored edge target label; may be {@code null}
     * @see VisualKey#EDGE_TARGET_LABEL
     */
    public String getEdgeTargetLabel() {
        return (String) get(VisualKey.EDGE_TARGET_LABEL);
    }

    /**
     * Sets a new edge target label.
     * @see VisualKey#EDGE_TARGET_LABEL
     */
    public void setEdgeTargetLabel(String newValue) {
        put(VisualKey.EDGE_TARGET_LABEL, newValue);
    }

    /**
     * Returns the edge target label position stored in this attribute map.
     * @return the stored edge target label position, or the default value if
     * there is nothing stored
     * @see VisualKey#EDGE_TARGET_POS
     */
    public Point2D getEdgeTargetPos() {
        return (Point2D) get(VisualKey.EDGE_TARGET_POS);
    }

    /**
     * Sets a new edge target position.
     * @see VisualKey#EDGE_TARGET_POS
     */
    public void setEdgeTargetPos(Point2D newValue) {
        put(VisualKey.EDGE_TARGET_POS, newValue);
    }

    /**
     * Returns the emphasised property stored in this attribute map.
     * @return the stored emphasis, or the default value if
     * there is nothing stored
     * @see VisualKey#EMPHASIS
     */
    public boolean isEmphasised() {
        return (Boolean) get(VisualKey.EMPHASIS);
    }

    /**
     * Sets a new emphasis value.
     * @see VisualKey#EMPHASIS
     */
    public void setEmphasis(boolean newValue) {
        put(VisualKey.EMPHASIS, newValue);
    }

    /**
     * Returns the error property stored in this attribute map.
     * @return the stored emphasis, or the default value if
     * there is nothing stored
     * @see VisualKey#ERROR
     */
    public boolean isError() {
        return (Boolean) get(VisualKey.ERROR);
    }

    /**
     * Sets a new error value.
     * @see VisualKey#ERROR
     */
    public void setError(boolean newValue) {
        put(VisualKey.ERROR, newValue);
    }

    /**
     * Returns the font value stored in this attribute map.
     * @return the stored font value, or the default value if
     * there is nothing stored
     * @see VisualKey#FONT
     */
    public int getFont() {
        return (Integer) get(VisualKey.FONT);
    }

    /**
     * Returns the foreground colour according to this attribute map.
     * This is the value of {@link #getColor()} if non-{@code null},
     * or the stored of default value for {@link VisualKey#FOREGROUND}.
     * @return the foreground colour, computed as described above
     * @see VisualKey#FOREGROUND
     */
    public Color getForeground() {
        Color result = getColor();
        if (result == null) {
            result = (Color) get(VisualKey.FOREGROUND);
        }
        return result;
    }

    /**
     * Returns the optional inner line colour stored in this attribute map.
     * @return the stored inner line colour; may be {@code null}
     * @see VisualKey#INNER_LINE
     */
    public Color getInnerLine() {
        return (Color) get(VisualKey.INNER_LINE);
    }

    /**
     * Returns the inset distance stored in this attribute map.
     * @return the stored inset distance, or the default value if
     * there is nothing stored
     * @see VisualKey#INSET
     */
    public int getInset() {
        return (Integer) get(VisualKey.INSET);
    }

    /**
     * Returns the label text stored in this attribute map.
     * @return the stored label text, or the empty string if
     * there is nothing stored
     * @see VisualKey#LABEL
     */
    public MultiLabel getLabel() {
        return (MultiLabel) get(VisualKey.LABEL);
    }

    /**
     * Sets a new label value.
     * @see VisualKey#LABEL
     */
    public void setLabel(MultiLabel newValue) {
        put(VisualKey.LABEL, newValue);
    }

    /**
     * Returns the label position stored in this attribute map.
     * @return the stored label position, or the empty string if
     * there is nothing stored
     * @see VisualKey#LABEL_POS
     */
    public Point2D getLabelPos() {
        return (Point2D) get(VisualKey.LABEL_POS);
    }

    /**
     * Sets a new label position value.
     * @see VisualKey#LABEL_POS
     */
    public void setLabelPos(Point2D newValue) {
        put(VisualKey.LABEL_POS, newValue);
    }

    /**
     * Returns the line style stored in this attribute map.
     * @return the stored line style, or the default value if
     * there is nothing stored
     * @see VisualKey#LINE_STYLE
     */
    public LineStyle getLineStyle() {
        return (LineStyle) get(VisualKey.LINE_STYLE);
    }

    /**
     * Sets a new line style value.
     * @see VisualKey#LINE_STYLE
     */
    public void setLineStyle(LineStyle newValue) {
        put(VisualKey.LINE_STYLE, newValue);
    }

    /**
     * Returns the line width stored in this attribute map.
     * @return the stored line width, or the default value if
     * there is nothing stored
     * @see VisualKey#LINE_WIDTH
     */
    public float getLineWidth() {
        return (Float) get(VisualKey.LINE_WIDTH);
    }

    /**
     * Returns the node position stored in this attribute map.
     * @return the stored node position, or the default value if
     * there is nothing stored.
     * @see VisualKey#NODE_POS
     */
    public Point2D getNodePos() {
        return (Point2D) get(VisualKey.NODE_POS);
    }

    /**
     * Sets a node position.
     * @see VisualKey#NODE_POS
     */
    public void setNodePos(Point2D newValue) {
        put(VisualKey.NODE_POS, newValue);
    }

    /**
     * Returns the node size stored in this attribute map.
     * @return the stored node size, or the default value if
     * there is nothing stored.
     * @see VisualKey#NODE_SIZE
     */
    public Dimension2D getNodeSize() {
        return (Dimension2D) get(VisualKey.NODE_SIZE);
    }

    /**
     * Sets a node size.
     * @see VisualKey#NODE_SIZE
     */
    public void setNodeSize(Dimension2D newValue) {
        put(VisualKey.NODE_SIZE, newValue);
    }

    /**
     * Returns the node opacity property stored in this attribute map.
     * @return the stored node opacity, or the default value if
     * there is nothing stored
     * @see VisualKey#OPAQUE
     */
    public boolean isOpaque() {
        return (Boolean) get(VisualKey.OPAQUE);
    }

    /**
     * Sets a new opacity value.
     * @see VisualKey#OPAQUE
     */
    public void setOpaque(boolean newValue) {
        put(VisualKey.OPAQUE, newValue);
    }

    /**
     * Returns the intermediate edge points stored in this attribute map.
     * @return the stored edge points, or the empty list if
     * there is nothing stored
     * @see VisualKey#POINTS
     */
    @SuppressWarnings("unchecked")
    public List<Point2D> getPoints() {
        return Collections.unmodifiableList((List<Point2D>) get(VisualKey.POINTS));
    }

    /**
     * Sets a new intermediate points list.
     * The list is aliased, not copied.
     * @see VisualKey#POINTS
     */
    public void setPoints(List<Point2D> newValue) {
        put(VisualKey.POINTS, newValue);
    }

    /**
     * Returns the text size stored in this attribute map.
     * @return the stored text size, or the default value if
     * there is nothing stored.
     * @see VisualKey#TEXT_SIZE
     */
    public Dimension2D getTextSize() {
        return (Dimension2D) get(VisualKey.TEXT_SIZE);
    }

    /**
     * Returns the visibility property stored in this attribute map.
     * @return the stored visibility, or the default value if
     * there is nothing stored
     * @see VisualKey#VISIBLE
     */
    public boolean isVisible() {
        return (Boolean) get(VisualKey.VISIBLE);
    }

    /**
     * Sets a new visibility value.
     * @see VisualKey#VISIBLE
     */
    public void setVisible(boolean newValue) {
        put(VisualKey.VISIBLE, newValue);
    }

    /** Returns the inner map. */
    Map<VisualKey,Object> getMap() {
        return this.map;
    }

    /** Converts this visual map into a JGraph-style attribute map. */
    public AttributeMap getAttributes() {
        return this.attrMap;
    }

    @Override
    public String toString() {
        return "VisualMap: " + this.map;
    }

    private final Map<VisualKey,Object> map;
    private final VisualAttributeMap attrMap;

    /**
     * Converts a colour dimension to a value that is whitewashed by
     * {@link #BACKGROUND_WHITEWASH} degrees.
     */
    static private int whitewash(int value) {
        int distance = 255 - value;
        return value + (distance * BACKGROUND_WHITEWASH / 100);
    }

    /**
     * Converts a colour dimension to a value that is whitewashed by
     * {@link #BACKGROUND_WHITEWASH} degrees.
     */
    static public Color whitewash(Color color) {
        int red = whitewash(color.getRed());
        int green = whitewash(color.getGreen());
        int blue = whitewash(color.getBlue());
        int alpha = whitewash(color.getAlpha());
        return new Color(red, green, blue, alpha);
    }

    /** Percentage of white in the background colour. */
    static private final int BACKGROUND_WHITEWASH = 90;
}
