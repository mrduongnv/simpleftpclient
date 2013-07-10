/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import view.FTPClientView;

/**
 *
 * @author Wise-Sw
 */
public class Client {
    private DefaultTableModel modelFileLocal;
    private JTable TableLocal;
    private DefaultMutableTreeNode rootLocal;
    private DefaultTreeModel treeModelLocal;
    private DefaultMutableTreeNode localDisc;
    private JTree treeLocal;
    private JEditorPane paneNotify;
    private JTree treeServer;
    private DefaultComboBoxModel recentLocal = new DefaultComboBoxModel();
    private JComboBox comboboxLocal;
    private JProgressBar bar;

    public Client(JTable TableLocal , JTree treeLocal, JEditorPane paneNotify, JTree treeServer, JComboBox comboboxLocal,JProgressBar bar) {
        this.TableLocal = TableLocal;
        this.treeLocal = treeLocal;
        this.paneNotify = paneNotify;
        this.treeServer = treeServer;
        this.comboboxLocal= comboboxLocal;
        this.bar = bar;
    }
    
    public void createColumn() {
        modelFileLocal = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelFileLocal.addColumn("Filename");
        modelFileLocal.addColumn("Filesize");
        modelFileLocal.addColumn("Filetype");
        modelFileLocal.addColumn("Last modified");
        TableLocal.setModel(modelFileLocal);
    }
    
    public void loadLocalTree() {
        rootLocal = new DefaultMutableTreeNode(new File("Computer"));

        treeModelLocal = new DefaultTreeModel(rootLocal);
        File[] file = File.listRoots();
        for (int i = 0; i < file.length; i++) {
            localDisc = new DefaultMutableTreeNode(file[i]);
            if (file[i].isDirectory() && file[i].listFiles() != null) {
                localDisc.add(new DefaultMutableTreeNode(new File("**")));
            }
            rootLocal.add(localDisc);
        }
        treeLocal.setModel(treeModelLocal);
        treeLocal.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                treeLocal.setSelectionPath(event.getPath());
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeLocal.getLastSelectedPathComponent();
                loadDirLocal(selectedNode);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });

        treeLocal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (treeLocal.getSelectionCount() > 0) {
                      comboBoxLoader();
                      loadFileLocal((DefaultMutableTreeNode) treeLocal.getLastSelectedPathComponent());
                }
            }
        });
        treeLocal.setDragEnabled(true);
        treeLocal.setCellRenderer(new localTreeCellRenderer());
        treeLocal.setShowsRootHandles(true);
    }

    private void loadDirLocal(DefaultMutableTreeNode node) {
        if (node != null) {
            if (node.getChildCount() != 0) {
                if (node.getChildAt(0).toString().equals("**")) {
                    File parentFile = (File) node.getUserObject();
                    treeLocal.setEnabled(false);
                    if (parentFile.isDirectory()) {
                        File[] childFile = FileSystemView.getFileSystemView().getFiles(parentFile, false);
                        for (File file : childFile) {
                            if (file.isDirectory()) {
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
                                if (file.listFiles().length > 0) {
                                    childNode.add(new DefaultMutableTreeNode(new File("**")));
                                }
                                node.add(childNode);
                            }
                        }
                    }
                    node.remove(0);
                    treeLocal.setEnabled(true);
                }
            }
        }
    }
    private void loadFileLocal(DefaultMutableTreeNode node) {
        ((DefaultTableModel) TableLocal.getModel()).getDataVector().removeAllElements();
        File file = (File) node.getUserObject();
        File[] childFile = FileSystemView.getFileSystemView().getFiles(file, false);
        if (childFile.length > 0) {
            final JLabel fileName = new JLabel();
            Vector folderBack = new Vector();
            folderBack.add("..");
            modelFileLocal.addRow(folderBack);
            for (File f : childFile) {
                Vector row = new Vector();
                fileName.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
                fileName.setText(FileSystemView.getFileSystemView().getSystemDisplayName(f));
                row.add(f.getName());
                double size = f.length() / 1024;
                if (f.listFiles() == null) {
                    row.add(size + " KB");
                } else {
                    row.add("");
                }
                row.add(FileSystemView.getFileSystemView().getSystemTypeDescription(f));
                Date date = new Date(f.lastModified());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY hh:mm:ss a");
                String dateModified = dateFormat.format(date);
                row.add(dateModified);
                modelFileLocal.addRow(row);
            }
        } else {
            Vector folderBack = new Vector();
            folderBack.add("..");
            modelFileLocal.addRow(folderBack);
        }
        TableLocal.setModel(modelFileLocal);
    }
    
    public void comboboxLocalItemStateChanged() {                                               
        // TODO add your handling code here:
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File(comboboxLocal.getSelectedItem().toString()));
        loadFileLocal(node);
    }
    private void comboBoxLoader() {
        MutableTreeNode selectedNode = (MutableTreeNode) treeLocal.getLastSelectedPathComponent();
        if (recentLocal.getSize() > 0) {
            boolean flag = false;
            for (int i = 0; i < recentLocal.getSize(); i++) {
                if (selectedNode.toString().equals(recentLocal.getElementAt(i).toString())) {
                    comboboxLocal.setSelectedIndex(i);
                    flag = false;
                    break;
                } else {
                    flag = true;
                }
            }
            if (flag) {
                //recentPath.removeAllElements();
                recentLocal.addElement(selectedNode.toString());
                comboboxLocal.setSelectedIndex(recentLocal.getIndexOf(selectedNode.toString()));
                //jComboBox3.setModel(recentPath);
            }
        } else {
            recentLocal.addElement(selectedNode.toString());
            comboboxLocal.setModel(recentLocal);
        }
    }
    
    public void TableLocalMouseClicked(MouseEvent evt) {                                        
        // TODO add your handling code here:
        if (TableLocal.getSelectedRowCount() > 0) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                JPopupMenu popupMenuLocal = PopupMenuManager.createLocalMenu();
                popupMenuLocal.show(TableLocal, evt.getX(), evt.getY());
                uploadAction();
            }
        }
    }         
    
    private void uploadAction() {
        PopupMenuManager.getUpload().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                final int[] selectedRowsCount = TableLocal.getSelectedRows();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeLocal.getLastSelectedPathComponent();
                File parentfile = (File) selectedNode.getUserObject();
                final File[] childFiles = FileSystemView.getFileSystemView().getFiles(parentfile, false);
                for (int i = 0; i < childFiles.length; i++) {
                    final int count = i;
                    if (selectedRowsCount.length > 0) {
                        int upCount = 0;
                        for (int j = 0; j < selectedRowsCount.length; j++) {
                            upCount ++;
                            final int temp = upCount;
                            if (childFiles[i].getName().equals(TableLocal.getValueAt(selectedRowsCount[j], 0))) {
                                final String remote = ((DefaultMutableTreeNode) treeServer.getLastSelectedPathComponent()).toString();
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        upload(childFiles[count], remote);
                                    }
                                });
                                thread.start();

                            }
                        }
                    }
                }
            }
        });
    }
    private synchronized void upload(File file, String remote) {
        
        paneNotify.setText(paneNotify.getText()+"Uploading: " + file.getName() + "...\n");
        
        if (file.isDirectory()) {
            boolean upDir = FTPServerConnect.createDir(remote, file.getName());
            if (upDir) {
                String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                String status = "Status: Created Directory";
                paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
                for (File f : file.listFiles()) {
                    String tempRemote = remote + "/" + file.getName();
                    upload(f, tempRemote);
                }
            } else {
                String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                String status = "Status: Create Fail";
                paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
            }
        } else {
            
            boolean upFile = FTPServerConnect.upload(file, remote);
            
            if (upFile) {
                String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                String status = "Status: Upload Success";
                paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
            } else {
                String reply = "Response: " + FTPServerConnect.getClient().getReplyString();
                String status = "Status: Upload Faild";
                paneNotify.setText(paneNotify.getText() + reply + "\n" + status + "\n");
                paneNotify.setSelectionEnd(paneNotify.getDocument().getLength());
            }
            
        }

    }
}
