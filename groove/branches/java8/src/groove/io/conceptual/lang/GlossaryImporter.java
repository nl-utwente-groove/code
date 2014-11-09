package groove.io.conceptual.lang;

import groove.io.conceptual.Glossary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Abstract class wrapping the functionality to create a design type from another format. */
public abstract class GlossaryImporter implements Messenger {
    /**
     * Returns a collection of strings representing each loaded type model.
     * Use {@link GlossaryImporter#getGlossary(String)} to retrieve the actual associated type
     * model.
     * @return A collection of strings representing each type model
     */
    public Collection<String> getGlossaryNames() {
        return this.m_glossaries.keySet();
    }

    /** Inserts a named type model into this importer. */
    public void putGlossary(String name, Glossary typeModel) {
        this.m_glossaries.put(name, typeModel);
    }

    /**
     * Returns the type model associated with the given name.
     * @param name The name of the type model to retrieve.
     * @return The glossary, or null if it could not be found.
     */
    public Glossary getGlossary(String name) {
        return this.m_glossaries.get(name);
    }

    private final Map<String,Glossary> m_glossaries = new HashMap<>();

    /**
     * Returns the first glossary found.
     * @return The glossary, or null if none not be found.
     */
    public Glossary getGlossary() {
        Collection<String> names = getGlossaryNames();
        if (names.size() > 0) {
            return getGlossary(names.iterator().next());
        }
        return null;
    }

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }

    private List<Message> m_messages = new ArrayList<Message>();
}
