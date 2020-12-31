package sudoku.solver.techniques;

import sudoku.solver.LogInfo;
import sudoku.solver.SudokuGrid;

import java.util.*;
import java.util.stream.IntStream;

import sudoku.Field;
public class HiddenSin extends Technique{
    public int cost = 3;
    public HiddenSin(SudokuGrid grid, ArrayList<LogInfo> log) {
        super(grid,log);
    }
    @Override
    public int apply() {
        for(Set<Field> line : SudokuGrid.lines) {
            int[] numO = new int[9];
            Field[] ex = new Field[9];
            IntStream.range(0,9).forEach(x->{numO[x]=0; ex[x]=null;}); 
            for(Field f : line) {
                for(Integer num : grid.pN.get(f)) {
                    numO[num-1]++;
                    ex[num-1]=f;
                }
            }
            for(int i=0;i<9;i++) {
                if(numO[i]==1) {
                    grid.setField(ex[i], i+1);
                    log.add(new LogInfo(toString(), Set.of(ex[i]), null, line, Set.of(i+1), true));
                    // log.add("HiddenSin: line, field "+ex[i]+" num "+(i+1));
                    return cost;
                }
            }
        }
        // for(Set<Field> column : SudokuGrid.columns) {
        //     int[] numO = new int[9];
        //     Field[] ex = new Field[9];
        //     IntStream.range(0,9).forEach(x->{numO[x]=0; ex[x]=null;}); 
        //     for(Field f : column) {
        //         for(Integer num : grid.pN.get(f)) {
        //             numO[num-1]++;
        //             ex[num-1]=f;
        //         }
        //     }
        //     for(int i=0;i<9;i++) {
        //         if(numO[i]==1) {
        //             grid.setField(ex[i], i+1);
        //             log.add(new LogInfo(toString(), Set.of(ex[i]), null, column, Set.of(i+1), true));
        //             // log.add("HiddenSin: column, field "+ex[i]+" num "+(i+1));
        //             return cost;
        //         }
        //     }
        // }
        return 0;
    }

    @Override
    public int getCost() {
        return cost;
    }
    @Override
    public String toString() {
        return "Hidden Single";
    }
}
