/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.aspect;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Pair;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectParser {
    /** Creates an aspect parser for a particular graph role. */
    private AspectParser() {
        // empty
    }

    /**
     * Converts a plain label to an aspect label.
     * @param text the plain label text to start from
     * @param role the graph role for which we are parsing
     * @return an aspect label, in which the aspect prefixes of {@code label}
     * have been parsed into aspect values.
     */
    public AspectLabel parse(String text, GraphRole role) {
        assert role.inGrammar();
        var result = parseMap.get(role).get(text);
        if (result == null) {
            result = new AspectLabel(role);
            parse(text, result);
            result.setFixed();
            parseMap.get(role).put(text, result);
        }
        return result;
    }

    /**
     * Recursively parses a string into an aspect label passed in as a parameter.
     * @param text the text to be parsed
     * @param result the aspect label to receive the result
     */
    private void parse(String text, AspectLabel result) {
        int nextSeparator;
        String rest = text;
        var status = Status.INIT;
        while (status != Status.DONE && (nextSeparator = rest.indexOf(SEPARATOR)) >= 0) {
            // find the prefixing sequence of letters
            StringBuilder prefixBuilder = new StringBuilder();
            int pos;
            char c;
            for (pos = 0; Character.isLetter(c = rest.charAt(pos)); pos++) {
                prefixBuilder.append(c);
            }
            String prefix = prefixBuilder.toString();
            // only continue parsing for aspects if the candidate aspect
            // prefix starts with a nonempty identifier that is not an
            // edge role prefix
            if (pos == 0 && nextSeparator != 0
                || pos != 0 && EdgeRole.getRole(prefix) != null && pos == nextSeparator) {
                status = Status.DONE;
                continue;
            }
            try {
                AspectKind kind = AspectKind.getKind(prefix);
                if (kind == null) {
                    throw new FormatException(
                        "Can't parse prefix '%s' (precede with ':' to use literal text)",
                        rest.substring(0, nextSeparator));
                }
                Pair<Aspect,String> parseResult
                    = kind.parseAspect(rest, result.getGraphRole(), status);
                Aspect aspect = parseResult.one();
                result.addAspect(aspect);
                rest = parseResult.two();
                status = kind.newStatus(status);
            } catch (FormatException exc) {
                result.addError("%s in '%s'", exc.getMessage(), text);
                status = Status.DONE;
            }
        }
        // special case: we will treat labels of the form type:prim
        // (with prim a primitive type) as prim:
        String typePrefix = EdgeRole.NODE_TYPE.getPrefix();
        if (rest.startsWith(typePrefix)) {
            String sort = rest.substring(typePrefix.length());
            if (Sort.getSort(sort) != null) {
                result.addAspect(Aspect.getAspect(sort));
                rest = "";
            }
        }
        result.setInnerText(rest);
    }

    /** Separator between aspect name and associated content. */
    static public final char ASSIGN = '=';

    /** Separator between aspect prefix and main label text. */
    static public final char SEPARATOR = ':';

    /** Returns the singleton instance of this class. */
    public static AspectParser getInstance() {
        return instance;
    }

    static private final AspectParser instance = new AspectParser();

    static private final EnumMap<GraphRole,Map<String,AspectLabel>> parseMap;
    static {
        parseMap = new EnumMap<>(GraphRole.class);
        for (var role : GraphRole.values()) {
            parseMap.put(role, new HashMap<>());
        }
        UserSignature.addUser(() -> parseMap.values().forEach(Map::clear));
    }

    /** Status of the parsing process. */
    static enum Status {
        /** Initial state. */
        INIT,
        /** After entering a role aspect. */
        ROLE,
        /** After parsing a final aspect kind. */
        DONE,
    }
}
