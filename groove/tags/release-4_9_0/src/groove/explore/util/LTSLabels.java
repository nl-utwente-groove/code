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

import groove.explore.GeneratorOptions;
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
    }

    /** Constructs a flag object with default values for selected flags. */
    public LTSLabels(Flag... flags) {
        for (Flag flag : flags) {
            setDefaultValue(flag);
        }
    }

    /**
     * Constructs a flags object according to a specification
     * formatted as described in {@link GeneratorOptions}.
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
            if (this.labelMap.containsKey(flag)) {
                throw new FormatException("Start flag '%c' occurs twice in %s",
                    flag.getId(), spec);
            }
            String value = flag.getDef();
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

    /** Indicates if the start label is set. */
    public boolean showStart() {
        return this.labelMap.containsKey(Flag.START);
    }

    /**
     * Returns the label to be used for start states in serialised LTSs, if any.
     * @return the label to be used for start states; if {@code null}, start states
     * remain unlabelled
     */
    public String getStartLabel() {
        return getValue(Flag.START);
    }

    /** Indicates if the open label is set. */
    public boolean showOpen() {
        return this.labelMap.containsKey(Flag.OPEN);
    }

    /**
     * Returns the label to be used for open states in serialised LTSs, if any.
     * @return the label to be used for open states; if {@code null}, open states
     * remain unlabelled
     */
    public String getOpenLabel() {
        return getValue(Flag.OPEN);
    }

    /** Indicates if the final label is set. */
    public boolean showFinal() {
        return this.labelMap.containsKey(Flag.FINAL);
    }

    /**
     * Returns the label to be used for final states in serialised LTSs, if any.
     * @return the label to be used for final states; if {@code null}, final states
     * remain unlabelled
     */
    public String getFinalLabel() {
        return getValue(Flag.FINAL);
    }

    /** Indicates if the result label is set. */
    public boolean showResult() {
        return this.labelMap.containsKey(Flag.RESULT);
    }

    /**
     * Returns the label to be used for result states in serialised LTSs, if any.
     * @return the label to be used for result states; if {@code null}, result states
     * remain unlabelled
     */
    public String getResultLabel() {
        return getValue(Flag.RESULT);
    }

    /** Indicates if the number flag is set. */
    public boolean showNumber() {
        return this.labelMap.containsKey(Flag.NUMBER);
    }

    /**
     * Returns the label to be used for state numbers in serialised LTSs, if any.
     * @return the label to be used for state numbers; if {@code null}, states
     * are not numbered
     */
    public String getNumberLabel() {
        return getValue(Flag.NUMBER);
    }

    private String getValue(Flag flag) {
        return this.labelMap.get(flag);
    }

    private boolean setDefaultValue(Flag flag) {
        return setValue(flag, flag.getDef());
    }

    private boolean setValue(Flag flag, String value) {
        return this.labelMap.put(flag, value) == null;
    }

    private Map<Flag,String> labelMap = new EnumMap<Flag,String>(Flag.class);

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
        START('s', "start"),
        /** Labelling for open states. */
        OPEN('o', "open"),
        /** Labelling for final states. */
        FINAL('f', "final"),
        /** Labelling for result states. */
        RESULT('r', "result"),
        /** Labelling of state numbers. */
        NUMBER('n', "s#");

        private Flag(char id, String def) {
            this.id = id;
            this.def = def;
        }

        /** Returns the identifying character for this flag. */
        public char getId() {
            return this.id;
        }

        /** Returns the default value for this flag. */
        public String getDef() {
            return this.def;
        }

        private final char id;
        private final String def;
    }
}
