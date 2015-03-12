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
			System.out.println("CLIENT: Just connected to "
					+ clientSocket.getRemoteSocketAddress());
			OutputStream outToServer = clientSocket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			String message = "Give me #" + mCount + "# lines of data!";
			out.writeUTF(message);
			InputStream inFromServer = clientSocket.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println("CLIENT: Server replyes with:");
			for (long i = 0; i < mCount; i++) {
				String reply = in.readUTF();
			    System.out.println(reply);
			}
			clientSocket.close();
			System.out.println("CLIENT: connection closed");
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
				mCount = 10;
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
