package groove.io.conceptual;

import groove.io.conceptual.value.Object;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A design (previously called instance model) is the GROOVE-internal representation
 * of a "rich graph", serving as intermediate model for importing and exporting
 * GROOVE graphs from and to external formats, such as ECore.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class Design implements Serializable {
    /**
     * Create new design, based on given glossary and with the given name
     * @param gloss glossary that this design takes its concepts from
     * @param name name of this design
     */
    public Design(Glossary gloss, String name) {
        this.m_tm = gloss;
        this.m_name = name;
    }

    /**
     * Returns the glossary that this design takes its concepts from.
     */
    public Glossary getGlossary() {
        return this.m_tm;
    }

    private Glossary m_tm;

    /**
     * Returns the name of the design.
     */
    public String getName() {
        return this.m_name;
    }

    private final String m_name;

    /**
     * Adds an object to this design.
     */
    public void addObject(Object o) {
        this.m_objects.add(o);
    }

    /**
     * Returns the collection of objects in this design.
     */
    public Collection<Object> getObjects() {
        return this.m_objects;
    }

    /** The set of objects in this design. */
    private final Set<Object> m_objects = new HashSet<Object>();
}
