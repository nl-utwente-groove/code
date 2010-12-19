/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: Bisimulator.java,v 1.16 2007-11-02 08:42:38 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.util.TreeHashSet;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements an algorithm to partition a given graph into sets of symmetric
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are predicted to be
 * symmetric if they map to the same (i.e., <tt>equal</tt>) certificate. This
 * strategy goes beyond bisimulation in that it breaks all apparent symmetries
 * in all possible ways and accumulates the results.
 * @author Arend Rensink
 * @version $Revision: 1529 $
 */
public class PartitionRefiner<N extends Node,L extends Label,E extends Edge>
        extends CertificateStrategy<N,L,E> {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * The strategy checks for isomorphism weakly, meaning that it might yield
     * false negatives.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public PartitionRefiner(Graph<N,L,E> graph) {
        this(graph, false);
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     * @param strong if <code>true</code>, the strategy puts more effort into
     *        getting distinct certificates.
     */
    public PartitionRefiner(Graph<N,L,E> graph, boolean strong) {
        super(graph);
        this.strong = strong;
    }

    @Override
    public <N1 extends Node,L1 extends Label,E1 extends Edge> CertificateStrategy<N1,L1,E1> newInstance(
            Graph<N1,L1,E1> graph, boolean strong) {
        return new PartitionRefiner<N1,L1,E1>(graph, strong);
    }

    /**
     * This method only returns a useful result after the graph certificate or
     * partition map has been calculated.
     */
    @Override
    public int getNodePartitionCount() {
        if (this.nodePartitionCount == 0) {
            computeCertificates();
        }
        return this.nodePartitionCount;
    }

    /** Right now only a strong strategy is implemented. */
    @Override
    public boolean getStrength() {
        return true;
    }

    @Override
    void iterateCertificates() {
        iterateCertificates1();
        iterateCertificates2();
    }

    /**
     * Iterates node certificates until this results in a stable partitioning.
     */
    private void iterateCertificates1() {
        // get local copies of attributes for speedup
        int nodeCertCount = this.nodeCertCount;
        // collect and then count the number of certificates
        boolean goOn;
        do {
            int oldPartitionCount = this.nodePartitionCount;
            // first compute the new edge certificates
            advanceEdgeCerts();
            advanceNodeCerts(this.iterateCount > 0
                && this.nodePartitionCount < nodeCertCount);
            // we stop the iteration when the number of partitions has not grown
            // moreover, when the number of partitions equals the number of
            // nodes then
            // it cannot grow, so we might as well stop straight away
            if (this.iterateCount == 0) {
                goOn = true;
            } else {
                goOn = this.nodePartitionCount > oldPartitionCount;
            }
            this.iterateCount++;
        } while (goOn);
        recordIterateCount(this.iterateCount);
        if (TRACE) {
            System.out.printf(
                "First iteration done; %d partitions for %d nodes in %d iterations%n",
                this.nodePartitionCount, this.nodeCertCount, this.iterateCount);
        }
    }

    /** Computes the node and edge certificate arrays. */
    private void iterateCertificates2() {
        if ((this.strong || BREAK_DUPLICATES)
            && this.nodePartitionCount < this.nodeCertCount) {
            // now look for smallest unbroken duplicate certificate (if any)
            int oldPartitionCount;
            do {
                oldPartitionCount = this.nodePartitionCount;
                List<MyNodeCert<N>> duplicates = getSmallestDuplicates();
                if (duplicates.isEmpty()) {
                    if (TRACE) {
                        System.out.printf("All duplicate certificates broken%n");
                    }
                    break;
                }
                checkpointCertificates();
                // successively break the symmetry at each of these
                for (MyNodeCert<N> duplicate : duplicates) {
                    duplicate.breakSymmetry();
                    iterateCertificates1();
                    rollBackCertificates();
                    this.nodePartitionCount = oldPartitionCount;
                }
                accumulateCertificates();
                // calculate the edge and node certificates once more
                // to push out the accumulated node values and get the correct
                // node partition count
                advanceEdgeCerts();
                advanceNodeCerts(true);
                if (TRACE) {
                    System.out.printf(
                        "Next iteration done; %d partitions for %d nodes in %d iterations%n",
                        this.nodePartitionCount, this.nodeCertCount,
                        this.iterateCount);
                }
            } while (true);// this.nodePartitionCount < this.nodeCertCount &&
            // this.nodePartitionCount > oldPartitionCount);
        }
        // so far we have done nothing with the self-edges, so
        // give them a chance to get their value right
        int edgeCount = this.edgeCerts.length;
        for (int i = this.edge2CertCount; i < edgeCount; i++) {
            ((MyEdge1Cert<?>) this.edgeCerts[i]).setNewValue();
        }
    }

    /**
     * Calls {@link MyCert#setNewValue()} on all edge certificates.
     */
    private void advanceEdgeCerts() {
        for (int i = 0; i < this.edge2CertCount; i++) {
            MyEdge2Cert<?> edgeCert = (MyEdge2Cert<?>) this.edgeCerts[i];
            this.graphCertificate += edgeCert.setNewValue();
        }
    }

    /**
     * Calls {@link MyCert#setNewValue()} on all node certificates. Also
     * calculates the certificate store on demand.
     * @param store if <code>true</code>, {@link #certStore} and
     *        {@link #nodePartitionCount} are recalculated
     */
    private void advanceNodeCerts(boolean store) {
        certStore.clear();
        for (int i = 0; i < this.nodeCertCount; i++) {
            MyNodeCert<?> nodeCert = (MyNodeCert<?>) this.nodeCerts[i];
            this.graphCertificate += nodeCert.setNewValue();
            if (store) {
                MyNodeCert<?> oldCertForValue = certStore.put(nodeCert);
                if (!nodeCert.isSingular()) {
                    if (oldCertForValue == null) {
                        // assume this certificate is singular
                        nodeCert.setSingular(this.iterateCount);
                    } else {
                        // the original certificate was not singular
                        oldCertForValue.setSingular(0);
                    }
                }
            }
        }
        if (store) {
            this.nodePartitionCount = certStore.size();
        }
    }

    /**
     * Calls {@link MyCert#setCheckpoint()} on all node and edge
     * certificates.
     */
    private void checkpointCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyCert<?> nodeCert = (MyCert<?>) this.nodeCerts[i];
            nodeCert.setCheckpoint();
        }
        for (int i = 0; i < this.edge2CertCount; i++) {
            MyCert<?> edgeCert = (MyCert<?>) this.edgeCerts[i];
            edgeCert.setCheckpoint();
        }
    }

    /** Calls {@link MyCert#rollBack()} on all node and edge certificates. */
    private void rollBackCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyCert<?> nodeCert = (MyCert<?>) this.nodeCerts[i];
            nodeCert.rollBack();
        }
        for (int i = 0; i < this.edge2CertCount; i++) {
            MyCert<?> edgeCert = (MyCert<?>) this.edgeCerts[i];
            edgeCert.rollBack();
        }
    }

    /**
     * Calls {@link MyCert#accumulate(int)} on all node and edge
     * certificates.
     */
    private void accumulateCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyCert<?> nodeCert = (MyCert<?>) this.nodeCerts[i];
            nodeCert.accumulate(this.iterateCount);
        }
        for (int i = 0; i < this.edge2CertCount; i++) {
            MyCert<?> edgeCert = (MyCert<?>) this.edgeCerts[i];
            edgeCert.accumulate(this.iterateCount);
        }
    }

    /** Returns the list of duplicate certificates with the smallest value. */
    private List<MyNodeCert<N>> getSmallestDuplicates() {
        List<MyNodeCert<N>> result = new LinkedList<MyNodeCert<N>>();
        MyNodeCert<N> minCert = null;
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert<N> cert = (MyNodeCert<N>) this.nodeCerts[i];
            if (!cert.isSingular()) {
                if (minCert == null) {
                    minCert = cert;
                    result.add(cert);
                } else if (cert.getValue() < minCert.getValue()) {
                    minCert = cert;
                    result.clear();
                    result.add(cert);
                } else if (cert.getValue() == minCert.getValue()) {
                    result.add(cert);
                }
            }
        }
        assert result.size() != 1;
        return result;
    }

    @Override
    NodeCertificate<N> createValueNodeCertificate(ValueNode node) {
        return new MyValueNodeCert<N>(node);
    }

    @Override
    MyNodeCert<N> createNodeCertificate(N node) {
        return new MyNodeCert<N>(node);
    }

    @Override
    MyEdge1Cert<E> createEdge1Certificate(E edge, NodeCertificate<N> source) {
        return new MyEdge1Cert<E>(edge, (MyNodeCert<N>) source);
    }

    @Override
    MyEdge2Cert<E> createEdge2Certificate(E edge, NodeCertificate<N> source,
            NodeCertificate<N> target) {
        return new MyEdge2Cert<E>(edge, (MyNodeCert<N>) source,
            (MyNodeCert<N>) target);
    }

    /**
     * Flag to indicate that more effort should be put into obtaining distinct
     * certificates.
     */
    private final boolean strong;
    /**
     * The number of pre-computed node partitions.
     */
    private int nodePartitionCount;
    /** Total number of iterations in {@link #iterateCertificates()}. */
    private int iterateCount;

    /**
     * Returns the total number of times symmetry was broken during the
     * calculation of the certificates.
     */
    static public int getSymmetryBreakCount() {
        return totalSymmetryBreakCount;
    }

    /**
     * The resolution of the tree-based certificate store.
     */
    static private final int TREE_RESOLUTION = 3;
    /**
     * Store for node certificates, to count the number of partitions
     */
    static private final TreeHashSet<MyNodeCert<?>> certStore =
        new TreeHashSet<MyNodeCert<?>>(TREE_RESOLUTION) {
            /**
             * For the purpose of this set, only the certificate value is of
             * importance.
             */
            @Override
            protected boolean allEqual() {
                return true;
            }

            @Override
            protected int getCode(MyNodeCert<?> key) {
                return key.getValue();
            }
        };

    /** Debug flag to switch the use of duplicate breaking on and off. */
    static private final boolean BREAK_DUPLICATES = true;
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;

    /**
     * Superclass of graph element certificates.
     */
    public static abstract class MyCert<E extends Element> implements
            CertificateStrategy.Certificate<E> {
        /** Constructs a certificate for a given graph element. */
        MyCert(E element) {
            this.element = element;
        }

        /**
         * Returns the certificate value. Note that this means the hash code is
         * not constant during the initial phase, and so no hash sets or maps
         * should be used.
         * @ensure <tt>result == getValue()</tt>
         * @see #getValue()
         */
        @Override
        public int hashCode() {
            return this.value;
        }

        /**
         * Tests if the other is a {@link PartitionRefiner.MyCert} with the same value.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof MyCert<?>
                && (this.value == ((MyCert<?>) obj).value);
        }

        /**
         * Returns the current certificate value.
         */
        final public int getValue() {
            return this.value;
        }

        /**
         * Computes, stores and returns a new value for this certificate. The
         * computation is done by invoking {@link #computeNewValue()}.
         * @return the freshly computed new value
         * @see #computeNewValue()
         */
        protected int setNewValue() {
            return this.value = computeNewValue();
        }

        /**
         * Callback method that provides the new value at each iteration.
         * @return the freshly computed new value
         * @see #setNewValue()
         */
        abstract protected int computeNewValue();

        /** Returns the element of which this is a certificate. */
        public E getElement() {
            return this.element;
        }

        /**
         * Sets a checkpoint that we can later roll back to.
         */
        public void setCheckpoint() {
            this.checkpointValue = this.value;
        }

        /**
         * Rolls back the value to that frozen at the latest checkpoint.
         */
        public void rollBack() {
            this.cumulativeValue += this.value;
            this.value = this.checkpointValue;
        }

        /**
         * Combines the accumulated intermediate values collected at rollback,
         * and adds them to the actual value.
         * @param round the iteration round
         */
        public void accumulate(int round) {
            this.value += this.cumulativeValue;
            this.cumulativeValue = 0;
        }

        /** The current value, which determines the hash code. */
        protected int value;
        /** The value as frozen at the last call of {@link #setCheckpoint()}. */
        private int checkpointValue;
        /**
         * The cumulative values as calculated during the {@link #rollBack()}s
         * after the last {@link #setCheckpoint()}.
         */
        private int cumulativeValue;
        /** The element for which this is a certificate. */
        private final E element;
    }

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision: 1529 $
     */
    static class MyNodeCert<N extends Node> extends MyCert<N> implements
            CertificateStrategy.NodeCertificate<N> {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyNodeCert(N node) {
            super(node);
            this.value = INIT_NODE_VALUE;
        }

        @Override
        public String toString() {
            return "c" + this.value;
        }

        /**
         * Returns <tt>true</tt> of <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyNodeCert} and has the same value as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            // if (obj instanceof NodeCertificate && this.singularRound ==
            // ((NodeCertificate) obj).singularRound) {
            // if (this.singularRound == 0) {
            return obj instanceof MyNodeCert
                && this.value == ((MyNodeCert<?>) obj).value;
            // } else {
            // return this.singularValue == ((NodeCertificate)
            // obj).singularValue
            // && this.singularRound == ((NodeCertificate) obj).singularRound;
            // }
            // } else {
            // return false;
            // }
        }

        //
        // @Override
        // public int hashCode() {
        // if (this.singularRound == 0) {
        // return super.hashCode();
        // } else {
        // return this.singularValue + this.singularRound;
        // }
        // }

        /**
         * Change the certificate value predictably to break symmetry.
         */
        public void breakSymmetry() {
            this.value ^= this.value << 5 ^ this.value >> 3;
        }

        /**
         * The new value for this certificate node is the sum of the values of
         * the incident certificate edges.
         */
        @Override
        protected int computeNewValue() {
            int result = this.nextValue ^ this.value;
            this.nextValue = 0;
            return result;
        }

        /**
         * Adds to the current value. Used during construction, to record the
         * initial value of incident edges.
         */
        protected void addValue(int inc) {
            this.value += inc;
        }

        /**
         * Adds a certain value to {@link #nextValue}.
         */
        protected void addNextValue(int value) {
            this.nextValue += value;
        }

        /**
         * Signals that the certificate has become singular at a certain round.
         * @param round the round at which the certificate is set to singular;
         *        if <code>0</code>, it is still duplicate.
         */
        protected void setSingular(int round) {
            this.singular = round > 0;
            // this.singularRound = round;
            // this.singularValue = getValue();
        }

        /**
         * Signals if the certificate is singular or duplicate.
         */
        protected boolean isSingular() {
            return this.singular;
            // return this.singularRound > 0;
        }

        //        
        // /** We also have to checkpoint the singularity information. */
        // @Override
        // public void setCheckpoint() {
        // super.setCheckpoint();
        // this.checkpointSingularRound = this.singularRound;
        // this.checkpointSingularValue = this.singularValue;
        // }
        //
        // /** We also have to roll back the singularity information. */
        // @Override
        // public void rollBack() {
        // super.rollBack();
        // if (this.cumulativeSingularRound == 0) {
        // this.cumulativeSingularRound = this.singularRound;
        // }
        // this.singularRound = this.checkpointSingularRound;
        // this.singularValue = this.checkpointSingularValue;
        // }
        //
        // @Override
        // public void accumulate(int round) {
        // super.accumulate(round);
        // this.singularRound = this.cumulativeSingularRound;
        // }

        /** The value for the next invocation of {@link #computeNewValue()} */
        int nextValue;
        /**
         * Records if the certificate has become singular at some point of the
         * calculation.
         */
        boolean singular;
        // /**
        // * Round at which the certificate has been set to singular; if
        // <code>0</code>,
        // * it is duplicate.
        // */
        // private int singularRound;
        // /**
        // * Frozen certificate value when the certificate was set to singular.
        // * If the certificate is singular, this is the value that will be used
        // * as a criterion for equality.
        // */
        // private int singularValue;
        // /** The value of {@link #singularRound} as frozen at the last
        // checkpoint. */
        // private int checkpointSingularRound;
        // /** The value of {@link #singularValue} as frozen at the last
        // checkpoint. */
        // private int checkpointSingularValue;
        // /** Stores the first round in which the certificate became singuler
        // (if any). */
        // private int cumulativeSingularRound;
    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class MyValueNodeCert<N extends Node> extends MyNodeCert<N> {
        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        @SuppressWarnings("unchecked")
        public MyValueNodeCert(ValueNode node) {
            super((N) node);
            this.node = node;
            this.value = node.getNumber();
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyValueNodeCert} and has the same node as this one.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof MyValueNodeCert
                && this.node.equals(((MyValueNodeCert<?>) obj).node);
        }

        /**
         * The new value for this certificate node is the sum of the values of
         * the incident certificate edges.
         */
        @Override
        protected int computeNewValue() {
            int result = this.nextValue ^ this.value;
            this.nextValue = 0;
            return result;
        }

        private final ValueNode node;
    }

    /**
     * An edge with certificate nodes as endpoints. The hash code is computed
     * dynamically, on the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision: 1529 $
     */
    static class MyEdge2Cert<E extends Edge> extends MyCert<E> implements
            EdgeCertificate<E> {
        /**
         * Constructs a certificate for a binary edge.
         * @param edge The target certificate node
         * @param source The source certificate node
         * @param target The label of the original edge
         */
        public MyEdge2Cert(E edge, MyNodeCert<? extends Node> source,
                MyNodeCert<? extends Node> target) {
            super(edge);
            this.source = source;
            this.target = target;
            this.labelIndex = edge.label().hashCode();
            initValue();
            source.addValue(this.value);
            target.addValue(this.value << 1);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "("
                + this.labelIndex + ")," + this.target + "]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyEdge2Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyEdge2Cert) {
                MyEdge2Cert<?> other = (MyEdge2Cert<?>) obj;
                if (this.value != other.value
                    || this.labelIndex != other.labelIndex
                    || !this.source.equals(other.source)) {
                    return false;
                } else if (this.target == this.source) {
                    return other.target == other.source;
                } else {
                    return this.target.equals(other.target);
                }
            } else {
                return false;
            }
        }

        //
        // @Override
        // protected int setNewValue() {
        // int sourceFrozen = this.source.isFrozen();
        // int targetFrozen = this.target.isFrozen();
        // if (sourceFrozen > 0 && targetFrozen > 0) {
        // setFrozen(Math.max(sourceFrozen, targetFrozen));
        // return getValue();
        // } else {
        // return super.setNewValue();
        // }
        // }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int targetShift = (this.labelIndex & 0xf) + 1;
            int sourceHashCode = this.source.value;
            int targetHashCode = this.target.value;
            int result =
                ((sourceHashCode << 8) | (sourceHashCode >>> 24))
                    + ((targetHashCode << targetShift) | (targetHashCode >>> targetShift))
                    + this.value;
            this.source.nextValue += 2 * result;
            this.target.nextValue -= 3 * result;
            return result;
        }

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.labelIndex;
        }

        /** The source certificate for the edge. */
        private final MyNodeCert<?> source;
        /** The target certificate for the edge; may be <tt>null</tt>. */
        private final MyNodeCert<?> target;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }

    /**
     * An edge with only one endpoint. The hash code is computed dynamically, on
     * the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision: 1529 $
     */
    static class MyEdge1Cert<E extends Edge> extends MyCert<E> implements
            EdgeCertificate<E> {
        /** Constructs a certificate edge for a predicate (i.e., a unary edge). */
        public MyEdge1Cert(E edge, MyNodeCert<?> source) {
            super(edge);
            this.source = source;
            this.labelIndex = edge.label().hashCode();
            initValue();
            source.addValue(this.value);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "("
                + this.labelIndex + ")]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyEdge1Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyEdge1Cert) {
                MyEdge1Cert<?> other = (MyEdge1Cert<?>) obj;
                return (this.value == other.value && this.labelIndex == other.labelIndex);
            } else {
                return false;
            }
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int sourceHashCode = this.source.hashCode();
            int result =
                (sourceHashCode << 8) + (sourceHashCode >> 24) + this.value;
            // source.nextValue += result;
            return result;
        }

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.labelIndex << 4;
        }

        /** The source certificate for the edge. */
        private final MyNodeCert<?> source;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }
}
