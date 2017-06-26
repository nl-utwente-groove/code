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
package groove.gui.list;

import java.util.ArrayList;
import java.util.List;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.graph.Element;
import groove.gui.list.ListPanel.SelectableListEntry;

/**
 * Class encoding a single message reporting a search result in a graph view.
 * @author Eduardo Zambon
 */
public class SearchResult implements SelectableListEntry {
    /** Constructs an error consisting of a string message. */
    public SearchResult(String message) {
        this.message = message;
    }

    /**
     * Constructs a search result consisting of a message to be formatted.
     * The actual message is constructed by calling {@link String#format(String, Object...)}
     * The parameters are interpreted as giving information about the result.
     */
    public SearchResult(String message, Object... pars) {
        this(String.format(message, pars));
        for (Object par : pars) {
            addContext(par);
        }
    }

    /**
     * Attempts to set a context value ({@link #graph},
     * {@link #elements}) from a given object.
     */
    private void addContext(Object par) {
        if (par instanceof AspectGraph) {
            this.graph = (AspectGraph) par;
            this.resourceName = this.graph.getQualName();
            this.resourceKind = ResourceKind.toResource(this.graph.getRole());
        } else if (par instanceof Element) {
            this.elements.add((Element) par);
        } else if (par instanceof Object[]) {
            for (Object subpar : (Object[]) par) {
                addContext(subpar);
            }
        }
    }

    /** Compares the message. */
    @Override
    public boolean equals(Object obj) {
        boolean result = obj instanceof SearchResult;
        if (result) {
            SearchResult err = (SearchResult) obj;
            result = toString().equals(err.toString());
        }
        return result;
    }

    /** The hash code is based on the message. */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return this.message;
    }

    /** Returns the graph in which the error occurs. May be {@code null}. */
    public final AspectGraph getGraph() {
        return this.graph;
    }

    /** Returns the list of elements in which the error occurs. May be empty. */
    @Override
    public final List<Element> getElements() {
        return this.elements;
    }

    /** Returns the resource kind for which this error occurs. */
    @Override
    public final ResourceKind getResourceKind() {
        return this.resourceKind;
    }

    /** Returns the resource name for which this error occurs. */
    @Override
    public final QualName getResourceName() {
        return this.resourceName;
    }

    /** The graph in which the result occurs. */
    private AspectGraph graph;
    /** The resource kind for which the result occurs. May be {@code null}. */
    private ResourceKind resourceKind;
    /** The name of the resource on which the result occurs. May be {@code null}. */
    private QualName resourceName;
    /** List of result elements. */
    private final List<Element> elements = new ArrayList<>();
    /** The result message. */
    private final String message;

}
