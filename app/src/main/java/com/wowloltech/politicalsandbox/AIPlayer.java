package com.wowloltech.politicalsandbox;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer(int id, double money, int recruits, int color, String name) {
        super(id, money, recruits, color, name);
        this.setIsHuman(false);
    }

    public AIPlayer(int id, double money, int recruits, int color, String name, List<Province> provinces, List<Army> armies) {
        super(id, money, recruits, color, name);
        this.setIsHuman(false);
        for (Province p : provinces) {
            getProvinces().add(p);
            p.setOwner(this);
        }
        for (Army a : armies) {
            getArmies().add(a);
            a.setOwner(this);
        }
    }

    @Override
    public void nextTurn() {
        super.nextTurn();
        List<Province> borderProvinces = new LinkedList<>();
        for (Province p : getProvinces())
            for (Province n : p.getNeighbours())
                if (n.getOwner() != p.getOwner()) {
                    borderProvinces.add(p);
                    break;
                }
        for (int i = 0; i < getArmies().size(); i++) {
            Army a = getArmies().get(i);
            Collections.sort(a.getLocation().getNeighbours(), new Comparator<Province>() {
                @Override
                public int compare(Province p1, Province p2) {
                    return -p1.getNumberOfFriendlyProvinces(AIPlayer.this).compareTo(p2.getNumberOfFriendlyProvinces(AIPlayer.this));
                }
            });
            for (Province p : a.getLocation().getNeighbours()) {
                if (p.getOwner() != a.getOwner()) {
                    int sum = 0;
                    for (Army enemy : p.getArmies()) {
                        sum += enemy.getStrength();
                    }
                    if (a.getStrength() - sum >= 40 && a.getSpeed() > 0) {
                        attackProvince(a, p);
                        borderProvinces.add(p);
                    }
                }
            }
            if (a.getLocation().getNumberOfFriendlyProvinces(AIPlayer.this) - a.getLocation().getNeighbours().size() == 0) {
                setMoney(getMoney() + (double) a.getStrength() / 50);
                setRecruits(getRecruits() + a.getStrength());
                borderProvinces.remove(a.getLocation());
                Army.remove(a);
                i--;
            }

        }

        int recruitableArmy;
        if (getMoneyIncome() * 100 <= getMoney() * 50)
            recruitableArmy = (int) (getMoneyIncome() * 100);
        else recruitableArmy = (int) (getMoney() * 50);
        recruitableArmy -= 10;
        int gthreat = 0;
        int[] threats = new int[borderProvinces.size()];
        for (
                int i = 0; i < borderProvinces.size(); i++) {
            Province p = borderProvinces.get(i);
            for (Province n : p.getNeighbours()) {
                int lthreat = 0;
                if (n.getOwner() != p.getOwner())
                    lthreat += 2500;
                for (Army a : n.getArmies())
                    if (a.getOwner() != p.getOwner()) {
                        lthreat += a.getStrength();
                        gthreat += a.getStrength();
                    }
                for (Province pn : n.getNeighbours())
                    if (borderProvinces.contains(pn)) {
                        threats[borderProvinces.indexOf(pn)] += lthreat;
                        gthreat += lthreat;
                    }
            }
        }
        for (int i = 0; i < borderProvinces.size(); i++) {
            //Log.d("myLog", toString()+" "+threats[i]+" "+"gthread "+gthreat+" "+recruitableArmy);
            int defense = 0;
            for (Army a : borderProvinces.get(i).getArmies())
                defense += a.getStrength();
            if (threats[i] > 0 && gthreat > 0 && recruitableArmy > 0 && (recruitableArmy * threats[i] / gthreat - defense) > 0) {
                pickMilitary((recruitableArmy * threats[i] / gthreat - defense), borderProvinces.get(i));
            }

        }
        for (Province p : getProvinces()) uniteArmy(p);
    }

}
