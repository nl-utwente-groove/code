package groove.io.conceptual.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groove.grammar.QualName;
import groove.io.conceptual.TypeModel;

@SuppressWarnings("javadoc")
public abstract class TypeImporter implements Messenger {
    private List<Message> m_messages = new ArrayList<>();
    protected Map<QualName,TypeModel> m_typeModels = new HashMap<>();

    /**
     * Returns a collection of strings representing each loaded type model. Use {@link TypeImporter#getTypeModel(QualName)} to retrieve the actual associated type
     * model.
     * @return A collection of strings representing each type model
     */
    public Collection<QualName> getTypeModelNames() {
        return this.m_typeModels.keySet();
    }

    /**
     * Returns the type model associated with the given name. Messages may be generated during this operation.
     * @param modelName The name of the type model to retrieve.
     * @return The type model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public abstract TypeModel getTypeModel(QualName modelName) throws ImportException;

    /**
     * Returns the first type model found.
     * @return The type model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public TypeModel getTypeModel() throws ImportException {
        Collection<QualName> names = getTypeModelNames();
        if (names.size() > 0) {
            return getTypeModel(names.iterator()
                .next());
        }
        return null;
    }

    protected void addMessage(Message m) {
        this.m_messages.add(m);
    }

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }

    @Override
    public void clearMessages() {
        this.m_messages.clear();
    }
}
