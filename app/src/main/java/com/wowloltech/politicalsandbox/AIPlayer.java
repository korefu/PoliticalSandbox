package com.wowloltech.politicalsandbox;

public class AIPlayer extends Player {
    public AIPlayer(int id) {
        super(id);
        setIsHuman(false);
    }

    public AIPlayer(int id, double money, int recruits) {
        super(id, money, recruits);
        this.setIsHuman(false);
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
        for (int i = 0; i < getProvinces().size(); i++) {
            setMoney(getMoney() + getProvinces().get(i).getIncome());
            setRecruits(getRecruits() + getProvinces().get(i).getRecruits());
        }
        // etc
    }

    @Override
    public String toString() {
        return "ИИ, №" + this.getId();
    }
}
