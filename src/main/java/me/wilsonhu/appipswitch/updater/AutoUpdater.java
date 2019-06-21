package me.wilsonhu.appipswitch.updater;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
 

public class AutoUpdater extends JFrame implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;
	DownloadTask task;
	public String url = null;
	private File updateFile;
    private JLabel labelStatusType = new JLabel("Looking for updates");
     
    private JLabel labelStatus = new JLabel("Searching...");
    private JProgressBar progressBar = new JProgressBar(0, 100);
     
    public AutoUpdater() {
        super("AppIPSwitch Auto Updater");
 
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
 
        progressBar.setPreferredSize(new Dimension(200, 30));
        progressBar.setStringPainted(true);
         
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelStatusType, constraints);
         
        constraints.gridy = 4;
        constraints.gridx = 0;
        add(labelStatus, constraints);
 
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 1.0;
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, constraints);
 
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(400, 120);
    }
    
    public void startUpdate() throws InvocationTargetException, InterruptedException {
    	CheckForUpdate thread = new CheckForUpdate(this);
		thread.start();
		thread.getThread().join();
    	if(url == null) {
    		try {
    			startAppIPSwitchAndExit();
    		} catch (IOException | InterruptedException e) {
    			e.printStackTrace();
    		}
    		return;
    	}
    }
    
    public String getUpdateURL() {
    	try {
            URL url = new URL("https://raw.githubusercontent.com/FlashyReese/AppIPSwitch/master/updater/latesturl");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
             
            String line;
            while ((line = in.readLine()) != null) {
                return line;
            }
            in.close();
             
        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
            this.log("Error", "Couldn't retrieve latest update url");
        }
        catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            this.log("Error", "Couldn't retrieve latest update url");
        }
    	return null;
    }
    
    void startDownload(String url) {
    	String downloadURL = url;
        String saveDir = System.getProperty("user.dir") + File.separator + "update";
        try {
            progressBar.setValue(0);
 
            task = new DownloadTask(this, downloadURL, saveDir);
            task.addPropertyChangeListener(this);
            task.execute();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error executing upload task: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }  
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
    
    public void log(String type, String msg) {
    	System.out.println(type + ": " + msg);
    	labelStatusType.setText(type);
    	labelStatus.setText(msg);
    }
    
    public File getUpdateFile() {
		return updateFile;
	}

	public void setUpdateFile(File updateFile) {
		this.updateFile = updateFile;
	}

	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AutoUpdater hello = new AutoUpdater();
                hello.setVisible(true);
                try {
					hello.startUpdate();
				} catch (InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
            }
        });
    }

	public void applyUpdate() {
		unZipIt(this.getUpdateFile(), System.getProperty("user.dir"));
		this.log("Finished", "Updater has finished applying updates");
		try {
			startAppIPSwitchAndExit();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	} 
	
	public void startAppIPSwitchAndExit() throws IOException, InterruptedException {
		File jar = new File("AppIPSwitch.jar");
		if(!jar.exists()) {
			log("Couldn't find Application", "Downloading");
			url = this.getUpdateURL();
			startDownload(url);
			return;
		}
		JOptionPane.showMessageDialog(this, labelStatus.getText(), labelStatusType.getText(), JOptionPane.INFORMATION_MESSAGE);
		File file = new File("AppIPSwitch.jar");
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	private void unZipIt(File zipFile, String outputFolder){
		byte[] buffer = new byte[1024];
		try{
			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdir();
			}
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    	ZipEntry ze = zis.getNextEntry();	
	    	while(ze!=null){
	    		String fileName = ze.getName();
	    		if(ze.isDirectory()) {
	    			ze = zis.getNextEntry();
	    			continue;
	    		}
	    		File newFile = new File(outputFolder + File.separator + fileName);
	    		this.log("Unpacking and Applying Update", "Unpacking " + newFile.getName());
	            new File(newFile.getParent()).mkdirs();
	            FileOutputStream fos = new FileOutputStream(newFile);             
	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	       		fos.write(buffer, 0, len);
	            }
	            fos.close();
	            this.log("Unpacking and Applying Update", "Applying " + newFile.getName());
	            ze = zis.getNextEntry();
	    	}
	        zis.closeEntry();
	    	zis.close();
	    	System.out.println("Done");
		}catch(IOException ex){
			ex.printStackTrace(); 
		}
	}
	
}