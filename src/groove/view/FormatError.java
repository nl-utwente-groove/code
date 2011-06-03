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

import groove.graph.Element;
import groove.view.aspect.AspectGraph;

import java.util.ArrayList;
import java.util.Arrays;
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
            this.subError = (FormatError) par;
            this.subError.transferTo(null, this);
        } else if (par instanceof AspectGraph) {
            this.graph = (AspectGraph) par;
        } else if (par instanceof ControlModel) {
            this.control = (ControlModel) par;
        } else if (par instanceof PrologModel) {
            this.prolog = (PrologModel) par;
        } else if (par instanceof Element) {
            this.elements.add((Element) par);
        } else if (par instanceof Integer) {
            this.numbers.add((Integer) par);
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
            result = Arrays.equals(getArguments(), err.getArguments());
            result &= toString().equals(err.toString());
        }
        return result;
    }

    /** The hash code is based on the error graph, error object and message. */
    @Override
    public int hashCode() {
        int result = toString().hashCode();
        result += Arrays.hashCode(getArguments());
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
    public final ControlModel getControl() {
        return this.control;
    }

    /** Returns the prolog view in which the error occurs. May be {@code null}. */
    public final PrologModel getProlog() {
        return this.prolog;
    }

    /** Returns the sub-error on which this one builds. May be {@code null}. */
    public final FormatError getSubError() {
        return this.subError;
    }

    /** Returns the graph in which the error occurs. May be {@code null}. */
    public final AspectGraph getGraph() {
        return this.graph;
    }

    /** Returns the list of elements in which the error occurs. May be empty. */
    public final List<Element> getElements() {
        return this.elements;
    }

    /** Returns a list of numbers associated with the error; typically,
     * line and column numbers. May be empty. */
    public final List<Integer> getNumbers() {
        return this.numbers;
    }

    /** Returns a new format error that extends this one with context information. */
    public FormatError extend(Object par) {
        return new FormatError(this, par);
    }

    /** Returns a new format error in which the context information is transferred. */
    public FormatError transfer(Map<?,?> map) {
        FormatError result = new FormatError(toString());
        transferTo(map, result);
        return result;
    }

    /**
     * Transfers the context information of this error object to
     * another, modulo a mapping.
     * @param map mapping from the context of this error to the context
     * of the result error; or {@code null} if there is no mapping
     * @param result the target of the transfer
     */
    private void transferTo(Map<?,?> map, FormatError result) {
        for (Object arg : getArguments()) {
            if (map != null && map.containsKey(arg)) {
                arg = map.get(arg);
            }
            result.addContext(arg);
        }
    }

    /** Returns the relevant contextual arguments of this error. */
    private Object[] getArguments() {
        List<Object> newArguments = new ArrayList<Object>();
        newArguments.addAll(this.elements);
        if (this.control != null) {
            newArguments.add(this.control);
        }
        if (this.prolog != null) {
            newArguments.add(this.prolog);
        }
        newArguments.addAll(this.numbers);
        if (this.subError != null) {
            newArguments.addAll(Arrays.asList(this.subError.getArguments()));
        }
        return newArguments.toArray();
    }

    /** The prolog view in which the error occurs. */
    private PrologModel prolog;
    /** The control view in which the error occurs. */
    private ControlModel control;
    /** The graph in which the error occurs. */
    private AspectGraph graph;
    /** List of erroneous elements. */
    private final List<Element> elements = new ArrayList<Element>();
    /** List of numbers; typically the line and column number in a textual program. */
    private final List<Integer> numbers = new ArrayList<Integer>();
    /** Possible suberror. */
    private FormatError subError;
    /** The error message. */
    private final String message;
}
