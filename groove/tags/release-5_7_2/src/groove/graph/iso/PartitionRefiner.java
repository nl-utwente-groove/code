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
 * $Id$
 */
package groove.graph.iso;

import java.util.LinkedList;
import java.util.List;

import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.collect.TreeHashSet;

/**
 * Implements an algorithm to partition a given graph into sets of symmetric
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are predicted to be
 * symmetric if they map to the same (i.e., <tt>equal</tt>) certificate. This
 * strategy goes beyond bisimulation in that it breaks all apparent symmetries
 * in all possible ways and accumulates the results.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PartitionRefiner extends CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * The strategy checks for isomorphism weakly, meaning that it might yield
     * false negatives.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public PartitionRefiner(Graph graph) {
        this(graph, false);
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     * @param strong if <code>true</code>, the strategy puts more effort into
     *        getting distinct certificates.
     */
    public PartitionRefiner(Graph graph, boolean strong) {
        super(graph);
        this.strong = strong;
    }

    @Override
    public CertificateStrategy newInstance(Graph graph, boolean strong) {
        return new PartitionRefiner(graph, strong);
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
        resizeTmpCertIxs();
        // get local copies of attributes for speedup
        int nodeCertCount = this.nodeCertCount;
        // collect and then count the number of certificates
        boolean goOn;
        do {
            int oldPartitionCount = this.nodePartitionCount;
            // first compute the new edge certificates
            advanceEdgeCerts();
            advanceNodeCerts(this.iterateCount > 0 && this.nodePartitionCount < nodeCertCount);
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
            System.out.printf("First iteration done; %d partitions for %d nodes in %d iterations%n",
                this.nodePartitionCount,
                this.nodeCertCount,
                this.iterateCount);
        }
    }

    /** Computes the node and edge certificate arrays. */
    private void iterateCertificates2() {
        if ((this.strong || BREAK_DUPLICATES) && this.nodePartitionCount < this.nodeCertCount) {
            resizeTmpCertIxs();
            // now look for smallest unbroken duplicate certificate (if any)
            int oldPartitionCount;
            do {
                oldPartitionCount = this.nodePartitionCount;
                List<MyNodeCert> duplicates = getSmallestDuplicates();
                if (duplicates.isEmpty()) {
                    if (TRACE) {
                        System.out.printf("All duplicate certificates broken%n");
                    }
                    // EZ: Need to increase the count here, otherwise this
                    // counter is always zero.
                    totalSymmetryBreakCount++;
                    break;
                }
                checkpointCertificates();
                // successively break the symmetry at each of these
                for (MyNodeCert duplicate : duplicates) {
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
                        this.nodePartitionCount,
                        this.nodeCertCount,
                        this.iterateCount);
                }
            } while (true);// this.nodePartitionCount < this.nodeCertCount &&
            // this.nodePartitionCount > oldPartitionCount);
        }
        // so far we have done nothing with the self-edges, so
        // give them a chance to get their value right
        int edgeCount = this.edgeCerts.length;
        for (int i = this.edge2CertCount; i < edgeCount; i++) {
            ((MyEdge1Cert) this.edgeCerts[i]).setNewValue();
        }
    }

    /** Extends the {@link #tmpCertIxs} array, if necessary. */
    private void resizeTmpCertIxs() {
        if (this.nodeCertCount > tmpCertIxs.length) {
            tmpCertIxs = new int[this.nodeCertCount + 100];
        }
    }

    /**
     * Calls {@link MyCert#setNewValue()} on all edge certificates.
     */
    private void advanceEdgeCerts() {
        for (int i = 0; i < this.edge2CertCount; i++) {
            MyEdge2Cert edgeCert = (MyEdge2Cert) this.edgeCerts[i];
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
        int tmpSize = 0;
        for (int i = 0; i < this.nodeCertCount; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            this.graphCertificate += nodeCert.setNewValue();
            if (store) {
                if (nodeCert.isSingular()) {
                    // add to the certStore later, to avoid resetting singularity
                    tmpCertIxs[tmpSize] = i;
                    tmpSize++;
                } else {
                    MyNodeCert oldCertForValue = certStore.put(nodeCert);
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
            // copy the remainder of the certificates to the store
            for (int i = 0; i < tmpSize; i++) {
                certStore.add((MyNodeCert) this.nodeCerts[tmpCertIxs[i]]);
            }
            this.nodePartitionCount = certStore.size();
            certStore.clear();
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
    private List<MyNodeCert> getSmallestDuplicates() {
        List<MyNodeCert> result = new LinkedList<>();
        MyNodeCert minCert = null;
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert cert = (MyNodeCert) this.nodeCerts[i];
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
    NodeCertificate createValueNodeCertificate(ValueNode node) {
        return new MyValueNodeCert(node);
    }

    @Override
    MyNodeCert createNodeCertificate(Node node) {
        return new MyNodeCert(node);
    }

    @Override
    MyEdge1Cert createEdge1Certificate(Edge edge, NodeCertificate source) {
        return new MyEdge1Cert(edge, (MyNodeCert) source);
    }

    @Override
    MyEdge2Cert createEdge2Certificate(Edge edge, NodeCertificate source, NodeCertificate target) {
        return new MyEdge2Cert(edge, (MyNodeCert) source, (MyNodeCert) target);
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
    static private final TreeHashSet<MyNodeCert> certStore =
        new TreeHashSet<MyNodeCert>(TREE_RESOLUTION) {
            /**
             * For the purpose of this set, only the certificate value is of
             * importance.
             */
            @Override
            protected boolean allEqual() {
                return true;
            }

            @Override
            protected int getCode(MyNodeCert key) {
                return key.getValue();
            }
        };
    /** Temporary storage for node certificates. */
    static private int[] tmpCertIxs = new int[100];

    /** Debug flag to switch the use of duplicate breaking on and off. */
    static private final boolean BREAK_DUPLICATES = true;
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;

    /**
     * Superclass of graph element certificates.
     */
    public static abstract class MyCert<E extends Element>
        implements CertificateStrategy.ElementCertificate<E> {
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
            if (this == obj) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            MyCert<?> other = ((MyCert<?>) obj);
            return this.value == other.value;
        }

        /**
         * Returns the current certificate value.
         */
        @Override
        final public int getValue() {
            return this.value;
        }

        @Override
        public void modifyValue(int mod) {
            this.value += mod;
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
        @Override
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
     * @version $Revision$
     */
    static class MyNodeCert extends MyCert<Node>implements CertificateStrategy.NodeCertificate {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyNodeCert(Node node) {
            super(node);
            if (node instanceof HostNode) {
                this.label = ((HostNode) node).getType().label();
                this.value = this.label.hashCode();
            } else {
                this.label = null;
                this.value = INIT_NODE_VALUE;
            }
        }

        /**
         * Tests if the other is a {@link PartitionRefiner.MyCert} with the same value.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (this.label == null) {
                return true;
            }
            return this.label.equals(((MyNodeCert) obj).label);
        }

        @Override
        public String toString() {
            return "c" + this.value;
        }

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
        }

        /** Possibly {@code null} node label. */
        private final Label label;
        /** The value for the next invocation of {@link #computeNewValue()} */
        int nextValue;
        /**
         * Records if the certificate has become singular at some point of the
         * calculation.
         */
        boolean singular;
    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class MyValueNodeCert extends MyNodeCert {
        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyValueNodeCert(ValueNode node) {
            super(node);
            this.nodeValue = node.getValue();
            this.value = this.nodeValue.hashCode();
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyValueNodeCert} and has the same node as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof MyValueNodeCert
                && this.nodeValue.equals(((MyValueNodeCert) obj).nodeValue);
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

        private final Object nodeValue;
    }

    /**
     * An edge with certificate nodes as endpoints. The hash code is computed
     * dynamically, on the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class MyEdge2Cert extends MyCert<Edge>implements EdgeCertificate {
        /**
         * Constructs a certificate for a binary edge.
         * @param edge The target certificate node
         * @param source The source certificate node
         * @param target The label of the original edge
         */
        public MyEdge2Cert(Edge edge, MyNodeCert source, MyNodeCert target) {
            super(edge);
            this.source = source;
            this.target = target;
            this.label = edge.label();
            this.initValue = this.label.hashCode();
            this.value = this.initValue;
            source.addValue(this.value);
            target.addValue(this.value << 1);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + this.label + "(" + this.initValue + ")," + this.target
                + "]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyEdge2Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            MyEdge2Cert other = (MyEdge2Cert) obj;
            if (!this.source.equals(other.source) || !this.label.equals(other.label)) {
                return false;
            }
            if (this.target == this.source) {
                return other.target == other.source;
            }
            return this.target.equals(other.target);
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int targetShift = (this.initValue & 0xf) + 1;
            int sourceHashCode = this.source.value;
            int targetHashCode = this.target.value;
            int result = ((sourceHashCode << 8) | (sourceHashCode >>> 24))
                + ((targetHashCode << targetShift) | (targetHashCode >>> targetShift)) + this.value;
            this.source.nextValue += 2 * result;
            this.target.nextValue -= 3 * result;
            return result;
        }

        private final Label label;
        /** The source certificate for the edge. */
        private final MyNodeCert source;
        /** The target certificate for the edge; may be <tt>null</tt>. */
        private final MyNodeCert target;
        /**
         * The hash code of the original edge label.
         */
        private final int initValue;
    }

    /**
     * An edge with only one endpoint. The hash code is computed dynamically, on
     * the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class MyEdge1Cert extends MyCert<Edge>implements EdgeCertificate {
        /** Constructs a certificate edge for a predicate (i.e., a unary edge). */
        public MyEdge1Cert(Edge edge, MyNodeCert source) {
            super(edge);
            this.source = source;
            this.label = edge.label();
            this.initValue = this.label.hashCode();
            this.value = this.initValue;
            source.addValue(this.value);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + this.label + "(" + this.initValue + ")]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link PartitionRefiner.MyEdge1Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            MyEdge1Cert other = (MyEdge1Cert) obj;
            return this.label.equals(other.label);
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int sourceHashCode = this.source.hashCode();
            int result = (sourceHashCode << 8) + (sourceHashCode >> 24) + this.value;
            // source.nextValue += result;
            return result;
        }

        /** The source certificate for the edge. */
        private final MyNodeCert source;
        /** Possibly {@code null} node label. */
        private final Label label;
        /**
         * The hash code of the original edge label.
         */
        private final int initValue;
    }
}
