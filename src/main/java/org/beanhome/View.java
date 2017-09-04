package org.beanhome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class View implements ActionListener {

	JFrame jf;
	JPanel jpanel;
	JPanel jpanel1;
	JButton jb1, jb2;
	JTextArea jta = null;
	JScrollPane jscrollPane;
	JTextField jtf1;

	public View() {

		jf = new JFrame("天猫商品抓取软件 ");
		Container contentPane = jf.getContentPane();
		contentPane.setLayout(new BorderLayout());

		jta = new JTextArea(10, 15);
		jta.setTabSize(4);
		jta.setFont(new Font("标楷体", Font.BOLD, 16));
		// jta.setLi  neWrap(true);// 激活自动换行功能
		// jta.setWrapStyleWord(true);// 激活断行不断字功能
		jta.setBackground(Color.lightGray);
		 jta.setEditable(false);

		jscrollPane = new JScrollPane(jta);
		jpanel = new JPanel();
		jpanel.setLayout(new GridLayout(1, 2));

		jtf1 = new JTextField("输入链接地址");

		jpanel1 = new JPanel();
		jpanel1.setLayout(new GridLayout(1, 2));
		jb1 = new JButton("抓取");
		jb1.addActionListener(this);
		jb2 = new JButton("复制");
		jb2.addActionListener(this);

		jpanel1.add(jb1);
		jpanel1.add(jb2);

		jpanel.add(jtf1);
		jpanel.add(jpanel1);

		contentPane.add(jscrollPane, BorderLayout.CENTER);
		contentPane.add(jpanel, BorderLayout.NORTH);

		jf.setSize(500, 300);
		jf.setLocation(400, 200);
		jf.setVisible(true);

		jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// 覆盖接口ActionListener的方法actionPerformed
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jb1) {
			// 发送逻辑
			String url = jtf1.getText();
			jta.setText("请求中...");
			List<String> items = getItemList(url);
			jta.setText("");
			for(String item :items){
				jta.append(item+"\n");
			}
//			getItemList(url);

		} else if (e.getSource() == jb2) {
			// System.out.println(1234);
			jta.selectAll();
			jta.copy();
		}
	}

//	public static boolean isURL(String str) {
//		Pattern exp = Pattern
//				.compile(
//						"^http://[\\w-\\.]+(?:/|(?:/[\\w\\.\\-]+)*(?:/[\\w\\.\\-]+\\.do))?$",
//						Pattern.CASE_INSENSITIVE);
//
//		return exp.matcher(str).matches();
//
//	}
	String defaultPath = System.getProperty("user.dir")+"/lib/chromedriver.exe";
	public boolean setChromeDrive(){
		System.getProperties()
		.setProperty("webdriver.chrome.driver",defaultPath);
		return new File(defaultPath).exists();
	}

	
	public List<String> getItemList(String url) {
		ArrayList<String> urlList = new ArrayList<String>();
		if(!setChromeDrive()){
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "找不到谷歌浏览器驱动，请确认文件是否存在:"+defaultPath, "出错了",JOptionPane.ERROR_MESSAGE);
			return urlList;
			
		}
		WebDriver webDriver = new ChromeDriver();
		webDriver.get(url);
		List<WebElement> webElements = webDriver
				.findElements(By
						.xpath("//a[starts-with(@href,'//detail.tmall.com/item.htm')]"));
		//*[@id="J_Itemlist_TLink_549718342700"]
		// System.out.println(webElements.size());
		HashSet<String> hashSet = new HashSet<String>();
		
		for (WebElement webElement : webElements) {
			if(webElement.getText().isEmpty())continue;
			if(webElement.getText().contains("天猫超市"))continue;
			String itemUrl = webElement.getAttribute("href");
			System.out.println(webElement.getText());
			if (!hashSet.contains(itemUrl)) {
				hashSet.add(itemUrl);
				// System.out.println(itemUrl);
				urlList.add(itemUrl);
			}

		}

		webDriver.close();

		return urlList;
	}

	public static void main(String[] args) {
		

//		System.out.println(System.getProperty("user.dir"));
		new View();
	}
}