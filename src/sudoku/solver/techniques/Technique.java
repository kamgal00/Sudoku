package sudoku.solver.techniques;

import java.util.ArrayList;

import sudoku.solver.SudokuGrid;

public abstract class Technique {
    SudokuGrid grid;
    ArrayList<String> log;
    public Technique(SudokuGrid grid, ArrayList<String> log) {
        this.grid=grid;
        this.log=log;
    }
    public abstract int apply();
    public abstract int getCost();
}
