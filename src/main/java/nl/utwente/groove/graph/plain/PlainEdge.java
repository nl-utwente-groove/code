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

    /**
     * Creates an default edge from a given source node, label text and target
     * node. To save space, a set of standard instances is kept internally, and
     * consulted to return the same object whenever an edge is requested with
     * the same end nodes and label text.
     * @param source the source node of the new edge; should not be
     *        <code>null</code>
     * @param text the text of the new edge; should not be <code>null</code>
     * @param target the target node of the new edge; should not be
     *        <code>null</code>
     * @return an edge based on <code>source</code>, <code>text</code> and
     *         <code>target</code>; the label is a {@link PlainLabel}
     * @see #createEdge(PlainNode, PlainLabel, PlainNode)
     */
    static public PlainEdge createEdge(PlainNode source, String text, PlainNode target) {
        return PlainFactory.instance().createEdge(source, text, target);
    }

    /** Default method that uses the DefaultEdge constructor. */
    static public PlainEdge createEdge(PlainNode source, PlainLabel label, PlainNode target) {
        return PlainFactory.instance().createEdge(source, label, target);
    }

}
