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
 * $Id$
 */
package groove.explore.encode;

import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.util.parse.FormatException;

/**
 * <!=========================================================================>
 * An EncodedType<A,B> describes functionality for encoding values of type
 * A by (serialized) values of type B. 
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public interface EncodedType<A,B> {

    /**
     * Creates a user-friendly editor in which the user can select values of
     * type B that represent values of type A.
     * The created panel must be suitable to be placed in the info panel of
     * the ExplorationDialog.
     * 
     * @param grammar - global environment
     */
    public EncodedTypeEditor<A,B> createEditor(GrammarModel grammar);

    /**
     * Create a value of type A out of a value of type B.
     * Throws a FormatException if the parsing fails.
     * 
     * @param rules - reference to the GTS
     * @param source - the input value of type B
     */
    public A parse(Grammar rules, B source) throws FormatException;

}