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
 * Created on 4 Nov 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package groove.graph;

/**
 * Factory interface for graph elements.
 * @author Arend Rensink
 * @version $REVISION $
 */
public interface ElementFactory {
	/** 
	 * Creates or looks up a label of the type of this factory, for a given label text.
	 * @param text the label text
	 * @return a label such that <code>result.text().equals(text)</code> 
	 */
	public Label newLabel(String text);
	/**
	 * Creates a fresh node of the type of this factory.
	 */ 
	public Node newNode();
	/** 
	 * Creates a unary edge of the type of this factory, with a given source node and label text.
	 * Convenience method for <code>newEdge(source, newLabel(text))</code>.
	 * @param source the source node of the new edge
	 * @param text the label text for the new edge
	 * @return an edge such that <code>result.source().equals(source)</code> and
	 * <code>result.label().text().equals(text)</code> 
	 * @see #newEdge(Node, Label) 
	 */
	public Edge newEdge(Node source, String text);
	/**
	 * Creates a unary edge of the type of this factory, with a given source and label. 
	 * @param source the source node of the new edge
	 * @param label the label of the new edge
	 * @return an edge such that <code>result.source().equals(source)</code> and
	 * <code>result.label().equals(label)</code> 
	 */
	public Edge newEdge(Node source, Label label);
	/** 
	 * Creates a binary edge of the type of this factory, with a given source node and label text.
	 * Convenience method for <code>newEdge(source, newLabel(text), target)</code>. 
	 * @param source the source node of the new edge
	 * @param text the label text for the new edge
	 * @param target the target node of the new edge
	 * @return an edge such that <code>result.source().equals(source)</code>, 
	 * <code>result.label().text().equals(text)</code> and
	 * <code>result.target().equals(target)</code>
	 * @see #newEdge(Node, Label, Node) 
	 */
	public Edge newEdge(Node source, String text, Node target);
	/** 
	 * Creates a binary edge of the type of this factory, with a given source and label. 
	 * @param source the source node of the new edge
	 * @param label the label of the new edge
	 * @param target the target node of the new edge
	 * @return an edge such that <code>result.source().equals(source)</code>, 
	 * <code>result.label().equals(label)</code> and
	 * <code>result.target().equals(target)</code>
	 */
	public Edge newEdge(Node source, Label label, Node target);
	/** 
	 * Creates an arbitrary edge of the type of this factory, 
	 * for given end nodes and label text.
	 * Convenience method for <code>newEdge(ends, newLabel(text))</code>. 
	 * @param ends the end nodes of the new edge
	 * @param text the label text for the new edge
	 * @return an edge such that <code>result.ends()</code> equals <code>ends</code> and 
	 * <code>result.label().text().equals(text)</code>
	 * @see #newEdge(Node[], Label) 
	 * @throws IllegalArgumentException if edges of arity <code>ends.length</code>
	 * are not supported 
	 */
	public Edge newEdge(Node[] ends, String text);
	/** 
	 * Creates an arbitrary edge of the type of this factory, 
	 * for given end nodes and label.
	 * Invokes {@link #newEdge(Node, Label)} or {@link #newEdge(Node, Label, Node)}
	 * in case the edge is to be unary or binary.
	 * @param ends the end nodes of the new edge
	 * @param label the label of the new edge
	 * @return an edge such that <code>result.ends()</code> equals <code>ends</code> and 
	 * <code>result.label().equals(label)</code>
	 * @throws IllegalArgumentException if edges of arity <code>ends.length</code>
	 * are not supported 
	 */
	public Edge newEdge(Node[] ends, Label label);
}
