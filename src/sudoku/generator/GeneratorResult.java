package sudoku.generator;

public class GeneratorResult {
    public Grid grid;
    public long seed;
    public int difficulty;
    public GeneratorResult(Grid grid, long seed, int difficulty) {
        this.grid=grid;
        this.seed=seed;
        this.difficulty=difficulty;
    }
}
