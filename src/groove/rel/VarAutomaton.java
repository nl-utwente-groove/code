// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: VarAutomaton.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $
 */
package groove.rel;

import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Map;
import java.util.Set;

/**
 * Extends the automation interface with support for variables.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface VarAutomaton extends Automaton, VarSetSupport {
    /**
     * Returns a relation consisting of pairs of nodes of a given graph between
     * which there is a path matching this automaton. If this automaton has variables,
     * the pairs are edges with {@link VarNodeEdgeMap}
     * labels giving a valuation of the variables.
     * @param graph the graph in which the paths are sought
     * @param startImages set of nodes in <code>graph</code> from which the
     * matching paths should start; if <code>null</code>, there is no constraint
     * @param endImages set of nodes in <code>graph</code> at which the
     * matching paths should end; if <code>null</code>, there is no constraint
     * @param valuation mapping from variables to edge labels that should be 
     * adhered to in the matching; if <code>null</code>, there is no constraint 
     * @see #hasVars()
     * @see #boundVarSet()
     */
    NodeRelation getMatches(Graph graph, Set<? extends Node> startImages, Set<? extends Node> endImages, Map<String,Label> valuation);
}