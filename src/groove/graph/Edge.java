/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: Edge.java,v 1.7 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

/**
 * Interface of a graph (hyper-)edge, with two endpoints (i.e., nodes) and label.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Edge<N extends Node> extends Element {
    /**
     * Returns the source node of this edge.
     */
    public N source();

    /**
     * Returns the target node of this edge.
     */
    public N target();

    /**
     * Returns the label of this edge. The label can never be <tt>null</tt>.
     * @return the label of this edge
     * @ensure <tt>result != null</tt>
     */
    public Label label();

    /**
     * Indicates if the edge label stands for a node type. Convenience
     * method for {@code label().isNodeType()}.
     * @see Label#isNodeType()
     */
    boolean isNodeType();

    /**
     * Indicates if the edge label stands for a flag. Convenience method for
     * {@code label().isNodeType()}.
     * @see Label#isFlag()
     */
    boolean isFlag();

    /**
     * Indicates if this is a (normal) binary edge. Convenience method for {@code
     * label().isNodeType()}.
     * @see Label#isBinary()
     */
    boolean isBinary();

}