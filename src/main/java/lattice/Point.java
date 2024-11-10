package lattice;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static parameters.Parameters.*;

public class Point {
    private static int indices = 0;
    private final int index;
    private final ArrayList<Point> neighbours;
    private PVector position;
    private PVector force;

    Point(PVector p) {
        position = p;
        index = indices++;
        neighbours = new ArrayList<>();
        force = new PVector(0, 0);
    }

    boolean equals(Point p) {
        return index == p.index;
    }

    void render(PApplet pApplet) {
        pApplet.noFill();
        pApplet.stroke(LATTICE_STROKE_COLOR.red(),
                LATTICE_STROKE_COLOR.green(),
                LATTICE_STROKE_COLOR.blue(),
                LATTICE_STROKE_COLOR.alpha());

        neighbours.stream()
                .filter(n -> n.index > index)
                .forEach(n -> pApplet.line(position.x, position.y, n.position.x, n.position.y));
    }

    void computeForce() {
        for (Point n : neighbours) {
            PVector v = PVector.sub(n.position, position);
            v.setMag(v.mag() - MESH_DISTANCE);
            force.add(v);
        }
    }

    void move() {
        position.add(force.limit(LATTICE_FORCE_LIMIT));
        force.x = 0;
        force.y = 0;
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getForce() {
        return force;
    }

    public void setForce(PVector force) {
        this.force = force;
    }

    public ArrayList<Point> getNeighbours() {
        return neighbours;
    }
}
