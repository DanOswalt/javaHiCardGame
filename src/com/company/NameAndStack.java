package com.company;


public class NameAndStack implements Comparable<NameAndStack> {
    public String name;
    public int chips;

    public NameAndStack(String name, int chips) {
        this.name = name;
        this.chips = chips;
    }

    @Override
    public int compareTo(NameAndStack otherStack) {
        Integer a = chips;
        Integer b = otherStack.chips;
        return b.compareTo(a);
    }
}
