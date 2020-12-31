package sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import sudoku.generator.Grid;
import sudoku.solver.LogInfo;
import sudoku.solver.SudokuSolver;

import java.awt.*;
import java.awt.event.*;

public class SudokuPanel extends JPanel {
    Field selectedField = null;
    Grid grid;
    Color selected = Color.GREEN, defaultColor = Color.LIGHT_GRAY, interferring =Color.GRAY;
    boolean adnMode=false;
    boolean didWin=false;
    Runnable onWin;
    JButton[][] buttons = new JButton[9][9];
    JPanel downPanel= new JPanel();
    JPanel infoPanel = new JPanel();
    JLabel drawingMode = new JLabel("Input mode: normal (press 'a' to toggle)  ");
    JLabel infoLabel = new JLabel("Use mouse/arrows/ctrl + arrows to select  ");
    JButton hintButton = new JButton("Hints");
    AdnPanel[][] adn = new AdnPanel[9][9];
    Dimension fieldDim = new Dimension(20,20);
    Set<Field> constants = new HashSet<>();
    private Font fieldFont = new Font("Serif", Font.PLAIN, 20);
    private Font anFont = new Font("Serif", Font.PLAIN, 10);
    private Font butsFont = new Font("Serif", Font.PLAIN, 15);

    JPanel rightSidePanel = new JPanel();
    private JLabel[] filledNums = new JLabel[9];

    // hints
    Color targetColor = Color.GREEN, changedColor = Color.CYAN, subsetColor = Color.GRAY;
    ArrayList<LogInfo> hints = new ArrayList<>();
    JPanel hintPanel = new JPanel();
    JButton nextHint=new JButton("Next hint");
    JButton previousHint = new JButton("Previous hint");
    JLabel techniqueLabel = new JLabel();
    JLabel numbersLabel = new JLabel();
    boolean hintMode = false;
    int currentIndex=0;
    private void resetHintMode() {
        if(!hintMode) return;
        hintMode=false;
        downPanel.remove(hintPanel);
        downPanel.add(infoPanel);
        repaintButtons();
    }
    private void initHints() {
        hintPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridheight=2;
        hintPanel.add(previousHint,gbc);
        gbc.gridx=2;
        hintPanel.add(nextHint,gbc);
        gbc.gridx=1;
        gbc.gridheight=1;
        hintPanel.add(techniqueLabel,gbc);
        gbc.gridy=1;
        hintPanel.add(numbersLabel, gbc);

        previousHint.addActionListener(e ->selectHint(currentIndex-1));
        nextHint.addActionListener(e ->selectHint(currentIndex+1));
        nextHint.addKeyListener(kl);
        previousHint.addKeyListener(kl);

    }
    private void generateHints() {
        if(hintMode) return;
        hintMode=true;
        hints.clear();
        SudokuSolver.rate(grid.grid, hints);
        if(hints.size()==0) {
            hintMode=false;
            return;
        }
        downPanel.remove(infoPanel);
        downPanel.add(hintPanel);
        selectHint(0);
        revalidate();
        repaint();
    }
    private void selectHint(int index) {
        LogInfo li=null;
        selectedField=null;
        try{
            li=hints.get(index);
        }
        catch(Exception e) { return;}
        // li.printAll();
        currentIndex=index;
        techniqueLabel.setText("  Method: "+li.methodName+"  ");
        numbersLabel.setText((li.numbers.size()==1 ?"  Number: " : "Numbers: ")+li.numbers.stream().map(i->String.valueOf(i)).reduce((a,b)->a+", "+b).get()+"  ");
        repaintButtons();
        for(Field f: li.container) buttons[f.x][f.y].setBackground(subsetColor);
        for(Field f: li.changedFields) buttons[f.x][f.y].setBackground(changedColor);
        for(Field f: li.targetFields) buttons[f.x][f.y].setBackground(targetColor);
        if(index==0) {
            previousHint.setEnabled(false);
        }
        else {
            previousHint.setEnabled(true);
        }
        if(index==hints.size()-1) {
            nextHint.setEnabled(false);
        }
        else {
            nextHint.setEnabled(true);
        }
        if(li.targetFields.size()==1) selectedField=li.targetFields.stream().findAny().get();
    }

    private void redrawFields() {
        int newS = Math.min(getWidth(), getHeight())/12;
        fieldDim.setSize(newS, newS);
        fieldFont = new Font("Serif", Font.PLAIN, newS/2);
        anFont = new Font("Serif", Font.PLAIN, newS/4);
        int butSize = (int) Math.round(newS/3.5);
        butsFont = new Font("Serif", Font.PLAIN, butSize );
        for(Field f : Field.allFields) {
            buttons[f.x][f.y].setPreferredSize(fieldDim);
            buttons[f.x][f.y].setFont(fieldFont);
            adn[f.x][f.y].setAnnFont(anFont);
        }
        drawingMode.setFont(butsFont);
        infoLabel.setFont(butsFont);
        previousHint.setFont(butsFont);
        nextHint.setFont(butsFont);
        techniqueLabel.setFont(butsFont);
        numbersLabel.setFont(butsFont);
        hintButton.setFont(butsFont);
        IntStream.range(0, 9).forEach(i-> filledNums[i].setFont(fieldFont));
        revalidate();
        repaint();
    }
    KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            resetHintMode();
            try{
                if(e.getKeyChar()=='a') {
                    adnMode=!adnMode;
                    if(adnMode) drawingMode.setText("Input mode: annotation (press 'a' to toggle)  ");
                    else drawingMode.setText("Input mode: normal (press 'a' to toggle)  ");
                    return;
                }
                if(e.getKeyCode()==KeyEvent.VK_UP ||
                e.getKeyCode()==KeyEvent.VK_DOWN ||
                e.getKeyCode()==KeyEvent.VK_LEFT ||
                e.getKeyCode()==KeyEvent.VK_RIGHT){
                    moveSelected(e.getKeyCode(), e.isControlDown());
                    return;
                }
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
                    resetSelected();
                    return;
                }
                if(e.getKeyCode()==KeyEvent.VK_DELETE || e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
                    setVal(0);
                    return;
                }
                int val = Integer.valueOf(String.valueOf(e.getKeyChar()));
                setVal(val);
            }
            catch(Exception ex) {}
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    };

    ComponentListener cl = new ComponentListener() {

        @Override
        public void componentResized(ComponentEvent e) { redrawFields();}

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}

    };
        
    public SudokuPanel(Grid grid, Runnable onWin) {
        super();
        this.onWin=onWin;
        this.grid = grid;
        constants = new HashSet<>(grid.getOccupied());
        initButtons();
        initHints();
        drawComponents();
        addComponentListener(cl);
        repaintButtons();
    }
    public void resetSelected(){
        selectedField=null;
        repaintButtons();
    }
    public void setVal(int val) {
        resetHintMode();
        if(selectedField==null) return;
        if(constants.contains(selectedField)) return;
        if(val<0 || val>9) return;
        if(val==0) {
            grid.resetField(selectedField);
            buttons[selectedField.x][selectedField.y].setText(" ");
            adn[selectedField.x][selectedField.y].resetAll();
        }
        else if(!adnMode){
            if(!grid.getPossibilitiesForField(selectedField).contains(val)) return;
            adn[selectedField.x][selectedField.y].resetAll();
            grid.setField(selectedField, val);
            buttons[selectedField.x][selectedField.y].setText(String.valueOf(val));
            for(Field f: grid.getInterferingFields(selectedField)) {
                adn[f.x][f.y].removeNum(val);
            }
            if(!didWin && grid.getEmpty().size()==0) {
                didWin=true;
                onWin.run();
            }

        }
        else {
            if(!grid.getPossibilitiesForField(selectedField).contains(val)) return;
            if(grid.grid[selectedField.x][selectedField.y]!=0) return;
            adn[selectedField.x][selectedField.y].changeNum(val);
        }

        repaintButtons();
    }

    public void moveSelected(int code, boolean fast) {
        int delta = fast ? 3 : 1;
        switch(code){
            case KeyEvent.VK_UP:
                selectedField=Field.getField(selectedField.x, (selectedField.y+9-delta)%9);
                break;

            case KeyEvent.VK_DOWN:
                selectedField=Field.getField(selectedField.x, (selectedField.y+delta)%9);
                break;

            case KeyEvent.VK_LEFT:
            selectedField=Field.getField((selectedField.x+9-delta)%9, selectedField.y);
            break;


            case KeyEvent.VK_RIGHT:
                selectedField=Field.getField((selectedField.x+delta)%9, selectedField.y);
                break;
        }
        repaintButtons();
    }

    private void initButtons(){
        for(Field f : Field.allFields) {
            JButton b = new JButton();
            AdnPanel adnp = new AdnPanel();
            b.add(adnp);
            b.setLayout(new GridBagLayout());
            adn[f.x][f.y]=adnp;
            b.setFont(fieldFont);
            if(grid.getFieldVal(f)==0) {
                b.setForeground(Color.BLUE);
                b.setText("");
            }
            else {
                b.setForeground(Color.BLACK);
                b.setText(String.valueOf(grid.getFieldVal(f)));
            }
            b.addActionListener((e)->{
                resetHintMode();
                selectedField=f;
                repaintButtons();
            });
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setContentAreaFilled(true);
            b.setBackground(Color.LIGHT_GRAY);
            b.setPreferredSize(new Dimension(40,40));
            b.setMargin(new Insets(0,0,0,0));
            b.setFocusable(true);
            b.addKeyListener(kl);
            buttons[f.x][f.y]=b;
        }
    }

    private void repaintButtons() {
        for (Field f : Field.allFields) {
            buttons[f.x][f.y].setBackground(defaultColor);
            // if(grid.getFieldVal(f)!=0) buttons[f.x][f.y].setText(String.valueOf(grid.getFieldVal(f)));
            // else buttons[f.x][f.y].setText("");
        }
        if(selectedField!=null){
            // for(Field f : grid.getInterferingFields(selectedField)) {
            //     buttons[f.x][f.y].setBackground(interferring);
            // }
            for(Field f : grid.getFieldsWithNum(grid.getFieldVal(selectedField))) {
                buttons[f.x][f.y].setBackground(interferring);
            }
            buttons[selectedField.x][selectedField.y].setBackground(selected);
        }
        Set<Integer> filled = grid.getFinishedNumbers();
        for(int i=0;i<9;i++) {
            filledNums[i].setForeground(filled.contains(i+1)? Color.GREEN : Color.DARK_GRAY);
        }
        revalidate();
        repaint();
    }
    private void drawComponents(){
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbl);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        // gbc.ipadx=15;
        // gbc.ipady=15;
        gbc.insets.set(2, 2, 2, 2);
        for(Field f : Field.allFields) {
            gbc.gridx=f.x+f.x/3;
            gbc.gridy=f.y+f.y/3;
            add(buttons[f.x][f.y], gbc);
        }
        gbc.fill=GridBagConstraints.VERTICAL;
        gbc.gridx=3;
        gbc.gridy=0;
        gbc.gridheight=11;
        add(new JSeparator(JSeparator.VERTICAL), gbc);
        gbc.gridx=7;
        gbc.gridy=0;
        gbc.gridheight=11;
        add(new JSeparator(JSeparator.VERTICAL), gbc);

        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.gridheight=1;
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=11;
        add(new JSeparator(JSeparator.HORIZONTAL), gbc);
        gbc.gridx=0;
        gbc.gridy=7;
        gbc.gridwidth=11;
        add(new JSeparator(JSeparator.HORIZONTAL), gbc);
        gbc.gridy=12;
        gbc.gridx=0;
        gbc.gridwidth=11;
        gbc.ipady=5;

        add(downPanel, gbc);
        infoPanel.setLayout(new GridBagLayout());
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1.0;
        gbc.weighty=1.0;
        gbc.fill= GridBagConstraints.CENTER;
        infoPanel.add(drawingMode,gbc);
        gbc.gridy=1;
        infoPanel.add(infoLabel, gbc);
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.gridheight=2;
        infoPanel.add(hintButton,gbc);
        hintButton.addKeyListener(kl);

        downPanel.add(infoPanel);

        hintButton.addActionListener(e->{
            generateHints();
            nextHint.requestFocusInWindow();
        });

        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.Y_AXIS));

        for(int i =0;i<9;i++) {
            filledNums[i] = new JLabel("   "+String.valueOf(i+1)+" ");
            filledNums[i].setForeground(Color.GREEN);
            rightSidePanel.add(filledNums[i]);
        }

        gbc.gridx=12;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=11;
        gbc.weightx=0;
        gbc.weighty=0;

        add(rightSidePanel, gbc);

        // add(drawingMode, gbc);
        // gbc.gridy=13;
        // gbc.gridx=0;
        // gbc.gridwidth=11;
        // gbc.ipady=5;
        // add(infoLabel, gbc);
    }
}

class AdnPanel extends JPanel{
    private Font adnFont = new Font("Serif", Font.PLAIN, 9);
    private JLabel[] adns=new JLabel[9];
    Set<Integer> nums = new HashSet<>();
    public AdnPanel(){
        super();
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.CENTER;
        for(int i=0;i<9;i++) {
            gbc.gridx=i%3;
            gbc.gridy=i/3;
            adns[i]=new JLabel("   ");
            adns[i].setFont(adnFont);
            add(adns[i], gbc);
        }
    }
    public void setAnnFont(Font f) {
        adnFont=f;
        for(int i=0;i<9;i++) adns[i].setFont(f);
    }
    public void resetAll() {
        nums.clear();
        for(JLabel l : adns) l.setText("   ");
    }
    public void addNum(int num) {
        nums.add(num);
        adns[num-1].setText(" "+String.valueOf(num)+" ");
    }
    public void removeNum(int num) {
        nums.remove(num);
        adns[num-1].setText("   ");
    }
    public void changeNum(int num) {
        if("   ".compareTo(adns[num-1].getText())==0) addNum(num);
        else removeNum(num);
    }
}
