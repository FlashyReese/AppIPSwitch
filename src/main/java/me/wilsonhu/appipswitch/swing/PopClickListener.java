package me.wilsonhu.appipswitch.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class PopClickListener extends MouseAdapter {
	
	private AppIPSwitch gui;
	
	public PopClickListener(AppIPSwitch main) {
		this.gui = main;
	}
	
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger() && AppIPSwitch.getInstance().isItemSelected())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() && AppIPSwitch.getInstance().isItemSelected())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        PopUpMenu menu = new PopUpMenu(gui);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}