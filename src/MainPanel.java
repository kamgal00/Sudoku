
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import sudoku.SudokuPanel;
import sudoku.generator.Difficulty;
import sudoku.generator.Generator;
import sudoku.generator.GeneratorResult;

public class MainPanel {
    private volatile boolean isGenerating=false;
    Lock genLock = new ReentrantLock();
    AtomicBoolean genRunning = new AtomicBoolean(false);
    JFrame frame;
    JPanel mainPanel;
    JPanel downPanel;
    JPanel infoPanel;
    JLabel seedL;
    JLabel rating;
    JLabel diff;
    JButton generateButton;
    JButton goToMenu;
    JButton setDifficulty;
    JPanel waitingPanel;
    JLabel waiting = new JLabel("Generating...");
    SudokuPanel sp;
    long currentSeed;
    int difficulty;
    Difficulty d = Difficulty.EASY;
    ComponentListener cl = new ComponentListener() {

        @Override
        public void componentResized(ComponentEvent e) { redrawFields();}

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void componentHidden(ComponentEvent e) {
            // TODO Auto-generated method stub

        }

    };
    
    private void redrawFields(){
        int newS = Math.min(frame.getWidth(), frame.getHeight())/36;
        Font newF = new Font(Font.SANS_SERIF, Font.PLAIN, newS);
        waiting.setFont(newF);
        seedL.setFont(newF);
        rating.setFont(newF);
        generateButton.setFont(newF);
        goToMenu.setFont(newF);
        setDifficulty.setFont(newF);
        diff.setFont(newF);
    }

    public MainPanel(JFrame frame) {
        this.frame=frame;
        mainPanel=new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.addComponentListener(cl);
        downPanel = new JPanel();
        downPanel.setLayout(new FlowLayout());
        generateButton = new JButton("Generate new sudoku");
        goToMenu = new JButton("Go to menu");
        setDifficulty = new JButton("Set difficulty");
        generateButton.addActionListener((e)->{
            generate();
        });
        goToMenu.addActionListener((e)->{
            // genLock.lock();
            // try{
            //     if(isGenerating) return;
            // }
            // finally{
            //     genLock.unlock();
            // }
            genRunning.set(false);
            removeFromFrame();
            Variables.menu.addToFrame();
        });
        setDifficulty.addActionListener((e)->{
            Difficulty newD=Difficulty.askForDifficulty(frame,d);
            if(newD!=null) {
                d=newD;
                generate();
            }
        });
        downPanel.add(goToMenu);
        downPanel.add(generateButton);
        downPanel.add(setDifficulty);
        mainPanel.add(downPanel, BorderLayout.SOUTH);
        
        waitingPanel=new JPanel();
        waitingPanel.setLayout(new GridBagLayout());
        waitingPanel.add(waiting);
        infoPanel=new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setHgap(10);
        infoPanel.setLayout(fl);
        seedL = new JLabel();
        diff = new JLabel();
        rating=new JLabel();
        infoPanel.add(seedL);
        infoPanel.add(diff);
        infoPanel.add(rating);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
    }
    public void redrawInfo(){
        seedL.setText("Seed: "+currentSeed+"  ");
        diff.setText("Difficulty: "+(d!=null ? d : "")+"  ");
        rating.setText("Rating: "+difficulty);

    }
    public void addToFrame() {
        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
        // frame.setSize(800,600);
    }
    public void removeFromFrame() {
        frame.remove(mainPanel);
    }
    public void generate() {
        genRunning.set(false);
        genLock.lock();
        try{
            if (isGenerating) return;
            isGenerating=true;
            if(sp!=null) mainPanel.remove(sp);
            mainPanel.add(waitingPanel);
            currentSeed=0;
            difficulty=0;
            redrawInfo();
            frame.revalidate();
            frame.repaint();
        }   
        finally{
            genLock.unlock();
        }
        
        new Thread(()->{
            genLock.lock();
            try {
                GeneratorResult gr = Generator.generateSudoku(d, genRunning);
                mainPanel.remove(waitingPanel);
                if(gr==null) {
                    frame.revalidate();
                    frame.repaint();
                    return;
                }
                sp=new SudokuPanel(gr.grid,()->{JOptionPane.showMessageDialog(frame,"Congratulations, you won!", "Sudoku",JOptionPane.INFORMATION_MESSAGE);});
                currentSeed=gr.seed;
                difficulty=gr.difficulty;
                mainPanel.add(sp);
                redrawInfo();
                frame.revalidate();
                frame.repaint();
            }
            finally{
                try{
                    isGenerating=false;
                }   
                finally{
                    genLock.unlock();
                }
            }
        }).start();
    }
    
}
