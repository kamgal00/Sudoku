package sudoku.solver.techniques;

import java.util.ArrayList;

import sudoku.solver.LogInfo;
import sudoku.solver.SudokuGrid;

public abstract class Technique {
    SudokuGrid grid;
    ArrayList<LogInfo> log;
    public Technique(SudokuGrid grid, ArrayList<LogInfo> log) {
        this.grid=grid;
        this.log=log;
    }
    public abstract int apply();
    public abstract int getCost();
}
