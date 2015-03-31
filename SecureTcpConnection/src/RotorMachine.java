import java.util.ArrayList;

public class RotorMachine {
	private int numberOfKeys;
	private int numberOfRotors;
	private ArrayList<Rotor> rotors;
	
	RotorMachine(int m, Scheme s) {
		numberOfKeys = m;
		numberOfRotors = s.rotorNumber;
		
		rotors = new ArrayList<Rotor>();
		for (int i = 0; i < numberOfRotors; i++) {
			int strokesPerRotation = (int) Math.pow(numberOfKeys, numberOfRotors - (i+1));
			int[] permutation = s.permutations[i];
			rotors.add(new Rotor(numberOfKeys, permutation, strokesPerRotation));
		}
	}
	
	
	public int encipher(int input) {
		int result = input;
		for (int i = 0; i < numberOfRotors; i++) {
			Rotor r = rotors.get(i);
			result = r.transform(result);
		}
		
		return result;
	}
	
	public String encrypt(String message) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			c = (char) encipher((int)c);
			builder.append(c);
		}
		return builder.toString();
	}
	
}

class Scheme {
	public int rotorNumber;
	public int[][] permutations;
	
	Scheme(int r, int[][] p) {
		rotorNumber = r;
		permutations = p;
	}
}

class Rotor {
	private int numberOfPins;
	private int[] permutation;
	private int rotation;
	private int strokes;
	private int strokesPerRotation;
	
	Rotor(int m, int[] p, int s) {
		numberOfPins = m;
		permutation = p;
		strokesPerRotation = s;
		rotation = 0;
		strokes = 0;
	}
	
	private void rotate() {
		rotation++;
		if (rotation > numberOfPins - 1) {
			rotation = 0;
		}
	}
	
	public int transform(int input) {
		int index = input + rotation;
		if (index > numberOfPins - 1) {
			index = index - numberOfPins;
		}
		int output = permutation[index];
		
		strokes++;
		if (strokes == strokesPerRotation) {
			rotate();
			strokes = 0;
		}
		return output;
	}
}
