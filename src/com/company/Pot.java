package com.company;

import java.util.ArrayList;

public class Pot {
    String potName;
    int chips;
    int winnerId;
    ArrayList<Integer> eligibleSeats = new ArrayList<>();

    public Pot(String name) {
        potName = name;
        chips = 0;
    }

    public void addChips(int chips) {
        this.chips += chips;
    }

    public void addMember(int playerId) {
        eligibleSeats.add(playerId);
    }

    public ArrayList<Integer> getEligibleSeats() {
        return eligibleSeats;
    }

    public void setWinnerId(int seatNum) {
        winnerId = seatNum;
    }
}
