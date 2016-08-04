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
package groove.gui.layout;

import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Action to set up the standard touchgraph layout algorithm on a given
 * MyJGraph. Adapted from <tt>jgraph.com.pad.Touch</tt>
 * @author Gaudenz Alder and Arend Rensink
 * @version $Revision$
 */
public class SpringLayouter extends AbstractLayouter {
    /** Constructs a template spring layouter. */
    private SpringLayouter() {
        super(ACTION_NAME);
    }

    /**
     * Constructs a new, named layout action on a given graph, with given layout
     * rigidity.
     * @param name name of this layout action
     * @param jgraph graph to be layed out
     * @param rigidity the initial rigidity of the layout action. A higher value
     *        means nodes are pulled closer together.
     * @require name != null, jgraph != null, rigidity > 0 jgraph.getModel()
     *          instanceof jgraph.GraphJModel
     */
    private SpringLayouter(String name, JGraph<?> jgraph, float rigidity) {
        super(name, jgraph);
    }

    @Override
    public Layouter newInstance(JGraph<?> jgraph) {
        return new SpringLayouter(getName(), jgraph, this.rigidity);
    }

    /**
     * Starts layouting in a parallel thread; or stops the current layouter
     * thread if one is running.
     */
    @Override
    public void start() {
        SpringLayouter.this.damper = 1.0;
        prepare(false);
        long currentTime = System.currentTimeMillis();
        while (SpringLayouter.this.damper > 0 && System.currentTimeMillis() - currentTime < TIMEOUT) {
            relax();
        }
        finish();
    }

    @Override
    protected void prepare(boolean recordImmovables) {
        super.prepare(recordImmovables);
        if (DEBUG) {
            System.out.println("Starting automatic layout");
        }
        //
        // initialise the layoutables, positions and deltas
        this.deltaMap.clear();
        int layoutableIndex = 0;
        this.layoutables = new LayoutNode[this.layoutMap.size()];
        this.positions = new Point2D.Double[this.layoutMap.size()];
        this.deltas = new Point2D.Float[this.layoutMap.size()];
        for (LayoutNode layoutable : this.layoutMap.values()) {
            this.layoutables[layoutableIndex] = layoutable;
            if (!this.immovableMap.containsKey(layoutable.getVertex())) {
                this.deltas[layoutableIndex] = new Point2D.Float(0, 0);
                this.deltaMap.put(layoutable, this.deltas[layoutableIndex]);
            }
            double p2X = layoutable.getX() + layoutable.getWidth() / 2;
            double p2Y = layoutable.getY() + layoutable.getHeight() / 2;
            this.positions[layoutableIndex] = new Point2D.Double(p2X, p2Y);
            layoutableIndex++;
        }
        // initialise the edge fragment arrays
        // Object[] graphEdges = jgraph.getEdges(jgraph.getRoots());
        List<LayoutNode> edgeSourceList = new LinkedList<>();
        List<LayoutNode> edgeTargetList = new LinkedList<>();
        for (Object jCell : getJGraph().getRoots()) {
            if (!(jCell instanceof JEdge)) {
                continue;
            }
            JEdge<?> jEdge = (JEdge<?>) jCell;
            if (!jEdge.getVisuals().isVisible()) {
                continue;
            }
            if (jEdge.isGrayedOut()) {
                continue;
            }
            LayoutNode source = this.layoutMap.get(jEdge.getSourceVertex());
            if (source == null) {
                continue;
            }
            LayoutNode target = this.layoutMap.get(jEdge.getTargetVertex());
            if (target == null) {
                continue;
            }
            edgeSourceList.add(source);
            edgeTargetList.add(target);
        }
        this.edgeSources = edgeSourceList.toArray(new LayoutNode[edgeSourceList.size()]);
        this.edgeTargets = edgeTargetList.toArray(new LayoutNode[edgeTargetList.size()]);
    }

    private void damp() {
        if (this.motionRatio <= 0.001) { // This is important. Only damp when
            // the graph starts to move
            // faster
            // When there is noise, you damp roughly half the time. (Which is a
            // lot)
            //
            // If things are slowing down, then you can let them do so on their
            // own,
            // without damping.

            // If max motion<0.2, damp away
            // If by the time the damper has ticked down to 0.9, maxMotion is
            // still>1, damp away
            // We never want the damper to be negative though
            if ((this.maxMotion < FAST_DAMPING_MOTION_TRESHHOLD || this.damper < FAST_DAMPING_DAMPER_TRESHHOLD)
                && this.damper > FAST_DAMPING) {
                this.damper -= FAST_DAMPING;
            } else if (this.maxMotion < MEDIUM_DAMPING_MOTION_TRESHHOLD
                && this.damper > MEDIUM_DAMPING) {
                this.damper -= MEDIUM_DAMPING;
            } else if (this.damper > SLOW_DAMPING) {
                this.damper -= SLOW_DAMPING;
            }
        }
        if (this.maxMotion <= SLOW_DAMPING) {
            this.damper = 0;
        }
    }

    // relaxEdges is more like tense edges up. All edges pull nodes closer
    // together;
    private synchronized void relaxEdges() {
        for (int i = 0; i < this.edgeSources.length; i++) {
            LayoutNode bf = this.edgeSources[i];
            LayoutNode bt = this.edgeTargets[i];
            double dx = (bt.getX() - bf.getX()) * this.workingRigidity / 100; // rigidity
            // makes
            // edges
            // tighter
            double dy = (bt.getY() - bf.getY()) * this.workingRigidity / 100;
            shiftDelta(bt, -dx, -dy);
            shiftDelta(bf, dx, dy);
        }
    }

    private synchronized void avoidLabels() {
        final float repSum = 200; // a repulsion constant
        for (int i = 0; i < this.layoutables.length; i++) {
            Point2D.Double bf = this.positions[i];
            float fromDx = 0;
            float fromDy = 0;
            for (int j = i + 1; j < this.layoutables.length; j++) {
                Point2D.Double bt = this.positions[j];

                double vx = bf.x - bt.x;
                double vy = bf.y - bt.y;
                if (Math.abs(vx) < repSum && Math.abs(vy) < repSum) {
                    double len = (vx * vx + vy * vy) / repSum; // so it's
                    // length
                    // squared
                    double dx, dy;
                    if (len < 1 / repSum) {
                        dx = repSum * (float) Math.random();
                        dy = repSum * (float) Math.random();
                    } else {
                        dx = vx / len;
                        dy = vy / len;
                    }
                    fromDx += dx;
                    fromDy += dy;
                    shiftDelta(this.deltas[j], -dx, -dy);
                }
            }
            shiftDelta(this.deltas[i], fromDx, fromDy);
        }
    }

    private synchronized void moveNodes() {
        float shiftX = 0;
        float shiftY = 0;
        if (MOVE_NODES_DEBUG) {
            System.out.println("Reset shiftX and shiftY");
        }
        this.lastMaxMotion = this.maxMotion;
        this.maxMotion = 0;
        for (int i = 0; i < this.deltas.length; i++) {
            LayoutNode key = this.layoutables[i];
            Point2D.Float delta = this.deltas[i];
            if (delta != null) {
                float dx = delta.x *= this.damper;
                float dy = delta.y *= this.damper;
                delta.setLocation(dx / 2, dy / 2);
                if (Math.abs(dx) > SMALL_VALUE || Math.abs(dy) > SMALL_VALUE) {
                    float distMoved = Math.abs(dx) + Math.abs(dy);
                    if (distMoved > this.maxMotion) {
                        this.maxMotion = distMoved;
                    }
                    Point2D.Double position = this.positions[i];
                    position.x += Math.max(-5, Math.min(5, dx)) - shiftX; // prevents
                    // too
                    // wild
                    // oscillations
                    position.y += Math.max(-5, Math.min(5, dy)) - shiftY; // prevents
                    // too
                    // wild
                    // oscillations
                    if (position.x < 0) {
                        shiftX += position.x;
                        if (MOVE_NODES_DEBUG) {
                            System.out.println("shiftX set to " + shiftX);
                        }
                        position.x = 0;
                    }
                    if (position.y < 0) {
                        shiftY += position.y;
                        if (MOVE_NODES_DEBUG) {
                            System.out.println("shiftY set to " + shiftY);
                        }
                        position.y = 0;
                    }
                    key.setLocation(Math.max(0, (int) position.x - key.getWidth() / 2),
                        Math.max(0, (int) position.y - key.getHeight() / 2));
                }
            }
        }
        if (this.maxMotion > 0) {
            this.motionRatio = this.lastMaxMotion / this.maxMotion - 1;
        } else {
            this.motionRatio = 0;
        }
        damp();
    }

    synchronized void relax() {
        for (int i = 0; i < 10; i++) {
            relaxEdges();
            avoidLabels();
            moveNodes();
        }
        this.workingRigidity = this.rigidity; // update rigidity
    }

    private void shiftDelta(LayoutNode key, double dx, double dy) {
        shiftDelta(this.deltaMap.get(key), dx, dy);
    }

    private void shiftDelta(Point2D.Float delta, double dx, double dy) {
        if (delta != null) {
            delta.x += dx;
            delta.y += dy;
        }
    }

    /**
     * An array of layoutables, corresponding to the keys in
     * LayoutAction#toLayoutableMap.
     */
    private LayoutNode[] layoutables;

    /**
     * More precise positions for the elements of layoutables.
     * @invariant <tt>positions.length == layoutables.length</tt>
     */
    private Point2D.Double[] positions;

    /**
     * Collective move info for the layoutables. The array contains null where a
     * layoutable is actually unmovable.
     * @invariant <tt>deltas.length == layoutables.length</tt>
     */
    private Point2D.Float[] deltas;

    /**
     * A map from layoutables to deltas. The entries are all the pairs
     * <tt>(layoutables[i],deltas[i])</tt> for which
     * <tt>deltas[i] != null</tt>
     */
    private final Map<LayoutNode,Point2D.Float> deltaMap = new HashMap<>();

    /**
     * Source vertices or midpoints of the edge fragments in this graph.
     * Transient value; only used while layout is running.
     * @invariant <tt>edgeFragmentSources: (Rectangle \cup Point)^*</tt>
     */
    private LayoutNode[] edgeSources;

    /**
     * Target midpoints or vertices of the edge fragments in this graph.
     * Transient value; only used while layout is running.
     * @invariant <tt>edgeFragmentTargets: (Rectangle \cup Point)^*</tt> and
     *            <tt>edgeFragmentSources.length == edgeFragmentTargets.length</tt>
     */
    private LayoutNode[] edgeTargets;

    double damper = 1.0; // A low damper value causes the graph to move
    // slowly

    private double maxMotion = 0; // Keep an eye on the fastest moving node to
    // see if the graph is
    // stabilizing

    private double lastMaxMotion = 0;

    private double motionRatio = 0; // It's sort of a ratio, equal to
    // lastMaxMotion/maxMotion-1

    private float workingRigidity = DEFAULT_RIGIDITY;

    /**
     * Rigidity has the same effect as the damper, except that it's a constant a
     * low rigidity value causes things to go slowly. a value that's too high
     * will cause oscillation
     * @invariant rigidity > 0
     */
    private float rigidity = DEFAULT_RIGIDITY;

    /** The prototype instance of this layouter. */
    public static final SpringLayouter PROTOTYPE = new SpringLayouter();
    /** Name of this layouter. */
    public static final String ACTION_NAME = "Spring layout";

    /** Text for stopping this layouter. */
    public static final String STOP_ACTION_NAME = "Stop layout";

    /**
     * Layout rigidity value in case none is provided.
     */
    public static final float DEFAULT_RIGIDITY = 2.0f;

    /**
     * Default time interval for this layout action (in ms).
     */
    public static final int DEFAULT_DURATION = 2000;

    /**
     * An epsilon float value, used as border case to decide whether a value is
     * "almost zero".
     */
    static private final float SMALL_VALUE = 0.1f;

    /** The damper decrease when we're damping slowly */
    static private final float SLOW_DAMPING = 0.0001f;

    /** The damper decrease when we're damping medium fast */
    static private final float MEDIUM_DAMPING = 0.003f;

    /** The damper decrease when we're damping fast */
    static private final float FAST_DAMPING = 0.01f;

    /** Bound for <tt>maxMotion</tt> below which we start damping medium */
    static private final float MEDIUM_DAMPING_MOTION_TRESHHOLD = 0.8f; // was
    // 0.4

    /** Bound for <tt>maxMotion</tt> below which we start damping fast */
    static private final float FAST_DAMPING_MOTION_TRESHHOLD = 0.4f; // was
    // 0.2

    /** Bound for <tt>damper</tt> below which we start damping fast */
    static private final float FAST_DAMPING_DAMPER_TRESHHOLD = 0.9f;

    // ---------------------- INSTANCE DEFINITIONS --------------------------

    private static final boolean DEBUG = false;

    private static final boolean MOVE_NODES_DEBUG = false;

    /** Maximal running time of the layouter (in milliseconds). */
    private static final int TIMEOUT = 2000;
}
