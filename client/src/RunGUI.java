import java.awt.*;

public class RunGUI implements Runnable {
    int x,y;
    public RunGUI(int x, int y){
        this.x = x;
        this.y = y;
    }
    public RunGUI(){
        this(400,400);
    }
    @Override
    public void run() {
        GUI gui = new GUI();
        gui.setMinimumSize(new Dimension(x, y));
        gui.setVisible(true);
    }
}

