package groove.view;

import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {
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
        if (isGrammarModified()) {
            this.initialise();
        }
        return !this.typeModelMap.isEmpty();
    }

    /**
     * @return the composite type graph from the model list, or {@code null}
     * if there are no active type models
     * @throws FormatException if any of the models has errors.
     * @throws IllegalArgumentException if the composition of types gives
     *         rise to typing cycles.
     */
    @Override
    public TypeGraph toResource() throws FormatException,
        IllegalArgumentException {
        maybeInitialise();
        if (hasErrors()) {
            throw new FormatException(getErrors());
        } else {
            if (this.typeModelMap.isEmpty()) {
                return null;
            } else {
                return this.typeGraph;
            }
        }
    }

    /**
     * @return the errors in the underlying type models.
     */
    @Override
    public List<FormatError> getErrors() {
        maybeInitialise();
        return this.errors;
    }

    /** Returns a mapping from names to type graphs,
     * which together make up the combined type model. */
    public Map<String,TypeGraph> getTypeGraphMap() {
        maybeInitialise();
        return this.typeGraph == null ? null
                : Collections.unmodifiableMap(this.typeGraphMap);
    }

    /**
     * Initialises the model if the grammar has been modified.
     * @see #isGrammarModified()
     * @see #initialise()
     */
    private void maybeInitialise() {
        if (isGrammarModified()) {
            this.initialise();
        }
    }

    /** Constructs the model and associated data structures from the model. */
    private void initialise() {
        this.typeModelMap.clear();
        this.typeGraphMap.clear();
        this.errors.clear();
        this.typeGraph = null;
        this.labelStore = null;
        for (ResourceModel<?> typeModel : getGrammar().getResourceSet(TYPE)) {
            if (typeModel.isEnabled()) {
                this.typeModelMap.put(typeModel.getName(),
                    (TypeModel) typeModel);
                this.errors.addAll(typeModel.getErrors());
            }
        }
        // first test if there is something to be done
        if (this.errors.isEmpty()) {
            // There are no errors in each of the models, try to compose the
            // type graph.
            this.typeGraph = new TypeGraph("combined type");
            Map<TypeNode,TypeNode> importNodes =
                new HashMap<TypeNode,TypeNode>();
            Map<TypeNode,TypeModel> importModels =
                new HashMap<TypeNode,TypeModel>();
            for (TypeModel model : this.typeModelMap.values()) {
                try {
                    TypeGraph graph = model.toResource();
                    this.typeGraphMap.put(model.getName(), graph);
                    Map<TypeNode,TypeNode> map = this.typeGraph.add(graph);
                    for (TypeNode node : graph.getImports()) {
                        importNodes.put(node, map.get(node));
                        importModels.put(node, model);
                    }
                } catch (FormatException e) {
                    this.errors.addAll(e.getErrors());
                } catch (IllegalArgumentException e) {
                    this.errors.add(new FormatError(e.getMessage()));
                }
            }
            // test that there are no imported types left
            for (Map.Entry<TypeNode,TypeNode> importEntry : importNodes.entrySet()) {
                if (importEntry.getValue().isImported()) {
                    TypeNode origNode = importEntry.getKey();
                    TypeModel origModel = importModels.get(origNode);
                    this.errors.add(new FormatError(
                        "Error in type graph '%s': Unresolved type import '%s'",
                        origModel.getName(), origNode.getType(), getInverse(
                            origModel.getMap().nodeMap(), origNode),
                        origModel.getSource()));
                }
            }
            if (this.errors.isEmpty()) {
                this.typeGraph.setFixed();
            } else {
                this.typeGraph = null;
            }
        }
    }

    /** Computes the label store or retrieves it from the type graph. */
    public LabelStore getLabelStore() {
        LabelStore result = null;
        maybeInitialise();
        if (this.labelStore == null) {
            if (this.typeGraph == null) {
                result = new LabelStore();
                for (ResourceKind kind : EnumSet.of(RULE, HOST)) {
                    for (ResourceModel<?> model : getGrammar().getResourceSet(
                        kind)) {
                        result.addLabels(((GraphBasedModel<?>) model).getLabels());
                    }
                }
                HostModel host = getGrammar().getStartGraphModel();
                if (host != null) {
                    result.addLabels(host.getLabels());
                }
                try {
                    result.addDirectSubtypes(getGrammar().getProperties().getSubtypes());
                } catch (FormatException exc) {
                    // do nothing
                }
                result.setFixed();
            } else {
                this.labelStore = this.typeGraph.getLabelStore();
            }
        }
        return this.labelStore;
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
    private final Map<String,TypeGraph> typeGraphMap =
        new TreeMap<String,TypeGraph>();
    /** The composed type graph; may be {@code null}. */
    private TypeGraph typeGraph;
    /**
     * The label store, either from the type graph
     * or independently computed.
     */
    private LabelStore labelStore;
    /** The list of errors in the composite type. */
    private final List<FormatError> errors = new ArrayList<FormatError>();
}