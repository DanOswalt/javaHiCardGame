package com.company;

public class Seat {
    int seatNumber; // 1-based
    Player player;
    int chips = 0;
    int committedBet = 0;
    Card hand = null;
    boolean isEmpty = true;
    boolean committedAllIn = false;
    boolean doesCall = false;

    // seats initialize empty;
    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void resetForNewHand() {
        committedBet = 0;
        committedAllIn = false;
        doesCall = false;
        hand = null;
    }
}

