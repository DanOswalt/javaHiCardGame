package com.company;

import java.util.Scanner;

public class Player {

    private boolean human;
    private String name;
    private int callThreshold;
    public int id;

    public Player (int id, boolean human, String name, int callThreshold) {
        this.id = id;
        this.human = human;
        this.name = name;
        this.callThreshold = callThreshold;
    }

    public boolean isHuman() {
        return this.human;
    }

    public boolean willPlay(int value) {
        if(human) {
            Scanner in = new Scanner(System.in);
            String answer;
            boolean play;

            System.out.println("  > Call? y/n");
            while (true) {
                answer = in.nextLine().trim().toLowerCase();
                if (answer.equals("y")) {
                    play = true;
                    break;
                } else if (answer.equals("n")) {
                    play = false;
                    break;
                } else {
                    System.out.println("  > Please answer y/n");
                }
            }
            return play;
        } else {
            return value >= callThreshold;
        }
    }

    public String name() {
        return name;
    }
}
