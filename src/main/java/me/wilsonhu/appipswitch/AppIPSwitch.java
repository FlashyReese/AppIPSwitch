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
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.wilsonhu.appipswitch.config.JsonManager;
import me.wilsonhu.appipswitch.config.Profile;
import me.wilsonhu.appipswitch.config.ProfileManager;
import me.wilsonhu.appipswitch.config.Settings;
import me.wilsonhu.appipswitch.core.Functions;
import me.wilsonhu.appipswitch.swing.ComboBoxRenderer;
import me.wilsonhu.appipswitch.swing.FileDropList;
import me.wilsonhu.appipswitch.swing.PopClickListener;

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
	public Profile profile;
	
	
	private JMenuBar jMenuBar;
	private JMenu jMenuHelp;
	
	//ForceBindIP Panel
	public URI FORCE_BIND_IP_URL;
	public JPanel fbPanel;
	public JButton changeDir;
	public JButton download;
	public JLabel txtisFound;
	public JLabel txtForceBindIP;
	public JLabel txtcurrentDir;
	public JLabel txtForceBindIPVersion;
	
	//Profile
	public FileDropList profileDropList = new FileDropList(this);
	public JTextField txtName;
	public JLabel lbltxtName;
	
	//NIC Panel
	public JPanel nidPanel;
	public JButton refreshNID;
	public JLabel nidName;
	public JLabel nidAddress;
	public JLabel nidType;
	public JLabel nidMACAddress;
	public JComboBox<NetworkInterface> networkDevices;
	
	//Settings Panel
	public JPanel settingsPanel;
	public JLabel txtSettings;
	public JButton run;
	public JCheckBox x64;
	public JCheckBox useI;
	public JCheckBox useCMDOptions;
	public JTextField txtCMDOptions;
	public JTextField txtExeDirectory;
	public JButton changeExeDir;
	
	
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
		/*
		 * Weird UI Bug when using JavaSE 11, this was built on JavaSE 1.8 setSize(880, 700)
		 * Probably should make this resizable 
		 */
		this.setSize(884, 490);
		initComponents();
	}
	
	public static void main(String[] args) {
		 try {
			 // set look and feel to system dependent
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 } catch (Exception ex) {
			 ex.printStackTrace();
		 }
		EventQueue.invokeLater(new Runnable() {
            public void run() {
            	getInstance().setVisible(true);
            	try {
					getInstance().onReady();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
	}
	
	public void onReady() throws Exception {
		getJsonManager().loadAllJsonSettings();
    	getFunctions().refreshFoundLabel();
    	getFunctions().checkItemSelected();
	}
	
	private void preStart() throws URISyntaxException {
		this.FORCE_BIND_IP_URL = new URI("https://r1ch.net/projects/forcebindip");
	}

	private void initComponents() {
		jMenuBar = new JMenuBar();
		jMenuHelp = new JMenu();
		jMenuHelp.setText("Help");
		JMenuItem checkForUpdates = new JMenuItem();
		checkForUpdates.setText("Check for Updates");
		checkForUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				File file = new File("AppIPSwitchUpdater.jar");
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
	    });
		jMenuHelp.add(checkForUpdates);
		
		jMenuBar.add(jMenuHelp);
		
		//TODO: Start of NID Panel
		nidPanel = new JPanel();
		nidPanel.setLayout(null);
		nidPanel.setName("Network Interface");
		nidName = new JLabel("Network Device:");
		nidName.setFont(new Font("Tahoma", 1, 12));
		nidName.setBounds(6, 6, 110, 20);
		
		nidAddress = new JLabel("IPv4 Address: ");
		nidAddress.setFont(new Font("Tahoma",0, 12));
		nidAddress.setBounds(8, 30, 300, 20);
		
		nidMACAddress = new JLabel("MAC Address: ");
		nidMACAddress.setFont(new Font("Tahoma",0, 12));
		nidMACAddress.setBounds(8, 50, 300, 20);
		
		nidType = new JLabel("Network Type: ");
		nidType.setFont(new Font("Tahoma",0, 12));
		nidType.setBounds(8, 70, 300, 20);
		
		networkDevices = new JComboBox<NetworkInterface>();
		getFunctions().refreshNetworkInterfaces();
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		networkDevices.setRenderer(renderer);
		networkDevices.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	getFunctions().updateNIDLabels();
		    	getFunctions().updateProfileFromOptions();
		    }
		});
		networkDevices.setBounds(112, 4, 300, 26);
		
		refreshNID = new JButton("Refresh");
		refreshNID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				getFunctions().refreshNetworkInterfaces();
				getFunctions().updateProfileFromOptions();
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
				getFunctions().updateProfileFromOptions();
            }
        });
		
		useI = new JCheckBox("Use Delayed Injection(-i)");
		useI.setFont(new Font("Tahoma", 0, 12));
		useI.setBounds(114, 24, 200, 22);
		useI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFunctions().updateProfileFromOptions();
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
            	getFunctions().updateProfileFromOptions();
            }
        });
		
		txtCMDOptions = new JTextField("");
		txtCMDOptions.setEnabled(false);
		txtCMDOptions.setFont(new Font("Tahoma", 0, 12));
		txtCMDOptions.setBounds(4, 72, 492, 22);
		txtCMDOptions.addKeyListener(new KeyAdapter() {
	         public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
	            	getFunctions().updateProfileFromOptions();
	            	requestFocusInWindow();
	            }
	         }
		});
		
		txtExeDirectory = new JTextField("");
		txtExeDirectory.setFont(new Font("Tahoma", 0, 12));
		txtExeDirectory.setBounds(4, 96, 412, 22);
		
		changeExeDir = new JButton("Select");
		changeExeDir.setBounds(416, 96, 80, 22);
		changeExeDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(profile != null) {
					File cDir = new File(profile.getExecutable());
					chooser.setCurrentDirectory(new File(cDir.getParent()));
				}else {
					chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				}
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Executables", "exe");   
				chooser.setFileFilter(filter);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					if(!isItemSelected() && profile == null) {
						Profile p = new Profile(chooser.getSelectedFile().getName(), "", chooser.getSelectedFile().getAbsolutePath(), false, false);
						profileDropList.addItem(p);
						profileDropList.getList().setSelectedIndex(profileDropList.getList().getModel().getSize()-1);
						getFunctions().checkItemSelected();
						if(isItemSelected())getFunctions().updateOptionsFromProfile();
					}
					String path = chooser.getSelectedFile().getAbsolutePath();
					txtExeDirectory.setText(path);
					profile.setExecutable(path);
				} 
				getFunctions().updateProfileFromOptions();
			}
		});
		
		settingsPanel.add(txtSettings);
		settingsPanel.add(x64);
		settingsPanel.add(useI);
		settingsPanel.add(useCMDOptions);
		settingsPanel.add(txtCMDOptions);
		settingsPanel.add(txtExeDirectory);
		settingsPanel.add(changeExeDir);
		
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
					 try {
						 getFunctions().refreshFoundLabel();
					 } catch (Exception e1) {
						 e1.printStackTrace();
					 }
					 getFunctions().checkItemSelected();
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
				//profile = null;
				profile = profileDropList.getSelectedValue();
				getFunctions().checkItemSelected();
				if(isItemSelected())getFunctions().updateOptionsFromProfile();
			}
		});
		profileDropList.getList().addMouseListener(new PopClickListener(this));
		profileDropList.setSize(350, 402);
		profileDropList.setLocation(10, 10);
		
		txtName = new JTextField();
		txtName.setBounds(420, 10, 450, 30);
		txtName.setFont(new Font("Tahoma", 0, 18));
		txtName.addKeyListener(new KeyAdapter() {
	         public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
	            	getFunctions().updateProfileFromOptions();
	            	requestFocusInWindow();
	            }
	         }
		});
		
		lbltxtName = new JLabel("Name: ");
		lbltxtName.setBounds(370, 10, 150, 30);
		
		run = new JButton("Run");
		run.setFont(new Font("Verdana", 1, 30));
		run.setBounds(370, 270, 500, 40);
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					getFunctions().runProcess(useI.isSelected(), x64.isSelected(), ((NetworkInterface)networkDevices.getSelectedItem()).getInetAddresses().nextElement().getHostAddress(), profileDropList.getSelectedValue().getExecutable(), useCMDOptions.isSelected() ? txtCMDOptions.getText() : "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				getFunctions().updateProfileFromOptions();
			}
		});
		
		//Panel Positioning
		nidPanel.setLocation(370, 42);
		nidPanel.setSize(500, 100);
		settingsPanel.setLocation(370, 144);
		settingsPanel.setSize(500, 124);
		fbPanel.setLocation(370, 312);
		fbPanel.setSize(500, 100);
		
		//Panel Customizing
		nidPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		settingsPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		fbPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		
		setJMenuBar(jMenuBar);
		this.add(settingsPanel);
		this.add(profileDropList);
		this.add(nidPanel);
		this.add(fbPanel);
		this.add(run);
		this.add(txtName);
		this.add(lbltxtName);
		
	}
	
	public void setSettings(Settings s) {
		this.settings = s;
	}
	
	public ProfileManager getProfileManager() {
		if(profileManager == null)profileManager = new ProfileManager();
		return profileManager;
	}
	
	public JsonManager getJsonManager() {
		if(jsonManager == null) jsonManager = new JsonManager(this);
		return jsonManager;
	}
	
	public Functions getFunctions() {
		if(functions == null) functions = new Functions(this);
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
	
	public boolean isItemSelected() {
		if(profileDropList.getSelectedValue() == null) {
			return false;
		}
		return true;
	}
}