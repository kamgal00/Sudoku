import javax.swing.*;

import sudoku.generator.Difficulty;
import sudoku.generator.Generator;
import sudoku.generator.Grid;

import java.awt.*;

public class MainMenu{
    private JFrame frame;
    private JPanel panel;
    private JLabel title;
    private JButton newGame;
    private JButton gameFromSeed;
    private JButton exitButton;
    public MainMenu(){
        frame = new JFrame();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        panel = new JPanel();
        panel.setLayout(gbl);
        frame.add(panel);

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
            Grid gg = Generator.generateSudoku(Difficulty.MEDIUM);
            System.out.println(gg);
            System.out.println("---");
        });
        panel.add(newGame, gbc);

        gameFromSeed=new JButton("Load game from seed");
        gbc.gridy=2;
        panel.add(gameFromSeed, gbc);

        exitButton=new JButton("Exit");
        gbc.gridy=3;
        panel.add(exitButton, gbc);

        frame.setSize(400, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
