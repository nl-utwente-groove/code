/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.explore.config;

import groove.util.parse.NullParser;
import groove.util.parse.Parser;

/**
 * Exploration content that can only have a {@code null} instance.
 */
public class Null {
    /**
     * Private constructor that is never invoked
     */
    private Null() {
        assert false;
    }

    /** Parser for {@link Null}. */
    public static Parser<Null> PARSER = NullParser.instance(Null.class);
}
