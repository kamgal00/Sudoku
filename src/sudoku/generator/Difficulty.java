package sudoku.generator;

public enum Difficulty {
    EASY(400,900), MEDIUM(900,1400), HARD(1400,1900), INSANE(1900, 2400), IMPOSSIBLE(2400, 5000);

    int min, max;
    Difficulty(int min, int max) {
        this.min=min;
        this.max=max;
    }
    public boolean match(int n) {
        return min<=n && n<max;
    }
    public static Difficulty getDifficulty(int n) {
        for(Difficulty d : Difficulty.values()) {
            if(d.match(n)) return d;
        }
        return null;
    }
}
