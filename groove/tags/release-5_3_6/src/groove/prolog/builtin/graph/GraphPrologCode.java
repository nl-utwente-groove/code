/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.builtin.graph;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import groove.grammar.host.HostGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.GraphState;

/**
 * This class contains some utility methods to extract Java objects from prolog terms
 * 
 * @author Michiel Hendriks
 */
public abstract class GraphPrologCode implements PrologCode {

    /**
     * Get a graph from a term representing a graph
     * @param term      A term representing a graph
     * @return          A graph
     */
    public static final Graph getGraph(Term term) throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (jot.value instanceof GraphState) {
                return ((GraphState) jot.value).getGraph();
            }
            if (jot.value instanceof Graph) {
                return (Graph) jot.value;
            }
            PrologException.domainError(GraphPrologCode.GRAPH_ATOM, term);
        } else {
            PrologException.typeError(GraphPrologCode.GRAPH_ATOM, term);
        }
        return null;
    }

    /**
     * Get a host graph from a term representing a host graph
     * @param term      A term representing a host graph
     * @return          A host graph
     */
    public static final HostGraph getHostGraph(Term term)
        throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (jot.value instanceof GraphState) {
                return ((GraphState) jot.value).getGraph();
            }
            if (jot.value instanceof HostGraph) {
                return (HostGraph) jot.value;
            }
            PrologException.domainError(GraphPrologCode.GRAPH_ATOM, term);
        } else {
            PrologException.typeError(GraphPrologCode.GRAPH_ATOM, term);
        }
        return null;
    }

    /**
     * Get an edge from a term representing an edge
     * @param term      A term representing an edge
     * @return          An edge
     */
    public static final Edge getEdge(Term term) throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (!(jot.value instanceof Edge)) {
                PrologException.domainError(GraphPrologCode.EDGE_ATOM, term);
            }
            return (Edge) jot.value;
        } else {
            PrologException.typeError(GraphPrologCode.EDGE_ATOM, term);
        }
        return null;
    }

    /**
     * Get a node from a term representing a node
     * @param term      A term representing a node
     * @return          A node
     */
    public static final Node getNode(Term term) throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (!(jot.value instanceof Node)) {
                PrologException.domainError(GraphPrologCode.NODE_ATOM, term);
            }
            return (Node) jot.value;
        } else {
            PrologException.typeError(GraphPrologCode.NODE_ATOM, term);
        }
        return null;
    }

    /**
     * Returns true if the input contains the option with the given values
     */
    public static final boolean hasOption(Interpreter interpreter, Term input,
            CompoundTermTag opt, Term[] values) throws PrologException {
        if (!(input instanceof CompoundTerm)) {
            return false;
        }
        CompoundTerm ct = (CompoundTerm) input;
        while (ct != null) {
            CompoundTerm entry = null;
            if (CompoundTerm.isListPair(ct)) {
                if (ct.args[0] instanceof CompoundTerm) {
                    entry = (CompoundTerm) ct.args[0];
                }
                if (ct.args[1] instanceof CompoundTerm) {
                    ct = (CompoundTerm) ct.args[1];
                } else {
                    // end of list?
                    break;
                }
                if (entry == null) {
                    // not option, continue
                    continue;
                }
            } else {
                entry = ct;
                ct = null;
            }
            if (entry.tag == opt) {
                for (int i = 0; i < opt.arity; i++) {
                    if (interpreter.simpleUnify(entry.args[i], values[i]) == FAIL) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Atom term "graph"
     */
    public static final AtomTerm GRAPH_ATOM = AtomTerm.get("graph");

    /**
     * Atom term "node"
     */
    public static final AtomTerm NODE_ATOM = AtomTerm.get("node");

    /**
     * Atom term "edge"
     */
    public static final AtomTerm EDGE_ATOM = AtomTerm.get("edge");

    @Override
    public void install(Environment env) {
        /**
         * Left blank by design
         */
    }

    @Override
    public void uninstall(Environment env) {
        /**
         * Left blank by design
         */
    }
}
