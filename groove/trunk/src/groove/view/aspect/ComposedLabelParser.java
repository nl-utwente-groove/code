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
 * $Id$
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.view.FormatException;

/**
 * Constructs a new label parser by combining existing ones.
 * @author Arend
 * @version $Revision $
 */
public class ComposedLabelParser implements LabelParser {
    /**
     * Constructs a label parser by combining a number of existing parsers.
     * @param parsers the parsers that should be composed; should not be empty
     */
    public ComposedLabelParser(LabelParser... parsers) {
        if (parsers.length == 0) {
            throw new IllegalArgumentException("Call with at least one parser");
        }
        this.parsers = parsers;
    }

    @Override
    public Label parse(Label label) throws FormatException {
        Label result = label;
        for (LabelParser parser : this.parsers) {
            result = parser.parse(result);
        }
        return result;
    }

    @Override
    public DefaultLabel unparse(Label label) {
        DefaultLabel result = null;
        for (LabelParser parser : this.parsers) {
            result = parser.unparse(result == null ? label : result);
        }
        return result;
    }

    private final LabelParser[] parsers;
}
