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
package groove.explore.util;

import groove.explore.Generator;
import groove.grammar.model.FormatException;
import groove.util.ExprParser;
import groove.util.Pair;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing special state labels for serialised LTSs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSLabels {
    /** Constructs a flag object with default values for selected flags. */
    public LTSLabels(boolean showStart, boolean showOpen, boolean showFinal,
            boolean showResult, boolean showNumber) {
        try {
            if (showStart) {
                setDefaultValue(Flag.START);
            }
            if (showOpen) {
                setDefaultValue(Flag.OPEN);
            }
            if (showFinal) {
                setDefaultValue(Flag.FINAL);
            }
            if (showResult) {
                setDefaultValue(Flag.RESULT);
            }
            if (showNumber) {
                setDefaultValue(Flag.NUMBER);
            }
        } catch (FormatException e) {
            assert false : "Unexpected error";
            throw new IllegalStateException(e);
        }
    }

    /** Constructs a flag object with default values for selected flags. */
    public LTSLabels(Flag... flags) {
        try {
            for (Flag flag : flags) {
                setDefaultValue(flag);
            }
        } catch (FormatException e) {
            assert false : "Unexpected error";
            throw new IllegalStateException(e);
        }
    }

    /** Constructs a flag object with default values for selected flags. */
    public LTSLabels(Map<Flag,String> flags) {
        try {
            for (Flag flag : flags.keySet()) {
                setValue(flag, flags.get(flag));
            }
        } catch (FormatException e) {
            assert false : "Unexpected error";
            throw new IllegalStateException(e);
        }
    }

    /**
     * Constructs a flags object according to a specification
     * formatted as described in {@link Generator}.
     */
    public LTSLabels(String spec) throws FormatException {
        Pair<String,List<String>> parsedFlags = FLAG_PARSER.parse(spec);
        String flagPart = parsedFlags.one();
        List<String> argPart = parsedFlags.two();
        int charIx = 0;
        int argIx = 0;
        while (charIx < flagPart.length()) {
            char c = flagPart.charAt(charIx);
            charIx++;
            Flag flag = getFlag(c);
            if (flag == null) {
                throw new FormatException("Unknown flag '%c' in %s", c, spec);
            }
            if (this.flagToLabelMap.containsKey(flag)) {
                throw new FormatException("Start flag '%c' occurs twice in %s",
                    flag.getId(), spec);
            }
            String value = flag.getDefault();
            if (charIx < flagPart.length()
                && flagPart.charAt(charIx) == SINGLE_QUOTE) {
                String arg = argPart.get(argIx);
                value = arg.substring(1, arg.length() - 1);
                argIx++;
                charIx++;
                if (flag == Flag.NUMBER && value.indexOf('#') < 0) {
                    throw new FormatException(
                        "State number label %s does not contain placeholder '#'",
                        value);
                }
            }
            if (!setValue(flag, value)) {
                throw new FormatException("Flag '%c' occurs twice in %s",
                    flag.getId(), spec);
            }
        }
    }

    /** Indicates if the {@link Flag#START} label is set. */
    public boolean showStart() {
        return this.flagToLabelMap.containsKey(Flag.START);
    }

    /**
     * Returns the label to be used for start states in serialised LTSs, if any.
     * @return the label to be used for start states; if {@code null}, start states
     * remain unlabelled
     */
    public String getStartLabel() {
        return getLabel(Flag.START);
    }

    /** Indicates if the {@link Flag#OPEN} label is set. */
    public boolean showOpen() {
        return this.flagToLabelMap.containsKey(Flag.OPEN);
    }

    /**
     * Returns the label to be used for open states in serialised LTSs, if any.
     * @return the label to be used for open states; if {@code null}, open states
     * remain unlabelled
     */
    public String getOpenLabel() {
        return getLabel(Flag.OPEN);
    }

    /** Indicates if the {@link Flag#FINAL} label is set. */
    public boolean showFinal() {
        return this.flagToLabelMap.containsKey(Flag.FINAL);
    }

    /**
     * Returns the label to be used for final states in serialised LTSs, if any.
     * @return the label to be used for final states; if {@code null}, final states
     * remain unlabelled
     */
    public String getFinalLabel() {
        return getLabel(Flag.FINAL);
    }

    /** Indicates if the result label is set. */
    public boolean showResult() {
        return this.flagToLabelMap.containsKey(Flag.RESULT);
    }

    /**
     * Returns the label to be used for result states in serialised LTSs, if any.
     * @return the label to be used for result states; if {@code null}, result states
     * remain unlabelled
     */
    public String getResultLabel() {
        return getLabel(Flag.RESULT);
    }

    /** Indicates if the {@link Flag#NUMBER} flag is set. */
    public boolean showNumber() {
        return this.flagToLabelMap.containsKey(Flag.NUMBER);
    }

    /**
     * Returns the label to be used for state numbers in serialised LTSs, if any.
     * @return the label to be used for state numbers; if {@code null}, states
     * are not numbered
     */
    public String getNumberLabel() {
        return getLabel(Flag.NUMBER);
    }

    /**
     * Returns the label associated with a given flag, if any.
     */
    public String getLabel(Flag flag) {
        return this.flagToLabelMap.get(flag);
    }

    /** Returns the flag corresponding to a given label text, if any. */
    public Flag getFlag(String label) {
        return this.labelToFlagMap.get(label);
    }

    private boolean setDefaultValue(Flag flag) throws FormatException {
        return setValue(flag, flag.getDefault());
    }

    private boolean setValue(Flag flag, String value) throws FormatException {
        Flag oldFlag = this.labelToFlagMap.put(value, flag);
        if (oldFlag != null) {
            throw new FormatException(
                "Label '%s' used for two different special labels");
        }
        return this.flagToLabelMap.put(flag, value) == null;
    }

    @Override
    public int hashCode() {
        return this.flagToLabelMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LTSLabels)) {
            return false;
        }
        LTSLabels other = (LTSLabels) obj;
        return this.flagToLabelMap.equals(other.flagToLabelMap);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Flag flag : Flag.values()) {
            String label = getLabel(flag);
            if (label != null) {
                result.append(flag.getId());
                if (!label.equals(flag.getDefault())) {
                    result.append(SINGLE_QUOTE);
                    result.append(label);
                    result.append(SINGLE_QUOTE);
                }
            }
        }
        return result.toString();
    }

    private final Map<Flag,String> flagToLabelMap = new EnumMap<Flag,String>(
        Flag.class);
    private final Map<String,Flag> labelToFlagMap =
        new HashMap<String,LTSLabels.Flag>();

    /** Returns the flag for a given identifying character. */
    private static Flag getFlag(char c) {
        return flagMap.get(c);
    }

    /** Flags object with all labels set to null. */
    public static final LTSLabels EMPTY = new LTSLabels();
    /** Flags object with all labels set to default. */
    public static final LTSLabels DEFAULT = new LTSLabels(Flag.values());

    private static final char SINGLE_QUOTE = ExprParser.SINGLE_QUOTE_CHAR;
    private static final ExprParser FLAG_PARSER;
    static {
        char[] singleQuoteArray = {SINGLE_QUOTE};
        FLAG_PARSER = new ExprParser(SINGLE_QUOTE, singleQuoteArray);
    }

    private static final Map<Character,Flag> flagMap =
        new HashMap<Character,LTSLabels.Flag>();

    static {
        for (Flag f : Flag.values()) {
            flagMap.put(f.getId(), f);
        }
    }

    /** Flag controlling extra labels in serialised LTSs. */
    public static enum Flag {
        /** Labelling for start states. */
        START('s', "start", "Start state"),
        /** Labelling for open states. */
        OPEN('o', "open", "Open states"),
        /** Labelling for final states. */
        FINAL('f', "final", "Final states"),
        /** Labelling for result states. */
        RESULT('r', "result", "Result states"),
        /** Labelling of state numbers. */
        NUMBER('n', "s#", "State number");

        private Flag(char id, String def, String descr) {
            this.id = id;
            this.def = def;
            this.descr = descr;
        }

        /** Returns the identifying character for this flag. */
        public char getId() {
            return this.id;
        }

        /** Returns the description text of this flag. */
        public String getDescription() {
            return this.descr;
        }

        /** Returns the default value for this flag. */
        public String getDefault() {
            return this.def;
        }

        private final char id;
        private final String descr;
        private final String def;
    }
}
