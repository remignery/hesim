package holdem;

public class Card {
	

	public int gRank;
	public char gFace;
	public char gSuit;
	public int gLoc;
	
	Card(int rank, int suit, int loc) {
		gFace = "23456789TJQKA".charAt(rank);
		gRank = rank;
		gSuit = "cdhs".charAt(suit);
		gLoc = loc;

	}
	char get_face() {
		return gFace;
	}
	char get_suit() {
		return gSuit;
	}
	int get_rank() {
		return gRank;
	}
	void set_face(char face) {
		gFace = face;
	}
	void set_suit(char suit) {
		gSuit = suit;
	}
	void set_rank(int rank) {
		gRank = rank;
	}
	

}
