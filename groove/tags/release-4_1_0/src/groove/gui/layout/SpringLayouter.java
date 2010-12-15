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
 * $Id: SpringLayouter.java,v 1.6 2008-01-30 09:33:00 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;

/**
 * Action to set up the standard touchgraph layout algorithm on a given
 * MyJGraph. Adapted from <tt>jgraph.com.pad.Touch</tt>
 * @author Gaudenz Alder and Arend Rensink
 * @version $Revision$
 */
public class SpringLayouter extends AbstractLayouter {
    /** Constructs a template spring layouter. */
    public SpringLayouter() {
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
    private SpringLayouter(String name, JGraph jgraph, float rigidity) {
        super(name, jgraph);
        // setEnabled(true);
        // setDuration(duration);
        setRigidity(rigidity);
    }

    public Layouter newInstance(JGraph jgraph) {
        return new SpringLayouter(this.name, jgraph, this.rigidity);
    }

    /**
     * @require rigidity > 0
     */
    public void setRigidity(float rigidity) {
        if (rigidity > 0) {
            this.rigidity = rigidity;
        }
    }

    /**
     * Starts layouting in a parallel thread; or stops the current layouter
     * thread if one is running.
     */
    public void start(boolean complete) {
        if (getText() != null && getText().equals(STOP_ACTION_NAME)) {
            stop();
        } else {
            stop();
            if (complete) {
                reset();
            }
            this.relaxer = new Thread() {
                @Override
                public void run() {
                    // Thread me = Thread.currentThread();
                    SpringLayouter.this.damper = 1.0;
                    prepare();
                    while (SpringLayouter.this.damper > 0) {
                        relax();
                        Thread.yield();
                    }
                    finish();
                    setText(SpringLayouter.this.name);
                }
            };
            this.relaxer.setPriority(Thread.MIN_PRIORITY);
            setText(STOP_ACTION_NAME);
            // because I don't see any way to make it thread safe
            // (the Renderers are global objects and spoil everything)
            // for now we just call this within the event dispatch thread
            this.relaxer.run();
            // if (duration > 0) {
            // stopTask = new TimerTask() {
            // public void run() {
            // stop();
            // }
            // };
            // layoutTimer.schedule(stopTask, duration);
            // }
        }
    }

    /**
     * Stops the automatic layout process. Stores the bounds back from the view
     * into the model, and sets the nodes to moveable.
     */
    public synchronized void stop() {
        if (isRunning()) {
            if (DEBUG) {
                System.out.println("Stopping automatic layout");
            }
            this.relaxer.interrupt();
            boolean joined = false;
            while (!joined) {
                try {
                    this.relaxer.join();
                    joined = true;
                } catch (InterruptedException exc) {
                    // proceed
                }
            }
            this.relaxer = null;
            // freeze();
        }
    }

    private boolean isRunning() {
        return this.relaxer != null && this.relaxer.isAlive();
    }

    /**
     * Sets the action name to reflect the duration.
     * @param duration the duration of the temporary layout action, in
     *        milliseconds
     * @require <tt>duration >= 0</tt>
     */
    protected void setName(int duration) {
        String actionName = getName();
        if (duration == 0) {
            actionName += " (until stable)";
        } else if (duration > 0 && duration != DEFAULT_DURATION) {
            String durationUnit = duration / 1000 == 1 ? " second" : " seconds";
            actionName += " (" + duration / 1000 + durationUnit + ")";
        }
        setName(actionName);
    }

    @Override
    protected void prepare() {
        super.prepare();
        if (DEBUG) {
            System.out.println("Starting automatic layout");
        }
        // 
        // initialise the layoutables, positions and deltas
        this.deltaMap.clear();
        int layoutableIndex = 0;
        this.layoutables = new Layoutable[this.toLayoutableMap.size()];
        this.positions = new Point2D.Double[this.toLayoutableMap.size()];
        this.deltas = new Point2D.Float[this.toLayoutableMap.size()];
        for (Map.Entry<Object,Layoutable> toLayoutableEntry : this.toLayoutableMap.entrySet()) {
            // Object key = toLayoutableEntry.getKey();
            Layoutable layoutable = toLayoutableEntry.getValue();
            this.layoutables[layoutableIndex] = layoutable;
            // CellView vertexView = jview.getMapping(cell, false);
            // assert vertexView != null : "Node " + graphVertices[i] + " does
            // not have a view";
            if (!this.immovableSet.contains(layoutable)) {
                this.deltas[layoutableIndex] = new Point2D.Float(0, 0);
                this.deltaMap.put(layoutable, this.deltas[layoutableIndex]);
            }
            double p2X = layoutable.getX() + layoutable.getWidth() / 2;
            double p2Y = layoutable.getY() + layoutable.getHeight() / 2;
            this.positions[layoutableIndex] = new Point2D.Double(p2X, p2Y);
            layoutableIndex++;
        }
        // initialise the edge fragment arrays
        GraphLayoutCache layoutCache = this.jgraph.getGraphLayoutCache();
        // Object[] graphEdges = jgraph.getEdges(jgraph.getRoots());
        List<Layoutable> edgeFragmentSourceList = new LinkedList<Layoutable>();
        List<Layoutable> edgeFragmentTargetList = new LinkedList<Layoutable>();
        for (int i = 0; i < this.jmodel.getRootCount(); i++) {
            JCell jCell = (JCell) this.jmodel.getRootAt(i);
            if (jCell instanceof JEdge && jCell.isVisible()
                && !this.jmodel.isGrayedOut(jCell)) {
                EdgeView edgeView =
                    (EdgeView) layoutCache.getMapping(jCell, false);
                List<?> edgePoints = edgeView.getPoints();
                for (int j = 0; j < edgePoints.size() - 1; j++) {
                    edgeFragmentSourceList.add(getLayoutableFor(edgePoints.get(j)));
                    edgeFragmentTargetList.add(getLayoutableFor(edgePoints.get(j + 1)));
                }
            }
        }
        assert !edgeFragmentSourceList.contains(null);
        assert !edgeFragmentTargetList.contains(null);
        this.edgeFragmentSources =
            edgeFragmentSourceList.toArray(new Layoutable[edgeFragmentSourceList.size()]);
        this.edgeFragmentTargets =
            edgeFragmentTargetList.toArray(new Layoutable[edgeFragmentTargetList.size()]);
    }

    /**
     * Returns the layoutable corresponding to a given <tt>PortView</tt> or
     * <tt>Point</tt>.
     * @param point the <tt>PortView</tt> or <tt>Point</tt>
     * @ensure <tt>result == null || result instanceof Layoutable</tt>
     */
    private Layoutable getLayoutableFor(Object point) {
        if (point instanceof Point2D) {
            return this.toLayoutableMap.get(point);
        } else {
            assert point instanceof PortView;
            Object cell = ((PortView) point).getParentView().getCell();
            assert cell instanceof DefaultGraphCell;
            return this.toLayoutableMap.get(cell);
        }
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
        for (int i = 0; i < this.edgeFragmentSources.length; i++) {
            Layoutable bf = this.edgeFragmentSources[i];
            Layoutable bt = this.edgeFragmentTargets[i];
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
            Layoutable from = this.layoutables[i];
            Point2D.Double bf = this.positions[i];
            float fromDx = 0;
            float fromDy = 0;
            for (int j = i + 1; j < this.layoutables.length; j++) {
                Layoutable to = this.layoutables[j];
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
                        if (from instanceof PointLayoutable
                            || to instanceof PointLayoutable) {
                            len += 5;
                        }
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
            Layoutable key = this.layoutables[i];
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
                    key.setLocation(
                        Math.max(0, (int) position.x - key.getWidth() / 2),
                        Math.max(0, (int) position.y - key.getHeight() / 2));
                    this.repaintNeeded = true;
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
        if (this.workingRigidity != this.rigidity) {
            this.workingRigidity = this.rigidity; // update rigidity
        }
        if (this.repaintNeeded) {
            this.jgraph.repaint();
            this.repaintNeeded = false;
        }
    }

    private void shiftDelta(Layoutable key, double dx, double dy) {
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
    private Layoutable[] layoutables;

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
    private final Map<Layoutable,Point2D.Float> deltaMap =
        new HashMap<Layoutable,Point2D.Float>();

    /**
     * Source vertices or midpoints of the edge fragments in this graph.
     * Transient value; only used while layout is running.
     * @invariant <tt>edgeFragmentSources: (Rectangle \cup Point)^*</tt>
     */
    private Layoutable[] edgeFragmentSources;

    /**
     * Target midpoints or vertices of the edge fragments in this graph.
     * Transient value; only used while layout is running.
     * @invariant <tt>edgeFragmentTargets: (Rectangle \cup Point)^*</tt> and
     *            <tt>edgeFragmentSources.length == edgeFragmentTargets.length</tt>
     */
    private Layoutable[] edgeFragmentTargets;

    /** A map from movable points to their calculated positions (as float points) */
    // private final Map positions = new IdentityHashMap();
    private Thread relaxer;

    // private boolean allowedToRun = false;

    // private boolean frozen = true;
    private boolean repaintNeeded = false;

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
}
