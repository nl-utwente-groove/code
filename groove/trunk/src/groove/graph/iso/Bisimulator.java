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

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.util.IntSet;
import groove.util.TreeIntSet;

/**
 * Implements an algorithm to partition a given graph into sets of bisimilar
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are bisimilar if they
 * map to the same (i.e., <tt>equal</tt>) certificate.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Bisimulator<N extends Node,E extends Edge<N>> extends
        CertificateStrategy<N,E> {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public Bisimulator(Graph<N,E> graph) {
        super(graph);
    }

    @Override
    public <N1 extends Node,E1 extends Edge<N1>> CertificateStrategy<N1,E1> newInstance(
            Graph<N1,E1> graph, boolean strong) {
        return new Bisimulator<N1,E1>(graph);
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

    /** Iterates node certificates until this results in a stable partitioning. */
    @Override
    void iterateCertificates() {
        // get local copies of attributes for speedup
        IntSet certStore = Bisimulator.certStore;
        int nodeCertCount = this.nodeCertCount;
        int partitionCount = 0;
        long certificateValue;
        // collect and then count the number of certificates
        boolean goOn;
        int iterateCount = 0;
        int breakSymmetryCount = 0;
        do {
            certificateValue = nodeCertCount;
            certStore.clear(nodeCertCount);
            // first compute the new edge certificates
            for (int i = 0; i < this.edge2CertCount; i++) {
                Certificate<E> edgeCert = (MyEdge2Cert<N,E>) this.edgeCerts[i];
                certificateValue += edgeCert.setNewValue();
            }
            // now compute the new node certificates
            // while keeping track of the lowest value, in case
            // we need to break symmetry
            int minCertValue = Integer.MAX_VALUE;
            MyNodeCert<N> minCert = null;
            for (MyNodeCert<N> nodeCert : (MyNodeCert<N>[]) this.nodeCerts) {
                int newCert = nodeCert.setNewValue();
                if (iterateCount > 0 && partitionCount < nodeCertCount) {
                    if (!certStore.add(newCert)) {
                        // this certificate is a duplicate; maybe it is the
                        // smallest
                        if (BREAK_SYMMETRIES && newCert < minCertValue
                            || minCert == null) {
                            minCertValue = newCert;
                            minCert = nodeCert;
                        }
                    }
                }
                certificateValue += newCert;
            }
            int newPartitionCount = certStore.size();
            // break symmetry if we have converged on a partition count
            // lower than the node count
            if (BREAK_SYMMETRIES && iterateCount > 0
                && newPartitionCount == partitionCount
                && newPartitionCount < nodeCertCount) {
                minCert.breakSymmetry();
                breakSymmetryCount++;
                totalSymmetryBreakCount++;
            }
            // we stop the iteration when the number of partitions has not grown
            // moreover, when the number of partitions equals the number of
            // nodes then
            // it cannot grow, so we might as well stop straight away
            // note, however, that doing so gives rise to more false positives
            // which, on the other hand, are easily recognisable as such
            if (iterateCount == 0) {
                goOn = true;
            } else if (BREAK_SYMMETRIES) {
                goOn =
                    newPartitionCount < nodeCertCount
                        && breakSymmetryCount < MAX_BREAK_SYMMETRY;
            } else {
                goOn = newPartitionCount > partitionCount;
            }
            if (partitionCount < nodeCertCount) {
                partitionCount = newPartitionCount;
            }
            iterateCount++;
        } while (goOn);
        this.nodePartitionCount = partitionCount;
        this.graphCertificate = Long.valueOf(certificateValue);
        if (USE_EDGE1_CERTIFICATES) {
            // so far we have done nothing with the flags, so
            // give them a chance to get their hash code right
            int edgeCount = this.edgeCerts.length;
            for (int i = this.edge2CertCount; i < edgeCount; i++) {
                ((MyEdge2Cert<N,E>) this.edgeCerts[i]).setNewValue();
            }
        }
        recordIterateCount(iterateCount);
    }

    /**
     * The number of pre-computed node partitions.
     */
    private int nodePartitionCount;

    /** Specialises the return type. */
    @Override
    MyNodeCert<N> getNodeCert(N node) {
        return (MyNodeCert<N>) super.getNodeCert(node);
    }

    @Override
    MyNodeCert<N> createValueNodeCertificate(ValueNode node) {
        return new MyValueNodeCert<N>(node);
    }

    @Override
    MyNodeCert<N> createNodeCertificate(N node) {
        return new MyNodeCert<N>(node);
    }

    @Override
    EdgeCertificate<N,E> createEdge1Certificate(E edge,
            CertificateStrategy.NodeCertificate<N> source) {
        return new MyEdge1Cert<N,E>(edge, (MyNodeCert<N>) source);
    }

    @Override
    EdgeCertificate<N,E> createEdge2Certificate(E edge,
            CertificateStrategy.NodeCertificate<N> source,
            CertificateStrategy.NodeCertificate<N> target) {
        return new MyEdge2Cert<N,E>(edge, (MyNodeCert<N>) source,
            (MyNodeCert<N>) target);
    }

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
     * The maximum number of times a symmetry breaking step will be undertaken.
     */
    static private final int MAX_BREAK_SYMMETRY = 10;
    /**
     * Store for node certificates, to count the number of partitions
     */
    static private final IntSet certStore = new TreeIntSet(TREE_RESOLUTION);
    /** Debug flag to switch the use of {@link MyEdge1Cert}s on and off. */
    static private final boolean USE_EDGE1_CERTIFICATES = true;
    /** Debug flag to switch the use symmetry breaking on and off. */
    static private final boolean BREAK_SYMMETRIES = false;
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;

    /**
     * Superclass of graph element certificates.
     */
    public static abstract class Certificate<EL extends Element> implements
            CertificateStrategy.Certificate<EL> {
        /** Constructs a certificate for a given graph element. */
        Certificate(EL element) {
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
         * Tests if the other is a {@link Bisimulator.Certificate} with the same value.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Certificate<?>
                && (this.value == ((Certificate<?>) obj).value);
        }

        /**
         * Returns the current certificate value.
         */
        public int getValue() {
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
        public EL getElement() {
            return this.element;
        }

        /** The current value, which determines the hash code. */
        protected int value;
        /** The element for which this is a certificate. */
        private final EL element;
    }

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class MyNodeCert<N extends Node> extends Certificate<N>
            implements CertificateStrategy.NodeCertificate<N> {
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
         * {@link Bisimulator.MyNodeCert} and has the same value as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof MyNodeCert
                && (this.value == ((Certificate<?>) obj).value);
        }

        /**
         * Change the certificate value predictably to break symmetry.
         */
        public void breakSymmetry() {
            this.value += this.value << 1;
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
        @SuppressWarnings("unused")
        protected void addNextValue(int value) {
            this.nextValue += value;
        }

        /** The value for the next invocation of {@link #computeNewValue()} */
        int nextValue;
    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account. It is assumed that
         * {@link ValueNode} is a subtype of the type parameter {@code N}.
     * @author Arend Rensink
     * @version $Revision $
     */
    static private class MyValueNodeCert<N extends Node> extends MyNodeCert<N> {
        /**
         * Constructs a new value node certificate.
         */
        @SuppressWarnings("unchecked")
        public MyValueNodeCert(ValueNode node) {
            super((N) node);
            this.node = node;
            this.value = node.getNumber();
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link Bisimulator.MyValueNodeCert} and has the same node as this one.
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
     * @version $Revision$
     */
    static private class MyEdge2Cert<N extends Node,E extends Edge<N>> extends
            Certificate<E> implements EdgeCertificate<N,E> {
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
            this.labelIndex = ((DefaultLabel) edge.label()).hashCode();
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
         * {@link Bisimulator.MyEdge2Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyEdge2Cert) {
                MyEdge2Cert<?,?> other = (MyEdge2Cert<?,?>) obj;
                if (this.value != other.value
                    || this.labelIndex != other.labelIndex
                    || this.source.value != other.source.value) {
                    return false;
                } else if (this.target == this.source) {
                    return other.target == other.source;
                } else {
                    return this.target.value == other.target.value;
                }
            } else {
                return false;
            }
        }

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
     * @version $Revision$
     */
    static private class MyEdge1Cert<N extends Node,E extends Edge<N>> extends
            Certificate<E> implements EdgeCertificate<N,E> {
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
         * {@link Bisimulator.MyEdge1Cert} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyEdge1Cert<?,?>) {
                MyEdge1Cert<?,?> other = (MyEdge1Cert<?,?>) obj;
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
        private final Certificate<? extends Node> source;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }
}