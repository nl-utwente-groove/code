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

import static groove.gui.look.MultiLabel.Orient.DOWN_LEFT;
import static groove.gui.look.MultiLabel.Orient.DOWN_RIGHT;
import static groove.gui.look.MultiLabel.Orient.LEFT;
import static groove.gui.look.MultiLabel.Orient.RIGHT;
import static groove.gui.look.MultiLabel.Orient.UP_LEFT;
import static groove.gui.look.MultiLabel.Orient.UP_RIGHT;
import groove.io.HTMLConverter;
import groove.io.Util;

import java.awt.geom.Point2D;

/**
 * Class wrapping the functionality to convert a multi-line label
 * into a list of labels with orientation decorations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MultiLabel {
    /**
     * Direction of a line of the multi-label.
     * This determines how the orientation decorations are placed.
     * @author rensink
     * @version $Revision $
     */
    public static enum Direct {
        /** Undirected label. */
        NONE {
            @Override
            public Orient getOrient(int dx, int dy) {
                return Orient.NONE;
            }
        },
        /** Directed from edge source to edge target. */
        FORWARD {
            @Override
            public Orient getOrient(int dx, int dy) {
                if (Math.abs(dx) >= Math.abs(dy) * 5) {
                    // vertical dimension negligible
                    return dx < 0 ? LEFT : RIGHT;
                } else if (dy < 0) {
                    return dx <= 0 ? UP_LEFT : UP_RIGHT;
                } else {
                    return dx < 0 ? DOWN_LEFT : DOWN_RIGHT;
                }
            }
        },
        /** Directed from edge target to edge source. */
        BACKWARD {
            @Override
            public Orient getOrient(int dx, int dy) {
                return FORWARD.getOrient(-dx, -dy);
            }
        },
        /** Both forward and backward. */
        BIRIDECTIONAL {
            @Override
            public Orient getOrient(int dx, int dy) {
                if (Math.abs(dx) >= Math.abs(dy) * 5) {
                    // vertical dimension negligible
                    return Orient.LEFT_RIGHT;
                } else if (dy < 0) {
                    return Orient.UP_DOWN;
                } else {
                    return Orient.DOWN_UP;
                }
            }
        };

        /**
         * Returns the correct orientation for a given vector.
         */
        abstract public Orient getOrient(int dx, int dy);

        /**
         * Returns the correct orientation for a line between given
         * start and end points.
         */
        public Orient getOrient(Point2D start, Point2D end) {
            int dx = (int) (end.getX() - start.getX());
            int dy = (int) (end.getY() - start.getY());
            return getOrient(dx, dy);
        }
    }

    /** Orientation decorations. */
    public static enum Orient {
        /** Pointing left. */
        NONE(null, null),
        /** Pointing left. */
        LEFT(LA, null),
        /** Pointing right. */
        RIGHT(null, RA),
        /** Pointing both left and right. */
        LEFT_RIGHT(LA, RA),
        /** Pointing up-left and down-right. */
        UP_DOWN(UA, DA),
        /** Pointing down-left and up-right. */
        DOWN_UP(DA, UA),
        /** Pointing up left. */
        UP_LEFT(UA, null),
        /** Pointing down left. */
        DOWN_LEFT(DA, null),
        /** Pointing up right. */
        UP_RIGHT(null, UA),
        /** Pointing down right. */
        DOWN_RIGHT(null, DA);

        private Orient(String left, String right) {
            this.left = left;
            this.right = right;
        }

        /** Inserts symbols in front and behind a given text, depending on this orientation. */
        public StringBuilder decorate(String text) {
            StringBuilder result = new StringBuilder(text);
            if (this.left != null) {
                result.insert(0, SP);
                result.insert(0, this.left);
            }
            if (this.right != null) {
                result.append(SP);
                result.append(this.right);
            }
            return result;
        }

        /** String to place to the left side of the label. */
        private final String left;
        /** String to place to the right side of the label. */
        private final String right;
    }

    static private final String LA = HTMLConverter.toHtml(Util.LT);
    static private final String RA = HTMLConverter.toHtml(Util.RT);
    static private final String UA = HTMLConverter.toHtml(Util.UT);
    static private final String DA = HTMLConverter.toHtml(Util.DT);
    static private final String SP = HTMLConverter.toHtml(Util.THIN_SPACE);
}
