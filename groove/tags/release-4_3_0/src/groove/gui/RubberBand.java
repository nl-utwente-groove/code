/*
 * Copyright (c) 2005, romain guy (romain.guy@jext.org) and craig wickesser (craig@codecraig.com)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package groove.gui;

import groove.gui.jgraph.JAttr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 * An abstract implementation of {@link RubberBand} which handles
 * the basic drawing/setup of the rubber band.
 *
 * @author rwickesser
 * @since 1.0
 * $Revision: 1.2 $
 */
public class RubberBand extends MouseInputAdapter {
    /**
     * Creates a new <code>RubberBand</code> and sets the canvas
     * @param canvas    the <code>RubberBandCanvas</code> on which the rubber band
     *                  will be drawn
     */
    public RubberBand(JComponent canvas) {
        this.canvas = canvas;
        this.bounds = new Rectangle();
    }

    /** Returns the bounds of the rubber band. */
    public Rectangle getBounds() {
        return this.bounds.getBounds();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int w = 0;
        int h = 0;

        // adjust x and width
        if (this.pressX < x) {
            int tmp = x;
            x = this.pressX;
            w = tmp - x;
        } else {
            w = this.pressX - x;
        }

        // adjust y and height
        if (this.pressY < y) {
            int tmp = y;
            y = this.pressY;
            h = tmp - y;
        } else {
            h = this.pressY - y;
        }

        // update rubber band size and location
        update(x, y, w, h);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isAllowed(e)) {
            this.pressX = e.getX();
            this.pressY = e.getY();
            this.bounds.setBounds(this.pressX, this.pressY, 0, 0);
            //            this.canvas.addMouseMotionListener(this);
            this.active = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.active) {
            // only report if the rectangle size is within an error margin
            if (this.bounds.width > 3 && this.bounds.height > 3) {
                stopRubberBand(e);
            }
            update(-1, -1, 0, 0);
            this.canvas.removeMouseMotionListener(this);
            this.active = false;
        }
    }

    /** 
     * Updates the bounds of the rubber band,
     * and optionally repaints the dirty area.
     */
    private void update(int x, int y, int width, int height) {
        Rectangle dirty = (Rectangle) this.bounds.clone();
        this.bounds.setBounds(x, y, width, height);
        dirty = dirty.union(this.bounds);
        // make sure the dirty area includes the contour of the rubber band
        dirty.x -= 1;
        dirty.y -= 1;
        dirty.height += 2;
        dirty.width += 2;
        repaint(dirty);
    }

    /** Callback method that makes sure the dirty rectangle gets repainted. */
    protected void repaint(Rectangle dirty) {
        this.canvas.repaint(dirty);
    }

    /** Renders the rubber band on the given graphics object. */
    public void draw(Graphics g) {
        if (this.bounds.width >= 0) {
            Color oldColor = g.getColor();
            g.setColor(JAttr.RUBBER_FOREGROUND);
            g.drawRect(this.bounds.x, this.bounds.y, this.bounds.width,
                this.bounds.height);
            g.setColor(JAttr.RUBBER_BACKGROUND);
            g.fillRect(this.bounds.x, this.bounds.y, this.bounds.width,
                this.bounds.height);
            g.setColor(oldColor);
        }
    }

    /** Callback method to determine whether the rubber band may be started. */
    protected boolean isAllowed(MouseEvent event) {
        return true;
    }

    /** Callback method invoked after the rubber band is released. */
    protected void stopRubberBand(MouseEvent event) {
        // does nothing
    }

    private boolean active;
    /** the canvas where the rubber band will be drawn onto */
    private final JComponent canvas;

    /** maintains the size and location of the rubber band */
    private final Rectangle bounds;
    /** stores the x coordinate of the mouse pressed event */
    private int pressX;
    /** stores the y coordinate of the mouse pressed event */
    private int pressY;

}