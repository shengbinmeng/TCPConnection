import java.io.*;
import java.net.*;

public class SecureTcpServer extends Thread {
	private ServerSocket serverSocket;

	public SecureTcpServer(int port) throws IOException {
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
				RotorMachine machine = null;
				
				// Keep serving this client until exception occurs or "Bye" message is received
				while (true) {
					try {						
						String message = in.readUTF();
						System.out.println("Received \"" + message + "\"");

						if (machine != null) {
							// We are communicating in secure
							message = machine.decrypt(message);
							System.out.println("Decrypted \"" + message + "\"");
						}
						
						if (message.equalsIgnoreCase("!Bye")) {
							// The client asks for terminating
							break;
						}
						
						if (message.equalsIgnoreCase("!SecureOff") && machine != null) {
							// The client wants to turn secure off
							String reply = machine.encrypt("!SecureOff");
							out.writeUTF(reply);
							machine = null;
							System.out.println("Secure off.");
							continue;
						}
						
						if (message.equalsIgnoreCase("!Secure") && machine == null) {
							// The client want to be secure
							String reply = "!Secure";
							out.writeUTF(reply);
							
							// The process of the Diffie-Hellman key exchange
							int DH_p = SharedInformation.DH_p;
							int DH_g = SharedInformation.DH_g;
							message = in.readUTF();
							int DH_A = Integer.parseInt(message);
							int DH_b = 15; // The number we secretly choose
							int DH_B = (int) (((long)Math.pow(DH_g, DH_b)) % DH_p);
							reply = "" + DH_B;
							out.writeUTF(reply);
							int DH_s = (int) (((long)Math.pow(DH_A, DH_b)) % DH_p);
							
							// Use the secret key to determine a scheme and construct a machine
							int secretKey = DH_s;
							int index = secretKey % SharedInformation.schemePermutations.length;
							int[][] schemePermutation = SharedInformation.schemePermutations[index];
							Scheme scheme = new Scheme(schemePermutation);
							machine = new RotorMachine(scheme);
							System.out.println("Secured. Following messages will be encrypted.");
							continue;
						}
						
						// The service is simply echoing back the received message
						String reply = "ECHO " + message;
						if (machine != null) {
							// We are communicating in secure
							reply = machine.encrypt(reply);
						}
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
			Thread t = new SecureTcpServer(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
