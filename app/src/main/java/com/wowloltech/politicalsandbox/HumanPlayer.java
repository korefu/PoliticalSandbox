package com.wowloltech.politicalsandbox;

import java.util.Iterator;

public class HumanPlayer extends Player {
    public HumanPlayer(int id) {
        super(id);
        setIsHuman(true);
    }

    public HumanPlayer(int id, double money, int recruits) {
        super(id, money, recruits);
        this.setIsHuman(true);
    }

    @Override
    boolean acceptAlliance(Event e) {
        return false;
    }

    @Override
    boolean acceptPeace(Event e) {
        return false;
    }

    @Override
    public void nextTurn() {
        double income = 0;
        int newRecruits = 0;
        for (int i = 0; i < this.getProvinces().size(); i++) {
            income += getProvinces().get(i).getIncome();
            newRecruits += getProvinces().get(i).getRecruits();
        }
        for (Army a : getArmies())
            income -= (double) a.getStrength() / 100;
        setMoney(getMoney() + income);
        setRecruits(getRecruits() + newRecruits);
        if (getMoney() < 0) {
            setMoney(0);
            for (Iterator<Army> i = getArmies().iterator(); i.hasNext(); ) {
                Army a = i.next();
                Army.remove(a, i);
            }
        }
    }

    @Override
    public String toString() {
        return "Человек, №" + this.getId();
    }
}
