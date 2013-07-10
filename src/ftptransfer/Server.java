/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import com.sun.activation.registries.MimeTypeFile;
import com.sun.org.apache.xalan.internal.lib.Extensions;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExtensionType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileTypeDetector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import org.apache.commons.net.ftp.FTPFile;
import sun.misc.ExtensionInfo;
import sun.security.x509.Extension;
import view.FTPClientView;

/**
 *
 * @author Wise-Sw
 */
public class Server {
    private DefaultTableModel modelFileServer;
    private JTable TableServer;
    private DefaultMutableTreeNode rootServer;
    private DefaultTreeModel treeModelServer;
    private DefaultMutableTreeNode serverDisc;
    private JTree treeServer;
    private JEditorPane paneNotify;
    private JTree treeLocal;
    private DefaultComboBoxModel recentServer = new DefaultComboBoxModel();
    private JComboBox comboboxServer;
    
    public Server(JTable TableServer,JTree treeServer,JEditorPane paneNotify,JTree treeLocal,JComboBox comboboxServer) {
        this.TableServer = TableServer;
        this.treeServer = treeServer;
        this.paneNotify = paneNotify;
        this.treeLocal = treeLocal;
        this.comboboxServer = comboboxServer;
        this.treeServer.setModel(treeModelServer);
    }

    public JTree getTreeServer() {
        return treeServer;
    }

    public JTable getTableServer() {
        return TableServer;
    }
    
    public void createColumn() 
    {
        modelFileServer = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelFileServer.addColumn("Filename");
        modelFileServer.addColumn("Filesize");
        modelFileServer.addColumn("Filetype");
        modelFileServer.addColumn("Last modified");
        TableServer.setModel(modelFileServer);
    }
    
    public void loadServerTree() 
    {
        try {
            rootServer = new DefaultMutableTreeNode("/");
            treeModelServer = new DefaultTreeModel(rootServer);
            FTPFile[] file = FTPServerConnect.getClient().listFiles();
            for (int i = 0; i < file.length; i++) {
                if (file[i].isDirectory()) 
                {
                    serverDisc = new DefaultMutableTreeNode(file[i].getName());
                    if(FTPServerConnect.getClient().listFiles(file[i].getName()).length != 0)
                    {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("");
                        serverDisc.add(childNode);
                    }
                    rootServer.add(serverDisc);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FTPClientView.class.getName()).log(Level.SEVERE, null, ex);
        }
        treeServer.setModel(treeModelServer);
        treeServer.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                System.out.println(event.getPath());
                System.out.println(event.getPath().getPath());
                treeServer.setSelectionPath(event.getPath());
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeServer.getLastSelectedPathComponent();
                loadDirServer(selectedNode);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });
        treeServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (treeServer.getSelectionCount() > 0) {
                    comboboxLoader();
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeServer.getLastSelectedPathComponent();
                    loadFileServer(selectedNode);
                }
            }
        });
        treeServer.setShowsRootHandles(true);
    }
     
    public void loadDirServer(DefaultMutableTreeNode node) {
        if (node != null) {
            if (node.getChildCount() != 0) {
                if (node.getChildAt(0).toString().equals("")) {
                    try {
                        FTPFile ftpParentFile = FTPServerConnect.getClient().mlistFile(node.toString());
                        System.out.println(node.toString());
                        treeServer.setEnabled(false);
                        if (ftpParentFile.isDirectory()) {
                            FTPFile[] ftpChildFile = FTPServerConnect.getClient().listFiles(ftpParentFile.getName());

                            for (FTPFile ftpFile : ftpChildFile) {
                                if (ftpFile.isDirectory()) {
                                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ftpParentFile.getName() + "/" + ftpFile.getName());
                                    if (FTPServerConnect.getClient().listFiles(ftpParentFile.getName() + "/" + ftpFile.getName()).length != 0) {
                                        childNode.add(new DefaultMutableTreeNode(""));
                                    }
                                    node.add(childNode);
                                }
                            }
                        }
                        node.remove(0);
                        treeServer.setEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
     
    private void loadFileServer(DefaultMutableTreeNode node) {
        ((DefaultTableModel) TableServer.getModel()).getDataVector().removeAllElements();
        try {
            FTPFile[] ftpFiles = FTPServerConnect.getClient().listFiles(node.toString());
            if (ftpFiles.length > 0) {
                Vector rowBack = new Vector();
                rowBack.add("..");
                modelFileServer.addRow(rowBack);
                for (FTPFile ftpFile : ftpFiles) {
                    Vector row = new Vector();
                    row.add(ftpFile.getName());
                    if (ftpFile.getSize() != 0) {
                        row.add(ftpFile.getSize() + " KB");
                    } else {
                        row.add("");
                    }
                    System.out.println(ftpFile.getLink());
                    row.add(ftpFile.getType());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY hh:mm:ss a");
                    String dateModified = dateFormat.format(ftpFile.getTimestamp().getTime());
                    row.add(dateModified);
                    modelFileServer.addRow(row);
                }
            } else {
                Vector rowBack = new Vector();
                rowBack.add("..");
                modelFileServer.addRow(rowBack);
            }
            TableServer.setModel(modelFileServer);
        } catch (IOException ex) {
            Logger.getLogger(FTPClientView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void comboboxServerItemStateChanged()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(recentServer.getSelectedItem().toString());
        loadFileServer(node);
    }
    private void comboboxLoader()
    {
        MutableTreeNode selectedNode = (MutableTreeNode) treeServer.getLastSelectedPathComponent();
        if(recentServer.getSize()>0)
        {
            boolean flag = false;
            for (int i = 0; i < recentServer.getSize(); i++) 
            {
                if(recentServer.getElementAt(i).equals(selectedNode.toString()))
                {
                    comboboxServer.setSelectedIndex(i);
                    flag = true;
                    break;
                }
            }
            if(!flag)
            {
                recentServer.addElement(selectedNode.toString());
                comboboxServer.setSelectedIndex(recentServer.getIndexOf(selectedNode.toString()));
            }
        }
        else
        {
            recentServer.addElement(selectedNode.toString());
            comboboxServer.setModel(recentServer);
        }
    }
    
    public void TableServerMouseClicked(MouseEvent evt) {                                         
        // TODO add your handling code here:
        if (TableServer.getSelectedRowCount() > 0) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                JPopupMenu popupMenuServer = PopupMenuManager.createServerMenu();
                popupMenuServer.show(TableServer, evt.getX(), evt.getY());
                deleteAction();
                downloadAction();
            }
        }
    }
    
    private void deleteAction() {
        PopupMenuManager.getDelete().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRowsCount = TableServer.getSelectedRows();
                try {
                    final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeServer.getLastSelectedPathComponent();
                    final FTPFile[] ftpFile = FTPServerConnect.getClient().listFiles(selectedNode.toString());
                    for (int i = 0; i < ftpFile.length; i++) {
                        final int count = i;
                        if (selectedRowsCount.length > 0) {
                            for (int j = 0; j < selectedRowsCount.length; j++) {
                                if (ftpFile[i].getName().equals(TableServer.getValueAt(selectedRowsCount[j], 0))) {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            delete(selectedNode.toString() + "/" + ftpFile[count].getName());                                          
                                        }
                                    });
                                    thread.start();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private synchronized void delete(String remote) {
        try {
            FTPFile ftpFile = FTPServerConnect.getClient().mlistFile(remote);
            if (ftpFile.isDirectory()) {
                System.out.println(remote);
                //String temp = remote;
                if (FTPServerConnect.getClient().listFiles(remote).length > 0) {
                    for (FTPFile f : FTPServerConnect.getClient().listFiles(remote)) {
                        String temp = remote + "/" + f.getName();
                        delete(temp);
                    }
                }
                boolean removeDir = FTPServerConnect.removeDir(remote);
                if (removeDir) {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Remove dir Success";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                } else {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Remove dir Faild";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                }
            } else {
                boolean delFile = FTPServerConnect.delete(remote);
                if (delFile) {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Delete Success";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                } else {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Delete Faild";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void downloadAction() {
        PopupMenuManager.getDownload().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRowsCount = TableServer.getSelectedRows();
                try {
                    final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeServer.getLastSelectedPathComponent();
                    final FTPFile[] ftpFile = FTPServerConnect.getClient().listFiles(selectedNode.toString());
                    for (int i = 0; i < ftpFile.length; i++) {
                        final int count = i;
                        if (selectedRowsCount.length > 0) {
                            for (int j = 0; j < selectedRowsCount.length; j++) {
                                if (ftpFile[i].getName().equals(TableServer.getValueAt(selectedRowsCount[j], 0))) {
                                    final String path = ((DefaultMutableTreeNode) treeLocal.getLastSelectedPathComponent()).toString();
                                    Thread thread =new Thread( new Runnable(){ 
                                        public void run() {
                                            download(selectedNode + "/" + ftpFile[count].getName(), path, ftpFile[count].getName());
                                        }
                                    });
                                    thread.start();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    private synchronized void download(String remote, String path, String dirName) {
        try {
            FTPFile ftpFile = FTPServerConnect.getClient().mlistFile(remote);
            if (ftpFile.isDirectory()) 
            {
                String dirPath = path + "\\" + dirName;
                boolean newDir = new File(dirPath).mkdir();
                if (newDir) {
                    File file = new File(dirPath);
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Created Directory";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                    FTPFile[] ftpFiles = FTPServerConnect.getClient().listFiles(ftpFile.getName());

                    for (FTPFile f : ftpFiles) {
                        path = file.getPath();
                        String tempRemote = remote + "/" + f.getName();
                        download(tempRemote, path, f.getName());
                    }
                } else {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Created Fail";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                }
            } else {
                boolean downFile = FTPServerConnect.download(path + "\\" + dirName, ftpFile.getName());
                if (downFile) {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Download Success";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                } else {
                    String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                    String status = "Status: Download Fail";
                    paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                    paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
