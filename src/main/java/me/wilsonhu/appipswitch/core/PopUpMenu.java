package me.wilsonhu.appipswitch.core;

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
    public PopUpMenu() {
        anItem = new JMenuItem("Delete!");
        anItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(AppIPSwitch.getInstance().isItemSelected()) {
					AppIPSwitch.getInstance().profileDropList.removeSelectedItem();
				}
			}
        });
        add(anItem);
    }
}