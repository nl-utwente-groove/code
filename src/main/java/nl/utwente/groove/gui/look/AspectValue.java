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
package nl.utwente.groove.gui.look;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraph;

/**
 * Visual value strategy that delegates its task to
 * specialised helper methods.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AspectValue<T> implements VisualValue<T> {
    @Override
    public <G extends @NonNull Graph> T get(JGraph<G> jGraph, JCell<G> cell) {
        if (cell instanceof AspectJVertex v) {
            return getForJVertex(v);
        }
        if (cell instanceof AspectJEdge e) {
            return getForJEdge(e);
        }
        return null;
    }

    /** Delegate method to retrieve the visual value from an {@link AspectJVertex}. */
    abstract protected T getForJVertex(AspectJVertex jVertex);

    /** Delegate method to retrieve the visual value from an {@link AspectJEdge}. */
    abstract protected T getForJEdge(AspectJEdge jEdge);
}
