package sudoku.solver.techniques;

import sudoku.solver.LogInfo;
import sudoku.solver.SudokuGrid;
import java.util.*;
import sudoku.Field;
public class Claiming extends Technique {
    int cost = 20;
    Map<Set<Field>, Set<Integer>> checked = new HashMap<>();
    public Claiming(SudokuGrid grid, ArrayList<LogInfo> log) {
        super(grid, log);
        for(Set<Field> row : SudokuGrid.rows){
            checked.put(row, new HashSet<>());
        }
        for(Set<Field> column : SudokuGrid.columns){
            checked.put(column, new HashSet<>());
        }
    }

    @Override
    public int apply() {
        for(Set<Field> row : SudokuGrid.rows) {
            for(int num=1;num<10;num++) {
                if(checked.get(row).contains(num)) continue;
                Set<Field> possibleFieldsForNum = new HashSet<>();
                for(Field f : row) {
                    if(grid.pN.get(f).contains(num)) possibleFieldsForNum.add(f);
                }
                if(possibleFieldsForNum.size()==0) {
                    checked.get(row).add(num);
                    continue;
                }
                for(Set<Field> box : SudokuGrid.intersectingBoxes.get(row)){
                    if(box.containsAll(possibleFieldsForNum)) {
                        checked.get(row).add(num);
                        Set<Field> toUpdate = new HashSet<>(box);
                        toUpdate.removeAll(possibleFieldsForNum);
                        boolean didChanged = false;
                        for(Field f : toUpdate) {
                            didChanged |= grid.pN.get(f).remove(num);
                        }
                        if(didChanged) {
                            log.add(new LogInfo(toString(), possibleFieldsForNum, toUpdate ,row, Set.of(num), false));
                            // log.add("Claiming: row "+row.stream().findAny().get().y+" fields "+possibleFieldsForNum+" num "+num);
                            return cost;
                        }
                    }
                }
            }
        }
        for(Set<Field> column : SudokuGrid.columns) {
            for(int num=1;num<10;num++) {
                if(checked.get(column).contains(num)) continue;
                Set<Field> possibleFieldsForNum = new HashSet<>();
                for(Field f : column) {
                    if(grid.pN.get(f).contains(num)) possibleFieldsForNum.add(f);
                }
                if(possibleFieldsForNum.size()==0) {
                    checked.get(column).add(num);
                    continue;
                }
                for(Set<Field> box : SudokuGrid.intersectingBoxes.get(column)){
                    if(box.containsAll(possibleFieldsForNum)) {
                        checked.get(column).add(num);
                        Set<Field> toUpdate = new HashSet<>(box);
                        toUpdate.removeAll(possibleFieldsForNum);
                        boolean didChanged = false;
                        for(Field f : toUpdate) {
                            didChanged |= grid.pN.get(f).remove(num);
                        }
                        if(didChanged) {
                            log.add(new LogInfo(toString(), possibleFieldsForNum, toUpdate ,column, Set.of(num), false));
                            // log.add("Claiming: column "+column.stream().findAny().get().y+" fields "+possibleFieldsForNum+" num "+num);
                            return cost;
                        }
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
        return "Claiming";
    }
    
}
