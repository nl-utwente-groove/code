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
import groove.io.Util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Class wrapping the functionality to convert a multi-line label
 * into a list of labels with orientation decorations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MultiLabel {
    /** Adds a directed line to this multiline label. */
    public void add(Line line, Direct direct) {
        if (!line.isEmpty()) {
            add(new Part(line, direct));
        }
    }

    /** Adds a list of directed lines to this multiline label. */
    public void addAll(List<Line> lines, Direct direct) {
        for (Line line : lines) {
            add(line, direct);
        }
    }

    /** Adds all parts of another multiline label to this one. */
    public void add(MultiLabel label) {
        for (Part part : label.getParts()) {
            add(part);
        }
    }

    private void add(Part part) {
        this.parts.add(part);
        this.direct = this.direct.union(part.getDirect());
    }

    /** Returns the line parts of this multiline label. */
    public List<Part> getParts() {
        return this.parts;
    }

    /** Returns the union of all directions in this label. */
    public Direct getDirect() {
        return this.direct;
    }

    /** Indicates if the list of lines is empty. */
    public boolean isEmpty() {
        return this.parts.isEmpty();
    }

    /**
     * Computes a string representation of this label, for a given renderer
     * and with or without orientation decorations. 
     */
    public StringBuilder toString(LineFormat renderer, Point2D start,
            Point2D end) {
        StringBuilder result = new StringBuilder();
        for (Part part : getParts()) {
            if (result.length() != 0) {
                result.append(renderer.getLineBreak());
            }
            Line line;
            if (start != null) {
                Orient orient = part.direct.getOrient(start, end);
                line = orient.decorate(part.line);
            } else {
                line = part.line;
            }
            result.append(renderer.toString(line));
        }
        return result;
    }

    /**
     * Computes a string representation of this label, for a given renderer
     * and without orientation decorations. 
     */
    public StringBuilder toString(LineFormat renderer) {
        return toString(renderer, null, null);
    }

    @Override
    public String toString() {
        return this.parts.toString();
    }

    private final List<Part> parts = new ArrayList<MultiLabel.Part>();
    /** The combined direction of this label. */
    private Direct direct = Direct.NONE;

    /**
     * Constructs a label with a given line and direction,
     * or empty if the line is {@code null}.
     * @param line line for the new label;may be {@code null}
     * @param direct direction for the new label; non-{@code null}
     */
    public static MultiLabel singleton(Line line, Direct direct) {
        MultiLabel result = new MultiLabel();
        if (line != null && !line.isEmpty()) {
            result.add(line, direct);
        }
        return result;
    }

    /** Single line of a multiline label. */
    public static class Part {
        /** Constructs a part consisting of a line and a direction. */
        public Part(Line line, Direct direct) {
            this.line = line;
            this.direct = direct;
        }

        /** Returns the line of this label part. */
        public Line getLine() {
            return this.line;
        }

        /** Returns the direction of this label part. */
        public Direct getDirect() {
            return this.direct;
        }

        @Override
        public String toString() {
            String result = "\"" + this.line + "\"";
            if (this.direct != Direct.NONE) {
                result += " as " + this.direct;
            }
            return result;
        }

        private final Line line;
        private final Direct direct;
    }

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
                if (Math.abs(dx) >= Math.abs(dy) * 3) {
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
                if (Math.abs(dx) >= Math.abs(dy) * 3) {
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
         * Computes the union of this direction and another.
         * Union means union of edge ends.
         */
        public Direct union(Direct other) {
            Direct result;
            switch (this) {
            case BACKWARD:
                if (other == BACKWARD || other == BIRIDECTIONAL) {
                    result = BIRIDECTIONAL;
                } else {
                    result = this;
                }
                break;
            case FORWARD:
                if (other == FORWARD || other == BIRIDECTIONAL) {
                    result = BIRIDECTIONAL;
                } else {
                    result = this;
                }
                break;
            case NONE:
                result = other;
                break;
            case BIRIDECTIONAL:
                result = this;
                break;
            default:
                assert false;
                result = null;
            }
            return result;
        }

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
        UP_DOWN(UAL, DAR),
        /** Pointing down-left and up-right. */
        DOWN_UP(DAL, UAR),
        /** Pointing up left. */
        UP_LEFT(UAL, null),
        /** Pointing down left. */
        DOWN_LEFT(DAL, null),
        /** Pointing up right. */
        UP_RIGHT(null, UAR),
        /** Pointing down right. */
        DOWN_RIGHT(null, DAR);

        private Orient(Line left, Line right) {
            this.left = left;
            this.right = right;
        }

        /** Inserts symbols in front and behind a given text, depending on this orientation. */
        public Line decorate(Line line) {
            Line result = line;
            if (this.left != null) {
                result = this.left.append(result);
            }
            if (this.right != null) {
                result = result.append(this.right);
            }
            return result;
        }

        /** String to place to the left side of the label. */
        private final Line left;
        /** String to place to the right side of the label. */
        private final Line right;
    }

    static private final String SP = "" + Util.THIN_SPACE;
    static private final Line LA = Line.atom("" + Util.LT + SP);
    static private final Line RA = Line.atom(SP + Util.RT);
    static private final Line UAL = Line.atom("" + Util.UT + SP);
    static private final Line DAL = Line.atom("" + Util.DT + SP);
    static private final Line UAR = Line.atom(SP + Util.UT);
    static private final Line DAR = Line.atom(SP + Util.DT);
}
