package org.beanhome.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.beanhome.util.FileUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MarketAnalysis2 {
	static String defaultPath = System.getProperty("user.dir")
			+ "/lib/chromedriver.exe";

	public static boolean setChromeDrive() {
		System.getProperties().setProperty("webdriver.chrome.driver",
				defaultPath);
		return new File(defaultPath).exists();
	}
	
	public static String getQueryUrl(String query){
		return "https://s.taobao.com/search?q="+query+"&imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&spm=a21bo.50862.201856-taobao-item.1&ie=utf8&initiative_id=tbindexz_20170904&sort=sale-desc";
	}
	
	public static int getSaleSampleCount(WebDriver webDriver,String query){
		webDriver.get(getQueryUrl(query));
		List<WebElement> webElements = webDriver
				.findElements(By.className("deal-cnt"));
		HashSet<String> hashSet = new HashSet<String>();
		int count =0;
		for (WebElement webElement : webElements) {
			try {
				count +=Integer.parseInt(webElement.getText().replace("人收货", ""));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return count;
	}

	
	public static void genMarketSalesCount(WebDriver webDriver){
        List<JsonObject> jos =HttpClientLogin.getCat3FromFile();
		
		for(JsonObject jo:jos){
			int count=getSaleSampleCount(webDriver, jo.get("cat_name").getAsString());
			jo.addProperty("saleCount", count);
			
			FileUtil.writeFile(new File("C:/Users/dvlp/Desktop/saleCount.txt"), jo.toString());
		}
	}
	
	
	
	public static List<Item>   getCat3SaleCountFromFile(){
		List<Item> jos = new ArrayList<Item>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader("C:/Users/dvlp/Desktop/saleCount.txt");
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				JsonParser jp = new JsonParser();
				JsonElement je = null;
				try {
					je = jp.parse(line);
				} catch (Exception e) {
					System.out.println("error:" + br.readLine());
				}
				if (je.isJsonObject()){
					JsonObject jo = je.getAsJsonObject();
					Item item = new Item(jo.get("saleCount").getAsInt(),jo);
					jos.add(item);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();

			} catch (Exception e2) {
			}
		}
		return jos;
	}
	
	
	public static void main(String[] args) {
		List<Item> items = getCat3SaleCountFromFile();
		Collections.sort(items);
		List<Item> subJos = items.subList(0, 20);
		for(Item item:subJos){
			System.out.println(item.getShortDesc());
		}
		

		
		

	}

	public static int time_wait=2000;
	public static void select(WebDriver webDriver, int typeNum, String parentTxt) {
		WebElement Btn1 = webDriver.findElement(By
				.xpath("//div[@class='goods-key-info-body']/div[1]/div["
						+ typeNum + "]"));

		String btnId = Btn1.getAttribute("id");
		String suffix = btnId.substring(9);
//		System.out.println(suffix);
		Btn1.click();

		try {
			Thread.sleep(time_wait);
		} catch (Exception e) {
			// TODO
		}

		WebElement pddOptionPan = webDriver.findElement(By
				.xpath("//div[@id='PddOptions"+suffix+"']"));
		List<WebElement> pddOptions = pddOptionPan.findElements(By
				.tagName("a"));
		List<String> vals = new ArrayList<String>();
		for (WebElement pddOption : pddOptions) {
			vals.add(pddOption.getAttribute("val"));
		}
		for (String val : vals) {
			 webDriver.findElement(By
					.xpath("//div[@class='goods-key-info-body']/div[1]/div["
							+ typeNum + "]")).click();
			WebElement we = pddOptionPan.findElement(By.xpath("//a[@val='"
					+ val + "']"));
			String text = we.getText();
			if (typeNum <= 2) {
				we.click();
				select(webDriver, typeNum + 1, text);
			}else{
				System.out.println(parentTxt+" "+text);
			}

			try {
				Thread.sleep(time_wait);
			} catch (Exception e) {
				// TODO
			}

		}

		// if(typeNum==2){
		// WebElement pddOptionPan =
		// webDriver.findElement(By.xpath("//div[@class='pdd-dui-options-wrap']"));
		// List<WebElement> pddOptions =
		// pddOptionPan.findElements(By.tagName("a"));
		// for(WebElement pddOption:pddOptions){
		// System.out.println(parentTxt+" "+pddOption.getText());
		//
		// }
		// return;
		// }

	}
	
	
	
	
}
class Item implements Comparable<Item>{

	private int salesCount;
	private JsonObject jo;
	
	
	
	public Item(int salesCount, JsonObject jo) {
		super();
		this.salesCount = salesCount;
		this.jo = jo;
	}



	public int compareTo(Item o) {
		if(salesCount>o.salesCount)return -1;
		if(salesCount==o.salesCount)return 0;
		return 1;
	}

//"cat_name":"健身","cat_desc":"书籍杂志三级"
	public String getShortDesc(){
		return fixLength(jo.get("cat_name").getAsString(),30)
				+" "+fixLength(jo.get("cat_desc").getAsString(),20)
				+" "+salesCount;
	}
	
	public String fixLength(String str,int len){
		return String.format("%-"+len+"s", str);
	}

	public JsonObject getJo() {
		return jo;
	}
	
	
	
}