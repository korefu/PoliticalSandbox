package com.wowloltech.politicalsandbox.models;

import com.wowloltech.politicalsandbox.Game;

import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(int id, double money, int recruits, int color, String name, Game game) {
        super(id, money, recruits, color, name, game);
        this.setIsHuman(true);
    }
    public HumanPlayer(int id, double money, int recruits, int color, String name, List<Province> provinces, List<Army> armies, Game game) {
        super(id, money, recruits, color, name, game);
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
