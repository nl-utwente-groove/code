/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: AspectParser.java,v 1.16 2008-03-13 14:41:55 rensink Exp $
 */
package groove.view.aspect;

import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.util.Pair;
import groove.view.FormatException;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision: 2929 $
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
        AspectLabel result = new AspectLabel(role);
        try {
            parse(text, result);
        } catch (FormatException exc) {
            result.addError("%s in '%s'", exc.getMessage(), text);
        }
        result.setFixed();
        return result;
    }

    /**
     * Recursively parses a string into an aspect label passed in as a parameter.
     * @param text the text to be parsed
     * @param result the aspect label to receive the result
     * @throws FormatException if there were parse errors in {@code text}
     */
    private void parse(String text, AspectLabel result) throws FormatException {
        int nextSeparator;
        boolean stopParsing = false;
        while (!stopParsing && (nextSeparator = text.indexOf(SEPARATOR)) >= 0) {
            // find the prefixing sequence of letters
            StringBuilder prefixBuilder = new StringBuilder();
            int pos;
            char c;
            for (pos = 0; Character.isLetter(c = text.charAt(pos)); pos++) {
                prefixBuilder.append(c);
            }
            String prefix = prefixBuilder.toString();
            stopParsing =
                EdgeRole.getRole(prefix) != null && pos == nextSeparator;
            if (!stopParsing) {
                AspectKind kind = AspectKind.getKind(prefix);
                if (kind == null) {
                    throw new FormatException(
                        "Can't parse prefix '%s' (precede with ':' to use literal text)",
                        text.substring(0, nextSeparator));
                }
                Pair<Aspect,String> parseResult = kind.parseAspect(text);
                Aspect aspect = parseResult.one();
                result.addAspect(aspect);
                text = parseResult.two();
                stopParsing = aspect.getKind().isLast();
            }
        }
        // special case: we will treat labels of the form type:prim 
        // (with prim a primitive type) as prim:
        String typePrefix = EdgeRole.NODE_TYPE.getPrefix();
        if (text.startsWith(typePrefix)) {
            Aspect primType =
                Aspect.getAspect(text.substring(typePrefix.length()));
            if (primType != null && primType.getKind().isData()) {
                result.addAspect(primType);
                text = "";
            }
        }
        result.setInnerText(text);
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
}
