import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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
        SwingUtilities.invokeLater(new RunGUI(500,800));
    }
}

class GUIpanel extends JPanel implements MouseListener, TreeModelListener {
    JTree fileJlist;
    FileTreeModel treeModel;
    JButton clear, exit, refresh, download;
    JLabel picLabel;
    Client client;
    String selectedFile = "";
    String placeholder = "placeholder/placeholder.bmp";
    final String DIRECTORY = "testing/";
    GUIpanel(){
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        client = new Client();
        clear = new JButton("Clear");
        refresh = new JButton("Refresh list");
        exit = new JButton("Exit &\nterminate server");
        download = new JButton("Download");
        treeModel = new FileTreeModel(new ArrayList<>());
        fileJlist = new JTree(treeModel);
        fileJlist.setRootVisible(false);
        JScrollPane listScroll = new JScrollPane(fileJlist);
        listScroll.setMinimumSize(new Dimension(50, 50));


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
        try {
            picLabel = new JLabel(new ImageIcon(ImageIO.read(new File(placeholder))));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        picLabel.setMinimumSize(new Dimension(500, 1000));
        JScrollPane picScroll = new JScrollPane(picLabel);
        picScroll.setMinimumSize(new Dimension(500, 500));
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 0;c.gridy = 0;
        this.add(exit, c);
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 1;c.gridy = 0;
        this.add(clear, c);
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 2;c.gridy = 0;
        this.add(refresh, c);
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 0;c.gridy = 1;c.gridheight = 5;c.gridwidth = 2;
        this.add(listScroll, c);
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 2;c.gridy = 1;
        this.add(download, c);
        c.fill = GridBagConstraints.HORIZONTAL;c.gridx = 0;c.gridy = 6;c.gridheight = 5;c.gridwidth = 3;
        c.anchor = GridBagConstraints.PAGE_END; c.weighty = 1;
        this.add(picScroll, c);
        client.guiPanel = this;
        client.init();
    }

    public void showPic(File picFile){
        try {
            BufferedImage myPicture = ImageIO.read(picFile);
            picLabel.setIcon(new ImageIcon(myPicture));
            picLabel.updateUI();
            repaint();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void showDownloadedPic(String fileName){
        File picture = new File(DIRECTORY + fileName);
        if (picture.exists()){
            showPic(picture);
        }
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
                showDownloadedPic(selectedFile);
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

