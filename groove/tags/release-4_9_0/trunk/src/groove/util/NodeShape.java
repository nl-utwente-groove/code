/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Geometry shapes.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum NodeShape {
    /** Rounded rectangle shape. */
    ROUNDED,
    /** Ellipse (or circle) shape. */
    ELLIPSE {
        /* Overridden for ellipses. */
        @Override
        Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py,
                Point2D q) {
            // ellipse given by (x-cx)^2*w^2 + (y-cy)^2*h^2 = h^2*w^2
            // with (cx,cy) the centre, w the width and h the height
            double cx = bounds.getCenterX();
            double cy = bounds.getCenterY();
            double x, y;
            double h = bounds.getWidth() / 2;
            double h2 = h * h;
            double w = bounds.getHeight() / 2;
            double w2 = w * w;
            // line given by dy*x - dx*y = dc
            // with dy = qy-py, dx = qx-px, dc = px*qy-py*qx
            double qx = q.getX();
            double qy = q.getY();
            double dx = qx - px;
            double dy = qy - py;
            double dc = px * qy - py * qx;
            // check for vertical lines
            if (dx == 0) {
                if (dy == 0) {
                    x = cx + w;
                    y = 0;
                } else {
                    x = dc / dy;
                    // we solve an equation A*y^2 + B*y + C = 0
                    double A = h2;
                    double B = -2 * h2 * cy;
                    double C = w2 * (x - cx) * (x - cx) + h2 * (cy * cy - w2);
                    // DQ = sqrt(B^2 - 4*A*C)
                    double DQ = Math.sqrt(B * B - 4 * A * C);
                    y = (-B + DQ) / (2 * A);
                    if (Math.signum(y - py) != Math.signum(dy)) {
                        y = (-B - DQ) / (2 * A);
                    }
                }
            } else {
                // line given by y = k*x + m
                // with k = dy/dx, m= -dc/dx
                double k = dy / dx;
                double m = -dc / dx;
                // we solve an equation A*x^2 + B*x + C = 0
                // auxiliary term for cy-m
                double cym = cy - m;
                double A = w2 + h2 * k * k;
                double B = -2 * (w2 * cx + h2 * k * cym);
                double C = w2 * cx * cx + h2 * (cym * cym - w2);
                // DQ = sqrt(B^2 - 4*A*C)
                double DQ = Math.sqrt(B * B - 4 * A * C);
                x = (-B + DQ) / (2 * A);
                if (Math.signum(x - px) != Math.signum(dx)) {
                    x = (-B - DQ) / (2 * A);
                }
                y = x * k + m;
            }
            return new Point2D.Double(x, y);
        }

        @Override
        Point2D getPerimeterPoint(double w, double h, double dx, double dy) {
            double x, y;
            if (dx == 0) {
                if (dy == 0) {
                    x = w;
                    y = 0;
                } else {
                    x = 0;
                    y = Math.signum(dy) * h;
                }
            } else if (dy == 0) {
                x = Math.signum(dx) * w;
                y = 0;
            } else {
                double dist = Math.sqrt(dx * dx + dy * dy);
                x = dx / dist * w;
                y = dy / dist * h;
            }
            return new Point2D.Double(x, y);
        }

        /* We can do better than calling getPerimeterPoint. */
        @Override
        public double getRadius(Rectangle2D bounds, double dx, double dy) {
            double result;
            double w = bounds.getWidth() / 2;
            double h = bounds.getHeight() / 2;
            if (dx == 0) {
                result = h;
            } else if (dy == 0) {
                result = w;
            } else {
                double dx2 = dx * dx;
                double dy2 = dy * dy;
                result = Math.sqrt((w * w * dx2 + h * h * dy2) / (dx2 + dy2));
            }
            return result;
        }
    },

    /** Diamond shape. */
    DIAMOND {
        /* Overridden for diamond shapes. */
        @Override
        Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py,
                Point2D q) {
            double cx = bounds.getCenterX();
            double cy = bounds.getCenterY();
            // angles from p to top, right, bottom and left diamond point
            double tPhi = Math.atan2(bounds.getMinY() - py, cx - px);
            double rPhi = Math.atan2(cy - py, bounds.getMaxX() - px);
            double bPhi = Math.atan2(bounds.getMaxY() - py, cx - px);
            double lPhi = Math.atan2(cy - py, bounds.getMinX() - px);
            // compute angle from p to q
            double dx = q.getX() - px;
            double dy = q.getY() - py;
            double alpha = Math.atan2(dy, dx);
            // compute edge line fragment
            double startX, startY, endX, endY;
            boolean bl = lPhi < 0 && alpha < lPhi;
            if (alpha < tPhi && !bl || lPhi > 0 && alpha > lPhi) { // top left edge
                startX = bounds.getMinX();
                startY = cy;
                endX = cx;
                endY = bounds.getMinY();
            } else if (alpha < rPhi && !bl) { // top right edge
                startX = cx;
                startY = bounds.getMinY();
                endX = bounds.getMaxX();
                endY = cy;
            } else if (alpha < bPhi && !bl) { // bottom right edge
                startX = bounds.getMaxX();
                startY = cy;
                endX = cx;
                endY = bounds.getMaxY();
            } else { // Bottom left edge
                startX = cx;
                startY = bounds.getMaxY();
                endX = bounds.getMinX();
                endY = cy;
            }
            Point2D result =
                lineIntersection(px, py, dx, dy, startX, startY, endX - startX,
                    endY - startY);
            return result;
        }

        /* Overridden for diamond shapes. */
        @Override
        public Point2D getPerimeterPoint(double w, double h, double dx,
                double dy) {
            double x, y;
            if (dx == 0) {
                x = 0;
                y = dy < 0 ? -h : h;
            } else if (dy == 0) {
                x = dx < 0 ? -w : w;
                y = 0;
            } else {
                // line from (0,0) to (dx,dy) described by y=r*x with r=dy/dx
                double r = dy / dx;
                // top right edge described by y = s*x + h with s=h/w
                double s = h / w;
                if (dx < 0 && dy < 0) {
                    // top left edge; y = -s*x - h
                    x = -h / (r + s);
                } else if (dy < 0) {
                    // top right edge; y = s*x - h
                    x = -h / (r - s);
                } else if (dx < 0) {
                    // bottom left edge; y = s*x + h
                    x = h / (r - s);
                } else {
                    // bottom right edge; y = -s*x + h
                    x = h / (r + s);
                }
                y = r * x;
            }
            return new Point2D.Double(x, y);
        }
    },

    /** Sharp-cornered rectangle shape. */
    RECTANGLE,

    /** Oval shape (rounded rectangle with larger rounding arc). */
    OVAL;

    /**
     * Computes the perimeter point on this shape, lying on the line from 
     * a given source point in the direction of a target point. 
     * If the source and target point coincide, the point to the east of
     * the source point is returned.
     * @param bounds bounds of the shape
     * @param p source point;
     * may be {@code null}, in which case the centre of the bounds is used
     * @param q target point
     */
    final public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D p,
            Point2D q) {
        Point2D result;
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        if (p == null || p.getX() == cx && p.getY() == cy) {
            result = getPerimeterPoint(bounds, q);
        } else {
            result = getPerimeterPoint(bounds, p.getX(), p.getY(), q);
        }
        return result;
    }

    /**
     * Computes the perimeter point on this shape, lying on the line from 
     * a given source point in the direction of a target point. 
     * If the source and target point coincide, the point to the east of
     * the source point is returned.
     * @param bounds bounds of the shape
     * @param px x-coordinate of source point;
     * @param py y-coordinate of source point;
     * @param q target point
     */
    Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py,
            Point2D q) {
        // distances from source point to left, right, top and bottom edge
        double dxRight = bounds.getMaxX() - px;
        double dxLeft = px - bounds.getMinX();
        double dyBottom = bounds.getMaxY() - py;
        double dyTop = py - bounds.getMinY();
        // angles to upper left, upper right, bottom left, bottom right corner
        double urPhi = Math.atan2(-dyTop, dxRight);
        double ulPhi = Math.atan2(-dyTop, -dxLeft);
        double brPhi = Math.atan2(dyBottom, dxRight);
        double blPhi = Math.atan2(dyBottom, -dxLeft);
        // compute angle from source to nextPoint
        double dx = q.getX() - px; // Compute Angle
        double dy = q.getY() - py;
        double alpha = Math.atan2(dy, dx);
        double x, y;
        double pi = Math.PI;
        if (alpha < ulPhi || alpha > blPhi) { // Left edge
            x = px - dxLeft;
            y = py - dxLeft * Math.tan(alpha);
        } else if (alpha < urPhi) { // Top Edge
            y = py - dyTop;
            x = px - dyTop * Math.tan(pi / 2 - alpha);
        } else if (alpha < brPhi) { // Right Edge
            x = px + dxRight;
            y = py + dxRight * Math.tan(alpha);
        } else { // Bottom Edge
            y = py + dyBottom;
            x = px + dyBottom * Math.tan(pi / 2 - alpha);
        }
        return new Point2D.Double(x, y);
    }

    /**
     * Computes the perimeter point on this shape, lying on the line from 
     * the centre of the shape into the direction of a target point. 
     * If the source and target point coincide, the point to the east of
     * the source point is returned.
     * @param bounds bounds of the shape
     * @param q target point
     */
    Point2D getPerimeterPoint(Rectangle2D bounds, Point2D q) {
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        // direction of q from centre
        double dx = q.getX() - cx;
        double dy = q.getY() - cy;
        double w = bounds.getWidth() / 2;
        double h = bounds.getHeight() / 2;
        Point2D result = getPerimeterPoint(w, h, dx, dy);
        result.setLocation(result.getX() + cx, result.getY() + cy);
        return result;
    }

    /**
     * Computes the perimeter point on this shape, lying on the line from 
     * the origin {@code (0,0)} into a given direction.
     * If the direction is {@code (0,0)}, the point to the east of
     * the origin is returned.
     * @param w horizontal radius (width) of the shape
     * @param h vertical radius (height) of the shape
     * @param dx x-coordinate of direction of the requested perimeter point
     * @param dy y-coordinate of direction of the requested perimeter point
     */
    Point2D getPerimeterPoint(double w, double h, double dx, double dy) {
        // coordinates of perimeter point
        double x, y;
        if (dx == 0) {
            if (dy == 0) {
                x = w;
                y = 0;
            } else {
                x = 0;
                y = dy < 0 ? -h : h;
            }
        } else if (dy == 0) {
            x = dx < 0 ? -w : w;
            y = 0;
        } else {
            // slope towards bottom right hand corner
            double s = h / w;
            // line to q described by y = r*x, where r = dy/dx
            double r = dy / dx;
            if (s < Math.abs(r)) {
                if (dy < 0) {
                    // top edge
                    y = -h;
                } else {
                    // bottom edge
                    y = h;
                }
                x = y / r;
            } else {
                if (dx < 0) {
                    // left edge
                    x = -w;
                } else {
                    // right edge
                    x = w;
                }
                y = x * r;
            }
        }
        return new Point2D.Double(x, y);
    }

    /** Calculates the radius for this shape, in a given direction. */
    public double getRadius(Rectangle2D bounds, double dx, double dy) {
        double result;
        double w = bounds.getWidth() / 2;
        double h = bounds.getHeight() / 2;
        if (dx == 0) {
            result = h;
        } else if (dy == 0) {
            result = w;
        } else {
            Point2D point = getPerimeterPoint(w, h, dx, dy);
            double px = point.getX();
            double py = point.getY();
            result = Math.sqrt(px * px + py * py);
        }
        return result;
    }

    /**
     * Computes the intersection of two lines.
     * @param x1 Start point of the first line (x-coordinate)
     * @param y1 Start point of the first line (y-coordinate)
     * @param dx1 vector of the first line (x-direction)
     * @param dy1 vector of the first line (y-direction)
     * @param x2 Start point of the second line (x-coordinate)
     * @param y2 Start point of the second line (y-coordinate)
     * @param dx2 vector of the second line (x-direction)
     * @param dy2 vector of the second line (y-direction)
     * @return Intersection point of the two lines, of <code>null</code> if they
     *         are parallel
     */
    public static Point2D lineIntersection(double x1, double y1, double dx1,
            double dy1, double x2, double y2, double dx2, double dy2) {
        double above = dx1 * (y2 - y1) - dy1 * (x2 - x1);
        double below = dx2 * dy1 - dx1 * dy2;
        if (below == 0) {
            // the lines are parallel
            return null;
        } else {
            double c2 = above / below;
            double x = x2 + dx2 * c2;
            double y = y2 + dy2 * c2;
            return new Point2D.Double(x, y);
        }
    }
}
