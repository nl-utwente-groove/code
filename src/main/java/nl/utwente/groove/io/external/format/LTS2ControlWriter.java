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
package nl.utwente.groove.io.external.format;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.graph.GraphWriter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSFragment;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.io.FileType;

/**
 * Writer that exports an LTS to a control program that enforces precisely the transitions in that LTS.
 * Accepts a {@link GTS} (exported without its internal states and transitions)
 * or a {@link GTSFragment}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LTS2ControlWriter extends GraphWriter {
    /** Constructs a writer for a single export. */
    public LTS2ControlWriter() {
        super(FileType.CONTROL);
    }

    @Override
    public boolean accepts(Graph graph) {
        return graph instanceof GTS || graph instanceof GTSFragment;
    }

    @Override
    protected String getIndentUnit() {
        return "  ";
    }

    /** The node and edge collections are ignored: the LTS itself determines what is exported. */
    @Override
    protected void doWrite(Graph graph, Collection<? extends Node> nodes,
                           Collection<? extends Edge> edges) throws IOException {
        GTSFragment gts;
        if (graph instanceof GTS g) {
            gts = g.toFragment(true, false);
        } else if (graph instanceof GTSFragment fragment) {
            gts = fragment;
        } else {
            throw new IllegalArgumentException("Cannot export %s to a control program: not an LTS"
                .formatted(graph.getName()));
        }
        this.gts = gts;
        this.covered.clear();
        this.depth = 0;
        var start = gts.startState();
        this.covered.add(start);
        emitState(start);
    }

    /** Returns the LTS fragment set by {@link #doWrite}. */
    private GTSFragment getGts() {
        var result = this.gts;
        assert result != null : "LTS not initialised";
        return result;
    }

    /** The LTS to be exported; only set from {@link #doWrite} on. */
    private @Nullable GTSFragment gts;

    /** The current indentation depth. */
    private int depth;

    /** The set of currently covered states. */
    private final Set<GraphState> covered = new HashSet<>();

    /** Recursively emits the properties that hold in this states, followed by the choice of outgoing transitions. */
    private void emitState(GraphState state) throws IOException {
        assert this.covered.contains(state);
        var gts = getGts();
        emit(this.depth, "// state " + state);
        for (var trans : state.getTransitions()) {
            if (trans.getRole() == EdgeRole.FLAG
                && !trans.label().getAction().getRole().isConstraint()) {
                emitTransition(trans);
            }
        }
        var outs = state
            .getTransitions()
            .stream()
            .filter(t -> gts.edgeSet().contains(t))
            .filter(t -> this.covered.add(t.target()))
            .toList();
        if (outs.isEmpty()) {
            if (gts.isFinal(state)) {
                emit(this.depth, "// final state");
            } else {
                emit(this.depth, "// deadlocked state");
                emit(this.depth, "halt");
            }
        } else if (outs.size() == 1 && !gts.isFinal(state)) {
            var out = outs.get(0);
            emitTransition(out);
            emitState(out.target());
        } else {
            boolean first = true;
            for (var out : outs) {
                if (first) {
                    emit(this.depth, "choice {");
                    first = false;
                } else {
                    emit(this.depth, "} or {");
                }
                this.depth++;
                emitTransition(out);
                emitState(out.target());
                this.depth--;
            }
            if (gts.isFinal(state)) {
                emit(this.depth, "} or { // final state");
            }
            emit(this.depth, "}");
        }
    }

    /** Emits a transition label with out-parameters adjusted to don't-care. */
    private void emitTransition(GraphTransition trans) throws IOException {
        // out-parameters must be don't care
        var args = clone(trans.getArguments());
        var sig = trans.getAction().getSignature();
        for (int i = 0; i < sig.size(); i++) {
            if (sig.getPar(i).isOutOnly()) {
                args[i] = null;
            }
        }
        emit(this.depth, trans.getAction().toLabelString(args, false) + ";");
    }

    /** Clones and returns a given array. */
    static private <T> T[] clone(T[] array) {
        var type = array.getClass().getComponentType();
        assert type != null; // the class of an array always has a component type
        @SuppressWarnings("unchecked")
        var result = (T[]) Array.newInstance(type, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }
}
