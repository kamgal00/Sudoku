package sudoku.generator;

public class SudokuSolution {
    private boolean hasSolution=false;
    public int[][] solution= new int[9][9];
    public int solutionsNumber=0, difficulty=0;
    public boolean hasSolution(){
        return hasSolution;
    }
    public void setSolution(int[][] grid) {
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++) {
                solution[i][j]=grid[i][j];
            }
        }
        hasSolution=true;
    }
}
