import javafx.collections.ListChangeListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

public class GUI extends JFrame {
    public GUI(){
        super();
        setTitle("Cache");
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

class CacheGUI extends JPanel implements MouseListener, ListSelectionListener, ListChangeListener {
    JTextArea logText, cacheData;
    JScrollPane logScroll, cacheScroll, digestScroll;
    JList<String> digestList;
    Relay cache;
    SwingWorker<Void,Void> worker;
    String selectedDigest;

    CacheGUI() {
        CacheGUI cacheGUI = this;
        cache = new Relay();
        logText = new JTextArea("", 20, 50);
        cacheData = new JTextArea("", 20, 50);
        logText.setLineWrap(true);
        cacheData.setLineWrap(true);
        digestList = new JList<>(new Vector<>(Builder.map.keySet()));
        logScroll = new JScrollPane(logText);
        DefaultCaret caret = (DefaultCaret)logText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        cacheScroll = new JScrollPane(cacheData);
        digestScroll = new JScrollPane(digestList);
        digestScroll.setMinimumSize(new Dimension(20,50));
        this.add(logScroll);
        this.add(digestScroll);
        this.add(cacheScroll);
        digestList.addMouseListener(this);
        cache.cacheGUI = this;
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                cache.connect();
                return null;
            }
        };
        worker.execute();
    }

    public void updateLog(){
        logText.setText(cache.logger.toString());
        digestList.setListData(new Vector<>(Builder.map.keySet()));
        repaint();
    }

    public void terminate(){
        System.exit(0);
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent() == digestList)
            valueChanged(null);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        selectedDigest = digestList.getSelectedValue();
        if (selectedDigest != null)
            cacheData.setText(Relay.toString(Builder.map.get(selectedDigest)));
        repaint();
    }

    @Override
    public void onChanged(Change c) {

    }
}

