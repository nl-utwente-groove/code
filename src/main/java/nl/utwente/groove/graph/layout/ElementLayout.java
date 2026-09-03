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

import java.awt.Point;
import java.awt.geom.Point2D;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Interface for classes containing layout information about a certain graph
 * element, in the form in which it is serialised in layout files.
 */
@NonNullByDefault
public interface ElementLayout {
    /**
     * Unit of relative (label) positions: such positions run from 0 to
     * {@link #PERMILLE} along the length of an edge. The value is persisted
     * in the layout information of saved graphs and must not be changed;
     * rendering backends must interpret positions in this unit (the JGraph
     * backend relies on it equalling {@code GraphConstants.PERMILLE}).
     */
    public static final int PERMILLE = 1000;

    /**
     * The default label position.
     */
    public static final Point2D defaultLabelPosition = new Point(PERMILLE / 2, 0);

    /**
     * The default node location.
     */
    public static final Point2D defaultNodeLocation = new Point(0, 0);

    /**
     * Indicates if this layout information contains just default information.
     * This applies in particular to edge information.
     */
    public boolean isDefault();
}
