import javax.swing.*;

import sudoku.generator.Difficulty;

import java.awt.*;
import java.awt.event.*;

public class MainMenu{
    private JFrame frame;
    private JPanel panel;
    private JLabel title;
    private JButton newGame;
    // private JButton gameFromSeed;
    private JButton exitButton;

    ComponentListener cl = new ComponentListener() {

        @Override
        public void componentResized(ComponentEvent e) { redrawFields();}

        @Override
        public void componentMoved(ComponentEvent e) {
            // TODO Auto-generated method stub

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
        int newS = Math.min(frame.getWidth(), frame.getHeight())/10;
        Font titleF = new Font(Font.SERIF, Font.PLAIN, newS);
        Font butF = new Font(Font.SERIF, Font.PLAIN, newS/4);
        title.setFont(titleF);
        newGame.setFont(butF);
        // gameFromSeed.setFont(butF);
        exitButton.setFont(butF);

    }
    public MainMenu(JFrame frame){
        this.frame=frame;
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        panel = new JPanel();
        panel.setLayout(gbl);
        panel.addComponentListener(cl);

        title = new JLabel("SUDOKU");
        title.setFont(new Font("Serif", Font.PLAIN, 40));
        gbc.gridx=0; gbc.gridy=0;
        gbc.insets.set(0, 0, 70, 0);
        panel.add(title, gbc);

        newGame= new JButton("New game");
        gbc.gridy=1;
        gbc.ipady=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets.set(10, 0, 10, 0);
        newGame.addActionListener((e)->{
            Difficulty d = Difficulty.askForDifficulty(frame,null);
            if(d==null) return;
            removeFromFrame();
            Variables.mPanel.d=d;
            Variables.mPanel.addToFrame();
            Variables.mPanel.generate();
            // JOptionPane.showInputDialog(panel, new SudokuPanel(Generator.generateSudoku(Difficulty.IMPOSSIBLE)), "Sudoku",JOptionPane.PLAIN_MESSAGE);
        });
        panel.add(newGame, gbc);

        // gameFromSeed=new JButton("Load game from seed");
        // gbc.gridy=2;
        // panel.add(gameFromSeed, gbc);

        exitButton=new JButton("Exit");
        gbc.gridy=3;
        panel.add(exitButton, gbc);
        exitButton.addActionListener((e)->{
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });
    }
    public void addToFrame() {
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
        // frame.setSize(400, 600);
    }
    public void removeFromFrame() {
        frame.remove(panel);
    }
}
