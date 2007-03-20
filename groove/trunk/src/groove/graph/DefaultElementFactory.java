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
 * Element factory that creates default nodes, labels and edges.
 * @author Arend Rensink
 * @version $REVISION $
 */
public class DefaultElementFactory implements ElementFactory {
	public UnaryEdge newEdge(Node source, String text) {
		return newEdge(source, newLabel(text));
	}

	public UnaryEdge newEdge(Node source, Label label) {
		return new DefaultFlag(source, label);
	}

	public BinaryEdge newEdge(Node source, String text, Node target) {
		return DefaultEdge.createEdge(source, text, target);
	}

	public BinaryEdge newEdge(Node source, Label label, Node target) {
		return DefaultEdge.createEdge(source, label, target);
	}

	public Edge newEdge(Node[] ends, String text) {
		return newEdge(ends, newLabel(text));
	}

	/** 
	 * This implementation throws an {@link IllegalArgumentException} 
	 * of the arity exceeds 2.
	 */
	public Edge newEdge(Node[] ends, Label label) {
		switch (ends.length) {
		case 1: return newEdge(ends[Edge.SOURCE_INDEX], label);
		case 2: return newEdge(ends[Edge.SOURCE_INDEX], label, ends[Edge.TARGET_INDEX]);
		default: throw new IllegalArgumentException("Factory does not supports edges of arity "+ends.length);
		}
	}

	public DefaultLabel newLabel(String text) {
		return DefaultLabel.createLabel(text);
	}

	public DefaultNode newNode() {
		return new DefaultNode();
	}
}
