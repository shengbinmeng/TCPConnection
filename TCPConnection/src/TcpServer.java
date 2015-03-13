import java.io.*;
import java.net.*;

public class TcpServer extends Thread {
	private ServerSocket serverSocket;

	public TcpServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}

	public void run() {
		System.out.println("Server started, listening on port "
				+ serverSocket.getLocalPort() + "...");
		while (true) {
			try {
				Socket server = serverSocket.accept();
				System.out.println("Connected to "
						+ server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(
						server.getInputStream());
				DataOutputStream out = new DataOutputStream(
						server.getOutputStream());
				while (true) {
					try {
						String message = in.readUTF();
						System.out.println("Received \"" + message + "\"");
						if (message.equalsIgnoreCase("Bye")) {
							break;
						}
						String reply = "ECHO " + message;
						out.writeUTF(reply);
					} catch (Exception e) {
						break;
					}
				}
				System.out.println("Done serving this client");
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		String help = "Usage: TcpServer [port]";
		if (args.length > 1) {
			System.out.println(help);
			return;
		}
		int port = 9999;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			}  catch (NumberFormatException e) {
				System.out.println("[port] is invalid!");
				System.out.println(help);
			}
		}
		try {
			Thread t = new TcpServer(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
