package io.github.davidqf555.minecraft.beams.common.entities;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Must be convex
 */
public class Cuboid {

    private final Vector3d[][] vertices;
    /**
     * 3x2, with 3 groupings of non-adjacent planes
     */
    private final ConvexQuadrilateral3D[][] sides;
    private ConvexPolygon2D[] slices;
    private AxisAlignedBB bounds;

    public Cuboid(Vector3d[][] vertices) {
        this.vertices = vertices;
        sides = new ConvexQuadrilateral3D[3][2];
        sides[0][0] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0][0], vertices[0][2]}, {vertices[0][1], vertices[0][3]}});
        sides[0][1] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[1][0], vertices[1][2]}, {vertices[1][1], vertices[1][3]}});
        sides[1][0] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0][0], vertices[1][3]}, {vertices[1][0], vertices[0][3]}});
        sides[1][1] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0][1], vertices[1][2]}, {vertices[1][1], vertices[0][2]}});
        sides[2][0] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0][0], vertices[1][1]}, {vertices[1][0], vertices[0][1]}});
        sides[2][1] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0][2], vertices[1][3]}, {vertices[1][2], vertices[0][3]}});
    }

    public void doBlockEffect(Consumer<BlockPos> effect) {
        int startY = MathHelper.floor(getBounds().minY);
        ConvexPolygon2D[] slices = getSlices();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int i = 0; i < slices.length; i++) {
            if (slices[i] != null) {
                pos.setY(startY + i);
                slices[i].doEffect((x, z) -> {
                    pos.setX(x);
                    pos.setZ(z);
                    effect.accept(pos);
                });
            }
        }
    }

    // TODO
    public boolean isColliding(AxisAlignedBB bounds) {
        return false;
    }

    public AxisAlignedBB getBounds() {
        if (bounds == null) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxZ = Double.MIN_VALUE;
            for (ConvexQuadrilateral3D[] arr : sides) {
                for (ConvexQuadrilateral3D plane : arr) {
                    for (Vector3d[] vertices : plane.vertices) {
                        for (Vector3d vertex : vertices) {
                            if (vertex.x() > maxX) {
                                maxX = vertex.x();
                            }
                            if (vertex.x() < minX) {
                                minX = vertex.x();
                            }
                            if (vertex.y() > maxY) {
                                maxY = vertex.y();
                            }
                            if (vertex.y() < maxY) {
                                maxY = vertex.y();
                            }
                            if (vertex.z() > maxZ) {
                                maxZ = vertex.z();
                            }
                            if (vertex.z() < maxZ) {
                                maxZ = vertex.z();
                            }
                        }
                    }
                }
            }
            bounds = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return bounds;
    }

    private ConvexPolygon2D[] getSlices() {
        if (slices == null) {
            slices = calculateSlices();
        }
        return slices;
    }

    private ConvexPolygon2D[] calculateSlices() {
        AxisAlignedBB bounds = getBounds();
        ConvexPolygon2D[] slices = new ConvexPolygon2D[MathHelper.ceil(bounds.maxY) - MathHelper.floor(bounds.minY) + 1];
        double[] heights = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vector3d::y).sorted().distinct().toArray();
        int index = 0;
        for (int i = 0; i < slices.length; i++) {
            // will be sorted, but can have duplicates
            List<Double> crit = new ArrayList<>();
            int low = MathHelper.floor(bounds.minY) + i;
            if (low >= bounds.minY) {
                crit.add((double) low);
            }
            while (index < heights.length && heights[index] < low + 1) {
                crit.add(heights[index]);
                index++;
            }
            int high = low + 1;
            if (high <= bounds.maxY) {
                crit.add((double) high);
            }
            List<Point2D> points = new ArrayList<>();
            for (double val : crit) {
                for (ConvexQuadrilateral3D[] sides : this.sides) {
                    for (ConvexQuadrilateral3D side : sides) {
                        if (side.within(val)) {
                            points.addAll(side.edgeIntersection(val));
                        }
                    }
                }
            }
            // points should never be empty
            slices[i] = calculateConvexHull(points);
        }
        return slices;
    }

    private static ConvexPolygon2D calculateConvexHull(List<Point2D> points) {
        List<Point2D> hull = grahamScan(points);
        hull.remove(hull.size() - 1);
        return new ConvexPolygon2D(hull.toArray(new Point2D[0]));
    }

    private static List<Point2D> grahamScan(List<Point2D> points) {
        Point2D p = points.stream().min((p1, p2) -> {
            if (p1.y != p2.y) {
                return Double.compare(p1.y, p2.y);
            }
            return Double.compare(p1.x, p2.x);
        }).orElseThrow(IllegalArgumentException::new);
        points.sort((p1, p2) -> {
            double dX1 = p1.x - p.x;
            double dY1 = p1.y - p.y;
            double dX2 = p2.x - p.x;
            double dY2 = p2.y - p.y;
            double cross = dX1 * dY2 - dX2 * dY1;
            if (cross == 0) {
                return dX1 == dX2 ? Double.compare(dY1, dY2) : Double.compare(Math.abs(dX1), Math.abs(dX2));
            }
            return cross < 0 ? 1 : -1;
        });
        points.add(p);
        List<Point2D> hull = new ArrayList<>();
        for (Point2D point : points) {
            hull.add(point);
            while (hull.size() >= 3) {
                Point2D prev1 = hull.get(hull.size() - 2);
                Point2D prev2 = hull.get(hull.size() - 3);
                double cross = (prev1.x - prev2.x) * (point.y - prev2.y) - (prev1.y - prev2.y) * (point.x - prev2.x);
                if (cross == 0) {
                    hull.remove(hull.size() - 2);
                    break;
                } else if (cross > 0) {
                    break;
                }
                hull.remove(hull.size() - 2);
            }
        }
        return hull;
    }

    public static class ConvexQuadrilateral3D {

        private final Vector3d[][] vertices;

        private ConvexQuadrilateral3D(Vector3d[][] vertices) {
            this.vertices = vertices;
        }

        private Set<Point2D> edgeIntersection(double y) {
            Set<Point2D> points = new HashSet<>();
            for (Vector3d v1 : vertices[0]) {
                for (Vector3d v2 : vertices[1]) {
                    if (Math.min(v1.y, v2.y) > y || Math.max(v1.y, v2.y) < y) {
                        continue;
                    }
                    if (v1.y == v2.y) {
                        points.add(new Point2D(v1.x, v1.z));
                        points.add(new Point2D(v2.x, v2.z));
                    } else if (v1.x == v2.x) {
                        double z = v1.z + (v2.z - v1.z) * (y - v1.y) / (v2.y - v1.y);
                        points.add(new Point2D(v1.x, z));
                    } else {
                        double x = v1.x + (v2.x - v1.x) * (y - v1.y) / (v2.y - v1.y);
                        double z = v1.z + (v2.z - v1.z) * (x - v1.z) / (v2.x - v1.x);
                        points.add(new Point2D(x, z));
                    }
                }
            }
            return points;
        }

        private boolean within(double y) {
            boolean min = false;
            boolean max = false;
            for (Vector3d[] points : vertices) {
                for (Vector3d point : points) {
                    if (point.y() >= y) {
                        max = true;
                    }
                    if (point.y() <= y) {
                        min = true;
                    }
                }
            }
            return min && max;
        }

    }

    private static class Point2D {

        private final double x, y;

        private Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Point2D && ((Point2D) obj).x == x && ((Point2D) obj).y == y;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(x) + Double.hashCode(y);
        }

    }

    private static class ConvexPolygon2D {

        private final LineSegment2D[] lines;
        private final double[] x;

        // vertices must be ordered so that its neighbors are adjacent to it
        private ConvexPolygon2D(Point2D[] vertices) {
            lines = new LineSegment2D[vertices.length];
            x = new double[vertices.length];
            for (int i = 0; i < lines.length - 1; i++) {
                lines[i] = new LineSegment2D(vertices[i], vertices[i + 1]);
                x[i] = vertices[i].x;
            }
            lines[lines.length - 1] = new LineSegment2D(vertices[lines.length - 1], vertices[0]);
            x[vertices.length - 1] = vertices[vertices.length - 1].x;
            Arrays.sort(x);
        }

        public void doEffect(BiConsumer<Integer, Integer> effect) {
            int index = 0;
            for (int x = MathHelper.floor(this.x[0]); x <= this.x[this.x.length - 1]; x++) {
                Set<Double> crit = new HashSet<>();
                crit.add((double) x);
                crit.add(x + 1.0);
                while (index < this.x.length && this.x[index] < x + 1) {
                    crit.add(this.x[index]);
                    index++;
                }
                for (double add : this.x) {
                    if (add >= x && add < x + 1) {
                        crit.add(add);
                    }
                }
                // for extra security in case of floating point calculation issues
                boolean found = false;
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                for (LineSegment2D line : lines) {
                    for (double val : crit) {
                        if (line.within(val)) {
                            double y = line.getY(val);
                            if (y < min) {
                                min = y;
                            }
                            if (y > max) {
                                max = y;
                            }
                            found = true;
                        }
                    }
                }
                // should always be true
                if (found) {
                    for (int y = MathHelper.floor(min); y <= max; y++) {
                        effect.accept(x, y);
                    }
                }
            }
        }

    }

    private static class LineSegment2D {

        private final Point2D start, end;
        private final double slope;

        private LineSegment2D(Point2D start, Point2D end) {
            this.start = start;
            this.end = end;
            slope = (end.y - start.y) / (end.x - start.x);
        }

        private double getY(double x) {
            return start.y + slope * (x - start.x);
        }

        private boolean within(double x) {
            return x >= Math.min(start.x, end.x) && x <= Math.max(start.x, end.x);
        }

    }

}
