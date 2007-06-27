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
 * $Id: GraphJEdge.java,v 1.11 2007-06-27 16:00:22 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.util.Converter;
import groove.view.DefaultLabelParser;
import groove.view.LabelParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are stored as a Set in
 * the user object. In the latter case, toString() the user object is the empty string.
 */
public class GraphJEdge extends JEdge {
    /**
     * Constructs a model edge based on a graph edge.
     * The graph edge is required to have at least arity two; yet we cannot
     * rely on it being a {@link groove.graph.BinaryEdge}, it might be regular with some
     * pseudo-ends or it might be a {@link groove.view.aspect.AspectEdge}. 
     * @param edge the underlying graph edge of this model edge.
     * @require <tt>edge != null && edge.endCount() >= 0</tt> 
     * @ensure labels().size()==1, labels().contains(edge.label)
     *         source() == edge.source(), target() == edge.target()
     * @throws IllegalArgumentException if <code>edge.endCount() < 2</code> 
     */
    GraphJEdge(GraphJModel jModel, BinaryEdge edge) {
    	this.jModel = jModel;
        this.source = edge.end(BinaryEdge.SOURCE_INDEX);
        this.target = edge.end(BinaryEdge.TARGET_INDEX);
        getUserObject().add(edge);
    }

    /** 
     * Returns <code>true</code> if the super method does so, and the edge has
     * at least one non-filtered list label.
     */
    @Override
    public boolean isVisible() {
        return super.isVisible() && !isFiltered();
    }
    
    /** Indicates if this edge is filtered (and therefore invisible). */
    boolean isFiltered() {
        boolean result = true;
        Iterator<String> listLabelIter = getListLabels().iterator();
        while (result && listLabelIter.hasNext()) {
            result = jModel.isFiltering(listLabelIter.next());
        }
        return result;
    }

    /**
     * Returns the common source of the underlying graph edges.
     */
    public Node getSourceNode() {
        return source;
    }

    /**
     * Returns the common target of the underlying graph edges.
     */
    public Node getTargetNode() {
        return target;
    }

    /**
     * Specialises the return type.
     */
    @Override
    public GraphJVertex getSourceVertex() {
        return (GraphJVertex) super.getSourceVertex();
    }

    /**
     * Specialises the return type.
     */
    @Override
    public GraphJVertex getTargetVertex() {
        return (GraphJVertex) super.getTargetVertex();
    }

	/**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<? extends Edge> getEdges() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Returns an arbitrary edge from the set of underlying edges.
     */
    public Edge getEdge() {
        return getUserObject().iterator().next();
    }
    
    /** 
     * Returns the actual graph edge <i>modelled</i> by this j-edge.
     * For this implementation this is the same as {@link #getEdge()}.
     * @see #getEdge()
     */
    Edge getActualEdge() {
    	return getEdge();
    }
    
    public Collection<StringBuilder> getLines() {
    	List<StringBuilder> result = new ArrayList<StringBuilder>();
		for (Edge edge: getUserObject()) {
			if (! jModel.isFiltering(getListLabel(edge))) {
				result.add(getLine(edge));
			}
		}
		return result;
	}

	/** 
     * Converts an edge to a line describing that edge as part of the
     * j-edge text.
     * Callback method from {@link #getLines()}.
     * @see #getLines()
     */
	StringBuilder getLine(Edge edge) {
		return new StringBuilder(edge.label().text());
	}

	public Collection<String> getListLabels() {
		List<String> result = new ArrayList<String>();
		for (Edge edge: getUserObject()) {
			result.add(getListLabel(edge));
		}
		return result;
	}

	/** 
	 * Returns the label of the edge as to be displayed in the label list.
	 * Callback method from {@link #getListLabels()}.
	 */
	String getListLabel(Edge edge) {
		return edge.label().text();
	}
	
	public Collection<String> getPlainLabels() {
		List<String> result = new ArrayList<String>();
		for (Edge edge: getUserObject()) {
			result.add(getLabelParser().unparse(edge.label()));
		}
		return result;
	}
    
    /** 
     * Returns a label parser for this jnode.
     * The label parser is used to obtain the plain labels. 
     */
    LabelParser getLabelParser() {
        if (labelParser == null) {
            labelParser = createLabelParser();
        }
        return labelParser;
    }
    
    /** Callback factory method to create a label parser for this jnode. */
    LabelParser createLabelParser() {
        return new DefaultLabelParser();
    }

	/** Specialises the return type of the method. */
    @Override
	public EdgeContent getUserObject() {
		return (EdgeContent) super.getUserObject();
	}
   
    @Override
    EdgeContent createUserObject() {
		return new EdgeContent();
	}

	/**
     * This implementation does nothing: setting the user object directly is
     * not the right way to go about it.
     * Instead use <code>{@link #addEdge}</code> and <code>{@link #removeEdge}</code>.
     */
    @Override
    public void setUserObject(Object value) {
    	// does nothing
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if <tt>edge</tt>
     * is not compatible with this j-edge and cannot be added.
     * This implementation returns <tt>true</tt> always.
     * @require <tt>edge.source() == getSourceNode</tt> and <tt>edge.target() == getTargetNode()</tt>
     * @ensure if <tt>result</tt> then <tt>getEdgeSet().contains(edge)</tt>
     */
    public boolean addEdge(BinaryEdge edge) {
        getUserObject().add(edge);
        return true;
    }
    
    /**
     * Adds an edge to the set underlying graph edges.
     */
    public void removeEdge(Edge edge) {
        getUserObject().remove(edge);
    }

    @Override
	StringBuilder getEdgeDescription() {
    	StringBuilder result = super.getEdgeDescription();
    	String sourceIdentity = getSourceVertex().getNodeIdentity();
    	if (sourceIdentity != null) {
    		result.append(" from ");
    		result.append(Converter.ITALIC_TAG.on(sourceIdentity));
    	}
    	String targetIdentity = getTargetVertex().getNodeIdentity();
    	if (targetIdentity != null) {
    		result.append(" to ");
    		result.append(Converter.ITALIC_TAG.on(targetIdentity));
    	}
    	return result;
	}
    
    /** This implementation recognises argument and operation edges. */
	@Override
	StringBuilder getEdgeKindDescription() {
		if (getActualEdge() instanceof AlgebraEdge) {
			return new StringBuilder("Argument edge");
		} else if (getActualEdge() instanceof ProductEdge) {
			return new StringBuilder("Operation edge");
		} else {
			return new StringBuilder("Edge");
		}
	}

    /** The label parser for this edge, used to get plain labels. */
    private LabelParser labelParser;
    /** Underlying {@link JModel} of this edge. */
	private final GraphJModel jModel;
	/** Source node of the underlying graph edges. */
    private final Node source;
    /** Target node of the underlying graph edges. */
    private final Node target;
}