package sudoku.solver.techniques;

import java.util.ArrayList;
import java.util.Set;

import sudoku.Field;
import sudoku.solver.LogInfo;
import sudoku.solver.SudokuGrid;

public class NakedSin extends Technique {
    public int cost = 8;
    public NakedSin(SudokuGrid grid, ArrayList<LogInfo> log) {
        super(grid,log);
    }

    @Override
    public int apply() {
        for(Field f : Field.allFields) {
            if(grid.pN.get(f).size()==1) {
                int num = grid.pN.get(f).stream().findAny().get();
                grid.setField(f, num);
                log.add(new LogInfo(toString(), Set.of(f), null, null, Set.of(num), true));
                // log.add("NakedSin: field "+f+" num "+num);
                return cost;
            }
        }
        return 0;
    }
    
    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Naked Single";
    }
}
