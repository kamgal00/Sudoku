package sudoku.solver.techniques;

import sudoku.solver.SudokuGrid;

import java.util.*;
import java.util.stream.IntStream;

import sudoku.Field;
public class HiddenSinBox extends Technique{
    public int cost = 1;
    public HiddenSinBox(SudokuGrid grid,ArrayList<String> log) {
        super(grid, log);
    }
    @Override
    public int apply() {
        for(Set<Field> box : SudokuGrid.boxes) {
            int[] numO = new int[9];
            Field[] ex = new Field[9];
            IntStream.range(0,9).forEach(x->{numO[x]=0; ex[x]=null;}); 
            for(Field f : box) {
                // System.out.println(f+" : "+grid.pN.get(f));
                for(Integer num : grid.pN.get(f)) {
                    numO[num-1]++;
                    ex[num-1]=f;
                }
            }
            for(int i=0;i<9;i++) {
                if(numO[i]==1) {
                    // System.out.println("HiddenSinBox: Setting "+(i+1)+" to field "+ex[i]);
                    grid.setField(ex[i], i+1);
                    // System.out.println("HiddentSinBox: Possibilities for field "+ex[i]+" after set: "+grid.pN.get(ex[i]));
                    log.add("HiddenSinBox, field "+ex[i]+" num "+(i+1));
                    return cost;
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
        return "HiddenSinBox";
    }
}
