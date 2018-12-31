package com.company;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Tournament {
    /*
       Tournament is the overall app controller.
       It will need to update the state of the tournament, and manage tables.

       [Attributes]

       Hand Number
       Blind Amount
       Starting Stack AMount
       Hands remaining until blind increase
       Number of players remaining
       Active Players array
       * It will have an array of players hard-coded in. Probably doesn't belong, but oh well.
       Status of tournament (Not started, playing, finished).
       Maintain a ordered array of chip-leaders.
       Maintain a ordered array of eliminated players.

       [Methods]

       Randomly assign players to table(s)
       Init new hand
       Update state
       Check if finished
       Remove player from table (when eliminated)
       Remove player from active players array

     */

    // these should be able to be set by user
    int INITIAL_STACK;
    int INITIAL_BLIND;
    int HANDS_PER_ROUND;
    int NUM_PLAYERS;
    String PLAYER_NAME;
    int[] BETS = {1, 2, 3, 5, 8, 12, 20, 35, 55, 90, 150, 240, 375, 625, 1000, 1600, 2500, 4000, 7000, 12000};
    Player[] players = {
            new Player(1, false, "Qued", 50),
            new Player(2,false, "Bood", 45),
            new Player(3, false, "Flek", 40),
            new Player(4, false, "Dave", 35),
            new Player(5,false, "Opal", 30),
            new Player(6,false, "Teroo", 25),
            new Player(7,false, "Hooll", 20),
            new Player(8,false, "Teroo", 15),
            new Player(9,false, "Mukt", 10),
            new Player(10,false, "Ploki", 5),
            new Player(11,false, "Zelmo", 0)
    };
    Player humanPlayer = new Player(0,true, "Dan", -1);

    ArrayList<Player> activePlayers = new ArrayList<>();
    ArrayList<Player> eliminatedPlayers = new ArrayList<>();

    // declare counters;
    int handNumber = 0;
    int handNumberInRound = 0;
    int betLevel= 0;
    int currentBet = 0;
    boolean isPlaying = false;
    Player winner;

    // one table

    Table table;
    TableReport tr;

    // One day, these could be initialized by user in constructor
    public Tournament() {
        INITIAL_STACK = 3;
        INITIAL_BLIND = 1;
        HANDS_PER_ROUND = 10;
        NUM_PLAYERS = 6;
        PLAYER_NAME = "DAN";
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        String answer;

        // create an empty table
        table = new Table(1);

        // seat the players

        // shuffle the players and slice off the top 5
        // append the human player
        // then shuffle again

        Collections.shuffle(Arrays.asList(players));

        for (int i = 0; i < 5; i++) {
            activePlayers.add(players[i]);
        }

        activePlayers.add(humanPlayer);

        Collections.shuffle(activePlayers);

        for (int i = 0; i < 6; i++) {
            table.putPlayerInSeatByIndex(activePlayers.get(i), i, INITIAL_STACK);
        }

        // show beginning message
        System.out.println("  Starting new game!");
        System.out.println(" ");
        isPlaying = true;

        /*
        *
        * main game loop
        *
         */

        do {
            // play hand
            playNextHand();

            // table reports eliminated players. if none, do nothing.

            // pause execution after hand finishes to check out results
            System.out.println(" ");
            System.out.println("  y to continue");

            while (true) {
                answer = in.nextLine().trim().toLowerCase();
                if (answer.equals("y")) {
                    System.out.println(" ");
                    break;
                } else {
                    System.out.println("Please hit y");
                }
            }
        } while (isPlaying);

        winner = activePlayers.get(0);

        System.out.println(" ");
        System.out.println("************************");
        System.out.println("* Winner: " + winner.name());
        System.out.println("************************");
        System.out.println("");



    }

    public void playNextHand() {

        // update counters
        incrementHandNumber();
        if (handNumberInRound == 1) {
            incrementBetLevel();
        }
        table.moveBlind();

        // display the current status
        displayTournamentState();
        table.displayCurrentTableState();

        // play the hand
        tr = table.playHand(currentBet);

        // check for eliminated players
        for (int i = 0; i < tr.eliminatedPlayerIds.size(); i++) {
            Player thisPlayer = getPlayerById(tr.eliminatedPlayerIds.get(i));

            System.out.println("  " + thisPlayer.name() + " eliminated");
            removeFromActivePlayersById(thisPlayer.id);
            eliminatedPlayers.add(thisPlayer);
        }

        // if there is only one player left, end the game and show the winner message
        isPlaying = !(activePlayers.size() == 1);
    }

    public void removeFromActivePlayersById (int playerId) {
        for (int i = 0; i < activePlayers.size(); i++) {
            Player thisPlayer = activePlayers.get(i);
            if (thisPlayer.id == playerId) {
                activePlayers.remove(i);
            }
        }
    }

    public void displayTournamentState() {
        System.out.println("==================");
        System.out.println(" ");
        System.out.println("  Hand: " + handNumber);
        System.out.println("  Bet: " + currentBet);
        System.out.println("  Blind: Seat " + table.blindSeat);
        System.out.println("  Remaining hands in level: " + handNumberInRound + "/" + HANDS_PER_ROUND);
        System.out.println("  Players remaining: " + activePlayers.size());
        System.out.println(" ");
    }

    public void incrementHandNumber() {
        handNumber++;
        handNumberInRound = handNumber % HANDS_PER_ROUND;
    }

    public void incrementBetLevel() {
        betLevel++;
        currentBet = BETS[betLevel - 1];
    }

    public Player getPlayerById(int playerId) {
        for (int i = 0; i < activePlayers.size(); i++) {
            Player thisPlayer = activePlayers.get(i);
            if (thisPlayer.id == playerId) {
                return thisPlayer;
            }
        }
        return null;
    }
}
