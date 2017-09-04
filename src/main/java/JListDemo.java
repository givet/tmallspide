

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class JListDemo extends JFrame {

	private static final long serialVersionUID = 1L;
	
	
	public JListDemo(File[] files){
		JList list = new JList();
		ImageListModel listModel = new ImageListModel();
		
		for(File file: files){
			listModel.addElement(file);
		}
		list.setModel(listModel);
		list.setCellRenderer(new ImageCellRender());
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(files.length/2);
		
		JScrollPane scrollPane = new JScrollPane(list);
		
		getContentPane().add(scrollPane,BorderLayout.CENTER);
		getContentPane().add(new JLabel("带图片的Jlist"),BorderLayout.NORTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
File[] files = new File("D:/1-pdd/1085").listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				return file.getName().endsWith("jpg");
			}
		});

		new JListDemo(files);
	}

}
