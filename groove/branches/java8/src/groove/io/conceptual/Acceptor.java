package groove.io.conceptual;

import java.io.Serializable;

/**
 * Accept interface for visitor pattern (See Visitor)
 * @author Harold Bruijntjes
 *
 */
public interface Acceptor extends Serializable {
    /**
     * Callback the visitor with the given parameter
     * @param v The visitor to visit
     * @param param string that may be passed along as a parameter
     * @return true if visited successfully
     */
    public boolean doVisit(Visitor v, String param);
}
