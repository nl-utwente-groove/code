/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: ControlTransition.java,v 1.8 2007-11-26 08:58:11 fladder Exp $
 */
package groove.control;

import groove.graph.BinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.Node;

/**
 * @author Staijen
 * 
 * Represents a transition in a control automaton, which is unique by its source, target and associated Rule.
 * This is a DefaultEdge to be able to visualize as GraphShape, and a LocationTransition to use for explocation.
 * 
 */
public class ControlTransition implements BinaryEdge, LocationTransition {
	
	private ControlState source;
	private ControlState target;
	private String text;
	
	private ControlTransition visibleParent;
	
	/**
	 * Creates a labelled controltransition between two controlstates
	 * @param source
	 * @param target
	 * @param label is the Rule associated with this transition
	 */
	public ControlTransition(ControlState source, ControlState target, String label)
	{
		this.source = source;
		this.target = target;
		this.text = label;
	}
	
	/** Returns the text on the label */
	public String getText() {
		return this.text;
	}
	
	public Label label() {
		return DefaultLabel.createLabel(this.getText());
	}
	
//	/**
//	 * @return priority of this transition, which equals the priority of the associated rule
//	 */
//	public int getPriority()
//	{
//		return this.rule.getPriority();
//	}
	
	public ControlState source() {
		// TODO Auto-generated method stub
		return source;
	}

	public ControlState target() {
		// TODO Auto-generated method stub
		return target;
	}
	
	public Node end(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int endCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int endIndex(Node node) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node[] ends() {
		// TODO Auto-generated method stub
		return new Node[]{target()};
	}

	public boolean hasEnd(Node node) {
		// TODO Auto-generated method stub
		return false;
	}
//
//	@Deprecated
//	public Edge imageFor(GenericNodeEdgeMap elementMap) {
//		throw new UnsupportedOperationException("Transition images are currenty not supported");
//	}
	
	public Node opposite() {
		return target();
	}

	public int compareTo(Element obj) {
		if (obj instanceof ControlState) {
            // for states, we just need to look at the source of this transition
            if (source().equals(obj)) {
                return +1;
            } else {
                return source().compareTo(obj);
            }
        } else {
            Edge other = (Edge) obj;
            if (!source().equals(other.source())) {
                return source().compareTo(other.source());
            }
            // for other edges, first the end count, then the label, then the other ends
            if (endCount() != other.endCount()) {
                return endCount() - other.endCount();
            }
            if (!label().equals(other.label())) {
                return label().compareTo(other.label());
            }
            for (int i = 1; i < endCount(); i++) {
                if (!end(i).equals(other.end(i))) {
                    return end(i).compareTo(other.end(i));
                }
            }
            return 0;
        }
	}
	
	/**
	 * Some acutal control transitions are not visible in the control automaton.
	 * @param parent the representing and visible parent element
	 */
	public void setVisibleParent(ControlTransition parent) {
		this.visibleParent = parent;
	}
	/**
	 * Some acutal control transitions are not visible in the control automaton.
	 * @return the representing and visible parent element
	 */	
	public ControlTransition getVisibleParent() {
		return this.visibleParent;
	}
	
	/**
	 * Modify the source. Ment to be used for merging states only.
	 * @param source
	 */
	public void setSource(ControlState source) { 
		this.source = source;
	}

	/**
	 * Modify the target. Ment to be used for merging states only.
	 * @param target
	 */
	public void setTarget(ControlState target) {
		this.target = target;
	}
	
	@Override
	public String toString() {
		return this.source + "--- " + text + " --->" + this.target; 
	}
}
