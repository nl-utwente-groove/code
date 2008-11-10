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
 * $Id: OperationNodeTest.java,v 1.4 2008-01-30 09:32:47 iovka Exp $
 */

package groove.test.graph;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.UnknownSymbolException;
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

    @Override
    public void setUp() {
        this.algebraGraph = AlgebraGraph.getInstance();
        this.iAlgebra = this.algebraGraph.getAlgebra(AlgebraConstants.INTEGER);
        this.sAlgebra = this.algebraGraph.getAlgebra(AlgebraConstants.STRING);
        this.bAlgebra = this.algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
        try {
            this.intNode =
                this.algebraGraph.getValueNode((Constant) this.iAlgebra.getOperation("1"));
            this.checkIntNode =
                this.algebraGraph.getValueNode((Constant) this.iAlgebra.getOperation("2"));
            this.boolNode =
                this.algebraGraph.getValueNode((Constant) this.bAlgebra.getOperation(DefaultBooleanAlgebra.TRUE));
            this.checkBoolNode =
                this.algebraGraph.getValueNode((Constant) this.bAlgebra.getOperation(DefaultBooleanAlgebra.FALSE));
            this.stringNode =
                this.algebraGraph.getValueNode((Constant) this.sAlgebra.getOperation("\"Hello\""));
            this.checkStringNode =
                this.algebraGraph.getValueNode((Constant) this.sAlgebra.getOperation("\"Hello World!\""));
        } catch (UnknownSymbolException exc) {
            exc.printStackTrace();
        }
    }

    final public void testEquals() {
        assertEquals(true, this.intNode.equals(this.intNode));
        assertEquals(false, this.intNode.equals(this.checkIntNode));
        assertEquals(true, this.boolNode.equals(this.boolNode));
        assertEquals(false, this.boolNode.equals(this.checkBoolNode));
        assertEquals(true, this.stringNode.equals(this.stringNode));
        assertEquals(false, this.stringNode.equals(this.checkStringNode));
    }
}