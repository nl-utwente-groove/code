package groove.control;

import groove.graph.Morphism;

/** Morphism between control automata. */
public class CtrlMorphism extends Morphism<CtrlState,CtrlTransition> {
    /** Creates an empty morphism. */
    public CtrlMorphism() {
        super(null);
    }
}