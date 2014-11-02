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
package groove.grammar.model;

/**
 * Common supertype for all resources (graphs and texts) of a grammar.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Resource {
    /** Returns the name of this resource. */
    String getName();

    /** Returns a copy of this resource, with a given (new) name.
     * Returns this object if the new name equals the old.
     */
    Resource rename(String newName);

    /**
     * Returns the resource kind of this resource.
     */
    ResourceKind getKind();
}
