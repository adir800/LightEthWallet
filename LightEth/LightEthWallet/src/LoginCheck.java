

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ddd.xdb.util.XDBFile.XDBDir;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import entity.Wallet;
import javafx.scene.shape.Path;



/**
 * Servlet implementation class LoginCheck
 */
@WebServlet("/LoginCheck")
public class LoginCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet user;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginCheck() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        // read form fields
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username: " + username);
        System.out.println("password: " + password);
 
        // do some processing here...
        if(checkLogin(username, password)) {
        	
        	HttpSession session = request.getSession();
        	session.setAttribute("user", this.user);
        	Cookie userName = new Cookie("user", this.user.name);
        	response.addCookie(userName);
        	
	        RequestDispatcher rd = request.getRequestDispatcher("menu.html");
	        rd.forward(request, response);
        } else {
			RequestDispatcher rd = request.getRequestDispatcher("login.html");
			PrintWriter out= response.getWriter();
			out.println("<font color=red>Wrong username or password.</font>");
			rd.include(request, response);
        }
        // get response writer
//        PrintWriter writer = response.getWriter();
//
//        // build HTML code
//        String htmlRespone = "<html>";
//        htmlRespone += "<h2>Your username is: " + username + "<br/>";
//        htmlRespone += "Your password is: " + password + "</h2>";
//        htmlRespone += "</html>";
//
//        // return response
//        writer.println(htmlRespone);
//        //********************************************************************
//			checkLogin(username, password);
	}
	
	private void sendHttpPostReq() throws IOException {
	    URL url = new URL("https://rahakott.io/api/v1.1/wallets");
	    String postData = "{\"api_key\":\"ffab97a787c5c9fffdd4c483944a6048\",\"currency\":\"ETH\"}";
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

//	    Gson gson = new Gson(); 
	    
//	    Object[] userArray = gson.fromJson(response, Object[].class);  
//	     
//	    for(Object user : userArray) {
//	        System.out.println(user);
//	    }
	    
	    
//	    JsonObject jsonReuslt = new Gson().fromJson(response, JsonObject.class);
	    JsonArray jsonReuslt = new Gson().fromJson(response, JsonArray.class);
	    
	    System.out.println("wallet name: " + jsonReuslt.get(0).getAsJsonObject().get("name").getAsString());
	}
	
	private boolean checkLogin(String userName, String password) {
//		String dbPath = "C:/Users/Adir/Desktop/Braude/DB";
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, userName);
		System.out.println("path: " + path);
		File folder = new File(path);
		if (folder.exists()) {
			String filePath = XDBDir.concatFileName(path, "db.json");
			try {
//			FileReader file = new FileReader(filePath);
//		    BufferedReader reader = new BufferedReader(file);
//		    String result = reader.readLine();
			Reader reader = Files.newBufferedReader(Paths.get(filePath));
		    this.user = new Gson().fromJson(reader,Wallet.class);

		    if(password.equals(user.password)) {
		    	return true;
		    } else {
		    	return false;
		    }
			} catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			System.out.println("checkLogin: user not exist");
			return false;
		}
	}

}
