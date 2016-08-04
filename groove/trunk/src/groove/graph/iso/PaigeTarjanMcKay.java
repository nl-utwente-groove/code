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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeMap;

import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.type.TypeLabel;
import groove.graph.Edge;
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
public class PaigeTarjanMcKay extends CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * The strategy checks for isomorphism weakly, meaning that it might yield
     * false negatives.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public PaigeTarjanMcKay(Graph graph) {
        this(graph, true);
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     * @param strong if <code>true</code>, the strategy puts more effort into
     *        getting distinct certificates.
     */
    public PaigeTarjanMcKay(Graph graph, boolean strong) {
        super(graph);
        this.strong = strong;
    }

    @Override
    public PaigeTarjanMcKay newInstance(Graph graph, boolean strong) {
        return new PaigeTarjanMcKay(graph, strong);
    }

    /**
     * This method only returns a useful result after the graph certificate or
     * partition map has been calculated.
     */
    @Override
    public int getNodePartitionCount() {
        if (this.partition == null) {
            computeCertificates();
        }
        return this.partition.size();
    }

    /** Right now only a strong strategy is implemented. */
    @Override
    public boolean getStrength() {
        return this.strong;
    }

    /** Computes the node and edge certificate arrays. */
    @Override
    void iterateCertificates() {
        this.partition = new NodePartition(this.nodeCerts);
        // initially all blocks are splitters
        Queue<Block> splitters = new LinkedList<>();
        Iterator<Block> iter = this.partition.sortedIterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            block.setSplitter(true);
            splitters.add(block);
        }
        if (RECORD) {
            this.partitionRecord = new ArrayList<>();
        }
        // first iteration
        split(splitters);
        if (TRACE) {
            System.err.printf(
                "First iteration done; %d partitions for %d nodes in %d iterations, certificate = %d%n",
                this.partition.size(),
                this.nodeCertCount,
                this.iterateCount,
                this.graphCertificate);
        }
        // check if duplicate
        if ((this.strong || BREAK_DUPLICATES) && this.partition.size() < this.nodeCertCount) {
            // now look for smallest unbroken duplicate certificate (if any)
            do {
                Block nontrivialBlock = selectNontrivialBlock();
                if (nontrivialBlock == null) {
                    if (TRACE) {
                        System.err.printf("All duplicate certificates broken%n");
                    }
                    break;
                } else if (TRACE) {
                    System.err.printf("Breaking symmetry at %s%n", nontrivialBlock);
                }
                checkpointCertificates();
                // successively break the symmetry at each of the nodes in the
                // nontrivial block
                for (MyNodeCert duplicate : nontrivialBlock.getNodes()
                    .toArray()) {
                    duplicate.breakSymmetry();
                    split(new LinkedList<>(duplicate.getBlock()
                        .split()));
                    rollBackCertificates();
                    this.partition = new NodePartition(this.nodeCerts);
                }
                accumulateCertificates();
                // calculate the node certificates once more
                // to push out the accumulated node values and get the correct
                // node partition count
                this.partition = new NodePartition(this.nodeCerts);
                if (TRACE) {
                    System.err.printf(
                        "Next iteration done; %d partitions for %d nodes in %d iterations, certificate = %d%n",
                        this.partition.size(),
                        this.nodeCertCount,
                        this.iterateCount,
                        this.graphCertificate);
                }
            } while (true);
        }
    }

    private void split(Queue<Block> splitterList) {
        // we could stop as soon as blockCount equals nodeCertCount
        // but that seems to give rise to many more false positives
        // while (this.nodePartitionCount < this.nodeCertCount
        // && !splitterList.isEmpty()) {
        while (!splitterList.isEmpty()) {
            // find the first non-empty splitter in the queue
            if (splitterList.peek()
                .size() > 0) {
                splitNext(splitterList);
                this.iterateCount++;
            } else {
                splitterList.poll();
            }
            // attempt to improve the graph certificate
            // int shift = this.iterateCount & 0x1F;
            // this.graphCertificate +=
            // this.graphCertificate << shift + this.graphCertificate >>>
            // (INT_WIDTH - shift);
        }
        // attempt to improve the graph certificate
        // if (!splitterList.isEmpty()) {
        // for (NodeCertificate nodeCert : this.nodeCerts) {
        // this.graphCertificate += nodeCert.getValue();
        // }
        // }
        recordIterateCount(this.iterateCount);
    }

    private void splitNext(Queue<Block> splitterList) {
        if (RECORD) {
            List<Block> clone = new ArrayList<>();
            for (Block block : splitterList) {
                clone.add(block.clone());
            }
            this.partitionRecord.add(clone);
        }
        // update the node certificates related to the splitter nodes
        // and collect the ensuing split blocks
        NodePartition splitBlocks = new NodePartition();
        markNodes(splitterList.poll(), splitBlocks);
        if (!SPLIT_ONE_AT_A_TIME) {
            while (!splitterList.isEmpty()) {
                markNodes(splitterList.poll(), splitBlocks);
            }
        }
        // process the split blocks
        if (RECORD) {
            List<Block> clone = new ArrayList<>();
            Iterator<Block> splitBlockIter = splitBlocks.sortedIterator();
            while (splitBlockIter.hasNext()) {
                clone.add(splitBlockIter.next());
            }
            this.partitionRecord.add(clone);
        }
        Iterator<Block> splitBlockIter = splitBlocks.sortedIterator();
        while (splitBlockIter.hasNext()) {
            Block block = splitBlockIter.next();
            Collection<Block> newBlocks = block.split();
            if (RECORD) {
                List<Block> clone = new ArrayList<>();
                for (Block newBlock : newBlocks) {
                    clone.add(newBlock.clone());
                }
                this.partitionRecord.add(clone);
            }
            splitterList.addAll(newBlocks);
        }
    }

    /**
     * Goes over the nodes in a given block, and updates and marks all its
     * adjacent nodes. The affected blocks (containing the marked nodes) are
     * collected.
     * @param splitter the block of which the adjacent nodes are to be marked
     * @param splitBlocks the collection of affected blocks
     */
    private void markNodes(Block splitter, NodePartition splitBlocks) {
        // first we copy the splitter's nodes into an array, to prevent
        // concurrent modifications due to the marking of nodes
        for (MyNodeCert splitterNode : splitter.getNodes()
            .toArray()) {
            for (MyEdge2Cert outEdge : splitterNode.getOutEdges()) {
                outEdge.updateTarget();
                Block splitBlock = outEdge.getTarget()
                    .mark();
                if (splitBlock != null) {
                    // add the new split block to the set
                    boolean isNew = splitBlocks.add(splitBlock);
                    assert isNew;
                }
            }
            for (MyEdge2Cert inEdge : splitterNode.getInEdges()) {
                inEdge.updateSource();
                Block splitBlock = inEdge.getSource()
                    .mark();
                if (splitBlock != null) {
                    // add the new split block to the set
                    boolean isNew = splitBlocks.add(splitBlock);
                    assert isNew;
                }
            }
        }
        splitter.setSplitter(false);
    }

    /**
     * Calls {@link MyNodeCert#setCheckpoint()} on all node and edge
     * certificates.
     */
    private void checkpointCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            nodeCert.setCheckpoint();
        }
    }

    /**
     * Calls {@link MyNodeCert#rollBack()} on all node and edge
     * certificates.
     */
    private void rollBackCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            nodeCert.rollBack();
        }
    }

    /**
     * Calls {@link MyNodeCert#accumulate(int)} on all node and edge
     * certificates.
     */
    private void accumulateCertificates() {
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            nodeCert.accumulate(this.iterateCount);
        }
    }

    /** Returns the non-trivial block unbroken with the smallest value. */
    private Block selectNontrivialBlock() {
        Block result = null;
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            if (!nodeCert.isBroken()) {
                Block block = nodeCert.getBlock();
                if (block.size() > 1
                    && (result == null || nodeCert.getValue() < result.getValue())) {
                    result = block;
                }
            }
        }
        return result;
    }

    @Override
    MyValueNodeCert createValueNodeCertificate(ValueNode node) {
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
    EdgeCertificate createEdge2Certificate(Edge edge, NodeCertificate source,
        NodeCertificate target) {
        return new MyEdge2Cert(edge, (MyNodeCert) source, (MyNodeCert) target);
    }

    /**
     * Flag to indicate that more effort should be put into obtaining distinct
     * certificates.
     */
    private final boolean strong;
    /** The current partition of the node certificates. */
    NodePartition partition;
    /** Total number of iterations in {@link #split(Queue)}. */
    private int iterateCount;

    /**
     * List of splitter lists generated during the algorithm. Only used when
     * {@link #RECORD} is set to <code>true</code>.
     */
    private List<List<Block>> partitionRecord;

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
     * Flag controlling the behaviour of the {@link #splitNext(Queue)} method.
     * If <code>true</code>, a single element of the splitter list is processed
     * at a time; otherwise, the entire list is processed.
     */
    static private final boolean SPLIT_ONE_AT_A_TIME = false;
    /** Debug flag to switch the use of duplicate breaking on and off. */
    static private final boolean BREAK_DUPLICATES = false;
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;
    /** Number of bits in an int. */
    static private final int INT_WIDTH = 32;

    // --------------------------- reporter definitions ---------------------
    /** Flag to turn on partition recording. */
    static private final boolean RECORD = false;

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision$
     */
    private class MyNodeCert implements NodeCertificate, Cloneable {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /** Creates a dummy certificate to serve as a head node for a list. */
        MyNodeCert(Block block) {
            this((Node) null);
            this.block = block;
            this.value = block.value;
        }

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyNodeCert(Node node) {
            this.element = node;
            if (node instanceof HostNode) {
                this.label = ((HostNode) node).getType()
                    .label();
                this.value = this.label.hashCode();
            } else {
                this.label = null;
                this.value = INIT_NODE_VALUE;
            }
            this.next = this;
            this.prev = this;
        }

        @Override
        public String toString() {
            return "c" + this.value;
        }

        /**
         * Returns <tt>true</tt> of <tt>obj</tt> is also a
         * {@link PaigeTarjanMcKay.MyNodeCert} and has the same value as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MyNodeCert other = ((MyNodeCert) obj);
            if (this.value != other.value) {
                return false;
            }
            if (this.label == null) {
                return true;
            }
            return this.label.equals(other.label);
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

        /** Only invokes the super method. Included for visibility. */
        @Override
        protected MyNodeCert clone() throws CloneNotSupportedException {
            return (MyNodeCert) super.clone();
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

        /** Sets a new certificate value. */
        void setValue(int value) {
            this.value = value;
        }

        /**
         * Adds a certain value to {@link #nextValue}.
         */
        void addNextValue(int value) {
            this.nextValue += value;
        }

        /**
         * Computes, stores and returns a new value for this certificate.
         */
        int setNextValue() {
            this.value += this.nextValue;
            this.nextValue = 0;
            PaigeTarjanMcKay.this.graphCertificate += this.value;
            return this.value;
        }

        /** Returns the element of which this is a certificate. */
        @Override
        public Node getElement() {
            return this.element;
        }

        /** Adds a self-edge certificate to this node certificate. */
        void addSelf(MyEdge1Cert edgeCert) {
            this.value += edgeCert.getValue();
        }

        /** Adds an outgoing edge certificate to this node certificate. */
        void addOutEdge(MyEdge2Cert edgeCert) {
            this.outEdges.add(edgeCert);
            this.value += edgeCert.getValue();
        }

        /** Adds an incoming edge certificate to this node certificate. */
        void addInEdge(MyEdge2Cert edgeCert) {
            this.inEdges.add(edgeCert);
            this.value += edgeCert.getValue() ^ TARGET_MASK;
        }

        /** Returns the list of incoming edge certificates. */
        List<MyEdge2Cert> getInEdges() {
            return this.inEdges;
        }

        /** Returns the list of outgoing edge certificates. */
        List<MyEdge2Cert> getOutEdges() {
            return this.outEdges;
        }

        /** Returns the containing block of this certificate. */
        final Block getBlock() {
            return this.block;
        }

        /** Sets the containing block of this certificate. */
        @SuppressWarnings("unused")
        final void setBlock(Block container) {
            this.block = container;
        }

        /**
         * Inserts this node certificate after another one. Removes the node
         * from its current position first.
         */
        void insertAfter(MyNodeCert other) {
            // link previous to next in the current list
            this.prev.next = this.next;
            this.next.prev = this.prev;
            // insert the node into the new list
            this.block = other.getBlock();
            this.prev = other;
            this.next = other.next;
            other.next = this;
            this.next.prev = this;
        }

        /**
         * Marks this node for changing its value. Also calls
         * {@link PaigeTarjanMcKay.Block#markNode(MyNodeCert)}.
         * @return the containing block, if that block had not been marked
         *         before.
         */
        Block mark() {
            if (this.block.isFinal()) {
                // we're not going to do anything with this certificate,
                // so put the next value back to 0 to avoid accidents
                this.nextValue = 0;
                return null;
            } else if (this.marked) {
                return null;
            } else {
                this.marked = true;
                return (getBlock().markNode(this)) ? getBlock() : null;
            }
        }

        /** Unmarks this node for changing value. */
        void unmark() {
            this.marked = false;
        }

        /**
         * Changes the certificate value predictably to break symmetry. Also
         * marks the node as changed.
         */
        void breakSymmetry() {
            this.value ^= this.value << 5 ^ this.value >> 3;
            this.broken = true;
            mark();
        }

        final boolean isBroken() {
            return this.broken;
        }

        /**
         * Sets a checkpoint that we can later roll back to.
         */
        void setCheckpoint() {
            this.checkpointValue = this.value;
        }

        /**
         * Rolls back the value to that frozen at the latest checkpoint.
         */
        void rollBack() {
            this.cumulativeValue += this.value;
            this.value = this.checkpointValue;
        }

        /**
         * Combines the accumulated intermediate values collected at rollback,
         * and adds them to the actual value.
         * @param round the iteration round
         */
        void accumulate(int round) {
            this.value += this.cumulativeValue;
            this.cumulativeValue = 0;
        }

        /** The value for the next invocation of {@link #setNextValue()} */
        private int nextValue;
        /** The current value, which determines the hash code. */
        int value;
        /** The element for which this is a certificate. */
        private final Node element;
        /** Potentially {@code null} node label. */
        private final TypeLabel label;
        /** List of certificates of incoming edges. */
        private final List<MyEdge2Cert> inEdges = new ArrayList<>();
        /** List of certificates of outgoing edges. */
        private final List<MyEdge2Cert> outEdges = new ArrayList<>();
        /** Current enclosing block. */
        private Block block;
        /** Next and previous node certificates in the list. */
        MyNodeCert next, prev;
        /** Flag to indicate that this certificate has been marked for change. */
        private boolean marked;
        /** The value as frozen at the last call of {@link #setCheckpoint()}. */
        private int checkpointValue;
        /**
         * The cumulative values as calculated during the {@link #rollBack()}s
         * after the last {@link #setCheckpoint()}.
         */
        private int cumulativeValue;
        /** Flag indicating if the symmetry of this block has been broken. */
        private boolean broken;
        /** Field to modify the value computed for the edge target. */
        static final int TARGET_MASK = 0x5555;
    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class MyValueNodeCert extends MyNodeCert {
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
         * {@link PaigeTarjanMcKay.MyValueNodeCert} and has the same node as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MyValueNodeCert)) {
                return false;
            }
            MyValueNodeCert other = (MyValueNodeCert) obj;
            return this.nodeValue.equals(other.nodeValue);
        }

        private final Object nodeValue;
    }

    private class MyEdge1Cert implements EdgeCertificate {
        MyEdge1Cert(Edge edge, MyNodeCert sourceCert) {
            this.edge = edge;
            this.sourceCert = sourceCert;
            this.label = edge.label();
            this.initValue = this.label.hashCode();
            this.value = this.initValue;
            sourceCert.addSelf(this);
        }

        @Override
        final public Edge getElement() {
            return this.edge;
        }

        @Override
        public int hashCode() {
            return this.sourceCert.hashCode() + this.initValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PaigeTarjanMcKay.MyEdge1Cert)) {
                return false;
            }
            MyEdge1Cert other = (MyEdge1Cert) obj;
            return other.sourceCert.equals(this.sourceCert) && other.label.equals(this.label);
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + this.label + "(" + this.label.hashCode() + ")]";
        }

        @Override
        final public int getValue() {
            return this.value;
        }

        @Override
        public void modifyValue(int mod) {
            // ignored
        }

        final MyNodeCert getSource() {
            return this.sourceCert;
        }

        private final Edge edge;
        private final Label label;
        private final MyNodeCert sourceCert;
        /**
         * The hash code of the original edge label.
         */
        protected final int initValue;
        private final int value;
    }

    private class MyEdge2Cert extends MyEdge1Cert {
        MyEdge2Cert(Edge edge, MyNodeCert sourceCert, MyNodeCert targetCert) {
            super(edge, sourceCert);
            this.targetCert = targetCert;
            sourceCert.addOutEdge(this);
            targetCert.addInEdge(this);
        }

        @Override
        public int hashCode() {
            return super.hashCode() + (getTarget().hashCode() << 2);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            return obj instanceof PaigeTarjanMcKay.MyEdge2Cert && ((MyEdge2Cert) obj).getTarget()
                .equals(getTarget());
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + getElement().label() + "(" + this.initValue + "),"
                + getTarget() + "]";
        }

        MyNodeCert getTarget() {
            return this.targetCert;
        }

        /** Updates the (next) value of the source certificate. */
        void updateSource() {
            getSource().addNextValue(3 * computeValue());
        }

        /** Updates the (next) value of the source certificate. */
        void updateTarget() {
            getTarget().addNextValue(-5 * computeValue());
        }

        /**
         * Computes a new hash value, based on the source and target
         * certificates and the label.
         */
        private int computeValue() {
            int shift = (this.initValue & 0xf) + 1;
            int targetValue = this.targetCert.getValue();
            int sourceValue = getSource().getValue();
            int result = ((sourceValue << shift) | (sourceValue >>> (INT_WIDTH - shift)))
                + ((targetValue >>> shift) | (targetValue << (INT_WIDTH - shift))) + this.initValue;
            PaigeTarjanMcKay.this.graphCertificate += result;
            return result;
        }

        /** The node certificate of the edge target. */
        private final MyNodeCert targetCert;
    }

    /**
     * An iterator over node certificates, based on a doubly linked list with a
     * dummy head node.
     */
    private class NodeCertificateList extends MyNodeCert implements Iterable<MyNodeCert> {
        /** Creates an empty list, associated with a given block. */
        NodeCertificateList(Block block) {
            super(block);
        }

        @Override
        public Iterator<MyNodeCert> iterator() {
            return new Iterator<MyNodeCert>() {
                @Override
                public boolean hasNext() {
                    if (this.next == null) {
                        this.next = NodeCertificateList.this.next;
                    }
                    return this.next != NodeCertificateList.this;
                }

                @Override
                public MyNodeCert next() {
                    if (hasNext()) {
                        MyNodeCert result = this.next;
                        this.next = this.next.next;
                        return result;
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                /**
                 * Node to be returned by the next invocation of {@link #next()}
                 * .
                 */
                private MyNodeCert next;
            };
        }

        /** Returns the first certificate in the list. */
        MyNodeCert first() {
            return this.next == this ? null : this.next;
        }

        /** Returns an array containing all certificates in this list. */
        MyNodeCert[] toArray() {
            // this implementation works under the assumption
            // that this is the array of nodes in the container block.
            assert this == getBlock().getNodes();
            MyNodeCert[] result =
                new PaigeTarjanMcKay.MyNodeCert[getBlock().size() - getBlock().markedSize()];
            int i = 0;
            for (MyNodeCert cert = this.next; cert != this; cert = cert.next, i++) {
                result[i] = cert;
            }
            return result;
        }
    }

    /**
     * Class implementing a partition, consisting of a set of blocks.
     */
    private class NodePartition extends TreeHashSet<Block> {
        /** Creates an empty partition. */
        NodePartition() {
            super(TREE_RESOLUTION);
        }

        /** Creates a partition from a given array of node certificates. */
        NodePartition(NodeCertificate[] nodes) {
            super(TREE_RESOLUTION);
            for (int i = 0; i < nodes.length; i++) {
                MyNodeCert nodeCert = (MyNodeCert) nodes[i];
                nodeCert.setNextValue();
                // create a new block and try to insert it into the result
                Block block = new Block(nodeCert.getValue());
                Block previous = put(block);
                // if there was already a block with this value, use that
                // instead
                if (previous != null) {
                    block = previous;
                }
                block.add(nodeCert);
            }
        }

        /** Block equality is determined entirely by hash code. */
        @Override
        protected boolean allEqual() {
            return true;
        }

        /**
         * Adds a given block to the partition, adjusting its key (and that of
         * its nodes) if a block with the same key is already in the partition.
         * @param block the block to be inserted
         * @param incr amount to increment the key by if a block with the same
         *        key is already in the partition
         */
        void add(Block block, int incr) {
            boolean isNew = add(block);
            if (!isNew) {
                // find a certificate value that works
                incr = incr == 0 ? 1 : incr;
                int newValue = block.getValue();
                do {
                    newValue += incr;
                    incr++;
                    block.setValue(newValue);
                    isNew = add(block);
                } while (!isNew);
                // we've found a new value, now updates the nodes
                for (MyNodeCert node : block.getNodes()) {
                    node.setValue(newValue);
                }
            }
        }
    }

    /** Represents a block of nodes in some partition. */
    private class Block implements Comparable<Block>, Cloneable {
        Block(int value) {
            this.value = value;
            this.nodes = new NodeCertificateList(this);
            this.markedNodes = new NodeCertificateList(this);
            PaigeTarjanMcKay.this.graphCertificate += this.value;
        }

        /** Indicates if this block is in the list of splitters. */
        boolean isSplitter() {
            return this.splitter;
        }

        /** Records that this block has been inserted in the list of splitters. */
        void setSplitter(boolean splitter) {
            this.splitter = splitter;
        }

        /**
         * Indicates if this block is final, meaning that it does not have to be
         * split anymore. This is the case if the size is 1, and the block is
         * not a splitter.
         */
        boolean isFinal() {
            return !isSplitter() && this.size == 1;
        }

        NodeCertificateList getNodes() {
            return this.nodes;
        }

        /**
         * Divides all the marked nodes in this block over new blocks, depending
         * on their value, and returns an ordered array of new splitters.
         */
        Collection<Block> split() {
            if (this.size == 1) {
                PaigeTarjanMcKay.this.partition.remove(this);
                // don't create a new block, rather reuse the current
                MyNodeCert node = this.markedNodes.first();
                node.unmark();
                node.insertAfter(this.nodes);
                this.markedSize = 0;
                int oldValue = this.value;
                this.value = node.setNextValue();
                PaigeTarjanMcKay.this.partition.add(this, oldValue);
                return Collections.emptySet();
            } else {
                Map<Integer,Block> blockMap = new TreeMap<>();
                Block lastBlock = null;
                // keep track of the largest block
                Block largestBlock = null;
                int largestSize = 0;
                int largestValue = 0;
                for (MyNodeCert markedNode : this.markedNodes) {
                    markedNode.unmark();
                    int newValue = markedNode.setNextValue();
                    if (lastBlock == null || lastBlock.value != newValue) {
                        lastBlock = blockMap.get(newValue);
                        if (lastBlock == null) {
                            blockMap.put(newValue, lastBlock = new Block(newValue));
                            lastBlock.setSplitter(true);
                        }
                    }
                    lastBlock.add(markedNode);
                    if (lastBlock.size() > largestSize
                        || lastBlock.size() == largestSize && newValue > largestValue) {
                        largestSize = lastBlock.size();
                        largestValue = newValue;
                        largestBlock = lastBlock;
                    }
                }
                this.size -= this.markedSize;
                this.markedSize = 0;
                // adjust the partition
                for (Block newBlock : blockMap.values()) {
                    PaigeTarjanMcKay.this.partition.add(newBlock, this.value);
                }
                if (this.size == 0) {
                    PaigeTarjanMcKay.this.partition.remove(this);
                }
                // if this block is not a splitter and not the largest, add it
                // to the split blocks,
                // remove the largest block and set it to non-splitter
                if (!isSplitter() && this.size < largestSize) {
                    assert largestBlock != null; // implied by largestSize > 0
                    largestBlock.setSplitter(false);
                    blockMap.remove(largestValue);
                    if (this.size > 0) {
                        this.setSplitter(true);
                        blockMap.put(this.value, this);
                    }
                }
                return blockMap.values();
            }
        }

        /**
         * Appends a given node certificate to this block, and sets the
         * certificate's block to this.
         */
        final void add(MyNodeCert node) {
            node.insertAfter(this.nodes);
            this.size++;
        }

        /**
         * Returns the current (total) size of the block. This includes marked
         * and unmarked nodes.
         */
        final int size() {
            return this.size;
        }

        /**
         * Returns the number of marked nodes.
         */
        final int markedSize() {
            return this.markedSize;
        }

        /**
         * Returns the current certificate value.
         */
        int getValue() {
            return this.value;
        }

        /**
         * Changes the certificate value.
         */
        void setValue(int value) {
            this.value = value;
        }

        /**
         * Transfers a node to the list of marked nodes. This means the block
         * should be split.
         * @param node the node to be marked
         * @return <code>true</code> if the block had not been selected for
         *         splitting before.
         */
        boolean markNode(MyNodeCert node) {
            assert node.getBlock() == this;
            boolean result = this.markedSize == 0;
            node.insertAfter(this.markedNodes);
            this.markedSize++;
            return result;
        }

        /**
         * A block is smaller than another if it has fewer nodes, or a smaller
         * hash value.
         */
        @Override
        public int compareTo(Block other) {
            int result = size() - other.size();
            if (result != 0) {
                return result;
            }
            return this.value < other.value ? -1 : this.value > other.value ? +1 : 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PaigeTarjanMcKay.Block && ((Block) obj).value == this.value;
        }

        @Override
        public int hashCode() {
            return this.value;
        }

        @Override
        public String toString() {
            List<Node> content = new ArrayList<>();
            for (MyNodeCert nodeCert : getNodes()) {
                content.add(nodeCert.getElement());
            }
            return String.format("B%dx%d%s", this.size, this.value, content);
        }

        /**
         * This implementation clones the node certificates as well, but not
         * their sets of in- and out-edges.
         */
        @Override
        public Block clone() {
            try {
                // avoid the use of the constructor
                // since this updates the graph certificate
                Block result = (Block) super.clone();
                // clone the nodes
                result.nodes = new NodeCertificateList(result);
                for (MyNodeCert node : getNodes()) {
                    result.add(node.clone());
                }
                // size is nodes + markedNodes
                result.size = this.size;
                // clone the marked nodes
                result.markedNodes = new NodeCertificateList(result);
                for (MyNodeCert markedNode : this.markedNodes) {
                    markedNode.clone()
                        .insertAfter(result.markedNodes);
                }
                result.markedSize = this.markedSize;
                return result;
            } catch (CloneNotSupportedException exc) {
                return null;
            }
        }

        /** The distinguishing value of this block. */
        private int value;
        /** Dummy head node of the list of nodes in this block. */
        private NodeCertificateList nodes;
        /** Size of the list of nodes. */
        private int size;
        /** Dummy head node of the list of marked nodes in this block. */
        private NodeCertificateList markedNodes;
        /** Size of the list of marked nodes. */
        private int markedSize;
        /** Flag indicating if this block is in the list of splitters. */
        private boolean splitter;
        // /** Flag indicating if this block is currently splitting. */
        // private boolean splitting;
    }
}
