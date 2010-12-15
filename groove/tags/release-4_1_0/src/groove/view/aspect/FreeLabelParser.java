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
 * $Id: FreeLabelParser.java,v 1.3 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.TypeLabel;

/**
 * Parser that turns a string into a default label, without (un)quoting or
 * (un)escaping.
 */
public class FreeLabelParser extends AbstractLabelParser<TypeLabel> {
    /**
     * Empty constructor with limited visibility, for creating the singleton
     * instance.
     */
    private FreeLabelParser() {
        // Empty
    }

    /** Callback method that actually creates the label. */
    @Override
    protected TypeLabel createLabel(String text) {
        return TypeLabel.createLabel(text);
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static FreeLabelParser getInstance() {
        return instance;
    }

    /** Singleton instance of this class. */
    static private final FreeLabelParser instance = new FreeLabelParser();
}
