/*
 * @(#)MyPortView.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder All rights reserved.
 * 
 * See LICENSE file in distribution for licensing details of this source file
 */
package groove.abstraction.neigh.gui.jgraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;

/**
 * View renderer for shape ports.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJPortView extends PortView {

    private static final float WIDTH = 8.0f;
    private static final float HEIGHT = 8.0f;

    private static EdgeSigPortRenderer renderer = new EdgeSigPortRenderer();

    private Shape shape;

    /** Basic constructor, defers to super class. */
    public ShapeJPortView(Object cell) {
        super(cell);
        this.getShape();
    }

    private Shape getShape() {
        if (this.shape == null) {
            this.shape = new Ellipse2D.Float(0.0f, 0.0f, WIDTH, HEIGHT);
        }
        return this.shape;
    }

    /** Returns the bounds for the port view. */
    @Override
    public Rectangle2D getBounds() {
        if (this.shape != null) {
            Point2D pt = (Point2D) getLocation().clone();
            Rectangle2D bounds = new Rectangle2D.Double();
            bounds.setFrame(pt.getX() - WIDTH / 2, pt.getY() - HEIGHT / 2,
                WIDTH, HEIGHT);
            return bounds;
        }
        return super.getBounds();
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    @Override
    public Point2D getLocation(EdgeView edge, Point2D nearest) {
        CellView vertex = getParentView();
        Rectangle2D r = vertex.getBounds();
        Point2D pos = this.getPortPosition(r);
        if (edge != null) {
            assert edge instanceof ShapeJEdgeView;
            ShapeJEdgeView edgeView = (ShapeJEdgeView) edge;
            ShapeJEdge jEdge = edgeView.getShapeJEdge();
            boolean srcVertex = edgeView.isSrcVertex((ShapeJVertexView) vertex);
            boolean tgtVertex = edgeView.isTgtVertex((ShapeJVertexView) vertex);
            if (nearest != null
            /*&& ((srcVertex && jEdge.isMainSrc()) || (tgtVertex && jEdge.isMainTgt()))*/) {
                pos = vertex.getPerimeterPoint(edge, null, nearest);
                Point2D newOffset = this.computeNewOffset(r, pos);
                GraphConstants.setOffset(this.allAttributes, newOffset);
            }
        }
        return pos;
    }

    private Point2D getPortPosition(Rectangle2D r) {
        Point2D offset = GraphConstants.getOffset(this.allAttributes);
        double x = offset.getX();
        double y = offset.getY();
        x = x * (r.getWidth() - 1) / GraphConstants.PERMILLE;
        y = y * (r.getHeight() - 1) / GraphConstants.PERMILLE;
        return new Point2D.Double(r.getX() + x, r.getY() + y);
    }

    private Point2D computeNewOffset(Rectangle2D r, Point2D pos) {
        double x = pos.getX() - r.getX();
        double y = pos.getY() - r.getY();
        x = (x * GraphConstants.PERMILLE) / (r.getWidth() - 1);
        y = (y * GraphConstants.PERMILLE) / (r.getHeight() - 1);
        return new Point2D.Double(x, y);
    }

    /** Port renderer. Draws a small black circle. */
    public static class EdgeSigPortRenderer extends PortRenderer {

        private ShapeJPortView view;

        @Override
        public Component getRendererComponent(org.jgraph.JGraph graph,
                CellView view, boolean sel, boolean focus, boolean preview) {
            assert view instanceof ShapeJPortView;
            this.view = (ShapeJPortView) view;
            return this;
        }

        @Override
        public void paint(Graphics g) {
            assert g instanceof Graphics2D;
            g.setColor(Color.BLACK);
            ((Graphics2D) g).fill(this.view.getShape());
        }

    }

}
