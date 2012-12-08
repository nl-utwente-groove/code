package groove.io.conceptual.lang;

import groove.io.conceptual.InstanceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InstanceImporter implements Messenger {
    protected Map<String,InstanceModel> m_instanceModels = new HashMap<String,InstanceModel>();
    private List<Message> m_messages = new ArrayList<Message>();

    /**
     * Returns a collection of strings representing each loaded instance model. Use {@link InstanceImporter#getInstanceModel(String)} to retrieve the actual
     * associated type model.
     * @return A collection of strings representing each instance model
     */
    public Collection<String> getInstanceModelNames() {
        return m_instanceModels.keySet();
    }

    /**
     * Returns the instance model associated with the given name. Messages may be generated during this operation.
     * @param modelName The name of the instance model to retrieve.
     * @return The instance model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public abstract InstanceModel getInstanceModel(String modelName) throws ImportException;

    /**
     * Returns the first instance model. Messages may be generated during this operation.
     * @return The instance model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public InstanceModel getInstanceModel() throws ImportException {
        Collection<String> names = getInstanceModelNames();
        if (names.size() > 0) {
            return getInstanceModel(names.iterator().next());
        }
        return null;
    }

    protected void addMessage(Message m) {
        m_messages.add(m);
    }

    public List<Message> getMessages() {
        return m_messages;
    }

    public void clearMessages() {
        m_messages.clear();
    }
}
