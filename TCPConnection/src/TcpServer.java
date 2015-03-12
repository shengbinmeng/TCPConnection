import java.io.*;
import java.net.*;

public class TcpServer extends Thread {
	private ServerSocket serverSocket;

	public TcpServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}

	public void run() {
		System.out.println("SERVER: Waiting for client on port "
				+ serverSocket.getLocalPort() + "...");
		Socket server = null;
		while (true) {
			try {
				server = serverSocket.accept();
				System.out.println("SERVER: Connected to "
						+ server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(
						server.getInputStream());
				String message = in.readUTF();
				System.out.println("SERVER: Received \"" + message + "\"");
				String sub = message.substring(message.indexOf('#')+1, message.lastIndexOf('#'));
				long count = Long.parseLong(sub);
				DataOutputStream out = new DataOutputStream(
						server.getOutputStream());
				for (long i = 0; i < count; i++) {
				    String reply = "Data Line " + i;
				    out.writeUTF(reply);
				}
			} catch (SocketTimeoutException e) {
				//e.printStackTrace();
				//break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		int port = 8989;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		try {
			Thread t = new TcpServer(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
