/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftptransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;



/**
 *
 * @author Wise-Sw
 */
public class FTPServerConnect {
    private static FTPClient client;

    public static FTPClient getClient() {
        return client;
    }
    
    public static boolean logIn(String host,String userName,String pass)
    {
        boolean check = false;
        FTPServerConnect.client = new FTPClient();
        try
        {
            FTPServerConnect.client.connect(host);
            check=FTPServerConnect.client.login(userName, pass);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.println(check);
        return check;
    }
    
    public static void disconnect()
    {
        if(FTPServerConnect.client.isConnected())
        {
            try
            {
                boolean logOut = FTPServerConnect.client.logout();
                if(logOut)
                    FTPServerConnect.client.disconnect();
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    public static boolean delete(String path)
    {
        boolean check=false;
        if(FTPServerConnect.client.isConnected())
        {
            try {
                check = FTPServerConnect.client.deleteFile(path);
            } catch (IOException ex) {
                Logger.getLogger(FTPServerConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return check;
    }
    
    public static boolean upload(File file,String remote)
    {
        boolean check=false;
        if(FTPServerConnect.client.isConnected())
        {
            try {
                FileInputStream fis = new FileInputStream(file);
                check = FTPServerConnect.client.storeFile(remote+"/"+file.getName(), fis);
            } catch (IOException ex) {
                Logger.getLogger(FTPServerConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return check;
    }
    
    public static boolean createDir(String remote, String dirName)
    {
        boolean check=false;
        if(FTPServerConnect.client.isConnected())
        {
            try {
                check = FTPServerConnect.client.makeDirectory(remote+"/"+dirName);
            } catch (IOException ex) {
                Logger.getLogger(FTPServerConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return check;
    }
    
    public static boolean removeDir(String remote)
    {
        boolean check=false;
        if(FTPServerConnect.client.isConnected())
        {
            try {
                check = FTPServerConnect.client.removeDirectory(remote);
            } catch (IOException ex) {
                Logger.getLogger(FTPServerConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return check;
    }
    
    public static boolean download(String path, String remote)
    {
        boolean check = false;
        if(FTPServerConnect.client.isConnected())
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(new File(path));
                check = FTPServerConnect.client.retrieveFile(remote, fos);
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return check;
    }
}
