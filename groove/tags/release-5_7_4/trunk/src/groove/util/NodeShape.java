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
    /** Hexagonal shape. */
    HEXAGON {
        @Override
        Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py, Point2D q) {
            Point2D result = null;
            this.bounds = bounds;
            this.x2 = px;
            this.dx2 = q.getX() - px;
            this.y2 = py;
            this.dy2 = q.getY() - py;
            this.nonnull = false;
            if (q.getY() < bounds.getY()) {
                // q lies in the top half
                result = getTopLeft();
                if (result == null) {
                    result = getTop();
                }
                if (result == null) {
                    result = getTopRight();
                }
            } else if (q.getY() > bounds.getMaxY()) {
                // q lies in the bottom half
                result = getBottomLeft();
                if (result == null) {
                    result = getBottom();
                }
                if (result == null) {
                    result = getBottomRight();
                }
            } else if (px > q.getX()) {
                // q lies to the left
                result = getTop();
                if (result == null) {
                    result = getTopLeft();
                }
                if (result == null) {
                    result = getBottomLeft();
                }
                if (result == null) {
                    result = getBottom();
                }
            } else {
                // q lies to the right
                result = getTop();
                if (result == null) {
                    result = getTopRight();
                }
                if (result == null) {
                    result = getBottomRight();
                }
                if (result == null) {
                    result = getBottom();
                }
            }
            return result;
        }

        @Override
        Point2D getPerimeterPoint(double w, double h, double dx, double dy) {
            Point2D result = null;
            this.bounds = new Rectangle2D.Double(-w, -h, 2 * w, 2 * h);
            this.x2 = 0;
            this.dx2 = dx;
            this.y2 = 0;
            this.dy2 = dy;
            this.nonnull = true;
            double ratio = w / h - 2 * HEX_EXTEND_RATIO;
            if (dy < 0) {
                // q lies in the top half
                double angle = dx / dy;
                if (angle > ratio) {
                    result = getTopLeft();
                } else if (angle < -ratio) {
                    result = getTopRight();
                } else {
                    result = getTop();
                }
                assert result != null;
            } else if (dy > 0) {
                // point lies in bottom half
                double angle = dx / dy;
                if (angle < -ratio) {
                    result = getBottomLeft();
                } else if (angle > ratio) {
                    result = getBottomRight();
                } else {
                    result = getBottom();
                }
                assert result != null;
            } else if (dx < 0) {
                result = new Point2D.Double(-w, 0);
            } else {
                result = new Point2D.Double(w, 0);
            }
            return result;
        }

        /** Returns the intersection of the top left border. */
        private Point2D getTopLeft() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getX();
            double y1 = this.bounds.getY() + height / 2;
            double dx1 = extend;
            double dy1 = -height / 2;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of the top border. */
        private Point2D getTop() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getX() + extend;
            double y1 = this.bounds.getY();
            double dx1 = this.bounds.getWidth() - 2 * extend;
            double dy1 = 0;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of the top right border. */
        private Point2D getTopRight() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getMaxX() - extend;
            double y1 = this.bounds.getY();
            double dx1 = extend;
            double dy1 = height / 2;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of the bottom right border. */
        private Point2D getBottomRight() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getMaxX();
            double y1 = this.bounds.getY() + height / 2;
            double dx1 = -extend;
            double dy1 = height / 2;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of the bottom border. */
        private Point2D getBottom() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getMaxX() - extend;
            double y1 = this.bounds.getMaxY();
            double dx1 = -this.bounds.getWidth() - 2 * extend;
            double dy1 = 0;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of the bottom left border. */
        private Point2D getBottomLeft() {
            double height = this.bounds.getHeight();
            double extend = height * HEX_EXTEND_RATIO;
            double x1 = this.bounds.getX();
            double y1 = this.bounds.getY() + height / 2;
            double dx1 = extend;
            double dy1 = height / 2;
            return intersect(x1, y1, dx1, dy1);
        }

        /** Returns the intersection of a given vector {@code v1 = (x1,y1) + (dx1,dy1)}
         * with the stored vector {@code (x2,y2) + (dx2,dy2)}, provided the point
         * actually lies on the {@code v1} segment; or {@code null} otherwise.
         */
        private Point2D intersect(double x1, double y1, double dx1, double dy1) {
            Point2D result =
                lineIntersection(x1, y1, dx1, dy1, this.x2, this.y2, this.dx2, this.dy2);
            if (result != null) {
                double x = result.getX();
                double y = result.getY();
                if (x > x1 + EPS + Math.max(dx1, 0)) {
                    assert !this.nonnull;
                    result = null;
                } else if (x < x1 - EPS + Math.min(dx1, 0)) {
                    assert !this.nonnull;
                    result = null;
                } else if (y > y1 + EPS + Math.max(dy1, 0)) {
                    assert !this.nonnull;
                    result = null;
                } else if (y < y1 - EPS + Math.min(dy1, 0)) {
                    assert !this.nonnull;
                    result = null;
                }
            }
            return result;
        }

        private Rectangle2D bounds;
        private double x2, y2, dx2, dy2;
        /** Flag indicating that a null return value of {@link #intersect} is allowed. */
        private boolean nonnull;
        private static final double EPS = 0.001;
    },
    /** Ellipse (or circle) shape. */
    ELLIPSE {
        /* Overridden for ellipses. */
        @Override
        Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py, Point2D q) {
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
                    y = cy;
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
        Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py, Point2D q) {
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
                lineIntersection(px, py, dx, dy, startX, startY, endX - startX, endY - startY);
            return result;
        }

        /* Overridden for diamond shapes. */
        @Override
        public Point2D getPerimeterPoint(double w, double h, double dx, double dy) {
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
    final public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D p, Point2D q) {
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
    Point2D getPerimeterPoint(Rectangle2D bounds, double px, double py, Point2D q) {
        // distances from source point to left, right, top and bottom edge
        double dxRight = bounds.getMaxX() - px;
        double dxLeft = px - bounds.getMinX();
        double dyBottom = bounds.getMaxY() - py;
        double dyTop = py - bounds.getMinY();
        // angles from source point to upper left, upper right, bottom left, bottom right corner
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
     * @return Intersection point of the two lines, of <code>null</code> if
     * the lines do not intersect in the given interval.
     */
    public static Point2D lineIntersection(double x1, double y1, double dx1, double dy1, double x2,
        double y2, double dx2, double dy2) {
        Point2D result = null;
        double below = dx2 * dy1 - dx1 * dy2;
        if (below != 0) {
            double above = dx1 * (y2 - y1) - dy1 * (x2 - x1);
            double c2 = above / below;
            double x = x2 + dx2 * c2;
            double y = y2 + dy2 * c2;
            result = new Point2D.Double(x, y);
        }
        return result;
    }

    /** Ratio of the left and right extensions of a hexagon to the height. */
    public static final double HEX_EXTEND_RATIO = .2;
}
