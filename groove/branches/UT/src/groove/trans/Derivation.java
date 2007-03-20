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
 * $Id: Derivation.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.Morphism;

/**
 * Interface for a direct derivation.
 * Only the derivation rule is initialized at construction time; all other data
 * are provided during <i>application</i>.
 * In addition, the interface provides the functionality to <i>minimize</i> an
 * interface in the form of a <i>footprint</i>. This is an array of elements
 * (typically from the source or target graph) that, together with the source
 * graph, allow to reconstruct the derivation up to node and edge set equality.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface Derivation {
    /**
     * Returns the transformation morphism underlying this derivation.
     */
    public Morphism getMorphism();

    /** 
     * Returns the source graph of this derivation.
     */
    public Graph getSource();

    /** 
     * Returns the target graph of this derivation.
     */
    public Graph getTarget();

    /** 
     * Returns the production rule on which this derivation is based.
     * @ensure result != null
     */
    public Rule getRule();

    /**
     * Returns the matching of the rule's LHS in the source graph.
     */
    public Matching getMatching();
    
//    /**
//     * Returns a footprint for this derivation.
//     * From the footprint, together with the producation rule and source graph,
//     * the derivation can be reconstructed up to node and edge set equality of
//     * the target. 
//     */
//    public Element[] getFootprint();
//    
//    /**
//     * Applies the derivation for a given matching.
//     * This sets the match, source, morphism and target.
//     * It is an error to invoke this or {@link #apply(Graph, Element[], GraphChangeCommand)}
//     * more than once.
//     * @see #apply(Graph, Element[], GraphChangeCommand)
//     */
//    public void apply(Matching match);
//    
//    /**
//     * Reconstructs the derivation from a given source graph and footprint.
//     * The target structure is provided in the form of an interface to add and
//     * remove elements.
//     * Does not set the target graph as returned in {@link #getTarget()}.
//     * It is an error to invoke this or {@link #apply(Matching)}
//     * more than once.
//     * @see #apply(Matching)
//     */
//    public void apply(Graph source, Element[] footprint, GraphChangeCommand target);
}