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
package groove.graph.aspect;

import java.util.Collection;

import groove.graph.Element;

/**
 * Extension of the {@link Element} interface with support for {@link Aspect}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface AspectElement extends Element {
    /**
     * Returns, for a given aspect, the value associated with this element.
     * This can be a declared value, an inferred value, or the aspect's 
     * default value.
     * @param aspect the aspect for which we want to know the value
     * @return the aspect value this element has for <code>aspect</code>, or 
     * <code>aspect.getDefaultValue()</code> if there is no explicit value
     * 
     */
    AspectValue getValue(Aspect aspect);

    /**
     * Returns the list of all explicitly declared aspect values.
     */
    Collection<AspectValue> getDeclaredValues();
    
    /**
     * Returns a mapping from aspects to corresponding aspect values.
     */
    AspectMap getAspectMap();
}
