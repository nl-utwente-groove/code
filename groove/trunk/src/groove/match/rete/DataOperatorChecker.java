/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.match.rete;

import groove.algebra.AlgebraFamily;
import groove.algebra.Operation;
import groove.algebra.Operator;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.match.rete.LookupEntry.Role;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.trans.HostElement;
import groove.trans.RuleElement;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * The checker n-node that corresponds with a data operator applied to 
 * data attributes in a graph 
 * @author Arash Jalali
 * @version $Revision $
 */
public class DataOperatorChecker extends ReteNetworkNode {

    private RuleElement[] pattern;
    private Operator operator;
    private Operation operation;
    private boolean dataCreator = false;
    private List<LookupEntry> argumentLocator = new ArrayList<LookupEntry>();

    /**
     * Creates a data operator checker that takes a match from its antecedent
     * and produces a match (if possible) that the given operation
     * (see the <code>opEdge</code>) is performed on the proper value nodes
     * in the given match.
     * 
     * @param network The owner RETE network
     * @param antecedent The static mapping of the antecedent
     * @param opEdge the edge with the operator label that represents  
     *               the operation this checker node should perform.
     */
    public DataOperatorChecker(ReteNetwork network,
            ReteStaticMapping antecedent, OperatorEdge opEdge) {
        super(network);
        assert antecedent.getLhsNodes().containsAll(
            opEdge.source().getArguments());
        this.operator = opEdge.getOperator();
        this.operation =
            AlgebraFamily.getInstance().getOperation(this.operator);
        this.dataCreator =
            !antecedent.getLhsNodes().contains(opEdge.target())
                && (opEdge.target().getConstant() == null);
        this.addAntecedent(antecedent.getNNode());
        antecedent.getNNode().addSuccessor(this);
        adjustPattern(opEdge);
        for (VariableNode vn : opEdge.source().getArguments()) {
            this.argumentLocator.add(antecedent.locateNode(vn));
        }
    }

    private void adjustPattern(OperatorEdge opEdge) {
        ReteNetworkNode antecedent = getAntecedents().get(0);
        RuleElement[] antecedentPattern = antecedent.getPattern();
        this.pattern = new RuleElement[antecedent.getPattern().length + 1];
        for (int i = 0; i < antecedentPattern.length; i++) {
            this.pattern[i] = antecedentPattern[i];
        }
        this.pattern[this.pattern.length - 1] = opEdge.target();
    }

    /**
     * Determines if this operator node produces data nodes or just
     * checks an algebraic relationship between the nodes in the 
     * match coming from its antecedent.
     */
    public boolean isDataCreator() {
        return this.dataCreator;
    }

    @Override
    public int demandOneMatch() {
        // TODO ARASH:implement on-demand
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        // TODO ARASH:implement on-demand
        return false;
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    public int size() {
        return this.pattern.length;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch subgraph) {
        Object[] matchUnits = subgraph.getAllUnits();
        //
        List<Object> arguments = new ArrayList<Object>();
        for (int i = 0; i < this.argumentLocator.size(); i++) {
            LookupEntry entry = this.argumentLocator.get(i);
            ValueNode vn = (ValueNode) entry.lookup(matchUnits);
            arguments.add(vn.getValue());
        }

        Object outcome = this.operation.apply(arguments);
        VariableNode opResultVarNode =
            ((VariableNode) this.pattern[this.pattern.length - 1]);
        ValueNode resultValueNode = null;
        boolean passDown = false;

        if (this.isDataCreator()) {
            passDown = true;
            resultValueNode =
                this.getOwner().getHostFactory().createValueNode(
                    this.operation.getResultAlgebra(), outcome);
        } else if (opResultVarNode.getConstant() != null) {
            resultValueNode =
                this.getOwner().getHostFactory().createValueNode(
                    this.operation.getResultAlgebra(), outcome);
            passDown =
                this.operation.getResultAlgebra().getSymbol(outcome).equals(
                    opResultVarNode.getConstant().getSymbol());
        } else {
            LookupEntry entry =
                this.getAntecedents().get(0).getPatternLookupTable().getNode(
                    (RuleNode) this.pattern[this.pattern.length - 1]);
            ValueNode n = (ValueNode) entry.lookup(matchUnits);
            if (n.getValue().equals(outcome)) {
                resultValueNode = n;
                passDown = true;
            }
        }
        if (passDown) {
            ReteSimpleMatch m =
                new ReteSimpleMatch(this, this.getOwner().isInjective(),
                    subgraph, new HostElement[] {resultValueNode});
            passDownMatchToSuccessors(m);
        }
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return this == node;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("- Data Operator Checker%s\n",
            this.isDataCreator() ? "(creator)" : ""));
        sb.append(String.format("- Operator - %s\n", this.operator.toString()));
        sb.append("---  Pattern-\n");
        for (int i = 0; i < this.pattern.length; i++) {
            sb.append(":" + "--- " + i + " -" + this.pattern[i].toString()
                + "\n");
        }
        for (int i = 0; i < this.argumentLocator.size(); i++) {
            LookupEntry entry = this.argumentLocator.get(i);
            if (entry.getRole() != Role.NODE) {
                sb.append(String.format("-- argument[%d]=element[%d]%s\n", i,
                    entry.getPos(), entry.getRole() == Role.SOURCE ? ".source"
                            : ".target"));
            } else {
                sb.append(String.format("-- argument[%d]=element[%d]\n", i,
                    entry.getPos()));
            }
        }
        return sb.toString();
    }
}
