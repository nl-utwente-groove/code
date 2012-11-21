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
 * an appropriate {@link LineRenderer}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Line {
    /** Converts this object to a string representation by applying a given renderer. */
    abstract public StringBuilder toString(LineRenderer renderer);

    /** Returns a coloured version of this line. */
    public Colored color(Color color) {
        return new Colored(color, this);
    }

    /** Returns a styled version of this line. */
    public Styled style(Style style) {
        return new Styled(style, this);
    }

    /** Returns a composed line consisting of this line and a sequence of others. */
    public Composed append(Line... lines) {
        Line[] sublines = new Line[lines.length + 1];
        sublines[0] = this;
        System.arraycopy(lines, 0, sublines, 1, lines.length);
        return new Composed(sublines);
    }

    /** Returns an atomic line consisting of a given string. */
    public static Atomic atomic(String text) {
        return new Atomic(text);
    }

    /** Returns a multiline consisting of a list of sublines */
    public static Multi multi(List<Line> sublines) {
        return new Multi(sublines);
    }

    /** Returns a composed line consisting of a list of fragments. */
    public static Composed composed(List<Line> fragments) {
        return new Composed(fragments);
    }

    /** Multiline consisting of a sequence of sublines. */
    static public class Multi extends Line {
        /** Constructs an instance for a list of sublines. */
        public Multi(List<Line> sublines) {
            this.sublines = new Line[sublines.size()];
            sublines.toArray(this.sublines);
        }

        @Override
        public StringBuilder toString(LineRenderer renderer) {
            StringBuilder[] sublines = new StringBuilder[this.sublines.length];
            for (int i = 0; i < sublines.length; i++) {
                sublines[i] = this.sublines[i].toString(renderer);
            }
            return renderer.applyMulti(sublines);
        }

        @Override
        public String toString() {
            return "Multi[" + Arrays.toString(this.sublines) + "]";
        }

        /** The sublines of this multiline. */
        private final Line[] sublines;
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
        public StringBuilder toString(LineRenderer renderer) {
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
        public StringBuilder toString(LineRenderer renderer) {
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
        public StringBuilder toString(LineRenderer renderer) {
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
        public StringBuilder toString(LineRenderer renderer) {
            return renderer.applyAtomic(this.text);
        }

        @Override
        public String toString() {
            return "Atomic[" + this.text + "]";
        }

        private final String text;
    }

    /** Character style. */
    public static enum Style {
        /** Bold font. */
        BOLD,
        /** Italic font. */
        ITALIC,
        /** Strikethrough font. */
        STRIKE;
    }
}
