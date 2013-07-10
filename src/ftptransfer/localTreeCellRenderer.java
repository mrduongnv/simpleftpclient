/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Wise-Sw
 */
public class localTreeCellRenderer extends DefaultTreeCellRenderer {
    private FileSystemView fileSystemView;
    private JLabel jLabel;

    public localTreeCellRenderer() {
        fileSystemView = FileSystemView.getFileSystemView();
        jLabel = new JLabel();
        jLabel.setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if(!(node.getUserObject() instanceof String))
        {
            File file = (File) node.getUserObject();
//            if(file.listFiles()==null)
//            {
//                jLabel.setIcon(leafIcon);
//            }
            jLabel.setIcon(fileSystemView.getSystemIcon(file));
            jLabel.setText(fileSystemView.getSystemDisplayName(file));
            if(sel)
            {
                jLabel.setBackground(backgroundSelectionColor);
            }else
            {
                jLabel.setBackground(backgroundNonSelectionColor);
            }
        }
        return jLabel;
    }
    
}
