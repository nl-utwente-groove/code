// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: AlgebraGraph.java,v 1.3 2007-07-21 20:07:52 rensink Exp $
 */

package groove.graph.algebra;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.DefaultIntegerAlgebra;
import groove.algebra.DefaultStringAlgebra;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Class description.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.3 $ $Date: 2007-07-21 20:07:52 $
 */
public class AlgebraGraph extends DefaultGraph {
	/** The first legal node number for algebra nodes. */
	public static final int START_NODE_NR = DefaultNode.MAX_NODE_NUMBER + 1; 

	/** The next available node number. */
	private static int nextNodeNr = START_NODE_NR;

	/** Returns the first fresh node number. */
	public static int getNextNodeNr() {
		int result = nextNodeNr;
		nextNodeNr++;
		return result;
	}

	/**
	 * mapping from objects standing for algebra values to the node representing that specific value.
	 */
	private Map<Object,ValueNode> constantToNodeMap = new HashMap<Object,ValueNode>();

	/**
	 * variable holding the singleton {@link groove.graph.algebra.AlgebraGraph}-instance
	 */
	private static AlgebraGraph algebraGraph = null;

	/**
	 * The constructor may not be accessible for other classes because of the usage
	 * of the singleton-pattern. 
	 */
	private AlgebraGraph() {
		// empty constructor
	}

	/**
	 * Facilitates the singleton-pattern.
	 * @return the only {@link groove.graph.algebra.AlgebraGraph}-instance
	 */
	public static AlgebraGraph getInstance() {
		if (algebraGraph == null)
			algebraGraph = new AlgebraGraph();
		return algebraGraph;
	}

    /**
     * Gets the {@link ValueNode} representing a given {@link Constant}, creating it if necessary.
     * @param constant the <code>Constant</code> for which to get the <code>ValueNode</code>
     * @return the unique <code>ValueNode</code> for the given constant.
     */
    public ValueNode getValueNode(Constant constant) {
        return getValueNode(constant.algebra(), constant.getValue());
    }

    /**
     * Gets the {@link ValueNode} representing a given algebra value, creating it if necessary.
     * @param algebra the algebra in which the value occurs.
     * @param value the <code>Constant</code> for which to get the <code>ValueNode</code>
     * 
     * @return the only <code>ValueNode</code>
     */
    public ValueNode getValueNode(Algebra algebra, Object value) {
        ValueNode result;
        if (constantToNodeMap.containsKey(value)) {
            result = constantToNodeMap.get(value);
        }
        else {
            result = createValueNode(algebra, value);
            constantToNodeMap.put(value, result);
        }
        return result;
    }

    /** Creates a value node for a given value of a given algebra. */
    protected ValueNode createValueNode(Algebra algebra, Object value) {
    	return new ValueNode(algebra, value);
    }

	/**
     * Returns the algebra that corresponds to the given type.
     * 
     * @param type the type of the algebra to be returned
     * @return the algebra of the given type
     */
    public Algebra getAlgebra(int type) {
    	switch (type) {
    		case AlgebraConstants.INTEGER:
    			return DefaultIntegerAlgebra.getInstance();
    		case AlgebraConstants.STRING:
    			return DefaultStringAlgebra.getInstance();
    		case AlgebraConstants.BOOLEAN:
    			return DefaultBooleanAlgebra.getInstance();
    		default:
    			return null;
    	}
    }
}
