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
package groove.gui.layout;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

import groove.graph.Edge;
import groove.graph.ElementMap;
import groove.graph.Node;
import groove.gui.look.VisualMap;

/**
 * Utility class for converting beck and forth between <b>jgraph</b> attribute
 * maps and layout information maps. The class is generic to enable use for
 * different type os nodes and edges: either GROOVE ones, or JGraph ones.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayoutMap implements Cloneable {
    /**
     * Constructs an empty, non-fixed layout map
     */
    public LayoutMap() {
        // explicit empty constructor
    }

    /** Retrieves the layout information for a given node. */
    public JVertexLayout getLayout(Node node) {
        return this.nodeMap.get(node);
    }

    /** Retrieves the layout information for a given edge. */
    public JEdgeLayout getLayout(Edge edge) {
        return this.edgeMap.get(edge);
    }

    /** Specialises the return type. */
    public Map<Node,JVertexLayout> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }

    /** Specialises the return type. */
    public Map<Edge,JEdgeLayout> edgeMap() {
        return Collections.unmodifiableMap(this.edgeMap);
    }

    /**
     * Turns this groove layout map into a jgraph attributes map.
     */
    public Map<Object,AttributeMap> toJAttrMap() {
        Map<Object,AttributeMap> result = new HashMap<>();
        for (Map.Entry<Node,JVertexLayout> layoutEntry : nodeMap().entrySet()) {
            JCellLayout layout = layoutEntry.getValue();
            result.put(layoutEntry.getKey(), layout.toJAttr());
        }
        for (Map.Entry<Edge,JEdgeLayout> layoutEntry : edgeMap().entrySet()) {
            JCellLayout layout = layoutEntry.getValue();
            result.put(layoutEntry.getKey(), layout.toJAttr());
        }
        return result;
    }

    /**
     * Inserts layout information for a given node key. Only really stores the
     * information if it is not default (according to the layout information
     * itself, i.e., <code>{@link JCellLayout#isDefault}</code>.
     */
    public JVertexLayout putNode(Node key, JVertexLayout layout) {
        if (!layout.isDefault()) {
            return this.nodeMap.put(key, layout);
        } else {
            return null;
        }
    }

    /**
     * Inserts layout information for a given key. Only really stores the
     * information if it is not default (according to the layout information
     * itself, i.e., <code>{@link JCellLayout#isDefault}</code>.
     */
    public void putEdge(Edge key, JEdgeLayout layout) {
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
    public void copyNodeWithOffset(Node newKey, Node oldKey, LayoutMap oldLayoutMap, double offsetX,
        double offsetY) {
        JVertexLayout oldLayout = oldLayoutMap == null ? null : oldLayoutMap.nodeMap.get(oldKey);
        if (oldLayout != null) {
            Rectangle2D oldBounds = oldLayout.getBounds();
            Rectangle2D.Double newBounds = new Rectangle2D.Double(oldBounds.getX() + offsetX,
                oldBounds.getY() + offsetY, oldBounds.getWidth(), oldBounds.getHeight());
            JVertexLayout newLayout = new JVertexLayout(newBounds);
            putNode(newKey, newLayout);
        }
    }

    /**
     * Inserts layout information for a given edge key, using the layout of
     * another edge (from which it was mapped). Also adds an offset.
     */
    public void copyEdgeWithOffset(Edge newKey, Edge oldKey, LayoutMap oldLayoutMap, double offsetX,
        double offsetY) {
        JEdgeLayout oldLayout = oldLayoutMap == null ? null : oldLayoutMap.edgeMap.get(oldKey);
        if (oldLayout != null) {
            List<Point2D> oldPoints = oldLayout.getPoints();
            List<Point2D> newPoints = new ArrayList<>();
            for (Point2D oldPoint : oldPoints) {
                newPoints
                    .add(new Point2D.Double(oldPoint.getX() + offsetX, oldPoint.getY() + offsetY));
            }
            Point2D labelPosition = oldLayout.getLabelPosition();
            JEdgeLayout newLayout =
                new JEdgeLayout(newPoints, labelPosition, oldLayout.getLineStyle());
            putEdge(newKey, newLayout);
        }
    }

    /**
     * Inserts layout information for a given key, on the basis of a
     * visual map.
     */
    public void putNode(Node key, VisualMap visuals) {
        putNode(key, JVertexLayout.newInstance(visuals));
    }

    /**
     * Inserts layout information for a given key, on the basis of a
     * visual map.
     */
    public void putEdge(Edge key, VisualMap visuals) {
        // at some point, only layout information about non-binary edges was stored
        // I do not understand or remember why that was ever a good idea
        // as it removes all record of manually layouted edge points
        // (SF Bug #434)
        putEdge(key, JEdgeLayout.newInstance(visuals));
    }

    /**
     * Inserts layout information for a given key, on the basis of jgraph
     * attributes. Only really stores the information if it is not default
     * (according to the layout information itself, i.e.,
     * <code>{@link JCellLayout#isDefault}</code>.
     */
    public void putNode(Node key, AttributeMap jAttr) {
        putNode(key, JVertexLayout.newInstance(jAttr));
    }

    /**
     * Inserts layout information for a given key, on the basis of jgraph
     * attributes. Only really stores the information if it is not default
     * (according to the layout information itself, i.e.,
     * <code>{@link JCellLayout#isDefault}</code>.
     */
    public void putEdge(Edge key, AttributeMap jAttr) {
        putEdge(key, JEdgeLayout.newInstance(jAttr));
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
    public LayoutMap afterInverse(ElementMap other) {
        LayoutMap result = newInstance();
        for (Map.Entry<Node,JVertexLayout> layoutEntry : nodeMap().entrySet()) {
            Node trafoValue = other.getNode(layoutEntry.getKey());
            if (trafoValue != null) {
                result.putNode(trafoValue, layoutEntry.getValue());
            }
        }
        for (Map.Entry<Edge,JEdgeLayout> layoutEntry : edgeMap().entrySet()) {
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

    /** Mapping from node keys to <tt>NT</tt>s. */
    private final Map<Node,JVertexLayout> nodeMap = new HashMap<>();
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private final Map<Edge,JEdgeLayout> edgeMap = new HashMap<>();

    /**
     * Tests if a given object is a jgraph vertex, a jgraph vertex view or a
     * groove node.
     */
    public static boolean isNode(Object key) {
        return (key instanceof DefaultGraphCell && !(key instanceof DefaultEdge))
            || (key instanceof VertexView) || (key instanceof Node);
    }

    /**
     * Turns a relative label position into an absolute label position.
     */
    static public Point2D toAbsPosition(List<Point2D> points, Point2D relPosition) {
        Rectangle bounds = toBounds(points);
        Point2D source = points.get(0);
        Point2D target = points.get(points.size() - 1);
        bounds.add(target);
        int unit = GraphConstants.PERMILLE;
        int x0 = bounds.x;
        int xdir = 1;
        if (source.getX() > target.getX()) {
            x0 += bounds.width;
            xdir = -1;
        }
        int y0 = bounds.y;
        int ydir = 1;
        if (source.getY() > target.getY()) {
            y0 += bounds.height;
            ydir = -1;
        }
        double x = x0 + xdir * (bounds.width * relPosition.getX() / unit);
        double y = y0 + ydir * (bounds.height * relPosition.getY() / unit);
        return new Point2D.Double(x, y);
    }

    /**
     * Converts a list of points to the minimal rectangle containing all of
     * them.
     */
    static public Rectangle toBounds(List<Point2D> points) {
        Rectangle bounds = new Rectangle();
        for (Point2D point : points) {
            bounds.add(point);
        }
        return bounds;

    }

    /**
     * Turns an absolute label position into a relative label position.
     */
    static public Point2D toRelPosition(List<Point2D> points, Point2D absPosition) {
        Rectangle bounds = toBounds(points);
        Point2D source = points.get(0);
        Point2D target = points.get(points.size() - 1);
        bounds.add(target);
        int unit = GraphConstants.PERMILLE;
        int x0 = bounds.x;
        if (source.getX() > target.getX()) {
            x0 += bounds.width;
        }
        int y0 = bounds.y;
        if (source.getY() > target.getY()) {
            y0 += bounds.height;
        }
        double x = Math.abs(x0 - absPosition.getX()) * unit / bounds.width;
        double y = Math.abs(y0 - absPosition.getY()) * unit / bounds.height;
        return new Point2D.Double(x, y);
    }

    /** Main method to test the functionality of this class. */
    static public void main(String[] args) {
        List<Point2D> points = new LinkedList<>();
        Point2D relPosition1 = new Point(100, 900);
        Point2D relPosition2 = new Point(1200, 50);
        points.add(new Point(100, 200));
        points.add(new Point(150, 50));
        testLabelPosition(points, JCellLayout.defaultLabelPosition);
        testLabelPosition(points, relPosition1);
        testLabelPosition(points, relPosition2);
        points.add(new Point(221, 100));
        testLabelPosition(points, JCellLayout.defaultLabelPosition);
        testLabelPosition(points, relPosition1);
        testLabelPosition(points, relPosition2);
        points.add(new Point(0, 150));
        testLabelPosition(points, JCellLayout.defaultLabelPosition);
        testLabelPosition(points, relPosition1);
        testLabelPosition(points, relPosition2);
        points.add(new Point(50, 0));
        testLabelPosition(points, JCellLayout.defaultLabelPosition);
        testLabelPosition(points, relPosition1);
        testLabelPosition(points, relPosition2);
    }

    static private void testLabelPosition(List<Point2D> points, Point2D relPosition) {
        System.out.print("Abs, rel, abs: ");
        Point2D absPosition = toAbsPosition(points, relPosition);
        System.out.print("" + absPosition + " ");
        relPosition = toRelPosition(points, absPosition);
        System.out.print("" + relPosition + " ");
        absPosition = toAbsPosition(points, relPosition);
        System.out.println(absPosition);
    }
}
