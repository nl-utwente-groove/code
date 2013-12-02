/* $Id: StartGraphState.java,v 1.7 2008-01-30 09:32:19 iovka Exp $ */
package groove.lts;

import groove.control.CtrlAut;
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
        CtrlAut ctrlAut = gts.getGrammar().getCtrlAut();
        setCtrlState(ctrlAut.getStart());
        setFrozenGraph(getCache().computeFrozenGraph(graph));
        this.graph = getCache().getGraph();
    }

    @Override
    public DeltaHostGraph getGraph() {
        return this.graph;
    }

    @Override
    protected void updateClosed() {
        // empty
    }

    /** The stored graph. */
    private final DeltaHostGraph graph;
}
