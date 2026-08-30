/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.iso;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.collect.IntSet;
import nl.utwente.groove.util.collect.TreeIntSet;

/**
 * Implements an algorithm to partition a given graph into sets of bisimilar
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are bisimilar if they
 * map to the same (i.e., <tt>equal</tt>) certificate.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Bisimulator extends CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public Bisimulator(Graph graph) {
        super(graph);
    }

    @Override
    public CertificateStrategy newInstance(Graph graph, boolean strong) {
        return new Bisimulator(graph);
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
                MyEdge2Cert edgeCert = (MyEdge2Cert) this.edgeCerts[i];
                // the parallel copies of the bundle would each have
                // contributed the same value
                certificateValue += (long) edgeCert.getMultiplicity() * edgeCert.setNewValue();
            }
            // now compute the new node certificates
            // while keeping track of the lowest value, in case
            // we need to break symmetry
            int minCertValue = Integer.MAX_VALUE;
            MyNodeCert minCert = null;
            //for (MyNodeCert<N> nodeCert : (MyNodeCert<N>[]) this.nodeCerts) {
            for (NodeCertificate nodeCert : this.nodeCerts) {
                int newCert = ((MyNodeCert) nodeCert).setNewValue();
                if (iterateCount > 0 && partitionCount < nodeCertCount) {
                    if (!certStore.add(newCert)) {
                        // this certificate is a duplicate; maybe it is the
                        // smallest
                        if (BREAK_SYMMETRIES && newCert < minCertValue || minCert == null) {
                            minCertValue = newCert;
                            minCert = (MyNodeCert) nodeCert;
                        }
                    }
                }
                certificateValue += newCert;
            }
            int newPartitionCount = certStore.size();
            // break symmetry if we have converged on a partition count
            // lower than the node count
            if (BREAK_SYMMETRIES && iterateCount > 0 && newPartitionCount == partitionCount
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
                goOn = newPartitionCount < nodeCertCount && breakSymmetryCount < MAX_BREAK_SYMMETRY;
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
                ((MyEdge1Cert) this.edgeCerts[i]).setNewValue();
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
    MyNodeCert getNodeCert(Node node) {
        return (MyNodeCert) super.getNodeCert(node);
    }

    @Override
    MyNodeCert createIdentityNodeCertificate(Node node) {
        return new MyIdentityNodeCert(node);
    }

    @Override
    MyNodeCert createNodeCertificate(Node node) {
        return new MyNodeCert(node);
    }

    @Override
    EdgeCertificate createEdge1Certificate(Edge edge, CertificateStrategy.NodeCertificate source,
                                           int multiplicity) {
        return new MyEdge1Cert(edge, (MyNodeCert) source, multiplicity);
    }

    @Override
    EdgeCertificate createEdge2Certificate(Edge edge, CertificateStrategy.NodeCertificate source,
                                           CertificateStrategy.NodeCertificate target,
                                           int multiplicity) {
        return new MyEdge2Cert(edge, (MyNodeCert) source, (MyNodeCert) target, multiplicity);
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
    public static abstract class Certificate<EL extends Element>
        implements CertificateStrategy.ElementCertificate<EL> {
        /** Constructs a certificate for a given graph element. */
        Certificate(EL element) {
            this.element = element;
        }

        /**
         * Returns the certificate value. Note that this means the hash code is
         * not constant during the initial phase, and so no hash sets or maps
         * should be used.
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
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Certificate<?> other = ((Certificate<?>) obj);
            return this.value == other.value;
        }

        /**
         * Returns the current certificate value.
         */
        @Override
        public int getValue() {
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
    static private class MyNodeCert extends Certificate<Node>
        implements CertificateStrategy.NodeCertificate {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyNodeCert(Node node) {
            super(node);
            Object seed = node.certificateSeed();
            this.seed = seed;
            this.value = seed == null
                ? INIT_NODE_VALUE
                : seed.hashCode();
        }

        /**
         * Tests if the other is a {@link Bisimulator.Certificate} with the same value.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !super.equals(obj)) {
                return false;
            }
            if (this.seed == null) {
                return true;
            }
            MyNodeCert other = ((MyNodeCert) obj);
            return this.seed.equals(other.seed);
        }

        @Override
        public String toString() {
            return "c" + this.value;
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

        /** Certificate seed of the node (possibly {@code null}). */
        final Object seed;
        /** The value for the next invocation of {@link #computeNewValue()} */
        int nextValue;
    }

    /**
     * Certificate for nodes with an identity certificate, such as data value
     * nodes. This takes the actual node identity (i.e., the certificate seed)
     * into account.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class MyIdentityNodeCert extends MyNodeCert {
        /**
         * Constructs a new identity node certificate.
         */
        public MyIdentityNodeCert(Node node) {
            super(node);
            assert this.seed != null;
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link Bisimulator.MyIdentityNodeCert} and has the same seed as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof MyIdentityNodeCert cert && this.seed.equals(cert.seed);
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
    }

    /**
     * An edge with certificate nodes as endpoints. The hash code is computed
     * dynamically, on the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class MyEdge2Cert extends Certificate<Edge> implements EdgeCertificate {
        /**
         * Constructs a certificate for a bundle of parallel binary edges.
         * @param edge a representative of the bundle
         * @param source The source certificate node
         * @param target The target certificate node
         * @param multiplicity the number of parallel copies in the bundle
         */
        public MyEdge2Cert(Edge edge, MyNodeCert source, MyNodeCert target, int multiplicity) {
            super(edge);
            this.source = source;
            this.target = target;
            this.multiplicity = multiplicity;
            this.label = edge.label();
            this.initValue = this.label.hashCode();
            initValue();
            source.addValue(multiplicity * this.value);
            target.addValue(multiplicity * (this.value << 1));
        }

        @Override
        public int getMultiplicity() {
            return this.multiplicity;
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "(" + this.initValue + "),"
                + this.target + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !super.equals(obj)) {
                return false;
            }
            MyEdge2Cert other = (MyEdge2Cert) obj;
            if (!this.source.equals(other.source) || !this.label.equals(other.label)
                || this.multiplicity != other.multiplicity) {
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
            this.source.nextValue += this.multiplicity * 2 * result;
            this.target.nextValue -= this.multiplicity * 3 * result;
            return result;
        }

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.initValue;
        }

        /** The source certificate for the edge. */
        private final MyNodeCert source;
        /** The target certificate for the edge; may be <tt>null</tt>. */
        private final MyNodeCert target;
        /** The number of parallel edges represented by this certificate. */
        private final int multiplicity;
        /** The original edge label. */
        private final Label label;
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
    static private class MyEdge1Cert extends Certificate<Edge> implements EdgeCertificate {
        /** Constructs a certificate edge for a bundle of parallel predicates
         * (i.e., unary edges).
         * @param edge a representative of the bundle
         * @param multiplicity the number of parallel copies in the bundle */
        public MyEdge1Cert(Edge edge, MyNodeCert source, int multiplicity) {
            super(edge);
            this.source = source;
            this.multiplicity = multiplicity;
            this.label = edge.label();
            this.initValue = this.label.hashCode();
            initValue();
            source.addValue(multiplicity * this.value);
        }

        @Override
        public int getMultiplicity() {
            return this.multiplicity;
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "(" + this.initValue + ")]";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !super.equals(obj)) {
                return false;
            }
            MyEdge1Cert other = (MyEdge1Cert) obj;
            return this.source.equals(other.source) && !this.label.equals(other.label)
                && this.multiplicity == other.multiplicity;
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

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.initValue << 4;
        }

        /** The source certificate for the edge. */
        private final MyNodeCert source;
        /** The number of parallel edges represented by this certificate. */
        private final int multiplicity;
        /** The original edge label. */
        private final Label label;
        /**
         * The hash code of the original edge label.
         */
        private final int initValue;
    }
}