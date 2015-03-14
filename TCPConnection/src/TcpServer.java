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
		
		// Keep running the server until an exception occurs
		while (true) {
			try {
				// Wait for a client to connect
				Socket server = serverSocket.accept();
				System.out.println("Connected to "
						+ server.getRemoteSocketAddress());
				
				// Get input and output stream from the TCP socket
				DataInputStream in = new DataInputStream(
						server.getInputStream());
				DataOutputStream out = new DataOutputStream(
						server.getOutputStream());
				
				// Keep serving this client until exception occurs or "Bye" message is received
				while (true) {
					try {
						String message = in.readUTF();
						System.out.println("Received \"" + message + "\"");
						if (message.equalsIgnoreCase("Bye")) {
							break;
						}
						
						// The service is simply echoing back the received message
						String reply = "ECHO " + message;
						out.writeUTF(reply);
					} catch (Exception e) {
						break;
					}
				}
				System.out.println("Done serving this client");
			} catch (SocketTimeoutException e) {
				// If accept times out, continue to wait
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
		
		// Use default port 9999 unless a port is provided
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
