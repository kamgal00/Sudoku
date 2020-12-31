package sudoku.solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sudoku.*;
public class SudokuGrid {

    int[][] grid = new int[9][9];
    public Map<Field, Set<Integer>> pN = new HashMap<>();
    static Map<Field, ArrayList<Set<Field>>> fieldContainers = new HashMap<>();
    public static ArrayList<Set<Field>> boxes=new ArrayList<>();
    public static ArrayList<Set<Field>> columns=new ArrayList<>();
    public static ArrayList<Set<Field>> rows=new ArrayList<>();
    public static ArrayList<Set<Field>> containers = new ArrayList<>();
    public static ArrayList<Set<Field>> lines = new ArrayList<>();
    public static Map<Set<Field>, Set<Set<Field>>> intersectingBoxes = new HashMap<>();
    static Set<Integer> numSet = new HashSet<>(IntStream.range(1, 10).boxed().collect(Collectors.toSet()));
    public Set<Field> empty = new HashSet<>(Field.allFields);
    static {
        for(Field f : Field.allFields) {
            fieldContainers.put(f, new ArrayList<>(3));
        }
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++) {
                Set<Field> box = new HashSet<>();
                for(int x=3*i;x<3*i+3;x++) {
                    for(int y=3*j;y<3*j+3;y++) {
                        box.add(Field.getField(x, y));
                    }
                }
                boxes.add(box);
            }
        }
        for(int i=0;i<9;i++) {
            int ii=i;
            Set<Field> row = new HashSet<>(IntStream.range(0, 9).mapToObj((x)->Field.getField(x,ii)).collect(Collectors.toSet()));
            Set<Field> column = new HashSet<>(IntStream.range(0, 9).mapToObj((x)->Field.getField(ii,x)).collect(Collectors.toSet()));
            rows.add(row);
            columns.add(column);
        }

        for(Set<Field> box : boxes) {
            for(Field f : box) fieldContainers.get(f).add(box);
        }
        for(Set<Field> row : rows) {
            for(Field f : row) fieldContainers.get(f).add( row);
        }
        for(Set<Field> column : columns) {
            for(Field f : column) fieldContainers.get(f).add(column);
        }

        for(Set<Field> row : rows) {
            intersectingBoxes.put(row, new HashSet<>());
            for(Field f : row) {
                intersectingBoxes.get(row).add(getBox(f));
            }
        }
        for(Set<Field> column : columns) {
            intersectingBoxes.put(column, new HashSet<>());
            for(Field f : column) {
                intersectingBoxes.get(column).add(getBox(f));
            }
        }
        lines.addAll(columns);
        lines.addAll(rows);
        containers.addAll(boxes);
        containers.addAll(lines);
    }

    public static Set<Field> getBox(Field f) {
        return fieldContainers.get(f).get(0);
    }
    public static Set<Field> getRow(Field f) {
        return fieldContainers.get(f).get(1);
    }
    public static Set<Field> getColumn(Field f) {
        return fieldContainers.get(f).get(2);
    }
    public void setField( Field f, int val) {
        // System.out.println("Setting field "+f+" to "+val);
        grid[f.x][f.y]=val;
        // System.out.println("pN before: "+pN);
        pN.get(f).clear();
        empty.remove(f);
        Set<Field> ch = new HashSet<>();
        ch.addAll(getBox(f));
        ch.addAll(getColumn(f));
        ch.addAll(getRow(f));
        for(Field x : ch) {
            pN.get(x).remove(val);
        }
        // System.out.println("pN after: "+pN);
    }
    public int getValue(Field f){
        return grid[f.x][f.y];
    }
    public SudokuGrid(int [][] initGrid) {
        // System.out.println(getBox(Field.getField(0)));
        for(Field f : Field.allFields){
            grid[f.x][f.y]=0;
            pN.put(f, new HashSet<>(numSet));
        }
        for(Field f: Field.allFields) {
            if(initGrid[f.x][f.y]!=0) setField(f, initGrid[f.x][f.y]);
        }
    }
}
