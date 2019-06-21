package me.wilsonhu.appipswitch.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;

import me.wilsonhu.appipswitch.AppIPSwitch;
import me.wilsonhu.appipswitch.config.Profile;
import mslinks.ShellLink;
import mslinks.ShellLinkException;

public class FileDropList extends JPanel implements DropTargetListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultListModel<Profile> listModel = new DefaultListModel<Profile>();
    private JScrollPane jScrollPane1;
    private JList<Profile> list;
    private AppIPSwitch gui;
    /**
     * Create the panel.
     */
    public FileDropList(AppIPSwitch main) {
        this.gui = main;
    	setLayout(null);
        list = new JList<Profile>();
        new DropTarget(list, this);
        list.setModel(listModel);
        list.setDragEnabled(true);
        FileListCellRenderer renderer = new FileListCellRenderer();
        list.setCellRenderer(renderer);
        //list.setTransferHandler(new FileTransferHandler());
        jScrollPane1 = new JScrollPane(list);
        this.setBorder(new LineBorder(Color.BLACK));
        add(jScrollPane1);
        
    }

    public JList<Profile> getList(){
    	return list;
    }
    
    public Profile getSelectedValue() {
    	
    	return list.getSelectedValue();
    }
    
    public void refresh() {
    	list.repaint();
    }
    
    public void setBounds(int x, int y, int x2, int y2) {
    	super.setBounds(x, y, x2, y2);
    }
    
    public void setSize(int x, int y) {
    	super.setSize(x, y);
    	jScrollPane1.setSize(this.getSize());
    }
    
    public void dragEnter(DropTargetDragEvent arg0) {
        // nothing
    }

    public void dragOver(DropTargetDragEvent arg0) {
        // nothing
    }

    public void dropActionChanged(DropTargetDragEvent arg0) {
        // nothing
    }

    public void dragExit(DropTargetEvent arg0) {
        // nothing
    }

    public void loadSettings() {
    	for(Profile p : gui.getProfileManager().getProfiles()) {
    		listModel.addElement(p);
    	}
    }
    
    public void addItem(Profile p ) {
    	listModel.addElement(p);
    }
    
    public void removeSelectedItem() {
    	listModel.removeElement(list.getSelectedValue());
    }
    
    public void drop(DropTargetDropEvent evt) {
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : files) {
                	if(file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("exe")) {
                		Profile profile = new Profile(file.getName(), "", file.getAbsolutePath(), false, false);
                        listModel.addElement(profile);
                	}
                	
                	if(file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("lnk")) {
                		ShellLink sl = new ShellLink(file);
    					if(sl.resolveTarget().substring(sl.resolveTarget().length()-3).toLowerCase().contains("exe")) {
    						Profile profile = new Profile(file.getName().replaceAll(".lnk", ""), "", sl.resolveTarget(), false, false);
    						listModel.addElement(profile);
    					}
                	}
                }
                gui.getJsonManager().writeProfilesJson();
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ShellLinkException e) {
			e.printStackTrace();
		} finally {
            evt.dropComplete(true);
        }
    }
}

class FileListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -7799441088157759804L;
    private FileSystemView fileSystemView;
    private JLabel label;
    private Color textSelectionColor = Color.BLACK;
    private Color backgroundSelectionColor = Color.CYAN;
    private Color textNonSelectionColor = Color.BLACK;
    private Color backgroundNonSelectionColor = Color.WHITE;

    FileListCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

    	Profile cfile = (Profile)value;
        File file = new File(cfile.getExecutable());
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(cfile.getName());
        label.setToolTipText(file.getPath());
        
        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}
