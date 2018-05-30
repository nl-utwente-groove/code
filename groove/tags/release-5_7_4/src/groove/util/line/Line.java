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
package groove.util.line;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import groove.gui.look.Values;
import groove.util.line.LineFormat.Builder;

/**
 * Generic representation of a formatted line of text,
 * typically used to represent a node or edge label.
 * The representation can be converted to a String by providing
 * an appropriate {@link LineFormat}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Line {
    /** Converts this object to a string representation by applying a given renderer. */
    abstract public <R extends Builder<R>> R toString(LineFormat<R> renderer);

    /**
     * Returns a flattened string rendering of this line.
     * Convenience method for {@code toString(StringFormat.instance()).toString()}.
     */
    public @NonNull String toFlatString() {
        String result = this.flatString;
        if (result == null) {
            this.flatString = result = toString(StringFormat.instance()).toString();
            assert result != null;
        }
        return result;
    }

    private String flatString;

    /**
     * Returns an HTML string rendering of this line.
     * Convenience method for {@code toString(HTMLLineFormat.instance()).toString()}.
     */
    public String toHTMLString() {
        if (this.htmlString == null) {
            this.htmlString = toString(HTMLLineFormat.instance()).toString();
        }
        return this.htmlString;
    }

    private String htmlString;

    /** Returns a coloured version of this line,
     * where the colour is specified as a logical colour type.
     */
    public Line color(ColorType type) {
        if (isEmpty()) {
            return this;
        } else {
            return new Colored(type, type.getColor(), this);
        }
    }

    /** Returns a coloured version of this line,
     * where the colour is specified as a user-provided RGB value.
     */
    public @NonNull Line color(Color color) {
        if (isEmpty()) {
            return this;
        } else {
            return new Colored(ColorType.RGB, color, this);
        }
    }

    /** Returns a styled version of this line. */
    public @NonNull Line style(Style style) {
        if (isEmpty()) {
            return this;
        } else {
            return new Styled(style, this);
        }
    }

    /** Returns a composed line consisting of this line and a sequence of others. */
    public @NonNull Line append(@NonNull Line... args) {
        Line result;
        if (isEmpty()) {
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
            System.arraycopy(oldFragments, 0, newFragments, 0, oldFragments.length);
            System.arraycopy(args, 0, newFragments, oldFragments.length, args.length);
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
    public @NonNull Line append(String atom) {
        Line result;
        if (this == empty) {
            result = Line.atom(atom);
        } else if (this instanceof Atomic) {
            result = Line.atom(((Atomic) this).text + atom);
        } else if (this instanceof Composed) {
            Line[] oldFragments = ((Composed) this).fragments;
            Line[] newFragments = new Line[oldFragments.length + 1];
            System.arraycopy(oldFragments, 0, newFragments, 0, oldFragments.length);
            newFragments[oldFragments.length] = Line.atom(atom);
            result = new Composed(newFragments);
        } else {
            result = new Composed(this, Line.atom(atom));
        }
        return result;
    }

    /** Returns a line that equals this one, except
     * that the first character has been turned into lowercase.
     */
    public Line toLower() {
        return capitalise(false);
    }

    /** Returns a line that equals this one, except
     * that the first character has been turned into uppercase.
     */
    public Line toUpper() {
        return capitalise(true);
    }

    /** Returns a line that equals this one, except
     * that the first character has been turned into upper- or
     * lowercase.
     * @param upper if {@code true}, convert the first character to
     * uppercase, otherwise to lowercase.
     */
    public Line capitalise(boolean upper) {
        Line result = this;
        if (isEmpty()) {
            return this;
        } else if (this instanceof Atomic) {
            char c = ((Atomic) this).text.charAt(0);
            char modC = upper ? Character.toUpperCase(c) : Character.toLowerCase(c);
            if (c != modC) {
                StringBuffer content = new StringBuffer(((Atomic) this).text);
                content.setCharAt(0, modC);
                result = Line.atom(content.toString());
            }
        } else if (this instanceof Composed) {
            Line[] oldFragments = ((Composed) this).fragments;
            Line newFragment0 = oldFragments[0].capitalise(upper);
            if (newFragment0 != oldFragments[0]) {
                Line[] newFragments = new Line[oldFragments.length];
                newFragments[0] = newFragment0;
                System.arraycopy(oldFragments, 1, newFragments, 1, oldFragments.length - 1);
                result = composed(Arrays.asList(newFragments));
            }
        } else if (this instanceof Styled) {
            Line oldSubLine = ((Styled) this).subline;
            Line newSubline = oldSubLine.capitalise(upper);
            if (newSubline != oldSubLine) {
                result = newSubline.style(((Styled) this).style);
            }
        } else {
            assert this instanceof Colored;
            Line oldSubLine = ((Colored) this).subline;
            Line newSubline = oldSubLine.capitalise(upper);
            if (newSubline != oldSubLine) {
                ColorType type = ((Colored) this).type;
                Color color = ((Colored) this).color;
                return type == ColorType.RGB ? newSubline.color(color) : newSubline.color(type);
            }
        }
        return result;
    }

    /** Tests if this is the empty line. */
    public boolean isEmpty() {
        return this == empty;
    }

    /** Returns the (fixed) empty line. */
    public static @NonNull Empty empty() {
        return empty;
    }

    private final static @NonNull Empty empty = new Empty();

    /** Returns the (fixed) horizontal rule. */
    public static HRule hrule() {
        return hrule;
    }

    private final static HRule hrule = new HRule();

    /** Returns an atomic line consisting of a given string. */
    public static @NonNull Line atom(String text) {
        if (text == null || text.length() == 0) {
            return empty;
        } else {
            return new Atomic(text);
        }
    }

    /** Returns a composed line consisting of a list of fragments. */
    public static @NonNull Composed composed(List<Line> fragments) {
        return new Composed(fragments);
    }

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
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            @SuppressWarnings("unchecked") R[] fragments = (R[]) new Builder[this.fragments.length];
            for (int i = 0; i < fragments.length; i++) {
                fragments[i] = this.fragments[i].toString(renderer);
            }
            return renderer.applyComposed(fragments);
        }

        @Override
        public String toString() {
            return "Composed[" + Arrays.toString(this.fragments) + "]";
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.fragments);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Composed other = (Composed) obj;
            if (!Arrays.equals(this.fragments, other.fragments)) {
                return false;
            }
            return true;
        }

        /** The fragments of this composed line. */
        private final Line[] fragments;
    }

    /** Line consisting of a coloured subline. */
    static public class Colored extends Line {
        /** Constructs an instance for a non-{@code null} colour and subline. */
        public Colored(ColorType type, Color color, Line subline) {
            assert type == ColorType.RGB || color == type.getColor();
            this.type = type;
            this.color = color;
            this.subline = subline;
        }

        @Override
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            R subline = this.subline.toString(renderer);
            return renderer.applyColored(this.type, this.color, subline);
        }

        @Override
        public String toString() {
            return "Colored[" + this.color + ", " + this.subline + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.color.hashCode();
            result = prime * result + this.subline.hashCode();
            result = prime * result + this.type.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Colored other = (Colored) obj;
            if (!this.color.equals(other.color)) {
                return false;
            }
            if (!this.subline.equals(other.subline)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            return true;
        }

        /** Colour to apply. */
        private final ColorType type;
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
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            R subline = this.subline.toString(renderer);
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
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            return renderer.applyAtomic(this.text);
        }

        @Override
        public String toString() {
            return "Atomic[" + this.text + "]";
        }

        @Override
        public int hashCode() {
            return this.text.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Atomic other = (Atomic) obj;
            if (!this.text.equals(other.text)) {
                return false;
            }
            return true;
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
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            return renderer.createResult();
        }

        @Override
        public String toString() {
            return "Empty";
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    /** Line consisting of a horizontal rule. */
    static public class HRule extends Line {
        /** Constructs the singleton instance. */
        private HRule() {
            // empty
        }

        @Override
        public <R extends Builder<R>> R toString(LineFormat<R> renderer) {
            return renderer.createHRule();
        }

        @Override
        public String toString() {
            return "HRule";
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    /** Character style. */
    public static enum Style {
        /** Bold font. */
        BOLD,
        /** Italic font. */
        ITALIC,
        /** Strikethrough font. */
        UNDERLINE,
        /** Underline font. */
        STRIKE,
        /** Superscript. */
        SUPER;
    }

    /** Logical text colours. */
    public static enum ColorType {
        /** Colour for eraser nodes and edges. */
        ERASER(Values.ERASER_FOREGROUND),
        /** Colour for creator nodes and edges. */
        CREATOR(Values.CREATOR_FOREGROUND),
        /** Colour for embargo nodes and edges. */
        EMBARGO(Values.EMBARGO_FOREGROUND),
        /** Colour for remark nodes and edges. */
        REMARK(Values.REMARK_FOREGROUND),
        /** User-specified RGB colour. */
        RGB(null);

        private ColorType(Color color) {
            this.color = color;
        }

        /** Returns the fixed colour associated with this colour type, if any. */
        public Color getColor() {
            return this.color;
        }

        /**
         * The fixed colour for the type, or {@code null}
         * if the colour type has no fixed colour.
         */
        private final Color color;
    }
}
