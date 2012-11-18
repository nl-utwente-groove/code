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

import groove.gui.layout.JCellLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Visual attribute keys.
 * Some of the keys are derived from the looks, others are
 * under direct control.
 * @author rensink
 * @version $Revision $
 */
public enum VisualKey {
    /** 
     * Node adornment text (parameter-style). Defaults to {@code null},
     * meaning no adornment. The empty string results in an adornment without
     * inscription.  
     */
    ADORNMENT(String.class, null, true),
    /** 
     * Background colour for nodes. Defaults to {@link Values#DEFAULT_BACKGROUND}.
     * A {@code null} value means a whitewashed version of the foreground is used. 
     */
    BACKGROUND(Color.class, Values.DEFAULT_BACKGROUND),
    /** Node bounds. */
    BOUNDS(Rectangle2D.class, new Rectangle2D.Double(10, 10, 19, 19), false),
    /** Controlled foreground colour, overriding {@link #FOREGROUND} if set. Defaults to {@code null}. */
    COLOR(Color.class, null, true),
    /** Edge dash pattern. Defaults to no dash. */
    DASH(float[].class, Values.NO_DASH),
    /** Edge source decoration. Defaults to {@link EdgeEnd#NONE}. */
    EDGE_SOURCE_SHAPE(EdgeEnd.class, EdgeEnd.NONE),
    /** HTML-formatted optional edge source label. Defaults to {@code null}. */
    EDGE_SOURCE_LABEL(String.class, null, true),
    /** Position of the optional edge source label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    EDGE_SOURCE_POS(Point2D.class, JCellLayout.defaultLabelPosition, false),
    /** Edge target decoration. Defaults to {@link EdgeEnd#ARROW}. */
    EDGE_TARGET_SHAPE(EdgeEnd.class, EdgeEnd.ARROW),
    /** HTML-formatted optional edge target label. Defaults to {@code null}. */
    EDGE_TARGET_LABEL(String.class, null, true),
    /** Position of the optional edge target label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    EDGE_TARGET_POS(Point2D.class, JCellLayout.defaultLabelPosition, false),
    /** Node or edge visibility. Defaults to {@code false}. */
    ERROR(Boolean.class, false, true),
    /** Node or edge emphasis. Defaults to {@code false}. */
    EMPHASIS(Boolean.class, false, true),
    /** Font setting for text, as a {@link Font} style value. Defaults to {@link Font#PLAIN}. */
    FONT(Integer.class, Font.PLAIN),
    /** Foreground colour. Defaults to {@link Values#DEFAULT_FOREGROUND}. */
    FOREGROUND(Color.class, Values.DEFAULT_FOREGROUND),
    /**
     * Line colour for an <i>inner</i> line drawn inside the normal outline
     * Defaults to {@code null}, meaning there is no
     * inner line. 
     */
    INNER_LINE(Color.class, null),
    /**
     * Extra space between text and border (needed if the node can have
     * a thicker border). Defaults to 0. 
     */
    INSET(Integer.class, 0),
    /**
     * HTML-formatted main label text. Defaults to the empty string.
     * A value of {@code null} on a node implies rendering it as a nodified edge.
     */
    LABEL(String.class, "", true),
    /** Position of the main edge label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    LABEL_POS(Point2D.class, JCellLayout.defaultLabelPosition, false),
    /** Edge layout line style. Defaults to {@link LineStyle#ORTHOGONAL}. */
    LINE_STYLE(LineStyle.class, LineStyle.ORTHOGONAL, false),
    /** Line width. Defaults to {@code 1}. */
    LINE_WIDTH(Float.class, 1f),
    /** Node shape. Defaults to {@link NodeShape#RECTANGLE} */
    NODE_SHAPE(NodeShape.class, NodeShape.RECTANGLE),
    /** Node opacity. Defaults to {@code false}. */
    OPAQUE(Boolean.class, false),
    /** Intermediate edge points. */
    POINTS(List.class,
            Arrays.asList(new Point2D.Double(), new Point2D.Double()), false),
    /** Node or edge visibility. Defaults to {@code true}. */
    VISIBLE(Boolean.class, true, true);

    /** Constructs a derived visual key. */
    private VisualKey(Class<?> type, Object defaultValue) {
        this(type, defaultValue, true, false);
    }

    /** Constructs a non-derived visual key that is possibly refreshable. */
    private VisualKey(Class<?> type, Object defaultValue, boolean refreshable) {
        this(type, defaultValue, false, refreshable);
    }

    /** Constructs a visual key that is possibly derived from a looks value. */
    private VisualKey(Class<?> type, Object defaultValue, boolean derived,
            boolean refreshable) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.derived = derived;
        this.refreshable = refreshable;
        test(defaultValue);
    }

    /** 
     * Tests if a given attribute value is of the correct type for this attribute.
     * @param value the value to be tested
     * @throws IllegalArgumentException if the value is not of the correct type
     */
    public void test(Object value) throws IllegalArgumentException {
        boolean error;
        if (value == null) {
            error = this.type == Integer.class || this.type == Boolean.class;
        } else {
            error = !this.type.isAssignableFrom(value.getClass());
        }
        if (error) {
            throw new IllegalArgumentException(String.format(
                "%s value %s should be of type %s", this, value, this.type));
        }
    }

    /** Returns the default value for this attribute. */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /** Indicates if the value for this key is derived from the looks. */
    public boolean isDerived() {
        return this.derived;
    }

    /** 
     * Indicates, for a non-derived key, if its value
     * is refreshed automatically.
     */
    public boolean isRefreshable() {
        return this.refreshable;
    }

    private final Class<?> type;
    private final Object defaultValue;
    private final boolean derived;
    private final boolean refreshable;

    /**
     * Creates a new visual key set.
     * The set initially contains all refreshable keys.
     */
    public static Set<VisualKey> createRefreshableKeys() {
        Set<VisualKey> result = EnumSet.noneOf(VisualKey.class);
        for (VisualKey k : refreshables()) {
            result.add(k);
        }
        return result;
    }

    /** 
     * Returns an array of automatically refreshable controlled keys.
     * The list consists of all keys for which {@link #isRefreshable()} holds.
     */
    public static VisualKey[] refreshables() {
        if (refreshables == null) {
            List<VisualKey> result = new ArrayList<VisualKey>();
            for (VisualKey key : VisualKey.values()) {
                if (key.isRefreshable()) {
                    result.add(key);
                }
            }
            refreshables = result.toArray(new VisualKey[result.size()]);
        }
        return refreshables;
    }

    private static VisualKey[] refreshables;

    /** 
     * Returns an array of derived keys.
     * The list consists of all keys for which {@link #isDerived()} holds.
     */
    public static VisualKey[] deriveds() {
        if (deriveds == null) {
            List<VisualKey> result = new ArrayList<VisualKey>();
            for (VisualKey key : VisualKey.values()) {
                if (key.isDerived()) {
                    result.add(key);
                }
            }
            deriveds = result.toArray(new VisualKey[result.size()]);
        }
        return deriveds;
    }

    private static VisualKey[] deriveds;
}
