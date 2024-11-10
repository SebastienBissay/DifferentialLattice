package lattice;

import processing.core.PApplet;
import processing.core.PVector;
import processing.data.IntList;
import utree.UTree;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;

public class Lattice {
    private static PApplet pApplet;

    public ArrayList<Point> points;
    UTree grid;

    public Lattice() {
        points = new ArrayList<>();
        grid = new UTree();
    }

    public static void setPApplet(PApplet pApplet) {
        Lattice.pApplet = pApplet;
    }

    public void add(PVector p) {
        Point pt = new Point(p);
        points.add(pt);
        grid.add(pt);
    }

    public void computeNeighbours() {
        ArrayList<Triangle> DT = new ArrayList<>();
        Point STA = new Point(new PVector(LATTICE_MARGIN, LATTICE_MARGIN));
        Point STB = new Point(new PVector(2 * WIDTH - LATTICE_MARGIN, LATTICE_MARGIN));
        Point STC = new Point(new PVector(LATTICE_MARGIN, 2 * HEIGHT - LATTICE_MARGIN));
        DT.add(new Triangle(STA, STB, STC));
        for (Point p : points) {
            p.getNeighbours().clear();

            ArrayList<Edge> edges = new ArrayList<>();
            for (int i = DT.size() - 1; i >= 0; i--) {
                Triangle t = DT.get(i);
                if (t.isInCircumCenter(p)) {
                    for (int j = 0; j < 3; j++) {
                        edges.add(new Edge(t.getVertices()[j], t.getVertices()[(j + 1) % 3]));
                    }
                    DT.remove(i);
                }
            }

            for (int i = edges.size() - 1; i >= 0; i--) {
                boolean isDouble = false;
                for (int j = 0; j < i; j++) {
                    if (edges.get(j).equals(edges.get(i))) {
                        isDouble = true;
                        edges.remove(j);
                        i--;
                        break;
                    }
                }

                if (isDouble) {
                    edges.remove(i);
                }
            }

            edges.forEach(e -> DT.add(new Triangle(e.a(), e.b(), p)));
        }

        DT.removeIf(t -> t.contains(STA) || t.contains(STB) || t.contains(STC));

        ArrayList<Edge> removed = new ArrayList<>();
        for (Triangle t : DT) {
            ArrayList<Edge> edges = new ArrayList<>();
            IntStream.range(0, 3)
                    .forEach(i -> edges.add(new Edge(t.getVertices()[i], t.getVertices()[(i + 1) % 3])));

            for (Edge e : edges) {
                if (!e.a().getNeighbours().contains(e.b())) {
                    e.a().getNeighbours().add(e.b());
                }
                if (!e.b().getNeighbours().contains(e.a())) {
                    e.b().getNeighbours().add(e.a());
                }
            }

            float l0 = edges.get(0).magSq();
            float l1 = edges.get(1).magSq();
            float l2 = edges.get(2).magSq();
            if (l0 < l1) {
                if (l1 < l2) {
                    removed.add(edges.get(2));
                } else {
                    removed.add(edges.get(1));
                }
            } else {
                if (l0 < l2) {
                    removed.add(edges.get(2));
                } else {
                    removed.add(edges.get(0));
                }
            }
        }
        removed.forEach(e -> {
            e.a().getNeighbours().remove(e.b());
            e.b().getNeighbours().remove(e.a());
        });
    }

    public void render() {
        points.forEach(p -> p.render(pApplet));
    }

    public void computeForces() {
        for (Point p : points) {
            p.computeForce();

            for (int i = max(0, floor((p.getPosition().x - REPULSION_RADIUS) / U_TREE_CELL_WIDTH));
                 i <= min(grid.nCellX - 1, floor((p.getPosition().x + REPULSION_RADIUS) / U_TREE_CELL_WIDTH));
                 i++) {
                for (int j = max(0, floor((p.getPosition().y - REPULSION_RADIUS) / U_TREE_CELL_HEIGHT));
                     j <= min(grid.nCellY - 1, floor((p.getPosition().y + REPULSION_RADIUS) / U_TREE_CELL_HEIGHT));
                     j++) {

                    for (Point q : grid.cells[i][j].getPoints()) {
                        if (p != q) {
                            PVector v = PVector.sub(p.getPosition(), q.getPosition());
                            if (v.magSq() <= sq(REPULSION_RADIUS)) {
                                v.setMag(LATTICE_FORCE_MAGNITUDE_MULTIPLIER / v.magSq());
                                v.limit(LATTICE_FORCE_LIMIT);
                                p.setForce(PVector.add(p.getForce(), v));
                            }
                        }
                    }
                }
            }
        }
    }

    public void move() {
        points.forEach(Point::move);
        grid.clear();
        grid.add(points);
    }

    public void randomAdd() {
        IntList densityCount = new IntList();
        for (Point p : points) {
            int d = floor(NOISE_MULTIPLIER
                    * pApplet.noise(p.getPosition().x * NOISE_SCALE, p.getPosition().y * NOISE_SCALE));
            d += floor(WEIGHT_TOWARDS_CENTER
                    * sqrt(sq(WIDTH / 2f - p.getPosition().x) + sq(HEIGHT / 2f - p.getPosition().y))
                    / (min(WIDTH, HEIGHT) / 2f));
            for (int i = max(0, floor((p.getPosition().x - REPULSION_RADIUS) / U_TREE_CELL_WIDTH));
                 i <= min(grid.nCellX - 1, floor((p.getPosition().x + REPULSION_RADIUS) / U_TREE_CELL_WIDTH));
                 i++) {
                for (int j = max(0, floor((p.getPosition().y - REPULSION_RADIUS) / U_TREE_CELL_HEIGHT));
                     j <= min(grid.nCellY - 1, floor((p.getPosition().y + REPULSION_RADIUS) / U_TREE_CELL_HEIGHT));
                     j++) {
                    for (Point q : grid.cells[i][j].getPoints()) {
                        if (p != q) {
                            PVector v = PVector.sub(p.getPosition(), q.getPosition());
                            if (v.magSq() < sq(REPULSION_RADIUS)) {
                                d++;
                            }
                        }
                    }
                }
            }

            densityCount.append(d);
        }

        int minDensity = densityCount.min();
        IntList indexes = new IntList();
        IntStream.range(0, points.size())
                .filter(i -> densityCount.get(i) == minDensity)
                .forEach(indexes::append);

        int index = indexes.get(floor(pApplet.random(indexes.size())));
        PVector p = points.get(index).getPosition().copy();
        float a = pApplet.random(TWO_PI);
        p.add(PVector.fromAngle(a).mult(MESH_DISTANCE));

        add(p);
    }
}
