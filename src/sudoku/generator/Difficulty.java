package sudoku.generator;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public enum Difficulty {
    EASY(80,120,0), MEDIUM(120,200,1), HARD(200,250,2);
    private static String[] vals = new String[3];
    private int id;
    static{
        vals[0]="EASY";
        vals[1]="MEDIUM";
        vals[2]="HARD";
    }
    private static Map<String, Difficulty> diffmap = new HashMap<>();
    static{
        diffmap.put("EASY", EASY);
        diffmap.put("MEDIUM", MEDIUM);
        diffmap.put("HARD", HARD);
    }

    int min, max;
    Difficulty(int min, int max, int id) {
        this.min=min;
        this.max=max;
        this.id=id;
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
    public static Difficulty askForDifficulty(JFrame f, Difficulty def) {
        if(def==null) def=EASY;
        String x = (String) JOptionPane.showInputDialog(f,"Choose difficulty", "Sudoku",JOptionPane.PLAIN_MESSAGE,null, vals, def.toString());
        if(x==null) return null;
        return diffmap.get(x);
    }
    @Override
    public String toString(){
        return vals[id];
    }
}
