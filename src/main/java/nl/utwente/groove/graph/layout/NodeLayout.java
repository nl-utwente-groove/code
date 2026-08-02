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

import java.awt.geom.Rectangle2D;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Class containing the information to lay out a node. The information consists
 * of the node bounds.
 */
@NonNullByDefault
public class NodeLayout implements ElementLayout {
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
    public NodeLayout(Rectangle2D bounds) {
        this.bounds = (Rectangle2D) bounds.clone();
    }

    /**
     * Returns the bounds attribute of a node layout.
     * @return the bounds attribute of a node layout
     */
    public Rectangle2D getBounds() {
        return (Rectangle2D) this.bounds.clone();
    }

    /**
     * Node information is default if the location is the origin <tt>(0,0)</tt>.
     */
    @Override
    public boolean isDefault() {
        return NodeLayout.isDefaultNodeLocation(getBounds().getX(), getBounds().getY());
    }

    /**
     * This layout equals another object if that is also a {@link NodeLayout},
     * with equal bounds.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof NodeLayout other) {
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
