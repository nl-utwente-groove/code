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
package groove.util;

/**
 * A <code>StoreCommandLineOption</code> is a <code>CommandLineOption</code>
 * that has an argument that is parsed into a locally stored value. This value
 * can be retrieved later.
 * 
 * @see CommandLineOption
 * @author Maarten de Mol
 */
public abstract class StoreCommandLineOption<A> implements CommandLineOption {

    // Local store of the parsed value.
    private A value;

    // Local store of the name and the parameterName.
    private final String name;
    private final String parameterName;

    /**
     * Constructor. Only stores the name and the parameterName locally.
     */
    public StoreCommandLineOption(String name, String parameterName) {
        this.name = name;
        this.parameterName = parameterName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getParameterName() {
        return this.parameterName;
    }

    /**
     * Getter for the locally stored value.
     */
    public A getValue() {
        return this.value;
    }

    @Override
    public boolean hasParameter() {
        return true;
    }

    @Override
    public void parse(String parameter) throws IllegalArgumentException {
        this.value = parseParameter(parameter);
    }

    /**
     * Method that parses a <code>String</code> parameter into a value. May
     * throw a <code>IllegalArgumentException</code> if the parameter does not
     * have the required format.
     */
    public abstract A parseParameter(String parameter);
}
