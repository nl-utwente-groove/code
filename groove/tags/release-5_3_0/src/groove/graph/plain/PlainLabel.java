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
package groove.graph.plain;

import groove.graph.TextLabel;

/**
 * Simple textual label, for use in {@link PlainGraph}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class PlainLabel extends TextLabel {
    /**
     * Constructor for a label with given text.
     */
    private PlainLabel(String text) {
        super(text);
    }

    @Override
    public String toParsableString() {
        return text();
    }

    /**
     * Constructs a plain label with a given text.
     */
    public static PlainLabel parseLabel(String text) {
        return new PlainLabel(text);
    }
}
