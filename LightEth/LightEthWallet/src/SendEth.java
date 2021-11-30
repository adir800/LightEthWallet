
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
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

import entity.Channel;
import entity.Channels;
import entity.Wallet;

/**
 * Servlet implementation class SendEth
 */
@WebServlet("/SendEth")
public class SendEth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet user;
	private Channels channels;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendEth() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dest_address = request.getParameter("dest_address");
		String amountStr = request.getParameter("amount");
		float amount = Float.parseFloat(amountStr);
    	HttpSession session = request.getSession();
    	this.user = (Wallet) session.getAttribute("user");
		if (this.user.name.equalsIgnoreCase(dest_address)) {
			RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
			PrintWriter out = response.getWriter();
			out.println("<font color=red>You cant send to your self!.</font>");
			rd.include(request, response);
		} else {
			// ********************** added

			if (amount <= 0) {
				// cant send nagtive or 0 anount
				RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>You cant send a nagtive or 0 amount!.</font>");
				rd.include(request, response);
			} else {
				// no money in the account to send
				float thisWalletBalance = Float.valueOf(getBalance());
				this.channels = getChannelsDb(this.user.name);
				float negativeChannelsBalance;
				if (this.channels != null) {
					negativeChannelsBalance = this.channels.getNegativeBalance();
				} else {
					negativeChannelsBalance = 0;
				}
				System.out.println("thisWalletBalance - negativeChannelsBalance < amount: " + thisWalletBalance
						+ negativeChannelsBalance + "<" + amount);
				if (thisWalletBalance + negativeChannelsBalance < amount) {
					RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
					PrintWriter out = response.getWriter();
					out.println(
							"<font color=red>You cant Deposit when your negative channels balance is bigger then your on-chain balance.</font>");
					rd.include(request, response);

					// *****************************
				} else {
					if (getUserWallet(dest_address) != null) {
						// Transfer money
						addToChannelDb(this.user.name, dest_address, -amount);
						addToChannelDb(dest_address, this.user.name, +amount);
						RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
						PrintWriter out = response.getWriter();
						out.println("<font color=green>Payment Successfully!.</font>");
						rd.include(request, response);
					} else {
						// if receiver account doesn't exist
						RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
						PrintWriter out = response.getWriter();
						out.println("<font color=red>Username not exist!.</font>");
						rd.include(request, response);
					}
				}
			}
		}

	}

	private boolean addToChannelDb(String userName, String dest_address, float amount) {
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, userName);
		String channelDbPath = XDBDir.concatFileName(path, "channels.json");

		System.out.println("path: " + channelDbPath);
		File channelsFile = new File(channelDbPath);
		if (channelsFile.exists()) {
			try {
				Reader reader = Files.newBufferedReader(Paths.get(channelDbPath));
				this.channels = new Gson().fromJson(reader, Channels.class);

				int channelIndex = checkIfChannelExist(this.channels, dest_address);
				if (channelIndex > -1) {

					this.channels.channels.get(channelIndex).balance += (amount);
					String fileData = new Gson().toJson(channels, Channels.class);
					createChannelsDb(path, fileData);
				} else {
					// new channel
					Channel channel = new Channel(dest_address, amount);
					this.channels.channels.add(channel);
					String fileData = new Gson().toJson(channels, Channels.class);
					createChannelsDb(path, fileData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			System.out.println("createChannelsDb: create File");
			this.channels = new Channels();
			Channel channel = new Channel(dest_address, amount);
			this.channels.channels.add(channel);
			String fileData = new Gson().toJson(channels, Channels.class);
			createChannelsDb(path, fileData);
			return false;
		}
	}

	private int checkIfChannelExist(Channels channels, String dest_address) {
		int index = -1;
		for (int i = 0; i < channels.channels.size(); i++) {
			if (channels.channels.get(i).destination_address.equalsIgnoreCase(dest_address)) {
				index = i;
				return index;
			}
		}
		return index;
	}

	private boolean createChannelsDb(String path, String fileData) {
		try {
			String fileName = "channels";
			FileWriter file = new FileWriter(path + "/" + fileName + ".json", false);
			System.out.println("fileData: " + fileData);
			BufferedWriter writer = new BufferedWriter(file);
			writer.write(fileData);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Wallet getUserWallet(String userName) {
//		String dbPath = "C:/Users/Adir/Desktop/Braude/DB";
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, userName);
		System.out.println("path: " + path);
		File folder = new File(path);
		if (folder.exists()) {
			String filePath = XDBDir.concatFileName(path, "db.json");
			try {
				Reader reader = Files.newBufferedReader(Paths.get(filePath));
				Wallet user = new Gson().fromJson(reader, Wallet.class);
				return user;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
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

	private Channels getChannelsDb(String userName) {
		Channels channels = null;
		String dbPath = Constants.path;
		String path = XDBDir.concatFileName(dbPath, userName);
		String channelDbPath = XDBDir.concatFileName(path, "channels.json");

		System.out.println("path: " + channelDbPath);
		File channelsFile = new File(channelDbPath);
		if (channelsFile.exists()) {
			try {
				Reader reader = Files.newBufferedReader(Paths.get(channelDbPath));
				channels = new Gson().fromJson(reader, Channels.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return channels;
	}
}
