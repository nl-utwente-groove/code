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
 * $Id: ComposedLabelParser.java,v 1.4 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import java.util.Collection;
import java.util.LinkedHashSet;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/**
 * Label parser consisting of a collection of parser, which are consecutively applied.
 * Parsing only succeeds if all parsers agree on the result.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
@Deprecated
public class ComposedLabelParser implements LabelParser {
    /** Constructs a new parser from a given collection of parsers. */
    public ComposedLabelParser(final Collection<LabelParser> parsers) {
        this.parsers = new LinkedHashSet<LabelParser>(parsers);
    }

    /**
     * Iterates over the stored parsers, and compares their results for the label text.
     * Throws an exception if the parsers do not agree on the result.
     */
    public Label parse(DefaultLabel label) throws FormatException {
        Label result = null;
        for (LabelParser parser : parsers) {
            Label newLabel = parser.parse(label);
            if (result == null) {
                result = newLabel;
            } else if (!result.equals(newLabel)) {
                throw new FormatException("label '%s' cannot be parsed unambiguously", label);
            }
        }
        return result;
    }

    /**
     * Tries each of the stored unparsers, and returns the result if all
     * stored parsers agree that it is parsed back to the original label.
     */
    public DefaultLabel unparse(Label label) {
        for (LabelParser parser : parsers) {
            DefaultLabel result = parser.unparse(label);
            try {
                if (parse(result).equals(label)) {
                    return result;
                }
            } catch (FormatException e) {
                // go on to try the next unparser
            }
        }
        // no success
        return null;
    }

    /** The sub-parsers of which this parser is composed. */
    private final Collection<LabelParser> parsers;
}
