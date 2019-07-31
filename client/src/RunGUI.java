import java.awt.*;

public class RunGUI implements Runnable {
    @Override
    public void run() {
        GUI gui = new GUI();
        gui.setMinimumSize(new Dimension(400, 400));
        gui.setVisible(true);
    }
}

