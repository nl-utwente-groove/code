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
package nl.utwente.groove.graph.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.GraphMap;
import nl.utwente.groove.graph.Node;

/**
 * Map storing the layout information for the nodes and edges of a graph.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LayoutMap implements Cloneable {
    /**
     * Constructs an empty, non-fixed layout map
     */
    public LayoutMap() {
        // explicit empty constructor
    }

    /** Retrieves the layout information for a given node. */
    public @Nullable NodeLayout getLayout(Node node) {
        return this.nodeMap.get(node);
    }

    /** Retrieves the layout information for a given edge. */
    public @Nullable EdgeLayout getLayout(Edge edge) {
        return this.edgeMap.get(edge);
    }

    /** Specialises the return type. */
    public Map<Node,NodeLayout> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }

    /** Specialises the return type. */
    public Map<Edge,EdgeLayout> edgeMap() {
        return Collections.unmodifiableMap(this.edgeMap);
    }

    /**
     * Inserts layout information for a given node key. Only really stores the
     * information if it is not default (according to the layout information
     * itself, i.e., <code>{@link ElementLayout#isDefault}</code>.
     */
    public @Nullable NodeLayout putNode(Node key, NodeLayout layout) {
        if (!layout.isDefault()) {
            return this.nodeMap.put(key, layout);
        } else {
            return null;
        }
    }

    /**
     * Inserts layout information for a given key. Only really stores the
     * information if it is not default (according to the layout information
     * itself, i.e., <code>{@link ElementLayout#isDefault}</code>.
     */
    public void putEdge(Edge key, EdgeLayout layout) {
        if (layout.isDefault()) {
            this.edgeMap.remove(key);
        } else {
            this.edgeMap.put(key, layout);
        }
    }

    /**
     * Inserts layout information for a given node key, using the layout of
     * another node (from which it was mapped). Also adds an offset.
     */
    public void copyNodeWithOffset(Node newKey, Node oldKey, @Nullable LayoutMap oldLayoutMap,
        double offsetX, double offsetY) {
        NodeLayout oldLayout = oldLayoutMap == null
            ? null
            : oldLayoutMap.getLayout(oldKey);
        if (oldLayout != null) {
            Rectangle2D oldBounds = oldLayout.getBounds();
            Rectangle2D.Double newBounds = new Rectangle2D.Double(oldBounds.getX() + offsetX,
                oldBounds.getY() + offsetY, oldBounds.getWidth(), oldBounds.getHeight());
            NodeLayout newLayout = new NodeLayout(newBounds);
            putNode(newKey, newLayout);
        }
    }

    /**
     * Inserts layout information for a given edge key, using the layout of
     * another edge (from which it was mapped). Also adds an offset.
     */
    public void copyEdgeWithOffset(Edge newKey, Edge oldKey, @Nullable LayoutMap oldLayoutMap,
        double offsetX, double offsetY) {
        EdgeLayout oldLayout = oldLayoutMap == null
            ? null
            : oldLayoutMap.getLayout(oldKey);
        if (oldLayout != null) {
            List<Point2D> oldPoints = oldLayout.getPoints();
            List<Point2D> newPoints = new ArrayList<>();
            for (Point2D oldPoint : oldPoints) {
                newPoints
                    .add(new Point2D.Double(oldPoint.getX() + offsetX, oldPoint.getY() + offsetY));
            }
            Point2D labelPosition = oldLayout.getLabelPosition();
            EdgeLayout newLayout =
                new EdgeLayout(newPoints, labelPosition, oldLayout.getLineStyle());
            putEdge(newKey, newLayout);
        }
    }

    /** Fills this layout map with the content of another. */
    public void load(LayoutMap other) {
        this.nodeMap.clear();
        this.nodeMap.putAll(other.nodeMap());
        this.edgeMap.clear();
        this.edgeMap.putAll(other.edgeMap());
    }

    /**
     * Composes the inverse of a given element map in front of this layout map.
     * The result is not fixed.
     */
    public LayoutMap afterInverse(GraphMap other) {
        LayoutMap result = newInstance();
        for (Map.Entry<Node,NodeLayout> layoutEntry : nodeMap().entrySet()) {
            Node trafoValue = other.getNode(layoutEntry.getKey());
            if (trafoValue != null) {
                result.putNode(trafoValue, layoutEntry.getValue());
            }
        }
        for (Map.Entry<Edge,EdgeLayout> layoutEntry : edgeMap().entrySet()) {
            Edge trafoValue = other.getEdge(layoutEntry.getKey());
            if (trafoValue != null) {
                result.putEdge(trafoValue, layoutEntry.getValue());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "LayoutMap [nodeMap=" + nodeMap() + ", edgeMap=" + edgeMap() + "]";
    }

    @Override
    public LayoutMap clone() {
        LayoutMap result = newInstance();
        result.nodeMap.putAll(nodeMap());
        result.edgeMap.putAll(edgeMap());
        return result;
    }

    /**
     * Specialises the return type of the super method to {@link LayoutMap}.
     */
    protected LayoutMap newInstance() {
        return new LayoutMap();
    }

    /** Mapping from node keys to node layouts. */
    private final Map<Node,NodeLayout> nodeMap = new HashMap<>();
    /** Mapping from edge keys to edge layouts. */
    private final Map<Edge,EdgeLayout> edgeMap = new HashMap<>();
}
