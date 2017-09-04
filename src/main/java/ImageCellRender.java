


import java.awt.Component;
import java.awt.Label;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

public class ImageCellRender extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof File) {
			File imageFile = (File) value;
			try {
				ImageIcon icon = new ImageIcon(imageFile.toURI().toURL());
				setIcon(icon);
				setText(imageFile.getName());
				setVerticalTextPosition(SwingConstants.BOTTOM);
				setHorizontalTextPosition(SwingConstants.CENTER);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return this;
	}
}

