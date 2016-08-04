package groove.grammar.model;

import static groove.grammar.model.ResourceKind.HOST;
import static groove.grammar.model.ResourceKind.PROPERTIES;
import static groove.grammar.model.ResourceKind.RULE;
import static groove.grammar.model.ResourceKind.TYPE;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectNode;
import groove.grammar.type.ImplicitTypeGraph;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {
    /**
     * Constructs a composite type model
     * @param grammar the underlying graph grammar; non-{@code null}
     */
    CompositeTypeModel(GrammarModel grammar) {
        super(grammar, TYPE);
    }

    @Override
    public Object getSource() {
        return null;
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

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (result) {
            result = isStale(TYPE, PROPERTIES);
            if (getGrammar().getActiveNames(TYPE)
                .isEmpty()) {
                // it's an implicit type graph; look also at the host graphs and rules
                result |= isStale(HOST, RULE);
            }
        }
        return result;
    }

    @Override
    TypeGraph compute() throws FormatException {
        TypeGraph result = null;
        FormatErrorSet errors = createErrors();
        this.typeModelMap.clear();
        for (QualName activeTypeName : getGrammar().getActiveNames(TYPE)) {
            ResourceModel<?> typeModel = getGrammar().getResource(TYPE, activeTypeName);
            this.typeModelMap.put(activeTypeName, (TypeModel) typeModel);
            for (FormatError error : typeModel.getErrors()) {
                errors.add("Error in type '%s': %s", activeTypeName, error, typeModel.getSource());
            }
        }
        errors.throwException();
        // first test if there is something to be done
        if (this.typeModelMap.isEmpty()) {
            result = getImplicitTypeGraph();
        } else {
            result = new TypeGraph(QualName.name(NAME));
            // There are no errors in each of the models, try to compose the
            // type graph.
            Map<TypeNode,TypeNode> importNodes = new HashMap<>();
            Map<TypeNode,TypeModel> importModels = new HashMap<>();
            for (TypeModel model : this.typeModelMap.values()) {
                try {
                    TypeGraph graph = model.toResource();
                    Map<TypeNode,TypeNode> map = result.add(graph);
                    for (TypeNode node : graph.getImports()) {
                        importNodes.put(node, map.get(node));
                        importModels.put(node, model);
                    }
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                } catch (IllegalArgumentException e) {
                    errors.add(e.getMessage());
                }
            }
            // test that there are no imported types left
            for (Map.Entry<TypeNode,TypeNode> importEntry : importNodes.entrySet()) {
                if (importEntry.getValue()
                    .isImported()) {
                    TypeNode origNode = importEntry.getKey();
                    TypeModel origModel = importModels.get(origNode);
                    errors.add("Error in type graph '%s': Unresolved type import '%s'",
                        origModel.getQualName(),
                        origNode.label(),
                        getInverse(origModel.getMap()
                            .nodeMap(), origNode),
                        origModel.getSource());
                }
            }
            result.setFixed();
        }
        errors.throwException();
        return result;
    }

    /**
     * Lazily constructs and returns the implicit type graph.
     */
    private TypeGraph getImplicitTypeGraph() {
        if (this.implicitTypeGraph == null) {
            this.implicitTypeGraph = ImplicitTypeGraph.newInstance(getLabels());
        }
        return this.implicitTypeGraph;
    }

    @Override
    void notifyWillRebuild() {
        this.implicitTypeGraph = null;
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

    private AspectNode getInverse(Map<AspectNode,?> map, TypeNode image) {
        AspectNode result = null;
        for (Map.Entry<AspectNode,?> entry : map.entrySet()) {
            if (entry.getValue()
                .equals(image)) {
                return entry.getKey();
            }
        }
        return result;
    }

    /** Mapping from active type names to corresponding type models. */
    private final Map<QualName,TypeModel> typeModelMap = new HashMap<>();
    /** The implicit type graph. */
    private TypeGraph implicitTypeGraph;

    /** Fixed name for the composite type model. */
    static public final String NAME = "composite-type";
}