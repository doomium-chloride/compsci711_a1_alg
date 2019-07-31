
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.List;

public class FileTreeModel implements TreeModel {
    List<File> list;
    public FileTreeModel(List<File> files){
        list = files;
    }
    public void list(List<File> newList){
        list = newList;
    }
    @Override
    public Object getRoot() {
        return list;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return list.get(index).getName();
    }

    @Override
    public int getChildCount(Object parent) {
        return list.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return node != getRoot();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return list.indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}
