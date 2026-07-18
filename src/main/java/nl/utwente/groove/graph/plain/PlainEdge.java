/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.plain;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.ANumberedEdge;

/**
 * Default implementation of an (immutable) graph edge, as a triple consisting
 * of source and target nodes and an arbitrary label.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-12 15:15:31 $
 */
@NonNullByDefault
public class PlainEdge extends ANumberedEdge<PlainNode,PlainLabel> {
    /**
     * Constructs a new edge on the basis of a given source, label and target.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     */
    PlainEdge(PlainNode source, PlainLabel label, PlainNode target, int nr) {
        super(source, label, target, nr);
    }

}
