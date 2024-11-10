package utree;

import lattice.Point;

import java.util.ArrayList;

public class Cell {
    private final ArrayList<Point> points;

    public Cell() {
        points = new ArrayList<>();
    }

    public void add(Point p) {
        points.add(p);
    }

    public void clear() {
        points.clear();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }
}
