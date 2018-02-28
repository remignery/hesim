package holdem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Player {
	

	private final int kNUMMOODS = 4;				// max # of profiles available
	private final int kMAXRULES = 64;				// max # of rules applicable per mood
	private final int kMAXFAULTS = 16;				// max # of faults
	private final int kMAXCHEATS = 16;				// max # of cheats
	private final int kFOLD = 0;					// action = fold
	private final int kCALL = 1;					// action = call
	private final int kRAISE = 2;					// action = raise
	private final int kPOSTSB = 3;					// action = post small blind
	private final int kPOSTBB = 4;					// action = post small blind
	
	private int[] gHole = new int[2];				// hole cards
	private String[] gOreps = new String[9];		// view of other players
	private String gSrep = "";						// how self thought viewed
	private double gStack = 0.0;					// current stack in $
	private int[] gHcs = new int[kNUMMOODS];		// hole card matrix preference indices
	private int gHcv = 0;							// hole card matrix indices variance
	private int gHcc = 0;							// hole card matrix indices choice
	private double gIoFactor = 1.0;					// implied odds estimation
	private double gIov	= 0.0;						// implied odds variance
	private double gSdestim = 0.0;					// showdown odds estimation
	private double gSdv = 0.0;						// showdown odds variance
	private int gAgrs[] = new int[kNUMMOODS];		// aggressivenesses
	private int gMood = 0;							// mood
	private int[] gFaults = new int[kMAXFAULTS];	// faults
	private int[] gCheats = new int[kMAXCHEATS];	// cheats
	private int[][] gPFaults = new int[10][kMAXRULES];	//  and who is perceived to use them
	private int[][] gRules = new int[kNUMMOODS][kMAXRULES];		// rule profiles
	private int[][] gPrules = new int[10][kMAXRULES];	//  and who is perceived to use them
	private int gSeat;
	private int gPos = 0;
	private String gName;
	private Card gHole1;
	private Card gHole2;
	private Integer gBuyIn = 1;
	private double bankRoll = 10000.0;
	private Hand H;
	private Game G;
	
	Player(Game theGame, int id) throws IOException {
		G = theGame;
		gSeat = id;
		load_params(gSeat);
	}
	
	void set_pos(Hand theHand, int pos) {
		H = theHand;
		gPos = pos;
	}
	void observe(String s) {
	}
	void put_hole1 (Card c1) {
		gHole1 = c1;
	}
	void put_hole2 (Card c2) {
		gHole2 = c2;
	}
	Card get_hole1 () {
		return gHole1;
	}
	Card get_hole2 () {
		return gHole2;
	}
	void xfer (double amt) {
		gStack += amt;
	}
	void set_stack (double pot) {
		gStack = pot;
	}
	double act(int pos, int stage, double bet) {
		return 0.0;
	}
	String show(int what) {
		return "TBD";
	}
	double postBigBlind(double bb) {
		gStack -= bb;
		return gStack;
	}
	double postSmallBlind(double sb) {
		gStack -= sb;
		return gStack;
	}
	double ante(double ante) {
		gStack -= ante;
		return gStack;
	}
	boolean rebuy_when_busted(boolean tournament) {
		return true;
	}
	boolean buy_addin(boolean tournament) {
		return true;
	}
	boolean show_hole(boolean force) {
		return force;
	}
	double tip(double win) {
		return 0.0;
	}
	double get_buyIn(double min, double max) {
		double ret = max;
		switch(gBuyIn) {
		case 0 :
			ret = min;
			break;
		case 1 :
			ret = (min+max)/2.0;
			break;
		}
		return ret;
	}
	String getName() {
		return gName;
	}
	double get_stack() {
		return gStack;
	}
	double get_action(int street, double see, double last, int limpers, int inAfter, int raiseCount) {
		double ret = 0.0;
		if (street == H.kPREFLOP) {
			if (last > 0.0) { // not 1st
				ret = get_later_post(see, last, raiseCount);
			}
			else {
				ret = get_pre_flop(see, limpers,  inAfter, raiseCount);
			}
		}
		else if (street == H.kFLOP) {
			ret = get_post_flop(see, limpers, inAfter, raiseCount);
		}
		else if (street == H.kTURN) {
			ret =  get_post_turn(see, limpers, inAfter, raiseCount);
		}
		else if (street == H.kRIVER) {
			ret = get_post_river(see, limpers, inAfter, raiseCount);
		}
		return Math.min(ret, gStack);
	}
	
	private double get_pre_flop(double see, int limpers, int inAfter, int raise_count) {
		
		double post = -1.0; // default fold
		boolean short_stacked = (gStack < (G.kShortStack*G.gBigBlind));
		
		// If small blind and start, post sb
		if ((gPos == 1) && (see == 0.0)) {
			post = G.gSmallBlind;
		}
		
		// If big blind and start, post bb
		else if ((gPos == 2) && (see == G.gSmallBlind)) {
			post = G.gBigBlind;
		}
		
		else {
			// Maybe change hole card matrix preference? TBD
			
			// Get decision from preferred hole card matrix
			int[][] dc = G.gHct[gHcs[gHcc]];
			int[] dl = dc[ 12-gHole1.get_rank()];
			int df = dl[ 12-gHole2.get_rank()];

			// On matrix value
			switch (df) {
			case 0:	// fold
				post = -1.0;
				break;
			// Group One Premium hands that will be raised or re-raised in any position. Raise or call all-in if need be.
			case 1:
				post = see + 3*G.gBigBlind; // if rule, adjust by position.
				break;
			// Group Two Raise the blinds and one or two limpers, otherwise will call if we are short stacked, or the amount to call does not commit our stack.
			case 2:
				if ((see == G.gBigBlind) && (limpers < 3)) {
					post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
				}
				else if (short_stacked || (see < gStack/G.kStackCommit)) {
					post = see; // call
				}
				break;
			// Group Three Raise the blinds in late position, limp in early position. Call if short stacked, or if the amount is small.
			case 3:
				if (raise_count == 2) { // 1st round see = sb and bb
					if (inAfter < 4) {
						post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
					}
					else if (inAfter > 5) { // Limp in early position
						post = see;
					} 
					else if (short_stacked || (see < gStack/G.kStackCommit)) {
						post = see; // call
					} // else fold
					else {
						post = -1.0;
					}
				}
				break;
			// Group Four Raise the blinds only in late position, limp only in mid to late position, and call if short stacked.
			case 4:
				if (raise_count == 2) { // 1st round see = sb and bb
					if (inAfter < 4) {
						post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
					}
					else if (inAfter < 6) {
						post = see; // call
					}
					else {
						post = -1.0; // fold
					}
				}
				else if (short_stacked) {
					post = see; // call
				}
				break;
			// Group Five Raise if heads-up, or raise the blinds short handed. Limp in mid to late position. Raise or call if short stacked.
			case 5:
				if (H.gCountInHand < 6) { // short handed
					if (H.gCountInHand < 3) { // Raise if heads-up 
						post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
					}
					else if (raise_count == 2) {
						post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
					}
				}
				if (post == 0.0) {
					
					if (inAfter < 6) { // limp in mid to late position
						if (raise_count == 2) { // 1st round see = sb and bb
							post = see; // call
						}
						else { // else fold
							post = -1.0;
						}
					} 
					else if (short_stacked) {
						post = see + ((G.gRnd.nextInt(2) > 0) ? (3*G.gBigBlind) : 0.0); // if rule, adjust by position.
					}
					else { // else fold
						post = -1.0;
					}
				}
				break;
			// Group Six Raise the blinds heads-up, or short-stacked. Call in the small blind, or limp in late position. Raise or call if short stacked.
			case 6:
				if ((H.gCountInHand < 3)) { // Raise if heads-up
					post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
				}
				else if ((inAfter < 4) || (gPos == 1)) {
					if (raise_count == 2) {
						post = see; // call
					}
				}
				else if (short_stacked) {
					post = see + ((G.gRnd.nextInt(2) > 0) ? (3*G.gBigBlind) : 0.0); // if rule, adjust by position.
				}
				else { // else fold
					post = -1.0;
				}
				break;
			// Group Seven Limp in late position, raise the blinds heads-up. Raise or call if short stacked.
			case 7:
				if (inAfter < 4) {
					if (raise_count == 2) { // 1st round see = sb and bb
						post = see; // call
					}
				} 
				if (H.gCountInHand < 3) {
					if (raise_count == 2) { // 1st round see = sb and bb
						post = see + 3*G.gBigBlind; // raise - if rule, adjust by position.
					}
				}
				else if (short_stacked) {
					post = see + ((G.gRnd.nextInt(2) > 0) ? (3*G.gBigBlind) : 0.0); // if rule, adjust by position.
				}
				break;
			// Group Eight Heads-up only. Raise if short-stacked, raise the blinds, or call small amounts if stacks are large.
			case 8:
				if (H.gCountInHand < 3) { // Heads-up only
					if (short_stacked) {
						post = see + 3*G.gBigBlind; // if rule, adjust by position.
					}
					else if ((raise_count == 2) && (see == G.gBigBlind)) {	
						post = see + 3*G.gBigBlind; // if rule, adjust by position.
					}
					if ((see < (gStack/G.kStackCommit)) || (gStack > (G.kLargeStack*G.gBigBlind))) {
						post = see; // Call
					}
				}
				break;
			}
		}
		return post;
	}
	
	private double get_later_post(double see, double last, int raiseCount) {
		double ret = -1.0;
		
		// Get pot odds
		double po = see/H.gPotTotal;
		
		// Get effective stack
		double eStack = -1.0;
		for (int i = 0; i < G.gSeatsNum; i++) {
			if ((i != gSeat) && H.gInHand[i] && (G.gPlayers[i].get_stack() > eStack)) {
				eStack = G.gPlayers[i].get_stack();
			}
		}
		if (eStack > gStack) {
			eStack = gStack;
		}
		
		// Get max implied odds
		double winPC = calc_preflop_winpc();
		double io = (see/eStack) * gIoFactor;
		
		// If io > winpc, post
		if (winPC > io) {
			
			// Make post large enough to discourage straight draws
			ret = see +  ((raiseCount > 3) ? 0 : (gStack/10)); // call
		}
		else {
			ret = -1.0; // fold
			
		}
		return ret;
	}

	// Returns chance percent to make winning hand
	// Use rule of thumb 1:9or10 for pairs to trips, 1:16 suited to flush 1:11 connected to straight (1:7 if also suited)
	private double calc_preflop_winpc() {
		double ret = 0.0;
		int outs = 0;
		
		// If pair in hole
		if (gHole1.get_rank() == gHole2.get_rank() ) {
			ret += 1/((gHole1.get_rank() < 10) ? 10 : 9); // =11.11 exact is 10.8 make a set = 3*(2/50); s
		}
		
		// Else to make 2 pairs on any unpaired hole cards
		else { 
			ret += 1/50; // 2 pairs exact
		}
		
		// If suited
		if (gHole1.get_suit() == gHole2.get_suit() ) {
			ret += 1/16; // =6.25 exact is 6.40 to make a flush on the river
		}
		
		// If connected
		if (Math.abs(gHole1.get_rank() - gHole2.get_rank())  == 1) {
			ret +=  1/11; // =9.09 exact is 9.08 to make a straight on the river (1/7 if also suited)
		}
		
		return ret;
	}

	private double get_post_flop(double see, int limpers, int inAfter, int raiseCount) {
		double ret = -1.0;
		
		// Get pot odds
		double po = see/H.gPotTotal;
		
		// Get effective stack
		double eStack = -1.0;
		for (int i = 0; i < G.gSeatsNum; i++) {
			if ((i != gSeat) && H.gInHand[i] && (G.gPlayers[i].get_stack() > eStack)) {
				eStack = G.gPlayers[i].get_stack();
			}
		}
		if (eStack > gStack) {
			eStack = gStack;
		}
		
		// Get max implied odds
		int[] winPC = H.eval_hand(3, gSeat); //calc_flop_winpc(3, false); //true);
		double io = (see/eStack) * gIoFactor;
		
		// If io > winpc, post
		if (winPC[0] > io) {
			
			// Make post large enough to discourage straight draws
			ret = see + ((raiseCount > 3) ? 0 : (gStack/10)); // call
		}
		else {
			ret = -1.0; // fold
			
		}
		return ret;
	}

	// Returns chance percent to make winning hand
	// Use rule of thumb 1:9or10 for pairs to trips, 1:16 suited to flush 1:11 connected to straight (1:7 if also suited)
	private double calc_flop_winpc(int n, boolean holes) {
		double ret = 0.0;
		
		// Get board texture
		int x = 0;
		int r1 = gHole1.get_rank()+2;
		int r2 = gHole2.get_rank()+2;
		int s1 = gHole1.get_suit();
		int s2 = gHole2.get_suit();
		n += holes? 2 : 0;
		
		
		// Collect shorthands
		int[] ri = {0,0,0,0,0,0,0,0}; // [8]
		int[] si = {0,0,0,0,0,0,0,0};
		boolean[] hi = {false,false,false,false,false,false,false,false};
		for (int i = 0; i < (n); i++) {
			ri[i] = (i < n) ? H.gBoard[i].get_rank()+2 : (i == n) ? r1 : r2;
			si[i] = (i < n) ? H.gBoard[i].get_suit()+2 : (i == n) ? s1 : s2;
			hi[i] = (i >= n);
		}
		
		// Sort hi to lo
		boolean change = true;
		while(change) {
			change = false;
			for (int c = 1; c < 7; c++) {
				if (ri[c] > ri[c-1]) {
					int r = ri[c-1];
					int s = si[c-1];
					boolean b = hi[c-1];
					ri[c-1] = ri[c];
					si[c-1] = si[c];
					hi[c-1] = hi[c];
					ri[c] = r;
					si[c] = s;
					hi[c] = b;
					change = true;
				}
			}
		} // endwhile change
		ri[n] = (ri[0] == 14) ? 1 : ri[0]; // wrap for straight detection with aces 
		si[n] = si[0]; 
		hi[n] = hi[0];
		
		int[] iv = {0,0,0,0,0,0,0,0};
		for (int i = 0; i < (n); i++) {
			iv[i] = ri[i]-ri[i+1];
		}

		// Scan for flushes, runs and pairings (obs except fl)
		int[] pr = new int[7];
		int[] sti = new int[7];
		int[] sto = new int[7];
		int[] fl = new int[7];
		for (int i = 0; i < (n); i++) {
			int pc = 0;
			for (int j = i+1; j < (n+1); j++) {
				if (ri[i] == ri[j]) {
					sto[j] = sto[i];
					sto[i] = 0;
					sti[j] = sti[i];
					sti[i] = 0;
					pc++;
					pr[i]++;
				}
				else {
					sto[i] += (ri[i] == (ri[j+pc]+1)) ? 1 : 0;
					sti[i] += (ri[i] == (ri[j+pc]+2)) ? 1 : 0;
				}
				if (j < (n+2)) {
					fl[i] += (si[i] == si[j]) ? 1 : 0;
				}
			}
		}
		
		// hi card +/- in hole v=1
		int df = (n < 4) ? 2 : 1;
		int dc = (n < 4) ? 1 : 2;
		x = hi[0] ? 1*df : -1*df;
		
		int gVis1 = 2;
		int gVis2 = 4;
		int gVos1 = 4;
		int gVos2 = 8;
		int gVf1 = 164;
		int gVf2 = 32;
		int gHc = 3; // high card in hole
		
		// pairings
		int pc = 0;
		int tc = 0;
		int qc = 0;
		int[] fp = {0,0,0,0,0,0,0,0};
		for (int i = 0; i < (n+2); i++) {
			if (iv[i] == 0) {
				if ((i > 0) && (iv[i-1] == 0)) {
					if ((i > 1) && (iv[i-2] == 0)) {
						qc++;
						tc--;
						fp[i] = 55;
						fp[i-1] = 55;
						fp[i-2] = 55;
					}
					else {
						tc++;
						pc--;
						fp[i] = 44;
						fp[i-1] = 44;
					}
				}
				else {
					pc++;
					fp[i] = 33;
				}
			}
		}
		
		// runs
		int[] soc = {0,0};
		int sco = 0;
		int[] sic = {0,0};
		int sci = 0;
		int[] f1 = {0,0,0,0,0,0,0,0};
		int[] f2 = {0,0,0,0,0,0,0,0};
		for (int i = 0; i < (n+2); i++) {
			if (iv[i] == 1) {
				if ((ri[i] == 14) || (ri[i] == 2)) {
					sic[sci]++;
				}
				else {
					soc[sco]++;
				}
				f1[i] = 22;
			}
			else if (soc[0] > 0) {
				sco = 1;
			}
			if (iv[i] == 2) {
				sic[sci]++;
				f2[i] = 11;
			}
			else if (sic[0] > 0) {
				sci = 1;
			}
		}
		
		int gVpr2h = 4;		// pair	in hole
		int gVpr1h = 2;		// pair 1 in hole
		int gVpr0h = -4;	// pair 0 in hole	
		int gVtr2h = 4;		// trips 2 in hole
		int gVtr1h = 2;		// trips 1 in hole
		int gVtr0h = -4;	// trips 0 in hole
		int gVqd2h = 4;		// quads 2 in hole
		int gVqd1h = 2;		// quads 1 in hole
		int gVqd0h = -4;	// quads 0 in hole
		int gVso2h = 20;	// outside straight draw 2 in hole
		int gVso1h = 10;	// outside straight draw 1 in hole
		int gVso0h = -20;	// outside straight draw 0 in hole
		int gVsi2h = 10;	// inside straight draw 2 in hole
		int gVsi1h = 5;		// inside straight draw 1 in hole
		int gVsi0h = -10;	// inside straight draw 0 in hole
		int gVsb = -10;		// the card outside straight on board only
		int hc = 0;
		int fl1 = -1;
		int fl2 = -1;
		for (int i = 0; i < (n+2); i++) {
 			if (fp[i] == 33) {
				x += (hi[i] ? (hi[i+1] ? gVpr2h : gVpr1h) : (hi[i+1] ? gVpr1h : gVpr0h)); 
			}
			if (fp[i] == 44) {
				x += (hi[i] ? (hi[i+1] ? gVtr2h : gVtr1h) : (hi[i+1] ? gVtr1h : gVpr0h)); 
			}
			if (fp[i] == 55) {
				x += (hi[i] ? (hi[i+1] ? gVqd2h : gVqd1h) : (hi[i+1] ? gVqd1h : gVqd0h)); 
			}
			if (f1[i] == 22) {
				hc += hi[i] ? 1 : 0; 
			}
			if (f2[i] == 11) {
				hc += hi[i] ? 1 : 0; 
			}
		}
		for (int i = 0; i < (n+2); i++) {
			if (fl[i] > 0) {
				if (fl1 < 0) {
					fl1 = i;
					i += fl[i];
				}
				else {
					fl2 = i;
				}
			}
		}
		
		if (((2*soc[0])) >= 6) {
			x += (hc > 1) ? gVso2h : (hc == 1) ? gVso1h : gVso0h; 
		}
		if ((sic[0] + (2*soc[0])) == 5) {
			x += (hc > 1) ? gVsi2h : (hc == 1) ? gVsi1h : gVsi0h; 
		}
		else if (((sic[0] + (2*soc[0])) == 4) && (hc == 0)) {
			x += gVsb; 
		}
		if (fl1 >= 0) {
		}
/*		
		int hp = 0;
		int ht = 0;
		int hq = 0;
		int hs = 0;
		int hf = 0;
		
		
		// If draws
		if (n < 4) { // not final

			for (int i = 0; i < (n+2); i++) {
				int bc = hi[i] ? hi[i+1] ? 2 : 1 : hi[i+1] ? 1 : 0;
				
				// straight inside draw x_xx 0/1/2 in hole v=2
				int stx = sti[i] + sto[i+1] + sto[i+2] + ((i > 0) ? sto[i-1] : 0) + ((i > 1) ? sto[i-2] : 0);
				if (stx > dc) {
					x += ((bc == 2) ? df*gVis2 : ((bc == 1) ? df*gVis1 : -df*gVis2));
					x += ((r1 == ri[i]) || (r2 == ri[i])) ? gHc : -gHc;
				}
				
				// straight inside draw xx_x 0/1/2 in hole v=2
				if (i > 0) {
					if ((sti[i]+sto[i-1]) > dc) {
						x += ((bc == 2) ? df*gVis2 : ((bc == 1) ? df*gVis1 : -df*gVis2));
						x += ((r1 == ri[i-1]) || (r2 == ri[i-1])) ? gHc : -gHc;
					}
				}
				
				// straight inside draw xxx_x 0/1/2 in hole v=2
				if (i > 1) {
					if ((sti[i]+sto[i-2]) > dc) {
						x += ((bc == 2) ? df*gVis2 : ((bc == 1) ? df*gVis1 : -df*gVis2));
						x += ((r1 == ri[i-1]) || (r2 == ri[i-1])) ? gHc : -gHc;
					}
				}
				
				// straight outside draw 0/1/2 in hole v=2
				if (sti[i] > dc) {
					x += ((bc == 2) ? df*gVos2 : ((bc == 1) ? df*gVos1 : -df&gVos2));
				}
				
				// flush draw 0/1/2 in hole v=2
				if (ff[i] > (dc+1)) {
					x += ((bc == 2) ? df*gVf2 : ((bc == 1) ? df*gVf1 : -df&gVf2));
				}
			}
		}
			
			
/*			
			
			
			
			
			
			
			// straight inside draw 0/1/2 in hole
			x += ((sch == 0) ? -df*2 : (sch == 1) ? df*2: df*4);
		}
		
		// straight outside draw 0/1/2 in hole
		if ((sc == 3) && (n < 4)) { // not final
			if (n < 3) { // flop
				x += ((sch == 0) ? -4 : (sch == 1) ? 4: 8);
			}
			else { // turn
				x += ((sch == 0) ? -2 : (sch == 1) ? 2: 4);
			}
		}
		
		// flush draw 0/1/2 in hole
		if ((fc == 6) && (n < 4)) { // not final
			if (n < 3) { // flop
				boolean fc_inhole = ((hr == r1) || (hr == r2));
				x += ((sch == 0) ? -4 : (sch == 1) ? 4: 8);
			}
			else { // turn
				x += ((sch == 0) ? -2 : (sch == 1) ? 2: 4);
			}
		}
		
		
		
		// straight flush draw not in hole
		// one pair not with hole
		// two pair not with hole
		// trips not with hole
		// straight not with hole
		// flush not with hole
		// house not with hole
		// straight flush not with hole
		// hi card in hole
		// straight draw in hole
		// flush draw in hole
		// straight flush draw in hole
		// one pair with hole, low kicker
		// one pair with hole, high kicker
		// one pair in hole, low kicker
		// one pair in hole, high kicker
		// two pair bottom one with hole, low kicker
		// two pair bottom one with hole, high kicker
		// two pair top one with hole, low kicker
		// two pair top one with hole, high kicker
		// two pair both with hole, low kicker
		// two pair both with hole, high kicker
		// trips with one hole
		// trips with two hole
		// straight with one hole under
		// straight with two hole under
		// straight with one hole over
		// straight with two hole over
		// flush with one hole low
		// flush with one hole high
		// flush with two hole low
		// flush with two hole high
		// house with one hole
		// house with two hole
		// straight flush with one hole under
		// straight flush with one hole over
		// straight flush with two hole under
		// straight flush with two hole over
		
		
		
	
		
		// If hole paired
		if (gHole1.get_rank() == gHole2.get_rank()) {
			for (int i = 0; i < n; i++) {
				if (H.gBoard[i].get_rank() == gHole1.get_rank()) {
					ret = ((ret == 0.9) ? 1.0 : 0.9);	// quads or trips
				}
				
				// If higher pair on board
				if (pc > 0);
			}
		
		}
			// Find trips or quads
		// Else
			// Find pairs to hole
			// Find straights to hole
			// Find flushes to hole
*/		
		return ret; // call
	}
	
	private double get_post_turn(double see, int limpers, int inAfter, int raiseCount) {
		// TODO Auto-generated method stub
		return see; // call
	}

	private double get_post_river(double see, int limpers, int inAfter, int raiseCount) {
		// TODO Auto-generated method stub
		return see; // call
	}
	
	double calc_pot_odds() {
		return 0.0;
	}
	
	void load_params(int id) throws IOException {
		gName = String.valueOf(id+1);
		String fName = String.format("Player%d.txt", id);
		try {
			FileReader fr = new FileReader(fName);
			BufferedReader in = new BufferedReader(fr);
			String line;
			/*
			# FIXED COLUMN FORMAT param at col 16
			stack           300                       # default stack
			buyin           1                         # 0=buy minimum/1=buy avg/2=buy max
			Hs              42507631                  # hole card matrix preference indices[8]
			Hv              0                         # hole card matrix indices variance
			Hc              0                         # hole card matrix indices initial choice
			Iv              0.0;                      # implied odds variance
			Sv              0.0;                      # showdown odds variance
			Al              0.0                       # lo aggressiveness
			Ah              0.0                       # hi aggressiveness
			Mood            0                         # mood index
			Av              0.0                       # aggressiveness variance
			R1_01           true                      # rule 1 applies in mood 1
			F_01            true                      # fault 3 applies
			R2_77           true                      # rule 77 applies in mood 2
			F_56            true                      # fault 56 applies
			 */
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				if (line.length() > 0) {
					String param = line.substring(16, 32).trim();
					switch(line.substring(0, 2).toLowerCase()) {
					case "bu":
						gBuyIn = Integer.valueOf(param);
						break;
					case "hc":
						gHcc = Integer.valueOf(param)%4;
						break;
					case "hs":
						for (int i = 0; i < kNUMMOODS; i++) {
							gHcs[i] = Integer.valueOf(param.substring(i, i+1));
						}
						break;
					case "mo":
						gMood = Integer.valueOf(param);
						break;
					case "f_":
						int fi = Integer.valueOf(line.substring(2, 4));
						gFaults[fi] = Integer.valueOf(param);
						break;
					case "c_":
						int ci = Integer.valueOf(line.substring(2, 4));
						gCheats[ci] = Integer.valueOf(param);
						break;
					case "r1":
					case "r2":
					case "r3":
					case "r4":
						int mi = Integer.valueOf(line.substring(1, 2));
						int ri = Integer.valueOf(line.substring(3, 5));
						gRules[mi][ri] = Integer.valueOf(param);
					break;
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(String.format("%s: File not found", fName));
		}
		
	}

	public void observe_reveal(int p, Card hole1, Card Hole2) {
		// TODO Auto-generated method stub
		
	}

	public void observe_post(int p, double post) {
		// TODO Auto-generated method stub
		
	}
	
}
