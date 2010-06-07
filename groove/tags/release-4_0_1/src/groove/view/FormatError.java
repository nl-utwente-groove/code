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

import java.util.Map;

/**
 * Class encoding a single message reporting an error in a graph view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormatError implements Comparable<FormatError> {
    /** Constructs an error for a given graph, erroneous element and string message. */
    public FormatError(AspectGraph graph, Element object, String message) {
        this(message);
        this.graph = graph;
        this.object = object;
    }

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
     * {@link #object}) from a given object.
     */
    private void addContext(Object par) {
        if (par instanceof FormatError) {
            this.graph = ((FormatError) par).getGraph();
            this.object = ((FormatError) par).getObject();
        } else if (par instanceof AspectGraph) {
            this.graph = (AspectGraph) par;
        } else if (par instanceof ControlView) {
            this.control = (ControlView) par;
        } else if (par instanceof Element) {
            this.object = (Element) par;
        }
    }

    /** Constructs an error from an existing error, by adding extra information. */
    public FormatError(FormatError prior, Object... pars) {
        this(prior.toString(), pars);
        if (this.object == null) {
            this.object = prior.getObject();
        }
        if (this.graph == null) {
            this.graph = prior.getGraph();
        }
    }

    /** Copies a given error while filling in the error graph. */
    public FormatError(AspectGraph graph, FormatError error) {
        this(graph, error.getObject(), error.toString());
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
                getObject() == null ? err.getObject() == null
                        : getObject().equals(err.getObject());
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
        result += getObject() == null ? 0 : getObject().hashCode();
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
        int result;
        if (getObject() == null) {
            // errors without object precede errors with
            result = other.getObject() == null ? 0 : -1;
        } else {
            result =
                other.getObject() == null ? 1 : getObject().compareTo(
                    other.getObject());
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

    /** Returns the graph element in which the error occurs. May be {@code null}. */
    public final Element getObject() {
        return this.object;
    }

    /** Returns a new format error that extends this one with context information. */
    public FormatError extend(Object par) {
        return new FormatError(this, par);
    }

    /** Returns a new format error in which the context information is transferred. */
    public FormatError transfer(Map<?,?> map) {
        Element newObject = this.object;
        if (map.containsKey(newObject)) {
            newObject = (Element) map.get(newObject);
        }
        AspectGraph newGraph = this.graph;
        if (map.containsKey(newGraph)) {
            newGraph = (AspectGraph) map.get(newGraph);
        }
        return new FormatError(this, newObject, newGraph);
    }

    /** The control view in which the error occurs. */
    private ControlView control;
    /** The graph in which the error occurs. */
    private AspectGraph graph;
    /** The erroneous element. */
    private Element object;
    /** The error message. */
    private final String message;
}
