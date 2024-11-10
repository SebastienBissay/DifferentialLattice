package utree;

import lattice.Point;

import java.util.ArrayList;
import java.util.Arrays;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;

public class UTree {
    public int nCellX;
    public int nCellY;
    public Cell[][] cells;

    public UTree() {
        nCellX = ceil(WIDTH / U_TREE_CELL_WIDTH);
        nCellY = ceil(HEIGHT / U_TREE_CELL_HEIGHT);
        cells = new Cell[nCellX][nCellY];
        for (int i = 0; i < nCellX; i++) {
            for (int j = 0; j < nCellY; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void clear() {
        Arrays.stream(cells).flatMap(Arrays::stream).forEach(Cell::clear);
    }

    public void add(Point p) {
        cells[max(0, min(nCellX - 1, floor(p.getPosition().x / U_TREE_CELL_WIDTH)))]
                [max(0, min(nCellY - 1, floor(p.getPosition().y / U_TREE_CELL_HEIGHT)))]
                .add(p);
    }

    public void add(ArrayList<Point> pts) {
        pts.forEach(this::add);
    }
}
