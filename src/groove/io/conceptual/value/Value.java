package groove.io.conceptual.value;

import groove.io.conceptual.Acceptor;
import groove.io.conceptual.type.Type;

public abstract class Value implements Acceptor {
    protected Type m_type;
    
    public Value(Type type) {
        m_type = type;
    }
    
    public Type getType() {
        return m_type;
    }
}
