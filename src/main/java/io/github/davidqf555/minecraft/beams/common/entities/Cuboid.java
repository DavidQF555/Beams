package io.github.davidqf555.minecraft.beams.common.entities;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Must be convex
 */
public class Cuboid {

    private final Vector3d[] vertices;
    private final ConvexQuadrilateral3D[] sides;
    private ConvexPolygon2D[] slices;
    private AxisAlignedBB bounds;

    public Cuboid(Vector3d[] vertices, ConvexQuadrilateral3D[] sides) {
        this.vertices = vertices;
        this.sides = sides;
    }

    public static Cuboid fromBeam(BeamEntity beam) {
        Vector3d start = beam.position();
        Vector3d end = beam.getEnd();
        Vector3d center = end.subtract(start).normalize();
        Vector3d horizontal = center.cross(new Vector3d(Vector3f.YP)).normalize();
        if (horizontal.lengthSqr() == 0) {
            horizontal = new Vector3d(Vector3f.ZP);
        }
        Vector3d vertical = horizontal.cross(center);

        Vector3d[] vertices = new Vector3d[8];
        double startWidth = beam.getStartWidth();
        double startHeight = beam.getStartHeight();
        double endWidth = beam.getEndWidth();
        double endHeight = beam.getEndHeight();
        vertices[0] = start.add(horizontal.scale(startWidth / 2)).add(vertical.scale(startHeight / 2));
        vertices[1] = start.add(horizontal.scale(startWidth / 2)).subtract(vertical.scale(startHeight / 2));
        vertices[2] = start.subtract(horizontal.scale(startWidth / 2)).subtract(vertical.scale(startHeight / 2));
        vertices[3] = start.subtract(horizontal.scale(startWidth / 2)).add(vertical.scale(startHeight / 2));
        vertices[4] = end.add(horizontal.scale(endWidth / 2)).add(vertical.scale(endHeight / 2));
        vertices[5] = end.add(horizontal.scale(endWidth / 2)).subtract(vertical.scale(endHeight / 2));
        vertices[6] = end.subtract(horizontal.scale(endWidth / 2)).subtract(vertical.scale(endHeight / 2));
        vertices[7] = end.subtract(horizontal.scale(endWidth / 2)).add(vertical.scale(endHeight / 2));

        ConvexQuadrilateral3D[] sides = new ConvexQuadrilateral3D[6];
        sides[0] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0], vertices[2]}, {vertices[1], vertices[3]}}, center.reverse());
        sides[1] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[4], vertices[6]}, {vertices[5], vertices[7]}}, center);
        sides[2] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[0], vertices[7]}, {vertices[3], vertices[4]}}, vertices[4].subtract(vertices[0]).cross(horizontal.reverse()).normalize());
        sides[3] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[2], vertices[5]}, {vertices[1], vertices[6]}}, vertices[6].subtract(vertices[2]).cross(horizontal).normalize());
        sides[4] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[2], vertices[7]}, {vertices[3], vertices[6]}}, vertices[6].subtract(vertices[2]).cross(vertical.reverse()).normalize());
        sides[5] = new ConvexQuadrilateral3D(new Vector3d[][]{{vertices[4], vertices[1]}, {vertices[0], vertices[5]}}, vertices[4].subtract(vertices[0]).cross(vertical).normalize());

        return new Cuboid(vertices, sides);
    }

    public void doBlockEffect(Consumer<BlockPos> effect) {
        int startY = MathHelper.floor(getBounds().minY);
        ConvexPolygon2D[] slices = getSlices();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int i = 0; i < slices.length; i++) {
            // should never be null, for redundancy
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

    public boolean isColliding(AxisAlignedBB bounds) {
        for (Vector3d vertex : new Vector3d[]{
                new Vector3d(bounds.minX, bounds.minY, bounds.minZ),
                new Vector3d(bounds.minX, bounds.minY, bounds.maxZ),
                new Vector3d(bounds.minX, bounds.maxY, bounds.minZ),
                new Vector3d(bounds.minX, bounds.maxY, bounds.maxZ),
                new Vector3d(bounds.maxX, bounds.minY, bounds.minZ),
                new Vector3d(bounds.maxX, bounds.minY, bounds.maxZ),
                new Vector3d(bounds.maxX, bounds.maxY, bounds.minZ),
                new Vector3d(bounds.maxX, bounds.maxY, bounds.maxZ),
        }) {
            if (isColliding(vertex)) {
                return true;
            }
        }
        // check if edges of cuboid intersect planes of AABB, equivalent to checking for partial enclosing
        for (ConvexQuadrilateral3D side : sides) {
            for (Vector3d start : side.vertices[0]) {
                for (Vector3d end : side.vertices[1]) {
                    if (bounds.clip(start, end).isPresent()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // assumes normals pointed outward
    public boolean isColliding(Vector3d pos) {
        for (ConvexQuadrilateral3D side : sides) {
            Vector3d dir = side.vertices[0][0].subtract(pos).normalize();
            if (dir.dot(side.normal) < 0) {
                return false;
            }
        }
        return true;
    }

    public AxisAlignedBB getBounds() {
        if (bounds == null) {
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxZ = -Double.MAX_VALUE;
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
                if (vertex.y() < minY) {
                    minY = vertex.y();
                }
                if (vertex.z() > maxZ) {
                    maxZ = vertex.z();
                }
                if (vertex.z() < minZ) {
                    minZ = vertex.z();
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
        ConvexPolygon2D[] slices = new ConvexPolygon2D[MathHelper.floor(bounds.maxY) - MathHelper.floor(bounds.minY) + 1];
        double[] heights = Arrays.stream(vertices).mapToDouble(Vector3d::y).sorted().distinct().toArray();
        int index = 0;
        for (int i = 0; i < slices.length; i++) {
            // will be sorted, but can have duplicates
            List<Double> crit = new ArrayList<>();
            int low = MathHelper.floor(bounds.minY) + i;
            if (low >= bounds.minY) {
                crit.add((double) low);
            }
            int high = low + 1;
            while (index < heights.length && heights[index] < high) {
                crit.add(heights[index]);
                index++;
            }
            if (high <= bounds.maxY) {
                crit.add((double) high);
            }
            List<Point2D> points = new ArrayList<>();
            for (double val : crit) {
                for (ConvexQuadrilateral3D side : sides) {
                    if (side.within(val)) {
                        points.addAll(side.edgeIntersection(val));
                    }
                }
            }
            // points should never be empty, for redundancy
            if (!points.isEmpty()) {
                slices[i] = calculateConvexHull(points);
            }
        }
        return slices;
    }

    // points must be mutable and nonempty
    private static ConvexPolygon2D calculateConvexHull(List<Point2D> points) {
        List<Point2D> hull = grahamScan(points);
        hull.remove(hull.size() - 1);
        return new ConvexPolygon2D(hull.toArray(new Point2D[0]));
    }

    // points must be mutable and nonempty
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
        private final Vector3d normal;

        public ConvexQuadrilateral3D(Vector3d[][] vertices, Vector3d normal) {
            this.vertices = vertices;
            this.normal = normal;
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
                        double z = v1.z + (v2.z - v1.z) * (x - v1.x) / (v2.x - v1.x);
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
                double max = -Double.MAX_VALUE;
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
