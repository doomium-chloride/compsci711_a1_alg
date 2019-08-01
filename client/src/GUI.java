import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    public GUIpanel panel;

    public GUI(){
        super();
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new GUIpanel();
        Container visibleArea = getContentPane();
        visibleArea.add(panel);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new RunGUI());
    }
}

class GUIpanel extends JPanel implements MouseListener, TreeModelListener {
    JTree fileJlist;
    FileTreeModel treeModel;
    JButton clear, exit, refresh, download;
    Client client;
    String selectedFile = "";
    GUIpanel(){
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        client = new Client();
        clear = new JButton("Clear");
        refresh = new JButton("Refresh list");
        exit = new JButton("Exit &\nterminate server");
        download = new JButton("Download");
        treeModel = new FileTreeModel(new ArrayList<>());
        fileJlist = new JTree(treeModel);
        fileJlist.setRootVisible(false);

        fileJlist.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                selectedFile = (String) fileJlist.getLastSelectedPathComponent();
                System.out.println("Selected = " + selectedFile);
            }
        });

        treeModel.addTreeModelListener(this);
        JComponent[] components = new JComponent[]{clear,refresh,exit,download,fileJlist};
        for (JComponent j : components){
            j.setAlignmentX(Component.CENTER_ALIGNMENT);
            j.addMouseListener(this);
        }
        this.add(refresh);
        this.add(exit);
        this.add(clear);
        this.add(fileJlist);
        this.add(download);
        client.init();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Component component = e.getComponent();
        System.out.println(component.toString());
        try {
            if (refresh == component) {
                List<File> fileList = client.getList();
                treeModel.list(fileList);
                treeNodesChanged(null);
            } else if (exit == component){
                client.sendCommand("exit");
                System.exit(0);
            } else if (clear == component){
                client.sendCommand("clear");
            } else if (download == component){
                client.command("dl:" + selectedFile);
            } else if (fileJlist == component){
                //do nothing
                System.out.println("file selected: " + selectedFile);
            }
            else {
                System.out.println("not implemented");
            }
        } catch (IOException | ClassNotFoundException x){
            x.printStackTrace();
        }
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
    public void treeNodesChanged(TreeModelEvent e) {
        fileJlist.setModel(treeModel);
        fileJlist.updateUI();
        repaint();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        treeNodesChanged(e);
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        treeNodesChanged(e);
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        treeNodesChanged(e);
    }
}

