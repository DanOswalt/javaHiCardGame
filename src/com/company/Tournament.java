package com.company;
import java.util.*;

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
    int NUM_TABLES;
    String PLAYER_NAME;
    int[] BETS = {1, 2, 3, 5, 8, 12, 20, 35, 55, 90, 150, 240, 375, 625, 1000, 1600, 2500, 4000, 7000, 12000};
    Player[] players = {
            new Player(1, false, "Qued", 50),
            new Player(2,false, "Bood", 48),
            new Player(3, false, "Flek", 46),
            new Player(4, false, "Dave", 44),
            new Player(5,false, "Opal", 42),
            new Player(6,false, "Teroo", 40),
            new Player(7,false, "Hooll", 38),
            new Player(8,false, "Noel", 36),
            new Player(9,false, "Mukt", 34),
            new Player(10,false, "Ploki", 32),
            new Player(11,false, "Zelmo", 30),
            new Player(12,false, "Micah", 28),
            new Player(13, false, "Kijj", 26),
            new Player(14,false, "Xui", 24),
            new Player(15, false, "Yui", 22),
            new Player(16, false, "Vur", 20),
            new Player(17,false, "Eeb", 18),
            new Player(18,false, "Bux", 16),
            new Player(19,false, "Kloo", 14),
            new Player(20,false, "Thup", 12),
            new Player(21,false, "Norg", 10),
            new Player(22,false, "Qas", 8),
            new Player(23,false, "Morkin", 6),
            new Player(24,false, "Razu", 4),
            new Player(25,false, "Orsh", 2),
            new Player(26,false, "Glis", 0)
    };
    Player humanPlayer = new Player(0,true, "Dan", -1);

    ArrayList<Player> activePlayers = new ArrayList<>();
    ArrayList<Player> eliminatedPlayers = new ArrayList<>();
    ArrayList<Player> playersToBeReseated = new ArrayList<>();
    ArrayList<Integer> chipsForPlayersToBeReseated = new ArrayList<>();
    ArrayList<Table> tables = new ArrayList<>();

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
        NUM_TABLES = 3;
        PLAYER_NAME = "DAN";
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        String answer;

        // create empty tables
        for (int i = 0; i < NUM_TABLES; i++) {
            tables.add(new Table(i + 1));
        }

        // shuffle the players and slice off the top 5
        // append the human player
        // then shuffle again

        Collections.shuffle(Arrays.asList(players));

        for (int i = 0; i < (NUM_TABLES * 6) - 1; i++) {
            activePlayers.add(players[i]);
        }

        activePlayers.add(humanPlayer);

        Collections.shuffle(activePlayers);

        // seat the players
        for (int i = 0; i < tables.size(); i++) {
            for (int j = 0; j < 6; j++) {
                tables.get(i).seatPlayerInFirstEmptySeat(activePlayers.get(j + (i * 6)), INITIAL_STACK);
            }
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

            // clean up operations
            // tables report eliminated players. if none, or the game is actually over, do nothing.
            if (isPlaying) balanceTables();

            // pause execution after hand finishes to check out results
            System.out.println(" ");

            while (true) {
                System.out.println("  y to continue, l for leaderboard, t for tables, q to quit");
                answer = in.nextLine().trim().toLowerCase();

                if (answer.equals("y")) {
                    System.out.println(" ");
                    break;
                } else if (answer.equals("l")){
//                    displayLeaderboard();
                    System.out.println(" ");
                } else if (answer.equals("t")){
                    displayTables();
                    System.out.println(" ");
                } else if (answer.equals("q")){
                    System.exit(0);
                } else {
                    System.out.println("  Please make a selection [yltq]");
                }
            }
        } while (isPlaying);

        winner = activePlayers.get(0);

        System.out.println(" ");
        System.out.println("************************");
        System.out.println("* Winner: " + winner.name());
        System.out.println("************************");
        System.out.println(" ");
    }

    public void displayTables() {
        System.out.println(" ");
        for (int i = 0; i < tables.size(); i++) {
            Table thisTable = tables.get(i);
            thisTable.displayCurrentTableState();
        }
        System.out.println(" ");
    }

    public void playNextHand() {

        // update counters
        incrementHandNumber();
        if (handNumberInRound == 1) {
            incrementBetLevel();
        }
        displayTournamentState();

        for (int i = 0; i < tables.size(); i++) {
            Table thisTable = tables.get(i);
            thisTable.moveBlind();
            thisTable.displayCurrentTableState();

            // play the hand
            tr = thisTable.playHand(currentBet);

            // check for eliminated players
            for (int j = 0; j < tr.eliminatedPlayerIds.size(); j++) {
                Player thisPlayer = getPlayerById(tr.eliminatedPlayerIds.get(j));

                System.out.println("  " + thisPlayer.name() + " eliminated");
                removeFromActivePlayersById(thisPlayer.id);
                eliminatedPlayers.add(thisPlayer);
            }
        }

        // if there is only one player left, end the game and show the winner message
        isPlaying = !(activePlayers.size() == 1);
    }

    // create a list of tables sorted by size
    // break down as many short tables as possible

    public void balanceTables() {

        // sort by table size
        Collections.sort(tables, new Comparator<Table>() {
            @Override
            public int compare(Table tableA, Table tableB) {
                Integer a = tableA.playersSeated;
                Integer b = tableB.playersSeated;

                return a.compareTo(b);
            }
        });

        // close some tables if possible
        while (canCloseATable()) closeATable();

        reseatPlayersFromClosedTables();

    }

    public boolean canCloseATable() {
        int capacity = tables.size() * 6;
        int diff = capacity - activePlayers.size();
        boolean closeATable = diff > 6;
        return closeATable;
    }

    public void closeATable() {
        // pop last table off arraylist
        Table closedTable = tables.remove(0);

        // remove all players and put them and their chips into arraylists
        for (int i = 0; i < 6; i++) {
            Seat thisSeat = closedTable.seats[i];
            if (!thisSeat.isEmpty) {
                playersToBeReseated.add(thisSeat.player);
                chipsForPlayersToBeReseated.add(thisSeat.chips);
            }
        }

        System.out.println("Closed table " + closedTable.getId());
    }

    public void reseatPlayersFromClosedTables() {
        int initialArraySize = playersToBeReseated.size();
        Player thisPlayer;
        Table thisTable;
        int theirChips;
        int tableIndex = 0;

        // will need to cycle back through tables if more players than tables
        for (int i = 0; i < initialArraySize; i++) {
            thisPlayer = playersToBeReseated.remove(0);
            theirChips = chipsForPlayersToBeReseated.remove(0);
            tableIndex = tableIndex == (tables.size() - 1) ? 0 : tableIndex + 1;

            thisTable = tables.get(tableIndex);

            // if working properly, this shouldn't ever try to seat someone at full table
            thisTable.seatPlayerInFirstEmptySeat(thisPlayer, theirChips);
        }
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
