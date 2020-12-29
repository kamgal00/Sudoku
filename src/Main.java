import javax.swing.JFrame;

public class Main{
    public static void main(String[] args) {
        Variables.frame=new JFrame();
        Variables.frame.setVisible(true);
        Variables.frame.setTitle("Sudoku");
        // Variables.frame.setFocusable(false);
        Variables.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Variables.mPanel = new MainPanel(Variables.frame);
        Variables.menu = new MainMenu(Variables.frame);
        Variables.menu.addToFrame();
        Variables.frame.setSize(800,600);
    }    
}