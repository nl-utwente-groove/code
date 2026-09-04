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
package nl.utwente.groove.gui.jgraph;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.Edge.Routing;
import org.jgraph.graph.GraphConstants;

import nl.utwente.groove.graph.layout.ElementLayout;
import nl.utwente.groove.gui.look.EdgeEnd;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualKey.Nature;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.util.Fonts;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Attribute map associated with a {@link VisualMap}.
 * Changes in this map are propagated back to the VisualMap,
 * if they correspond to controlled {@link VisualKey}s.
 */
public class VisualAttributeMap extends AttributeMap implements VisualMap.Listener {
    /**
     * Constructs an attribute map for a given visual map, and registers it as the
     * listener of that map so that it stays in step with subsequent changes.
     */
    VisualAttributeMap(VisualMap visuals) {
        this(visuals, true);
    }

    @SuppressWarnings("unchecked")
    private VisualAttributeMap(VisualMap visuals, boolean bind) {
        super.put(GraphConstants.GROUPOPAQUE, true);
        super.put(GraphConstants.AUTOSIZE, true);
        super.put(GraphConstants.EDITABLE, true);
        super.put(GraphConstants.SELECTABLE, true);
        super.put(GraphConstants.ROUTING, edgeRouting);
        this.visuals = visuals;
        this.changedKeys = EnumSet.noneOf(VisualKey.class);
        setStale(visuals.keySet());
        if (bind) {
            visuals.setListener(this);
        }
    }

    /**
     * Converts a visual map to a detached JGraph attribute map, holding only the
     * attributes corresponding to the keys in the visual map. The visual map is
     * left untouched (in particular, its listener is not replaced).
     */
    public static AttributeMap toAttributes(VisualMap visuals) {
        return (AttributeMap) new VisualAttributeMap(visuals, false).clone();
    }

    /*
     * Notifies the attribute map that a visual key change has occurred,
     * which may require refreshing the attribute map.
     */
    @Override
    public void changed(VisualKey key) {
        setStale(key);
    }

    /**
     * Notifies the attribute map that a visual key change has occurred,
     * which may require refreshing the attribute map;
     * @param key the key whose value has changed in the visual map
     */
    private void setStale(VisualKey key) {
        // only react to key changes that have a corresponding
        // attribute
        if (getAttrKey(key) != null) {
            this.changedKeys.add(key);
        }
    }

    /**
     * Notifies the attribute map that a set of key change have occurred,
     * which may require refreshing the attribute map;
     * @param keys the keys whose values have changed in the visual map
     */
    private void setStale(Set<VisualKey> keys) {
        if (!keys.isEmpty()) {
            for (VisualKey key : keys) {
                setStale(key);
            }
        }
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @SuppressWarnings("rawtypes")
    @Override
    public synchronized Enumeration elements() {
        refreshIfRequired();
        return super.elements();
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized Object get(Object key) {
        refreshIfRequired();
        return super.get(key);
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @SuppressWarnings("unchecked")
    @Override
    public Set<Map.Entry<?,?>> entrySet() {
        refreshIfRequired();
        return super.entrySet();
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public Collection<?> values() {
        refreshIfRequired();
        return super.values();
    }

    /* Overridden to avoid creating another map depending the same VisualMap. */
    @Override
    public Object clone() {
        AttributeMap result = new AttributeMap();
        for (Map.Entry<?,?> entry : entrySet()) {
            result.applyValue(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized int size() {
        refreshIfRequired();
        return super.size();
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized boolean isEmpty() {
        refreshIfRequired();
        return super.isEmpty();
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized Enumeration<?> keys() {
        refreshIfRequired();
        return super.keys();
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized boolean contains(Object value) {
        refreshIfRequired();
        return super.contains(value);
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized boolean containsKey(Object key) {
        refreshIfRequired();
        return super.containsKey(key);
    }

    /* Overridden to ensure the map is fresh w.r.t. the backing VisualMap. */
    @Override
    public synchronized int hashCode() {
        refreshIfRequired();
        return super.hashCode();
    }

    /* Overridden to make sure the backing map is kept in sync. */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized Object put(Object key, Object value) {
        Object result;
        refreshIfRequired();
        VisualKey vKey = getVisualKey(key);
        // do nothing for derived keys or keys that are unknown in the visual map
        if (vKey != null && vKey.getNature() == Nature.CONTROLLED) {
            Object vValue;
            Object[] vValues;
            // also update the backing visual map
            // convert those values for which this is necessary
            switch (vKey) {
            case EDGE_SOURCE_LABEL:
                vValues = asPair(value);
                if (vValues == null) {
                    return super.get(key);
                }
                vValue = vValues[0];
                this.visuals.put(VisualKey.EDGE_TARGET_LABEL, vValues[1]);
                break;
            case EDGE_SOURCE_POS:
                vValues = asPair(value);
                if (vValues == null) {
                    return super.get(key);
                }
                vValue = vValues[0];
                this.visuals.put(VisualKey.EDGE_TARGET_POS, vValues[1]);
                break;
            case EDGE_TARGET_LABEL:
                vValues = asPair(value);
                if (vValues == null) {
                    return super.get(key);
                }
                vValue = vValues[1];
                this.visuals.put(VisualKey.EDGE_SOURCE_LABEL, vValues[0]);
                break;
            case EDGE_TARGET_POS:
                vValues = asPair(value);
                if (vValues == null) {
                    return super.get(key);
                }
                vValue = vValues[1];
                this.visuals.put(VisualKey.EDGE_SOURCE_POS, vValues[0]);
                break;
            case LINE_STYLE:
                vValue = LineStyle.getStyle((Integer) value);
                break;
            case NODE_POS:
                Rectangle2D bounds = (Rectangle2D) value;
                vValue = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
                break;
            default:
                vValue = value;
            }
            this.visuals.put(vKey, vValue);
            result = super.put(key, value);
        } else {
            result = super.get(key);
        }
        return result;
    }

    /**
     * Converts an attribute value to the 2-element array expected for the
     * extra-label attributes, which GROOVE reserves for the pair of edge
     * source/target (multiplicity) labels; returns {@code null} if the value
     * is not an array of length 2. JGraph can deliver arrays of other lengths,
     * e.g. for user-created extra labels (see gh #843).
     */
    static private Object[] asPair(Object value) {
        return value instanceof Object[] result && result.length == 2
            ? result
            : null;
    }

    /* Overridden to make sure the backing map is kept in sync. */
    @Override
    public synchronized Object remove(Object key) {
        refreshIfRequired();
        VisualKey vKey = getVisualKey(key);
        if (vKey != null) {
            if (vKey.getNature() == Nature.DERIVED) {
                throw new UnsupportedOperationException();
            }
            // also remove supplementary keys
            switch (vKey) {
            case EDGE_SOURCE_LABEL:
                this.visuals.remove(VisualKey.EDGE_TARGET_LABEL);
                break;
            case EDGE_SOURCE_POS:
                this.visuals.remove(VisualKey.EDGE_TARGET_POS);
                break;
            case EDGE_TARGET_LABEL:
                this.visuals.remove(VisualKey.EDGE_SOURCE_LABEL);
                break;
            case EDGE_TARGET_POS:
                this.visuals.remove(VisualKey.EDGE_SOURCE_POS);
                break;
            default:
                // nothing to be done
            }
            this.visuals.remove(vKey);
        }
        return super.remove(key);
    }

    /* This would also clear all derived values, so we do not allow it. */
    @Override
    public synchronized void clear() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public AttributeMap applyMap(Map change) {
        return super.applyMap(change);
    }

    private void refreshIfRequired() {
        if (!this.changedKeys.isEmpty()) {
            for (VisualKey vKey : this.changedKeys) {
                putVisual(vKey, this.visuals.get(vKey));
            }
            this.changedKeys.clear();
        }
    }

    /**
     * Transfers the value for a given visual key into this attribute map,
     * without recursively triggering updates in the map.
     */
    @SuppressWarnings("unchecked")
    private void putVisual(VisualKey key, Object value) {
        switch (key) {
        case EDGE_SOURCE_LABEL:
            if (value == null) {
                super.remove(GraphConstants.EXTRALABELPOSITIONS);
            } else {
                value = new String[] {(String) value, this.visuals.getEdgeTargetLabel()};
                if (!super.containsKey(GraphConstants.EXTRALABELPOSITIONS)) {
                    super.put(GraphConstants.EXTRALABELPOSITIONS, EXTRA_LABEL_POSITIONS);
                }
            }
            break;
        case EDGE_SOURCE_POS:
            value = new Point2D[] {(Point2D) value, this.visuals.getEdgeTargetPos()};
            break;
        case EDGE_SOURCE_SHAPE:
            EdgeEnd sourceShape = (EdgeEnd) value;
            value = getArrowCode(sourceShape);
            // additionally set the size and fill
            super.put(GraphConstants.BEGINSIZE, sourceShape.getSize());
            super.put(GraphConstants.BEGINFILL, sourceShape.isFilled());
            break;
        case EDGE_TARGET_LABEL:
            if (value == null) {
                super.remove(GraphConstants.EXTRALABELPOSITIONS);
            } else {
                value = new String[] {this.visuals.getEdgeSourceLabel(), (String) value};
                if (!super.containsKey(GraphConstants.EXTRALABELPOSITIONS)) {
                    super.put(GraphConstants.EXTRALABELPOSITIONS, EXTRA_LABEL_POSITIONS);
                }
            }
            break;
        case EDGE_TARGET_POS:
            value = new Point2D[] {this.visuals.getEdgeSourcePos(), (Point2D) value};
            break;
        case EDGE_TARGET_SHAPE:
            EdgeEnd targetShape = (EdgeEnd) value;
            value = getArrowCode(targetShape);
            // additionally set the size and fill
            super.put(GraphConstants.ENDSIZE, targetShape.getSize());
            super.put(GraphConstants.ENDFILL, targetShape.isFilled());
            break;
        case FONT:
            value = Fonts.getLabelFont().deriveFont((Integer) value);
            break;
        case COLOR:
        case FOREGROUND:
            // additionally set the line colour
            if (value == null) {
                super.remove(GraphConstants.LINECOLOR);
            } else {
                super.put(GraphConstants.LINECOLOR, value);
            }
            break;
        case LINE_STYLE:
            value = ((LineStyle) value).getCode();
            break;
        case NODE_POS:
            Rectangle2D b = (Rectangle2D) super.get(GraphConstants.BOUNDS);
            Dimension2D size;
            if (b == null) {
                size = (Dimension2D) VisualKey.NODE_SIZE.getDefaultValue();
            } else {
                size = new Dimension((int) b.getWidth(), (int) b.getHeight());
            }
            Point2D pos = (Point2D) value;
            pos = new Point2D.Double(pos.getX() - size.getWidth() / 2,
                pos.getY() - size.getHeight() / 2);
            b = new Rectangle();
            b.setFrame(pos, size);
            value = b;
            break;
        case POINTS:
            value = new ArrayList<Object>((List<?>) value);
            break;
        default:
            // nothing to be done
        }
        String attrKey = getAttrKey(key);
        if (attrKey != null) {
            if (value == null) {
                super.remove(attrKey);
            } else {
                super.put(attrKey, value);
            }
        }
    }

    /**
     * The visual map from which this map was generated,
     * and to which changes are pushed back.
     */
    private final VisualMap visuals;
    /** Set of keys that have changed in the {@link VisualMap}. */
    private final Set<VisualKey> changedKeys;

    /** Returns the visual key corresponding to a given attribute map key. */
    public static VisualKey getVisualKey(Object key) {
        return attrToVisualKeyMap.get(key);
    }

    /** Returns the attribute map key corresponding to a given visual map key. */
    public static String getAttrKey(VisualKey key) {
        return visualToAttrKeyMap.get(key);
    }

    /** Returns the JGraph arrow code for a given edge end decoration. */
    private static int getArrowCode(EdgeEnd end) {
        return switch (end) {
        case ARROW, UNFILLED -> GraphConstants.ARROW_CLASSIC;
        case NONE -> GraphConstants.ARROW_NONE;
        case SUBTYPE -> GraphConstants.ARROW_TECHNICAL;
        case COMPOSITE -> GraphConstants.ARROW_DIAMOND;
        case NESTING, SIMPLE -> GraphConstants.ARROW_SIMPLE;
        };
    }

    static {
        // the LineStyle codes are persisted in graph layouts and passed to
        // JGraph unchanged (in both directions), so the two sides must agree;
        // MANHATTAN is GROOVE's own and is interpreted by JEdgeView instead
        assert LineStyle.ORTHOGONAL.getCode() == GraphConstants.STYLE_ORTHOGONAL;
        assert LineStyle.SPLINE.getCode() == GraphConstants.STYLE_SPLINE;
        assert LineStyle.BEZIER.getCode() == GraphConstants.STYLE_BEZIER;
        // persisted label positions are likewise passed through unchanged;
        // the local defeats constant folding, which ecj would otherwise flag
        int permille = ElementLayout.PERMILLE;
        assert permille == GraphConstants.PERMILLE;
    }

    /** Permille fractional distance of in multiplicity label from source node. */
    private static final double IN_MULT_DIST = GraphConstants.PERMILLE * 90 / 100;
    /** Permille fractional distance of out multiplicity label from target node. */
    private static final double OUT_MULT_DIST = GraphConstants.PERMILLE * 10 / 100;
    /** x-position of multiplicity labels. */
    private static final double MULT_X = -11;
    private static final Point2D[] EXTRA_LABEL_POSITIONS
        = {new Point2D.Double(IN_MULT_DIST, MULT_X), new Point2D.Double(OUT_MULT_DIST, MULT_X)};

    private final static Map<Object,VisualKey> attrToVisualKeyMap;
    private final static Map<VisualKey,String> visualToAttrKeyMap;

    static {
        Map<Object,VisualKey> a2v = new HashMap<>();
        Map<VisualKey,String> v2a = new EnumMap<>(VisualKey.class);
        for (VisualKey vKey : VisualKey.values()) {
            String aKey;
            switch (vKey) {
            case BACKGROUND:
                aKey = GraphConstants.BACKGROUND;
                break;
            case COLOR:
                aKey = GraphConstants.FOREGROUND;
                break;
            case DASH:
                aKey = GraphConstants.DASHPATTERN;
                break;
            case EDGE_SOURCE_LABEL:
                aKey = GraphConstants.EXTRALABELS;
                break;
            case EDGE_SOURCE_POS:
                aKey = GraphConstants.EXTRALABELPOSITIONS;
                break;
            case EDGE_SOURCE_SHAPE:
                aKey = GraphConstants.LINEBEGIN;
                break;
            case EDGE_TARGET_LABEL:
                aKey = GraphConstants.EXTRALABELS;
                break;
            case EDGE_TARGET_POS:
                aKey = GraphConstants.EXTRALABELPOSITIONS;
                break;
            case EDGE_TARGET_SHAPE:
                aKey = GraphConstants.LINEEND;
                break;
            case FONT:
                aKey = GraphConstants.FONT;
                break;
            case FOREGROUND:
                aKey = GraphConstants.FOREGROUND;
                break;
            case INSET:
                aKey = GraphConstants.INSET;
                break;
            case LABEL_POS:
                aKey = GraphConstants.LABELPOSITION;
                break;
            case LINE_STYLE:
                aKey = GraphConstants.LINESTYLE;
                break;
            case LINE_WIDTH:
                aKey = GraphConstants.LINEWIDTH;
                break;
            case OPAQUE:
                aKey = GraphConstants.OPAQUE;
                break;
            case POINTS:
                aKey = GraphConstants.POINTS;
                break;
            case NODE_POS:
                aKey = GraphConstants.BOUNDS;
                break;
            case INNER_LINE:
            case NODE_SHAPE:
            case EMPHASIS:
                aKey = null;
                break;
            default:
                assert vKey.getNature() == Nature.REFRESHABLE;
                aKey = null;
            }
            if (aKey != null) {
                a2v.put(aKey, vKey);
                v2a.put(vKey, aKey);
            }
        }
        attrToVisualKeyMap = a2v;
        visualToAttrKeyMap = v2a;
    }

    private final static Routing edgeRouting = new LoopRouting();
}
