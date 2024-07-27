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

import java.util.HashSet;
import java.util.Set;

import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSFragment;
import nl.utwente.groove.lts.GraphState;

/**
 * Class that exports an LTS to a control program that enforces precisely the transitions in that LTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTS2ControlExporter extends AbstractExporter.Writer {
    /**
     * Constructor for the singleton instance.
     */
    private LTS2ControlExporter() {
        super(ExportKind.GRAPH);
        register(FileType.CONTROL);
    }

    @Override
    public boolean exports(Exportable exportable) {
        var graph = exportable.graph();
        return super.exports(exportable) && (graph instanceof GTS || graph instanceof GTSFragment);
    }

    @Override
    protected void initialise(Exportable exportable, FileType fileType) throws PortException {
        var graph = exportable.graph();
        if (graph instanceof GTS gts) {
            this.gts = gts.toFragment(true, false);
        } else if (graph instanceof GTSFragment fragment) {
            this.gts = fragment;
        } else {
            throw new PortException("Cannot export %s to %s: not an LTS", exportable.qualName(),
                fileType);
        }
    }

    private GTSFragment gts;

    @Override
    protected void execute() throws PortException {
        this.covered.clear();
        var start = this.gts.startState();
        this.covered.add(start);
        emit(start);
    }

    /** Recursively emits the properties that hold in this states, followed by the choice of outgoing transitions. */
    private void emit(GraphState state) {
        assert this.covered.contains(state);
        emit("// state " + state);
        state
            .getTransitions()
            .stream()
            .filter(t -> t.getRole() == EdgeRole.FLAG)
            .filter(t -> !t.label().getAction().getRole().isConstraint())
            .forEach(t -> emit(t.label() + ";"));
        var outs = state
            .getTransitions()
            .stream()
            .filter(t -> this.gts.edgeSet().contains(t))
            .filter(t -> this.covered.add(t.target()))
            .toList();
        if (outs.isEmpty()) {
            emit("// final state");
        } else if (outs.size() == 1 && !state.isFinal()) {
            var out = outs.get(0);
            emit(out.label() + ";");
            emit(out.target());
        } else {
            boolean first = true;
            for (var out : outs) {
                if (first) {
                    emit("choice {");
                    first = false;
                } else {
                    emit("} or {");
                }
                increaseIndent();
                emit(out.label() + ";");
                emit(out.target());
                decreaseIndent();
            }
            if (state.isFinal()) {
                emit("} or { // final state");
            }
            emit("}");
        }
    }

    /** The set of currently covered states. */
    private final Set<GraphState> covered = new HashSet<>();

    /** Returns the singleton instance of this class. */
    static public LTS2ControlExporter instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    static private LTS2ControlExporter INSTANCE = new LTS2ControlExporter();
}
