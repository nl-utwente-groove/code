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
package nl.utwente.groove.grammar.model;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.model.GraphBasedModel.ModelMap;

/** Mapping from aspect graph to host graph. */
public class HostModelMap extends ModelMap<@NonNull HostNode,@NonNull HostEdge> {
    /**
     * Creates a new, empty map.
     */
    public HostModelMap(HostFactory factory) {
        super(factory);
    }
}