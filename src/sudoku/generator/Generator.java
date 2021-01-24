package sudoku.generator;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import sudoku.generator.Grid.FieldContainer;
import java.util.ArrayList;
import java.util.Collections;
import sudoku.Field;
import sudoku.solver.*;
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
    private static GeneratorResult gS(Difficulty d, AtomicBoolean running) {
        Random seedGen = new Random();
        main_loop:
        while(running.get()){
            long seed = seedGen.nextLong();
            Random r = new Random(seed);
            Grid g = createSudoku(r);
            List<Field> moves = new ArrayList<>(Field.allFields);
            SudokuSolution s=new SudokuSolution();
            Collections.shuffle(moves, r);
            int difficulty=0;
            for(Field f:moves.subList(0, 35)) {
                g.resetField(f);
            }
            solveSudoku(g, s, (ss)->ss.solutionsNumber>1);
            if(s.solutionsNumber>1) continue main_loop;
            for(Field f:moves.subList(35, 81)) {
                if(!running.get()) return null;
                int prev = g.grid[f.x][f.y];
                g.resetField(f);
                
                int rating =0;
                ArrayList<LogInfo> tech = new ArrayList<>();
                try {
                    rating = SudokuSolver.rate(g.grid, tech);
                }
                catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
                if(rating<0) {
                    g.setField(f, prev);
                }
                else {
                    difficulty = rating;
                    if (d.match(difficulty)) {
                        // System.out.println("Got difficulty "+difficulty);
                        // tech.stream().forEach(System.out::println);
                        return new GeneratorResult(g, seed, difficulty);
                    }
                    else if(difficulty>=d.max) {
                        continue main_loop;
                    }
                }
            }
            // System.out.println("Got difficulty "+difficulty);
            if(d.match(difficulty)){
                return new GeneratorResult(g, seed, difficulty);
            }
        }
        return null;
    }
    public static GeneratorResult generateSudoku(Difficulty d, AtomicBoolean running) {
        int threads=Runtime.getRuntime().availableProcessors();
        List<Callable<GeneratorResult>> generators = new ArrayList<>();
        running.set(true);
        for(int i=0;i<threads;i++) {
            generators.add(()->gS(d, running));
        }
        ExecutorService es = Executors.newFixedThreadPool(threads);
        try{
            GeneratorResult g = es.invokeAny(generators);
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
