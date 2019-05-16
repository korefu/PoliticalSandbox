package com.wowloltech.politicalsandbox;

import java.util.Iterator;

public class HumanPlayer extends Player {
    public HumanPlayer(int id) {
        super(id);
        setIsHuman(true);
    }

    public HumanPlayer(int id, double money, int recruits, int color, String name) {
        super(id, money, recruits, color, name);
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
       super.nextTurn();
    }

}
