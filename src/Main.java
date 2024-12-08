
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PlayOff NBA");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setSize(1200, 673);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
