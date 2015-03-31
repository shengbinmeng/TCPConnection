import java.util.ArrayList;


public class SharedInformation {
	static final int schemeNumber = 5;
	static final int keyNumber = 30;

	static ArrayList<Scheme> schemes;
	
	static {
		schemes = new ArrayList<Scheme>();
		for (int i = 0; i < schemeNumber; i++) {
			int[][] p = {
			{1, 2, 3},
			{4, 5, 6}
			};
			Scheme s = new Scheme(i, p);
			schemes.add(s);
		}
	}
}
