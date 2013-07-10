/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Wise-Sw
 */
public class PopupMenuManager {
    private static JPopupMenu popupMenuLocal;
    private static JPopupMenu popupMenuServer;
    private static JMenuItem upload= new JMenuItem("Upload");
    private static JMenuItem download = new JMenuItem("Download");
    private static JMenuItem delete = new JMenuItem("Delete");

    public static JMenuItem getUpload() {
        return upload;
    }

    public static JMenuItem getDownload() {
        return download;
    }

    public static JMenuItem getDelete() {
        return delete;
    }

    public static JPopupMenu createLocalMenu()
    {
        PopupMenuManager.upload.setIcon(new ImageIcon("D:\\upload.png"));
        PopupMenuManager.popupMenuLocal = new JPopupMenu();
        PopupMenuManager.popupMenuLocal.add(PopupMenuManager.upload);
        return PopupMenuManager.popupMenuLocal;
    }
    public static JPopupMenu createServerMenu()
    {
        PopupMenuManager.download.setIcon(new ImageIcon("D:\\download.png"));
        PopupMenuManager.delete.setIcon(new ImageIcon("D:\\delete.png"));
        PopupMenuManager.popupMenuServer = new JPopupMenu();
        PopupMenuManager.popupMenuServer.add(download);
        PopupMenuManager.popupMenuServer.add(delete);
        return PopupMenuManager.popupMenuServer;
    }
}
