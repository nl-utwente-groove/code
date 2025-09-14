/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.line;

import java.awt.Color;

import nl.utwente.groove.util.line.HTMLLineFormat.HTMLBuilder;
import nl.utwente.groove.util.line.Line.ColorType;
import nl.utwente.groove.util.line.Line.Style;

/**
 * Formatter that only measures the width and height of the rectangle
 * @author Arend Rensink
 * @version $Revision$
 */
public class MatrixFormat extends LineFormat<MatrixFormat.MatrixBuilder> {
    /** Empty constructor for the singleton instance. */
    private MatrixFormat() {
        // empty
    }

    @Override
    public MatrixBuilder applyColored(ColorType type, Color color, MatrixBuilder subline) {
        return subline;
    }

    @Override
    public MatrixBuilder applyStyled(Style style, MatrixBuilder subline) {
        return subline;
    }

    @Override
    public MatrixBuilder createHRule() {
        var result = createResult();
        result.appendLineBreak();
        return result;
    }

    @Override
    public MatrixBuilder applyAtomic(String text) {
        return new MatrixBuilder(text);
    }

    @Override
    public MatrixBuilder createResult() {
        return new MatrixBuilder();
    }

    /** Returns the singleton instance of this format. */
    public static MatrixFormat instance() {
        if (instance == null) {
            instance = new MatrixFormat();
        }
        return instance;
    }

    /** The singleton instance of this format. */
    private static MatrixFormat instance;

    /** Builder for the {@link MatrixFormat} class. */
    static public class MatrixBuilder extends LineFormat.Builder<MatrixBuilder> {
        /** Constructs an initially empty builder. */
        MatrixBuilder() {
            this.height = 1;
            this.html = new HTMLBuilder();
        }

        /** Constructs a builder initialised with a given string. */
        MatrixBuilder(String atom) {
            this.height = 1;
            int width = this.firstLineLength = this.lastLineLength = this.width = atom.length();
            this.html = new HTMLBuilder("X".repeat(width));
        }

        @Override
        public boolean isEmpty() {
            return this.html.isEmpty();
        }

        @Override
        public StringBuilder getResult() {
            return this.html.getResult();
        }

        @Override
        public void append(MatrixBuilder other) {
            int lineLength = this.lastLineLength + other.firstLineLength;
            this.width = Math.max(Math.max(this.width, other.width), lineLength);
            this.height = this.height + other.height - 1;
            this.lastLineLength = other.lastLineLength;
            this.html.append(other.html);
        }

        @Override
        public void appendLineBreak() {
            this.height++;
            this.lastLineLength = 0;
        }

        /** Returns the width of the matrix. */
        public int getWidth() {
            return this.width;
        }

        /** Returns the height of the matrix. */
        public int getHeight() {
            return this.height;
        }

        private final HTMLBuilder html;
        private int width;
        private int height;
        private int firstLineLength;
        private int lastLineLength;
    }
}
