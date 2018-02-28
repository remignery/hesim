package holdem;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Hand {
public boolean gTest = true;
	// Constants
	public final int kPREFLOP = 0;
	public final int kFLOP = 1;
	public final int kTURN = 2;
	public final int kRIVER = 3;
	public final int kSHOWDOWN = 4;
	public final int kHOLE = 5;
	public final int kFOLD = 6;
	public final int kCHECK = 7;
	public final int kCALL = 8;
	public final int kRAISE = 9;
	
	// Hand parameters
	public double gRake = 0.0;
	public String gHandLog = "";
	public boolean[] gInHand = new boolean[10];
	public boolean[][] gInPot = new boolean[10][9];
	public double[] gPots = new double[9];
	public double[] gAllInShort = new double[10];
	public int gPotIdx = 0;
	public double gPotTotal;
	public int gCountInHand = 0;
	public boolean gNoFlop = false;
	public double gPotInit = 0.0;
	public Card[] gBoard = new Card[7];
	public int gBoardCount = 0;
	public int gCountInPot = 0;
	public Game G;
	public Card[][] gObCards = new Card[10][2];
	public double[][] gObPosts = new double[10][20];
	public int[] gObPostsIdx = new int[10];
	public int gStreet;
	
	// Hand stats
	public int flop_val = 0;
	public int flop_one_outs = 0;
	public int flop_two_outs = 0;
	public int turn_val = 0;
	public int turn_one_outs = 0;
	public int turn_two_outs = 0;
	public int river_val = 0;
	public int river_one_outs = 0;
	public int river_two_outs = 0;
	
	public final int gValQuad0 =  				30000;		// quads with 0 holes depends on kickers
	public final int gValQuad12 = 				30001;		// quads with any holes nuts
	
	public final int gValHiBoat00 = 			40000;		// house 3 over 2 with 0 holes has <13 oneouts and <12 twoouts
	public final int gValHiBoat01 = 			40001;		// house 3 over 2 with 1 hole in pair 1 oneout
	public final int gValHiBoat10 = 			40002;		// house 3 over 2 with 1 hole in trip has 1 oneout
	public final int gValHiBoat11 = 			40003;		// house 3 over 2 with 1 hole in trip and 1 in pair <5 oneout and <12 twoouts
	public final int gValHiBoat02 = 			40004;		// house 3 over 3 with 2 holes in pair
	public final int gValHiBoat20 = 			40005;		// house 2 over 3 with 2 holes in trip
	
	public final int gValLoBoat00 = 			40006;		// house 2 over 3 with 0 holes has <4 oneouts and <12 twoouts
	public final int gValLoBoat01 = 			40007;		// house 2 over 3 with 1 hole in pair
	public final int gValLoBoat10 = 			40008;		// house 2 over 3 with 1 hole in trip
	public final int gValLoBoat11 = 			40009;		// house 2 over 3 with 1 hole in trip and 1 in pair
	public final int gValLoBoat02 = 			40010;		// house 2 over 3 with 2 holes in pair
	public final int gValLoBoat20 = 			40011;		// house 2 over 3 with 2 holes in trip
	
	public final int gVal3Flush0 = 2000;			// 3 flush draw with 0 holes
	
	public final int gVal4Flush0 = 4000;			// 4 flush draw with 0 holes
	public final int gVal4Flush1 = 4001;				// 4 flush draw with 1 hole
	public final int gVal4Flush2 = 4002;				// 4 flush draw with 2 holes
	public final int gVal4FlushOut = -500;			// cost of 5 flush outs
	
	public final int gVal5Flush0 = 20000;			// flush with no holes
	public final int gVal5Flush1 = 20000;			// flush with 1 hole
	public final int gVal5Flush2 = 20000;			// flush with 2 holes
	public final int gVal5FlushOut = -500;			// cost of 5 flush outs
	
	public final int gVal3Straight0 = 1000;			// 3 straight draw with 0 holes
	
	public final int gVal4straight0 = -1000;			// 4 straight draw with 0 holes
	public final int gVal4hiStraight1 = 500;			// 4 straight draw with 1 hole
	public final int gVal4inStraight1 = 1000;		// 4 straight draw with 0 holes
	public final int gVal4loStraight1 = 500;			// 4 straight draw with 1 hole
	public final int gVal4hiStraight2 = 500;			// 4 straight draw with 1 hole
	public final int gVal4inStraight2 = 1000;		// 4 straight draw with 0 holes
	public final int gVal4loStraight2 = 500;			// 4 straight draw with 1 hole
	
	public final int gVal5straight0 = 	10000;			// 4 straight draw with 0 holes
	public final int gVal5hiStraight1 = 10001;			// 4 straight draw with 1 hole
	public final int gVal5inStraight1 = 10002;		// 4 straight draw with 1 inside hole
	public final int gVal5loStraight1 = 10003;			// 4 straight draw with 1 hole
	public final int gVal5hiStraight2 = 10004;			// 4 straight draw with 1 hole
	public final int gVal5inStraight2 = 10005;			// 4 straight draw with 0 holes
	public final int gVal5loStraight2 = 10006;			// 4 straight draw with 1 hole
	
	public final int gVal4straightFlush0 = 500;			// 4 straight flush draw with 0 holes
	public final int gVal4hiStraightFlush1 = 500;			// 4 straight flush draw with 1 top hole
	public final int gVal4inStraightFlush1 = 1000;			// 4 straight flush draw with 0 holes
	public final int gVal4loStraightFlush1 = 500;			// 4 straight flush draw with 1 hole
	public final int gVal4hiStraightFlush2 = 500;			// 4 straight flush draw with 1 hole
	public final int gVal4inStraightFlush2 = 1000;			// 4 straight flush draw with 0 holes
	public final int gVal4loStraightFlush2 = 500;			// 4 straight flush draw with 1 hole
	
	public final int gVal5straightFlush0 = 		40001;		// 5 straight flush with 0 holes
	public final int gVal5hiStraightFlush1 = 	40002;		// 5 straight flush with 1 top hole nuts
	public final int gVal5inStraightFlush1 = 	40003;		// 5 straight flush with 1 inside nuts
	public final int gVal5loStraightFlush1 = 	40004;		// 5 straight flush with 1 lo hole
	public final int gVal5hiStraightFlush2 = 	40005;		// 5 straight flush with 2 top holes nuts
	public final int gVal5inStraightFlush2 = 	40006;		// 5 straight flush with 2 inside holes nuts
	public final int gVal5loStraightFlush2 = 	40007;		// 5 straight flush with 2 lo holes 1 two out
	
	public final int gValTrip0 = 500;				// trips with 0 hole cards
	public final int gValTrip1 = 500;			// trips with 1 hole card
	public final int gValTrip2 = 500;			// trips with 2 hole cards
	
	public final int gValPair0 = 200;				// pairs with 0 hole card
	public final int gValPair1 = 200;				// pair with 1 hole card
	public final int gValPair2 = 200;				// pair with 2 hole cards
	public final int gValPairOut = 200;				// cost of pair outs
	public final int gValHiCard = 100;

	Hand(  Game game, Deck theDeck, int handCount) throws IOException {
		G = game;
if (gTest) test();
		if (G.gInGameNum > 0) {
			
			// Initialize for the new hand
			// Set player positions
			int pos = 0;
			for (int i = 0; i < G.gSeatsNum; i++) {
				if (G.gInGame[(G.gButton+i)%G.gSeatsNum]) {
					G.gPlayers[(G.gButton+i)%G.gSeatsNum].set_pos(this, (G.gInGameNum > 2) ? pos : (pos == 1) ? 2 : 1);
					pos++;
				}
			}
			
			gHandLog  = String.format("%06d %2d ", handCount, G.gButton+1);
			gCountInHand = 0;
			gPotTotal = 0.0;
			for (int i = 0; i < G.gSeatsNum; i++) {
				if (G.gInGame[i]) {
					gInHand[i] = true;
					gCountInHand = G.gSeatsNum;
					for (int j = 0; j < 9; j++) {
						gInPot[i][j] = true;
						if (i == 0) {
							gPots[j] = 0.0;
						}
					} // endfor i
					gAllInShort[i] = 0.0;
				}
			} // endfor i
			gPotIdx = 0;
			gNoFlop = true;
		
			// Ante
			if (G.gAnte > 0.0) {
				for (int i = 0; i < G.gSeatsNum; i++) {
					if (G.gInGame[i]) {
						if (G.gPlayers[i].get_stack() <= G.gAnte) {
							G.gInGame[i] = false;
						}
						else {
							G.gPlayers[i].ante(G.gAnte);
							gPots[0] += G.gAnte;
							gPotTotal += G.gAnte;
						}
					}
				} // endfor i
			}
			
			gPotInit = gPots[0];
			gHandLog += String.format("%.0f ", G.gAnte);
			
			//Shuffle
			theDeck.shuffle(G.gRnd);
		
			// Deal (by seat, not button since is does not matter)
			gHandLog += "/";
			for (int i = 0; i < G.gSeatsNum; i++) {
				if (G.gInGame[i]) {
					Card c1 = theDeck.pull();
					Card c2 = theDeck.pull();
					if ((c1.get_rank() < c2.get_rank()) == (c1.get_suit() == c2.get_suit())) {
						
						G.gPlayers[i].put_hole1(c2);
						G.gPlayers[i].put_hole2(c1);
						gHandLog += String.format("%s:%c%c%c%c/", G.gPlayers[i].getName(),
							 c2.get_face(), c1.get_face(), c2.get_suit(), c1.get_suit());	
					}
					else {
						G.gPlayers[i].put_hole1(c1);
						G.gPlayers[i].put_hole2(c2);
						gHandLog += String.format("%s:%c%c%c%c/", G.gPlayers[i].getName(),
							 c1.get_face(), c2.get_face(), c1.get_suit(), c2.get_suit());	
					}
				}
			} // endfor i
if (handCount == 25) {
	handCount = 25;
}
			// Play 
			if (gCountInHand > 1) {
				play_hand(theDeck);
			}
			
		}
	}

	void play_hand(Deck theDeck) {
		
		// Bet pre-flop
		gHandLog += " ";
		bet(kPREFLOP);
		if (gCountInHand > 1) {
			
			// Flop
			for (int i = 0; i < 3; i++) {
				gBoard[i] = theDeck.pull();
				gHandLog += String.format("%c", gBoard[i].get_face());
				gBoardCount += 3;
			}
			for (int i = 0; i < 3; i++) {
				gHandLog += String.format("%c", gBoard[i].get_suit());
			}
			gNoFlop = false;
			gHandLog += " ";
			int[] a = eval_hand(3, -1);
			flop_val = a[0];
			flop_one_outs = a[2];
			flop_two_outs = a[3];
			
			// Bet flop
			bet(kFLOP);
			if (gCountInHand > 1) {
			
				// Turn
				gBoard[3] = theDeck.pull();
				gBoardCount++;
				gHandLog += String.format("%c%c ", gBoard[3].get_face(), gBoard[3].get_suit());
				a = eval_hand(4, -1);
				turn_val = a[0];
				turn_one_outs = a[2];
				turn_two_outs = a[3];
				
				// Bet turnD
				bet(kTURN);
				if (gCountInHand > 1) {
				
					// River TBD
					gBoard[4] = theDeck.pull();
					gBoardCount++;
					gHandLog += String.format("%c%c ", gBoard[4].get_face(), gBoard[4].get_suit());
					 a = eval_hand(5, -1);
					river_val = a[0];
					river_one_outs = a[2];
					river_two_outs = a[3];
					
					// Bet river TBD
					bet(kRIVER);
					if (gCountInHand > 1) {
			
						// Showdown TBD
						rake();
						showdown();
					}
				}
			}
		}
	
		// If no showdown
		if (gCountInHand == 1) {
			rake();
			int p = 0;
			for (; p < G.gSeatsNum; p++) {
				if (gInHand[p]) {
					break;
				}
			}
			gHandLog += String.format("(%.0f)", G.gPlayers[p].get_stack());
			for (int t = 0; t <= gPotIdx; t++) {
				
				G.gPlayers[p].xfer(gPots[t]);
				gPots[t] = 0.0;
			}
			gHandLog += String.format(" %s:+%.0f=%.0f", G.gPlayers[p].getName(), gPotTotal, G.gPlayers[p].get_stack());
			gPotTotal = 0.0;
		}
	}
	
	void bet(int street) {

		gStreet = street;
		double[] last = new double[10];
		gCountInHand = 0;
		gCountInPot = 0;
		for (int i = 0; i < G.gSeatsNum; i++) {
			last[i] = 0.0;
			if (gInHand[i]) {
				gCountInHand++;
				if (gInPot[i][gPotIdx]) {
					gCountInPot++;
				}
			}
		}
		
		// Until all bets in
		double bet = 0.0;
		int limpers = 0;
		int raiseCount = 0;
		int lastRaiser = -1;
		boolean option = false;
		int p = G.gButton;	// start at small blind
		int action = 0;
		while (gCountInPot > 1) {
		
			// Step to next better
			int next = 0;
			for (; next < 10; next++) {
				p = (++p)%G.gSeatsNum;
				if ((gInHand[p] == true)
					&& (gInPot[p][gPotIdx] == true)
					&& (gAllInShort[p] == 0.0)) {
					break;
				}
			}	
			if (next == 10) break;
			
			// If better is the last raiser, break
			if (p == lastRaiser) {
				if (raiseCount == 0) { // back to bb
					option = true;
				}
				else {
					break;  // early exit - betting round completed
				}
			}
			if (lastRaiser < 0) {
				lastRaiser = p; // small blind
			}
			
			int inAfter = 0;
			for (int i = 1; i < G.gSeatsNum; i++) {
				if (gInHand[(p+i)%G.gSeatsNum]) {
					inAfter++;
				}
			}
			
			// Push chips maybe *************************************************************************
			double see = bet - last[p];
			double post = G.gPlayers[p].get_action(street, see, last[p], limpers, inAfter, raiseCount);
			gObPosts[p][(++gObPostsIdx[p])%20] = post; // save observation data in ring

			if ((post == 0.0) && (see == 0.0)) {
				action = kCHECK;
				if (option) {
					break; // early exit - no option raise
				}
			}
			else if (post == see) {
				action = kCALL;
				if ((bet == G.gBigBlind) && (last[p] == 0.0)) {
					limpers++;
				}
			}
			else if (post > see) {
				action = kRAISE;
				raiseCount++;
				bet += (post - see);
				lastRaiser = p;
			}
			else {
				action = kFOLD;
				gInHand[p] = false;
				gInPot[p][gPotIdx] = false;
				gCountInHand--;
				if (p == lastRaiser) {
					lastRaiser = -1;
				}
			}
			players_observe(action, p, see, bet);
				
			// If there is a bet to see even your own
			if (post >= 0.0) {
				
				// If in the current pot
				if (gInPot[p][gPotIdx]) {
					String cAllIn = "";
					double cstack = G.gPlayers[p].get_stack();
					
					// If not all-in already
					if (cstack > 0.0) {
						
						// If post is larger than stack
						if (post >= cstack) {
							
							// Record short bet and go all in
							gAllInShort[p] = cstack;
							gPots[gPotIdx] += cstack;
							gPotTotal += cstack;
							G.gPlayers[p].set_stack(0.0);
							if (p == lastRaiser) {
								lastRaiser = -1;
							}
							cAllIn = String.format("*%.0f/%.0f", cstack, cstack);
						}
						
						// Else put the post in the pot
						else {
							gPots[gPotIdx] += post;
							gPotTotal += post;
							G.gPlayers[p].xfer(-post);
						}
					}
					//gHandLog += String.format("%d%c%.0f/%.0f/%.0f/=%.0f%s ", p+1, "FXCRSB".charAt(action), see, post, bet, gPots[gPotIdx], cAllIn);
					gHandLog += String.format("%d%c%.0f=%.0f%s ", p+1, "FXCRSB".charAt(action-kFOLD), post-last[p], bet, cAllIn);
				}
			}
			
			// Remember bet size met
			last[p] = bet;
			
		} // endwhile ((gCountInHand > 1) && (raiseCount < 4))
		
		// Betting round has ended, if and while all-ins
		while (true) {
			
			// Find smallest all-in short amount if any
			double allInShort = 2*bet;
			int allInPlayer = -1;
			for (int i = 0; i < G.gSeatsNum; i++) {
				if ((gAllInShort[i] > 0.0) && (gAllInShort[i] < allInShort) && gInPot[i][gPotIdx]) {
					allInShort = gAllInShort[i];
					allInPlayer = i;
					gAllInShort[i] = 0.0;
				}
			}
			
			// If no shorts, break
			if (allInPlayer < 0) {
				break;
			}
			
			// Move bets above short into next side pot
			for (int i = 0; i < G.gSeatsNum; i++) {
				if (gInHand[i]) {
					if (i != allInPlayer) {
						gPots[gPotIdx] -= (bet-allInShort);
						gPots[gPotIdx+1] += (bet-allInShort);
					}
					else {
						
						// And remove allInPlayer in from next side pot
						gInPot[i][gPotIdx+1] = false;
					}
				}
			} // endfor i
			
			// Advance to next side pot
			gPotIdx++;
			
		}  // endwhile true
	}
	
	void rake() {
		if (G.gRakeApplied) {
			double rake = 0.0;
			
			if (G.gFixedRake > 0.0) {
				rake += G.gFixedRake;
			}
			
			if (G.gRakePC > 0.0) {
				rake += G.gRakePC*gPotTotal;
			}
			
			if ((G.gRakeCap > 0.0) && (rake > G.gRakeCap)) {
				rake = G.gRakeCap;
			}
			
			if ((G.gRakeMin > 0.0) && (rake < G.gRakeMin)) {
				rake = G.gRakeMin;
			}
			
			double deadDrop = 0.0;
			if (G.gDeadDropRake > 0.0) {
				deadDrop = Math.min(G.gDeadDropRake, G.gPlayers[G.gButton].get_stack());
			}
			
			if (gNoFlop && G.gRakeNoFlop) {
 				rake = 0.0;
				deadDrop = 0.0;
			}
			
			gHandLog += String.format(" H=%.0f", rake+deadDrop);
			
			// For all pots, apply rake
			G.gPlayers[G.gButton].ante(deadDrop);
			G.gRakeTotal += deadDrop;
			for (int i = 0; i <= gPotIdx; i++) {
				double pRake = Math.min(rake, gPots[i]);
				gPots[i] -= pRake;
				gPotTotal -= pRake;
				G.gRakeTotal += pRake;
				rake -= pRake;
			}
		}
		// TBD
	}
	
	
	void showdown() {
		for (int pot = 0; pot <= gPotIdx; pot++) {
			int topVal = -1;
			int[] winners = new int[10];
			int winnersCount = 0;
			for (int i = 0; i < G.gSeatsNum; i++) {
				int p = (G.gButton+i)%G.gSeatsNum;
				if ((gInHand[p]) && (gInPot[p][pot])) {
					Card[] cards7 = {
							G.gPlayers[p].get_hole1(),
							G.gPlayers[p].get_hole2(),
							gBoard[0],
							gBoard[1],
							gBoard[2],
							gBoard[3],
							gBoard[4],
							gBoard[0]};
					
					// Sort hi to lo by rank
					boolean change = true;
					while(change) {
						change = false;
						for (int c = 1; c < 7; c++) {
							if (cards7[c].get_rank() > cards7[c-1].get_rank()) {
								Card x = cards7[c-1];
								cards7[c-1] = cards7[c];
								cards7[c] = x;
								change = true;
							}
						}
					} // endwhile change
					cards7[7] = cards7[0]; // wrap for straight detection 
					
					int val = 0;
					int lastRank = -10;
					int[] flushCount = new int[4];
					int[] flushVal = new int[4];
					int flushIndex = -1;
					int rc = 0;
					int sc = 0;
					int st = -1;
					int p1 = -1;
					int p2 = -1;
					int trip = -1;
					int quad = -1;
	
					// Parse 8 cards
					for (int c = 0; c < 8; c++) {
						int newRank = cards7[c].get_rank();
						val += (2^newRank);
						
						// Detect flushes
						if (c < 7) {
							int k = "cdhs".indexOf(cards7[c].get_suit());
							flushCount[k]++;
							if (flushCount[k] < 6) {
								flushVal[k] += newRank;
							}
							if (flushCount[k] > 4) {
								flushIndex = k;
							}
						}
						
						// Detect pairs, trips and quads
						if (newRank == lastRank) {
							rc++;
							if (c == 6) {
								newRank = -1;
							}
						}
						if (newRank != lastRank) {
							switch(rc) {
							case 1:
								if ((p1 >= 0) && (p2 < 0)) {
									p2 = lastRank;
								}
								else if (p1 < 0) {
									p1 = lastRank;
								}
								break;
							case 2:
								if (p1 == trip) {
									p1 = -1;
								}
								if (p2 == trip) {
									p2 = -1;
								}
								trip = lastRank;
								break;
							case 3:
								quad = lastRank;
								break;
							}
							rc = 0;
						}
						
						// Detect straights
						if (newRank == (lastRank-1)) {
							if (sc == 0) {
								st = lastRank;
							}
							sc++;
						}
						else {
							if (sc < 4) {
								sc = 0;
							}
						}
						
						lastRank = newRank;
					} // endfor 7 cards
					
					if ((flushIndex >= 0) && (sc > 3)) { // if maybe straight flush
						int fsc = 0;
						for (int j = 0; j < 8; j++) {
							int k = "cdhs".indexOf(cards7[j].get_suit());
							if (k == flushIndex) {
								if (j > 0) {
									if (cards7[j].get_rank() == lastRank-1) {
										fsc++;
									}
									else {
										fsc = 0;
									}
									lastRank = cards7[j].get_rank();
								}
							}
						} // endfor j		
						if (fsc > 3) {
							val = (40000 + flushVal[flushIndex]);
							quad = 1;
						}
					}
					if (quad >= 0) { // quads
						val += 30000;
					}
					else if ((trip >= 0) && (p1 >= 0)) { // house
						val +=  (20000 + (100*trip) + (50*p1));
					}
					else if (flushIndex >= 0) { // flush
						val = (10000 + flushVal[flushIndex]);
					}
					else if (trip >= 0) { // trips
						val +=  (8000 + (100*trip));
					}
					else if (sc > 3) { // straight
						val += (6000 + 100*st);
					}
					else if ((p1 >= 0) && (p2 >= 0)) { // two pair
						val +=  (4000 + (100*p1) + (50*p2));
					}
					else if (p1 >= 0) { // one pair
						val +=  (2000 + (100*p1));
					}
					
					// Request reveal
					if (G.gPlayers[p].show_hole(val > topVal)) {
						gObCards [p][0] = G.gPlayers[p].get_hole1();
						gObCards[p][1] = G.gPlayers[p].get_hole2();
						players_observe(kHOLE, p, 0, 0);
					}
					
					if (val > topVal) {
						topVal = val;
						winners[0] = p;
						winnersCount = 1;
					}
					else if (val == topVal) {
						winners[winnersCount] = p;
						winnersCount++;
					}
				}
			} // endfor each player
		
			// For all winners in that pot
			for (int i = 0; i < winnersCount; i++) {
				int p = winners[i];
				
				// Divide that pot among the winners
				gHandLog += String.format("(%.0f)", G.gPlayers[p].get_stack());
				G.gPlayers[p].xfer(gPots[pot]/winnersCount);
				if (G.gTipping) {
					G.gRakeTotal += G.gPlayers[p].tip(gPots[pot]/winnersCount);
				}
				gHandLog += String.format("%s:+%.0f=%.0f", G.gPlayers[p].getName(), gPots[pot]/winnersCount, G.gPlayers[p].get_stack());
			}
		} // endfor each pot

	}	// hand ended so no need to updat gPotTotal
	
	// Called for every action by other players that players can observe
	void players_observe(int what, int p, double see, double raise) {
		for (int i = 0; i < G.gSeatsNum; i++) {
			if (G.gInGame[i]) {
				switch (what) {
				case kHOLE :
					G.gPlayers[i].observe_reveal(p, G.gPlayers[p].get_hole1(), G.gPlayers[p].get_hole2());
					break;
				case kFOLD :
					if (i==0) {
						if (gStreet > kPREFLOP) {
							G.sees_flop[p]++;
						}
					}
					G.gPlayers[i].observe_post(p, gObPosts[p][gObPostsIdx[p]%20]);
				case kCALL :
					if (i==0) {
						G.calls[p]++;
						if (see == G.gBigBlind) {
							G.limps[p]++;
						}
					}
					G.gPlayers[i].observe_post(p, gObPosts[p][gObPostsIdx[p]%20]);
				case kRAISE :
					if (i==0) {
						G.raises[p]++;
					}
					G.gPlayers[i].observe_post(p, gObPosts[p][gObPostsIdx[p]%20]);
					break;
				}
			}
		}
	}
	
	int[] eval_hand(int n, int p) {
		int oneOuts = 0;
		int twoOuts = 0;	// Ax, pairs and suited connectors only
		int makeOuts = 0;
		int nn = n;
		
		Card[] cards = {
				gBoard[0],
				gBoard[1],
				gBoard[2],
				gBoard[3],
				gBoard[4],
				gBoard[4],
				gBoard[4],
				gBoard[4]};
		if (p >= 0) {
			cards[n++] = G.gPlayers[p].get_hole1();
			cards[n++] = G.gPlayers[p].get_hole2();
		}
		// Sort hi to lo by rank
		boolean change = true;
		while(change) {
			change = false;
			for (int c = 1; c < n; c++) {
				if (cards[c].get_rank() > cards[c-1].get_rank()) {
					Card x = cards[c-1];
					cards[c-1] = cards[c];
					cards[c] = x;
					change = true;
				}
			}
		} // endwhile change
		cards[n] = cards[0]; // wrap for straight detection 

		// Parse n+1 cards
		Card[] holeCards = new Card[2];
		int hci = 0;
		
		Card[][] flushes = new Card[4][7];
		int[] fi = {0,0,0,0};
		
		Card[][] pairings = new Card[4][7];
		int[] p2i = {0,0,0,0};
		int p1i = 0;
		
		int[] pa = {0,0,0,0};		// pairings
		int[] ph = {0,0,0,0};		// pairs holes
		int[] pr = {-1,-1,-1,-1};	// pairs rank
		int pai = 0;				// pair count
		Card[][] straights = new Card[4][7];
		int[] s2i = {0,0,0,0};
		boolean[] hiGut = {false, false, false, false};
		boolean[] loGut = {false, false, false, false};
		int s1i = 0;
		
		for (int c = 0; c < n; c++) {

			// Hole cards
			if (cards[c].gLoc >= 0) {
				holeCards[hci++] = cards[c];
			}
			
			// Flushes
			int f = "cdhs".indexOf(cards[c].gSuit);
			flushes[f][fi[f]++] = cards[c];
			
			// Pairing
			if (cards[c].gRank == cards[c+1].gRank) {
				pairings[p1i][p2i[p1i]++] = cards[c];
				pa[pai]++;
				pr[pai] = cards[c].gRank;
				if (cards[c].gLoc >= 0) {
					ph[pai]++;
				}
			}
			
			// Straights
			else if (cards[c].gRank == (cards[c+1].gRank+1)) {
				if ((c > 0) && (s2i[s1i] == 0) && (cards[c-1].gRank == cards[c].gRank+2)) {
					hiGut[s1i] = true;
				}
				if ((c > 1) && (c < (n-1)) && (s2i[s1i] > 1) && (cards[c+1].gRank == cards[c].gRank-2)) {
					loGut[s1i] = true;
				}
				straights[s1i][s2i[s1i]++] = cards[c];
			}
			
			else {
				if (p2i[p1i] > 0) {
					pairings[p1i][p2i[p1i]++] = cards[c];
					p1i++;
				}
				if (s2i[s1i] > 0) {
					straights[s1i][s2i[s1i]++] = cards[c];
					s1i++;
				}
				
				if (pa[pai] > 0) {
					pai++;
				}
			}
			
if (gTest) System.out.printf("%c%c%c", (cards[c].gLoc < 0) ? '-' : '+', cards[c].gFace, cards[c].gSuit);
		} // endfor c
		

		int val = 0;
		
		// If flush
		int f5i = -1; 
		for (int i = 0; i < 4; i++) {
			if (fi[i] > 2) {
				int t = 12;
				int holenum = 0;
				int tophole = -2;
				if (nn < 5) {
					for (int j = 0; j < fi[i]; j++) {
						while ((t > tophole) && (flushes[i][j].gRank < t)) {
							oneOuts++;
							t--;
						}
						if (flushes[i][j].gLoc >= 0) {
							if (holenum == 0) {
								tophole = flushes[i][j].gRank;
								holenum++;
							}
						}
						twoOuts += Math.max((((j > 0) ? flushes[i][j-1].gRank : 12) - flushes[i][j].gRank) - 1, 0);
						t--;
					} // endfor j
				}
				if ((nn < 5) && (fi[i] == 3) && (holenum == 0)) {
					val += gVal3Flush0;
				}
				else if ((nn < 5) && (fi[i] == 4)) {
					val += ((holenum == 2) ? gVal4Flush2 : (holenum == 1) ? gVal4Flush1 : gVal4Flush0) + (oneOuts * gVal4FlushOut);
				}
				else if (fi[i] == 5) {
					f5i = i;
					val += ((holenum == 2) ? gVal5Flush2 : (holenum == 1) ? gVal5Flush1 : gVal5Flush0) + (oneOuts * gVal5FlushOut);
				}
			}
		}  // endfor i
		
		// If straight
		for (int i = 0; i < s1i; i++) {
			int holenum = 0;
			boolean flushtoo = true;
			for (int j = 0; j < s2i[i]; j++) {
				if (straights[i][j].gSuit != straights[i][0].gSuit) {
					flushtoo = false;
				}
				if (straights[i][j].gLoc >= 0) {
						holenum++;
				}
			} // endfor j
			boolean hi = (straights[i][0].gLoc >= 0);
			boolean lo = false;
			if (s2i[i] > 2) {
				if (hi) {
					lo = (straights[i][s2i[i]-1].gLoc >= 0);
				}
				else {
					lo = ((straights[i][s2i[i]-1].gLoc >= 0) && (straights[i][s2i[i]-2].gLoc >= 0));
				}
			}
			
			if ((nn < 5) && (s2i[i] == 3) && (hiGut[i] || loGut[i])) {
				val += gVal3Straight0;
				makeOuts += (hiGut[i] ? 4 : loGut[i] ? 4 : 0);
				oneOuts += (hi ? loGut[i] ? 4 : 0 : lo ? hiGut[i] ? 4 : 0 : 0);
				twoOuts += ((straights[i][0].gRank < 11) ? 4 : 0) + ((straights[i][s2i[i]-1].gRank > 1) ? 4 : 0); // suited connectors
			}
			else if ((nn < 5) && (s2i[i] == 4)) { // draw
				makeOuts += ((straights[i][0].gRank > 11) ? 12  : ((straights[i][3].gRank < 1) ? 12 : 13)) - 4;
				if (flushtoo) {
					if (hi) {
						val += ((holenum == 2) ? gVal4hiStraightFlush2 : (holenum == 1) ? gVal4hiStraightFlush1 : gVal4straightFlush0);
					}
					else if (lo) {
						val += ((holenum == 2) ? gVal4loStraightFlush2 : (holenum == 1) ? gVal4loStraightFlush1 : gVal4straightFlush0);
						oneOuts = makeOuts/8;
					}
					else {
						val += ((holenum == 2) ? gVal4inStraightFlush2 : (holenum == 1) ? gVal4inStraightFlush1 : gVal4straightFlush0);
					}
				}
				else {
					if (hi) {
						val += ((holenum == 2) ? gVal4hiStraight2 : (holenum == 1) ? gVal4hiStraight1 : gVal4straight0);
					}
					else if (lo) {
						val += ((holenum == 2) ? gVal4loStraight2 : (holenum == 1) ? gVal4loStraight1 : gVal4straight0);
						oneOuts += makeOuts/2;
					}
					else {
						val += ((holenum == 2) ? gVal4inStraight2 : (holenum == 1) ? gVal4inStraight1 : gVal4Flush0);
					}
				}
			}
			else if (s2i[i] == 5) {
				if(flushtoo) {
					if (hi) {
						val += ((holenum == 2) ? gVal5hiStraightFlush2 : (holenum == 1) ? gVal5hiStraightFlush1 : gVal5straightFlush0);
					}
					else if (lo) {
						val += ((holenum == 2) ? gVal5loStraightFlush2 : (holenum == 1) ? gVal5loStraightFlush1 : gVal5straightFlush0);
						oneOuts = (straights[i][0].gRank < 12) ? 1  : 0;
					}
					else {
						val += ((holenum == 2) ? gVal5inStraightFlush2 : (holenum == 1) ? gVal5inStraightFlush1 : gVal5straightFlush0);
					}
				}
				else {
					if (hi) {
						val += ((holenum == 2) ? gVal5hiStraight2 : (holenum == 1) ? gVal5hiStraight1 : gVal5straight0);
					}
					else if (lo) {
						val += ((holenum == 2) ? gVal5loStraight2 : (holenum == 1) ? gVal5loStraight1 : gVal5straight0);
						oneOuts = (straights[i][0].gRank < 12) ? 4  : 0;
					}
					else {
						val += ((holenum == 2) ? gVal5inStraight2 : (holenum == 1) ? gVal5inStraight1 : gVal5straight0);
					}
				}
			}
		} // endfor i
		
		// Top cards
		Card[] topCards = cards;
		int tci = 0;
		for (int i = 0; i < n; i++) {
			int j = 0;
			for (; j < p1i; j++) {
				if (cards[i].gRank == pairings[j][0].gRank) {
					break;
				}
			} // endfor j
			if (j == p1i) {
				topCards[tci++] = cards[i];
			}
		} // endfor i
		
		// If pairings
		int pairHoleNum = -1;
		int tripHoleNum = -1;
		for (int i = 0; i < p1i; i++) {
			int holenum = 0;
			int rank = pairings[i][0].gRank;
			for (int j = 0; j < p2i[i]; j++) {
				if (pairings[i][j].gLoc >= 0) {
					holenum++;
				}
			}
			for (int j = 0; j < n; j++) {
				if (cards[j].gRank != rank) {
				}
			}
			if (p2i[i] == 2) { // pair maybe boat
				if (tripHoleNum == 0) { // boat ! there is a higher trip on board
					if (pairHoleNum >= 0) { // ignore 2d pair
						val += ((holenum == 2) ? gValHiBoat20 : (holenum == 1) ? gValHiBoat10 : gValHiBoat00);
						if (holenum == 2) {
							oneOuts = 1 + ((topCards[0].gRank > rank) ? 3 : 0) + ((topCards[1].gRank > rank) ? 3 : 0);
						}
						else if (holenum == 1) {
							oneOuts = 1;
							twoOuts = (6*(11-rank)) - ((topCards[0].gRank > rank) ? 3 : 0) - ((topCards[1].gRank > rank) ? 3 : 0);
						}
						else {
							makeOuts = (tci*3) + (4*(12-holeCards[0].gRank));
							oneOuts = makeOuts;
						}
					}
				}
				else if (tripHoleNum == 1) { // boat! higher trip
					if (pairHoleNum >= 0) { // ignore 2d pair
						val +=  (holenum == 1) ? gValHiBoat11 : gValHiBoat01;
						makeOuts = 1;
						oneOuts = 1;
						twoOuts = 0;
					}
				}
				else if (tripHoleNum == 2) { // boat! higher trip
					if (pairHoleNum >= 0) { // ignore 2d pair
						val +=  gValHiBoat02;
						makeOuts = 1;
						oneOuts = 1;
						twoOuts = 0;
					}
				}
				else { // just a pair for now
					if (pairHoleNum < 0) {
						oneOuts = 0;
						makeOuts = 0;
					}
					val += ((holenum == 2) ? gValPair2 : (holenum == 1) ? gValPair1 : gValPair0)
							+ ((14-pairings[i][0].gRank) * gValPairOut);
					makeOuts += 2; // to trips
					if (holenum > 0) {
						for (int j = 0; j < tci; j++) {
							if (topCards[j].gRank > rank) {
								makeOuts += 3;
								if (topCards[j].gLoc < 0) {
									oneOuts += 3;
									twoOuts += 6; // higher pair
								}
							}
						}
					}
					else { // no hole in pair
						oneOuts += 2; // to trips
						makeOuts += (tci*3) + (4*(12-holeCards[0].gRank));
						oneOuts += makeOuts;
						if (holeCards[0].gRank < 12) {
							twoOuts += (8*(12-tci-p1i)); // Ax
						}
						twoOuts += 6*(12-tci-p1i); // pair
					}
					pairHoleNum = holenum;
				}
			}
			else if (p2i[i] == 3) { // trips maybe boat
				tripHoleNum = holenum;
				oneOuts += ((pairHoleNum >= 0) ? 2 : 0);
				if (pairHoleNum == 0) { // there is a higher pair
					val += ((holenum == 2) ? gValLoBoat20 : (holenum == 1) ? gValLoBoat10 : gValLoBoat00);
				}
				else if (pairHoleNum == 1) {
					val +=  (holenum == 1) ? gValLoBoat11 : gValLoBoat01;
				}
				else if (pairHoleNum == 2) {
					val +=  gValLoBoat02;
				}
				else { // just trips
					val += ((holenum == 2) ? gValTrip2 : (holenum == 1) ? gValTrip1 : gValTrip0);
					makeOuts += 1; // to quads
					for (int j = 0; j < n; j++) {
						if (cards[j].gRank != pairings[i][0].gRank) {
							makeOuts += 3; // to house
							if (holenum == 0) {
								oneOuts += 3;
							}
						}
					}
					if (holenum == 0) { // trip is on the board
						oneOuts += 1;
						twoOuts += (12-pairings[i][0].gRank); // higher pairs to make house
					}
				}
			}
			else if (p2i[i] == 4) {
				val += ((holenum > 0) ? gValQuad12 : gValQuad0/(13-holeCards[0].gRank));
				makeOuts = (holenum == 0) ? 4*(12-holeCards[0].gRank) : 0;
				oneOuts = (holenum == 0) ? 4*(12-holeCards[0].gRank) : 0;
			}
		} // endfor pi
		
		// If hi card only
		if (oneOuts == 0) {
			makeOuts = (tci*3) + (4*(12-holeCards[0].gRank));
			oneOuts = makeOuts;
		}
		if (topCards[0] == holeCards[0]) {
			val += gValHiCard ;
		}
		
		int[] ret = {val, makeOuts, oneOuts, twoOuts};	
		return ret;	
	}

	int get_one_outs(int val) {
		int oneOuts = 0;
		switch (val) {
		case gValQuad12 :				// quads with any holes
		case gValQuad0 :				// quads with 0 holes
		case gValHiBoat00 :			// house 3 over 2 with 0 holes
		case gValHiBoat01 :			// house 3 over 2 with 1 or 2 holes in trip
		case gValHiBoat10 :			// house 3 over 2 with 1 hole in trip and 1 in pair
		case gValHiBoat11 :			// house 2 over 3 with 0 holes
		case gValHiBoat02 :			// house 2 over 3 with 0 holes
		case gValHiBoat20 :			// house 2 over 3 with 0 holes
		case gValLoBoat00 :			// house 2 over 3 with 1 or 2 holes in trip
		case gValLoBoat01 :			// house 2 over 3 with 0 holes
		case gValLoBoat10 :			// house 2 over 3 with 2 holes in pair
		case gValLoBoat11 :			// house 2 over 3 with 1 or 2 holes in trip
		case gValLoBoat02 :			// house 2 over 3 with 0 holes
		case gValLoBoat20 :			// house 2 over 3 with 2 holes in pair
		case gVal3Flush0 :				// 3 flush draw with 0 holes
		case gVal4Flush0 :				// 4 flush draw with 0 holes
		case gVal4Flush1 :				// 4 flush draw with 1 hole
		case gVal4Flush2 :				// 4 flush draw with 2 holes
		case gVal4FlushOut : 			// cost of 5 flush outs
		case gVal5Flush0 :				// flush with no holes
/*
		case gVal5Flush1 :				// flush with 1 hole
		case gVal5Flush2 :				// flush with 2 holes
		case gVal5FlushOut : 			// cost of 5 flush outs
		case gVal3Straight0 :			// 3 straight draw with 0 holes
		case gVal4straight0 :			// 4 straight draw with 0 holes
		case gVal4hiStraight1 : 		// 4 straight draw with 1 hole
		case gVal4inStraight1 :			// 4 straight draw with 0 holes
		case gVal4loStraight1 : 		// 4 straight draw with 1 hole
		case gVal4hiStraight2 : 		// 4 straight draw with 1 hole
		case gVal4inStraight2 :			// 4 straight draw with 0 holes
		case gVal4loStraight2 : 		// 4 straight draw with 1 hole
		case gVal5straight0 :			// 4 straight draw with 0 holes
		case gVal5hiStraight1 : 		// 4 straight draw with 1 hole
		case gVal5inStraight1 :			// 4 straight draw with 1 inside hole
		case gVal5loStraight1 : 		// 4 straight draw with 1 hole
		case gVal5hiStraight2 : 		// 4 straight draw with 1 hole
		case gVal5inStraight2 :			// 4 straight draw with 0 holes
		case gVal5loStraight2 : 		// 4 straight draw with 1 hole
		case gVal4straightFlush0 : 		// 4 straight flush draw with 0 holes
		case gVal4hiStraightFlush1 : 	// 4 straight flush draw with 1 top hole
		case gVal4inStraightFlush1 :	// 4 straight flush draw with 0 holes
		case gVal4loStraightFlush1 : 	// 4 straight flush draw with 1 hole
		case gVal4hiStraightFlush2 : 	// 4 straight flush draw with 1 hole
		case gVal4inStraightFlush2 :	// 4 straight flush draw with 0 holes
		case gVal4loStraightFlush2 : 	// 4 straight flush draw with 1 hole
		case gVal5straightFlush0 : 		// 4 straight flush draw with 0 holes
		case gVal5hiStraightFlush1 : 	// 5 straight flush with 1 hole
		case gVal5inStraightFlush1 :	// 5 straight flush with 0 holes
		case gVal5loStraightFlush1 : 	// 5 straight flush with 1 hole
		case gVal5hiStraightFlush2 : 	// 5 straight flush with 2 holes
		case gVal5inStraightFlush2 :	// 5 straight flush with 2 holes
		case gVal5loStraightFlush2 : 	// 5 straight flush with 2 holes
		case gValTrip0 :				// trips with 0 hole cards
		case gValTrip1 :				// trips with 1 hole card
		case gValTrip2 :				// trips with 2 hole cards
		case gValPair0 : 				// pairs with 0 hole card
		case gValPair1 :				// pair with 1 hole card
		case gValPair2 :				// pair with 2 hole cards
		case gValPairOut :*/
		}
 		return oneOuts;
 	}
 	void test() throws IOException {
 		int s = 13;
 		do {
			int n = 5;
			try {
				FileReader fr = new FileReader("test.txt");
				BufferedReader in = new BufferedReader(fr);
				String line = in.readLine();
				n = Integer.valueOf(line.substring(0, 1));
				for (int i = 0; i < 7; i++) {
					int j = 2*i+1;
					int rank = "23456789TJQKA".indexOf(line.substring(j, j+1));
					int suit = "cdhs".indexOf(line.substring(j+1, j+2));
					if (i < 5) {
						gBoard[i] = new Card(rank, suit, -1);
					}
					else if (i == 5) {
						G.gPlayers[0].put_hole1(new Card(rank, suit, 0));
					}
					else {
						G.gPlayers[0].put_hole2(new Card(rank, suit, 0));
					}
				} // endfor i
				in.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}
			
			int[] vals = eval_hand(Math.min(n, 5), 0);
			System.out.printf(" val=%d makeOuts=%d oneOuts=%d two_outs=%d\n", vals[0], vals[1], vals[2], vals[3]);
			
			s = System.in.read();
			System.in.read();
			
		} while (s == 13);
	}
}
