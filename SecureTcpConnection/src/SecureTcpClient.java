import java.net.*;
import java.io.*;

public class SecureTcpClient extends Thread {	
	private String mServerName;
	private int mServerPort;

	public SecureTcpClient(String serverName, int port) throws IOException {
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
			
			// The process of the Diffie-Hellman key exchange
			int DH_p = SharedInformation.DH_p;
			int DH_g = SharedInformation.DH_g;
			int DH_a = 6; // The number we secretly choose
			int DH_A = (int) (((long)Math.pow(DH_g, DH_a)) % DH_p); // 8
			String sent = "" + DH_A;
			out.writeUTF(sent);
			String received = in.readUTF();
			int DH_B = Integer.parseInt(received);
			int DH_s = (int) (((long)Math.pow(DH_B, DH_a)) % DH_p);
			
			// Use the secret key to determine a scheme and construct a machine
			int secretKey = DH_s;
			int index = secretKey % SharedInformation.schemePermutations.length;
			int[][] schemePermutation = SharedInformation.schemePermutations[index];
			Scheme scheme = new Scheme(schemePermutation);
			RotorMachine machine = new RotorMachine(scheme);
			System.out.println("Secured. Following messages will be encrypted.");
			
			while (true) {
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				// Ignore the empty input
				if (message.equals("")) {
					continue;
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
				reply = machine.decrypt(reply);
				System.out.println("Server reply decrypted: " + reply);
			}
			
			clientSocket.close();
			System.out.println("Connection closed");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error. The server may be down.");
		}
	}
	
	public static void main(String[] args) {
		
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
