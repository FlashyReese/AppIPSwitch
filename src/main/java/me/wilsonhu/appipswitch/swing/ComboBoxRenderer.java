package me.wilsonhu.appipswitch.swing;

import java.awt.Color;
import java.awt.Component;
import java.net.NetworkInterface;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxRenderer extends BasicComboBoxRenderer {
	private static final long serialVersionUID = 1L	;
    private JLabel label;
    private Color textSelectionColor = Color.BLACK;
    private Color backgroundSelectionColor = Color.CYAN;
    private Color textNonSelectionColor = Color.BLACK;
    private Color backgroundNonSelectionColor = Color.WHITE;

	
	public ComboBoxRenderer() {
		super();
		label = new JLabel();
		setOpaque(true);
	}

	public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {

		
		NetworkInterface nid = (NetworkInterface)value;
        //label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(nid.getDisplayName());
        //label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(nid.getInetAddresses().nextElement().getHostAddress());

        if (isSelected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
	}
}