/* $Id$ */
package groove.lts;

import groove.control.instance.Automaton;
import groove.grammar.host.DeltaHostGraph;
import groove.grammar.host.HostGraph;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class StartGraphState extends AbstractGraphState {
    /**
     * Creates a start state based on a given system record and start graph.
     */
    public StartGraphState(GTS gts, HostGraph graph) {
        super(StateReference.newInstance(gts), 0);
        Automaton aut = gts.getGrammar().getControl();
        setFrame(aut.getStart());
        setFrozenGraph(getCache().computeFrozenGraph(graph));
        this.graph = getCache().getGraph();
    }

    @Override
    public DeltaHostGraph getGraph() {
        return this.graph;
    }

    /** The stored graph. */
    private final DeltaHostGraph graph;
}
