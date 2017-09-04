package org.beanhome.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.beanhome.util.FileUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author 作者 ：chenzenan E-mail:chen.ze.nan@163.com
 * @version 创建时间：2012-8-10 下午04:35:48 Copyright ? 2012-8-10 Shanghai XXX Co.
 *          Ltd. All right reserved.
 */
public class HttpClientLogin {

	public static String transToString(Cookie[] cookies) {
		StringBuffer tmpcookies = new StringBuffer();
		for (Cookie c : cookies) {
			tmpcookies.append(c.toString() + ";");
		}
		return tmpcookies.toString();
	}

	public static HttpClient getLoginClient(String loginUrl, String username,
			String password) throws HttpException, IOException {
		HttpClient httpClient = new HttpClient();

		httpClient.getHostConfiguration().setProxy("168.4.3.237", 808);
		PostMethod postMethod = new PostMethod(loginUrl);

		// 设置登陆时要求的信息，用户名和密码
		NameValuePair[] data = {
				new NameValuePair(username.split("=")[0],
						username.split("=")[1]),
				new NameValuePair(password.split("=")[0],
						password.split("=")[1]) };
		postMethod.setRequestBody(data);
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.executeMethod(postMethod);
		return httpClient;
	}

	public static String doGetWithLoginClient(String dataUrl, HttpClient client)
			throws HttpException, IOException {
		GetMethod getMethod = new GetMethod(dataUrl);
		// 每次访问需授权的网址时需带上前面的 cookie 作为通行证
		getMethod.setRequestHeader("cookie", transToString(client.getState()
				.getCookies()));
		// getMethod.setRequestHeader("Referer", "http://www.cc");
		// getMethod.setRequestHeader("User-Agent", "www Spot");
		client.executeMethod(getMethod);
		return getMethod.getResponseBodyAsString();
	}

	public static void main(String[] args) {
		// 登陆 Url
		String loginUrl = "http://mms.pinduoduo.com/auth";

		// 需登陆后访问的 Url
		String dataUrl = "http://mms.pinduoduo.com/malls/445706/commit/category_list";

		try {
			HttpClient httpClient = getLoginClient(loginUrl,
					"username=pdd4457066294", "password=6283838wbh");

			getCat3(new File("c:/cat3.txt"), new File("c:/cat2.txt"),
					httpClient);

		} catch (HttpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	

	public static void getCat3(File cat3, File cat2, HttpClient httpClient) {
		List<JsonObject> jos = new ArrayList<JsonObject>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(cat2);
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				JsonParser jp = new JsonParser();
				JsonElement je = null;
				try {
					je = jp.parse(line);
				} catch (Exception e) {
					System.out.println("error:" + br.readLine());
					return;
				}
				if (je.isJsonObject())
					jos.add(je.getAsJsonObject());

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

		System.out.println(jos.size());

		for (JsonObject cat2jo : jos) {
			try {
				get(3, cat2jo.get("id").getAsString(), httpClient);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void get(int level, String parentId, HttpClient httpClient)
			throws HttpException, IOException {
		String dataUrl = "http://mms.pinduoduo.com/malls/445706/commit/categories?level=%s&parentId=%s";
		dataUrl = String.format(dataUrl, level, parentId);
		String text = doGetWithLoginClient(dataUrl, httpClient);

		JsonParser jp = new JsonParser();
		JsonElement je = null;
		try {
			je = jp.parse(text);
		} catch (Exception e) {
			System.out.println("error:" + text);
			return;
		}
		// System.out.println(je);

		JsonArray ja = null;
		if (je.isJsonArray())
			ja = je.getAsJsonArray();

		if (ja == null)
			return;
		Iterator<JsonElement> ji = ja.iterator();
		while (ji.hasNext()) {
			JsonElement jele = ji.next();
			JsonObject jo = jele.getAsJsonObject();
			if (level < 3) {
				get(level + 1, jo.get("id").getAsString(), httpClient);
			} else {
				System.out.println(jo);
				FileUtil.writeFile(new File("C:/cat3.txt"), jo.toString());
				// System.out.println(jo);
			}

		}
	}
	
	public static List<JsonObject>   getCat3FromFile(){
		List<JsonObject> jos = new ArrayList<JsonObject>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader("C:/cat3.txt");
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
				if (je.isJsonObject())
					jos.add(je.getAsJsonObject());

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

	

}
