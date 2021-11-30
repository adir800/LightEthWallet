
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ddd.xdb.util.XDBFile.XDBDir;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import entity.Channels;
import entity.Wallet;
import entity.Channel;

/**
 * Servlet implementation class GetBalance
 */
@WebServlet("/GetBalance")
public class GetBalance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet user;
	private Channels channels;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetBalance() {
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
		String balance = "66.6 ETH";

    	HttpSession session = request.getSession();
    	this.user = (Wallet) session.getAttribute("user");
		System.out.println("userName Is: " + user.name);

		// get both balance
		float realBalanceFloat = Float.parseFloat(getBalance());
		realBalanceFloat += this.getBalanceWallet(); // add wallet balance
		String realBalanceStr = String.valueOf(realBalanceFloat); // Covert back to string

		balance = realBalanceStr + " ETH";
		request.setAttribute("balance", balance);
		String address = this.user.current_address;
		request.setAttribute("address", address);
		request.getRequestDispatcher("balance.jsp").forward(request, response);

	}

	private String getBalance() {
		String urlStr = "https://rahakott.io/api/v1.1/wallets/balance";
		String postData = "{\"api_key\":\"ffab97a787c5c9fffdd4c483944a6048\",\"oid\":\"" + this.user.oid + "\"}";
		try {
			JsonObject resultJson = HttpConnectionUtil.sendHttpPostReq(urlStr, postData);
			String onChainConfirmedBalance = resultJson.get("confirmed").getAsString();
			System.out.println(" OnChain Balance: " + onChainConfirmedBalance);
			return onChainConfirmedBalance;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private float getBalanceWallet() {
		String ChannelsSumStr;
		if (getChannelsDb()) {
			return channels.getChannelsSum();
		} else {
			return 0;
		}
	}

	private boolean getChannelsDb() {
//		String dbPath = "C:/Users/Adir/Desktop/Braude/DB";
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, this.user.name);
		String channelDbPath = XDBDir.concatFileName(path, "channels.json");

		File channelsFile = new File(channelDbPath);
		if (channelsFile.exists()) {
			try {
				Reader reader = Files.newBufferedReader(Paths.get(channelDbPath));
				this.channels = new Gson().fromJson(reader, Channels.class);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

}
