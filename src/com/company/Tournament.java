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
    int[] BETS = {1, 2, 3, 4, 6, 8, 10, 13, 16, 20, 25, 32, 40, 50, 65, 90, 120, 160, 220, 300, 400, 550, 750, 1000, 1400, 2000, 3000, 5000, 8000, 12000, 18000, 26000, 40000, 64000, 100000};
    Player[] rivals = {
            new Player(1, false, "-Qued", 50),
            new Player(2,false, "-Bood", 48),
            new Player(3, false, "-Flek", 46),
            new Player(4, false, "-Dave", 44),
            new Player(5,false, "-Opal", 42),
            new Player(6,false, "-Teroo", 40),
            new Player(7,false, "-Hooll", 38),
            new Player(8,false, "-Noel", 36),
            new Player(9,false, "-Mukt", 34),
            new Player(10,false, "-Ploki", 32),
            new Player(11,false, "-Zelmo", 30),
            new Player(12,false, "-Micah", 28),
            new Player(13, false, "-Kijj", 26),
            new Player(14,false, "-Xui", 24),
            new Player(15, false, "-Yui", 22),
            new Player(16, false, "-Vur", 20),
            new Player(17,false, "-Eeb", 18),
            new Player(18,false, "-Bux", 16),
            new Player(19,false, "-Kloo", 14),
            new Player(20,false, "-Thup", 12),
            new Player(21,false, "-Norg", 10),
            new Player(22,false, "-Qas", 8),
            new Player(23,false, "-Morkin", 6),
            new Player(24,false, "-Razu", 4),
            new Player(25,false, "-Orsh", 2),
            new Player(26,false, "-Glis", 0),
            new Player(27, false, "-Charf", 50),
            new Player(28,false, "-Nusg", 48),
            new Player(29, false, "-Ams", 46),
            new Player(30, false, "-Stubb", 44),
            new Player(31,false, "-Norsh", 42),
            new Player(32,false, "-Jamblo", 40),
            new Player(33,false, "-Nux", 38),
            new Player(34,false, "-Espi", 36),
            new Player(35,false, "-Vorp", 34),
            new Player(36,false, "-Chup", 32),
            new Player(37,false, "-Meg", 30),
            new Player(38,false, "-Zmike", 28),
            new Player(39, false, "-Sloov", 26),
            new Player(40,false, "-Duxi", 24),
            new Player(41, false, "-Alpgi", 22),
            new Player(42, false, "-Yap", 20),
            new Player(43,false, "-Snuv", 18),
            new Player(44,false, "-Quibl", 16),
            new Player(45,false, "-Kluo", 14),
            new Player(46,false, "-Bovvr", 12),
            new Player(47,false, "-Unge", 10),
            new Player(48,false, "-Wert", 8),
            new Player(49,false, "-Uif", 6),
            new Player(50,false, "-Omzi", 4),
            new Player(51,false, "-Golop", 2),
            new Player(52,false, "-Erth", 0),
            new Player(53, false, "-Zizor", 50),
            new Player(54,false, "-Uchu", 40),
            new Player(55, false, "-Izbir", 30),
            new Player(56, false, "-Nagl", 20),
            new Player(57,false, "-Hujj", 10),
            new Player(58,false, "-Glau", 0),
            new Player(59,false, "-Xaeiou", 30)
    };
    Player humanPlayer = new Player(0,true, "-Dan", -1);

    ArrayList<Player> activePlayers = new ArrayList<>();
    ArrayList<Player> eliminatedPlayers = new ArrayList<>();
    ArrayList<Player> playersToBeReseated = new ArrayList<>();
    ArrayList<Integer> chipsForPlayersToBeReseated = new ArrayList<>();
    ArrayList<NameAndStack> leaderboard = new ArrayList<>();
    ArrayList<Table> tables = new ArrayList<>();

    // declare counters;
    int handNumber = 0;
    int handNumberInRound = 0;
    int betLevel= 0;
    int currentBet = 0;
    boolean isPlaying = false;
    Player winner;

    // One day, these could be initialized by user in constructor
    public Tournament() {
        INITIAL_STACK = 5;
        INITIAL_BLIND = 1;
        HANDS_PER_ROUND = 20;
        NUM_TABLES = 200;
        PLAYER_NAME = "DAN";
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        String answer;
        int totalPlayers = NUM_TABLES * 6;
        int totalRivals = 59;
        totalRivals = totalRivals > rivals.length ? rivals.length : totalRivals;
        int totalRandos = totalPlayers - totalRivals - 1;

        System.out.printf("players %d rivals %d randos %d", totalPlayers, totalRivals, totalRandos);

        // create empty tables
        for (int i = 0; i < NUM_TABLES; i++) {
            tables.add(new Table(i + 1));
        }

        // shuffle the players and slice off the top 5
        // append the human player
        // then shuffle again

        // shuffle the rivals list
        Collections.shuffle(Arrays.asList(rivals));

        // first, add in human
        activePlayers.add(humanPlayer);

        // second, add in rivals
        for (int i = 0; i < totalRivals; i++) {
          activePlayers.add(rivals[i]);
        }

        // third, add in randos
        for (int i = 0; i < totalRandos; i++) {
            activePlayers.add(new Player(i + 1 + 60));
        }

        // then shuffle the whole thing
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
                System.out.println("  y to continue, l for leaderboard, t for tables, f to set featured table, q to quit");
                answer = in.nextLine().trim().toLowerCase();

                if (answer.equals("y")) {
                    System.out.println(" ");
                    break;
                } else if (answer.equals("l")){
                    displayLeaderboard();
                    System.out.println(" ");
                } else if (answer.equals("t")){
                    displayTables();
                    System.out.println(" ");
                } else if (answer.equals("f")){
                    setFeaturedTable();
                    System.out.println(" ");
                } else if (answer.equals("q")){
                    System.exit(0);
                } else {
                    System.out.println("  Please make a selection [yltfq]");
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

    public void displayLeaderboard() {

        int size = leaderboard.size() < 10 ? leaderboard.size() : 10;
        int rivalCounter = 10;

        Collections.sort(leaderboard);

        System.out.println(" ");
        for (int i = 0; i < size; i++) {
            NameAndStack stack = leaderboard.get(i);

            System.out.println("  " + (i + 1) + ". " + stack.name + ": " + stack.chips);
        }
        System.out.println(" ");

        // show top 5 rivals
        for (int i = 0; i < leaderboard.size(); i++) {
            NameAndStack thisStack = leaderboard.get(i);

            if (thisStack.name.contains("-")) {
                System.out.println("  *" + (i + 1) + ". " + thisStack.name + ": " + thisStack.chips);
                rivalCounter--;
            }

            if (rivalCounter == 0) break;
        }



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

        leaderboard.clear();

        for (int i = 0; i < tables.size(); i++) {
            Table thisTable = tables.get(i);
            thisTable.moveBlind();
            if (thisTable.featuredTable) thisTable.displayCurrentTableState();

            // play the hand
            TableReport tr = thisTable.playHand(currentBet);

            for (NameAndStack stack : tr.nameAndStacks) {
                leaderboard.add(stack);
            }

            // check for eliminated players
            for (int j = 0; j < tr.eliminatedPlayerIds.size(); j++) {
                Player thisPlayer = getPlayerById(tr.eliminatedPlayerIds.get(j));
                int finishingPlace = activePlayers.size();

                System.out.println("  Table " + thisTable.getId() + ": " + thisPlayer.name() + " eliminated in " + finishingPlace + " place.");
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

        // balance swaps first

        // distribute unseated players from closed tables
        reseatPlayersFromClosedTables();

        Collections.sort(tables);

    }

    public boolean canCloseATable() {
        int capacity = tables.size() * 6;
        int diff = capacity - activePlayers.size();
        boolean closeATable = diff >= 6;
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

        System.out.println("  Closed table " + closedTable.getId());
    }

    public void reseatPlayersFromClosedTables() {
        int initialArraySize = playersToBeReseated.size();
        Player thisPlayer;
        Table thisTable;
        int theirChips;
        int tableIndex = 0;

        // will need to cycle back through tables if more players than tables
        for (int i = 0; i < initialArraySize; i++) {
            thisTable = tables.get(tableIndex);
            if (thisTable.playersSeated == 6) continue;

            thisPlayer = playersToBeReseated.remove(0);
            theirChips = chipsForPlayersToBeReseated.remove(0);

            thisTable.seatPlayerInFirstEmptySeat(thisPlayer, theirChips);
            tableIndex = tableIndex == (tables.size() - 1) ? 0 : tableIndex + 1;
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

    public void setFeaturedTable() {
        Scanner in = new Scanner(System.in);
        int answer;
        Table thisTable;
        Table thatTable;

            System.out.println("  Select table number: ");
            answer = in.nextInt();
            int tablesSize = tables.size();

            for (int i = 0; i < tablesSize; i++) {
                if (answer < 1 ) System.out.println("  Table selection out of range.");

                thisTable = tables.get(i);

                if (thisTable.getId() == answer) {
                    thisTable.featuredTable = true;
                    System.out.println("  Featured table set to table: " + answer);

                    for (int j = 0; j < tables.size(); j++) {
                        thatTable = tables.get(j);
                        if (thatTable.featuredTable == true && thatTable.getId() != answer) {
                            thatTable.featuredTable = false;
                            return;
                        }
                    }
                }
            }
    }
}
