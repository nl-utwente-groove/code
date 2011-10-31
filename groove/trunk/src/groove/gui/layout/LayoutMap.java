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
 * $Id: LayoutMap.java,v 1.3 2008-01-30 09:33:00 iovka Exp $
 */
package groove.gui.layout;

import groove.graph.Edge;
import groove.graph.ElementMap;
import groove.graph.Node;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Utility class for converting beck and forth between <b>jgraph</b> attribute
 * maps and layout information maps. The class is generic to enable use for
 * different type os nodes and edges: either GROOVE ones, or JGraph ones.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayoutMap<N extends Node,E extends Edge> {
    /**
     * Tests if a given object is a jgraph vertex, a jgraph vertex view or a
     * groove node.
     */
    public static boolean isNode(Object key) {
        return (key instanceof DefaultGraphCell && !(key instanceof DefaultEdge))
            || (key instanceof VertexView) || (key instanceof Node);
    }

    /** Retrieves the layout information for a given node. */
    public JVertexLayout getLayout(N node) {
        return this.nodeMap.get(node);
    }

    /** Retrieves the layout information for a given edge. */
    public JEdgeLayout getLayout(E edge) {
        return this.edgeMap.get(edge);
    }

    /** Specialises the return type. */
    public Map<N,JVertexLayout> nodeMap() {
        return this.nodeMap;
    }

    /** Specialises the return type. */
    public Map<E,JEdgeLayout> edgeMap() {
        return this.edgeMap;
    }

    /**
     * Turns a relative label position into an absolute label position.
     */
    static public Point toAbsPosition(List<Point> points, Point relPosition) {
        Rectangle bounds = toBounds(points);
        Point source = points.get(0);
        Point target = points.get(points.size() - 1);
        bounds.add(target);
        int unit = GraphConstants.PERMILLE;
        int x0 = bounds.x;
        int xdir = 1;
        if (source.x > target.x) {
            x0 += bounds.width;
            xdir = -1;
        }
        int y0 = bounds.y;
        int ydir = 1;
        if (source.y > target.y) {
            y0 += bounds.height;
            ydir = -1;
        }
        int x = x0 + xdir * (bounds.width * relPosition.x / unit);
        int y = y0 + ydir * (bounds.height * relPosition.y / unit);
        return new Point(x, y);
    }

    /**
     * Converts a list of points to the minimal rectangle containing all of
     * them.
     */
    static public Rectangle toBounds(List<Point> points) {
        Rectangle bounds = new Rectangle();
        for (Point point : points) {
            bounds.add(point);
        }
        return bounds;

    }

    /**
     * Turns an absolute label position into a relative label position.
     */
    static public Point toRelPosition(List<Point> points, Point absPosition) {
        Rectangle bounds = toBounds(points);
        Point source = points.get(0);
        Point target = points.get(points.size() - 1);
        bounds.add(target);
        int unit = GraphConstants.PERMILLE;
        int x0 = bounds.x;
        if (source.x > target.x) {
            x0 += bounds.width;
        }
        int y0 = bounds.y;
        if (source.y > target.y) {
            y0 += bounds.height;
        }
        int x = Math.abs(x0 - absPosition.x) * unit / bounds.width;
        int y = Math.abs(y0 - absPosition.y) * unit / bounds.height;
        return new Point(x, y);
    }

    /** Main method to test the functionality of this class. */
    static public void main(String[] args) {
        List<Point> points = new LinkedList<Point>();
        Point relPosition1 = new Point(100, 900);
        Point relPosition2 = new Point(1200, 50);
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

    static private void testLabelPosition(List<Point> points, Point relPosition) {
        System.out.print("Abs, rel, abs: ");
        Point absPosition = toAbsPosition(points, relPosition);
        System.out.print("" + absPosition + " ");
        relPosition = toRelPosition(points, absPosition);
        System.out.print("" + relPosition + " ");
        absPosition = toAbsPosition(points, relPosition);
        System.out.println(absPosition);
    }

    /**
     * Constructs an empty layout map
     */
    public LayoutMap() {
        // explicit empty constructor
    }

    /**
     * Turns this groove layout map into a jgraph attributes map.
     */
    public Map<Object,AttributeMap> toJAttrMap() {
        Map<Object,AttributeMap> result = new HashMap<Object,AttributeMap>();
        for (Map.Entry<N,JVertexLayout> layoutEntry : nodeMap().entrySet()) {
            JCellLayout layout = layoutEntry.getValue();
            result.put(layoutEntry.getKey(), layout.toJAttr());
        }
        for (Map.Entry<E,JEdgeLayout> layoutEntry : edgeMap().entrySet()) {
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
    public JVertexLayout putNode(N key, JVertexLayout layout) {
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
    public void putEdge(E key, JEdgeLayout layout) {
        if (layout.isDefault()) {
            this.edgeMap.remove(key);
        } else {
            this.edgeMap.put(key, layout);
        }
    }

    /**
     * Inserts layout information for a given key, on the basis of jgraph
     * attributes. Only really stores the information if it is not default
     * (according to the layout information itself, i.e.,
     * <code>{@link JCellLayout#isDefault}</code>.
     */
    public void putNode(N key, AttributeMap jAttr) {
        putNode(key, JVertexLayout.newInstance(jAttr));
    }

    /**
     * Inserts layout information for a given key, on the basis of jgraph
     * attributes. Only really stores the information if it is not default
     * (according to the layout information itself, i.e.,
     * <code>{@link JCellLayout#isDefault}</code>.
     */
    public void putEdge(E key, AttributeMap jAttr) {
        putEdge(key, JEdgeLayout.newInstance(jAttr));
    }

    /**
     * Composes the inverse of a given element map in front of this layout map.
     */
    public <OtherN extends Node,OtherE extends Edge> LayoutMap<OtherN,OtherE> afterInverse(
            ElementMap<N,E,OtherN,OtherE> other) {
        LayoutMap<OtherN,OtherE> result = newInstance();
        for (Map.Entry<N,JVertexLayout> layoutEntry : nodeMap().entrySet()) {
            OtherN trafoValue = other.getNode(layoutEntry.getKey());
            if (trafoValue != null) {
                result.putNode(trafoValue, layoutEntry.getValue());
            }
        }
        for (Map.Entry<E,JEdgeLayout> layoutEntry : edgeMap().entrySet()) {
            OtherE trafoValue = other.getEdge(layoutEntry.getKey());
            if (trafoValue != null) {
                result.putEdge(trafoValue, layoutEntry.getValue());
            }
        }
        return result;
    }

    /**
     * Specialises the return type of the super method to {@link LayoutMap}.
     */
    protected <OtherN extends Node,OtherE extends Edge> LayoutMap<OtherN,OtherE> newInstance() {
        return new LayoutMap<OtherN,OtherE>();
    }

    /** Mapping from node keys to <tt>NT</tt>s. */
    private final Map<N,JVertexLayout> nodeMap = new HashMap<N,JVertexLayout>();
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private final Map<E,JEdgeLayout> edgeMap = new HashMap<E,JEdgeLayout>();
}
