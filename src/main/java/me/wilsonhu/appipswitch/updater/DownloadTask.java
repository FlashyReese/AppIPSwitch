package me.wilsonhu.appipswitch.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
 
/**
 * Execute file download in a background thread and update the progress.
 * @author www.codejava.net
 *
 */
public class DownloadTask extends SwingWorker<Void, Void> {
    private static final int BUFFER_SIZE = 4096;   
    private String downloadURL;
    private String saveDirectory;
    private AutoUpdater gui;
     
    public DownloadTask(AutoUpdater gui, String downloadURL, String saveDirectory) {
        this.gui = gui;
        this.downloadURL = downloadURL;
        this.saveDirectory = saveDirectory;
    }
     
    /**
     * Executed in background thread
     */
    @Override
    protected Void doInBackground() throws Exception {
        try {
            HTTPDownloadUtil util = new HTTPDownloadUtil();
            util.downloadFile(downloadURL);
             
            // set file information on the GUI
             
            String saveFilePath = saveDirectory + File.separator + util.getFileName();
            File file = new File(saveFilePath);
            new File(file.getParent()).mkdirs();
            InputStream inputStream = util.getInputStream();
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(file);
 
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = util.getContentLength();
 
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                double currentBytes = totalBytesRead/1000000;
                double totalBytes = fileSize/1000000;
                
                gui.log("Downloading Update","Downloading " + util.getFileName() + " (" + currentBytes + "/" + totalBytes + "MB)...");
                setProgress(percentCompleted);         
            }
 
            outputStream.close();
            util.disconnect();
            gui.setUpdateFile(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(gui, "Error downloading file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);           
            ex.printStackTrace();
            setProgress(0);
            cancel(true);          
        }
        return null;
    }
 
    /**
     * Executed in Swing's event dispatching thread
     */
    @Override
    protected void done() {
        if (!isCancelled()) {
        	gui.applyUpdate();
            //JOptionPane.showMessageDialog(gui, "File has been downloaded successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }  
}