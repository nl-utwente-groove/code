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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
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

import groove.gui.Options;
import groove.gui.look.VisualKey.Nature;
import groove.util.line.LineStyle;

/**
 * Attribute map associated with a {@link VisualMap}.
 * Changes in this map are propagated back to the VisualMap,
 * if they correspond to controlled {@link VisualKey}s.
 */
public class VisualAttributeMap extends AttributeMap {
    @SuppressWarnings("unchecked")
    VisualAttributeMap(VisualMap visuals) {
        super.put(GraphConstants.GROUPOPAQUE, true);
        super.put(GraphConstants.AUTOSIZE, true);
        super.put(GraphConstants.EDITABLE, true);
        super.put(GraphConstants.SELECTABLE, true);
        super.put(GraphConstants.ROUTING, edgeRouting);
        this.visuals = visuals;
        this.changedKeys = EnumSet.noneOf(VisualKey.class);
        setStale(visuals.getMap()
            .keySet());
    }

    /**
     * Notifies the attribute map that a visual key change has occurred,
     * which may require refreshing the attribute map;
     * @param key the key whose value has changed in the visual map
     */
    void setStale(VisualKey key) {
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
    void setStale(Set<VisualKey> keys) {
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
                assert value instanceof Object[] : String.format("Incorrect value %s", value);
                vValues = ((Object[]) value);
                assert vValues.length == 2 : String.format("Incorrect value array %s",
                    Arrays.toString(vValues));
                vValue = vValues[0];
                this.visuals.put(VisualKey.EDGE_TARGET_LABEL, vValues[1], false);
                break;
            case EDGE_SOURCE_POS:
                assert value instanceof Object[] : String.format("Incorrect value %s", value);
                vValues = ((Object[]) value);
                assert vValues.length == 2 : String.format("Incorrect value array %s",
                    Arrays.toString(vValues));
                vValue = vValues[0];
                this.visuals.put(VisualKey.EDGE_TARGET_POS, vValues[1], false);
                break;
            case EDGE_TARGET_LABEL:
                assert value instanceof Object[] : String.format("Incorrect value %s", value);
                vValues = ((Object[]) value);
                assert vValues.length == 2 : String.format("Incorrect value array %s",
                    Arrays.toString(vValues));
                vValue = vValues[1];
                this.visuals.put(VisualKey.EDGE_SOURCE_LABEL, vValues[0], false);
                break;
            case EDGE_TARGET_POS:
                assert value instanceof Object[] : String.format("Incorrect value %s", value);
                vValues = ((Object[]) value);
                assert vValues.length == 2 : String.format("Incorrect value array %s",
                    Arrays.toString(vValues));
                vValue = vValues[1];
                this.visuals.put(VisualKey.EDGE_SOURCE_POS, vValues[0], false);
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
            this.visuals.put(vKey, vValue, false);
            result = super.put(key, value);
        } else {
            result = super.get(key);
        }
        return result;
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
                this.visuals.remove(VisualKey.EDGE_TARGET_LABEL, false);
                break;
            case EDGE_SOURCE_POS:
                this.visuals.remove(VisualKey.EDGE_TARGET_POS, false);
                break;
            case EDGE_TARGET_LABEL:
                this.visuals.remove(VisualKey.EDGE_SOURCE_LABEL, false);
                break;
            case EDGE_TARGET_POS:
                this.visuals.remove(VisualKey.EDGE_SOURCE_POS, false);
                break;
            default:
                // nothing to be done
            }
            this.visuals.remove(vKey, false);
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
            value = sourceShape.getCode();
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
            value = targetShape.getCode();
            // additionally set the size and fill
            super.put(GraphConstants.ENDSIZE, targetShape.getSize());
            super.put(GraphConstants.ENDFILL, targetShape.isFilled());
            break;
        case FONT:
            value = Options.getLabelFont()
                .deriveFont((Integer) value);
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

    /** Permille fractional distance of in multiplicity label from source node. */
    private static final double IN_MULT_DIST = GraphConstants.PERMILLE * 90 / 100;
    /** Permille fractional distance of out multiplicity label from target node. */
    private static final double OUT_MULT_DIST = GraphConstants.PERMILLE * 10 / 100;
    /** x-position of multiplicity labels. */
    private static final double MULT_X = -11;
    private static final Point2D[] EXTRA_LABEL_POSITIONS =
        {new Point2D.Double(IN_MULT_DIST, MULT_X), new Point2D.Double(OUT_MULT_DIST, MULT_X)};

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
