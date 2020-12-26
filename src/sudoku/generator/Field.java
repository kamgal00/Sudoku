package sudoku.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Field {
    final int x, y;
    private static final Field[][] fields=new Field[9][9];
    public static final Set<Field> allFields=new HashSet<>();
    static{
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++) {
                fields[i][j]=new Field(i, j);
                allFields.add(fields[i][j]);
            }
    }
    private Field(int x, int y) {
        this.x=x;
        this.y=y;
    }
    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(!(o instanceof Field)) return false;
        Field x=(Field) o;
        return hashCode()==x.hashCode();
    }
    @Override
    public int hashCode() {
        return 9*y+x;
    }
    public static Field getField(int x, int y) {
        return fields[x][y];
    }
    public static Field getField(int code) {
        return fields[code%9][code/9];
    }
    @Override
    public String toString() {
        return x+" "+y;
    }
    public static List<Field> getRow(int row) {
        List<Field> out = new ArrayList<>();
        for(int i=0;i<9;i++) {
            out.add(fields[i][row]);
        }
        return out;
    }
    public static List<Field> getColumn(int column) {
        List<Field> out = new ArrayList<>();
        for(int i=0;i<9;i++) {
            out.add(fields[column][i]);
        }
        return out;
    }
    public static List<Field> getSquare(int x, int y) {
        List<Field> out = new ArrayList<>();
        for(int i=3*x; i<3*x+3;i++){
            for(int j=3*y; j<3*y+3;j++){
                out.add(fields[i][j]);
            }
        }
        return out;
    }
}
