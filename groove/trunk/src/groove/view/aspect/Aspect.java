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
 * $Id: Aspect.java,v 1.11 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import java.util.Set;

/**
 * Interface for an aspect of graphs. Examples of aspects are: the roles in a
 * rule, typing information, or graph condition information.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class Aspect {
    /**
     * Returns the set of all possible aspect values (for either nodes or
     * edges), as a set of <code>AspectValue</code>s.
     * @see #getNodeValues()
     * @see #getEdgeValues()
     */
    abstract Set<AspectValue> getValues();

    /**
     * Returns the possible node aspect values, as a set of
     * <code>AspectValue</code>s.
     * @see #getValues()
     */
    abstract Set<AspectValue> getNodeValues();

    /**
     * Returns the possible edge aspect values, as a set of
     * <code>AspectValue</code>s.
     * @see #getValues()
     */
    abstract Set<AspectValue> getEdgeValues();

    /** Returns the array of all known aspects. */
    static public Aspect[] getAllAspects() {
        if (allAspects == null) {
            allAspects =
                new Aspect[] {AttributeAspect.getInstance(),
                    RuleAspect.getInstance(), NestingAspect.getInstance(),
                    ParameterAspect.getInstance(), TypeAspect.getInstance()};
        }
        return allAspects;
    }

    /**
     * String used to separate the textual representation of aspect values in a
     * label. When the separator occurs twice in direct succession, this denotes
     * the end of the aspect prefix.
     */
    public static final char VALUE_SEPARATOR = ':';
    /**
     * String used to separate the name and content of aspect values.
     */
    public static final String CONTENT_ASSIGN = "=";
    /**
     * String used to separate substrings within the content of an aspect value.
     */
    public static final String CONTENT_SEPARATOR = "/";
    /**
     * Array of all known aspects.
     */
    private static Aspect[] allAspects;
}
