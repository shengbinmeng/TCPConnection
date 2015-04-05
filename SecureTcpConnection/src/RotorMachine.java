import java.util.ArrayList;

public class RotorMachine {
	private ArrayList<Rotor> rotors;
	
	RotorMachine(Scheme s) {
		int numberOfKeys = s.permutations[0].length;
		int rotorNumber = s.rotorNumber;
		
		rotors = new ArrayList<Rotor>();
		for (int i = 0; i < rotorNumber; i++) {
			// The key strokes needed for one rotation (Rotors rotate at different speeds)
			int strokesPerRotation = (int) Math.pow(numberOfKeys, rotorNumber - (i+1));
			int[] permutation = s.permutations[i];
			rotors.add(new Rotor(permutation, strokesPerRotation));
		}
	}
	
	// Reset the machine to initial state
	private void reset() {
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(i);
			r.reset();
		}
	}
	
	// Encipher a character
	private char encipher(char symbol) {
		char code = symbol;
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(i);
			code = r.transform(code);
		}
		return code;
	}
	
	// Decipher a character
	private char decipher(char code) {
		char symbol = code;
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(rotors.size()-1 - i);
			symbol = r.transformInverse(symbol);
		}
		
		return symbol;
	}
	
	// Encrypt a message
	public String encrypt(String message) {
		message = message.toUpperCase();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			if (SharedInformation.keys.indexOf(c) == -1) {
				// We have a character that is not supported
				return "";
			}
			c = encipher(c);
			builder.append(c);
		}
		String result = builder.toString();
		reset();
		return result;
	}
	
	// Decrypt a message
	public String decrypt(String message) {
		message = message.toUpperCase();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			if (SharedInformation.keys.indexOf(c) == -1) {
				// We have a character that is not supported
				return "";
			}
			c = decipher(c);
			builder.append(c);
		}
		String result = builder.toString();
		reset();
		return result;
	}
	
}

// A scheme for a rotor machine. Nothing more than permutations for each rotor.
class Scheme {
	public int[][] permutations;
	public int rotorNumber;
	
	Scheme(int[][] p) {
		permutations = p;
		rotorNumber = p.length;
	}
}

class Rotor {
	private int[] permutation;
	private int rotation;
	private int strokes;
	private int strokesPerRotation;
	private int[] permutationInverse;
	
	Rotor(int[] p, int s) {
		permutation = p;
		permutationInverse = new int[p.length];
		// Inverse permutation used for decipher
		for (int i = 0; i < p.length; i++) {
			permutationInverse[p[i]] = i;
		}
		strokesPerRotation = s;
		rotation = 0;
		strokes = 0;
	}
	
	private void rotate() {
		rotation++;
		if (rotation > permutation.length - 1) {
			rotation = 0;
		}
	}
	
	public char transform(char symbol) {
		int index = SharedInformation.keys.indexOf(symbol);
		
		// If the rotor has been rotated, the permutation needs to be changed accordingly
		index = index + rotation;
		if (index > permutation.length - 1) {
			index = index - permutation.length;
		}
		int newIndex = permutation[index];
		char code = SharedInformation.keys.charAt(newIndex);
		
		// Each transformation increases the stroke count
		strokes++;
		// And the rotor rotates at specified speed
		if (strokes == strokesPerRotation) {
			rotate();
			strokes = 0;
		}
		return code;
	}
	
	// The inverse of transform (see above for the transform procedure)
	public char transformInverse(char code) {
		int newIndex = SharedInformation.keys.indexOf(code);
		int index = permutationInverse[newIndex];
		index = index - rotation;
		if (index < 0) {
			index = index + permutation.length;
		}
		char symbol = SharedInformation.keys.charAt(index);
		
		strokes++;
		if (strokes == strokesPerRotation) {
			rotate();
			strokes = 0;
		}
		return symbol;
	}
	
	
	// Reset the rotor's rotation and stroke state to initial
	public void reset() {
		rotation = 0;
		strokes = 0;
	}
}
