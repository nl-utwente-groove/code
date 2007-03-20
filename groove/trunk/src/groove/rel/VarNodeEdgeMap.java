/* $Id: VarNodeEdgeMap.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $ */
package groove.rel;

import groove.graph.NodeEdgeMap;

/**
 * Meet of two interfaces
 * @author Arend Rensink
 * @version $Revision $
 */
public interface VarNodeEdgeMap extends NodeEdgeMap, VarMap {
	// Combines two interfaces without adding functionality
	VarNodeEdgeMap clone();
}
