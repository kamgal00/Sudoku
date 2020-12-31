package sudoku.solver;

import java.util.ArrayList;

import sudoku.solver.techniques.Claiming;
import sudoku.solver.techniques.HiddenSin;
import sudoku.solver.techniques.HiddenSinBox;
import sudoku.solver.techniques.NakedSin;
import sudoku.solver.techniques.NakedSub;
import sudoku.solver.techniques.Pointing;
import sudoku.solver.techniques.Technique;

public class SudokuSolver {
    public static int rate(int[][] g, ArrayList<LogInfo> log) {
        SudokuGrid grid = new SudokuGrid(g);
        ArrayList<Technique> t = new ArrayList<>();
        t.add(new HiddenSin(grid, log));
        t.add(new NakedSin(grid, log));
        t.add(new Pointing(grid, log));
        t.add(new HiddenSinBox(grid, log));
        t.add(new Claiming(grid, log));
        t.add(new NakedSub(grid, log));
        t.sort((a,b)-> a.getCost() - b.getCost());
        int rating=0;
        
        main_loop:
        while(true) {
            if(grid.empty.size()==0) return rating;
            for(Technique tech : t) {
                int result = tech.apply();
                if(result>0) {
                    rating+=result;
                    // System.out.println(tech+" "+result);
                    continue main_loop;
                }
            }
            return -1;
        }
        
    }
    
}
