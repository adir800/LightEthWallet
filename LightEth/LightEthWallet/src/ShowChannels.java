
import java.io.File;
import java.io.IOException;
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

import entity.Channels;
import entity.Wallet;

/**
 * Servlet implementation class ShowChannels
 */
@WebServlet("/ShowChannels")
public class ShowChannels extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Channels channels;
	private Wallet user;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShowChannels() {
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
		HttpSession session = request.getSession();
		this.user = (Wallet) session.getAttribute("user");
		if (getChannelsDb()) {
			session.setAttribute("channelsDb", this.channels);
			int count = this.channels.channels.size();
			System.out.println("channels count = " + count);
			request.setAttribute("count", count);
			request.setAttribute("channels", this.channels.channels);
		} else {
			request.setAttribute("count", 0);
		}
		request.getRequestDispatcher("show_channels2.jsp").forward(request, response);
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
