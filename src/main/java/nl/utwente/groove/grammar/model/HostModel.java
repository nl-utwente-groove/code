/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.model;

import java.util.HashSet;
import java.util.Set;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Status;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Graph-based model of a host graph graph. Attribute values are represented
 * by {@link ValueNode}s.
 * @author Arend Rensink
 * @version $Rev$
 */
public class HostModel extends GraphBasedModel<HostGraph> {
    /**
     * Constructs an instance from a given aspect graph.
     * @param grammar the grammar to which the host graph belongs; may be {@code null} if
     * there is no enclosing grammar
     */
    public HostModel(GrammarModel grammar, AspectGraph source) {
        super(grammar, source);
        source.testFixed(true);
        setDependencies(ResourceKind.TYPE, ResourceKind.PROPERTIES);
    }

    /**
     * Constructs the host graph from this resource.
     * @throws FormatException if the resource contains errors.
     */
    public HostGraph toHost() throws FormatException {
        return toResource();
    }

    @Override
    public HostModelMap getMap() {
        synchronise();
        if (hasErrors()) {
            throw new IllegalStateException();
        }
        return this.morphism.get().map();
    }

    @Override
    public TypeGraph getTypeGraph() {
        return this.morphism.get().getTypeGraph();
    }

    @Override
    public TypeModelMap getTypeMap() {
        return this.typeMap.get();
    }

    /** Map from source model to types. */
    private final Factory<TypeModelMap> typeMap = Factory.lazy(this::createTypeMap);

    private TypeModelMap createTypeMap() {
        synchronise();
        TypeModelMap result = null;
        if (getStatus() == Status.DONE) {
            var morphism = this.morphism.get();
            var hostModelMap = morphism.map();
            // create the type map
            result = new TypeModelMap(morphism.getTypeGraph().getFactory());
            for (var ne : hostModelMap.nodeMap().entrySet()) {
                result.putNode(ne.getKey(), ne.getValue().getType());
            }
            for (var ee : hostModelMap.edgeMap().entrySet()) {
                result.putEdge(ee.getKey(), ee.getValue().getType());
            }
        }
        return result;
    }

    @Override
    public Set<TypeLabel> getLabels() {
        return this.labelSet.get();
    }

    /** Set of labels occurring in this graph. */
    private final Factory<Set<TypeLabel>> labelSet = Factory.lazy(this::createLabels);

    private Set<TypeLabel> createLabels() {
        Set<TypeLabel> result = new HashSet<>();
        for (AspectEdge edge : getSource().normalise().edgeSet()) {
            TypeLabel label = edge.getTypeLabel();
            if (label != null) {
                result.add(label);
            }
        }
        return result;
    }

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.labelSet.reset();
        this.morphism.reset();
        this.typeMap.reset();
    }

    @Override
    HostGraph compute() throws FormatException {
        getSource().getErrors().throwException();
        var morphism = this.morphism.get();
        morphism.getErrors().throwException();
        return morphism.target();
    }

    private final Factory<HostModelMorphism> morphism
        = Factory.lazy(() -> HostModelMorphism.instance(getGrammar(), getSource()));
}
