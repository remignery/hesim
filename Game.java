package holdem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Game {

	// Constants
	public int kShortStack = 20;					// # BBs below which you are short stacked
	public int kLargeStack = 50;					// # BBs above which you are deep stacked
	public int kStackCommit = 10;					// Stack divisor for committed threshold

	// Loaded parameters
	public Double gAnte = 0.0;
	public Double gBigBlind = 0.0;
	public Integer gRepCount = 0;
	public Integer gSeatsNum = 0;
	public Long gSeed = 0L;
	public Double gSmallBlind = 0.0;
	public Double gStackMin = 0.0;
	public Double gStackMax = 0.0;
	public Double gRakeCap = 0.0;
	public boolean gRakeApplied = false;
	public Double gDeadDropRake = 0.0;
	public Double gFixedRake = 0.0;
	public Double gRakeMin = 0.0;
	public Boolean gRakeNoFlop = false;
	public Double gRakePC = 0.0;
	public Double gStartStack = 0.0;
	public boolean gReseatBusted = false;
	public boolean gTipping = false;
	public boolean gTournament = false;
	private boolean gTournamentRebuysWhenBusted = false;
	private int gTournamentHandsNoRebuyAfter = 20;
	private double gTournamentRebuyAmount = 0.0;
	private int gTournamentRebuyBlinds = 100;
	private int gTournamentHandsBeforeAnte = 20;
	private Double gTournamentStartingAnte = 1.0;
	private int gTournamentHandsBetweenAnteBumps = 10;
	private int gTournamentHandsBeforeBlindBumps = 40;
	private int gTournamentHandsBetweenBlindBumps = 20;
	
	// Game globals
	public Player[] gPlayers = new Player[10];
	public boolean[] gInGame = new boolean[10];
	public int gInGameNum = 10;
	public double gRakeTotal = 0.0;
	public int gButton = 0;
	public Random gRnd;
	
	// Player stats
	public int[] opens = {0,0,0,0,0,0,0,0,0,0};
	public int[] sees_flop = {0,0,0,0,0,0,0,0,0,0};
	public int[] limps = {0,0,0,0,0,0,0,0,0,0};
	public int[] check_raises = {0,0,0,0,0,0,0,0,0,0};
	public int[] all_ins = {0,0,0,0,0,0,0,0,0,0};
	public int[] calls = {0,0,0,0,0,0,0,0,0,0};
	public int[] raises = {0,0,0,0,0,0,0,0,0,0};

	
	// matrix templates
	public int[][][] gHct = {
		{{1,1,2,2,3,7,7,7,8,6,8,8,6},{2,1,2,3,7,7,7,0,0,0,0,0,0},{3,4,1,3,8,7,0,0,0,0,0,0,0},
		 {3,4,4,1,3,8,0,0,0,0,0,0,0},{4,7,8,7,2,5,8,0,0,0,0,0,0},{6,7,0,0,0,2,6,8,0,0,0,0,0},
		 {6,0,0,0,0,0,3,6,8,0,0,0,0},{6,0,0,0,0,0,0,3,6,8,0,0,0},{7,0,0,0,0,0,0,4,8,0,0,0,0},
		 {7,0,0,0,0,0,0,0,0,4,0,0,9},{8,0,0,0,9,0,0,0,0,0,5,0,0},{8,0,0,0,0,0,0,0,0,0,0,5,9},
		 {9,0,0,0,0,0,0,0,0,0,0,9,5}}, // al inne
		{{1,1,1,2,2,3,3,4,4,5,8,8,5},{1,1,2,2,3,3,4,6,6,7,8,6,6},{2,2,1,2,4,5,6,8,6,6,7,7,7},
		 {3,3,4,1,3,6,8,6,7,7,7,0,0},{4,7,7,7,1,4,7,6,7,4,0,0,0},{4,7,7,8,5,2,5,7,7,0,0,0,0},
		 {5,6,8,6,6,7,3,5,7,7,0,0,0},{6,8,0,0,0,0,7,3,5,6,7,0,0},{6,0,0,0,0,0,0,7,4,5,6,0,0},
		 {6,0,0,0,0,0,0,0,8,4,5,0,0},{7,0,0,0,9,0,0,0,0,0,5,5,0},{7,0,0,0,0,0,0,0,0,0,0,5,0},
		 {9,0,0,0,0,0,0,0,0,0,0,0,5}}, // amanda
		{{1,1,2,2,3,5,5,6,6,6,6,6,6},{2,1,3,4,4,6,6,7,8,8,8,8,8},{3,5,1,4,5,6,7,8,0,0,0,0,0},
		 {5,6,6,1,5,6,7,8,0,0,0,0,0},{6,6,6,6,2,6,7,8,0,0,0,0,0},{8,8,0,0,0,3,6,7,8,0,0,0,0},
		 {8,0,0,0,0,0,4,6,7,8,0,0,0},{0,0,0,0,0,0,0,5,6,7,8,0,0},{0,0,0,0,0,0,0,6,6,8,0,0,0},
		 {0,0,0,0,0,0,0,0,0,6,7,8,0},{0,0,0,0,0,0,0,0,0,0,6,7,8},{0,0,0,0,0,0,0,0,0,0,0,6,7},
		 {0,0,0,0,0,0,0,0,0,0,0,0,6}}, // angus
		{{1,1,2,2,2,2,3,4,6,5,6,6,5},{2,1,2,3,3,5,6,6,6,6,7,7,7},{3,3,1,2,4,6,6,7,7,8,8,8,8},
		 {3,4,3,1,3,6,7,8,8,8,8,8,8},{4,5,6,3,2,4,6,8,0,0,0,0,0},{4,6,7,6,5,3,5,8,0,0,0,0,0},
		 {5,7,8,8,8,8,3,5,8,0,0,0,0},{6,8,8,0,0,0,8,4,5,8,0,0,0},{6,8,0,0,0,0,8,4,6,0,0,0,0},
		 {6,8,0,0,0,0,0,0,0,4,6,0,0},{6,8,0,0,9,0,0,0,0,0,5,7,0},{6,8,0,0,0,0,0,0,0,0,0,5,0},
		 {7,8,0,0,0,0,0,0,0,0,0,0,5}}, // chuck reese
		{{1,1,2,2,3,4,6,6,6,6,6,6,6},{2,1,2,3,3,4,4,6,6,6,6,6,6},{3,3,1,2,3,3,4,6,6,6,6,6,6},
		 {3,4,3,1,2,3,3,4,6,6,6,6,6},{4,4,4,3,2,2,3,4,4,6,6,6,6},{7,7,5,3,3,3,2,3,4,4,0,0,0},
		 {7,6,6,6,6,6,4,2,3,4,4,6,6},{7,7,7,7,7,6,6,4,2,3,4,6,6},{7,8,0,8,0,7,6,6,5,2,4,6,6},
		 {7,8,8,8,8,7,7,6,6,5,4,6,6},{7,8,8,8,0,8,7,7,6,6,5,4,6},{7,8,0,8,8,8,0,7,7,6,6,5,6},
		 {7,8,8,8,0,8,0,0,0,7,8,0,5}}, // daniel xn
		{{1,1,2,3,3,5,5,6,6,6,6,6,6},{2,1,3,4,4,6,6,6,6,6,0,6,0},{3,5,1,4,5,6,6,6,6,6,6,6,6},
		 {5,6,6,1,5,6,6,6,0,6,0,6,0},{6,6,6,6,1,6,6,6,6,6,6,6,6},{8,8,8,8,6,3,6,6,0,6,0,6,0},
		 {8,8,8,0,0,6,4,6,6,6,6,6,6},{8,8,0,0,0,0,6,5,6,6,0,6,0},{8,0,0,0,0,0,0,0,6,6,6,6,6},
		 {0,0,0,0,0,0,0,0,0,6,6,6,0},{0,0,0,0,9,0,0,0,0,0,7,6,6},{0,0,0,0,0,0,0,0,0,0,0,7,0},
		 {0,0,0,0,0,0,0,0,0,0,0,0,7}}, // evil lynn
		{{1,1,1,1,2,3,3,4,5,5,5,5,5},{1,1,1,2,2,3,3,5,6,6,6,7,7},{3,2,1,2,2,3,5,5,6,6,6,7,7},
		 {3,3,3,1,2,3,5,5,6,6,6,7,7},{4,4,4,3,1,3,2,3,6,6,6,7,7},{5,5,5,5,5,1,3,4,5,5,6,7,7},
		 {5,6,6,6,5,5,3,3,4,5,5,7,7},{6,6,6,6,6,5,5,3,3,4,5,7,7},{6,7,7,7,6,6,5,5,3,3,4,5,6},
		 {7,8,8,8,7,6,6,6,6,3,3,4,6},{7,8,8,8,7,7,7,7,6,6,3,3,6},{7,8,8,8,7,7,7,7,7,7,6,3,3},
		 {7,8,8,8,0,0,0,0,0,0,0,0,3}}, // gus xensen
		{{1,1,2,2,3,6,7,7,8,6,7,7,6},{2,1,2,3,4,7,7,6,6,6,6,6,6},{3,4,1,3,8,6,6,6,6,0,0,0,0},
		 {3,4,6,1,3,6,6,6,0,0,0,0,0},{4,7,8,7,2,6,6,6,0,0,0,0,0},{8,7,6,6,6,3,6,6,0,0,0,0,0},
		 {8,6,6,0,0,6,4,6,6,0,0,0,0},{6,6,0,0,0,0,6,4,6,6,0,0,0},{6,6,0,0,0,0,0,5,8,6,0,0,0},
		 {6,6,0,0,0,0,0,0,0,5,6,0,0},{6,0,0,0,9,0,0,0,0,0,5,0,0},{6,0,0,0,0,0,0,0,0,0,0,5,0},
		 {0,0,0,0,0,0,0,0,0,0,0,0,5}}}; // harmony

	
	Game(int gameId) throws IOException {
		
		String forceLine = "";
		
		// Load params
		load_params(gameId);
		
		// Seat the players
		gInGameNum = 0;
		for (int i = 0; i < gSeatsNum; i++) {
			gInGame[i] = false;
			gPlayers[i] = new Player(this, i);
			if (gStartStack > 0.0) {
				gPlayers[i].set_stack(gStartStack);
			}
			else {
				gPlayers[i].set_stack(gPlayers[i].get_buyIn(gStackMin, gStackMax));
			}
			if (gPlayers[i].get_stack() > 0.0) {
				gInGame[i] = true;
				gInGameNum++;
			}
		}
		
		// Create deck
		Deck theDeck = new Deck();

		// Initialize game
		gRnd = (gSeed > 0) ? new Random(gSeed) : new Random();
		if (gRepCount < 1) {
			gRepCount = ((gRepCount < 0) ? -gRepCount : 1);
			System.out.printf("   %s\n", forceLine);
		}
		gButton = gRnd.nextInt(gInGameNum);
		
		// For as many hands as ordered
		if (gStartStack > 0.0) {
			//System.out.printf("%d players with $%.0f each for %d hands, blinds are $%.0f/%.0f and the ante is $%.0f.\n", 
			System.out.printf("%d players with $%.0f each for %d hands, blinds are $%.0f/%.0f and the ante is $%.0f.        see/post/bet=pot\n", 
				gInGameNum, gStartStack, gRepCount, gBigBlind, gSmallBlind, gAnte);
		}
		else {
			System.out.printf("%d players for %d hands, blinds are $%.0f/%.0f and the ante is $%.0f.\n", 
				gInGameNum, gStartStack, gRepCount, gBigBlind, gSmallBlind, gAnte);
			double total = 0.0;
			for (int i = 0; i < gSeatsNum; i++) {
				if (gInGame[i]) {
					System.out.printf("Player %d has $%.2f...\n", gPlayers[i].getName(), gPlayers[i].get_stack());
				}
				total += gPlayers[i].get_stack();
			}
			System.out.printf("Total on the table $%.2f.\n", total);
			
		}
		if (gRakeApplied) {
			System.out.printf("A %s rake of %s applies to each hand%s%s%s.\n",
				((gFixedRake > 0.0) ? "fixed" : ((gRakePC > 0.0) ? "percentage" : ((gDeadDropRake > 0.0) ? "dead drop" : ""))), 
				((gFixedRake > 0.0) ? String.format("$%.2f", gFixedRake) : ((gRakePC > 0.0) ? String.format("%.1f", gRakePC)
						: ((gDeadDropRake > 0.0) ? String.format("$%.2f", gDeadDropRake) : ""))),
				((gRakeMin > 0.0) ? String.format(", a minimum of $%.2f", gRakeMin) : ""),
				((gRakeCap > 0.0) ? String.format(", capped at $%.2f", gRakeCap) : ""),
				((gRakeNoFlop) ? ", no rake if no flop" : ""));
		}
		for (int handCount = 1; handCount <= gRepCount; handCount++) { //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
			
			// Play a hand **********************************
			Hand theHand = new Hand(this, theDeck, handCount);
			
			
			// Process busted players
			gInGameNum = 0;
			for (int i = 0; i < gSeatsNum; i++) {
				gInGame[i] = false;
				if (gPlayers[i].get_stack() > 0.0) {
					gInGame[i] = true;
					gInGameNum ++;
				}
				else {
					if (gTournament) {
						if (gTournamentRebuysWhenBusted) {
							if (handCount <= gTournamentHandsNoRebuyAfter) {
								gPlayers[i].xfer(gTournamentRebuyAmount);
								gPlayers[i].xfer(gTournamentRebuyBlinds*gBigBlind);
							}
						}
					}
					else if (gReseatBusted) {
						gInGame[i] = seat_player(i);
					}
				}
			}
			if (gInGameNum < 2) {
				
				// We have a winner!
				break;
			}
			
			// If tournament
			if (gTournament) {
				if (handCount == gTournamentHandsBeforeAnte) {
					gAnte =  gTournamentStartingAnte;
				}
				if ((gAnte > 0.0) && ((handCount%gTournamentHandsBetweenAnteBumps) == 0)) {
					gAnte += gTournamentStartingAnte;
				}
				if ((handCount >= gTournamentHandsBeforeBlindBumps)
						&& ((handCount%gTournamentHandsBetweenBlindBumps) == 0)) {
					gBigBlind *= 2;
					gSmallBlind *= 2;
				}
			// Process rebuys TBD
			// Update blinds and ante TBD
			}
			
			// Log hand
			System.out.println(theHand.gHandLog);
			if (theHand.gHandLog.contains("*")) {
				double total = 0.0;
				for (int i = 0; i < gSeatsNum; i++) {
					if (gInGame[i]) {
						System.out.printf("%s:=%.0f ", 
								gPlayers[i].getName(), gPlayers[i].get_stack());
						total += gPlayers[i].get_stack();
					}
				}
				System.out.printf("total stakes=%.0f\n", total);
			}
			
			// Advance the button
	 		do {
	 			gButton = (++gButton)%gSeatsNum;
	 		} while (gInGame[gButton] == false);
			
		} // endfor handcount ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
		
		double total = 0.0;
		for (int i = 0; i < gSeatsNum; i++) {
			System.out.printf("  Player %s has $%.2f.\n", gPlayers[i].getName(), gPlayers[i].get_stack());
			total += gPlayers[i].get_stack();
		}
		System.out.printf("Total on the table $%.2f.\n", total);
		if (gRakeApplied) {
			System.out.printf("The house won $%.2f.\n", gRakeTotal );
		}

	}

	boolean seat_player(int i) throws NumberFormatException, IOException {
		gInGame[i] = false; // TBE
		gPlayers[i] = new Player(this, i);
		if (gStartStack > 0.0) {
			gPlayers[i].set_stack(gStartStack);
		}
		else {
			gPlayers[i].set_stack(gPlayers[i].get_buyIn(gStackMin, gStackMax));
		}
		if (gPlayers[i].get_stack() > 0.0) {
			gInGame[i] = true;
			gInGameNum++;
		}
		return gInGame[i];
	}
	
	void load_params(int gameId) throws IOException {
		String id = (gameId > 0) ? String.valueOf(gameId) : "";
		try {
			FileReader fr = new FileReader(String.format("initHoldem.txt%s", id));
			BufferedReader in = new BufferedReader(fr);
			String line;
			/*
			# FIXED COLUMN FORMAT param at col 16
			_ante           0                         # starting ante
			_bigblind       2                         # starting big blind
			_hands          200                       # 0 to use force deal, negative to test hand
			_players        10                        # number of players
			_rseed          1234567                   # 0 for time random
			_smallblind     1                         # starting small blind
			ka              false                     # Rake applied
			kc              10.0                      # Rake cap
			kd              0.0                       # Dead drop rake
			kf              0.0                       # Fixed rake
			km              0.0                       # Rake min
			kn              true                      # Rake no flop
			kp              0.0                       # Rake %
			sstack          300.0                     # starting stack forced size
			smin            100.0                     # starting stack min size
			smax            300.0                     # starting stack max size
			*/
			boolean xx = false;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				if (line.length() > 0) {
					String param = line.substring(16, 32).trim();
					switch(line.substring(0, 2).toLowerCase()) {
					case "_a":
						gAnte = Double.valueOf(param);
						break;
					case "_b":
						gBigBlind = Double.valueOf(param);
						break;
					case "_h":
						gRepCount = Integer.valueOf(param);
						break;
					case "_p":
						gSeatsNum = Integer.valueOf(param);
						break;
					case "_r":
						gSeed = Long.valueOf(param);
						break;
					case "_s":
						gSmallBlind = Double.valueOf(param);
						break;
					case "sm":
						gStackMin = Double.valueOf(param);
						break;
					case "ss":
						gStartStack  = Double.valueOf(param);
						break;
					case "sx":
						gStackMax = Double.valueOf(param);
						break;
					case "ka":
						gRakeApplied = Boolean.valueOf(param);
						break;
					case "kc":
						gRakeCap = Double.valueOf(param);
						break;
					case "kd":
						gDeadDropRake = Double.valueOf(param);
						break;
					case "kf":
						gFixedRake = Double.valueOf(param);
						break;
					case "km":
						gRakeMin = Double.valueOf(param);
						break;
					case "kn":
						gRakeNoFlop = Boolean.valueOf(param);
						break;
					case "kp":
						gRakePC = Double.valueOf(param);
						break;
					case "re" :
						gReseatBusted = Boolean.valueOf(param);
						break;
					case "ti" :
						gTipping = Boolean.valueOf(param);
						break;
					case "to" :
						gTournament = Boolean.valueOf(param);
						break;
					case "t_" :
						switch(line.substring(2, 4).toLowerCase()) {
						case "to" :
							gTournament = Boolean.valueOf(param);
							break;
						case "rb" :
							gTournamentRebuysWhenBusted = Boolean.valueOf(param);
							break;
						case "hr" :
							gTournamentHandsNoRebuyAfter = Integer.valueOf(param);
							break;
						case "ra" :
							gTournamentRebuyAmount = Double.valueOf(param);
							break;
						case "br" :
							gTournamentRebuyBlinds = Integer.valueOf(param);
							break;
						case "ha" :
							gTournamentHandsBeforeAnte = Integer.valueOf(param);
							break;
						case "sa" :
							gTournamentStartingAnte = Double.valueOf(param);
							break;
						case "ab" :
							gTournamentHandsBetweenAnteBumps = Integer.valueOf(param);
							break;
						case "bb" :
							gTournamentHandsBeforeBlindBumps = Integer.valueOf(param);
							break;
						case "hb" :
							gTournamentHandsBetweenBlindBumps = Integer.valueOf(param);
							break;
						} // endcase
					case "xx" :
						xx = true;
						break;
					} // endcase
					if (xx) {
						break;
					}
				}
			} // endwhile
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Disappeared");
		}
	}
} // endclass
