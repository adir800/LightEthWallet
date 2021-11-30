
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ddd.xdb.util.XDBFile.XDBDir;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import entity.Wallet;

/**
 * Servlet implementation class RegisterCheck
 */
@WebServlet("/RegisterCheck")
public class RegisterCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet wallet;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterCheck() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// read form fields
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		System.out.println("username: " + username);
		System.out.println("password: " + password);

		// do some processing here...

		// get response writer
		PrintWriter writer = response.getWriter();
		String htmlRespone;
		if (addToDB(username, password)) {
			// build HTML code
//			htmlRespone = "<html>";
//			htmlRespone += "<h2>Register: " + "<br/>";
//			htmlRespone += "username: " + username + "<br/>";
//			htmlRespone += "password: " + password + "</h2>";
//			htmlRespone += "</html>";
			
	        RequestDispatcher rd = request.getRequestDispatcher("login.html");
	        rd.forward(request, response);
		} else {
//			htmlRespone = "<html>";
//			htmlRespone += "<h2>Username already exist" + "</h2>";
//			htmlRespone += "</html>";
//			writer.println(htmlRespone);
			RequestDispatcher rd = request.getRequestDispatcher("register.html");
			PrintWriter out= response.getWriter();
			out.println("<font color=red>Username already exist.</font>");
			rd.include(request, response);
		}

		// return response
//		writer.printlnhtmlRespone);
	}

	private boolean addToDB(String userName, String password) {
//		String dbPath = "C:/Users/Adir/Desktop/Braude/DB";
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, userName);
		System.out.println("path: " + path);
		File folder = new File(path);
		if (folder.exists()) {
			return false;
		}
		boolean bool = folder.mkdirs();
		if (bool) {
			System.out.println("Directory created successfully");
			createWallet(userName, path, password);
			return true;
		} else {
			System.out.println("Sorry couldn’t create specified directory");
			return false;
		}
	}
	
	private boolean createWallet(String walletName, String path, String password) {
		String urlStr = "https://rahakott.io/api/v1.1/wallets/new";
		String postData = "{\"api_key\":\"ffab97a787c5c9fffdd4c483944a6048\",\"name\":\"" + walletName + "\",\"currency\":\"ETH\"}";
		try {
			JsonObject resultJson = HttpConnectionUtil.sendHttpPostReq(urlStr, postData);
			resultJson.addProperty("password", password);
			this.wallet = new Gson().fromJson(resultJson, Wallet.class);
			createDbFile(path, resultJson.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean createDbFile(String path, String fileData) {
		try {
			String fileName = "db";
			FileWriter file = new FileWriter(path + "/" + fileName + ".json");
			System.out.println("fileData: " + fileData);
		    BufferedWriter writer = new BufferedWriter(file);
		    writer.write(fileData);
		     
		    writer.close();
//			file.write(fileData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
