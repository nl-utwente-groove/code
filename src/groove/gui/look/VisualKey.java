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

import static groove.gui.look.VisualKey.Nature.CONTROLLED;
import static groove.gui.look.VisualKey.Nature.DERIVED;
import static groove.gui.look.VisualKey.Nature.REFRESHABLE;
import groove.gui.layout.JCellLayout;
import groove.util.NodeShape;
import groove.util.line.LineStyle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    ADORNMENT(String.class, null, REFRESHABLE),
    /**
     * Background colour for nodes. Defaults to {@link Values#DEFAULT_BACKGROUND}.
     * A {@code null} value means a whitewashed version of the foreground is used.
     */
    BACKGROUND(Color.class, Values.DEFAULT_BACKGROUND, DERIVED),
    /** Controlled foreground colour, overriding {@link #FOREGROUND} if set. Defaults to {@code null}. */
    COLOR(Color.class, null, CONTROLLED),
    /** Edge dash pattern. Defaults to no dash. */
    DASH(float[].class, Values.NO_DASH, DERIVED),
    /** Edge source decoration. Defaults to {@link EdgeEnd#NONE}. */
    EDGE_SOURCE_SHAPE(EdgeEnd.class, EdgeEnd.NONE, REFRESHABLE),
    /** HTML-formatted optional edge source label. Defaults to {@code null}. */
    EDGE_SOURCE_LABEL(String.class, null, REFRESHABLE),
    /** Position of the optional edge source label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    EDGE_SOURCE_POS(Point2D.class, JCellLayout.defaultLabelPosition, CONTROLLED),
    /** Edge target decoration. Defaults to {@link EdgeEnd#ARROW}. */
    EDGE_TARGET_SHAPE(EdgeEnd.class, EdgeEnd.ARROW, REFRESHABLE),
    /** HTML-formatted optional edge target label. Defaults to {@code null}. */
    EDGE_TARGET_LABEL(String.class, null, REFRESHABLE),
    /** Position of the optional edge target label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    EDGE_TARGET_POS(Point2D.class, JCellLayout.defaultLabelPosition, CONTROLLED),
    /** Node or edge error. Defaults to {@code false}. */
    ERROR(Boolean.class, false, REFRESHABLE),
    /** Node or edge emphasis. Defaults to {@code false}. */
    EMPHASIS(Boolean.class, false, CONTROLLED),
    /** Font setting for text, as a {@link Font} style value. Defaults to {@link Font#PLAIN}. */
    FONT(Integer.class, Font.PLAIN, DERIVED),
    /** Foreground colour. Defaults to {@link Values#DEFAULT_FOREGROUND}. */
    FOREGROUND(Color.class, Values.DEFAULT_FOREGROUND, DERIVED),
    /**
     * Line colour for an <i>inner</i> line drawn inside the normal outline
     * Defaults to {@code null}, meaning there is no
     * inner line.
     */
    INNER_LINE(Color.class, null, DERIVED),
    /**
     * Extra space between text and border (needed if the node can have
     * a thicker border). Defaults to 0.
     */
    INSET(Integer.class, 0, DERIVED),
    /**
     * HTML-formatted main label text. Defaults to the empty string.
     * A value of {@code null} on a node implies rendering it as a nodified edge.
     */
    LABEL(MultiLabel.class, new MultiLabel(), REFRESHABLE),
    /** Position of the main edge label. Defaults to {@link JCellLayout#defaultLabelPosition}. */
    LABEL_POS(Point2D.class, JCellLayout.defaultLabelPosition, CONTROLLED),
    /** Edge layout line style. Defaults to {@link LineStyle#ORTHOGONAL}. */
    LINE_STYLE(LineStyle.class, LineStyle.ORTHOGONAL, CONTROLLED),
    /** Line width. Defaults to {@code 1}. */
    LINE_WIDTH(Float.class, 1f, DERIVED),
    /** Node position, corresponding to the centre of the node bounds. */
    NODE_POS(Point2D.class, new Point2D.Double(10, 10), CONTROLLED),
    /** Node shape. Defaults to {@link NodeShape#RECTANGLE} */
    NODE_SHAPE(NodeShape.class, NodeShape.RECTANGLE, DERIVED),
    /** Size of the node inscription. The rendered node adds insets to the size. */
    NODE_SIZE(Dimension2D.class, new Dimension(19, 19), REFRESHABLE),
    /** Node opacity. Defaults to {@code false}. */
    OPAQUE(Boolean.class, false, DERIVED),
    /** Intermediate edge points. */
    POINTS(List.class, Arrays.asList(new Point2D.Double(), new Point2D.Double()), CONTROLLED),
    /** Computed text bounds. */
    TEXT_SIZE(Dimension2D.class, null, REFRESHABLE),
    /** Node or edge visibility. Defaults to {@code true}. */
    VISIBLE(Boolean.class, true, REFRESHABLE);

    /** Constructs a visual key that is possibly derived from a looks value. */
    private VisualKey(Class<?> type, Object defaultValue, Nature nature) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.nature = nature;
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
            throw new IllegalArgumentException(String.format("%s value %s should be of type %s",
                this,
                value,
                this.type));
        }
    }

    /** Returns the default value for this attribute. */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /** Returns the nature of this key. */
    public Nature getNature() {
        return this.nature;
    }

    private final Class<?> type;
    private final Object defaultValue;
    private final Nature nature;

    /**
     * Returns an array of automatically refreshable controlled keys.
     * The list consists of all {@link Nature#REFRESHABLE} keys.
     */
    public static VisualKey[] refreshables() {
        return REFRESHABLES;
    }

    /**
     * Returns an array of derived keys.
     * The list consists of all {@link Nature#DERIVED} keys.
     */
    public static VisualKey[] deriveds() {
        return DERIVEDS;
    }

    /**
     * Returns an array of controlled keys.
     * The list consists of all {@link Nature#CONTROLLED} keys.
     */
    public static VisualKey[] controlleds() {
        return CONTROLLEDS;
    }

    /** The array of refreshable keys. */
    private static final VisualKey[] REFRESHABLES;
    /** The array of derived keys. */
    private static final VisualKey[] DERIVEDS;
    /** The array of controlled keys. */
    private static final VisualKey[] CONTROLLEDS;

    static {
        List<VisualKey> deriveds = new ArrayList<>();
        List<VisualKey> refreshables = new ArrayList<>();
        List<VisualKey> controlleds = new ArrayList<>();
        for (VisualKey key : VisualKey.values()) {
            switch (key.getNature()) {
            case CONTROLLED:
                controlleds.add(key);
                break;
            case DERIVED:
                deriveds.add(key);
                break;
            case REFRESHABLE:
                refreshables.add(key);
                break;
            }
        }
        DERIVEDS = deriveds.toArray(new VisualKey[deriveds.size()]);
        REFRESHABLES = refreshables.toArray(new VisualKey[refreshables.size()]);
        CONTROLLEDS = controlleds.toArray(new VisualKey[controlleds.size()]);
    }

    /** Nature of a visible key. */
    public enum Nature {
        /** Key that is derived from a {@link Look}. */
        DERIVED,
        /** Key that can be set directly by the user. */
        CONTROLLED,
        /** Key that is refreshed through a {@link VisualValue}. */
        REFRESHABLE;
    }
}
