package sudoku.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import sudoku.*;
public class Grid {
    class FieldContainer{
        Set<Field> fields;
        Map<Integer, Set<Field>> possibilities=new HashMap<>();
        Set<Integer> freeNumbers;
        public FieldContainer(Collection<Field> fs){
            fields=new HashSet<>(fs);
            for(int i=1;i<=9;i++) possibilities.put(i, new HashSet<>(fs));
            freeNumbers = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
        }
        public Set<Field> getPossibilitiesForNum(int num) {
            Set<Field> out =new HashSet<>(possibilities.get(num));
            out.retainAll(empty);
            return out;
        }
        public Set<Integer> getFreeNumbers(){
            return new HashSet<>(freeNumbers);
        }
    }
    public int[][] grid = new int[9][9];
    private final Map<Field, Set<FieldContainer>> cont = new HashMap<>();
    private Map<Field, Map<Integer, Integer>> pos = new HashMap<>();
    Set<Field> empty;
    final Set<FieldContainer> containers = new HashSet<>();
    public Grid(){
        empty=new HashSet<>(Field.allFields);
        for(Field f : Field.allFields) {
            grid[f.x][f.y]=0;
            cont.put(f, new HashSet<>());
            Map<Integer, Integer> m = new HashMap<>();
            pos.put(f, m);
            for(int i=1;i<=9;i++) m.put(i,3);
        }
        for(int i=0;i<9;i++) {
            containers.add(new FieldContainer(Field.getColumn(i)));
            containers.add(new FieldContainer(Field.getRow(i)));
            containers.add(new FieldContainer(Field.getSquare(i%3, i/3)));
        }
        for(FieldContainer c : containers){
            for(Field f : c.fields) {
                cont.get(f).add(c);
            }
        }
    }
    public Grid(int[][] grid) {
        this();
        for(Field f : Field.allFields) {
            setField(f, grid[f.x][f.y]);
        }
    }

    private void sub(Field f, int num) {
        if(num==0) return;
        pos.get(f).put(num, pos.get(f).get(num)-1);
        for(FieldContainer c : cont.get(f)) {
            c.possibilities.get(num).remove(f);
        }
    }
    private void add(Field f, int num) {
        if(num==0) return;
        pos.get(f).put(num, pos.get(f).get(num)+1);
        if(pos.get(f).get(num)==3) {
            for(FieldContainer c : cont.get(f)) {
                c.possibilities.get(num).add(f);
            }
        }
    }
    public void resetField(Field f) {
        empty.add(f);
        if( grid[f.x][f.y]==0) return;
        int val=grid[f.x][f.y];
        for(FieldContainer c : cont.get(f)){
            c.freeNumbers.add(val);
            for(Field ff : c.fields){
                add(ff, val);
            }
        }
        grid[f.x][f.y]=0;
    }
    public void setField(Field f, int val) {
        resetField(f);
        if(val==0) {
            return;
        }
        empty.remove(f);
        for(FieldContainer c : cont.get(f)){
            c.freeNumbers.remove(val);
            for(Field ff : c.fields){
                sub(ff, val);
            }
        }
        grid[f.x][f.y]=val;
    }
    public Set<Field> getEmpty(){
        return new HashSet<>(empty);
    }
    public Set<Field> getOccupied() {
        Set<Field> out = new HashSet<>(Field.allFields);
        out.removeAll(empty);
        return out;
    }
    public Set<Integer> getPossibilitiesForField(Field f) {
        Set<Integer> out = new HashSet<>();
        for(int i=1;i<=9;i++) {
            if(pos.get(f).get(i)==3) out.add(i);
        }
        return out;
    }
    public Set<Field> getInterferingFields(Field f) {
        Set<Field> out = new HashSet<>();
        for(FieldContainer c: cont.get(f)) {
            out.addAll(c.fields);
        }
        out.remove(f);
        return out;
    } 
    public Set<Field> getFieldsWithNum(int num) {
        if(num==0) return new HashSet<>();
        Set<Field> out = new HashSet<>();
        for(Field f : Field.allFields) {
            if(num==grid[f.x][f.y]) out.add(f);
        }
        return out;
    }
    @Override
    public String toString(){
        StringBuilder out=new StringBuilder();
        for(int i=0;i<9;i++) {
            for(int j=0;j<9;j++) {
                out.append(grid[i][j]).append(" ");
            }
            out.append("\n");
        }
        return out.toString();
    }
    public Integer getFieldVal(Field f) {
        return grid[f.x][f.y];
    }
}
