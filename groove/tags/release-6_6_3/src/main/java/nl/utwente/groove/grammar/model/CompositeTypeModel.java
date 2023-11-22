package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.ResourceKind.HOST;
import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;
import static nl.utwente.groove.grammar.model.ResourceKind.RULE;
import static nl.utwente.groove.grammar.model.ResourceKind.TYPE;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.type.ImplicitTypeGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {
    /**
     * Constructs a composite type model
     * @param grammar the underlying graph grammar; non-{@code null}
     */
    CompositeTypeModel(GrammarModel grammar) {
        super(grammar, TYPE);
        setDependencies(PROPERTIES);
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String getName() {
        return "Composite type graph";
    }

    /**
     * Returns the constructed composite type graph, or the implicit
     * type graph if there are either no constituent type graph models enabled,
     * or there are errors in the constituent type graph models.
     * @see #toResource()
     */
    public TypeGraph getTypeGraph() {
        TypeGraph result;
        try {
            result = toResource();
        } catch (FormatException e) {
            result = getImplicitTypeGraph();
        }
        return result;
    }

    /** Indicates if the outcome of {@link #getTypeGraph()} is an implicit type graph,
     * without actually constructing the type graph if it is implicit.
     * An implicit type graph is one that has precisely the types based on the labels
     * in the host and rule graphs.
     */
    public boolean isImplicit() {
        try {
            if (getGrammar().getActiveNames(TYPE).isEmpty()) {
                return true;
            } else {
                // if toResource does not throw a format exception,
                // an explicit type graph was successfully computed
                toResource();
                return false;
            }
        } catch (FormatException exc) {
            return true;
        }
    }

    /** Indicates if this model is composed of more than one underlying type model. */
    public boolean isMultiple() {
        return getGrammar().getActiveNames(TYPE).size() > 1;
    }

    /** Returns the derived mapping from type labels to sort maps for this type model.
     * The mapping is only available if the type graph is not implicit (see {@link #isImplicit}).
     * #see {@link TypeGraph#getTypeSortMap()}
     * @return the derived mapping, or {@code null} if the type graph is implicit
     */
    public Map<TypeLabel,SortMap> getTypeSortMap() {
        return isImplicit()
            ? null
            : getTypeGraph().getTypeSortMap();
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (getGrammar().getActiveNames(TYPE).isEmpty()) {
            // this is an implicit type graph; look also at the host graphs and rules
            // these are not dependencies by default, to avoid cyclic dependencies between TYPE and RULE
            result |= isStale(HOST, RULE);
        }
        return result;
    }

    @Override
    TypeGraph compute() throws FormatException {
        TypeGraph result = null;
        Map<QualName,TypeModel> typeModelMap = computeTypeModelMap();
        // first test if there is something to be done
        if (typeModelMap.isEmpty()) {
            result = getImplicitTypeGraph();
        } else if (typeModelMap.size() == 1) {
            result = typeModelMap.values().iterator().next().toResource();
        } else {
            result = new TypeGraph(QualName.name(NAME));
            FormatErrorSet errors = createErrors();
            // There are no errors in each of the models, try to compose the
            // type graph.
            Map<TypeNode,TypeNode> nodeMergeMap = new HashMap<>();
            Map<TypeNode,TypeModel> importModels = new HashMap<>();
            for (TypeModel model : typeModelMap.values()) {
                try {
                    TypeGraph graph = model.toResource();
                    nodeMergeMap.putAll(result.add(graph));
                    for (TypeNode node : result.getImports()) {
                        if (!importModels.containsKey(node)) {
                            importModels.put(node, model);
                        }
                    }
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                } catch (IllegalArgumentException e) {
                    errors.add(e.getMessage());
                }
            }
            errors.throwException();
            var imports = result.getImports();
            if (!imports.isEmpty()) {
                errors.wrap(nodeMergeMap);
                for (var imported : imports) {
                    TypeModel origModel = importModels.get(imported);
                    errors.wrap(origModel.getMap());
                    errors.add("Unresolved type import %s", imported, origModel.getSource());
                }
            }
            errors.throwException();
            result.setFixed();
            try {
                result.test();
            } catch (FormatException exc) {
                errors = new FormatErrorSet();
                for (var error : exc.getErrors()) {
                    errors.add(error.toString() + " in the combined type graph", error, this);
                }
                errors.throwException();
            }
        }
        return result;
    }

    /**
     * Computes the mapping from names to active type models.
     * @throws FormatException if there are format errors in the active type models
     */
    private Map<QualName,TypeModel> computeTypeModelMap() throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Map<QualName,TypeModel> typeModelMap = new HashMap<>();
        for (QualName activeTypeName : getGrammar().getActiveNames(TYPE)) {
            ResourceModel<?> typeModel = getGrammar().getResource(TYPE, activeTypeName);
            typeModelMap.put(activeTypeName, (TypeModel) typeModel);
            for (FormatError error : typeModel.getErrors()) {
                errors.add("Error in type '%s': %s", activeTypeName, error, typeModel.getSource());
            }
        }
        errors.throwException();
        return typeModelMap;
    }

    /**
     * Lazily constructs and returns the implicit type graph.
     */
    private TypeGraph getImplicitTypeGraph() {
        return this.implicitTypeGraph.get();
    }

    /** The implicit type graph. */
    private final LazyFactory<TypeGraph> implicitTypeGraph
        = LazyFactory.instance(() -> ImplicitTypeGraph.newInstance(getLabels()));

    @Override
    void notifyWillRebuild() {
        this.implicitTypeGraph.reset();
    }

    /**
     * Computes the set of all labels occurring in the rules and host graph.
     * This is used to construct the implicit type graph,
     * if no type graphs are enabled.
     */
    private Set<TypeLabel> getLabels() {
        Set<TypeLabel> result = new HashSet<>();
        // get the labels from the rules and host graphs
        for (ResourceKind kind : EnumSet.of(RULE, HOST)) {
            for (ResourceModel<?> model : getGrammar().getResourceSet(kind)) {
                result.addAll(((GraphBasedModel<?>) model).getLabels());
            }
        }
        // get the labels from the external start graph
        HostModel host = getGrammar().getStartGraphModel();
        if (host != null) {
            result.addAll(host.getLabels());
        }
        return result;
    }

    /** Fixed name for the composite type model. */
    static public final String NAME = "composite-type";
}