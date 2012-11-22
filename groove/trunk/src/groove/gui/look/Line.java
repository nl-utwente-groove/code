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

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Generic representation of a label line.
 * The representation can be converted to a String by providing
 * an appropriate {@link LineFormat}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Line {
    /** Converts this object to a string representation by applying a given renderer. */
    abstract public StringBuilder toString(LineFormat renderer);

    /** Returns a coloured version of this line. */
    public Line color(Color color) {
        if (isEmpty()) {
            return this;
        } else {
            return new Colored(color, this);
        }
    }

    /** Returns a styled version of this line. */
    public Line style(Style style) {
        if (isEmpty()) {
            return this;
        } else {
            return new Styled(style, this);
        }
    }

    /** Returns a composed line consisting of this line and a sequence of others. */
    public Line append(Line... args) {
        Line result;
        if (this == empty) {
            if (args.length == 0) {
                result = this;
            } else if (args.length == 1) {
                result = args[0];
            } else {
                result = new Composed(args);
            }
        } else if (this instanceof Composed) {
            Line[] oldFragments = ((Composed) this).fragments;
            Line[] newFragments = new Line[oldFragments.length + args.length];
            System.arraycopy(oldFragments, 0, newFragments, 0,
                oldFragments.length);
            System.arraycopy(args, 0, newFragments, oldFragments.length,
                args.length);
            result = new Composed(newFragments);
        } else {
            Line[] newFragments = new Line[args.length + 1];
            newFragments[0] = this;
            System.arraycopy(args, 0, newFragments, 1, args.length);
            result = new Composed(newFragments);
        }
        return result;
    }

    /** Returns a composed line consisting of this line and an atomic line. */
    public Line append(String atom) {
        Line result;
        if (this == empty) {
            result = Line.atom(atom);
        } else if (this instanceof Atomic) {
            result = Line.atom(((Atomic) this).text + atom);
        } else if (this instanceof Composed) {
            Line[] oldFragments = ((Composed) this).fragments;
            Line[] newFragments = new Line[oldFragments.length + 1];
            System.arraycopy(oldFragments, 0, newFragments, 0,
                oldFragments.length);
            newFragments[oldFragments.length] = Line.atom(atom);
            result = new Composed(newFragments);
        } else {
            result = new Composed(this, Line.atom(atom));
        }
        return result;
    }

    /** Tests if this is the empty line. */
    public boolean isEmpty() {
        return this == empty;
    }

    /** Returns the (fixed) empty line. */
    public static Empty empty() {
        return empty;
    }

    /** Returns an atomic line consisting of a given string. */
    public static Line atom(String text) {
        if (text == null || text.length() == 0) {
            return empty;
        } else {
            return new Atomic(text);
        }
    }

    /** Returns a composed line consisting of a list of fragments. */
    public static Composed composed(List<Line> fragments) {
        return new Composed(fragments);
    }

    private final static Empty empty = new Empty();

    /** Composed line consisting of a sequence of subline fragments. */
    static public class Composed extends Line {
        /** Constructs an instance for a list of subline fragments. */
        public Composed(Line... fragments) {
            this.fragments = fragments;
        }

        /** Constructs an instance for a list of subline fragments. */
        public Composed(List<Line> fragments) {
            this.fragments = new Line[fragments.size()];
            fragments.toArray(this.fragments);
        }

        @Override
        public StringBuilder toString(LineFormat renderer) {
            StringBuilder[] fragments =
                new StringBuilder[this.fragments.length];
            for (int i = 0; i < fragments.length; i++) {
                fragments[i] = this.fragments[i].toString(renderer);
            }
            return renderer.applyComposed(fragments);
        }

        @Override
        public String toString() {
            return "Composed[" + Arrays.toString(this.fragments) + "]";
        }

        /** The fragments of this composed line. */
        private final Line[] fragments;
    }

    /** Line consisting of a coloured subline. */
    static public class Colored extends Line {
        /** Constructs an instance for a non-{@code null} colour and subline. */
        public Colored(Color color, Line subline) {
            this.color = color;
            this.subline = subline;
        }

        @Override
        public StringBuilder toString(LineFormat renderer) {
            StringBuilder subline = this.subline.toString(renderer);
            return renderer.applyColored(this.color, subline);
        }

        @Override
        public String toString() {
            return "Colored[" + this.color + ", " + this.subline + "]";
        }

        /** Colour to apply. */
        private final Color color;
        /** The subline to be coloured. */
        private final Line subline;
    }

    /** Line consisting of a subline with a character style applied. */
    static public class Styled extends Line {
        /** Constructs an instance for a non-{@code null} colour and subline. */
        public Styled(Style style, Line subline) {
            this.style = style;
            this.subline = subline;
        }

        @Override
        public StringBuilder toString(LineFormat renderer) {
            StringBuilder subline = this.subline.toString(renderer);
            return renderer.applyStyled(this.style, subline);
        }

        @Override
        public String toString() {
            return "Styled[" + this.style + ", " + this.subline + "]";
        }

        /** Style to apply. */
        private final Style style;
        /** The subline to be coloured. */
        private final Line subline;
    }

    /** Line consisting of an atomic string. */
    static public class Atomic extends Line {
        /** Constructs an instance for a non-{@code null} string. */
        public Atomic(String text) {
            this.text = text;
        }

        @Override
        public StringBuilder toString(LineFormat renderer) {
            return renderer.applyAtomic(this.text);
        }

        @Override
        public String toString() {
            return "Atomic[" + this.text + "]";
        }

        private final String text;
    }

    /** Empty line consisting of an atomic string. */
    static public class Empty extends Line {
        /** Constructs an instance for a non-{@code null} string. */
        private Empty() {
            // empty
        }

        @Override
        public StringBuilder toString(LineFormat renderer) {
            return new StringBuilder();
        }

        @Override
        public String toString() {
            return "Empty";
        }
    }

    /** Character style. */
    public static enum Style {
        /** Bold font. */
        BOLD,
        /** Italic font. */
        ITALIC,
        /** Strikethrough font. */
        STRIKE,
        /** Superscript. */
        SUPER;
    }
}
