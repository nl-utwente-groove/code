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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ControlModel;
import nl.utwente.groove.grammar.model.PrologModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphMap;
import nl.utwente.groove.graph.GraphProperties.Key;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.gui.list.ListPanel.SelectableListEntry;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.collect.ListComparator;

/**
 * Class encoding a single message reporting an error in a graph view.
 * @author Arend Rensink
 * @version $Revision$
 */
public class FormatError
    implements Comparable<FormatError>, SelectableListEntry, Fixable, Cloneable {
    /**
     * Constructs an error consisting of a message to be formatted.
     * The actual message is constructed by calling {@link String#format(String, Object...)}
     * The parameters are interpreted as giving information about the error.
     */
    public FormatError(String message, Object... pars) {
        this.message = String.format(message, pars);
        for (Object par : pars) {
            addContext(par);
        }
    }

    /**
     * Attempts to set a context value ({@link #graph}, {@link #control},
     * {@link #elements}) from a given object.
     */
    private void addContext(Object par) {
        assert !isFixed();
        if (par instanceof Object[] a) {
            Arrays.stream(a).forEach(this::addContext);
        } else if (par instanceof Collection<?> c) {
            c.forEach(this::addContext);
        } else if (par instanceof FormatError e) {
            e.getArguments().forEach(this::addContext);
            this.resourceKind = e.getResourceKind();
            e.getResourceNames().forEach(n -> addResource(getResourceKind(), n));
        } else if (par instanceof Key k) {
            this.key = k;
        } else if (par instanceof GraphState s) {
            this.state = s;
        } else if (par instanceof AspectGraph g) {
            this.graph = g;
            addResource(ResourceKind.toResource(g.getRole()), g.getQualName());
        } else if (par instanceof ControlModel c) {
            this.control = c;
            addResource(ResourceKind.CONTROL, c.getQualName());
        } else if (par instanceof PrologModel p) {
            this.prolog = p;
            addResource(ResourceKind.PROLOG, p.getQualName());
        } else if (par instanceof Element e) {
            this.elements.add(e);
        } else if (par instanceof Integer i) {
            this.numbers.add(i);
        } else if (par instanceof TypeGraph tg) {
            addResource(ResourceKind.TYPE, tg.getQualName());
        } else if (par instanceof Rule r) {
            addResource(ResourceKind.RULE, r.getQualName());
        } else if (par instanceof Recipe r) {
            addResource(ResourceKind.CONTROL, r.getControlName());
        } else if (par instanceof GrammarKey k) {
            addResource(ResourceKind.PROPERTIES, QualName.name(k.getName()));
        } else if (par instanceof Resource r) {
            addResource(r.kind(), r.name());
        }
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

    /** Returns the message of this format error
     * in which all '%' characters have been replaced by '%%', so
     * that it can be used as input to {@link String#format(String, Object...)} without
     * expecting any arguments.
     */
    public String toFormattableString() {
        var message = this.message;
        StringBuffer result = new StringBuffer(message.length() + 1);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                result.append("%%");
            } else {
                result.append(c);
            }
        }
        return result.toString();
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
        if (result == 0) {
            result = elemListComparator.compare(getElements(), other.getElements());
        }
        return result;
    }

    /** Returns the control view in which the error occurs. May be {@code null}. */
    public final @Nullable ControlModel getControl() {
        return this.control;
    }

    /** The control view in which the error occurs. */
    private ControlModel control;

    /** Returns the prolog view in which the error occurs. May be {@code null}. */
    public final @Nullable PrologModel getProlog() {
        return this.prolog;
    }

    /** The prolog view in which the error occurs. */
    private PrologModel prolog;

    /** Returns the graph in which the error occurs. May be {@code null}. */
    public final @Nullable AspectGraph getGraph() {
        return this.graph;
    }

    /** The graph in which the error occurs. */
    private AspectGraph graph;

    /** Returns the state in which the error occurs. May be {@code null}. */
    public final @Nullable GraphState getState() {
        return this.state;
    }

    /** The (possibly {@code null}) state in which the error occurs. */
    private GraphState state;

    /** Returns the property key in which the error occurs. May be {@code null}. */
    @Override
    public final @Nullable Key getPropertyKey() {
        return this.key;
    }

    /** The (possibly {@code null}) key in which the error occurs. */
    private Key key;

    /** Returns the list of elements in which the error occurs, together
     * with its projections. May be empty. */
    @Override
    public List<Element> getElements() {
        return this.elements;
    }

    /** List of erroneous elements. */
    private List<Element> elements = new ArrayList<>();

    /** Modifies the list of elements in this error by applying a mapping to it.
     * Returns this error for chaining.
     */
    FormatError apply(Map<? extends Element,? extends Element> map) {
        if (!map.isEmpty()) {
            var elements = this.elements;
            List<Element> newElements = new ArrayList<>(elements.size());
            for (var e : elements) {
                var i = map.get(e);
                if (i != null) {
                    newElements.add(i);
                }
            }
            this.elements.addAll(newElements);
        }
        return this;
    }

    /** Returns a list of numbers associated with the error; typically,
     * line and column numbers. May be empty. */
    public final List<Integer> getNumbers() {
        return this.numbers;
    }

    /** List of numbers; typically the line and column number in a textual program. */
    private final List<Integer> numbers = new ArrayList<>();

    /** Sets the resource in which this error occurs. */
    private void addResource(ResourceKind kind, QualName name) {
        this.resourceKind = kind;
        this.resourceNames.add(name);
    }

    /** Returns the resource kind for which this error occurs. */
    @Override
    public final ResourceKind getResourceKind() {
        return this.resourceKind;
    }

    /** The resource kind for which the error occurs. May be {@code null}. */
    private ResourceKind resourceKind;

    /** Returns the resource kind for which this error occurs. */
    @Override
    public final SortedSet<QualName> getResourceNames() {
        return this.resourceNames;
    }

    /** The name of the resource on which the error occurs. May be {@code null}. */
    private final SortedSet<QualName> resourceNames = new TreeSet<>();

    /** Returns a new format error in which the context information is transferred modulo
     * a graph map. The new error has no parent.
     * @param map mapping from the context of this error to the context
     * of the result error; or {@code null} if there is no mapping
     */
    FormatError transfer(GraphMap map) {
        var result = this;
        if (!map.isEmpty()) {
            Map<Object,Object> elementMap = new HashMap<>();
            elementMap.putAll(map.nodeMap());
            elementMap.putAll(map.edgeMap());
            result = clone(elementMap);
        }
        return result;
    }

    /** Returns a new format error that extends this one with context information.
     * The new error has no parent as yet.
     */
    public FormatError extend(Object... pars) {
        FormatError result = clone(null);
        for (Object par : pars) {
            result.addContext(par);
        }
        return result;
    }

    /** Returns the contextual arguments of this error. */
    private List<Object> getArguments() {
        List<Object> result = new ArrayList<>();
        result.addAll(getElements());
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
        if (this.key != null) {
            result.add(this.key);
        }
        return result;
    }

    @Override
    public FormatError clone() {
        return clone(null);
    }

    /** Returns a clone of this error, with no parent.
     * An optional graph map determines how the context arguments are mapped.
     */
    private FormatError clone(@Nullable Map<?,?> map) {
        var result = new FormatError(toFormattableString());
        for (var arg : getArguments()) {
            var newArg = map != null && map.containsKey(arg)
                ? map.get(arg)
                : arg;
            result.addContext(newArg);
        }
        result.resourceKind = getResourceKind();
        getResourceNames().forEach(n -> result.addResource(getResourceKind(), n));
        return result;
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            var allElements = getElements();
            this.elements.clear();
            this.elements.addAll(allElements);
            this.fixed = true;
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    /** Flag indicating if this object is fixed. */
    private boolean fixed;

    private static final NodeComparator nodeComparator = NodeComparator.instance();
    private static final Comparator<Edge> edgeComparator = EdgeComparator.instance();
    /** Comparator for graph elements. */
    private static final Comparator<Element> elemComparator = (el1, el2) -> {
        int result = el1.getClass().getName().compareTo(el2.getClass().getName());
        if (result != 0) {
            return result;
        }
        return switch (el1) {
        case Node n1 -> nodeComparator.compare(n1, (Node) el2);
        case Edge e1 -> edgeComparator.compare(e1, (Edge) el2);
        default -> throw Exceptions.UNREACHABLE;
        };
    };
    /** Comparator for lists of graph elements. */
    private static final Comparator<List<Element>> elemListComparator
        = ListComparator.<Element>instance(elemComparator);

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
