import java.util.ArrayList;

public class RotorMachine {
	private ArrayList<Rotor> rotors;
	
	RotorMachine(Scheme s) {
		int numberOfKeys = s.permutations[0].length;
		int rotorNumber = s.rotorNumber;
		
		rotors = new ArrayList<Rotor>();
		for (int i = 0; i < rotorNumber; i++) {
			int strokesPerRotation = (int) Math.pow(numberOfKeys, rotorNumber - (i+1));
			int[] permutation = s.permutations[i];
			rotors.add(new Rotor(permutation, strokesPerRotation));
		}
	}
	
	private void reset() {
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(i);
			r.reset();
		}
	}
	
	private char encipher(char symbol) {
		char code = symbol;
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(i);
			code = r.transform(code);
		}
		return code;
	}
	
	private char decipher(char code) {
		char symbol = code;
		for (int i = 0; i < rotors.size(); i++) {
			Rotor r = rotors.get(rotors.size()-1 - i);
			symbol = r.transformInverse(symbol);
		}
		
		return symbol;
	}
	
	public String encrypt(String message) {
		message = message.toUpperCase();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			if (SharedInformation.keys.indexOf(c) == -1) {
				return "";
			}
			c = encipher(c);
			builder.append(c);
		}
		String result = builder.toString();
		reset();
		return result;
	}
	
	public String decrypt(String message) {
		message = message.toUpperCase();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			if (SharedInformation.keys.indexOf(c) == -1) {
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
		index = index + rotation;
		if (index > permutation.length - 1) {
			index = index - permutation.length;
		}
		int newIndex = permutation[index];
		char code = SharedInformation.keys.charAt(newIndex);
		
		strokes++;
		if (strokes == strokesPerRotation) {
			rotate();
			strokes = 0;
		}
		return code;
	}
	
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
	
	public void reset() {
		rotation = 0;
		strokes = 0;
	}
}
