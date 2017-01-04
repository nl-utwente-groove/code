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

import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.type.TypeLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.collect.TreeHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
public class SimplePaigeTarjanMcKay extends CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * The strategy checks for isomorphism weakly, meaning that it might yield
     * false negatives.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public SimplePaigeTarjanMcKay(Graph graph) {
        this(graph, false);
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     * @param strong if <code>true</code>, the strategy puts more effort into
     *        getting distinct certificates.
     */
    public SimplePaigeTarjanMcKay(Graph graph, boolean strong) {
        super(graph);
        this.strong = strong;
    }

    @Override
    public SimplePaigeTarjanMcKay newInstance(Graph graph, boolean strong) {
        return new SimplePaigeTarjanMcKay(graph);
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
        // create the splitter array
        certStore.clear();
        for (int i = 0; i < this.nodeCerts.length; i++) {
            MyNodeCert nodeCert = (MyNodeCert) this.nodeCerts[i];
            MyNodeCert previous = certStore.put(nodeCert);
            Block block;
            if (previous == null) {
                block = new Block(this, nodeCert.getValue());
                block.setSplitter(true);
            } else {
                block = previous.getBlock();
            }
            block.append(nodeCert);
        }
        Queue<Block> splitters = new LinkedList<>();
        Iterator<MyNodeCert> iter = certStore.sortedIterator();
        while (iter.hasNext()) {
            splitters.add(iter.next().getBlock());
        }
        // Block[] resultArray = new Block[result.size()];
        // result.values().toArray(resultArray);
        // Arrays.sort(resultArray);
        if (RECORD) {
            this.partitionRecord = new ArrayList<>();
        }
        this.nodePartitionCount = splitters.size();
        // first iteration
        split(splitters);
        if (TRACE) {
            System.out.printf(
                "First iteration done; %d partitions for %d nodes in %d iterations%n",
                this.nodePartitionCount, this.nodeCertCount, this.iterateCount);
        }
    }

    private void split(Queue<Block> splitterList) {
        while (!splitterList.isEmpty()) {
            // find the first non-empty splitter in the queue
            Block splitter = splitterList.poll();
            if (splitter.size() > 0) {
                splitNext(splitter, splitterList);
            }
        }
    }

    private void splitNext(Block splitter, Queue<Block> splitterList) {
        if (RECORD) {
            Queue<Block> clone = new LinkedList<>();
            clone.add(splitter.clone());
            for (Block block : splitterList) {
                clone.add(block.clone());
            }
            this.partitionRecord.add(clone);
        }
        // update the node certificates related to the splitter nodes
        TreeHashSet<Block> splitBlocks = new TreeHashSet<>();
        for (MyNodeCert splitterNode : splitter.getNodes()) {
            for (MyEdge2Cert outEdge : splitterNode.outEdges) {
                Block splitBlock = outEdge.getTarget().getBlock();
                if (splitBlock.startSplit()) {
                    // add the new split block to the set
                    Block oldSplitBlock = splitBlocks.put(splitBlock);
                    // if another (different) block with the same value was
                    // already in the set
                    // (which would not happen given an ideal hash function)
                    // then merge the two blocks
                    if (oldSplitBlock != null && oldSplitBlock != splitBlock) {
                        oldSplitBlock.merge(splitBlock);
                    }
                }
                outEdge.updateTarget();
            }
            for (MyEdge2Cert inEdge : splitterNode.inEdges) {
                Block splitBlock = inEdge.getSource().getBlock();
                if (splitBlock.startSplit()) {
                    // add the new split block to the set
                    Block oldSplitBlock = splitBlocks.put(splitBlock);
                    // if another (different) block with the same value was
                    // already in the set
                    // (which would not happen given an ideal hash function)
                    // then merge the two blocks
                    if (oldSplitBlock != null && oldSplitBlock != splitBlock) {
                        oldSplitBlock.merge(splitBlock);
                    }
                }
                inEdge.updateSource();
            }
        }
        splitter.setSplitter(false);
        // process the split blocks
        if (RECORD) {
            Queue<Block> clone = new LinkedList<>();
            Iterator<Block> splitBlockIter = splitBlocks.sortedIterator();
            while (splitBlockIter.hasNext()) {
                clone.add(splitBlockIter.next());
            }
            this.partitionRecord.add(clone);
        }
        Iterator<Block> splitBlockIter = splitBlocks.sortedIterator();
        while (splitBlockIter.hasNext()) {
            Block block = splitBlockIter.next();
            Block[] newBlocks = block.split();
            if (RECORD) {
                Queue<Block> clone = new LinkedList<>();
                for (Block newBlock : newBlocks) {
                    clone.add(newBlock.clone());
                }
                this.partitionRecord.add(clone);
            }
            if (newBlocks.length > 0) {
                int last = block.isSplitter() ? newBlocks.length : newBlocks.length - 1;
                for (int i = 0; i < last; i++) {
                    splitterList.add(newBlocks[i]);
                    newBlocks[i].setSplitter(true);
                }
                this.nodePartitionCount += newBlocks.length - 1;
            }
        }
    }

    @Override
    NodeCertificate createValueNodeCertificate(ValueNode node) {
        return new MyValueNodeCert(node);
    }

    @Override
    NodeCertificate createNodeCertificate(Node node) {
        return new MyNodeCert(node);
    }

    @Override
    EdgeCertificate createEdge1Certificate(Edge edge, NodeCertificate source) {
        return new MyEdge1Cert(edge, (MyNodeCert) source);
    }

    @Override
    EdgeCertificate createEdge2Certificate(Edge edge, NodeCertificate source, NodeCertificate target) {
        return new MyEdge2Cert(this, edge, (MyNodeCert) source, (MyNodeCert) target);
    }

    /**
     * Flag to indicate that more effort should be put into obtaining distinct
     * certificates.
     */
    @SuppressWarnings("unused")
    private final boolean strong;
    /**
     * The number of pre-computed node partitions.
     */
    private int nodePartitionCount;
    /** Total number of iterations in iterateCertificates(). */
    private int iterateCount;

    /**
     * List of splitter lists generated during the algorithm. Only used when
     * {@link #RECORD} is set to <code>true</code>.
     */
    private List<Queue<Block>> partitionRecord;

    /**
     * Returns the total number of times symmetry was broken during the
     * calculation of the certificates.
     */
    static public int getSymmetryBreakCount() {
        return totalSymmetryBreakCount;
    }

    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;

    /** Number of bits in an int. */
    static private final int INT_WIDTH = 32;

    /**
     * The resolution of the tree-based certificate store.
     */
    static private final int TREE_RESOLUTION = 3;
    /**
     * Store for node certificates, to count the number of partitions
     */
    static private final TreeHashSet<MyNodeCert> certStore = new TreeHashSet<MyNodeCert>(
        TREE_RESOLUTION) {
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
    /** Static empty list, to be shared among split blocks. */
    private static final List<MyNodeCert> EMPTY_NODE_LIST = Collections.emptyList();
    /**
     * Static empty array of blocks, to be returned in case of singular split
     * blocks.
     */
    private static final Block[] EMPTY_BLOCK_ARRAY = new Block[0];

    /** Debug flag to switch the use of duplicate breaking on and off. */
    @SuppressWarnings("unused")
    static private final boolean BREAK_DUPLICATES = true;
    /** Flag to turn on partition recording. */
    static private final boolean RECORD = false;

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class MyNodeCert implements NodeCertificate {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public MyNodeCert(Node node) {
            this.node = node;
            if (node instanceof HostNode) {
                this.label = ((HostNode) node).getType().label();
                this.value = this.label.hashCode();
            } else {
                this.label = null;
                this.value = INIT_NODE_VALUE;
            }
        }

        @Override
        public String toString() {
            return "c" + this.value;
        }

        /**
         * Returns <tt>true</tt> of <tt>obj</tt> is also a
         * {@link SimplePaigeTarjanMcKay.MyNodeCert} and has the same value as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MyNodeCert)) {
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

        /**
         * Returns the current certificate value.
         */
        @Override
        public final int getValue() {
            return this.value;
        }

        /**
         * Adds a certain value to {@link #nextValue}.
         */
        @Override
        public void modifyValue(int value) {
            this.nextValue += value;
        }

        /**
         * Computes, stores and returns a new value for this certificate.
         */
        void setNewValue() {
            this.value += this.nextValue;
            this.nextValue = 0;
        }

        /** Returns the element of which this is a certificate. */
        @Override
        public Node getElement() {
            return this.node;
        }

        /** Adds a self-edge certificate to this node certificate. */
        void addSelf(MyEdge1Cert edgeCert) {
            this.value += edgeCert.getValue();
        }

        /** Adds an outgoing edge certificate to this node certificate. */
        void addOutgoing(MyEdge2Cert edgeCert) {
            this.outEdges.add(edgeCert);
            this.value += edgeCert.getValue();
        }

        /** Adds an incoming edge certificate to this node certificate. */
        void addIncoming(MyEdge2Cert edgeCert) {
            this.inEdges.add(edgeCert);
            this.value += edgeCert.getValue() ^ TARGET_MASK;
        }

        final Block getBlock() {
            return this.container;
        }

        final void setBlock(Block container) {
            this.container = container;
        }

        /** The value for the next invocation of computeNewValue() */
        private int nextValue;
        /** The current value, which determines the hash code. */
        int value;
        /** The element for which this is a certificate. */
        private final Node node;
        /** Potentially {@code null} node label. */
        private final TypeLabel label;
        /** List of certificates of incoming edges. */
        private final List<MyEdge2Cert> inEdges = new ArrayList<>();
        /** List of certificates of outgoing edges. */
        private final List<MyEdge2Cert> outEdges = new ArrayList<>();
        /** Current enclosing block. */
        private Block container;

        static final int TARGET_MASK = 0x5555;

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
            this.value = node.getValue().hashCode();
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link SimplePaigeTarjanMcKay.MyValueNodeCert} and has the same node as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof MyValueNodeCert
                && this.nodeValue.equals(((MyValueNodeCert) obj).nodeValue);
        }

        private final Object nodeValue;
    }

    static class MyEdge1Cert implements EdgeCertificate {
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
            return this.sourceCert.hashCode() + this.edge.label().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MyEdge1Cert)) {
                return false;
            }
            MyEdge1Cert other = (MyEdge1Cert) obj;
            return other.sourceCert.equals(this.sourceCert) && other.label.equals(this.label);
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + this.label + "(" + this.initValue + ")]";
        }

        @Override
        public final int getValue() {
            return this.value;
        }

        @Override
        public void modifyValue(int mod) {
            // ignore
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

    static class MyEdge2Cert extends MyEdge1Cert {
        MyEdge2Cert(SimplePaigeTarjanMcKay strategy, Edge edge, MyNodeCert sourceCert,
                MyNodeCert targetCert) {
            super(edge, sourceCert);
            this.targetCert = targetCert;
            sourceCert.addOutgoing(this);
            targetCert.addIncoming(this);
            this.strategy = strategy;
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
            return obj instanceof MyEdge2Cert && super.equals(obj)
                && ((MyEdge2Cert) obj).getTarget().equals(getTarget());
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + getElement().label() + "(" + this.initValue + "),"
                + getTarget() + "]";
        }

        private MyNodeCert getTarget() {
            return this.targetCert;
        }

        /** Updates the (next) value of the source certificate. */
        void updateSource() {
            getSource().modifyValue(3 * computeValue());
        }

        /** Updates the (next) value of the source certificate. */
        void updateTarget() {
            getTarget().modifyValue(-5 * computeValue());
        }

        /**
         * Computes a new hash value, based on the source and target
         * certificates and the label.
         */
        private int computeValue() {
            int shift = (this.initValue & 0xf) + 1;
            int targetValue = this.targetCert.getValue();
            int sourceValue = getSource().getValue();
            int result =
                ((sourceValue << shift) | (sourceValue >>> (INT_WIDTH - shift)))
                    + ((targetValue >>> shift) | (targetValue << (INT_WIDTH - shift)))
                    + this.initValue;
            this.strategy.graphCertificate += result;
            return result;
        }

        /** The node certificate of the edge target. */
        private final MyNodeCert targetCert;
        /** The strategy to which this certificate belongs. */
        private final SimplePaigeTarjanMcKay strategy;
    }

    /** Represents a block of nodes in some partition. */
    static class Block implements Comparable<Block>, Cloneable {
        Block(SimplePaigeTarjanMcKay strategy, int value) {
            // this.head = new NodeCertificate(this);
            this.nodes = new LinkedList<>();
            this.value = value;
            this.strategy = strategy;
            strategy.graphCertificate += value;
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
         * Starts splitting this block.
         * @return <code>true</code> if the split has just started,
         *         <code>false</code> if splitting had already started before.
         */
        final boolean startSplit() {
            if (!this.splitting) {
                return this.splitting = true;
            } else {
                return false;
            }
        }

        /**
         * Divides all the nodes in this block over new blocks, depending on
         * their value, and returns an array of all the new blocks.
         */
        Block[] split() {
            if (size() == 1) {
                MyNodeCert node = this.nodes.get(0);
                node.setNewValue();
                this.value = node.getValue();
                this.strategy.graphCertificate += this.value;
                this.splitting = false;
                return EMPTY_BLOCK_ARRAY;
            } else {
                Map<Integer,Block> blockMap = new HashMap<>();
                Block block = null;
                for (MyNodeCert node : this.nodes) {
                    node.setNewValue();
                    if (block == null || block.value != node.getValue()) {
                        block = blockMap.get(node.getValue());
                        if (block == null) {
                            blockMap.put(node.getValue(),
                                block = new Block(this.strategy, node.getValue()));
                        }
                    }
                    block.append(node);
                }
                this.nodes = EMPTY_NODE_LIST;
                if (blockMap.size() == 1) {
                    // the one block is given by block
                    return new Block[] {block};
                } else {
                    // collect and order the sub-blocks
                    Block[] result = new Block[blockMap.size()];
                    blockMap.values().toArray(result);
                    Arrays.sort(result);
                    return result;
                }
            }
        }

        /** Merges this block with another with the same hash code. */
        void merge(Block other) {
            assert this.value == other.value : String.format(
                "Merging blocks %s and %s with distinct hash codes", this, other);
            for (MyNodeCert otherNode : other.getNodes()) {
                otherNode.setBlock(this);
                this.nodes.add(otherNode);
            }
        }

        /**
         * Appends a given node certificate to this block, and sets the
         * certificate's block to this.
         */
        final void append(MyNodeCert node) {
            this.nodes.add(node);
            node.setBlock(this);
        }

        /** Returns the current size of the block. */
        final int size() {
            return this.nodes.size();
        }

        List<MyNodeCert> getNodes() {
            return this.nodes;
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
            return obj instanceof Block && ((Block) obj).value == this.value;
        }

        @Override
        public int hashCode() {
            return this.value;
        }

        @Override
        public String toString() {
            List<Node> content = new ArrayList<>();
            for (MyNodeCert nodeCert : this.nodes) {
                content.add(nodeCert.getElement());
            }
            return String.format("B%dx%d%s", this.nodes.size(), this.value, content);
        }

        @Override
        public Block clone() {
            try {
                Block result = (Block) super.clone();
                result.nodes = new ArrayList<>(this.nodes);
                return result;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        private final SimplePaigeTarjanMcKay strategy;
        /** The distinguishing value of this block. */
        private int value;
        /** List of marked nodes, in case the block is currently being split. */
        private List<MyNodeCert> nodes;
        /** Flag indicating if this block is in the list of splitters. */
        private boolean splitter;
        /** Flag indicating if this block is currently splitting. */
        private boolean splitting;
    }
}
