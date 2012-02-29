package groove.view;

import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectNode;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {
    /**
     * Constructs a composite type model
     * @param grammar the underlying graph grammar; non-{@code null}
     */
    CompositeTypeModel(GrammarModel grammar) {
        super(grammar, ResourceKind.TYPE, "Composed type for "
            + grammar.getName());
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /** 
     * Returns the constructed composite type graph, or the implicit
     * type graph if there are either no constituent type graph models enabled,
     * or there are errors in the constituent type graph models.
     * @see #toResource()
     */
    public TypeGraph getTypeGraph() {
        try {
            return toResource();
        } catch (FormatException e) {
            return getImplicitTypeGraph();
        }
    }

    @Override
    TypeGraph compute() throws FormatException {
        TypeGraph result = null;
        Collection<FormatError> errors = createErrors();
        this.typeModelMap.clear();
        for (ResourceModel<?> typeModel : getGrammar().getResourceSet(TYPE)) {
            if (typeModel.isEnabled()) {
                this.typeModelMap.put(typeModel.getName(),
                    (TypeModel) typeModel);
                for (FormatError error : typeModel.getErrors()) {
                    errors.add(new FormatError("Error in type '%s': %s",
                        typeModel.getName(), error, typeModel.getSource()));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        // first test if there is something to be done
        if (this.typeModelMap.isEmpty()) {
            result = getImplicitTypeGraph();
        } else {
            result = new TypeGraph("combined type");
            // There are no errors in each of the models, try to compose the
            // type graph.
            Map<TypeNode,TypeNode> importNodes =
                new HashMap<TypeNode,TypeNode>();
            Map<TypeNode,TypeModel> importModels =
                new HashMap<TypeNode,TypeModel>();
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
                    errors.add(new FormatError(e.getMessage()));
                }
            }
            // test that there are no imported types left
            for (Map.Entry<TypeNode,TypeNode> importEntry : importNodes.entrySet()) {
                if (importEntry.getValue().isImported()) {
                    TypeNode origNode = importEntry.getKey();
                    TypeModel origModel = importModels.get(origNode);
                    errors.add(new FormatError(
                        "Error in type graph '%s': Unresolved type import '%s'",
                        origModel.getName(), origNode.label(), getInverse(
                            origModel.getMap().nodeMap(), origNode),
                        origModel.getSource()));
                }
            }
            result.setFixed();
        }
        if (errors.isEmpty()) {
            return result;
        } else {
            throw new FormatException(errors);
        }
    }

    /**
     * Lazily constructs and returns the implicit type graph.
     */
    private TypeGraph getImplicitTypeGraph() {
        if (this.implicitTypeGraph == null) {
            this.implicitTypeGraph = TypeGraph.createImplicitType(getLabels());
        }
        return this.implicitTypeGraph;
    }

    @Override
    void notifyGrammarModified() {
        this.implicitTypeGraph = null;
    }

    /**
     * Computes the set of all labels occurring in the rules and host graph.
     * This is used to construct the implicit type graph,
     * if no type graphs are enabled.
     */
    private Set<TypeLabel> getLabels() {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        // get the labels from the rules and host graphs
        for (ResourceKind kind : EnumSet.of(RULE, HOST)) {
            for (ResourceModel<?> model : getGrammar().getResourceSet(kind)) {
                result.addAll(((GraphBasedModel<?>) model).getLabels());
            }
        }
        // get the labels from the external start graph
        if (getGrammar().getStartGraphNames().isEmpty()) {
            HostModel host = getGrammar().getStartGraphModel();
            if (host != null) {
                result.addAll(host.getLabels());
            }
        }
        return result;
    }

    private AspectNode getInverse(Map<AspectNode,?> map, TypeNode image) {
        AspectNode result = null;
        for (Map.Entry<AspectNode,?> entry : map.entrySet()) {
            if (entry.getValue().equals(image)) {
                return entry.getKey();
            }
        }
        return result;
    }

    /** Mapping from active type names to corresponding type models. */
    private final Map<String,TypeModel> typeModelMap =
        new HashMap<String,TypeModel>();
    /** The implicit type graph. */
    private TypeGraph implicitTypeGraph;
}