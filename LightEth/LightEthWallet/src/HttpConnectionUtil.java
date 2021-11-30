import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class HttpConnectionUtil {

	public static JsonObject sendHttpPostReq(String urlStr, String postData) throws IOException {
//	    URL url = new URL("https://rahakott.io/api/v1.1/wallets");
		URL url = new URL(urlStr);
//	    String postData = "{\"api_key\":\"ffab97a787c5c9fffdd4c483944a6048\",\"currency\":\"ETH\"}";
	    byte[] postDataBytes = postData.getBytes("UTF-8");
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json");
	    conn.setRequestProperty("Accept", "application/json");
	    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	    conn.setDoOutput(true);
	    conn.getOutputStream().write(postDataBytes);
	    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	    StringBuilder sb = new StringBuilder();
	    for (int c; (c = in.read()) >= 0;)
	        sb.append((char)c);
	    String response = sb.toString();
	    System.out.println(response);

 
//	    JsonObject jsonReuslt = new Gson().fromJson(response, JsonObject.class);
	    JsonObject jsonReuslt = new Gson().fromJson(response, JsonObject.class);
	    return jsonReuslt;
//	    System.out.println("wallet name: " + jsonReuslt.get(0).getAsJsonObject().get("name").getAsString());
	}
	
}
