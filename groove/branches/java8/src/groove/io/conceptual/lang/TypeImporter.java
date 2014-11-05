package groove.io.conceptual.lang;

import groove.io.conceptual.TypeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Abstract class wrapping the functionality to create a design type from another format. */
public abstract class TypeImporter implements Messenger {
    /**
     * Returns a collection of strings representing each loaded type model.
     * Use {@link TypeImporter#getTypeModel(String)} to retrieve the actual associated type
     * model.
     * @return A collection of strings representing each type model
     */
    public Collection<String> getTypeModelNames() {
        return this.m_typeModels.keySet();
    }

    /** Inserts a named type model into this importer. */
    public void putTypeModel(String name, TypeModel typeModel) {
        this.m_typeModels.put(name, typeModel);
    }

    /**
     * Returns the type model associated with the given name.
     * @param modelName The name of the type model to retrieve.
     * @return The type model, or null if the model could not be found.
     */
    public TypeModel getTypeModel(String modelName) {
        return this.m_typeModels.get(modelName);
    }

    private final Map<String,TypeModel> m_typeModels = new HashMap<String,TypeModel>();

    /**
     * Returns the first type model found.
     * @return The type model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public TypeModel getTypeModel() throws ImportException {
        Collection<String> names = getTypeModelNames();
        if (names.size() > 0) {
            return getTypeModel(names.iterator().next());
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

    private List<Message> m_messages = new ArrayList<Message>();
}
