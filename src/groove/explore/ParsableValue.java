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
package groove.explore;

import groove.explore.encode.Serialized;
import groove.grammar.model.GrammarModel;
import groove.util.Version;

/**
 * Type for parsable (enumerated) values.
 * Implementing classes are {@link AcceptorValue} and {@link StrategyValue}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface ParsableValue {
    /** Returns the identifying keyword of this value. */
    public String getKeyword();

    /** Returns the name of this value. */
    public String getName();

    /** Returns the description of this value. */
    public String getDescription();

    /** 
     * Indicates if this strategy is in development
     * (and so should not be included in release versions,
     * as indicated by {@link Version#isDevelopmentVersion()}).
     * @return {@code true} if this value is for development versions only.
     */
    public boolean isDevelopment();

    /** Converts this value to a {@link Serialized} object. */
    public Serialized toSerialized();

    /** Indicates if this is the default value of its kind for a given grammar. */
    public boolean isDefault(GrammarModel grammar);
}
