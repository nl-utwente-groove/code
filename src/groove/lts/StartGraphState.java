/* $Id: StartGraphState.java,v 1.7 2008-01-30 09:32:19 iovka Exp $ */
package groove.lts;

import groove.control.ControlState;
import groove.control.CtrlAut;
import groove.control.CtrlState;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.trans.SystemRecord;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class StartGraphState extends AbstractGraphState {
    /**
     * Creates a start state based on a given system record and start graph,
     * with <code>null</code> control location.
     */
    public StartGraphState(SystemRecord record, Graph graph) {
        this(record, graph, null);
    }

    /**
     * Creates a start state based on a given system record, start graph and
     * control location.
     */
    public StartGraphState(SystemRecord record, Graph graph, ControlState control) {
        super(StateReference.newInstance(record), control);
        setFrozenGraph(getCache().computeFrozenGraph(graph));
        this.graph = getCache().getGraph();
        CtrlAut ctrlAut = record.getGrammar().getCtrlAut();
        this.ctrlState =
            ctrlAut == null ? null
                    : record.getGrammar().getCtrlAut().getStart();
        GraphInfo.transfer(graph, this.graph, null);
    }

    @Override
    public Graph getGraph() {
        if (this.graph == null) {
            this.graph = getCache().getGraph();
        }
        return this.graph;
    }

    @Override
    protected void updateClosed() {
        // empty
    }

    @Override
    public CtrlState getCtrlState() {
        return this.ctrlState;
    }

    /** The stored graph. */
    private Graph graph;
    /** The (possibly {@code null} control state. */
    private final CtrlState ctrlState;
}
