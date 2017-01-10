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
 * $Id: AcceptorKind.java 5811 2016-10-26 15:31:09Z rensink $
 */
package groove.explore.config;

import groove.util.parse.NullParser;
import groove.util.parse.Parser;

/**
 * Key determining the maximum size of the frontier set.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum FrontierSizeKind implements SettingKey {
    /** Final states. */
    COMPLETE("complete", null, "Complete exploration", null),
    /** States satisfying a graph condition. */
    BEAM("beam", "Size", "Limited-size (beam) exploration", Parser.natural),
    /** States satisfying a propositional formula. */
    SINGLE("single", null, "Linear exploration", null),;

    private FrontierSizeKind(String name, String contentName, String explanation,
        Parser<?> parser) {
        this.name = name;
        this.contentName = contentName;
        this.explanation = explanation;
        this.parser = parser == null ? NullParser.instance(Object.class) : parser;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getContentName() {
        return this.contentName;
    }

    private final String contentName;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<?> parser() {
        return this.parser;
    }

    private final Parser<?> parser;
}
