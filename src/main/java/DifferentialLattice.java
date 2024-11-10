import lattice.Lattice;
import processing.core.PApplet;
import processing.core.PVector;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class DifferentialLattice extends PApplet {
    private Lattice lattice;

    public static void main(String[] args) {
        PApplet.main(DifferentialLattice.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
        noiseSeed(floor(random(MAX_INT)));
    }

    @Override
    public void setup() {
        background(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue());

        Lattice.setPApplet(this);

        lattice = new Lattice();
        for (int i = 0; i < 3; i++) {
            float r = STARTING_TRIANGLE_RADIUS + STARTING_TRIANGLE_RADIUS_GAUSSIAN_FACTOR * randomGaussian();
            float a = random(TWO_PI);
            lattice.add(PVector.fromAngle(a).mult(r).add(width / 2f, height / 2f));
        }
    }

    @Override
    public void draw() {
        lattice.computeNeighbours();
        lattice.computeForces();
        lattice.move();
        lattice.render();

        lattice.randomAdd();

        if (lattice.points.size() >= TARGET_NUMBER_OF_POINTS) {
            noLoop();
            saveSketch(this);
        }
    }
}
