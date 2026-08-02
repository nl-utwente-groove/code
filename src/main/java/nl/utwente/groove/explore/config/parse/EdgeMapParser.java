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
package nl.utwente.groove.explore.config.parse;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parser for a mapping of edge labels to upper bounds, in the syntax
 * <code>label&gt;num[,label&gt;num]*</code>. Successor of the parsing in
 * the legacy {@code EncodedEdgeMap}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class EdgeMapParser {
    private EdgeMapParser() {
        // static utility class
    }

    /**
     * Parses an edge-bound map, with labels resolved in the type graph of a
     * given grammar.
     * @throws FormatException if the text does not parse, or contains a
     * label that does not occur in the type graph
     */
    public static Map<TypeLabel,Integer> parse(Grammar grammar,
                                               String text) throws FormatException {
        return parse(grammar.getTypeGraph(), text);
    }

    /** Parses an edge-bound map, with labels resolved in a given type graph. */
    public static Map<TypeLabel,Integer> parse(TypeGraph typeGraph,
                                               String text) throws FormatException {
        if (text.isEmpty()) {
            throw new FormatException("The empty string is not a valid condition edge>num");
        }
        Map<TypeLabel,Integer> result = new TreeMap<>();
        for (String unit : text.split(",")) {
            String[] assignment = unit.split(">");
            if (assignment.length != 2) {
                throw new FormatException("'%s' is not a valid condition edge>num", unit);
            }
            result.put(parseLabel(typeGraph, assignment[0]), parseBound(assignment[1]));
        }
        return result;
    }

    /** Resolves an edge label in a type graph. */
    private static TypeLabel parseLabel(TypeGraph typeGraph,
                                        String text) throws FormatException {
        for (TypeLabel label : typeGraph.getLabels()) {
            if (label.text().equals(text)) {
                return label;
            }
        }
        throw new FormatException("'%s' is not a valid edge name in the current grammar", text);
    }

    /** Parses an edge bound (a non-negative number). */
    private static Integer parseBound(String text) throws FormatException {
        int result;
        try {
            result = Integer.parseInt(text, 10);
        } catch (NumberFormatException exc) {
            throw new FormatException("'%s' is not a valid edge bound", text);
        }
        if (result < 0) {
            throw new FormatException("'%s' is not a valid edge bound", text);
        }
        return result;
    }
}
