package sudoku.solver.techniques;

import sudoku.solver.SudokuGrid;

import java.util.*;
import java.util.stream.IntStream;

import sudoku.Field;
public class HiddenSin extends Technique{
    public int cost = 3;
    public HiddenSin(SudokuGrid grid, ArrayList<String> log) {
        super(grid,log);
    }
    @Override
    public int apply() {
        for(Set<Field> row : SudokuGrid.rows) {
            int[] numO = new int[9];
            Field[] ex = new Field[9];
            IntStream.range(0,9).forEach(x->{numO[x]=0; ex[x]=null;}); 
            for(Field f : row) {
                for(Integer num : grid.pN.get(f)) {
                    numO[num-1]++;
                    ex[num-1]=f;
                }
            }
            for(int i=0;i<9;i++) {
                if(numO[i]==1) {
                    grid.setField(ex[i], i+1);
                    log.add("HiddenSin: row, field "+ex[i]+" num "+(i+1));
                    return cost;
                }
            }
        }
        for(Set<Field> column : SudokuGrid.columns) {
            int[] numO = new int[9];
            Field[] ex = new Field[9];
            IntStream.range(0,9).forEach(x->{numO[x]=0; ex[x]=null;}); 
            for(Field f : column) {
                for(Integer num : grid.pN.get(f)) {
                    numO[num-1]++;
                    ex[num-1]=f;
                }
            }
            for(int i=0;i<9;i++) {
                if(numO[i]==1) {
                    grid.setField(ex[i], i+1);
                    log.add("HiddenSin: column, field "+ex[i]+" num "+(i+1));
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
        return "HiddenSin";
    }
}
