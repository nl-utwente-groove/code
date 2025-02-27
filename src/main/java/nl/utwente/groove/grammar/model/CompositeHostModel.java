package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.ResourceKind.HOST;
import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;
import static nl.utwente.groove.grammar.model.ResourceKind.TYPE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/** Class to store the models that are used to compose the type graph. */
public class CompositeHostModel extends ResourceModel<HostGraph> {
    /**
     * Constructs a composite type model
     * @param grammar the underlying graph grammar
     */
    CompositeHostModel(@NonNull GrammarModel grammar) {
        super(grammar, TYPE);
        setDependencies(PROPERTIES);
    }

    @Override
    public @NonNull GrammarModel getGrammar() {
        var result = super.getGrammar();
        assert result != null;
        return result;
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String getName() {
        return "Composite host graph";
    }

    /** Indicates if this model is composed of more than one underlying host model. */
    public boolean isMultiple() {
        return getGrammar().getActiveNames(HOST).size() > 1;
    }

    @Override
    HostGraph compute() throws FormatException {
        var hostModelMap = computeHostModelMap();
        if (hostModelMap.isEmpty()) {
            throw new FormatException("No active start graph");
        } else {
            AspectGraph startGraph = AspectGraph.mergeGraphs(getGrammar().getActiveGraphs(HOST));
            return new HostModel(getGrammar(), startGraph).compute();
        }
    }

    /**
     * Computes the mapping from names to active host models.
     * @throws FormatException if there are format errors in the active type models
     */
    private Map<QualName,HostModel> computeHostModelMap() throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Map<QualName,HostModel> result = new HashMap<>();
        for (var activeHostName : getGrammar().getActiveNames(HOST)) {
            ResourceModel<?> hostModel = getGrammar().getResource(HOST, activeHostName);
            result.put(activeHostName, (HostModel) hostModel);
            for (FormatError error : hostModel.getErrors()) {
                errors
                    .add("Error in host graph '%s': %s", activeHostName, error,
                         hostModel.getSource());
            }
        }
        errors.throwException();
        return result;
    }

    /** Fixed name for the composite host model. */
    static public final String NAME = "composite-host";
}