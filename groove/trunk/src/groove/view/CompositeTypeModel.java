package groove.view;

import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {

    private Map<String,TypeModel> typeModelMap;
    private Map<String,TypeGraph> typeGraphMap;
    private TypeGraph resource;
    private List<FormatError> errors;

    CompositeTypeModel(GrammarModel grammar) {
        super(ResourceKind.TYPE, "Composed type for " + grammar.getName());
        this.typeModelMap = new HashMap<String,TypeModel>();
        this.resource = null;
        this.errors = new ArrayList<FormatError>();
        for (String typeName : grammar.getTypeNames()) {
            TypeModel typeModel = grammar.getTypeModel(typeName);
            if (typeModel != null && typeModel.isEnabled()) {
                this.typeModelMap.put(typeName, typeModel);
                this.errors.addAll(typeModel.getErrors());
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
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
        this.initialise();
        if (this.resource == null) {
            throw new FormatException(this.getErrors());
        } else {
            if (this.typeModelMap.isEmpty()) {
                return null;
            } else {
                return this.resource;
            }
        }
    }

    /**
     * @return the errors in the underlying type models.
     */
    @Override
    public List<FormatError> getErrors() {
        this.initialise();
        return this.errors;
    }

    /** Returns a mapping from names to type graphs,
     * which together make up the combined type model. */
    public Map<String,TypeGraph> getTypeGraphMap() {
        this.initialise();
        return this.resource == null ? null
                : Collections.unmodifiableMap(this.typeGraphMap);
    }

    /** Constructs the model and associated data structures from the model. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors.isEmpty() && this.resource == null) {
            // There are no errors in each of the models, try to compose the
            // type graph.
            this.resource = new TypeGraph("combined type");
            this.typeGraphMap = new TreeMap<String,TypeGraph>();
            Map<TypeNode,TypeNode> importNodes =
                new HashMap<TypeNode,TypeNode>();
            Map<TypeNode,TypeModel> importModels =
                new HashMap<TypeNode,TypeModel>();
            for (TypeModel model : this.typeModelMap.values()) {
                try {
                    TypeGraph graph = model.toResource();
                    this.typeGraphMap.put(model.getName(), graph);
                    Map<TypeNode,TypeNode> map = this.resource.add(graph);
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
                this.resource.setFixed();
            } else {
                this.resource = null;
            }
        }
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
}