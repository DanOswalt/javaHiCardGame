package com.company;

import java.util.Scanner;
import java.util.Random;

public class Player {

    private boolean human;
    private String name;
    private int callThreshold;
    public int id;
    boolean rival;

    public Player (int id, boolean human, String name, int callThreshold) {
        this.id = id;
        this.human = human;
        this.name = name;
        this.callThreshold = callThreshold;
        this.rival = true;
    }

    public Player (int id) {
        this.id = id;
        this.human = false;
        this.name = getRandName();
        this.callThreshold = getRandCallThreshold();
        this.rival = false;
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

    public String getRandName() {

        Random rand = new Random();

        int numChars = rand.nextInt(5) + 3;
        int pattern = rand.nextInt(2);

        String randName;

        String[] vowels = {
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "e", "e", "e", "e", "e", "e", "e", "e", "e", "e",
            "o", "o", "o", "o", "o", "o", "o", "o", "o", "o",
            "i", "i", "i", "i", "i", "u", "u", "u", "u", "u",
            "ae", "ai", "au", "aa", "ea", "ee", "ei", "eu", "ia", "ie",
            "io", "ua", "ue", "ui", "uo", "eau", "oa", "oi", "ou", "ea"
        };

        String[] first_conson = {
            "B", "C", "D", "F", "G", "H", "J", "K", "L", "M",
            "N", "N", "P", "Q", "R", "S", "T", "V", "W", "X",
            "Y", "Z", "Ch", "Sh", "Ph", "Th", "Sh", "Str", "Sk", "Sp",
            "Kr", "Kl", "Qu", "Fr", "Bl", "Pl", "Tr", "Tw", "Dr", "Br",
            "Gh", "Gr", "Gl", "Pr", "Zh", "Fl", "Cl", "Cr", "Chr", "Spr",
            "R", "S", "T", "L", "N", "R", "S", "T", "L", "N"
        };

        String[] other_conson = {
            "b","c","d","f","g","h","j","k","l","m",
            "n","n","p","q","r","s","t","u","v","x",
            "y","z","ch","sh","ph","th","st","str","sk","sp",
            "ss","tt","qu","mm","nn","gg","tr","rt","lt","ft",
            "gh","rg","dd","rp","ll","ck","rf","cr","chr","spr",
            "r","s","t","l","n","r","s","t","l","n"
        };

        if(pattern < 2 ) {
            randName = first_conson[rand.nextInt(59)] +
                    vowels[rand.nextInt(59)] +
                    other_conson[rand.nextInt(59)] +
                    vowels[rand.nextInt(59)] +
                    other_conson[rand.nextInt(59)];

        } else {
            randName = vowels[rand.nextInt(59)] +
                    other_conson[rand.nextInt(59)] +
                    vowels[rand.nextInt(59)] +
                    other_conson[rand.nextInt(59)] +
                    vowels[rand.nextInt(59)];

            randName = randName.substring(0,1).toUpperCase();
        }

        //lop off at x chars length to keep it non-ridiculous
        randName = randName.substring(0, Math.min(randName.length(), numChars));

        return randName;
    }

    public int getRandCallThreshold() {
        Random rand = new Random();
        return rand.nextInt(51);
    }
}
