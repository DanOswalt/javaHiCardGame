package com.company;

import java.util.Collections;
import java.util.ArrayList;

public class Table {
    int id;
    int blindSeat = 0;
    int actionSeat = 0;
    int playersSeated;
    boolean outstandingBets = false;

    ArrayList<Integer> eliminatedPlayers = new ArrayList<>();

    ArrayList<Pot> pots = new ArrayList<>();
    ArrayList<Integer> seatsCommitted = new ArrayList<Integer>();
    String[] potNames = {
            "Main Pot",
            "Sidepot A",
            "Sidepot B",
            "Sidepot C",
            "Sidepot D"
    };

    Seat[] seats = new Seat[6];

    // Use Seat instead of mutating the Player object; the Table interacts with the Seat
    // The seat gets dealt the roll, has chips, etc. That way, the Player is sort of just read for
    // metadata and doesn't hold game-state.

    // table needs an array of seats

    public Table(int id) {
        this.id = id;

        for(int i = 0; i < 6; i++) {
            seats[i] = new Seat(i + 1);
        }
    }

    public void putPlayerInSeatByIndex(Player player, int index, int chips) {
        seats[index].player = player;
        seats[index].isEmpty = false;
        seats[index].setChips(chips);
        playersSeated++;
    }

    public void displayCurrentTableState() {

        System.out.println("*-------------*");
        for(int i = 0; i < 6; i++) {
            if (seats[i].isEmpty) {
                System.out.println("  " + (i + 1) + ")  " );
            } else {
                System.out.println("  " + (i + 1) + ") " + (blindSeat == (i + 1) ? "> " : "  ") + seats[i].player.name() + " " + seats[i].chips + " " );
            }
        }
        System.out.println("*-------------*");
        System.out.println(" ");
    }

    public void moveBlind() {
        // keep incrementing until you find the next occupied seat
        do { blindSeat = blindSeat == 6 ? 1 : blindSeat + 1; } while (seats[blindSeat - 1].isEmpty);
    }

    public void moveAction() {
        // keep incrementing until you find the next occupied seat
        do { actionSeat = actionSeat == 6 ? 1 : actionSeat + 1; } while (seats[actionSeat - 1].isEmpty);
    }

    public TableReport playHand(int currentBet) {
        int playersLeftToAct = playersSeated;

        actionSeat = blindSeat;

        seatsCommitted.clear();
        pots.clear();
        eliminatedPlayers.clear();
        outstandingBets = false;

        Deck d = new Deck();
        Collections.shuffle(d.deck);

        while (playersLeftToAct > 0) {

            // alias the seat
            Seat activeSeat = seats[actionSeat - 1];

            // reset
            activeSeat.resetForNewHand();

            // get roll
            activeSeat.hand = d.dealCard();

            if(activeSeat.player.isHuman()) {
                System.out.println("  Your hand: " + activeSeat.hand.name);
            }

            // find what the bet size will be and if player will be all-in
            if (activeSeat.chips <= currentBet) {
                activeSeat.committedBet = activeSeat.chips;
                activeSeat.committedAllIn = true;
            } else {
                activeSeat.committedBet = currentBet;
                activeSeat.committedAllIn = false;
            }

            // first, do the forced bet
            if (actionSeat == blindSeat) {
                seatsCommitted.add(actionSeat);
                activeSeat.doesCall = true;

                if (activeSeat.committedAllIn) {
                    System.out.println("  " + activeSeat.player.name() + " posts blind (" + activeSeat.committedBet + ") and is all in!");
                } else {
                    System.out.println("  " + activeSeat.player.name() + " posts blind (" + activeSeat.committedBet +").");
                }

            // then do the decisions
            } else {
                activeSeat.doesCall = activeSeat.player.willPlay(activeSeat.hand.value);

                if (activeSeat.doesCall && activeSeat.committedAllIn) {
                    System.out.println("  " + activeSeat.player.name() + " calls (" + activeSeat.committedBet + ") and is all in!");
                    seatsCommitted.add(actionSeat);
                } else if (activeSeat.doesCall) {
                    System.out.println("  " + activeSeat.player.name() + " calls (" + activeSeat.committedBet + ").");
                    seatsCommitted.add(actionSeat);
                } else {
                    System.out.println("  " + activeSeat.player.name() + " folds.");
                }
            }

            playersLeftToAct--;
            moveAction();
        }

        // after all bets have been placed, evaluate the bets and divvy into pots.

        divvyIntoPots();
        getWinners();
        awardChips();
        displayWinners();
        eliminatePlayers();

        TableReport tr = new TableReport(playersSeated, 18, eliminatedPlayers);

        return tr;

    }

    public void eliminatePlayers() {

        for (int i = 0; i < 6; i ++) {
            Seat thisSeat = seats[i];

            if (thisSeat.isEmpty) { continue; }

            if (thisSeat.chips == 0) {
                eliminatedPlayers.add(thisSeat.player.id);
                playersSeated--;

                thisSeat.resetForNewHand();
                thisSeat.player = null;
                thisSeat.isEmpty = true;
            }
        }
    }

    public void displayWinners() {
        Pot p;

        System.out.println();

        for (int i = 0; i < seats.length; i++) {
            if (seats[i].doesCall) {
                System.out.println("  " + seats[i].player.name() + " shows " + seats[i].hand.name + ".");
            }
        }

        System.out.println();

        for (int i = 0; i < pots.size(); i++) {
            p = pots.get(i);

            System.out.println("  " + p.potName + " (" + p.chips + ") won by " + seats[p.winnerId - 1].player.name() + " with " + seats[p.winnerId - 1].hand.name + ".");
        }
    }

    public void getWinners() {
        int max;
        int winnerSeat;
        int firstSeat;
        ArrayList<Integer> eligible;

        for (int i = 0; i < pots.size(); i++) {

            Pot thisPot = pots.get(i);

            // get the arrayList of eligible seats
            eligible = thisPot.getEligibleSeats();
            firstSeat = eligible.get(0);

            // initialize with first seat's hand
            max = seats[firstSeat - 1].hand.value;
            winnerSeat = firstSeat;

            for (int j = 1; j < eligible.size(); j++) {
                int thisSeat = eligible.get(j);
                int thisVal = seats[thisSeat - 1].hand.value;

                if (thisVal > max) {
                    max = thisVal;
                    winnerSeat = thisSeat;
                }
            }

            thisPot.setWinnerId(winnerSeat);
        }
    }

    public void awardChips() {
        Pot p;

        for (int i = 0; i < pots.size(); i++) {
            p = pots.get(i);

            seats[p.winnerId - 1 ].chips += p.chips;
        }
    }


    public void divvyIntoPots() {
        int potIndex = 0;

        // this do block should create the main pot always, even with one bettor
        do {
            // find smallest common chip value
            int minBet = seats[seatsCommitted.get(0) - 1].committedBet;
            for (int i = 1; i < seatsCommitted.size(); i++) {
                int thisBet = seats[seatsCommitted.get(i) - 1].committedBet;
                minBet = thisBet < minBet ? thisBet : minBet;
            }

            // create a pot
            Pot pot = new Pot(potNames[potIndex]);
            potIndex++;

            int seatsCommittedSize = seatsCommitted.size();

            for (int i = 0; i < seatsCommittedSize; i++) {

                // get the seat id
                int seatNum = seatsCommitted.get(i);
                pot.addMember(seatNum);

                // move their chips to this pot
                pot.addChips(minBet);
                seats[seatNum - 1].chips -= minBet;
                seats[seatNum - 1].committedBet -= minBet;
            }

            // filter out the ones without chips
            filterZeroCommittedChips();

            // add pot to the pots array
            pots.add(pot);
            checkOutstandingBets();

        } while (seatsCommitted.size() > 1 && outstandingBets);

    }

    public void checkOutstandingBets() {
        for (int i = 0; i < seatsCommitted.size(); i++) {
            if (seats[seatsCommitted.get(i) - 1].committedBet > 0) {
                System.out.println(seats[seatsCommitted.get(i) - 1].committedBet);
                outstandingBets = true;
                return;
            }
        }
        outstandingBets = false;
    }

    public void filterZeroCommittedChips() {

        // create a separate list of ones without chips committed
        // then, for each one in that list, remove that id from committed

        ArrayList<Integer> removalList = new ArrayList<>();

        for (int i = 0; i < seatsCommitted.size(); i++) {
            Seat thisSeat = seats[seatsCommitted.get(i) - 1];
            if (thisSeat.committedBet == 0) {
                removalList.add(thisSeat.seatNumber);
            }
        }

        for (int i = 0; i < removalList.size(); i++) {
            seatsCommitted.remove(seatsCommitted.indexOf(removalList.get(i)));
        }
    }
}
