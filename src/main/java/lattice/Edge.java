package lattice;

import processing.core.PVector;

public record Edge(Point a, Point b) {

    public boolean equals(Edge e) {
        return (((a.equals(e.a)) && (b.equals(e.b))) || ((a.equals(e.b)) && (b.equals(e.a))));
    }

    public float magSq() {
        PVector v = PVector.sub(a.getPosition(), b.getPosition());
        return v.magSq();
    }
}
