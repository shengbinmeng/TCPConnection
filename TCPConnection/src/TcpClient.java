import java.net.*;
import java.io.*;

public class TcpClient extends Thread {
	private String mServerName;
	private int mServerPort;

	public TcpClient(String serverName, int port) throws IOException {
		mServerName = serverName;
		mServerPort = port;
	}

	public void run() {
		try {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket(mServerName, mServerPort);
			System.out.println("CLIENT: Just connected to "
					+ clientSocket.getRemoteSocketAddress());
			while (true) {
				if (clientSocket.isClosed()) {
					clientSocket = new Socket(mServerName, mServerPort);
				}
				OutputStream outToServer = clientSocket.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				out.writeUTF(message);
				InputStream inFromServer = clientSocket.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				String reply = in.readUTF();
				System.out.println("CLIENT: Server replyes with \"" + reply + "\".");
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String serverName = "localhost";
		if (args.length > 0) {
			serverName = args[0];
		}
		int port = 8989;
		if (args.length > 1) {
			port = Integer.parseInt(args[1]);
		}
		try {
			Thread t = new TcpClient(serverName, port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}