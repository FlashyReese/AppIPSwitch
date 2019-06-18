package me.wilsonhu.appipswitch;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.wilsonhu.appipswitch.config.JsonManager;
import me.wilsonhu.appipswitch.config.Profile;
import me.wilsonhu.appipswitch.config.ProfileManager;
import me.wilsonhu.appipswitch.config.Settings;
import me.wilsonhu.appipswitch.core.ComboBoxRenderer;
import me.wilsonhu.appipswitch.core.FileDropList;
import me.wilsonhu.appipswitch.core.Functions;
import me.wilsonhu.appipswitch.core.PopClickListener;

public class AppIPSwitch extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static AppIPSwitch instance;
	private JsonManager jsonManager;
	private Settings settings;
	
	private ProfileManager profileManager;
	private Functions functions;
	private Profile profile;
	
	
	//ForceBindIP Panel
	URI FORCE_BIND_IP_URL;
	private JPanel fbPanel;
	private JButton changeDir;
	private JButton download;
	private JLabel txtisFound;
	private JLabel txtForceBindIP;
	private JLabel txtcurrentDir;
	private JLabel txtForceBindIPVersion;
	//Profile
	public FileDropList profileDropList = new FileDropList();
	private JTextField txtName;
	private JLabel lbltxtName;
	
	
	//NIC Panel
	private JPanel nidPanel;
	private JButton refreshNID;
	private JLabel nidName;
	private JLabel nidAddress;
	private JLabel nidType;
	private JLabel nidMACAddress;
	private JComboBox<NetworkInterface> networkDevices;
	
	//Settings Panel
	private JPanel settingsPanel;
	private JLabel txtSettings;
	private JButton run;
	private JCheckBox x64;
	private JCheckBox useI;
	private JCheckBox useCMDOptions;
	private JTextField txtCMDOptions;
	
	public AppIPSwitch() {
		try {
			preStart();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("AppIPSwitch");
	    setLayout(null);
		setResizable(false);
	    //this.setLocationRelativeTo(null);
		this.setSize(880, 700);
	    //pack();
		initComponents();
	}

	private void preStart() throws URISyntaxException {
		this.FORCE_BIND_IP_URL = new URI("https://r1ch.net/projects/forcebindip");
	}

	private void initComponents() {
		//TODO: Start of NID Panel
		nidPanel = new JPanel();
		nidPanel.setLayout(null);
		nidPanel.setName("Network Interface");
		nidName = new JLabel("Network Device:");
		nidName.setFont(new Font("Tahoma", 1, 12));
		nidName.setBounds(8, 10, 110, 20);
		
		nidAddress = new JLabel("IPv4 Address: ");
		nidAddress.setFont(new Font("Tahoma",0, 12));
		nidAddress.setBounds(28, 30, 300, 20);
		
		nidMACAddress = new JLabel("MAC Address: ");
		nidMACAddress.setFont(new Font("Tahoma",0, 12));
		nidMACAddress.setBounds(28, 50, 300, 20);
		
		nidType = new JLabel("Network Type: ");
		nidType.setFont(new Font("Tahoma",0, 12));
		nidType.setBounds(28, 70, 300, 20);
		
		networkDevices = new JComboBox<NetworkInterface>();
		refreshNetworkInterfaces();
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		networkDevices.setRenderer(renderer);
		networkDevices.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	updateNIDLabels();
		    	updateProfileFromOptions();
		    }
		});
		networkDevices.setBounds(112, 4, 300, 26);
		
		refreshNID = new JButton("Refresh");
		refreshNID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				refreshNetworkInterfaces();
				updateProfileFromOptions();
			}
		});
		refreshNID.setBounds(414, 4, 80, 26);
		nidPanel.add(nidName);
		nidPanel.add(networkDevices);
		nidPanel.add(refreshNID);
		nidPanel.add(nidAddress);
		nidPanel.add(nidMACAddress);
		nidPanel.add(nidType);
		
		//End of NID Panel TODO: Start Settings Panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(null);
		settingsPanel.setName("Settings");
		
		txtSettings = new JLabel("Settings");
		txtSettings.setFont(new Font("Tahoma", 1, 12));
		txtSettings.setBounds(8, 4, 110, 20);
		
		x64 = new JCheckBox("Use 64-bit");
		x64.setFont(new Font("Tahoma", 0, 12));
		x64.setBounds(4, 24, 110, 22);
		x64.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	updateProfileFromOptions();
            	//System.out.println(profileDropList.getSelectedValue().getName() + " - x64 " + profileDropList.getSelectedValue().isX64());
            }
        });
		
		useI = new JCheckBox("Use Delayed Injection(-i)");
		useI.setFont(new Font("Tahoma", 0, 12));
		useI.setBounds(114, 24, 200, 22);
		useI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	updateProfileFromOptions();
            	//System.out.println(profileDropList.getSelectedValue().getName() + " - useI " + profileDropList.getSelectedValue().isUseI());
            }
        });
		
		useCMDOptions = new JCheckBox("Use Command Line Options");
		useCMDOptions.setFont(new Font("Tahoma", 0, 12));
		useCMDOptions.setBounds(4, 48, 200, 22);
		useCMDOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	if(useCMDOptions.isSelected()) {
            		txtCMDOptions.setEnabled(true);
            	}else {
            		txtCMDOptions.setEnabled(false);
            	}
            	updateProfileFromOptions();
            	//System.out.println(profileDropList.getSelectedValue().getName() + " - useCMD " + profileDropList.getSelectedValue().getCmdOptions());
            }
        });
		
		txtCMDOptions = new JTextField("");
		txtCMDOptions.setEnabled(false);
		txtCMDOptions.setFont(new Font("Tahoma", 0, 12));
		txtCMDOptions.setBounds(4, 72, 492, 22);
		txtCMDOptions.addKeyListener(new KeyAdapter() {
	         public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
	            	updateProfileFromOptions();
	            	requestFocusInWindow();
	            }
	         }
		});
		
		settingsPanel.add(txtSettings);
		settingsPanel.add(x64);
		settingsPanel.add(useI);
		settingsPanel.add(useCMDOptions);
		settingsPanel.add(txtCMDOptions);
		
		//End of Settings Panel TODO: Start of ForceBindIP Panel
		fbPanel = new JPanel();
		fbPanel.setLayout(null);
		fbPanel.setName("ForceBindIP");
		txtForceBindIP = new JLabel("ForceBindIP");
		txtForceBindIP.setFont(new Font("Tahoma", 1, 12));
		txtForceBindIP.setBounds(8, 2, 110, 20);
		
		txtisFound = new JLabel("Not Found");
		txtisFound.setFont(new Font("Tahoma",0, 12));
		txtisFound.setBounds(12, 22, 100, 20);
		
		txtForceBindIPVersion = new JLabel("");
		txtForceBindIPVersion.setFont(new Font("Tahoma",0, 12));
		txtForceBindIPVersion.setBounds(114, 22, 300, 20);
		
		txtcurrentDir = new JLabel("Directory: ");
		txtcurrentDir.setFont(new Font("Tahoma",0, 12));
		txtcurrentDir.setBounds(12, 42, 490, 20);
		
		changeDir = new JButton("Change Directory");
		changeDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
				 JFileChooser chooser = new JFileChooser();
				 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 chooser.setCurrentDirectory(new File(getSettings().getForceBindDir()));
				 chooser.setAcceptAllFileFilterUsed(false);
			        
				 if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					 String path = chooser.getSelectedFile().getAbsolutePath();
					 getFunctions().changeDir(path);
					 refreshFoundLabel();
					 checkItemSelected();
				 }  
			}
		});
		changeDir.setBounds(12, 62, 150, 26);
		
		download = new JButton("Download");
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(FORCE_BIND_IP_URL);
			        } catch (Exception e2) {
			            e2.printStackTrace();
			        }
			    }
			}
		});
		download.setBounds(164, 62, 150, 26);
		
		fbPanel.add(txtForceBindIP);
		fbPanel.add(txtisFound);
		fbPanel.add(txtForceBindIPVersion);
		fbPanel.add(txtcurrentDir);
		fbPanel.add(changeDir);
		fbPanel.add(download);
		
		profileDropList.getList().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				//if(profileDropList.getSelectedValue() != null && profile != profileDropList.getSelectedValue()) {
				profile = null;
				profile = profileDropList.getSelectedValue();
				checkItemSelected();
				if(isItemSelected())updateOptionsFromProfile();
				//}
			}
		});
		profileDropList.getList().addMouseListener(new PopClickListener());
		profileDropList.setSize(350, 610);
		profileDropList.setLocation(10, 50);
		
		txtName = new JTextField();
		txtName.setBounds(420, 50, 450, 30);
		txtName.setFont(new Font("Tahoma", 0, 18));
		txtName.addKeyListener(new KeyAdapter() {
	         public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
	            	updateProfileFromOptions();
	            	requestFocusInWindow();
	            }
	         }
	         /*public void keyTyped(KeyEvent e) {
				  char c = e.getKeyChar();
				  if (c == '/' || c == '\'' || c == ':' || c == '*' || c == '?' || c == '"' || c == '<' || c == '>' || c == '|') {
				     e.consume();
				  }
	         }*/
		});
		
		lbltxtName = new JLabel("Name: ");
		lbltxtName.setBounds(370, 50, 150, 30);
		
		run = new JButton("Run");
		run.setFont(new Font("Verdana", 1, 30));
		run.setBounds(370, 286, 500, 40);
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					getFunctions().runProcess(useI.isSelected(), x64.isSelected(), ((NetworkInterface)networkDevices.getSelectedItem()).getInetAddresses().nextElement().getHostAddress(), profileDropList.getSelectedValue().getExecutable(), useCMDOptions.isSelected() ? txtCMDOptions.getText() : "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateProfileFromOptions();
			}
		});
		
		
		//Panel Positioning
		nidPanel.setLocation(370, 82);
		nidPanel.setSize(500, 100);
		settingsPanel.setLocation(370, 184);
		settingsPanel.setSize(500, 100);
		fbPanel.setLocation(370, 328);
		fbPanel.setSize(500, 100);
		
		//Panel Customizing
		nidPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		settingsPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		fbPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		
		
		this.add(settingsPanel);
		this.add(profileDropList);
		this.add(nidPanel);
		this.add(fbPanel);
		this.add(run);
		this.add(txtName);
		this.add(lbltxtName);
		
	}
	
	public void updateOptionsFromProfile() {
		//System.out.println("CurrentProfile " + profile.getName());
		//System.out.println(String.format("CMD: %s x64: %s useI: %s NID: %s File: %s", profile.getCmdOptions(), profile.isX64(), profile.isUseI(), profile.getNID(), profile.getExecutable()));
		txtName.setText(profile.getName());
		x64.setSelected(profile.isX64());
		useI.setSelected(profile.isUseI());
		useCMDOptions.setSelected(profile.isUseCMDOptions());
		txtCMDOptions.setText(profile.getCmdOptions());
		loadSavedMACAddress(profile.getNID());
	}
	
	public void updateProfileFromOptions() {
		if(profileDropList.getSelectedValue() == null)return;
		if(!txtName.getText().trim().isEmpty()) {
			profileDropList.getSelectedValue().setName(txtName.getText());
		}else {
			txtName.setText(profileDropList.getSelectedValue().getName());
		}
		profileDropList.getSelectedValue().setCmdOptions(txtCMDOptions.getText());
		profileDropList.getSelectedValue().setX64(x64.isSelected());
		profileDropList.getSelectedValue().setUseI(useI.isSelected());
		profileDropList.getSelectedValue().setUseCMDOptions(useCMDOptions.isSelected());
		profileDropList.refresh();
		this.getProfileManager().getProfiles().clear();
		for(int i = 0; i < profileDropList.getList().getModel().getSize(); i++){
		     Profile p = profileDropList.getList().getModel().getElementAt(i);
		     this.getProfileManager().getProfiles().add(p);
		}
		this.getJsonManager().writeProfilesJson();
	}
	
	private void updateNIDLabels() {
		NetworkInterface currentInterface = (NetworkInterface)networkDevices.getSelectedItem();
		if(currentInterface == null)return;
		this.nidAddress.setText("IPv4 Address:     " + currentInterface.getInetAddresses().nextElement().getHostAddress());
		byte[] mac1 = null;
		String macAddress = "";
		try {
			mac1 = currentInterface.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
        if (mac1 != null) {
            for (int k = 0; k < mac1.length; k++) {
                macAddress = macAddress + String.format("%02X%s", mac1[k], (k < mac1.length - 1) ? "-" : "");
            }
        }
        if(macAddress.trim().equals("")) {
        	this.nidMACAddress.setText("MAC Address:     Not Available");
        }else {
        	this.nidMACAddress.setText("MAC Address:     " + macAddress);
        }
        this.nidType.setText("Network Type:   " + currentInterface.getName());
    	if(profileDropList.getSelectedValue() != null)profileDropList.getSelectedValue().setNID(macAddress);
	}
	
	public ProfileManager getProfileManager() {
		if(profileManager == null)profileManager = new ProfileManager();
		return profileManager;
	}
	
	public JsonManager getJsonManager() {
		if(jsonManager == null) jsonManager = new JsonManager();
		return jsonManager;
	}
	
	public Functions getFunctions() {
		if(functions == null) functions = new Functions();
		return functions;
	}
	
	public static AppIPSwitch getInstance() {
		if(instance == null) instance = new AppIPSwitch();
		return instance;
	}
	
	public Settings getSettings() {
		if(settings == null)settings = new Settings();
		return settings;
	}
	
	public void setSettings(Settings s) {
		this.settings = s;
	}
	
	public static void main(String[] args) {
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppIPSwitch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AppIPSwitch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AppIPSwitch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(AppIPSwitch.class.getName()).log(Level.SEVERE, null, ex);
        }
		EventQueue.invokeLater(new Runnable() {
            public void run() {
            	getInstance().setVisible(true);
            	getInstance().onReady();
            }
        });
	}
	
	public void refreshNetworkInterfaces() {
		networkDevices.removeAllItems();
		try {
			for(NetworkInterface nid: Collections.list(NetworkInterface.getNetworkInterfaces())) {
				
				if(nid.getInetAddresses().hasMoreElements() && nid.getInetAddresses().nextElement().getHostAddress().contains(".")) {
					networkDevices.addItem(nid);
				}
			}
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		updateNIDLabels();
	}

	public void loadSavedMACAddress(String mac) {
		int size = networkDevices.getItemCount();
		for (int i = 0; i < size; i++) {
		  NetworkInterface item = networkDevices.getItemAt(i);
		  byte[] mac1 = null;
		  String macAddress = "";
		  try {
			  mac1 = item.getHardwareAddress();
		  } catch (SocketException e) {
			  e.printStackTrace();
		  }
		  if (mac1 != null) {
			  for (int k = 0; k < mac1.length; k++) {
				  macAddress = macAddress + String.format("%02X%s", mac1[k], (k < mac1.length - 1) ? "-" : "");
			  }
		  }
		  if(mac.equalsIgnoreCase(macAddress) && !mac.trim().isEmpty()) {
			  networkDevices.setSelectedItem(item);
		  }
		}
		
	}
	public String readFileAsString(String fileName)throws Exception 
	  { 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	  } 
	
	public void refreshFoundLabel(){
		if(this.getFunctions().isProgramFound()){
			txtisFound.setText("Found");
			txtisFound.setForeground(Color.GREEN);
			try {
				txtForceBindIPVersion.setText("Version: " + readFileAsString(getSettings().getForceBindDir() + File.separator + getSettings().getForceBindVersionFile()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			txtisFound.setText("Not Found");
			txtisFound.setForeground(Color.RED);
			txtForceBindIPVersion.setText("");
		}
		
		txtcurrentDir.setText("Directory: " + getSettings().getForceBindDir());
		txtcurrentDir.setToolTipText(getSettings().getForceBindDir());
	}
	
	public boolean isItemSelected() {
		if(profileDropList.getSelectedValue() == null) {
			return false;
		}
		return true;
	}
	
	public void checkItemSelected() {
		if(isItemSelected() && getFunctions().isProgramFound()) {
			run.setEnabled(true);
			txtName.setEnabled(true);
			networkDevices.setEnabled(true);
			x64.setEnabled(true);
			useI.setEnabled(true);
			useCMDOptions.setEnabled(true);
		}else {
			run.setEnabled(false);
			txtName.setEnabled(false);
			networkDevices.setEnabled(false);
			x64.setEnabled(false);
			useI.setEnabled(false);
			useCMDOptions.setEnabled(false);
			txtCMDOptions.setEnabled(false);
		}
	}
	
	public void onReady() {
    	getInstance().getJsonManager().loadAllJsonSettings();
    	getInstance().refreshFoundLabel();
    	checkItemSelected();
	}
}