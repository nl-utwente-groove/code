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
package groove.grammar.groovy;

/** GROOVY-related utilities. */
public class Util {
    /** Tests if the GROOVY library is present. */
    public static boolean isGroovyPresent() {
        return groovyPresent;
    }

    private final static boolean groovyPresent;

    static {
        boolean present = false;
        try {
            Util.class.getClassLoader().loadClass("groovy.lang.GroovyShell");
            present = true;
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        groovyPresent = present;
    }
}
