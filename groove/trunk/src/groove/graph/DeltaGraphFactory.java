/* $Id: DeltaGraphFactory.java,v 1.1 2007-04-27 22:07:04 rensink Exp $ */
package groove.graph;

/**
 * Extension of the {@link Graph} interface with a method to create
 * new graphs using a delta applier.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface DeltaGraphFactory<G extends Graph> {
	/** Creates a new graph from this graph by applying a delts to the current graph. */
	G newGraph(G basis, DeltaApplier applier);
}
