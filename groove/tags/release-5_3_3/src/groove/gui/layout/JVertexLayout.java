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

import groove.gui.jgraph.JAttr;
import groove.gui.look.VisualMap;

import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Class containing the information to lay out a node. The information consists
 * of the node bounds.
 */
public class JVertexLayout implements JCellLayout {
    /**
     * Factory method to construct a new nod layout out of an attribute map.
     * Parameters not provided in the attribute map receive a default value.
     * @param jAttr the attribute map
     * @return a new node layout based on <code>jAttr</code>
     */
    static public JVertexLayout newInstance(AttributeMap jAttr) {
        Rectangle2D bounds = GraphConstants.getBounds(jAttr);
        if (bounds == null) {
            bounds = new Rectangle(JAttr.DEFAULT_NODE_BOUNDS);
        }
        return new JVertexLayout(bounds);
    }

    /**
     * Factory method to construct a new nod layout out of an attribute map.
     * Parameters not provided in the attribute map receive a default value.
     * @param visuals the visual attribute map
     * @return a new node layout based on <code>jAttr</code>
     */
    static public JVertexLayout newInstance(VisualMap visuals) {
        Dimension2D size = visuals.getNodeSize();
        Point2D pos = visuals.getNodePos();
        return new JVertexLayout(new Rectangle2D.Double(pos.getX() - size.getWidth() / 2,
            pos.getY() - size.getHeight() / 2, size.getWidth(), size.getHeight()));
    }

    /**
     * Indicates whether a given node location is the default location.
     * @param x the x-coordinate of the node location to be tested
     * @param y the y-coordinate of the node location to be tested
     * @return <code>true</code> if <code>location</code> is the default
     *         node location
     */
    static public boolean isDefaultNodeLocation(double x, double y) {
        return defaultNodeLocation.getX() == x && defaultNodeLocation.getY() == y;
    }

    /**
     * Constructs a node layout from a given bounds rectangle.
     * @param bounds the intended bounds
     */
    public JVertexLayout(Rectangle2D bounds) {
        this.bounds = (Rectangle2D) bounds.clone();
    }

    /**
     * Returns the bounds attribute of a node layout.
     * @return the bounds attribute of a node layout
     */
    public Rectangle2D getBounds() {
        return (Rectangle2D) this.bounds.clone();
    }

    /** Converts the layout information into a visual map. */
    @Override
    public VisualMap toVisuals() {
        VisualMap result = new VisualMap();
        if (this.bounds != null) {
            result.setNodePos(new Point2D.Double(this.bounds.getCenterX(), this.bounds.getCenterY()));
        }
        return result;
    }

    /**
     * Converts the layout information into an attribute map as required by
     * <tt>jgraph</tt>. The attribute map contains the stored bounds.
     * @return an attribute map with layout information
     */
    @Override
    public AttributeMap toJAttr() {
        AttributeMap result = new AttributeMap();
        if (this.bounds != null) {
            GraphConstants.setBounds(result, this.bounds);
        }
        return result;
    }

    /**
     * Node information is default if the location is the origin <tt>(0,0)</tt>.
     */
    @Override
    public boolean isDefault() {
        return JVertexLayout.isDefaultNodeLocation(getBounds().getX(), getBounds().getY());
    }

    /**
     * This layout equals another object if that is also a {@link JVertexLayout},
     * with equal bounds.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JVertexLayout) {
            JVertexLayout other = (JVertexLayout) obj;
            return getBounds().equals(other.getBounds());
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code of the bounds.
     */
    @Override
    public int hashCode() {
        return getBounds().hashCode();
    }

    @Override
    public String toString() {
        return "Bounds=" + getBounds();
    }

    /** The node bounds. */
    private final Rectangle2D bounds;
}