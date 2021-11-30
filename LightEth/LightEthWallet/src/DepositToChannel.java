
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
 * Servlet implementation class DepositToChannel
 */
@WebServlet("/DepositToChannel")
public class DepositToChannel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet user;
	private Channels channels;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DepositToChannel() {
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
		try {
			HttpSession session = request.getSession();
			this.user = (Wallet) session.getAttribute("user");
			this.channels = (Channels) session.getAttribute("channelsDb");
			int tableIndex = (int) session.getAttribute("tableIndex");

			System.out.println("tableIndex = " + tableIndex);

			String amountStr = request.getParameter("amount");
			float amount = Float.parseFloat(amountStr);

			System.out.println("amount = " + amountStr);
			String dest_address = this.channels.channels.get(tableIndex).destination_address;
			// a check to soo if balance have the money
			float thisWalletBalance = 0;
			// get this wallet balance
			thisWalletBalance = Float.valueOf(getBalance());
//			if (amount > thisWalletBalance) // if wallet balance is low msg and do nothing
//			{
//				float thisWalletChannlesBalance = this.channels.getChannelsSum();
//				if (thisWalletBalance < (thisWalletChannlesBalance - amount)) // if the (on chain balance) -(channel
//																				// balance)<amount to deposit=>do
//																				// nothing
//				{
//					RequestDispatcher rd = request.getRequestDispatcher("deposit_to_channel.html");
//					PrintWriter out = response.getWriter();
//					out.println("<font color=red>Insufficient account Balance for deposit.</font>");
//					rd.include(request, response);
//				} else {
//					RequestDispatcher rd = request.getRequestDispatcher("deposit_to_channel.html");
//					PrintWriter out = response.getWriter();
//					out.println("<font color=red>Insufficient Wallet Balance for deposit.</font>");
//					rd.include(request, response);
//				}
			this.channels = getChannelsDb(this.user.name);
			float negativeChannelsBalance;
			if (this.channels != null) {
				negativeChannelsBalance = this.channels.getNegativeBalance();
			} else {
				negativeChannelsBalance = 0;
			}
			if (amount <= 0) {
				// cant send nagtive or 0 anount
				RequestDispatcher rd = request.getRequestDispatcher("deposit_to_channel.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>You cant send a nagtive or 0 amount!.</font>");
				rd.include(request, response);
			} else {
				if (thisWalletBalance + negativeChannelsBalance < amount) {
					RequestDispatcher rd = request.getRequestDispatcher("deposit_to_channel.html");
					PrintWriter out = response.getWriter();
					out.println(
							"<font color=red>You cant Deposit when your negative channels balance is bigger then your on-chain balance.</font>");
					rd.include(request, response);
				} else {
					if (getUserWallet(dest_address) != null) {
						if (addToChannelDb(this.user.name, dest_address, +amount)) {
							if (addToChannelDb(dest_address, this.user.name, -amount)) {
								request.getRequestDispatcher("ShowChannels").forward(request, response);
							}
						} else {
							RequestDispatcher rd = request.getRequestDispatcher("deposit_to_channel.html");
							PrintWriter out = response.getWriter();
							out.println("<font color=green>Deposit To Channel Error!.</font>");
							rd.include(request, response);
						}
					}
//			Channels userChannels = this.channels;

//          servletContext.setAttribute("channels", userChannels);
//			RequestDispatcher rd = request.getRequestDispatcher("send_eth.html");
//			PrintWriter out = response.getWriter();
//			out.println("<font color=green>Depost To Channel Successfully!.</font>");
//			rd.include(request, response);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Exception");
		}
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

	private boolean addToChannelDb(String userName, String dest_address, float amount) {
//		String dbPath = "C:/Users/Adir/Desktop/Braude/DB";
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
					// TO DO : check balance and amount!!!
					this.channels.channels.get(channelIndex).balance += (amount);
					String fileData = new Gson().toJson(channels, Channels.class);
					createChannelsDb(path, fileData);
					return true;
				} else {
					System.out.println("Error!, channel not exist");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			System.out.println("Error!, channels file not exist");
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
}
