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
package groove.util.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import groove.grammar.GrammarKey;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ControlModel;
import groove.grammar.model.PrologModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Edge;
import groove.graph.EdgeComparator;
import groove.graph.Element;
import groove.graph.Node;
import groove.graph.NodeComparator;
import groove.gui.list.ListPanel.SelectableListEntry;
import groove.lts.GraphState;
import groove.util.Pair;

/**
 * Class encoding a single message reporting an error in a graph view.
 * @author Arend Rensink
 * @version $Revision $
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
        if (par instanceof FormatError) {
            this.subError = (FormatError) par;
            this.subError.transferTo(null, this);
        } else if (par instanceof GraphState) {
            this.state = (GraphState) par;
        } else if (par instanceof AspectGraph) {
            this.graph = (AspectGraph) par;
            setResource(ResourceKind.toResource(this.graph.getRole()),
                QualName.parse(this.graph.getName()));
        } else if (par instanceof ControlModel) {
            this.control = (ControlModel) par;
            setResource(ResourceKind.CONTROL, this.control.getQualName());
        } else if (par instanceof PrologModel) {
            this.prolog = (PrologModel) par;
            setResource(ResourceKind.PROLOG, this.prolog.getQualName());
        } else if (par instanceof Element) {
            this.elements.add((Element) par);
        } else if (par instanceof Integer) {
            this.numbers.add((Integer) par);
        } else if (par instanceof Object[]) {
            for (Object subpar : (Object[]) par) {
                addContext(subpar);
            }
        } else if (par instanceof Rule) {
            setResource(ResourceKind.RULE, ((Rule) par).getQualName());
        } else if (par instanceof Recipe) {
            setResource(ResourceKind.CONTROL, ((Recipe) par).getControlName());
        } else if (par instanceof GrammarKey) {
            setResource(ResourceKind.PROPERTIES, QualName.name(((GrammarKey) par).getName()));
        } else if (par instanceof Resource) {
            setResource(((Resource) par).one(), ((Resource) par).two());
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

    /** Returns the sub-error on which this one builds. May be {@code null}. */
    public final FormatError getSubError() {
        return this.subError;
    }

    /** Possible suberror. */
    private FormatError subError;

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

    /** Returns the list of elements in which the error occurs. May be empty. */
    @Override
    public final List<Element> getElements() {
        return this.elements;
    }

    /** List of erroneous elements. */
    private final List<Element> elements = new ArrayList<>();

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

    /** Returns a new format error that extends this one with context information. */
    public FormatError extend(Object... par) {
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
        result.resourceKind = getResourceKind();
        result.resourceName = getResourceName();
    }

    /** Returns the relevant contextual arguments of this error. */
    private Object[] getArguments() {
        List<Object> newArguments = new ArrayList<>();
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

    private static int compare(Element o1, Element o2) {
        int result = o1.getClass()
            .getName()
            .compareTo(o2.getClass()
                .getName());
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
    public static class Resource extends Pair<ResourceKind,QualName> {
        /** Constructs a resource parameter. */
        public Resource(ResourceKind one, QualName two) {
            super(one, two);
        }
    }
}
