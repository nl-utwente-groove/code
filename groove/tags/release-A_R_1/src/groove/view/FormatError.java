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
 * $Id$
 */
package groove.view;

import groove.control.ControlView;
import groove.graph.Element;
import groove.view.aspect.AspectGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class encoding a single message reporting an error in a graph view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormatError implements Comparable<FormatError> {
    /** Constructs an error consisting of a string message. */
    public FormatError(String message) {
        this.message = message;
    }

    /**
     * Constructs an error consisting of a message to be formatted.
     * The actual message is constructed by calling {@link String#format(String, Object...)}
     * The parameters are interpreted as giving information about the error.
     * @param message
     * @param pars
     */
    public FormatError(String message, Object... pars) {
        this(String.format(message, pars));
        for (Object par : pars) {
            addContext(par);
        }
    }

    /**
     * Attempts to set a context value ({@link #graph}, {@link #control}, 
     * {@link #elements}) from a given object.
     */
    private void addContext(Object par) {
        if (par instanceof FormatError) {
            this.graph = ((FormatError) par).getGraph();
            this.elements.addAll(((FormatError) par).getElements());
        } else if (par instanceof AspectGraph) {
            this.graph = (AspectGraph) par;
        } else if (par instanceof ControlView) {
            this.control = (ControlView) par;
        } else if (par instanceof Element) {
            this.elements.add((Element) par);
        } else if (par instanceof Object[]) {
            for (Object subpar : (Object[]) par) {
                addContext(subpar);
            }
        }
    }

    /** Constructs an error from an existing error, by adding extra information. */
    public FormatError(FormatError prior, Object... pars) {
        this(prior.toString(), pars);
        this.elements.addAll(prior.getElements());
        if (this.graph == null) {
            this.graph = prior.getGraph();
        }
    }

    /** Compares the error graph, error object and message. */
    @Override
    public boolean equals(Object obj) {
        boolean result = obj instanceof FormatError;
        if (result) {
            FormatError err = (FormatError) obj;
            result =
                getGraph() == null ? err.getGraph() == null
                        : getGraph().equals(err.getGraph());
            result &=
                getControl() == null ? err.getControl() == null
                        : getControl().equals(err.getControl());
            result &=
                getElements() == null ? err.getElements() == null
                        : getElements().equals(err.getElements());
            result &= toString().equals(err.toString());
        }
        return result;
    }

    /** The hash code is based on the error graph, error object and message. */
    @Override
    public int hashCode() {
        int result = toString().hashCode();
        result += getGraph() == null ? 0 : getGraph().hashCode();
        result += getControl() == null ? 0 : getControl().hashCode();
        result += getElements() == null ? 0 : getElements().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /** 
     * Compares only the error element and message.
     * This means that identically worded errors with the same element but for different graphs will be collapsed. 
     */
    @Override
    public int compareTo(FormatError other) {
        int result = 0;
        // establish lexicographical ordering of error objects
        int upper = Math.min(this.elements.size(), other.elements.size());
        for (int i = 0; i < upper; i++) {
            result = getElements().get(i).compareTo(other.getElements().get(i));
            if (result != 0) {
                break;
            }
        }
        if (result == 0) {
            result = this.elements.size() - other.elements.size();
        }
        if (result == 0) {
            result = toString().compareTo(other.toString());
        }
        return result;
    }

    /** Returns the control view in which the error occurs. May be {@code null}. */
    public final ControlView getControl() {
        return this.control;
    }

    /** Returns the graph in which the error occurs. May be {@code null}. */
    public final AspectGraph getGraph() {
        return this.graph;
    }

    /** Returns the list of elements in which the error occurs. May be {@code null}. */
    public final List<Element> getElements() {
        return this.elements;
    }

    /** Returns a new format error that extends this one with context information. */
    public FormatError extend(Object par) {
        return new FormatError(this, par);
    }

    /** Returns a new format error in which the context information is transferred. */
    public FormatError transfer(Map<?,?> map) {
        List<Element> newElements = new ArrayList<Element>();
        for (Element errorObject : this.elements) {
            if (map.containsKey(errorObject)) {
                newElements.add((Element) map.get(errorObject));
            } else {
                newElements.add(errorObject);
            }
        }
        return new FormatError(this.toString(), newElements.toArray());
    }

    /** The control view in which the error occurs. */
    private ControlView control;
    /** The graph in which the error occurs. */
    private AspectGraph graph;
    /** The erroneous element. */
    private final List<Element> elements = new ArrayList<Element>();
    /** The error message. */
    private final String message;
}
