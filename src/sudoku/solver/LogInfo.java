package sudoku.solver;

import java.util.*;
import sudoku.Field;
public class LogInfo {
    public String methodName;
    public Set<Field> targetFields;
    public Set<Field> changedFields;
    public Set<Field> container;
    public Set<Integer> numbers;
    public boolean addedNum;

    public LogInfo(String methodName, Set<Field> targetFields, Set<Field> changedFields, Set<Field> container,  Set<Integer> numbers,
            boolean addedNum) {
        
        this.methodName = new String(methodName);

        if(targetFields!= null) this.targetFields = new HashSet<>(targetFields);
        else this.targetFields=new HashSet<>();

        if(changedFields!= null) this.changedFields = new HashSet<>(changedFields);
        else this.changedFields=new HashSet<>();

        if(container!= null) this.container = new HashSet<>(container);
        else this.container=new HashSet<>();

        if(numbers!= null) this.numbers = new HashSet<>(numbers);
        else this.numbers=new HashSet<>();

        this.addedNum = addedNum;
    }
    @Override
    public String toString() {
        return methodName+" fields: "+targetFields+" numbers: "+numbers;
    }
    public void printAll(){
        System.out.println(methodName);
        System.out.println(targetFields);
        System.out.println(changedFields);
        System.out.println(targetFields);
        System.out.println(numbers);
    }
    
}
