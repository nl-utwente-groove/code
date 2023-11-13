/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.annotation;

import java.lang.reflect.AccessibleObject;
import java.util.Map;
import java.util.TreeMap;

/**
 * Auxiliary mapping for {@link Help} items
 * @author Arend Rensink
 * @version $Revision$
 */
public class HelpMap extends TreeMap<String,String> {
    /** Adds a Help item derived from an annotated source.
     * @see Help#createHelp(Class, Map)
     */
    public void add(Class<?> source, Map<String,String> tokenMap) {
        add(Help.createHelp(source, tokenMap));
    }

    /** Adds a Help item derived from an annotated source.
     * @see Help#createHelp(AccessibleObject, Map)
     */
    public void add(AccessibleObject source, Map<String,String> tokenMap) {
        add(Help.createHelp(source, tokenMap));
    }

    /** Adds a Help item to this map. */
    public void add(Help help) {
        if (help != null) {
            put(help.getItem(), help.getTip());
        }
    }
}
