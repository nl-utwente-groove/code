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

import groove.gui.look.VisualMap;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Interface for classes containing layout information about a certain graph
 * element. The main functionality is to convert between <tt>jgraph</tt>
 * representations of such layout information and <tt>groove</tt> serializable
 * representations of the same.
 */
public interface JCellLayout {
    /**
     * The default label position.
     */
    public static final Point2D defaultLabelPosition = new Point(
        GraphConstants.PERMILLE / 2, 0);

    /**
     * The default node location.
     */
    public static final Point2D defaultNodeLocation = new Point(0, 0);

    /** Converts the layout information into a visual map. */
    public VisualMap toVisuals();

    /**
     * Converts the layout information to a <tt>jgraph</tt> attribute map.
     */
    public AttributeMap toJAttr();

    /**
     * Indicates if this layout information contains just default information.
     * This applies in particular to edge information.
     */
    public boolean isDefault();
}