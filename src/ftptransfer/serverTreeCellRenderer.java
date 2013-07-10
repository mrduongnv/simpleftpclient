/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author Wise-Sw
 */
public class serverTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private JLabel jLabel;

    public serverTreeCellRenderer() 
    {
        jLabel = new JLabel();
        jLabel.setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if(!(node.toString().equals("")))
        {
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            Icon folderIcon = defaults.getIcon("FileView.directoryIcon");
            try {
                FTPFile ftpFile = FTPServerConnect.getClient().mlistFile(node.toString());
                String[] textDis= ftpFile.getName().split("/");
                if(textDis.length>0)
                {
                    jLabel.setText(textDis[textDis.length-1]);
                }
                else
                {
                    jLabel.setText(ftpFile.getName());
                }
                
            } catch (IOException ex) {
                Logger.getLogger(serverTreeCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
            jLabel.setIcon(folderIcon);
            if(sel)
            {
                jLabel.setBackground(backgroundSelectionColor);
            }
            else
            {
                jLabel.setBackground(backgroundNonSelectionColor);
            }
        }
        return jLabel;
    }
    
    
}
