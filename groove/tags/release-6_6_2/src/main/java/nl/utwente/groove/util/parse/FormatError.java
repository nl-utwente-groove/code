/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ControlModel;
import nl.utwente.groove.grammar.model.PrologModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphMap;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.gui.list.ListPanel.SelectableListEntry;
import nl.utwente.groove.lts.GraphState;

/**
 * Class encoding a single message reporting an error in a graph view.
 * @author Arend Rensink
 * @version $Revision$
 */
public class FormatError implements Comparable<FormatError>, SelectableListEntry {
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

    /** Sets the resource in which this error occurs. */
    public void setResource(ResourceKind kind, QualName name) {
        this.resourceKind = kind;
        this.resourceName = name;
    }

    /**
     * Attempts to set a context value ({@link #graph}, {@link #control},
     * {@link #elements}) from a given object.
     */
    private void addContext(Object par) {
        if (par instanceof FormatError e) {
            addContext(e.getArguments());
            extendProjection(e.projection, true);
        } else if (par instanceof GraphState s) {
            this.state = s;
        } else if (par instanceof AspectGraph g) {
            this.graph = g;
            setResource(ResourceKind.toResource(g.getRole()), QualName.parse(g.getName()));
        } else if (par instanceof ControlModel c) {
            this.control = c;
            setResource(ResourceKind.CONTROL, c.getQualName());
        } else if (par instanceof PrologModel p) {
            this.prolog = p;
            setResource(ResourceKind.PROLOG, p.getQualName());
        } else if (par instanceof Element e) {
            this.elements.add(e);
        } else if (par instanceof Integer i) {
            this.numbers.add(i);
        } else if (par instanceof Object[] a) {
            for (Object subpar : a) {
                addContext(subpar);
            }
        } else if (par instanceof Collection<?> c) {
            for (Object subpar : c) {
                addContext(subpar);
            }
        } else if (par instanceof Rule r) {
            setResource(ResourceKind.RULE, r.getQualName());
        } else if (par instanceof Recipe r) {
            setResource(ResourceKind.CONTROL, r.getControlName());
        } else if (par instanceof GrammarKey k) {
            setResource(ResourceKind.PROPERTIES, QualName.name(k.getName()));
        } else if (par instanceof Resource r) {
            setResource(r.kind(), r.name());
        }
    }

    /** Constructs an error from an existing error, by adding extra information. */
    public FormatError(FormatError prior, Object... pars) {
        // don't call this(String,Object...) as the prior string may contain %'s
        // which give rise to exceptions in String.format()
        this(prior.toString());
        for (Object par : prior.getArguments()) {
            addContext(par);
        }
        for (Object par : pars) {
            addContext(par);
        }
        this.elements.addAll(prior.getElements());
        if (this.graph == null) {
            this.graph = prior.getGraph();
        }
        if (this.resourceKind == null) {
            this.resourceKind = prior.getResourceKind();
        }
        if (this.resourceName == null) {
            this.resourceName = prior.getResourceName();
        }
        this.projection.putAll(prior.projection);
    }

    /** Compares the error graph, error object and message. */
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (obj instanceof FormatError err) {
            result = getArguments().equals(err.getArguments());
            result &= toString().equals(err.toString());
        } else {
            result = false;
        }
        return result;
    }

    /** The hash code is based on the error graph, error object and message. */
    @Override
    public int hashCode() {
        int result = toString().hashCode();
        result += getArguments().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /** The error message. */
    private final String message;

    /**
     * Compares only the error element and message.
     * This means that identically worded errors with the same element but for different graphs will be collapsed.
     */
    @Override
    public int compareTo(FormatError other) {
        int result = toString().compareTo(other.toString());
        // establish lexicographical ordering of error objects
        List<Element> myElements = this.getElements();
        List<Element> otherElements = other.getElements();
        int upper = Math.min(myElements.size(), otherElements.size());
        for (int i = 0; result == 0 && i < upper; i++) {
            result = compare(myElements.get(i), otherElements.get(i));
        }
        if (result == 0) {
            result = myElements.size() - otherElements.size();
        }
        return result;
    }

    /** Returns the control view in which the error occurs. May be {@code null}. */
    public final ControlModel getControl() {
        return this.control;
    }

    /** The control view in which the error occurs. */
    private ControlModel control;

    /** Returns the prolog view in which the error occurs. May be {@code null}. */
    public final PrologModel getProlog() {
        return this.prolog;
    }

    /** The prolog view in which the error occurs. */
    private PrologModel prolog;

    /** Returns the graph in which the error occurs. May be {@code null}. */
    public final AspectGraph getGraph() {
        return this.graph;
    }

    /** The graph in which the error occurs. */
    private AspectGraph graph;

    /** Returns the state in which the error occurs. May be {@code null}. */
    public final GraphState getState() {
        return this.state;
    }

    /** The state in which the error occurs. */
    private GraphState state;

    /** List of erroneous elements. */
    private final List<Element> elements = new ArrayList<>();

    /** Returns the list of elements in which the error occurs, together
     * with its projections. May be empty. */
    @Override
    public List<Element> getElements() {
        var result = new ArrayList<Element>();
        for (var e : this.elements) {
            while (e != null) {
                result.add(e);
                e = this.projection.get(e);
            }
        }
        return result;
    }

    /** Mapping from the graph {@link Element}s in this error to the
     * {@link AspectElement}s from which they originated.
     */
    private final Map<Element,Element> projection = new HashMap<>();

    /** Returns a list of numbers associated with the error; typically,
     * line and column numbers. May be empty. */
    public final List<Integer> getNumbers() {
        return this.numbers;
    }

    /** List of numbers; typically the line and column number in a textual program. */
    private final List<Integer> numbers = new ArrayList<>();

    /** Returns the resource kind for which this error occurs. */
    @Override
    public final ResourceKind getResourceKind() {
        return this.resourceKind;
    }

    /** The resource kind for which the error occurs. May be {@code null}. */
    private ResourceKind resourceKind;

    /** Returns the resource kind for which this error occurs. */
    @Override
    public final QualName getResourceName() {
        return this.resourceName;
    }

    /** The name of the resource on which the error occurs. May be {@code null}. */
    private QualName resourceName;

    /** Clones this error while adding a projection.
     * The projection goes outward, i.e., from (graph) elements in this error to
     * contextual (graph) elements that originated the error.
     * @param projection mapping from error {@link Element}s to contextual {@link Element}s
     */
    FormatError project(Map<? extends Element,? extends Element> projection) {
        var result = this;
        if (!projection.isEmpty()) {
            result = new FormatError(toString());
            result.copyFrom(this, null);
            result.extendProjection(projection, true);
        }
        return result;
    }

    /** Clones this error while adding a projection.
     * The projection goes outward, i.e., from (graph) elements in this error to
     * contextual (graph) elements that originated the error.
     * @param projection mapping from error {@link Element}s to contextual {@link Element}s
     */
    public FormatError project(GraphMap projection) {
        var result = this;
        if (!projection.isEmpty()) {
            result = new FormatError(toString());
            result.copyFrom(this, null);
            result.extendProjection(projection.nodeMap(), true);
            result.extendProjection(projection.edgeMap(), true);
        }
        return result;
    }

    /** Clones this error while adding a wrapper.
     * The wrapper goes inward, i.e., from contextual
     * (graph) elements that originated the error to (graph) elements in this error.
     * This is the inverse wrt to {@link #project(GraphMap)}
     * @param wrapper mapping from contextual {@link Element}s to error {@link Element}s
     */
    public FormatError unwrap(Map<? extends Element,? extends Element> wrapper) {
        var result = this;
        if (!wrapper.isEmpty()) {
            result = new FormatError(toString());
            result.copyFrom(this, null);
            result.extendProjection(wrapper, false);
        }
        return result;
    }

    /** Clones this error while adding a wrapper.
     * The wrapper goes inward, i.e., from contextual
     * (graph) elements that originated the error to (graph) elements in this error.
     * This is the inverse wrt to {@link #project(GraphMap)}
     * @param wrapper mapping from contextual {@link Element}s to error {@link Element}s
     */
    public FormatError unwrap(GraphMap wrapper) {
        var result = this;
        if (!wrapper.isEmpty()) {
            result = new FormatError(toString());
            result.copyFrom(this, null);
            result.extendProjection(wrapper.edgeMap(), false);
            result.extendProjection(wrapper.nodeMap(), false);
        }
        return result;
    }

    /** Returns a new format error in which the context information is transferred.
     * @param map mapping from the context of this error to the context
     * of the result error; or {@code null} if there is no mapping
     */
    public FormatError transfer(GraphMap map) {
        var result = this;
        if (!map.isEmpty()) {
            result = new FormatError(toString());
            Map<Object,Object> elementMap = new HashMap<>();
            elementMap.putAll(map.nodeMap());
            elementMap.putAll(map.edgeMap());
            result.copyFrom(this, elementMap);
        }
        return result;
    }

    /**
     * Transfers the context information of another error object to
     * this one, modulo a mapping.
     * @param prior the source of the transfer
     * @param map mapping from the context of the prior error to the context
     * of this error; or {@code null} if there is no mapping
     */
    private void copyFrom(FormatError prior, @Nullable Map<?,?> map) {
        this.projection.putAll(prior.projection);
        for (Object arg : prior.getArguments()) {
            var transferredArg = map != null && map.containsKey(arg)
                ? map.get(arg)
                : arg;
            addContext(transferredArg);
            var priorImage = prior.projection.remove(arg);
            if (priorImage != null && transferredArg instanceof Element te) {
                this.projection.put(te, priorImage);
            }
        }
        this.resourceKind = prior.getResourceKind();
        this.resourceName = prior.getResourceName();
    }

    /** Extends the projection map of this error with a given map.
     * @param map the extension map
     * @param outward flag indicating if {@code map} goes outward
     * (<i>from</i> context elements in this error) or inward
     * (<i>to</i> elements in this error)
     */
    private void extendProjection(Map<? extends Element,? extends Element> map, boolean outward) {
        for (var e : map.entrySet()) {
            if (outward) {
                this.projection.put(e.getKey(), e.getValue());
            } else {
                this.projection.put(e.getValue(), e.getKey());
            }
        }
    }

    /** Returns a new format error that extends this one with context information. */
    public FormatError extend(Object... par) {
        return new FormatError(this, par);
    }

    /** Returns the contextual arguments of this error. */
    private List<Object> getArguments() {
        List<Object> result = new ArrayList<>();
        result.addAll(this.elements);
        if (this.control != null) {
            result.add(this.control);
        }
        if (this.prolog != null) {
            result.add(this.prolog);
        }
        result.addAll(this.numbers);
        if (this.graph != null) {
            result.add(this.graph);
        }
        if (this.state != null) {
            result.add(this.state);
        }
        return result;
    }

    private static int compare(Element o1, Element o2) {
        int result = o1.getClass().getName().compareTo(o2.getClass().getName());
        if (result != 0) {
            return result;
        }
        if (o1 instanceof Node) {
            result = nodeComparator.compare((Node) o1, (Node) o2);
        } else {
            result = edgeComparator.compare((Edge) o1, (Edge) o2);
        }
        return result;
    }

    private static final NodeComparator nodeComparator = NodeComparator.instance();
    private static final Comparator<Edge> edgeComparator = EdgeComparator.instance();

    /** Constructs a control parameter from a given name. */
    public static Resource control(QualName name) {
        return resource(ResourceKind.CONTROL, name);
    }

    /** Constructs a resource parameter from a given resource kind and name. */
    public static Resource resource(ResourceKind kind, QualName name) {
        return new Resource(kind, name);
    }

    /** Resource parameter class. */
    public static record Resource(ResourceKind kind, QualName name) {
        // empty by design

    }
}
