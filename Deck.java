package holdem;

import java.util.Random;

public class Deck {
	private Card[] gCards = new Card[53];
	private int[] gIxs = new int[53];
	private int gNextCard = 0;
	Deck() {
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				Card nc = new Card(i, j, -1);
				gCards[4 * i + j] = nc;
				gIxs[4 * i + j] = 4 * i + j;
			}
		}
	}
	
	void shuffle(Random rnd) {
		for (int i = 0; i < 400; i++) {
			int x = (int) rnd.nextInt(52);
			int y = (int) rnd.nextInt(52);
			int z = gIxs[x];
			gIxs[x] = gIxs[y];
			gIxs[y] = z;
		} // endfor i

	}
	
	Card pull() {
		Card c = gCards[gIxs[gNextCard]];
		gNextCard = (gNextCard < 51) ? (gNextCard + 1) : 0;
		return c;
	}
	
}
