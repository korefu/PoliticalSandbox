package com.wowloltech.politicalsandbox;

import java.util.Iterator;

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
        super.nextTurn();
        int recruitableArmy;
        if (getMoneyIncome() * 100 <= getMoney()*50)
            recruitableArmy = (int)(getMoneyIncome()*100);
        else  recruitableArmy =  (int)(getMoney() * 50);
        for (Province p: getProvinces()) combineArmy(p);
        for (int i=0;i<getArmies().size();i++) {
            Army a = getArmies().get(i);
            for (Province p : Map.getNeighbours(a.getLocation())) {
                if (p.getOwner() != a.getOwner()) {
                    int sum = 0;
                    for (Army enemy : p.getOwner().getArmies()) {
                        if (enemy.getLocation() == p) sum += enemy.getStrength();
                    }
                    if (sum <= a.getStrength()) attackProvince(a, p);
                }
            }
        }
        int gthreat = 0;
        int[] threats = new int[getProvinces().size()];
        for (int i = 0;i<getProvinces().size();i++) {
            Province p = getProvinces().get(i);
            for (Province n: Map.getNeighbours(p)) {
                if (n.getOwner() != p.getOwner()) {
                    threats[i]++;
                    gthreat++;
                }
                for (Army a: n.getArmies())
                    if (a.getOwner() != p.getOwner()) {
                        threats[i] += a.getStrength();
                        gthreat += a.getStrength();
                    }
            }
        }
        for (int i = 0;i<getProvinces().size();i++) {
            pickMilitary(recruitableArmy/gthreat * threats[i], getProvinces().get(i));

        }
    }

    @Override
    public String toString() {
        return "ИИ, №" + this.getId();
    }
}
