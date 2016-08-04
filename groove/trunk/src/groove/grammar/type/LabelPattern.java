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
package groove.grammar.type;

import groove.grammar.aspect.AspectKind;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.graph.EdgeRole;
import groove.util.Pair;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

/**
 * Encoding of a string pattern that can generate a label from the
 * values of certain node attribute fields.
 * This is used in labelling nodified edges.
 * @author Arend Rensink
 * @version $Revision $
 * @see AspectKind#EDGE
 */
public class LabelPattern {
    /**
     * Constructs a new pattern, with a given format string
     * and list of attribute field names.
     */
    public LabelPattern(String format, List<String> argNames) throws FormatException {
        this.format = format;
        this.argNames.addAll(argNames);
        List<Object> testValues1 = new ArrayList<>();
        List<Object> testValues2 = new ArrayList<>();
        for (int i = 0; i < argNames.size(); i++) {
            this.argPositions.put(argNames.get(i), i);
            testValues1.add(null);
            testValues2.add("");
        }
        try {
            getLabel(testValues1.toArray());
            getLabel(testValues2.toArray());
        } catch (IllegalFormatException exc) {
            throw new FormatException("Format string \"%s\" not valid for %d arguments", format,
                argNames.size());
        }
    }

    /** Returns the format string. */
    public final String getFormat() {
        return this.format;
    }

    /** Returns the list of argument names. */
    public final List<String> getArgNames() throws IllegalFormatException {
        return this.argNames;
    }

    /**
     * Returns the label text constructed by instantiating the format
     * string with a list of values.
     * @throws IllegalFormatException if the format cannot be instantiated
     * correctly with the given parameters
     */
    public String getLabel(Object... values) {
        return String.format(getFormat(), values);
    }

    /**
     * Returns the label text constructed by looking up the argument values
     * among the outgoing edges of a given node.
     */
    public String getLabel(HostGraph host, HostNode source) {
        Object[] values = new Object[this.argNames.size()];
        for (HostEdge outEdge : host.outEdgeSet(source)) {
            Integer position = this.argPositions.get(outEdge.label().text());
            if (position != null && outEdge.target() instanceof ValueNode) {
                values[position] = ((ValueNode) outEdge.target()).getTerm().toDisplayString();
            }
        }
        return getLabel(values);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.argNames.hashCode();
        result = prime * result + this.format.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('"');
        result.append(this.format);
        result.append('"');
        for (String argName : this.argNames) {
            result.append(',');
            result.append(argName);
        }
        return result.toString();
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
        LabelPattern other = (LabelPattern) obj;
        if (!this.argNames.equals(other.argNames)) {
            return false;
        }
        if (!this.format.equals(other.format)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a pattern obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public LabelPattern relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        LabelPattern result = this;
        if (oldLabel.getRole() == EdgeRole.BINARY) {
            List<String> newArgNames = new ArrayList<>();
            boolean isNew = false;
            for (int i = 0; i < getArgNames().size(); i++) {
                String oldArgName = getArgNames().get(i);
                boolean relabel = oldLabel.text().equals(oldArgName);
                String newArgName = relabel ? newLabel.text() : oldArgName;
                isNew |= newArgName != oldArgName;
                newArgNames.add(newArgName);
            }
            if (isNew) {
                try {
                    result = new LabelPattern(getFormat(), newArgNames);
                } catch (FormatException e) {
                    assert false;
                }
            }
        }
        return result;
    }

    private final String format;
    private final List<String> argNames = new ArrayList<>();
    private final Map<String,Integer> argPositions = new HashMap<>();

    /**
     * Parses a string of the form {@code "format",id1,id2,...'} into a
     * label pattern with format string {@code "format"} and argument
     * names {@code id1}, {@code id2} etc.
     */
    public static LabelPattern parse(String text) throws FormatException {
        Pair<String,List<String>> result = parser.parse(text);
        String resultText = result.one();
        List<String> resultArgs = result.two();
        if (resultText.isEmpty() || resultText.charAt(0) != StringHandler.PLACEHOLDER
            || resultArgs.size() != 1) {
            throw new FormatException("Incorrect label pattern %s", text);
        }
        String format = result.two().get(0).substring(1, result.two().get(0).length() - 1);
        String[] split = resultText.split(",", -1);
        if (split.length == 0 || split[0].length() != 1) {
            throw new FormatException("Incorrect label pattern %s", text);
        }
        List<String> argNames = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            if (isIdentifier(split[i])) {
                argNames.add(split[i]);
            } else {
                throw new FormatException("Incorrect attribute name '%s' in label pattern %s",
                    split[i], text);
            }
        }
        return new LabelPattern(format, argNames);
    }

    private static boolean isIdentifier(String text) {
        boolean result = text.length() > 0;
        if (result) {
            result = Character.isJavaIdentifierStart(text.charAt(0));
            for (int i = 1; result && i < text.length(); i++) {
                result = Character.isJavaIdentifierPart(text.charAt(i));
            }
        }
        return result;
    }

    private static StringHandler parser = new StringHandler("\"");
}
