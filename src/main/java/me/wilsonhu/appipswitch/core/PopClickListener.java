package me.wilsonhu.appipswitch.core;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class PopClickListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger() && AppIPSwitch.getInstance().isItemSelected())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() && AppIPSwitch.getInstance().isItemSelected())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        PopUpMenu menu = new PopUpMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}