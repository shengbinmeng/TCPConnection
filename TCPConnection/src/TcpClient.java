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
			// Create a TCP socket to server
			Socket clientSocket = new Socket(mServerName, mServerPort);
			System.out.println("Connected to "
					+ clientSocket.getRemoteSocketAddress());
			
			// Get input stream from user and output stream to server
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			OutputStream outToServer = clientSocket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = clientSocket.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			
			long count = 1;
			// If the <count> argument is given by user, use it as the line number limit
			if (mCount > 0) {
				count = mCount;
			}
			
			// Allow the user to input and send <count> lines of messages at most
			while (count > 0) {
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				out.writeUTF(message);
				
				// End the communication when the user inputs "Bye" (case-insensitive)
				if (message.equalsIgnoreCase("Bye")) {
					break;
				}
				
				// Get reply from the server and print it
				String reply = in.readUTF();
			    System.out.println("Server replyed with: " + reply);
			    
			    // Only decrease count when the argument <count> is given
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
		
		// Argument <count> means the number of lines the user will send at most
		String help = "Usage: TcpClient <host> <port> [count]";
		if (args.length < 2 || args.length > 3) {
			System.out.println("Number of arguments is invaild!");
			System.out.println(help);
			return;
		}
		String serverName  = args[0];
		try {
			int port = Integer.parseInt(args[1]);
			if (port < 0) {
				System.out.println("<port> must be positive!");
				System.out.println(help);
				return;
			}
			if (args.length == 3) {
				mCount = Long.parseLong(args[2]);
			} else {
				// If <count> argument is not given, we use -1 to indicate unlimited lines of message
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
