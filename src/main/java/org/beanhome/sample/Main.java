package org.beanhome.sample;

import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;



public class Main {
	public static void main(String[] args) {
		
		testSelenium();
		
		
		
		
//		 try {
//			 Document doc = Jsoup.connect("https://s.taobao.com/search?q=%E6%94%B6%E7%BA%B3%E6%9E%B6&imgfile=&js=1&stats_click=search_radio_tmall%3A1&initiative_id=staobaoz_20170816&tab=mall&ie=utf8") 
////					  .data("query", "Java")   // 请求参数
//					  .userAgent("I ’ m jsoup") // 设置 User-Agent 
////					  .cookie("auth", "token") // 设置 cookie 
//					  .timeout(3000)           // 设置连接超时时间
//					  .post();                 // 使用 POST 方法访问 URL 
//			 Elements links = doc.select("a[href]");
//			System.out.println(doc);
////			 System.out.println(links);
//		} catch (Exception e) {
//			System.err.print(e);
//		}
		 
//		 openDefaultBrowser();
	
	     
	}
	
	
	////div[@id='mainsrp-itemlist']//a[starts-with(@href,'//detail.tmall.com/item.htm')]
	public static void testSelenium() {
	    System.getProperties().setProperty("webdriver.chrome.driver",
	        "D:\\workspace\\beyond\\tmallspider\\src\\main\\resources\\chromedriver.exe");
	    WebDriver webDriver = new ChromeDriver();
	    webDriver.get("https://s.taobao.com/search?q=%E6%94%B6%E7%BA%B3%E6%9E%B6&imgfile=&js=1&stats_click=search_radio_tmall%3A1&initiative_id=staobaoz_20170816&tab=mall&ie=utf8");
	    List<WebElement> webElements = webDriver.findElements(By.xpath("//a[starts-with(@href,'//detail.tmall.com/item.htm')]"));
	  
//	    System.out.println(webElements.size());
	    HashSet<String> hashSet = new HashSet<String>();
	    for(WebElement webElement:webElements ){
	    	String itemUrl = webElement.getAttribute("href");
	    	if(!hashSet.contains(itemUrl)){
	    		hashSet.add(itemUrl);
	    		System.out.println(itemUrl);
	    	}
	    	
	    }
	    
	    
	    webDriver.close();
	}
	
//	/** 
//     * 打开默认浏览器访问页面 
//     */  
//    public static void openDefaultBrowser(){  
//        //启用系统默认浏览器来打开网址。  
//        try {  
//            URI uri = new URI("https://s.taobao.com/search?q=%E6%94%B6%E7%BA%B3%E6%9E%B6&imgfile=&js=1&stats_click=search_radio_tmall%3A1&initiative_id=staobaoz_20170816&tab=mall&ie=utf8");  
//            Desktop.getDesktop().browse(uri);  
//        } catch (URISyntaxException e) {  
//            e.printStackTrace();  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        }  
//    }  
   
}
