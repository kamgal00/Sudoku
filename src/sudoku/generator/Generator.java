package sudoku.generator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import sudoku.generator.Grid.FieldContainer;
import java.util.ArrayList;
import java.util.Collections;

public class Generator {

    public static void solveSudoku(Grid grid, SudokuSolution s, Predicate<SudokuSolution> ender){

        if(grid.empty.size()==0){
            s.solutionsNumber++;
            if(!s.hasSolution()) s.setSolution(grid.grid);
            return;
        }
        FieldContainer c=null;
        int cPossibleFields = 10;
        int missingNum=10;

        Field f=null;
        int fPossibleNumbers = 10;

        for(FieldContainer container : grid.containers) {
            for(int col:container.freeNumbers) {
                if(container.getPossibilitiesForNum(col).size()<cPossibleFields) {
                    cPossibleFields=container.getPossibilitiesForNum(col).size();
                    c=container;
                    missingNum=col;
                }
            }
        }
        for(Field field : grid.empty) {
            if(grid.getPossibilitiesForField(field).size()<fPossibleNumbers) {
                fPossibleNumbers=grid.getPossibilitiesForField(field).size();
                f=field;
            }
        }
        if(fPossibleNumbers< cPossibleFields) {
            s.difficulty+=(fPossibleNumbers-1)*(fPossibleNumbers-1);
            for(int col : grid.getPossibilitiesForField(f)) {
                grid.setField(f, col);
                solveSudoku(grid, s, ender);
                grid.resetField(f);
                if(ender.test(s)) return;
            }
        }
        else {
            s.difficulty+=(cPossibleFields-1)*(cPossibleFields-1);
            for(Field ff : new HashSet<>(c.getPossibilitiesForNum(missingNum))){
                grid.setField(ff, missingNum);
                solveSudoku(grid, s, ender);
                grid.resetField(ff);
                if(ender.test(s)) return;
            }
        }
    }
    
    private static Grid createSudoku(Random r) {
        Grid g = new Grid();
        for(int i=0;i<3;i++) {
            List<Field> sq = Field.getSquare(i, i);
            Collections.shuffle(sq, r);
            for(int j=0;j<9;j++) {
                g.setField(sq.get(j),j+1);
            }
        }
        SudokuSolution s = new SudokuSolution();
        solveSudoku(g, s, (a)->a.hasSolution());
        return new Grid(s.solution);
    }
    // private static class GridState{
    //     Grid grid;
    //     int state;
    //     List<Field> moves;
    //     List<Integer> vals;
    //     public GridState(Grid g, List<Field> moves) {
    //         grid=g;
    //         this.moves=moves;
    //         state=0;
    //         vals=new ArrayList<>(81);
    //         for(Field f: moves) {
    //             vals.add(grid.grid[f.x][f.y]);
    //         }
    //     }
    //     public void goToState(int newState) {
    //         while(state<newState) {
    //             grid.resetField(moves.get(state));
    //             state++;
    //         }
    //         while(state>newState) {
    //             state--;
    //             grid.setField(moves.get(state), vals.get(state));
    //         }
    //     }
    //     public List<Field> getRemaining() {
    //         return moves.subList(state, 81);
    //     }
    // }
    private static Grid gS(Difficulty d, AtomicBoolean running) {
        Random seedGen = new Random();
        main_loop:
        while(running.get()){
            long seed = seedGen.nextLong();
            Random r = new Random(seed);
            Grid g = createSudoku(r);
            List<Field> moves = new ArrayList<>(Field.allFields);
            SudokuSolution s=new SudokuSolution();
            Collections.shuffle(moves, r);
            // GridState gs = new GridState(g, moves);
            // int left=0, right=81, mid=0;
            // while(left<right) {
            //     mid=(left+right+1)/2;
            //     gs.goToState(mid);
            //     s=new SudokuSolution();
            //     solveSudoku(g, s, (ss)->ss.solutionsNumber>1);
            //     if(s.solutionsNumber>1) {
            //         right=mid-1;
            //     }
            //     else{
            //         left=mid;
            //     }
                
            // }
            int difficulty=0;
            // List<Field> remaining = gs.getRemaining();
            for(Field f:moves.subList(0, 35)) {
                g.resetField(f);
            }
            solveSudoku(g, s, (ss)->ss.solutionsNumber>1);
            if(s.solutionsNumber>1) continue main_loop;
            for(Field f:moves.subList(35, 71)) {
                if(!running.get()) return null;
                int prev = g.grid[f.x][f.y];
                g.resetField(f);
                s=new SudokuSolution();
                solveSudoku(g, s, (ss)->ss.solutionsNumber>1);
                if(s.solutionsNumber>1) {
                    g.setField(f, prev);
                }
                else{
                    difficulty=s.difficulty*100+g.empty.size();
                    if (d.match(difficulty)) {
                        System.out.println("Got difficulty "+difficulty);
                        return g;
                    }
                    else if(difficulty>=d.max) {
                        continue main_loop;
                    }
                }
            }
            System.out.println("Got difficulty "+difficulty);
            if(d.match(difficulty)){
                return g;
            }
        }
        return null;
    }
    public static Grid generateSudoku(Difficulty d) {
        int threads=Runtime.getRuntime().availableProcessors();
        List<Callable<Grid>> generators = new ArrayList<>();
        AtomicBoolean running = new AtomicBoolean(true);
        for(int i=0;i<threads;i++) {
            generators.add(()->gS(d, running));
        }
        ExecutorService es = Executors.newFixedThreadPool(threads);
        try{
            Grid g = es.invokeAny(generators);
            running.set(false);
            es.shutdown();
            es.awaitTermination(30, TimeUnit.SECONDS);
            return g;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
