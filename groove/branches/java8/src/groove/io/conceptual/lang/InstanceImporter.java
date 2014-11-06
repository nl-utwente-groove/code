package groove.io.conceptual.lang;

import groove.io.conceptual.Design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Abstract superclass for importers from an external format to the conceptual instance model. */
public abstract class InstanceImporter implements Messenger {
    private final Map<String,Design> m_instanceModels = new HashMap<String,Design>();
    private final List<Message> m_messages = new ArrayList<Message>();

    /** Adds an instance model to the set of imported models. */
    protected void addInstanceModel(Design model) {
        this.m_instanceModels.put(model.getName(), model);
    }

    /**
     * Returns the instance model associated with the given name. Messages may be generated during this operation.
     * @param modelName The name of the instance model to retrieve.
     * @return The instance model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public Design getInstanceModel(String modelName) {
        return this.m_instanceModels.get(modelName);
    }

    /**
     * Returns the first instance model. Messages may be generated during this operation.
     * @return The instance model, or null if the model could not be found.
     * @throws ImportException When the conversion fails, an ImportException may be thrown.
     */
    public Design getDesign() throws ImportException {
        Iterator<Design> iter = this.m_instanceModels.values().iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }
}
