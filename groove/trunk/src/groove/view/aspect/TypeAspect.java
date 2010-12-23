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
 * $Id$
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Graph aspect dealing with node types.
 * @author Arend
 * @version $Revision $
 */
public class TypeAspect extends AbstractAspect {
    /** Private constructor to create the singleton instance. */
    private TypeAspect() {
        super(TYPE_ASPECT_NAME);
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static TypeAspect getInstance() {
        return instance;
    }

    /**
     * The singleton instance of this class.
     */
    static private final TypeAspect instance = new TypeAspect();
    /** Name of this aspect. */
    static public final String TYPE_ASPECT_NAME = "type";
    //    /** The node type aspect value. */
    //    static public final AspectValue NODE_TYPE;
    //    /** Name of the flag aspect value. */
    //    static public final AspectValue FLAG;
    /** Name of the path aspect value. */
    static public final String PATH_NAME = "path";
    /** The path aspect value. */
    static public final AspectValue PATH;
    /** Name of the abstract aspect value. */
    static public final String ABS_NAME = "abs";
    /** The abstract aspect value. */
    static public final AspectValue ABS;
    /** Name of the subtype aspect value. */
    static public final String SUB_NAME = "sub";
    /** The subtype aspect value. */
    static public final AspectValue SUB;
    /** Name of the empty aspect value. */
    static public final String EMPTY_NAME = "";
    /** The empty aspect value. */
    static public final AspectValue EMPTY;

    static {
        try {
            PATH = instance.addEdgeValue(PATH_NAME);
            PATH.setLast(true);
            SUB = instance.addEdgeValue(SUB_NAME);
            SUB.setLast(true);
            ABS = instance.addEdgeValue(ABS_NAME);
            EMPTY = instance.addEdgeValue(EMPTY_NAME);
            EMPTY.setLast(true);
        } catch (FormatException exc) {
            throw new Error("Aspect '" + TYPE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }
}
