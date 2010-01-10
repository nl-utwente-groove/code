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
 * $Id: LabelParser.java,v 1.4 2008-01-30 09:33:26 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.view.FormatException;

/** Interface for parsers from preliminary labels to real labels. */
public interface LabelParser {
    /**
     * Method turning a default label into a possibly structured label.
     * @param text the default label to be parsed
     * @return the label constructed from <code>label</code>
     * @throws FormatException if <code>label</code> is not correctly formatted
     *         according to this parser.
     */
    Label parse(String text) throws FormatException;

    /**
     * Method turning a structured label into a default label from which the
     * original label can be reconstructed by this parser, or <code>null</code>
     * if no such default label can be found.
     */
    DefaultLabel unparse(Label label);
}