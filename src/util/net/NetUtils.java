package util.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class NetUtils {

	public static final String CHARSET = "UTF-8";
	
	public static String httpGet(String pageUrl, HttpHeader header) throws Exception{
		return getAction(pageUrl, header);
	}
	
	public static String httpGet(String pageUrl) throws Exception{
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.addParam("Cookie","lianjia_uuid=0d23d166-e3d9-4fcb-83c7-d4092329d996; lianjia_token=2.004d8c51e03486bc485c2178d116a5a09b; _jzqx=1.1488869749.1488869749.1.jzqsr=captcha%2Elianjia%2Ecom|jzqct=/.-; UM_distinctid=15ac52ca0891-0cac9462e32eff-57e1b3c-232800-15ac52ca08acc2; _UC_agent=1; _jzqa=1.1510479098958538500.1488869749.1499648652.1499733135.6; _qzja=1.2124754629.1488869748580.1499648652458.1499733135277.1499733140235.1499733591670.0.0.0.19.6; Hm_lvt_efa595b768cc9dc7d7f9823368e795f1=1499733213; all-lj=78917a1433741fe7067e3641b5c01569; select_city=440300; _smt_uid=5838eb29.10a4a54c; CNZZDATA1255849469=641029319-1480122892-%7C1501717335; Hm_lvt_9152f8221cb6243a53c83b956842be8a=1500943107,1501115519; Hm_lpvt_9152f8221cb6243a53c83b956842be8a=1501719622; CNZZDATA1254525948=96801367-1480120471-%7C1501718678; CNZZDATA1255633284=910940289-1480121700-%7C1501716676; CNZZDATA1255604082=503361032-1480123341-%7C1501717250; _ga=GA1.2.600642120.1480125231; _gid=GA1.2.1752783769.1501548607; lianjia_ssid=12b9dcfb-59cf-48d5-8a37-e020335080c9");
		return getAction(pageUrl, httpHeader);
	}
	
	private static String getAction(String pageUrl, HttpHeader header) throws Exception{
		@SuppressWarnings("resource")
		HttpClient client  = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet();
	    httpGet.setURI(new URI(pageUrl));
	    String content = "";
	    if(header != null){
	    	httpGet = header.attachHeader(httpGet);
	    }
	    BufferedReader in=null;
	    try {
			HttpResponse response = client.execute(httpGet);      
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {      
			    	in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			    	StringBuffer sb = new StringBuffer("");
			    	String line = "";
			    	while((line = in.readLine())!=null){
			    		sb.append(line).append("\n");
			    	}
			    	in.close();
			    	content = sb.toString();
			        
			} else {
				throw new Exception("网络解析错误:" + response.getStatusLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
	    	if(in != null){
	    		in.close();
	    	}
	    }
	    return content;
	}
	


	public static String post(String postUrl, HashMap<String, String> map,
			String encoding) throws Exception {
		URL url = new URL(postUrl);
		HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream(), encoding);
			osw.write(parseParam(map));
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();

	}

	private static String parseParam(HashMap<String, String> map)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			sb.append(key).append("=").append(map.get(key));
			sb.append("&");
		}
		System.out.println("param:" + sb.substring(0, sb.length() - 1));
		return sb.substring(0, sb.length() - 1);

	}

}
