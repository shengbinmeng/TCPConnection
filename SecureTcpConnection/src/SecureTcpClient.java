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
			RotorMachine machine = null;
			while (true) {
				String message = inFromUser.readLine();
				if (message == null) {
					break;
				}
				if (message.equals("")) {
					continue;
				}
				
				if (message.equalsIgnoreCase("P-Secure") && machine == null) {
					// Generate the key
					out.writeUTF(message);
					String reply = in.readUTF();
					if (reply.equalsIgnoreCase("P-Secure") == false) {
						System.out.println("Server replyed with: " + reply);
						System.out.println("Something wrong!");
						break;
					}
					int DH_p = SharedInformation.DH_p;
					int DH_g = SharedInformation.DH_g;
					int DH_a = 6;
					int DH_A = (int) (((long)Math.pow(DH_g, DH_a)) % DH_p); // 8
					message = "" + DH_A;
					out.writeUTF(message);
					reply = in.readUTF();
					int DH_B = Integer.parseInt(reply);
					int DH_s = (int) (((long)Math.pow(DH_B, DH_a)) % DH_p);
					System.out.println("Secret Key: " + DH_s);
					int secretKey = DH_s;
					int index = secretKey % SharedInformation.schemePermutations.length;
					int[][] schemePermutation = SharedInformation.schemePermutations[index];
					Scheme scheme = new Scheme(schemePermutation);
					machine = new RotorMachine(scheme);
					System.out.println("Secured. Following messages will be encrypted.");
					continue;
				}
				if (message.equalsIgnoreCase("P-Secure-Off") && machine != null) {
					message = machine.encrypt(message);
					out.writeUTF(message);
					String reply = in.readUTF();
					reply = machine.decrypt(reply);
					if (reply.equalsIgnoreCase("P-Secure-Off") == false) {
						System.out.println("Server replyed with: " + reply);
						System.out.println("Something wrong!");
						break;
					}
					machine = null;
					System.out.println("Secure off.");
					continue;
				}
				
				if (machine != null) {
					String enctyptedMessage = machine.encrypt(message);
					out.writeUTF(enctyptedMessage);
				} else {
					out.writeUTF(message);
				}
				
				// End the communication when the user inputs "Bye" (case-insensitive)
				if (message.equalsIgnoreCase("P-Bye")) {
					break;
				}
				
				// Get reply from the server and print it
				String reply = in.readUTF();
			    System.out.println("Server replyed with: " + reply);
				if (machine != null) {
					reply = machine.decrypt(reply);
					System.out.println("Server reply decrypted: " + reply);
				}
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
