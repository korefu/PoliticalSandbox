package com.wowloltech.politicalsandbox;

import java.util.Iterator;
import java.util.List;

public class HumanPlayer extends Player {
    public HumanPlayer(int id) {
        super(id);
        setIsHuman(true);
    }

    public HumanPlayer(int id, double money, int recruits, int color, String name) {
        super(id, money, recruits, color, name);
        this.setIsHuman(true);
    }
    public HumanPlayer(int id, double money, int recruits, int color, String name, List<Province> provinces, List<Army> armies) {
        super(id, money, recruits, color, name);
        this.setIsHuman(true);
        for (Province p: provinces) {
            getProvinces().add(p);
            p.setOwner(this);
        }
        for (Army a: armies) {
            getArmies().add(a);
            a.setOwner(this);
        }
    }
    @Override
    public void nextTurn() {
       super.nextTurn();
    }

}
