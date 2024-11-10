package lattice;

import processing.core.PVector;

import static processing.core.PApplet.sq;

public class Triangle {
    private final Point[] vertices;

    private final PVector circumCenter;
    private final float circumRadius;

    public Triangle(Point A, Point B, Point C) {
        vertices = new Point[3];
        vertices[0] = A;
        vertices[1] = B;
        vertices[2] = C;

        // Compute circumCircle
        float D = 2 * (A.getPosition().x * (B.getPosition().y - C.getPosition().y)
                + B.getPosition().x * (C.getPosition().y - A.getPosition().y)
                + C.getPosition().x * (A.getPosition().y - B.getPosition().y));
        float Ux = (1 / D) * ((sq(A.getPosition().x) + sq(A.getPosition().y)) * (B.getPosition().y - C.getPosition().y)
                + (sq(B.getPosition().x) + sq(B.getPosition().y)) * (C.getPosition().y - A.getPosition().y)
                + (sq(C.getPosition().x) + sq(C.getPosition().y)) * (A.getPosition().y - B.getPosition().y));
        float Uy = (1 / D) * ((sq(A.getPosition().x) + sq(A.getPosition().y)) * (C.getPosition().x - B.getPosition().x)
                + (sq(B.getPosition().x) + sq(B.getPosition().y)) * (A.getPosition().x - C.getPosition().x)
                + (sq(C.getPosition().x) + sq(C.getPosition().y)) * (B.getPosition().x - A.getPosition().x));
        circumCenter = new PVector(Ux, Uy);
        circumRadius = PVector.sub(circumCenter, A.getPosition()).mag();
    }

    public boolean isInCircumCenter(PVector p) {
        PVector v = PVector.sub(p, circumCenter);
        return (v.magSq() <= sq(circumRadius));
    }

    public boolean isInCircumCenter(Point point) {
        return isInCircumCenter(point.getPosition());
    }

    public boolean contains(Point p) {
        for (int i = 0; i < 3; i++) {
            if (vertices[i].equals(p)) {
                return true;
            }
        }
        return false;
    }

    public Point[] getVertices() {
        return vertices;
    }
}
