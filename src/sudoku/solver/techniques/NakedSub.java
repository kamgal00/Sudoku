package sudoku.solver.techniques;

import java.util.*;
import java.util.stream.Collectors;

import sudoku.solver.LogInfo;
import sudoku.solver.SudokuGrid;
import sudoku.Field;

public class NakedSub extends Technique {

    int cost = 35;

    Map<Set<Field>, Set<Field>> checked = new HashMap<>();

    public NakedSub(SudokuGrid grid, ArrayList<LogInfo> log) {
        super(grid, log);
        for(Set<Field> container : SudokuGrid.containers) {
            checked.put(container, new HashSet<>());
        }
    }

    @Override
    public int apply() {
        for(Set<Field> container : SudokuGrid.containers){
            for(Field f : container) {
                if(checked.get(container).contains(f)) continue;
                Set<Field> toCheck = new HashSet<>(container);
                toCheck.removeAll(checked.get(container));
                Set<Field> sameNums = toCheck.stream().filter( s-> grid.pN.get(f).equals(grid.pN.get(s))).collect(Collectors.toSet());
                if(sameNums.size()==grid.pN.get(f).size()) {
                    checked.get(container).addAll(sameNums);
                    Set<Field> toUpdate = new HashSet<>(container);
                    toUpdate.removeAll(sameNums);
                    boolean didChanged = toUpdate.stream().map(s -> grid.pN.get(s).removeAll(grid.pN.get(f))).reduce(false, (a,b)->a||b);
                    if(didChanged){
                        log.add(new LogInfo(toString(), sameNums, toUpdate, container, grid.pN.get(f),  false));
                        // log.add(toString()+" fields: "+sameNums+" nums: "+grid.pN.get(f));
                        return cost;
                    }
                }
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
        return "Naked Subset";
    }
    
}
