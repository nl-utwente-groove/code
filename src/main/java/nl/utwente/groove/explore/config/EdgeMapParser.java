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
package nl.utwente.groove.explore.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parser for a mapping of edge labels to upper bounds, in the syntax
 * <code>label&gt;num[,label&gt;num]*</code>. Labels may carry an explicit
 * {@code type:} or {@code flag:} prefix; a bare name denotes a binary edge
 * if the type graph has one, and is otherwise accepted if it resolves
 * unambiguously. Successor of the parsing in the legacy {@code EncodedEdgeMap}.
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
        Map<TypeLabel,Integer> result = new TreeMap<>();
        for (var entry : parseRaw(text).entrySet()) {
            result.put(parseLabel(typeGraph, entry.getKey()), entry.getValue());
        }
        return result;
    }

    /**
     * Parses an edge-bound map syntactically, without resolving the labels;
     * the keys of the resulting map are the (possibly prefixed) label texts.
     * This is the check to use when no grammar is available yet.
     * @throws FormatException if the text does not parse
     */
    public static Map<String,Integer> parseRaw(String text) throws FormatException {
        if (text.isEmpty()) {
            throw new FormatException("The empty string is not a valid condition edge>num");
        }
        Map<String,Integer> result = new LinkedHashMap<>();
        for (String unit : text.split(",")) {
            String[] assignment = unit.split(">");
            if (assignment.length != 2) {
                throw new FormatException("'%s' is not a valid condition edge>num", unit);
            }
            result.put(assignment[0], parseBound(assignment[1]));
        }
        return result;
    }

    /** Resolves an edge label in a type graph.
     * The label text may carry an explicit {@code type:} or {@code flag:}
     * prefix, which selects exactly that label role; a bare name denotes a
     * binary edge if the type graph has one, and otherwise resolves to a
     * node type or flag of that name provided this is unambiguous.
     */
    private static TypeLabel parseLabel(TypeGraph typeGraph,
                                        String text) throws FormatException {
        var prefixedText = EdgeRole.parseLabel(text);
        EdgeRole role = prefixedText.one();
        String name = prefixedText.two();
        @Nullable
        TypeLabel binary = null, nodeType = null, flag = null;
        for (TypeLabel label : typeGraph.getLabels()) {
            if (!label.text().equals(name)) {
                continue;
            }
            EdgeRole labelRole = label.getRole();
            if (labelRole == EdgeRole.BINARY) {
                binary = label;
            } else if (labelRole == EdgeRole.NODE_TYPE) {
                nodeType = label;
            } else {
                flag = label;
            }
        }
        @Nullable
        TypeLabel result;
        if (role == EdgeRole.NODE_TYPE) {
            result = nodeType;
        } else if (role == EdgeRole.FLAG) {
            result = flag;
        } else if (binary != null) {
            // a bare name primarily denotes a binary edge
            result = binary;
        } else if (nodeType != null && flag != null) {
            throw new FormatException(
                "Edge name '%s' is ambiguous in the current grammar; use a '%s' or '%s' prefix",
                text, EdgeRole.NODE_TYPE.getPrefix(), EdgeRole.FLAG.getPrefix());
        } else {
            result = nodeType != null
                ? nodeType
                : flag;
        }
        if (result == null) {
            throw new FormatException("'%s' is not a valid edge name in the current grammar", text);
        }
        return result;
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
