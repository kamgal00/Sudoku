package sudoku.solver.techniques;

import sudoku.solver.SudokuGrid;
import java.util.*;
import sudoku.*;
public class Pointing extends Technique {
    public int cost = 9;
    Map<Set<Field>, Set<Integer>> checked = new HashMap<>(); 

    public Pointing(SudokuGrid grid, ArrayList<String> log) {
        super(grid,log);
        for(Set<Field> box : SudokuGrid.boxes) {
            checked.put(box, new HashSet<>());
        }
    }
	@Override
    public int apply() {
        for(Set<Field> box : SudokuGrid.boxes) {
            for(int num=1;num<10;num++) {
                if(checked.get(box).contains(num)) continue;

                Set<Field> pos = new HashSet<>();
                for(Field f : box) {
                    if(grid.pN.get(f).contains(num)) pos.add(f);
                }
                if(pos.size()==0) {
                    checked.get(box).add(num);
                    continue;
                }
                Field example = pos.stream().findAny().get();
                if(pos.stream().allMatch(f->f.x==example.x)) {
                    checked.get(box).add(num);
                    Set<Field> elseFields = new HashSet<>(SudokuGrid.getColumn(example));
                    elseFields.removeAll(box);
                    boolean didChanged = false;
                    for(Field f : elseFields) {
                        didChanged |= grid.pN.get(f).remove(num);
                    }
                    if(didChanged){
                        log.add("Pointing: fields "+pos+ " num "+num);
                        return cost;
                    }
                }
                if(pos.stream().allMatch(f->f.y==example.y)) {
                    checked.get(box).add(num);
                    Set<Field> elseFields = new HashSet<>(SudokuGrid.getRow(example));
                    elseFields.removeAll(box);
                    boolean didChanged = false;
                    for(Field f : elseFields) {
                        didChanged |= grid.pN.get(f).remove(num);
                    }
                    if(didChanged){
                        log.add("Pointing: fields "+pos+ " num "+num);
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
        return "Pointing";
    }
}
