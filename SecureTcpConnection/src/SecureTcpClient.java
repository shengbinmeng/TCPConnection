import java.net.*;
import java.io.*;

public class SecureTcpClient extends Thread {	
	private String mServerName;
	private int mServerPort;

	public SecureTcpClient(String serverName, int port) throws IOException {
		mServerName = serverName;
		mServerPort = port;
	}
	
	private int generateKey() {
		
		return 0;
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
			
			int key = generateKey();
			int index = key % SharedInformation.schemeNumber;
			Scheme scheme = SharedInformation.schemes.get(index);
			RotorMachine machine = new RotorMachine(SharedInformation.keyNumber, scheme);
			
			while (true) {
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				
				String enctyptedMessage = machine.encrypt(message);
				
				out.writeUTF(enctyptedMessage);
				
				// End the communication when the user inputs "Bye" (case-insensitive)
				if (message.equalsIgnoreCase("Bye")) {
					break;
				}
				
				// Get reply from the server and print it
				String reply = in.readUTF();
			    System.out.println("Server replyed with: " + reply);
			}
			
			clientSocket.close();
			System.out.println("Connection closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		// Argument <count> means the number of lines the user will send at most
		String help = "Usage: TcpClient <host> <port>";
		if (args.length != 2) {
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

			Thread t = new SecureTcpClient(serverName, port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("<port> must be integer!");
			System.out.println(help);
		}
	}
}
