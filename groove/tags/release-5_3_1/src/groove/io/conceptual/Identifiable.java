package groove.io.conceptual;

import java.io.Serializable;

/** Interface for elements that have an Id */
public interface Identifiable extends Serializable {
    /**
     * Get the Id of this element
     * @return This elements' Id
     */
    public Id getId();
}
