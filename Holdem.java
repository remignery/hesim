package holdem;

import java.io.IOException;
import java.util.Random;



public class Holdem {
	
	static String gInitFile = "initHoldem.txt";
	static int gRepCount;

	public static void main(String[] args) throws IOException {
		gRepCount = (args.length > 0) ? Integer.parseInt(args[0]) : 2000;
		gInitFile = (args.length > 1) ? args[1] : "initHoldem.txt";
		//emulate_holdem();
		Game theGame = new Game(0);

	}
}
