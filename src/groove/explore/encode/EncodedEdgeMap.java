/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.encode;

import java.util.Map;
import java.util.TreeMap;

import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.util.parse.FormatException;

/**
 * An <code>EncodedEdgeMap</code> describes an encoding of a mapping of edge
 * labels to numbers (upperbounds) by means of a <code>String</code>. The
 * syntax of the <code>String</code> is <code>label>num,[label>num]*</code>
 *
 * @author Maarten de Mol
 */
public class EncodedEdgeMap implements EncodedType<Map<TypeLabel,Integer>,String> {

    /**
     * An edge mapping is simply edited as a <code>String</code>. The edited
     * <code>String</code> can be of any form; its validity is checked only
     * when the <code>parse</code> method is called.
     */
    @Override
    public EncodedTypeEditor<Map<TypeLabel,Integer>,String> createEditor(GrammarModel grammar) {
        return new StringEditor<>(grammar, "label>num,[label>num]*", "", 30);
    }

    /**
     * Parse an edge label out of a <code>String</code>. Returns a
     * <code>FormatException</code> when parsing fails.
     */
    private TypeLabel parseLabel(TypeGraph typegraph, String source) throws FormatException {
        for (TypeLabel label : typegraph.getLabels()) {
            if (label.text().equals(source)) {
                return label;
            }
        }
        throw new FormatException(
            "'" + source + "' is not a valid edge name in the current grammar.");
    }

    /**
     * Parse an edge bound (any positive number) out of a <code>String</code>.
     * Returns a <code>FormatException</code> if parsing fails.
     */
    private Integer parseBound(String source) throws FormatException {
        Integer num = Integer.parseInt(source, 10);
        if (num < 0) {
            throw new FormatException("'" + source + "' is not a valid edge bound.");
        }
        return num;
    }

    /**
     * Parse an edge-bound map out of a given formatted <code>String</code>.
     * The syntax is <code>label>num,[label>num]*</code>.
     * Throws a <code>FormatException</code> if parsing fails.
     */
    @Override
    public Map<TypeLabel,Integer> parse(Grammar rules, String source) throws FormatException {

        // Disallow the empty string.
        if (source.equals("")) {
            throw new FormatException("The empty string is not a valid condition edge>num.");
        }

        // Allocate the result map.
        Map<TypeLabel,Integer> edgeMap = new TreeMap<>();

        // Get the type graph from the GTS.
        TypeGraph typeGraph = rules.getTypeGraph();

        // Split the source String (assumed to be a comma separated list).
        String[] units = source.split(",");

        // Parse the units one by one and add them to the result map.
        for (String unit : units) {
            String[] assignment = unit.split(">");
            if (assignment.length != 2) {
                throw new FormatException("'" + unit + "' is not a valid condition edge>num.");
            }
            edgeMap.put(parseLabel(typeGraph, assignment[0]), parseBound(assignment[1]));
        }

        // Return the result map.
        return edgeMap;
    }
}
