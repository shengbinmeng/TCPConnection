import java.net.*;
import java.io.*;

public class TcpClient extends Thread {
	private String mServerName;
	private int mServerPort;
	static private long mCount;

	public TcpClient(String serverName, int port) throws IOException {
		mServerName = serverName;
		mServerPort = port;
	}

	public void run() {
		try {
			Socket clientSocket = new Socket(mServerName, mServerPort);
			System.out.println("Connected to "
					+ clientSocket.getRemoteSocketAddress());
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			OutputStream outToServer = clientSocket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = clientSocket.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			long count = 1;
			if (mCount > 0) {
				count = mCount;
			}
			while (count > 0) {
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				out.writeUTF(message);
				if (message.equalsIgnoreCase("Bye")) {
					break;
				}
				String reply = in.readUTF();
			    System.out.println("Server replyed with: " + reply);
			    if (mCount > 0) {
				    count--;
			    }
			}
			
			clientSocket.close();
			System.out.println("Connection closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String help = "Usage: TcpClient <host> <port> [count]";
		if (args.length < 2 || args.length > 3) {
			System.out.println("Number of arguments is invaild!");
			System.out.println(help);
			return;
		}
		String serverName  = args[0];
		try {
			int port = Integer.parseInt(args[1]);
			if (args.length == 3) {
				mCount = Long.parseLong(args[2]);
			} else {
				// Default
				mCount = -1;
			}
			Thread t = new TcpClient(serverName, port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("<port> or [count] must be integer!");
			System.out.println(help);
		}
	}
}
