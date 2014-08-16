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
package groove.io.graph;

import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.plain.PlainLabel;
import groove.util.DefaultDispenser;
import groove.util.Dispenser;

/**
 * Factory for elements of {@link AttrGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttrFactory extends ElementFactory<AttrNode,AttrEdge> {
    /** Private constructor for the singleton instance. */
    private AttrFactory() {
        // empty
    }

    @Override
    protected AttrNode newNode(int nr) {
        return new AttrNode(nr);
    }

    @Override
    public Label createLabel(String text) {
        return PlainLabel.parseLabel(text);
    }

    @Override
    public AttrEdge createEdge(AttrNode source, Label label, AttrNode target) {
        return new AttrEdge(source, (PlainLabel) label, target, this.edgeNrDispenser.getNext());
    }

    @Override
    public Morphism<AttrNode,AttrEdge> createMorphism() {
        throw new UnsupportedOperationException();
    }

    private final Dispenser edgeNrDispenser = new DefaultDispenser();

    /** Returns the singleton instance of this class. */
    public static AttrFactory instance() {
        return instance;
    }

    private static AttrFactory instance = new AttrFactory();
}