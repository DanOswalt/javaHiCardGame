package com.company;

import java.util.ArrayList;

public class TableReport {

    // This table report is for the table to send back to the Tournament object
    // at the end of each hand.

    ArrayList<Integer> eliminatedPlayerIds = new ArrayList<>();
    ArrayList<NameAndStack> nameAndStacks = new ArrayList<>();
    int playersAtTable;
    int chipsAtTable;

    public TableReport(int playersAtTable, int chipsAtTable, ArrayList<Integer> eliminatedPlayerIds, ArrayList<NameAndStack> nameAndStacks) {
        this.playersAtTable = playersAtTable;
        this.chipsAtTable = chipsAtTable;
        this.eliminatedPlayerIds = eliminatedPlayerIds;
        this.nameAndStacks = nameAndStacks;

    }
}
