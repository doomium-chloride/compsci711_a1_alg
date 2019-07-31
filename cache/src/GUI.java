import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    public GUI(){
        super();
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CacheGUI panel = new CacheGUI();
        Container visibleArea = getContentPane();
        visibleArea.add(panel);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new RunGUI());
    }
}

class CacheGUI extends JPanel {
    JTextArea logText, cacheData;
    JScrollPane logScroll, cacheScroll, digestScroll;
    JTree digestList;
    Relay cache;
    CacheGUI(){
        cache = new Relay();
        logText = new JTextArea(5,20);
        cacheData = new JTextArea(5,20);
        digestList = new JTree();
        digestList.setRootVisible(false);
        logScroll = new JScrollPane(logText);
        cacheScroll = new JScrollPane(cacheData);
        digestScroll = new JScrollPane(digestList);
        this.add(logScroll);
        this.add(digestList);
        this.add(cacheScroll);
    }
}
