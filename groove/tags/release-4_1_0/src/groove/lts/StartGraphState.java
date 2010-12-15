/* $Id: StartGraphState.java,v 1.7 2008-01-30 09:32:19 iovka Exp $ */
package groove.lts;

import groove.control.CtrlAut;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.trans.DeltaHostGraph;
import groove.trans.SystemRecord;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class StartGraphState extends AbstractGraphState {
    /**
     * Creates a start state based on a given system record and start graph
     */
    public StartGraphState(SystemRecord record, Graph graph) {
        super(StateReference.newInstance(record));
        setFrozenGraph(getCache().computeFrozenGraph(graph));
        this.graph = getCache().getGraph();
        CtrlAut ctrlAut = record.getGrammar().getCtrlAut();
        if (ctrlAut != null) {
            setCtrlState(ctrlAut.getStart());
        }
        GraphInfo.transfer(graph, this.graph, null);
    }

    @Override
    public DeltaHostGraph getGraph() {
        if (this.graph == null) {
            this.graph = getCache().getGraph();
        }
        return this.graph;
    }

    @Override
    protected void updateClosed() {
        // empty
    }

    /** The stored graph. */
    private DeltaHostGraph graph;
}
