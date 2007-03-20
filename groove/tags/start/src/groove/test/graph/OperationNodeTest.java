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
 * $Id: OperationNodeTest.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */

package groove.test.graph;

import groove.algebra.Algebra;
import groove.algebra.DefaultBooleanAlgebra;
import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.ValueNode;
import junit.framework.TestCase;

/**
 * @author Harmen Kastenberg
 * @version $Revision 1.0$
 * 
 * Class description.
 */
public class OperationNodeTest extends TestCase {

    ValueNode intNode, checkIntNode;
    ValueNode stringNode, checkStringNode;
    ValueNode boolNode, checkBoolNode;
    AlgebraGraph algebraGraph;
    Algebra iAlgebra, sAlgebra, bAlgebra;

    public void setUp() {
        algebraGraph = AlgebraGraph.getInstance();
        iAlgebra = algebraGraph.getAlgebra(AlgebraConstants.INTEGER);
        sAlgebra = algebraGraph.getAlgebra(AlgebraConstants.STRING);
        bAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
        intNode = new ValueNode(iAlgebra, "1");
        checkIntNode = new ValueNode(iAlgebra, "2");
        boolNode = new ValueNode(bAlgebra, DefaultBooleanAlgebra.TRUE);
        checkBoolNode = new ValueNode(bAlgebra, DefaultBooleanAlgebra.FALSE);
        stringNode = new ValueNode(sAlgebra, "Hello");
        checkStringNode = new ValueNode(sAlgebra, "Hello World!");
    }

    final public void testEquals() {
        assertEquals(true, intNode.equals(intNode));
        assertEquals(false, intNode.equals(checkIntNode));
        assertEquals(true, boolNode.equals(boolNode));
        assertEquals(false, boolNode.equals(checkBoolNode));
        assertEquals(true, stringNode.equals(stringNode));
        assertEquals(false, stringNode.equals(checkStringNode));
    }
}