import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private GUI gui;

    public Window() {

        setTitle("SCC.300");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        gui = new GUI(this);
        setContentPane(gui);

    }

    public GUI getGui() {
        return gui;
    }

}
