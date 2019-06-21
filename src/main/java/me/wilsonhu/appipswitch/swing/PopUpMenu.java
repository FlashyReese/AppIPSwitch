package me.wilsonhu.appipswitch.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class PopUpMenu extends JPopupMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;
    public PopUpMenu(AppIPSwitch main) {
        anItem = new JMenuItem("Delete!");
        anItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(main.isItemSelected()) {
					main.profileDropList.removeSelectedItem();
					main.profileDropList.getList().setSelectedIndex(main.profileDropList.getList().getModel().getSize()-1);
					main.getFunctions().checkItemSelected();
					if(main.isItemSelected())main.getFunctions().updateOptionsFromProfile();
				}
			}
        });
        add(anItem);
    }
}