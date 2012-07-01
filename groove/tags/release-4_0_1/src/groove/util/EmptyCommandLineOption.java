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
 * A <code>EmptyCommandLineOption</code> is a <code>CommandLineOption</code>
 * that does not expect a parameter. It can be generated by giving a name and
 * a one-line description.
 * 
 * @see CommandLineOption
 * @author Maarten de Mol
 */
public class EmptyCommandLineOption implements CommandLineOption {

    // Local store of the name and the description.
    private final String name;
    private final String description;

    /**
     * Constructor. Only task is to locally store the given name and the
     * given one-line description.
     */
    public EmptyCommandLineOption(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public String[] getDescription() {
        String[] result = new String[1];
        result[0] = this.description;
        return result;
    }

    @Override
    public boolean hasParameter() {
        return false;
    }

    @Override
    public void parse(String parameter) throws IllegalArgumentException {
        // This command line option does not have a parameter, so it does not
        // need to be parsed.
    }
}