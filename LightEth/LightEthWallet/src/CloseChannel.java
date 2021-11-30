
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

import entity.Channels;
import entity.Wallet;

/**
 * Servlet implementation class CloseChannel
 */
@WebServlet("/CloseChannel")
public class CloseChannel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Wallet user;
	private Channels channels;
	private Wallet recipientWallet;
	private float amount;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CloseChannel() {
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
		
    	HttpSession session = request.getSession();
    	this.user = (Wallet) session.getAttribute("user");
    	this.channels = (Channels) session.getAttribute("channelsDb");
    	int tableIndex = (int) session.getAttribute("tableIndex");
		
		System.out.println("tableIndex = " + tableIndex);

		String dest_address = this.channels.channels.get(tableIndex).destination_address;
		this.recipientWallet = getUserWallet(dest_address);
		this.amount = this.channels.channels.get(tableIndex).balance;
		if (this.recipientWallet != null) {
			if (addToChannelDb(this.user.name, dest_address)) {
				if (addToChannelDb(dest_address, this.user.name)) {
					if (this.amount < 0) {
						sendOnChainEth(this.user, this.recipientWallet, -amount);
					}
					else {
						sendOnChainEth(this.recipientWallet, this.user, amount);
					}
						request.getRequestDispatcher("ShowChannels").forward(request, response);
				} else {
					System.out.println("Close Channel Error!!");
					request.getRequestDispatcher("ShowChannels").forward(request, response);
				}
			} else {
				System.out.println("Close Channel Error!!");
				request.getRequestDispatcher("ShowChannels").forward(request, response);
			}
		}
	}

	private void sendOnChainEth(Wallet userWallet, Wallet recipientWallet, float amount) {
		String urlStr = "https://rahakott.io/api/v1.1/send";
		String walletStr = userWallet.oid; // (string, required) - unique wallet identifier
		String recipientStr = recipientWallet.current_address; // (string, required) - recipient's address

		// {"api_key":"1234567890abcdef1234567890abcdef","wallet":"abadf547c544f067661604db8ab522ee","recipient":"mn18nUWGL8sHsxKXCbBJ8G7nAbmA8XB7Vb","amount":1000000,"external_only":true,"subtract_fees":true}
		String postData = "{\"api_key\":\"ffab97a787c5c9fffdd4c483944a6048\",\"wallet\":\"" + walletStr
				+ "\",\"recipient\":\"" + recipientStr + "\",\"amount\":" + amount + "}";
		System.out.println(postData);
		try {
			JsonObject resultJson = HttpConnectionUtil.sendHttpPostReq(urlStr, postData);
			System.out.println("resultJson: " + resultJson.toString());
			// String onChainConfirmedBalance = resultJson.get("confirmed").getAsString();
			// System.out.println(" OnChain Balance: " + onChainConfirmedBalance);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean addToChannelDb(String userName, String dest_address) {
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
					this.channels.channels.remove(channelIndex);
					String fileData = new Gson().toJson(channels, Channels.class);
					// if channel balance is negative send money else receive payment
//					if (this.channels.channels.get(channelIndex).balance < 0) {
//						payOnChain(channelIndex);
//					} else {
//						recevePaymentOnChain(channelIndex);
//					}

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

	// TODO fix method duplicates in SendEth, Depost To Channel , Close Channel
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


}
